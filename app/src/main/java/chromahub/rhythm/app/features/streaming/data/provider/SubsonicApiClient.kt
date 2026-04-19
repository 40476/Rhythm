package chromahub.rhythm.app.features.streaming.data.provider

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Subsonic-compatible API client used for Navidrome/Subsonic service support.
 */
class SubsonicApiClient(context: Context) {

    private data class Credentials(
        val serverUrl: String,
        val username: String,
        val password: String
    )

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Volatile
    private var credentials: Credentials? = loadCredentials()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    fun isConnected(): Boolean = credentials?.let { it.serverUrl.isNotBlank() && it.username.isNotBlank() && it.password.isNotBlank() } == true

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

        credentials = Credentials(
            serverUrl = normalizedUrl,
            username = username.trim(),
            password = password
        )

        return ping().map {
            prefs.edit()
                .putString(KEY_SERVER_URL, normalizedUrl)
                .putString(KEY_USERNAME, username.trim())
                .putString(KEY_PASSWORD, password)
                .apply()
            ProviderConnectionResult(displayName = username.trim(), serverUrl = normalizedUrl)
        }.onFailure {
            credentials = null
        }
    }

    fun logout() {
        credentials = null
        prefs.edit().clear().apply()
    }

    suspend fun ping(): Result<Boolean> {
        return requestAndParse("ping").map { true }
    }

    suspend fun searchSongs(query: String, limit: Int = 30): Result<List<ProviderSong>> {
        if (!isConnected()) {
            return Result.failure(IllegalStateException("Subsonic service is not connected"))
        }

        val params = mapOf(
            "query" to query,
            "artistCount" to "0",
            "albumCount" to "0",
            "songCount" to limit.coerceIn(1, 100).toString()
        )

        return requestAndParse("search3", params).map { response ->
            val songs = response.optJSONObject("searchResult3")?.optJSONArray("song")
            buildList {
                for (i in 0 until (songs?.length() ?: 0)) {
                    val song = songs?.optJSONObject(i) ?: continue
                    val id = song.optString("id", "")
                    if (id.isBlank()) continue

                    val coverArtId = song.optString("coverArt").takeIf { it.isNotBlank() }
                    add(
                        ProviderSong(
                            providerId = id,
                            title = song.optString("title", song.optString("name", "Unknown title")),
                            artist = song.optString("artist", "Unknown artist"),
                            album = song.optString("album", "Unknown album"),
                            durationMs = song.optLong("duration", 0L) * 1000L,
                            artworkUrl = coverArtId?.let { buildCoverArtUrl(it, 500) }
                        )
                    )
                }
            }
        }
    }

    fun buildStreamUrl(songId: String, maxBitRateKbps: Int = 0, format: String? = null): String? {
        val cred = credentials ?: return null
        if (songId.isBlank()) return null
        val (token, salt) = generateAuthParams(cred.password)

        val urlBuilder = "${cred.serverUrl}/rest/stream.view".toHttpUrl().newBuilder()
            .addQueryParameter("u", cred.username)
            .addQueryParameter("t", token)
            .addQueryParameter("s", salt)
            .addQueryParameter("v", API_VERSION)
            .addQueryParameter("c", CLIENT_ID)
            .addQueryParameter("f", "json")
            .addQueryParameter("id", songId)

        if (maxBitRateKbps > 0) {
            urlBuilder.addQueryParameter("maxBitRate", maxBitRateKbps.toString())
        }
        if (!format.isNullOrBlank()) {
            urlBuilder.addQueryParameter("format", format)
        }

        return urlBuilder.build().toString()
    }

    fun buildCoverArtUrl(coverArtId: String, size: Int = 500): String? {
        val cred = credentials ?: return null
        if (coverArtId.isBlank()) return null
        val (token, salt) = generateAuthParams(cred.password)

        return "${cred.serverUrl}/rest/getCoverArt.view".toHttpUrl().newBuilder()
            .addQueryParameter("u", cred.username)
            .addQueryParameter("t", token)
            .addQueryParameter("s", salt)
            .addQueryParameter("v", API_VERSION)
            .addQueryParameter("c", CLIENT_ID)
            .addQueryParameter("f", "json")
            .addQueryParameter("id", coverArtId)
            .addQueryParameter("size", size.toString())
            .build()
            .toString()
    }

    private suspend fun request(endpoint: String, params: Map<String, String> = emptyMap()): Result<String> {
        val cred = credentials ?: return Result.failure(IllegalStateException("Credentials not set"))

        return withContext(Dispatchers.IO) {
            try {
                val url = buildApiUrl(cred, endpoint, params)
                val request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .header("User-Agent", "Rhythm/1.0 (Android)")
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
                Log.e(TAG, "Subsonic request failed for endpoint=$endpoint", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun requestAndParse(endpoint: String, params: Map<String, String> = emptyMap()): Result<JSONObject> {
        return request(endpoint, params).fold(
            onSuccess = { parseSubsonicResponse(it) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun parseSubsonicResponse(raw: String): Result<JSONObject> {
        return try {
            val root = JSONObject(raw)
            val wrapper = root.optJSONObject("subsonic-response")
                ?: return Result.failure(IllegalStateException("Invalid Subsonic response"))

            val status = wrapper.optString("status", "failed")
            if (status != "ok") {
                val error = wrapper.optJSONObject("error")
                val code = error?.optInt("code", -1) ?: -1
                val message = error?.optString("message", "Unknown error") ?: "Unknown error"
                return Result.failure(IllegalStateException("Subsonic error $code: $message"))
            }

            Result.success(wrapper)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildApiUrl(cred: Credentials, endpoint: String, params: Map<String, String>): String {
        val (token, salt) = generateAuthParams(cred.password)
        val builder = "${cred.serverUrl}/rest/$endpoint.view".toHttpUrl().newBuilder()
            .addQueryParameter("u", cred.username)
            .addQueryParameter("t", token)
            .addQueryParameter("s", salt)
            .addQueryParameter("v", API_VERSION)
            .addQueryParameter("c", CLIENT_ID)
            .addQueryParameter("f", "json")

        params.forEach { (key, value) ->
            builder.addQueryParameter(key, value)
        }

        return builder.build().toString()
    }

    private fun generateAuthParams(password: String): Pair<String, String> {
        val salt = UUID.randomUUID().toString().take(6)
        val token = md5(password + salt)
        return token to salt
    }

    private fun md5(value: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(value.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { "%02x".format(it) }
    }

    private fun loadCredentials(): Credentials? {
        val server = prefs.getString(KEY_SERVER_URL, null).orEmpty()
        val user = prefs.getString(KEY_USERNAME, null).orEmpty()
        val pass = prefs.getString(KEY_PASSWORD, null).orEmpty()

        if (server.isBlank() || user.isBlank() || pass.isBlank()) {
            return null
        }

        return Credentials(serverUrl = server, username = user, password = pass)
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
            return "Use https:// for remote Subsonic/Navidrome servers"
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
        private const val TAG = "SubsonicApiClient"
        private const val PREFS_NAME = "streaming_subsonic_credentials"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"

        private const val API_VERSION = "1.16.1"
        private const val CLIENT_ID = "Rhythm"
    }
}
