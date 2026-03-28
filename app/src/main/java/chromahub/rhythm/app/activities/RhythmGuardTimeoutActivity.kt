@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package chromahub.rhythm.app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.ui.theme.RhythmTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RhythmGuardTimeoutActivity : ComponentActivity() {

    private val appSettings by lazy { AppSettings.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reason = intent.getStringExtra(EXTRA_REASON).orEmpty()
        val timeoutUntilMs = intent.getLongExtra(EXTRA_TIMEOUT_UNTIL_MS, 0L)
        val timeoutStartedAtMs = intent.getLongExtra(EXTRA_TIMEOUT_STARTED_AT_MS, 0L)

        setContent {
            RhythmTheme {
                RhythmGuardTimeoutScreen(
                    reason = reason,
                    fallbackTimeoutUntilMs = timeoutUntilMs,
                    fallbackTimeoutStartedAtMs = timeoutStartedAtMs,
                    appSettings = appSettings,
                    onExitTimeout = {
                        appSettings.clearRhythmGuardListeningTimeout()
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_REASON = "extra_timeout_reason"
        private const val EXTRA_TIMEOUT_UNTIL_MS = "extra_timeout_until_ms"
        private const val EXTRA_TIMEOUT_STARTED_AT_MS = "extra_timeout_started_at_ms"

        fun start(
            context: Context,
            reason: String,
            timeoutUntilMs: Long,
            timeoutStartedAtMs: Long
        ) {
            val intent = Intent(context, RhythmGuardTimeoutActivity::class.java).apply {
                putExtra(EXTRA_REASON, reason)
                putExtra(EXTRA_TIMEOUT_UNTIL_MS, timeoutUntilMs)
                putExtra(EXTRA_TIMEOUT_STARTED_AT_MS, timeoutStartedAtMs)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }
    }
}

@Composable
private fun RhythmGuardTimeoutScreen(
    reason: String,
    fallbackTimeoutUntilMs: Long,
    fallbackTimeoutStartedAtMs: Long,
    appSettings: AppSettings,
    onExitTimeout: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val timeoutUntilMsState by appSettings.rhythmGuardTimeoutUntilMs.collectAsState()
    val timeoutReasonState by appSettings.rhythmGuardTimeoutReason.collectAsState()
    var fallbackConsumed by rememberSaveable { mutableStateOf(false) }
    val timeoutUntilMs = when {
        timeoutUntilMsState > 0L -> {
            fallbackConsumed = true
            timeoutUntilMsState
        }
        !fallbackConsumed && fallbackTimeoutUntilMs > System.currentTimeMillis() -> fallbackTimeoutUntilMs
        else -> 0L
    }
    val timeoutStartedAtMs = if (fallbackTimeoutStartedAtMs > 0L) {
        fallbackTimeoutStartedAtMs
    } else {
        (timeoutUntilMs - 15 * 60_000L).coerceAtLeast(0L)
    }

    var remainingSeconds by remember(timeoutUntilMs) {
        mutableLongStateOf(((timeoutUntilMs - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L))
    }

    val totalSeconds = ((timeoutUntilMs - timeoutStartedAtMs).coerceAtLeast(1_000L) / 1000f)
    val elapsedSeconds = (totalSeconds - remainingSeconds.toFloat()).coerceAtLeast(0f)
    val progress = (elapsedSeconds / totalSeconds).coerceIn(0f, 1f)

    LaunchedEffect(timeoutUntilMs) {
        if (timeoutUntilMs <= 0L) {
            onExitTimeout()
            return@LaunchedEffect
        }

        while (true) {
            val left = ((timeoutUntilMs - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
            remainingSeconds = left
            if (left <= 0L) break
            delay(1000L)
        }

        onExitTimeout()
    }

    BackHandler {
        onExitTimeout()
    }

    // Responsive sizing
    val isTablet = false // For timeout activity, assume phone layout
    val contentMaxWidth = 600.dp
    val cardPadding = if (isTablet) 32.dp else 28.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .let { if (isTablet) it.width(contentMaxWidth) else it },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Timeout card container
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = if (isTablet) 4.dp else 2.dp,
            modifier = Modifier
                .fillMaxSize()
                .let { if (isTablet) it.width(contentMaxWidth) else it }
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(
                    start = cardPadding,
                    end = cardPadding,
                    top = cardPadding * 2,
                    bottom = cardPadding
                ),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                // Timeout icon at top left
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 32.dp)
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Rhythm Guard",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Left-aligned texts
                Text(
                    text = context.getString(R.string.settings_rhythm_guard_timeout_activity_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val displayReason = if (timeoutReasonState.isNotBlank()) timeoutReasonState else reason
                Text(
                    text = if (displayReason.isNotBlank()) {
                        displayReason
                    } else {
                        context.getString(R.string.settings_rhythm_guard_timeout_activity_default_reason)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Circular progress with countdown
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn(animationSpec = tween(1000))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.size(176.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularWavyProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = formatCountdown(remainingSeconds),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // App logo and name at center
                val infiniteTransition = rememberInfiniteTransition(label = "timeout_animations")
                val logoGlow by infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = EaseInOut),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                    ),
                    label = "logoGlow"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp)
                ) {
                    // App logo with glowing effect
                    AnimatedVisibility(
                        visible = true,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(animationSpec = tween(1000))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rhythm_splash_logo),
                                contentDescription = "Rhythm Logo",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(3.dp))

                    // App name
                    AnimatedVisibility(
                        visible = true,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
                    ) {
                        Text(
                            text = "Rhythm",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Action buttons at bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Exit timeout button
                    val exitButtonScale = remember { Animatable(1f) }
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                exitButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                exitButtonScale.animateTo(1f, animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                ))
                            }
                            onExitTimeout()
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = exitButtonScale.value
                                scaleY = exitButtonScale.value
                            },
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            context.getString(R.string.settings_rhythm_guard_timeout_activity_exit),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Close app button
                    val closeButtonScale = remember { Animatable(1f) }
                    Button(
                        onClick = {
                            scope.launch {
                                closeButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                closeButtonScale.animateTo(1f, animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                ))
                            }
                            val activity = context as? ComponentActivity
                            activity?.finishAffinity()
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = closeButtonScale.value
                                scaleY = closeButtonScale.value
                            },
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Text(
                            context.getString(R.string.settings_rhythm_guard_timeout_activity_close_app),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatCountdown(seconds: Long): String {
    val safeSeconds = seconds.coerceAtLeast(0L)
    val hours = safeSeconds / 3600L
    val minutes = (safeSeconds % 3600L) / 60L
    val secs = safeSeconds % 60L

    return if (hours > 0L) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}
