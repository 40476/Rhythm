package chromahub.rhythm.app.features.local.presentation.components.bottomsheets

import android.content.Context
import android.media.AudioManager
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.data.model.PlaybackLocation
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.util.HapticUtils
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackBottomSheet(
    locations: List<PlaybackLocation>,
    currentLocation: PlaybackLocation?,
    volume: Float,
    isMuted: Boolean,
    musicViewModel: MusicViewModel,
    onLocationSelect: (PlaybackLocation) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onMaxVolume: () -> Unit,
    onRefreshDevices: () -> Unit,
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateToEqualizer: (() -> Unit)? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    
    // System volume state
    var systemVolume by remember { mutableFloatStateOf(0.5f) }
    var systemMaxVolume by remember { mutableStateOf(15) }
    
    // Collect settings
    val playbackSpeed by musicViewModel.playbackSpeed.collectAsState()
    val playbackPitch by musicViewModel.playbackPitch.collectAsState()
    val syncSpeedAndPitch by appSettings.syncSpeedAndPitch.collectAsState()
    val gaplessPlayback by appSettings.gaplessPlayback.collectAsState()
    val useSystemVolume by appSettings.useSystemVolume.collectAsState()
    val crossfadeEnabled by appSettings.crossfade.collectAsState()
    val crossfadeDuration by appSettings.crossfadeDuration.collectAsState()
    val audioNormalization by appSettings.audioNormalization.collectAsState()
    val replayGain by appSettings.replayGain.collectAsState()
    val equalizerEnabled by appSettings.equalizerEnabled.collectAsState()
    val bassBoostEnabled by appSettings.bassBoostEnabled.collectAsState()
    val bassBoostStrength by appSettings.bassBoostStrength.collectAsState()
    val virtualizerEnabled by appSettings.virtualizerEnabled.collectAsState()
    val virtualizerStrength by appSettings.virtualizerStrength.collectAsState()
    
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

    // Initialize system volume and monitor for changes
    LaunchedEffect(Unit) {
        delay(100) // Reduced delay for faster appearance
        showContent = true
        
        // Get system volume
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        systemVolume = currentVolume.toFloat() / maxVolume.toFloat()
        systemMaxVolume = maxVolume
    }
    
    // Monitor system volume changes using ContentObserver (no polling)
    DisposableEffect(Unit) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volumeObserver = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                val cv = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val mv = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val newVol = cv.toFloat() / mv.toFloat()
                if (newVol != systemVolume) {
                    systemVolume = newVol
                    systemMaxVolume = mv
                }
            }
        }
        context.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            volumeObserver
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(volumeObserver)
        }
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
            // Header - Fixed at top, doesn't scroll
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                PlaybackHeader(
                    haptics = haptics
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Active Device Card
                item {
                    AnimateIn {
                        ActiveDeviceCard(
                            location = currentLocation,
                            onSwitchDevice = {
                                // Use native Android output switcher
                                musicViewModel.showOutputSwitcherDialog()
                            },
                            onRefreshDevices = onRefreshDevices,
                            haptics = haptics
                        )
                    }
                }
                
                // Volume Control Section
                item {
                    AnimateIn {
                        VolumeControlCard(
                            volume = volume,
                            isMuted = isMuted,
                            systemVolume = systemVolume,
                            systemMaxVolume = systemMaxVolume,
                            appSettings = appSettings,
                            context = context,
                            onVolumeChange = onVolumeChange,
                            onToggleMute = onToggleMute,
                            onMaxVolume = onMaxVolume,
                            onSystemVolumeChange = { newVolume ->
                                systemVolume = newVolume
                            },
                            haptics = haptics
                        )
                    }
                }
                
                // Playback Quick Settings Section
                item {
                    AnimateIn {
                        PlaybackQuickSettingsCard(
                            gaplessPlayback = gaplessPlayback,
                            useSystemVolume = useSystemVolume,
                            crossfadeEnabled = crossfadeEnabled,
                            crossfadeDuration = crossfadeDuration,
                            onGaplessPlaybackChange = {
                                musicViewModel.setGaplessPlayback(it)
                            },
                            onUseSystemVolumeChange = { appSettings.setUseSystemVolume(it) },
                            onCrossfadeEnabledChange = { appSettings.setCrossfade(it) },
                            onCrossfadeDurationChange = { appSettings.setCrossfadeDuration(it) },
                            onNavigateToSettings = onNavigateToSettings,
                            haptics = haptics,
                            context = context
                        )
                    }
                }
                
                // Audio Effects Section
                item {
                    AnimateIn {
                        AudioEffectsCard(
                            audioNormalization = audioNormalization,
                            replayGain = replayGain,
                            equalizerEnabled = equalizerEnabled,
                            bassBoostEnabled = bassBoostEnabled,
                            bassBoostStrength = bassBoostStrength,
                            virtualizerEnabled = virtualizerEnabled,
                            virtualizerStrength = virtualizerStrength,
                            onAudioNormalizationChange = { appSettings.setAudioNormalization(it) },
                            onReplayGainChange = { appSettings.setReplayGain(it) },
                            onEqualizerEnabledChange = { musicViewModel.setEqualizerEnabled(it) },
                            onBassBoostEnabledChange = { enabled ->
                                appSettings.setBassBoostEnabled(enabled)
                                musicViewModel.setBassBoost(enabled, bassBoostStrength.toShort())
                            },
                            onBassBoostStrengthChange = { strength ->
                                appSettings.setBassBoostStrength(strength)
                                musicViewModel.setBassBoost(bassBoostEnabled, strength.toShort())
                            },
                            onVirtualizerEnabledChange = { enabled ->
                                appSettings.setVirtualizerEnabled(enabled)
                                musicViewModel.setVirtualizer(enabled, virtualizerStrength.toShort())
                            },
                            onVirtualizerStrengthChange = { strength ->
                                appSettings.setVirtualizerStrength(strength)
                                musicViewModel.setVirtualizer(virtualizerEnabled, strength.toShort())
                            },
                            onNavigateToEqualizer = onNavigateToEqualizer,
                            haptics = haptics,
                            context = context
                        )
                    }
                }
                
                // Playback Pitch Section
                item {
                    AnimateIn {
                        PlaybackPitchCard(
                            currentPitch = playbackPitch,
                            onPitchChange = { pitch ->
                                musicViewModel.setPlaybackPitch(pitch)
                                if (syncSpeedAndPitch) musicViewModel.setPlaybackSpeed(pitch)
                            },
                            syncEnabled = syncSpeedAndPitch,
                            onSyncChange = { appSettings.setSyncSpeedAndPitch(it) },
                            haptics = haptics,
                            context = context
                        )
                    }
                }
                
                // Playback Speed Section
                item {
                    AnimateIn {
                        PlaybackSpeedCard(
                            currentSpeed = playbackSpeed,
                            onSpeedChange = { speed ->
                                musicViewModel.setPlaybackSpeed(speed)
                                if (syncSpeedAndPitch) musicViewModel.setPlaybackPitch(speed)
                            },
                            syncEnabled = syncSpeedAndPitch,
                            onSyncChange = { appSettings.setSyncSpeedAndPitch(it) },
                            haptics = haptics,
                            context = context
                        )
                    }
                }
                

            }
        }
    }
}

@Composable
private fun PlaybackHeader(
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
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
                text = context.getString(R.string.bottomsheet_playback),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
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
                    text = context.getString(R.string.audio_settings),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ActiveDeviceCard(
    location: PlaybackLocation?,
    onSwitchDevice: () -> Unit,
    onRefreshDevices: () -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Subtle pulse animation for connected device
    val infiniteTransition = rememberInfiniteTransition(label = "devicePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    Card(
        onClick = onSwitchDevice,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
            // .graphicsLayer {
            //     if (location != null) {
            //         scaleX = pulseScale
            //         scaleY = pulseScale
            //     }
            // },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.active_device),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Refresh button with rotation animation
                var isRefreshing by remember { mutableStateOf(false) }
                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (isRefreshing) 360f else 0f,
                    animationSpec = androidx.compose.animation.core.tween(
                        durationMillis = 500,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    ),
                    finishedListener = {
                        isRefreshing = false
                    },
                    label = "rotation"
                )
                
                IconButton(
                    onClick = {
                        isRefreshing = true
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        onRefreshDevices()
                    },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = RhythmIcons.Refresh,
                        contentDescription = "Refresh devices",
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer {
                                rotationZ = rotation
                            }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Device info
            if (location != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Device icon background - smaller for compact screens
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getDeviceIcon(location),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Device details
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        val typeDescription = when {
                            location.id.startsWith("bt_") -> "Bluetooth device"
                            location.id == "wired_headset" -> "Wired headphones"
                            location.id == "speaker" -> "Phone speaker"
                            else -> "Audio device"
                        }
                        
                        Text(
                            text = "$typeDescription • Tap to switch",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Active indicator with switch icon
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.SyncAlt,
                                contentDescription = "Switch device",
                                
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // No device state
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = RhythmIcons.Speaker,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = context.getString(R.string.bottomsheet_no_device),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = context.getString(R.string.tap_select_output),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VolumeControlCard(
    volume: Float,
    isMuted: Boolean,
    systemVolume: Float,
    systemMaxVolume: Int,
    appSettings: AppSettings,
    context: Context,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onMaxVolume: () -> Unit,
    onSystemVolumeChange: (Float) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    modifier: Modifier = Modifier
) {
    val useSystemVolume by appSettings.useSystemVolume.collectAsState()
    
    // Remember previous volume before muting for system volume
    var previousSystemVolume by remember { mutableFloatStateOf(0.5f) }
    
    // Current volume values based on setting
    val currentVolume = if (useSystemVolume) systemVolume else volume
    val currentIsMuted = if (useSystemVolume) (systemVolume == 0f) else isMuted
    
    // Animated volume for smooth transitions
    val animatedVolume by animateFloatAsState(
        targetValue = if (currentIsMuted) 0f else currentVolume,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "animatedVolume"
    )
    
    // System volume control functions
    val setSystemVolume = { newVolume: Float ->
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val targetVolume = (newVolume * systemMaxVolume).toInt().coerceIn(0, systemMaxVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
        onSystemVolumeChange(newVolume)
    }
    
    val toggleSystemMute = {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (systemVolume > 0f) {
            // Mute - save current volume
            previousSystemVolume = systemVolume
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
            onSystemVolumeChange(0f)
        } else {
            // Unmute - restore previous volume
            val volumeToRestore = if (previousSystemVolume > 0f) previousSystemVolume else 0.5f
            val targetVolume = (volumeToRestore * systemMaxVolume).toInt().coerceIn(1, systemMaxVolume)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
            onSystemVolumeChange(volumeToRestore)
        }
    }
    
    val setSystemMaxVolume = {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, systemMaxVolume, 0)
        onSystemVolumeChange(1f)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Volume header with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated icon based on volume level
                val iconAlpha by animateFloatAsState(
                    targetValue = if (currentIsMuted) 0.5f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "iconAlpha"
                )
                
                Icon(
                    imageVector = if (currentIsMuted) RhythmIcons.VolumeOff else 
                                if (animatedVolume < 0.3f) RhythmIcons.VolumeMute else 
                                if (animatedVolume < 0.7f) RhythmIcons.VolumeDown else 
                                RhythmIcons.VolumeUp,
                    contentDescription = "Volume",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = iconAlpha),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (useSystemVolume) "System Volume" else "App Volume",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Text(
                    //     text = "Tap to switch between app and system volume",
                    //     style = MaterialTheme.typography.bodySmall,
                    //     color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    // )
                }
                
                // Volume percentage
                Text(
                    text = "${(animatedVolume * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Volume controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Volume down button
                IconButton(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        if (useSystemVolume) {
                            val newVolume = (systemVolume - 0.1f).coerceAtLeast(0f)
                            setSystemVolume(newVolume)
                        } else {
                            val newVolume = (volume - 0.1f).coerceAtLeast(0f)
                            onVolumeChange(newVolume)
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = RhythmIcons.Remove,
                        contentDescription = "Decrease volume",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Volume slider
                Slider(
                    value = currentVolume,
                    onValueChange = { newVolume ->
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                        if (useSystemVolume) {
                            setSystemVolume(newVolume)
                        } else {
                            onVolumeChange(newVolume)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                )
                
                // Volume up button
                IconButton(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        if (useSystemVolume) {
                            val newVolume = (systemVolume + 0.1f).coerceAtMost(1f)
                            setSystemVolume(newVolume)
                        } else {
                            val newVolume = (volume + 0.1f).coerceAtMost(1f)
                            onVolumeChange(newVolume)
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = RhythmIcons.Add,
                        contentDescription = "Increase volume",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mute and Max Volume buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mute button
                val muteButtonColor by animateColorAsState(
                    targetValue = if (currentIsMuted) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                    label = "muteButtonColor"
                )
                val muteContentColor by animateColorAsState(
                    targetValue = if (currentIsMuted) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    label = "muteContentColor"
                )
                
                Surface(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        if (useSystemVolume) {
                            toggleSystemMute()
                        } else {
                            onToggleMute()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = muteButtonColor,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentIsMuted) RhythmIcons.VolumeOff else RhythmIcons.VolumeMute,
                            contentDescription = if (currentIsMuted) "Unmute" else "Mute",
                            tint = muteContentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentIsMuted) "Unmute" else "Mute",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = muteContentColor
                        )
                    }
                }
                
                // Max volume button
                Surface(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        if (useSystemVolume) {
                            setSystemMaxVolume()
                        } else {
                            onMaxVolume()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = RhythmIcons.VolumeUp,
                            contentDescription = context.getString(R.string.bottomsheet_max),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = context.getString(R.string.bottomsheet_max),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaybackSpeedCard(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    syncEnabled: Boolean,
    onSyncChange: (Boolean) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    context: Context,
    modifier: Modifier = Modifier
) {
    var selectedSpeed by remember(currentSpeed) { mutableFloatStateOf(currentSpeed) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Speed,
                    contentDescription = "Playback Speed",
                    
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = context.getString(R.string.playback_speed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Current speed display
                Text(
                    text = "${String.format("%.2f", selectedSpeed)}x",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sync toggle — expressive design matching settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(34.dp),
                        color = if (syncEnabled) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerHighest
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SyncAlt,
                                contentDescription = null,
                                tint = if (syncEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                                       else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = context.getString(R.string.sync_with_pitch),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (syncEnabled) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                chromahub.rhythm.app.features.local.presentation.screens.settings.TunerAnimatedSwitch(
                    checked = syncEnabled,
                    onCheckedChange = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                        onSyncChange(it)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Slider with labels
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "0.25x",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "3.0x",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = selectedSpeed,
                    onValueChange = { newValue ->
                        selectedSpeed = newValue
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                    },
                    onValueChangeFinished = {
                        onSpeedChange(selectedSpeed)
                    },
                    valueRange = 0.25f..3.0f,
                    steps = 54,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quick preset buttons
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf(0.5f, 0.75f, 0.8f, 0.9f, 1.0f, 1.25f, 1.5f, 2.0f, 2.5f, 3.0f)) { presetSpeed ->
                    AssistChip(
                        onClick = {
                            selectedSpeed = presetSpeed
                            onSpeedChange(presetSpeed)
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        },
                        label = {
                            Text(
                                text = "${String.format("%.2f", presetSpeed)}x",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedSpeed == presetSpeed)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (selectedSpeed == presetSpeed)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = null
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaybackQuickSettingsCard(
    gaplessPlayback: Boolean,
    useSystemVolume: Boolean,
    crossfadeEnabled: Boolean,
    crossfadeDuration: Float,
    onGaplessPlaybackChange: (Boolean) -> Unit,
    onUseSystemVolumeChange: (Boolean) -> Unit,
    onCrossfadeEnabledChange: (Boolean) -> Unit,
    onCrossfadeDurationChange: (Float) -> Unit,
    onNavigateToSettings: (() -> Unit)? = null,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    context: Context,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = RhythmIcons.Settings,
                    contentDescription = "Quick Settings",
                    
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = context.getString(R.string.playback_settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Use System Volume
            AudioSettingRow(
                title = "Use System Volume",
                description = "Control system volume instead of app volume",
                enabled = useSystemVolume,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onUseSystemVolumeChange(it)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Gapless Playback
            AudioSettingRow(
                title = "Gapless Playback",
                description = "Seamless transitions between tracks",
                enabled = gaplessPlayback,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onGaplessPlaybackChange(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Crossfade
            AudioSettingRow(
                title = "Crossfade",
                description = "Fade between songs for smooth transitions",
                enabled = crossfadeEnabled,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onCrossfadeEnabledChange(it)
                }
            )

            // Crossfade duration slider (visible when crossfade enabled)
            AnimatedVisibility(visible = crossfadeEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Duration",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${String.format("%.1f", crossfadeDuration)}s",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = crossfadeDuration,
                        onValueChange = { onCrossfadeDurationChange(it) },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            // Info link to Queue & Playback settings — placed at the bottom of this card
            if (onNavigateToSettings != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToSettings.invoke() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = RhythmIcons.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "More adjustments in ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = context.getString(R.string.settings_queue_playback_title),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaybackPitchCard(
    currentPitch: Float,
    onPitchChange: (Float) -> Unit,
    syncEnabled: Boolean,
    onSyncChange: (Boolean) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    context: Context,
    modifier: Modifier = Modifier
) {
    var selectedPitch by remember(currentPitch) { mutableFloatStateOf(currentPitch) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = "Playback Pitch",
                    
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = context.getString(R.string.player_pitch_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Current pitch display
                Text(
                    text = "${String.format("%.2f", selectedPitch)}x",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sync toggle — expressive design matching settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(34.dp),
                        color = if (syncEnabled) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerHighest
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SyncAlt,
                                contentDescription = null,
                                tint = if (syncEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                                       else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = context.getString(R.string.sync_with_speed),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (syncEnabled) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                chromahub.rhythm.app.features.local.presentation.screens.settings.TunerAnimatedSwitch(
                    checked = syncEnabled,
                    onCheckedChange = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                        onSyncChange(it)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Slider with labels
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "0.25x",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "3.0x",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = selectedPitch,
                    onValueChange = { newValue ->
                        selectedPitch = newValue
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                    },
                    onValueChangeFinished = {
                        onPitchChange(selectedPitch)
                    },
                    valueRange = 0.25f..3.0f,
                    steps = 54,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quick preset buttons
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf(0.5f, 0.75f, 0.8f, 0.9f, 1.0f, 1.25f, 1.5f, 2.0f, 2.5f, 3.0f)) { presetPitch ->
                    AssistChip(
                        onClick = {
                            selectedPitch = presetPitch
                            onPitchChange(presetPitch)
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        },
                        label = {
                            Text(
                                text = "${String.format("%.2f", presetPitch)}x",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedPitch == presetPitch)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (selectedPitch == presetPitch)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = null
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioSettingRow(
    title: String,
    description: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        AnimatedAudioSwitch(
            checked = enabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun AnimatedAudioSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    chromahub.rhythm.app.features.local.presentation.screens.settings.TunerAnimatedSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
    )
}


@Composable
private fun getDeviceIcon(location: PlaybackLocation) = when {
    location.id.startsWith("bt_") -> RhythmIcons.BluetoothFilled
    location.id == "wired_headset" -> RhythmIcons.HeadphonesFilled
    location.id == "speaker" -> RhythmIcons.SpeakerFilled
    else -> RhythmIcons.Speaker
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
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 350, delayMillis = 0),
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
private fun AudioEffectsCard(
    audioNormalization: Boolean,
    replayGain: Boolean,
    equalizerEnabled: Boolean,
    bassBoostEnabled: Boolean,
    bassBoostStrength: Int,
    virtualizerEnabled: Boolean,
    virtualizerStrength: Int,
    onAudioNormalizationChange: (Boolean) -> Unit,
    onReplayGainChange: (Boolean) -> Unit,
    onEqualizerEnabledChange: (Boolean) -> Unit,
    onBassBoostEnabledChange: (Boolean) -> Unit,
    onBassBoostStrengthChange: (Int) -> Unit,
    onVirtualizerEnabledChange: (Boolean) -> Unit,
    onVirtualizerStrengthChange: (Int) -> Unit,
    onNavigateToEqualizer: (() -> Unit)? = null,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = RhythmIcons.Equalizer,
                    contentDescription = null,
                    
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = context.getString(R.string.audio_effects),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Normalization toggle
            //AudioSettingRow(
            //    title = context.getString(R.string.audio_normalization),
            //    description = context.getString(R.string.audio_normalization_desc),
            //    enabled = audioNormalization,
            //    onToggle = {
            //        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            //        onAudioNormalizationChange(it)
            //    }
            //)

            //Spacer(modifier = Modifier.height(16.dp))

            // Replay Gain toggle
            //AudioSettingRow(
            //    title = context.getString(R.string.replay_gain),
            //    description = context.getString(R.string.replay_gain_desc),
            //    enabled = replayGain,
            //    onToggle = {
            //        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            //        onReplayGainChange(it)
            //    }
            //)

            //Spacer(modifier = Modifier.height(16.dp))

            //HorizontalDivider(
            //    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            //    thickness = 0.5.dp
            //)

            Spacer(modifier = Modifier.height(16.dp))

            // Equalizer quick toggle
            AudioSettingRow(
                title = context.getString(R.string.equalizer),
                description = context.getString(R.string.settings_equalizer_desc),
                enabled = equalizerEnabled,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                    onEqualizerEnabledChange(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bass Boost toggle + strength slider
            AudioSettingRow(
                title = context.getString(R.string.bass_boost),
                description = context.getString(R.string.bass_boost_desc),
                enabled = bassBoostEnabled,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                    onBassBoostEnabledChange(it)
                }
            )
            AnimatedVisibility(visible = bassBoostEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = context.getString(R.string.strength),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${bassBoostStrength / 10}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = bassBoostStrength.toFloat(),
                        onValueChange = { onBassBoostStrengthChange(it.toInt()) },
                        valueRange = 0f..1000f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Virtualizer toggle + strength slider
            AudioSettingRow(
                title = context.getString(R.string.virtualizer),
                description = context.getString(R.string.virtualizer_desc),
                enabled = virtualizerEnabled,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                    onVirtualizerEnabledChange(it)
                }
            )
            AnimatedVisibility(visible = virtualizerEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = context.getString(R.string.strength),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${virtualizerStrength / 10}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = virtualizerStrength.toFloat(),
                        onValueChange = { onVirtualizerStrengthChange(it.toInt()) },
                        valueRange = 0f..1000f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            // Open EQ settings link
            if (onNavigateToEqualizer != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToEqualizer.invoke() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = RhythmIcons.Equalizer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = context.getString(R.string.open_equalizer_settings),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

