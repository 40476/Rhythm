package chromahub.rhythm.app.features.local.presentation.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import chromahub.rhythm.app.ui.LocalMiniPlayerPadding
import androidx.compose.runtime.Composable
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.data.model.Album
import chromahub.rhythm.app.shared.data.model.Artist
import chromahub.rhythm.app.shared.data.model.Playlist
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.features.local.presentation.components.player.MiniPlayer
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import chromahub.rhythm.app.shared.data.model.AppSettings
import androidx.compose.material3.rememberModalBottomSheetState
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.AddToPlaylistBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.dialogs.CreatePlaylistDialog
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.AlbumBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.SongInfoBottomSheet
import chromahub.rhythm.app.util.ImageUtils
import chromahub.rhythm.app.util.M3ImageUtils
import chromahub.rhythm.app.util.GenreUtils
import chromahub.rhythm.app.util.HapticUtils
import chromahub.rhythm.app.util.ArtistSeparator
import chromahub.rhythm.app.shared.presentation.components.common.M3PlaceholderType
import chromahub.rhythm.app.shared.presentation.components.common.rememberExpressiveShapeFor
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveShapeTarget

// Experimental API opt-ins required for:
// - Material3 SearchBar APIs (DockedSearchBar, SearchBarDefaults) - stable in Material3 1.4.0
// - ModalBottomSheet, rememberModalBottomSheetState
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    musicViewModel: MusicViewModel,
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    playlists: List<Playlist>,
    currentSong: Song?,
    isPlaying: Boolean,
    progress: Float,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onPlayPause: () -> Unit,
    onPlayerClick: () -> Unit,
    onSkipNext: () -> Unit = {},
    onAddSongToPlaylist: (Song, String) -> Unit = { _, _ -> },
    onCreatePlaylist: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToArtist: (Artist) -> Unit = {}
) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val viewModel = musicViewModel
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current
    val isCompactWidth = configuration.screenWidthDp < 380
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilterOptions by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    var showAllSongsPage by remember { mutableStateOf(false) }
    val showKeyboardOnSearchOpen by appSettings.showKeyboardOnSearchOpen.collectAsState()
    val artistSeparatorEnabled by appSettings.artistSeparatorEnabled.collectAsState()
    val artistSeparatorDelimiters by appSettings.artistSeparatorDelimiters.collectAsState()
    var didAutoFocusOnEntry by remember { mutableStateOf(false) }
    
    // Filter states
    var filterSongs by remember { mutableStateOf(true) }
    var filterAlbums by remember { mutableStateOf(true) }
    var filterArtists by remember { mutableStateOf(true) }
    var filterPlaylists by remember { mutableStateOf(true) }
    
    // Collect search history from ViewModel
    val searchHistory by viewModel.searchHistory.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    
    // Enhanced search logic with better ranking and fuzzy matching
    val searchedSongs by remember(searchQuery, songs) {
        derivedStateOf {
            if (searchQuery.isBlank()) emptyList()
            else {
                val query = searchQuery.lowercase()
                songs.filter { song ->
                    listOf(song.title, song.artist, song.album)
                        .any { it.contains(query, ignoreCase = true) } ||
                        GenreUtils.matchesGenreQuery(song.genre, query)
                }.sortedWith(compareBy<Song> { song ->
                    val splitGenres = GenreUtils.splitGenres(song.genre)
                    // Prioritize exact matches in title
                    when {
                        song.title.lowercase() == query -> 0
                        song.title.lowercase().startsWith(query) -> 1
                        song.artist.lowercase() == query -> 2
                        song.artist.lowercase().startsWith(query) -> 3
                        song.album.lowercase() == query -> 4
                        song.album.lowercase().startsWith(query) -> 5
                        splitGenres.any { it.equals(query, ignoreCase = true) } -> 6
                        splitGenres.any { it.startsWith(query, ignoreCase = true) } -> 7
                        else -> 8
                    }
                }.thenBy { it.title })
            }
        }
    }

    val filteredSongs by remember(searchedSongs, filterSongs) {
        derivedStateOf {
            if (filterSongs) searchedSongs else emptyList()
        }
    }

    val searchedAlbums by remember(searchQuery, albums) {
        derivedStateOf {
            if (searchQuery.isBlank()) emptyList()
            else {
                val query = searchQuery.lowercase()
                albums.filter { album ->
                    listOf(album.title, album.artist)
                        .any { it.contains(query, ignoreCase = true) }
                }.sortedWith(compareBy<Album> { album ->
                    // Prioritize exact matches in title
                    when {
                        album.title.lowercase() == query -> 0
                        album.title.lowercase().startsWith(query) -> 1
                        album.artist.lowercase() == query -> 2
                        album.artist.lowercase().startsWith(query) -> 3
                        else -> 4
                    }
                }.thenBy { it.title })
            }
        }
    }

    val filteredAlbums by remember(searchedAlbums, filterAlbums) {
        derivedStateOf {
            if (filterAlbums) searchedAlbums else emptyList()
        }
    }
    
    // Artists are already processed by repository based on user preference
    val uniqueArtists = remember(artists) {
        artists
    }

    val resolveArtistFromSong: (Song) -> Artist? = remember(
        artists,
        artistSeparatorEnabled,
        artistSeparatorDelimiters
    ) {
        { song ->
            val delimiters = artistSeparatorDelimiters.ifBlank { "/;,+&" }
            val artistCandidates = ArtistSeparator.splitArtists(
                artistString = song.artist,
                delimiters = delimiters,
                enabled = artistSeparatorEnabled
            )

            artistCandidates.firstNotNullOfOrNull { candidate ->
                artists.find { it.name.equals(candidate, ignoreCase = true) }
            } ?: artists.find { it.name.equals(song.artist, ignoreCase = true) }
                ?: artistCandidates.firstOrNull()?.takeIf { it.isNotBlank() }?.let { fallback ->
                    Artist(id = fallback, name = fallback)
                }
        }
    }

    val searchedArtists by remember(searchQuery, uniqueArtists) {
        derivedStateOf {
            if (searchQuery.isBlank()) emptyList()
            else {
                val query = searchQuery.lowercase()
                uniqueArtists.filter { artist ->
                    artist.name.contains(query, ignoreCase = true)
                }.sortedWith(compareBy<Artist> { artist ->
                    // Prioritize exact matches and starts with
                    when {
                        artist.name.lowercase() == query -> 0
                        artist.name.lowercase().startsWith(query) -> 1
                        else -> 2
                    }
                }.thenBy { it.name })
            }
        }
    }

    val filteredArtists by remember(searchedArtists, filterArtists) {
        derivedStateOf {
            if (filterArtists) searchedArtists else emptyList()
        }
    }

    val searchedPlaylists by remember(searchQuery, playlists) {
        derivedStateOf {
            if (searchQuery.isBlank()) emptyList()
            else {
                val query = searchQuery.lowercase()
                playlists.filter { playlist ->
                    playlist.name.contains(query, ignoreCase = true)
                }.sortedWith(compareBy<Playlist> { playlist ->
                    // Prioritize exact matches and starts with
                    when {
                        playlist.name.lowercase() == query -> 0
                        playlist.name.lowercase().startsWith(query) -> 1
                        else -> 2
                    }
                }.thenBy { it.name })
            }
        }
    }

    val filteredPlaylists by remember(searchedPlaylists, filterPlaylists) {
        derivedStateOf {
            if (filterPlaylists) searchedPlaylists else emptyList()
        }
    }
    
    val totalResults = filteredSongs.size + filteredAlbums.size + filteredArtists.size + filteredPlaylists.size
    val hasSearchResults = totalResults > 0
    val hasNoResults = searchQuery.isNotEmpty() && totalResults == 0
    
    val scope = rememberCoroutineScope()
    
    // Bottom sheet + dialog state for add-to-playlist flow
    var showAddToPlaylistSheet by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    val addToPlaylistSheetState = rememberModalBottomSheetState()
    
    // Song options bottom sheet state (3-dot menu)
    var showSongOptionsSheet by remember { mutableStateOf(false) }
    
    // Album bottom sheet state
    var showAlbumBottomSheet by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    val albumBottomSheetState = rememberModalBottomSheetState()
    
    // Song info bottom sheet state
    var showSongInfoSheet by remember { mutableStateOf(false) }
    
    val haptics = LocalHapticFeedback.current

    val onSearchSongClick: (Song) -> Unit = { song ->
        if (searchQuery.isNotBlank()) {
            viewModel.addSearchQuery(searchQuery)
        }
        onSongClick(song)
    }
    
    // Screen entrance animation
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(50)
        showContent = true
    }

    LaunchedEffect(showContent, showKeyboardOnSearchOpen) {
        if (showContent && showKeyboardOnSearchOpen && !didAutoFocusOnEntry) {
            didAutoFocusOnEntry = true
            isSearchActive = true
            kotlinx.coroutines.delay(120)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "contentAlpha"
    )
    
    val contentOffset by animateFloatAsState(
        targetValue = if (showContent) 0f else 30f,
        animationSpec = tween(durationMillis = 450),
        label = "contentOffset"
    )

    val handleSearchBack: () -> Unit = {
        when {
            showAllSongsPage -> {
                showAllSongsPage = false
            }
            searchQuery.isNotEmpty() || showFilterOptions || isSearchActive -> {
                searchQuery = ""
                showFilterOptions = false
                showAllSongsPage = false
                isSearchActive = false
                focusManager.clearFocus()
                keyboardController?.hide()
            }
            else -> onBack()
        }
    }

    BackHandler(enabled = true) {
        handleSearchBack()
    }

    DisposableEffect(Unit) {
        val window = context.findActivity()?.window
        val originalSoftInputMode = window?.attributes?.softInputMode
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        onDispose {
            if (originalSoftInputMode != null) {
                window.setSoftInputMode(originalSoftInputMode)
            }
        }
    }

    val showFloatingFilters = showFilterOptions && searchQuery.isNotEmpty() && !showAllSongsPage
    val controlsScrimAlpha by animateFloatAsState(
        targetValue = if (showFloatingFilters) 0.98f else 0.92f,
        animationSpec = tween(durationMillis = 220),
        label = "search_controls_scrim_alpha"
    )
    val controlsScrimHeight = when {
        showFloatingFilters && isCompactWidth -> 300.dp
        showFloatingFilters -> 320.dp
        isCompactWidth -> 148.dp
        else -> 164.dp
    }
    val miniPlayerBottomPadding = LocalMiniPlayerPadding.current.calculateBottomPadding()
    val controlsBottomPadding = miniPlayerBottomPadding + if (isCompactWidth) 2.dp else 4.dp

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {}
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffset
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 4.dp)
            ) {
                when {
                    showAllSongsPage -> {
                        AllSongsPage(
                            songs = filteredSongs,
                            onSongClick = onSearchSongClick,
                            onAddSongToPlaylist = { song ->
                                selectedSong = song
                                showAddToPlaylistSheet = true
                            },
                            onSongMoreClick = { song ->
                                selectedSong = song
                                showSongOptionsSheet = true
                            },
                            onBack = { showAllSongsPage = false },
                            haptics = haptics
                        )
                    }

                    searchQuery.isEmpty() -> {
                        val recommendedSongs = remember(songs, recentlyPlayed) {
                            viewModel.getRecommendedSongs().take(6)
                        }
                        DefaultSearchContent(
                            songs = songs,
                            musicViewModel = viewModel,
                            searchHistory = searchHistory,
                            recentlyPlayed = recentlyPlayed,
                            recommendedSongs = recommendedSongs,
                            onSongClick = onSearchSongClick,
                            onSearchQuerySelect = { query ->
                                searchQuery = query
                                isSearchActive = true
                            },
                            onAddSongToPlaylist = { song ->
                                selectedSong = song
                                showAddToPlaylistSheet = true
                            },
                            onClearSearchHistory = {
                                viewModel.clearSearchHistory()
                            }
                        )
                    }

                    hasSearchResults -> {
                        AnimatedResultsContainer(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            SearchResults(
                                modifier = Modifier.fillMaxSize(),
                                songs = filteredSongs,
                                albums = filteredAlbums,
                                artists = filteredArtists,
                                playlists = filteredPlaylists,
                                searchQuery = searchQuery,
                                totalResults = totalResults,
                                onSongClick = onSearchSongClick,
                                onAlbumClick = onAlbumClick,
                                onArtistClick = onArtistClick,
                                onPlaylistClick = onPlaylistClick,
                                onAddSongToPlaylist = { song ->
                                    selectedSong = song
                                    showAddToPlaylistSheet = true
                                },
                                onSongMoreClick = { song ->
                                    selectedSong = song
                                    showSongOptionsSheet = true
                                },
                                onAlbumBottomSheetClick = { album ->
                                    selectedAlbum = album
                                    showAlbumBottomSheet = true
                                },
                                onArtistBottomSheetClick = { artist ->
                                    onNavigateToArtist(artist)
                                },
                                onViewAllSongsClick = { showAllSongsPage = true },
                                filterSection = {}
                            )
                        }
                    }

                    else -> {
                        NoSearchResults(
                            searchQuery = searchQuery,
                            hasActiveFilters = filterSongs || filterAlbums || filterArtists || filterPlaylists,
                            onEnableFilters = {
                                showFilterOptions = true
                                filterSongs = true
                                filterAlbums = true
                                filterArtists = true
                                filterPlaylists = true
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .padding(bottom = miniPlayerBottomPadding)
                    .height(controlsScrimHeight)
                    .graphicsLayer {
                        alpha = controlsScrimAlpha
                    }
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = if (isCompactWidth) 10.dp else 14.dp)
                    .padding(bottom = controlsBottomPadding),
                verticalArrangement = Arrangement.spacedBy(if (isCompactWidth) 8.dp else 10.dp)
            ) {
                AnimatedVisibility(
                    visible = showFloatingFilters,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = context.getString(R.string.search_filter_results),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                item {
                                    val allSelected = filterSongs && filterAlbums && filterArtists && filterPlaylists
                                    val noneSelected = !filterSongs && !filterAlbums && !filterArtists && !filterPlaylists

                                    FilterChip(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            if (allSelected || (!allSelected && !noneSelected)) {
                                                filterSongs = false
                                                filterAlbums = false
                                                filterArtists = false
                                                filterPlaylists = false
                                            } else {
                                                filterSongs = true
                                                filterAlbums = true
                                                filterArtists = true
                                                filterPlaylists = true
                                            }
                                        },
                                        label = { Text(if (allSelected) "Deselect All" else "Select All") },
                                        selected = allSelected,
                                        leadingIcon = if (allSelected) {
                                            { Icon(RhythmIcons.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    )
                                }

                                item {
                                    FilterChip(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            filterSongs = !filterSongs
                                        },
                                        label = { Text("Songs (${searchedSongs.size})") },
                                        selected = filterSongs,
                                        leadingIcon = if (filterSongs) {
                                            { Icon(RhythmIcons.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }

                                item {
                                    FilterChip(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            filterAlbums = !filterAlbums
                                        },
                                        label = { Text("Albums (${searchedAlbums.size})") },
                                        selected = filterAlbums,
                                        leadingIcon = if (filterAlbums) {
                                            { Icon(RhythmIcons.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }

                                item {
                                    FilterChip(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            filterArtists = !filterArtists
                                        },
                                        label = { Text("Artists (${searchedArtists.size})") },
                                        selected = filterArtists,
                                        leadingIcon = if (filterArtists) {
                                            { Icon(RhythmIcons.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }

                                item {
                                    FilterChip(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            filterPlaylists = !filterPlaylists
                                        },
                                        label = { Text("Playlists (${searchedPlaylists.size})") },
                                        selected = filterPlaylists,
                                        leadingIcon = if (filterPlaylists) {
                                            { Icon(RhythmIcons.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (totalResults == 0) "No results found" else "$totalResults results found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (totalResults == 0) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = if (totalResults == 0) FontWeight.Medium else FontWeight.Normal
                                )

                                if (totalResults > 0) {
                                    val activeFilters = listOf(
                                        "Songs" to filterSongs,
                                        "Albums" to filterAlbums,
                                        "Artists" to filterArtists,
                                        "Playlists" to filterPlaylists
                                    ).count { it.second }

                                    Text(
                                        text = "$activeFilters filters active",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isCompactWidth) 8.dp else 10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                    ) {
                        IconButton(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                handleSearchBack()
                            },
                            modifier = Modifier.size(if (isCompactWidth) 44.dp else 48.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = RhythmIcons.Back,
                                contentDescription = "Back",
                                modifier = Modifier.size(if (isCompactWidth) 18.dp else 20.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(if (isCompactWidth) 20.dp else 24.dp),
                        color = if (isSearchActive || searchQuery.isNotEmpty()) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSearchActive || searchQuery.isNotEmpty()) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            } else {
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            }
                        )
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { newQuery ->
                                searchQuery = newQuery
                                isSearchActive = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = if (isCompactWidth) 52.dp else 56.dp)
                                .padding(horizontal = if (isCompactWidth) 2.dp else 4.dp, vertical = 2.dp)
                                .focusRequester(focusRequester),
                            placeholder = {
                                Text(
                                    if (isCompactWidth) "Search songs, artists..." else "Search songs, artists, albums...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = RhythmIcons.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(if (isCompactWidth) 18.dp else 20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(if (isCompactWidth) 2.dp else 4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                                showFilterOptions = !showFilterOptions
                                            },
                                            modifier = Modifier.size(if (isCompactWidth) 36.dp else 40.dp),
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = if (showFilterOptions) {
                                                    MaterialTheme.colorScheme.secondaryContainer
                                                } else {
                                                    Color.Transparent
                                                },
                                                contentColor = if (showFilterOptions) {
                                                    MaterialTheme.colorScheme.onSecondaryContainer
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.FilterList,
                                                contentDescription = "Filters",
                                                modifier = Modifier.size(if (isCompactWidth) 16.dp else 18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                                searchQuery = ""
                                                isSearchActive = false
                                                showFilterOptions = false
                                                showAllSongsPage = false
                                                focusManager.clearFocus()
                                                keyboardController?.hide()
                                            },
                                            modifier = Modifier.size(if (isCompactWidth) 36.dp else 40.dp),
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        ) {
                                            Icon(
                                                imageVector = RhythmIcons.Close,
                                                contentDescription = "Clear search",
                                                modifier = Modifier.size(if (isCompactWidth) 16.dp else 18.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                focusManager.clearFocus()
                                if (searchQuery.isNotEmpty()) {
                                    viewModel.addSearchQuery(searchQuery)
                                }
                            }),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
    
    // Add-to-playlist bottom sheet
    if (showAddToPlaylistSheet && selectedSong != null) {
        AddToPlaylistBottomSheet(
            song = selectedSong!!,
            playlists = playlists,
            onDismissRequest = { showAddToPlaylistSheet = false },
            onAddToPlaylist = { playlist ->
                onAddSongToPlaylist(selectedSong!!, playlist.id ?: "")
                scope.launch {
                    addToPlaylistSheetState.hide()
                }.invokeOnCompletion {
                    if (!addToPlaylistSheetState.isVisible) {
                        showAddToPlaylistSheet = false
                    }
                }
            },
            onCreateNewPlaylist = {
                scope.launch { addToPlaylistSheetState.hide() }.invokeOnCompletion {
                    if (!addToPlaylistSheetState.isVisible) {
                        showAddToPlaylistSheet = false
                        showCreatePlaylistDialog = true
                    }
                }
            },
            sheetState = addToPlaylistSheetState
        )
    }
    
    // Album bottom sheet
    if (showAlbumBottomSheet && selectedAlbum != null) {
        AlbumBottomSheet(
            album = selectedAlbum!!,
            onDismiss = { 
                scope.launch {
                    albumBottomSheetState.hide()
                }.invokeOnCompletion {
                    if (!albumBottomSheetState.isVisible) {
                        showAlbumBottomSheet = false
                    }
                }
            },
            onSongClick = onSearchSongClick,
            onPlayAll = { songs -> 
                // Play all songs from the album with proper sorting
                if (songs.isNotEmpty()) {
                    viewModel.playQueueWithUserRule(songs, sourceLabel = "Search Album")
                } else {
                    onAlbumClick(selectedAlbum!!)
                }
            },
            onShufflePlay = { songs -> 
                // Play shuffled songs from the album (respects shuffle settings)
                if (songs.isNotEmpty()) {
                    viewModel.playShuffled(songs)
                } else {
                    onAlbumClick(selectedAlbum!!)
                }
            },  
            onAddToQueue = { song -> viewModel.addSongToQueue(song) },
            onAddSongToPlaylist = { song ->
                selectedSong = song
                showAddToPlaylistSheet = true
            },
            onPlayerClick = onPlayerClick,
            haptics = LocalHapticFeedback.current,
            sheetState = albumBottomSheetState,
            onPlayNext = { song -> viewModel.playNext(song) },
            onToggleFavorite = { song -> viewModel.toggleFavorite(song) },
            favoriteSongs = viewModel.favoriteSongs.collectAsState().value,
            onShowSongInfo = { song ->
                selectedSong = song
                showSongInfoSheet = true
            },
            onAddToBlacklist = { song ->
                val appSettings = AppSettings.getInstance(context)
                appSettings.addToBlacklist(song.id)
                Toast.makeText(context, "${song.title} added to blacklist", Toast.LENGTH_SHORT).show()
            },
            currentSong = currentSong,
            isPlaying = isPlaying
        )
    }
    
    // Song info bottom sheet
    if (showSongInfoSheet && selectedSong != null) {
        SongInfoBottomSheet(
            song = selectedSong,
            onDismiss = { showSongInfoSheet = false },
            appSettings = AppSettings.getInstance(context),
            onEditSong = { title, artist, album, genre, year, trackNumber, artworkUri, removeArtwork ->
                viewModel.saveMetadataChanges(
                    song = selectedSong!!,
                    title = title,
                    artist = artist,
                    album = album,
                    genre = genre,
                    year = year,
                    trackNumber = trackNumber,
                    artworkUri = artworkUri,
                    removeArtwork = removeArtwork,
                    onSuccess = { fileWriteSucceeded ->
                        if (fileWriteSucceeded) {
                            Toast.makeText(context, "Metadata saved successfully to file!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    },
                    onPermissionRequired = { pendingRequest ->
                        Toast.makeText(context, "Permission required to modify file metadata", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
    
    // Song options bottom sheet (3-dot menu)
    if (showSongOptionsSheet && selectedSong != null) {
        SearchSongOptionsBottomSheet(
            song = selectedSong!!,
            onDismiss = { showSongOptionsSheet = false },
            onPlayNext = {
                viewModel.playNext(selectedSong!!)
                showSongOptionsSheet = false
                Toast.makeText(context, "${selectedSong!!.title} will play next", Toast.LENGTH_SHORT).show()
            },
            onAddToQueue = {
                viewModel.addSongToQueue(selectedSong!!)
                showSongOptionsSheet = false
                Toast.makeText(context, "${selectedSong!!.title} added to queue", Toast.LENGTH_SHORT).show()
            },
            onAddToPlaylist = {
                showSongOptionsSheet = false
                showAddToPlaylistSheet = true
            },
            onToggleFavorite = {
                viewModel.toggleFavorite(selectedSong!!)
                showSongOptionsSheet = false
            },
            isFavorite = viewModel.favoriteSongs.collectAsState().value.contains(selectedSong!!.id),
            onShowSongInfo = {
                showSongOptionsSheet = false
                showSongInfoSheet = true
            },
            onGoToAlbum = {
                showSongOptionsSheet = false
                val album = albums.find { it.title == selectedSong!!.album }
                if (album != null) {
                    selectedAlbum = album
                    showAlbumBottomSheet = true
                } else {
                    Toast.makeText(context, "Album not found", Toast.LENGTH_SHORT).show()
                }
            },
            onGoToArtist = {
                showSongOptionsSheet = false
                val artist = resolveArtistFromSong(selectedSong!!)
                if (artist != null) {
                    onNavigateToArtist(artist)
                } else {
                    Toast.makeText(context, "Artist not found", Toast.LENGTH_SHORT).show()
                }
            },
            onAddToBlacklist = {
                val appSettings = AppSettings.getInstance(context)
                appSettings.addToBlacklist(selectedSong!!.id)
                showSongOptionsSheet = false
                Toast.makeText(context, "${selectedSong!!.title} added to blacklist", Toast.LENGTH_SHORT).show()
            },
            haptics = haptics
        )
    }
    
    // Create playlist dialog
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onConfirm = { name ->
                onCreatePlaylist(name)
                showCreatePlaylistDialog = false
            }
        )
    }
}

@Composable
private fun AnimatedResultsContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val containerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "results_container_alpha"
    )
    val containerOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 22f,
        animationSpec = tween(durationMillis = 320),
        label = "results_container_offset"
    )

    Card(
        modifier = modifier.graphicsLayer {
            alpha = containerAlpha
            translationY = containerOffsetY
        },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SearchResults(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    playlists: List<Playlist>,
    searchQuery: String,
    totalResults: Int,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onAddSongToPlaylist: (Song) -> Unit,
    onSongMoreClick: (Song) -> Unit = {},
    onAlbumBottomSheetClick: (Album) -> Unit = {}, // New parameter
    onArtistBottomSheetClick: (Artist) -> Unit = {},
    onViewAllSongsClick: () -> Unit,
    filterSection: @Composable () -> Unit // Add this parameter
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val miniPlayerBottomPadding = LocalMiniPlayerPadding.current.calculateBottomPadding()
    val contentBottomPadding = (miniPlayerBottomPadding + 20.dp).coerceAtLeast(96.dp)

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = contentBottomPadding
        )
    ) {
        // Filter section
        item {
            filterSection()
        }

        // Results header with improved design
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(12.dp)
                        )
                    }
                    
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = context.getString(R.string.search_results),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$totalResults items found for \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Songs section
        if (songs.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = RhythmIcons.Song,
                                contentDescription = "Songs",
                                
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.search_songs),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${songs.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val visibleSongs = songs.take(5)
                        visibleSongs.forEachIndexed { index, song ->
                            SearchSongItem(
                                song = song,
                                onClick = { onSongClick(song) },
                                onMoreClick = {
                                    onSongMoreClick(song)
                                },
                                onAddToPlaylist = { onAddSongToPlaylist(song) },
                                haptics = haptics,
                                index = index,
                                totalCount = visibleSongs.size
                            )
                        }
                    }

                    if (songs.size > 5) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onViewAllSongsClick()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = context.getString(R.string.search_view_all_songs),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = context.getString(R.string.search_see_all_songs, songs.size),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                                Icon(
                                    imageVector = RhythmIcons.Forward,
                                    contentDescription = "View all",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Albums section
        if (albums.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = RhythmIcons.Album,
                                contentDescription = "Albums",
                                
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.search_albums),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${albums.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(
                            items = albums.take(10),
                            key = { "album_${it.id}" },
                            contentType = { "album" }
                        ) { album ->
                            SearchAlbumItem(
                                album = album,
                                onClick = { onAlbumBottomSheetClick(album) } // Use the new lambda
                            )
                        }
                    }
                }
            }
        }
        
        // Artists section
        if (artists.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = RhythmIcons.Artist,
                                contentDescription = "Artists",
                                
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.search_artists),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${artists.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(
                            items = artists.take(10),
                            key = { "artist_${it.id}" },
                            contentType = { "artist" }
                        ) { artist ->
                            SearchArtistItem(
                                artist = artist,
                                onClick = {
                                    onArtistBottomSheetClick(artist)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Playlists section
        if (playlists.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = RhythmIcons.PlaylistFilled,
                                contentDescription = "Playlists",
                                
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.search_playlists),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${playlists.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(
                            items = playlists.take(10),
                            key = { "playlist_${it.id}" },
                            contentType = { "playlist" }
                        ) { playlist ->
                            SearchPlaylistItem(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist) }
                            )
                        }
                    }
                }
            }
        }
        
    }
}

@Composable
fun SearchBrowseContent(
    albums: List<Album>,
    artists: List<Artist>,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    searchHistory: List<String> = emptyList(),
    onSearchHistoryClick: (String) -> Unit = {},
    onClearSearchHistory: () -> Unit = {},
    recentlyPlayed: List<Song> = emptyList(),
    focusSongs: List<Song> = emptyList(),
    energeticSongs: List<Song> = emptyList(),
    relaxingSongs: List<Song> = emptyList(),
    onSongClick: (Song) -> Unit = {},
    onArtistBottomSheetClick: (Artist) -> Unit = {}
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    // Make entire content scrollable
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp), // Bottom padding for navigation
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Recently searched section with improved styling
            if (searchHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(R.string.search_recently_searched),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    
                    IconButton(
                        onClick = onClearSearchHistory,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = context.getString(R.string.search_clear),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Enhanced search history items
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    searchHistory.forEach { searchTerm ->
                        RecentSearchItem(
                            searchTerm = searchTerm,
                            onClick = { onSearchHistoryClick(searchTerm) }
                        )
                    }
                }
            } else {
                // Improved empty state for search history
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(R.string.search_recently_searched),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(56.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = context.getString(R.string.search_no_recent),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        
        // Mood & Moments and Recently Played (shared design)
        if (recentlyPlayed.isNotEmpty()) {
            item {
                RecentlyPlayedSection(recentlyPlayed = recentlyPlayed, onSongClick = onSongClick)
            }
        } else {
            // Empty state for recently played in SearchBrowseContent
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.Queue,
                                contentDescription = null,
                                
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = context.getString(R.string.search_no_recently_played),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = context.getString(R.string.search_no_recently_played_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Add bottom spacing
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun RecentSearchItem(
    searchTerm: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search icon in a circular container with enhanced styling
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RhythmIcons.Search,
                    contentDescription = null,
                    
                    modifier = Modifier.size(22.dp)
                )
            }
            
            // Search term with improved typography
            Text(
                text = searchTerm,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
            
            // Enhanced forward icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RhythmIcons.Forward,
                    contentDescription = "Search",
                    
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSongItem(
    song: Song,
    onClick: () -> Unit,
    onMoreClick: () -> Unit = {},
    onAddToPlaylist: (Song) -> Unit = {},
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    index: Int = 0,
    totalCount: Int = 1
) {
    val context = LocalContext.current
    
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = groupedSongItemShape(index, totalCount),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art - use M3ImageUtils.TrackImage with expressive shape from settings
            M3ImageUtils.TrackImage(
                imageUrl = song.artworkUri,
                trackName = song.title,
                modifier = Modifier.size(48.dp),
                applyExpressiveShape = true
            )
            
            // Song info with reduced spacing and font sizes
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "${song.artist} • ${song.album}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 3-dot menu button with reduced size
            FilledIconButton(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onMoreClick()
                },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Icon(
                    imageVector = RhythmIcons.More,
                    contentDescription = "More options",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun groupedSongItemShape(index: Int, totalCount: Int): RoundedCornerShape {
    return when {
        totalCount <= 1 -> RoundedCornerShape(24.dp)
        index == 0 -> RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 6.dp,
            bottomEnd = 6.dp
        )
        index == totalCount - 1 -> RoundedCornerShape(
            topStart = 6.dp,
            topEnd = 6.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )
        else -> RoundedCornerShape(6.dp)
    }
}

@Composable
fun SearchAlbumItem(
    album: Album,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                onClick()
            }),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Album cover - use M3ImageUtils.AlbumArt with expressive shape from settings
            M3ImageUtils.AlbumArt(
                imageUrl = album.artworkUri,
                albumName = album.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                applyExpressiveShape = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Album info
            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = album.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SearchArtistItem(
    artist: Artist,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                onClick()
            }),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Artist image with expressive shape from settings
            M3ImageUtils.ArtistImage(
                imageUrl = artist.artworkUri,
                artistName = artist.name,
                modifier = Modifier
                    .size(96.dp)
                    .padding(4.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = artist.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GenreCard(
    genre: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        modifier = Modifier.clickable(onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        })
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = genre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SearchPlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier
            .width(150.dp)
            .padding(vertical = 6.dp), // Added vertical padding for consistency
        shape = RoundedCornerShape(20.dp), // Increased radius
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Playlist artwork - use M3ImageUtils.PlaylistImage with expressive shape from settings
            if (playlist.artworkUri != null) {
                M3ImageUtils.PlaylistImage(
                    imageUrl = playlist.artworkUri,
                    playlistName = playlist.name,
                    modifier = Modifier.size(68.dp)
                )
            } else {
                // Placeholder when no artwork
                Surface(
                    modifier = Modifier.size(68.dp),
                    shape = RoundedCornerShape(25.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = RhythmIcons.PlaylistFilled,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium, // Changed to titleMedium
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "${playlist.songs.size} songs",
                style = MaterialTheme.typography.bodyMedium, // Changed to bodyMedium
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Use onSurfaceVariant color
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clickable(onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                onClick()
            })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Category icon with tonal container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Category title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun RecommendedAlbumItem(
    album: Album,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                onClick()
            })
    ) {
        // Album art with Material 3 card styling
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .apply(ImageUtils.buildImageRequest(
                            album.artworkUri,
                            album.title,
                            LocalContext.current.cacheDir,
                            M3PlaceholderType.ALBUM
                        ))
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Album info
        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = album.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MoodCard(
    title: String,
    description: String,
    backgroundColor: Color,
    contentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .clickable {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                onClick()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Surface(
                shape = CircleShape,
                color = contentColor.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                        onClick()
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = RhythmIcons.Play,
                        contentDescription = "Play",
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ---------- Shared composables copied from NewHomeScreen ----------
@Composable
private fun RecentlyPlayedSection(
    recentlyPlayed: List<Song>,
    onSongClick: (Song) -> Unit
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = RhythmIcons.Queue,
                        contentDescription = null,
                        
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = context.getString(R.string.search_recently_played),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                
                // Show count badge
                if (recentlyPlayed.size > 8) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "${recentlyPlayed.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(
                    items = recentlyPlayed.take(8),
                    key = { "recent_${it.id}" },
                    contentType = { "song" }
                ) { song ->
                    EnhancedRecentChip(song = song, onClick = { onSongClick(song) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedRecentChip(
    song: Song,
    onClick: () -> Unit,
    onMoreClick: (Song) -> Unit = {}
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(160.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .apply(ImageUtils.buildImageRequest(
                            song.artworkUri,
                            song.title,
                            context.cacheDir,
                            M3PlaceholderType.TRACK
                        ))
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DefaultSearchContent(
    songs: List<Song>,
    musicViewModel: MusicViewModel,
    searchHistory: List<String>,
    recentlyPlayed: List<Song>,
    recommendedSongs: List<Song>,
    onSongClick: (Song) -> Unit,
    onSearchQuerySelect: (String) -> Unit,
    onAddSongToPlaylist: (Song) -> Unit,
    onClearSearchHistory: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current // Get the HapticFeedback instance
    val isCompactWidth = LocalConfiguration.current.screenWidthDp < 380
    val miniPlayerBottomPadding = LocalMiniPlayerPadding.current.calculateBottomPadding()
    val bottomControlsClearance = if (isCompactWidth) 172.dp else 188.dp
    val contentBottomPadding = miniPlayerBottomPadding + bottomControlsClearance

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = contentBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search History with improved design
        item {
            if (searchHistory.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = RhythmIcons.Search,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = context.getString(R.string.search_recent_searches),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                            
                            FilledTonalButton(
                                onClick = onClearSearchHistory,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(searchHistory.take(6), key = { it }) { query ->
                                Card(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        onSearchQuerySelect(query)
                                    },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.wrapContentWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = RhythmIcons.Search,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = query,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Empty state card for no recent searches
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.Search,
                                contentDescription = null,
                                
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = context.getString(R.string.search_no_recent_searches),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = context.getString(R.string.search_no_recent_searches_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Recently Played using the NewHomeScreen widget style
        if (recentlyPlayed.isNotEmpty()) {
            item {
                RecentlyPlayedSection(
                    recentlyPlayed = recentlyPlayed,
                    onSongClick = onSongClick
                )
            }
        } else {
            // Empty state for recently played
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.Queue,
                                contentDescription = null,
                                
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = context.getString(R.string.search_no_recently_played),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = context.getString(R.string.search_no_recently_played_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Genre-based Search/Browse section
        item {
            GenreBrowseSection(
                songs = songs,
                musicViewModel = musicViewModel,
                onGenreClick = { genre ->
                    onSearchQuerySelect(genre)
                }
            )
        }
        
        if (recommendedSongs.isNotEmpty()) {
            item {
                RecommendedForYouSection(
                    songs = recommendedSongs,
                    onSongClick = onSongClick
                )
            }
        }
    }
}

@Composable
private fun NoSearchResults(
    searchQuery: String,
    hasActiveFilters: Boolean,
    onEnableFilters: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val animatedSize by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = 100f
            ),
            label = "iconAnimation"
        )
        
        // Enhanced empty state with gradient background
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = RhythmIcons.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(60.dp * animatedSize)
                        .graphicsLayer(
                            scaleX = animatedSize,
                            scaleY = animatedSize
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (hasActiveFilters)
                "No Results Found"
            else
                "All Filters Disabled",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = if (hasActiveFilters)
                "We couldn't find anything matching \"$searchQuery\". Try adjusting your search terms or filters."
            else
                "Enable at least one filter to search your music library.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
        )
        
        if (!hasActiveFilters) {
            Spacer(modifier = Modifier.height(24.dp))
            FilledTonalButton(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                    onEnableFilters()
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enable Filters", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search Tips",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val tips = listOf(
                        "Try different keywords",
                        "Check your spelling",
                        "Use fewer words",
                        "Try searching by artist or album"
                    )
                    
                    tips.forEach { tip ->
                        Text(
                            text = "• $tip",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendedForYouSection(
    songs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    val context = LocalContext.current
    if (songs.isEmpty()) return
    val sectionShape = RoundedCornerShape(20.dp)

    Card(
        shape = sectionShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.82f)
                        )
                    ),
                    shape = sectionShape
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = context.getString(R.string.search_recommended),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = context.getString(R.string.search_recommended_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 6.dp)
                ) {
                    items(
                        items = songs,
                        key = { "recommended_${it.id}" },
                        contentType = { "recommended_song" }
                    ) { song ->
                        RecommendedSongItem(
                            song = song,
                            onClick = { onSongClick(song) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendedSongItem(
    song: Song,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    val recommendationShape = RoundedCornerShape(20.dp)
    val songArtShape = rememberExpressiveShapeFor(
        target = ExpressiveShapeTarget.SONG_ART,
        fallbackShape = RoundedCornerShape(14.dp)
    )
    val controlShape = CircleShape

    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        },
        shape = recommendationShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
        ),
        modifier = Modifier.width(220.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .apply(
                        ImageUtils.buildImageRequest(
                            song.artworkUri,
                            song.title,
                            context.cacheDir,
                            M3PlaceholderType.TRACK
                        )
                    )
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(songArtShape)
            )

            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = controlShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 0.dp
                ) {
                    IconButton(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                            onClick()
                        },
                        modifier = Modifier.size(34.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Play,
                            contentDescription = "Play",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodPlaylistCard(
    title: String,
    subtitle: String,
    emoji: String,
    songs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                        if (songs.isNotEmpty()) onSongClick(songs.first())
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = RhythmIcons.Play,
                        contentDescription = "Play",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            if (songs.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = songs.take(5),
                        key = { "mood_${it.id}" },
                        contentType = { "song" }
                    ) { song ->
                        Card(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                onSongClick(song)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            modifier = Modifier.width(120.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .apply(ImageUtils.buildImageRequest(
                                            song.artworkUri,
                                            song.title,
                                            LocalContext.current.cacheDir,
                                            M3PlaceholderType.TRACK
                                        ))
                                        .build(),
                                    contentDescription = song.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                
                                Text(
                                    text = song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllSongsPage(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onAddSongToPlaylist: (Song) -> Unit,
    onSongMoreClick: (Song) -> Unit = {},
    onBack: () -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback // Add haptics parameter
) {
    val context = LocalContext.current
    val miniPlayerBottomPadding = LocalMiniPlayerPadding.current.calculateBottomPadding()
    val contentBottomPadding = (miniPlayerBottomPadding + 20.dp).coerceAtLeast(96.dp)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onBack()
                }) {
                    Icon(RhythmIcons.Back, contentDescription = "Back")
                }
                Text(
                    text = context.getString(R.string.search_all_songs, songs.size),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f),
                thickness = 1.dp
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = contentBottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = songs,
                    key = { _, song -> "song_${song.id}_${song.uri}" },
                    contentType = { _, _ -> "song" }
                ) { index, song ->
                    AnimateIn(modifier = Modifier.animateItem()) {
                        SearchSongItem(
                            song = song,
                            onClick = { onSongClick(song) },
                            onMoreClick = {
                                onSongMoreClick(song)
                            },
                            onAddToPlaylist = { onAddSongToPlaylist(song) },
                            haptics = haptics,
                            index = index,
                            totalCount = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreBrowseSection(
    songs: List<Song>,
    musicViewModel: MusicViewModel,
    onGenreClick: (String) -> Unit
) {
    val context = LocalContext.current

    // Get genre detection state from ViewModel
    val isGenreDetectionComplete by musicViewModel.isGenreDetectionComplete.collectAsState()

    // Extract unique genres from songs - split multi-genre strings on comma/semicolon
    val genres = remember(songs) {
        songs
            .flatMap { song -> GenreUtils.splitGenres(song.genre) }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }

    val genreSongCounts = remember(genres, songs) {
        genres.associateWith { genre ->
            songs.count { song -> GenreUtils.matchesGenre(song.genre, genre) }
        }
    }

    // Determine actual loading state: show loading only if detection not complete AND no genres exist
    // Once detection completes, always show results (genres or empty state)
    val isActuallyLoading = !isGenreDetectionComplete && genres.isEmpty()

    // Always show the card, but vary the content based on state
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = RhythmIcons.Actions.Tune,
                    contentDescription = null,
                    
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = context.getString(R.string.search_browse_by_genre),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Show appropriate content based on state
            when {
                isActuallyLoading -> {
                    // Loading state - only show when actively detecting AND no genres yet
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = context.getString(R.string.search_detecting_genres),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = context.getString(R.string.search_genre_suggestions),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                genres.isEmpty() -> {
                    // No genres found
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = context.getString(R.string.search_no_genres),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                else -> {
                    val rows = remember(genres) { genres.chunked(2) }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rows.forEach { rowGenres ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowGenres.forEach { genre ->
                                    GenreBrowseItemCard(
                                        genre = genre,
                                        songCount = genreSongCounts[genre] ?: 0,
                                        onClick = { onGenreClick(genre) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (rowGenres.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun genreIconFor(genre: String): ImageVector {
    val normalized = genre.lowercase()
    return when {
        normalized.contains("hip hop") || normalized.contains("hip-hop") || normalized.contains("rap") || normalized.contains("trap") -> Icons.Default.Mic
        normalized.contains("rock") || normalized.contains("metal") || normalized.contains("punk") || normalized.contains("grunge") -> RhythmIcons.Music.Audiotrack
        normalized.contains("electronic") || normalized.contains("edm") || normalized.contains("house") || normalized.contains("techno") || normalized.contains("trance") || normalized.contains("synth") -> RhythmIcons.Player.Equalizer
        normalized.contains("classical") || normalized.contains("instrumental") || normalized.contains("orchestra") || normalized.contains("opera") -> RhythmIcons.Music.Album
        normalized.contains("jazz") || normalized.contains("blues") || normalized.contains("soul") || normalized.contains("r&b") || normalized.contains("funk") -> RhythmIcons.Music.MusicNote
        normalized.contains("ambient") || normalized.contains("chill") || normalized.contains("lofi") || normalized.contains("lo-fi") || normalized.contains("acoustic") -> RhythmIcons.Devices.Headphones
        normalized.contains("pop") || normalized.contains("dance") || normalized.contains("disco") || normalized.contains("k-pop") || normalized.contains("j-pop") -> RhythmIcons.Music.MusicNote
        normalized.contains("country") || normalized.contains("folk") -> RhythmIcons.Music.Audiotrack
        else -> RhythmIcons.Music.MusicNote
    }
}

@Composable
private fun GenreBrowseItemCard(
    genre: String,
    songCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val isCompactWidth = LocalConfiguration.current.screenWidthDp < 380

    val cardShape = RoundedCornerShape(20.dp)
    val accentIndex = remember(genre) { (genre.hashCode() and 0x7fffffff) % 3 }
    val (genreContainerColor, genreTitleColor, genreMetaColor, iconChipColor) = when (accentIndex) {
        0 -> listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.84f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        )
        1 -> listOf(
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.92f),
            MaterialTheme.colorScheme.onTertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.84f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)
        )
        else -> listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.92f),
            MaterialTheme.colorScheme.onSecondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.84f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)
        )
    }

    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = genreContainerColor),
        modifier = modifier.heightIn(min = if (isCompactWidth) 90.dp else 98.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompactWidth) 10.dp else 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconChipColor,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Icon(
                    imageVector = genreIconFor(genre),
                    contentDescription = null,
                    tint = genreTitleColor,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                        .size(if (isCompactWidth) 14.dp else 15.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isCompactWidth) 6.dp else 8.dp))

            Text(
                text = genre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = genreTitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (songCount == 1) "1" else "$songCount",
                    style = MaterialTheme.typography.labelLarge,
                    color = genreMetaColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AnimateIn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = 50),
        label = "alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.graphicsLayer(
            alpha = alpha,
            scaleX = scale,
            scaleY = scale
        )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSongOptionsBottomSheet(
    song: Song,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onToggleFavorite: () -> Unit,
    isFavorite: Boolean,
    onShowSongInfo: () -> Unit,
    onGoToAlbum: () -> Unit,
    onGoToArtist: () -> Unit,
    onAddToBlacklist: () -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    var showContent by remember { mutableStateOf(false) }
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "contentAlpha"
    )
    
    val contentTranslation by animateFloatAsState(
        targetValue = if (showContent) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "contentTranslation"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        showContent = true
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.primary
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with song info (fixed at top)
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                SongOptionsHeader(song = song)
            }
            
            // Actions section with grid layout (wrapped in scrollable column)
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1: Play next, Add to queue
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = Icons.Rounded.SkipNext,
                                text = "Play next",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onPlayNext()
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = RhythmIcons.Queue,
                                text = "Add to queue",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onAddToQueue()
                                }
                            )
                        }
                    }
                    
                    // Row 2: Add to playlist, Toggle favorite
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = RhythmIcons.AddToPlaylist,
                                text = "Add to playlist",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onAddToPlaylist()
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = if (isFavorite) RhythmIcons.Favorite else Icons.Rounded.FavoriteBorder,
                                text = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                iconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onToggleFavorite()
                                }
                            )
                        }
                    }
                    
                    // Row 3: Go to album, Go to artist
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = RhythmIcons.Album,
                                text = "Go to album",
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onGoToAlbum()
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = RhythmIcons.Artist,
                                text = "Go to artist",
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onGoToArtist()
                                }
                            )
                        }
                    }
                    
                    // Row 4: Song info, Add to blacklist
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = Icons.Rounded.Info,
                                text = "Song info",
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onShowSongInfo()
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SongOptionGridItem(
                                icon = Icons.Rounded.Block,
                                text = "Add to blacklist",
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                iconColor = MaterialTheme.colorScheme.error,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onAddToBlacklist()
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SongOptionsHeader(
    song: Song,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Song options",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(18.dp))
        
        // Song info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album art
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 0.dp
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .apply(ImageUtils.buildImageRequest(
                                song.artworkUri,
                                song.title,
                                context.cacheDir,
                                M3PlaceholderType.TRACK
                            ))
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Song info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Now playing",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "${song.artist} • ${song.album}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SongOptionItem(
    icon: ImageVector,
    text: String,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with colored background
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = containerColor.copy(alpha = 0.3f),
                tonalElevation = 0.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    containerColor.copy(alpha = 0.15f),
                                    containerColor.copy(alpha = 0.05f)
                                ),
                                radius = 20f
                            )
                        )
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SongOptionGridItem(
    icon: ImageVector,
    text: String,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with colored background
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = containerColor.copy(alpha = 0.3f),
                tonalElevation = 0.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    containerColor.copy(alpha = 0.15f),
                                    containerColor.copy(alpha = 0.05f)
                                ),
                                radius = 22f
                            )
                        )
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
