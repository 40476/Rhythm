package chromahub.rhythm.app.features.streaming.data.provider

import android.content.Context
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.zip.InflaterInputStream

/**
 * QQ Music cookie-based API client.
 *
 * Search support is implemented by syncing songs from the user's playlists,
 * then filtering locally by query.
 */
class QqMusicApiClient(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    @Volatile
    private var persistedCookies: Map<String, String> = emptyMap()

    @Volatile
    private var cachedSongs: List<ProviderSong> = emptyList()

    @Volatile
    private var lastCacheUpdateMs: Long = 0L

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

        setPersistedCookies(parsedCookies)
        if (!hasLogin()) {
            return Result.failure(IllegalArgumentException("QQ login cookies are missing required fields"))
        }

        return withContext(Dispatchers.IO) {
            try {
                val nickname = fetchNickname().ifBlank {
                    fallbackDisplayName.ifBlank { "QQ Music user" }
                }

                prefs.edit()
                    .putString(KEY_COOKIES_JSON, JSONObject(parsedCookies).toString())
                    .putString(KEY_NICKNAME, nickname)
                    .apply()

                cachedSongs = emptyList()
                lastCacheUpdateMs = 0L

                Result.success(ProviderConnectionResult(displayName = nickname, serverUrl = ""))
            } catch (e: Exception) {
                Log.e(TAG, "QQ Music login failed", e)
                Result.failure(e)
            }
        }
    }

    fun logout() {
        cookieStore.clear()
        persistedCookies = emptyMap()
        cachedSongs = emptyList()
        lastCacheUpdateMs = 0L
        prefs.edit().clear().apply()
    }

    suspend fun searchSongs(query: String, limit: Int = 30): Result<List<ProviderSong>> {
        if (!hasLogin()) {
            return Result.failure(IllegalStateException("QQ Music service is not connected"))
        }

        return withContext(Dispatchers.IO) {
            runCatching {
                ensureLibraryCache()
                cachedSongs
                    .asSequence()
                    .filter { song ->
                        song.title.contains(query, ignoreCase = true) ||
                            song.artist.contains(query, ignoreCase = true) ||
                            song.album.contains(query, ignoreCase = true)
                    }
                    .take(limit.coerceIn(1, 200))
                    .toList()
            }
        }
    }

    suspend fun getSongUrl(songMid: String): Result<String> {
        if (!hasLogin()) {
            return Result.failure(IllegalStateException("QQ Music service is not connected"))
        }
        if (songMid.isBlank()) {
            return Result.failure(IllegalArgumentException("songMid is empty"))
        }

        return withContext(Dispatchers.IO) {
            runCatching {
                val defaultPurl = requestPurl(songMid)
                if (defaultPurl.isBlank()) {
                    throw IllegalStateException("No playable URL for songMid=$songMid")
                }

                val defaultUrl = if (defaultPurl.startsWith("http", true)) {
                    defaultPurl
                } else {
                    "https://ws.stream.qqmusic.qq.com/$defaultPurl"
                }

                val mediaMid = defaultPurl.substringBefore('?').drop(4).substringBefore('.')
                if (mediaMid.isNotBlank()) {
                    val mp3Filename = "M500${mediaMid}.mp3"
                    val mp3Purl = requestPurl(songMid, filename = mp3Filename)
                    if (mp3Purl.isNotBlank()) {
                        return@runCatching if (mp3Purl.startsWith("http", true)) {
                            mp3Purl
                        } else {
                            "https://ws.stream.qqmusic.qq.com/$mp3Purl"
                        }
                    }
                }

                defaultUrl
            }
        }
    }

    private suspend fun ensureLibraryCache() {
        val now = System.currentTimeMillis()
        if (cachedSongs.isNotEmpty() && now - lastCacheUpdateMs < CACHE_TTL_MS) {
            return
        }

        val playlists = fetchUserPlaylists()
        if (playlists.isEmpty()) {
            cachedSongs = emptyList()
            lastCacheUpdateMs = now
            return
        }

        val collected = linkedMapOf<String, ProviderSong>()
        playlists.take(MAX_PLAYLISTS_TO_SYNC).forEach { playlistId ->
            val tracks = fetchPlaylistSongs(playlistId)
            tracks.forEach { track ->
                collected.putIfAbsent(track.providerId, track)
            }
            if (collected.size >= MAX_SONGS_TO_CACHE) {
                return@forEach
            }
        }

        cachedSongs = collected.values.take(MAX_SONGS_TO_CACHE)
        lastCacheUpdateMs = System.currentTimeMillis()
    }

    private suspend fun fetchUserPlaylists(): List<Long> {
        val result = linkedSetOf<Long>()

        runCatching {
            val rawCreated = getUserCreatedPlaylists(start = 0, size = PLAYLIST_PAGE_SIZE)
            val rootCreated = JSONObject(rawCreated)
            val list = rootCreated.optJSONObject("data")?.optJSONArray("disslist")
            for (i in 0 until (list?.length() ?: 0)) {
                val item = list?.optJSONObject(i) ?: continue
                val id = item.optLong("tid", 0L)
                if (id > 0L) result.add(id)
            }
        }

        runCatching {
            val rawCollected = getUserPlaylists(start = 0, count = PLAYLIST_PAGE_SIZE)
            val rootCollected = JSONObject(rawCollected)
            val list = rootCollected.optJSONObject("data")?.optJSONArray("cdlist")
            for (i in 0 until (list?.length() ?: 0)) {
                val item = list?.optJSONObject(i) ?: continue
                val id = item.optLong("dissid", 0L)
                if (id > 0L) result.add(id)
            }
        }

        return result.toList()
    }

    private suspend fun fetchPlaylistSongs(playlistId: Long): List<ProviderSong> {
        return withContext(Dispatchers.IO) {
            val byId = linkedMapOf<String, ProviderSong>()
            var songBegin = 0
            var page = 0

            while (page < MAX_PAGES_PER_PLAYLIST && byId.size < MAX_SONGS_TO_CACHE) {
                val raw = getPlaylistDetail(playlistId = playlistId, songBegin = songBegin, songNum = PLAYLIST_SONG_PAGE_SIZE)
                val root = JSONObject(raw)
                if (root.optInt("code", -1) != 0) {
                    break
                }

                val cdlist = root.optJSONArray("cdlist") ?: break
                val firstCd = cdlist.optJSONObject(0) ?: break
                val songs = firstCd.optJSONArray("songlist") ?: break
                val fetched = songs.length()
                if (fetched == 0) break

                for (i in 0 until fetched) {
                    val track = songs.optJSONObject(i) ?: continue
                    val parsed = parseTrack(track)
                    if (parsed != null) {
                        byId.putIfAbsent(parsed.providerId, parsed)
                    }
                }

                songBegin += fetched
                val expected = firstCd.optInt("songnum", -1)
                val reachedExpected = expected > 0 && songBegin >= expected
                val hasMore = fetched >= PLAYLIST_SONG_PAGE_SIZE
                if (reachedExpected || !hasMore) {
                    break
                }

                page += 1
            }

            byId.values.toList()
        }
    }

    private fun parseTrack(track: JSONObject): ProviderSong? {
        val songMid = track.optString("songmid", track.optString("mid", "")).trim()
        if (songMid.isBlank()) return null

        val title = decodeBase64IfNeeded(
            track.optString("songname", track.optString("title", "Unknown title"))
        ).ifBlank { "Unknown title" }

        val singers = track.optJSONArray("singer")
        val artist = if (singers != null && singers.length() > 0) {
            buildString {
                for (i in 0 until singers.length()) {
                    val name = decodeBase64IfNeeded(singers.optJSONObject(i)?.optString("name", "").orEmpty())
                    if (name.isBlank()) continue
                    if (isNotEmpty()) append(", ")
                    append(name)
                }
            }.ifBlank { "Unknown artist" }
        } else {
            "Unknown artist"
        }

        val albumJson = track.optJSONObject("album")
        val album = decodeBase64IfNeeded(
            track.optString("albumname", albumJson?.optString("name", "") ?: "Unknown album")
        ).ifBlank { "Unknown album" }

        val albumMid = track.optString("albummid", albumJson?.optString("mid", "") ?: "")
        val artworkUrl = if (albumMid.isNotBlank()) {
            "https://y.qq.com/music/photo_new/T002R300x300M000${albumMid}.jpg"
        } else {
            null
        }

        val durationMs = track.optLong("interval", 0L).coerceAtLeast(0L) * 1000L

        return ProviderSong(
            providerId = songMid,
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            artworkUrl = artworkUrl
        )
    }

    private suspend fun requestPurl(songMid: String, filename: String? = null): String {
        val raw = getSongDownloadUrl(songMid, filename)
        val root = JSONObject(raw)
        return root
            .optJSONObject("req_0")
            ?.optJSONObject("data")
            ?.optJSONArray("midurlinfo")
            ?.optJSONObject(0)
            ?.optString("purl", "")
            .orEmpty()
    }

    private suspend fun getSongDownloadUrl(songMid: String, filename: String? = null): String {
        return withContext(Dispatchers.IO) {
            val uin = extractUin()
            val authst = extractAuthToken()

            val param = mutableMapOf<String, Any>(
                "guid" to "327783793guid",
                "songmid" to listOf(songMid),
                "songtype" to listOf(0),
                "uin" to uin,
                "loginflag" to 1,
                "platform" to "20",
                "xcdn" to 1
            )
            if (!filename.isNullOrBlank()) {
                param["filename"] = listOf(filename)
            }

            val payload = JSONObject(
                mapOf(
                    "req_0" to mapOf(
                        "module" to "music.vkey.GetEVkey",
                        "method" to "GetUrl",
                        "param" to param
                    ),
                    "comm" to mapOf(
                        "uin" to uin,
                        "format" to "json",
                        "ct" to 19,
                        "cv" to 1602,
                        "authst" to authst
                    )
                )
            )

            makePostRequest("https://u.y.qq.com/cgi-bin/musicu.fcg", payload)
        }
    }

    private suspend fun fetchNickname(): String {
        return withContext(Dispatchers.IO) {
            runCatching {
                val raw = getUserCreatedPlaylists(start = 0, size = 1)
                val root = JSONObject(raw)
                if (root.optInt("code", -1) != 0) {
                    return@runCatching ""
                }

                root.optJSONObject("data")?.optString("hostname", "").orEmpty()
            }.getOrElse { "" }
        }
    }

    private suspend fun getUserPlaylists(start: Int, count: Int): String {
        return withContext(Dispatchers.IO) {
            val uin = extractUin()
            val gtk = getGtk()
            val ein = (start + count - 1).coerceAtLeast(start)

            val url = "https://c.y.qq.com/fav/fcgi-bin/fcg_get_profile_order_asset.fcg?" +
                "format=json&inCharset=utf-8&outCharset=utf-8&notice=0" +
                "&platform=yqq&needNewCode=1" +
                "&uin=$uin&g_tk=$gtk&cid=205360956&userid=$uin&reqtype=3&sin=$start&ein=$ein"

            makeGetRequest(url)
        }
    }

    private suspend fun getUserCreatedPlaylists(start: Int, size: Int): String {
        return withContext(Dispatchers.IO) {
            val uin = extractUin()
            val gtk = getGtk()

            val url = "https://c6.y.qq.com/rsc/fcgi-bin/fcg_user_created_diss?" +
                "format=json&inCharset=utf-8&outCharset=utf-8&notice=0" +
                "&platform=yqq.json&needNewCode=1" +
                "&uin=$uin&g_tk=$gtk&g_tk_new_20200303=$gtk&hostuin=$uin&sin=$start&size=$size"

            makeGetRequest(url)
        }
    }

    private suspend fun getPlaylistDetail(playlistId: Long, songBegin: Int, songNum: Int): String {
        return withContext(Dispatchers.IO) {
            val gtk = getGtk()
            val url = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?" +
                "type=1&json=1&utf8=1&onlysong=0" +
                "&disstid=$playlistId&song_begin=$songBegin&song_num=$songNum" +
                "&g_tk=$gtk&format=json&inCharset=utf-8&outCharset=utf-8"

            makeGetRequest(url)
        }
    }

    private fun makeGetRequest(url: String): String {
        val requestBuilder = Request.Builder()
            .url(url)
            .header("Referer", "https://y.qq.com/")
            .header("User-Agent", "Mozilla/5.0")

        buildCookieHeader()?.let { requestBuilder.header("Cookie", it) }

        return okHttpClient.newCall(requestBuilder.build()).execute().use { response ->
            val bodyBytes = response.body?.bytes() ?: ByteArray(0)
            if (!response.isSuccessful) {
                throw IllegalStateException("QQ Music GET failed: HTTP ${response.code}")
            }
            decompressIfNeeded(bodyBytes)
        }
    }

    private fun makePostRequest(url: String, payload: JSONObject): String {
        val requestBuilder = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Referer", "https://y.qq.com/")
            .header("Origin", "https://y.qq.com")
            .header("User-Agent", "Mozilla/5.0")

        buildCookieHeader()?.let { requestBuilder.header("Cookie", it) }

        val request = requestBuilder
            .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        return okHttpClient.newCall(request).execute().use { response ->
            val bodyBytes = response.body?.bytes() ?: ByteArray(0)
            if (!response.isSuccessful) {
                throw IllegalStateException("QQ Music POST failed: HTTP ${response.code}")
            }
            decompressIfNeeded(bodyBytes)
        }
    }

    private fun decompressIfNeeded(data: ByteArray): String {
        if (data.isEmpty()) return ""

        val direct = String(data, StandardCharsets.UTF_8).trim()
        if (direct.startsWith("{") || direct.startsWith("[")) {
            return direct
        }

        return try {
            var offset = 0
            for (i in 0 until minOf(data.size, 10)) {
                if (data[i] == 0x78.toByte() && i + 1 < data.size) {
                    offset = i
                    break
                }
            }

            val zlibData = if (offset > 0) data.copyOfRange(offset, data.size) else data
            val inflater = InflaterInputStream(ByteArrayInputStream(zlibData))
            val output = ByteArrayOutputStream()
            inflater.copyTo(output)
            output.toString(StandardCharsets.UTF_8.name())
        } catch (e: Exception) {
            Log.w(TAG, "QQ Music response decompression failed; returning raw text", e)
            direct
        }
    }

    private fun decodeBase64IfNeeded(input: String): String {
        if (input.isBlank()) return input
        val base64Pattern = Regex("^[A-Za-z0-9+/=]+$")
        if (!base64Pattern.matches(input) || input.length < 4) {
            return input
        }

        return try {
            val decoded = Base64.decode(input, Base64.DEFAULT)
            val value = String(decoded, Charsets.UTF_8)
            if (value.isNotBlank() && !value.contains('\u0000')) value else input
        } catch (_: Exception) {
            input
        }
    }

    private fun buildCookieHeader(): String? {
        if (persistedCookies.isEmpty()) return null
        return persistedCookies.entries.joinToString(separator = "; ") { (key, value) -> "$key=$value" }
    }

    private fun setPersistedCookies(cookies: Map<String, String>) {
        persistedCookies = cookies.toMap()
        seedCookieJar("y.qq.com")
        seedCookieJar("u.y.qq.com")
        seedCookieJar("u6.y.qq.com")
        seedCookieJar("c.y.qq.com")
        seedCookieJar("c6.y.qq.com")
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

    private fun hasLogin(): Boolean {
        val uin = persistedCookies["uin"] ?: persistedCookies["p_uin"]
        val key = persistedCookies["qqmusic_key"] ?: persistedCookies["qm_keyst"]
        return !uin.isNullOrBlank() && !key.isNullOrBlank()
    }

    private fun extractUin(): String {
        val raw = persistedCookies["uin"]
            ?: persistedCookies["p_uin"]
            ?: persistedCookies["luin"]
            ?: persistedCookies["wxuin"]
            ?: "0"

        return raw.replace(Regex("[^0-9]"), "").ifBlank { "0" }
    }

    private fun extractAuthToken(): String {
        return persistedCookies["qm_keyst"]
            ?: persistedCookies["qqmusic_key"]
            ?: ""
    }

    private fun getGtk(): Long {
        val skey = persistedCookies["p_skey"] ?: persistedCookies["skey"] ?: ""
        var hash = 5381L
        for (char in skey) {
            hash += (hash shl 5) + char.code.toLong()
        }
        return hash and 0x7fffffffL
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
        private const val TAG = "QqMusicApiClient"
        private const val PREFS_NAME = "streaming_qqmusic_credentials"
        private const val KEY_COOKIES_JSON = "cookies_json"
        private const val KEY_NICKNAME = "nickname"

        private const val CACHE_TTL_MS = 5 * 60 * 1000L
        private const val PLAYLIST_PAGE_SIZE = 100
        private const val PLAYLIST_SONG_PAGE_SIZE = 500
        private const val MAX_PLAYLISTS_TO_SYNC = 40
        private const val MAX_PAGES_PER_PLAYLIST = 6
        private const val MAX_SONGS_TO_CACHE = 3000
    }
}
