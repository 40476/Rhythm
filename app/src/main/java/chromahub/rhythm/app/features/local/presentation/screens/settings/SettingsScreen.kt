package chromahub.rhythm.app.features.local.presentation.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import chromahub.rhythm.app.shared.data.repository.PlaybackStatsRepository
import chromahub.rhythm.app.shared.presentation.navigation.RhythmGuardRiskLevel
import chromahub.rhythm.app.shared.presentation.navigation.rhythmGuardResolveRiskLevel
import androidx.compose.ui.draw.clip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.presentation.components.common.CollapsibleHeaderScreen
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.ui.utils.LazyListStateSaver
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.features.local.presentation.components.settings.LanguageSwitcherDialog
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.LensBlur
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalConfiguration
import chromahub.rhythm.app.ui.theme.RhythmTheme
import chromahub.rhythm.app.util.HapticUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import chromahub.rhythm.app.features.local.presentation.components.player.SleepTimerBottomSheetNew
import chromahub.rhythm.app.features.local.presentation.navigation.Screen
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel

// Define routes for navigation
object SettingsRoutes {
    const val NOTIFICATIONS = "notifications_settings"
    const val EXPERIMENTAL_FEATURES = "experimental_features_settings"
    const val ABOUT = "about_screen"
    const val UPDATES = "updates_screen"
    const val MEDIA_SCAN = "media_scan_settings"
    const val ARTIST_SEPARATORS = "artist_separators_settings"
    const val PLAYLISTS = "playlist_settings"
    const val API_MANAGEMENT = "api_management_settings"
    const val CACHE_MANAGEMENT = "cache_management_settings"
    const val BACKUP_RESTORE = "backup_restore_settings"
    const val LIBRARY_TAB_ORDER = "library_tab_order_settings"
    const val THEME_CUSTOMIZATION = "theme_customization_settings"
    const val PLAYER_CUSTOMIZATION = "player_customization_settings"
    const val MINIPLAYER_CUSTOMIZATION = "miniplayer_customization_settings"
    const val EQUALIZER = "equalizer_settings"
    const val SLEEP_TIMER = "sleep_timer_settings"
    const val CRASH_LOG_HISTORY = "crash_log_history_settings"
    const val QUEUE_PLAYBACK = "queue_playback_settings"
    const val LYRICS_SOURCE = "lyrics_source_settings"
    const val WIDGET = "widget_settings"
    const val HOME_SCREEN = "home_screen_settings"
    const val GESTURES = "gestures_settings"
    const val LISTENING_STATS = "listening_stats"
    const val EXPRESSIVE_SHAPES = "expressive_shapes_settings"
    const val LIBRARY_SETTINGS = "library_settings"
    const val RHYTHM_GUARD = "rhythm_guard_settings"
    @Deprecated("Use RHYTHM_GUARD")
    const val RHYTHM_AURA = RHYTHM_GUARD
}

data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val description: String? = null,
    val onClick: (() -> Unit)? = null,
    val toggleState: Boolean? = null,
    val onToggleChange: ((Boolean) -> Unit)? = null,
    val data: Any? = null,
    val enabled: Boolean = true
)

data class SettingGroup(
    val title: String,
    val items: List<SettingItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateTo: (String) -> Unit, // Add navigation callback
    scrollState: LazyListState? = null, // Optional scroll state parameter
    isTablet: Boolean = false // Whether this is displayed on a tablet
) {
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val appSettings = AppSettings.getInstance(context)
    
    // Collect states for toggles
    val updatesEnabled by appSettings.updatesEnabled.collectAsState()
    val hapticFeedbackEnabled by appSettings.hapticFeedbackEnabled.collectAsState()
    val useSystemVolume by appSettings.useSystemVolume.collectAsState()
    val stopPlaybackOnZeroVolume by appSettings.stopPlaybackOnZeroVolume.collectAsState()
    val audioNormalization by appSettings.audioNormalization.collectAsState()
    val replayGain by appSettings.replayGain.collectAsState()
    val resumeOnDeviceReconnect by appSettings.resumeOnDeviceReconnect.collectAsState()
    val defaultScreen by appSettings.defaultScreen.collectAsState()
    val showAlphabetBar by appSettings.showAlphabetBar.collectAsState()
    val showScrollToTop by appSettings.showScrollToTop.collectAsState()
    val appMode by appSettings.appMode.collectAsState()
    val rhythmGuardMode by appSettings.rhythmGuardMode.collectAsState()
    val showSettingsSuggestions by appSettings.showSettingsSuggestions.collectAsState()
    
    var showDefaultScreenDialog by remember { mutableStateOf(false) }
    var showLanguageSwitcher by remember { mutableStateOf(false) }
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    val searchIndex = remember(context) { buildSettingsSearchIndex(context) }
    val searchResults = remember(searchQuery, searchIndex) { 
        searchSettings(searchQuery, searchIndex) 
    }
    val isSearchActive = searchQuery.isNotEmpty()

    CollapsibleHeaderScreen(
        title = context.getString(R.string.settings_title),
        showBackButton = !isTablet,
        onBackClick = {
            if (isSearchActive) {
                searchQuery = ""
            } else {
                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                onBackClick()
            }
        }
    ) { modifier ->
        val settingGroups = listOf(
            // 1. Look & Feel
            SettingGroup(
                title = context.getString(R.string.settings_section_appearance),
                items = buildList {
                    add(SettingItem(Icons.Default.Palette, context.getString(R.string.settings_theme_customization), context.getString(R.string.settings_theme_customization_desc), onClick = { onNavigateTo(SettingsRoutes.THEME_CUSTOMIZATION) }))
                    add(SettingItem(Icons.Default.Interests, context.getString(R.string.settings_shapes), context.getString(R.string.settings_shapes_desc), onClick = { onNavigateTo(SettingsRoutes.EXPRESSIVE_SHAPES) }))
                    add(SettingItem(Icons.Default.MusicNote, context.getString(R.string.settings_player_customization), context.getString(R.string.settings_player_customization_desc), onClick = { onNavigateTo(SettingsRoutes.PLAYER_CUSTOMIZATION) }))
                    add(SettingItem(Icons.Default.PlayCircleFilled, context.getString(R.string.settings_miniplayer_customization), context.getString(R.string.settings_miniplayer_customization_desc), onClick = { onNavigateTo(SettingsRoutes.MINIPLAYER_CUSTOMIZATION) }))
                }
            ),
            // 2. Home & Widgets - only show in LOCAL mode
            if (appMode == "LOCAL") SettingGroup(
                title = context.getString(R.string.settings_section_home_widgets),
                items = listOf(
                    SettingItem(Icons.Default.Home, context.getString(R.string.settings_home_customization), context.getString(R.string.settings_home_customization_desc), onClick = { onNavigateTo(SettingsRoutes.HOME_SCREEN) }),
                    SettingItem(Icons.Default.Widgets, context.getString(R.string.settings_widget), context.getString(R.string.settings_widget_desc), onClick = { onNavigateTo(SettingsRoutes.WIDGET) })
                )
            ) else null,
            // 3. Navigation & Controls
            SettingGroup(
                title = context.getString(R.string.settings_section_user_interface),
                items = buildList {
                    add(SettingItem(
                        Icons.Default.Home,
                        context.getString(R.string.settings_default_screen),
                        if (defaultScreen == "library") context.getString(R.string.library) else context.getString(R.string.home),
                        onClick = { showDefaultScreenDialog = true }
                    ))
                    add(SettingItem(
                        Icons.Default.Public,
                        context.getString(R.string.settings_language),
                        context.getString(R.string.settings_language_desc),
                        onClick = { showLanguageSwitcher = true }
                    ))
                    if (appMode == "LOCAL") {
                        //add(SettingItem(Icons.Default.Reorder, context.getString(R.string.settings_library_tab_order), context.getString(R.string.settings_library_tab_order_desc), onClick = { onNavigateTo(SettingsRoutes.LIBRARY_TAB_ORDER) }))
                    }
                    add(SettingItem(
                        Icons.Default.TouchApp, 
                        context.getString(R.string.settings_haptic_feedback), 
                        context.getString(R.string.settings_haptic_feedback_desc), 
                        toggleState = hapticFeedbackEnabled,
                        onToggleChange = { appSettings.setHapticFeedbackEnabled(it) }
                    ))
                    add(SettingItem(
                        Icons.Default.Gesture,
                        context.getString(R.string.settings_gestures),
                        context.getString(R.string.settings_gestures_desc),
                        onClick = { onNavigateTo(SettingsRoutes.GESTURES) }
                    ))
                    add(SettingItem(
                        Icons.Default.Lightbulb,
                        "Settings Suggestions",
                        "Show contextual suggestions at the top",
                        toggleState = showSettingsSuggestions,
                        onToggleChange = { appSettings.setShowSettingsSuggestions(it) }
                    ))
                }
            ),
            // 4. Queue & Playback
            SettingGroup(
                title = context.getString(R.string.settings_section_queue_playback),
                items = buildList {
                    add(SettingItem(Icons.Default.QueueMusic, context.getString(R.string.settings_queue_playback_title), context.getString(R.string.settings_queue_playback_desc), onClick = { onNavigateTo(SettingsRoutes.QUEUE_PLAYBACK) }))
                    if (appMode == "LOCAL") {
                        add(SettingItem(Icons.Default.AccessTime, context.getString(R.string.sleep_timer), context.getString(R.string.sleep_timer_set_control), onClick = { onNavigateTo(SettingsRoutes.SLEEP_TIMER) }))
                    }
                }
            ),
            // 5. Audio & Lyrics
            SettingGroup(
                title = context.getString(R.string.settings_section_audio_lyrics),
                items = buildList {
                    add(SettingItem(
                        RhythmIcons.Player.VolumeUp, 
                        context.getString(R.string.settings_system_volume), 
                        context.getString(R.string.settings_system_volume_desc), 
                        toggleState = useSystemVolume,
                        onToggleChange = { appSettings.setUseSystemVolume(it) }
                    ))
                    add(SettingItem(
                        RhythmIcons.Player.Stop,
                        context.getString(R.string.settings_stop_playback_on_zero_volume),
                        context.getString(R.string.settings_stop_playback_on_zero_volume_desc),
                        toggleState = stopPlaybackOnZeroVolume,
                        onToggleChange = { appSettings.setStopPlaybackOnZeroVolume(it) }
                    ))
                    add(SettingItem(
                        RhythmIcons.Devices.Bluetooth,
                        context.getString(R.string.settings_resume_on_device_reconnect),
                        context.getString(R.string.settings_resume_on_device_reconnect_desc),
                        toggleState = resumeOnDeviceReconnect,
                        onToggleChange = { appSettings.setResumeOnDeviceReconnect(it) }
                    ))
                    //add(SettingItem(Icons.Default.GraphicEq, context.getString(R.string.audio_normalization), context.getString(R.string.audio_normalization_desc), toggleState = audioNormalization, onToggleChange = { appSettings.setAudioNormalization(it) }))
                    //add(SettingItem(Icons.Default.GraphicEq, context.getString(R.string.replay_gain), context.getString(R.string.replay_gain_desc), toggleState = replayGain, onToggleChange = { appSettings.setReplayGain(it) }))
                    if (appMode == "LOCAL") {
                        add(SettingItem(Icons.Default.Equalizer, context.getString(R.string.settings_equalizer_title), context.getString(R.string.settings_equalizer_desc), onClick = { onNavigateTo(SettingsRoutes.EQUALIZER) }))
                    }
                }
            ),
            // 6. Library & Media - only show in LOCAL mode
            if (appMode == "LOCAL") SettingGroup(
                title = context.getString(R.string.settings_section_library_content),
                items = listOf(
                    SettingItem(Icons.Default.Folder, context.getString(R.string.settings_media_scan_title), context.getString(R.string.settings_media_scan_desc), onClick = { onNavigateTo(SettingsRoutes.MEDIA_SCAN) }),
                    SettingItem(Icons.Default.Person, context.getString(R.string.settings_artist_parsing), context.getString(R.string.settings_artist_parsing_desc), onClick = { onNavigateTo(SettingsRoutes.ARTIST_SEPARATORS) }),
                    SettingItem(Icons.Default.PlaylistAddCheckCircle, context.getString(R.string.settings_playlists_title), context.getString(R.string.settings_playlists_desc), onClick = { onNavigateTo(SettingsRoutes.PLAYLISTS) }),
                    SettingItem(Icons.Default.LibraryMusic, context.getString(R.string.settings_library_settings), context.getString(R.string.settings_library_settings_desc), onClick = { onNavigateTo(SettingsRoutes.LIBRARY_SETTINGS) })
                )
            ) else null,
            // 6. Notifications & Services
            SettingGroup(
                title = context.getString(R.string.settings_section_notifications_services),
                items = buildList {
                    //add(SettingItem(Icons.Default.Notifications, context.getString(R.string.settings_notifications), context.getString(R.string.settings_notifications_desc), onClick = { onNavigateTo(SettingsRoutes.NOTIFICATIONS) }))
                    if (appMode == "LOCAL") {
                        add(SettingItem(Icons.Default.Api, context.getString(R.string.settings_api_management), context.getString(R.string.settings_api_management_desc), onClick = { onNavigateTo(SettingsRoutes.API_MANAGEMENT) }))
                    }
                }
            ),
            // 7. Data & Storage - only show in LOCAL mode
            if (appMode == "LOCAL") SettingGroup(
                title = context.getString(R.string.settings_section_storage_data),
                items = listOf(
                    SettingItem(Icons.Default.Storage, context.getString(R.string.settings_cache_management_title), context.getString(R.string.settings_cache_management_desc), onClick = { onNavigateTo(SettingsRoutes.CACHE_MANAGEMENT) }),
                    SettingItem(Icons.Default.Backup, context.getString(R.string.settings_backup_restore_title), context.getString(R.string.settings_backup_restore_desc), onClick = { onNavigateTo(SettingsRoutes.BACKUP_RESTORE) }),
                    SettingItem(Icons.Default.AutoGraph, context.getString(R.string.settings_rhythm_stats), context.getString(R.string.settings_rhythm_stats_desc), onClick = { onNavigateTo(SettingsRoutes.LISTENING_STATS) }),
                    SettingItem(Icons.Default.Security, context.getString(R.string.settings_rhythm_guard), context.getString(R.string.settings_rhythm_guard_list_desc), onClick = { onNavigateTo(SettingsRoutes.RHYTHM_GUARD) })
                )
            ) else null,
            // 8. Updates & Info
            SettingGroup(
                title = context.getString(R.string.settings_section_updates_info),
                items = listOf(
                    SettingItem(
                        Icons.Default.Update,
                        context.getString(R.string.settings_updates_title),
                        context.getString(R.string.settings_updates_desc),
                        toggleState = updatesEnabled,
                        onToggleChange = { appSettings.setUpdatesEnabled(it) },
                        onClick = { onNavigateTo(SettingsRoutes.UPDATES) }
                    ),
                    SettingItem(Icons.Default.Info, context.getString(R.string.settings_about_title), context.getString(R.string.settings_about_desc), onClick = { onNavigateTo(SettingsRoutes.ABOUT) })
                )
            ),
            // 9. Advanced
            SettingGroup(
                title = context.getString(R.string.settings_section_advanced),
                items = listOf(
                    SettingItem(Icons.Default.BugReport, context.getString(R.string.settings_crash_log_history), context.getString(R.string.settings_crash_log_history_desc), onClick = { onNavigateTo(SettingsRoutes.CRASH_LOG_HISTORY) }),
                    SettingItem(Icons.Default.Science, context.getString(R.string.settings_experimental_features), context.getString(R.string.settings_experimental_features_desc), onClick = { onNavigateTo(SettingsRoutes.EXPERIMENTAL_FEATURES) })
                )
            )
        ).filterNotNull() // Filter out null groups (for streaming mode)

        val lazyListState = scrollState ?: rememberSaveable(
            key = "settings_scroll_state",
            saver = LazyListStateSaver
        ) {
            LazyListState()
        }
        
        // Main content with search
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            SettingsSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .padding(horizontal = if (isTablet) 32.dp else 24.dp)
                    .padding(top = 8.dp, bottom = 8.dp)
            )
            
            // Show search results or normal settings
            if (isSearchActive) {
                SettingsSearchResults(
                    results = searchResults,
                    onResultClick = { result ->
                        searchQuery = "" // Clear search
                        if (result.route != null) {
                            onNavigateTo(result.route)
                        }
                    },
                    modifier = Modifier.padding(horizontal = if (isTablet) 32.dp else 24.dp)
                )
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (isTablet) 32.dp else 24.dp)
                ) {
                    item {
                        if (showSettingsSuggestions) {
                            SettingsTipsRow(
                                onNavigateTo = onNavigateTo,
                                rhythmGuardMode = rhythmGuardMode,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 2.dp)
                            )
                        }
                    }

                    itemsIndexed(settingGroups, key = { _, group -> "setting_${group.title}" }) { index, group ->
                        Spacer(
                            modifier = Modifier.height(
                                if (index == 0 && showSettingsSuggestions) 10.dp else 28.dp
                            )
                        )
                        Text(
                            text = group.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                            letterSpacing = 0.5.sp
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                            )
                        ) {
                            Column {
                                group.items.forEachIndexed { index, item ->
                                    SettingRow(item = item)
                                    
                                    if (index < group.items.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            thickness = 1.dp,
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                                        )
                                    }
                                }
                            }
                        }
                    
                        
                    
                    }
                }
            } // End of else branch for search
        } // End of Column
        
        // Default screen selection bottom sheet
        if (showDefaultScreenDialog) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            
            ModalBottomSheet(
                onDismissRequest = { showDefaultScreenDialog = false },
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
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = context.getString(R.string.settings_default_screen),
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
                                    text = context.getString(R.string.settings_default_screen_desc),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    // Home option
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                            appSettings.setDefaultScreen("home")
                            showDefaultScreenDialog = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (defaultScreen == "home") 
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
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = if (defaultScreen == "home") 
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(32.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.common_home),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (defaultScreen == "home") 
                                        MaterialTheme.colorScheme.onPrimaryContainer 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = context.getString(R.string.settings_home_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (defaultScreen == "home") 
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            if (defaultScreen == "home") {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    
                    // Library option
                    Card(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                            appSettings.setDefaultScreen("library")
                            showDefaultScreenDialog = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (defaultScreen == "library") 
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
                                imageVector = RhythmIcons.Library,
                                contentDescription = null,
                                tint = if (defaultScreen == "library")
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(32.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.common_library),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (defaultScreen == "library") 
                                        MaterialTheme.colorScheme.onPrimaryContainer 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = context.getString(R.string.settings_library_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (defaultScreen == "library") 
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            if (defaultScreen == "library") {
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
        
        // Language switcher dialog
        if (showLanguageSwitcher) {
            LanguageSwitcherDialog(
                onDismiss = { showLanguageSwitcher = false }
            )
        }
        
        // App Mode selection dialog
    }
}

@Composable
fun SettingRow(item: SettingItem) {
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
        label = "setting_scale"
    )
    
    val iconBackgroundColor by animateColorAsState(
        targetValue = when {
            item.toggleState == true -> MaterialTheme.colorScheme.primaryContainer
            isPressed -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_bg_color"
    )
    
    val iconTintColor by animateColorAsState(
        targetValue = when {
            item.toggleState == true -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_tint_color"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container with expressive design
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(24.dp),
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
            modifier = Modifier
                .weight(1f)
                .then(
                    if (item.onClick != null && item.toggleState == null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                isPressed = true
                                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                                item.onClick()
                            }
                        )
                    } else if (item.onClick != null && item.toggleState != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                isPressed = true
                                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                                item.onClick()
                            }
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            item.description?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }

        if (item.toggleState != null && item.onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Navigate",
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            AnimatedSwitch(
                checked = item.toggleState,
                onCheckedChange = {
                    HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                    item.onToggleChange?.invoke(it)
                }
            )
        } else if (item.toggleState != null) {
            AnimatedSwitch(
                checked = item.toggleState,
                onCheckedChange = {
                    HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                    item.onToggleChange?.invoke(it)
                }
            )
        } else if (item.onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Navigate",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    RhythmTheme {
        SettingsScreen(onBackClick = {}, onNavigateTo = {})
    }
}

// Wrapper function for navigation
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreenWrapper(
    onBack: () -> Unit,
    appSettings: chromahub.rhythm.app.shared.data.model.AppSettings,
    navController: androidx.navigation.NavController
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    var currentRoute by rememberSaveable { mutableStateOf<String?>(null) }
    var showSleepTimerBottomSheet by rememberSaveable { mutableStateOf(false) }
    val musicViewModel: MusicViewModel = viewModel()

    // Hoist the main settings scroll state to persist across navigation
    val mainSettingsScrollState = rememberSaveable(
        key = "main_settings_scroll_state",
        saver = LazyListStateSaver
    ) {
        LazyListState()
    }

    // Handle back navigation - if we're in a subsettings screen, go back to main screen
    val handleBack = {
        if (currentRoute != null) {
            currentRoute = null
        } else {
            onBack()
        }
    }

    // Handle system back gestures when in subsettings
    BackHandler(enabled = currentRoute != null) {
        handleBack()
    }

    val onNavigateToSubsetting = { route: String ->
        if (route == SettingsRoutes.LISTENING_STATS) {
            navController.navigate("listening_stats")
        } else if (route == SettingsRoutes.EQUALIZER) {
            navController.navigate(Screen.Equalizer.route)
        } else if (route == SettingsRoutes.SLEEP_TIMER) {
            showSleepTimerBottomSheet = true
        } else {
            currentRoute = route
        }
    }

    if (isTablet) {
        // Tablet layout: Master-detail with settings always visible on left
        Row(modifier = Modifier.fillMaxSize()) {
            // Master pane - always visible settings list
            Surface(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight(),
            ) {
                SettingsScreen(
                    onBackClick = handleBack,
                    onNavigateTo = onNavigateToSubsetting,
                    scrollState = mainSettingsScrollState,
                    isTablet = true
                )
            }

            // Divider

            // Detail pane - subsettings or placeholder
            Surface(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                AnimatedContent(
                    targetState = currentRoute,
                    transitionSpec = {
                        if (targetState != null) {
                            // Slide in from right for subsettings
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = androidx.compose.animation.core.EaseOutCubic
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 350,
                                    delayMillis = 50
                                )
                            ) togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { -it / 4 },
                                animationSpec = tween(
                                    durationMillis = 350,
                                    easing = androidx.compose.animation.core.EaseInCubic
                                )
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = 250)
                            )
                        } else {
                            // Slide in from left when going back to placeholder
                            slideInHorizontally(
                                initialOffsetX = { -it / 4 },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = androidx.compose.animation.core.EaseOutCubic
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 350,
                                    delayMillis = 50
                                )
                            ) togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(
                                    durationMillis = 350,
                                    easing = androidx.compose.animation.core.EaseInCubic
                                )
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = 250)
                            )
                        }
                    },
                    label = "tablet_detail_navigation",
                    contentKey = { it ?: "placeholder" }
                ) { route ->
                    when (route) {
                        SettingsRoutes.NOTIFICATIONS -> NotificationsSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.PLAYLISTS -> PlaylistsSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.MEDIA_SCAN -> MediaScanSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.ARTIST_SEPARATORS -> ArtistSeparatorsSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.ABOUT -> chromahub.rhythm.app.features.local.presentation.screens.settings.AboutScreen(
                            onBackClick = { currentRoute = null },
                            onNavigateToUpdates = { currentRoute = SettingsRoutes.UPDATES }
                        )
                        SettingsRoutes.UPDATES -> UpdatesSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.EXPERIMENTAL_FEATURES -> ExperimentalFeaturesScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.API_MANAGEMENT -> ApiManagementSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.CACHE_MANAGEMENT -> CacheManagementSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.BACKUP_RESTORE -> BackupRestoreSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.LIBRARY_TAB_ORDER -> LibraryTabOrderSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.THEME_CUSTOMIZATION -> ThemeCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.PLAYER_CUSTOMIZATION -> PlayerCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.MINIPLAYER_CUSTOMIZATION -> MiniPlayerCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.CRASH_LOG_HISTORY -> CrashLogHistorySettingsScreen(onBackClick = { currentRoute = null }, appSettings = appSettings)
                        SettingsRoutes.QUEUE_PLAYBACK -> QueuePlaybackSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.LYRICS_SOURCE -> LyricsSourceSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.WIDGET -> WidgetSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.HOME_SCREEN -> HomeScreenCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.GESTURES -> GesturesSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.EXPRESSIVE_SHAPES -> ExpressiveShapesSettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.LIBRARY_SETTINGS -> LibrarySettingsScreen(onBackClick = { currentRoute = null })
                        SettingsRoutes.RHYTHM_GUARD -> RhythmGuardSettingsScreen(onBackClick = { currentRoute = null })
                        else -> PlaceholderSettingsScreen()
                    }
                }
            }
        }
    } else {
        // Phone layout: Traditional navigation with AnimatedContent
        AnimatedContent(
            targetState = currentRoute,
            transitionSpec = {
                if (targetState != null) {
                    // Enhanced slide in from right when navigating to a screen
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = androidx.compose.animation.core.EaseOutCubic
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
                            easing = androidx.compose.animation.core.EaseOutCubic
                        )
                    ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { -it / 4 },
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = androidx.compose.animation.core.EaseInCubic
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 250)
                    ) + scaleOut(
                        targetScale = 0.95f,
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = androidx.compose.animation.core.EaseInCubic
                        )
                    )
                } else {
                    // Enhanced slide in from left when going back
                    slideInHorizontally(
                        initialOffsetX = { -it / 4 },
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = androidx.compose.animation.core.EaseOutCubic
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
                            easing = androidx.compose.animation.core.EaseOutCubic
                        )
                    ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = androidx.compose.animation.core.EaseInCubic
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 250)
                    ) + scaleOut(
                        targetScale = 0.92f,
                        animationSpec = tween(
                            durationMillis = 350,
                            easing = androidx.compose.animation.core.EaseInCubic
                        )
                    )
                }
            },
            label = "settings_navigation",
            contentKey = { it ?: "main_settings" }
        ) { route ->
            when (route) {
                SettingsRoutes.NOTIFICATIONS -> NotificationsSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.PLAYLISTS -> PlaylistsSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.MEDIA_SCAN -> MediaScanSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.ARTIST_SEPARATORS -> ArtistSeparatorsSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.ABOUT -> chromahub.rhythm.app.features.local.presentation.screens.settings.AboutScreen(
                    onBackClick = { currentRoute = null },
                    onNavigateToUpdates = { currentRoute = SettingsRoutes.UPDATES }
                )
                SettingsRoutes.UPDATES -> UpdatesSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.EXPERIMENTAL_FEATURES -> ExperimentalFeaturesScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.API_MANAGEMENT -> ApiManagementSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.CACHE_MANAGEMENT -> CacheManagementSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.BACKUP_RESTORE -> BackupRestoreSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.LIBRARY_TAB_ORDER -> LibraryTabOrderSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.THEME_CUSTOMIZATION -> ThemeCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.PLAYER_CUSTOMIZATION -> PlayerCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.MINIPLAYER_CUSTOMIZATION -> MiniPlayerCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.CRASH_LOG_HISTORY -> CrashLogHistorySettingsScreen(onBackClick = { currentRoute = null }, appSettings = appSettings)
                SettingsRoutes.QUEUE_PLAYBACK -> QueuePlaybackSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.LYRICS_SOURCE -> LyricsSourceSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.WIDGET -> WidgetSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.HOME_SCREEN -> HomeScreenCustomizationSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.GESTURES -> GesturesSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.EXPRESSIVE_SHAPES -> ExpressiveShapesSettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.LIBRARY_SETTINGS -> LibrarySettingsScreen(onBackClick = { currentRoute = null })
                SettingsRoutes.RHYTHM_GUARD -> RhythmGuardSettingsScreen(onBackClick = { currentRoute = null })
                else -> SettingsScreen(
                    onBackClick = handleBack,
                    onNavigateTo = onNavigateToSubsetting,
                    scrollState = mainSettingsScrollState
                )
            }
        }
    }

    if (showSleepTimerBottomSheet) {
        SleepTimerBottomSheetNew(
            onDismiss = { showSleepTimerBottomSheet = false },
            currentSong = null,
            isPlaying = false,
            musicViewModel = musicViewModel
        )
    }
}

@Composable
private fun AnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TunerAnimatedSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
    )
}

data class SettingsTipData(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val text: String,
    val route: String? = null,
    val isPrimary: Boolean = false,
    val riskLevel: RhythmGuardRiskLevel? = null,
    val fillProgress: Float? = null
)

@Composable
fun SettingsTipsRow(
    onNavigateTo: (String) -> Unit,
    rhythmGuardMode: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var dismissedIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    
    // Playback stats
    var todayExposureMinutes by remember { mutableStateOf(0) }
    var currentRiskLevel by remember { mutableStateOf(RhythmGuardRiskLevel.LOW) }
    
    val appSettings = remember { chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context) }
    val limitMinutes by appSettings.rhythmGuardAlertThresholdMinutes.collectAsState()
    val manualVolumeFloat by appSettings.rhythmGuardManualVolumeThreshold.collectAsState()

    LaunchedEffect(limitMinutes, manualVolumeFloat) {
        val statsRepo = chromahub.rhythm.app.shared.data.repository.PlaybackStatsRepository.getInstance(context)
        val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
        
        while (true) {
            val stats = statsRepo.loadSummary(chromahub.rhythm.app.shared.data.repository.StatsTimeRange.TODAY)
            todayExposureMinutes = (stats.totalDurationMs / 60000).toInt()
            
            val maxVol = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
            val currentVol = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
            val currentVolumePercent = if (maxVol > 0) ((currentVol.toFloat() / maxVol) * 100).toInt() else 0
            
            val safeLimit = if (limitMinutes > 0) limitMinutes else 90
            val safeThreshold = (manualVolumeFloat * 100).toInt().coerceAtLeast(1)
            
            currentRiskLevel = chromahub.rhythm.app.shared.presentation.navigation.rhythmGuardResolveRiskLevel(
                currentVolumePercent = currentVolumePercent, 
                safeThresholdPercent = safeThreshold,
                exposureMinutes = todayExposureMinutes,
                exposureLimitMinutes = safeLimit
            )
            kotlinx.coroutines.delay(2000)
        }
    }
    
    // Generate a fixed seed when the view enters to keep shuffle stable during recompositions
    val shuffleSeed = rememberSaveable { kotlin.random.Random.nextInt() }
    
    val tips = remember(rhythmGuardMode, dismissedIds, todayExposureMinutes, currentRiskLevel) {
        val random = kotlin.random.Random(shuffleSeed)
        buildList {
            if ("rhythm_guard" !in dismissedIds) {
                val desc = when (rhythmGuardMode) {
                    "OFF" -> context.getString(R.string.settings_tip_rhythm_guard_off)
                    "MANUAL" -> context.getString(R.string.settings_tip_rhythm_guard_manual)
                    else -> context.getString(R.string.settings_tip_rhythm_guard_auto)
                }
                
                val progress = (todayExposureMinutes.toFloat() / 90f).coerceIn(0.05f, 1f)

                add(
                    SettingsTipData(
                        id = "rhythm_guard",
                        icon = Icons.Default.Security,
                        title = "Rhythm Guard",
                        text = desc,
                        route = SettingsRoutes.RHYTHM_GUARD,
                        isPrimary = true,
                        riskLevel = if (rhythmGuardMode == "OFF") null else currentRiskLevel,
                        fillProgress = if (rhythmGuardMode == "OFF") null else progress
                    )
                )
            }
            if ("theme" !in dismissedIds) {
                val descs = listOf(
                    context.getString(R.string.settings_tip_theme),
                    "Refresh your music player with lively AMOLED black and beautiful pastel colors.",
                    "Make Rhythm truly yours by matching the app's accent to your system."
                )
                add(
                    SettingsTipData(
                        id = "theme",
                        icon = Icons.Default.Palette,
                        title = "Personalization",
                        text = descs.random(random),
                        route = SettingsRoutes.THEME_CUSTOMIZATION
                    )
                )
            }
            if ("gestures" !in dismissedIds) {
                val descs = listOf(
                    context.getString(R.string.settings_tip_gestures),
                    context.getString(R.string.settings_tip_gestures_swipe),
                    context.getString(R.string.settings_tip_gestures_artwork)
                )
                add(
                    SettingsTipData(
                        id = "gestures",
                        icon = Icons.Default.Gesture,
                        title = context.getString(R.string.settings_gestures),
                        text = descs.random(random),
                        route = SettingsRoutes.GESTURES
                    )
                )
            }
            if ("media_scan" !in dismissedIds) {
                val descs = listOf(
                    context.getString(R.string.settings_tip_media_scan),
                    "Avoid unwanted audio clips. Explicitly define which folders we should scan for music.",
                    "Keep voice notes and ringtones away from your playlists using focus folders."
                )
                add(
                    SettingsTipData(
                        id = "media_scan",
                        icon = Icons.Default.Folder,
                        title = "Library Focus",
                        text = descs.random(random),
                        route = SettingsRoutes.MEDIA_SCAN
                    )
                )
            }
            if ("sleep_timer" !in dismissedIds) {
                val descs = listOf(
                    "Drift off to sleep while your music is playing. We'll automatically pause it.",
                    "Set an automated sleep timer before bed so playback stops perfectly."
                )
                add(
                    SettingsTipData(
                        id = "sleep_timer",
                        icon = Icons.Default.AccessTime,
                        title = "Sleep Timer",
                        text = descs.random(random),
                        route = SettingsRoutes.SLEEP_TIMER
                    )
                )
            }
            if ("equalizer" !in dismissedIds) {
                val descs = listOf(
                    "Elevate your experience. Boost the bass and adjust frequencies using the Equalizer.",
                    "Tune the sound to your headphones securely with our advanced audio effects."
                )
                add(
                    SettingsTipData(
                        id = "equalizer",
                        icon = Icons.Default.Equalizer,
                        title = "Audio Equalizer",
                        text = descs.random(random),
                        route = SettingsRoutes.EQUALIZER
                    )
                )
            }
        }.shuffled(random)
    }

    if (tips.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(tips, key = { it.id }) { tip ->
                SettingsTipCard(
                    tip = tip,
                    onDismiss = { dismissedIds = dismissedIds + tip.id },
                    onClick = { tip.route?.let { onNavigateTo(it) } }
                )
            }
        }
    }
}

@Composable
fun SettingsTipCard(
    tip: SettingsTipData,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    val isPrimary = tip.isPrimary
    val containerColor = if (isPrimary) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val contentColor = if (isPrimary) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val defaultIconBg = if (isPrimary) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondaryContainer
    val iconColor = if (isPrimary) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondaryContainer

    // Use health status directly for icon background if available, looks much cleaner than partial filling!
    val indicatorColor = if (tip.riskLevel != null) {
        when (tip.riskLevel) {
            RhythmGuardRiskLevel.LOW -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
            RhythmGuardRiskLevel.MODERATE -> androidx.compose.ui.graphics.Color(0xFFFF9800)
            RhythmGuardRiskLevel.HIGH -> androidx.compose.ui.graphics.Color(0xFFFF5722)
            RhythmGuardRiskLevel.SEVERE -> MaterialTheme.colorScheme.error
        }
    } else null

    Card(
        modifier = Modifier
            .width(320.dp)
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Expressive icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(indicatorColor ?: defaultIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tip.icon,
                            contentDescription = null,
                            tint = if (indicatorColor != null) androidx.compose.ui.graphics.Color.White else iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    androidx.compose.material3.IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = tip.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.85f),
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}