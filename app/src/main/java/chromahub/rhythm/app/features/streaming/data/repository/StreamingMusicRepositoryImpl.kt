package chromahub.rhythm.app.features.streaming.data.repository

import android.content.Context
import chromahub.rhythm.app.core.domain.model.AlbumItem
import chromahub.rhythm.app.core.domain.model.ArtistItem
import chromahub.rhythm.app.core.domain.model.PlayableItem
import chromahub.rhythm.app.core.domain.model.PlaylistItem
import chromahub.rhythm.app.core.domain.model.SourceType
import chromahub.rhythm.app.features.streaming.data.provider.JellyfinApiClient
import chromahub.rhythm.app.features.streaming.data.provider.NeteaseApiClient
import chromahub.rhythm.app.features.streaming.data.provider.ProviderConnectionResult
import chromahub.rhythm.app.features.streaming.data.provider.ProviderSong
import chromahub.rhythm.app.features.streaming.data.provider.QqMusicApiClient
import chromahub.rhythm.app.features.streaming.data.provider.SubsonicApiClient
import chromahub.rhythm.app.features.streaming.domain.model.BrowseCategory
import chromahub.rhythm.app.features.streaming.domain.model.StreamingAlbum
import chromahub.rhythm.app.features.streaming.domain.model.StreamingArtist
import chromahub.rhythm.app.features.streaming.domain.model.StreamingPlaylist
import chromahub.rhythm.app.features.streaming.domain.model.StreamingServiceId
import chromahub.rhythm.app.features.streaming.domain.model.StreamingSong
import chromahub.rhythm.app.features.streaming.domain.repository.StreamingMusicRepository
import chromahub.rhythm.app.shared.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Provider-backed implementation used by Rhythm GO mode.
 */
class StreamingMusicRepositoryImpl(
    context: Context
) : StreamingMusicRepository {

    data class ServiceConnectionInfo(
        val displayName: String,
        val serverUrl: String
    )

    private val appSettings = AppSettings.getInstance(context)

    private val subsonicClient = SubsonicApiClient(context)
    private val jellyfinClient = JellyfinApiClient(context)
    private val neteaseClient = NeteaseApiClient(context)
    private val qqMusicClient = QqMusicApiClient(context)

    private val songsFlow = MutableStateFlow<List<PlayableItem>>(emptyList())
    private val albumsFlow = MutableStateFlow<List<AlbumItem>>(emptyList())
    private val artistsFlow = MutableStateFlow<List<ArtistItem>>(emptyList())
    private val playlistsFlow = MutableStateFlow<List<PlaylistItem>>(emptyList())

    private val likedSongsFlow = MutableStateFlow<List<StreamingSong>>(emptyList())
    private val followedArtistsFlow = MutableStateFlow<List<StreamingArtist>>(emptyList())
    private val savedAlbumsFlow = MutableStateFlow<List<StreamingAlbum>>(emptyList())
    private val downloadedSongsFlow = MutableStateFlow<List<StreamingSong>>(emptyList())

    private val likedSongIds = linkedSetOf<String>()
    private val followedArtistIds = linkedSetOf<String>()
    private val savedAlbumIds = linkedSetOf<String>()

    private val songCache = LinkedHashMap<String, StreamingSong>()

    override val currentService: SourceType
        get() = serviceToSourceType(activeServiceId())

    suspend fun connect(
        serviceId: String,
        serverUrl: String,
        username: String,
        password: String
    ): ServiceConnectionInfo {
        val normalizedService = normalizeServiceId(serviceId)

        val result = when (normalizedService) {
            StreamingServiceId.SUBSONIC -> subsonicClient.login(serverUrl, username, password)
            StreamingServiceId.JELLYFIN -> jellyfinClient.login(serverUrl, username, password)
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> {
                val cookieInput = resolveCookieInput(username, password)
                neteaseClient.login(cookieInput, username)
            }
            StreamingServiceId.QQ_MUSIC -> {
                val cookieInput = resolveCookieInput(username, password)
                qqMusicClient.login(cookieInput, username)
            }
            else -> Result.failure(IllegalArgumentException("Unsupported streaming service"))
        }

        val connection = result.getOrElse { throw it }
        return ServiceConnectionInfo(
            displayName = connection.displayName,
            serverUrl = connection.serverUrl
        )
    }

    suspend fun disconnect(serviceId: String) {
        when (normalizeServiceId(serviceId)) {
            StreamingServiceId.SUBSONIC -> subsonicClient.logout()
            StreamingServiceId.JELLYFIN -> jellyfinClient.logout()
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> neteaseClient.logout()
            StreamingServiceId.QQ_MUSIC -> qqMusicClient.logout()
        }

        if (activeServiceId() == normalizeServiceId(serviceId)) {
            clearInMemoryCatalog()
        }
    }

    fun isServiceConnected(serviceId: String): Boolean {
        return when (normalizeServiceId(serviceId)) {
            StreamingServiceId.SUBSONIC -> subsonicClient.isConnected()
            StreamingServiceId.JELLYFIN -> jellyfinClient.isConnected()
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> neteaseClient.isConnected()
            StreamingServiceId.QQ_MUSIC -> qqMusicClient.isConnected()
            else -> false
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return isServiceConnected(activeServiceId())
    }

    override suspend fun authenticate(): Boolean {
        return when (activeServiceId()) {
            StreamingServiceId.SUBSONIC -> subsonicClient.ping().isSuccess
            StreamingServiceId.JELLYFIN -> jellyfinClient.ping().isSuccess
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> neteaseClient.isConnected()
            StreamingServiceId.QQ_MUSIC -> qqMusicClient.isConnected()
            else -> false
        }
    }

    override suspend fun logout() {
        disconnect(activeServiceId())
    }

    override suspend fun getRecommendations(limit: Int): List<StreamingSong> {
        val activePrefix = "${activeServiceId()}::"
        return songCache.values
            .asSequence()
            .filter { it.id.startsWith(activePrefix) }
            .take(limit.coerceAtLeast(1))
            .toList()
    }

    override suspend fun getNewReleases(limit: Int): List<StreamingAlbum> {
        return albumsFlow.value
            .filterIsInstance<StreamingAlbum>()
            .take(limit.coerceAtLeast(1))
    }

    override suspend fun getFeaturedPlaylists(limit: Int): List<StreamingPlaylist> {
        return playlistsFlow.value
            .filterIsInstance<StreamingPlaylist>()
            .take(limit.coerceAtLeast(1))
    }

    override suspend fun getBrowseCategories(): List<BrowseCategory> {
        return listOf(
            BrowseCategory(id = "recent", name = "Recent"),
            BrowseCategory(id = "favorites", name = "Favorites"),
            BrowseCategory(id = "library", name = "Library")
        )
    }

    override suspend fun getCategoryPlaylists(categoryId: String, limit: Int): List<StreamingPlaylist> {
        return emptyList()
    }

    override suspend fun getTopCharts(limit: Int): List<StreamingSong> {
        return getRecommendations(limit)
    }

    override fun getLikedSongs(): Flow<List<StreamingSong>> = likedSongsFlow.asStateFlow()

    override suspend fun likeSong(songId: String): Boolean {
        likedSongIds.add(songId)
        updateLikedSongsFlow()
        return true
    }

    override suspend fun unlikeSong(songId: String): Boolean {
        val removed = likedSongIds.remove(songId)
        updateLikedSongsFlow()
        return removed
    }

    override suspend fun isSongLiked(songId: String): Boolean = likedSongIds.contains(songId)

    override suspend fun followArtist(artistId: String): Boolean {
        followedArtistIds.add(artistId)
        updateFollowedArtistsFlow()
        return true
    }

    override suspend fun unfollowArtist(artistId: String): Boolean {
        val removed = followedArtistIds.remove(artistId)
        updateFollowedArtistsFlow()
        return removed
    }

    override suspend fun isArtistFollowed(artistId: String): Boolean = followedArtistIds.contains(artistId)

    override fun getFollowedArtists(): Flow<List<StreamingArtist>> = followedArtistsFlow.asStateFlow()

    override suspend fun saveAlbum(albumId: String): Boolean {
        savedAlbumIds.add(albumId)
        updateSavedAlbumsFlow()
        return true
    }

    override suspend fun unsaveAlbum(albumId: String): Boolean {
        val removed = savedAlbumIds.remove(albumId)
        updateSavedAlbumsFlow()
        return removed
    }

    override fun getSavedAlbums(): Flow<List<StreamingAlbum>> = savedAlbumsFlow.asStateFlow()

    override suspend fun followPlaylist(playlistId: String): Boolean = false

    override suspend fun unfollowPlaylist(playlistId: String): Boolean = false

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        isPublic: Boolean
    ): StreamingPlaylist? = null

    override suspend fun addSongsToPlaylist(playlistId: String, songIds: List<String>): Boolean = false

    override suspend fun removeSongsFromPlaylist(playlistId: String, songIds: List<String>): Boolean = false

    override suspend fun getStreamingUrl(songId: String): String? {
        val (serviceId, providerId) = decodeSongId(songId) ?: return null
        val bitrate = desiredBitrateKbps()

        val resolved = when (serviceId) {
            StreamingServiceId.SUBSONIC -> subsonicClient.buildStreamUrl(providerId, bitrate)
            StreamingServiceId.JELLYFIN -> jellyfinClient.buildStreamUrl(providerId, bitrate)
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> {
                val neteaseId = providerId.toLongOrNull() ?: return null
                neteaseClient.getSongUrl(neteaseId, neteaseQualityLevel()).getOrNull()
            }
            StreamingServiceId.QQ_MUSIC -> qqMusicClient.getSongUrl(providerId).getOrNull()
            else -> null
        }

        if (!resolved.isNullOrBlank()) {
            val existing = songCache[songId]
            if (existing != null) {
                songCache[songId] = existing.copy(streamingUrl = resolved)
            }
        }

        return resolved
    }

    override suspend fun getRelatedTracks(songId: String, limit: Int): List<StreamingSong> = emptyList()

    override suspend fun getArtistTopTracks(artistId: String, limit: Int): List<StreamingSong> {
        return songsFlow.value
            .filterIsInstance<StreamingSong>()
            .filter { artistIdForSong(it) == artistId }
            .take(limit.coerceAtLeast(1))
    }

    override suspend fun getArtistAlbums(artistId: String): List<StreamingAlbum> {
        return albumsFlow.value
            .filterIsInstance<StreamingAlbum>()
            .filter { artistIdForAlbum(it) == artistId }
    }

    override suspend fun getRelatedArtists(artistId: String, limit: Int): List<StreamingArtist> = emptyList()

    override suspend fun downloadSong(songId: String): Boolean = false

    override suspend fun removeDownload(songId: String): Boolean = false

    override suspend fun isDownloaded(songId: String): Boolean = false

    override fun getDownloadedSongs(): Flow<List<StreamingSong>> = downloadedSongsFlow.asStateFlow()

    override suspend fun searchSongs(query: String): List<PlayableItem> {
        val serviceId = activeServiceId()
        if (!isServiceConnected(serviceId)) {
            clearInMemoryCatalog()
            return emptyList()
        }

        val providerResult: Result<List<ProviderSong>> = when (serviceId) {
            StreamingServiceId.SUBSONIC -> subsonicClient.searchSongs(query, SEARCH_LIMIT)
            StreamingServiceId.JELLYFIN -> jellyfinClient.searchSongs(query, SEARCH_LIMIT)
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> neteaseClient.searchSongs(query, SEARCH_LIMIT)
            StreamingServiceId.QQ_MUSIC -> qqMusicClient.searchSongs(query, SEARCH_LIMIT)
            else -> Result.success(emptyList())
        }

        val mappedSongs = providerResult.getOrElse { emptyList() }
            .map { mapProviderSong(serviceId, it) }

        refreshCatalog(mappedSongs)
        return mappedSongs
    }

    override suspend fun searchAlbums(query: String): List<AlbumItem> {
        val songs = searchSongs(query).filterIsInstance<StreamingSong>()
        return buildAlbumItems(activeServiceId(), songs)
    }

    override suspend fun searchArtists(query: String): List<ArtistItem> {
        val songs = searchSongs(query).filterIsInstance<StreamingSong>()
        return buildArtistItems(activeServiceId(), songs)
    }

    override suspend fun searchPlaylists(query: String): List<PlaylistItem> = emptyList()

    override fun getSongs(): Flow<List<PlayableItem>> = songsFlow.asStateFlow()

    override fun getAlbums(): Flow<List<AlbumItem>> = albumsFlow.asStateFlow()

    override fun getArtists(): Flow<List<ArtistItem>> = artistsFlow.asStateFlow()

    override fun getPlaylists(): Flow<List<PlaylistItem>> = playlistsFlow.asStateFlow()

    override suspend fun getSongById(id: String): PlayableItem? = songCache[id]

    override suspend fun getAlbumById(id: String): AlbumItem? {
        return albumsFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun getArtistById(id: String): ArtistItem? {
        return artistsFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun getPlaylistById(id: String): PlaylistItem? {
        return playlistsFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun getSongsForAlbum(albumId: String): List<PlayableItem> {
        return songsFlow.value
            .filterIsInstance<StreamingSong>()
            .filter { buildAlbumId(activeServiceId(), it.artist, it.album) == albumId }
    }

    private fun refreshCatalog(songs: List<StreamingSong>) {
        val serviceId = activeServiceId()

        songs.forEach { song ->
            songCache[song.id] = song
        }
        trimSongCache()

        songsFlow.value = songs
        albumsFlow.value = buildAlbumItems(serviceId, songs)
        artistsFlow.value = buildArtistItems(serviceId, songs)
        playlistsFlow.value = emptyList()

        updateLikedSongsFlow()
        updateSavedAlbumsFlow()
        updateFollowedArtistsFlow()
    }

    private fun clearInMemoryCatalog() {
        songsFlow.value = emptyList()
        albumsFlow.value = emptyList()
        artistsFlow.value = emptyList()
        playlistsFlow.value = emptyList()
    }

    private fun updateLikedSongsFlow() {
        likedSongsFlow.value = likedSongIds.mapNotNull { id -> songCache[id] }
    }

    private fun updateSavedAlbumsFlow() {
        savedAlbumsFlow.value = savedAlbumIds.mapNotNull { id ->
            albumsFlow.value.firstOrNull { it.id == id } as? StreamingAlbum
        }
    }

    private fun updateFollowedArtistsFlow() {
        followedArtistsFlow.value = followedArtistIds.mapNotNull { id ->
            artistsFlow.value.firstOrNull { it.id == id } as? StreamingArtist
        }
    }

    private fun mapProviderSong(serviceId: String, providerSong: ProviderSong): StreamingSong {
        val encodedId = encodeSongId(serviceId, providerSong.providerId)
        val sourceType = serviceToSourceType(serviceId)
        val streamingUrl = when (serviceId) {
            StreamingServiceId.SUBSONIC -> subsonicClient.buildStreamUrl(providerSong.providerId, desiredBitrateKbps())
            StreamingServiceId.JELLYFIN -> jellyfinClient.buildStreamUrl(providerSong.providerId, desiredBitrateKbps())
            else -> null
        }

        return StreamingSong(
            id = encodedId,
            title = providerSong.title,
            artist = providerSong.artist,
            album = providerSong.album,
            duration = providerSong.durationMs,
            artworkUri = providerSong.artworkUrl,
            sourceType = sourceType,
            streamingUrl = streamingUrl,
            previewUrl = null,
            isPlayable = true,
            externalId = providerSong.providerId
        )
    }

    private fun buildAlbumItems(serviceId: String, songs: List<StreamingSong>): List<StreamingAlbum> {
        return songs
            .groupBy { it.artist to it.album }
            .map { (key, tracks) ->
                val (artist, album) = key
                StreamingAlbum(
                    id = buildAlbumId(serviceId, artist, album),
                    title = album,
                    artist = artist,
                    artworkUri = tracks.firstOrNull()?.artworkUri,
                    songCount = tracks.size,
                    year = null,
                    sourceType = serviceToSourceType(serviceId)
                )
            }
            .sortedBy { it.title.lowercase() }
    }

    private fun buildArtistItems(serviceId: String, songs: List<StreamingSong>): List<StreamingArtist> {
        return songs
            .groupBy { it.artist }
            .map { (artistName, tracks) ->
                val albumCount = tracks.map { it.album }.distinct().size
                StreamingArtist(
                    id = buildArtistId(serviceId, artistName),
                    name = artistName,
                    artworkUri = tracks.firstOrNull()?.artworkUri,
                    songCount = tracks.size,
                    albumCount = albumCount,
                    sourceType = serviceToSourceType(serviceId)
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    private fun artistIdForSong(song: StreamingSong): String {
        val serviceId = decodeSongId(song.id)?.first ?: activeServiceId()
        return buildArtistId(serviceId, song.artist)
    }

    private fun artistIdForAlbum(album: StreamingAlbum): String {
        val serviceId = album.id.substringBefore("::", activeServiceId())
        return buildArtistId(serviceId, album.artist)
    }

    private fun buildArtistId(serviceId: String, artist: String): String {
        return "$serviceId::artist::${normalizeKey(artist)}"
    }

    private fun buildAlbumId(serviceId: String, artist: String, album: String): String {
        return "$serviceId::album::${normalizeKey(artist)}::${normalizeKey(album)}"
    }

    private fun normalizeKey(value: String): String {
        return value.trim().lowercase().replace("\\s+".toRegex(), "_")
    }

    private fun trimSongCache() {
        if (songCache.size <= MAX_CACHE_SIZE) return

        val toRemove = songCache.size - MAX_CACHE_SIZE
        repeat(toRemove) {
            val firstKey = songCache.entries.firstOrNull()?.key ?: return
            songCache.remove(firstKey)
        }
    }

    private fun activeServiceId(): String {
        return normalizeServiceId(appSettings.streamingService.value)
    }

    private fun encodeSongId(serviceId: String, providerId: String): String {
        return "$serviceId::$providerId"
    }

    private fun decodeSongId(songId: String): Pair<String, String>? {
        val separatorIndex = songId.indexOf("::")
        if (separatorIndex <= 0 || separatorIndex >= songId.length - 2) {
            return null
        }

        val serviceId = songId.substring(0, separatorIndex)
        val providerId = songId.substring(separatorIndex + 2)
        return serviceId to providerId
    }

    private fun desiredBitrateKbps(): Int {
        return when (appSettings.streamingQuality.value.uppercase()) {
            "LOW" -> 96
            "NORMAL" -> 160
            "HIGH" -> 320
            "LOSSLESS" -> 0
            else -> 320
        }
    }

    private fun neteaseQualityLevel(): String {
        return when (appSettings.streamingQuality.value.uppercase()) {
            "LOW" -> "standard"
            "NORMAL" -> "higher"
            "HIGH" -> "exhigh"
            "LOSSLESS" -> "lossless"
            else -> "exhigh"
        }
    }

    private fun resolveCookieInput(username: String, password: String): String {
        return when {
            looksLikeCookieBlob(password) -> password
            looksLikeCookieBlob(username) -> username
            else -> password
        }
    }

    private fun looksLikeCookieBlob(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.startsWith("{") || trimmed.contains('=')
    }

    private fun serviceToSourceType(serviceId: String): SourceType {
        return when (serviceId) {
            StreamingServiceId.SUBSONIC -> SourceType.SUBSONIC
            StreamingServiceId.JELLYFIN -> SourceType.JELLYFIN
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> SourceType.NETEASE_CLOUD_MUSIC
            StreamingServiceId.QQ_MUSIC -> SourceType.QQ_MUSIC
            else -> SourceType.UNKNOWN
        }
    }

    private fun normalizeServiceId(serviceId: String): String {
        val normalized = serviceId.uppercase()
        return if (StreamingServiceId.all.contains(normalized)) {
            normalized
        } else {
            StreamingServiceId.SUBSONIC
        }
    }

    private companion object {
        private const val SEARCH_LIMIT = 60
        private const val MAX_CACHE_SIZE = 4000
    }
}
