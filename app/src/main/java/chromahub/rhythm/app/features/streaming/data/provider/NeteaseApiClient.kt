package chromahub.rhythm.app.features.streaming.data.provider

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Netease Cloud Music API client.
 * Uses cookie-based authentication and Netease-specific encryption modes.
 */
class NeteaseApiClient(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    @Volatile
    private var persistedCookies: Map<String, String> = emptyMap()

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val host = url.host
                val hostCookies = cookieStore.getOrPut(host) { mutableListOf() }
                hostCookies.removeAll { existing -> cookies.any { it.name == existing.name } }
                hostCookies.addAll(cookies)
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host] ?: emptyList()
            }
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    init {
        val stored = prefs.getString(KEY_COOKIES_JSON, null)
        if (!stored.isNullOrBlank()) {
            parseCookieInput(stored)?.let { setPersistedCookies(it) }
        }
    }

    fun isConnected(): Boolean = hasLogin()

    fun getUsername(): String = prefs.getString(KEY_NICKNAME, null).orEmpty()

    fun getServerUrl(): String = ""

    suspend fun login(cookieInput: String, fallbackDisplayName: String): Result<ProviderConnectionResult> {
        val parsedCookies = parseCookieInput(cookieInput)
            ?: return Result.failure(IllegalArgumentException("Enter valid cookies (JSON or cookie header)"))

        if (parsedCookies["MUSIC_U"].isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("MUSIC_U cookie is required for Netease login"))
        }

        setPersistedCookies(parsedCookies)

        return withContext(Dispatchers.IO) {
            try {
                val profile = fetchCurrentProfile()
                val nickname = profile?.optString("nickname").orEmpty().ifBlank {
                    fallbackDisplayName.ifBlank { "Netease user" }
                }

                prefs.edit()
                    .putString(KEY_COOKIES_JSON, JSONObject(parsedCookies).toString())
                    .putString(KEY_NICKNAME, nickname)
                    .apply()

                Result.success(ProviderConnectionResult(displayName = nickname, serverUrl = ""))
            } catch (e: Exception) {
                Log.e(TAG, "Netease login failed", e)
                Result.failure(e)
            }
        }
    }

    fun logout() {
        cookieStore.clear()
        persistedCookies = emptyMap()
        prefs.edit().clear().apply()
    }

    suspend fun searchSongs(query: String, limit: Int = 30): Result<List<ProviderSong>> {
        if (!hasLogin()) {
            return Result.failure(IllegalStateException("Netease service is not connected"))
        }

        return withContext(Dispatchers.IO) {
            try {
                val params = mutableMapOf<String, Any>(
                    "s" to query,
                    "type" to "1",
                    "limit" to limit.coerceIn(1, 100).toString(),
                    "offset" to "0",
                    "total" to "true"
                )

                val raw = request(
                    url = "https://music.163.com/weapi/cloudsearch/get/web",
                    params = params,
                    mode = CryptoMode.WEAPI,
                    method = "POST",
                    usePersistedCookies = true
                )

                val root = JSONObject(raw)
                val songs = root.optJSONObject("result")?.optJSONArray("songs")

                val mapped = buildList {
                    for (i in 0 until (songs?.length() ?: 0)) {
                        val track = songs?.optJSONObject(i) ?: continue
                        val providerId = track.optLong("id").takeIf { it > 0L }?.toString() ?: continue

                        val artists = track.optJSONArray("ar")
                        val artist = buildString {
                            for (idx in 0 until (artists?.length() ?: 0)) {
                                if (idx > 0) append(", ")
                                append(artists?.optJSONObject(idx)?.optString("name", "") ?: "")
                            }
                        }.ifBlank { "Unknown artist" }

                        val album = track.optJSONObject("al")
                        add(
                            ProviderSong(
                                providerId = providerId,
                                title = track.optString("name", "Unknown title"),
                                artist = artist,
                                album = album?.optString("name", "Unknown album") ?: "Unknown album",
                                durationMs = track.optLong("dt", 0L),
                                artworkUrl = album?.optString("picUrl")
                            )
                        )
                    }
                }

                Result.success(mapped)
            } catch (e: Exception) {
                Log.e(TAG, "Netease search failed", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getSongUrl(songId: Long, qualityLevel: String): Result<String> {
        if (!hasLogin()) {
            return Result.failure(IllegalStateException("Netease service is not connected"))
        }

        return withContext(Dispatchers.IO) {
            runCatching {
                val levels = linkedSetOf(qualityLevel, "higher", "standard")
                var failureReason: String? = null

                for (level in levels) {
                    val raw = requestSongUrlRaw(songId, level)
                    val root = JSONObject(raw)
                    val code = root.optInt("code", -1)
                    if (code != 200) {
                        failureReason = "API code=$code for level=$level"
                        continue
                    }

                    val data = root.optJSONArray("data")
                    val entry = data?.optJSONObject(0)
                    val url = entry?.optString("url", "")
                    if (!url.isNullOrBlank() && url != "null") {
                        return@runCatching url
                    }

                    failureReason = "empty url for level=$level"
                }

                throw IllegalStateException("No playable Netease URL ($failureReason)")
            }
        }
    }

    private fun hasLogin(): Boolean = !persistedCookies["MUSIC_U"].isNullOrBlank()

    private fun setPersistedCookies(cookies: Map<String, String>) {
        val merged = cookies.toMutableMap()
        merged.putIfAbsent("os", "pc")
        merged.putIfAbsent("appver", "8.10.35")
        persistedCookies = merged.toMap()

        seedCookieJar("music.163.com")
        seedCookieJar("interface.music.163.com")
    }

    private fun seedCookieJar(host: String) {
        val list = cookieStore.getOrPut(host) { mutableListOf() }
        persistedCookies.forEach { (name, value) ->
            val cookie = Cookie.Builder()
                .name(name)
                .value(value)
                .domain(host)
                .path("/")
                .build()
            list.removeAll { it.name == name }
            list.add(cookie)
        }
    }

    private suspend fun fetchCurrentProfile(): JSONObject? {
        val raw = callWeApi("/w/nuser/account/get", emptyMap())
        val root = JSONObject(raw)
        if (root.optInt("code", -1) != 200) {
            return null
        }
        return root.optJSONObject("profile")
    }

    private suspend fun requestSongUrlRaw(songId: Long, level: String): String {
        val encodeType = if (level == "lossless" || level == "jyeffect") "flac" else "mp3"
        val params = mutableMapOf<String, Any>(
            "ids" to "[$songId]",
            "level" to level,
            "encodeType" to encodeType
        )

        return callEApi("/song/enhance/player/url/v1", params)
    }

    private suspend fun callWeApi(path: String, params: Map<String, Any>): String {
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        return request(
            url = "https://music.163.com/weapi$normalizedPath",
            params = params,
            mode = CryptoMode.WEAPI,
            method = "POST",
            usePersistedCookies = true
        )
    }

    private suspend fun callEApi(path: String, params: Map<String, Any>): String {
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        return request(
            url = "https://interface.music.163.com/eapi$normalizedPath",
            params = params,
            mode = CryptoMode.EAPI,
            method = "POST",
            usePersistedCookies = true
        )
    }

    private suspend fun request(
        url: String,
        params: Map<String, Any>,
        mode: CryptoMode,
        method: String,
        usePersistedCookies: Boolean
    ): String {
        return withContext(Dispatchers.IO) {
            val requestUrl = url.toHttpUrl()

            val encryptedParams: Map<String, String> = when (mode) {
                CryptoMode.WEAPI -> NeteaseEncryption.weApiEncrypt(params)
                CryptoMode.EAPI -> NeteaseEncryption.eApiEncrypt(requestUrl.encodedPath, params)
                CryptoMode.LINUX -> NeteaseEncryption.linuxApiEncrypt(params)
                CryptoMode.API -> params.mapValues { it.value.toString() }
            }

            var realUrl = requestUrl
            val builder = Request.Builder()
                .header("Accept", "*/*")
                .header("Accept-Language", "zh-CN,zh-Hans;q=0.9")
                .header("Connection", "keep-alive")
                .header("Referer", "https://music.163.com")
                .header("Host", requestUrl.host)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Rhythm)")

            if (usePersistedCookies) {
                buildCookieHeader()?.let { builder.header("Cookie", it) }
            }

            if (mode == CryptoMode.WEAPI) {
                val csrf = persistedCookies["__csrf"].orEmpty()
                realUrl = requestUrl.newBuilder()
                    .setQueryParameter("csrf_token", csrf)
                    .build()
            }

            builder.url(realUrl)

            when (method.uppercase(Locale.getDefault())) {
                "POST" -> {
                    val bodyBuilder = FormBody.Builder(StandardCharsets.UTF_8)
                    encryptedParams.forEach { (key, value) ->
                        bodyBuilder.add(key, value)
                    }
                    builder.post(bodyBuilder.build())
                }
                "GET" -> {
                    val urlBuilder = realUrl.newBuilder()
                    encryptedParams.forEach { (key, value) ->
                        urlBuilder.addQueryParameter(key, value)
                    }
                    builder.url(urlBuilder.build())
                }
                else -> throw IllegalArgumentException("Unsupported method: $method")
            }

            okHttpClient.newCall(builder.build()).execute().use { response ->
                val bytes = response.body?.bytes() ?: throw IllegalStateException("Empty response body")
                String(bytes, StandardCharsets.UTF_8)
            }
        }
    }

    private fun buildCookieHeader(): String? {
        if (persistedCookies.isEmpty()) return null
        return persistedCookies.entries.joinToString(separator = "; ") { (key, value) -> "$key=$value" }
    }

    private fun parseCookieInput(input: String): Map<String, String>? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return null

        return runCatching {
            if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                buildMap {
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        put(key, obj.optString(key, ""))
                    }
                }.filterValues { it.isNotBlank() }
            } else {
                trimmed.split(';')
                    .mapNotNull { token ->
                        val idx = token.indexOf('=')
                        if (idx <= 0) return@mapNotNull null
                        val key = token.substring(0, idx).trim()
                        val value = token.substring(idx + 1).trim()
                        if (key.isBlank() || value.isBlank()) null else key to value
                    }
                    .toMap()
            }
        }.getOrNull()?.takeIf { it.isNotEmpty() }
    }

    private companion object {
        private const val TAG = "NeteaseApiClient"
        private const val PREFS_NAME = "streaming_netease_credentials"
        private const val KEY_COOKIES_JSON = "cookies_json"
        private const val KEY_NICKNAME = "nickname"
    }
}
