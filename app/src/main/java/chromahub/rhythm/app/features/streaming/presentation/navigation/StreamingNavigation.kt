package chromahub.rhythm.app.features.streaming.presentation.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import chromahub.rhythm.app.R
import chromahub.rhythm.app.features.local.presentation.components.player.MiniPlayer
import chromahub.rhythm.app.features.local.presentation.navigation.Screen
import chromahub.rhythm.app.features.local.presentation.screens.EqualizerScreen
import chromahub.rhythm.app.features.local.presentation.screens.PlayerScreen
import chromahub.rhythm.app.features.local.presentation.screens.settings.QueuePlaybackSettingsScreen
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel as LocalMusicViewModel
import chromahub.rhythm.app.features.streaming.domain.model.StreamingSong
import chromahub.rhythm.app.features.streaming.presentation.screens.StreamingContentHomeScreen
import chromahub.rhythm.app.features.streaming.presentation.screens.StreamingHomeScreen
import chromahub.rhythm.app.features.streaming.presentation.screens.StreamingSearchScreen
import chromahub.rhythm.app.features.streaming.presentation.screens.StreamingServiceSetupScreen
import chromahub.rhythm.app.features.streaming.presentation.screens.StreamingSettingsScreen
import chromahub.rhythm.app.features.streaming.presentation.viewmodel.StreamingMusicViewModel
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveShapes
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.ui.theme.MusicDimensions
import chromahub.rhythm.app.util.HapticUtils

private sealed class StreamingScreen(val route: String, val titleRes: Int? = null) {
    data object Integration : StreamingScreen("streaming_integration")
    data object Home : StreamingScreen("streaming_home", R.string.home)
    data object Search : StreamingScreen("streaming_search", R.string.search)
    data object Settings : StreamingScreen("streaming_settings", R.string.settings_title)
    data object Player : StreamingScreen("streaming_player")
    data object ServiceSetup : StreamingScreen("streaming_service_setup/{serviceId}") {
        fun createRoute(serviceId: String): String = "streaming_service_setup/$serviceId"
    }
}

@Composable
fun StreamingNavigation(
    localMusicViewModel: LocalMusicViewModel = viewModel(),
    streamingMusicViewModel: StreamingMusicViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToPlayer: () -> Unit = {},
    onSwitchToLocalMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: StreamingScreen.Integration.route

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val appSettings = remember { AppSettings.getInstance(context) }

    val currentSong by localMusicViewModel.currentSong.collectAsState()
    val isPlaying by localMusicViewModel.isPlaying.collectAsState()
    val progress by localMusicViewModel.progress.collectAsState()
    val queueState by localMusicViewModel.currentQueue.collectAsState()
    val currentDevice by localMusicViewModel.currentDevice.collectAsState()
    val locations by localMusicViewModel.locations.collectAsState()
    val volume by localMusicViewModel.volume.collectAsState()
    val isMuted by localMusicViewModel.isMuted.collectAsState()
    val isShuffleEnabled by localMusicViewModel.isShuffleEnabled.collectAsState()
    val repeatMode by localMusicViewModel.repeatMode.collectAsState()
    val isFavorite by localMusicViewModel.isFavorite.collectAsState()
    val showLyrics by localMusicViewModel.showLyrics.collectAsState()
    val showOnlineOnlyLyrics by localMusicViewModel.showOnlineOnlyLyrics.collectAsState()
    val lyrics by localMusicViewModel.currentLyrics.collectAsState()
    val isLoadingLyrics by localMusicViewModel.isLoadingLyrics.collectAsState()
    val playlists by localMusicViewModel.playlists.collectAsState()
    val songs by localMusicViewModel.songs.collectAsState()
    val albums by localMusicViewModel.albums.collectAsState()
    val artists by localMusicViewModel.artists.collectAsState()
    val isMediaLoading by localMusicViewModel.isBuffering.collectAsState()
    val isSeeking by localMusicViewModel.isSeeking.collectAsState()

    val sessions by streamingMusicViewModel.serviceSessions.collectAsState()
    val hasConnectedService = sessions.values.any { it.isConnected }

    val isServiceSetupRoute = currentRoute.startsWith("streaming_service_setup")
    val isPlayerRoute = currentRoute == StreamingScreen.Player.route
    val isPlayerUtilityRoute = currentRoute == Screen.Equalizer.route || currentRoute == Screen.TunerQueuePlayback.route
    val showMiniPlayer = currentSong != null && !isPlayerRoute
    val showBottomNav = hasConnectedService && !isPlayerRoute && !isPlayerUtilityRoute && (
        currentRoute == StreamingScreen.Home.route ||
            currentRoute == StreamingScreen.Settings.route ||
            currentRoute == StreamingScreen.Search.route
        )
    val requiresConnectedService =
        currentRoute == StreamingScreen.Home.route ||
            currentRoute == StreamingScreen.Search.route ||
            currentRoute == StreamingScreen.Settings.route

    val navigateToTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LaunchedEffect(streamingMusicViewModel, localMusicViewModel, navController) {
        streamingMusicViewModel.setPlaybackHandler { streamingQueue, startIndex ->
            val mappedQueue = streamingQueue.mapNotNull { it.toLocalSong() }
            if (mappedQueue.isEmpty()) {
                return@setPlaybackHandler
            }

            val safeIndex = startIndex.coerceIn(0, mappedQueue.lastIndex)
            val targetSong = mappedQueue[safeIndex]

            localMusicViewModel.playSongFromSearch(targetSong, mappedQueue)
            onNavigateToPlayer()
            navController.navigate(StreamingScreen.Player.route) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(hasConnectedService, currentRoute) {
        when {
            !hasConnectedService && requiresConnectedService && !isServiceSetupRoute -> {
                navController.navigate(StreamingScreen.Integration.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
            hasConnectedService && currentRoute == StreamingScreen.Integration.route -> {
                navController.navigate(StreamingScreen.Home.route) {
                    popUpTo(StreamingScreen.Integration.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.25f to MaterialTheme.colorScheme.surface.copy(alpha = 0.28f),
                                0.62f to MaterialTheme.colorScheme.surface.copy(alpha = 0.76f),
                                1f to MaterialTheme.colorScheme.surface.copy(alpha = 1f)
                            )
                        )
                    )
            ) {
                AnimatedVisibility(
                    visible = showMiniPlayer,
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
                            onPlayPause = { localMusicViewModel.togglePlayPause() },
                            onPlayerClick = {
                                onNavigateToPlayer()
                                navController.navigate(StreamingScreen.Player.route) {
                                    launchSingleTop = true
                                }
                            },
                            onSkipNext = { localMusicViewModel.skipToNext() },
                            onSkipPrevious = { localMusicViewModel.skipToPrevious() },
                            onDismiss = { localMusicViewModel.clearCurrentSong() },
                            isMediaLoading = isMediaLoading,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }

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
                    StreamingBottomBar(
                        currentRoute = currentRoute,
                        navController = navController,
                        context = context,
                        haptic = haptic,
                        onSearchClick = { navigateToTopLevel(StreamingScreen.Search.route) }
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = StreamingScreen.Integration.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(
                route = StreamingScreen.Integration.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(250))
                }
            ) {
                StreamingHomeScreen(
                    viewModel = streamingMusicViewModel,
                    onNavigateToSettings = onNavigateToSettings,
                    onConfigureService = { serviceId ->
                        navController.navigate(StreamingScreen.ServiceSetup.createRoute(serviceId)) {
                            launchSingleTop = true
                        }
                    },
                    onSwitchToLocalMode = onSwitchToLocalMode
                )
            }

            composable(
                route = StreamingScreen.Home.route,
                enterTransition = {
                    when {
                        initialState.destination.route == StreamingScreen.Settings.route -> {
                            fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                        }
                        else -> {
                            fadeIn(animationSpec = tween(300))
                        }
                    }
                },
                exitTransition = {
                    when {
                        targetState.destination.route == StreamingScreen.Settings.route -> {
                            fadeOut(animationSpec = tween(300)) +
                                slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                        }
                        else -> {
                            fadeOut(animationSpec = tween(300))
                        }
                    }
                },
                popEnterTransition = {
                    when {
                        initialState.destination.route == StreamingScreen.Settings.route -> {
                            fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(350, easing = EaseInOutQuart)
                                )
                        }
                        else -> {
                            fadeIn(animationSpec = tween(200))
                        }
                    }
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(200))
                }
            ) {
                StreamingContentHomeScreen(
                    viewModel = streamingMusicViewModel,
                    onNavigateToSettings = {
                        navigateToTopLevel(StreamingScreen.Settings.route)
                    },
                    onConfigureService = { serviceId ->
                        navController.navigate(StreamingScreen.ServiceSetup.createRoute(serviceId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = StreamingScreen.Search.route,
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
                StreamingSearchScreen(
                    viewModel = streamingMusicViewModel,
                    onConfigureService = { serviceId ->
                        navController.navigate(StreamingScreen.ServiceSetup.createRoute(serviceId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = StreamingScreen.Settings.route,
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
                StreamingSettingsScreen(
                    viewModel = streamingMusicViewModel,
                    onOpenGlobalSettings = onNavigateToSettings,
                    onConfigureService = { serviceId ->
                        navController.navigate(StreamingScreen.ServiceSetup.createRoute(serviceId)) {
                            launchSingleTop = true
                        }
                    },
                    onSwitchToLocalMode = onSwitchToLocalMode
                )
            }

            composable(
                route = StreamingScreen.Player.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = EaseInOutQuart
                        )
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 250)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 200)
                    )
                },
                popExitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 250)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 200)
                    )
                },
                popEnterTransition = {
                    slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(durationMillis = 350)
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            ) {
                val currentSongDurationMs = (currentSong?.duration ?: 0L) * 1000L

                PlayerScreen(
                    song = currentSong,
                    isPlaying = isPlaying,
                    progress = progress,
                    location = currentDevice,
                    queuePosition = (queueState.currentIndex + 1).coerceAtLeast(1),
                    queueTotal = queueState.songs.size.coerceAtLeast(1),
                    onPlayPause = { localMusicViewModel.togglePlayPause() },
                    onSkipNext = { localMusicViewModel.skipToNext() },
                    onSkipPrevious = { localMusicViewModel.skipToPrevious() },
                    onSeek = { position -> localMusicViewModel.seekTo(position) },
                    onLyricsSeek = { lyricPositionMs ->
                        if (currentSongDurationMs > 0L) {
                            localMusicViewModel.seekTo(
                                (lyricPositionMs.toFloat() / currentSongDurationMs.toFloat()).coerceIn(0f, 1f)
                            )
                        }
                    },
                    onBack = { navController.popBackStack() },
                    onLocationClick = { localMusicViewModel.showOutputSwitcherDialog() },
                    onQueueClick = {},
                    locations = locations,
                    onLocationSelect = { location -> localMusicViewModel.setCurrentDevice(location) },
                    volume = volume,
                    isMuted = isMuted,
                    onVolumeChange = { newVolume -> localMusicViewModel.setVolume(newVolume) },
                    onToggleMute = { localMusicViewModel.toggleMute() },
                    onMaxVolume = { localMusicViewModel.maxVolume() },
                    onRefreshDevices = { localMusicViewModel.startDeviceMonitoringOnDemand() },
                    onStopDeviceMonitoring = { localMusicViewModel.stopDeviceMonitoringOnDemand() },
                    onToggleShuffle = { localMusicViewModel.toggleShuffle() },
                    onToggleRepeat = { localMusicViewModel.toggleRepeatMode() },
                    onToggleFavorite = { localMusicViewModel.toggleFavorite() },
                    isShuffleEnabled = isShuffleEnabled,
                    repeatMode = repeatMode,
                    isFavorite = isFavorite,
                    showLyrics = showLyrics,
                    onlineOnlyLyrics = showOnlineOnlyLyrics,
                    lyrics = lyrics,
                    isLoadingLyrics = isLoadingLyrics,
                    onRetryLyrics = { localMusicViewModel.retryFetchLyrics() },
                    playlists = playlists,
                    queue = queueState.songs,
                    onSongClick = { song -> localMusicViewModel.playSong(song) },
                    onSongClickAtIndex = { index -> localMusicViewModel.playSongAtIndex(index) },
                    onRemoveFromQueueAtIndex = { index -> localMusicViewModel.removeFromQueueAtIndex(index) },
                    onMoveQueueItem = { fromIndex, toIndex ->
                        localMusicViewModel.moveQueueItem(fromIndex, toIndex)
                    },
                    onAddSongsToQueue = { navigateToTopLevel(StreamingScreen.Search.route) },
                    onNavigateToLibrary = { navigateToTopLevel(StreamingScreen.Home.route) },
                    onClearQueue = { localMusicViewModel.clearQueue() },
                    isMediaLoading = isMediaLoading,
                    isSeeking = isSeeking,
                    songs = songs,
                    albums = albums,
                    artists = artists,
                    onPlayAlbumSongs = { albumSongs -> localMusicViewModel.playSongs(albumSongs) },
                    onShuffleAlbumSongs = { albumSongs -> localMusicViewModel.playShuffled(albumSongs) },
                    onPlayArtistSongs = { artistSongs -> localMusicViewModel.playSongs(artistSongs) },
                    onShuffleArtistSongs = { artistSongs -> localMusicViewModel.playShuffled(artistSongs) },
                    appSettings = appSettings,
                    musicViewModel = localMusicViewModel,
                    navController = navController
                )
            }

            composable(route = Screen.TunerQueuePlayback.route) {
                QueuePlaybackSettingsScreen(onBackClick = { navController.popBackStack() })
            }

            composable(route = Screen.Equalizer.route) {
                EqualizerScreen(
                    navController = navController,
                    viewModel = localMusicViewModel
                )
            }

            composable(
                route = StreamingScreen.ServiceSetup.route,
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType }),
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
                val serviceId = backStackEntry.arguments?.getString("serviceId").orEmpty()
                if (serviceId.isNotBlank()) {
                    StreamingServiceSetupScreen(
                        serviceId = serviceId,
                        viewModel = streamingMusicViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

private fun StreamingSong.toLocalSong(): Song? {
    val playbackUrl = when {
        !streamingUrl.isNullOrBlank() -> streamingUrl
        !previewUrl.isNullOrBlank() -> previewUrl
        else -> null
    } ?: return null

    return Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = Uri.parse(playbackUrl),
        artworkUri = artworkUri?.takeIf { it.isNotBlank() }?.let(Uri::parse)
    )
}

@Composable
private fun StreamingBottomBar(
    currentRoute: String,
    navController: NavHostController,
    context: android.content.Context,
    haptic: HapticFeedback,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = ExpressiveShapes.Full,
                tonalElevation = 3.dp,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .height(MusicDimensions.bottomNavigationHeight)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val items = listOf(
                        Triple(
                            StreamingScreen.Home.route,
                            context.getString(R.string.home),
                            Pair(RhythmIcons.HomeFilled, RhythmIcons.Home)
                        ),
                        Triple(
                            StreamingScreen.Settings.route,
                            context.getString(R.string.settings_title),
                            Pair(RhythmIcons.SettingsFilled, RhythmIcons.Settings)
                        )
                    )

                    items.forEach { (route, title, icons) ->
                        val isSelected = currentRoute == route
                        val (selectedIcon, unselectedIcon) = icons

                        val animatedScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "streaming_bottom_scale_$title"
                        )

                        val animatedAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0.7f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "streaming_bottom_alpha_$title"
                        )

                        val pillWidth by animateDpAsState(
                            targetValue = if (isSelected) 120.dp else 0.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "streaming_bottom_pill_$title"
                        )

                        val iconColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            animationSpec = tween(300),
                            label = "streaming_bottom_icon_$title"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
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
                                        if (isSelected) {
                                            Modifier
                                                .background(MaterialTheme.colorScheme.primaryContainer, ExpressiveShapes.Full)
                                                .height(48.dp)
                                                .widthIn(min = pillWidth)
                                                .padding(horizontal = 18.dp)
                                        } else {
                                            Modifier.padding(horizontal = 16.dp)
                                        }
                                    )
                            ) {
                                androidx.compose.animation.Crossfade(
                                    targetState = isSelected,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessVeryLow
                                    ),
                                    label = "streaming_icon_crossfade_$title"
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

            Spacer(modifier = Modifier.width(12.dp))

            val searchInteractionSource = remember { MutableInteractionSource() }
            val isSearchPressed by searchInteractionSource.collectIsPressedAsState()
            val searchScale by animateFloatAsState(
                targetValue = if (isSearchPressed) 0.88f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "streaming_search_button_scale"
            )

            FilledIconButton(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                    onSearchClick()
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = CircleShape,
                interactionSource = searchInteractionSource,
                modifier = Modifier
                    .size(MusicDimensions.bottomNavigationHeight)
                    .graphicsLayer {
                        scaleX = searchScale
                        scaleY = searchScale
                    }
            ) {
                Icon(
                    imageVector = RhythmIcons.Search,
                    contentDescription = context.getString(R.string.search),
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}
