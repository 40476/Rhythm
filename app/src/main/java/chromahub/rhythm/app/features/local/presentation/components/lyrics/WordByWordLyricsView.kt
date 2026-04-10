package chromahub.rhythm.app.features.local.presentation.components.lyrics

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chromahub.rhythm.app.R
import chromahub.rhythm.app.util.RhythmLyricsParser
import chromahub.rhythm.app.util.WordByWordLyricLine
import kotlin.math.abs

/**
 * Represents either a lyrics line or a gap indicator
 */
sealed class LyricsItem {
    data class LyricLine(val line: WordByWordLyricLine, val index: Int) : LyricsItem()
    data class Gap(val duration: Long, val startTime: Long) : LyricsItem()
}

private const val LARGE_SCROLL_CATCH_UP_DELTA = 8

private fun WordByWordLyricLine.effectiveLineEndtime(): Long {
    val maxWordEnd = words.maxOfOrNull { it.endtime } ?: lineEndtime
    return maxOf(lineEndtime, maxWordEnd, lineTimestamp)
}

private fun WordByWordLyricLine.timingRichnessScore(): Int {
    if (words.isEmpty()) return 0

    val distinctWordStarts = words.map { it.timestamp }.distinct().size
    val advancingStarts = words.zipWithNext().count { (first, second) ->
        second.timestamp > first.timestamp
    }
    val partWords = words.count { it.isPart }
    val positiveDurations = words.count { it.endtime > it.timestamp }

    return (distinctWordStarts * 32) + (advancingStarts * 24) + (partWords * 16) + (positiveDurations * 8)
}

private suspend fun LazyListState.animateToLyricItemWithCatchUp(
    targetIndex: Int,
    scrollOffset: Int,
    lastIndex: Int
) {
    val currentIndex = firstVisibleItemIndex
    val delta = abs(currentIndex - targetIndex)

    if (delta >= LARGE_SCROLL_CATCH_UP_DELTA) {
        val prePositionIndex = if (targetIndex > currentIndex) {
            (targetIndex - 1).coerceAtLeast(0)
        } else {
            (targetIndex + 1).coerceAtMost(lastIndex)
        }

        if (prePositionIndex != targetIndex) {
            scrollToItem(index = prePositionIndex, scrollOffset = scrollOffset)
        }
    }

    animateScrollToItem(index = targetIndex, scrollOffset = scrollOffset)
}

/**
 * Animation presets for word-by-word highlighting
 * TODO: Implement different animation styles for word transitions
 */
enum class WordAnimationPreset {
    DEFAULT,      // Standard fade and scale
    BOUNCE,       // Bouncy spring animation (TODO: implement)
    SLIDE,        // Slide-in from sides (TODO: implement)
    GLOW,         // Glowing highlight effect (TODO: implement)
    KARAOKE,      // Filling bar effect (TODO: implement)
    MINIMAL       // Subtle color change only (TODO: implement)
}

/**
 * Composable for displaying Rhythm word-by-word synchronized lyrics
 * TODO: Add animation preset system for different word highlighting styles
 */
@Composable
fun WordByWordLyricsView(
    wordByWordLyrics: String,
    currentPlaybackTime: Long,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onSeek: ((Long) -> Unit)? = null,
    syncOffset: Long = 0L, // TODO: Add UI controls for adjusting sync offset in real-time
    animationPreset: WordAnimationPreset = WordAnimationPreset.DEFAULT, // TODO: Implement animation presets
    lyricsSource: String? = null, // Source of lyrics
    textSizeMultiplier: Float = 1.0f, // Scale factor for lyrics text size
    textAlignment: TextAlign = TextAlign.Center, // Alignment of lyrics text
    showTranslation: Boolean = true,
    showRomanization: Boolean = true
) {
    val context = LocalContext.current
    // TODO: Apply syncOffset to all timestamp comparisons for manual sync adjustment
    val adjustedPlaybackTime = currentPlaybackTime + syncOffset
    
    val parsedLyrics = remember(wordByWordLyrics) {
        RhythmLyricsParser.parseWordByWordLyrics(wordByWordLyrics)
    }

    val visibleLyricsLines = remember(parsedLyrics) {
        parsedLyrics
    }

    // Create items list with gaps for instrumental sections
    val lyricsItems = remember(visibleLyricsLines) {
        val items = mutableListOf<LyricsItem>()
        visibleLyricsLines.forEachIndexed { index, line ->
            items.add(LyricsItem.LyricLine(line, index))
            
            // Check for gap to next line
            if (index < visibleLyricsLines.size - 1) {
                val nextLine = visibleLyricsLines[index + 1]
                val gapDuration = nextLine.lineTimestamp - line.effectiveLineEndtime()
                if (gapDuration > 3000) { // 3 seconds threshold
                    items.add(LyricsItem.Gap(gapDuration, line.effectiveLineEndtime()))
                }
            }
        }
        items
    }

    // Find current line index (among lyric lines only) - using adjustedPlaybackTime for sync offset
    val currentLineIndex by remember(adjustedPlaybackTime, visibleLyricsLines) {
        derivedStateOf {
            val lastIndexAtPlayback = visibleLyricsLines.indexOfLast { line ->
                adjustedPlaybackTime >= line.lineTimestamp
            }

            if (lastIndexAtPlayback < 0) {
                -1
            } else {
                val activeTimestamp = visibleLyricsLines[lastIndexAtPlayback].lineTimestamp
                var firstIndexAtTimestamp = lastIndexAtPlayback
                while (firstIndexAtTimestamp > 0 && visibleLyricsLines[firstIndexAtTimestamp - 1].lineTimestamp == activeTimestamp) {
                    firstIndexAtTimestamp--
                }

                var bestIndex = firstIndexAtTimestamp
                var bestScore = visibleLyricsLines[firstIndexAtTimestamp].timingRichnessScore()

                for (index in (firstIndexAtTimestamp + 1)..lastIndexAtPlayback) {
                    val candidate = visibleLyricsLines[index]
                    if (candidate.lineTimestamp != activeTimestamp) continue

                    val candidateScore = candidate.timingRichnessScore()
                    if (candidateScore > bestScore) {
                        bestScore = candidateScore
                        bestIndex = index
                    }
                }

                bestIndex
            }
        }
    }

    // Auto-scroll to current lyric line with elastic spring animation
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex >= 0 && visibleLyricsLines.isNotEmpty()) {
            // Find the corresponding item index in lyricsItems
            val targetItemIndex = lyricsItems.indexOfFirst { item ->
                item is LyricsItem.LyricLine && item.index == currentLineIndex
            }

            if (targetItemIndex >= 0) {
                val offset = listState.layoutInfo.viewportSize.height / 3
                listState.animateToLyricItemWithCatchUp(
                    targetIndex = targetItemIndex,
                    scrollOffset = -offset,
                    lastIndex = lyricsItems.lastIndex
                )
            }
        }
    }

    if (visibleLyricsLines.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = context.getString(R.string.word_by_word_unavailable),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = when (textAlignment) {
                TextAlign.Start -> Alignment.Start
                TextAlign.End -> Alignment.End
                else -> Alignment.CenterHorizontally
            },
            contentPadding = PaddingValues(vertical = 30.dp)
        ) {
            itemsIndexed(lyricsItems) { _, item ->
                when (item) {
                    is LyricsItem.LyricLine -> {
                        val line = item.line
                        val index = item.index
                        val isCurrentLine = currentLineIndex == index
                        val isUpcomingLine = index > currentLineIndex
                        val linesAhead = index - currentLineIndex
                        
                        // Animated scale for current line with elastic spring
                        val scale by animateFloatAsState(
                            targetValue = when {
                                isCurrentLine -> 1.08f
                                isUpcomingLine && linesAhead == 1 -> 1.02f
                                else -> 1f
                            },
                            animationSpec = spring<Float>(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessVeryLow
                            ),
                            label = "lineScale"
                        )

                        // Staggered opacity animation for upcoming lines with elastic effect
                        val opacity by animateFloatAsState(
                            targetValue = when {
                                isCurrentLine -> 1f
                                isUpcomingLine && linesAhead <= 4 -> 0.9f - (linesAhead * 0.1f)
                                else -> 0.3f
                            },
                            animationSpec = if (isUpcomingLine) {
                                spring<Float>(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessVeryLow,
                                    visibilityThreshold = 0.01f
                                )
                            } else {
                                spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            },
                            label = "lineOpacity"
                        )

                        // Staggered translation animation for upcoming lines with elastic bounce
                        val animatedTranslationY by animateFloatAsState(
                            targetValue = when {
                                isUpcomingLine && linesAhead <= 3 -> (linesAhead * 6f)
                                else -> 0f
                            },
                            animationSpec = spring<Float>(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessVeryLow
                            ),
                            label = "lineTranslation"
                        )

                        // Calculate distance-based alpha for better readability
                        val distanceFromCurrent = abs(index - currentLineIndex)
                        
                        // Build annotated string with word-level highlighting (using adjustedPlaybackTime)
                        val annotatedText = buildAnnotatedString {
                            val activeWordIndex = if (isCurrentLine) {
                                val exactActive = line.words.indexOfLast { word ->
                                    adjustedPlaybackTime >= word.timestamp && adjustedPlaybackTime <= word.endtime
                                }

                                if (exactActive >= 0) {
                                    exactActive
                                } else {
                                    // Fallback: keep the latest word active until next line starts.
                                    line.words.indexOfLast { word ->
                                        adjustedPlaybackTime >= word.timestamp
                                    }
                                }
                            } else {
                                -1
                            }

                            line.words.forEachIndexed { wordIndex, word ->
                                // TODO: Apply animation preset here based on animationPreset parameter
                                val isWordActive = isCurrentLine && wordIndex == activeWordIndex
                                
                                // Improved alpha values based on distance for better readability
                                val wordAlpha = when {
                                    isWordActive -> 1f
                                    isCurrentLine -> 0.95f // Active line words that haven't been sung yet
                                    distanceFromCurrent == 1 -> 0.75f // Next/previous line
                                    distanceFromCurrent == 2 -> 0.60f
                                    distanceFromCurrent == 3 -> 0.45f
                                    else -> 0.32f // Far away lines
                                }
                                
                                // Apply different colors based on voice tag
                                val baseColor = when (line.voiceTag) {
                                    "v2" -> MaterialTheme.colorScheme.secondary
                                    "v3" -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.primary // Default/v1
                                }
                                
                                val wordColor = if (isWordActive) {
                                    baseColor // Active word gets voice-specific color
                                } else if (isCurrentLine) {
                                    // Inactive words in current line - use voice color but slightly dimmed
                                    when (line.voiceTag) {
                                        "v2" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                        "v3" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = wordAlpha)
                                }
                                
                                withStyle(
                                    SpanStyle(
                                        color = wordColor,
                                        fontWeight = if (isWordActive) FontWeight.Bold else 
                                            if (isCurrentLine) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                ) {
                                    // Add space before word if it's not a syllable part
                                    if (wordIndex > 0 && !word.isPart) {
                                        append(" ")
                                    }
                                    append(word.text)
                                }
                            }
                        }
                        
                        val translationAlpha by animateFloatAsState(
                            targetValue = if (isCurrentLine) 0.95f else 0.72f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "translationAlpha"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSeek?.invoke(line.lineTimestamp)
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    alpha = opacity
                                    translationY = animatedTranslationY
                                },
                            horizontalAlignment = when (textAlignment) {
                                TextAlign.Start -> Alignment.Start
                                TextAlign.End -> Alignment.End
                                else -> Alignment.CenterHorizontally
                            }
                        ) {
                            Text(
                                text = annotatedText,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize * textSizeMultiplier,
                                    lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * 1.4f * textSizeMultiplier
                                ),
                                textAlign = textAlignment,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (showTranslation && !line.translation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = line.translation,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = if (isCurrentLine) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * (0.92f * textSizeMultiplier),
                                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.32f * textSizeMultiplier
                                    ),
                                    color = MaterialTheme.colorScheme.tertiary.copy(
                                        alpha = if (isCurrentLine) 0.86f else 0.62f
                                    ),
                                    textAlign = textAlignment,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(translationAlpha)
                                )
                            }

                            if (showRomanization && !line.romanization.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = line.romanization,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize * (0.9f * textSizeMultiplier),
                                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.3f * textSizeMultiplier,
                                        letterSpacing = 0.02.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = if (isCurrentLine) 0.68f else 0.5f
                                    ),
                                    textAlign = textAlignment,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    is LyricsItem.Gap -> {
                        // Visual indicator for instrumental gap
                        val isCurrentGap = adjustedPlaybackTime >= item.startTime &&
                            adjustedPlaybackTime < item.startTime + item.duration
                        
                        val gapHeight = (item.duration / 1000f).coerceIn(20f, 80f) // 20-80dp based on duration
                        
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gapHeight.dp)
                                .padding(horizontal = 32.dp)
                        )
                        
                        // Musical note icon or wave indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val iconScale by animateFloatAsState(
                                targetValue = if (isCurrentGap) 1.5f else 1f,
                                animationSpec = spring<Float>(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessVeryLow
                                ),
                                label = "iconScale"
                            )
                            
                            Text(
                                text = "♪",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = if (isCurrentGap) 0.8f else 0.3f
                                ),
                                modifier = Modifier.graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                }
                            )
                        }
                        
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gapHeight.dp)
                                .padding(horizontal = 32.dp)
                        )
                    }
                }
            }
            
            // Display lyrics source at the bottom
            if (!lyricsSource.isNullOrBlank()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Lyrics by $lyricsSource",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}
