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
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Equalizer
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
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
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
        
        // Appearance Section
        add(SearchableSettingItem(
            id = "theme_customization",
            title = context.getString(R.string.settings_theme_customization),
            description = context.getString(R.string.settings_theme_customization_desc),
            keywords = listOf("theme", "color", "appearance", "dark mode", "light mode", "colors", "customize", "style"),
            icon = Icons.Default.Palette,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "home_customization",
            title = context.getString(R.string.settings_home_customization),
            description = context.getString(R.string.settings_home_customization_desc),
            keywords = listOf("home", "screen", "layout", "sections", "customize", "greeting", "carousel", "discover"),
            icon = Icons.Default.Home,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "widget_settings",
            title = context.getString(R.string.settings_widget),
            description = context.getString(R.string.settings_widget_desc),
            keywords = listOf("widget", "home screen", "launcher", "music widget", "album art"),
            icon = Icons.Default.Widgets,
            route = SettingsRoutes.WIDGET,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "player_customization",
            title = context.getString(R.string.settings_player_customization),
            description = context.getString(R.string.settings_player_customization_desc),
            keywords = listOf("player", "now playing", "full player", "music player", "controls", "artwork"),
            icon = Icons.Default.MusicNote,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_customization",
            title = context.getString(R.string.settings_miniplayer_customization),
            description = context.getString(R.string.settings_miniplayer_customization_desc),
            keywords = listOf("miniplayer", "mini player", "compact player", "bottom bar", "progress"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "album_bottom_sheet_blur",
            title = context.getString(R.string.settings_album_bottom_sheet_gradient_blur),
            description = context.getString(R.string.settings_album_bottom_sheet_gradient_blur_desc),
            keywords = listOf("album", "bottom sheet", "gradient", "blur", "effect", "background"),
            icon = Icons.Default.LensBlur,
            route = null,
            parentScreen = "Settings",
            settingKey = "albumBottomSheetGradientBlur"
        ))
        
        // User Interface Section
        add(SearchableSettingItem(
            id = "default_screen",
            title = context.getString(R.string.settings_default_screen),
            description = context.getString(R.string.settings_default_screen_desc),
            keywords = listOf("default", "screen", "start", "launch", "home", "library", "startup"),
            icon = Icons.Default.Home,
            route = null,
            parentScreen = "Settings",
            settingKey = "defaultScreen"
        ))
        add(SearchableSettingItem(
            id = "language",
            title = context.getString(R.string.settings_language),
            description = context.getString(R.string.settings_language_desc),
            keywords = listOf("language", "locale", "translation", "english", "spanish", "french", "german", "hindi", "chinese", "japanese", "korean"),
            icon = Icons.Default.Public,
            route = null,
            parentScreen = "Settings",
            settingKey = "language"
        ))
        add(SearchableSettingItem(
            id = "haptic_feedback",
            title = context.getString(R.string.settings_haptic_feedback),
            description = context.getString(R.string.settings_haptic_feedback_desc),
            keywords = listOf("haptic", "vibration", "feedback", "touch", "vibrate"),
            icon = Icons.Default.TouchApp,
            route = null,
            parentScreen = "Settings",
            settingKey = "hapticFeedback"
        ))
        add(SearchableSettingItem(
            id = "gestures",
            title = context.getString(R.string.settings_gestures),
            description = context.getString(R.string.settings_gestures_desc),
            keywords = listOf("gestures", "swipe", "touch", "double tap", "navigation"),
            icon = Icons.Default.Gesture,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Settings"
        ))
        
        // Audio & Playback Section
        add(SearchableSettingItem(
            id = "system_volume",
            title = context.getString(R.string.settings_system_volume),
            description = context.getString(R.string.settings_system_volume_desc),
            keywords = listOf("volume", "system volume", "audio", "sound", "media volume"),
            icon = RhythmIcons.Player.VolumeUp,
            route = null,
            parentScreen = "Settings",
            settingKey = "useSystemVolume"
        ))
        add(SearchableSettingItem(
            id = "lyrics_source",
            title = context.getString(R.string.lyrics_source_priority),
            description = context.getString(R.string.playback_lyrics_priority_desc),
            keywords = listOf("lyrics", "synced lyrics", "lrc", "subtitle", "song text", "karaoke"),
            icon = Icons.Default.Lyrics,
            route = null,
            parentScreen = "Settings",
            settingKey = "lyricsSource"
        ))
        add(SearchableSettingItem(
            id = "queue_playback",
            title = context.getString(R.string.settings_queue_playback_title),
            description = context.getString(R.string.settings_queue_playback_desc),
            keywords = listOf("queue", "playback", "shuffle", "repeat", "auto queue", "playlist"),
            icon = Icons.Default.QueueMusic,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "equalizer",
            title = context.getString(R.string.settings_equalizer_title),
            description = context.getString(R.string.settings_equalizer_desc),
            keywords = listOf("equalizer", "eq", "audio", "bass", "treble", "sound", "effects", "audio enhancement"),
            icon = Icons.Default.Equalizer,
            route = SettingsRoutes.EQUALIZER,
            parentScreen = "Settings"
        ))
        
        // Library Content Section
        add(SearchableSettingItem(
            id = "artist_parsing",
            title = context.getString(R.string.settings_artist_parsing),
            description = context.getString(R.string.settings_artist_parsing_desc),
            keywords = listOf("artist", "parsing", "separator", "featuring", "collaboration", "split", "feat"),
            icon = Icons.Default.Person,
            route = SettingsRoutes.ARTIST_SEPARATORS,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "media_scan",
            title = context.getString(R.string.settings_media_scan_title),
            description = context.getString(R.string.settings_media_scan_desc),
            keywords = listOf("media", "scan", "folder", "exclude", "include", "library", "music folder", "directory"),
            icon = Icons.Default.Folder,
            route = SettingsRoutes.MEDIA_SCAN,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "playlists",
            title = context.getString(R.string.settings_playlists_title),
            description = context.getString(R.string.settings_playlists_desc),
            keywords = listOf("playlist", "m3u", "import", "export", "manage", "collection"),
            icon = Icons.Default.PlaylistAddCheckCircle,
            route = SettingsRoutes.PLAYLISTS,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "song_ratings",
            title = "Song Ratings",
            description = "Enable or disable song rating system",
            keywords = listOf("rating", "star", "favorite", "like", "score", "rate songs"),
            icon = Icons.Default.Star,
            route = null,
            parentScreen = "Settings",
            settingKey = "enableRatingSystem"
        ))
        
        // Storage & Data Section
        add(SearchableSettingItem(
            id = "cache_management",
            title = context.getString(R.string.settings_cache_management_title),
            description = context.getString(R.string.settings_cache_management_desc),
            keywords = listOf("cache", "storage", "clear", "delete", "memory", "disk space", "images", "album art"),
            icon = Icons.Default.Storage,
            route = SettingsRoutes.CACHE_MANAGEMENT,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "backup_restore",
            title = context.getString(R.string.settings_backup_restore_title),
            description = context.getString(R.string.settings_backup_restore_desc),
            keywords = listOf("backup", "restore", "export", "import", "settings", "playlists", "data"),
            icon = Icons.Default.Backup,
            route = SettingsRoutes.BACKUP_RESTORE,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "listening_stats",
            title = context.getString(R.string.settings_rhythm_stats),
            description = context.getString(R.string.settings_rhythm_stats_desc),
            keywords = listOf("stats", "statistics", "listening", "history", "play count", "most played", "analytics"),
            icon = Icons.Default.AutoGraph,
            route = SettingsRoutes.LISTENING_STATS,
            parentScreen = "Settings"
        ))
        
        // Integrations Section
        add(SearchableSettingItem(
            id = "api_management",
            title = context.getString(R.string.settings_api_management),
            description = context.getString(R.string.settings_api_management_desc),
            keywords = listOf("api", "spotify", "last.fm", "scrobble", "integration", "services", "discord", "rich presence"),
            icon = Icons.Default.Api,
            route = SettingsRoutes.API_MANAGEMENT,
            parentScreen = "Settings"
        ))
        
        // Updates & Info Section
        add(SearchableSettingItem(
            id = "updates",
            title = context.getString(R.string.settings_updates_title),
            description = context.getString(R.string.settings_updates_desc),
            keywords = listOf("update", "check update", "new version", "download", "changelog", "auto update"),
            icon = Icons.Default.Update,
            route = SettingsRoutes.UPDATES,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "about",
            title = context.getString(R.string.settings_about_title),
            description = context.getString(R.string.settings_about_desc),
            keywords = listOf("about", "version", "app info", "credits", "developer", "github", "license"),
            icon = Icons.Default.Info,
            route = SettingsRoutes.ABOUT,
            parentScreen = "Settings"
        ))
        
        // Advanced Section
        add(SearchableSettingItem(
            id = "crash_log_history",
            title = context.getString(R.string.settings_crash_log_history),
            description = context.getString(R.string.settings_crash_log_history_desc),
            keywords = listOf("crash", "log", "error", "bug", "debug", "report", "history"),
            icon = Icons.Default.BugReport,
            route = SettingsRoutes.CRASH_LOG_HISTORY,
            parentScreen = "Settings"
        ))
        add(SearchableSettingItem(
            id = "experimental_features",
            title = context.getString(R.string.settings_experimental_features),
            description = context.getString(R.string.settings_experimental_features_desc),
            keywords = listOf("experimental", "beta", "testing", "new features", "labs", "festive", "christmas", "decoration"),
            icon = Icons.Default.Science,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Settings"
        ))
        
        // ======================== THEME CUSTOMIZATION SCREEN ========================
        add(SearchableSettingItem(
            id = "theme_follow_system",
            title = "Follow System Theme",
            description = "Automatically switch between light and dark mode",
            keywords = listOf("system theme", "auto", "automatic", "follow system", "dark light"),
            icon = Icons.Default.Settings,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_dark_mode",
            title = "Dark Mode",
            description = "Enable dark theme manually",
            keywords = listOf("dark mode", "dark theme", "night mode", "black theme"),
            icon = Icons.Default.DarkMode,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_color_source",
            title = "Color Source",
            description = "Choose where app colors come from (Album Art, System, Custom)",
            keywords = listOf("color source", "album art colors", "monet", "material you", "dynamic colors", "custom colors"),
            icon = Icons.Default.Palette,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_color_schemes",
            title = "Color Schemes",
            description = "Browse and select predefined color palettes",
            keywords = listOf("color scheme", "palette", "preset", "default purple", "warm sunset", "cool ocean", "forest green", "rose pink"),
            icon = Icons.Default.ColorLens,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_font_source",
            title = "Font Source",
            description = "Choose between system fonts or custom fonts",
            keywords = listOf("font", "typography", "text style", "font family", "custom font"),
            icon = Icons.Default.TextFields,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        add(SearchableSettingItem(
            id = "theme_import_font",
            title = "Import Custom Font",
            description = "Import your own font file (.ttf, .otf)",
            keywords = listOf("import font", "custom font", "ttf", "otf", "font file"),
            icon = Icons.Default.FileUpload,
            route = SettingsRoutes.THEME_CUSTOMIZATION,
            parentScreen = "Theme"
        ))
        
        // ======================== PLAYER CUSTOMIZATION SCREEN ========================
        add(SearchableSettingItem(
            id = "player_chip_order",
            title = "Chip Order & Visibility",
            description = "Customize and reorder player action chips",
            keywords = listOf("chip", "button order", "action chips", "player buttons", "reorder", "visibility"),
            icon = Icons.Default.Reorder,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_show_lyrics",
            title = "Show Lyrics",
            description = "Display synchronized lyrics in player",
            keywords = listOf("lyrics", "synced lyrics", "karaoke", "text", "song words"),
            icon = Icons.Rounded.Lyrics,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "lyrics_show_translation",
            title = "Show Translation",
            description = "Display translated lyrics when available",
            keywords = listOf("lyrics", "translation", "translate", "multi-language", "subtitle"),
            icon = Icons.Default.Public,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "lyrics_show_romanization",
            title = "Show Romanization",
            description = "Display romanized lyrics when available",
            keywords = listOf("lyrics", "romanization", "romaji", "pinyin", "transliteration"),
            icon = Icons.Default.TextFields,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "keep_screen_on_lyrics",
            title = "Keep Screen Awake",
            description = "Prevent screen from turning off while lyrics are visible",
            keywords = listOf("screen", "awake", "wake", "lyrics", "screen on", "display", "timeout", "dim"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player",
            settingKey = "keepScreenOnLyrics"
        ))
        add(SearchableSettingItem(
            id = "embed_lyrics_in_file",
            title = "Embed Lyrics in File",
            description = "Write lyrics directly into audio file metadata",
            keywords = listOf("embed", "lyrics", "file", "metadata", "write", "save", "tag", "id3"),
            icon = Icons.Default.MusicNote,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "playback_pitch",
            title = "Playback Pitch",
            description = "Adjust audio pitch independently from speed",
            keywords = listOf("pitch", "tone", "semitone", "key", "audio pitch", "higher", "lower"),
            icon = Icons.Default.Equalizer,
            route = null,
            parentScreen = "Player",
            settingKey = "playbackPitch"
        ))
        add(SearchableSettingItem(
            id = "lossless_artwork",
            title = "Lossless Artwork",
            description = "Save album art as PNG instead of compressed JPEG",
            keywords = listOf("lossless", "artwork", "png", "quality", "album art", "image", "uncompressed", "high quality"),
            icon = Icons.Default.HighQuality,
            route = null,
            parentScreen = "Library Content",
            settingKey = "losslessArtwork"
        ))
        add(SearchableSettingItem(
            id = "player_gradient",
            title = "Gradient Overlay",
            description = "Show gradient overlay on album artwork",
            keywords = listOf("gradient", "overlay", "artwork gradient", "background"),
            icon = Icons.Default.Gradient,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_seek_buttons",
            title = "Seek Buttons",
            description = "Show 10-second skip forward/backward buttons",
            keywords = listOf("seek", "skip", "forward", "backward", "10 seconds", "rewind"),
            icon = Icons.Default.Forward10,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_text_alignment",
            title = "Text Alignment",
            description = "Choose text alignment (Left, Center, Right)",
            keywords = listOf("text", "alignment", "left", "center", "right", "title position"),
            icon = Icons.Default.FormatAlignCenter,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_progress_style",
            title = "Progress Style",
            description = "Choose progress bar style",
            keywords = listOf("progress bar", "seekbar", "style", "wavy", "dotted", "dashed", "glowing"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_artwork_radius",
            title = "Artwork Corner Radius",
            description = "Adjust album artwork corner roundness",
            keywords = listOf("artwork", "corner", "radius", "rounded", "square", "album art shape"),
            icon = Icons.Default.RoundedCorner,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        add(SearchableSettingItem(
            id = "player_quality_badges",
            title = "Audio Quality Badges",
            description = "Display codec and bitrate information",
            keywords = listOf("quality", "badge", "codec", "bitrate", "flac", "mp3", "audio format"),
            icon = Icons.Default.HighQuality,
            route = SettingsRoutes.PLAYER_CUSTOMIZATION,
            parentScreen = "Player"
        ))
        
        // ======================== MINIPLAYER CUSTOMIZATION SCREEN ========================
        add(SearchableSettingItem(
            id = "miniplayer_show_progress",
            title = "Show Progress",
            description = "Display progress indicator in mini player",
            keywords = listOf("miniplayer progress", "progress bar", "indicator", "mini player"),
            icon = Icons.Default.Visibility,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_circular_progress",
            title = "Circular Progress",
            description = "Use circular progress on play/pause button",
            keywords = listOf("circular", "progress", "round", "play button", "miniplayer"),
            icon = Icons.Default.ChangeCircle,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_progress_style",
            title = "Progress Style",
            description = "Choose mini player progress bar style",
            keywords = listOf("progress style", "miniplayer", "wavy", "dotted", "normal"),
            icon = Icons.Default.LinearScale,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_show_artwork",
            title = "Show Artwork",
            description = "Display album artwork in mini player",
            keywords = listOf("artwork", "album art", "cover", "image", "miniplayer"),
            icon = Icons.Default.Album,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_artwork_size",
            title = "Artwork Size",
            description = "Adjust album artwork size in mini player",
            keywords = listOf("artwork size", "image size", "cover size", "miniplayer"),
            icon = Icons.Default.PhotoSizeSelectLarge,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_corner_radius",
            title = "Corner Radius",
            description = "Adjust mini player corner roundness",
            keywords = listOf("corner", "radius", "rounded", "shape", "miniplayer"),
            icon = Icons.Default.RoundedCorner,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_show_time",
            title = "Show Time",
            description = "Display playback time in mini player",
            keywords = listOf("time", "duration", "elapsed", "remaining", "miniplayer"),
            icon = Icons.Default.Timer,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        add(SearchableSettingItem(
            id = "miniplayer_tablet_layout",
            title = "Tablet Layout on Phone",
            description = "Use tablet-style miniplayer on phones",
            keywords = listOf("tablet", "layout", "phone", "style", "miniplayer"),
            icon = Icons.Default.Tablet,
            route = SettingsRoutes.MINIPLAYER_CUSTOMIZATION,
            parentScreen = "MiniPlayer"
        ))
        
        // ======================== GESTURES SCREEN ========================
        add(SearchableSettingItem(
            id = "gesture_miniplayer_swipe",
            title = "MiniPlayer Swipe Gestures",
            description = "Swipe up/down to open/dismiss, left/right to skip tracks",
            keywords = listOf("swipe", "gesture", "miniplayer", "up", "down", "left", "right", "skip"),
            icon = Icons.Rounded.Swipe,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        add(SearchableSettingItem(
            id = "gesture_player_dismiss",
            title = "Swipe Down to Dismiss",
            description = "Close player by swiping down on the screen",
            keywords = listOf("swipe down", "dismiss", "close", "player", "gesture"),
            icon = Icons.Rounded.SwipeDown,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        add(SearchableSettingItem(
            id = "gesture_artwork_swipe",
            title = "Swipe Artwork for Tracks",
            description = "Swipe left/right on album artwork to skip tracks",
            keywords = listOf("swipe", "artwork", "album art", "skip", "next", "previous"),
            icon = Icons.Rounded.SwipeLeft,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        add(SearchableSettingItem(
            id = "gesture_double_tap",
            title = "Double Tap Artwork",
            description = "Double tap on album art to play/pause",
            keywords = listOf("double tap", "artwork", "play", "pause", "tap gesture"),
            icon = Icons.Rounded.TouchApp,
            route = SettingsRoutes.GESTURES,
            parentScreen = "Gestures"
        ))
        
        // ======================== QUEUE & PLAYBACK SCREEN ========================
        add(SearchableSettingItem(
            id = "queue_exoplayer_shuffle",
            title = "Use ExoPlayer Shuffle",
            description = "Use native ExoPlayer shuffle algorithm",
            keywords = listOf("shuffle", "exoplayer", "random", "playback", "algorithm"),
            icon = RhythmIcons.Shuffle,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_auto_add",
            title = "Auto Add to Queue",
            description = "Automatically add songs to queue",
            keywords = listOf("auto queue", "add", "automatic", "playlist"),
            icon = RhythmIcons.Queue,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_clear_on_new",
            title = "Clear Queue on New Song",
            description = "Clear the queue when playing a new song",
            keywords = listOf("clear queue", "new song", "replace", "reset"),
            icon = RhythmIcons.Delete,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_action_dialog",
            title = "Queue Action Dialog",
            description = "Ask what to do when queue is not empty",
            keywords = listOf("queue dialog", "ask", "prompt", "action", "replace queue"),
            icon = RhythmIcons.Queue,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_repeat_persistence",
            title = "Remember Repeat Mode",
            description = "Remember repeat mode between sessions",
            keywords = listOf("repeat", "remember", "save", "persistence", "loop"),
            icon = RhythmIcons.Repeat,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_shuffle_persistence",
            title = "Remember Shuffle Mode",
            description = "Remember shuffle mode between sessions",
            keywords = listOf("shuffle", "remember", "save", "persistence", "random"),
            icon = RhythmIcons.Shuffle,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_stop_on_close",
            title = "Stop Playback on App Close",
            description = "Stop music when closing the app",
            keywords = listOf("stop", "playback", "close", "exit", "quit"),
            icon = Icons.Default.Stop,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
        ))
        add(SearchableSettingItem(
            id = "sleep_timer",
            title = "Sleep Timer",
            description = "Auto-stop playback after a set time",
            keywords = listOf("sleep", "timer", "auto stop", "automatic", "fade out", "pause", "bedtime"),
            icon = Icons.Default.Timer,
            route = SettingsRoutes.SLEEP_TIMER,
            parentScreen = "Audio & Playback"
        ))
        add(SearchableSettingItem(
            id = "queue_hours_format",
            title = "Use Hours in Time Format",
            description = "Show hours in playback time display",
            keywords = listOf("hours", "time", "format", "duration", "display"),
            icon = Icons.Default.AccessTime,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback"
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
            title = "Remember Queue",
            description = "Save and restore queue when restarting app",
            keywords = listOf("queue", "remember", "save", "restore", "persistence", "restart", "app"),
            icon = RhythmIcons.Queue,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "queuePersistenceEnabled"
        ))
        add(SearchableSettingItem(
            id = "playlist_action_dialog",
            title = "Playlist Action Dialog",
            description = "Choose what happens when clicking playlist items",
            keywords = listOf("playlist", "action", "dialog", "click", "behavior", "load", "play"),
            icon = Icons.Default.QueueMusic,
            route = SettingsRoutes.QUEUE_PLAYBACK,
            parentScreen = "Queue & Playback",
            settingKey = "playlistClickBehavior"
        ))
        
        // ======================== EXPERIMENTAL FEATURES SCREEN ========================
        
        // Bit-Perfect Audio
        add(SearchableSettingItem(
            id = "bit_perfect_mode",
            title = "Bit-Perfect Mode",
            description = "Output audio at its native sample rate without resampling (e.g., 44.1kHz for CD, 96kHz for Hi-Res). Requires app restart.",
            keywords = listOf("bit perfect", "bit-perfect", "audio", "sample rate", "resampling", "hi-res", "quality", "lossless", "44.1khz", "48khz", "96khz", "192khz", "native", "dac"),
            icon = Icons.Default.HighQuality,
            route = null,
            parentScreen = "Audio & Playback",
            settingKey = "bitPerfectMode"
        ))
        
        add(SearchableSettingItem(
            id = "exp_music_mode",
            title = "Music Mode",
            description = "Choose between local files or streaming",
            keywords = listOf("music mode", "local", "streaming", "source", "files"),
            icon = Icons.Default.Storage,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_festive_theme",
            title = "Festive Theme",
            description = "Show festive decorations across the app",
            keywords = listOf("festive", "christmas", "halloween", "diwali", "holi", "new year", "decoration", "snow", "snowflake"),
            icon = Icons.Default.Celebration,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_auto_detect_holidays",
            title = "Auto-Detect Holidays",
            description = "Automatically show decorations for holidays",
            keywords = listOf("auto detect", "holiday", "automatic", "festive", "seasonal"),
            icon = Icons.Default.AutoAwesome,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_ignore_mediastore",
            title = "Ignore MediaStore Covers",
            description = "Extract album art from audio files directly",
            keywords = listOf("mediastore", "album art", "cover", "extract", "embedded"),
            icon = RhythmIcons.Album,
            route = null,
            parentScreen = "Library Content"
        ))
        add(SearchableSettingItem(
            id = "exp_codec_monitoring",
            title = "Codec Monitoring",
            description = "Log audio codec and format info for debugging",
            keywords = listOf("codec", "debug", "log", "monitoring", "audio format"),
            icon = Icons.Default.Code,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_audio_device_logging",
            title = "Audio Device Logging",
            description = "Log audio device changes (Bluetooth, headphones, etc.)",
            keywords = listOf("audio device", "bluetooth", "headphones", "log", "debug"),
            icon = Icons.Default.Headphones,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        add(SearchableSettingItem(
            id = "exp_launch_onboarding",
            title = "Launch Onboarding",
            description = "Reset and relaunch the onboarding experience",
            keywords = listOf("onboarding", "reset", "restart", "welcome", "setup", "intro"),
            icon = Icons.Default.RestartAlt,
            route = SettingsRoutes.EXPERIMENTAL_FEATURES,
            parentScreen = "Experimental"
        ))
        
        // ======================== HOME SCREEN CUSTOMIZATION ========================
        add(SearchableSettingItem(
            id = "home_section_order",
            title = "Section Order",
            description = "Reorder and customize home screen sections",
            keywords = listOf("section order", "home", "reorder", "arrange", "layout"),
            icon = Icons.Default.Reorder,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_greeting",
            title = "Show Greeting",
            description = "Display greeting message on home screen",
            keywords = listOf("greeting", "hello", "welcome", "message", "home"),
            icon = Icons.Default.Info,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_recently_played",
            title = "Recently Played",
            description = "Show recently played section",
            keywords = listOf("recently played", "history", "recent", "last played"),
            icon = Icons.Default.AccessTime,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_discover_carousel",
            title = "Discover Carousel",
            description = "Show discover/featured carousel",
            keywords = listOf("discover", "carousel", "featured", "slider", "banner"),
            icon = Icons.Default.AutoAwesome,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        add(SearchableSettingItem(
            id = "home_carousel_auto_scroll",
            title = "Carousel Auto Scroll",
            description = "Automatically scroll through discover carousel",
            keywords = listOf("auto scroll", "carousel", "automatic", "slide"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.HOME_SCREEN,
            parentScreen = "Home"
        ))
        
        // ======================== NOTIFICATIONS SCREEN ========================
        add(SearchableSettingItem(
            id = "notifications_custom",
            title = "Custom Notifications",
            description = "Use custom notification style for media controls",
            keywords = listOf("notification", "custom", "media controls", "now playing notification"),
            icon = Icons.Default.Notifications,
            route = SettingsRoutes.NOTIFICATIONS,
            parentScreen = "Notifications"
        ))
        
        // ======================== EXPRESSIVE SHAPES SCREEN ========================
        add(SearchableSettingItem(
            id = "expressive_shapes_enabled",
            title = "Expressive Shapes",
            description = "Enable custom shapes for UI elements",
            keywords = listOf("shapes", "expressive", "custom", "ui", "design", "artwork", "corners"),
            icon = Icons.Default.Palette,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Settings",
            settingKey = "expressiveShapesEnabled"
        ))
        add(SearchableSettingItem(
            id = "shape_preset",
            title = "Shape Preset",
            description = "Choose from predefined shape collections",
            keywords = listOf("preset", "shapes", "collection", "playful", "organic", "geometric", "retro", "custom"),
            icon = Icons.Default.ColorLens,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePreset"
        ))
        add(SearchableSettingItem(
            id = "shape_album_art",
            title = "Album Artwork Shape",
            description = "Shape for album artwork displays",
            keywords = listOf("album", "artwork", "shape", "cover", "image", "display"),
            icon = Icons.Default.Album,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapeAlbumArt"
        ))
        add(SearchableSettingItem(
            id = "shape_player_art",
            title = "Player Artwork Shape",
            description = "Shape for player screen artwork",
            keywords = listOf("player", "artwork", "shape", "screen", "display", "now playing"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePlayerArt"
        ))
        add(SearchableSettingItem(
            id = "shape_song_art",
            title = "Song Artwork Shape",
            description = "Shape for song artwork in lists",
            keywords = listOf("song", "artwork", "shape", "list", "thumbnail", "image"),
            icon = Icons.Default.MusicNote,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapeSongArt"
        ))
        add(SearchableSettingItem(
            id = "shape_playlist_art",
            title = "Playlist Artwork Shape",
            description = "Shape for playlist covers",
            keywords = listOf("playlist", "artwork", "shape", "cover", "collection"),
            icon = Icons.Default.PlaylistAddCheckCircle,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePlaylistArt"
        ))
        add(SearchableSettingItem(
            id = "shape_artist_art",
            title = "Artist Artwork Shape",
            description = "Shape for artist images",
            keywords = listOf("artist", "artwork", "shape", "image", "profile", "photo"),
            icon = Icons.Default.Person,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapeArtistArt"
        ))
        add(SearchableSettingItem(
            id = "shape_player_controls",
            title = "Player Controls Shape",
            description = "Shape for player control buttons",
            keywords = listOf("player", "controls", "shape", "buttons", "play", "pause", "skip"),
            icon = Icons.Default.PlayCircleFilled,
            route = SettingsRoutes.EXPRESSIVE_SHAPES,
            parentScreen = "Shapes",
            settingKey = "expressiveShapePlayerControls"
        ))
        add(SearchableSettingItem(
            id = "shape_mini_player",
            title = "Mini Player Shape",
            description = "Shape for mini player artwork",
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
                item(key = "header_$screenName") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = screenName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }
                
                item(key = "card_$screenName") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            items.forEachIndexed { index, item ->
                                SearchResultRow(
                                    item = item,
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        onResultClick(item)
                                    }
                                )
                                if (index < items.lastIndex) {
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
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SearchResultRow(
    item: SearchableSettingItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                }
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            tonalElevation = 0.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            if (item.route != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "in ${item.parentScreen}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
