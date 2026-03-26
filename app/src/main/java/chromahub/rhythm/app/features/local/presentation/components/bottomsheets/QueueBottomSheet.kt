package chromahub.rhythm.app.features.local.presentation.components.bottomsheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.VideoLibrary
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.shared.presentation.components.common.M3PlaceholderType
import chromahub.rhythm.app.shared.presentation.components.common.DragDropLazyColumn
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveClickableSurface
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveFilledTonalIconButton
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveShapeTarget
import chromahub.rhythm.app.shared.presentation.components.common.rememberExpressiveShapeFor
import chromahub.rhythm.app.util.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    currentSong: Song?,
    queue: List<Song>,
    currentQueueIndex: Int = 0,
    onSongClick: (Song) -> Unit,
    onSongClickAtIndex: (Int) -> Unit = { _ -> }, // New parameter for index-based clicking
    onDismiss: () -> Unit,
    onRemoveSong: (Song) -> Unit = {},
    onMoveQueueItem: (Int, Int) -> Unit = { _, _ -> },
    onAddSongsClick: () -> Unit = {},
    onClearQueue: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val appSettings = remember(context) { AppSettings.getInstance(context) }
    val hidePlayedQueueSongs by appSettings.hidePlayedQueueSongs.collectAsState()
    val showAlreadyPlayedSongsInQueue = !hidePlayedQueueSongs
    // Animation states
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
        targetValue = if (showContent) 0f else 30f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "contentTranslation"
    )

    LaunchedEffect(Unit) {
        delay(50) // Reduced delay for faster appearance
        showContent = true
    }

    // Create a mutable queue for reordering that updates when the queue changes
    val mutableQueue = remember(queue) { 
        if (queue.isNotEmpty()) queue.toMutableStateList() else mutableStateListOf()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
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
                .padding(bottom = 24.dp)
        ) {
            // Header with title and actions
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                QueueHeader(
                    queueSize = mutableQueue.size,
                    onAddSongsClick = onAddSongsClick,
                    onClearQueue = if (mutableQueue.isNotEmpty()) onClearQueue else null
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (mutableQueue.isEmpty()) {
                // Empty queue state
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    EmptyQueueContent(
                        onAddSongsClick = onAddSongsClick
                    )
                }
            } else {
                // Now Playing section - show current song separately
                currentSong?.let { song ->
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it }
                    ) {
                        NowPlayingCard(
                            song = song,
                            onClick = { onSongClick(song) }
                        )
                    }
                    
                    // Add spacing between Now Playing and the rest
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                val currentSongIndexInQueue = currentQueueIndex.coerceIn(0, mutableQueue.lastIndex)
                // Show full queue (except current song card) so finished songs remain visible.
                val visibleQueue = mutableQueue.mapIndexedNotNull { index, song ->
                    if (!showAlreadyPlayedSongsInQueue && index < currentSongIndexInQueue) return@mapIndexedNotNull null
                    if (index == currentSongIndexInQueue) null else index to song
                }

                if (visibleQueue.isNotEmpty()) {
                    // Queue header for visible queue songs
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.bottomsheet_up_next),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            Text(
                                text = "${visibleQueue.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Queue list with reordering using custom drag and drop
                    val lazyListState = rememberLazyListState()
                    
                    DragDropLazyColumn(
                        items = visibleQueue,
                        modifier = Modifier.fillMaxWidth(),
                        lazyListState = lazyListState,
                        onMove = { fromIndex, toIndex ->
                            val actualFromIndex = visibleQueue[fromIndex].first
                            val actualToIndex = visibleQueue[toIndex].first
                            onMoveQueueItem(actualFromIndex, actualToIndex)
                        },
                        itemKey = { queueItem -> "${queueItem.first}_${queueItem.second.id}" }
                    ) { queueItem, isDragging, _ ->
                        val actualQueuePosition = queueItem.first
                        val song = queueItem.second
                        val isPlayed = actualQueuePosition < currentSongIndexInQueue
                        
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AnimateIn {
                                QueueItem(
                                    song = song,
                                    index = actualQueuePosition,
                                    isPlayed = isPlayed,
                                    isDragging = isDragging,
                                    onSongClick = { 
                                        // Use index-based click to handle duplicate songs correctly
                                        onSongClickAtIndex(actualQueuePosition)
                                    },
                                    onRemove = { 
                                        try {
                                            val indexToRemove = mutableQueue.indexOf(song)
                                            if (indexToRemove >= 0 && indexToRemove < mutableQueue.size) {
                                                mutableQueue.removeAt(indexToRemove)
                                            }
                                            onRemoveSong(song)
                                        } catch (e: Exception) {
                                            // Handle error silently
                                        }
                                    }
                                )
                            }
                        }
                    }
                } else if (currentSong != null) {
                    // Show empty up next content when only current song is in queue
                    // Add more spacing before empty state
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    EmptyUpNextContent(
                        onAddSongsClick = onAddSongsClick
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueHeader(
    queueSize: Int,
    onAddSongsClick: () -> Unit,
    onClearQueue: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = context.getString(R.string.bottomsheet_queue),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (queueSize > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        text = if (queueSize == 1) "1 song" else "$queueSize songs",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Add songs button
            FilledTonalIconButton(
                onClick = onAddSongsClick,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.VideoLibrary,
                    contentDescription = "Add songs",
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Clear queue button (only show if queue is not empty)
            onClearQueue?.let { clearAction ->
                FilledTonalIconButton(
                    onClick = clearAction,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear queue",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NowPlayingCard(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Subtle pulsing animation for the Now Playing indicator
    val infiniteTransition = rememberInfiniteTransition(label = "nowPlayingPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 4.dp
        )
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
                tonalElevation = 4.dp
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .apply(ImageUtils.buildImageRequest(
                            song.artworkUri,
                            song.title,
                            LocalContext.current.cacheDir,
                            M3PlaceholderType.TRACK
                        ))
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Song info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = context.getString(R.string.now_playing),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${song.artist} • ${song.album}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Playing indicator with pulse animation
            Icon(
                imageVector = RhythmIcons.MusicNote,
                contentDescription = "Now playing",
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = pulseAlpha),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun QueueItem(
    song: Song,
    index: Int,
    isPlayed: Boolean,
    isDragging: Boolean,
    onSongClick: () -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current

    val dragCardScale by animateFloatAsState(
        targetValue = if (isDragging) 1.015f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dragCardScale"
    )

    val dragCardOffsetY by animateFloatAsState(
        targetValue = if (isDragging) -4f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dragCardOffsetY"
    )

    val cardColor by animateColorAsState(
        targetValue = when {
            isDragging -> MaterialTheme.colorScheme.secondaryContainer
            isPlayed -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.42f)
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(durationMillis = 180),
        label = "queueItemCardColor"
    )

    val titleColor by animateColorAsState(
        targetValue = when {
            isDragging -> MaterialTheme.colorScheme.onSecondaryContainer
            isPlayed -> MaterialTheme.colorScheme.onTertiaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 180),
        label = "queueItemTitleColor"
    )

    val subtitleColor by animateColorAsState(
        targetValue = when {
            isDragging -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
            isPlayed -> MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f)
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 180),
        label = "queueItemSubtitleColor"
    )
    
    val songArtShape = rememberExpressiveShapeFor(
        ExpressiveShapeTarget.SONG_ART,
        fallbackShape = RoundedCornerShape(8.dp)
    )

    ExpressiveClickableSurface(
        onClick = onSongClick,
        color = cardColor,
        tonalElevation = if (isDragging) 0.dp else 1.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = dragCardScale
                scaleY = dragCardScale
                translationY = dragCardOffsetY
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number indicator with updated style
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDragging) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Album art
            Surface(
                modifier = Modifier.size(48.dp),
                shape = songArtShape,
                tonalElevation = 2.dp
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
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Song info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Drag handle with improved visual feedback
            val handleScale by animateFloatAsState(
                targetValue = if (isDragging) 1.3f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "handleScale"
            )
            
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Drag to reorder",
                tint = if (isDragging)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else if (isPlayed)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        scaleX = handleScale
                        scaleY = handleScale
                    }
            )
                        
            Spacer(modifier = Modifier.width(8.dp))
            
            // Remove button with hover effect
            var isPressed by remember { mutableStateOf(false) }
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.9f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "buttonScale"
            )
            
            ExpressiveFilledTonalIconButton(
                onClick = {
                    isPressed = true
                    onRemove()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Remove from queue",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimateIn(
    delay: Int = 50,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    val alpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 350, delayMillis = 0),
        label = "alpha"
    )

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    val translationY by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 0f else 20f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "translationY"
    )

    Box(
        modifier = modifier.graphicsLayer(
            alpha = alpha,
            scaleX = scale,
            scaleY = scale,
            translationY = translationY
        )
    ) {
        content()
    }
}

@Composable
private fun EmptyQueueContent(
    onAddSongsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated empty state with better design
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = RhythmIcons.Queue,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = context.getString(R.string.queue_empty),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = context.getString(R.string.queue_add_songs),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Better styled button
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .clickable { onAddSongsClick() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = context.getString(R.string.browse_library),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyUpNextContent(
    onAddSongsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp), // Increased height for better spacing
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Enhanced empty state design
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(72.dp) // Increased size
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = RhythmIcons.Queue,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(36.dp) // Increased size
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Enhanced text styling
            Text(
                text = context.getString(R.string.queue_no_more),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = context.getString(R.string.queue_add_more),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Enhanced button styling
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.clickable { onAddSongsClick() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = context.getString(R.string.add_more_songs),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}
