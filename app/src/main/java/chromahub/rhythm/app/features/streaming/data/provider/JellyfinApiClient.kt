package chromahub.rhythm.app.features.streaming.data.provider

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Jellyfin API client for authentication, search and stream URL generation.
 */
class JellyfinApiClient(context: Context) {

    private data class Credentials(
        val serverUrl: String,
        val username: String,
        val accessToken: String,
        val userId: String
    )

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Volatile
    private var credentials: Credentials? = loadCredentials()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    fun isConnected(): Boolean = credentials != null

    fun getServerUrl(): String = credentials?.serverUrl.orEmpty()

    fun getUsername(): String = credentials?.username.orEmpty()

    suspend fun login(serverUrl: String, username: String, password: String): Result<ProviderConnectionResult> {
        val normalizedUrl = normalizeServerUrl(serverUrl)
        val validationError = validateServerUrl(normalizedUrl)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }
        if (username.isBlank()) {
            return Result.failure(IllegalArgumentException("Username is required"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password is required"))
        }

        return authenticateByName(
            serverUrl = normalizedUrl,
            username = username.trim(),
            password = password
        ).map { (token, userId) ->
            val cred = Credentials(
                serverUrl = normalizedUrl,
                username = username.trim(),
                accessToken = token,
                userId = userId
            )
            credentials = cred
            prefs.edit()
                .putString(KEY_SERVER_URL, normalizedUrl)
                .putString(KEY_USERNAME, username.trim())
                .putString(KEY_ACCESS_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .apply()
            ProviderConnectionResult(displayName = username.trim(), serverUrl = normalizedUrl)
        }
    }

    fun logout() {
        credentials = null
        prefs.edit().clear().apply()
    }

    suspend fun ping(): Result<Boolean> {
        return request("/System/Ping").map { true }
    }

    suspend fun searchSongs(query: String, limit: Int = 30): Result<List<ProviderSong>> {
        val cred = credentials ?: return Result.failure(IllegalStateException("Jellyfin service is not connected"))

        val params = mapOf(
            "SearchTerm" to query,
            "IncludeItemTypes" to "Audio",
            "Recursive" to "true",
            "Fields" to "MediaSources,Genres,Path,Artists,AlbumArtist,AlbumId,Album,RunTimeTicks",
            "Limit" to limit.coerceIn(1, 100).toString()
        )

        return requestJson("/Users/${cred.userId}/Items", params).map { response ->
            val items = response.optJSONArray("Items")
            buildList {
                for (i in 0 until (items?.length() ?: 0)) {
                    val song = items?.optJSONObject(i) ?: continue
                    val id = song.optString("Id", "")
                    if (id.isBlank()) continue

                    val title = song.optString("Name", "Unknown title")
                    val artist = parseArtist(song)
                    val album = song.optString("Album", "Unknown album")
                    val durationMs = song.optLong("RunTimeTicks", 0L) / 10_000L

                    add(
                        ProviderSong(
                            providerId = id,
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = durationMs,
                            artworkUrl = buildImageUrl(id)
                        )
                    )
                }
            }
        }
    }

    fun buildStreamUrl(itemId: String, maxBitRateKbps: Int = 0): String? {
        val cred = credentials ?: return null
        if (itemId.isBlank()) return null

        val urlBuilder = "${cred.serverUrl}/Audio/$itemId/universal".toHttpUrl().newBuilder()
            .addQueryParameter("UserId", cred.userId)
            .addQueryParameter("DeviceId", DEVICE_ID)
            .addQueryParameter("Container", "mp3,flac,m4a,ogg,wav,aac,opus,webm")
            .addQueryParameter("AudioCodec", "mp3,flac,aac,opus")
            .addQueryParameter("api_key", cred.accessToken)

        if (maxBitRateKbps > 0) {
            urlBuilder.addQueryParameter("MaxStreamingBitrate", (maxBitRateKbps * 1000).toString())
        }

        return urlBuilder.build().toString()
    }

    fun buildImageUrl(itemId: String, maxWidth: Int = 500): String? {
        val cred = credentials ?: return null
        if (itemId.isBlank()) return null
        return "${cred.serverUrl}/Items/$itemId/Images/Primary?maxWidth=$maxWidth&quality=90"
    }

    private suspend fun authenticateByName(
        serverUrl: String,
        username: String,
        password: String
    ): Result<Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject().apply {
                    put("Username", username)
                    put("Pw", password)
                }.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("${serverUrl.trimEnd('/')}/Users/AuthenticateByName")
                    .header("Authorization", buildAuthorizationHeader(token = null))
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    val raw = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                    }

                    val json = JSONObject(raw)
                    val token = json.optString("AccessToken", "")
                    val userId = json.optJSONObject("User")?.optString("Id", "") ?: ""

                    if (token.isBlank() || userId.isBlank()) {
                        return@withContext Result.failure(IllegalStateException("Invalid Jellyfin authentication response"))
                    }

                    Result.success(token to userId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Jellyfin authentication failed", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun request(path: String, params: Map<String, String> = emptyMap()): Result<String> {
        val cred = credentials ?: return Result.failure(IllegalStateException("Credentials not set"))

        return withContext(Dispatchers.IO) {
            try {
                val urlBuilder = "${cred.serverUrl}$path".toHttpUrl().newBuilder()
                params.forEach { (key, value) ->
                    urlBuilder.addQueryParameter(key, value)
                }

                val request = Request.Builder()
                    .url(urlBuilder.build())
                    .header("Authorization", buildAuthorizationHeader(token = cred.accessToken))
                    .header("Accept", "application/json")
                    .get()
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    val body = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                    }
                    Result.success(body)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Jellyfin request failed for path=$path", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun requestJson(path: String, params: Map<String, String> = emptyMap()): Result<JSONObject> {
        return request(path, params).map { JSONObject(it) }
    }

    private fun parseArtist(song: JSONObject): String {
        val artists = song.optJSONArray("Artists")
        val names = buildList {
            for (i in 0 until (artists?.length() ?: 0)) {
                val name = artists?.optString(i).orEmpty()
                if (name.isNotBlank()) add(name)
            }
        }

        if (names.isNotEmpty()) {
            return names.joinToString(", ")
        }

        return song.optString("AlbumArtist", "Unknown artist")
    }

    private fun buildAuthorizationHeader(token: String?): String {
        val tokenPart = if (!token.isNullOrBlank()) ", Token=\"$token\"" else ""
        return "MediaBrowser Client=\"$CLIENT_NAME\", Device=\"$DEVICE_NAME\", DeviceId=\"$DEVICE_ID\", Version=\"$CLIENT_VERSION\"$tokenPart"
    }

    private fun loadCredentials(): Credentials? {
        val server = prefs.getString(KEY_SERVER_URL, null).orEmpty()
        val user = prefs.getString(KEY_USERNAME, null).orEmpty()
        val token = prefs.getString(KEY_ACCESS_TOKEN, null).orEmpty()
        val userId = prefs.getString(KEY_USER_ID, null).orEmpty()

        if (server.isBlank() || user.isBlank() || token.isBlank() || userId.isBlank()) {
            return null
        }

        return Credentials(serverUrl = server, username = user, accessToken = token, userId = userId)
    }

    private fun normalizeServerUrl(input: String): String {
        val trimmed = input.trim().trimEnd('/')
        if (trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)) {
            return trimmed
        }
        return "https://$trimmed"
    }

    private fun validateServerUrl(url: String): String? {
        val parsed = url.toHttpUrlOrNull() ?: return "Enter a valid server URL"
        if (parsed.username.isNotEmpty() || parsed.password.isNotEmpty()) {
            return "Server URL must not contain embedded credentials"
        }

        if (!parsed.isHttps && !isPrivateHost(parsed.host)) {
            return "Use https:// for remote Jellyfin servers"
        }

        return null
    }

    private fun isPrivateHost(host: String): Boolean {
        if (host.equals("localhost", true) || host.endsWith(".local", true)) {
            return true
        }

        val parts = host.split('.')
        if (parts.size != 4) return false
        val octets = parts.map { it.toIntOrNull() ?: return false }

        val first = octets[0]
        val second = octets[1]

        return first == 10 ||
            (first == 172 && second in 16..31) ||
            (first == 192 && second == 168) ||
            (first == 127)
    }

    private companion object {
        private const val TAG = "JellyfinApiClient"
        private const val PREFS_NAME = "streaming_jellyfin_credentials"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_USERNAME = "username"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"

        private const val CLIENT_NAME = "Rhythm"
        private const val CLIENT_VERSION = "1.0"
        private const val DEVICE_NAME = "Android"
        private const val DEVICE_ID = "Rhythm-Android"
    }
}
