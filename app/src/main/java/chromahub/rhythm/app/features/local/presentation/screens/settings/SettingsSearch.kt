package chromahub.rhythm.app.features.local.presentation.screens.settings

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LensBlur
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Swipe
import androidx.compose.material.icons.rounded.SwipeDown
import androidx.compose.material.icons.rounded.SwipeLeft
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.presentation.components.Material3SettingsGroup
import chromahub.rhythm.app.shared.presentation.components.Material3SettingsItem
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.util.HapticUtils

/**
 * Represents a searchable setting item with its metadata for search indexing
 */
data class SearchableSettingItem(
    val id: String,
    val title: String,
    val description: String,
    val keywords: List<String>,
    val icon: ImageVector,
    val route: String?, // null means it's in the main settings screen
    val parentScreen: String, // e.g., "Settings", "Theme", "Player", etc.
    val settingKey: String? = null // for highlighting specific setting
)

/**
 * Builds the complete search index for all settings in the app
 */
fun buildSettingsSearchIndex(context: Context): List<SearchableSettingItem> {
    return buildList {
        // ======================== MAIN SETTINGS SCREEN ========================
        
        // Look & Feel Section
        add(SearchableSettingItem(
            id = "theme_customization",
            title = context.getString(R.string.settings_theme_customization),
            description = context.getString(R.string.settings_theme_customization_desc),
            keywords = listOf("theme", "color", "appearance", "dark mode", "light mode", "colors", "customize", "style"),
            icon = Icons.Default.Palette,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = context.getString(R.string.settings_section_appearance)
        ))
        add(SearchableSettingItem(
            id = "expressive_shapes_nav",
            title = context.getString(R.string.settings_shapes),
            description = context.getString(R.string.settings_shapes_desc),
            keywords = listOf("shapes", "expressive", "custom", "corners", "rounded", "design"),
            icon = Icons.Default.Palette,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = context.getString(R.string.settings_section_appearance)
        ))
        add(SearchableSettingItem(
            id = "player_customization",
            title = context.getString(R.string.settings_player_customization),
            description = context.getString(R.string.settings_player_customization_desc),
            keywords = listOf("player", "now playing", "full player", "music player", "controls", "artwork"),
            icon = Icons.Default.MusicNote,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = context.getString(R.string.settings_section_appearance)
        ))
        add(SearchableSettingItem(
            id = "miniplayer_customization",
            title = context.getString(R.string.settings_miniplayer_customization),
            description = context.getString(R.string.settings_miniplayer_customization_desc),
            keywords = listOf("miniplayer", "mini player", "compact player", "bottom bar", "progress"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = context.getString(R.string.settings_section_appearance)
        ))
        add(SearchableSettingItem(
            id = "album_bottom_sheet_blur",
            title = context.getString(R.string.settings_album_bottom_sheet_gradient_blur),
            description = context.getString(R.string.settings_album_bottom_sheet_gradient_blur_desc),
            keywords = listOf("album", "bottom sheet", "gradient", "blur", "effect", "background"),
            icon = Icons.Default.LensBlur,
            route = SettingsRoutes.LIBRARY_SETTINGS,
            parentScreen = context.getString(R.string.settings_library_settings),
            settingKey = "albumBottomSheetGradientBlur"
        ))
        
        // Home & Widgets Section
        add(SearchableSettingItem(
            id = "home_customization",
            title = context.getString(R.string.settings_home_customization),
            description = context.getString(R.string.settings_home_customization_desc),
            keywords = listOf("home", "screen", "layout", "sections", "customize", "greeting", "carousel", "discover"),
            icon = Icons.Default.Home,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = context.getString(R.string.settings_section_home_widgets)
        ))
        add(SearchableSettingItem(
            id = "widget_settings",
            title = context.getString(R.string.settings_widget),
            description = context.getString(R.string.settings_widget_desc),
            keywords = listOf("widget", "home screen", "launcher", "music widget", "album art"),
            icon = Icons.Default.Widgets,
            route = SettingsRoutes.WIDGET,
            parentScreen = context.getString(R.string.settings_section_home_widgets)
        ))
        
        // Navigation & Interaction Section
        add(SearchableSettingItem(
            id = "default_screen",
            title = context.getString(R.string.settings_default_screen),
            description = context.getString(R.string.settings_default_screen_desc),
            keywords = listOf("default", "screen", "start", "launch", "home", "library", "startup"),
            icon = Icons.Default.Home,
            route = null,
            parentScreen = context.getString(R.string.settings_section_user_interface),
            settingKey = "defaultScreen"
        ))
        add(SearchableSettingItem(
            id = "language",
            title = context.getString(R.string.settings_language),
            description = context.getString(R.string.settings_language_desc),
            keywords = listOf("language", "locale", "translation", "english", "spanish", "french", "german", "hindi", "chinese", "japanese", "korean"),
            icon = Icons.Default.Public,
            route = null,
            parentScreen = context.getString(R.string.settings_section_user_interface),
            settingKey = "language"
        ))
        add(SearchableSettingItem(
            id = "haptic_feedback",
            title = context.getString(R.string.settings_haptic_feedback),
            description = context.getString(R.string.settings_haptic_feedback_desc),
            keywords = listOf("haptic", "vibration", "feedback", "touch", "vibrate"),
            icon = Icons.Default.TouchApp,
            route = null,
            parentScreen = context.getString(R.string.settings_section_user_interface),
            settingKey = "hapticFeedback"
        ))
        add(SearchableSettingItem(
            id = "settings_suggestions",
            title = "Settings Suggestions",
            description = "Show contextual suggestions at the top",
            keywords = listOf("suggestions", "tips", "recommendations", "contextual", "settings"),
            icon = Icons.Default.AutoAwesome,
            route = null,
            parentScreen = context.getString(R.string.settings_section_user_interface),
            settingKey = "showSettingsSuggestions"
        ))
        add(SearchableSettingItem(
            id = "gestures",
            title = context.getString(R.string.settings_gestures),
            description = context.getString(R.string.settings_gestures_desc),
            keywords = listOf("gestures", "swipe", "touch", "double tap", "navigation"),
            icon = Icons.Default.Gesture,
            route = SettingsRoutes.GESTURES,
            parentScreen = context.getString(R.string.settings_section_user_interface)
        ))
        add(SearchableSettingItem(
            id = "auto_focus_search",
            title = context.getString(R.string.settings_show_keyboard_on_search_open),
            description = context.getString(R.string.settings_show_keyboard_on_search_open_desc),
            keywords = listOf(
                "search",
                "keyboard",
                "focus",
                "auto focus",
                "search screen",
                "open keyboard",
                "auto keyboard"
            ),
            icon = Icons.Default.Search,
            route = null,
            parentScreen = context.getString(R.string.settings_section_user_interface),
            settingKey = "showKeyboardOnSearchOpen"
        ))
        
        // Audio & Playback Section
        add(SearchableSettingItem(
            id = "system_volume",
            title = context.getString(R.string.settings_system_volume),
            description = context.getString(R.string.settings_system_volume_desc),
            keywords = listOf("volume", "system volume", "audio", "sound", "media volume"),
            icon = RhythmIcons.Player.VolumeUp,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = context.getString(R.string.settings_section_queue_playback),
            settingKey = "useSystemVolume"
        ))
        add(SearchableSettingItem(
            id = "resume_on_device_reconnect",
            title = context.getString(R.string.settings_resume_on_device_reconnect),
            description = context.getString(R.string.settings_resume_on_device_reconnect_desc),
            keywords = listOf("resume", "device", "reconnect", "bluetooth", "headphones", "audio device", "playback"),
            icon = RhythmIcons.Devices.Bluetooth,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = context.getString(R.string.settings_section_queue_playback),
            settingKey = "resumeOnDeviceReconnect"
        ))
        add(SearchableSettingItem(
            id = "show_lyrics",
            title = context.getString(R.string.settings_show_lyrics),
            description = context.getString(R.string.settings_show_lyrics_desc),
            keywords = listOf("lyrics", "show", "display", "text", "song words"),
            icon = Icons.Default.Lyrics,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = context.getString(R.string.settings_player_customization),
            settingKey = "showLyrics"
        ))
        add(SearchableSettingItem(
            id = "lyrics_source",
            title = context.getString(R.string.lyrics_source_priority),
            description = context.getString(R.string.playback_lyrics_priority_desc),
            keywords = listOf("lyrics", "synced lyrics", "lrc", "subtitle", "song text", "karaoke", "source", "priority"),
            icon = Icons.Default.Lyrics,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = context.getString(R.string.settings_player_customization),
            settingKey = "lyricsSource"
        ))
        add(SearchableSettingItem(
            id = "queue_playback",
            title = context.getString(R.string.settings_queue_playback_title),
            description = context.getString(R.string.settings_queue_playback_desc),
            keywords = listOf("queue", "playback", "shuffle", "repeat", "auto queue", "playlist"),
            icon = Icons.Default.QueueMusic,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = context.getString(R.string.settings_section_queue_playback)
        ))
        add(SearchableSettingItem(
            id = "equalizer",
            title = context.getString(R.string.settings_equalizer_title),
            description = context.getString(R.string.settings_equalizer_desc),
            keywords = listOf("equalizer", "eq", "audio", "bass", "treble", "sound", "effects", "audio enhancement"),
            icon = Icons.Default.Equalizer,
            route = SettingsRoutes.EQUALIZER,
            parentScreen = context.getString(R.string.settings_section_audio_lyrics)
        ))

        
        // Library & Media Section
        add(SearchableSettingItem(
            id = "media_scan",
            title = context.getString(R.string.settings_media_scan_title),
            description = context.getString(R.string.settings_media_scan_desc),
            keywords = listOf("media", "scan", "folder", "exclude", "include", "library", "music folder", "directory"),
            icon = Icons.Default.Folder,
            route = SettingsRoutes.MEDIA_SCAN,
            parentScreen = context.getString(R.string.settings_section_library_content)
        ))
        add(SearchableSettingItem(
            id = "media_scan_hidden_whitelist",
            title = context.getString(R.string.settings_include_hidden_whitelisted_media),
            description = context.getString(R.string.settings_include_hidden_whitelisted_media_desc),
            keywords = listOf("hidden", "nomedia", "whitelist", "scan behavior", "folders", "media scan"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.MEDIA_SCAN,
            parentScreen = context.getString(R.string.settings_section_library_content),
            settingKey = "includeHiddenWhitelistedMedia"
        ))
        add(SearchableSettingItem(
            id = "artist_parsing",
            title = context.getString(R.string.settings_artist_parsing),
            description = context.getString(R.string.settings_artist_parsing_desc),
            keywords = listOf("artist", "parsing", "separator", "featuring", "collaboration", "split", "feat"),
            icon = Icons.Default.Person,
            route = SettingsRoutes.ARTIST_SEPARATORS,
            parentScreen = context.getString(R.string.settings_section_library_content)
        ))
        add(SearchableSettingItem(
            id = "playlists",
            title = context.getString(R.string.settings_playlists_title),
            description = context.getString(R.string.settings_playlists_desc),
            keywords = listOf("playlist", "m3u", "import", "export", "manage", "collection"),
            icon = Icons.Default.PlaylistAddCheckCircle,
            route = SettingsRoutes.PLAYLISTS,
            parentScreen = context.getString(R.string.settings_section_library_content)
        ))
        add(SearchableSettingItem(
            id = "library_settings",
            title = context.getString(R.string.settings_library_settings),
            description = context.getString(R.string.settings_library_settings_desc),
            keywords = listOf("library", "settings", "song ratings", "artwork", "album artist", "cover", "blur", "gradient"),
            icon = Icons.Default.LibraryMusic,
            route = SettingsRoutes.LIBRARY_SETTINGS,
            parentScreen = context.getString(R.string.settings_section_library_content)
        ))
        add(SearchableSettingItem(
            id = "song_ratings",
            title = context.getString(R.string.settings_song_ratings),
            description = context.getString(R.string.settings_song_ratings_desc),
            keywords = listOf("rating", "star", "favorite", "like", "score", "rate songs"),
            icon = Icons.Default.Star,
            route = SettingsRoutes.LIBRARY_SETTINGS,
            parentScreen = context.getString(R.string.settings_library_settings),
            settingKey = "enableRatingSystem"
        ))
        add(SearchableSettingItem(
            id = "library_tab_order",
            title = context.getString(R.string.settings_library_tab_order),
            description = context.getString(R.string.settings_library_tab_order_desc),
            keywords = listOf("library", "tab", "order", "reorder", "visibility", "hide tab", "show tab"),
            icon = Icons.Default.Reorder,
            route = SettingsRoutes.LIBRARY_SETTINGS,
            parentScreen = context.getString(R.string.settings_library_settings),
            settingKey = "libraryTabOrder"
        ))
        add(SearchableSettingItem(
            id = "library_combine_discs",
            title = context.getString(R.string.settings_library_combine_discs),
            description = context.getString(R.string.settings_library_combine_discs_desc),
            keywords = listOf("disc", "multi-disc", "combine", "album", "sorting", "track list"),
            icon = Icons.Default.Album,
            route = SettingsRoutes.LIBRARY_SETTINGS,
            parentScreen = context.getString(R.string.settings_library_settings),
            settingKey = "libraryCombineDiscs"
        ))
        
        // Notifications & Services Section
        add(SearchableSettingItem(
            id = "notifications",
            title = context.getString(R.string.settings_notifications),
            description = context.getString(R.string.settings_notifications_desc),
            keywords = listOf("notification", "alert", "media control", "playback notification", "status bar"),
            icon = Icons.Default.Notifications,
            route = SettingsRoutes.NOTIFICATIONS,
            parentScreen = context.getString(R.string.settings_section_notifications_services)
        ))
        add(SearchableSettingItem(
            id = "api_management",
            title = context.getString(R.string.settings_api_management),
            description = context.getString(R.string.settings_api_management_desc),
            keywords = listOf("api", "spotify", "last.fm", "scrobble", "integration", "services", "discord", "rich presence"),
            icon = Icons.Default.Api,
            route = SettingsRoutes.API_MANAGEMENT,
            parentScreen = context.getString(R.string.settings_section_notifications_services)
        ))
        
        // Data & Storage Section
        add(SearchableSettingItem(
            id = "cache_management",
            title = context.getString(R.string.settings_cache_management_title),
            description = context.getString(R.string.settings_cache_management_desc),
            keywords = listOf("cache", "storage", "clear", "delete", "memory", "disk space", "images", "album art"),
            icon = Icons.Default.Storage,
            route = SettingsRoutes.CACHE_MANAGEMENT,
            parentScreen = context.getString(R.string.settings_section_storage_data)
        ))
        add(SearchableSettingItem(
            id = "artwork_cache_size",
            title = context.getString(R.string.settings_artwork_cache_size),
            description = context.getString(R.string.cache_current_status),
            keywords = listOf("artwork cache", "embedded art", "song art cache", "cache size", "album art files", "image cache", "trim artwork cache", "clear all cache"),
            icon = Icons.Default.Storage,
            route = SettingsRoutes.CACHE_MANAGEMENT,
            parentScreen = context.getString(R.string.settings_cache_management_title)
        ))
        add(SearchableSettingItem(
            id = "backup_restore",
            title = context.getString(R.string.settings_backup_restore_title),
            description = context.getString(R.string.settings_backup_restore_desc),
            keywords = listOf("backup", "restore", "export", "import", "settings", "playlists", "data"),
            icon = Icons.Default.Backup,
            route = SettingsRoutes.BACKUP_RESTORE,
            parentScreen = context.getString(R.string.settings_section_storage_data)
        ))
        add(SearchableSettingItem(
            id = "listening_stats",
            title = context.getString(R.string.settings_rhythm_stats),
            description = context.getString(R.string.settings_rhythm_stats_desc),
            keywords = listOf("stats", "statistics", "listening", "history", "play count", "most played", "analytics"),
            icon = Icons.Default.AutoGraph,
            route = SettingsRoutes.LISTENING_STATS,
            parentScreen = context.getString(R.string.settings_section_storage_data)
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard",
            title = context.getString(R.string.settings_rhythm_guard),
            description = context.getString(R.string.settings_rhythm_guard_list_desc),
            keywords = listOf("aura", "ear health", "hearing", "safe listening", "auto mode", "manual mode", "volume warning"),
            icon = Icons.Default.Security,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_section_storage_data)
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_mode",
            title = context.getString(R.string.settings_rhythm_guard_mode_title),
            description = context.getString(R.string.settings_rhythm_guard_mode_desc),
            keywords = listOf("auto", "manual", "off", "mode", "listening health mode"),
            icon = Icons.Default.Security,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardMode"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_enable",
            title = context.getString(R.string.settings_rhythm_guard_enable_search_title),
            description = context.getString(R.string.settings_rhythm_guard_enable_search_desc),
            keywords = listOf("enable", "disable", "on", "off", "protection switch", "guard toggle"),
            icon = Icons.Default.Security,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardMode"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_age",
            title = context.getString(R.string.settings_rhythm_guard_age_search_title),
            description = context.getString(R.string.settings_rhythm_guard_age_search_desc),
            keywords = listOf("age", "hearing profile", "safe volume", "daily limit", "ear health"),
            icon = Icons.Default.Person,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardAge"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_manual_warnings",
            title = context.getString(R.string.settings_rhythm_guard_manual_warning_toggle),
            description = context.getString(R.string.settings_rhythm_guard_manual_warning_toggle_desc),
            keywords = listOf("warning", "manual", "volume warning", "risk warning", "health warning"),
            icon = Icons.Default.Warning,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardManualWarningsEnabled"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_threshold",
            title = context.getString(R.string.settings_rhythm_guard_manual_threshold_search_title),
            description = context.getString(R.string.settings_rhythm_guard_manual_threshold_search_desc),
            keywords = listOf("threshold", "safe volume", "manual threshold", "volume limit", "ear safety"),
            icon = Icons.Default.GraphicEq,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardManualVolumeThreshold"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_alert_threshold",
            title = context.getString(R.string.settings_rhythm_guard_alert_threshold_search_title),
            description = context.getString(R.string.settings_rhythm_guard_alert_threshold_search_desc),
            keywords = listOf("exposure", "alert threshold", "daily limit", "minutes", "safety alert"),
            icon = Icons.Default.Timer,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardAlertThresholdMinutes"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_warning_timeout",
            title = context.getString(R.string.settings_rhythm_guard_warning_timeout_search_title),
            description = context.getString(R.string.settings_rhythm_guard_warning_timeout_search_desc),
            keywords = listOf("cooldown", "alert timeout", "repeat warning", "warning interval"),
            icon = Icons.Default.AccessTime,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardWarningTimeoutMinutes"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_post_timeout_cooldown",
            title = context.getString(R.string.settings_rhythm_guard_post_timeout_cooldown_search_title),
            description = context.getString(R.string.settings_rhythm_guard_post_timeout_cooldown_search_desc),
            keywords = listOf("post-timeout", "recovery cooldown", "timeout cooldown", "break cooldown"),
            icon = Icons.Default.AccessTime,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardPostTimeoutCooldownMinutes"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_protection_presets",
            title = context.getString(R.string.settings_rhythm_guard_protection_presets_search_title),
            description = context.getString(R.string.settings_rhythm_guard_protection_presets_search_desc),
            keywords = listOf("preset", "strict", "balanced", "gentle", "quick setup", "guard profile"),
            icon = Icons.Default.Security,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardProtectionPreset"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_break_resume",
            title = context.getString(R.string.settings_rhythm_guard_break_resume_search_title),
            description = context.getString(R.string.settings_rhythm_guard_break_resume_search_desc),
            keywords = listOf("break", "resume", "timeout length", "scheduled break", "pause duration"),
            icon = Icons.Default.Schedule,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "rhythmGuardBreakResumeMinutes"
        ))
        add(SearchableSettingItem(
            id = "rhythm_guard_zero_volume",
            title = context.getString(R.string.settings_stop_playback_on_zero_volume),
            description = context.getString(R.string.settings_stop_playback_on_zero_volume_desc),
            keywords = listOf("zero volume", "pause on zero", "mute protection", "auto pause"),
            icon = Icons.Default.Stop,
            route = SettingsRoutes.RHYTHM_GUARD,
            parentScreen = context.getString(R.string.settings_rhythm_guard),
            settingKey = "stopPlaybackOnZeroVolume"
        ))
        
        // Updates & Info Section
        add(SearchableSettingItem(
            id = "updates",
            title = context.getString(R.string.settings_updates_title),
            description = context.getString(R.string.settings_updates_desc),
            keywords = listOf("update", "check update", "new version", "download", "changelog", "auto update"),
            icon = Icons.Default.Update,
            route = SettingsRoutes.UPDATES,
            parentScreen = context.getString(R.string.settings_section_updates_info),
            settingKey = "updatesEnabled"
        ))
        add(SearchableSettingItem(
            id = "updates_interval",
            title = context.getString(R.string.updates_check_interval_title),
            description = context.getString(R.string.onboarding_check_interval_desc),
            keywords = listOf("update interval", "check frequency", "hourly", "daily", "weekly", "polling schedule"),
            icon = Icons.Default.Schedule,
            route = SettingsRoutes.UPDATES,
            parentScreen = context.getString(R.string.settings_updates_title),
            settingKey = "updateCheckIntervalHours"
        ))
        add(SearchableSettingItem(
            id = "about",
            title = context.getString(R.string.settings_about_title),
            description = context.getString(R.string.settings_about_desc),
            keywords = listOf("about", "version", "app info", "credits", "developer", "github", "license"),
            icon = Icons.Default.Info,
            route = SettingsRoutes.ABOUT,
            parentScreen = context.getString(R.string.settings_section_updates_info)
        ))
        
        // Advanced Section
        add(SearchableSettingItem(
            id = "crash_log_history",
            title = context.getString(R.string.settings_crash_log_history),
            description = context.getString(R.string.settings_crash_log_history_desc),
            keywords = listOf("crash", "log", "error", "bug", "debug", "report", "history"),
            icon = Icons.Default.BugReport,
            route = SettingsRoutes.CRASH_LOG_HISTORY,
            parentScreen = context.getString(R.string.settings_section_advanced)
        ))
        add(SearchableSettingItem(
            id = "experimental_features",
            title = context.getString(R.string.settings_experimental_features),
            description = context.getString(R.string.settings_experimental_features_desc),
            keywords = listOf("experimental", "beta", "testing", "new features", "labs", "festive", "christmas", "decoration"),
            icon = Icons.Default.Science,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = context.getString(R.string.settings_section_advanced)
        ))
        add(SearchableSettingItem(
            id = "experimental_go_mode",
            title = context.getString(R.string.exp_go_mode),
            description = context.getString(R.string.exp_go_mode_desc),
            keywords = listOf("go mode", "rhythm go", "streaming mode", "streaming navigation", "integration"),
            icon = Icons.Default.CloudQueue,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = context.getString(R.string.settings_experimental_features)
        ))
        
        // ======================== THEME CUSTOMIZATION SCREEN ========================
        add(SearchableSettingItem(
            id = "theme_follow_system",
            title = context.getString(R.string.settings_theme_follow_system),
            description = context.getString(R.string.settings_theme_follow_system_desc),
            keywords = listOf("system theme", "auto", "automatic", "follow system", "dark light"),
            icon = Icons.Default.Settings,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_dark_mode",
            title = context.getString(R.string.settings_theme_dark_mode),
            description = context.getString(R.string.settings_theme_dark_mode_desc),
            keywords = listOf("dark mode", "dark theme", "night mode", "black theme"),
            icon = Icons.Default.DarkMode,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_color_source",
            title = context.getString(R.string.settings_theme_color_source),
            description = context.getString(R.string.settings_theme_color_source_desc),
            keywords = listOf("color source", "album art colors", "monet", "material you", "dynamic colors", "custom colors"),
            icon = Icons.Default.Palette,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_color_schemes",
            title = context.getString(R.string.settings_theme_color_schemes),
            description = context.getString(R.string.settings_theme_color_schemes_desc),
            keywords = listOf("color scheme", "palette", "preset", "default purple", "warm sunset", "cool ocean", "forest green", "rose pink"),
            icon = Icons.Default.ColorLens,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_font_source",
            title = context.getString(R.string.settings_theme_font_source),
            description = context.getString(R.string.settings_theme_font_source_desc),
            keywords = listOf("font", "typography", "text style", "font family", "custom font"),
            icon = Icons.Default.TextFields,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_import_font",
            title = context.getString(R.string.settings_theme_import_font),
            description = context.getString(R.string.settings_theme_import_font_desc),
            keywords = listOf("import font", "custom font", "ttf", "otf", "font file"),
            icon = Icons.Default.FileUpload,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_enabled",
            title = context.getString(R.string.settings_enable_festive),
            description = context.getString(R.string.settings_enable_festive_desc),
            keywords = listOf("festive", "holiday", "christmas", "new year", "decorations", "theme"),
            icon = Icons.Default.Celebration,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveThemeEnabled"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_auto_detect",
            title = context.getString(R.string.settings_auto_detect_holidays),
            description = context.getString(R.string.settings_auto_detect_holidays_desc),
            keywords = listOf("auto detect", "holiday", "seasonal", "automatic", "festive"),
            icon = Icons.Default.AutoAwesome,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveThemeAutoDetect"
        ))
        add(SearchableSettingItem(
            id = "theme_festival_type",
            title = context.getString(R.string.settings_select_festival),
            description = context.getString(R.string.settings_choose_festive_theme),
            keywords = listOf("festival", "christmas", "new year", "holiday theme", "festive type"),
            icon = Icons.Default.Celebration,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveThemeType"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_intensity",
            title = context.getString(R.string.settings_decoration_intensity),
            description = context.getString(R.string.settings_adjust_festive_decorations),
            keywords = listOf("intensity", "decoration", "festive", "amount", "strength"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveThemeIntensity"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_snowflake_size",
            title = context.getString(R.string.settings_snowflake_size),
            description = context.getString(R.string.settings_adjust_snowflake_size),
            keywords = listOf("snowflake", "size", "snow", "festive", "particle size"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveSnowflakeSize"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_snowflake_area",
            title = context.getString(R.string.settings_snowflake_display_area),
            description = context.getString(R.string.settings_toggle_decoration_elements),
            keywords = listOf("snowflake area", "full", "sides", "top", "coverage", "festive"),
            icon = Icons.Default.GraphicEq,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveSnowflakeArea"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_snowfall",
            title = context.getString(R.string.settings_snowfall),
            description = context.getString(R.string.settings_snowfall_desc),
            keywords = listOf("snowfall", "snow", "animation", "festive", "decoration"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveShowSnowfall"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_top_lights",
            title = context.getString(R.string.settings_top_lights),
            description = context.getString(R.string.settings_top_lights_desc),
            keywords = listOf("top lights", "lights", "christmas lights", "festive", "decoration"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveShowTopLights"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_side_garland",
            title = context.getString(R.string.settings_side_garland),
            description = context.getString(R.string.settings_side_garland_desc),
            keywords = listOf("side garland", "garland", "ornaments", "festive", "decoration"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveShowSideGarland"
        ))
        add(SearchableSettingItem(
            id = "theme_festive_snow_pile",
            title = context.getString(R.string.settings_snow_pile),
            description = context.getString(R.string.settings_snow_pile_desc),
            keywords = listOf("snow pile", "bottom snow", "festive", "decoration"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme",
            settingKey = "festiveShowBottomSnow"
        ))
        
        // ======================== PLAYER CUSTOMIZATION SCREEN ========================
        add(SearchableSettingItem(
            id = "player_chip_order",
            title = context.getString(R.string.settings_player_chip_order),
            description = context.getString(R.string.settings_player_chip_order_desc),
            keywords = listOf("chip", "button order", "action chips", "player buttons", "reorder", "visibility"),
            icon = Icons.Default.Reorder,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_show_lyrics",
            title = context.getString(R.string.settings_show_lyrics_player),
            description = context.getString(R.string.settings_show_lyrics_player_desc),
            keywords = listOf("lyrics", "synced lyrics", "karaoke", "text", "song words"),
            icon = Icons.Rounded.Lyrics,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "lyrics_show_translation",
            title = context.getString(R.string.settings_lyrics_show_translation),
            description = context.getString(R.string.settings_lyrics_show_translation_desc),
            keywords = listOf("lyrics", "translation", "translate", "multi-language", "subtitle"),
            icon = Icons.Default.Public,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "showLyricsTranslation"
        ))
        add(SearchableSettingItem(
            id = "lyrics_show_romanization",
            title = context.getString(R.string.settings_lyrics_show_romanization),
            description = context.getString(R.string.settings_lyrics_show_romanization_desc),
            keywords = listOf("lyrics", "romanization", "romaji", "pinyin", "transliteration"),
            icon = Icons.Default.TextFields,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "showLyricsRomanization"
        ))
        add(SearchableSettingItem(
            id = "keep_screen_on_lyrics",
            title = context.getString(R.string.settings_keep_screen_on_lyrics),
            description = context.getString(R.string.settings_keep_screen_on_lyrics_desc),
            keywords = listOf("screen", "awake", "wake", "lyrics", "screen on", "display", "timeout", "dim"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player",
            settingKey = "keepScreenOnLyrics"
        ))
        add(SearchableSettingItem(
            id = "embed_lyrics_in_file",
            title = context.getString(R.string.settings_embed_lyrics_in_file),
            description = context.getString(R.string.settings_embed_lyrics_in_file_desc),
            keywords = listOf("embed", "lyrics", "file", "metadata", "write", "save", "tag", "id3"),
            icon = Icons.Default.MusicNote,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "lossless_artwork",
            title = context.getString(R.string.settings_lossless_artwork),
            description = context.getString(R.string.settings_lossless_artwork_desc),
            keywords = listOf("lossless", "artwork", "png", "quality", "album art", "image", "uncompressed", "high quality"),
            icon = Icons.Default.HighQuality,
            route = SettingsRoutes.LIBRARY_SETTINGS,
            parentScreen = context.getString(R.string.settings_library_settings),
            settingKey = "losslessArtwork"
        ))
        add(SearchableSettingItem(
            id = "player_gradient",
            title = context.getString(R.string.settings_player_gradient),
            description = context.getString(R.string.settings_player_gradient_desc),
            keywords = listOf("gradient", "overlay", "artwork gradient", "background"),
            icon = Icons.Default.Gradient,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_seek_buttons",
            title = context.getString(R.string.settings_player_seek_buttons),
            description = context.getString(R.string.settings_player_seek_buttons_desc),
            keywords = listOf("seek", "skip", "forward", "backward", "10 seconds", "rewind"),
            icon = Icons.Default.Forward10,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_text_alignment",
            title = context.getString(R.string.settings_player_text_alignment),
            description = context.getString(R.string.settings_player_text_alignment_desc),
            keywords = listOf("text", "alignment", "left", "center", "right", "title position"),
            icon = Icons.Default.FormatAlignCenter,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_progress_style",
            title = context.getString(R.string.settings_player_progress_style),
            description = context.getString(R.string.settings_player_progress_style_desc),
            keywords = listOf("progress bar", "seekbar", "style", "wavy", "dotted", "dashed", "glowing"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_artwork_radius",
            title = context.getString(R.string.settings_player_artwork_radius),
            description = context.getString(R.string.settings_player_artwork_radius_desc),
            keywords = listOf("artwork", "corner", "radius", "rounded", "square", "album art shape"),
            icon = Icons.Default.RoundedCorner,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_quality_badges",
            title = context.getString(R.string.settings_player_quality_badges),
            description = context.getString(R.string.settings_player_quality_badges_desc),
            keywords = listOf("quality", "badge", "codec", "bitrate", "flac", "mp3", "audio format"),
            icon = Icons.Default.HighQuality,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        
        // ======================== MINIPLAYER CUSTOMIZATION SCREEN ========================
        add(SearchableSettingItem(
            id = "miniplayer_show_progress",
            title = context.getString(R.string.settings_miniplayer_show_progress),
            description = context.getString(R.string.settings_miniplayer_show_progress_desc),
            keywords = listOf("miniplayer progress", "progress bar", "indicator", "mini player"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_circular_progress",
            title = context.getString(R.string.settings_miniplayer_circular_progress),
            description = context.getString(R.string.settings_miniplayer_circular_progress_desc),
            keywords = listOf("circular", "progress", "round", "play button", "miniplayer"),
            icon = Icons.Default.ChangeCircle,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_progress_style",
            title = context.getString(R.string.settings_miniplayer_progress_style),
            description = context.getString(R.string.settings_miniplayer_progress_style_desc),
            keywords = listOf("progress style", "miniplayer", "wavy", "dotted", "normal"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_show_artwork",
            title = context.getString(R.string.settings_miniplayer_show_artwork),
            description = context.getString(R.string.settings_miniplayer_show_artwork_desc),
            keywords = listOf("artwork", "album art", "cover", "image", "miniplayer"),
            icon = Icons.Default.Album,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_artwork_size",
            title = context.getString(R.string.settings_miniplayer_artwork_size),
            description = context.getString(R.string.settings_miniplayer_artwork_size_desc),
            keywords = listOf("artwork size", "image size", "cover size", "miniplayer"),
            icon = Icons.Default.PhotoSizeSelectLarge,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_corner_radius",
            title = context.getString(R.string.settings_miniplayer_corner_radius),
            description = context.getString(R.string.settings_miniplayer_corner_radius_desc),
            keywords = listOf("corner", "radius", "rounded", "shape", "miniplayer"),
            icon = Icons.Default.RoundedCorner,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_show_time",
            title = context.getString(R.string.settings_miniplayer_show_time),
            description = context.getString(R.string.settings_miniplayer_show_time_desc),
            keywords = listOf("time", "duration", "elapsed", "remaining", "miniplayer"),
            icon = Icons.Default.Timer,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_tablet_layout",
            title = context.getString(R.string.settings_miniplayer_tablet_layout),
            description = context.getString(R.string.settings_miniplayer_tablet_layout_desc),
            keywords = listOf("tablet", "layout", "phone", "style", "miniplayer"),
            icon = Icons.Default.Tablet,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        
        // ======================== GESTURES SCREEN ========================
        add(SearchableSettingItem(
            id = "gesture_miniplayer_swipe",
            title = context.getString(R.string.settings_gesture_miniplayer_swipe),
            description = context.getString(R.string.settings_gesture_miniplayer_swipe_desc),
            keywords = listOf("swipe", "gesture", "miniplayer", "up", "down", "left", "right", "skip"),
            icon = Icons.Rounded.Swipe,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        add(SearchableSettingItem(
            id = "gesture_player_dismiss",
            title = context.getString(R.string.settings_gesture_player_dismiss),
            description = context.getString(R.string.settings_gesture_player_dismiss_desc),
            keywords = listOf("swipe down", "dismiss", "close", "player", "gesture"),
            icon = Icons.Rounded.SwipeDown,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        add(SearchableSettingItem(
            id = "gesture_artwork_swipe",
            title = context.getString(R.string.settings_gesture_artwork_swipe),
            description = context.getString(R.string.settings_gesture_artwork_swipe_desc),
            keywords = listOf("swipe", "artwork", "album art", "skip", "next", "previous"),
            icon = Icons.Rounded.SwipeLeft,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        add(SearchableSettingItem(
            id = "gesture_double_tap",
            title = context.getString(R.string.settings_gesture_double_tap),
            description = context.getString(R.string.settings_gesture_double_tap_desc),
            keywords = listOf("double tap", "artwork", "play", "pause", "tap gesture"),
            icon = Icons.Rounded.TouchApp,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        
        // ======================== QUEUE & PLAYBACK SCREEN ========================
        add(SearchableSettingItem(
            id = "queue_exoplayer_shuffle",
            title = context.getString(R.string.settings_use_exoplayer_shuffle),
            description = context.getString(R.string.settings_use_exoplayer_shuffle_desc),
            keywords = listOf("shuffle", "exoplayer", "random", "playback", "algorithm"),
            icon = RhythmIcons.Shuffle,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_auto_add",
            title = context.getString(R.string.settings_queue_auto_add),
            description = context.getString(R.string.settings_queue_auto_add_desc),
            keywords = listOf("auto queue", "add", "automatic", "playlist"),
            icon = RhythmIcons.Queue,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "autoAddToQueue"
        ))
        add(SearchableSettingItem(
            id = "queue_clear_on_new",
            title = context.getString(R.string.settings_queue_clear_on_new),
            description = context.getString(R.string.settings_queue_clear_on_new_desc),
            keywords = listOf("clear queue", "new song", "replace", "reset"),
            icon = RhythmIcons.Delete,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "clearQueueOnNewSong"
        ))
        add(SearchableSettingItem(
            id = "shuffle_engine",
            title = context.getString(R.string.settings_use_exoplayer_shuffle),
            description = context.getString(R.string.settings_use_exoplayer_shuffle_desc),
            keywords = listOf("shuffle engine", "exoplayer shuffle", "shuffle timeline", "shuffle mode", "shuffle algorithm"),
            icon = RhythmIcons.Shuffle,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "shuffleUsesExoplayer"
        ))
        add(SearchableSettingItem(
            id = "queue_hide_played",
            title = context.getString(R.string.settings_show_played_queue_songs),
            description = context.getString(R.string.settings_show_played_queue_songs_desc),
            keywords = listOf("queue", "played", "history", "show", "finished songs", "already played"),
            icon = RhythmIcons.Queue,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "hidePlayedQueueSongs"
        ))
        add(SearchableSettingItem(
            id = "queue_action_dialog",
            title = context.getString(R.string.settings_queue_action_dialog),
            description = context.getString(R.string.settings_queue_action_dialog_desc),
            keywords = listOf("queue dialog", "ask", "prompt", "action", "replace queue"),
            icon = RhythmIcons.Queue,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "showQueueDialog"
        ))
        add(SearchableSettingItem(
            id = "queue_repeat_persistence",
            title = context.getString(R.string.settings_queue_repeat_persistence),
            description = context.getString(R.string.settings_queue_repeat_persistence_desc),
            keywords = listOf("repeat", "remember", "save", "persistence", "loop"),
            icon = RhythmIcons.Repeat,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "repeatModePersistence"
        ))
        add(SearchableSettingItem(
            id = "queue_shuffle_persistence",
            title = context.getString(R.string.settings_queue_shuffle_persistence),
            description = context.getString(R.string.settings_queue_shuffle_persistence_desc),
            keywords = listOf("shuffle", "remember", "save", "persistence", "random"),
            icon = RhythmIcons.Shuffle,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "shuffleModePersistence"
        ))
        add(SearchableSettingItem(
            id = "queue_stop_on_close",
            title = context.getString(R.string.settings_queue_stop_on_close),
            description = context.getString(R.string.settings_queue_stop_on_close_desc),
            keywords = listOf("stop", "playback", "close", "exit", "quit"),
            icon = Icons.Default.Stop,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "stopPlaybackOnAppClose"
        ))
        add(SearchableSettingItem(
            id = "sleep_timer",
            title = context.getString(R.string.settings_sleep_timer_search),
            description = context.getString(R.string.settings_sleep_timer_search_desc),
            keywords = listOf("sleep", "timer", "auto stop", "automatic", "fade out", "pause", "bedtime"),
            icon = Icons.Default.Timer,
            route = SettingsRoutes.SLEEP_TIMER,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_hours_format",
            title = context.getString(R.string.settings_queue_hours_format),
            description = context.getString(R.string.settings_queue_hours_format_desc),
            keywords = listOf("hours", "time", "format", "duration", "display"),
            icon = Icons.Default.AccessTime,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "useHoursInTimeFormat"
        ))
        add(SearchableSettingItem(
            id = "gapless_playback",
            title = context.getString(R.string.settings_gapless_playback),
            description = context.getString(R.string.settings_gapless_playback_desc),
            keywords = listOf("gapless", "transition", "silence", "playback", "next track"),
            icon = Icons.Default.GraphicEq,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "gaplessPlayback"
        ))
        add(SearchableSettingItem(
            id = "crossfade",
            title = context.getString(R.string.settings_crossfade),
            description = context.getString(R.string.settings_crossfade_desc),
            keywords = listOf("crossfade", "transition", "fade", "overlap", "smooth", "songs", "playback"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "crossfade"
        ))
        add(SearchableSettingItem(
            id = "crossfade_repeat_one",
            title = context.getString(R.string.settings_crossfade_repeat_one),
            description = context.getString(R.string.settings_crossfade_repeat_one_desc),
            keywords = listOf("crossfade", "repeat one", "loop one", "transition", "single track"),
            icon = RhythmIcons.Repeat,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "crossfadeRepeatOne"
        ))
        add(SearchableSettingItem(
            id = "crossfade_duration",
            title = context.getString(R.string.settings_crossfade_duration),
            description = context.getString(R.string.settings_crossfade_duration_desc, 4.0f),
            keywords = listOf("crossfade", "duration", "seconds", "time", "length", "transition"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "crossfadeDuration"
        ))
        add(SearchableSettingItem(
            id = "queue_persistence",
            title = context.getString(R.string.settings_queue_persistence),
            description = context.getString(R.string.settings_queue_persistence_desc),
            keywords = listOf("queue", "remember", "save", "restore", "persistence", "restart", "app"),
            icon = RhythmIcons.Queue,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "queuePersistenceEnabled"
        ))
        add(SearchableSettingItem(
            id = "playlist_action_dialog",
            title = context.getString(R.string.settings_playlist_action_dialog),
            description = context.getString(R.string.settings_playlist_action_dialog_desc),
            keywords = listOf("playlist", "action", "dialog", "click", "behavior", "load", "play"),
            icon = Icons.Default.QueueMusic,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "playlistClickBehavior"
        ))
        add(SearchableSettingItem(
            id = "list_queue_action_dialog",
            title = context.getString(R.string.settings_list_queue_action_dialog),
            description = context.getString(R.string.settings_list_queue_action_dialog_desc),
            keywords = listOf("queue", "play all", "section", "replace", "play next", "add to end", "behavior", "rule"),
            icon = Icons.Default.QueueMusic,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "listQueueActionBehavior"
        ))
        
        // ======================== EXPERIMENTAL FEATURES SCREEN ========================
        
        // High-Resolution Audio
        add(SearchableSettingItem(
            id = "bit_perfect_mode",
            title = context.getString(R.string.settings_bit_perfect_mode),
            description = context.getString(R.string.settings_bit_perfect_mode_desc_native),
            keywords = listOf("high-resolution", "hi-res", "bit perfect", "bit-perfect", "audio", "sample rate", "resampling", "hi-res", "quality", "lossless", "44.1khz", "48khz", "96khz", "192khz", "native", "dac", "exclusive", "exclusive usb", "usb-c", "otg"),
            icon = Icons.Default.HighQuality,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "bitPerfectMode"
        ))
        add(SearchableSettingItem(
            id = "audio_routing_mode",
            title = context.getString(R.string.settings_audio_routing),
            description = context.getString(R.string.settings_dac_usb_audio_desc),
            keywords = listOf("audio routing", "dac", "usb audio", "default routing", "app routing", "system routing", "output", "exclusive", "exclusive usb", "usb-c", "bit perfect", "direct"),
            icon = Icons.Default.Headphones,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "audioRoutingMode"
        ))
        
        add(SearchableSettingItem(
            id = "exp_festive_theme",
            title = context.getString(R.string.settings_exp_festive_theme),
            description = context.getString(R.string.settings_exp_festive_theme_desc),
            keywords = listOf("festive", "christmas", "new year", "decoration", "snow", "snowflake"),
            icon = Icons.Default.Celebration,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_auto_detect_holidays",
            title = context.getString(R.string.settings_exp_auto_detect_holidays),
            description = context.getString(R.string.settings_exp_auto_detect_holidays_desc),
            keywords = listOf("auto detect", "holiday", "automatic", "festive", "seasonal"),
            icon = Icons.Default.AutoAwesome,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_ignore_mediastore",
            title = context.getString(R.string.settings_exp_ignore_mediastore),
            description = context.getString(R.string.settings_exp_ignore_mediastore_desc),
            keywords = listOf("song art", "song artwork", "mediastore", "album art", "cover", "extract", "embedded"),
            icon = RhythmIcons.Album,
            route = SettingsRoutes.LIBRARY_SETTINGS,
            parentScreen = context.getString(R.string.settings_library_settings),
            settingKey = "preferSongArtwork"
        ))
        add(SearchableSettingItem(
            id = "exp_codec_monitoring",
            title = context.getString(R.string.settings_exp_codec_monitoring),
            description = context.getString(R.string.settings_exp_codec_monitoring_desc),
            keywords = listOf("codec", "debug", "log", "monitoring", "audio format"),
            icon = Icons.Default.Code,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_audio_device_logging",
            title = context.getString(R.string.settings_exp_audio_device_logging),
            description = context.getString(R.string.settings_exp_audio_device_logging_desc),
            keywords = listOf("audio device", "bluetooth", "headphones", "log", "debug"),
            icon = Icons.Default.Headphones,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_launch_onboarding",
            title = context.getString(R.string.settings_exp_launch_onboarding),
            description = context.getString(R.string.settings_exp_launch_onboarding_desc),
            keywords = listOf("rhythm tour", "tour", "reset", "restart", "welcome", "setup", "intro", "onboarding"),
            icon = Icons.Default.RestartAlt,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_test_crash",
            title = context.getString(R.string.settings_exp_test_crash),
            description = context.getString(R.string.settings_exp_test_crash_desc),
            keywords = listOf("crash", "test", "debug", "error", "reporting"),
            icon = Icons.Default.BugReport,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "scrobbling_enabled",
            title = context.getString(R.string.settings_scrobbling_enabled),
            description = context.getString(R.string.settings_scrobbling_enabled_desc),
            keywords = listOf("scrobbling", "last.fm", "listening data", "music tracking"),
            icon = Icons.Default.MusicNote,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "scrobblingEnabled"
        ))
        add(SearchableSettingItem(
            id = "discord_enabled",
            title = context.getString(R.string.settings_discord_enabled_search),
            description = context.getString(R.string.settings_discord_enabled_search_desc),
            keywords = listOf("discord", "rich presence", "status", "show listening"),
            icon = Icons.Default.Public,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "discordRichPresenceEnabled"
        ))
        add(SearchableSettingItem(
            id = "broadcast_status_enabled",
            title = context.getString(R.string.settings_broadcast_status_enabled),
            description = context.getString(R.string.settings_broadcast_status_enabled_desc),
            keywords = listOf("broadcast", "status", "playback", "share", "other apps"),
            icon = Icons.Default.Public,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "broadcastStatusEnabled"
        ))
        add(SearchableSettingItem(
            id = "bluetooth_lyrics_enabled",
            title = context.getString(R.string.settings_bluetooth_lyrics_enabled),
            description = context.getString(R.string.settings_bluetooth_lyrics_enabled_desc),
            keywords = listOf("bluetooth", "lyrics", "avrcp", "metadata", "rokid", "smart glasses"),
            icon = Icons.Default.Lyrics,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental",
            settingKey = "bluetoothLyricsEnabled"
        ))
        add(SearchableSettingItem(
            id = "home_section_order",
            title = context.getString(R.string.settings_home_section_order),
            description = context.getString(R.string.settings_home_section_order_desc),
            keywords = listOf("section order", "home", "reorder", "arrange", "layout"),
            icon = Icons.Default.Reorder,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_greeting",
            title = context.getString(R.string.settings_home_greeting_search),
            description = context.getString(R.string.settings_home_greeting_search_desc),
            keywords = listOf("greeting", "hello", "welcome", "message", "home"),
            icon = Icons.Default.Info,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_recently_played",
            title = context.getString(R.string.settings_home_recently_played),
            description = context.getString(R.string.settings_home_recently_played_desc),
            keywords = listOf("recently played", "history", "recent", "last played"),
            icon = Icons.Default.AccessTime,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_discover_carousel",
            title = context.getString(R.string.settings_home_discover_carousel),
            description = context.getString(R.string.settings_home_discover_carousel_desc),
            keywords = listOf("discover", "carousel", "featured", "slider", "banner"),
            icon = Icons.Default.AutoAwesome,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_carousel_auto_scroll",
            title = context.getString(R.string.settings_home_carousel_auto_scroll),
            description = context.getString(R.string.settings_home_carousel_auto_scroll_desc),
            keywords = listOf("auto scroll", "carousel", "automatic", "slide"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        
        // ======================== NOTIFICATIONS SCREEN ========================
        add(SearchableSettingItem(
            id = "notifications_updates",
            title = context.getString(R.string.settings_update_notifications),
            description = context.getString(R.string.settings_update_notifications_merged_desc),
            keywords = listOf("updates", "update notifications", "new version", "release", "update available", "up to date", "update error", "check result"),
            icon = Icons.Default.Update,
            route = SettingsRoutes.NOTIFICATIONS,
            parentScreen = "Notifications",
            settingKey = "updateNotificationsEnabled"
        ))
        add(SearchableSettingItem(
            id = "notifications_rhythm_guard_alerts",
            title = context.getString(R.string.settings_notifications_rhythm_guard_alerts),
            description = context.getString(R.string.settings_notifications_rhythm_guard_alerts_desc),
            keywords = listOf("rhythm guard", "safety alert", "hearing warning", "volume risk", "exposure alert"),
            icon = Icons.Default.Warning,
            route = SettingsRoutes.NOTIFICATIONS,
            parentScreen = "Notifications",
            settingKey = "rhythmGuardAlertNotificationsEnabled"
        ))
        add(SearchableSettingItem(
            id = "notifications_rhythm_guard_timers",
            title = context.getString(R.string.settings_notifications_rhythm_guard_timers),
            description = context.getString(R.string.settings_notifications_rhythm_guard_timers_desc),
            keywords = listOf("rhythm guard timer", "break timer", "timeout", "resume countdown", "listening break"),
            icon = Icons.Default.Timer,
            route = SettingsRoutes.NOTIFICATIONS,
            parentScreen = "Notifications",
            settingKey = "rhythmGuardTimerNotificationsEnabled"
        ))
        add(SearchableSettingItem(
            id = "notifications_rhythm_pulse",
            title = context.getString(R.string.settings_notifications_rhythm_pulse),
            description = context.getString(R.string.settings_notifications_rhythm_pulse_desc),
            keywords = listOf("rhythm tips", "greetings", "comic tips", "music tips", "motivational notifications"),
            icon = Icons.Default.Celebration,
            route = SettingsRoutes.NOTIFICATIONS,
            parentScreen = "Notifications",
            settingKey = "rhythmPulseNotificationsEnabled"
        ))
        add(SearchableSettingItem(
            id = "notifications_rhythm_pulse_interval",
            title = context.getString(R.string.settings_notifications_rhythm_pulse_interval),
            description = context.getString(R.string.settings_notifications_rhythm_pulse_interval_desc),
            keywords = listOf("tips interval", "rhythm tips frequency", "hours", "6 hours", "24 hours", "72 hours"),
            icon = Icons.Default.Schedule,
            route = SettingsRoutes.NOTIFICATIONS,
            parentScreen = "Notifications",
            settingKey = "rhythmPulseNotificationIntervalHours"
        ))
        
        // ======================== EXPRESSIVE SHAPES SCREEN ========================
        add(SearchableSettingItem(
            id = "expressive_shapes_enabled",
            title = context.getString(R.string.settings_expressive_shapes_enabled),
            description = context.getString(R.string.settings_expressive_shapes_enabled_search_desc),
            keywords = listOf("shapes", "expressive", "custom", "ui", "design", "artwork", "corners"),
            icon = Icons.Default.Palette,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Settings",
            settingKey = "expressiveShapesEnabled"
        ))
        add(SearchableSettingItem(
            id = "shape_preset",
            title = context.getString(R.string.settings_shape_preset),
            description = context.getString(R.string.settings_shape_preset_desc),
            keywords = listOf("preset", "shapes", "collection", "playful", "organic", "geometric", "retro", "custom"),
            icon = Icons.Default.ColorLens,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePreset"
        ))
        add(SearchableSettingItem(
            id = "shape_album_art",
            title = context.getString(R.string.settings_shape_album_art),
            description = context.getString(R.string.settings_shape_album_art_desc),
            keywords = listOf("album", "artwork", "shape", "cover", "image", "display"),
            icon = Icons.Default.Album,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapeAlbumArt"
        ))
        add(SearchableSettingItem(
            id = "shape_player_art",
            title = context.getString(R.string.settings_shape_player_art),
            description = context.getString(R.string.settings_shape_player_art_desc),
            keywords = listOf("player", "artwork", "shape", "screen", "display", "now playing"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePlayerArt"
        ))
        add(SearchableSettingItem(
            id = "shape_song_art",
            title = context.getString(R.string.settings_shape_song_art),
            description = context.getString(R.string.settings_shape_song_art_desc),
            keywords = listOf("song", "artwork", "shape", "list", "thumbnail", "image"),
            icon = Icons.Default.MusicNote,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapeSongArt"
        ))
        add(SearchableSettingItem(
            id = "shape_playlist_art",
            title = context.getString(R.string.settings_shape_playlist_art),
            description = context.getString(R.string.settings_shape_playlist_art_desc),
            keywords = listOf("playlist", "artwork", "shape", "cover", "collection"),
            icon = Icons.Default.PlaylistAddCheckCircle,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePlaylistArt"
        ))
        add(SearchableSettingItem(
            id = "shape_artist_art",
            title = context.getString(R.string.settings_shape_artist_art),
            description = context.getString(R.string.settings_shape_artist_art_desc),
            keywords = listOf("artist", "artwork", "shape", "image", "profile", "photo"),
            icon = Icons.Default.Person,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapeArtistArt"
        ))
        add(SearchableSettingItem(
            id = "shape_player_controls",
            title = context.getString(R.string.settings_shape_player_controls),
            description = context.getString(R.string.settings_shape_player_controls_desc),
            keywords = listOf("player", "controls", "shape", "buttons", "play", "pause", "skip"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePlayerControls"
        ))
        add(SearchableSettingItem(
            id = "shape_mini_player",
            title = context.getString(R.string.settings_shape_mini_player),
            description = context.getString(R.string.settings_shape_mini_player_desc),
            keywords = listOf("mini player", "artwork", "shape", "compact", "bottom bar"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapeMiniPlayer"
        ))
    }
}

/**
 * Performs search on the settings index
 */
fun searchSettings(query: String, index: List<SearchableSettingItem>): List<SearchableSettingItem> {
    if (query.isBlank()) return emptyList()
    
    val normalizedQuery = query.lowercase().trim()
    val queryWords = normalizedQuery.split(" ").filter { it.isNotBlank() }
    
    return index.filter { item ->
        val titleMatch = item.title.lowercase().contains(normalizedQuery)
        val descMatch = item.description.lowercase().contains(normalizedQuery)
        val keywordMatch = item.keywords.any { keyword ->
            keyword.lowercase().contains(normalizedQuery) ||
            queryWords.any { word -> keyword.lowercase().contains(word) }
        }
        val parentMatch = item.parentScreen.lowercase().contains(normalizedQuery)
        
        titleMatch || descMatch || keywordMatch || parentMatch
    }.sortedByDescending { item ->
        // Prioritize exact title matches, then keyword matches
        when {
            item.title.lowercase() == normalizedQuery -> 100
            item.title.lowercase().startsWith(normalizedQuery) -> 90
            item.title.lowercase().contains(normalizedQuery) -> 80
            item.keywords.any { it.lowercase() == normalizedQuery } -> 70
            item.keywords.any { it.lowercase().startsWith(normalizedQuery) } -> 60
            item.keywords.any { it.lowercase().contains(normalizedQuery) } -> 50
            item.description.lowercase().contains(normalizedQuery) -> 40
            else -> 30
        }
    }
}

/**
 * Search bar composable for settings
 */
@Composable
fun SettingsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = context.getString(R.string.search_settings_hint),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )
            
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = context.getString(R.string.clear_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            onQueryChange("")
                        }
                )
            }
        }
    }
}

/**
 * Search results list composable
 */
@Composable
fun SettingsSearchResults(
    results: List<SearchableSettingItem>,
    onResultClick: (SearchableSettingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        if (results.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = context.getString(R.string.no_results_found),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = context.getString(R.string.try_different_search),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Group results by parent screen
            val groupedResults = results.groupBy { it.parentScreen }
            
            groupedResults.forEach { (screenName, items) ->
                item(key = "group_$screenName") {
                    Spacer(modifier = Modifier.height(16.dp))

                    val materialItems = items.map { setting ->
                        Material3SettingsItem(
                            icon = setting.icon,
                            title = { Text(setting.title) },
                            description = { Text(setting.description) },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = context.getString(R.string.cd_navigate),
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                onResultClick(setting)
                            }
                        )
                    }

                    Material3SettingsGroup(
                        title = screenName,
                        items = materialItems,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

