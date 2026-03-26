package chromahub.rhythm.app.shared.presentation.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Queue
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Info // Added Info icon
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.filled.PlaylistPlay // New import for PlaylistPlay filled icon
import androidx.compose.material.icons.automirrored.rounded.Chat // For Telegram support group
import androidx.compose.material.icons.rounded.PushPin // Added PushPin import
import androidx.compose.material.icons.outlined.PushPin // Added PushPin outlined import
import androidx.compose.material.icons.rounded.KeyboardArrowUp // Added KeyboardArrowUp import

/**
 * Material Design 3 icons for the Rhythm app
 */
object RhythmIcons {
    /**
     * Player controls
     */
    object Player {
        // Standard player controls
        val Play = Icons.Rounded.PlayArrow
        val Pause = Icons.Rounded.Pause
        val SkipNext = Icons.Rounded.SkipNext
        val SkipPrevious = Icons.Rounded.SkipPrevious
        val Replay10 = Icons.Rounded.Replay10 // Assuming this icon exists or will be created
        val Forward10 = Icons.Rounded.Forward10 // Assuming this icon exists or will be created
        val Shuffle = Icons.Rounded.Shuffle
        val Repeat = Icons.Rounded.Repeat
        val RepeatOne = Icons.Rounded.RepeatOne
        val Lyrics = Icons.Rounded.Lyrics // Added Lyrics icon
        val Stop = Icons.Filled.Stop
        
        // Volume controls
        val VolumeUp = Icons.AutoMirrored.Rounded.VolumeUp
        val VolumeDown = Icons.AutoMirrored.Rounded.VolumeDown
        val VolumeMute = Icons.AutoMirrored.Rounded.VolumeMute
        val VolumeOff = Icons.AutoMirrored.Rounded.VolumeOff
        
        // Additional player controls
        val Queue = Icons.AutoMirrored.Rounded.QueueMusic
        val Equalizer = Icons.Rounded.Equalizer
        val Speed = Icons.Rounded.Equalizer
        val Timer = Icons.Rounded.Timer
    }
    
    /**
     * Navigation icons
     */
    object Navigation {
        // Bottom navigation
        val Home = Icons.Rounded.Home
        val HomeOutlined = Icons.Outlined.Home
        val Search = Icons.Rounded.Search
        val SearchOutlined = Icons.Outlined.Search
        val Library = Icons.Rounded.LibraryMusic
        val LibraryOutlined = Icons.Outlined.LibraryMusic
        val Settings = Icons.Rounded.Settings
        val SettingsOutlined = Icons.Outlined.Settings
        
        // Navigation actions
        val Back = Icons.AutoMirrored.Rounded.ArrowBack
        val Forward = Icons.AutoMirrored.Rounded.ArrowForward
        val Close = Icons.Rounded.Close
    }
    
    /**
     * Music item icons
     */
    object Music {
        // Music entities
        val Song = Icons.Rounded.MusicNote
        val SongOutlined = Icons.Outlined.MusicNote
        val Album = Icons.Rounded.Album
        val AlbumOutlined = Icons.Outlined.Album
        val Artist = Icons.Rounded.Person
        val ArtistOutlined = Icons.Outlined.Person
        val Playlist = Icons.AutoMirrored.Rounded.PlaylistPlay
        val PlaylistOutlined = Icons.AutoMirrored.Outlined.PlaylistAdd
        val MusicNote = Icons.Rounded.MusicNote
        val Audiotrack = Icons.Rounded.Audiotrack
    }
    
    /**
     * Action icons
     */
    object Actions {
        // Common actions
        val Favorite = Icons.Rounded.Favorite
        val FavoriteOutlined = Icons.Rounded.FavoriteBorder
        val Add = Icons.Rounded.Add
        val Remove = Icons.Rounded.Remove
        val Edit = Icons.Rounded.Edit
        val Delete = Icons.Rounded.Delete
        val Check = Icons.Rounded.Check
        val More = Icons.Rounded.MoreVert
        val List = Icons.AutoMirrored.Rounded.List
        val Apps = Icons.Rounded.Apps
        val Refresh = Icons.Rounded.Refresh
        val Download = Icons.Rounded.Download
        val Tune = Icons.Rounded.Tune
        val Sort = Icons.AutoMirrored.Rounded.Sort
        val Update = Icons.Rounded.SystemUpdate // Added Update icon
        val Info = Icons.Rounded.Info // Added Info icon
        val Pushpin = Icons.Rounded.PushPin // Added PushPin icon
        val PinOutline = Icons.Outlined.PushPin // Added PushPin outline icon
        val ArrowUpward = Icons.Rounded.KeyboardArrowUp // Added ArrowUpward icon
    }
    
    /**
     * Device icons
     */
    object Devices {
        // Audio output devices
        val Bluetooth = Icons.Rounded.Bluetooth
        val BluetoothOutlined = Icons.Outlined.Bluetooth
        val Headphones = Icons.Rounded.Headphones
        val HeadphonesOutlined = Icons.Outlined.Headphones
        val Speaker = Icons.Rounded.Speaker
        val SpeakerOutlined = Icons.Outlined.Speaker
        
        // Cast devices
        val Cast = Icons.Rounded.Cast
        val CastConnected = Icons.Rounded.CastConnected
        
        // Location
        val Location = Icons.Rounded.Place
        val LocationOutlined = Icons.Outlined.Place
    }
    
    /**
     * Legacy icons (for backward compatibility)
     * These will be gradually phased out in favor of the categorized icons above
     */
    // Player controls
    val Play = Player.Play
    val Pause = Player.Pause
    val SkipNext = Player.SkipNext
    val SkipPrevious = Player.SkipPrevious
    val Replay10 = Player.Replay10
    val Forward10 = Player.Forward10
    val Shuffle = Player.Shuffle
    val Repeat = Player.Repeat
    val RepeatOne = Player.RepeatOne
    val VolumeUp = Player.VolumeUp
    val VolumeDown = Player.VolumeDown
    val VolumeMute = Player.VolumeMute
    val VolumeOff = Player.VolumeOff
    
    // Navigation
    val Home = Navigation.HomeOutlined
    val HomeFilled = Navigation.Home
    val Search = Navigation.SearchOutlined
    val SearchFilled = Navigation.Search
    val Library = Navigation.LibraryOutlined
    val Settings = Navigation.SettingsOutlined
    val SettingsFilled = Navigation.Settings
    
    // Mood & moments icons (using appropriate Material icons as substitutes)
    val Energy = Icons.Rounded.Equalizer
    val Relax = Icons.Rounded.Headphones
    val Focus = Icons.Rounded.Timer
    
    // System icons
    val Notifications = Icons.Rounded.Refresh
    
    // Music items
    val Song = Music.SongOutlined
    val SongFilled = Music.Song
    val Album = Music.AlbumOutlined
    val Artist = Music.ArtistOutlined
    val ArtistFilled = Music.Artist
    val Playlist = Music.PlaylistOutlined
    val PlaylistFilled = Music.Playlist // Added PlaylistFilled

    // Actions
    val Favorite = Actions.FavoriteOutlined
    val FavoriteFilled = Actions.Favorite
    val Add = Actions.Add
    val Remove = Actions.Remove
    val Edit = Actions.Edit
    val Delete = Actions.Delete
    val AddToPlaylist = Icons.AutoMirrored.Rounded.PlaylistAdd
    val AddToQueue = Icons.AutoMirrored.Rounded.PlaylistAdd
    val More = Actions.More
    val Queue = Player.Queue
    val MusicNote = Music.MusicNote
    val Check = Actions.Check
    val Close = Navigation.Close
    val Back = Navigation.Back
    val Forward = Navigation.Forward
    val ArrowRight = Navigation.Forward // Alias for consistency
    val Download = Actions.Download
    val List = Actions.List
    val AppsGrid = Actions.Apps
    val Refresh = Actions.Refresh
    val Telegram = Icons.AutoMirrored.Rounded.Chat // Added Telegram icon
    val Equalizer = Player.Equalizer // Equalizer icon for audio effects
    val Pushpin = Actions.Pushpin // Added Pushpin icon
    val PinOutline = Actions.PinOutline // Added PinOutline icon
    val ArrowUpward = Actions.ArrowUpward // Added ArrowUpward icon
    val Tune = Actions.Tune // Added Tune icon for crossfade settings
    
    // Location
    val Location = Devices.LocationOutlined
    val LocationFilled = Devices.Location
    
    // Audio devices
    val Bluetooth = Devices.BluetoothOutlined
    val BluetoothFilled = Devices.Bluetooth
    val Headphones = Devices.HeadphonesOutlined
    val HeadphonesFilled = Devices.Headphones
    val Speaker = Devices.SpeakerOutlined
    val SpeakerFilled = Devices.Speaker
    val Cast = Devices.Cast
    val CastConnected = Devices.CastConnected
}
