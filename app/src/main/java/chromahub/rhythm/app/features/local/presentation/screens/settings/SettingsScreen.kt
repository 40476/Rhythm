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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Science
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
import chromahub.rhythm.app.features.local.presentation.navigation.Screen

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
}

data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val description: String? = null,
    val onClick: (() -> Unit)? = null,
    val toggleState: Boolean? = null,
    val onToggleChange: ((Boolean) -> Unit)? = null,
    val data: Any? = null
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
    val bitPerfectMode by appSettings.bitPerfectMode.collectAsState()
    val showLyrics by appSettings.showLyrics.collectAsState()
    val groupByAlbumArtist by appSettings.groupByAlbumArtist.collectAsState()
    val ignoreMediaStoreCovers by appSettings.ignoreMediaStoreCovers.collectAsState()
    val losslessArtwork by appSettings.losslessArtwork.collectAsState()
    val defaultScreen by appSettings.defaultScreen.collectAsState()
    val showAlphabetBar by appSettings.showAlphabetBar.collectAsState()
    val showScrollToTop by appSettings.showScrollToTop.collectAsState()
    val albumBottomSheetGradientBlur by appSettings.albumBottomSheetGradientBlur.collectAsState()
    val appMode by appSettings.appMode.collectAsState()
    val enableRatingSystem by appSettings.enableRatingSystem.collectAsState()
    
    var showDefaultScreenDialog by remember { mutableStateOf(false) }
    var showLyricsSourceDialog by remember { mutableStateOf(false) }
    var showLanguageSwitcher by remember { mutableStateOf(false) }
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    val searchIndex = remember(context) { buildSettingsSearchIndex(context) }
    val searchResults = remember(searchQuery, searchIndex) { 
        searchSettings(searchQuery, searchIndex) 
    }
    val isSearchActive = searchQuery.isNotEmpty()

    CollapsibleHeaderScreen(
        title = "Settings",
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
            SettingGroup(
                title = context.getString(R.string.settings_section_appearance),
                items = buildList {
                    add(SettingItem(Icons.Default.Palette, context.getString(R.string.settings_theme_customization), context.getString(R.string.settings_theme_customization_desc), onClick = { onNavigateTo(SettingsRoutes.THEME_CUSTOMIZATION) }))
                    add(SettingItem(Icons.Default.Interests, "Shapes", "Expressive Shapes", onClick = { onNavigateTo(SettingsRoutes.EXPRESSIVE_SHAPES) }))
                    // Local-only customizations
                    if (appMode == "LOCAL") {
                        add(SettingItem(Icons.Default.Home, context.getString(R.string.settings_home_customization), context.getString(R.string.settings_home_customization_desc), onClick = { onNavigateTo(SettingsRoutes.HOME_SCREEN) }))
                        add(SettingItem(Icons.Default.Widgets, context.getString(R.string.settings_widget), context.getString(R.string.settings_widget_desc), onClick = { onNavigateTo(SettingsRoutes.WIDGET) }))
                    }
                    add(SettingItem(Icons.Default.MusicNote, context.getString(R.string.settings_player_customization), context.getString(R.string.settings_player_customization_desc), onClick = { onNavigateTo(SettingsRoutes.PLAYER_CUSTOMIZATION) }))
                    add(SettingItem(Icons.Default.PlayCircleFilled, context.getString(R.string.settings_miniplayer_customization), context.getString(R.string.settings_miniplayer_customization_desc), onClick = { onNavigateTo(SettingsRoutes.MINIPLAYER_CUSTOMIZATION) }))
                    add(SettingItem(Icons.Default.LensBlur, context.getString(R.string.settings_album_bottom_sheet_gradient_blur), context.getString(R.string.settings_album_bottom_sheet_gradient_blur_desc), toggleState = albumBottomSheetGradientBlur, onToggleChange = { appSettings.setAlbumBottomSheetGradientBlur(it) }))
                }
            ),
            SettingGroup(
                title = context.getString(R.string.settings_section_user_interface),
                items = listOf(
                    SettingItem(
                        Icons.Default.Home,
                        context.getString(R.string.settings_default_screen),
                        if (defaultScreen == "library") context.getString(R.string.library) else context.getString(R.string.home),
                        onClick = { showDefaultScreenDialog = true }
                    ),
                    SettingItem(
                        Icons.Default.Public,
                        context.getString(R.string.settings_language),
                        context.getString(R.string.settings_language_desc),
                        onClick = { showLanguageSwitcher = true }
                    ),
                    SettingItem(
                        Icons.Default.TouchApp, 
                        context.getString(R.string.settings_haptic_feedback), 
                        context.getString(R.string.settings_haptic_feedback_desc), 
                        toggleState = hapticFeedbackEnabled,
                        onToggleChange = { appSettings.setHapticFeedbackEnabled(it) }
                    ),
                    SettingItem(
                        Icons.Default.Gesture,
                        context.getString(R.string.settings_gestures),
                        context.getString(R.string.settings_gestures_desc),
                        onClick = { onNavigateTo(SettingsRoutes.GESTURES) }
                    )
                )
            ),
            SettingGroup(
                title = context.getString(R.string.settings_section_audio_playback),
                items = buildList {
                    add(SettingItem(
                        RhythmIcons.Player.VolumeUp, 
                        context.getString(R.string.settings_system_volume), 
                        context.getString(R.string.settings_system_volume_desc), 
                        toggleState = useSystemVolume,
                        onToggleChange = { appSettings.setUseSystemVolume(it) }
                    ))
                    add(SettingItem(Icons.Default.Lyrics, context.getString(R.string.lyrics_source_priority), context.getString(R.string.playback_lyrics_priority_desc), onClick = { 
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                        showLyricsSourceDialog = true 
                    }))
                    add(SettingItem(Icons.Default.QueueMusic, context.getString(R.string.settings_queue_playback_title), context.getString(R.string.settings_queue_playback_desc), onClick = { onNavigateTo(SettingsRoutes.QUEUE_PLAYBACK) }))
                    // Equalizer only in LOCAL mode for now
                    if (appMode == "LOCAL") {
                        add(SettingItem(Icons.Default.Equalizer, context.getString(R.string.settings_equalizer_title), context.getString(R.string.settings_equalizer_desc), onClick = { onNavigateTo(SettingsRoutes.EQUALIZER) }))
                        add(SettingItem(Icons.Default.AccessTime, context.getString(R.string.sleep_timer), context.getString(R.string.sleep_timer_set_control), onClick = { onNavigateTo(SettingsRoutes.SLEEP_TIMER) }))
                        add(SettingItem(
                            Icons.Default.GraphicEq,
                            "Bit-Perfect Mode",
                            "Native sample rate, no resampling. Restart required.",
                            toggleState = bitPerfectMode,
                            onToggleChange = { appSettings.setBitPerfectMode(it) }
                        ))
                    }
                }
            ),
            // Library Content section - only show in LOCAL mode
            if (appMode == "LOCAL") SettingGroup(
                title = context.getString(R.string.settings_section_library_content),
                items = listOf(
                    SettingItem(Icons.Default.Person, context.getString(R.string.settings_artist_parsing), context.getString(R.string.settings_artist_parsing_desc), onClick = { onNavigateTo(SettingsRoutes.ARTIST_SEPARATORS) }),
                    SettingItem(Icons.Default.Folder, context.getString(R.string.settings_media_scan_title), context.getString(R.string.settings_media_scan_desc), onClick = { onNavigateTo(SettingsRoutes.MEDIA_SCAN) }),
                    SettingItem(Icons.Default.PlaylistAddCheckCircle, context.getString(R.string.settings_playlists_title), context.getString(R.string.settings_playlists_desc), onClick = { onNavigateTo(SettingsRoutes.PLAYLISTS) }),
                    SettingItem(Icons.Default.Star, "Song Ratings", "Enable or disable song rating system", toggleState = enableRatingSystem, onToggleChange = { appSettings.setEnableRatingSystem(it) }),
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
                    )
                )
            ) else null,
            // Storage & Data section - only show in LOCAL mode
            if (appMode == "LOCAL") SettingGroup(
                title = context.getString(R.string.settings_section_storage_data),
                items = listOf(
                    SettingItem(Icons.Default.Storage, context.getString(R.string.settings_cache_management_title), context.getString(R.string.settings_cache_management_desc), onClick = { onNavigateTo(SettingsRoutes.CACHE_MANAGEMENT) }),
                    SettingItem(Icons.Default.Backup, context.getString(R.string.settings_backup_restore_title), context.getString(R.string.settings_backup_restore_desc), onClick = { onNavigateTo(SettingsRoutes.BACKUP_RESTORE) }),
                    SettingItem(Icons.Default.AutoGraph, context.getString(R.string.settings_rhythm_stats), context.getString(R.string.settings_rhythm_stats_desc), onClick = { onNavigateTo(SettingsRoutes.LISTENING_STATS) })
                )
            ) else null,
            // Integrations - only show in LOCAL mode (for external services, scrobbling, etc)
            if (appMode == "LOCAL") SettingGroup(
                title = context.getString(R.string.settings_section_services),
                items = listOf(
                    SettingItem(Icons.Default.Api, context.getString(R.string.settings_api_management), context.getString(R.string.settings_api_management_desc), onClick = { onNavigateTo(SettingsRoutes.API_MANAGEMENT) })
                )
            ) else null,
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
                    items(settingGroups, key = { "setting_${it.title}" }) { group ->
                        Spacer(modifier = Modifier.height(28.dp))
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

            // Quick Tips Card
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
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
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
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

                        TipItem(
                            icon = Icons.Default.Palette,
                            text = context.getString(R.string.settings_tip_theme)
                        )
                        TipItem(
                            icon = Icons.Default.TouchApp,
                            text = context.getString(R.string.settings_tip_haptic)
                        )
                        TipItem(
                            icon = Icons.Default.Folder,
                            text = "Use media scan to hide unwanted files from your library"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
//                Spacer(modifier = Modifier.height(24.dp)) // Space at the bottom
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
                                    tint = MaterialTheme.colorScheme.primary,
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
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        // Lyrics source priority dialog
        if (showLyricsSourceDialog) {
            LyricsSourceDialog(
                onDismiss = { showLyricsSourceDialog = false },
                appSettings = appSettings,
                context = context,
                haptic = hapticFeedback
            )
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
            navController.navigate(Screen.TunerSleepTimer.route)
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
                else -> SettingsScreen(
                    onBackClick = handleBack,
                    onNavigateTo = onNavigateToSubsetting,
                    scrollState = mainSettingsScrollState
                )
            }
        }
    }
}

@Composable
private fun AnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "thumbColor"
    )
    
    val trackColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "trackColor"
    )
    
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = thumbColor,
            checkedTrackColor = trackColor,
            uncheckedThumbColor = thumbColor,
            uncheckedTrackColor = trackColor
        ),
        thumbContent = {
            androidx.compose.animation.AnimatedVisibility(
                visible = checked,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    )
}

@Composable
private fun TipItem(
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

