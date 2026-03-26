

package chromahub.rhythm.app.features.local.presentation.components.bottomsheets

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.data.model.Album
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.features.local.presentation.components.player.PlayingEqIcon
import chromahub.rhythm.app.shared.presentation.components.common.M3PlaceholderType
import chromahub.rhythm.app.features.local.presentation.components.player.formatDuration
import chromahub.rhythm.app.shared.presentation.components.common.AutoScrollingTextOnDemand
import chromahub.rhythm.app.shared.presentation.components.AudioQualityBadges
import chromahub.rhythm.app.util.ImageUtils
import chromahub.rhythm.app.util.HapticUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AlbumSortOrder {
    TRACK_NUMBER,
    TITLE_ASC,
    TITLE_DESC,
    DURATION_ASC,
    DURATION_DESC
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AlbumBottomSheet(
    album: Album,
    onDismiss: () -> Unit,
    onSongClick: (Song) -> Unit,
    onPlayAll: (List<Song>) -> Unit,
    onShufflePlay: (List<Song>) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddSongToPlaylist: (Song) -> Unit,
    onPlayerClick: () -> Unit,
    sheetState: SheetState,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onPlayNext: (Song) -> Unit = {},
    onToggleFavorite: (Song) -> Unit = {},
    favoriteSongs: Set<String> = emptySet(),
    onShowSongInfo: (Song) -> Unit = {},
    onAddToBlacklist: (Song) -> Unit = {},
    currentSong: Song? = null,
    isPlaying: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // Detect tablet mode
    val isTablet = configuration.screenWidthDp >= 600

    // App settings for persistence
    val appSettings = remember { AppSettings.getInstance(context) }
    val savedSortOrder by appSettings.albumSortOrder.collectAsState()
    val useHoursFormat by appSettings.useHoursInTimeFormat.collectAsState()
    val albumBottomSheetGradientBlur by appSettings.albumBottomSheetGradientBlur.collectAsState()

    // Sort order state
    var sortOrder by remember {
        mutableStateOf(
            try {
                AlbumSortOrder.valueOf(savedSortOrder)
            } catch (e: Exception) {
                AlbumSortOrder.TRACK_NUMBER
            }
        )
    }
    var showSortMenu by remember { mutableStateOf(false) }

    // Save sort order when changed
    LaunchedEffect(sortOrder) {
        appSettings.setAlbumSortOrder(sortOrder.name)
    }

    // Staggered animation states
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Infinite shimmer animation for artwork background - loops continuously
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerOffset"
    )

    // Header animations
    val headerScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "headerScale"
    )

    val headerAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "headerAlpha"
    )

    // Content slide animation
    val contentOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 100f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "contentOffset"
    )

    // Sort songs
    val sortedSongs = remember(album.songs, sortOrder) {
        when (sortOrder) {
            AlbumSortOrder.TRACK_NUMBER -> album.songs.sortedWith { a, b ->
                if (a.discNumber != b.discNumber) {
                    a.discNumber.compareTo(b.discNumber)
                } else {
                    when {
                        a.trackNumber > 0 && b.trackNumber > 0 -> a.trackNumber.compareTo(b.trackNumber)
                        a.trackNumber > 0 -> -1
                        b.trackNumber > 0 -> 1
                        else -> a.title.compareTo(b.title, ignoreCase = true)
                    }
                }
            }
            AlbumSortOrder.TITLE_ASC -> album.songs.sortedBy { it.title.lowercase() }
            AlbumSortOrder.TITLE_DESC -> album.songs.sortedByDescending { it.title.lowercase() }
            AlbumSortOrder.DURATION_ASC -> album.songs.sortedBy { it.duration }
            AlbumSortOrder.DURATION_DESC -> album.songs.sortedByDescending { it.duration }
        }
    }

    // Calculate total duration
    val totalDuration = remember(sortedSongs) {
        sortedSongs.sumOf { it.duration }
    }

    // Lazy list state for scroll-based effects
    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 }
    }

    if (isTablet) {
        // Tablet layout: Dialog with side-by-side layout
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceContainerLow,
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .navigationBarsPadding()
                ) {
                    // Background artwork with blur effect for tablet (conditional)
                    if (album.artworkUri != null && albumBottomSheetGradientBlur) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .apply(
                                    ImageUtils.buildImageRequest(
                                        album.artworkUri,
                                        album.title,
                                        context.cacheDir,
                                        M3PlaceholderType.ALBUM
                                    )
                                )
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(25.dp)
                                .graphicsLayer { alpha = 0.4f }
                        )
                    }

                    // Animated gradient shimmer overlay (conditional)
                    if (albumBottomSheetGradientBlur) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.White.copy(alpha = 0.03f),
                                                Color.Transparent
                                            ),
                                            start = Offset(shimmerOffset - 500f, 0f),
                                            end = Offset(shimmerOffset, size.height)
                                        )
                                    )
                                }
                        )
                    }

                    // Multi-layer gradient overlay for depth (conditional)
                    if (albumBottomSheetGradientBlur) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0f to MaterialTheme.colorScheme.surfaceContainerLow.copy(
                                                alpha = 0.4f
                                            ),
                                            0.3f to Color.Transparent,
                                            0.7f to MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                            1f to MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                        )
                    }

                    // Radial highlight accent (conditional)
                    if (albumBottomSheetGradientBlur) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                            Color.Transparent
                                        ),
                                        center = Offset(0.7f, 0.2f),
                                        radius = 1000f
                                    )
                                )
                        )
                    }

                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left side: Album info and artwork
                        Surface(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight(),
                            color = Color.Transparent
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 24.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Album artwork
                                Surface(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .graphicsLayer {
                                            alpha = headerAlpha
                                            scaleX = headerScale
                                            scaleY = headerScale
                                        },
                                    shape = RoundedCornerShape(24.dp),
                                    shadowElevation = 16.dp,
                                    tonalElevation = 8.dp
                                ) {
                                    Box {
                                        if (album.artworkUri != null) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .apply(
                                                        ImageUtils.buildImageRequest(
                                                            album.artworkUri,
                                                            album.title,
                                                            context.cacheDir,
                                                            M3PlaceholderType.ALBUM
                                                        )
                                                    )
                                                    .build(),
                                                contentDescription = "Album artwork for ${album.title}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.linearGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.primaryContainer,
                                                                MaterialTheme.colorScheme.tertiaryContainer
                                                            )
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Album,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                        alpha = 0.7f
                                                    ),
                                                    modifier = Modifier.size(80.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Album info
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AutoScrollingTextOnDemand(
                                        text = album.title,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        gradientEdgeColor = MaterialTheme.colorScheme.surfaceContainer,
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = true,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = album.artist,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Metadata
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (album.year > 0) {
                                            MetadataChip(
                                                text = "${album.year}",
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }

                                        MetadataChip(
                                            text = "${sortedSongs.size} songs",
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )

                                        MetadataChip(
                                            text = formatDuration(totalDuration, useHoursFormat),
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Action buttons - Grouped button design
                                var shufflePressed by remember { mutableStateOf(false) }
                                var playAllPressed by remember { mutableStateOf(false) }
                                
                                val shuffleScale by animateFloatAsState(
                                    targetValue = if (shufflePressed) 0.96f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "shuffleScale"
                                )
                                
                                val playAllScale by animateFloatAsState(
                                    targetValue = if (playAllPressed) 0.96f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "playAllScale"
                                )
                                
                                LaunchedEffect(shufflePressed) {
                                    if (shufflePressed) {
                                        delay(150)
                                        shufflePressed = false
                                    }
                                }
                                
                                LaunchedEffect(playAllPressed) {
                                    if (playAllPressed) {
                                        delay(150)
                                        playAllPressed = false
                                    }
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    // Play All Button - Equal sizing with text
                                    Button(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(
                                                context,
                                                haptics,
                                                HapticFeedbackType.LongPress
                                            )
                                            playAllPressed = true
                                            if (sortedSongs.isNotEmpty()) {
                                                onPlayAll(sortedSongs)
                                            } else {
                                                onPlayAll(album.songs)
                                            }
                                            onDismiss()
                                            onPlayerClick()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .graphicsLayer {
                                                scaleX = playAllScale
                                                scaleY = playAllScale
                                            },
                                        shape = ButtonGroupDefaults.connectedLeadingButtonShapes().shape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = RhythmIcons.Play,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = context.getString(R.string.bottomsheet_play_all),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    // Shuffle Button - Equal sizing with text
                                    FilledTonalButton(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(
                                                context,
                                                haptics,
                                                HapticFeedbackType.LongPress
                                            )
                                            shufflePressed = true
                                            if (sortedSongs.isNotEmpty()) {
                                                onShufflePlay(sortedSongs)
                                            } else {
                                                onShufflePlay(album.songs)
                                            }
                                            onDismiss()
                                            onPlayerClick()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .graphicsLayer {
                                                scaleX = shuffleScale
                                                scaleY = shuffleScale
                                            },
                                        shape = ButtonGroupDefaults.connectedTrailingButtonShapes().shape,
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = RhythmIcons.Shuffle,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Shuffle",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Divider

                        // Right side: Songs list
                        Surface(
                            modifier = Modifier
                                .weight(0.6f)
                                .fillMaxHeight(),
                            color = Color.Transparent
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Header with sort
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 24.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = context.getString(R.string.bottomsheet_tracks),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Sort and Close buttons
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Sort button
                                            Box {
                                                FilledTonalIconButton(
                                                    onClick = { showSortMenu = true },
                                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                ) {
                                                    Icon(
                                                        imageVector = RhythmIcons.Actions.Sort,
                                                        contentDescription = "Sort songs",
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }

                                                DropdownMenu(
                                                    expanded = showSortMenu,
                                                    onDismissRequest = { showSortMenu = false },
                                                    shape = RoundedCornerShape(16.dp),
                                                    modifier = Modifier.padding(4.dp)
                                                ) {
                                                    AlbumSortOrder.entries.forEach { order ->
                                                        val isSelected = sortOrder == order
                                                        Surface(
                                                            color = if (isSelected)
                                                                MaterialTheme.colorScheme.primaryContainer.copy(
                                                                    alpha = 0.8f
                                                                )
                                                            else
                                                                Color.Transparent,
                                                            shape = RoundedCornerShape(12.dp),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(
                                                                    horizontal = 8.dp,
                                                                    vertical = 2.dp
                                                                )
                                                        ) {
                                                            DropdownMenuItem(
                                                                text = {
                                                                    Text(
                                                                        text = when (order) {
                                                                            AlbumSortOrder.TRACK_NUMBER -> "Track Number"
                                                                            AlbumSortOrder.TITLE_ASC, AlbumSortOrder.TITLE_DESC -> "Title"
                                                                            AlbumSortOrder.DURATION_ASC, AlbumSortOrder.DURATION_DESC -> "Duration"
                                                                        },
                                                                        style = MaterialTheme.typography.bodyMedium,
                                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                                        color = if (isSelected)
                                                                            MaterialTheme.colorScheme.onPrimaryContainer
                                                                        else
                                                                            MaterialTheme.colorScheme.onSurface
                                                                    )
                                                                },
                                                                leadingIcon = {
                                                                    Icon(
                                                                        imageVector = when (order) {
                                                                            AlbumSortOrder.TRACK_NUMBER -> Icons.Filled.FormatListNumbered
                                                                            AlbumSortOrder.TITLE_ASC, AlbumSortOrder.TITLE_DESC -> Icons.Filled.SortByAlpha
                                                                            AlbumSortOrder.DURATION_ASC, AlbumSortOrder.DURATION_DESC -> Icons.Filled.AccessTime
                                                                        },
                                                                        contentDescription = null,
                                                                        tint = if (isSelected)
                                                                            MaterialTheme.colorScheme.onPrimaryContainer
                                                                        else
                                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                },
                                                                trailingIcon = {
                                                                    when (order) {
                                                                        AlbumSortOrder.TITLE_ASC, AlbumSortOrder.DURATION_ASC -> {
                                                                            Icon(
                                                                                imageVector = Icons.Default.ArrowUpward,
                                                                                contentDescription = "Ascending",
                                                                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                                            )
                                                                        }

                                                                        AlbumSortOrder.TITLE_DESC, AlbumSortOrder.DURATION_DESC -> {
                                                                            Icon(
                                                                                imageVector = Icons.Default.ArrowDownward,
                                                                                contentDescription = "Descending",
                                                                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                                            )
                                                                        }

                                                                        else -> {}
                                                                    }
                                                                },
                                                                onClick = {
                                                                    HapticUtils.performHapticFeedback(
                                                                        context,
                                                                        haptics,
                                                                        HapticFeedbackType.LongPress
                                                                    )
                                                                    sortOrder = order
                                                                    showSortMenu = false
                                                                    appSettings.setAlbumSortOrder(
                                                                        order.name
                                                                    )
                                                                },
                                                                colors = androidx.compose.material3.MenuDefaults.itemColors(
                                                                    textColor = if (isSelected)
                                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                                    else
                                                                        MaterialTheme.colorScheme.onSurface
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // Close button on tablet
                                            IconButton(
                                                onClick = onDismiss,
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Close",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }

                                    // Songs list
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(horizontal = 12.dp)
                                            .graphicsLayer { translationY = contentOffset },
                                        shape = RoundedCornerShape(
                                            topStart = 28.dp,
                                            topEnd = 28.dp
                                        ),
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        tonalElevation = 1.dp
                                    ) {
                                        if (sortedSongs.isNotEmpty()) {
                                            LazyColumn(
                                                state = listState,
                                                modifier = Modifier.fillMaxSize(),
                                                contentPadding = PaddingValues(
                                                    top = 8.dp,
                                                    bottom = 24.dp
                                                ),
                                                userScrollEnabled = true
                                            ) {
                                                itemsIndexed(
                                                    items = sortedSongs,
                                                    key = { index, song -> "album_song_${song.id}_$index" }
                                                ) { index, song ->
                                                    val shouldAnimate = index < 10
                                                    var itemVisible by remember { mutableStateOf(!shouldAnimate) }

                                                    val itemAlpha by animateFloatAsState(
                                                        targetValue = if (itemVisible) 1f else 0f,
                                                        animationSpec = tween(
                                                            durationMillis = 300,
                                                            easing = FastOutSlowInEasing
                                                        ),
                                                        label = "itemAlpha_$index"
                                                    )

                                                    val itemTranslation by animateFloatAsState(
                                                        targetValue = if (itemVisible) 0f else 60f,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                                            stiffness = Spring.StiffnessMediumLow
                                                        ),
                                                        label = "itemTranslation_$index"
                                                    )

                                                    val itemScale by animateFloatAsState(
                                                        targetValue = if (itemVisible) 1f else 0.9f,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessMedium
                                                        ),
                                                        label = "itemScale_$index"
                                                    )

                                                    if (shouldAnimate) {
                                                        LaunchedEffect(Unit) {
                                                            delay(50L * index + 100L)
                                                            itemVisible = true
                                                        }
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .graphicsLayer {
                                                                alpha = itemAlpha
                                                                translationX = itemTranslation
                                                                scaleX = itemScale
                                                                scaleY = itemScale
                                                            }
                                                    ) {
                                                        ExpressiveSongItem(
                                                            song = song,
                                                            index = index + 1,
                                                            onClick = {
                                                                onSongClick(song)
                                                                onDismiss()
                                                                onPlayerClick()
                                                            },
                                                            onAddToQueue = { onAddToQueue(song) },
                                                            onAddToPlaylist = {
                                                                onAddSongToPlaylist(
                                                                    song
                                                                )
                                                            },
                                                            onPlayNext = { onPlayNext(song) },
                                                            onToggleFavorite = {
                                                                onToggleFavorite(
                                                                    song
                                                                )
                                                            },
                                                            isFavorite = favoriteSongs.contains(song.id),
                                                            onShowSongInfo = { onShowSongInfo(song) },
                                                            onAddToBlacklist = {
                                                                onAddToBlacklist(
                                                                    song
                                                                )
                                                            },
                                                            currentSong = currentSong,
                                                            isPlaying = isPlaying,
                                                            useHoursFormat = useHoursFormat,
                                                            haptics = haptics,
                                                            modifier = Modifier.animateItem()
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                        modifier = Modifier.size(80.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = Icons.Filled.MusicOff,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                                    alpha = 0.6f
                                                                ),
                                                                modifier = Modifier.size(40.dp)
                                                            )
                                                        }
                                                    }

                                                    Text(
                                                        text = context.getString(R.string.bottomsheet_no_songs_album),
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }
    } else {
        // Phone layout: Bottom sheet
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = Modifier
                .fillMaxHeight()
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceContainerLow,
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // ═══════════════════════════════════════════════════════════════════
                    // EXPRESSIVE HEADER SECTION
                    // ═══════════════════════════════════════════════════════════════════
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .graphicsLayer {
                                alpha = headerAlpha
                                scaleX = headerScale
                                scaleY = headerScale
                            }
                    ) {
                        // Background artwork with blur effect (conditional)
                        if (album.artworkUri != null && albumBottomSheetGradientBlur) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .apply(
                                        ImageUtils.buildImageRequest(
                                            album.artworkUri,
                                            album.title,
                                            context.cacheDir,
                                            M3PlaceholderType.ALBUM
                                        )
                                    )
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .blur(20.dp)
                                    .graphicsLayer { alpha = 0.5f }
                            )
                        }

                        // Animated gradient shimmer overlay (conditional)
                        if (albumBottomSheetGradientBlur) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .drawBehind {
                                        drawRect(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.White.copy(alpha = 0.05f),
                                                    Color.Transparent
                                                ),
                                                start = Offset(shimmerOffset - 500f, 0f),
                                                end = Offset(shimmerOffset, size.height)
                                            )
                                        )
                                    }
                            )
                        }

                        // Multi-layer gradient overlay for depth (conditional)
                        if (albumBottomSheetGradientBlur) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colorStops = arrayOf(
                                                0f to MaterialTheme.colorScheme.surfaceContainerLow.copy(
                                                    alpha = 0.3f
                                                ),
                                                0.4f to Color.Transparent,
                                                0.7f to MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                                1f to MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    )
                            )
                        }

                        // Radial highlight accent (conditional)
                        if (albumBottomSheetGradientBlur) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                Color.Transparent
                                            ),
                                            center = Offset(0.3f, 0.3f),
                                            radius = 800f
                                        )
                                    )
                            )
                        }

                        // Drag handle with animation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp),
                                shape = RoundedCornerShape(2.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            ) {}
                        }

                        // Close button
//                    Surface(
//                        onClick = {
//                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
//                            onDismiss()
//                        },
//                        modifier = Modifier
//                            .align(Alignment.TopEnd)
//                            .padding(WindowInsets.statusBars.asPaddingValues())
//                            .padding(16.dp)
//                            .size(44.dp),
//                        shape = CircleShape,
//                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f),
//                        shadowElevation = 0.dp
//                    ) {
//                        Box(contentAlignment = Alignment.Center) {
//                            Icon(
//                                imageVector = Icons.Filled.Close,
//                                contentDescription = "Close",
//                                tint = MaterialTheme.colorScheme.onSurface,
//                                modifier = Modifier.size(22.dp)
//                            )
//                        }
//                    }

                        // Main content layout
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(48.dp))

                            // Floating album artwork card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                // Album art with animated elevation
                                val artworkScale by animateFloatAsState(
                                    targetValue = if (isVisible) 1f else 0.8f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow,
                                        visibilityThreshold = 0.001f
                                    ),
                                    label = "artworkScale"
                                )

                                Surface(
                                    modifier = Modifier
                                        .size(140.dp)
                                        .graphicsLayer {
                                            scaleX = artworkScale
                                            scaleY = artworkScale
                                        },
                                    shape = RoundedCornerShape(20.dp),
                                    shadowElevation = 16.dp,
                                    tonalElevation = 8.dp
                                ) {
                                    Box {
                                        if (album.artworkUri != null) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .apply(
                                                        ImageUtils.buildImageRequest(
                                                            album.artworkUri,
                                                            album.title,
                                                            context.cacheDir,
                                                            M3PlaceholderType.ALBUM
                                                        )
                                                    )
                                                    .build(),
                                                contentDescription = "Album artwork for ${album.title}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            // Gradient fallback
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.linearGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.primaryContainer,
                                                                MaterialTheme.colorScheme.tertiaryContainer
                                                            )
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Album,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                        alpha = 0.7f
                                                    ),
                                                    modifier = Modifier.size(64.dp)
                                                )
                                            }
                                        }

                                        // Shine overlay effect
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            Color.White.copy(alpha = 0.15f),
                                                            Color.Transparent,
                                                            Color.Transparent
                                                        ),
                                                        start = Offset(0f, 0f),
                                                        end = Offset(300f, 300f)
                                                    )
                                                )
                                        )
                                    }
                                }

                                // Album info with staggered animations
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Album title with entrance animation
                                    AnimatedVisibility(
                                        visible = isVisible,
                                        enter = fadeIn(tween(400, delayMillis = 100)) +
                                                slideInHorizontally(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    ),
                                                    initialOffsetX = { 50 }
                                                )
                                    ) {
                                        Text(
                                            text = album.title,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 28.sp,
                                            textAlign = TextAlign.Start
                                        )
                                    }

                                    // Artist name
                                    AnimatedVisibility(
                                        visible = isVisible,
                                        enter = fadeIn(tween(400, delayMillis = 200)) +
                                                slideInHorizontally(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    ),
                                                    initialOffsetX = { 50 }
                                                )
                                    ) {
                                        Text(
                                            text = album.artist,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Metadata chips row
                                    AnimatedVisibility(
                                        visible = isVisible,
                                        enter = fadeIn(tween(400, delayMillis = 300)) +
                                                slideInHorizontally(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    ),
                                                    initialOffsetX = { 50 }
                                                )
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (album.year > 0) {
                                                MetadataChip(
                                                    text = "${album.year}",
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }

                                            MetadataChip(
                                                text = "${sortedSongs.size} songs",
                                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                    }

                                    // Audio quality badges
                                    if (album.songs.isNotEmpty()) {
                                        AnimatedVisibility(
                                            visible = isVisible,
                                            enter = fadeIn(tween(400, delayMillis = 400))
                                        ) {
                                            AudioQualityBadges(
                                                song = album.songs.first(),
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // ═══════════════════════════════════════════════════════════════════
                            // ACTION BUTTONS ROW - Grouped button design
                            // ═══════════════════════════════════════════════════════════════════
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(tween(500, delayMillis = 400)) +
                                        slideInVertically(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            ),
                                            initialOffsetY = { 40 }
                                        )
                            ) {
                                var shufflePressed by remember { mutableStateOf(false) }
                                var playAllPressed by remember { mutableStateOf(false) }
                                
                                val shuffleScale by animateFloatAsState(
                                    targetValue = if (shufflePressed) 0.96f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "shuffleScale"
                                )
                                
                                val playAllScale by animateFloatAsState(
                                    targetValue = if (playAllPressed) 0.96f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "playAllScale"
                                )
                                
                                LaunchedEffect(shufflePressed) {
                                    if (shufflePressed) {
                                        delay(150)
                                        shufflePressed = false
                                    }
                                }
                                
                                LaunchedEffect(playAllPressed) {
                                    if (playAllPressed) {
                                        delay(150)
                                        playAllPressed = false
                                    }
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    // Play All Button - Equal sizing with text
                                    Button(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(
                                                context,
                                                haptics,
                                                HapticFeedbackType.LongPress
                                            )
                                            playAllPressed = true
                                            if (sortedSongs.isNotEmpty()) {
                                                onPlayAll(sortedSongs)
                                            } else {
                                                onPlayAll(album.songs)
                                            }
                                            scope.launch {
                                                sheetState.hide()
                                            }.invokeOnCompletion {
                                                if (!sheetState.isVisible) {
                                                    onDismiss()
                                                    onPlayerClick()
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .graphicsLayer {
                                                scaleX = playAllScale
                                                scaleY = playAllScale
                                            },
                                        shape = ButtonGroupDefaults.connectedLeadingButtonShapes().shape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = RhythmIcons.Play,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = context.getString(R.string.bottomsheet_play_all),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    // Shuffle Button - Equal sizing with text
                                    FilledTonalButton(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(
                                                context,
                                                haptics,
                                                HapticFeedbackType.LongPress
                                            )
                                            shufflePressed = true
                                            if (sortedSongs.isNotEmpty()) {
                                                onShufflePlay(sortedSongs)
                                            } else {
                                                onShufflePlay(album.songs)
                                            }
                                            scope.launch {
                                                sheetState.hide()
                                            }.invokeOnCompletion {
                                                if (!sheetState.isVisible) {
                                                    onDismiss()
                                                    onPlayerClick()
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .graphicsLayer {
                                                scaleX = shuffleScale
                                                scaleY = shuffleScale
                                            },
                                        shape = ButtonGroupDefaults.connectedTrailingButtonShapes().shape,
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = RhythmIcons.Shuffle,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Shuffle",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ═══════════════════════════════════════════════════════════════════
                    // SONGS SECTION HEADER
                    // ═══════════════════════════════════════════════════════════════════
                    AnimatedVisibility(
                        visible = isVisible && sortedSongs.isNotEmpty(),
                        enter = fadeIn(tween(500, delayMillis = 500)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 30 }
                                )
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = context.getString(R.string.bottomsheet_tracks),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = formatDuration(
                                            totalDuration,
                                            useHoursFormat
                                        ) + " total",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Song count badge
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "${sortedSongs.size}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            )
                                        )
                                    }

                                    // Sort button
                                    Box {
                                        var sortButtonPressed by remember { mutableStateOf(false) }
                                        val sortButtonScale by animateFloatAsState(
                                            targetValue = if (sortButtonPressed) 0.85f else 1f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            ),
                                            label = "sortButtonScale"
                                        )

                                        FilledTonalIconButton(
                                            onClick = {
                                                HapticUtils.performHapticFeedback(
                                                    context,
                                                    haptics,
                                                    HapticFeedbackType.TextHandleMove
                                                )
                                                showSortMenu = true
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .graphicsLayer {
                                                    scaleX = sortButtonScale
                                                    scaleY = sortButtonScale
                                                }
                                                .pointerInput(Unit) {
                                                    detectTapGestures(
                                                        onPress = {
                                                            sortButtonPressed = true
                                                            tryAwaitRelease()
                                                            sortButtonPressed = false
                                                        }
                                                    )
                                                },
                                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        ) {
                                            Icon(
                                                imageVector = RhythmIcons.Actions.Sort,
                                                contentDescription = "Sort songs",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showSortMenu,
                                            onDismissRequest = { showSortMenu = false },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            AlbumSortOrder.entries.forEach { order ->
                                                val isSelected = sortOrder == order
                                                Surface(
                                                    color = if (isSelected)
                                                        MaterialTheme.colorScheme.primaryContainer.copy(
                                                            alpha = 0.8f
                                                        )
                                                    else
                                                        Color.Transparent,
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = when (order) {
                                                                    AlbumSortOrder.TRACK_NUMBER -> "Track Number"
                                                                    AlbumSortOrder.TITLE_ASC, AlbumSortOrder.TITLE_DESC -> "Title"
                                                                    AlbumSortOrder.DURATION_ASC, AlbumSortOrder.DURATION_DESC -> "Duration"
                                                                },
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                                color = if (isSelected)
                                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                                else
                                                                    MaterialTheme.colorScheme.onSurface
                                                            )
                                                        },
                                                        leadingIcon = {
                                                            Icon(
                                                                imageVector = when (order) {
                                                                    AlbumSortOrder.TRACK_NUMBER -> Icons.Filled.FormatListNumbered
                                                                    AlbumSortOrder.TITLE_ASC, AlbumSortOrder.TITLE_DESC -> Icons.Filled.SortByAlpha
                                                                    AlbumSortOrder.DURATION_ASC, AlbumSortOrder.DURATION_DESC -> Icons.Filled.AccessTime
                                                                },
                                                                contentDescription = null,
                                                                tint = if (isSelected)
                                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                                else
                                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        },
                                                        trailingIcon = {
                                                            when (order) {
                                                                AlbumSortOrder.TITLE_ASC, AlbumSortOrder.DURATION_ASC -> {
                                                                    Icon(
                                                                        imageVector = Icons.Default.ArrowUpward,
                                                                        contentDescription = "Ascending",
                                                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                }

                                                                AlbumSortOrder.TITLE_DESC, AlbumSortOrder.DURATION_DESC -> {
                                                                    Icon(
                                                                        imageVector = Icons.Default.ArrowDownward,
                                                                        contentDescription = "Descending",
                                                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                }

                                                                else -> {}
                                                            }
                                                        },
                                                        onClick = {
                                                            HapticUtils.performHapticFeedback(
                                                                context,
                                                                haptics,
                                                                HapticFeedbackType.LongPress
                                                            )
                                                            sortOrder = order
                                                            showSortMenu = false
                                                            appSettings.setAlbumSortOrder(order.name)
                                                        },
                                                        colors = androidx.compose.material3.MenuDefaults.itemColors(
                                                            textColor = if (isSelected)
                                                                MaterialTheme.colorScheme.onPrimaryContainer
                                                            else
                                                                MaterialTheme.colorScheme.onSurface
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ═══════════════════════════════════════════════════════════════════
                    // SONGS LIST
                    // ═══════════════════════════════════════════════════════════════════
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .graphicsLayer { translationY = contentOffset },
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        tonalElevation = 1.dp
                    ) {
                        if (sortedSongs.isNotEmpty()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                                userScrollEnabled = true
                            ) {
                                itemsIndexed(
                                    items = sortedSongs,
                                    key = { index, song -> "album_song_${song.id}_$index" }
                                ) { index, song ->
                                    // Staggered animation with smooth entrance for first 10 items
                                    val shouldAnimate = index < 10
                                    var itemVisible by remember { mutableStateOf(!shouldAnimate) }

                                    // Animation progress for stagger effect
                                    val itemAlpha by animateFloatAsState(
                                        targetValue = if (itemVisible) 1f else 0f,
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = FastOutSlowInEasing
                                        ),
                                        label = "itemAlpha_$index"
                                    )

                                    val itemTranslation by animateFloatAsState(
                                        targetValue = if (itemVisible) 0f else 60f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        label = "itemTranslation_$index"
                                    )

                                    val itemScale by animateFloatAsState(
                                        targetValue = if (itemVisible) 1f else 0.9f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "itemScale_$index"
                                    )

                                    if (shouldAnimate) {
                                        LaunchedEffect(Unit) {
                                            delay(50L * index + 100L) // Staggered delay with initial offset
                                            itemVisible = true
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer {
                                                alpha = itemAlpha
                                                translationX = itemTranslation
                                                scaleX = itemScale
                                                scaleY = itemScale
                                            }
                                    ) {
                                        ExpressiveSongItem(
                                            song = song,
                                            index = index + 1,
                                            onClick = {
                                                onSongClick(song)
                                                scope.launch {
                                                    sheetState.hide()
                                                }.invokeOnCompletion {
                                                    if (!sheetState.isVisible) {
                                                        onDismiss()
                                                        onPlayerClick()
                                                    }
                                                }
                                            },
                                            onAddToQueue = { onAddToQueue(song) },
                                            onAddToPlaylist = { onAddSongToPlaylist(song) },
                                            onPlayNext = { onPlayNext(song) },
                                            onToggleFavorite = { onToggleFavorite(song) },
                                            isFavorite = favoriteSongs.contains(song.id),
                                            onShowSongInfo = { onShowSongInfo(song) },
                                            onAddToBlacklist = { onAddToBlacklist(song) },
                                            currentSong = currentSong,
                                            isPlaying = isPlaying,
                                            useHoursFormat = useHoursFormat,
                                            haptics = haptics,
                                            modifier = Modifier.animateItem()
                                        )
                                    }
                                }
                            }
                        } else {
                            // Empty state
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Filled.MusicOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.6f
                                                ),
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = context.getString(R.string.bottomsheet_no_songs_album),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SUPPORTING COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun MetadataChip(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpressiveSongItem(
    song: Song,
    index: Int,
    onClick: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    modifier: Modifier = Modifier,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onPlayNext: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    isFavorite: Boolean = false,
    onShowSongInfo: () -> Unit = {},
    onAddToBlacklist: () -> Unit = {},
    currentSong: Song? = null,
    isPlaying: Boolean = false,
    useHoursFormat: Boolean = false
) {
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val isCurrentSong = currentSong?.id == song.id

    // Animated properties for current song highlight
    val containerColor by animateColorAsState(
        targetValue = when {
            isCurrentSong -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            isPressed -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> Color.Transparent
        },
        animationSpec = tween(250),
        label = "containerColor"
    )

    val titleColor by animateColorAsState(
        targetValue = if (isCurrentSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(250),
        label = "titleColor"
    )

    val itemScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "itemScale"
    )

    // No pulse animation - removed per user request

    Surface(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Track number
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (song.trackNumber > 0) "${song.trackNumber}" else "$index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrentSong)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Album artwork thumbnail
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(50.dp),
                tonalElevation = 2.dp,
                shadowElevation = if (isCurrentSong) 4.dp else 1.dp
            ) {
                Box {
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
                        modifier = Modifier.fillMaxSize()
                    )

                    // Playing overlay with EQ icon
                    if (isCurrentSong && isPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            PlayingEqIcon(
                                modifier = Modifier.size(width = 24.dp, height = 20.dp),
                                color = Color.White,
                                isPlaying = isPlaying,
                                bars = 3
                            )
                        }
                    }
                }
            }

            // Song info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCurrentSong) FontWeight.Bold else FontWeight.Medium,
                        color = titleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Favorite indicator
                    AnimatedVisibility(
                        visible = isFavorite,
                        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Duration
                Text(
                    text = formatDuration(song.duration, useHoursFormat),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // More options button
            Box {
                var moreButtonPressed by remember { mutableStateOf(false) }
                val moreButtonScale by animateFloatAsState(
                    targetValue = if (moreButtonPressed) 0.85f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "moreButtonScale"
                )

                FilledTonalIconButton(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        showDropdown = true
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .graphicsLayer {
                            scaleX = moreButtonScale
                            scaleY = moreButtonScale
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    moreButtonPressed = true
                                    tryAwaitRelease()
                                    moreButtonPressed = false
                                }
                            )
                        },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = RhythmIcons.More,
                        contentDescription = "More options",
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        showDropdown = false
                    },
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(5.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    // Play next
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Play next",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SkipNext,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showDropdown = false
                                onPlayNext()
                            }
                        )
                    }

                    // Add to queue
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Add to queue",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = RhythmIcons.Queue,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showDropdown = false
                                onAddToQueue()
                            }
                        )
                    }

                    // Toggle favorite
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isFavorite) "Remove from favorites" else "Add to favorites",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                                        contentDescription = null,
                                        
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showDropdown = false
                                onToggleFavorite()
                            }
                        )
                    }

                    // Add to playlist
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Add to playlist",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showDropdown = false
                                onAddToPlaylist()
                            }
                        )
                    }

                    // Song info
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Song info",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showDropdown = false
                                onShowSongInfo()
                            }
                        )
                    }

                    // Add to blacklist
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Add to blacklist",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Block,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showDropdown = false
                                onAddToBlacklist()
                            }
                        )
                    }
                }
            }
        }
    }
}

// Keep backward compatibility alias
@Composable
fun EnhancedAlbumSongItem(
    song: Song,
    onClick: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    modifier: Modifier = Modifier,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onPlayNext: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    isFavorite: Boolean = false,
    onShowSongInfo: () -> Unit = {},
    onAddToBlacklist: () -> Unit = {},
    currentSong: Song? = null,
    isPlaying: Boolean = false,
    useHoursFormat: Boolean = false
) {
    ExpressiveSongItem(
        song = song,
        index = 1,
        onClick = onClick,
        onAddToQueue = onAddToQueue,
        onAddToPlaylist = onAddToPlaylist,
        modifier = modifier,
        haptics = haptics,
        onPlayNext = onPlayNext,
        onToggleFavorite = onToggleFavorite,
        isFavorite = isFavorite,
        onShowSongInfo = onShowSongInfo,
        onAddToBlacklist = onAddToBlacklist,
        currentSong = currentSong,
        isPlaying = isPlaying,
        useHoursFormat = useHoursFormat
    )
}