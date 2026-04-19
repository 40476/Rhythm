package chromahub.rhythm.app.features.streaming.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import chromahub.rhythm.app.core.domain.model.SourceType
import chromahub.rhythm.app.core.domain.model.StreamingConfig
import chromahub.rhythm.app.core.domain.model.StreamingQuality
import chromahub.rhythm.app.features.streaming.data.repository.StreamingMusicRepositoryImpl
import chromahub.rhythm.app.features.streaming.data.repository.StreamingServiceSession
import chromahub.rhythm.app.features.streaming.data.repository.StreamingServiceSessionRepository
import chromahub.rhythm.app.features.streaming.di.StreamingMusicModule
import chromahub.rhythm.app.features.streaming.domain.model.BrowseCategory
import chromahub.rhythm.app.features.streaming.domain.model.StreamingAlbum
import chromahub.rhythm.app.features.streaming.domain.model.StreamingArtist
import chromahub.rhythm.app.features.streaming.domain.model.StreamingPlaylist
import chromahub.rhythm.app.features.streaming.domain.model.StreamingServiceId
import chromahub.rhythm.app.features.streaming.domain.model.StreamingServiceRules
import chromahub.rhythm.app.features.streaming.domain.model.StreamingSong
import chromahub.rhythm.app.shared.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for managing streaming music playback and library.
 * Handles authentication, browsing, and playback for streaming services.
 */
class StreamingMusicViewModel(application: Application) : AndroidViewModel(application) {
    private val appSettings = AppSettings.getInstance(application)
    private val serviceSessionRepository = StreamingServiceSessionRepository(application)
    private val repository = StreamingMusicModule.provideStreamingMusicRepository(application)
    private val providerRepository = repository as? StreamingMusicRepositoryImpl
    private var playbackHandler: ((List<StreamingSong>, Int) -> Unit)? = null

    
    // Authentication state
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    val serviceSessions: StateFlow<Map<String, StreamingServiceSession>> = serviceSessionRepository.sessions
    
    private val _currentService = MutableStateFlow(SourceType.SUBSONIC)
    val currentService: StateFlow<SourceType> = _currentService.asStateFlow()
    
    private val _streamingConfig = MutableStateFlow(StreamingConfig())
    val streamingConfig: StateFlow<StreamingConfig> = _streamingConfig.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Home content
    private val _recommendations = MutableStateFlow<List<StreamingSong>>(emptyList())
    val recommendations: StateFlow<List<StreamingSong>> = _recommendations.asStateFlow()
    
    private val _newReleases = MutableStateFlow<List<StreamingAlbum>>(emptyList())
    val newReleases: StateFlow<List<StreamingAlbum>> = _newReleases.asStateFlow()
    
    private val _featuredPlaylists = MutableStateFlow<List<StreamingPlaylist>>(emptyList())
    val featuredPlaylists: StateFlow<List<StreamingPlaylist>> = _featuredPlaylists.asStateFlow()
    
    // Browse content
    private val _browseCategories = MutableStateFlow<List<BrowseCategory>>(emptyList())
    val browseCategories: StateFlow<List<BrowseCategory>> = _browseCategories.asStateFlow()
    
    private val _topCharts = MutableStateFlow<List<StreamingSong>>(emptyList())
    val topCharts: StateFlow<List<StreamingSong>> = _topCharts.asStateFlow()
    
    // Library content
    private val _likedSongs = MutableStateFlow<List<StreamingSong>>(emptyList())
    val likedSongs: StateFlow<List<StreamingSong>> = _likedSongs.asStateFlow()
    
    private val _savedAlbums = MutableStateFlow<List<StreamingAlbum>>(emptyList())
    val savedAlbums: StateFlow<List<StreamingAlbum>> = _savedAlbums.asStateFlow()
    
    private val _followedArtists = MutableStateFlow<List<StreamingArtist>>(emptyList())
    val followedArtists: StateFlow<List<StreamingArtist>> = _followedArtists.asStateFlow()
    
    private val _savedPlaylists = MutableStateFlow<List<StreamingPlaylist>>(emptyList())
    val savedPlaylists: StateFlow<List<StreamingPlaylist>> = _savedPlaylists.asStateFlow()
    
    private val _downloadedSongs = MutableStateFlow<List<StreamingSong>>(emptyList())
    val downloadedSongs: StateFlow<List<StreamingSong>> = _downloadedSongs.asStateFlow()
    
    // Current playback state
    private val _currentSong = MutableStateFlow<StreamingSong?>(null)
    val currentSong: StateFlow<StreamingSong?> = _currentSong.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    // Queue
    private val _queue = MutableStateFlow<List<StreamingSong>>(emptyList())
    val queue: StateFlow<List<StreamingSong>> = _queue.asStateFlow()
    
    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<StreamingSearchResults>(StreamingSearchResults())
    val searchResults: StateFlow<StreamingSearchResults> = _searchResults.asStateFlow()
    
    init {
        observeSelectedService()
    }

    private fun observeSelectedService() {
        viewModelScope.launch {
            appSettings.streamingService.collect { serviceId ->
                val normalizedServiceId = normalizeServiceId(serviceId)
                if (normalizedServiceId != serviceId) {
                    appSettings.setStreamingService(normalizedServiceId)
                    return@collect
                }

                _currentService.value = sourceTypeFromServiceId(normalizedServiceId)

                val connected = checkAndSyncAuthentication(normalizedServiceId)
                if (connected) {
                    loadHomeContent()
                } else {
                    clearContent()
                }
            }
        }
    }
    
    /**
     * Check if user is authenticated with the current service.
     */
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            checkAndSyncAuthentication()
        }
    }
    
    /**
     * Select a streaming service.
     */
    fun selectService(service: SourceType) {
        viewModelScope.launch {
            val serviceId = serviceIdFromSourceType(service)
            _currentService.value = service
            if (appSettings.streamingService.value != serviceId) {
                appSettings.setStreamingService(serviceId)
            }
            checkAndSyncAuthentication(serviceId)
        }
    }
    
    /**
     * Authenticate with the current streaming service.
     */
    fun authenticate() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                checkAndSyncAuthentication()
                if (!_isAuthenticated.value) {
                    _error.value = "Open service setup and connect an account first"
                }
            } catch (e: Exception) {
                _error.value = "Authentication failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Log out from the current streaming service.
     */
    fun logout() {
        val selectedService = appSettings.streamingService.value
        disconnectService(selectedService)
    }

    fun connectService(serviceId: String, serverUrl: String, username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val normalizedServiceId = normalizeServiceId(serviceId)
                validateCredentials(normalizedServiceId, serverUrl, username, password)

                val connection = providerRepository?.connect(
                    serviceId = normalizedServiceId,
                    serverUrl = serverUrl,
                    username = username,
                    password = password
                ) ?: throw IllegalStateException("Streaming repository is not initialized")

                serviceSessionRepository.connect(
                    serviceId = normalizedServiceId,
                    serverUrl = connection.serverUrl.trim(),
                    username = connection.displayName.trim()
                )
                if (appSettings.streamingService.value != normalizedServiceId) {
                    appSettings.setStreamingService(normalizedServiceId)
                }
                checkAndSyncAuthentication(normalizedServiceId)
                loadHomeContent()
            } catch (e: Exception) {
                _error.value = "Connection failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun disconnectService(serviceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val normalizedServiceId = normalizeServiceId(serviceId)
                providerRepository?.disconnect(normalizedServiceId)

                serviceSessionRepository.disconnect(normalizedServiceId)
                if (appSettings.streamingService.value == normalizedServiceId) {
                    checkAndSyncAuthentication(normalizedServiceId)
                    clearContent()
                }
            } catch (e: Exception) {
                _error.value = "Disconnect failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getServiceSession(serviceId: String): StreamingServiceSession {
        return serviceSessionRepository.getSession(serviceId)
    }

    fun setPlaybackHandler(handler: (List<StreamingSong>, Int) -> Unit) {
        playbackHandler = handler
    }
    
    /**
     * Load home screen content.
     */
    fun loadHomeContent() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                if (!checkAndSyncAuthentication()) {
                    clearContent()
                    return@launch
                }

                _recommendations.value = repository.getRecommendations(limit = 24)
                _newReleases.value = repository.getNewReleases(limit = 24)
                _featuredPlaylists.value = repository.getFeaturedPlaylists(limit = 24)
            } catch (e: Exception) {
                _error.value = "Failed to load content: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresh home screen content.
     */
    fun refreshHome() {
        loadHomeContent()
    }
    
    /**
     * Load browse categories.
     */
    fun loadBrowseCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                if (!checkAndSyncAuthentication()) {
                    _browseCategories.value = emptyList()
                    return@launch
                }

                _browseCategories.value = repository.getBrowseCategories()
            } catch (e: Exception) {
                _error.value = "Failed to load categories: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load top charts.
     */
    fun loadTopCharts() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                if (!checkAndSyncAuthentication()) {
                    _topCharts.value = emptyList()
                    return@launch
                }

                _topCharts.value = repository.getTopCharts()
            } catch (e: Exception) {
                _error.value = "Failed to load charts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load user's library content.
     */
    fun loadLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                if (!checkAndSyncAuthentication()) {
                    _likedSongs.value = emptyList()
                    _savedAlbums.value = emptyList()
                    _followedArtists.value = emptyList()
                    _savedPlaylists.value = emptyList()
                    _downloadedSongs.value = emptyList()
                    return@launch
                }

                _likedSongs.value = repository.getLikedSongs().first()
                _savedAlbums.value = repository.getSavedAlbums().first()
                _followedArtists.value = repository.getFollowedArtists().first()
                _downloadedSongs.value = repository.getDownloadedSongs().first()
            } catch (e: Exception) {
                _error.value = "Failed to load library: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Search across the streaming service.
     */
    fun search(query: String) {
        _searchQuery.value = query
        
        if (query.isBlank()) {
            _searchResults.value = StreamingSearchResults()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                if (!checkAndSyncAuthentication()) {
                    _searchResults.value = StreamingSearchResults()
                    _error.value = "Connect to a streaming service first"
                    return@launch
                }

                val songs = repository.searchSongs(query).filterIsInstance<StreamingSong>()
                val artistsFromRepository = repository.searchArtists(query).filterIsInstance<StreamingArtist>()
                val albumsFromRepository = repository.searchAlbums(query).filterIsInstance<StreamingAlbum>()
                val playlists = repository.searchPlaylists(query).filterIsInstance<StreamingPlaylist>()

                val derivedArtists = songs
                    .mapNotNull { song ->
                        if (song.artist.isBlank()) {
                            null
                        } else {
                            StreamingArtist(
                                id = "${song.sourceType.name}:artist:${song.artist.lowercase()}",
                                name = song.artist,
                                artworkUri = song.artworkUri,
                                songCount = 0,
                                albumCount = 0,
                                sourceType = song.sourceType
                            )
                        }
                    }
                    .distinctBy { it.id }

                val derivedAlbums = songs
                    .mapNotNull { song ->
                        if (song.album.isBlank()) {
                            null
                        } else {
                            StreamingAlbum(
                                id = "${song.sourceType.name}:album:${song.album.lowercase()}",
                                title = song.album,
                                artist = song.artist,
                                artworkUri = song.artworkUri,
                                songCount = 0,
                                year = null,
                                sourceType = song.sourceType
                            )
                        }
                    }
                    .distinctBy { it.id }

                _searchResults.value = StreamingSearchResults(
                    songs = songs,
                    albums = if (albumsFromRepository.isNotEmpty()) albumsFromRepository else derivedAlbums,
                    artists = if (artistsFromRepository.isNotEmpty()) artistsFromRepository else derivedArtists,
                    playlists = playlists
                )
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Play a streaming song.
     */
    fun playSong(song: StreamingSong) {
        viewModelScope.launch {
            if (!song.isPlayable) {
                _error.value = "This track is not available for playback"
                return@launch
            }

            if (!checkAndSyncAuthentication()) {
                _error.value = "Connect to a streaming service first"
                return@launch
            }

            val resolvedUrl = if (song.streamingUrl.isNullOrBlank()) {
                repository.getStreamingUrl(song.id)
            } else {
                song.streamingUrl
            }

            if (resolvedUrl.isNullOrBlank()) {
                _error.value = "Unable to resolve stream URL for this song"
                return@launch
            }

            val resolvedSong = song.copy(streamingUrl = resolvedUrl)

            val baseQueue = when {
                _queue.value.isNotEmpty() -> _queue.value
                _searchResults.value.songs.isNotEmpty() -> _searchResults.value.songs
                _recommendations.value.isNotEmpty() -> _recommendations.value
                else -> listOf(resolvedSong)
            }

            val queueWithResolvedSong = baseQueue.map {
                if (it.id == resolvedSong.id) resolvedSong else it
            }
            val selectedIndex = queueWithResolvedSong.indexOfFirst { it.id == resolvedSong.id }
                .takeIf { it >= 0 } ?: 0

            _queue.value = queueWithResolvedSong
            _currentSong.value = queueWithResolvedSong[selectedIndex]
            _isPlaying.value = true

            playbackHandler?.invoke(queueWithResolvedSong, selectedIndex)
        }
    }
    
    /**
     * Play an album.
     */
    fun playAlbum(album: StreamingAlbum) {
        viewModelScope.launch {
            val tracks = album.getTracks()
            if (tracks.isNotEmpty()) {
                _queue.value = tracks
                playSong(tracks.first())
            }
        }
    }
    
    /**
     * Play a playlist.
     */
    fun playPlaylist(playlist: StreamingPlaylist) {
        viewModelScope.launch {
            val tracks = playlist.getTracks()
            if (tracks.isNotEmpty()) {
                _queue.value = tracks
                playSong(tracks.first())
            }
        }
    }
    
    /**
     * Toggle play/pause.
     */
    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        // TODO: Connect to MediaPlaybackService
    }
    
    /**
     * Skip to next song.
     */
    fun skipToNext() {
        val currentIndex = _queue.value.indexOf(_currentSong.value)
        if (currentIndex >= 0 && currentIndex < _queue.value.size - 1) {
            playSong(_queue.value[currentIndex + 1])
        }
    }
    
    /**
     * Skip to previous song.
     */
    fun skipToPrevious() {
        val currentIndex = _queue.value.indexOf(_currentSong.value)
        if (currentIndex > 0) {
            playSong(_queue.value[currentIndex - 1])
        }
    }
    
    /**
     * Seek to a position.
     */
    fun seekTo(progress: Float) {
        _progress.value = progress
        // TODO: Connect to MediaPlaybackService
    }
    
    /**
     * Like/save a song.
     */
    fun likeSong(song: StreamingSong) {
        viewModelScope.launch {
            try {
                repository.likeSong(song.id)
                _likedSongs.value = repository.getLikedSongs().first()
            } catch (e: Exception) {
                _error.value = "Failed to save song: ${e.message}"
            }
        }
    }
    
    /**
     * Unlike/unsave a song.
     */
    fun unlikeSong(song: StreamingSong) {
        viewModelScope.launch {
            try {
                repository.unlikeSong(song.id)
                _likedSongs.value = repository.getLikedSongs().first()
            } catch (e: Exception) {
                _error.value = "Failed to remove song: ${e.message}"
            }
        }
    }
    
    /**
     * Follow an artist.
     */
    fun followArtist(artist: StreamingArtist) {
        viewModelScope.launch {
            try {
                repository.followArtist(artist.id)
                _followedArtists.value = repository.getFollowedArtists().first()
            } catch (e: Exception) {
                _error.value = "Failed to follow artist: ${e.message}"
            }
        }
    }
    
    /**
     * Save an album.
     */
    fun saveAlbum(album: StreamingAlbum) {
        viewModelScope.launch {
            try {
                repository.saveAlbum(album.id)
                _savedAlbums.value = repository.getSavedAlbums().first()
            } catch (e: Exception) {
                _error.value = "Failed to save album: ${e.message}"
            }
        }
    }
    
    /**
     * Download a song for offline playback.
     */
    fun downloadSong(song: StreamingSong) {
        viewModelScope.launch {
            try {
                repository.downloadSong(song.id)
                _downloadedSongs.value = repository.getDownloadedSongs().first()
            } catch (e: Exception) {
                _error.value = "Download failed: ${e.message}"
            }
        }
    }
    
    /**
     * Set streaming quality.
     */
    fun setStreamingQuality(quality: StreamingQuality) {
        viewModelScope.launch {
            _streamingConfig.value = _streamingConfig.value.copy(streamingQuality = quality)
            appSettings.setStreamingQuality(quality.name)
        }
    }
    
    /**
     * Clear all loaded content.
     */
    private fun clearContent() {
        _recommendations.value = emptyList()
        _newReleases.value = emptyList()
        _featuredPlaylists.value = emptyList()
        _browseCategories.value = emptyList()
        _topCharts.value = emptyList()
        _likedSongs.value = emptyList()
        _savedAlbums.value = emptyList()
        _followedArtists.value = emptyList()
        _savedPlaylists.value = emptyList()
        _downloadedSongs.value = emptyList()
        _queue.value = emptyList()
        _currentSong.value = null
    }
    
    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }

    private suspend fun checkAndSyncAuthentication(
        serviceId: String = appSettings.streamingService.value
    ): Boolean {
        val normalizedServiceId = normalizeServiceId(serviceId)
        val connected = providerRepository?.isServiceConnected(normalizedServiceId)
            ?: serviceSessionRepository.isConnected(normalizedServiceId)

        _isAuthenticated.value = connected
        _streamingConfig.value = _streamingConfig.value.copy(
            activeService = sourceTypeFromServiceId(normalizedServiceId),
            isAuthenticated = connected
        )
        return connected
    }

    private fun validateCredentials(
        serviceId: String,
        serverUrl: String,
        username: String,
        password: String
    ) {
        if (StreamingServiceRules.requiresServerUrl(serviceId) && serverUrl.isBlank()) {
            throw IllegalArgumentException("Server URL is required")
        }
        val requiresUsername = serviceId != StreamingServiceId.NETEASE_CLOUD_MUSIC &&
            serviceId != StreamingServiceId.QQ_MUSIC
        if (requiresUsername && username.isBlank()) {
            throw IllegalArgumentException("Username is required")
        }
        if (password.isBlank()) {
            throw IllegalArgumentException("Password is required")
        }
    }

    private fun sourceTypeFromServiceId(serviceId: String): SourceType {
        return when (serviceId.uppercase()) {
            StreamingServiceId.SUBSONIC -> SourceType.SUBSONIC
            StreamingServiceId.JELLYFIN -> SourceType.JELLYFIN
            StreamingServiceId.NETEASE_CLOUD_MUSIC -> SourceType.NETEASE_CLOUD_MUSIC
            StreamingServiceId.QQ_MUSIC -> SourceType.QQ_MUSIC
            else -> SourceType.UNKNOWN
        }
    }

    private fun serviceIdFromSourceType(sourceType: SourceType): String {
        return when (sourceType) {
            SourceType.SUBSONIC -> StreamingServiceId.SUBSONIC
            SourceType.JELLYFIN -> StreamingServiceId.JELLYFIN
            SourceType.NETEASE_CLOUD_MUSIC -> StreamingServiceId.NETEASE_CLOUD_MUSIC
            SourceType.QQ_MUSIC -> StreamingServiceId.QQ_MUSIC
            SourceType.SPOTIFY,
            SourceType.APPLE_MUSIC,
            SourceType.YOUTUBE_MUSIC,
            SourceType.DEEZER,
            SourceType.LOCAL,
            SourceType.UNKNOWN -> StreamingServiceId.SUBSONIC
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
}

/**
 * Container for streaming search results.
 */
data class StreamingSearchResults(
    val songs: List<StreamingSong> = emptyList(),
    val albums: List<StreamingAlbum> = emptyList(),
    val artists: List<StreamingArtist> = emptyList(),
    val playlists: List<StreamingPlaylist> = emptyList()
) {
    val isEmpty: Boolean
        get() = songs.isEmpty() && albums.isEmpty() && artists.isEmpty() && playlists.isEmpty()
    
    val totalCount: Int
        get() = songs.size + albums.size + artists.size + playlists.size
}
