package chromahub.rhythm.app.features.local.presentation.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import chromahub.rhythm.app.shared.presentation.components.common.CollapsibleHeaderScreen
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveFilledIconButton
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveShapes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chromahub.rhythm.app.ui.LocalMiniPlayerPadding
import chromahub.rhythm.app.ui.UiConstants
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import chromahub.rhythm.app.R
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.AddToPlaylistBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.AlbumBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.ArtistBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.SongInfoBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.UpdateBottomSheet
import chromahub.rhythm.app.features.local.presentation.screens.AddToPlaylistScreen
import chromahub.rhythm.app.features.local.presentation.components.dialogs.CreatePlaylistDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.QueueActionDialog
import chromahub.rhythm.app.features.local.presentation.components.player.MiniPlayer
import chromahub.rhythm.app.features.local.presentation.components.player.SleepTimerBottomSheetNew
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.features.local.presentation.screens.LibraryScreen
import chromahub.rhythm.app.features.local.presentation.screens.HomeScreen
import chromahub.rhythm.app.features.local.presentation.screens.ListeningStatsScreen
import chromahub.rhythm.app.features.local.presentation.screens.EqualizerScreen
import chromahub.rhythm.app.features.local.presentation.screens.PlayerScreen

import chromahub.rhythm.app.features.local.presentation.screens.PlaylistDetailScreen
import chromahub.rhythm.app.features.local.presentation.screens.ArtistDetailScreen
import chromahub.rhythm.app.features.local.presentation.screens.SearchScreen
import chromahub.rhythm.app.features.local.presentation.screens.settings.SettingsScreenWrapper
import chromahub.rhythm.app.features.local.presentation.screens.settings.*
import chromahub.rhythm.app.shared.data.model.PlaybackLocation
import chromahub.rhythm.app.shared.presentation.components.MediaScanLoader // Add MediaScanLoader import
import chromahub.rhythm.app.util.HapticUtils
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel.SortOrder
import chromahub.rhythm.app.shared.presentation.viewmodel.ThemeViewModel
import chromahub.rhythm.app.shared.presentation.viewmodel.AppUpdaterViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import chromahub.rhythm.app.features.local.presentation.screens.LibraryTab
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.runtime.mutableStateOf
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.rememberLazyListState // Redundant, but ensuring it's there
import androidx.compose.material3.OutlinedTextField // Redundant, but ensuring it's there
import androidx.compose.foundation.shape.CircleShape // Redundant, but ensuring it's there
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.text.style.TextAlign // Redundant, but ensuring it's there

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library?tab={tab}") {
        fun createRoute(tab: LibraryTab = LibraryTab.SONGS): String = "library?tab=${tab.name.lowercase()}"
    }
    object Player : Screen("player")
    object Settings : Screen("settings")
    object AddToPlaylist : Screen("add_to_playlist")
    object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    object ArtistDetail : Screen("artist/{artistName}") {
        fun createRoute(artistName: String) = "artist/${Uri.encode(artistName)}"
    }
    
    // Tuner Settings Subroutes
    object TunerNotifications : Screen("tuner_notifications_settings")
    object TunerExperimentalFeatures : Screen("tuner_experimental_features_settings")
    object TunerAbout : Screen("tuner_about_screen")
    object TunerUpdates : Screen("tuner_updates_screen")
    object TunerMediaScan : Screen("tuner_media_scan_settings")
    object TunerPlaylists : Screen("tuner_playlist_settings")
    object TunerApiManagement : Screen("tuner_api_management_settings")
    object TunerCacheManagement : Screen("tuner_cache_management_settings")
    object TunerBackupRestore : Screen("tuner_backup_restore_settings")
    object TunerLibraryTabOrder : Screen("tuner_library_tab_order_settings")
    object TunerThemeCustomization : Screen("tuner_theme_customization_settings")
    object TunerEqualizer : Screen("tuner_equalizer_settings")
    object TunerSleepTimer : Screen("tuner_sleep_timer_settings")
    object TunerCrashLogHistory : Screen("tuner_crash_log_history_settings")
    object TunerQueuePlayback : Screen("tuner_queue_playback_settings")
    object TunerHomeScreen : Screen("tuner_home_screen_settings")
    
    // Stats Screen
    object ListeningStats : Screen("listening_stats")
    object Equalizer : Screen("equalizer")
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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun LocalNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: MusicViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    appSettings: chromahub.rhythm.app.shared.data.model.AppSettings // Add appSettings parameter
) {
    // Update monitoring
    val updaterViewModel: AppUpdaterViewModel = viewModel()
    val updateAvailable by updaterViewModel.updateAvailable.collectAsState()
    val updatesEnabled by appSettings.updatesEnabled.collectAsState()
    val latestVersion by updaterViewModel.latestVersion.collectAsState()
    var showUpdateBottomSheet by remember { mutableStateOf(false) }
    
    // Track the version we've shown to avoid re-showing for the same version
    var lastShownVersion by remember { mutableStateOf<String?>(null) }
    
    // Monitor for updates and show bottom sheet automatically
    LaunchedEffect(updateAvailable, updatesEnabled, latestVersion) {
        if (updateAvailable && updatesEnabled) {
            val currentVersion = latestVersion?.versionName
            // Show if we haven't shown this version yet, or if the version changed
            if (currentVersion != null && currentVersion != lastShownVersion) {
                showUpdateBottomSheet = true
                lastShownVersion = currentVersion
            }
        } else {
            // Reset when update is no longer available
            lastShownVersion = null
        }
    }
    // Collect state from ViewModel
    val songs by viewModel.filteredSongs.collectAsState() // Use filtered songs to exclude blacklisted ones
    val allSongs by viewModel.songs.collectAsState() // Keep all songs for specific cases
    val albums by viewModel.filteredAlbums.collectAsState() // Use filtered albums to exclude albums with all songs blacklisted
    val artists by viewModel.filteredArtists.collectAsState() // Use filtered artists to exclude artists with all songs blacklisted
    val playlists by viewModel.playlists.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val showLyrics by viewModel.showLyrics.collectAsState()
    val showOnlineOnlyLyrics by viewModel.showOnlineOnlyLyrics.collectAsState()
    val lyrics by viewModel.currentLyrics.collectAsState()
    val isLoadingLyrics by viewModel.isLoadingLyrics.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val currentDevice by viewModel.currentDevice.collectAsState()
    val isMediaScanning by viewModel.isMediaScanning.collectAsState() // Add media scanning state

    // Theme state
    val useSystemTheme by themeViewModel.useSystemTheme.collectAsState()
    val darkMode by themeViewModel.darkMode.collectAsState()
    
    // Library tab order settings - used to determine first visible tab
    val libraryTabOrder by appSettings.libraryTabOrder.collectAsState()
    val hiddenLibraryTabs by appSettings.hiddenLibraryTabs.collectAsState()
    
    // Compute the first visible tab based on user's tab order (respecting hidden tabs)
    val firstVisibleLibraryTab = remember(libraryTabOrder, hiddenLibraryTabs) {
        val firstVisibleId = libraryTabOrder.firstOrNull { !hiddenLibraryTabs.contains(it) }
        when (firstVisibleId) {
            "SONGS" -> LibraryTab.SONGS
            "PLAYLISTS" -> LibraryTab.PLAYLISTS
            "ALBUMS" -> LibraryTab.ALBUMS
            "ARTISTS" -> LibraryTab.ARTISTS
            "EXPLORER" -> LibraryTab.EXPLORER
            else -> LibraryTab.SONGS // Default fallback
        }
    }
    
    // Default landing screen
    val defaultScreen by appSettings.defaultScreen.collectAsState()
    val startDestination = when (defaultScreen) {
        "library" -> Screen.Library.createRoute(firstVisibleLibraryTab)
        else -> Screen.Home.route
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as android.app.Activity)
    val isTablet = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    val onPlayPause = { viewModel.togglePlayPause() }
    val onSkipNext = { viewModel.skipToNext() }
    val onSkipPrevious = { viewModel.skipToPrevious() }
    val onSeek = { value: Float -> viewModel.seekTo(value) }
    val onLyricsSeek: (Long) -> Unit = { timestampMs ->
        // Use the timestamp-based seekTo method directly for lyrics
        Log.d("RhythmNavigation", "Lyrics seek: timestampMs=$timestampMs")
        viewModel.seekTo(timestampMs)
    }
    val onPlaySong = { song: chromahub.rhythm.app.shared.data.model.Song -> viewModel.playSong(song) }
    val onPlayAlbum = { album: chromahub.rhythm.app.shared.data.model.Album -> viewModel.playAlbum(album) }
    val onPlayAlbumShuffled = { album: chromahub.rhythm.app.shared.data.model.Album -> viewModel.playAlbumShuffled(album) }
    val onPlayArtist = { artist: chromahub.rhythm.app.shared.data.model.Artist -> viewModel.playArtist(artist) }
    val onPlayPlaylist =
        { playlist: chromahub.rhythm.app.shared.data.model.Playlist -> viewModel.playPlaylist(playlist) }
    val onPlayPlaylistShuffled = { playlist: chromahub.rhythm.app.shared.data.model.Playlist -> viewModel.playPlaylistShuffled(playlist) }
    val onToggleShuffle = { viewModel.toggleShuffle() }
    val onToggleRepeat = { viewModel.toggleRepeatMode() }
    val onToggleFavorite = { viewModel.toggleFavorite() }

    // Player click navigates to full player screen
    val onPlayerClick = {
        navController.navigate(Screen.Player.route)
    }

    // Track current destination for hiding navigation bar on player screen
    var currentRoute by remember { mutableStateOf(Screen.Home.route) }

    // Update current route when destination changes
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentRoute = backStackEntry.destination.route ?: Screen.Home.route
            // Update selectedTab based on current route
            when {
                currentRoute == Screen.Home.route -> selectedTab = 0
                currentRoute.startsWith("library") -> selectedTab = 1
            }
        }
    }

    // Provide dynamic mini-player padding with comprehensive navigation handling
    val showMiniPlayer = currentSong != null && currentRoute != Screen.Player.route
    val showNavBar = remember(currentRoute) {
        currentRoute == Screen.Home.route || currentRoute.startsWith("library") || currentRoute == Screen.Search.route || currentRoute == Screen.Settings.route || currentRoute == Screen.ListeningStats.route
    }
    val showBottomNav = remember(currentRoute) {
        currentRoute == Screen.Home.route || currentRoute.startsWith("library")
    }
    
    // Calculate content bottom padding based on visible UI elements
    // System insets are handled separately via windowInsetsPadding on the bottomBar
    val miniPlayerPaddingValues = remember(showMiniPlayer, showBottomNav, isTablet) {
        var totalPadding = 0.dp
        
        // Add MiniPlayer height if visible (only on phones, not tablets)
        if (showMiniPlayer && !isTablet) {
            totalPadding += UiConstants.MiniPlayerHeight + 16.dp // Card height + spacing
        }
        
        // Add NavBar height if visible (only on phones, not tablets)
        if (showBottomNav && !isTablet) {
            totalPadding += UiConstants.NavBarHeight + 16.dp // NavBar height + spacing
        }
        
        // Return padding values (system insets handled by bottomBar container)
        PaddingValues(bottom = totalPadding)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(LocalMiniPlayerPadding provides miniPlayerPaddingValues) {
        if (isTablet) {
            // Tablet layout with NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                // Navigation rail for tablets
                AnimatedVisibility(
                    visible = showNavBar,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeOut(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    LocalNavigationRail(
                        currentRoute = currentRoute,
                        navController = navController,
                        firstVisibleLibraryTab = firstVisibleLibraryTab,
                        context = context,
                        haptic = haptic
                    )
                }
                
                // Main content wrapped in Scaffold (without bottom nav)
                LocalNavigationContent(
                    navController = navController,
                    viewModel = viewModel,
                    themeViewModel = themeViewModel,
                    appSettings = appSettings,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    currentSong = currentSong,
                    currentRoute = currentRoute,
                    isPlaying = isPlaying,
                    progress = progress,
                    onPlayPause = onPlayPause,
                    onPlayerClick = onPlayerClick,
                    onSkipNext = onSkipNext,
                    onSkipPrevious = onSkipPrevious,
                    showMiniPlayer = showMiniPlayer,
                    showBottomNav = false, // Hide nav bar in content for tablet
                    isTablet = true,
                    startDestination = startDestination,
                    songs = songs,
                    allSongs = allSongs,
                    albums = albums,
                    artists = artists,
                    playlists = playlists,
                    isShuffleEnabled = isShuffleEnabled,
                    repeatMode = repeatMode,
                    isFavorite = isFavorite,
                    sortOrder = sortOrder,
                    showLyrics = showLyrics,
                    showOnlineOnlyLyrics = showOnlineOnlyLyrics,
                    lyrics = lyrics,
                    isLoadingLyrics = isLoadingLyrics,
                    recentlyPlayed = recentlyPlayed,
                    currentDevice = currentDevice,
                    isMediaScanning = isMediaScanning,
                    useSystemTheme = useSystemTheme,
                    darkMode = darkMode,
                    libraryTabOrder = libraryTabOrder,
                    hiddenLibraryTabs = hiddenLibraryTabs,
                    firstVisibleLibraryTab = firstVisibleLibraryTab,
                    onPlaySong = onPlaySong,
                    onPlayAlbum = onPlayAlbum,
                    onPlayAlbumShuffled = onPlayAlbumShuffled,
                    onPlayArtist = onPlayArtist,
                    onPlayPlaylist = onPlayPlaylist,
                    onPlayPlaylistShuffled = onPlayPlaylistShuffled,
                    onToggleShuffle = onToggleShuffle,
                    onToggleRepeat = onToggleRepeat,
                    onToggleFavorite = onToggleFavorite,
                    onSeek = onSeek,
                    onLyricsSeek = onLyricsSeek
                )
            }
        } else {
            // Phone layout with original bottom navigation
            LocalNavigationContent(
                navController = navController,
                viewModel = viewModel,
                themeViewModel = themeViewModel,
                appSettings = appSettings,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                currentSong = currentSong,
                currentRoute = currentRoute,
                isPlaying = isPlaying,
                progress = progress,
                onPlayPause = onPlayPause,
                onPlayerClick = onPlayerClick,
                onSkipNext = onSkipNext,
                onSkipPrevious = onSkipPrevious,
                showMiniPlayer = showMiniPlayer,
                showBottomNav = showBottomNav,
                isTablet = false,
                startDestination = startDestination,
                songs = songs,
                allSongs = allSongs,
                albums = albums,
                artists = artists,
                playlists = playlists,
                isShuffleEnabled = isShuffleEnabled,
                repeatMode = repeatMode,
                isFavorite = isFavorite,
                sortOrder = sortOrder,
                showLyrics = showLyrics,
                showOnlineOnlyLyrics = showOnlineOnlyLyrics,
                lyrics = lyrics,
                isLoadingLyrics = isLoadingLyrics,
                recentlyPlayed = recentlyPlayed,
                currentDevice = currentDevice,
                isMediaScanning = isMediaScanning,
                useSystemTheme = useSystemTheme,
                darkMode = darkMode,
                libraryTabOrder = libraryTabOrder,
                hiddenLibraryTabs = hiddenLibraryTabs,
                firstVisibleLibraryTab = firstVisibleLibraryTab,
                onPlaySong = onPlaySong,
                onPlayAlbum = onPlayAlbum,
                onPlayAlbumShuffled = onPlayAlbumShuffled,
                onPlayArtist = onPlayArtist,
                onPlayPlaylist = onPlayPlaylist,
                onPlayPlaylistShuffled = onPlayPlaylistShuffled,
                onToggleShuffle = onToggleShuffle,
                onToggleRepeat = onToggleRepeat,
                onToggleFavorite = onToggleFavorite,
                onSeek = onSeek,
                onLyricsSeek = onLyricsSeek
            )
        }
    }
    
    // Global Update Bottom Sheet - shows automatically when update is available
    if (showUpdateBottomSheet) {
        UpdateBottomSheet(
            updaterViewModel = updaterViewModel,
            onDismiss = { showUpdateBottomSheet = false },
            onUpdateClick = { immediate ->
                showUpdateBottomSheet = false
                // Navigate to update settings
                navController.navigate(Screen.TunerUpdates.route)
            }
        )
    }
}

@Composable
private fun LocalNavigationContent(
    navController: NavHostController,
    viewModel: MusicViewModel,
    themeViewModel: ThemeViewModel,
    appSettings: chromahub.rhythm.app.shared.data.model.AppSettings,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    currentSong: chromahub.rhythm.app.shared.data.model.Song?,
    currentRoute: String,
    isPlaying: Boolean,
    progress: Float,
    onPlayPause: () -> Unit,
    onPlayerClick: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    showMiniPlayer: Boolean,
    showBottomNav: Boolean,
    isTablet: Boolean,
    startDestination: String,
    songs: List<chromahub.rhythm.app.shared.data.model.Song>,
    allSongs: List<chromahub.rhythm.app.shared.data.model.Song>,
    albums: List<chromahub.rhythm.app.shared.data.model.Album>,
    artists: List<chromahub.rhythm.app.shared.data.model.Artist>,
    playlists: List<chromahub.rhythm.app.shared.data.model.Playlist>,
    isShuffleEnabled: Boolean,
    repeatMode: Int,
    isFavorite: Boolean,
    sortOrder: MusicViewModel.SortOrder,
    showLyrics: Boolean,
    showOnlineOnlyLyrics: Boolean,
    lyrics: chromahub.rhythm.app.shared.data.model.LyricsData?,
    isLoadingLyrics: Boolean,
    recentlyPlayed: List<chromahub.rhythm.app.shared.data.model.Song>,
    currentDevice: PlaybackLocation?,
    isMediaScanning: Boolean,
    useSystemTheme: Boolean,
    darkMode: Boolean,
    libraryTabOrder: List<String>,
    hiddenLibraryTabs: Set<String>,
    firstVisibleLibraryTab: LibraryTab,
    onPlaySong: (chromahub.rhythm.app.shared.data.model.Song) -> Unit,
    onPlayAlbum: (chromahub.rhythm.app.shared.data.model.Album) -> Unit,
    onPlayAlbumShuffled: (chromahub.rhythm.app.shared.data.model.Album) -> Unit,
    onPlayArtist: (chromahub.rhythm.app.shared.data.model.Artist) -> Unit,
    onPlayPlaylist: (chromahub.rhythm.app.shared.data.model.Playlist) -> Unit,
    onPlayPlaylistShuffled: (chromahub.rhythm.app.shared.data.model.Playlist) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSeek: (Float) -> Unit,
    onLyricsSeek: (Long) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isRemovalSnackbar = data.visuals.message.contains("removed from playlist")

                Snackbar(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    action = {
                        data.visuals.actionLabel?.let { label ->
                            TextButton(onClick = { data.performAction() }) {
                                Text(
                                    label
                                )
                            }
                        }
                    },
                    actionOnNewLine = data.visuals.actionLabel != null && data.visuals.message.length > 50,
                    shape = ExpressiveShapes.Full, // Expressive pill shape
                    containerColor = if (isRemovalSnackbar) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (isRemovalSnackbar) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    actionContentColor = if (isRemovalSnackbar) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (isRemovalSnackbar) RhythmIcons.Delete else RhythmIcons.Actions.Check,
                                contentDescription = if (isRemovalSnackbar) "Removed" else "Info",
                                tint = if (isRemovalSnackbar) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = data.visuals.message,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            // Wrap entire bottom bar in Column with system insets handling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars) // Handle system navigation bars once
                    .padding(bottom = 4.dp) // Minimum bottom margin for gesture navigation safety
            ) {
                // Global MiniPlayer (hidden on full player screen) with bounce entrance animation
                // Show at bottom on phones, or on right side if tablet miniplayer is enabled
                AnimatedVisibility(
                    visible = currentSong != null && currentRoute != Screen.Player.route,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeOut(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        MiniPlayer(
                            song = currentSong,
                            isPlaying = isPlaying,
                            progress = progress,
                            onPlayPause = onPlayPause,
                            onPlayerClick = onPlayerClick,
                            onSkipNext = onSkipNext,
                            onSkipPrevious = onSkipPrevious,
                            onDismiss = {
                                // Clear the current song to hide the mini player
                                viewModel.clearCurrentSong()
                            },
                            modifier = Modifier.align(Alignment.BottomEnd) // Align to bottom-end (right side)
                        )
                    }
                }

                // Navigation bar shown only on specific routes with spring animation
                AnimatedVisibility(
                    visible = showBottomNav,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeOut(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Column {
                        // // Add subtle separator when mini player is visible for better visual separation
                        // if (showMiniPlayer) {
                        //     HorizontalDivider(
                        //         thickness = 1.dp,
                        //         color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        //         modifier = Modifier.padding(horizontal = 32.dp)
                        //     )
                        // }

                        // New outer Box to layer navigation bar and search icon
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = if (showMiniPlayer) 2.dp else 8.dp,
                                    bottom = 8.dp // Simple spacing - no system insets here
                                )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp) // Overall horizontal padding for the row
                                .align(Alignment.CenterHorizontally), // Align the row to the center horizontally within the Column
                            verticalAlignment = Alignment.Bottom // Align items to the bottom of the row
                        ) {
                            // Expressive Navigation bar Surface with pill shape
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = ExpressiveShapes.Full, // Full pill shape for expressive design
                                tonalElevation = 3.dp,
                                shadowElevation = 0.dp,
                                modifier = Modifier
                                    .height(64.dp)
                                    .weight(1f) // Make it take up available space
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Use first visible library tab based on user's tab order
                                    val libraryRoute =
                                        Screen.Library.createRoute(firstVisibleLibraryTab)
                                    val items = listOf(
                                        Triple(
                                            Screen.Home.route, "Home",
                                            Pair(RhythmIcons.HomeFilled, RhythmIcons.Home)
                                        ),
                                        Triple(
                                            libraryRoute, "Library",
                                            Pair(Icons.Filled.LibraryMusic, RhythmIcons.Library)
                                        )
                                    )

                                    items.forEachIndexed { index, (route, title, icons) ->
                                        val isSelected = when (title) {
                                            "Home" -> currentRoute == Screen.Home.route
                                            "Library" -> currentRoute.startsWith("library")
                                            else -> false
                                        }

                                        val (selectedIcon, unselectedIcon) = icons

                                        // Enhanced animation values with spring physics
                                        val animatedScale by animateFloatAsState(
                                            targetValue = if (isSelected) 1.05f else 1.0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            ),
                                            label = "scale_$title"
                                        )

                                        val animatedAlpha by animateFloatAsState(
                                            targetValue = if (isSelected) 1f else 0.7f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                stiffness = Spring.StiffnessLow
                                            ),
                                            label = "alpha_$title"
                                        )

                                        // Background pill animation with spring
                                        val pillWidth by animateDpAsState(
                                            targetValue = if (isSelected) 120.dp else 0.dp,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            ),
                                            label = "pillWidth_$title"
                                        )

                                        // Icon color animation
                                        val iconColor by animateColorAsState(
                                            targetValue = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            animationSpec = tween(300),
                                            label = "iconColor_$title"
                                        )

                                        val haptic = LocalHapticFeedback.current

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clickable {
                                                    HapticUtils.performHapticFeedback(
                                                        context,
                                                        haptic,
                                                        HapticFeedbackType.LongPress
                                                    )
                                                    navController.navigate(route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Horizontal layout for icon and text with animated pill background
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .graphicsLayer {
                                                        scaleX = animatedScale
                                                        scaleY = animatedScale
                                                        alpha = animatedAlpha
                                                    }
                                                    .then(
                                                        if (isSelected) Modifier
                                                            .clip(ExpressiveShapes.Full) // Expressive pill shape
                                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                                            .height(48.dp)
                                                            .widthIn(min = pillWidth) // Animated width
                                                            .padding(horizontal = 18.dp)
                                                        else Modifier.padding(horizontal = 16.dp)
                                                    )
                                            ) {
                                                // Animated icon with crossfade
                                                androidx.compose.animation.Crossfade(
                                                    targetState = isSelected,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessVeryLow
                                                    ),
                                                    label = "iconCrossfade_$title"
                                                ) { selected ->
                                                    Icon(
                                                        imageVector = if (selected) selectedIcon else unselectedIcon,
                                                        contentDescription = title,
                                                        tint = iconColor,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }

                                                AnimatedVisibility(
                                                    visible = isSelected,
                                                    enter = fadeIn(
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessMedium
                                                        )
                                                    ) + expandHorizontally(
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    ),
                                                    exit = fadeOut(
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    ) + shrinkHorizontally(
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    )
                                                ) {
                                                    Row {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = title,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = iconColor,
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

                            Spacer(modifier = Modifier.width(12.dp)) // Gap between nav bar and search icon

                            // Expressive Search Icon Button with bouncy animation
                            val searchInteractionSource = remember { MutableInteractionSource() }
                            val isSearchPressed by searchInteractionSource.collectIsPressedAsState()
                            val searchScale by animateFloatAsState(
                                targetValue = if (isSearchPressed) 0.88f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "search_scale"
                            )
                            
                            FilledIconButton(
                                onClick = {
                                    HapticUtils.performHapticFeedback(
                                        context,
                                        haptic,
                                        HapticFeedbackType.LongPress
                                    )
                                    navController.navigate(Screen.Search.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = ExpressiveShapes.Full,
                                interactionSource = searchInteractionSource,
                                modifier = Modifier
                                    .size(64.dp) // Match height of navigation bar
                                    .graphicsLayer {
                                        scaleX = searchScale
                                        scaleY = searchScale
                                    }
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LocalMiniPlayerPadding.current)
            ) {
                composable(
                    route = Screen.Home.route,
                    enterTransition = {
                        when {
                            initialState.destination.route?.startsWith("library") == true -> {
                                // Horizontal slide animation when coming from Library
                                fadeIn(animationSpec = tween(300)) +
                                        slideInHorizontally(
                                            initialOffsetX = { -it },
                                            animationSpec = tween(350, easing = EaseInOutQuart)
                                        )
                            }

                            else -> {
                                // Default animation for other sources
                                fadeIn(animationSpec = tween(300))
                            }
                        }
                    },
                    exitTransition = {
                        when {
                            targetState.destination.route?.startsWith("library") == true -> {
                                // Horizontal slide animation when going to Library
                                fadeOut(animationSpec = tween(300)) +
                                        slideOutHorizontally(
                                            targetOffsetX = { -it },
                                            animationSpec = tween(350, easing = EaseInOutQuart)
                                        )
                            }

                            else -> {
                                // Default animation for other destinations
                                fadeOut(animationSpec = tween(300))
                            }
                        }
                    },
                    popEnterTransition = {
                        when {
                            initialState.destination.route?.startsWith("library") == true -> {
                                // Restore horizontal slide animation when popping back from Library
                                fadeIn(animationSpec = tween(300)) +
                                        slideInHorizontally(
                                            initialOffsetX = { -it },
                                            animationSpec = tween(350, easing = EaseInOutQuart)
                                        )
                            }

                            else -> {
                                // Simple faster fade animation when popping back from other screens
                                fadeIn(animationSpec = tween(200))
                            }
                        }
                    },
                    popExitTransition = {
                        // Simple faster fade animation when being popped from
                        fadeOut(animationSpec = tween(200))
                    }
                ) {
                    HomeScreen(
                        songs = songs,
                        albums = albums,
                        artists = artists,
                        recentlyPlayed = recentlyPlayed,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        progress = progress,
                        onSongClick = onPlaySong,
                        onAlbumClick = onPlayAlbum,
                        onArtistClick = onPlayArtist,
                        onPlayPause = onPlayPause,
                        onPlayerClick = {
                            navController.navigate(Screen.Player.route)
                        },
                        onViewAllSongs = {
                            // Navigate to songs screen
                            navController.navigate(Screen.Library.createRoute(LibraryTab.SONGS)) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onViewAllAlbums = {
                            navController.navigate(Screen.Library.createRoute(LibraryTab.ALBUMS)) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onViewAllArtists = {
                            navController.navigate(Screen.Library.createRoute(LibraryTab.ARTISTS)) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onSkipNext = onSkipNext,
                        onSearchClick = {
                            navController.navigate(Screen.Search.route)
                        },
                        onSettingsClick = {
                            // Navigate to the settings screen
                            navController.navigate(Screen.Settings.route)
                        },
                        onNavigateToLibrary = {
                            // Navigate to library with playlists tab selected
                            navController.navigate(Screen.Library.createRoute(LibraryTab.PLAYLISTS)) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToPlaylist = { playlistId ->
                            // Navigate to the specified playlist
                            // For "favorites", we'll use the ID "1" which is the favorites playlist
                            val id = if (playlistId == "favorites") "1" else playlistId
                            navController.navigate(Screen.PlaylistDetail.createRoute(id))
                        },
                        onAddToQueue = { song ->
                            viewModel.addSongToQueue(song)
                        },
                        onAddSongToPlaylist = { song, playlistId ->
                            viewModel.addSongToPlaylist(song, playlistId) { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        },
                        onNavigateToStats = {
                            navController.navigate(Screen.ListeningStats.route)
                        },
                        onNavigateToArtist = { artist ->
                            navController.navigate(Screen.ArtistDetail.createRoute(artist.name))
                        }
                    )
                }

                composable(
                    Screen.Search.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    }
                ) {
                    SearchScreen(
                        musicViewModel = viewModel,
                        songs = songs,
                        albums = albums,
                        artists = artists,
                        playlists = playlists,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        progress = progress,
                        onSongClick = onPlaySong,
                        onAlbumClick = onPlayAlbum,
                        onArtistClick = onPlayArtist,
                        onPlaylistClick = { playlist ->
                            // Navigate to playlist detail screen
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                        },
                        onPlayPause = onPlayPause,
                        onPlayerClick = {
                            navController.navigate(Screen.Player.route)
                        },
                        onSkipNext = onSkipNext,
                        onAddSongToPlaylist = { song, playlistId ->
                            viewModel.addSongToPlaylist(song, playlistId) { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        },
                        onCreatePlaylist = { name ->
                            viewModel.createPlaylist(name)
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                        onNavigateToArtist = { artist ->
                            navController.navigate(Screen.ArtistDetail.createRoute(artist.name))
                        }
                    )
                }

                composable(
                    Screen.Settings.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    }
                ) {
                    // Use the settings screen (now the default)
                    SettingsScreenWrapper(
                        onBack = {
                            navController.popBackStack()
                        },
                        appSettings = appSettings,
                        navController = navController
                    )
                }
                // Tuner Settings Subroutes
                composable(Screen.TunerNotifications.route) {
                    NotificationsSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerExperimentalFeatures.route) {
                    ExperimentalFeaturesScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerAbout.route) {
                    chromahub.rhythm.app.features.local.presentation.screens.settings.AboutScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToUpdates = { navController.navigate(Screen.TunerUpdates.route) }
                    )
                }

                composable(Screen.TunerUpdates.route) {
                    UpdatesSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerMediaScan.route) {
                    MediaScanSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerPlaylists.route) {
                    PlaylistsSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerApiManagement.route) {
                    ApiManagementSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerCacheManagement.route) {
                    CacheManagementSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerBackupRestore.route) {
                    BackupRestoreSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerLibraryTabOrder.route) {
                    LibraryTabOrderSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerThemeCustomization.route) {
                    ThemeCustomizationSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(
                    route = Screen.Equalizer.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    }
                ) {
                    EqualizerScreen(navController = navController)
                }

                composable(Screen.TunerSleepTimer.route) {
                    // Show sleep timer bottom sheet directly instead of placeholder screen
                    var showBottomSheet by remember { mutableStateOf(true) }
                    
                    if (showBottomSheet) {
                        SleepTimerBottomSheetNew(
                            onDismiss = {
                                showBottomSheet = false
                                navController.popBackStack()
                            },
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            musicViewModel = viewModel
                        )
                    } else {
                        // Navigate back if bottom sheet is dismissed without opening
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }
                }

                composable(Screen.TunerCrashLogHistory.route) {
                    CrashLogHistorySettingsScreen(
                        onBackClick = { navController.popBackStack() },
                        appSettings = appSettings
                    )
                }

                composable(Screen.TunerQueuePlayback.route) {
                    QueuePlaybackSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(Screen.TunerHomeScreen.route) {
                    HomeScreenCustomizationSettingsScreen(onBackClick = { navController.popBackStack() })
                }

                composable(
                    route = Screen.ListeningStats.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    }
                ) {
                    ListeningStatsScreen(navController = navController)
                }

                composable(
                    route = Screen.Equalizer.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    }
                ) {
                    EqualizerScreen(navController = navController)
                }

                composable(
                    route = Screen.Library.route,
                    arguments = listOf(
                        navArgument("tab") {
                            type = NavType.StringType
                            defaultValue = "songs"
                        }
                    ),
                    enterTransition = {
                        when (initialState.destination.route) {
                            Screen.Home.route -> {
                                // Horizontal slide animation when coming from Home
                                fadeIn(animationSpec = tween(300)) +
                                        slideInHorizontally(
                                            initialOffsetX = { it },
                                            animationSpec = tween(350, easing = EaseInOutQuart)
                                        )
                            }

                            else -> {
                                // Default animation for other sources
                                fadeIn(animationSpec = tween(300))
                            }
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            Screen.Home.route -> {
                                // Horizontal slide animation when going to Home
                                fadeOut(animationSpec = tween(300)) +
                                        slideOutHorizontally(
                                            targetOffsetX = { it },
                                            animationSpec = tween(350, easing = EaseInOutQuart)
                                        )
                            }

                            else -> {
                                // Default animation for other destinations
                                fadeOut(animationSpec = tween(300))
                            }
                        }
                    },
                    popEnterTransition = {
                        when (initialState.destination.route) {
                            Screen.Home.route -> {
                                // Restore horizontal slide animation when popping back from Home
                                fadeIn(animationSpec = tween(300)) +
                                        slideInHorizontally(
                                            initialOffsetX = { it },
                                            animationSpec = tween(350, easing = EaseInOutQuart)
                                        )
                            }

                            else -> {
                                // Simple faster fade animation when popping back from other screens
                                fadeIn(animationSpec = tween(200))
                            }
                        }
                    },
                    popExitTransition = {
                        when (targetState.destination.route) {
                            Screen.Home.route -> {
                                // Restore horizontal slide animation when popping back to Home
                                fadeOut(animationSpec = tween(300)) +
                                        slideOutHorizontally(
                                            targetOffsetX = { it },
                                            animationSpec = tween(350, easing = EaseInOutQuart)
                                        )
                            }

                            else -> {
                                // Simple faster fade animation when being popped from for other destinations
                                fadeOut(animationSpec = tween(200))
                            }
                        }
                    }
                ) {
                    val tabArg = it.arguments?.getString("tab") ?: "songs"
                    val initialTab = when (tabArg) {
                        "playlists" -> LibraryTab.PLAYLISTS
                        "albums" -> LibraryTab.ALBUMS
                        "artists" -> LibraryTab.ARTISTS
                        "explorer" -> LibraryTab.EXPLORER
                        else -> LibraryTab.SONGS
                    }

                    LibraryScreen(
                        songs = songs,
                        albums = albums,
                        playlists = playlists,
                        artists = artists,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        progress = progress,
                        onSongClick = onPlaySong,
                        onPlayPause = onPlayPause,
                        onPlayerClick = {
                            navController.navigate(Screen.Player.route)
                        },
                        onPlaylistClick = { playlist ->
                            // Navigate to playlist detail screen
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                        },
                        onAddPlaylist = {
                            // This is now handled internally with the dialog
                        },
                        onAlbumClick = onPlayAlbum,
                        onArtistClick = { artist ->
                            // Handle artist click - could navigate to artist detail or show bottom sheet
                            // For now, we'll handle it within LibraryScreen
                        },
                        onAlbumShufflePlay = onPlayAlbumShuffled,
                        onPlayQueue = { songs ->
                            // Play queue with proper replacement
                            viewModel.playQueue(songs)
                        },
                        onPlayQueueFromIndex = { songs, startIndex ->
                            // Play queue from specific index
                            viewModel.playQueue(songs, startIndex = startIndex)
                        },
                        onShuffleQueue = { songs ->
                            // Shuffle using playShuffled to respect settings
                            viewModel.playShuffled(songs)
                        },
                        onAlbumBottomSheetClick = { album ->
                            // This will open the album bottom sheet within LibraryScreen
                            // The LibraryScreen handles this internally now
                        },
                        onSort = {
                            // Implement sort functionality
                            viewModel.sortLibrary()
                        },
                        onAddSongToPlaylist = { song, playlistId ->
                            // Add song to playlist
                            viewModel.addSongToPlaylist(song, playlistId) { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        },
                        onCreatePlaylist = { name ->
                            viewModel.createPlaylist(name)
                        },
                        onRefreshClick = {
                            viewModel.refreshLibrary()
                        }, // Added onRefreshClick
                        sortOrder = sortOrder,
                        onSkipNext = onSkipNext,
                        onAddToQueue = { song ->
                            // Add song to queue
                            viewModel.addSongToQueue(song)
                        },
                        initialTab = initialTab,
                        musicViewModel = viewModel, // Pass musicViewModel
                        onExportAllPlaylists = { format, includeDefault, userDirectoryUri, resultCallback ->
                            // Export all playlists with optional user-selected directory
                            viewModel.exportAllPlaylists(
                                format,
                                includeDefault,
                                userDirectoryUri
                            ) { result ->
                                result.fold(
                                    onSuccess = { message ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    },
                                    onFailure = { error ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Export failed: ${error.message}")
                                        }
                                    }
                                )
                                resultCallback(result)
                            }
                        },
                        onImportPlaylist = { uri, resultCallback, onRestartRequired ->
                            // Import playlist from URI with restart functionality
                            viewModel.importPlaylist(uri, { result ->
                                result.fold(
                                    onSuccess = { message ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    },
                                    onFailure = { error ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Import failed: ${error.message}")
                                        }
                                    }
                                )
                                resultCallback(result)
                            }, onRestartRequired = {
                                // Trigger restart dialog or function
                                onRestartRequired?.invoke()
                            })
                        },
                        onRestartApp = {
                            viewModel.restartApp()
                        },
                        onNavigateToArtist = { artist ->
                            navController.navigate(Screen.ArtistDetail.createRoute(artist.name))
                        }
                    )
                }

                composable(
                    route = Screen.Player.route,
                    enterTransition = {
                        slideInVertically(
                            initialOffsetY = { it / 3 },  // slide in from 1/3 of the way down for smoother effect
                            animationSpec = tween(
                                durationMillis = 350, // slightly longer for more floaty effect
                                easing = EaseOutQuint // smooth acceleration for floaty feel
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseOutQuint
                            )
                        )
                    },
                    exitTransition = {
                        slideOutVertically(
                            targetOffsetY = { it / 2 },
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = EaseInOutQuart // smoother exit animation
                            )
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = 200
                            )
                        )
                    },
                    popExitTransition = {
                        slideOutVertically(
                            targetOffsetY = { it / 2 },
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = EaseInOutQuart
                            )
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = 200
                            )
                        )
                    },
                    popEnterTransition = {
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(
                                durationMillis = 350,
                                easing = EaseOutQuint
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseOutQuint
                            )
                        )
                    }
                ) {
                    val showAddToPlaylistSheet = remember { mutableStateOf(false) }
                    val showCreatePlaylistDialog = remember { mutableStateOf(false) }

                    // If we're returning from AddToPlaylist route with a song to add, show the bottom sheet
                    LaunchedEffect(viewModel.selectedSongForPlaylist.collectAsState().value) {
                        if (viewModel.selectedSongForPlaylist.value != null) {
                            showAddToPlaylistSheet.value = true
                        }
                    }

                    // Show create playlist dialog if needed
                    if (showCreatePlaylistDialog.value) {
                        // Get the non-delegated value of currentSong
                        val songForDialog = currentSong
                        if (songForDialog != null) {
                            CreatePlaylistDialog(
                                onDismiss = {
                                    showCreatePlaylistDialog.value = false
                                },
                                onConfirm = { name ->
                                    viewModel.createPlaylist(name)
                                    showCreatePlaylistDialog.value = false
                                },
                                song = songForDialog,
                                onConfirmWithSong = { name ->
                                    viewModel.createPlaylist(name)
                                    // The new playlist will be at the end of the list
                                    val newPlaylist = viewModel.playlists.value.last()
                                    viewModel.addSongToPlaylist(
                                        songForDialog,
                                        newPlaylist.id
                                    ) { message ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                    showCreatePlaylistDialog.value = false
                                }
                            )
                        }
                    }

                    PlayerScreen(
                        song = currentSong,
                        isPlaying = isPlaying,
                        progress = progress,
                        location = currentDevice,
                        queuePosition = viewModel.currentQueue.collectAsState().value.currentIndex + 1,
                        queueTotal = viewModel.currentQueue.collectAsState().value.songs.size,
                        onPlayPause = onPlayPause,
                        onSkipNext = onSkipNext,
                        onSkipPrevious = onSkipPrevious,
                        onSeek = { position ->
                            // Use the progress-based seekTo method directly
                            Log.d(
                                "LyricsSeek",
                                "Navigation onSeek - Position: $position, Duration: ${currentSong?.duration}s"
                            )
                            viewModel.seekTo(position)
                        },
                        onLyricsSeek = onLyricsSeek,
                        onBack = {
                            navController.popBackStack()
                        },
                        onLocationClick = {
                            // Show the system output switcher dialog directly
                            viewModel.showOutputSwitcherDialog()
                        },
                        onQueueClick = {
                            // Show queue bottom sheet directly in PlayerScreen
                            // No need to navigate to a separate screen
                        },
                        onToggleShuffle = {
                            viewModel.toggleShuffle()
                        },
                        onToggleRepeat = {
                            viewModel.toggleRepeatMode()
                        },
                        onToggleFavorite = {
                            viewModel.toggleFavorite()
                        },
                        onAddToPlaylist = {
                            currentSong?.let { song ->
                                viewModel.setSelectedSongForPlaylist(song)
                                showAddToPlaylistSheet.value = true
                            }
                        },
                        isShuffleEnabled = isShuffleEnabled,
                        repeatMode = repeatMode,
                        isFavorite = isFavorite,
                        showLyrics = showLyrics,
                        onlineOnlyLyrics = showOnlineOnlyLyrics,
                        lyrics = lyrics,
                        isLoadingLyrics = isLoadingLyrics,
                        onRetryLyrics = {
                            viewModel.retryFetchLyrics()
                        },
                        volume = viewModel.volume.collectAsState().value,
                        isMuted = viewModel.isMuted.collectAsState().value,
                        onVolumeChange = { volume ->
                            viewModel.setVolume(volume)
                        },
                        onToggleMute = {
                            viewModel.toggleMute()
                        },
                        onRefreshDevices = {
                            viewModel.startDeviceMonitoringOnDemand()
                        },
                        onStopDeviceMonitoring = {
                            viewModel.stopDeviceMonitoringOnDemand()
                        },
                        locations = viewModel.locations.collectAsState().value,
                        onLocationSelect = { location ->
                            viewModel.setCurrentDevice(location)
                        },
                        onMaxVolume = {
                            viewModel.maxVolume()
                        },
                        playlists = playlists,
                        queue = viewModel.currentQueue.collectAsState().value.songs,
                        onSongClick = { song ->
                            // Play the selected song from the queue (fallback for non-indexed clicks)
                            viewModel.playSong(song)
                        },
                        onSongClickAtIndex = { index ->
                            // Play song at specific index to handle duplicates correctly
                            viewModel.playSongAtIndex(index)
                        },
                        onRemoveFromQueue = { song ->
                            viewModel.removeFromQueue(song)
                        },
                        onMoveQueueItem = { fromIndex, toIndex ->
                            viewModel.moveQueueItem(fromIndex, toIndex)
                        },
                        onAddSongsToQueue = {
                            // This parameter is now unused in PlayerScreen, as navigation is handled directly
                            // within the QueueBottomSheet's onAddSongsClick.
                            // However, keeping it here for API compatibility if needed elsewhere.
                            viewModel.addSongsToQueue()
                        },
                        onNavigateToLibrary = { tab ->
                            // Navigate to the LibraryScreen with the specified tab
                            navController.navigate(Screen.Library.createRoute(tab)) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        showAddToPlaylistSheet = showAddToPlaylistSheet.value,
                        onAddToPlaylistSheetDismiss = {
                            showAddToPlaylistSheet.value = false
                            viewModel.clearSelectedSongForPlaylist()
                        },
                        onAddSongToPlaylist = { song, playlistId ->
                            viewModel.addSongToPlaylist(song, playlistId) { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        },
                        onCreatePlaylist = { name ->
                            viewModel.createPlaylist(name)
                        },
                        onShowCreatePlaylistDialog = {
                            showCreatePlaylistDialog.value = true
                        },
                        onClearQueue = {
                            // Use the proper clearQueue method
                            viewModel.clearQueue()
                        },
                        // New parameters for loader control and bottom sheets
                        isMediaLoading = viewModel.isBuffering.collectAsState().value,
                        isSeeking = viewModel.isSeeking.collectAsState().value,
                        onShowAlbumBottomSheet = {
                            // This is now handled internally by the PlayerScreen
                        },
                        onShowArtistBottomSheet = {
                            // This is now handled internally by the PlayerScreen
                        },
                        // Pass album and artist data for bottom sheets
                        songs = viewModel.songs.collectAsState().value,
                        albums = viewModel.albums.collectAsState().value,
                        artists = viewModel.artists.collectAsState().value,
                        onPlayAlbumSongs = { songs -> viewModel.playSongs(songs) },
                        onShuffleAlbumSongs = { songs -> viewModel.playShuffled(songs) },
                        onPlayArtistSongs = { songs -> viewModel.playSongs(songs) },
                        onShuffleArtistSongs = { songs -> viewModel.playShuffled(songs) },
                        appSettings = appSettings,
                        musicViewModel = viewModel,
                        navController = navController
                    )
                }

                // Add playlist detail screen
                @OptIn(ExperimentalMaterial3Api::class)
                composable(
                    route = Screen.PlaylistDetail.route,
                    arguments = listOf(
                        navArgument("playlistId") {
                            type = NavType.StringType
                        }
                    ),
                    // Enhanced transitions to match AboutScreen pattern
                    enterTransition = {
                        fadeIn(animationSpec = tween(350)) +
                                scaleIn(
                                    initialScale = 0.85f,
                                    animationSpec = tween(400, easing = EaseOutQuint)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(350)) +
                                scaleOut(
                                    targetScale = 0.85f,
                                    animationSpec = tween(300, easing = EaseInOutQuart)
                                )
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(350)) +
                                scaleIn(
                                    initialScale = 0.85f,
                                    animationSpec = tween(400, easing = EaseOutQuint)
                                )
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(350)) +
                                scaleOut(
                                    targetScale = 0.85f,
                                    animationSpec = tween(300, easing = EaseInOutQuart)
                                )
                    }
                ) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                    val playlist = playlists.find { it.id == playlistId }

                    // Album/Artist data for bottom sheets
                    val allAlbums by viewModel.albums.collectAsState()
                    var selectedAlbumForSheet by remember { mutableStateOf<chromahub.rhythm.app.shared.data.model.Album?>(null) }
                    var showAlbumSheet by remember { mutableStateOf(false) }
                    val playlistHaptics = LocalHapticFeedback.current

                    if (playlist != null) {
                        PlaylistDetailScreen(
                            playlist = playlist,
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            progress = progress,
                            onPlayPause = onPlayPause,
                            onPlayerClick = {
                                navController.navigate(Screen.Player.route)
                            },
                            onPlayAll = {
                                onPlayPlaylist(playlist)
                            },
                            onShufflePlay = {
                                // Play shuffled playlist songs using the proper shuffled playlist playback
                                onPlayPlaylistShuffled(playlist)
                            },
                            onSongClick = onPlaySong,
                            onPlaySongFromPlaylist = { song, playlistSongs ->
                                viewModel.playSongFromContext(song, playlistSongs, playlist.name)
                            },
                            onBack = {
                                navController.popBackStack()
                            },
                            onRemoveSong = { song, message ->
                                viewModel.removeSongFromPlaylist(
                                    song,
                                    playlistId
                                ) { snackbarMessage ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(snackbarMessage)
                                    }
                                }
                            },
                            onRenamePlaylist = { newName ->
                                viewModel.renamePlaylist(playlistId, newName)
                            },
                            onDeletePlaylist = {
                                viewModel.deletePlaylist(playlistId)
                                navController.popBackStack()
                            },
                            onAddSongsToPlaylist = {
                                // Set the target playlist ID and navigate to search screen
                                viewModel.setTargetPlaylistForAddingSongs(playlistId)
                                navController.navigate(Screen.AddToPlaylist.route)
                            },
                            onSkipNext = onSkipNext,
                            onReorderSongs = { fromIndex, toIndex ->
                                viewModel.reorderPlaylistSongs(playlistId, fromIndex, toIndex)
                            },
                            onUpdatePlaylistSongs = { newSongList ->
                                viewModel.updatePlaylistSongs(playlistId, newSongList)
                            },
                            onPlayNext = { song ->
                                viewModel.playNext(song)
                            },
                            onAddToQueue = { song ->
                                viewModel.addSongToQueue(song)
                            },
                            onGoToAlbum = { song ->
                                val album = allAlbums.find { it.title == song.album }
                                if (album != null) {
                                    selectedAlbumForSheet = album
                                    showAlbumSheet = true
                                }
                            },
                            onGoToArtist = { song ->
                                navController.navigate(Screen.ArtistDetail.createRoute(song.artist))
                            }
                        )
                    }

                    // Album Bottom Sheet
                    if (showAlbumSheet && selectedAlbumForSheet != null) {
                        val albumSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                        AlbumBottomSheet(
                            album = selectedAlbumForSheet!!,
                            onDismiss = { showAlbumSheet = false; selectedAlbumForSheet = null },
                            onSongClick = onPlaySong,
                            onPlayAll = { songs -> viewModel.playSongs(songs) },
                            onShufflePlay = { songs -> viewModel.playShuffled(songs) },
                            onAddToQueue = { song -> viewModel.addSongToQueue(song) },
                            onAddSongToPlaylist = { },
                            onPlayerClick = { navController.navigate(Screen.Player.route) },
                            sheetState = albumSheetState,
                            haptics = playlistHaptics,
                            onPlayNext = { song -> viewModel.playNext(song) },
                            currentSong = currentSong,
                            isPlaying = isPlaying
                        )
                    }
                }

                // Artist Detail Screen
                composable(
                    route = Screen.ArtistDetail.route,
                    arguments = listOf(
                        navArgument("artistName") {
                            type = NavType.StringType
                        }
                    ),
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    }
                ) { backStackEntry ->
                    val artistName = backStackEntry.arguments?.getString("artistName")?.let { Uri.decode(it) } ?: ""
                    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
                    
                    // Write permission launcher for Android 11+ metadata editing
                    val writePermissionLauncher = rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult()
                    ) { result: ActivityResult ->
                        if (result.resultCode == android.app.Activity.RESULT_OK) {
                            viewModel.completeMetadataWriteAfterPermission(
                                onSuccess = {
                                    android.widget.Toast.makeText(context, "Metadata saved successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMessage ->
                                    android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            viewModel.cancelPendingMetadataWrite()
                            android.widget.Toast.makeText(context, "Permission denied. Changes saved to library only.", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                    
                    // State for bottom sheets
                    var showAddToPlaylistSheet by remember { mutableStateOf(false) }
                    var selectedSongForPlaylist by remember { mutableStateOf<chromahub.rhythm.app.shared.data.model.Song?>(null) }
                    var showSongInfoSheet by remember { mutableStateOf(false) }
                    var selectedSongForInfo by remember { mutableStateOf<chromahub.rhythm.app.shared.data.model.Song?>(null) }
                    var showAlbumBottomSheet by remember { mutableStateOf(false) }
                    var selectedAlbum by remember { mutableStateOf<chromahub.rhythm.app.shared.data.model.Album?>(null) }
                    val albumBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
                    
                    ArtistDetailScreen(
                        artistName = artistName,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSongClick = onPlaySong,
                        onAlbumClick = { album ->
                            selectedAlbum = album
                            showAlbumBottomSheet = true
                        },
                        onPlayAll = { songs ->
                            if (songs.isNotEmpty()) {
                                viewModel.playSongs(songs)
                            }
                        },
                        onShufflePlay = { songs ->
                            if (songs.isNotEmpty()) {
                                viewModel.playShuffled(songs)
                            }
                        },
                        onAddToQueue = { song ->
                            viewModel.addSongToQueue(song)
                        },
                        onAddSongToPlaylist = { song ->
                            selectedSongForPlaylist = song
                            showAddToPlaylistSheet = true
                        },
                        onPlayerClick = {
                            navController.navigate(Screen.Player.route)
                        },
                        onPlayNext = { song ->
                            viewModel.playNext(song)
                        },
                        onToggleFavorite = { song ->
                            viewModel.toggleFavorite(song)
                        },
                        favoriteSongs = favoriteSongs,
                        onShowSongInfo = { song ->
                            selectedSongForInfo = song
                            showSongInfoSheet = true
                        },
                        currentSong = currentSong,
                        isPlaying = isPlaying
                    )
                    
                    // Add to playlist bottom sheet
                    if (showAddToPlaylistSheet && selectedSongForPlaylist != null) {
                        AddToPlaylistBottomSheet(
                            song = selectedSongForPlaylist!!,
                            playlists = playlists,
                            onDismissRequest = { 
                                showAddToPlaylistSheet = false
                                selectedSongForPlaylist = null
                            },
                            onAddToPlaylist = { playlist ->
                                viewModel.addSongToPlaylist(selectedSongForPlaylist!!, playlist.id) { message ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }
                                showAddToPlaylistSheet = false
                                selectedSongForPlaylist = null
                            },
                            onCreateNewPlaylist = {
                                // For now, just close the sheet. Could implement create playlist dialog here
                                showAddToPlaylistSheet = false
                                selectedSongForPlaylist = null
                            }
                        )
                    }
                    
                    // Song info bottom sheet
                    if (showSongInfoSheet && selectedSongForInfo != null) {
                        SongInfoBottomSheet(
                            song = selectedSongForInfo!!,
                            onDismiss = { 
                                showSongInfoSheet = false
                                selectedSongForInfo = null
                            },
                            appSettings = appSettings,
                            onEditSong = { title, artist, album, genre, year, trackNumber ->
                                viewModel.saveMetadataChanges(
                                    song = selectedSongForInfo!!,
                                    title = title,
                                    artist = artist,
                                    album = album,
                                    genre = genre,
                                    year = year,
                                    trackNumber = trackNumber,
                                    onSuccess = { fileWriteSucceeded ->
                                        if (fileWriteSucceeded) {
                                            android.widget.Toast.makeText(context, "Metadata saved successfully to file!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onError = { errorMessage ->
                                        android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_LONG).show()
                                    },
                                    onPermissionRequired = { pendingRequest ->
                                        try {
                                            val intentSenderRequest = androidx.activity.result.IntentSenderRequest.Builder(
                                                pendingRequest.intentSender
                                            ).build()
                                            writePermissionLauncher.launch(intentSenderRequest)
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "Permission required to modify file metadata", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        )
                    }
                    
                    // Album bottom sheet
                    if (showAlbumBottomSheet && selectedAlbum != null) {
                        AlbumBottomSheet(
                            album = selectedAlbum!!,
                            onDismiss = { 
                                showAlbumBottomSheet = false
                                selectedAlbum = null
                            },
                            onSongClick = onPlaySong,
                            onPlayAll = { songs -> viewModel.playSongs(songs) },
                            onShufflePlay = { songs -> viewModel.playShuffled(songs) },
                            onAddToQueue = { song -> viewModel.addSongToQueue(song) },
                            onAddSongToPlaylist = { song ->
                                selectedSongForPlaylist = song
                                showAddToPlaylistSheet = true
                            },
                            onPlayerClick = { navController.navigate(Screen.Player.route) },
                            sheetState = albumBottomSheetState,
                            haptics = LocalHapticFeedback.current,
                            onPlayNext = { song -> viewModel.playNext(song) },
                            onToggleFavorite = { song -> viewModel.toggleFavorite(song) },
                            favoriteSongs = favoriteSongs,
                            onShowSongInfo = { song ->
                                selectedSongForInfo = song
                                showSongInfoSheet = true
                            },
                            onAddToBlacklist = { song -> appSettings.addToBlacklist(song.id) },
                            currentSong = currentSong,
                            isPlaying = isPlaying
                        )
                    }
                }

                // Add to playlist screen
                @OptIn(ExperimentalMaterial3Api::class)
                composable(
                    Screen.AddToPlaylist.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                    }
                ) {
                    val songToAdd = viewModel.selectedSongForPlaylist.collectAsState().value
                    val targetPlaylistId = viewModel.targetPlaylistId.collectAsState().value

                    var searchQuery by remember { mutableStateOf("") }

                    // If we have a target playlist ID, we're adding songs to that playlist
                    if (targetPlaylistId != null) {
                        val targetPlaylist = playlists.find { it.id == targetPlaylistId }

                        if (targetPlaylist != null) {
                            // Filter available songs
                            val availableSongs =
                                remember(allSongs, targetPlaylist.songs, searchQuery) {
                                    allSongs.filter { song ->
                                        // Filter out songs that are already in the playlist
                                        !targetPlaylist.songs.any { it.id == song.id }
                                    }
                                }

                            if (availableSongs.isEmpty() && searchQuery.isBlank()) {
                                // No songs to add and no search query
                                AlertDialog(
                                    onDismissRequest = {
                                        viewModel.clearTargetPlaylistForAddingSongs()
                                        navController.popBackStack()
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    title = { Text("No Songs Available") },
                                    text = { Text("All songs are already in this playlist.") },
                                    confirmButton = {
                                        Button(onClick = {
                                            viewModel.clearTargetPlaylistForAddingSongs()
                                            navController.popBackStack()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("OK")
                                        }
                                    },
                                    shape = RoundedCornerShape(24.dp)
                                )
                            } else {
                                // Show the new multi-selection screen
                                AddToPlaylistScreen(
                                    targetPlaylist = targetPlaylist,
                                    availableSongs = availableSongs,
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { searchQuery = it },
                                    onBackClick = {
                                        viewModel.clearTargetPlaylistForAddingSongs()
                                        navController.popBackStack()
                                    },
                                    onAddSongsToPlaylist = { songs ->
                                        // Add multiple songs to the playlist using batch operation
                                        if (songs.size == 1) {
                                            // Single song - use original method for individual feedback
                                            viewModel.addSongToPlaylist(
                                                songs[0],
                                                targetPlaylistId
                                            ) { message ->
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(message)
                                                }
                                            }
                                        } else {
                                            // Multiple songs - use batch operation
                                            val (successCount, playlistName) = viewModel.addSongsToPlaylist(
                                                songs,
                                                targetPlaylistId
                                            )
                                            coroutineScope.launch {
                                                val message = when {
                                                    successCount == 0 -> "No songs added - they may already be in the playlist"
                                                    successCount == songs.size -> "Added $successCount songs to $playlistName"
                                                    else -> "Added $successCount of ${songs.size} songs to $playlistName"
                                                }
                                                snackbarHostState.showSnackbar(message)
                                            }
                                        }
                                    }
                                )
                            }
                        } else {
                            // Playlist not found, go back
                            LaunchedEffect(Unit) {
                                viewModel.clearTargetPlaylistForAddingSongs()
                                navController.popBackStack()
                            }
                        }
                    }
                    // If we have a song to add (from Player or Search), we're adding it to a playlist
                    else if (songToAdd != null) {
                        // Use a simpler approach without the bottom sheet state
                        var showCreatePlaylistDialog by remember { mutableStateOf(false) }

                        if (showCreatePlaylistDialog) {
                            CreatePlaylistDialog(
                                onDismiss = {
                                    showCreatePlaylistDialog = false
                                },
                                onConfirm = { name ->
                                    viewModel.createPlaylist(name)
                                    showCreatePlaylistDialog = false
                                },
                                song = songToAdd,
                                onConfirmWithSong = { name ->
                                    viewModel.createPlaylist(name)
                                    // The new playlist will be at the end of the list
                                    val newPlaylist = viewModel.playlists.value.last()
                                    viewModel.addSongToPlaylist(
                                        songToAdd,
                                        newPlaylist.id
                                    ) { message ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                    viewModel.clearSelectedSongForPlaylist()
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            // Navigate back to the player screen and show the bottom sheet there
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    } else {
                        // No song selected and no target playlist, go back
                        LaunchedEffect(Unit) {
                            viewModel.clearSelectedSongForPlaylist()
                            viewModel.clearTargetPlaylistForAddingSongs()
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }

    // Queue action dialog - show at top level
    val queueActionRequest by viewModel.queueActionRequest.collectAsState()
    val currentQueueForDialog by viewModel.currentQueue.collectAsState()

    queueActionRequest?.let { request ->
        QueueActionDialog(
            song = request.song,
            queueSize = currentQueueForDialog.songs.size,
            onDismiss = { viewModel.dismissQueueActionDialog() },
            onClearAndPlay = {
                viewModel.handleQueueActionChoice(request.song, clearQueue = true)
            },
            onAddToQueue = {
                viewModel.handleQueueActionChoice(request.song, clearQueue = false)
            }
        )
    }

    // Media scan loader overlay for refresh operations
    AnimatedVisibility(
        visible = isMediaScanning,
        enter = fadeIn(
            animationSpec = tween(
                800,
                easing = androidx.compose.animation.core.EaseOutCubic
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                800,
                easing = androidx.compose.animation.core.EaseInCubic
            )
        )
    ) {
        MediaScanLoader(
            musicViewModel = viewModel,
            onScanComplete = {
                // Media scan loader will hide automatically when isMediaScanning becomes false
            }
        )
    }
    }

/**
 * Navigation rail for tablets with Material 3 design - Local Navigation
 */
@Composable
private fun LocalNavigationRail(
    currentRoute: String,
    navController: NavHostController,
    firstVisibleLibraryTab: LibraryTab,
    context: android.content.Context,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    // Calculate rail height based on number of items (5 items * 64dp + padding)
    val railHeight = (5 * 64 + 32).dp // Increased padding from 24 to 32
    
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),
        contentAlignment = Alignment.Center // Vertically center the rail
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 3.dp,
            modifier = Modifier
                .height(railHeight)
                .width(80.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp), // Increased from 12.dp to 16.dp
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
            val libraryRoute = Screen.Library.createRoute(firstVisibleLibraryTab)
            val items = listOf(
                LocalNavRailItem(
                    route = Screen.Home.route,
                    title = "Home",
                    selectedIcon = RhythmIcons.HomeFilled,
                    unselectedIcon = RhythmIcons.Home,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                ),
                LocalNavRailItem(
                    route = libraryRoute,
                    title = "Library",
                    selectedIcon = Icons.Filled.LibraryMusic,
                    unselectedIcon = RhythmIcons.Library,
                    onClick = {
                        navController.navigate(libraryRoute) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    }
                ),
                LocalNavRailItem(
                    route = Screen.ListeningStats.route,
                    title = "Stats",
                    selectedIcon = Icons.Filled.AutoGraph,
                    unselectedIcon = Icons.Outlined.AutoGraph,
                    onClick = {
                        navController.navigate(Screen.ListeningStats.route)
                    }
                ),
                LocalNavRailItem(
                    route = Screen.Search.route,
                    title = "Search",
                    selectedIcon = Icons.Filled.Search,
                    unselectedIcon = Icons.Outlined.Search,
                    onClick = {
                        navController.navigate(Screen.Search.route)
                    }
                ),
                LocalNavRailItem(
                    route = Screen.Settings.route,
                    title = "Settings",
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                    onClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            )
            
            items.forEach { item ->
                LocalNavigationRailItemWithAnimation(
                    item = item,
                    isSelected = when (item.title) {
                        "Home" -> currentRoute == Screen.Home.route
                        "Library" -> currentRoute.startsWith("library")
                        "Search" -> currentRoute == Screen.Search.route
                        "Settings" -> currentRoute.contains("settings")
                        "Stats" -> currentRoute == Screen.ListeningStats.route
                        else -> false
                    },
                    haptic = haptic,
                    context = context
                )
            }
        }
        }
    }
}

/**
 * Data class for local navigation rail items
 */
private data class LocalNavRailItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val onClick: () -> Unit
)

/**
 * Local navigation rail item with animated selection indicator
 */
@Composable
private fun LocalNavigationRailItemWithAnimation(
    item: LocalNavRailItem,
    isSelected: Boolean,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    context: android.content.Context
) {
    // Enhanced animation values with spring physics
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale_${item.title}"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "alpha_${item.title}"
    )

    // Icon color animation
    val iconColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "iconColor_${item.title}"
    )

    // Indicator pill animation
    val indicatorHeight by animateDpAsState(
        targetValue = if (isSelected) 56.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "indicatorHeight_${item.title}"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                item.onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    alpha = animatedAlpha
                }
                .then(
                    if (isSelected) Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .size(64.dp, indicatorHeight.coerceAtLeast(0.dp))
                        .padding(vertical = 8.dp)
                    else Modifier.padding(8.dp)
                )
        ) {
            // Animated icon with crossfade
            androidx.compose.animation.Crossfade(
                targetState = isSelected,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow
                ),
                label = "iconCrossfade_${item.title}"
            ) { selected ->
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = fadeOut(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = iconColor
                )
            }
        }
    }
}