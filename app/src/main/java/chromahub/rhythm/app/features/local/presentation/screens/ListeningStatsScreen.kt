package chromahub.rhythm.app.features.local.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import chromahub.rhythm.app.shared.data.repository.PlaybackStatsRepository
import chromahub.rhythm.app.shared.data.repository.StatsTimeRange
import chromahub.rhythm.app.shared.presentation.components.common.CollapsibleHeaderScreen
import chromahub.rhythm.app.shared.presentation.components.common.TabAnimation
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.util.HapticUtils
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import chromahub.rhythm.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Rhythm Stats - Your Musical Journey
 * Redesigned with cosmic/comic theme, Library-style tabs, and time-based decorations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningStatsScreen(
    navController: NavController,
    viewModel: MusicViewModel = viewModel()
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    // Get data
    val songs by viewModel.songs.collectAsState()
    
    // UI State - Default to WEEK for better UX
    var selectedRange by remember { mutableStateOf(StatsTimeRange.WEEK) }
    var statsSummary by remember { mutableStateOf<PlaybackStatsRepository.PlaybackStatsSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val tabRowState = rememberLazyListState()
    
    // Screen entrance animation
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(50)
        showContent = true
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
    
    val contentScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.95f,
        animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic),
        label = "contentScale"
    )
    
    // Load stats when range changes
    LaunchedEffect(selectedRange, songs) {
        isLoading = true
        statsSummary = viewModel.loadPlaybackStats(selectedRange)
        isLoading = false
    }
    
    // Auto-scroll tab row when selection changes
    LaunchedEffect(selectedRange) {
        val index = StatsTimeRange.entries.indexOf(selectedRange)
        tabRowState.animateScrollToItem(index.coerceAtLeast(0))
    }
    
    CollapsibleHeaderScreen(
        title = context.getString(R.string.stats_title),
        showBackButton = true,
        onBackClick = {
            HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
            navController.popBackStack()
        }
    ) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffset
                    scaleX = contentScale
                    scaleY = contentScale
                }
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time Range Tabs - Library style
            RhythmTimeRangeTabs(
                selectedRange = selectedRange,
                onRangeSelected = { range ->
                    HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                    selectedRange = range
                },
                tabRowState = tabRowState
            )
            
            // Content with animated transitions
            AnimatedContent(
                targetState = Pair(isLoading, selectedRange),
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + slideInHorizontally { it / 4 }) togetherWith
                    (fadeOut(animationSpec = tween(200)) + slideOutHorizontally { -it / 4 })
                },
                label = "statsContentTransition",
                modifier = Modifier.padding(horizontal = 24.dp)
            ) { (loading, _) ->
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (statsSummary == null || statsSummary!!.totalPlayCount == 0) {
                    EmptyRhythmView()
                } else {
                    val stats = statsSummary!!
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cosmic Time Widget with time-of-day decoration
                        AnimatedCardEntrance(delay = 0) {
                            CosmicListeningTimeWidget(stats.totalDurationMs)
                        }
                        
                        // Quick Stats Row
                        AnimatedCardEntrance(delay = 100) {
                            QuickStatsRow(stats)
                        }
                        
                        // Musical Journey Section
                        AnimatedCardEntrance(delay = 200) {
                            MusicalJourneyCard(stats)
                        }
                        
                        // Rating Statistics
                        AnimatedCardEntrance(delay = 300) {
                            RatingStatsCard(viewModel)
                        }
                        
                        // Top Vibes (Songs)
                        if (stats.topSongs.isNotEmpty()) {
                            AnimatedCardEntrance(delay = 400) {
                                TopVibesCard(stats.topSongs)
                            }
                        }
                        
                        // Star Artists
                        if (stats.topArtists.isNotEmpty()) {
                            AnimatedCardEntrance(delay = 500) {
                                StarArtistsCard(stats.topArtists)
                            }
                        }
                        
                        // Top Albums
                        if (stats.topAlbums.isNotEmpty()) {
                            AnimatedCardEntrance(delay = 550) {
                                TopAlbumsCard(stats.topAlbums)
                            }
                        }
                        
                        // Genre Mix
                        if (stats.topGenres.isNotEmpty()) {
                            AnimatedCardEntrance(delay = 600) {
                                GenreMixCard(stats.topGenres)
                            }
                        }
                        
                        // Beat Timeline
                        if (stats.timeline.isNotEmpty()) {
                            AnimatedCardEntrance(delay = 700) {
                                BeatTimelineCard(stats.timeline)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Time Range Tabs - Library style with animations
 */
@Composable
private fun RhythmTimeRangeTabs(
    selectedRange: StatsTimeRange,
    onRangeSelected: (StatsTimeRange) -> Unit,
    tabRowState: androidx.compose.foundation.lazy.LazyListState
) {
    val context = LocalContext.current
    val tabNames = mapOf(
        StatsTimeRange.TODAY to context.getString(R.string.stats_today),
        StatsTimeRange.WEEK to context.getString(R.string.stats_this_week),
        StatsTimeRange.MONTH to context.getString(R.string.stats_this_month),
        StatsTimeRange.ALL_TIME to context.getString(R.string.stats_all_time)
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        LazyRow(
            state = tabRowState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(StatsTimeRange.entries) { index, range ->
                val isSelected = range == selectedRange
                
                TabAnimation(
                    index = index,
                    selectedIndex = StatsTimeRange.entries.indexOf(selectedRange),
                    title = tabNames[range] ?: range.displayName,
                    selectedColor = MaterialTheme.colorScheme.primary,
                    onSelectedColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedColor = MaterialTheme.colorScheme.surfaceContainer,
                    onUnselectedColor = MaterialTheme.colorScheme.onSurface,
                    onClick = { onRangeSelected(range) },
                    modifier = Modifier.padding(all = 2.dp),
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (range) {
                                    StatsTimeRange.TODAY -> Icons.Outlined.Today
                                    StatsTimeRange.WEEK -> Icons.Outlined.DateRange
                                    StatsTimeRange.MONTH -> Icons.Outlined.CalendarMonth
                                    StatsTimeRange.ALL_TIME -> Icons.Outlined.AllInclusive
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = tabNames[range] ?: range.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Get time of day for decoration
 */
private fun getTimeOfDay(): TimeOfDay {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> TimeOfDay.MORNING
        in 12..16 -> TimeOfDay.AFTERNOON
        in 17..20 -> TimeOfDay.EVENING
        else -> TimeOfDay.NIGHT
    }
}

private enum class TimeOfDay {
    MORNING, AFTERNOON, EVENING, NIGHT
}

/**
 * Cosmic Listening Time Widget with time-of-day themed decoration
 */
@Composable
private fun CosmicListeningTimeWidget(totalDurationMs: Long) {
    val context = LocalContext.current
    val timeOfDay = remember { getTimeOfDay() }
    
    // Time-based gradient colors
    val gradientColors = when (timeOfDay) {
        TimeOfDay.MORNING -> listOf(
            Color(0xFFFFD54F), // Warm yellow
            Color(0xFFFFB74D), // Orange
            Color(0xFFFF8A65)  // Coral
        )
        TimeOfDay.AFTERNOON -> listOf(
            Color(0xFF81D4FA), // Light blue
            Color(0xFF4FC3F7), // Sky blue
            Color(0xFF29B6F6)  // Bright blue
        )
        TimeOfDay.EVENING -> listOf(
            Color(0xFFFF8A65), // Coral
            Color(0xFFE57373), // Soft red
            Color(0xFFBA68C8)  // Purple
        )
        TimeOfDay.NIGHT -> listOf(
            Color(0xFF7C4DFF), // Deep purple
            Color(0xFF536DFE), // Indigo
            Color(0xFF448AFF)  // Blue
        )
    }
    
    val decorationIcon = when (timeOfDay) {
        TimeOfDay.MORNING -> Icons.Outlined.WbSunny
        TimeOfDay.AFTERNOON -> Icons.Outlined.LightMode
        TimeOfDay.EVENING -> Icons.Outlined.WbTwilight
        TimeOfDay.NIGHT -> Icons.Outlined.NightsStay
    }
    
    val decorationText = when (timeOfDay) {
        TimeOfDay.MORNING -> context.getString(R.string.stats_rise_rhythm)
        TimeOfDay.AFTERNOON -> context.getString(R.string.stats_midday_beats)
        TimeOfDay.EVENING -> context.getString(R.string.stats_evening_vibes)
        TimeOfDay.NIGHT -> context.getString(R.string.stats_night_grooves)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(gradientColors)
                )
                .padding(20.dp)
        ) {
            // Decorative elements
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top decoration row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = decorationIcon,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = decorationText,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Decorative stars for night, sun rays for day
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        when (timeOfDay) {
                            TimeOfDay.NIGHT -> {
                                repeat(3) {
                                    Text("✦", color = Color.White.copy(alpha = 0.7f))
                                }
                            }
                            TimeOfDay.MORNING -> {
                                Text("🌅", style = MaterialTheme.typography.titleMedium)
                            }
                            TimeOfDay.AFTERNOON -> {
                                Text("☀️", style = MaterialTheme.typography.titleMedium)
                            }
                            TimeOfDay.EVENING -> {
                                Text("🌆", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Main content
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon container
                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatDuration(totalDurationMs),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        // Comic dialogue based on listening time
                        val hours = totalDurationMs / (1000 * 60 * 60)
                        val motivationalMessage = when {
                            hours < 1 -> context.getString(R.string.stats_just_started)
                            hours < 5 -> context.getString(R.string.stats_nice_rhythm)
                            hours < 10 -> context.getString(R.string.stats_jamming)
                            hours < 24 -> context.getString(R.string.stats_enthusiast)
                            hours < 50 -> context.getString(R.string.stats_legendary)
                            hours < 100 -> context.getString(R.string.stats_maestro)
                            else -> context.getString(R.string.stats_sage)
                        }
                        
                        Text(
                            text = context.getString(R.string.stats_total_listening_time),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = motivationalMessage,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Bottom decorative wave pattern
                if (timeOfDay == TimeOfDay.NIGHT) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✧ ･ﾟ: *✧･ﾟ:* 🎵 *:･ﾟ✧*:･ﾟ✧",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Quick Stats Row
 */
@Composable
private fun QuickStatsRow(stats: PlaybackStatsRepository.PlaybackStatsSummary) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickStatChip(
                icon = Icons.Outlined.PlayCircle,
                value = "${stats.totalPlayCount}",
                label = context.getString(R.string.stats_total_plays),
                modifier = Modifier.weight(1f)
            )
            QuickStatChip(
                icon = Icons.Outlined.MusicNote,
                value = "${stats.uniqueSongs}",
                label = context.getString(R.string.stats_unique_tracks),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickStatChip(
                icon = Icons.Outlined.People,
                value = "${stats.uniqueArtists}",
                label = "Unique Artists",
                modifier = Modifier.weight(1f)
            )
            QuickStatChip(
                icon = Icons.Outlined.TrendingUp,
                value = formatDuration(stats.averageDailyDurationMs),
                label = "Avg / Day",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickStatChip(
    icon: ImageVector,
    value: String,
    label: String,
    emoji: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (emoji.isNotEmpty()) {
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.titleMedium
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Animated card entrance with staggered delay
 */
@Composable
private fun AnimatedCardEntrance(
    delay: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }
    
    val cardAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 0),
        label = "cardAlpha"
    )
    
    val cardOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 20f,
        animationSpec = tween(durationMillis = 400, delayMillis = 0),
        label = "cardOffset"
    )
    
    val cardScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.95f,
        animationSpec = tween(durationMillis = 500, delayMillis = 0, easing = EaseInOutCubic),
        label = "cardScale"
    )
    
    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = cardAlpha
                translationY = cardOffset
                scaleX = cardScale
                scaleY = cardScale
            }
    ) {
        content()
    }
}

/**
 * Empty state view
 */
@Composable
private fun EmptyRhythmView() {
    val comicMessages = listOf(
        "🎵 The stage is empty!" to "Time to drop some beats and fill this space!",
        "📊 Stats on vacation!" to "Play music to bring them back to life!",
        "🎸 Your rhythm is sleeping" to "Wake it up with some tunes!",
        "🎧 Stats: 404 Not Found" to "Error: No beats detected. Please jam out!",
        "🎹 Chart-topping silence!" to "Let's change that with your favorite tracks!"
    )
    val message = remember { comicMessages.random() }

    // Animation for the empty state
    val infiniteTransition = rememberInfiniteTransition(label = "emptyStatePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val iconRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconRotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Enhanced speech bubble with better styling
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 0.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = message.first,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = message.second,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Musical Journey Card - Listening habits
 */
@Composable
private fun MusicalJourneyCard(stats: PlaybackStatsRepository.PlaybackStatsSummary) {
    // Comic achievement badge
    val achievementBadge = when {
        stats.activeDays >= 30 -> "🏆 Monthly Maestro!"
        stats.activeDays >= 14 -> "🎖️ Two-Week Wonder!"
        stats.activeDays >= 7 -> "⭐ Weekly Warrior!"
        stats.activeDays >= 3 -> "🎵 Getting Groovy!"
        else -> "🌱 Fresh Start!"
    }
    
    RhythmSectionCard(
        title = "Your Musical Journey",
        icon = Icons.Outlined.Explore
    ) {
        // Achievement badge
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = achievementBadge,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            JourneyStatRow(Icons.Outlined.CalendarToday, "Active Days", "${stats.activeDays}")
            if (stats.longestStreakDays > 1) {
                JourneyStatRow(Icons.Outlined.LocalFireDepartment, "Longest Streak", "${stats.longestStreakDays} days")
            }
            JourneyStatRow(Icons.Outlined.Restore, "Sessions", "${stats.totalSessions}")
            JourneyStatRow(Icons.Outlined.Timer, "Avg Session", formatDuration(stats.averageSessionDurationMs))
            if (stats.peakDayOfWeek != null) {
                JourneyStatRow(Icons.Outlined.Whatshot, "Peak Day", stats.peakDayOfWeek)
            }
        }
    }
}

@Composable
private fun JourneyStatRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainerLow,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Top Vibes - Top Songs
 */
@Composable
private fun TopVibesCard(songs: List<PlaybackStatsRepository.SongPlaybackSummary>) {
    RhythmSectionCard(
        title = "Top Vibes",
        icon = Icons.Outlined.Favorite
    ) {
        // Comic subtitle
        Text(
            text = "Your most played anthems!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            songs.take(5).forEachIndexed { index, song ->
                RhythmRankItem(
                    rank = index + 1,
                    title = song.title,
                    subtitle = song.artist,
                    plays = song.playCount,
                    duration = song.totalDurationMs,
                    isTopItem = index == 0
                )
            }
        }
    }
}

/**
 * Star Artists
 */
@Composable
private fun StarArtistsCard(artists: List<PlaybackStatsRepository.ArtistPlaybackSummary>) {
    RhythmSectionCard(
        title = "Star Artists",
        icon = Icons.Outlined.Stars
    ) {
        // Comic subtitle
        Text(
            text = "Your hall of fame performers!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            artists.take(5).forEachIndexed { index, artist ->
                RhythmRankItem(
                    rank = index + 1,
                    title = artist.artist,
                    subtitle = "${artist.uniqueSongs} tracks",
                    plays = artist.playCount,
                    duration = artist.totalDurationMs,
                    isTopItem = index == 0
                )
            }
        }
    }
}

/**
 * Top Albums
 */
@Composable
private fun TopAlbumsCard(albums: List<PlaybackStatsRepository.AlbumPlaybackSummary>) {
    RhythmSectionCard(
        title = "Top Albums",
        icon = Icons.Outlined.Album
    ) {
        Text(
            text = "Your favorite album collections!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            albums.take(5).forEachIndexed { index, album ->
                RhythmRankItem(
                    rank = index + 1,
                    title = album.album,
                    subtitle = "${album.uniqueSongs} tracks",
                    plays = album.playCount,
                    duration = album.totalDurationMs,
                    isTopItem = index == 0
                )
            }
        }
    }
}

/**
 * Genre Mix
 */
@Composable
private fun GenreMixCard(genres: List<PlaybackStatsRepository.GenrePlaybackSummary>) {
    RhythmSectionCard(
        title = "Genre Mix",
        icon = Icons.Outlined.Category
    ) {
        // Comic subtitle
        Text(
            text = "Your musical taste palette!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            genres.take(5).forEach { genre ->
                GenreMixRow(
                    name = genre.genre,
                    percentage = genre.percentage
                )
            }
        }
    }
}

@Composable
private fun GenreMixRow(name: String, percentage: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        )
    }
}

/**
 * Beat Timeline
 */
@Composable
private fun BeatTimelineCard(timeline: List<PlaybackStatsRepository.TimelineEntry>) {
    RhythmSectionCard(
        title = "Beat Timeline",
        icon = Icons.Outlined.Timeline
    ) {
        val maxPlays = timeline.maxOf { it.playCount }.coerceAtLeast(1)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            timeline.take(7).forEach { entry ->
                BeatTimelineRow(
                    label = entry.label,
                    plays = entry.playCount,
                    percentage = entry.playCount.toFloat() / maxPlays
                )
            }
        }
    }
}

@Composable
private fun BeatTimelineRow(label: String, plays: Int, percentage: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage.coerceAtLeast(0.05f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
            )
        }
        
        Text(
            text = "$plays",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Rhythm Rank Item for top songs/artists
 */
@Composable
private fun RhythmRankItem(
    rank: Int,
    title: String,
    subtitle: String,
    plays: Int,
    duration: Long,
    isTopItem: Boolean
) {
    val bgColor = if (isTopItem) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank badge with medals/emojis
        val (rankDisplay, rankBgColor, rankTextColor) = when (rank) {
            1 -> Triple("🥇", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            2 -> Triple("🥈", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
            3 -> Triple("🥉", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
            else -> Triple("$rank", MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .background(rankBgColor, RoundedCornerShape(10.dp))
        ) {
            Text(
                text = rankDisplay,
                style = if (rank <= 3) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (rank <= 3) Color.White else rankTextColor
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isTopItem) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "$plays plays",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Rhythm Section Card - Base card for sections
 */
@Composable
private fun RhythmSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp),
                    shadowElevation = 0.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

private fun formatDuration(ms: Long): String {
    val seconds = ms / 1000
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "< 1m"
    }
}

/**
 * Rating Statistics Card
 */
@Composable
private fun RatingStatsCard(viewModel: MusicViewModel) {
    val context = LocalContext.current
    val appSettings = chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context)
    val ratingDistribution = appSettings.getRatingDistribution()
    val totalRated = ratingDistribution.values.sum()
    
    if (totalRated == 0) {
        // Don't show card if no rated songs
        return
    }
    
    RhythmSectionCard(
        title = "Song Ratings",
        icon = Icons.Outlined.Star
    ) {
        // Rating distribution bar chart
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total rated songs
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Rated Songs",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$totalRated",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Rating distribution
            Text(
                text = "Rating Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // 5 star ratings (from 5 down to 1)
            (5 downTo 1).forEach { rating ->
                val count = ratingDistribution[rating] ?: 0
                val percentage = if (totalRated > 0) (count.toFloat() / totalRated * 100).toInt() else 0
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            chromahub.rhythm.app.shared.presentation.components.RatingStarsDisplay(
                                rating = rating,
                                size = 14.dp
                            )
                            Text(
                                text = chromahub.rhythm.app.shared.presentation.components.getRatingLabel(rating),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "$count ($percentage%)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(percentage / 100f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (rating) {
                                        5 -> Color(0xFFFFD700) // Gold
                                        4 -> MaterialTheme.colorScheme.primary
                                        3 -> MaterialTheme.colorScheme.secondary
                                        2 -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.outline
                                    }
                                )
                        )
                    }
                }
            }
            
            // Quick actions
            if (totalRated > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quick Playlists",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Top Rated playlist (5 stars)
                    if ((ratingDistribution[5] ?: 0) > 0) {
                        AssistChip(
                            onClick = {
                                viewModel.playRatingPlaylist(5, shuffled = false)
                            },
                            label = {
                                Text(
                                    "Absolute Favorites",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                    
                    // 4+ stars playlist
                    if (totalRated > 0) {
                        AssistChip(
                            onClick = {
                                viewModel.playMinimumRatingPlaylist(4, shuffled = false)
                            },
                            label = {
                                Text(
                                    "Loved Songs",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}
