@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package chromahub.rhythm.app.features.local.presentation.screens.settings

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import chromahub.rhythm.app.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Slider
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import chromahub.rhythm.app.BuildConfig
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.data.model.Playlist
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.shared.data.repository.PlaybackStatsRepository
import chromahub.rhythm.app.shared.data.repository.StatsTimeRange
import chromahub.rhythm.app.util.GsonUtils
import chromahub.rhythm.app.util.HapticUtils
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlin.system.exitProcess
import chromahub.rhythm.app.shared.presentation.components.common.CollapsibleHeaderScreen
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveScrollBar
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveButtonGroup
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.StandardBottomSheetHeader
import chromahub.rhythm.app.shared.presentation.components.common.StyledProgressBar
import chromahub.rhythm.app.shared.presentation.components.common.ProgressStyle
import chromahub.rhythm.app.shared.presentation.components.common.ThumbStyle
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.LicensesBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.UpdateBottomSheet
import chromahub.rhythm.app.ui.utils.LazyListStateSaver
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import chromahub.rhythm.app.shared.presentation.viewmodel.AppUpdaterViewModel
import chromahub.rhythm.app.ui.theme.getFontPreviewStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File
import chromahub.rhythm.app.utils.FontLoader
import chromahub.rhythm.app.ui.theme.parseCustomColorScheme
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.text.HtmlCompat
import chromahub.rhythm.app.shared.presentation.components.common.M3FourColorCircularLoader
import chromahub.rhythm.app.features.local.presentation.components.player.PlayingEqIcon
import chromahub.rhythm.app.features.local.presentation.components.dialogs.CreatePlaylistDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.BulkPlaylistExportDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.PlaylistImportDialog
import chromahub.rhythm.app.shared.presentation.components.common.rememberExpressiveShape
import chromahub.rhythm.app.features.local.presentation.components.dialogs.PlaylistOperationProgressDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.PlaylistOperationResultDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.AppRestartDialog
import chromahub.rhythm.app.features.local.presentation.components.player.PlayerChipOrderBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.settings.HomeSectionOrderBottomSheet

@Composable
fun TunerSettingRow(item: SettingItem) {
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val hapticFeedbackEnabled by appSettings.hapticFeedbackEnabled.collectAsState()
    
    // Animation states
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tuner_setting_scale"
    )
    
    val iconBackgroundColor by animateColorAsState(
        targetValue = when {
            !item.enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            item.toggleState == true -> MaterialTheme.colorScheme.primaryContainer
            isPressed -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tuner_icon_bg_color"
    )
    
    val iconTintColor by animateColorAsState(
        targetValue = when {
            !item.enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
            item.toggleState == true -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tuner_icon_tint_color"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = if (item.enabled) 1f else 0.6f
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (item.enabled && item.onClick != null && item.toggleState == null) {
                    Modifier.clickable(onClick = {
                        isPressed = true
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                        item.onClick()
                    })
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container with expressive design
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(34.dp),
            color = iconBackgroundColor,
            tonalElevation = if (item.toggleState == true) 2.dp else 0.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(24.dp),
                    tint = iconTintColor
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (item.enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            item.description?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.enabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    },
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }

        if (item.toggleState != null && item.onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = context.getString(R.string.cd_navigate),
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            TunerAnimatedSwitch(
                checked = item.toggleState,
                onCheckedChange = {
                    if (!item.enabled) return@TunerAnimatedSwitch
                    HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                    item.onToggleChange?.invoke(it)
                },
                enabled = item.enabled
            )
        } else if (item.toggleState != null) {
            TunerAnimatedSwitch(
                checked = item.toggleState,
                onCheckedChange = {
                    if (!item.enabled) return@TunerAnimatedSwitch
                    HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                    item.onToggleChange?.invoke(it)
                },
                enabled = item.enabled
            )
        } else if (item.onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = context.getString(R.string.cd_navigate),
                modifier = Modifier.size(20.dp),
                tint = if (item.enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                }
            )
        }
    }
}

@Composable
fun TunerAnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val thumbColor by animateColorAsState(
        targetValue = when {
            checked && !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            checked -> MaterialTheme.colorScheme.onPrimary
            else -> Color.White
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tuner_thumb_color"
    )
    
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled && checked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
            !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
            checked -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.82f)
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tuner_track_color"
    )

    val iconTint by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = if (enabled) 1f else 0.55f)
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tuner_switch_icon_tint"
    )
    
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = thumbColor,
            checkedTrackColor = trackColor,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = thumbColor,
            uncheckedTrackColor = trackColor,
            uncheckedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledCheckedThumbColor = thumbColor,
            disabledCheckedTrackColor = trackColor,
            disabledUncheckedThumbColor = thumbColor,
            disabledUncheckedTrackColor = trackColor,
        ),
        thumbContent = {
            AnimatedContent(
                targetState = checked,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(140)) + scaleIn(initialScale = 0.85f)) togetherWith
                        (fadeOut(animationSpec = tween(120)) + scaleOut(targetScale = 0.85f))
                },
                label = "tuner_switch_icon"
            ) { isChecked ->
                Icon(
                    imageVector = if (isChecked) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = iconTint
                )
            }
        }
    )
}

@Composable
private fun TunerSettingCard(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && checked == null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (checked != null && onCheckedChange != null) {
                TunerAnimatedSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            } else if (onClick != null) {
                val context = LocalContext.current
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = context.getString(R.string.cd_navigate),
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Individual screens with actual settings
@Composable
fun NotificationsSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val useCustomNotification by appSettings.useCustomNotification.collectAsState()

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_notifications),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        val settingGroups = listOf(
            SettingGroup(
                title = context.getString(R.string.settings_notification_style),
                items = listOf(
                    SettingItem(
                        Icons.Default.Notifications,
                        context.getString(R.string.settings_custom_notifications),
                        context.getString(R.string.settings_custom_notifications_desc),
                        toggleState = useCustomNotification,
                        onToggleChange = { appSettings.setUseCustomNotification(it) }
                    )
                )
            )
        )

        val lazyListState = rememberSaveable(
            key = "notifications_settings_scroll_state",
            saver = LazyListStateSaver
        ) {
            androidx.compose.foundation.lazy.LazyListState()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            items(
                items = settingGroups,
                key = { "setting_${it.title}" },
                contentType = { "settingGroup" }
            ) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        group.items.forEachIndexed { index, item ->
                            TunerSettingRow(item = item)
                            if (index < group.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Queue & Playback Settings Screen
@Composable
fun QueuePlaybackSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)

    val shuffleUsesExoplayer by appSettings.shuffleUsesExoplayer.collectAsState()
    val autoAddToQueue by appSettings.autoAddToQueue.collectAsState()
    val clearQueueOnNewSong by appSettings.clearQueueOnNewSong.collectAsState()
    val hidePlayedQueueSongs by appSettings.hidePlayedQueueSongs.collectAsState()
    val showAlreadyPlayedSongsInQueue = !hidePlayedQueueSongs
    val showQueueDialog by appSettings.showQueueDialog.collectAsState()
    val repeatModePersistence by appSettings.repeatModePersistence.collectAsState()
    val shuffleModePersistence by appSettings.shuffleModePersistence.collectAsState()
    val queuePersistenceEnabled by appSettings.queuePersistenceEnabled.collectAsState()
    val playlistClickBehavior by appSettings.playlistClickBehavior.collectAsState(initial = "ask")
    val useHoursInTimeFormat by appSettings.useHoursInTimeFormat.collectAsState()
    val gaplessEnabled by appSettings.gaplessPlayback.collectAsState()
    val crossfadeEnabled by appSettings.crossfade.collectAsState()
    val crossfadeDuration by appSettings.crossfadeDuration.collectAsState()
    val crossfadeRepeatOne by appSettings.crossfadeRepeatOne.collectAsState()
    val stopPlaybackOnAppClose by appSettings.stopPlaybackOnAppClose.collectAsState()
    val audioRoutingMode by appSettings.audioRoutingMode.collectAsState()
    val bitPerfectMode by appSettings.bitPerfectMode.collectAsState()

    var showPlaylistBehaviorDialog by remember { mutableStateOf(false) }
    var showQueueDialogSettingDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }
    var restartDialogMessage by remember { mutableStateOf("") }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_queue_playback),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        val settingGroups = listOf(
            SettingGroup(
                title = context.getString(R.string.settings_queue_behavior),
                items = listOf(
                    SettingItem(
                        RhythmIcons.Shuffle,
                        context.getString(R.string.settings_use_exoplayer_shuffle),
                        context.getString(R.string.settings_use_exoplayer_shuffle_desc),
                        toggleState = shuffleUsesExoplayer,
                        onToggleChange = { appSettings.setShuffleUsesExoplayer(it) }
                    ),
                    SettingItem(
                        RhythmIcons.Queue,
                        context.getString(R.string.settings_auto_queue),
                        context.getString(R.string.settings_auto_queue_desc),
                        toggleState = autoAddToQueue,
                        onToggleChange = { appSettings.setAutoAddToQueue(it) }
                    ),
                    SettingItem(
                        RhythmIcons.Delete,
                        context.getString(R.string.settings_clear_queue_on_new_song),
                        context.getString(R.string.settings_clear_queue_on_new_song_desc),
                        toggleState = clearQueueOnNewSong,
                        onToggleChange = { appSettings.setClearQueueOnNewSong(it) }
                    ),
                    SettingItem(
                        RhythmIcons.Queue,
                        context.getString(R.string.settings_show_played_queue_songs),
                        context.getString(R.string.settings_show_played_queue_songs_desc),
                        toggleState = showAlreadyPlayedSongsInQueue,
                        onToggleChange = { appSettings.setHidePlayedQueueSongs(!it) }
                    ),
                    SettingItem(
                        RhythmIcons.Queue,
                        context.getString(R.string.settings_queue_action_dialog),
                        when {
                            clearQueueOnNewSong -> context.getString(R.string.settings_queue_action_dialog_desc_disabled)
                            showQueueDialog -> context.getString(R.string.settings_queue_action_dialog_desc_ask)
                            else -> context.getString(R.string.settings_queue_action_dialog_desc_always)
                        },
                        onClick = { showQueueDialogSettingDialog = true },
                        enabled = !clearQueueOnNewSong
                    ),
                    SettingItem(
                        androidx.compose.material.icons.Icons.AutoMirrored.Filled.QueueMusic,
                        context.getString(R.string.settings_playlist_action_dialog),
                        when (playlistClickBehavior) {
                            "play_all" -> context.getString(R.string.settings_playlist_action_play_all)
                            "play_one" -> context.getString(R.string.settings_playlist_action_play_one)
                            else -> context.getString(R.string.settings_playlist_action_ask)
                        },
                        onClick = { showPlaylistBehaviorDialog = true }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_playback_persistence),
                items = listOf(
                    SettingItem(
                        RhythmIcons.Repeat,
                        context.getString(R.string.settings_remember_repeat_mode),
                        context.getString(R.string.settings_remember_repeat_mode_desc),
                        toggleState = repeatModePersistence,
                        onToggleChange = { appSettings.setRepeatModePersistence(it) }
                    ),
                    SettingItem(
                        RhythmIcons.Shuffle,
                        context.getString(R.string.settings_remember_shuffle_mode),
                        context.getString(R.string.settings_remember_shuffle_mode_desc),
                        toggleState = shuffleModePersistence,
                        onToggleChange = { appSettings.setShuffleModePersistence(it) }
                    ),
                    SettingItem(
                        RhythmIcons.Queue,
                        context.getString(R.string.settings_remember_queue),
                        context.getString(R.string.settings_remember_queue_desc),
                        toggleState = queuePersistenceEnabled,
                        onToggleChange = { appSettings.setQueuePersistenceEnabled(it) }
                    ),
                    SettingItem(
                        androidx.compose.material.icons.Icons.Default.Stop,
                        context.getString(R.string.settings_stop_playback_on_close),
                        context.getString(R.string.settings_stop_playback_on_close_desc),
                        toggleState = stopPlaybackOnAppClose,
                        onToggleChange = { appSettings.setStopPlaybackOnAppClose(it) }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_audio_effects),
                items = listOf(
                    SettingItem(
                        Icons.Default.GraphicEq,
                        context.getString(R.string.settings_gapless_playback),
                        context.getString(R.string.settings_gapless_playback_desc),
                        toggleState = gaplessEnabled,
                        onToggleChange = { appSettings.setGaplessPlayback(it) }
                    ),
                    SettingItem(
                        RhythmIcons.Tune,
                        context.getString(R.string.settings_crossfade),
                        context.getString(R.string.settings_crossfade_desc),
                        toggleState = crossfadeEnabled,
                        onToggleChange = { appSettings.setCrossfade(it) },
                        // Pass the crossfade duration as extra data for rendering
                        data = if (crossfadeEnabled) crossfadeDuration else null
                    ),
                    SettingItem(
                        RhythmIcons.Repeat,
                        context.getString(R.string.settings_crossfade_repeat_one),
                        context.getString(R.string.settings_crossfade_repeat_one_desc),
                        toggleState = crossfadeRepeatOne,
                        onToggleChange = { appSettings.setCrossfadeRepeatOne(it) },
                        enabled = crossfadeEnabled
                    ),
                    SettingItem(
                        Icons.Default.HighQuality,
                        context.getString(R.string.settings_bit_perfect_mode),
                        if (audioRoutingMode == "app") context.getString(R.string.settings_bit_perfect_mode_desc_dac) else context.getString(R.string.settings_bit_perfect_mode_desc_native),
                        toggleState = bitPerfectMode,
                        onToggleChange = { 
                            appSettings.setBitPerfectMode(it)
                            showRestartDialog = true
                            restartDialogMessage = "High-resolution audio mode changed. Restart the app to apply the new audio settings."
                        }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_time_display),
                items = listOf(
                    SettingItem(
                        androidx.compose.material.icons.Icons.Default.AccessTime,
                        context.getString(R.string.settings_use_hours),
                        if (useHoursInTimeFormat) context.getString(R.string.settings_use_hours_enabled) else context.getString(R.string.settings_use_hours_disabled),
                        toggleState = useHoursInTimeFormat,
                        onToggleChange = { appSettings.setUseHoursInTimeFormat(it) }
                    )
                )
            )
        )

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            items(
                items = settingGroups,
                key = { "queueplayback_${it.title}" },
                contentType = { "settingGroup" }
            ) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        group.items.forEachIndexed { index, item ->
                            TunerSettingRow(item = item)
                            
                            // If this is a crossfade setting with enabled state and data (duration), show the slider
                            if (item.data != null && item.data is Float && item.toggleState == true) {
                                val duration = item.data as Float
                                // Remove divider between crossfade and duration slider
                                // Master animation for crossfade duration slider
                                AnimatedVisibility(
                                    visible = crossfadeEnabled,
                                    enter = fadeIn(tween(500, delayMillis = 200)) +
                                            slideInVertically(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                ),
                                                initialOffsetY = { 40 }
                                            ),
                                    exit = fadeOut(tween(300)) +
                                           shrinkVertically(
                                               animationSpec = spring(
                                                   dampingRatio = Spring.DampingRatioMediumBouncy,
                                                   stiffness = Spring.StiffnessLow
                                               ),
                                               shrinkTowards = Alignment.Top
                                           )
                                ) {
                                    // Crossfade duration slider integrated within the same card
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = context.getString(R.string.settings_crossfade_duration),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = context.getString(R.string.settings_crossfade_duration_desc, crossfadeDuration),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Slider(
                                            value = crossfadeDuration,
                                            onValueChange = { appSettings.setCrossfadeDuration(it) },
                                            valueRange = 0.5f..12f,
                                            steps = 22,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = context.getString(R.string.settings_crossfade_min),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = context.getString(R.string.settings_crossfade_max),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                            // Add divider between crossfade and high-resolution settings
                            if (index < group.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
            
            item(key = "audio_routing_section") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_audio_routing),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(34.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                tonalElevation = 0.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = context.getString(R.string.settings_dac_usb_audio),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = context.getString(R.string.settings_dac_usb_audio_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ExpressiveButtonGroup(
                            items = listOf(context.getString(R.string.settings_audio_routing_default), context.getString(R.string.settings_audio_routing_app), context.getString(R.string.settings_audio_routing_system)),
                            selectedIndex = when (audioRoutingMode) {
                                "default" -> 0
                                "app" -> 1
                                "system" -> 2
                                else -> 0
                            },
                            onItemClick = { index ->
                                when (index) {
                                    0 -> {
                                        appSettings.setAudioRoutingMode("default")
                                        showRestartDialog = true
                                        restartDialogMessage = "Audio routing changed to Default. Restart the app to apply the changes."
                                    }
                                    1 -> {
                                        appSettings.setAudioRoutingMode("app")
                                        // App routing enables high-resolution mode for direct DAC output
                                        if (!appSettings.bitPerfectMode.value) {
                                            appSettings.setBitPerfectMode(true)
                                        }
                                        showRestartDialog = true
                                        restartDialogMessage = "Audio routing changed to App mode. Restart the app to apply the changes."
                                    }
                                    2 -> {
                                        appSettings.setAudioRoutingMode("system")
                                        showRestartDialog = true
                                        restartDialogMessage = "Audio routing changed to System. Restart the app to apply the changes."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (audioRoutingMode) {
                                "app" -> context.getString(R.string.settings_audio_routing_app_desc)
                                "system" -> context.getString(R.string.settings_audio_routing_system_desc)
                                else -> context.getString(R.string.settings_audio_routing_default_desc)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item(key = "queue_playback_bottom_spacer") { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    // Playlist Click Behavior Dialog
    if (showPlaylistBehaviorDialog) {
        val haptic = LocalHapticFeedback.current
        val scope = rememberCoroutineScope()
        val playlistSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = { showPlaylistBehaviorDialog = false },
            sheetState = playlistSheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.playlist_action_title),
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
                                text = context.getString(R.string.playlist_action_desc),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Option 1: Ask each time
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            scope.launch {
                                appSettings.setPlaylistClickBehavior("ask")
                                showPlaylistBehaviorDialog = false
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (playlistClickBehavior == "ask")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = if (playlistClickBehavior == "ask") {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
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
                                color = if (playlistClickBehavior == "ask")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Help,
                                        contentDescription = null,
                                        tint = if (playlistClickBehavior == "ask")
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.playlist_ask_each_time),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (playlistClickBehavior == "ask")
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.playlist_ask_each_time_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (playlistClickBehavior == "ask")
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (playlistClickBehavior == "ask") {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = context.getString(R.string.ui_selected),
                                    
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Option 2: Load entire playlist
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            scope.launch {
                                appSettings.setPlaylistClickBehavior("play_all")
                                showPlaylistBehaviorDialog = false
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (playlistClickBehavior == "play_all")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = if (playlistClickBehavior == "play_all") {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
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
                                color = if (playlistClickBehavior == "play_all")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.QueueMusic,
                                        contentDescription = null,
                                        tint = if (playlistClickBehavior == "play_all")
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.playlist_action_load_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (playlistClickBehavior == "play_all")
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.playlist_action_load_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (playlistClickBehavior == "play_all")
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (playlistClickBehavior == "play_all") {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Option 3: Play only this song
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            scope.launch {
                                appSettings.setPlaylistClickBehavior("play_one")
                                showPlaylistBehaviorDialog = false
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (playlistClickBehavior == "play_one")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = if (playlistClickBehavior == "play_one") {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
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
                                color = if (playlistClickBehavior == "play_one")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = RhythmIcons.Play,
                                        contentDescription = null,
                                        tint = if (playlistClickBehavior == "play_one")
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.playlist_action_single_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (playlistClickBehavior == "play_one")
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.playlist_action_single_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (playlistClickBehavior == "play_one")
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (playlistClickBehavior == "play_one") {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Show Queue Dialog Setting Dialog
    if (showQueueDialogSettingDialog) {
        val haptic = LocalHapticFeedback.current
        val scope = rememberCoroutineScope()
        val queueSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = { showQueueDialogSettingDialog = false },
            sheetState = queueSheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.queue_action_title),
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
                                text = context.getString(R.string.queue_action_choose),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Option 1: Ask each time (show dialog)
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            scope.launch {
                                appSettings.setShowQueueDialog(true)
                                showQueueDialogSettingDialog = false
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (showQueueDialog)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = if (showQueueDialog) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
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
                                color = if (showQueueDialog)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Help,
                                        contentDescription = null,
                                        tint = if (showQueueDialog)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.queue_action_ask_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (showQueueDialog)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.queue_action_ask_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (showQueueDialog)
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (showQueueDialog) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Option 2: Always add to queue
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            scope.launch {
                                appSettings.setShowQueueDialog(false)
                                showQueueDialogSettingDialog = false
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (!showQueueDialog)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = if (!showQueueDialog) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
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
                                color = if (!showQueueDialog)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlaylistAdd,
                                        contentDescription = null,
                                        tint = if (!showQueueDialog)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.queue_action_always_add_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (!showQueueDialog)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.queue_action_always_add_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (!showQueueDialog)
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!showQueueDialog) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // App Restart Dialog for audio routing changes
    if (showRestartDialog) {
        AppRestartDialog(
            onDismiss = { showRestartDialog = false },
            onRestart = {
                showRestartDialog = false
                chromahub.rhythm.app.util.AppRestarter.restartApp(context)
            },
            onContinue = {
                showRestartDialog = false
                // Continue without restart
            },
            message = restartDialogMessage
        )
    }
}

// ✅ FULLY MERGED Playlists Screen (simplified playlist management)
@Composable
fun PlaylistsSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val appSettings = AppSettings.getInstance(context)
    val musicViewModel: MusicViewModel = viewModel()
    val playlists by musicViewModel.playlists.collectAsState()
    val defaultPlaylistsEnabled by appSettings.defaultPlaylistsEnabled.collectAsState()

    val defaultPlaylists = playlists.filter { it.isDefault }
    val userPlaylists = playlists.filter { !it.isDefault }
    val emptyPlaylists = playlists.filter { !it.isDefault && it.songs.isEmpty() }

    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<Playlist?>(null) }
    var showBulkExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showOperationProgress by remember { mutableStateOf(false) }
    var operationProgressText by remember { mutableStateOf("") }
    var showCleanupConfirmDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }

    val settingGroups = listOf(
        SettingGroup(
            title = context.getString(R.string.settings_playlists_overview),
            items = listOf() // Empty items - we'll add the stat card separately
        ),
        SettingGroup(
            title = context.getString(R.string.settings_playlists_management),
            items = listOf(
                SettingItem(
                    Icons.Default.AddCircle,
                    context.getString(R.string.settings_create_new_playlist),
                    context.getString(R.string.settings_create_new_playlist_desc),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        showCreatePlaylistDialog = true
                    }
                ),
                SettingItem(
                    Icons.Default.Upload,
                    context.getString(R.string.settings_import_playlists),
                    context.getString(R.string.settings_import_playlists_desc),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        showImportDialog = true
                    }
                ),
                SettingItem(
                    Icons.Default.Download,
                    context.getString(R.string.settings_export_all_playlists),
                    context.getString(R.string.settings_export_all_playlists_desc),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        showBulkExportDialog = true
                    }
                )
            ) + if (emptyPlaylists.isNotEmpty()) listOf(
                SettingItem(
                    Icons.Default.Delete,
                    context.getString(R.string.settings_cleanup_empty_playlists),
                    context.getString(R.string.settings_cleanup_empty_playlists_desc, emptyPlaylists.size, if (emptyPlaylists.size > 1) "s" else ""),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        showCleanupConfirmDialog = true
                    }
                )
            ) else emptyList()
        ),
        SettingGroup(
            title = context.getString(R.string.settings_default_playlists),
            items = listOf(
                SettingItem(
                    Icons.Default.LibraryMusic,
                    context.getString(R.string.settings_enable_default_playlists),
                    context.getString(R.string.settings_enable_default_playlists_desc),
                    onClick = null,
                    toggleState = defaultPlaylistsEnabled,
                    onToggleChange = { enabled ->
                        appSettings.setDefaultPlaylistsEnabled(enabled)
                        // Reload playlists to apply the change
                        if (enabled) {
                            // Add default playlists if they don't exist
                            val currentPlaylists = playlists.toMutableList()
                            if (currentPlaylists.none { it.id == "2" }) {
                                currentPlaylists.add(Playlist("2", "Recently Added"))
                            }
                            if (currentPlaylists.none { it.id == "3" }) {
                                currentPlaylists.add(Playlist("3", "Most Played"))
                            }
                            // Save updated playlists
                            val playlistsJson = GsonUtils.gson.toJson(currentPlaylists)
                            appSettings.setPlaylists(playlistsJson)
                        } else {
                            // Remove default playlists (except Favorites)
                            val filteredPlaylists = playlists.filter { it.id == "1" || !it.isDefault }
                            val playlistsJson = GsonUtils.gson.toJson(filteredPlaylists)
                            appSettings.setPlaylists(playlistsJson)
                        }
                        // Show restart dialog
                        showRestartDialog = true
                    }
                )
            ) + if (defaultPlaylists.isNotEmpty()) {
                defaultPlaylists.map { playlist ->
                    SettingItem(
                        Icons.Default.MusicNote,
                        playlist.name,
                        "${playlist.songs.size} songs",
                        onClick = null, // No action for default playlists
                        data = playlist.id
                    )
                }
            } else {
                listOf(
                    SettingItem(
                        Icons.Default.Info,
                        context.getString(R.string.settings_no_default_playlists),
                        context.getString(R.string.settings_no_default_playlists_desc),
                        onClick = null
                    )
                )
            }
        ),
        SettingGroup(
            title = context.getString(R.string.settings_my_playlists),
            items = if (userPlaylists.isNotEmpty()) {
                userPlaylists.map { playlist ->
                    SettingItem(
                        Icons.Default.QueueMusic,
                        playlist.name,
                        "${playlist.songs.size} songs",
                        onClick = null, // No navigation
                        data = playlist.id // Store playlist ID for deletion
                    )
                }
            } else {
                listOf(
                    SettingItem(
                        Icons.Default.Add,
                        context.getString(R.string.settings_no_custom_playlists),
                        context.getString(R.string.settings_no_custom_playlists_desc),
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            showCreatePlaylistDialog = true
                        }
                    )
                )
            }
        )
    )

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_playlists),
        showBackButton = true,
        onBackClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onBackClick()
        }
    ) { modifier ->
        val lazyListState = rememberSaveable(
            key = "playlists_settings_scroll_state",
            saver = LazyListStateSaver
        ) {
            androidx.compose.foundation.lazy.LazyListState()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Collection Statistics Card
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_playlists_overview),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${playlists.size}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = context.getString(R.string.settings_total),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${userPlaylists.size}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = context.getString(R.string.settings_custom),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${defaultPlaylists.size}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = context.getString(R.string.settings_default),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(settingGroups.filter { it.title != context.getString(R.string.settings_playlists_overview) }) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        group.items.forEachIndexed { index, item ->
                            // Check if this is a toggle setting (has both toggleState and onToggleChange)
                            if (item.toggleState != null && item.onToggleChange != null) {
                                TunerSettingRow(item = item)
                            } else if (group.title == context.getString(R.string.settings_default_playlists) || 
                                       group.title == context.getString(R.string.settings_my_playlists)) {
                                PlaylistSettingRow(
                                    item = item,
                                    isDefaultPlaylist = group.title == context.getString(R.string.settings_default_playlists),
                                    onDelete = { playlist ->
                                        playlistToDelete = playlist
                                    }
                                )
                            } else {
                                TunerSettingRow(item = item)
                            }
                            if (index < group.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onConfirm = { name ->
                musicViewModel.createPlaylist(name)
                showCreatePlaylistDialog = false
            }
        )
    }

    if (showBulkExportDialog) {
        BulkPlaylistExportDialog(
            playlistCount = playlists.size,
            onDismiss = { showBulkExportDialog = false },
            onExport = { format, includeDefault ->
                showBulkExportDialog = false
                showOperationProgress = true
                operationProgressText = context.getString(R.string.operation_exporting_playlists)
                musicViewModel.exportAllPlaylists(format, includeDefault) { result ->
                    showOperationProgress = false
                }
            },
            onExportToCustomLocation = { format, includeDefault, directoryUri ->
                showBulkExportDialog = false
                showOperationProgress = true
                operationProgressText = context.getString(R.string.operation_exporting_playlists_location)
                musicViewModel.exportAllPlaylists(format, includeDefault, directoryUri) { result ->
                    showOperationProgress = false
                }
            }
        )
    }

    if (showImportDialog) {
        PlaylistImportDialog(
            onDismiss = { showImportDialog = false },
            onImport = { uri, onResult, _ ->
                showImportDialog = false
                showOperationProgress = true
                operationProgressText = context.getString(R.string.operation_importing_playlist)
                musicViewModel.importPlaylist(uri, onResult) {
                    // When import completes successfully, show restart dialog
                    showOperationProgress = false
                    showRestartDialog = true
                }
            }
        )
    }

    if (showOperationProgress) {
        PlaylistOperationProgressDialog(
            operation = operationProgressText,
            onDismiss = { /* Cannot dismiss during operation */ }
        )
    }

    if (showCleanupConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCleanupConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = context.getString(R.string.dialog_cleanup_empty_playlists_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(context.getString(R.string.dialog_cleanup_empty_playlists_message, emptyPlaylists.size, if (emptyPlaylists.size > 1) "s" else ""))
            },
            confirmButton = {
                Button(
                    onClick = {
                        emptyPlaylists.forEach { playlist ->
                            musicViewModel.deletePlaylist(playlist.id)
                        }
                        showCleanupConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.dialog_delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCleanupConfirmDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.dialog_cancel))
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // App Restart Dialog for default playlists toggle
    if (showRestartDialog) {
        AppRestartDialog(
            onDismiss = { showRestartDialog = false },
            onRestart = {
                showRestartDialog = false
                chromahub.rhythm.app.util.AppRestarter.restartApp(context)
            },
            onContinue = {
                showRestartDialog = false
                // Continue without restart
            }
        )
    }

    playlistToDelete?.let { playlist ->
        AlertDialog(
            onDismissRequest = { playlistToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = context.getString(R.string.dialog_delete_playlist_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(context.getString(R.string.dialog_delete_playlist_message, playlist.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        musicViewModel.deletePlaylist(playlist.id)
                        playlistToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.dialog_delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { playlistToDelete = null }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.dialog_cancel))
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun PlaylistSettingRow(
    item: SettingItem,
    isDefaultPlaylist: Boolean,
    onDelete: (Playlist) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val musicViewModel: MusicViewModel = viewModel()
    val playlists by musicViewModel.playlists.collectAsState()

    // Get playlist from data field (should be playlist ID)
    val playlistId = item.data as? String
    val playlist = playlists.find { it.id == playlistId }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            item.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Show delete icon only for user playlists
        if (!isDefaultPlaylist && playlist != null) {
            IconButton(
                onClick = {
                    HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                    onDelete(playlist)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete playlist",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ✅ REDESIGNED Media Scan Screen with improved UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScanSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val appSettings = AppSettings.getInstance(context)
    val musicViewModel: MusicViewModel = viewModel()

    // Get all songs and filtered items
    val allSongs by musicViewModel.songs.collectAsState()
    val filteredSongs by musicViewModel.filteredSongs.collectAsState()
    val blacklistedSongs by appSettings.blacklistedSongs.collectAsState()
    val blacklistedFolders by appSettings.blacklistedFolders.collectAsState()
    val whitelistedSongs by appSettings.whitelistedSongs.collectAsState()
    val whitelistedFolders by appSettings.whitelistedFolders.collectAsState()

    // Get current media scan mode from settings
    val mediaScanMode by appSettings.mediaScanMode.collectAsState()

    // Mode state
    var currentMode by remember {
        mutableStateOf(
            if (mediaScanMode == "whitelist") chromahub.rhythm.app.shared.presentation.components.MediaScanMode.WHITELIST
            else chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST
        )
    }

    // Bottom sheet states
    var showSongsBottomSheet by remember { mutableStateOf(false) }
    var showFoldersBottomSheet by remember { mutableStateOf(false) }

    // File picker launcher for folder selection
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val docId = DocumentsContract.getTreeDocumentId(uri)
                    val split = docId.split(":")

                    if (split.size >= 2) {
                        val storageType = split[0] // e.g., "primary", "home", or specific SD card ID
                        val relativePath = split[1] // e.g., "Music/MyFolder"

                        // Build the full path based on storage type
                        val fullPath = when (storageType) {
                            "primary" -> "/storage/emulated/0/$relativePath"
                            "home" -> "/storage/emulated/0/$relativePath"
                            else -> {
                                // For SD cards or other storage, try to construct path
                                // This is a best-effort approach
                                if (storageType.contains("-")) {
                                    // SD card UUID format
                                    "/storage/$storageType/$relativePath"
                                } else {
                                    // Fallback to emulated storage
                                    "/storage/emulated/0/$relativePath"
                                }
                            }
                        }

                        if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) {
                            appSettings.addFolderToBlacklist(fullPath)
                        } else {
                            appSettings.addFolderToWhitelist(fullPath)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MediaScanSettingsScreen", "Error parsing folder path", e)
                }
            }
        }
    }

    // Computed values OUTSIDE LazyColumn
    val filteredSongDetails = remember(allSongs, blacklistedSongs, whitelistedSongs, currentMode) {
        when (currentMode) {
            chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST ->
                allSongs.filter { song -> blacklistedSongs.contains(song.id) }
            chromahub.rhythm.app.shared.presentation.components.MediaScanMode.WHITELIST ->
                allSongs.filter { song -> whitelistedSongs.contains(song.id) }
        }
    }

    val filteredFoldersList = remember(blacklistedFolders, whitelistedFolders, currentMode) {
        when (currentMode) {
            chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST -> blacklistedFolders
            chromahub.rhythm.app.shared.presentation.components.MediaScanMode.WHITELIST -> whitelistedFolders
        }
    }

    val settingGroups = listOf(
        SettingGroup(
            title = context.getString(R.string.settings_mode_selection),
            items = listOf(
                SettingItem(
                    Icons.Default.Block,
                    context.getString(R.string.settings_blacklist_mode),
                    context.getString(R.string.settings_blacklist_mode_desc),
                    toggleState = currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST,
                    onToggleChange = { enabled ->
                        if (enabled) {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            currentMode = chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST
                            appSettings.setMediaScanMode("blacklist")
                        }
                    }
                ),
                SettingItem(
                    Icons.Default.CheckCircle,
                    context.getString(R.string.settings_whitelist_mode),
                    context.getString(R.string.settings_whitelist_mode_desc),
                    toggleState = currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.WHITELIST,
                    onToggleChange = { enabled ->
                        if (enabled) {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            currentMode = chromahub.rhythm.app.shared.presentation.components.MediaScanMode.WHITELIST
                            appSettings.setMediaScanMode("whitelist")
                        }
                    }
                )
            )
        ),
        SettingGroup(
            title = context.getString(R.string.settings_song_management),
            items = listOf(
                SettingItem(
                    Icons.AutoMirrored.Filled.QueueMusic,
                    context.getString(R.string.settings_manage_songs),
                    context.getString(R.string.settings_manage_songs_desc, filteredSongDetails.size, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked) else context.getString(R.string.settings_whitelisted)),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        showSongsBottomSheet = true
                    }
                ),
                SettingItem(
                    Icons.Default.Clear,
                    context.getString(R.string.settings_clear_all_songs),
                    context.getString(R.string.settings_clear_all_songs_desc, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked) else context.getString(R.string.settings_whitelisted)),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) {
                            appSettings.clearBlacklist()
                        } else {
                            appSettings.clearWhitelist()
                        }
                    }
                )
            )
        ),
        SettingGroup(
            title = context.getString(R.string.settings_folder_management),
            items = listOf(
                SettingItem(
                    Icons.Default.Folder,
                    context.getString(R.string.settings_manage_folders),
                    context.getString(R.string.settings_manage_folders_desc, filteredFoldersList.size, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked) else context.getString(R.string.settings_whitelisted)),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        showFoldersBottomSheet = true
                    }
                ),
                SettingItem(
                    Icons.Default.Add,
                    context.getString(R.string.settings_add_folder),
                    context.getString(R.string.settings_add_folder_desc, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_block) else context.getString(R.string.settings_whitelist)),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        folderPickerLauncher.launch(intent)
                    }
                ),
                SettingItem(
                    Icons.Default.Clear,
                    context.getString(R.string.settings_clear_all_folders),
                    context.getString(R.string.settings_clear_all_folders_desc, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked) else context.getString(R.string.settings_whitelisted)),
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) {
                            blacklistedFolders.forEach { folder ->
                                appSettings.removeFolderFromBlacklist(folder)
                            }
                        } else {
                            whitelistedFolders.forEach { folder ->
                                appSettings.removeFolderFromWhitelist(folder)
                            }
                        }
                    }
                )
            )
        )
    )

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_media_scan),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        val lazyListState = rememberSaveable(
            key = "media_scan_settings_scroll_state",
            saver = LazyListStateSaver
        ) {
            androidx.compose.foundation.lazy.LazyListState()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main overview content
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Mode Selection with ExpressiveButtonGroup
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = context.getString(R.string.settings_mode_selection),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            ExpressiveButtonGroup(
                                items = listOf(
                                    context.getString(R.string.settings_blacklist_mode),
                                    context.getString(R.string.settings_whitelist_mode)
                                ),
                                selectedIndex = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) 0 else 1,
                                onItemClick = { index ->
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    currentMode = if (index == 0) {
                                        appSettings.setMediaScanMode("blacklist")
                                        chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST
                                    } else {
                                        appSettings.setMediaScanMode("whitelist")
                                        chromahub.rhythm.app.shared.presentation.components.MediaScanMode.WHITELIST
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                    context.getString(R.string.settings_blacklist_mode_desc)
                                else
                                    context.getString(R.string.settings_whitelist_mode_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            settingGroups.drop(1).forEach { group ->
                item {
                    Text(
                        text = group.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            group.items.forEachIndexed { index, item ->
                                TunerSettingRow(item = item)
                                if (index < group.items.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Tips Card
            item {
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
                                text = context.getString(R.string.settings_quick_tips),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        MediaScanTipItem(
                            icon = Icons.Default.Block,
                            text = context.getString(R.string.settings_quick_tip_blacklist)
                        )
                        MediaScanTipItem(
                            icon = Icons.Default.CheckCircle,
                            text = context.getString(R.string.settings_quick_tip_whitelist)
                        )
                        MediaScanTipItem(
                            icon = Icons.Default.Folder,
                            text = context.getString(R.string.settings_quick_tip_folder)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Songs bottom sheet
    if (showSongsBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = { showSongsBottomSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.settings_manage_songs),
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
                                text = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked_songs) else context.getString(R.string.settings_whitelisted_songs),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) Icons.Default.Block else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${filteredSongDetails.size}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked) else context.getString(R.string.settings_whitelisted),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${allSongs.size}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = context.getString(R.string.settings_total_songs),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Songs list with lazy column
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredSongDetails,
                        key = { "filtered_${it.id}" },
                        contentType = { "song" }
                    ) { song ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                            MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = song.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = song.artist,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                FilledIconButton(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) {
                                            appSettings.removeFromBlacklist(song.id)
                                        } else {
                                            appSettings.removeFromWhitelist(song.id)
                                        }
                                    },
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                            MaterialTheme.colorScheme.errorContainer
                                        else MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                            MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Clear button at bottom
                if (filteredSongDetails.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                            if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) {
                                appSettings.clearBlacklist()
                            } else {
                                appSettings.clearWhitelist()
                            }
                            showSongsBottomSheet = false
                        },
                        border = BorderStroke(2.dp, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(context.getString(R.string.settings_clear_all_button, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked) else context.getString(R.string.settings_whitelisted)))
                    }
                }
            }
        }
    }

    // Folders bottom sheet
    if (showFoldersBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = { showFoldersBottomSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.settings_manage_folders),
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
                                text = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked_folders) else context.getString(R.string.settings_whitelisted_folders),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) Icons.Default.FolderOff else Icons.Default.Folder,
                            contentDescription = null,
                            tint = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${filteredFoldersList.size}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) context.getString(R.string.settings_blocked_folders) else context.getString(R.string.settings_whitelisted_folders),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Folders list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredFoldersList, key = { "folder_${it.hashCode()}" }) { folder ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Folder,
                                        contentDescription = null,
                                        tint = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = File(folder).name.ifEmpty { context.getString(R.string.settings_root) },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = folder,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                FilledIconButton(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) {
                                            appSettings.removeFolderFromBlacklist(folder)
                                        } else {
                                            appSettings.removeFolderFromWhitelist(folder)
                                        }
                                    },
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                            MaterialTheme.colorScheme.errorContainer
                                        else MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = context.getString(R.string.cd_remove),
                                        tint = if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                            MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Action buttons at bottom
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                            folderPickerLauncher.launch(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(context.getString(R.string.settings_add_folder_button))
                    }

                    if (filteredFoldersList.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST) {
                                    blacklistedFolders.forEach { folder ->
                                        appSettings.removeFolderFromBlacklist(folder)
                                    }
                                } else {
                                    whitelistedFolders.forEach { folder ->
                                        appSettings.removeFolderFromWhitelist(folder)
                                    }
                                }
                                showFoldersBottomSheet = false
                            },
                            border = BorderStroke(2.dp, if (currentMode == chromahub.rhythm.app.shared.presentation.components.MediaScanMode.BLACKLIST)
                                MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(context.getString(R.string.settings_clear_all_button_short))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSeparatorsSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val appSettings = AppSettings.getInstance(context)
    val scope = rememberCoroutineScope()

    val artistSeparatorEnabled by appSettings.artistSeparatorEnabled.collectAsState()
    val artistSeparatorDelimiters by appSettings.artistSeparatorDelimiters.collectAsState()
    val groupByAlbumArtist by appSettings.groupByAlbumArtist.collectAsState()

    var showDelimiterBottomSheet by remember { mutableStateOf(false) }
    var tempDelimiters by remember { mutableStateOf(artistSeparatorDelimiters) }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.artists_title),
        showBackButton = true,
        onBackClick = {
            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
            onBackClick()
        }
    ) { modifier ->
        val settingGroups = listOf(
            SettingGroup(
                title = context.getString(R.string.artist_multi_parsing),
                items = listOf(
                    SettingItem(
                        Icons.Default.Person,
                        context.getString(R.string.artist_enable_separation),
                        context.getString(R.string.artist_enable_separation_desc),
                        toggleState = artistSeparatorEnabled,
                        onToggleChange = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            appSettings.setArtistSeparatorEnabled(it)
                        }
                    ),
                    SettingItem(
                        Icons.Default.Settings,
                        context.getString(R.string.artist_configure_delimiters),
                        context.getString(R.string.artist_current_delimiters, artistSeparatorDelimiters.toCharArray().joinToString(", ")),
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            tempDelimiters = artistSeparatorDelimiters
                            showDelimiterBottomSheet = true
                        }
                    )
                ),
            ),
            SettingGroup(
                title = context.getString(R.string.artist_library_organization),
                items = listOf(
                    SettingItem(
                        Icons.Default.Album,
                        context.getString(R.string.artist_group_by_album_artist),
                        context.getString(R.string.artist_group_by_album_artist_desc),
                        toggleState = groupByAlbumArtist,
                        onToggleChange = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            appSettings.setGroupByAlbumArtist(it)
                        }
                    )
                )
            )
        )

        val lazyListState = rememberSaveable(
            key = "artist_separators_scroll_state",
            saver = LazyListStateSaver
        ) {
            androidx.compose.foundation.lazy.LazyListState()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            items(
                items = settingGroups,
                key = { "setting_${it.title}" },
                contentType = { "settingGroup" }
            ) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        when (group.title) {
                            "Multi-Artist Parsing" -> {
                                // First item: Enable Artist Separation
                                TunerSettingRow(item = group.items[0])

                                // Second item: Configure Delimiters with AnimatedVisibility
                                AnimatedVisibility(
                                    visible = artistSeparatorEnabled,
                                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                                ) {
                                    Column {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                        TunerSettingRow(item = group.items[1])
                                    }
                                }
                            }
                            else -> {
                                // Default rendering for other groups
                                group.items.forEachIndexed { index, item ->
                                    TunerSettingRow(item = item)
                                    if (index < group.items.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Info Card
            item {
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
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.settings_about_multi_artist),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = context.getString(R.string.settings_multi_artist_parsing_info),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Examples
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.settings_examples),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        ArtistSeparatorExampleItem(
                            original = "Artist1/Artist2",
                            result = "Artist1, Artist2"
                        )
                        ArtistSeparatorExampleItem(
                            original = "Artist1; Artist2",
                            result = "Artist1, Artist2"
                        )
                        ArtistSeparatorExampleItem(
                            original = "Artist1 & Artist2",
                            result = "Artist1, Artist2"
                        )
                        ArtistSeparatorExampleItem(
                            original = "Artist1\\\\/Artist2",
                            result = "Artist1/Artist2 (escaped)"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Delimiter Configuration Bottom Sheet
    if (showDelimiterBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = { showDelimiterBottomSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.settings_configure_delimiters),
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
                                text = context.getString(R.string.settings_select_artist_separators),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val commonDelimiters = listOf(
                    '/' to context.getString(R.string.delimiter_slash),
                    ';' to context.getString(R.string.delimiter_semicolon),
                    ',' to context.getString(R.string.delimiter_comma),
                    '+' to context.getString(R.string.delimiter_plus),
                    '&' to context.getString(R.string.delimiter_ampersand)
                )

                // Delimiter options in a grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(commonDelimiters) { (char, name) ->
                        val isSelected = tempDelimiters.contains(char)
                        
                        // Master animation states
                        var isPressed by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.96f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "delimiter_scale"
                        )
                        
                        val containerColor by animateColorAsState(
                            targetValue = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "delimiter_container_color"
                        )
                        
                        Card(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                isPressed = true
                                tempDelimiters = if (tempDelimiters.contains(char)) {
                                    tempDelimiters.replace(char.toString(), "")
                                } else {
                                    tempDelimiters + char
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = containerColor
                            ),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Delimiter Preview
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                
                                // Optional description could go here if needed
                            }
                        }
                        
                        // Reset press state
                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                delay(150)
                                isPressed = false
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons Row - Consistent with other bottom sheets
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(500, delayMillis = 600)) +
                            slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialOffsetY = { 40 }
                            )
                ) {
                    var savePressed by remember { mutableStateOf(false) }
                    var resetPressed by remember { mutableStateOf(false) }

                    val saveScale by animateFloatAsState(
                        targetValue = if (savePressed) 0.96f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "saveScale"
                    )

                    val resetScale by animateFloatAsState(
                        targetValue = if (resetPressed) 0.96f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "resetScale"
                    )

                    LaunchedEffect(savePressed) {
                        if (savePressed) {
                            delay(150)
                            savePressed = false
                        }
                    }

                    LaunchedEffect(resetPressed) {
                        if (resetPressed) {
                            delay(150)
                            resetPressed = false
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Reset Button - Secondary action
                        OutlinedButton(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                resetPressed = true
                                tempDelimiters = artistSeparatorDelimiters // Reset to original
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .graphicsLayer {
                                    scaleX = resetScale
                                    scaleY = resetScale
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.5.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Save Changes Button - Primary action
                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                savePressed = true
                                scope.launch {
                                    appSettings.setArtistSeparatorDelimiters(tempDelimiters)
                                    showDelimiterBottomSheet = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .graphicsLayer {
                                    scaleX = saveScale
                                    scaleY = saveScale
                                },
                            shape = RoundedCornerShape(16.dp),
                            enabled = tempDelimiters.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistSeparatorExampleItem(original: String, result: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "\"$original\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 24.dp, top = 2.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = result,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ApiServiceRow(
    title: String,
    description: String,
    status: String,
    isConfigured: Boolean,
    icon: ImageVector,
    isEnabled: Boolean = true,
    showToggle: Boolean = false,
    onToggle: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    when {
                        !isEnabled -> MaterialTheme.colorScheme.surfaceVariant
                        isConfigured -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    !isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                    isConfigured -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onErrorContainer
                },
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = when {
                        !isEnabled -> MaterialTheme.colorScheme.surfaceVariant
                        isConfigured -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (!isEnabled) context.getString(R.string.status_disabled) else status,
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            !isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                            isConfigured -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onErrorContainer
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        // Toggle or Arrow icon
        if (showToggle && onToggle != null) {
            TunerAnimatedSwitch(
                checked = isEnabled,
                onCheckedChange = { enabled ->
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onToggle(enabled)
                }
            )
        }
    }
}

// SpotifyApiConfigDialog removed - Canvas API has been removed



@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onNavigateToUpdates: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val appUpdaterViewModel: AppUpdaterViewModel = viewModel()
    var showLicensesSheet by remember { mutableStateOf(false) }

    CollapsibleHeaderScreen(
        title = "About",
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        val lazyListState = rememberSaveable(
            key = "about_screen_scroll_state",
            saver = LazyListStateSaver
        ) {
            androidx.compose.foundation.lazy.LazyListState()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // App Header Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Rhythm logo and name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = chromahub.rhythm.app.R.drawable.rhythm_splash_logo),
                                contentDescription = context.getString(R.string.updates_rhythm_logo_cd),
                                modifier = Modifier.size(82.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = context.getString(R.string.app_name),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = context.getString(R.string.settings_about_music_player),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "v${BuildConfig.VERSION_NAME}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Your Music Your Rhythm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

//            item {
                // Project Details Card
//                Card(
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.surfaceContainer
//                    ),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
//                    shape = RoundedCornerShape(20.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.padding(bottom = 12.dp)
//                        ) {
//                            Icon(
//                                imageVector = RhythmIcons.Actions.Info,
//                                contentDescription = null,
//                                
//                                modifier = Modifier.size(20.dp)
//                            )
//                            Spacer(modifier = Modifier.width(12.dp))
//                            Text(
//                                text = context.getString(R.string.settings_about_project_details),
//                                style = MaterialTheme.typography.titleMedium,
//                                fontWeight = FontWeight.Bold,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                        }
//
//                        DetailRow("Version", BuildConfig.VERSION_NAME)
//                        DetailRow("Build", BuildConfig.VERSION_CODE.toString())
//                        DetailRow("Target SDK", Build.VERSION.SDK_INT.toString())
//                        DetailRow("Architecture", "ARM64 & ARM32")
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        Text(
//                            text = context.getString(R.string.settings_about_built_with),
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//            }

            item {
                // Developer Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.ArtistFilled,
                                contentDescription = null,
                                
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.settings_about_credits),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        CommunityMember(
                            name = "Anjishnu Nandi",
                            role = "Lead Developer & Project Architect",
                            githubUsername = "cromaguy",
                            avatarUrl = "https://github.com/cromaguy.png",
                            supportUrl = "https://ko-fi.com/anjishnunandi",
                            context = context
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/anjishnunandi"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF13C3FF),
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalCafe,
                                    contentDescription = "Buy Coffee",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buy Me a Coffee",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Team ChromaHub
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = context.getString(R.string.settings_about_team_chromahub),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = context.getString(R.string.settings_about_team_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Acknowledgments
                        Text(
                            text = context.getString(R.string.special_thanks),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "• Google Material Design Team for design principles\n" +
                                  "• Android Open Source Project contributors\n" +
                                  "• Jetpack Compose development team\n" +
                                  "• Open source community for inspiration and libraries",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                // Community Credits Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.FavoriteFilled,
                                contentDescription = null,
                                
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.settings_about_community),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = context.getString(R.string.settings_about_community_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Community Members Grid
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CommunityMember(
                                name = "Izzy",
                                role = "Manages updates on IzzyOnDroid",
                                githubUsername = "IzzySoft",
                                avatarUrl = "https://github.com/IzzySoft.png",
                                context = context
                            )

                            CommunityMember(
                                name = "Christian",
                                role = "Collab & Project Booming's Lead Dev",
                                githubUsername = "mardous",
                                avatarUrl = "https://github.com/mardous.png",
                                context = context
                            )

                            CommunityMember(
                                name = "theovilardo",
                                role = "Collab & Project PixelPlayer's Lead Dev",
                                githubUsername = "theovilardo",
                                avatarUrl = "https://github.com/theovilardo.png",
                                context = context
                            )

                            CommunityMember(
                                name = "firefly-sylestia",
                                role = "Tester",
                                githubUsername = "firefly-sylestia",
                                avatarUrl = "https://github.com/firefly-sylestia.png",
                                context = context
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Community Call-to-Action
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.FavoriteFilled,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = context.getString(R.string.settings_about_want_featured),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = context.getString(R.string.settings_about_contribute),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Sponsors Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.FavoriteFilled,
                                contentDescription = null,
                                
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Sponsors",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "We would like to thank our sponsors for their generous support.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            CommunityMember(
                                name = "HyXeny",
                                role = "Sponsor",
                                githubUsername = "HyXeny",
                                avatarUrl = "https://github.com/HyXeny.png",
                                context = context
                            )

                            CommunityMember(
                                name = "Xiaomiraphealin",
                                role = "Sponsor",
                                githubUsername = "Xiaomiraphealin",
                                avatarUrl = "https://github.com/Xiaomiraphealin.png",
                                context = context
                            )
                        }

                    }
                }
            }

            item {
                // Open Source Licenses Button
                Button(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                        showLicensesSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Settings,
                            contentDescription = null,
                            
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = context.getString(R.string.settings_about_open_source_libs),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = context.getString(R.string.settings_about_view_dependencies),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                // Action Buttons Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.Download,
                                contentDescription = null,
                                
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.settings_about_actions),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                appUpdaterViewModel.checkForUpdates(force = true)
                                onNavigateToUpdates?.invoke()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = context.getString(R.string.settings_about_check_updates),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://rhythmweb.vercel.app/"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Language,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = context.getString(R.string.settings_about_visit_website),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cromaguy/Rhythm"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Code,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = context.getString(R.string.settings_about_view_github),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cromaguy/Rhythm/issues"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.BugReport,
                                    contentDescription = "Report Bug",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = context.getString(R.string.settings_about_report_bug),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/XjPyUYPQYc"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5865F2),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Chat,
                                    contentDescription = "Discord",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Join Discord",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/RhythmSupport"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0088CC),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Send,
                                    contentDescription = "Telegram",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Join Telegram",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Show licenses bottom sheet
        if (showLicensesSheet) {
            LicensesBottomSheet(
                onDismiss = { showLicensesSheet = false }
            )
        }
    }
}

@Composable
private fun CommunityMember(
    name: String,
    role: String,
    githubUsername: String,
    avatarUrl: String,
    supportUrl: String? = null,
    context: android.content.Context
) {
    val haptics = LocalHapticFeedback.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/$githubUsername"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ) {
        // Avatar with fallback
        val fallbackPainter = rememberVectorPainter(RhythmIcons.ArtistFilled)

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "$name's avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            error = fallbackPainter,
            placeholder = fallbackPainter
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = role,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Link,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "@$githubUsername",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                if (supportUrl != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.LocalCafe,
                        contentDescription = "Support",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF13C3FF)
                    )
                }
            }
        }

        Icon(
            imageVector = RhythmIcons.ArtistFilled,
            contentDescription = "View GitHub Profile",
            
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun TechStackItem(
    technology: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = technology,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CreditItem(
    name: String,
    role: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = role,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun UpdatesSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val updaterViewModel: AppUpdaterViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // Collect state from ViewModel and AppSettings
    val updatesEnabled by appSettings.updatesEnabled.collectAsState()
    val autoCheckForUpdates by appSettings.autoCheckForUpdates.collectAsState()
    val updateNotificationsEnabled by appSettings.updateNotificationsEnabled.collectAsState()
    val useSmartUpdatePolling by appSettings.useSmartUpdatePolling.collectAsState()
    val updateChannel by appSettings.updateChannel.collectAsState()
    val updateCheckIntervalHours by appSettings.updateCheckIntervalHours.collectAsState()
    val currentVersion by updaterViewModel.currentVersion.collectAsState()
    val latestVersion by updaterViewModel.latestVersion.collectAsState()
    val isCheckingForUpdates by updaterViewModel.isCheckingForUpdates.collectAsState()
    val updateAvailable by updaterViewModel.updateAvailable.collectAsState()
    val error by updaterViewModel.error.collectAsState()
    val isDownloading by updaterViewModel.isDownloading.collectAsState()
    val downloadProgress by updaterViewModel.downloadProgress.collectAsState()
    val downloadedFile by updaterViewModel.downloadedFile.collectAsState()
    val whatsNew = latestVersion?.whatsNew ?: emptyList()
    val knownIssues = latestVersion?.knownIssues ?: emptyList()

    // Dialog states
    var showChannelDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }

    // Determine status text for header
    val statusText = when {
        error != null -> context.getString(R.string.updates_status_error)
        isCheckingForUpdates -> context.getString(R.string.updates_status_checking)
        updateAvailable && latestVersion != null -> context.getString(R.string.updates_status_update_available)
        !updatesEnabled -> context.getString(R.string.updates_status_disabled)
        !autoCheckForUpdates -> context.getString(R.string.updates_status_manual)
        else -> context.getString(R.string.updates_status_up_to_date)
    }

    // Check for updates when the screen is first shown and updates are enabled
    LaunchedEffect(updatesEnabled) {
        if (updatesEnabled) {
            updaterViewModel.checkForUpdates(force = true)
        }
    }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.updates_title),
        showBackButton = true,
        onBackClick = onBackClick,
//        actions = {
//            Text(
//                text = statusText,
//                style = MaterialTheme.typography.bodyMedium,
//                color = when {
//                    error != null -> MaterialTheme.colorScheme.error
//                    updateAvailable -> Color(0xFF4CAF50)
//                    !updatesEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
//                    else -> MaterialTheme.colorScheme.primary
//                },
//                modifier = Modifier.padding(end = 16.dp)
//            )
//        }
    ) { modifier ->
        val lazyListState = rememberSaveable(
            key = "updates_settings_scroll_state",
            saver = LazyListStateSaver
        ) {
            androidx.compose.foundation.lazy.LazyListState()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Combined App Info and Update Status Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Rhythm logo and name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = chromahub.rhythm.app.R.drawable.rhythm_splash_logo),
                                contentDescription = "Rhythm Logo",
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = context.getString(R.string.common_rhythm),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Version info
                        Text(
                            text = context.getString(R.string.updates_version_prefix, currentVersion.versionName),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = context.getString(R.string.updates_released_prefix, currentVersion.releaseDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Status display with loader and progress like onboarding
                        when {
                            error != null -> {
                                Icon(
                                    imageVector = Icons.Rounded.Error,
                                    contentDescription = context.getString(R.string.updates_error),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = context.getString(R.string.updates_check_failed),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error ?: context.getString(R.string.updates_unknown_error),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { updaterViewModel.clearError() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(context.getString(R.string.ui_dismiss))
                                    }
                                    Button(
                                        onClick = {
                                            if (error?.contains("unknown sources", ignoreCase = true) == true ||
                                                error?.contains("install from unknown", ignoreCase = true) == true) {
                                                val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                                    data = Uri.parse("package:${context.packageName}")
                                                }
                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    val fallbackIntent = Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
                                                    context.startActivity(fallbackIntent)
                                                }
                                            } else {
                                                updaterViewModel.checkForUpdates(force = true)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (error?.contains("unknown sources", ignoreCase = true) == true ||
                                                             error?.contains("install from unknown", ignoreCase = true) == true)
                                                Icons.Rounded.Settings else Icons.Rounded.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (error?.contains("unknown sources", ignoreCase = true) == true ||
                                                 error?.contains("install from unknown", ignoreCase = true) == true) context.getString(R.string.updates_open_settings) else context.getString(R.string.updates_retry))
                                    }
                                }
                            }

                            !updatesEnabled -> {
                                Icon(
                                    imageVector = Icons.Rounded.UpdateDisabled,
                                    contentDescription = context.getString(R.string.updates_disabled),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = context.getString(R.string.updates_disabled),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = context.getString(R.string.updates_disabled_message),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { appSettings.setUpdatesEnabled(true) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SystemUpdate,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(context.getString(R.string.updates_enable_updates))
                                }
                            }

                            isCheckingForUpdates -> {
                                M3FourColorCircularLoader(
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = context.getString(R.string.updates_checking),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = { updaterViewModel.checkForUpdates(force = true) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(context.getString(R.string.updates_check_now))
                                }
                            }

                            updateAvailable && latestVersion != null -> {
                                Icon(
                                    imageVector = Icons.Rounded.Update,
                                    contentDescription = "Update available",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = context.getString(R.string.updates_available),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.updates_version_prefix, latestVersion?.versionName ?: ""),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.updates_released_prefix, latestVersion?.releaseDate ?: ""),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                latestVersion?.let { version ->
                                    if (version.apkSize > 0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = RhythmIcons.Download,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = version.apkAssetName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "(${updaterViewModel.getReadableFileSize(version.apkSize)})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isDownloading) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = context.getString(R.string.updates_downloading),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { downloadProgress / 100f },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "${downloadProgress.toInt()}%",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedButton(
                                            onClick = { updaterViewModel.cancelDownload() },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Cancel,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(context.getString(R.string.updates_cancel_download))
                                        }
                                    }
                                } else if (downloadedFile != null) {
                                    Icon(
                                        imageVector = RhythmIcons.Check,
                                        contentDescription = null,
                                        
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = context.getString(R.string.updates_download_complete),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = downloadedFile?.name ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { updaterViewModel.installDownloadedApk() },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = RhythmIcons.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(context.getString(R.string.updates_install_update))
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { updaterViewModel.downloadUpdate() },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = RhythmIcons.Download,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(context.getString(R.string.updates_download_update))
                                        }
                                    }
                                }
                            }

                            !isCheckingForUpdates && error == null -> {
                                if (!autoCheckForUpdates) {
                                    Icon(
                                        imageVector = RhythmIcons.Refresh,
                                        contentDescription = context.getString(R.string.updates_manual_check),
                                        
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = context.getString(R.string.updates_manual_check),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = context.getString(R.string.updates_auto_disabled),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = { updaterViewModel.checkForUpdates(force = true) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Search,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Check Now")
                                        }
                                        OutlinedButton(
                                            onClick = { appSettings.setAutoCheckForUpdates(true) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Autorenew,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(context.getString(R.string.updates_enable_auto_check))
                                        }
                                    }
                                } else {
                                    Icon(
                                        imageVector = RhythmIcons.Check,
                                        contentDescription = context.getString(R.string.updates_up_to_date),
                                        
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = context.getString(R.string.updates_up_to_date_message),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { updaterViewModel.checkForUpdates(force = true) },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(context.getString(R.string.updates_check_again))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // What's New section
            item {
                AnimatedVisibility(
                    visible = updatesEnabled && latestVersion != null && whatsNew.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = context.getString(R.string.updates_whats_new),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
                                whatsNew.forEachIndexed { index, change ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .padding(top = 8.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        AndroidView(
                                            modifier = Modifier.fillMaxWidth(),
                                            factory = { ctx ->
                                                TextView(ctx).apply {
                                                    setTextColor(onSurfaceColor)
                                                }
                                            },
                                            update = { textView ->
                                                textView.text = HtmlCompat.fromHtml(change, HtmlCompat.FROM_HTML_MODE_COMPACT)
                                                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
                                            }
                                        )
                                    }
                                    if (index < whatsNew.size - 1) {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Known Issues section
            item {
                AnimatedVisibility(
                    visible = updatesEnabled && latestVersion != null && knownIssues.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = context.getString(R.string.updates_known_issues),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
                                knownIssues.forEachIndexed { index, issue ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .padding(top = 8.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.error,
                                                    CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        AndroidView(
                                            modifier = Modifier.fillMaxWidth(),
                                            factory = { ctx ->
                                                TextView(ctx).apply {
                                                    setTextColor(onSurfaceColor)
                                                }
                                            },
                                            update = { textView ->
                                                textView.text = HtmlCompat.fromHtml(issue, HtmlCompat.FROM_HTML_MODE_COMPACT)
                                                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
                                            }
                                        )
                                    }
                                    if (index < knownIssues.size - 1) {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Settings Section below
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.updates_settings),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        TunerSettingRow(
                            item = SettingItem(
                                Icons.Default.SystemUpdate,
                                context.getString(R.string.updates_enable),
                                context.getString(R.string.updates_enable_updates),
                                toggleState = updatesEnabled,
                                onToggleChange = { appSettings.setUpdatesEnabled(it) }
                            )
                        )

                        AnimatedVisibility(
                            visible = updatesEnabled,
                            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                        ) {
                            Column {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Default.Update,
                                        context.getString(R.string.onboarding_periodic_check_title),
                                        context.getString(R.string.onboarding_periodic_check_desc),
                                        toggleState = autoCheckForUpdates,
                                        onToggleChange = { appSettings.setAutoCheckForUpdates(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Default.Notifications,
                                        context.getString(R.string.onboarding_update_notifications_title),
                                        context.getString(R.string.onboarding_update_notifications_desc),
                                        toggleState = updateNotificationsEnabled,
                                        onToggleChange = { appSettings.setUpdateNotificationsEnabled(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Default.CloudSync,
                                        context.getString(R.string.onboarding_smart_polling_title),
                                        context.getString(R.string.onboarding_smart_polling_desc),
                                        toggleState = useSmartUpdatePolling,
                                        onToggleChange = { appSettings.setUseSmartUpdatePolling(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Default.Category,
                                        context.getString(R.string.updates_channel_title),
                                        "${updateChannel.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} - Tap to change",
                                        onClick = { showChannelDialog = true }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Default.Schedule,
                                        context.getString(R.string.updates_check_interval_title),
                                        context.getString(R.string.updates_check_frequency),
                                        onClick = { showIntervalDialog = true }
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Informational card about smart polling
            item {
                AnimatedVisibility(
                    visible = updatesEnabled && useSmartUpdatePolling,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.updates_smart_polling),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.updates_smart_polling_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Update Channel Dialog
    if (showChannelDialog) {
        AlertDialog(
            onDismissRequest = { showChannelDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text(context.getString(R.string.updates_channel_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = context.getString(R.string.updates_channel_desc),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val channels = listOf(
                        "stable" to context.getString(R.string.updates_channel_desc),
                        "beta" to context.getString(R.string.updates_experimental_coming)
                    )

                    channels.forEach { (channel, description) ->
                        Card(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                appSettings.setUpdateChannel(channel)
                                showChannelDialog = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (updateChannel == channel)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = channel.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (updateChannel == channel) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showChannelDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.ui_close))
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Update Check Interval Dialog
    if (showIntervalDialog) {
        AlertDialog(
            onDismissRequest = { showIntervalDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text(context.getString(R.string.updates_check_interval_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = context.getString(R.string.updates_check_frequency),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val intervals = listOf(
                        1 to context.getString(R.string.updates_check_frequency),
                        6 to "6h",
                        12 to "12h",
                        24 to "24h",
                        168 to "168h"
                    )

                    intervals.forEach { (hours, label) ->
                        Card(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                appSettings.setUpdateCheckIntervalHours(hours)
                                showIntervalDialog = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (updateCheckIntervalHours == hours)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (updateCheckIntervalHours == hours) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                if (updateCheckIntervalHours == hours) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showIntervalDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ExperimentalFeaturesScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val hapticFeedbackEnabled by appSettings.hapticFeedbackEnabled.collectAsState()
    val showLyrics by appSettings.showLyrics.collectAsState()
    val ignoreMediaStoreCovers by appSettings.ignoreMediaStoreCovers.collectAsState()
    val losslessArtwork by appSettings.losslessArtwork.collectAsState()
    val appMode by appSettings.appMode.collectAsState()
    val allowCellularStreaming by appSettings.allowCellularStreaming.collectAsState()
    val bitPerfectMode by appSettings.bitPerfectMode.collectAsState()
    val haptic = LocalHapticFeedback.current
    
    // Third-party integrations states
    val scrobblingEnabled by appSettings.scrobblingEnabled.collectAsState()
    val discordRichPresenceEnabled by appSettings.discordRichPresenceEnabled.collectAsState()
    val broadcastStatusEnabled by appSettings.broadcastStatusEnabled.collectAsState()
    
    val forcePlayerCompactMode by appSettings.forcePlayerCompactMode.collectAsState()
    
    val updaterViewModel: AppUpdaterViewModel = viewModel()
    val latestVersion by updaterViewModel.latestVersion.collectAsState()

    var showAppModeDialog by remember { mutableStateOf(false) }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_experimental),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        val settingGroups = buildList {
            // Music Source settings group
            add(
                SettingGroup(
                    title = context.getString(R.string.exp_music_source),
                    items = listOf(
                        SettingItem(
                            Icons.Default.Storage,
                            context.getString(R.string.exp_music_mode),
                            if (appMode == "LOCAL") context.getString(R.string.exp_local_files_desc) else context.getString(R.string.exp_streaming_desc),
                            onClick = { showAppModeDialog = true }
                        ),
                        SettingItem(
                            Icons.Default.Public,
                            context.getString(R.string.exp_cellular_streaming),
                            context.getString(R.string.exp_cellular_streaming_desc),
                            toggleState = allowCellularStreaming,
                            onToggleChange = { appSettings.setAllowCellularStreaming(it) }
                        ).takeIf { appMode == "STREAMING" } ?: SettingItem(
                            Icons.Default.Info,
                            context.getString(R.string.exp_note),
                            context.getString(R.string.exp_streaming_coming_soon)
                        )
                    )
                )
            )
            
            // Developer/Debugging features group
            add(
                SettingGroup(
                    title = context.getString(R.string.exp_developer_debugging),
                    items = listOf(
                        SettingItem(
                            Icons.Default.Code,
                            context.getString(R.string.exp_codec_monitoring),
                            context.getString(R.string.exp_codec_monitoring_desc),
                            toggleState = appSettings.codecMonitoringEnabled.collectAsState().value,
                            onToggleChange = { appSettings.setCodecMonitoringEnabled(it) }
                        ),
                        SettingItem(
                            Icons.Default.Headphones,
                            context.getString(R.string.exp_audio_device_logging),
                            context.getString(R.string.exp_audio_device_logging_desc),
                            toggleState = appSettings.audioDeviceLoggingEnabled.collectAsState().value,
                            onToggleChange = { appSettings.setAudioDeviceLoggingEnabled(it) }
                        ),
                        SettingItem(
                            Icons.Default.RestartAlt,
                            context.getString(R.string.exp_launch_onboarding),
                            context.getString(R.string.exp_launch_onboarding_desc),
                            onClick = { appSettings.setOnboardingCompleted(false) }
                        ),
                        SettingItem(
                            Icons.Default.BugReport,
                            context.getString(R.string.exp_test_crash),
                            context.getString(R.string.exp_test_crash_desc),
                            onClick = { chromahub.rhythm.app.util.CrashReporter.testCrash() }
                        ),
                        SettingItem(
                            Icons.Default.Smartphone,
                            context.getString(R.string.exp_force_player_compact_mode),
                            context.getString(R.string.exp_force_player_compact_mode_desc),
                            toggleState = forcePlayerCompactMode,
                            onToggleChange = { appSettings.setForcePlayerCompactMode(it) }
                        )
                    )
                )
            )
            
            // Third-Party Integrations group
            add(
                SettingGroup(
                    title = context.getString(R.string.exp_third_party_integrations),
                    items = listOf(
                        SettingItem(
                            Icons.Default.MusicNote,
                            context.getString(R.string.scrobbling_enabled),
                            context.getString(R.string.scrobbling_desc),
                            toggleState = scrobblingEnabled,
                            onToggleChange = { appSettings.setScrobblingEnabled(it) }
                        ),
                        SettingItem(
                            Icons.Default.Forum,
                            context.getString(R.string.discord_enabled),
                            context.getString(R.string.discord_desc),
                            toggleState = discordRichPresenceEnabled,
                            onToggleChange = { appSettings.setDiscordRichPresenceEnabled(it) }
                        ),
                        SettingItem(
                            Icons.Default.Wifi,
                            context.getString(R.string.broadcast_status_enabled),
                            context.getString(R.string.broadcast_status_desc),
                            toggleState = broadcastStatusEnabled,
                            onToggleChange = { appSettings.setBroadcastStatusEnabled(it) }
                        )
                    )
                )
            )
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            items(settingGroups, key = { "setting_${it.title}_${settingGroups.indexOf(it)}" }) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        group.items.forEachIndexed { index, item ->
                            TunerSettingRow(item = item)
                            if (index < group.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(R.string.updates_experimental_coming),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
    
    if (showAppModeDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        
        ModalBottomSheet(
            onDismissRequest = { showAppModeDialog = false },
            sheetState = sheetState,
            dragHandle = { 
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.exp_music_mode),
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
                                text = context.getString(R.string.exp_choose_music_source),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Local mode option
                Card(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        appSettings.setAppMode("LOCAL")
                        showAppModeDialog = false
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (appMode == "LOCAL") 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = if (appMode == "LOCAL")
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = context.getString(R.string.exp_local_files),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (appMode == "LOCAL") 
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = context.getString(R.string.exp_local_files_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (appMode == "LOCAL") 
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (appMode == "LOCAL") {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // Streaming mode option
                Card(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        appSettings.setAppMode("STREAMING")
                        showAppModeDialog = false
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (appMode == "STREAMING") 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = if (appMode == "STREAMING")
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = context.getString(R.string.exp_streaming_services),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (appMode == "STREAMING") 
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = context.getString(R.string.exp_streaming_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (appMode == "STREAMING") 
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (appMode == "STREAMING") {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Show update bottomsheet - removed, now handled globally in LocalNavigation
}

/**
 * Toggle card for individual decoration elements
 */
@Composable
private fun DecorationToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon with background
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isEnabled)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isEnabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Toggle switch
            TunerAnimatedSwitch(
                checked = isEnabled,
                onCheckedChange = {
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                    onToggle(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FestivalSelectionBottomSheet(
    currentFestival: String,
    onDismiss: () -> Unit,
    onFestivalSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.primary
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        val contentHorizontalPadding = 24.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 0.dp)
                .padding(bottom = 24.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            StandardBottomSheetHeader(
                title = context.getString(R.string.settings_select_festival),
                subtitle = context.getString(R.string.settings_choose_festive_theme),
                visible = showContent,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Festival Options
            val festivals = listOf(
                Triple("CHRISTMAS", context.getString(R.string.settings_festival_christmas), Icons.Default.AcUnit),
                Triple("NEW_YEAR", context.getString(R.string.settings_festival_new_year), Icons.Default.Celebration)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = contentHorizontalPadding)
            ) {
                festivals.forEach { (value, name, icon) ->
                    val isSelected = currentFestival == value
                    val isAvailable = true

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            if (isAvailable) {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                onFestivalSelected(value)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = when {
                                        !isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                        ),
                                        color = when {
                                            !isAvailable -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = context.getString(R.string.ui_selected),
                                    
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = contentHorizontalPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = context.getString(R.string.settings_more_festivals),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun getFestivalDisplayName(festivalType: String): String {
    return when (festivalType) {
        "CHRISTMAS" -> "Christmas"
        "NEW_YEAR" -> "New Year"
        "NONE" -> "None"
        "CUSTOM" -> "Custom"
        else -> "Not selected"
    }
}

//            item { Spacer(modifier = Modifier.height(40.dp)) }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsSourceDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    context: Context,
    haptic: HapticFeedback
) {
    val lyricsSourcePreference by appSettings.lyricsSourcePreference.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.primary
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {

                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp, vertical = 16.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = context.getString(R.string.lyrics_source_priority),
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
                                    text = context.getString(R.string.lyrics_choose_source),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

            // Options
            val sourceOptions = listOf(
                chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.EMBEDDED_FIRST to Triple(
                    "Embedded First",
                    "Try audio file metadata → Online APIs → .lrc files",
                    Icons.Default.MusicNote
                ),
                chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.API_FIRST to Triple(
                    "API First",
                    "Try online services → Audio metadata → .lrc files",
                    Icons.Default.CloudDownload
                ),
                chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.LOCAL_FIRST to Triple(
                    "Local .lrc First",
                    "Try .lrc files → Audio metadata → Online APIs",
                    Icons.Default.Folder
                )
            )

            sourceOptions.forEach { (preference, info) ->
                val (title, description, icon) = info
                val isSelected = lyricsSourcePreference == preference

                Card(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        appSettings.setLyricsSourcePreference(preference)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = context.getString(R.string.lyrics_embedded_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibrarySettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)

    val enableRatingSystem by appSettings.enableRatingSystem.collectAsState()
    val groupByAlbumArtist by appSettings.groupByAlbumArtist.collectAsState()
    val ignoreMediaStoreCovers by appSettings.ignoreMediaStoreCovers.collectAsState()
    val losslessArtwork by appSettings.losslessArtwork.collectAsState()
    val albumBottomSheetGradientBlur by appSettings.albumBottomSheetGradientBlur.collectAsState()

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_library_settings),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        val settingGroups = listOf(
            SettingGroup(
                title = context.getString(R.string.settings_library_group_organization),
                items = listOf(
                    SettingItem(
                        Icons.Default.Star,
                        context.getString(R.string.settings_song_ratings),
                        context.getString(R.string.settings_song_ratings_desc),
                        toggleState = enableRatingSystem,
                        onToggleChange = { appSettings.setEnableRatingSystem(it) }
                    ),
                    SettingItem(
                        Icons.Default.Person,
                        context.getString(R.string.settings_group_by_album_artist),
                        context.getString(R.string.settings_group_by_album_artist_desc),
                        toggleState = groupByAlbumArtist,
                        onToggleChange = { appSettings.setGroupByAlbumArtist(it) }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_library_group_artwork),
                items = listOf(
                    SettingItem(
                        RhythmIcons.Album,
                        context.getString(R.string.settings_ignore_mediastore_covers),
                        context.getString(R.string.settings_ignore_mediastore_covers_desc),
                        toggleState = ignoreMediaStoreCovers,
                        onToggleChange = { appSettings.setIgnoreMediaStoreCovers(it) }
                    ),
                    SettingItem(
                        Icons.Default.MusicNote,
                        context.getString(R.string.settings_lossless_artwork),
                        context.getString(R.string.settings_lossless_artwork_desc),
                        toggleState = losslessArtwork,
                        onToggleChange = { appSettings.setLosslessArtwork(it) }
                    ),
                    SettingItem(
                        Icons.Default.LensBlur,
                        context.getString(R.string.settings_album_bottom_sheet_gradient_blur),
                        context.getString(R.string.settings_album_bottom_sheet_gradient_blur_desc),
                        toggleState = albumBottomSheetGradientBlur,
                        onToggleChange = { appSettings.setAlbumBottomSheetGradientBlur(it) }
                    )
                )
            )
        )

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            items(settingGroups, key = { "libsettings_${it.title}" }) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        group.items.forEachIndexed { index, item ->
                            TunerSettingRow(item = item)
                            if (index < group.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun RhythmGuardSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)

    val auraMode by appSettings.rhythmGuardMode.collectAsState()
    val auraAge by appSettings.rhythmGuardAge.collectAsState()
    val manualWarningsEnabled by appSettings.rhythmGuardManualWarningsEnabled.collectAsState()
    val manualVolumeThreshold by appSettings.rhythmGuardManualVolumeThreshold.collectAsState()
    val alertThresholdMinutes by appSettings.rhythmGuardAlertThresholdMinutes.collectAsState()
    val warningTimeoutMinutes by appSettings.rhythmGuardWarningTimeoutMinutes.collectAsState()
    val breakResumeMinutes by appSettings.rhythmGuardBreakResumeMinutes.collectAsState()

    val dailyListeningStats by appSettings.dailyListeningStats.collectAsState()

    val stopPlaybackOnZeroVolume by appSettings.stopPlaybackOnZeroVolume.collectAsState()

    val currentSystemVolume = rememberSystemMusicVolumeFraction(context)
    val playbackStatsRepository = remember(context) { PlaybackStatsRepository.getInstance(context) }

    var todayExposureMs by remember { mutableLongStateOf(0L) }
    var weeklyAverageSessions by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(dailyListeningStats) {
        val todaySummary = runCatching {
            playbackStatsRepository.loadSummary(StatsTimeRange.TODAY)
        }.getOrNull()
        val weekSummary = runCatching {
            playbackStatsRepository.loadSummary(StatsTimeRange.WEEK)
        }.getOrNull()

        todayExposureMs = todaySummary?.totalDurationMs ?: 0L
        weeklyAverageSessions = weekSummary?.averageSessionsPerDay
            ?: rhythmGuardWeeklyAverageSessions(dailyListeningStats)
    }

    val activePolicy = remember(auraAge) { appSettings.getRhythmGuardPolicy(auraAge) }
    val policyTable = remember { appSettings.getRhythmGuardPolicyBands() }
    val isRhythmGuardEnabled = auraMode != AppSettings.RHYTHM_GUARD_MODE_OFF
    val recommendedVolumeThreshold = activePolicy.maxVolumeThreshold
    val recommendedDailyMinutes = activePolicy.recommendedDailyMinutes
    val effectiveExposureLimitMinutes = if (auraMode == AppSettings.RHYTHM_GUARD_MODE_AUTO) {
        recommendedDailyMinutes
    } else if (alertThresholdMinutes > 0) {
        alertThresholdMinutes
    } else {
        recommendedDailyMinutes
    }
    val totalExposureMinutes = (todayExposureMs / 60000L).toInt().coerceAtLeast(0)

    val currentVolumePercent = (currentSystemVolume * 100f).toInt().coerceIn(0, 100)
    val manualThresholdPercent = (manualVolumeThreshold * 100f).toInt().coerceIn(0, 100)
    val recommendedThresholdPercent = (recommendedVolumeThreshold * 100f).toInt().coerceIn(0, 100)
    val formattedTotalExposure = remember(todayExposureMs) {
        rhythmGuardFormatDurationFromMillis(todayExposureMs)
    }
    val formattedDailyTarget = remember(effectiveExposureLimitMinutes) {
        rhythmGuardFormatDurationFromMinutes(effectiveExposureLimitMinutes)
    }
    val formattedTimeout = remember(warningTimeoutMinutes) {
        rhythmGuardFormatDurationFromMinutes(warningTimeoutMinutes)
    }
    val formattedResumeInterval = remember(breakResumeMinutes) {
        rhythmGuardFormatDurationFromMinutes(breakResumeMinutes)
    }
    val activeVolumeThreshold = if (auraMode == AppSettings.RHYTHM_GUARD_MODE_AUTO) {
        recommendedVolumeThreshold
    } else {
        manualVolumeThreshold
    }
    val activeThresholdPercent = if (auraMode == AppSettings.RHYTHM_GUARD_MODE_AUTO) {
        recommendedThresholdPercent
    } else {
        manualThresholdPercent
    }

    val showVolumeWarning = isRhythmGuardEnabled &&
        auraMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL &&
        manualWarningsEnabled &&
        currentSystemVolume > manualVolumeThreshold
    val showExposureWarning = isRhythmGuardEnabled &&
        auraMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL &&
        totalExposureMinutes > effectiveExposureLimitMinutes
    val volumeLoadRatio = (currentSystemVolume / maxOf(activeVolumeThreshold, 0.01f)).coerceAtLeast(0f)
    val exposureLoadRatio = (totalExposureMinutes / maxOf(effectiveExposureLimitMinutes, 1).toFloat()).coerceAtLeast(0f)
    val sessionLoadRatio = (weeklyAverageSessions / 8f).coerceAtLeast(0f)
    val healthRiskScore = if (isRhythmGuardEnabled) {
        (
            (volumeLoadRatio * 0.45f) +
                (exposureLoadRatio * 0.45f) +
                (sessionLoadRatio * 0.10f) +
                if (auraMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL && !manualWarningsEnabled) 0.08f else 0f
            ).coerceIn(0f, 1.5f)
    } else {
        0f
    }
    val overallHealthLevel = when {
        !isRhythmGuardEnabled -> RhythmGuardOverallHealthLevel.OFF
        healthRiskScore < 0.72f -> RhythmGuardOverallHealthLevel.GOOD
        healthRiskScore < 1.0f -> RhythmGuardOverallHealthLevel.FAIR
        else -> RhythmGuardOverallHealthLevel.RISK
    }
    val overallHealthProgress = if (isRhythmGuardEnabled) {
        (1f - healthRiskScore.coerceIn(0f, 1f)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val formattedWeeklyDensity = remember(weeklyAverageSessions) {
        String.format(Locale.getDefault(), "%.1f", weeklyAverageSessions)
    }
    val overallHealthSummary = remember(
        currentVolumePercent,
        activeThresholdPercent,
        formattedTotalExposure,
        formattedDailyTarget,
        formattedWeeklyDensity
    ) {
        context.getString(
            R.string.settings_rhythm_guard_overall_health_summary,
            currentVolumePercent,
            activeThresholdPercent,
            formattedTotalExposure,
            formattedDailyTarget,
            formattedWeeklyDensity
        )
    }

    LaunchedEffect(auraMode, auraAge) {
        if (auraMode == AppSettings.RHYTHM_GUARD_MODE_AUTO) {
            appSettings.applyRhythmGuardAutoProfileForAge(auraAge)
        }
    }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_rhythm_guard),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.settings_rhythm_guard_health_overview_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = context.getString(R.string.settings_rhythm_guard_health_overview_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        RhythmGuardOverallHealthCard(
                            level = overallHealthLevel,
                            progress = overallHealthProgress,
                            summary = overallHealthSummary,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Hero-style metrics inspired by Pixel Player stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RhythmGuardHeroCard(
                                title = context.getString(R.string.settings_rhythm_guard_snapshot_exposure_title),
                                value = formattedTotalExposure,
                                subtitle = "/$formattedDailyTarget",
                                progress = (totalExposureMinutes / maxOf(effectiveExposureLimitMinutes, 1).toFloat()).coerceIn(0f, 1f),
                                icon = Icons.Default.Schedule,
                                isWarning = showExposureWarning,
                                modifier = Modifier.weight(1f)
                            )

                            RhythmGuardHeroCard(
                                title = context.getString(R.string.settings_rhythm_guard_snapshot_volume_title),
                                value = "$currentVolumePercent%",
                                subtitle = "of ${activeThresholdPercent}%",
                                progress = (currentSystemVolume / maxOf(activeVolumeThreshold, 0.01f)).coerceIn(0f, 1f),
                                icon = Icons.Default.GraphicEq,
                                isWarning = showVolumeWarning,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = context.getString(R.string.settings_rhythm_guard_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(34.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                tonalElevation = 0.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.settings_rhythm_guard_mode_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = context.getString(R.string.settings_rhythm_guard_mode_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isRhythmGuardEnabled) {
                                        context.getString(R.string.label_enabled)
                                    } else {
                                        context.getString(R.string.label_disabled)
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            TunerAnimatedSwitch(
                                checked = isRhythmGuardEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        val restoredMode = if (auraMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL) {
                                            AppSettings.RHYTHM_GUARD_MODE_MANUAL
                                        } else {
                                            AppSettings.RHYTHM_GUARD_MODE_AUTO
                                        }
                                        appSettings.setRhythmGuardMode(restoredMode)
                                    } else {
                                        appSettings.setRhythmGuardMode(AppSettings.RHYTHM_GUARD_MODE_OFF)
                                    }
                                }
                            )
                        }

                        if (isRhythmGuardEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))

                            ExpressiveButtonGroup(
                                items = listOf(
                                    context.getString(R.string.settings_rhythm_guard_mode_auto),
                                    context.getString(R.string.settings_rhythm_guard_mode_manual)
                                ),
                                selectedIndex = if (auraMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL) 1 else 0,
                                onItemClick = { index ->
                                    when (index) {
                                        0 -> appSettings.setRhythmGuardMode(AppSettings.RHYTHM_GUARD_MODE_AUTO)
                                        else -> appSettings.setRhythmGuardMode(AppSettings.RHYTHM_GUARD_MODE_MANUAL)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            if (isRhythmGuardEnabled) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.settings_rhythm_guard_age_label, auraAge),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = context.getString(
                                        R.string.settings_rhythm_guard_age_desc,
                                        recommendedThresholdPercent,
                                        recommendedDailyMinutes
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Slider(
                            value = auraAge.toFloat(),
                            onValueChange = { appSettings.setRhythmGuardAge(it.toInt()) },
                            valueRange = 8f..80f,
                            steps = 71
                        )
                    }
                }
            }

            if (auraMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL) {
                item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = context.getString(R.string.settings_rhythm_guard_alert_controls_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Threshold control
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = context.getString(
                                    R.string.settings_rhythm_guard_alert_threshold_title,
                                    if (alertThresholdMinutes > 0) {
                                        rhythmGuardFormatDurationFromMinutes(alertThresholdMinutes)
                                    } else {
                                        context.getString(R.string.settings_rhythm_guard_alert_threshold_policy_default)
                                    }
                                ),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(-1, 60, 90, 120).forEach { option ->
                                    FilterChip(
                                        selected = alertThresholdMinutes == option,
                                        onClick = { appSettings.setRhythmGuardAlertThresholdMinutes(option) },
                                        label = {
                                            Text(
                                                if (option > 0) {
                                                    rhythmGuardFormatDurationFromMinutes(option)
                                                } else {
                                                    context.getString(R.string.settings_rhythm_guard_alert_threshold_policy_default)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                            Slider(
                                value = maxOf(alertThresholdMinutes, 15).toFloat(),
                                onValueChange = { appSettings.setRhythmGuardAlertThresholdMinutes(it.toInt()) },
                                valueRange = 15f..360f,
                                steps = 344
                            )
                        }

                        // Timeout control
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = context.getString(
                                    R.string.settings_rhythm_guard_alert_timeout_title,
                                    formattedTimeout
                                ),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(2, 5, 10, 15).forEach { option ->
                                    FilterChip(
                                        selected = warningTimeoutMinutes == option,
                                        onClick = { appSettings.setRhythmGuardWarningTimeoutMinutes(option) },
                                        label = { Text(rhythmGuardFormatDurationFromMinutes(option)) }
                                    )
                                }
                            }
                            Slider(
                                value = warningTimeoutMinutes.toFloat(),
                                onValueChange = { appSettings.setRhythmGuardWarningTimeoutMinutes(it.toInt()) },
                                valueRange = 1f..30f,
                                steps = 28
                            )
                        }

                        // Break interval control
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = context.getString(
                                    R.string.settings_rhythm_guard_break_resume_default_title,
                                    formattedResumeInterval
                                ),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(10, 15, 30, 60).forEach { option ->
                                    FilterChip(
                                        selected = breakResumeMinutes == option,
                                        onClick = { appSettings.setRhythmGuardBreakResumeMinutes(option) },
                                        label = { Text(rhythmGuardFormatDurationFromMinutes(option)) }
                                    )
                                }
                            }
                            Slider(
                                value = breakResumeMinutes.toFloat(),
                                onValueChange = { appSettings.setRhythmGuardBreakResumeMinutes(it.toInt()) },
                                valueRange = 1f..120f,
                                steps = 118
                            )
                        }
                    }
                }
            }
            }

            if (auraMode == AppSettings.RHYTHM_GUARD_MODE_AUTO) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = context.getString(R.string.settings_rhythm_guard_auto_policy_table_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            policyTable.forEachIndexed { index, band ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = context.getString(
                                            R.string.settings_rhythm_guard_auto_policy_band,
                                            band.minAge,
                                            band.maxAge
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = context.getString(
                                            R.string.settings_rhythm_guard_auto_policy_value,
                                            (band.maxVolumeThreshold * 100f).toInt(),
                                            band.recommendedDailyMinutes
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (auraAge in band.minAge..band.maxAge) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        fontWeight = if (auraAge in band.minAge..band.maxAge) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )
                                }
                                if (index < policyTable.lastIndex) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }

            if (auraMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL) {
                item {
                    val manualSettingItems = listOf(
                        SettingItem(
                            Icons.Default.Warning,
                            context.getString(R.string.settings_rhythm_guard_manual_warning_toggle),
                            context.getString(R.string.settings_rhythm_guard_manual_warning_toggle_desc),
                            toggleState = manualWarningsEnabled,
                            onToggleChange = { appSettings.setRhythmGuardManualWarningsEnabled(it) }
                        ),
                        SettingItem(
                            Icons.Default.Stop,
                            context.getString(R.string.settings_stop_playback_on_zero_volume),
                            context.getString(R.string.settings_stop_playback_on_zero_volume_desc),
                            toggleState = stopPlaybackOnZeroVolume,
                            onToggleChange = { appSettings.setStopPlaybackOnZeroVolume(it) }
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = context.getString(R.string.settings_rhythm_guard_manual_threshold_title, manualThresholdPercent),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = context.getString(R.string.settings_rhythm_guard_manual_threshold_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Slider(
                                    value = manualVolumeThreshold,
                                    onValueChange = { appSettings.setRhythmGuardManualVolumeThreshold(it) },
                                    valueRange = 0.40f..0.95f
                                )
                            }
                        }

                        Text(
                            text = context.getString(R.string.settings_rhythm_guard_manual_controls_title),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                manualSettingItems.forEachIndexed { index, item ->
                                    TunerSettingRow(item = item)
                                    if (index < manualSettingItems.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showVolumeWarning || showExposureWarning) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = context.getString(R.string.settings_rhythm_guard_warning_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when {
                                    showExposureWarning -> context.getString(
                                        R.string.settings_rhythm_guard_warning_daily_exposure,
                                        formattedTotalExposure,
                                        formattedDailyTarget
                                    )
                                    else -> context.getString(
                                        R.string.settings_rhythm_guard_warning_high_volume,
                                        activeThresholdPercent
                                    )
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

private fun rhythmGuardWeeklyAverageSessions(stats: Map<String, Long>): Float {
    if (stats.isEmpty()) return 0f
    val recentDays = stats.toList()
        .sortedByDescending { it.first }
        .take(7)
        .map { it.second }

    if (recentDays.isEmpty()) return 0f
    return recentDays.average().toFloat()
}

@Composable
private fun rememberSystemMusicVolumeFraction(context: Context): Float {
    var systemVolume by remember { mutableFloatStateOf(0f) }

    DisposableEffect(context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager

        fun refreshVolume() {
            val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
            val currentVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
            systemVolume = if (maxVolume > 0) currentVolume.toFloat() / maxVolume.toFloat() else 0f
        }

        refreshVolume()

        val observer = object : android.database.ContentObserver(
            android.os.Handler(android.os.Looper.getMainLooper())
        ) {
            override fun onChange(selfChange: Boolean) {
                refreshVolume()
            }
        }

        context.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            observer
        )

        onDispose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    return systemVolume
}

private fun rhythmGuardFormatDurationFromMinutes(minutes: Int): String {
    val safeMinutes = minutes.coerceAtLeast(0)
    val days = safeMinutes / (24 * 60)
    val hours = (safeMinutes % (24 * 60)) / 60
    val mins = safeMinutes % 60

    return when {
        days > 0 && hours > 0 && mins > 0 -> "${days}d ${hours}h ${mins}m"
        days > 0 && hours > 0 -> "${days}d ${hours}h"
        days > 0 && mins > 0 -> "${days}d ${mins}m"
        days > 0 -> "${days}d"
        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins}m"
    }
}

private fun rhythmGuardFormatDurationFromMillis(durationMs: Long): String {
    return rhythmGuardFormatDurationFromMinutes((durationMs / 60000L).toInt())
}

@Composable
private fun RhythmGuardOverviewGauge(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    progress: Float,
    isWarning: Boolean,
    icon: ImageVector
) {
    val progressValue = progress.coerceIn(0f, 1f)
    val progressPercent = (progressValue * 100f).toInt()
    val containerColor = if (isWarning) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = if (isWarning) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.14f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor.copy(alpha = 0.92f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = contentColor.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = if (isWarning) "Risk" else "Safe",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            LinearWavyProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(14.dp)),
                color = if (isWarning) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                trackColor = contentColor.copy(alpha = 0.16f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isWarning) {
                        stringResource(R.string.settings_rhythm_guard_snapshot_widget_above_limit)
                    } else {
                        stringResource(R.string.settings_rhythm_guard_snapshot_widget_within_limit)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun RhythmGuardMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    progress: Float,
    containerColor: Color,
    contentColor: Color
) {
    val progressValue = progress.coerceIn(0f, 1f)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.14f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor.copy(alpha = 0.92f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${(progressValue * 100f).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            LinearWavyProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(14.dp)),
                color = contentColor.copy(alpha = 0.78f),
                trackColor = contentColor.copy(alpha = 0.2f)
            )

            Text(
                text = stringResource(R.string.settings_rhythm_guard_snapshot_widget_load_label),
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.76f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RhythmGuardHeroCard(
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    icon: ImageVector,
    isWarning: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isWarning) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = if (isWarning) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .height(184.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor.copy(alpha = 0.8f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor.copy(alpha = 0.7f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearWavyProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            trackColor = contentColor.copy(alpha = 0.16f)
        )
    }
}

private enum class RhythmGuardOverallHealthLevel {
    OFF,
    GOOD,
    FAIR,
    RISK
}

@Composable
private fun RhythmGuardOverallHealthCard(
    level: RhythmGuardOverallHealthLevel,
    progress: Float,
    summary: String,
    modifier: Modifier = Modifier
) {
    val containerColor = when (level) {
        RhythmGuardOverallHealthLevel.GOOD -> MaterialTheme.colorScheme.primaryContainer
        RhythmGuardOverallHealthLevel.FAIR -> MaterialTheme.colorScheme.tertiaryContainer
        RhythmGuardOverallHealthLevel.RISK -> MaterialTheme.colorScheme.errorContainer
        RhythmGuardOverallHealthLevel.OFF -> MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = when (level) {
        RhythmGuardOverallHealthLevel.GOOD -> MaterialTheme.colorScheme.onPrimaryContainer
        RhythmGuardOverallHealthLevel.FAIR -> MaterialTheme.colorScheme.onTertiaryContainer
        RhythmGuardOverallHealthLevel.RISK -> MaterialTheme.colorScheme.onErrorContainer
        RhythmGuardOverallHealthLevel.OFF -> MaterialTheme.colorScheme.onSurface
    }
    val indicatorColor = when (level) {
        RhythmGuardOverallHealthLevel.GOOD -> MaterialTheme.colorScheme.primary
        RhythmGuardOverallHealthLevel.FAIR -> MaterialTheme.colorScheme.tertiary
        RhythmGuardOverallHealthLevel.RISK -> MaterialTheme.colorScheme.error
        RhythmGuardOverallHealthLevel.OFF -> MaterialTheme.colorScheme.outline
    }
    val statusLabel = when (level) {
        RhythmGuardOverallHealthLevel.GOOD -> stringResource(R.string.settings_rhythm_guard_health_good)
        RhythmGuardOverallHealthLevel.FAIR -> stringResource(R.string.settings_rhythm_guard_health_fair)
        RhythmGuardOverallHealthLevel.RISK -> stringResource(R.string.settings_rhythm_guard_health_risk)
        RhythmGuardOverallHealthLevel.OFF -> stringResource(R.string.settings_rhythm_guard_health_off)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.14f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.settings_rhythm_guard_overall_health_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = contentColor.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.86f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            LinearWavyProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = indicatorColor,
                trackColor = contentColor.copy(alpha = 0.18f)
            )
        }
    }
}
@Composable
fun CacheManagementSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val musicViewModel: MusicViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // Collect states
    val maxCacheSize by appSettings.maxCacheSize.collectAsState()
    val clearCacheOnExit by appSettings.clearCacheOnExit.collectAsState()

    // Local states
    var currentCacheSize by remember { mutableStateOf(0L) }
    var isCalculatingSize by remember { mutableStateOf(false) }
    var isClearingCache by remember { mutableStateOf(false) }
    var showCacheSizeDialog by remember { mutableStateOf(false) }
    var showClearCacheSuccess by remember { mutableStateOf(false) }
    var cacheDetails by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var isRebuildingRoom by remember { mutableStateOf(false) }
    var roomSongCount by remember { mutableStateOf(-1) }

    // Calculate cache size when the screen opens
    LaunchedEffect(Unit) {
        isCalculatingSize = true
        try {
            currentCacheSize = chromahub.rhythm.app.util.CacheManager.getCacheSize(context)
            cacheDetails = chromahub.rhythm.app.util.CacheManager.getDetailedCacheSize(context)
        } catch (e: Exception) {
            Log.e("CacheManagement", "Error calculating cache size", e)
        } finally {
            isCalculatingSize = false
        }
    }

    // Calculate storage backend stats
    LaunchedEffect(Unit) {
        try {
            val repo = musicViewModel.getMusicRepository()
            roomSongCount = try {
                repo.getRoomSongCount()
            } catch (_: Exception) { -1 }
        } catch (e: Exception) {
            Log.e("CacheManagement", "Error calculating storage stats", e)
        }
    }

    // Show cache size dialog
    if (showCacheSizeDialog) {
        CacheSizeDialog(
            currentSize = maxCacheSize,
            onDismiss = { showCacheSizeDialog = false },
            onSave = { size ->
                appSettings.setMaxCacheSize(size)
                showCacheSizeDialog = false
            }
        )
    }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_cache),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Current Cache Status
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PieChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.cache_current_status),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isCalculatingSize) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = context.getString(R.string.cache_calculating),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            // Total cache size
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = context.getString(R.string.cache_total_size),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = chromahub.rhythm.app.util.CacheManager.formatBytes(currentCacheSize),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Cache breakdown
                            cacheDetails.forEach { (label, size) ->
                                if (size > 0) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "  • $label:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = chromahub.rhythm.app.util.CacheManager.formatBytes(size),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Cache limit
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = context.getString(R.string.cache_limit),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format("%.1f", maxCacheSize / (1024f * 1024f))} MB",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Cache Settings
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.getString(R.string.settings_cache_settings),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingRow(
                            icon = Icons.Filled.DataUsage,
                            title = context.getString(R.string.cache_max_size),
                            description = "${String.format("%.1f", maxCacheSize / (1024f * 1024f))} MB",
                            onClick = { showCacheSizeDialog = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Filled.AutoDelete,
                            title = context.getString(R.string.cache_clear_on_exit),
                            description = context.getString(R.string.settings_cache_clear_on_exit_desc),
                            toggleState = clearCacheOnExit,
                            onToggleChange = { appSettings.setClearCacheOnExit(it) }
                        )
                    }
                }
            }

            // Cache Actions
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_cache_actions),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingRow(
                            icon = Icons.Filled.MusicNote,
                            title = context.getString(R.string.settings_clear_lyrics_cache),
                            description = context.getString(R.string.settings_clear_lyrics_cache_desc),
                            onClick = {
                                scope.launch {
                                    try {
                                        isClearingCache = true
                                        musicViewModel.clearLyricsCacheAndRefetch()
                                        currentCacheSize = chromahub.rhythm.app.util.CacheManager.getCacheSize(context)
                                        cacheDetails = chromahub.rhythm.app.util.CacheManager.getDetailedCacheSize(context)
                                        Toast.makeText(context, context.getString(R.string.settings_lyrics_cache_cleared), Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Log.e("CacheManagement", "Error clearing lyrics cache", e)
                                        Toast.makeText(context, context.getString(R.string.settings_lyrics_cache_clear_failed), Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isClearingCache = false
                                    }
                                }
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        try {
                                            isClearingCache = true
                                            chromahub.rhythm.app.util.CacheManager.clearAllCache(context, null)
                                            musicViewModel.getMusicRepository().clearInMemoryCaches()
                                            musicViewModel.getMusicRepository().clearSongCacheData()
                                            currentCacheSize = chromahub.rhythm.app.util.CacheManager.getCacheSize(context)
                                            cacheDetails = chromahub.rhythm.app.util.CacheManager.getDetailedCacheSize(context)
                                            showClearCacheSuccess = true
                                            Toast.makeText(context, context.getString(R.string.settings_all_cache_cleared), Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Log.e("CacheManagement", "Error clearing cache", e)
                                            Toast.makeText(context, context.getString(R.string.settings_cache_clear_failed), Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isClearingCache = false
                                        }
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = context.getString(R.string.settings_clear_all_cache),
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.settings_clear_all_cache),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = context.getString(R.string.settings_clear_all_cache_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isClearingCache) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Library Storage section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_cache_backend),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Room stats
                        SettingRow(
                            icon = Icons.Filled.TableChart,
                            title = context.getString(R.string.settings_storage_room_stats),
                            description = if (roomSongCount >= 0)
                                context.getString(R.string.settings_storage_song_count, roomSongCount)
                            else context.getString(R.string.settings_storage_not_available)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        // Rebuild Room Database
                        SettingRow(
                            icon = Icons.Filled.Sync,
                            title = context.getString(R.string.settings_storage_rebuild_room),
                            description = context.getString(R.string.settings_storage_rebuild_room_desc),
                            onClick = {
                                if (!isRebuildingRoom) {
                                    scope.launch {
                                        isRebuildingRoom = true
                                        try {
                                            musicViewModel.getMusicRepository().clearSongCacheData()
                                            musicViewModel.refreshLibrary()
                                            roomSongCount = musicViewModel.getMusicRepository().getRoomSongCount()
                                            Toast.makeText(context, context.getString(R.string.settings_storage_rebuild_success), Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Log.e("CacheManagement", "Error rebuilding Room DB", e)
                                            Toast.makeText(context, context.getString(R.string.settings_storage_rebuild_failed), Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isRebuildingRoom = false
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                if (isRebuildingRoom) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            // Information section
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.cache_about),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        listOf(
                            context.getString(R.string.settings_cache_info_1),
                            context.getString(R.string.settings_cache_info_2),
                            context.getString(R.string.settings_cache_info_3),
                            context.getString(R.string.settings_cache_info_4)
                        ).forEach { info ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FiberManualRecord,
                                    contentDescription = null,
                                    modifier = Modifier.size(8.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = info,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// CacheSizeDialog composable for setting maximum cache size
@Composable
fun CacheSizeDialog(
    currentSize: Long,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    // Convert size ranges to slider values (in MB)
    val minSize = 64L * 1024L * 1024L // 64 MB
    val maxSize = 2048L * 1024L * 1024L // 2 GB
    val stepSize = 64L * 1024L * 1024L // 64 MB steps

    // Current size in MB for slider
    val currentSizeMB = (currentSize / (1024L * 1024L)).coerceIn(64L, 2048L)
    var selectedSizeMB by remember { mutableFloatStateOf(currentSizeMB.toFloat()) }

    // Helper function to format size display
    fun formatSizeDisplay(sizeMB: Float): String {
        return when {
            sizeMB >= 1024f -> "${String.format("%.1f", sizeMB / 1024f)} GB"
            else -> "${sizeMB.toInt()} MB"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = context.getString(R.string.cache_max_size),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = context.getString(R.string.cache_max_size_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Current selection display
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = formatSizeDisplay(selectedSizeMB),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = context.getString(R.string.cache_size_limit),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Slider
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "64 MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "2 GB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = selectedSizeMB,
                        onValueChange = { newValue ->
                            // Snap to 64MB increments
                            val snappedValue =
                                ((newValue / 64f).toInt() * 64f).coerceIn(64f, 2048f)
                            selectedSizeMB = snappedValue
                            HapticUtils.performHapticFeedback(
                                context,
                                haptics,
                                HapticFeedbackType.TextHandleMove
                            )
                        },
                        valueRange = 64f..2048f,
                        steps = ((2048 - 64) / 64) - 1, // Number of steps between min and max
                        colors = androidx.compose.material3.SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick size options
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val quickSizes = listOf(128f, 256f, 512f, 1024f)
                    val quickLabels = listOf("128MB", "256MB", "512MB", "1GB")

                    quickSizes.forEachIndexed { index, size ->
                        Surface(
                            onClick = {
                                selectedSizeMB = size
                                HapticUtils.performHapticFeedback(
                                    context,
                                    haptics,
                                    HapticFeedbackType.TextHandleMove
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedSizeMB == size)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = quickLabels[index],
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedSizeMB == size)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    HapticUtils.performHapticFeedback(
                        context,
                        haptics,
                        HapticFeedbackType.LongPress
                    )
                    val sizeInBytes = selectedSizeMB.toLong() * 1024L * 1024L
                    onSave(sizeInBytes)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(context.getString(R.string.ui_save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(context.getString(R.string.ui_cancel))
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

// BackupInfoItem composable for displaying backup information
@Composable
private fun BackupInfoItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// Backup & Restore Screen (merged from BackupRestoreBottomSheet)
@Composable
fun BackupRestoreSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val musicViewModel: MusicViewModel = viewModel()

    // Collect states
    val autoBackupEnabled by appSettings.autoBackupEnabled.collectAsState()
    val lastBackupTimestamp by appSettings.lastBackupTimestamp.collectAsState()
    val backupLocation by appSettings.backupLocation.collectAsState()

    // Local states
    var isCreatingBackup by remember { mutableStateOf(false) }
    var isPreparingRestore by remember { mutableStateOf(false) }
    var isRestoringFromFile by remember { mutableStateOf(false) }
    var isRestoringFromClipboard by remember { mutableStateOf(false) }
    var showBackupSelectionSheet by remember { mutableStateOf(false) }
    var showRestoreSelectionSheet by remember { mutableStateOf(false) }
    var pendingRestorePayload by remember { mutableStateOf<String?>(null) }
    var pendingBackupSections by remember { mutableStateOf(AppSettings.BackupRestoreSections()) }
    var backupSections by remember { mutableStateOf(AppSettings.BackupRestoreSections()) }
    var restoreSections by remember { mutableStateOf(AppSettings.BackupRestoreSections()) }
    var resultSheetState by remember { mutableStateOf<BackupRestoreResultState?>(null) }

    val isBusy = isCreatingBackup || isPreparingRestore || isRestoringFromFile || isRestoringFromClipboard

    fun selectedSectionsSummary(sections: AppSettings.BackupRestoreSections): String {
        val lines = mutableListOf<String>()
        if (sections.includeGeneralSettings) lines += "• General app settings"
        if (sections.includeLibraryData) lines += "• Playlists, favorites, and folder lists"
        if (sections.includeStatsAndRhythmGuard) lines += "• Listening stats and Rhythm Guard data"
        return lines.joinToString("\n")
    }

    fun showError(message: String) {
        resultSheetState = BackupRestoreResultState(
            title = context.getString(R.string.ui_error),
            message = message,
            isError = true,
            requiresRestart = false
        )
    }

    // File picker launcher for backup export
    val backupLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    try {
                        isCreatingBackup = true
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)

                        musicViewModel.ensurePlaylistsSaved()
                        val backupJson = appSettings.createBackup(pendingBackupSections)

                        val outputStream = context.contentResolver.openOutputStream(uri)
                            ?: throw IllegalStateException("Unable to open backup destination")
                        outputStream.use { stream ->
                            stream.write(backupJson.toByteArray())
                            stream.flush()
                        }

                        appSettings.setLastBackupTimestamp(System.currentTimeMillis())
                        appSettings.setBackupLocation(uri.toString())

                        resultSheetState = BackupRestoreResultState(
                            title = context.getString(R.string.settings_backup_created),
                            message = "Backup completed successfully.\n\nIncluded sections:\n${selectedSectionsSummary(pendingBackupSections)}",
                            isError = false,
                            requiresRestart = false
                        )

                        // Also copy to clipboard
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Rhythm Backup", backupJson)
                        clipboard.setPrimaryClip(clip)
                    } catch (e: Exception) {
                        showError("Failed to create backup: ${e.message}")
                    } finally {
                        isCreatingBackup = false
                    }
                }
            } ?: run {
                isCreatingBackup = false
            }
        } else {
            isCreatingBackup = false
        }
    }

    // File picker launcher for restore
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    try {
                        isRestoringFromFile = true
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)

                        val inputStream = context.contentResolver.openInputStream(uri)
                        val backupJson = inputStream?.bufferedReader()?.use { it.readText() }

                        if (!backupJson.isNullOrEmpty()) {
                            pendingRestorePayload = backupJson
                            restoreSections = AppSettings.BackupRestoreSections()
                            showRestoreSelectionSheet = true
                        } else {
                            showError("Unable to read the backup file")
                        }
                    } catch (e: Exception) {
                        showError("Failed to restore from file: ${e.message}")
                    } finally {
                        isRestoringFromFile = false
                    }
                }
            } ?: run {
                isRestoringFromFile = false
            }
        } else {
            isRestoringFromFile = false
        }
    }

    // Restore from clipboard logic
    fun restoreFromClipboard() {
        scope.launch {
            try {
                isRestoringFromClipboard = true
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)

                // Get backup from clipboard
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val backupJson = clip.getItemAt(0).coerceToText(context)?.toString()
                    if (!backupJson.isNullOrBlank()) {
                        pendingRestorePayload = backupJson
                        restoreSections = AppSettings.BackupRestoreSections()
                        showRestoreSelectionSheet = true
                    } else {
                        showError("Clipboard does not contain readable backup text")
                    }
                } else {
                    showError("No backup data found in clipboard. Please copy a backup first.")
                }
            } catch (e: Exception) {
                showError("Failed to restore backup: ${e.message}")
            } finally {
                isRestoringFromClipboard = false
            }
        }
    }

    fun applyRestoreWithSections(backupJson: String, sections: AppSettings.BackupRestoreSections) {
        scope.launch {
            try {
                isPreparingRestore = true
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)

                if (appSettings.restoreFromBackup(backupJson, sections)) {
                    musicViewModel.reloadPlaylistsFromSettings()
                    resultSheetState = BackupRestoreResultState(
                        title = context.getString(R.string.settings_restore_completed),
                        message = "Restore completed successfully.\n\nRestored sections:\n${selectedSectionsSummary(sections)}",
                        isError = false,
                        requiresRestart = true
                    )
                } else {
                    showError("Invalid backup format, no sections selected, or corrupted data")
                }
            } catch (e: Exception) {
                showError("Failed to restore backup: ${e.message}")
            } finally {
                isPreparingRestore = false
            }
        }
    }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_backup_restore),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Status Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Last Backup Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (lastBackupTimestamp > 0)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (lastBackupTimestamp > 0) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (lastBackupTimestamp > 0)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (lastBackupTimestamp > 0) {
                                    val sdf = SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                                    sdf.format(Date(lastBackupTimestamp))
                                } else "Never",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (lastBackupTimestamp > 0)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = context.getString(R.string.last_backup),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (lastBackupTimestamp > 0)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    // Auto Backup Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (autoBackupEnabled)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (autoBackupEnabled) Icons.Filled.Autorenew else Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = if (autoBackupEnabled)
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (autoBackupEnabled) context.getString(R.string.settings_backup_enabled) else context.getString(R.string.settings_backup_manual),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (autoBackupEnabled)
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = context.getString(R.string.auto_backup),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (autoBackupEnabled)
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Backup location info if available
            backupLocation?.let { location ->
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = location.substringAfterLast("/"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            val settingGroups = listOf(
                SettingGroup(
                    title = context.getString(R.string.settings_backup_settings),
                    items = listOf(
                        SettingItem(
                            Icons.Default.Autorenew,
                            context.getString(R.string.settings_auto_backup),
                            context.getString(R.string.settings_auto_backup_desc),
                            toggleState = autoBackupEnabled,
                            onToggleChange = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                appSettings.setAutoBackupEnabled(it)
                                if (it) appSettings.triggerImmediateBackup()
                            }
                        )
                    )
                ),
                SettingGroup(
                    title = context.getString(R.string.settings_backup_actions),
                    items = listOf(
                        SettingItem(
                            Icons.Default.Save,
                            context.getString(R.string.settings_create_backup),
                            context.getString(R.string.settings_create_backup_desc),
                            onClick = {
                                if (!isBusy) {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                    backupSections = AppSettings.BackupRestoreSections()
                                    showBackupSelectionSheet = true
                                }
                            }
                        )
                    )
                ),
                SettingGroup(
                    title = context.getString(R.string.settings_restore_actions),
                    items = listOf(
                        SettingItem(
                            Icons.Default.ContentCopy,
                            context.getString(R.string.settings_restore_clipboard),
                            context.getString(R.string.settings_restore_clipboard_desc),
                            onClick = {
                                if (!isBusy) {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                    restoreFromClipboard()
                                }
                            }
                        ),
                        SettingItem(
                            Icons.Default.FolderOpen,
                            context.getString(R.string.settings_restore_file),
                            context.getString(R.string.settings_restore_file_desc),
                            onClick = {
                                if (!isBusy) {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                    isRestoringFromFile = true
                                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "application/json"
                                    }
                                    filePickerLauncher.launch(intent)
                                }
                            }
                        )
                    )
                )
            )

            items(settingGroups, key = { "setting_${it.title}_${settingGroups.indexOf(it)}" }) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        group.items.forEachIndexed { index, item ->
                            TunerSettingRow(item = item)
                            if (index < group.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }

            // Tips/Information Card
            item {
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
                                text = context.getString(R.string.backup_whats_included_placeholder),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        BackupInfoItem(
                            icon = Icons.Filled.Save,
                            text = context.getString(R.string.backup_all_settings_placeholder)
                        )
                        BackupInfoItem(
                            icon = Icons.Filled.RestoreFromTrash,
                            text = context.getString(R.string.backup_restore_tap_placeholder)
                        )
                        BackupInfoItem(
                            icon = Icons.Filled.Security,
                            text = context.getString(R.string.backup_local_storage_placeholder)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }

    if (showBackupSelectionSheet) {
        BackupRestoreSectionPickerBottomSheet(
            title = "Choose Backup Sections",
            subtitle = "Select what to include in this backup file.",
            confirmLabel = context.getString(R.string.settings_backup_action_short),
            confirmIcon = Icons.Default.Backup,
            sections = backupSections,
            isProcessing = isCreatingBackup,
            onSectionsChange = { backupSections = it },
            onDismiss = { showBackupSelectionSheet = false },
            onConfirm = { selectedSections ->
                if (!selectedSections.hasAtLeastOneSectionSelected) {
                    showError("Choose at least one section to create a backup")
                    return@BackupRestoreSectionPickerBottomSheet
                }

                pendingBackupSections = selectedSections
                showBackupSelectionSheet = false

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(
                        Intent.EXTRA_TITLE,
                        "rhythm_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(Date())}.json"
                    )
                }
                backupLocationLauncher.launch(intent)
            }
        )
    }

    if (showRestoreSelectionSheet && pendingRestorePayload != null) {
        BackupRestoreSectionPickerBottomSheet(
            title = "Choose Restore Sections",
            subtitle = "Select which sections from the backup should be restored.",
            confirmLabel = context.getString(R.string.settings_restore_action_short),
            confirmIcon = Icons.Default.SystemUpdateAlt,
            sections = restoreSections,
            isProcessing = isPreparingRestore,
            onSectionsChange = { restoreSections = it },
            onDismiss = {
                showRestoreSelectionSheet = false
                pendingRestorePayload = null
            },
            onConfirm = { selectedSections ->
                if (!selectedSections.hasAtLeastOneSectionSelected) {
                    showError("Choose at least one section to restore")
                    return@BackupRestoreSectionPickerBottomSheet
                }

                val backupJson = pendingRestorePayload ?: return@BackupRestoreSectionPickerBottomSheet
                showRestoreSelectionSheet = false
                pendingRestorePayload = null
                applyRestoreWithSections(backupJson, selectedSections)
            }
        )
    }

    resultSheetState?.let { state ->
        BackupRestoreResultBottomSheet(
            state = state,
            onDismiss = { resultSheetState = null },
            onPrimaryAction = {
                resultSheetState = null
                if (state.requiresRestart) {
                    val packageManager = context.packageManager
                    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                    val componentName = intent?.component
                    val mainIntent = Intent.makeRestartActivityTask(componentName)
                    context.startActivity(mainIntent)
                    (context as? Activity)?.finish()
                    Runtime.getRuntime().exit(0)
                }
            }
        )
    }
}

private data class BackupRestoreResultState(
    val title: String,
    val message: String,
    val isError: Boolean,
    val requiresRestart: Boolean
)

@Composable
private fun BackupRestoreSectionPickerBottomSheet(
    title: String,
    subtitle: String,
    confirmLabel: String,
    confirmIcon: ImageVector,
    sections: AppSettings.BackupRestoreSections,
    isProcessing: Boolean,
    onSectionsChange: (AppSettings.BackupRestoreSections) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (AppSettings.BackupRestoreSections) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var showContent by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "backup_restore_picker_alpha"
    )

    LaunchedEffect(Unit) {
        delay(80)
        showContent = true
    }

    val selectedSectionCount = listOf(
        sections.includeGeneralSettings,
        sections.includeLibraryData,
        sections.includeStatsAndRhythmGuard
    ).count { it }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            StandardBottomSheetHeader(
                title = title,
                subtitle = subtitle,
                visible = showContent,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Choose sections",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$selectedSectionCount of 3 enabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = {
                                onSectionsChange(
                                    sections.copy(
                                        includeGeneralSettings = true,
                                        includeLibraryData = true,
                                        includeStatsAndRhythmGuard = true
                                    )
                                )
                            },
                            enabled = !isProcessing,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text("All", style = MaterialTheme.typography.labelLarge)
                        }
                        FilledTonalButton(
                            onClick = {
                                onSectionsChange(
                                    sections.copy(
                                        includeGeneralSettings = false,
                                        includeLibraryData = false,
                                        includeStatsAndRhythmGuard = false
                                    )
                                )
                            },
                            enabled = !isProcessing,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text("None", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BackupRestoreSectionRow(
                    icon = Icons.Default.Settings,
                    title = "General Settings",
                    description = "Theme, player, UI, API, and app preferences.",
                    checked = sections.includeGeneralSettings,
                    badge = "Core",
                    onCheckedChange = { onSectionsChange(sections.copy(includeGeneralSettings = it)) }
                )

                BackupRestoreSectionRow(
                    icon = Icons.Default.LibraryMusic,
                    title = "Library Data",
                    description = "Playlists, favorites, blacklist/whitelist, pinned folders.",
                    checked = sections.includeLibraryData,
                    badge = "Collection",
                    onCheckedChange = { onSectionsChange(sections.copy(includeLibraryData = it)) }
                )

                BackupRestoreSectionRow(
                    icon = Icons.Default.AutoGraph,
                    title = "Stats & Rhythm Guard",
                    description = "Play counts, daily stats, genres, and Rhythm Guard configuration.",
                    checked = sections.includeStatsAndRhythmGuard,
                    badge = "Insight",
                    onCheckedChange = { onSectionsChange(sections.copy(includeStatsAndRhythmGuard = it)) }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.ui_cancel))
                }

                Button(
                    onClick = { onConfirm(sections) },
                    modifier = Modifier.weight(1f),
                    enabled = sections.hasAtLeastOneSectionSelected && !isProcessing,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = confirmIcon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(confirmLabel)
                    }
                }
            }
        }
    }
}

@Composable
private fun BackupRestoreSectionRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    badge: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                TunerAnimatedSwitch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (checked) "Included" else "Excluded",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BackupRestoreResultBottomSheet(
    state: BackupRestoreResultState,
    onDismiss: () -> Unit,
    onPrimaryAction: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showContent by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "backup_restore_result_alpha"
    )

    LaunchedEffect(Unit) {
        delay(80)
        showContent = true
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            StandardBottomSheetHeader(
                title = state.title,
                subtitle = if (state.requiresRestart) {
                    "Restart is required to finish applying changes"
                } else if (state.isError) {
                    "Action could not be completed"
                } else {
                    "Backup and restore status"
                },
                visible = showContent,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isError) {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
                    } else {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (state.isError) {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (state.isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (state.isError) Icons.Default.Close else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (state.isError) {
                                    MaterialTheme.colorScheme.onError
                                } else {
                                    MaterialTheme.colorScheme.onPrimary
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.isError) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!state.requiresRestart) {
                    FilledTonalButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(context.getString(R.string.ui_close))
                    }
                }
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (state.requiresRestart) {
                            Icons.Default.RestartAlt
                        } else if (state.isError) {
                            Icons.Default.Refresh
                        } else {
                            Icons.Default.Check
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (state.requiresRestart) {
                            context.getString(R.string.settings_restart_now)
                        } else {
                            context.getString(R.string.ui_ok)
                        }
                    )
                }
            }
        }
    }
}

// Library Tab Order Screen (merged from LibraryTabOrderBottomSheet)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryTabOrderSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val tabOrder by appSettings.libraryTabOrder.collectAsState()
    var reorderableList by remember { mutableStateOf(tabOrder.toList()) }

    // Helper function to get display name and icon for tab
    fun getTabInfo(tabId: String): Pair<String, ImageVector> {
        return when (tabId) {
            "SONGS" -> Pair("Songs", RhythmIcons.Relax)
            "PLAYLISTS" -> Pair("Playlists", RhythmIcons.PlaylistFilled)
            "ALBUMS" -> Pair("Albums", RhythmIcons.Music.Album)
            "ARTISTS" -> Pair("Artists", RhythmIcons.Artist)
            "EXPLORER" -> Pair("Explorer", Icons.Default.Folder)
            else -> Pair(tabId, RhythmIcons.Music.Song)
        }
    }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_tab_order),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Text(
                    text = context.getString(R.string.library_reorder_tabs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Reorderable list
            itemsIndexed(
                items = reorderableList,
                key = { _, item -> item }
            ) { index, tabId ->
                val (tabName, tabIcon) = getTabInfo(tabId)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .animateItem(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Position indicator
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            // Tab icon
                            Icon(
                                imageVector = tabIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )

                            // Tab name
                            Text(
                                text = tabName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Reorder buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Move up button
                            FilledIconButton(
                                onClick = {
                                    if (index > 0) {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        val newList = reorderableList.toMutableList()
                                        val item = newList.removeAt(index)
                                        newList.add(index - 1, item)
                                        reorderableList = newList
                                    }
                                },
                                enabled = index > 0,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = context.getString(R.string.settings_move_up),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Move down button
                            FilledIconButton(
                                onClick = {
                                    if (index < reorderableList.size - 1) {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        val newList = reorderableList.toMutableList()
                                        val item = newList.removeAt(index)
                                        newList.add(index + 1, item)
                                        reorderableList = newList
                                    }
                                },
                                enabled = index < reorderableList.size - 1,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = context.getString(R.string.settings_move_down),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Reset button
                    OutlinedButton(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            appSettings.resetLibraryTabOrder()
                            reorderableList = listOf("SONGS", "PLAYLISTS", "ALBUMS", "ARTISTS", "EXPLORER")
                            Toast.makeText(context, context.getString(R.string.settings_tab_order_reset), Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(context.getString(R.string.ui_reset))
                    }

                    // Save button
                    Button(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            appSettings.setLibraryTabOrder(reorderableList)
                            Toast.makeText(context, context.getString(R.string.settings_tab_order_saved), Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// Gestures Settings Screen
@Composable
fun GesturesSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val haptic = LocalHapticFeedback.current

    // Gesture settings
    val miniPlayerSwipeGestures by appSettings.miniPlayerSwipeGestures.collectAsState()
    val gesturePlayerSwipeDismiss by appSettings.gesturePlayerSwipeDismiss.collectAsState()
    val gesturePlayerSwipeTracks by appSettings.gesturePlayerSwipeTracks.collectAsState()
    val gestureArtworkDoubleTap by appSettings.gestureArtworkDoubleTap.collectAsState()

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_gestures),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Mini Player Gestures
            item(key = "miniplayer_gestures_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_miniplayer),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            item(key = "miniplayer_gestures_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        TunerSettingRow(
                            item = SettingItem(
                                Icons.Rounded.Swipe,
                                "Swipe Gestures",
                                "Swipe up/down to open/dismiss, left/right to skip tracks",
                                toggleState = miniPlayerSwipeGestures,
                                onToggleChange = { 
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    appSettings.setMiniPlayerSwipeGestures(it) 
                                }
                            )
                        )
                    }
                }
            }

            // Full Player Gestures
            item(key = "player_gestures_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_full_player),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            item(key = "player_gestures_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        TunerSettingRow(
                            item = SettingItem(
                                Icons.Rounded.SwipeDown,
                                "Swipe Down to Dismiss",
                                "Close player by swiping down on the screen",
                                toggleState = gesturePlayerSwipeDismiss,
                                onToggleChange = { 
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    appSettings.setGesturePlayerSwipeDismiss(it) 
                                }
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        TunerSettingRow(
                            item = SettingItem(
                                Icons.Rounded.SwipeLeft,
                                "Swipe Artwork for Tracks",
                                "Swipe left/right on album artwork to skip tracks",
                                toggleState = gesturePlayerSwipeTracks,
                                onToggleChange = { 
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    appSettings.setGesturePlayerSwipeTracks(it) 
                                }
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        TunerSettingRow(
                            item = SettingItem(
                                Icons.Rounded.TouchApp,
                                "Double Tap Artwork",
                                "Double tap on album art to play/pause",
                                toggleState = gestureArtworkDoubleTap,
                                onToggleChange = { 
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    appSettings.setGestureArtworkDoubleTap(it) 
                                }
                            )
                        )
                    }
                }
            }

            // Tips
            item(key = "gesture_tips") {
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
                                text = context.getString(R.string.settings_quick_tips),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        GestureTipItem(
                            icon = Icons.Default.SwipeVertical,
                            text = context.getString(R.string.settings_swipe_up_open)
                        )
                        GestureTipItem(
                            icon = Icons.Default.SwipeDown,
                            text = context.getString(R.string.settings_swipe_down_dismiss_tip)
                        )
                        GestureTipItem(
                            icon = Icons.Default.TouchApp,
                            text = context.getString(R.string.settings_double_tap_artwork_tip)
                        )
                        GestureTipItem(
                            icon = Icons.Default.Speed,
                            text = context.getString(R.string.settings_disable_unused_gestures)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun GestureTipItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// MiniPlayer Customization Screen
@Composable
fun MiniPlayerCustomizationSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // MiniPlayer settings
    val miniPlayerProgressStyle by appSettings.miniPlayerProgressStyle.collectAsState()
    val miniPlayerShowProgress by appSettings.miniPlayerShowProgress.collectAsState()
    val miniPlayerShowArtwork by appSettings.miniPlayerShowArtwork.collectAsState()
    val miniPlayerArtworkSize by appSettings.miniPlayerArtworkSize.collectAsState()
    val miniPlayerCornerRadius by appSettings.miniPlayerCornerRadius.collectAsState()
    val miniPlayerShowTime by appSettings.miniPlayerShowTime.collectAsState()
    val miniPlayerUseCircularProgress by appSettings.miniPlayerUseCircularProgress.collectAsState()
    val miniPlayerAlwaysShowTablet by appSettings.miniPlayerAlwaysShowTablet.collectAsState()
    val expressiveShapesEnabled by appSettings.expressiveShapesEnabled.collectAsState()

    var showMiniPlayerProgressStyleSheet by remember { mutableStateOf(false) }
    var showMiniPlayerArtworkSizeSheet by remember { mutableStateOf(false) }
    var showMiniPlayerCornerRadiusSheet by remember { mutableStateOf(false) }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_miniplayer),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {

            // Progress Display Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_progress_display),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Progress preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            val previewStyle = try {
                                ProgressStyle.valueOf(miniPlayerProgressStyle)
                            } catch (e: IllegalArgumentException) {
                                ProgressStyle.NORMAL
                            }
                            if (miniPlayerShowProgress && !miniPlayerUseCircularProgress) {
                                StyledProgressBar(
                                    progress = 0.45f,
                                    style = previewStyle,
                                    modifier = Modifier.fillMaxWidth(),
                                    progressColor = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    height = 4.dp,
                                    isPlaying = true
                                )
                            } else if (miniPlayerShowProgress && miniPlayerUseCircularProgress) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularWavyProgressIndicator(
                                        progress = { 0.45f },
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = context.getString(R.string.settings_progress_hidden),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.Visibility,
                            title = context.getString(R.string.settings_show_progress),
                            description = context.getString(R.string.settings_show_progress_desc),
                            toggleState = miniPlayerShowProgress,
                            onToggleChange = { appSettings.setMiniPlayerShowProgress(it) }
                        )
                        
                        // Progress Mode and Style - conditionally shown when Show Progress is on
                        AnimatedVisibility(
                            visible = miniPlayerShowProgress,
                            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                        ) {
                            Column {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                
                                // Progress Mode Button Group
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Icon container with expressive design
                                        Surface(
                                            modifier = Modifier.size(40.dp),
                                            shape = RoundedCornerShape(34.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            tonalElevation = 0.dp
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ChangeCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = context.getString(R.string.settings_progress_mode),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = context.getString(R.string.settings_choose_progress_style),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    ExpressiveButtonGroup(
                                        items = listOf("Linear", "Circular"),
                                        selectedIndex = if (miniPlayerUseCircularProgress) 1 else 0,
                                        onItemClick = { index ->
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            appSettings.setMiniPlayerUseCircularProgress(index == 1)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                
                                // Show progress style only for linear mode (circular always uses Material 3 wavy style)
                                AnimatedVisibility(
                                    visible = !miniPlayerUseCircularProgress,
                                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                                ) {
                                    Column {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                        SettingRow(
                                            icon = Icons.Default.LinearScale,
                                            title = "Progress Style",
                                            description = miniPlayerProgressStyle.lowercase().replaceFirstChar { it.uppercase() },
                                            onClick = { showMiniPlayerProgressStyleSheet = true }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Artwork Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_artwork),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        SettingRow(
                            icon = Icons.Default.Album,
                            title = context.getString(R.string.settings_show_artwork),
                            description = context.getString(R.string.settings_show_artwork_desc),
                            toggleState = miniPlayerShowArtwork,
                            onToggleChange = { appSettings.setMiniPlayerShowArtwork(it) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.PhotoSizeSelectLarge,
                            title = "Artwork Size",
                            description = "${miniPlayerArtworkSize}dp",
                            onClick = { showMiniPlayerArtworkSizeSheet = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        if (expressiveShapesEnabled) {
                            SettingRow(
                                icon = Icons.Default.RoundedCorner,
                                title = "Corner Radius",
                                description = "Managed by Expressive Shapes",
                                onClick = null
                            )
                        } else {
                            SettingRow(
                                icon = Icons.Default.RoundedCorner,
                                title = "Corner Radius",
                                description = "${miniPlayerCornerRadius}dp",
                                onClick = { showMiniPlayerCornerRadiusSheet = true }
                            )
                        }
                    }
                }
            }

            // Display Options Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_display_options),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        SettingRow(
                            icon = Icons.Default.Timer,
                            title = context.getString(R.string.settings_show_time),
                            description = context.getString(R.string.settings_show_time_desc),
                            toggleState = miniPlayerShowTime,
                            onToggleChange = { appSettings.setMiniPlayerShowTime(it) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.Tablet,
                            title = context.getString(R.string.settings_tablet_layout),
                            description = context.getString(R.string.settings_tablet_layout_desc),
                            toggleState = miniPlayerAlwaysShowTablet,
                            onToggleChange = { appSettings.setMiniPlayerAlwaysShowTablet(it) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Description Card
            item {
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
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "MiniPlayer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = context.getString(R.string.settings_customize_miniplayer),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // MiniPlayer Progress Style Bottom Sheet
    if (showMiniPlayerProgressStyleSheet) {
        ProgressStyleBottomSheet(
            title = "Progress Style",
            currentStyle = miniPlayerProgressStyle,
            onStyleSelected = { style ->
                appSettings.setMiniPlayerProgressStyle(style)
                showMiniPlayerProgressStyleSheet = false
            },
            onDismiss = { showMiniPlayerProgressStyleSheet = false },
            context = context,
            haptics = haptics
        )
    }

    // MiniPlayer Artwork Size Bottom Sheet
    if (showMiniPlayerArtworkSizeSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var tempSize by remember { mutableIntStateOf(miniPlayerArtworkSize) }

        ModalBottomSheet(
            onDismissRequest = { showMiniPlayerArtworkSizeSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Artwork Size",
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
                                text = "${tempSize}dp",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = tempSize.toFloat(),
                    onValueChange = { tempSize = it.toInt() },
                    onValueChangeFinished = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        appSettings.setMiniPlayerArtworkSize(tempSize)
                    },
                    valueRange = 40f..72f,
                    steps = 31,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // MiniPlayer Corner Radius Bottom Sheet
    if (showMiniPlayerCornerRadiusSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var tempRadius by remember { mutableIntStateOf(miniPlayerCornerRadius) }

        ModalBottomSheet(
            onDismissRequest = { showMiniPlayerCornerRadiusSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Corner Radius",
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
                                text = "${tempRadius}dp",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = tempRadius.toFloat(),
                    onValueChange = { tempRadius = it.toInt() },
                    onValueChangeFinished = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        appSettings.setMiniPlayerCornerRadius(tempRadius)
                    },
                    valueRange = 0f..28f,
                    steps = 27,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Player Customization Screen
@Composable
fun PlayerCustomizationSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // State variables
    val showLyrics by appSettings.showLyrics.collectAsState()
    val lyricsSourcePreference by appSettings.lyricsSourcePreference.collectAsState()
    val playerShowGradientOverlay by appSettings.playerShowGradientOverlay.collectAsState()
    val playerLyricsTransition by appSettings.playerLyricsTransition.collectAsState()
    val playerLyricsTextSize by appSettings.playerLyricsTextSize.collectAsState()
    val playerLyricsAlignment by appSettings.playerLyricsAlignment.collectAsState()
    val playerShowArtBelowLyrics by appSettings.playerShowArtBelowLyrics.collectAsState()
    val playerShowSeekButtons by appSettings.playerShowSeekButtons.collectAsState()
    val playerTextAlignment by appSettings.playerTextAlignment.collectAsState()
    val playerShowSongInfoOnArtwork by appSettings.playerShowSongInfoOnArtwork.collectAsState()
    val playerArtworkCornerRadius by appSettings.playerArtworkCornerRadius.collectAsState()
    val playerShowAudioQualityBadges by appSettings.playerShowAudioQualityBadges.collectAsState()
    val expressiveShapesEnabled by appSettings.expressiveShapesEnabled.collectAsState()

    // Progress bar settings
    val playerProgressStyle by appSettings.playerProgressStyle.collectAsState()
    val playerProgressThumbStyle by appSettings.playerProgressThumbStyle.collectAsState()

    var showChipOrderBottomSheet by remember { mutableStateOf(false) }
    var showLyricsSourceDialog by remember { mutableStateOf(false) }
    var showTextAlignmentSheet by remember { mutableStateOf(false) }
    var showCornerRadiusSheet by remember { mutableStateOf(false) }
    var showPlayerProgressStyleSheet by remember { mutableStateOf(false) }
    var showPlayerThumbStyleSheet by remember { mutableStateOf(false) }

    if (showLyricsSourceDialog) {
        LyricsSourceDialog(onDismiss = { showLyricsSourceDialog = false }, appSettings = appSettings, context = context, haptic = haptics)
    }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_player),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {

            // Player Controls Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_player_controls),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        SettingRow(
                            icon = Icons.Default.Reorder,
                            title = context.getString(R.string.settings_chip_order),
                            description = context.getString(R.string.settings_chip_order_desc),
                            onClick = { showChipOrderBottomSheet = true }
                        )
                    }
                }
            }

            // Display Options Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Display Options",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Art View Overlay toggle
                        SettingRow(
                            icon = Icons.Default.Gradient,
                            title = context.getString(R.string.settings_artwork_overlay),
                            description = context.getString(R.string.settings_artwork_overlay_desc),
                            toggleState = playerShowGradientOverlay,
                            onToggleChange = { appSettings.setPlayerShowGradientOverlay(it) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.Info,
                            title = context.getString(R.string.settings_song_info_artwork),
                            description = context.getString(R.string.settings_song_info_artwork_desc),
                            toggleState = playerShowSongInfoOnArtwork,
                            onToggleChange = { appSettings.setPlayerShowSongInfoOnArtwork(it) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.HighQuality,
                            title = context.getString(R.string.settings_audio_quality_badges),
                            description = context.getString(R.string.settings_audio_quality_badges_desc),
                            toggleState = playerShowAudioQualityBadges,
                            onToggleChange = { appSettings.setPlayerShowAudioQualityBadges(it) }
                        )
                    }
                }
            }

            // Lyrics Customization Section (always visible)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_lyrics_customization),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Show Lyrics toggle
                        SettingRow(
                            icon = Icons.Rounded.Lyrics,
                            title = context.getString(R.string.settings_show_lyrics),
                            description = context.getString(R.string.settings_show_lyrics_desc),
                            toggleState = showLyrics,
                            onToggleChange = { appSettings.setShowLyrics(it) }
                        )
                        // All lyrics settings visible when lyrics are enabled
                        AnimatedVisibility(
                            visible = showLyrics,
                            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                        ) {
                            Column {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                // Lyrics Source Priority
                                SettingRow(
                                    icon = Icons.Default.Lyrics,
                                    title = context.getString(R.string.lyrics_source_priority),
                                    description = context.getString(R.string.playback_lyrics_priority_desc),
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        showLyricsSourceDialog = true
                                    }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                SettingRow(
                                    icon = Icons.Rounded.Translate,
                                    title = context.getString(R.string.lyrics_show_translation),
                                    description = context.getString(R.string.lyrics_show_translation_desc),
                                    toggleState = appSettings.showLyricsTranslation.collectAsState().value,
                                    onToggleChange = { appSettings.setShowLyricsTranslation(it) }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                SettingRow(
                                    icon = Icons.Rounded.Language,
                                    title = context.getString(R.string.lyrics_show_romanization),
                                    description = context.getString(R.string.lyrics_show_romanization_desc),
                                    toggleState = appSettings.showLyricsRomanization.collectAsState().value,
                                    onToggleChange = { appSettings.setShowLyricsRomanization(it) }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                SettingRow(
                                    icon = Icons.Rounded.LightMode,
                                    title = context.getString(R.string.settings_keep_screen_on_lyrics),
                                    description = context.getString(R.string.settings_keep_screen_on_lyrics_desc),
                                    toggleState = appSettings.keepScreenOnLyrics.collectAsState().value,
                                    onToggleChange = { appSettings.setKeepScreenOnLyrics(it) }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                        // Lyrics Transition
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(34.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    tonalElevation = 0.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.Animation,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = context.getString(R.string.settings_lyrics_transition),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Art ↔ Lyrics switch animation",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ExpressiveButtonGroup(
                                items = listOf("Slide", "Fade", "Scale", "Up"),
                                selectedIndex = playerLyricsTransition,
                                onItemClick = { index ->
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                    appSettings.setPlayerLyricsTransition(index)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        // Lyrics Text Size
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(34.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    tonalElevation = 0.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.FormatSize,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = context.getString(R.string.settings_lyrics_text_size),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Size: ${(playerLyricsTextSize * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Slider(
                                value = playerLyricsTextSize,
                                onValueChange = { appSettings.setPlayerLyricsTextSize(it) },
                                valueRange = 0.5f..2.0f,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("50%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("200%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        // Lyrics Alignment
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(34.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    tonalElevation = 0.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.FormatAlignCenter,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Lyrics Alignment",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = context.getString(R.string.settings_lyrics_alignment_desc),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ExpressiveButtonGroup(
                                items = listOf("Left", "Center", "Right"),
                                selectedIndex = when (playerLyricsAlignment) {
                                    "START" -> 0
                                    "END" -> 2
                                    else -> 1
                                },
                                onItemClick = { index ->
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                    appSettings.setPlayerLyricsAlignment(when (index) {
                                        0 -> "START"
                                        2 -> "END"
                                        else -> "CENTER"
                                    })
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        // Show art below lyrics
                        SettingRow(
                            icon = Icons.Default.Image,
                            title = context.getString(R.string.settings_show_art_below_lyrics),
                            description = context.getString(R.string.settings_show_art_below_lyrics_desc),
                            toggleState = playerShowArtBelowLyrics,
                            onToggleChange = { appSettings.setPlayerShowArtBelowLyrics(it) }
                        )
                            } // end AnimatedVisibility Column
                        } // end AnimatedVisibility
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_layout_options),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        SettingRow(
                            icon = Icons.Default.Forward10,
                            title = context.getString(R.string.settings_seek_buttons),
                            description = context.getString(R.string.settings_seek_buttons_desc),
                            toggleState = playerShowSeekButtons,
                            onToggleChange = { appSettings.setPlayerShowSeekButtons(it) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.FormatAlignCenter,
                            title = context.getString(R.string.settings_text_alignment),
                            description = when(playerTextAlignment) {
                                "START" -> context.getString(R.string.settings_left_aligned)
                                "END" -> context.getString(R.string.settings_right_aligned)
                                else -> context.getString(R.string.settings_center_aligned)
                            },
                            onClick = { showTextAlignmentSheet = true }
                        )
                    }
                }
            }

            // Progress Bar Style Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Progress Display",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Progress style preview with thumb
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            val previewStyle = try {
                                ProgressStyle.valueOf(playerProgressStyle)
                            } catch (e: IllegalArgumentException) {
                                ProgressStyle.WAVY
                            }
                            val previewThumbStyle = try {
                                ThumbStyle.valueOf(playerProgressThumbStyle)
                            } catch (e: IllegalArgumentException) {
                                ThumbStyle.CIRCLE
                            }
                            StyledProgressBar(
                                progress = 0.65f,
                                style = previewStyle,
                                modifier = Modifier.fillMaxWidth(),
                                progressColor = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                height = 8.dp,
                                isPlaying = true,
                                showThumb = previewThumbStyle != ThumbStyle.NONE,
                                thumbStyle = previewThumbStyle,
                                thumbSize = 14.dp
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.LinearScale,
                            title = "Progress Style",
                            description = playerProgressStyle.lowercase().replaceFirstChar { it.uppercase() },
                            onClick = { showPlayerProgressStyleSheet = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        SettingRow(
                            icon = Icons.Default.TouchApp,
                            title = context.getString(R.string.settings_thumb_style),
                            description = playerProgressThumbStyle.lowercase().replaceFirstChar { it.uppercase() },
                            onClick = { showPlayerThumbStyleSheet = true }
                        )
                    }
                }
            }

            // Artwork Customization Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Artwork",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    if (expressiveShapesEnabled) {
                        SettingRow(
                            icon = Icons.Default.RoundedCorner,
                            title = "Corner Radius",
                            description = "Managed by Expressive Shapes",
                            onClick = null
                        )
                    } else {
                        SettingRow(
                            icon = Icons.Default.RoundedCorner,
                            title = "Corner Radius",
                            description = "${playerArtworkCornerRadius}dp",
                            onClick = { showCornerRadiusSheet = true }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Description Card
            item {
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
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Player Screen",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = context.getString(R.string.settings_personalize_player),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Tips Card
            // item {
            //     Spacer(modifier = Modifier.height(24.dp))
            //     Card(
            //         modifier = Modifier.fillMaxWidth(),
            //         shape = RoundedCornerShape(18.dp),
            //         colors = CardDefaults.cardColors(
            //             containerColor = MaterialTheme.colorScheme.primaryContainer
            //         )
            //     ) {
            //         Column(
            //             modifier = Modifier.padding(20.dp)
            //         ) {
            //             Row(
            //                 verticalAlignment = Alignment.CenterVertically
            //             ) {
            //                 Icon(
            //                     imageVector = Icons.Filled.Lightbulb,
            //                     contentDescription = null,
            //                     modifier = Modifier.size(24.dp)
            //                 )
            //                 Spacer(modifier = Modifier.width(12.dp))
            //                 Text(
            //                     text = "Player Tips",
            //                     style = MaterialTheme.typography.titleMedium,
            //                     fontWeight = FontWeight.Bold,
            //                     color = MaterialTheme.colorScheme.onPrimaryContainer
            //                 )
            //             }
            //             Spacer(modifier = Modifier.height(12.dp))

            //             PlayerTipItem(
            //                 icon = Icons.Default.Reorder,
            //                 text = "Tap 'Chip Order & Visibility' to customize action chips"
            //             )
            //             PlayerTipItem(
            //                 icon = Icons.Rounded.Lyrics,
            //                 text = "Enable lyrics to see synchronized lyrics while playing"
            //             )
            //             PlayerTipItem(
            //                 icon = Icons.Default.VideoLibrary,
            //                 text = "Canvas backgrounds add animated visuals to supported songs"
            //             )
            //         }
            //     }
            // }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Show chip order bottom sheet
    if (showChipOrderBottomSheet) {
        PlayerChipOrderBottomSheet(
            onDismiss = { showChipOrderBottomSheet = false },
            appSettings = appSettings,
            haptics = haptics
        )
    }

    // Text Alignment Bottom Sheet
    if (showTextAlignmentSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showTextAlignmentSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Header
                Text(
                    text = "Text Alignment",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Alignment options
                listOf(
                    Triple("START", "Left", Icons.AutoMirrored.Filled.AlignHorizontalLeft),
                    Triple("CENTER", "Center", Icons.Default.FormatAlignCenter),
                    Triple("END", "Right", Icons.AutoMirrored.Filled.AlignHorizontalRight)
                ).forEach { (value, label, icon) ->
                    val isSelected = playerTextAlignment == value
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                            appSettings.setPlayerTextAlignment(value)
                            showTextAlignmentSheet = false
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Player Progress Style Bottom Sheet
    if (showPlayerProgressStyleSheet) {
        ProgressStyleBottomSheet(
            title = "Progress Style",
            currentStyle = playerProgressStyle,
            onStyleSelected = { style ->
                appSettings.setPlayerProgressStyle(style)
                showPlayerProgressStyleSheet = false
            },
            onDismiss = { showPlayerProgressStyleSheet = false },
            context = context,
            haptics = haptics
        )
    }

    // Player Thumb Style Bottom Sheet
    if (showPlayerThumbStyleSheet) {
        ThumbStyleBottomSheet(
            title = "Thumb Style",
            currentStyle = playerProgressThumbStyle,
            onStyleSelected = { style ->
                appSettings.setPlayerProgressThumbStyle(style)
                showPlayerThumbStyleSheet = false
            },
            onDismiss = { showPlayerThumbStyleSheet = false },
            context = context,
            haptics = haptics
        )
    }

    // Corner Radius Bottom Sheet
    if (showCornerRadiusSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var tempRadius by remember { mutableIntStateOf(playerArtworkCornerRadius) }

        ModalBottomSheet(
            onDismissRequest = { showCornerRadiusSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Corner Radius",
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
                                text = "${tempRadius}dp",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = tempRadius.toFloat(),
                    onValueChange = { tempRadius = it.toInt() },
                    onValueChangeFinished = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        appSettings.setPlayerArtworkCornerRadius(tempRadius)
                    },
                    valueRange = 0f..40f,
                    steps = 39,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(R.string.settings_adjust_artwork_corners),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: (() -> Unit)? = null,
    toggleState: Boolean? = null,
    onToggleChange: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onClick()
                }
                else Modifier
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (toggleState != null && onToggleChange != null) {
            TunerAnimatedSwitch(
                checked = toggleState,
                onCheckedChange = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                    onToggleChange(it)
                }
            )
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Navigate",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlayerTipItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Bottom sheet for selecting progress bar style with visual previews
 */
@Composable
private fun ProgressStyleBottomSheet(
    title: String,
    currentStyle: String,
    onStyleSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    context: Context,
    haptics: HapticFeedback
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val progressStyles = listOf(
        ProgressStyleOption("NORMAL", "Normal", Icons.Default.LinearScale, "Standard progress bar"),
        ProgressStyleOption("WAVY", "Wavy", Icons.Default.GraphicEq, "Animated wavy line"),
        ProgressStyleOption("ROUNDED", "Rounded", Icons.Default.RoundedCorner, "Pill-shaped progress"),
        ProgressStyleOption("THIN", "Thin", Icons.Default.Remove, "Thin elegant line"),
        ProgressStyleOption("THICK", "Thick", Icons.Default.DragHandle, "Bold thick bar"),
        ProgressStyleOption("GRADIENT", "Gradient", Icons.Default.Gradient, "Multi-color gradient"),
        ProgressStyleOption("SEGMENTED", "Segmented", Icons.Default.MoreHoriz, "Segmented blocks"),
        ProgressStyleOption("DOTS", "Dots", Icons.Default.FiberManualRecord, "Dot indicators")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Style options in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(progressStyles) { styleOption ->
                    val isSelected = currentStyle == styleOption.id
                    val progressStyleEnum = try {
                        ProgressStyle.valueOf(styleOption.id)
                    } catch (e: IllegalArgumentException) {
                        ProgressStyle.NORMAL
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        border = if (isSelected)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else null,
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                            onStyleSelected(styleOption.id)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Preview of the style
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                StyledProgressBar(
                                    progress = 0.6f,
                                    style = progressStyleEnum,
                                    modifier = Modifier.fillMaxWidth(),
                                    progressColor = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.primary,
                                    trackColor = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    height = 6.dp,
                                    isPlaying = true
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = styleOption.icon,
                                    contentDescription = null,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = styleOption.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = styleOption.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Data class for progress style options
 */
private data class ProgressStyleOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String
)

/**
 * Data class for thumb style options
 */
private data class ThumbStyleOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String
)

/**
 * Bottom sheet for selecting thumb style
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThumbStyleBottomSheet(
    title: String,
    currentStyle: String,
    onStyleSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    context: Context,
    haptics: HapticFeedback
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val thumbStyles = listOf(
        ThumbStyleOption("NONE", "None", Icons.Default.VisibilityOff, "No thumb indicator"),
        ThumbStyleOption("CIRCLE", "Circle", Icons.Default.FiberManualRecord, "Circular with highlight"),
        ThumbStyleOption("PILL", "Pill", Icons.Default.RoundedCorner, "Vertical pill shape"),
        ThumbStyleOption("DIAMOND", "Diamond", Icons.Default.ChangeHistory, "Diamond rhombus"),
        ThumbStyleOption("LINE", "Line", Icons.Default.Remove, "Thin vertical line"),
        ThumbStyleOption("SQUARE", "Square", Icons.Default.CropSquare, "Rounded square"),
        ThumbStyleOption("GLOW", "Glow", Icons.Default.Flare, "Glowing circle"),
        ThumbStyleOption("ARROW", "Arrow", Icons.Default.PlayArrow, "Arrow pointer"),
        ThumbStyleOption("DOT", "Dot", Icons.Default.Adjust, "Small dot with ring")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Style options in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(thumbStyles) { styleOption ->
                    val isSelected = currentStyle == styleOption.id
                    val thumbStyleEnum = try {
                        ThumbStyle.valueOf(styleOption.id)
                    } catch (e: IllegalArgumentException) {
                        ThumbStyle.CIRCLE
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        border = if (isSelected)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else null,
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                            onStyleSelected(styleOption.id)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Preview of the thumb style
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                StyledProgressBar(
                                    progress = 0.6f,
                                    style = ProgressStyle.NORMAL,
                                    modifier = Modifier.fillMaxWidth(),
                                    progressColor = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.primary,
                                    trackColor = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    height = 6.dp,
                                    isPlaying = true,
                                    showThumb = thumbStyleEnum != ThumbStyle.NONE,
                                    thumbStyle = thumbStyleEnum,
                                    thumbSize = 12.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = styleOption.icon,
                                    contentDescription = null,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = styleOption.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = styleOption.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Data classes and enums for theme customization
data class ColorSchemeOption(
    val name: String,
    val displayName: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val tertiaryColor: Color
)

data class FontOption(
    val name: String,
    val displayName: String,
    val description: String
)

enum class ColorSource(val displayName: String, val description: String, val icon: ImageVector) {
    ALBUM_ART("Album Art", "Extract colors from currently playing album artwork", Icons.Filled.Image),
    MONET("System Colors", "Use Material You colors from your wallpaper", Icons.Filled.ColorLens),
    CUSTOM("Custom Scheme", "Choose from predefined color schemes", Icons.Filled.Palette)
}

enum class FontSource(val displayName: String, val description: String, val icon: ImageVector) {
    SYSTEM("System Font", "Use the device's default font", Icons.Filled.PhoneAndroid),
    CUSTOM("Custom Font", "Import and use a custom font file", Icons.Filled.TextFields)
}

// HSL Color conversion utilities
data class HSLColor(val hue: Float, val saturation: Float, val lightness: Float)

fun Color.toHSL(): HSLColor {
    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val diff = max - min

    val lightness = (max + min) / 2f

    val saturation = if (diff == 0f) 0f else diff / (1f - kotlin.math.abs(2f * lightness - 1f))

    val hue = when (max) {
        min -> 0f
        r -> ((g - b) / diff) % 6
        g -> (b - r) / diff + 2
        b -> (r - g) / diff + 4
        else -> 0f
    } * 60f

    return HSLColor(
        hue = if (hue < 0) hue + 360f else hue,
        saturation = saturation,
        lightness = lightness
    )
}

fun HSLColor.toColor(): Color {
    val c = (1f - kotlin.math.abs(2f * lightness - 1f)) * saturation
    val x = c * (1f - kotlin.math.abs((hue / 60f) % 2f - 1f))
    val m = lightness - c / 2f

    val (r, g, b) = when {
        hue < 60 -> Triple(c, x, 0f)
        hue < 120 -> Triple(x, c, 0f)
        hue < 180 -> Triple(0f, c, x)
        hue < 240 -> Triple(0f, x, c)
        hue < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f),
        alpha = 1f
    )
}

@Composable
fun ThemeCustomizationSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val haptic = LocalHapticFeedback.current

    // Theme states
    val useSystemTheme by appSettings.useSystemTheme.collectAsState()
    val darkMode by appSettings.darkMode.collectAsState()
    val amoledTheme by appSettings.amoledTheme.collectAsState()
    val useDynamicColors by appSettings.useDynamicColors.collectAsState()
    val customColorScheme by appSettings.customColorScheme.collectAsState()
    val colorSource by appSettings.colorSource.collectAsState()
    val extractedAlbumColors by appSettings.extractedAlbumColors.collectAsState()

    // Font states
    val fontSource by appSettings.fontSource.collectAsState()
    val customFontPath by appSettings.customFontPath.collectAsState()
    val customFontFamily by appSettings.customFontFamily.collectAsState()

    // Color source state - initialize based on saved setting
    var selectedColorSource by remember(colorSource) {
        mutableStateOf(
            when (colorSource) {
                "ALBUM_ART" -> ColorSource.ALBUM_ART
                "MONET" -> ColorSource.MONET
                "CUSTOM" -> ColorSource.CUSTOM
                else -> ColorSource.CUSTOM
            }
        )
    }

    // Font source state - initialize based on saved setting
    var selectedFontSource by remember(fontSource) {
        mutableStateOf(
            when (fontSource) {
                "CUSTOM" -> FontSource.CUSTOM
                "SYSTEM" -> FontSource.SYSTEM
                else -> FontSource.SYSTEM
            }
        )
    }

    // Font file picker launcher
    val fontPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Copy font to internal storage
            val fontPath = FontLoader.copyFontToInternalStorage(context, it)
            if (fontPath != null) {
                // Validate that the font can be loaded
                val testFont = FontLoader.loadCustomFont(context, fontPath)
                if (testFont != null) {
                    // Save to settings
                    appSettings.setCustomFontPath(fontPath)
                    appSettings.setFontSource("CUSTOM")

                    // Extract and save font name
                    val fontName = FontLoader.getFontFileName(fontPath) ?: "Custom Font"
                    appSettings.setCustomFontFamily(fontName)

                    // Show success feedback
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                    Toast.makeText(context, context.getString(R.string.theme_font_imported), Toast.LENGTH_SHORT).show()
                } else {
                    // Font file copied but can't be loaded
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.Reject)
                    Toast.makeText(context, context.getString(R.string.theme_font_invalid), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Failed to copy font file
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.Reject)
                Toast.makeText(context, context.getString(R.string.theme_font_import_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Color schemes - expanded list matching bottomsheet
    val colorSchemes = remember {
        listOf(
            ColorSchemeOption(
                name = "Default",
                displayName = "Default Purple",
                description = "The classic Rhythm experience with vibrant purple tones",
                primaryColor = Color(0xFF5C4AD5),
                secondaryColor = Color(0xFF5D5D6B),
                tertiaryColor = Color(0xFFFFDDB6)
            ),
            ColorSchemeOption(
                name = "Warm",
                displayName = "Warm Sunset",
                description = "Cozy orange and red tones for a warm atmosphere",
                primaryColor = Color(0xFFFF6B35),
                secondaryColor = Color(0xFFF7931E),
                tertiaryColor = Color(0xFFFFC857)
            ),
            ColorSchemeOption(
                name = "Cool",
                displayName = "Cool Ocean",
                description = "Refreshing blue and teal tones for a calming vibe",
                primaryColor = Color(0xFF1E88E5),
                secondaryColor = Color(0xFF00897B),
                tertiaryColor = Color(0xFF80DEEA)
            ),
            ColorSchemeOption(
                name = "Forest",
                displayName = "Forest Green",
                description = "Natural green tones inspired by nature",
                primaryColor = Color(0xFF2E7D32),
                secondaryColor = Color(0xFF558B2F),
                tertiaryColor = Color(0xFF9CCC65)
            ),
            ColorSchemeOption(
                name = "Rose",
                displayName = "Rose Pink",
                description = "Elegant pink and magenta tones",
                primaryColor = Color(0xFFE91E63),
                secondaryColor = Color(0xFFC2185B),
                tertiaryColor = Color(0xFFF8BBD0)
            ),
            ColorSchemeOption(
                name = "Monochrome",
                displayName = "Monochrome",
                description = "Minimalist grayscale for a clean, modern look",
                primaryColor = Color(0xFF424242),
                secondaryColor = Color(0xFF616161),
                tertiaryColor = Color(0xFF9E9E9E)
            ),
            ColorSchemeOption(
                name = "Lavender",
                displayName = "Lavender",
                description = "Calming purple and lavender tones for relaxation",
                primaryColor = Color(0xFF7C4DFF),
                secondaryColor = Color(0xFF9575CD),
                tertiaryColor = Color(0xFFBA68C8)
            ),
            ColorSchemeOption(
                name = "Ocean",
                displayName = "Deep Ocean",
                description = "Deep blues and aquamarines for oceanic serenity",
                primaryColor = Color(0xFF006064),
                secondaryColor = Color(0xFF00838F),
                tertiaryColor = Color(0xFF00ACC1)
            ),
            ColorSchemeOption(
                name = "Aurora",
                displayName = "Northern Lights",
                description = "Vibrant greens and blues inspired by the aurora borealis",
                primaryColor = Color(0xFF00C853),
                secondaryColor = Color(0xFF00E676),
                tertiaryColor = Color(0xFF69F0AE)
            ),
            ColorSchemeOption(
                name = "Amber",
                displayName = "Golden Amber",
                description = "Rich amber and gold tones for a luxurious feel",
                primaryColor = Color(0xFFFF6F00),
                secondaryColor = Color(0xFFFF8F00),
                tertiaryColor = Color(0xFFFFC107)
            ),
            ColorSchemeOption(
                name = "Crimson",
                displayName = "Deep Crimson",
                description = "Bold burgundy and crimson shades for drama",
                primaryColor = Color(0xFFB71C1C),
                secondaryColor = Color(0xFFC62828),
                tertiaryColor = Color(0xFFD32F2F)
            ),
            ColorSchemeOption(
                name = "Emerald",
                displayName = "Emerald Dream",
                description = "Fresh emerald greens with natural forest hues",
                primaryColor = Color(0xFF2E7D32),
                secondaryColor = Color(0xFF388E3C),
                tertiaryColor = Color(0xFF4CAF50)
            ),
            ColorSchemeOption(
                name = "Mint",
                displayName = "Mint",
                description = "Fresh and clean cyan and mint green tones",
                primaryColor = Color(0xFF0097A7),
                secondaryColor = Color(0xFF00ACC1),
                tertiaryColor = Color(0xFF00BCD4)
            )
        )
    }

    // Font options - matching bottomsheet
    val fontOptions = remember {
        listOf(
            FontOption(
                name = "Geom",
                displayName = "Geom",
                description = "Modern, clean sans-serif font from Google"
            ),
            FontOption(
                name = "System",
                displayName = "System Default",
                description = "Use your device's default font"
            ),
            FontOption(
                name = "Slate",
                displayName = "Slate",
                description = "Elegant serif font with a classic, traditional appearance"
            ),
            FontOption(
                name = "Inter",
                displayName = "Inter",
                description = "Clean and modern sans-serif font, highly readable"
            ),
            FontOption(
                name = "JetBrains",
                displayName = "JetBrains Mono",
                description = "Monospace font perfect for technical content"
            ),
            FontOption(
                name = "Quicksand",
                displayName = "Quicksand",
                description = "Rounded font with a softer, friendlier appearance"
            )
        )
    }
    val currentFont by appSettings.customFont.collectAsState()

    // Dialog states
    var showColorSourceDialog by remember { mutableStateOf(false) }
    var showFontSourceDialog by remember { mutableStateOf(false) }
    var showColorSchemesDialog by remember { mutableStateOf(false) }
    var showCustomColorsDialog by remember { mutableStateOf(false) }
    var showFontSelectionDialog by remember { mutableStateOf(false) }
    var navigateToExpressiveShapes by remember { mutableStateOf(false) }
    
    // Restart dialog states
    var showRestartDialog by remember { mutableStateOf(false) }
    var restartDialogMessage by remember { mutableStateOf("") }
    
    // Festive theme states
    val festiveThemeEnabled by appSettings.festiveThemeEnabled.collectAsState()
    val festiveThemeAutoDetect by appSettings.festiveThemeAutoDetect.collectAsState()
    val festiveThemeType by appSettings.festiveThemeType.collectAsState()
    val festiveThemeIntensity by appSettings.festiveThemeIntensity.collectAsState()
    val festiveSnowflakeSize by appSettings.festiveSnowflakeSize.collectAsState()
    val festiveSnowflakeArea by appSettings.festiveSnowflakeArea.collectAsState()
    val festiveShowTopLights by appSettings.festiveShowTopLights.collectAsState()
    val festiveShowSideGarland by appSettings.festiveShowSideGarland.collectAsState()
    val festiveShowBottomSnow by appSettings.festiveShowBottomSnow.collectAsState()
    val festiveShowSnowfall by appSettings.festiveShowSnowfall.collectAsState()
    var showFestivalSelectionDialog by remember { mutableStateOf(false) }
    
    // Handle navigation to Expressive Shapes screen with proper animation
    AnimatedContent(
        targetState = navigateToExpressiveShapes,
        transitionSpec = {
            if (targetState) {
                // Slide in from right when navigating to Expressive Shapes
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = EaseOutCubic
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 350,
                        delayMillis = 50
                    )
                ) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = EaseOutCubic
                    )
                ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { -it / 4 },
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = EaseInCubic
                    )
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 250)
                ) + scaleOut(
                    targetScale = 0.95f,
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = EaseInCubic
                    )
                )
            } else {
                // Slide in from left when going back to Theme
                slideInHorizontally(
                    initialOffsetX = { -it / 4 },
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = EaseOutCubic
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 350,
                        delayMillis = 50
                    )
                ) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = EaseOutCubic
                    )
                ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = EaseInCubic
                    )
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 250)
                ) + scaleOut(
                    targetScale = 0.92f,
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = EaseInCubic
                    )
                )
            }
        },
        label = "theme_to_shapes_navigation",
        contentKey = { it }
    ) { isNavigatingToShapes ->
        if (isNavigatingToShapes) {
            ExpressiveShapesSettingsScreen(onBackClick = { navigateToExpressiveShapes = false })
        } else {
            CollapsibleHeaderScreen(
                title = context.getString(R.string.settings_theme),
                showBackButton = true,
                onBackClick = onBackClick
            ) { modifier ->
        val settingGroups = listOf(
            SettingGroup(
                title = context.getString(R.string.settings_display_mode),
                items = listOf(
                    // Display Mode Button Group
                    SettingItem(
                        Icons.Default.Settings,
                        context.getString(R.string.settings_theme_mode),
                        context.getString(R.string.settings_theme_mode_desc),
                        onClick = {
                            // This will be replaced with button group below
                        }
                    ),
                    // AMOLED Theme - always in list, rendered conditionally via AnimatedVisibility
                    SettingItem(
                        Icons.Default.Brightness2,
                        context.getString(R.string.settings_amoled_theme),
                        context.getString(R.string.settings_amoled_theme_desc),
                        toggleState = amoledTheme,
                        onToggleChange = { appSettings.setAmoledTheme(it) }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_color_customization),
                items = listOf(
                    SettingItem(
                        Icons.Default.Palette,
                        context.getString(R.string.settings_color_source),
                        when (selectedColorSource) {
                            ColorSource.ALBUM_ART -> context.getString(R.string.settings_color_source_album)
                            ColorSource.MONET -> context.getString(R.string.settings_color_source_monet)
                            ColorSource.CUSTOM -> context.getString(R.string.settings_color_source_custom, customColorScheme)
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            showColorSourceDialog = true
                        }
                    ),
                    SettingItem(
                        Icons.Default.ColorLens,
                        context.getString(R.string.settings_color_schemes),
                        if (selectedColorSource == ColorSource.CUSTOM)
                            context.getString(R.string.settings_color_schemes_desc)
                        else
                            context.getString(R.string.settings_custom_only),
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            showColorSchemesDialog = true
                        }
                    ),
                    SettingItem(
                        Icons.Default.Brush,
                        context.getString(R.string.settings_custom_colors),
                        if (selectedColorSource == ColorSource.CUSTOM)
                            context.getString(R.string.settings_custom_colors_desc)
                        else
                            context.getString(R.string.settings_custom_only),
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            showCustomColorsDialog = true
                        }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_font_customization),
                items = listOf(
                    SettingItem(
                        Icons.Default.TextFields,
                        context.getString(R.string.settings_font_source),
                        when (selectedFontSource) {
                            FontSource.SYSTEM -> context.getString(R.string.settings_font_source_system, currentFont)
                            FontSource.CUSTOM -> context.getString(
                                R.string.settings_font_source_custom,
                                customFontFamily
                            )
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            showFontSourceDialog = true
                        }
                    ),
                    SettingItem(
                        Icons.Default.TextFields,
                        context.getString(R.string.settings_font_selection),
                        if (selectedFontSource == FontSource.SYSTEM)
                            context.getString(R.string.settings_font_selection_desc)
                        else
                            context.getString(R.string.settings_system_font_only),
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            showFontSelectionDialog = true
                        }
                    ),
                    SettingItem(
                        Icons.Default.FileUpload,
                        context.getString(R.string.settings_import_custom_font),
                        if (customFontPath != null)
                            context.getString(R.string.settings_font_imported_name, customFontFamily)
                        else
                            context.getString(R.string.settings_import_font_desc),
                        onClick = {
                            HapticUtils.performHapticFeedback(
                                context,
                                haptic,
                                HapticFeedbackType.LongPress
                            )
                            fontPickerLauncher.launch("font/*")
                        }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_festive_themes),
                items = buildList {
                    add(
                        SettingItem(
                            Icons.Default.Celebration,
                            context.getString(R.string.settings_enable_festive),
                            context.getString(R.string.settings_enable_festive_desc),
                            toggleState = festiveThemeEnabled,
                            onToggleChange = { appSettings.setFestiveThemeEnabled(it) }
                        )
                    )
                    if (festiveThemeEnabled) {
                        add(
                            SettingItem(
                                Icons.Default.EventAvailable,
                                context.getString(R.string.settings_auto_detect_holidays),
                                context.getString(R.string.settings_auto_detect_holidays_desc),
                                toggleState = festiveThemeAutoDetect,
                                onToggleChange = { appSettings.setFestiveThemeAutoDetect(it) }
                            )
                        )
                        if (!festiveThemeAutoDetect) {
                            add(
                                SettingItem(
                                    Icons.Default.AutoAwesome,
                                    context.getString(R.string.settings_select_festival),
                                    getFestivalDisplayName(festiveThemeType),
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        showFestivalSelectionDialog = true
                                    }
                                )
                            )
                        }
                    }
                }
            )
        )

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            items(settingGroups, key = { "setting_${it.title}_${settingGroups.indexOf(it)}" }) { group ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        when (group.title) {
                            context.getString(R.string.settings_display_mode) -> {
                                // Display Mode Button Group
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Icon container with expressive design
                                        Surface(
                                            modifier = Modifier.size(40.dp),
                                            shape = RoundedCornerShape(34.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            tonalElevation = 0.dp
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Settings,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = context.getString(R.string.settings_theme_mode),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = context.getString(R.string.settings_theme_mode_desc),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    ExpressiveButtonGroup(
                                        items = listOf(
                                            context.getString(R.string.settings_theme_system),
                                            context.getString(R.string.settings_theme_light),
                                            context.getString(R.string.settings_theme_dark)
                                        ),
                                        selectedIndex = when {
                                            useSystemTheme -> 0
                                            !darkMode -> 1
                                            else -> 2
                                        },
                                        onItemClick = { index ->
                                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                            when (index) {
                                                0 -> { // System
                                                    appSettings.setUseSystemTheme(true)
                                                }
                                                1 -> { // Light
                                                    appSettings.setUseSystemTheme(false)
                                                    appSettings.setDarkMode(false)
                                                }
                                                2 -> { // Dark
                                                    appSettings.setUseSystemTheme(false)
                                                    appSettings.setDarkMode(true)
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // AMOLED Theme - conditionally shown when Dark mode is selected
                                AnimatedVisibility(
                                    visible = !useSystemTheme && darkMode,
                                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                                ) {
                                    Column {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                        TunerSettingRow(item = group.items[1]) // AMOLED Theme
                                    }
                                }
                            }
                            else -> {
                                // Default rendering for other groups
                                group.items.forEachIndexed { index, item ->
                                    TunerSettingRow(item = item)
                                    if (index < group.items.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tips Card
            item {
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
                                text = context.getString(R.string.theme_good_to_know),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        ThemeTipItem(
                            icon = Icons.Filled.Palette,
                            text = context.getString(R.string.theme_tip_album_art)
                        )
                        ThemeTipItem(
                            icon = Icons.Filled.Wallpaper,
                            text = context.getString(R.string.theme_tip_material_you)
                        )
                        ThemeTipItem(
                            icon = Icons.Filled.FontDownload,
                            text = context.getString(R.string.theme_tip_custom_fonts)
                        )
                    }
                }
            }
        }
        }  // End of CollapsibleHeaderScreen else block
        }  // End of AnimatedContent
    }

    // App Restart Dialog for theme changes
    if (showRestartDialog) {
        AppRestartDialog(
            onDismiss = { showRestartDialog = false },
            onRestart = {
                showRestartDialog = false
                chromahub.rhythm.app.util.AppRestarter.restartApp(context)
            },
            onContinue = {
                showRestartDialog = false
                // Continue without restart
            },
            message = restartDialogMessage
        )
    }

    // Dialogs
    ColorSourceDialog(
        showDialog = showColorSourceDialog,
        onDismiss = { showColorSourceDialog = false },
        selectedColorSource = selectedColorSource,
        onColorSourceSelected = { selectedColorSource = it },
        appSettings = appSettings,
        context = context,
        haptic = haptic
    )

    FontSourceDialog(
        showDialog = showFontSourceDialog,
        onDismiss = { showFontSourceDialog = false },
        selectedFontSource = selectedFontSource,
        onFontSourceSelected = { selectedFontSource = it },
        appSettings = appSettings,
        customFontPath = customFontPath,
        context = context,
        haptic = haptic,
        onShowRestartDialog = { message ->
            showRestartDialog = true
            restartDialogMessage = message
        }
    )

    ColorSchemesDialog(
        showDialog = showColorSchemesDialog,
        onDismiss = { showColorSchemesDialog = false },
        colorSchemes = colorSchemes,
        currentScheme = customColorScheme,
        selectedColorSource = selectedColorSource,
        onSchemeSelected = { scheme ->
            appSettings.setCustomColorScheme(scheme)
            showColorSchemesDialog = false
        },
        appSettings = appSettings,
        context = context,
        haptic = haptic
    )

    CustomColorsDialog(
        showDialog = showCustomColorsDialog,
        onDismiss = { showCustomColorsDialog = false },
        currentScheme = customColorScheme,
        selectedColorSource = selectedColorSource,
        onApply = { primary, secondary, tertiary ->
            val primaryHex = String.format("%06X", (primary.toArgb() and 0xFFFFFF))
            val secondaryHex = String.format("%06X", (secondary.toArgb() and 0xFFFFFF))
            val tertiaryHex = String.format("%06X", (tertiary.toArgb() and 0xFFFFFF))
            val customScheme = "custom_${primaryHex}_${secondaryHex}_${tertiaryHex}"
            appSettings.setCustomColorScheme(customScheme)
            showCustomColorsDialog = false
        },
        appSettings = appSettings,
        context = context,
        haptic = haptic
    )

    FontSelectionDialog(
        showDialog = showFontSelectionDialog,
        onDismiss = { showFontSelectionDialog = false },
        fontOptions = fontOptions,
        currentFont = currentFont,
        selectedFontSource = selectedFontSource,
        onFontSelected = { selectedFont ->
            appSettings.setCustomFont(selectedFont)
            showFontSelectionDialog = false
            showRestartDialog = true
            restartDialogMessage = "Font changed. Restart the app to apply the new font."
        },
        appSettings = appSettings,
        context = context,
        haptic = haptic
    )

    // Festival Selection Dialog with Intensity Controls
    if (showFestivalSelectionDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        
        ModalBottomSheet(
            onDismissRequest = { showFestivalSelectionDialog = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            val festiveContentPadding = 24.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                item {
                    StandardBottomSheetHeader(
                        title = context.getString(R.string.theme_festive_settings),
                        subtitle = context.getString(R.string.settings_choose_festive_theme),
                        visible = true,
                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = festiveContentPadding)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = context.getString(R.string.settings_select_festival),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val festivals = listOf(
                            "CHRISTMAS" to context.getString(R.string.settings_festival_christmas),
                            "NEW_YEAR" to context.getString(R.string.settings_festival_new_year)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            festivals.forEach { (id, name) ->
                                val isSelected = id == festiveThemeType
                                Card(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        appSettings.setFestiveThemeType(id)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceContainerHigh
                                    ),
                                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = context.getString(R.string.ui_selected),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = context.getString(R.string.settings_decoration_intensity),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.settings_intensity),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(festiveThemeIntensity * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = festiveThemeIntensity,
                            onValueChange = { appSettings.setFestiveThemeIntensity(it) },
                            valueRange = 0.1f..1f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.settings_snowflake_size),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(festiveSnowflakeSize * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = festiveSnowflakeSize,
                            onValueChange = { appSettings.setFestiveSnowflakeSize(it) },
                            valueRange = 0.5f..2.0f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = context.getString(R.string.settings_snowflake_display_area),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = festiveSnowflakeArea == "FULL_SCREEN",
                                onClick = { appSettings.setFestiveSnowflakeArea("FULL_SCREEN") },
                                label = { Text(context.getString(R.string.settings_area_full)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = festiveSnowflakeArea == "LEFT_RIGHT_ONLY",
                                onClick = { appSettings.setFestiveSnowflakeArea("LEFT_RIGHT_ONLY") },
                                label = { Text(context.getString(R.string.settings_area_sides)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = festiveSnowflakeArea == "TOP_ONE_THIRD",
                                onClick = { appSettings.setFestiveSnowflakeArea("TOP_ONE_THIRD") },
                                label = { Text(context.getString(R.string.settings_area_top_third)) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = context.getString(R.string.settings_decoration_elements),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DecorationToggleCard(
                                title = context.getString(R.string.settings_snowfall),
                                description = context.getString(R.string.settings_snowfall_desc),
                                icon = Icons.Rounded.AcUnit,
                                isEnabled = festiveShowSnowfall,
                                onToggle = { appSettings.setFestiveShowSnowfall(it) }
                            )
                            DecorationToggleCard(
                                title = context.getString(R.string.settings_top_lights),
                                description = context.getString(R.string.settings_top_lights_desc),
                                icon = Icons.Rounded.Lightbulb,
                                isEnabled = festiveShowTopLights,
                                onToggle = { appSettings.setFestiveShowTopLights(it) }
                            )
                            DecorationToggleCard(
                                title = context.getString(R.string.settings_side_garland),
                                description = context.getString(R.string.settings_side_garland_desc),
                                icon = Icons.Rounded.Park,
                                isEnabled = festiveShowSideGarland,
                                onToggle = { appSettings.setFestiveShowSideGarland(it) }
                            )
                            DecorationToggleCard(
                                title = context.getString(R.string.settings_snow_pile),
                                description = context.getString(R.string.settings_snow_pile_desc),
                                icon = Icons.Rounded.Terrain,
                                isEnabled = festiveShowBottomSnow,
                                onToggle = { appSettings.setFestiveShowBottomSnow(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// Color Source and Font Source Dialogs for Theme Customization
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSourceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    selectedColorSource: ColorSource,
    onColorSourceSelected: (ColorSource) -> Unit,
    appSettings: AppSettings,
    context: Context,
    haptic: HapticFeedback
) {
    if (showDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.theme_color_source),
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
                                text = context.getString(R.string.theme_color_source_desc),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ColorSource.entries.forEach { source ->
                        val isSelected = selectedColorSource == source
                        Card(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                onColorSourceSelected(source)
                                when (source) {
                                    ColorSource.MONET -> {
                                        appSettings.setUseDynamicColors(true)
                                        appSettings.setColorSource("MONET")
                                    }
                                    ColorSource.ALBUM_ART -> {
                                        appSettings.setUseDynamicColors(false)
                                        appSettings.setColorSource("ALBUM_ART")
                                    }
                                    ColorSource.CUSTOM -> {
                                        appSettings.setUseDynamicColors(false)
                                        appSettings.setColorSource("CUSTOM")
                                    }
                                }
                                onDismiss()
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = if (isSelected) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                null
                            },
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
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = source.icon,
                                            contentDescription = null,
                                            tint = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = source.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = source.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Selected",
                                        
                                        modifier = Modifier.size(28.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontSourceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    selectedFontSource: FontSource,
    onFontSourceSelected: (FontSource) -> Unit,
    appSettings: AppSettings,
    customFontPath: String?,
    context: Context,
    haptic: HapticFeedback,
    onShowRestartDialog: (String) -> Unit
) {
    if (showDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.theme_font_source),
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
                                text = context.getString(R.string.theme_font_source_desc),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FontSource.entries.forEach { source ->
                        val isSelected = selectedFontSource == source
                        Card(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                onFontSourceSelected(source)
                                when (source) {
                                    FontSource.SYSTEM -> {
                                        appSettings.setFontSource("SYSTEM")
                                        if (customFontPath == null) {
                                            appSettings.setCustomFont("System")
                                        }
                                    }
                                    FontSource.CUSTOM -> {
                                        appSettings.setFontSource("CUSTOM")
                                    }
                                }
                                onDismiss()
                                onShowRestartDialog("Font source changed. Restart the app to apply the new font.")
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = if (isSelected) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                null
                            },
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
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = source.icon,
                                            contentDescription = null,
                                            tint = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = source.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = source.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Selected",
                                        
                                        modifier = Modifier.size(28.dp)
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
//                                    modifier = Modifier.size(40.dp)
//                                ) {
//                                    Box(
//                                        contentAlignment = Alignment.Center,
//                                        modifier = Modifier.fillMaxSize()
//                                    ) {
//                                        Icon(
//                                            imageVector = source.icon,
//                                            contentDescription = null,
//                                            tint = if (isSelected)
//                                                MaterialTheme.colorScheme.onPrimary
//                                            else
//                                                MaterialTheme.colorScheme.onSurfaceVariant,
//                                            modifier = Modifier.size(20.dp)
//                                        )
//                                    }
//                                }
//
//                                Spacer(modifier = Modifier.width(16.dp))
//
//                                Column(modifier = Modifier.weight(1f)) {
//                                    Text(
//                                        text = source.displayName,
//                                        style = MaterialTheme.typography.titleMedium,
//                                        fontWeight = FontWeight.SemiBold,
//                                        color = if (isSelected)
//                                            MaterialTheme.colorScheme.onPrimaryContainer
//                                        else
//                                            MaterialTheme.colorScheme.onSurface
//                                    )
//                                    Spacer(modifier = Modifier.height(4.dp))
//                                    Text(
//                                        text = source.description,
//                                        style = MaterialTheme.typography.bodySmall,
//                                        color = if (isSelected)
//                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
//                                        else
//                                            MaterialTheme.colorScheme.onSurfaceVariant
//                                    )
//                                }
//
//                                if (isSelected) {
//                                    Icon(
//                                        imageVector = Icons.Filled.CheckCircle,
//                                        contentDescription = "Selected",
//                                        
//                                        modifier = Modifier.size(24.dp)
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

// Color Schemes Dialog for Theme Customization
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSchemesDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    colorSchemes: List<ColorSchemeOption>,
    currentScheme: String,
    selectedColorSource: ColorSource,
    onSchemeSelected: (String) -> Unit,
    appSettings: AppSettings,
    context: Context,
    haptic: HapticFeedback
) {
    if (showDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.theme_color_schemes),
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
                                text = context.getString(R.string.theme_color_schemes_desc),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedColorSource != ColorSource.CUSTOM) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = context.getString(R.string.theme_color_schemes_unavailable),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = context.getString(R.string.theme_color_schemes_switch),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Featured schemes
                        item {
                            Text(
                                text = context.getString(R.string.theme_featured_schemes),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val featuredSchemes = colorSchemes.filter {
                            it.name in listOf("Default", "Warm", "Cool", "Forest", "Rose", "Monochrome")
                        }

                        items(featuredSchemes, key = { "featured_${it.name}" }) { option ->
                            ColorSchemeCard(
                                option = option,
                                isSelected = currentScheme == option.name,
                                onSelect = {
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    onSchemeSelected(option.name)
                                }
                            )
                        }

                        // More schemes
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = context.getString(R.string.theme_more_schemes),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val otherSchemes = colorSchemes.filter {
                            it.name !in listOf("Default", "Warm", "Cool", "Forest", "Rose", "Monochrome")
                        }

                        items(otherSchemes, key = { "other_${it.name}" }) { option ->
                            ColorSchemeCard(
                                option = option,
                                isSelected = currentScheme == option.name,
                                onSelect = {
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    onSchemeSelected(option.name)
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSchemeCard(
    option: ColorSchemeOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced color preview with better visibility
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = option.primaryColor,
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                ) {}
                Surface(
                    shape = CircleShape,
                    color = option.secondaryColor,
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                ) {}
                Surface(
                    shape = CircleShape,
                    color = option.tertiaryColor,
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                ) {}
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// Custom Colors Dialog for Theme Customization
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomColorsDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentScheme: String,
    selectedColorSource: ColorSource,
    onApply: (Color, Color, Color) -> Unit,
    appSettings: AppSettings,
    context: Context,
    haptic: HapticFeedback
) {
    if (showDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        // Parse current custom colors from the scheme name, or use defaults
        val customScheme = parseCustomColorScheme(currentScheme, false)

        var primaryColor by remember(currentScheme) {
            if (customScheme != null) {
                mutableStateOf(customScheme.primary)
            } else {
                mutableStateOf(Color(0xFF5C4AD5)) // Default purple
            }
        }
        var secondaryColor by remember(currentScheme) {
            if (customScheme != null) {
                mutableStateOf(customScheme.secondary)
            } else {
                mutableStateOf(Color(0xFF5D5D6B))
            }
        }
        var tertiaryColor by remember(currentScheme) {
            if (customScheme != null) {
                mutableStateOf(customScheme.tertiary)
            } else {
                mutableStateOf(Color(0xFFFFDDB6))
            }
        }

        var selectedColorType by remember { mutableStateOf(ColorType.PRIMARY) }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Header
                AnimateIn {
                    StandardBottomSheetHeader(
                        title = context.getString(R.string.theme_custom_picker),
                        subtitle = context.getString(R.string.theme_custom_picker_desc),
                        visible = showContent
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable content
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (selectedColorSource != ColorSource.CUSTOM) {
                        // Unavailable state
                        item {
                            AnimateIn {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = null,
                                            
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = context.getString(R.string.theme_custom_colors_unavailable),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = context.getString(R.string.theme_custom_colors_switch),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Main Color Customization Card
                        item {
                            AnimateIn {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        // Color Preview Section
                                        Text(
                                            text = "Color Preview",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        // Color row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            ColorPreviewItem(
                                                label = "Primary",
                                                color = primaryColor,
                                                isSelected = selectedColorType == ColorType.PRIMARY,
                                                onClick = {
                                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                                    selectedColorType = ColorType.PRIMARY
                                                }
                                            )
                                            ColorPreviewItem(
                                                label = "Secondary",
                                                color = secondaryColor,
                                                isSelected = selectedColorType == ColorType.SECONDARY,
                                                onClick = {
                                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                                    selectedColorType = ColorType.SECONDARY
                                                }
                                            )
                                            ColorPreviewItem(
                                                label = "Tertiary",
                                                color = tertiaryColor,
                                                isSelected = selectedColorType == ColorType.TERTIARY,
                                                onClick = {
                                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                                    selectedColorType = ColorType.TERTIARY
                                                }
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        // Color Picker Section
                                        Text(
                                            text = "Color Picker",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        when (selectedColorType) {
                                            ColorType.PRIMARY -> ColorPickerControls(
                                                color = primaryColor,
                                                onColorChange = { primaryColor = it }
                                            )
                                            ColorType.SECONDARY -> ColorPickerControls(
                                                color = secondaryColor,
                                                onColorChange = { secondaryColor = it }
                                            )
                                            ColorType.TERTIARY -> ColorPickerControls(
                                                color = tertiaryColor,
                                                onColorChange = { tertiaryColor = it }
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        // Color Inspirations Section
                                        Text(
                                            text = "Color Inspirations",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        // Warm colors
                                        PresetColorRow(
                                            title = "Warm & Cozy",
                                            colors = listOf(
                                                Color(0xFFFF6B35), Color(0xFFFF8F00), Color(0xFFFF6F00),
                                                Color(0xFFFF5722), Color(0xFFE91E63), Color(0xFF9C27B0)
                                            ),
                                            onColorSelected = { color ->
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                                when (selectedColorType) {
                                                    ColorType.PRIMARY -> primaryColor = color
                                                    ColorType.SECONDARY -> secondaryColor = color
                                                    ColorType.TERTIARY -> tertiaryColor = color
                                                }
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Cool colors
                                        PresetColorRow(
                                            title = "Cool & Fresh",
                                            colors = listOf(
                                                Color(0xFF1E88E5), Color(0xFF0097A7), Color(0xFF00ACC1),
                                                Color(0xFF00BCD4), Color(0xFF4DD0E1), Color(0xFF26A69A)
                                            ),
                                            onColorSelected = { color ->
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                                when (selectedColorType) {
                                                    ColorType.PRIMARY -> primaryColor = color
                                                    ColorType.SECONDARY -> secondaryColor = color
                                                    ColorType.TERTIARY -> tertiaryColor = color
                                                }
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Nature colors
                                        PresetColorRow(
                                            title = "Nature & Earth",
                                            colors = listOf(
                                                Color(0xFF2E7D32), Color(0xFF388E3C), Color(0xFF4CAF50),
                                                Color(0xFF66BB6A), Color(0xFF81C784), Color(0xFFA5D6A7)
                                            ),
                                            onColorSelected = { color ->
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                                when (selectedColorType) {
                                                    ColorType.PRIMARY -> primaryColor = color
                                                    ColorType.SECONDARY -> secondaryColor = color
                                                    ColorType.TERTIARY -> tertiaryColor = color
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Action Buttons
                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                        onApply(primaryColor, secondaryColor, tertiaryColor)
                                        val primaryHex = String.format("%06X", (primaryColor.toArgb() and 0xFFFFFF))
                                        val secondaryHex = String.format("%06X", (secondaryColor.toArgb() and 0xFFFFFF))
                                        val tertiaryHex = String.format("%06X", (tertiaryColor.toArgb() and 0xFFFFFF))
                                        val customScheme = "custom_${primaryHex}_${secondaryHex}_${tertiaryHex}"
                                        appSettings.setCustomColorScheme(customScheme)
                                        Toast.makeText(context, context.getString(R.string.theme_colors_applied), Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(context.getString(R.string.ui_apply))
                                }
                            }
                        }

                        // Bottom padding
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
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

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 350, delayMillis = 0),
        label = "alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    val translationY by animateFloatAsState(
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
private fun ExpressiveColorPickerControls(
    color: Color,
    onColorChange: (Color) -> Unit
) {
    val context = LocalContext.current
    val hsl = remember(color) { color.toHSL() }

    var hue by remember(color) { mutableStateOf(hsl.hue) }
    var saturation by remember(color) { mutableStateOf(hsl.saturation) }
    var lightness by remember(color) { mutableStateOf(hsl.lightness) }

    var showAdvanced by remember { mutableStateOf(false) }

    // Update color when HSL values change
    LaunchedEffect(hue, saturation, lightness) {
        val newColor = HSLColor(hue, saturation, lightness).toColor()
        onColorChange(newColor)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Enhanced Current color display with gradient background
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shadowElevation = 4.dp,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Subtle pattern overlay
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Palette,
                                contentDescription = null,
                                tint = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = String.format("#%06X", (color.toArgb() and 0xFFFFFF)),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.9f) else Color.White,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Color properties
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.alpha(0.8f)
                        ) {
                            Text(
                                text = "H:${hue.toInt()}°",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "S:${(saturation * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "L:${(lightness * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enhanced Hue Slider with gradient preview
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hue",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "${hue.toInt()}°",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hue slider with enhanced gradient
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = (0..360 step 10).map { h ->
                                    HSLColor(h.toFloat(), 1f, 0.5f).toColor()
                                }
                            )
                        )
                ) {
                    Slider(
                        value = hue,
                        onValueChange = { hue = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxSize(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Enhanced Saturation Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saturation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "${(saturation * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.LightGray,
                                    HSLColor(hue, 1f, lightness).toColor()
                                )
                            )
                        )
                ) {
                    Slider(
                        value = saturation,
                        onValueChange = { saturation = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxSize(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Enhanced Lightness Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lightness",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "${(lightness * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black,
                                    HSLColor(hue, saturation, 0.5f).toColor(),
                                    Color.White
                                )
                            )
                        )
                ) {
                    Slider(
                        value = lightness,
                        onValueChange = { lightness = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxSize(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Advanced RGB controls toggle with enhanced design
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                        
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = context.getString(R.string.theme_advanced_rgb),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                TunerAnimatedSwitch(
                    checked = showAdvanced,
                    onCheckedChange = { showAdvanced = it }
                )
            }
        }

        // Advanced RGB controls with enhanced design
        AnimatedVisibility(
            visible = showAdvanced,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val red = (color.red * 255).toInt()
                    val green = (color.green * 255).toInt()
                    val blue = (color.blue * 255).toInt()

                    var redValue by remember(color) { mutableStateOf(red.toFloat()) }
                    var greenValue by remember(color) { mutableStateOf(green.toFloat()) }
                    var blueValue by remember(color) { mutableStateOf(blue.toFloat()) }

                    // Update HSL when RGB changes
                    LaunchedEffect(redValue, greenValue, blueValue) {
                        val rgbColor = Color(
                            red = redValue / 255f,
                            green = greenValue / 255f,
                            blue = blueValue / 255f
                        )
                        val newHsl = rgbColor.toHSL()
                        hue = newHsl.hue
                        saturation = newHsl.saturation
                        lightness = newHsl.lightness
                    }

                    Text(
                        text = "RGB Values",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ExpressiveColorSlider(
                        label = "Red",
                        value = redValue,
                        onValueChange = { redValue = it },
                        color = Color.Red,
                        valueRange = 0f..255f
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExpressiveColorSlider(
                        label = "Green",
                        value = greenValue,
                        onValueChange = { greenValue = it },
                        color = Color.Green,
                        valueRange = 0f..255f
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExpressiveColorSlider(
                        label = "Blue",
                        value = blueValue,
                        onValueChange = { blueValue = it },
                        color = Color.Blue,
                        valueRange = 0f..255f
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetColorRow(
    title: String,
    colors: List<Color>,
    onColorSelected: (Color) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(colors) { color ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = color,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onColorSelected(color) },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    // Empty content
                }
            }
        }
    }
}

@Composable
private fun ExpressiveColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = value.toInt().toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = color.copy(alpha = 0.3f),
                    activeTickColor = color,
                    inactiveTickColor = color.copy(alpha = 0.3f)
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// Helper function to get color temperature (approximate)
private fun getColorTemperature(color: Color): Int {
    // Simplified color temperature calculation
    val r = color.red
    val g = color.green
    val b = color.blue

    // Rough approximation using RGB ratios
    val temperature = if (r > g && r > b) {
        2000 + (r - b) * 3000 // Warm
    } else if (b > r && b > g) {
        6000 + (b - r) * 4000 // Cool
    } else {
        4000 + (g - r) * 2000 // Neutral
    }

    return temperature.toInt().coerceIn(2000, 10000)
}

@Composable
private fun ColorPickerControls(
    color: Color,
    onColorChange: (Color) -> Unit
) {
    val context = LocalContext.current
    val hsl = remember(color) { color.toHSL() }

    var hue by remember(color) { mutableStateOf(hsl.hue) }
    var saturation by remember(color) { mutableStateOf(hsl.saturation) }
    var lightness by remember(color) { mutableStateOf(hsl.lightness) }

    var showAdvanced by remember { mutableStateOf(false) }

    // Update color when HSL values change
    LaunchedEffect(hue, saturation, lightness) {
        val newColor = HSLColor(hue, saturation, lightness).toColor()
        onColorChange(newColor)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Current color display with hex code
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = null,
                            tint = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format("#%06X", (color.toArgb() and 0xFFFFFF)),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.9f) else Color.White,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hue Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hue",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Text(
                    text = "${hue.toInt()}°",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hue slider with color gradient
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = (0..360 step 20).map { h ->
                                HSLColor(h.toFloat(), 1f, 0.5f).toColor()
                            }
                        )
                    )
            ) {
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f,
                    modifier = Modifier.fillMaxSize(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Saturation Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saturation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Text(
                    text = "${(saturation * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.LightGray,
                                HSLColor(hue, 1f, lightness).toColor()
                            )
                        )
                    )
            ) {
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxSize(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lightness Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lightness",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Text(
                    text = "${(lightness * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black,
                                HSLColor(hue, saturation, 0.5f).toColor(),
                                Color.White
                            )
                        )
                    )
            ) {
                Slider(
                    value = lightness,
                    onValueChange = { lightness = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxSize(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Advanced RGB controls toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = context.getString(R.string.theme_advanced_rgb),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TunerAnimatedSwitch(
                checked = showAdvanced,
                onCheckedChange = { showAdvanced = it }
            )
        }

        // Advanced RGB controls
        AnimatedVisibility(
            visible = showAdvanced,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                val red = (color.red * 255).toInt()
                val green = (color.green * 255).toInt()
                val blue = (color.blue * 255).toInt()

                var redValue by remember(color) { mutableStateOf(red.toFloat()) }
                var greenValue by remember(color) { mutableStateOf(green.toFloat()) }
                var blueValue by remember(color) { mutableStateOf(blue.toFloat()) }

                // Update HSL when RGB changes
                LaunchedEffect(redValue, greenValue, blueValue) {
                    val rgbColor = Color(
                        red = redValue / 255f,
                        green = greenValue / 255f,
                        blue = blueValue / 255f
                    )
                    val newHsl = rgbColor.toHSL()
                    hue = newHsl.hue
                    saturation = newHsl.saturation
                    lightness = newHsl.lightness
                }

                ColorSlider(
                    label = "Red",
                    value = redValue,
                    onValueChange = { redValue = it },
                    color = Color.Red,
                    valueRange = 0f..255f
                )

                Spacer(modifier = Modifier.height(16.dp))

                ColorSlider(
                    label = "Green",
                    value = greenValue,
                    onValueChange = { greenValue = it },
                    color = Color.Green,
                    valueRange = 0f..255f
                )

                Spacer(modifier = Modifier.height(16.dp))

                ColorSlider(
                    label = "Blue",
                    value = blueValue,
                    onValueChange = { blueValue = it },
                    color = Color.Blue,
                    valueRange = 0f..255f
                )
            }
        }
    }
}

@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = value.toInt().toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.3f),
                activeTickColor = color,
                inactiveTickColor = color.copy(alpha = 0.3f)
            )
        )
    }
}

private enum class ColorType {
    PRIMARY, SECONDARY, TERTIARY
}

// Font Selection Dialog for Theme Customization
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    fontOptions: List<FontOption>,
    currentFont: String,
    selectedFontSource: FontSource,
    onFontSelected: (String) -> Unit,
    appSettings: AppSettings,
    context: Context,
    haptic: HapticFeedback
) {
    if (showDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        LaunchedEffect(Unit) {
            delay(100)
            showContent = true
        }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .graphicsLayer(alpha = contentAlpha)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.theme_font_selection),
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
                                text = context.getString(R.string.theme_font_selection_desc),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedFontSource != FontSource.SYSTEM) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = context.getString(R.string.theme_system_fonts_unavailable),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = context.getString(R.string.theme_system_fonts_switch),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(fontOptions, key = { "font_${it.name}" }) { option ->
                            FontCard(
                                option = option,
                                isSelected = currentFont == option.name,
                                onSelect = {
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    onFontSelected(option.name)
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FontCard(
    option: FontOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = option.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font preview text
            Surface(
                color = if (isSelected)
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "The quick brown fox jumps over the lazy dog",
                    style = getFontPreviewStyle(option.name),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// API Management Screen
@Composable
fun ApiManagementSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val appSettings = AppSettings.getInstance(context)
    val scope = rememberCoroutineScope()

    // API states
    val deezerApiEnabled by appSettings.deezerApiEnabled.collectAsState()
    val lrclibApiEnabled by appSettings.lrclibApiEnabled.collectAsState()
    val ytMusicApiEnabled by appSettings.ytMusicApiEnabled.collectAsState()
    val spotifyApiEnabled by appSettings.spotifyApiEnabled.collectAsState()
    val spotifyClientId by appSettings.spotifyClientId.collectAsState()
    val spotifyClientSecret by appSettings.spotifyClientSecret.collectAsState()

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_api_management),
        showBackButton = true,
        onBackClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onBackClick()
        }
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }


            // API Services
            item {
                Text(
                    text = context.getString(R.string.external_services),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Deezer API - Only show if enabled in BuildConfig
                        if (chromahub.rhythm.app.BuildConfig.ENABLE_DEEZER) {
                            ApiServiceRow(
                                title = "Deezer",
                                description = "Free artist images and album artwork - no setup needed",
                                status = "Ready",
                                isConfigured = true,
                                isEnabled = deezerApiEnabled,
                                icon = Icons.Default.Public,
                                showToggle = true,
                                onToggle = { enabled -> appSettings.setDeezerApiEnabled(enabled) },
                                onClick = { /* No configuration needed */ }
                            )
                            
                            // Show divider only if there are more items below
                            if (chromahub.rhythm.app.BuildConfig.ENABLE_LRCLIB || 
                                chromahub.rhythm.app.BuildConfig.ENABLE_YOUTUBE_MUSIC) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }

                        // LRCLib API - Only show if enabled in BuildConfig
                        if (chromahub.rhythm.app.BuildConfig.ENABLE_LRCLIB) {
                            ApiServiceRow(
                                title = "LRCLib",
                                description = "Free line-by-line synced lyrics (Fallback)",
                                status = "Ready",
                                isConfigured = true,
                                isEnabled = lrclibApiEnabled,
                                icon = RhythmIcons.Queue,
                                showToggle = true,
                                onToggle = { enabled -> appSettings.setLrcLibApiEnabled(enabled) },
                                onClick = { /* No configuration needed */ }
                            )
                            
                            if (chromahub.rhythm.app.BuildConfig.ENABLE_YOUTUBE_MUSIC) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }

                        // YouTube Music API - Only show if enabled in BuildConfig
                        if (chromahub.rhythm.app.BuildConfig.ENABLE_YOUTUBE_MUSIC) {
                            ApiServiceRow(
                                title = "YouTube Music",
                                description = "Fallback for artist images and album artwork",
                                status = "Ready",
                                isConfigured = true,
                                isEnabled = ytMusicApiEnabled,
                                icon = RhythmIcons.Album,
                                showToggle = true,
                                onToggle = { enabled -> appSettings.setYTMusicApiEnabled(enabled) },
                                onClick = { /* No configuration needed */ }
                            )
                        }

                        // Always show divider before GitHub (which is always present)
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        // GitHub API
                        ApiServiceRow(
                            title = "GitHub",
                            description = "App updates and release information",
                            status = "Ready",
                            isConfigured = true,
                            isEnabled = true, // Always enabled for updates
                            icon = RhythmIcons.Download,
                            showToggle = false, // Can't disable update checks
                            onClick = { /* No configuration needed */ }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.api_services),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        Text(
                            text = context.getString(R.string.external_services_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

// Sleep Timer Settings - Removed (now accessed via navigation in player screen)
// Sleep timer launches as bottom sheet from player or via Settings -> Sleep Timer
// No dedicated settings screen needed

// Crash Log History Settings Screen
@Composable
fun CrashLogHistorySettingsScreen(onBackClick: () -> Unit, appSettings: AppSettings) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val crashLogHistory by appSettings.crashLogHistory.collectAsState()

    var showLogDetailDialog by remember { mutableStateOf(false) }
    var selectedLog: String? by remember { mutableStateOf(null) }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_crash_log),
        showBackButton = true,
        onBackClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onBackClick()
        }
    ) { modifier ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            if (crashLogHistory.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "No crashes",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = context.getString(R.string.settings_no_crash_logs),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Crash logs section
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.BugReport,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = context.getString(R.string.crash_reports),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            crashLogHistory.forEachIndexed { index, entry ->
                                CrashLogEntryCard(entry = entry) {
                                    selectedLog = entry.log
                                    showLogDetailDialog = true
                                }
                                if (index < crashLogHistory.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Action buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            appSettings.clearCrashLogHistory()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = crashLogHistory.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(context.getString(R.string.settings_clear_all_logs))
                    }
                }
            }
        }
    }

    // Log detail dialog
    if (showLogDetailDialog) {
        AlertDialog(
            onDismissRequest = { showLogDetailDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.BugReport,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(context.getString(R.string.settings_crash_log_details)) },
            text = {
                OutlinedTextField(
                    value = selectedLog ?: "No log details available.",
                    onValueChange = { /* Read-only */ },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Rhythm Crash Log", selectedLog)
                        clipboard.setPrimaryClip(clip)
                        showLogDetailDialog = false
                        Toast.makeText(context, context.getString(R.string.settings_log_copied), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.settings_copy_log))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogDetailDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun CrashLogEntryCard(entry: chromahub.rhythm.app.shared.data.model.CrashLogEntry, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Crashed on: ${dateFormat.format(Date(entry.timestamp))}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.log.lines().firstOrNull() ?: "No details available.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "View details",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ThemeTipItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun MediaScanTipItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// Lyrics Source Settings Screen
@Composable
fun LyricsSourceSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val hapticFeedback = LocalHapticFeedback.current

    val lyricsSourcePreference by appSettings.lyricsSourcePreference.collectAsState()

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_lyrics_source),
        showBackButton = true,
        onBackClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onBackClick()
        }
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Text(
                    text = context.getString(R.string.lyrics_source_priority_title),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Text(
                    text = context.getString(R.string.lyrics_source_priority_desc_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
            }

            val sourceOptions = listOf<Pair<chromahub.rhythm.app.shared.data.model.LyricsSourcePreference, Triple<String, String, androidx.compose.ui.graphics.vector.ImageVector>>>(
                chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.EMBEDDED_FIRST to Triple(
                    "Embedded First",
                    "Prefer lyrics embedded in audio files, fallback to online APIs",
                    Icons.Default.MusicNote
                ),
                chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.API_FIRST to Triple(
                    "Online First",
                    "Prefer online APIs (Apple Music, LRCLib), fallback to embedded",
                    Icons.Default.CloudQueue
                ),
                chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.LOCAL_FIRST to Triple(
                    "Local First",
                    "Prefer local .lrc files, then embedded lyrics, then online APIs",
                    Icons.Default.Storage
                )
            )

            items(sourceOptions, key = { (pref, _) -> "source_${pref.name}" }) { (preference, info) ->
                val (title, description, icon) = info
                val isSelected = lyricsSourcePreference == preference

                Card(
                    onClick = {
                        HapticUtils.performHapticFeedback(
                            context,
                            hapticFeedback,
                            HapticFeedbackType.TextHandleMove
                        )
                        appSettings.setLyricsSourcePreference(preference)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = if (isSelected) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else null,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 2.dp else 0.dp
                    ),
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
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Info Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "About Lyrics Sources",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        Text(
                            text = "• Embedded lyrics are stored in your audio files\n" +
                                    "• Online APIs provide high-quality synced lyrics\n" +
                                    "• Apple Music offers word-by-word sync\n" +
                                    "• LRCLib provides free line-by-line sync",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenCustomizationSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val haptic = LocalHapticFeedback.current

    // State for home section order bottom sheet
    var showHomeSectionOrderSheet by remember { mutableStateOf(false) }

    // Collect all home screen settings
    val showGreeting by appSettings.homeShowGreeting.collectAsState()
    val showRecentlyPlayed by appSettings.homeShowRecentlyPlayed.collectAsState()
    val showDiscoverCarousel by appSettings.homeShowDiscoverCarousel.collectAsState()
    val showArtists by appSettings.homeShowArtists.collectAsState()
    val showNewReleases by appSettings.homeShowNewReleases.collectAsState()
    val showRecentlyAdded by appSettings.homeShowRecentlyAdded.collectAsState()
    val showRecommended by appSettings.homeShowRecommended.collectAsState()
    val showListeningStats by appSettings.homeShowListeningStats.collectAsState()
    val discoverAutoScroll by appSettings.homeDiscoverAutoScroll.collectAsState()
    val discoverAutoScrollInterval by appSettings.homeDiscoverAutoScrollInterval.collectAsState()
    val discoverItemCount by appSettings.homeDiscoverItemCount.collectAsState()
    val recentlyPlayedCount by appSettings.homeRecentlyPlayedCount.collectAsState()
    val artistsCount by appSettings.homeArtistsCount.collectAsState()
    val newReleasesCount by appSettings.homeNewReleasesCount.collectAsState()
    val recentlyAddedCount by appSettings.homeRecentlyAddedCount.collectAsState()
    val recommendedCount by appSettings.homeRecommendedCount.collectAsState()
    val carouselHeight by appSettings.homeCarouselHeight.collectAsState()
    val discoverCarouselStyle by appSettings.homeDiscoverCarouselStyle.collectAsState()

    // Discover Widget visibility settings
    val discoverShowAlbumName by appSettings.homeDiscoverShowAlbumName.collectAsState()
    val discoverShowArtistName by appSettings.homeDiscoverShowArtistName.collectAsState()
    val discoverShowYear by appSettings.homeDiscoverShowYear.collectAsState()
    val discoverShowPlayButton by appSettings.homeDiscoverShowPlayButton.collectAsState()
    val discoverShowGradient by appSettings.homeDiscoverShowGradient.collectAsState()

    // Show bottom sheet if requested
    if (showHomeSectionOrderSheet) {
        HomeSectionOrderBottomSheet(
            onDismiss = { showHomeSectionOrderSheet = false },
            appSettings = appSettings
        )
    }

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_home_screen),
        showBackButton = true,
        onBackClick = {
            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
            onBackClick()
        }
    ) { modifier ->
        val lazyListState = rememberSaveable(
            key = "home_screen_settings_scroll_state",
            saver = LazyListStateSaver
        ) {
            androidx.compose.foundation.lazy.LazyListState()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            // ==================== HEADER CUSTOMIZATION ====================
            item(key = "header_customization_header", contentType = "section_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_header_customization),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            
            // Consolidated Header Settings Card
            item(key = "header_settings_card", contentType = "settings_card") {
                val collapseBehavior by appSettings.headerCollapseBehavior.collectAsState()
                val displayMode by appSettings.homeHeaderDisplayMode.collectAsState()
                val visibilityMode by appSettings.homeAppIconVisibility.collectAsState()
                
                val displayLabel = when (displayMode) {
                    0 -> "icon"
                    1 -> "name"
                    2 -> "icon & name"
                    else -> "content"
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Always Start Collapsed Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Icon container with expressive design
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(34.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    tonalElevation = 0.dp
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.UnfoldLess,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = context.getString(R.string.settings_always_start_collapsed),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = context.getString(R.string.settings_start_collapsed),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            TunerAnimatedSwitch(
                                checked = collapseBehavior == 1,
                                onCheckedChange = { 
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    appSettings.setHeaderCollapseBehavior(if (it) 1 else 0) 
                                }
                            )
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        
                        // Header Display Mode
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon container with expressive design
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(34.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    tonalElevation = 0.dp
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Stars,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = context.getString(R.string.settings_header_display),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = context.getString(R.string.settings_choose_header_content),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            ExpressiveButtonGroup(
                                items = listOf("Icon", "Name", "Both"),
                                selectedIndex = displayMode,
                                onItemClick = { index ->
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    appSettings.setHomeHeaderDisplayMode(index)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Hide visibility settings when "Both" is selected since it always shows both
                        AnimatedVisibility(visible = displayMode != 2) {
                            Column {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                
                                // Visibility Mode
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Icon container with expressive design
                                        Surface(
                                            modifier = Modifier.size(40.dp),
                                            shape = RoundedCornerShape(34.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            tonalElevation = 0.dp
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Visibility,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = context.getString(R.string.settings_visibility),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "When to show $displayLabel in header",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    ExpressiveButtonGroup(
                                        items = listOf("Always", "Expanded", "Collapsed"),
                                        selectedIndex = visibilityMode,
                                        onItemClick = { index ->
                                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                            appSettings.setHomeAppIconVisibility(index)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==================== GREETING SETTINGS ====================
            item(key = "greeting_settings_header", contentType = "section_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Greeting",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            item(key = "greeting_toggle_card", contentType = "settings_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                appSettings.setHomeShowGreeting(!showGreeting)
                            }
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(34.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                tonalElevation = 0.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Face,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = context.getString(R.string.settings_show_greeting),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = context.getString(R.string.settings_show_greeting_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        TunerAnimatedSwitch(
                            checked = showGreeting,
                            onCheckedChange = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                appSettings.setHomeShowGreeting(it)
                            }
                        )
                    }
                }
            }

            // ==================== SECTION ORDER & VISIBILITY ====================
            item(key = "section_order_header", contentType = "section_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = context.getString(R.string.settings_section_order_visibility),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            item(key = "section_order_button", contentType = "action_button") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            showHomeSectionOrderSheet = true
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Icon container with expressive design
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(34.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                tonalElevation = 0.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Reorder,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = context.getString(R.string.settings_reorder_toggle_sections),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = context.getString(R.string.settings_customize_home_layout),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // ==================== WIDGET ITEM COUNTS ====================
            item(key = "widget_counts_settings", contentType = "slider_group") {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = context.getString(R.string.settings_widget_item_counts),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            var firstItem = true
                            
                            AnimatedVisibility(visible = showRecentlyPlayed) {
                                Column {
                                    if (!firstItem) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                    HomeSettingsSliderRow(
                                        icon = Icons.Rounded.History,
                                        title = "Recently Played",
                                        description = "$recentlyPlayedCount songs",
                                        value = recentlyPlayedCount.toFloat(),
                                        valueRange = 3f..12f,
                                        steps = 8,
                                        onValueChange = { appSettings.setHomeRecentlyPlayedCount(it.toInt()) }
                                    )
                                }
                            }
                            
                            if (showRecentlyPlayed) { firstItem = false }

                            AnimatedVisibility(visible = showArtists) {
                                Column {
                                    if (!firstItem) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                    HomeSettingsSliderRow(
                                        icon = Icons.Rounded.People,
                                        title = "Top Artists",
                                        description = "$artistsCount artists",
                                        value = artistsCount.toFloat(),
                                        valueRange = 4f..20f,
                                        steps = 15,
                                        onValueChange = { appSettings.setHomeArtistsCount(it.toInt()) }
                                    )
                                }
                            }
                            
                            if (showArtists) { firstItem = false }

                            AnimatedVisibility(visible = showNewReleases) {
                                Column {
                                    if (!firstItem) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                    HomeSettingsSliderRow(
                                        icon = Icons.Rounded.NewReleases,
                                        title = "New Releases",
                                        description = "$newReleasesCount albums",
                                        value = newReleasesCount.toFloat(),
                                        valueRange = 4f..20f,
                                        steps = 15,
                                        onValueChange = { appSettings.setHomeNewReleasesCount(it.toInt()) }
                                    )
                                }
                            }
                            
                            if (showNewReleases) { firstItem = false }

                            AnimatedVisibility(visible = showRecentlyAdded) {
                                Column {
                                    if (!firstItem) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                    HomeSettingsSliderRow(
                                        icon = Icons.Rounded.LibraryAdd,
                                        title = "Recently Added",
                                        description = "$recentlyAddedCount albums",
                                        value = recentlyAddedCount.toFloat(),
                                        valueRange = 4f..20f,
                                        steps = 15,
                                        onValueChange = { appSettings.setHomeRecentlyAddedCount(it.toInt()) }
                                    )
                                }
                            }
                            
                            if (showRecentlyAdded) { firstItem = false }

                            AnimatedVisibility(visible = showRecommended) {
                                Column {
                                    if (!firstItem) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                    }
                                    HomeSettingsSliderRow(
                                        icon = Icons.Rounded.Recommend,
                                        title = "Recommended",
                                        description = "$recommendedCount songs",
                                        value = recommendedCount.toFloat(),
                                        valueRange = 2f..8f,
                                        steps = 5,
                                        onValueChange = { appSettings.setHomeRecommendedCount(it.toInt()) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==================== DISCOVER CAROUSEL SETTINGS ====================
            item(key = "discover_carousel_settings", contentType = "settings_card") {
                AnimatedVisibility(visible = showDiscoverCarousel) {
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = context.getString(R.string.settings_discover_carousel),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                // Carousel Style Selector
                                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ViewCarousel,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                                                .padding(8.dp),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Carousel Style",
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = context.getString(R.string.settings_choose_album_display),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    ExpressiveButtonGroup(
                                        items = listOf("Default", "Hero"),
                                        selectedIndex = discoverCarouselStyle,
                                        onItemClick = { index ->
                                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                            appSettings.setHomeDiscoverCarouselStyle(index)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Rounded.PlayCircle,
                                        "Auto-Scroll",
                                        "Automatically cycle through albums",
                                        toggleState = discoverAutoScroll,
                                        onToggleChange = { appSettings.setHomeDiscoverAutoScroll(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Rounded.Album,
                                        "Album Name",
                                        "Show album title on card",
                                        toggleState = discoverShowAlbumName,
                                        onToggleChange = { appSettings.setHomeDiscoverShowAlbumName(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Rounded.Person,
                                        "Artist Name",
                                        "Show artist name on card",
                                        toggleState = discoverShowArtistName,
                                        onToggleChange = { appSettings.setHomeDiscoverShowArtistName(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Rounded.CalendarToday,
                                        "Release Year",
                                        "Show album release year",
                                        toggleState = discoverShowYear,
                                        onToggleChange = { appSettings.setHomeDiscoverShowYear(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Rounded.PlayArrow,
                                        "Play Button",
                                        "Show quick play button",
                                        toggleState = discoverShowPlayButton,
                                        onToggleChange = { appSettings.setHomeDiscoverShowPlayButton(it) }
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                TunerSettingRow(
                                    item = SettingItem(
                                        Icons.Rounded.Gradient,
                                        "Gradient Overlay",
                                        "Show gradient behind text",
                                        toggleState = discoverShowGradient,
                                        onToggleChange = { appSettings.setHomeDiscoverShowGradient(it) }
                                    )
                                )
                                
                                AnimatedVisibility(visible = discoverAutoScroll) {
                                    Column {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                        )
                                        HomeSettingsSliderRow(
                                            icon = Icons.Rounded.Timer,
                                            title = "Auto-Scroll Interval",
                                            description = "$discoverAutoScrollInterval seconds",
                                            value = discoverAutoScrollInterval.toFloat(),
                                            valueRange = 2f..15f,
                                            steps = 12,
                                            onValueChange = { appSettings.setHomeDiscoverAutoScrollInterval(it.toInt()) }
                                        )
                                    }
                                }
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                HomeSettingsSliderRow(
                                    icon = Icons.Rounded.ViewCarousel,
                                    title = "Album Count",
                                    description = "$discoverItemCount albums",
                                    value = discoverItemCount.toFloat(),
                                    valueRange = 3f..12f,
                                    steps = 8,
                                    onValueChange = { appSettings.setHomeDiscoverItemCount(it.toInt()) }
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                HomeSettingsSliderRow(
                                    icon = Icons.Rounded.Height,
                                    title = "Carousel Height",
                                    description = "${carouselHeight}dp",
                                    value = carouselHeight.toFloat(),
                                    valueRange = 180f..320f,
                                    steps = 13,
                                    onValueChange = { appSettings.setHomeCarouselHeight(it.toInt()) }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Tips Card
            item(key = "tips_card", contentType = "tips") {
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
                                text = "Quick Tips",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        HomeScreenTipItem(
                            icon = Icons.Default.Visibility,
                            text = "Toggle widgets to customize your home screen"
                        )
                        HomeScreenTipItem(
                            icon = Icons.Default.Speed,
                            text = "Disable unused sections for faster loading"
                        )
                        HomeScreenTipItem(
                            icon = Icons.Default.Album,
                            text = "Discover carousel showcases featured albums"
                        )
                        HomeScreenTipItem(
                            icon = Icons.Default.TrendingUp,
                            text = "Statistics update based on listening habits"
                        )
                    }
                }
            }

            item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun HomeScreenTipItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun CarouselStyleSelector(
    selectedStyle: Int,
    onStyleSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val styles = listOf(
        Triple(0, "Default", "2 side peek albums"),
        Triple(1, "Hero", "1 side peek album")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                // Icon container with expressive design matching TunerSettingRow
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(34.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    tonalElevation = 0.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ViewCarousel,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Carousel Style",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Choose how albums are displayed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                styles.forEach { (style, title, description) ->
                    val isSelected = selectedStyle == style

                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            onStyleSelected(style)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isSelected)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else
                            null
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (style) {
                                    0 -> Icons.Rounded.ViewColumn
                                    else -> Icons.Rounded.CenterFocusWeak
                                },
                                contentDescription = null,
                                tint = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSettingsSliderCard(
    icon: ImageVector,
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = value,
                onValueChange = {
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onValueChange(it)
                },
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun HomeSettingsSliderRow(
    icon: ImageVector,
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon container with expressive design matching TunerSettingRow
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(34.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                tonalElevation = 0.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = value,
            onValueChange = {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                onValueChange(it)
            },
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 56.dp, end = 8.dp)
        )
    }
}

// ============================================================================
// EXPRESSIVE SHAPES SETTINGS SCREEN
// ============================================================================

/**
 * Data class for shape option display
 */
private data class ShapeOption(
    val id: String,
    val displayName: String,
    val description: String,
    val category: String
)

/**
 * Data class for shape preset display
 */
private data class PresetOption(
    val id: String,
    val displayName: String,
    val description: String,
    val icon: ImageVector
)

/**
 * Settings screen for configuring Material 3 Expressive Shapes
 * Allows users to customize organic shapes for different UI components
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveShapesSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    // Collect settings states
    val expressiveShapesEnabled by appSettings.expressiveShapesEnabled.collectAsState()
    val currentPreset by appSettings.expressiveShapePreset.collectAsState()
    val shapeAlbumArt by appSettings.expressiveShapeAlbumArt.collectAsState()
    val shapePlayerArt by appSettings.expressiveShapePlayerArt.collectAsState()
    val shapeSongArt by appSettings.expressiveShapeSongArt.collectAsState()
    val shapePlaylistArt by appSettings.expressiveShapePlaylistArt.collectAsState()
    val shapeArtistArt by appSettings.expressiveShapeArtistArt.collectAsState()
    val shapePlayerControls by appSettings.expressiveShapePlayerControls.collectAsState()
    val shapeMiniPlayer by appSettings.expressiveShapeMiniPlayer.collectAsState()
    
    // Dialog states
    var showPresetDialog by remember { mutableStateOf(false) }
    var showShapePickerDialog by remember { mutableStateOf<String?>(null) } // Target ID
    
    // Define presets
    val presets = remember {
        listOf(
            PresetOption("DEFAULT", "Default", "Gentle expressive shapes for all ages", Icons.Default.RadioButtonUnchecked),
            PresetOption("FRIENDLY", "Friendly", "Warm and approachable shapes", Icons.Default.Favorite),
            PresetOption("CHEERFUL", "Cheerful", "Bright and expressive shapes", Icons.Default.WbSunny),
            PresetOption("MODERN", "Modern", "Contemporary expressive design", Icons.Default.Star),
            PresetOption("PLAYFUL", "Playful", "Fun and expressive shapes", Icons.Default.Celebration),
            PresetOption("ORGANIC", "Organic", "Nature-inspired shapes", Icons.Default.Park),
            PresetOption("GEOMETRIC", "Geometric", "Clean and modern shapes", Icons.Default.Category),
            PresetOption("RETRO", "Retro", "Pixelated nostalgic shapes", Icons.Default.Gamepad),
            PresetOption("CUSTOM", "Custom", "Your personalized selection", Icons.Default.Tune)
        )
    }
    
    // Define available shapes grouped by category
    val allShapes = remember {
        listOf(
            // Basic Shapes
            ShapeOption("CIRCLE", "Circle", "A perfect circle", "Basic"),
            ShapeOption("SQUARE", "Square", "Rounded square", "Basic"),
            ShapeOption("OVAL", "Oval", "Elongated oval", "Basic"),
            ShapeOption("PILL", "Pill", "Capsule shape", "Basic"),
            ShapeOption("DIAMOND", "Diamond", "Diamond shape", "Basic"),
            ShapeOption("TRIANGLE", "Triangle", "Rounded triangle", "Basic"),
            ShapeOption("PENTAGON", "Pentagon", "Five-sided polygon", "Basic"),
            // Organic Shapes
            ShapeOption("FLOWER", "Flower", "Flower with petals", "Organic"),
            ShapeOption("CLOVER_4_LEAF", "4-Leaf Clover", "Four-leaf clover", "Organic"),
            ShapeOption("CLOVER_8_LEAF", "8-Leaf Clover", "Eight-leaf clover", "Organic"),
            ShapeOption("HEART", "Heart", "Heart shape", "Organic"),
            ShapeOption("BUN", "Bun", "Bun/bread shape", "Organic"),
            ShapeOption("BUTTERFLY", "Butterfly", "Butterfly shape", "Organic"),
            // Playful Shapes
            ShapeOption("BOOM", "Boom", "Explosion shape", "Playful"),
            ShapeOption("SOFT_BOOM", "Soft Boom", "Softer explosion", "Playful"),
            ShapeOption("BURST", "Burst", "Starburst shape", "Playful"),
            ShapeOption("SOFT_BURST", "Soft Burst", "Softer starburst", "Playful"),
            ShapeOption("SUNNY", "Sunny", "Sun with rays", "Playful"),
            ShapeOption("VERY_SUNNY", "Very Sunny", "Sun with more rays", "Playful"),
            ShapeOption("STAR", "Star", "Star shape", "Playful"),
            // Cookie Shapes
            ShapeOption("COOKIE_4", "Cookie 4", "4-sided cookie", "Cookie"),
            ShapeOption("COOKIE_6", "Cookie 6", "6-sided cookie", "Cookie"),
            ShapeOption("COOKIE_7", "Cookie 7", "7-sided cookie", "Cookie"),
            ShapeOption("COOKIE_9", "Cookie 9", "9-sided cookie", "Cookie"),
            ShapeOption("COOKIE_12", "Cookie 12", "12-sided cookie", "Cookie"),
            // Whimsical Shapes
            ShapeOption("GHOSTISH", "Ghostish", "Ghost-like shape", "Whimsical"),
            ShapeOption("PUFFY", "Puffy", "Cloud-like shape", "Whimsical"),
            ShapeOption("PUFFY_DIAMOND", "Puffy Diamond", "Puffy diamond", "Whimsical"),
            ShapeOption("BUN", "Bun", "Bun/bread shape", "Whimsical"),
            ShapeOption("FAN", "Fan", "Fan shape", "Whimsical"),
            ShapeOption("ARROW", "Arrow", "Arrow pointer", "Whimsical"),
            // Special Shapes
            ShapeOption("ARCH", "Arch", "Arch shape", "Special"),
            ShapeOption("CLAM_SHELL", "Clam Shell", "Shell shape", "Special"),
            ShapeOption("GEM", "Gem", "Gemstone shape", "Special"),
            ShapeOption("SEMI_CIRCLE", "Semi Circle", "Half circle", "Special"),
            ShapeOption("SLANTED", "Slanted", "Slanted square", "Special"),
            // Pixel Shapes
            ShapeOption("PIXEL_CIRCLE", "Pixel Circle", "Pixelated circle", "Pixel"),
            ShapeOption("PIXEL_TRIANGLE", "Pixel Triangle", "Pixelated triangle", "Pixel")
        )
    }
    
    // Define shape targets with current values
    val shapeTargets = remember(shapeAlbumArt, shapePlayerArt, shapeSongArt, shapePlaylistArt, shapeArtistArt, shapePlayerControls, shapeMiniPlayer) {
        listOf(
            Triple("ALBUM_ART", "Album Artwork" to "Shape for album artwork", shapeAlbumArt),
            Triple("PLAYER_ART", "Player Artwork" to "Shape for player screen artwork", shapePlayerArt),
            Triple("SONG_ART", "Song Artwork" to "Shape for song artwork in lists", shapeSongArt),
            Triple("PLAYLIST_ART", "Playlist Artwork" to "Shape for playlist covers", shapePlaylistArt),
            Triple("ARTIST_ART", "Artist Artwork" to "Shape for artist images", shapeArtistArt),
            Triple("PLAYER_CONTROLS", "Player Controls" to "Shape for player control buttons", shapePlayerControls),
            Triple("MINI_PLAYER", "Mini Player" to "Shape for mini player artwork", shapeMiniPlayer)
        )
    }
    
    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_expressive_shapes),
        showBackButton = true,
        onBackClick = onBackClick
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enable/Disable Card 
            item(key = "expressive_shapes_toggle") {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (expressiveShapesEnabled)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Interests,
                            contentDescription = null,
                            tint = if (expressiveShapesEnabled) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    expressiveShapesEnabled -> "Active"
                                    else -> "Disabled"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TunerAnimatedSwitch(
                            checked = expressiveShapesEnabled,
                            onCheckedChange = { enabled ->
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                appSettings.setExpressiveShapesEnabled(enabled)
                            }
                        )
                    }
                }
            }
            
            // Preset Selection with animation
            item(key = "preset_section") {
                AnimatedVisibility(
                    visible = expressiveShapesEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                    Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Quick Presets",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            TunerSettingRow(
                                item = SettingItem(
                                    Icons.Default.Style,
                                    "Shape Preset",
                                    presets.find { it.id == currentPreset }?.displayName ?: "Default",
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        showPresetDialog = true
                                    }
                                )
                            )
                        }
                    }
                }
            }
            
            // Preset Preview Row with animation
            item(key = "preset_preview") {
                AnimatedVisibility(
                    visible = expressiveShapesEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp)
                    ) {
                        items(presets.filter { it.id != "CUSTOM" }) { preset ->
                            val isSelected = preset.id == currentPreset
                            Card(
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    appSettings.applyExpressiveShapePreset(preset.id)
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(90.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = preset.displayName,
                                        modifier = Modifier.size(28.dp),
                                        tint = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = preset.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Randomize Button
            item(key = "randomize_shapes") {
                AnimatedVisibility(
                    visible = expressiveShapesEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                            appSettings.randomizeExpressiveShapes()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Randomize shapes",
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Randomize All Shapes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
            
            // Individual Shape Customization with animation
            item(key = "individual_shapes_header") {
                AnimatedVisibility(
                    visible = expressiveShapesEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Individual Shape Settings",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            shapeTargets.forEachIndexed { index, (targetId, namePair, currentShape) ->
                                val (targetName, targetDesc) = namePair
                                val currentShapeName = allShapes.find { it.id == currentShape }?.displayName ?: currentShape
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                            showShapePickerDialog = targetId
                                        }
                                        .padding(horizontal = 18.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Shape preview with actual MaterialShape
                                    Surface(
                                        modifier = Modifier.size(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            // Simple shape preview icon
                                            Icon(
                                                imageVector = when (targetId) {
                                                    "ALBUM_ART" -> Icons.Default.Album
                                                    "PLAYER_ART" -> Icons.Default.MusicNote
                                                    "SONG_ART" -> Icons.Default.AudioFile
                                                    "PLAYLIST_ART" -> Icons.Default.QueueMusic
                                                    "ARTIST_ART" -> Icons.Default.Person
                                                    "PLAYER_CONTROLS" -> Icons.Default.PlayCircle
                                                    "MINI_PLAYER" -> Icons.Default.MusicNote
                                                    else -> Icons.Default.Category
                                                },
                                                contentDescription = targetName,
                                                modifier = Modifier.size(24.dp),
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = targetName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = currentShapeName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = "Change shape",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                                
                                if (index < shapeTargets.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                    } // Column
                } // AnimatedVisibility
            }
            
            // Info/Tip Card about M3 Expressive
            item(key = "expressive_info_card") {
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
                                text = "About Expressive Shapes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Material 3 Expressive introduces organic, playful shapes like flowers, hearts, cookies, and more. These shapes create a unique, expressive experience that makes your music app feel more personal and fun. Enable expressive shapes to bring your music collection to life with beautiful, age-appropriate organic forms.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f
                        )
                    }
                }
            }

            // Bottom spacer
            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    
    // Preset Selection Bottom Sheet
    if (showPresetDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var showPresetContent by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            delay(100)
            showPresetContent = true
        }
        
        ModalBottomSheet(
            onDismissRequest = { showPresetDialog = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                // Header with animation
                StandardBottomSheetHeader(
                    title = "Choose a Preset",
                    subtitle = "Select a theme for all components",
                    visible = showPresetContent
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = preset.id == currentPreset
                        
                        // Master animation states
                        var isPressed by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.96f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "preset_scale"
                        )
                        
                        val containerColor by animateColorAsState(
                            targetValue = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "preset_container_color"
                        )
                        
                        Card(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                isPressed = true
                                appSettings.applyExpressiveShapePreset(preset.id)
                                showPresetDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = containerColor
                            ),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                color = if (isSelected)
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                else
                                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = preset.icon,
                                            contentDescription = preset.displayName,
                                            modifier = Modifier.size(24.dp),
                                            tint = if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = preset.displayName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = preset.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                        
                        // Reset press state
                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                delay(150)
                                isPressed = false
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Individual Shape Picker Bottom Sheet
    showShapePickerDialog?.let { targetId ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        val targetName = shapeTargets.find { it.first == targetId }?.second?.first ?: targetId
        var showShapeContent by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            delay(100)
            showShapeContent = true
        }
        
        val currentShapeForTarget = when (targetId) {
            "ALBUM_ART" -> shapeAlbumArt
            "PLAYER_ART" -> shapePlayerArt
            "SONG_ART" -> shapeSongArt
            "PLAYLIST_ART" -> shapePlaylistArt
            "ARTIST_ART" -> shapeArtistArt
            "PLAYER_CONTROLS" -> shapePlayerControls
            "MINI_PLAYER" -> shapeMiniPlayer
            else -> "CIRCLE"
        }
        
        // Group shapes by category
        val groupedShapes = allShapes.groupBy { it.category }
        
        ModalBottomSheet(
            onDismissRequest = { showShapePickerDialog = null },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Header with animation
                StandardBottomSheetHeader(
                    title = "Shape for $targetName",
                    subtitle = "Choose an expressive shape",
                    visible = showShapeContent
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Shape options in a grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    groupedShapes.forEach { (category, shapes) ->
                        item(key = "category_$category", span = { GridItemSpan(2) }) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                            )
                        }
                        
                        items(
                            items = shapes,
                            key = { "shape_${it.id}" }
                        ) { shape ->
                            val isSelected = shape.id == currentShapeForTarget
                            
                            // Master animation states
                            var isPressed by remember { mutableStateOf(false) }
                            val scale by animateFloatAsState(
                                targetValue = if (isPressed) 0.96f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "shape_scale"
                            )
                            
                            val containerColor by animateColorAsState(
                                targetValue = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "shape_container_color"
                            )
                            
                            Card(
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                    isPressed = true
                                    when (targetId) {
                                        "ALBUM_ART" -> appSettings.setExpressiveShapeAlbumArt(shape.id)
                                        "PLAYER_ART" -> appSettings.setExpressiveShapePlayerArt(shape.id)
                                        "SONG_ART" -> appSettings.setExpressiveShapeSongArt(shape.id)
                                        "PLAYLIST_ART" -> appSettings.setExpressiveShapePlaylistArt(shape.id)
                                        "ARTIST_ART" -> appSettings.setExpressiveShapeArtistArt(shape.id)
                                        "PLAYER_CONTROLS" -> appSettings.setExpressiveShapePlayerControls(shape.id)
                                        "MINI_PLAYER" -> appSettings.setExpressiveShapeMiniPlayer(shape.id)
                                    }
                                    showShapePickerDialog = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = containerColor
                                ),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Shape Preview
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = rememberExpressiveShape(shape.id, CircleShape),
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize())
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = shape.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    
                                    Text(
                                        text = shape.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = MaterialTheme.typography.labelSmall.lineHeight * 1.1f
                                    )
                                }
                            }
                            
                            // Reset press state
                            LaunchedEffect(isPressed) {
                                if (isPressed) {
                                    delay(150)
                                    isPressed = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPreviewItem(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = color,
            modifier = Modifier
                .size(48.dp)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            if (isSelected) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun PlaceholderSettingsScreen() {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Large settings icon
        Surface(
            shape = RoundedCornerShape(55.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(120.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = RhythmIcons.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = context.getString(R.string.settings_select_option),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Description
//        Text(
//            text = context.getString(R.string.settings_select_option_desc),
//            style = MaterialTheme.typography.bodyLarge,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
//            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
//        )

        Spacer(modifier = Modifier.height(48.dp))

        // Additional visual elements for tablet UI
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(200.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choose a setting from the left panel",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap on any setting option to view and modify its preferences here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}




