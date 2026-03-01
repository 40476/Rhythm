package chromahub.rhythm.app.infrastructure.service

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import androidx.media3.session.MediaNotification
import androidx.media3.session.DefaultMediaNotificationProvider
import chromahub.rhythm.app.activities.MainActivity
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.infrastructure.service.player.RhythmPlayerEngine
import chromahub.rhythm.app.infrastructure.service.player.TransitionController
import chromahub.rhythm.app.infrastructure.widget.WidgetUpdater
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes as ExoAudioAttributes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import chromahub.rhythm.app.shared.data.model.Playlist

@OptIn(UnstableApi::class)
class MediaPlaybackService : MediaLibraryService(), Player.Listener {
    private var mediaSession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer
    private lateinit var customCommands: List<CommandButton>

    private var controller: MediaController? = null
    
    // Service-scoped coroutine scope for background operations
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Track current custom layout state to avoid unnecessary updates
    private var lastShuffleState: Boolean? = null
    private var lastRepeatMode: Int? = null
    private var lastFavoriteState: Boolean? = null
    
    // Debounce custom layout updates to prevent flickering
    private var updateLayoutJob: Job? = null
    
    // Rhythm player engine (dual-player crossfade) and transition controller
    private lateinit var rhythmPlayerEngine: RhythmPlayerEngine
    private lateinit var transitionController: TransitionController
    
    // Sleep Timer functionality
    private var sleepTimerJob: Job? = null
    private var sleepTimerDurationMs: Long = 0L
    private var sleepTimerStartTime: Long = 0L
    private var fadeOutEnabled: Boolean = true
    private var pauseOnlyEnabled: Boolean = false
    
    // Audio effects (for equalizer integration)
    private var equalizer: android.media.audiofx.Equalizer? = null
    
    // Rhythm audio processors (replaced Android BassBoost and Spatializer for better quality)
    private var rhythmBassBoostProcessor: chromahub.rhythm.app.infrastructure.audio.RhythmBassBoostProcessor? = null
    private var rhythmSpatializationProcessor: chromahub.rhythm.app.infrastructure.audio.RhythmSpatializationProcessor? = null
    
    private var virtualizerStrength: Short = 0 // Store strength for virtualizer
    private var isInitializingAudioEffects: Boolean = false // Prevent concurrent initialization
    private var audioEffectsInitialized: Boolean = false // Track if effects have been successfully initialized
    private var isBassBoostAvailable: Boolean = true // Rhythm bass boost is always available
    
    // Player listener reference for proper cleanup
    private var playerListener: Player.Listener? = null
    
    // BroadcastReceiver to listen for favorite changes from ViewModel
    private val favoriteChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "chromahub.rhythm.app.action.FAVORITE_CHANGED" -> {
                    Log.d(TAG, "Received favorite change notification from ViewModel")
                    // Update notification custom layout
                    scheduleCustomLayoutUpdate(250) // Longer delay for external changes
                    // Also update widget
                    updateWidgetFromMediaItem(player.currentMediaItem)
                }
            }
        }
    }

    private val repeatCommand: CommandButton
        get() = when (val mode = controller?.repeatMode ?: Player.REPEAT_MODE_OFF) {
            Player.REPEAT_MODE_OFF -> customCommands[2]
            Player.REPEAT_MODE_ALL -> customCommands[3]
            Player.REPEAT_MODE_ONE -> customCommands[4]
            else -> customCommands[2] // Fallback to REPEAT_MODE_OFF command
        }

    private val shuffleCommand: CommandButton
        get() = if (controller?.shuffleModeEnabled == true) {
            customCommands[1]
        } else {
            customCommands[0]
        }

    private fun getCurrentFavoriteCommand(): CommandButton {
        return if (isCurrentSongFavorite()) {
            customCommands[6] // Remove from favorites (filled heart)
        } else {
            customCommands[5] // Add to favorites (heart outline)
        }
    }

    // Track external files that have been played
    private val externalUriCache = ConcurrentHashMap<String, MediaItem>()

    // Settings manager
    private lateinit var appSettings: AppSettings
    
    // Scrobbler manager for Last.fm / Pano Scrobbler integration
    private lateinit var scrobblerManager: chromahub.rhythm.app.utils.ScrobblerManager
    
    // Discord Rich Presence manager
    private lateinit var discordRichPresenceManager: chromahub.rhythm.app.utils.DiscordRichPresenceManager
    
    // Status broadcaster for Tasker, KWGT, and other automation apps
    private lateinit var statusBroadcaster: chromahub.rhythm.app.utils.StatusBroadcaster
    
    // Custom notification provider for app-specific notifications
    private var customNotificationProvider: DefaultMediaNotificationProvider? = null
    
    // SharedPreferences keys
    companion object {
        private const val TAG = "MediaPlaybackService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "RhythmMediaPlayback"

        private const val PREF_NAME = "rhythm_preferences"
        private const val PREF_HIGH_QUALITY_AUDIO = "high_quality_audio"
        private const val PREF_GAPLESS_PLAYBACK = "gapless_playback"
        private const val PREF_CROSSFADE = "crossfade"
        private const val PREF_CROSSFADE_DURATION = "crossfade_duration"
        private const val PREF_AUDIO_NORMALIZATION = "audio_normalization"
        private const val PREF_REPLAY_GAIN = "replay_gain"
        
        // Intent action for updating settings
        const val ACTION_UPDATE_SETTINGS = "chromahub.rhythm.app.action.UPDATE_SETTINGS"
        
        // Intent action for playing external files
        const val ACTION_PLAY_EXTERNAL_FILE = "chromahub.rhythm.app.action.PLAY_EXTERNAL_FILE"
        
        // Intent action for initializing the service
        const val ACTION_INIT_SERVICE = "chromahub.rhythm.app.action.INIT_SERVICE"
        
        // Intent actions for sleep timer
        const val ACTION_START_SLEEP_TIMER = "chromahub.rhythm.app.action.START_SLEEP_TIMER"
        const val ACTION_STOP_SLEEP_TIMER = "chromahub.rhythm.app.action.STOP_SLEEP_TIMER"
        
        // Intent actions for equalizer
        const val ACTION_SET_EQUALIZER_ENABLED = "chromahub.rhythm.app.action.SET_EQUALIZER_ENABLED"
        const val ACTION_SET_EQUALIZER_BAND = "chromahub.rhythm.app.action.SET_EQUALIZER_BAND"
        const val ACTION_SET_BASS_BOOST = "chromahub.rhythm.app.action.SET_BASS_BOOST"
        const val ACTION_SET_VIRTUALIZER = "chromahub.rhythm.app.action.SET_VIRTUALIZER"
        const val ACTION_APPLY_EQUALIZER_PRESET = "chromahub.rhythm.app.action.APPLY_EQUALIZER_PRESET"
        const val ACTION_GET_EQUALIZER_DIAGNOSTICS = "chromahub.rhythm.app.action.GET_EQUALIZER_DIAGNOSTICS"
        
        // Widget control actions
        const val ACTION_PLAY_PAUSE = "chromahub.rhythm.app.action.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = "chromahub.rhythm.app.action.SKIP_NEXT"
        const val ACTION_SKIP_PREVIOUS = "chromahub.rhythm.app.action.SKIP_PREVIOUS"
        const val ACTION_TOGGLE_FAVORITE = "chromahub.rhythm.app.action.TOGGLE_FAVORITE"
        
        // Broadcast actions for status updates
        const val BROADCAST_SLEEP_TIMER_STATUS = "chromahub.rhythm.app.broadcast.SLEEP_TIMER_STATUS"
        const val EXTRA_TIMER_ACTIVE = "timer_active"
        const val EXTRA_REMAINING_TIME = "remaining_time"
        
        // Audio session ID
        const val ACTION_GET_AUDIO_SESSION_ID = "chromahub.rhythm.app.action.GET_AUDIO_SESSION_ID"
        const val BROADCAST_AUDIO_SESSION_ID = "chromahub.rhythm.app.broadcast.AUDIO_SESSION_ID"
        const val EXTRA_AUDIO_SESSION_ID = "audio_session_id"
        
        // Mute/Unmute actions (Media3 1.9.0 feature)
        const val ACTION_MUTE = "chromahub.rhythm.app.action.MUTE"
        const val ACTION_UNMUTE = "chromahub.rhythm.app.action.UNMUTE"
        const val ACTION_TOGGLE_MUTE = "chromahub.rhythm.app.action.TOGGLE_MUTE"

        // Playback custom commands
        const val REPEAT_MODE_ALL = "repeat_all"
        const val REPEAT_MODE_ONE = "repeat_one"
        const val REPEAT_MODE_OFF = "repeat_off"
        const val SHUFFLE_MODE_ON = "shuffle_on"
        const val SHUFFLE_MODE_OFF = "shuffle_off"
        const val FAVORITE_ON = "favorite_on"
        const val FAVORITE_OFF = "favorite_off"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // Create notification channel first (required for Android 8.0+)
        createNotificationChannel()

        // Start foreground immediately to avoid ANR
        startForegroundWithNotification("Rhythm Music", "Starting service...")

        // Initialize settings manager (fast operation)
        updateForegroundNotification("Rhythm Music", "Loading settings...")
        appSettings = AppSettings.getInstance(applicationContext)
        
        // Initialize Rhythm audio processors early (before player creation)
        try {
            rhythmBassBoostProcessor = chromahub.rhythm.app.infrastructure.audio.RhythmBassBoostProcessor()
            rhythmSpatializationProcessor = chromahub.rhythm.app.infrastructure.audio.RhythmSpatializationProcessor()
            isBassBoostAvailable = true
            appSettings.setBassBoostAvailable(true)
            Log.d(TAG, "Rhythm audio processors initialized early")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Rhythm processors", e)
            rhythmBassBoostProcessor = null
            rhythmSpatializationProcessor = null
            isBassBoostAvailable = false
            appSettings.setBassBoostAvailable(false)
        }
        
        // Initialize scrobbler manager
        scrobblerManager = chromahub.rhythm.app.utils.ScrobblerManager(applicationContext)
        
        // Initialize Discord Rich Presence manager
        discordRichPresenceManager = chromahub.rhythm.app.utils.DiscordRichPresenceManager(applicationContext)
        
        // Initialize status broadcaster for Tasker/KWGT
        statusBroadcaster = chromahub.rhythm.app.utils.StatusBroadcaster(applicationContext)

        // Register BroadcastReceiver for favorite changes
        updateForegroundNotification("Rhythm Music", "Setting up components...")
        val filter = IntentFilter("chromahub.rhythm.app.action.FAVORITE_CHANGED")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(favoriteChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(favoriteChangeReceiver, filter)
        }

        try {
            // Initialize core components on main thread (required for media service)
            updateForegroundNotification("Rhythm Music", "Initializing player...")
            initializePlayer()

            updateForegroundNotification("Rhythm Music", "Creating playback controls...")
            createCustomCommands()

            // Create the media session (required synchronously)
            updateForegroundNotification("Rhythm Music", "Setting up media session...")
            mediaSession = createMediaSession()

            // Initialize controller asynchronously to avoid blocking
            updateForegroundNotification("Rhythm Music", "Initializing media controller...")
            createController()

            // Observe notification preference changes
            updateForegroundNotification("Rhythm Music", "Service ready")
            observeNotificationSettings()

            Log.d(TAG, "Service initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing service", e)
            updateForegroundNotification("Rhythm Music", "Initialization failed")
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rhythm Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback controls"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun startForegroundWithNotification(title: String = "Rhythm Music", content: String = "Initializing music service...") {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(chromahub.rhythm.app.R.drawable.ic_notification)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        // Call the system's startForeground() method
        super.startForeground(NOTIFICATION_ID, notification)
        Log.d(TAG, "Started foreground service: $title - $content")
    }

    private fun updateForegroundNotification(title: String, content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(chromahub.rhythm.app.R.drawable.ic_notification)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        Log.d(TAG, "Updated foreground notification: $title - $content")
    }
    
    private fun observeNotificationSettings() {
        serviceScope.launch {
            appSettings.useCustomNotification.collect { useCustomNotification ->
                Log.d(TAG, "Notification preference changed: $useCustomNotification")
                updateNotificationProvider(useCustomNotification)
            }
        }
    }
    
    private fun updateNotificationProvider(useCustomNotification: Boolean) {
        try {
            if (useCustomNotification && customNotificationProvider == null) {
                // Switch to custom notification provider
                customNotificationProvider = DefaultMediaNotificationProvider.Builder(this)
                    .setChannelId(CHANNEL_ID)
                    .setNotificationId(NOTIFICATION_ID)
                    .build()
                Log.d(TAG, "Switched to custom notification provider")
            } else if (!useCustomNotification && customNotificationProvider != null) {
                // Switch back to system media notifications
                customNotificationProvider = null
                Log.d(TAG, "Switched to system media notifications")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating notification provider", e)
        }
    }
    
    private fun initializePlayer() {
        // Initialize RhythmPlayerEngine for crossfade support
        // Pass bit-perfect mode setting to enable native sample rate output
        val bitPerfectEnabled = appSettings.bitPerfectMode.value
        Log.d(TAG, "Initializing player with bit-perfect mode: $bitPerfectEnabled")
        rhythmPlayerEngine = RhythmPlayerEngine(
            this, 
            bitPerfectMode = bitPerfectEnabled,
            bassBoostProcessor = rhythmBassBoostProcessor,
            spatializationProcessor = rhythmSpatializationProcessor
        )
        rhythmPlayerEngine.initialize()
        
        // The master player is exposed to MediaSession and used everywhere
        player = rhythmPlayerEngine.masterPlayer as ExoPlayer
        
        // Register player swap listener for crossfade transitions
        rhythmPlayerEngine.addPlayerSwapListener { newPlayer ->
            Log.d(TAG, "Player swapped during crossfade transition")
            val oldPlayer = player
            player = newPlayer as ExoPlayer
            
            // Move the service-level player listener to the new player
            playerListener?.let { listener ->
                oldPlayer.removeListener(listener)
                newPlayer.addListener(listener)
            }
            
            // Update the MediaSession to use the new player
            mediaSession?.player = newPlayer
            
            // Force custom layout update for the new player
            scheduleCustomLayoutUpdate(50)
            
            // Update widget with current song info
            updateWidgetFromMediaItem(newPlayer.currentMediaItem)
            
            // Reinitialize audio effects with new session ID
            if (newPlayer.audioSessionId != 0) {
                initializeAudioEffects()
            }
        }
            
        // Add listener to initialize audio effects when session ID is ready and handle errors
        // Store reference for proper cleanup in onDestroy
        playerListener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && player.audioSessionId != 0) {
                    // Reinitialize audio effects with valid session ID
                    val previouslyEnabled = equalizer?.enabled ?: false
                    Log.d(TAG, "Player ready with session ID ${player.audioSessionId}, reinitializing effects (EQ was: $previouslyEnabled)")
                    initializeAudioEffects()
                    
                    // Force reload audio effects settings to fix cold boot issue
                    // This ensures bass boost and spatial audio are properly applied on first playback
                    // Increased delay to ensure player is fully ready and processors are connected
                    serviceScope.launch {
                        delay(200) // Increased delay to ensure audio pipeline is fully initialized
                        Log.d(TAG, "Force-reloading audio effects settings after player ready")
                        loadSavedAudioEffects()
                        
                        // Additional verification: Re-apply Rhythm processor settings after another small delay
                        // This fixes the issue where processors don't receive settings on cold boot
                        delay(100)
                        Log.d(TAG, "Re-applying Rhythm processor settings for cold boot fix")
                        
                        // Re-apply bass boost if enabled
                        if (appSettings.bassBoostEnabled.value && rhythmBassBoostProcessor != null) {
                            rhythmBassBoostProcessor?.setEnabled(true)
                            rhythmBassBoostProcessor?.setStrength(appSettings.bassBoostStrength.value.toShort())
                            Log.d(TAG, "Cold boot: Re-applied bass boost - enabled=true, strength=${appSettings.bassBoostStrength.value}")
                        }
                        
                        // Re-apply spatial audio if enabled
                        if (appSettings.virtualizerEnabled.value && rhythmSpatializationProcessor != null) {
                            rhythmSpatializationProcessor?.setEnabled(true)
                            rhythmSpatializationProcessor?.setStrength(appSettings.virtualizerStrength.value.toShort())
                            Log.d(TAG, "Cold boot: Re-applied spatial audio - enabled=true, strength=${appSettings.virtualizerStrength.value}")
                        }
                    }
                    
                    // Verify state was preserved
                    val currentlyEnabled = equalizer?.enabled ?: false
                    if (previouslyEnabled != currentlyEnabled && appSettings.equalizerEnabled.value)
                    {
                        Log.w(TAG, "Equalizer state changed after reinitialization! Was: $previouslyEnabled, Now: $currentlyEnabled, Expected: ${appSettings.equalizerEnabled.value}") // Force re-apply settings
                       setEqualizerEnabled(appSettings.equalizerEnabled.value)
                    }
                }
            }
            
            override fun onPlayerError(error: PlaybackException) {
                handlePlaybackError(error)
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // Send scrobble broadcast when play/pause state changes
                if (appSettings.scrobblingEnabled.value) {
                    val position = player.currentPosition
                    if (isPlaying) {
                        scrobblerManager.scrobbleResumed(position)
                    } else {
                        scrobblerManager.scrobblePaused(position)
                    }
                }
                
                // Update Discord Rich Presence when play/pause state changes
                if (appSettings.discordRichPresenceEnabled.value) {
                    val currentMediaItem = player.currentMediaItem
                    if (currentMediaItem != null) {
                        val song = convertMediaItemToSong(currentMediaItem)
                        if (song != null) {
                            if (isPlaying) {
                                discordRichPresenceManager.updateNowPlaying(song, true, player.currentPosition)
                            } else {
                                discordRichPresenceManager.updatePaused(song)
                            }
                        }
                    } else if (!isPlaying) {
                        discordRichPresenceManager.clearPresence()
                    }
                }
                
                // Broadcast status for Tasker/KWGT/automation apps
                if (appSettings.broadcastStatusEnabled.value) {
                    statusBroadcaster.broadcastPlaystateChanged(isPlaying, player.currentPosition)
                }
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // Send scrobble broadcast when track changes
                if (appSettings.scrobblingEnabled.value && mediaItem != null) {
                    try {
                        val song = convertMediaItemToSong(mediaItem)
                        if (song != null) {
                            scrobblerManager.scrobbleNowPlaying(song, player.currentPosition)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error scrobbling track change", e)
                    }
                }
                
                // Update Discord Rich Presence when track changes
                if (appSettings.discordRichPresenceEnabled.value && mediaItem != null) {
                    try {
                        val song = convertMediaItemToSong(mediaItem)
                        if (song != null) {
                            discordRichPresenceManager.resetStartTime()
                            discordRichPresenceManager.updateNowPlaying(song, player.isPlaying, player.currentPosition)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating Discord presence on track change", e)
                    }
                }
                
                // Broadcast status for Tasker/KWGT/automation apps
                if (appSettings.broadcastStatusEnabled.value && mediaItem != null) {
                    try {
                        val song = convertMediaItemToSong(mediaItem)
                        if (song != null) {
                            statusBroadcaster.broadcastNowPlaying(
                                song,
                                player.isPlaying,
                                player.currentPosition,
                                player.mediaItemCount,
                                player.currentMediaItemIndex
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error broadcasting status on track change", e)
                    }
                }
                
                // Update widget when media item changes
                serviceScope.launch {
                    updateWidgetFromMediaItem(mediaItem)
                }
            }
            
            // NEW in Media3 1.9.0: Monitor audio capabilities changes
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                Log.d(TAG, "Audio session ID changed: $audioSessionId")
                // Reinitialize audio effects with new session
                if (audioSessionId != 0) {
                    initializeAudioEffects()
                }
            }
        }
        playerListener?.let { player.addListener(it) }
        
        // Initialize transition controller for crossfade scheduling
        transitionController = TransitionController(rhythmPlayerEngine, appSettings)
        transitionController.initialize()
        
        // Apply current settings
        applyPlayerSettings()
        
        // Try to initialize audio effects (might fail if session ID not ready)
        initializeAudioEffects()
    }
    
    private fun handlePlaybackError(error: PlaybackException) {
        val message = when (error.errorCode) {
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ->
                "Audio codec not supported on this device"
            PlaybackException.ERROR_CODE_IO_UNSPECIFIED ->
                "Cannot read audio file"
            PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED ->
                "Audio format not supported"
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                "Audio file not found"
            PlaybackException.ERROR_CODE_IO_NO_PERMISSION ->
                "Permission denied to access audio file"
            else -> "Playback error: ${error.message}"
        }
        Log.e(TAG, "Playback error: $message", error)
        
        // Gracefully recover from playback errors by skipping to the next track
        // This prevents codec/format errors from stopping playback entirely
        try {
            if (player.hasNextMediaItem()) {
                Log.w(TAG, "Recovering from playback error - skipping to next track")
                player.seekToNextMediaItem()
                player.prepare()
            } else {
                Log.w(TAG, "No next track available for error recovery")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to recover from playback error", e)
        }
    }

    private fun createController() {
        // Build the controller asynchronously to avoid blocking the main thread
        val controllerFuture = MediaController.Builder(this, mediaSession!!.token)
            .buildAsync()
        
        controllerFuture.addListener({
            try {
                controller = controllerFuture.get()
                controller?.addListener(this)
                // Only set custom layout if controller is properly initialized
                controller?.let {
                    forceCustomLayoutUpdate() // Use force update for initial setup
                }
                Log.d(TAG, "MediaController initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing MediaController", e)
            }
        }, androidx.core.content.ContextCompat.getMainExecutor(this))
    }

    private fun createCustomCommands() {
        customCommands = listOf(
            CommandButton.Builder(CommandButton.ICON_SHUFFLE_OFF)
                .setDisplayName("Shuffle mode")
                .setSessionCommand(
                    SessionCommand(SHUFFLE_MODE_ON, Bundle.EMPTY)
                )
                .build(),
            CommandButton.Builder(CommandButton.ICON_SHUFFLE_ON)
                .setDisplayName("Shuffle mode")
                .setSessionCommand(
                    SessionCommand(SHUFFLE_MODE_OFF, Bundle.EMPTY)
                )
                .build(),
            CommandButton.Builder(CommandButton.ICON_REPEAT_OFF)
                .setDisplayName("Repeat mode")
                .setSessionCommand(
                    SessionCommand(REPEAT_MODE_ALL, Bundle.EMPTY)
                )
                .build(),
            CommandButton.Builder(CommandButton.ICON_REPEAT_ALL)
                .setDisplayName("Repeat mode")
                .setSessionCommand(
                    SessionCommand(REPEAT_MODE_ONE, Bundle.EMPTY)
                )
                .build(),
            CommandButton.Builder(CommandButton.ICON_REPEAT_ONE)
                .setDisplayName("Repeat mode")
                .setSessionCommand(
                    SessionCommand(REPEAT_MODE_OFF, Bundle.EMPTY)
                )
                .build(),
            // Favorite commands - use custom icons via extras bundle
            createCustomIconButton(
                "Add to favorites",
                FAVORITE_ON,
                chromahub.rhythm.app.R.drawable.ic_favorite_border
            ),
            createCustomIconButton(
                "Remove from favorites",
                FAVORITE_OFF,
                chromahub.rhythm.app.R.drawable.ic_favorite_filled
            )
        )
    }

    private fun createCustomIconButton(displayName: String, commandAction: String, iconResId: Int): CommandButton {
        val extras = Bundle().apply {
            putInt("iconResId", iconResId)
        }
        return CommandButton.Builder(CommandButton.ICON_UNDEFINED)
            .setDisplayName(displayName)
            .setSessionCommand(SessionCommand(commandAction, extras))
            .setExtras(extras)
            .build()
    }

    private fun createMediaSession(): MediaLibrarySession {
        // PendingIntent that launches MainActivity when user taps media controls
        val sessionIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val sessionBuilder = MediaLibrarySession.Builder(
            this,
            player,
            MediaSessionCallback()
        ).setSessionActivity(pendingIntent)
        
        // Configure notification provider based on user setting
        if (appSettings.useCustomNotification.value) {
            // Use custom notification provider for app-specific styling
            customNotificationProvider = DefaultMediaNotificationProvider.Builder(this)
                .setChannelId(CHANNEL_ID)
                .setNotificationId(NOTIFICATION_ID)
                .build()
            // Note: MediaLibrarySession doesn't directly support setMediaNotificationProvider
            // The custom notification provider will be handled through MediaNotificationManager
        }
        // If custom notifications are disabled, Media3 uses system media notifications
        
        return sessionBuilder.build()
    }
    
    private fun isCurrentSongFavorite(): Boolean {
        val currentMediaItem = player.currentMediaItem
        return if (currentMediaItem != null) {
            // Get favorite songs from settings
            val favoriteSongsJson = appSettings.favoriteSongs.value
            if (favoriteSongsJson != null && favoriteSongsJson.isNotEmpty()) {
                try {
                    val type = object : TypeToken<Set<String>>() {}.type
                    val favoriteSongs: Set<String> = Gson().fromJson(favoriteSongsJson, type)
                    favoriteSongs.contains(currentMediaItem.mediaId)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing favorite songs", e)
                    false
                }
            } else {
                false
            }
        } else {
            false
        }
    }
    
    private fun toggleCurrentSongFavorite() {
        val currentMediaItem = player.currentMediaItem
        if (currentMediaItem != null) {
            serviceScope.launch {
                try {
                    // Get current favorites
                    val favoriteSongsJson = appSettings.favoriteSongs.value
                    val currentFavorites = if (favoriteSongsJson != null && favoriteSongsJson.isNotEmpty()) {
                        try {
                            val type = object : TypeToken<Set<String>>() {}.type
                            Gson().fromJson<Set<String>>(favoriteSongsJson, type).toMutableSet()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing favorite songs", e)
                            mutableSetOf<String>()
                        }
                    } else {
                        mutableSetOf<String>()
                    }
                    
                    val songId = currentMediaItem.mediaId
                    val wasRemoving = currentFavorites.contains(songId)
                    
                    // Toggle favorite status
                    if (currentFavorites.contains(songId)) {
                        currentFavorites.remove(songId)
                        Log.d(TAG, "Removed song from favorites via notification: $songId")
                    } else {
                        currentFavorites.add(songId)
                        Log.d(TAG, "Added song to favorites via notification: $songId")
                    }
                    
                    // Save updated favorites
                    val updatedJson = Gson().toJson(currentFavorites)
                    appSettings.setFavoriteSongs(updatedJson)
                    
                    // Also need to update the favorites playlist to stay in sync
                    updateFavoritesPlaylist(songId, !wasRemoving)
                    
                    // Notify ViewModel about favorite change
                    val notifyIntent = Intent("chromahub.rhythm.app.action.FAVORITE_CHANGED")
                    sendBroadcast(notifyIntent)
                    Log.d(TAG, "Sent FAVORITE_CHANGED broadcast to notify ViewModel")
                    
                    // Schedule custom layout update with debouncing
                    scheduleCustomLayoutUpdate(200) // Longer delay for favorite changes
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error toggling favorite", e)
                }
            }
        }
    }
    
    private fun updateFavoritesPlaylist(songId: String, isAdding: Boolean) {
        try {
            // Get current playlists
            val playlistsJson = appSettings.playlists.value
            if (playlistsJson.isNullOrEmpty()) return
            
            val type = object : TypeToken<List<Playlist>>() {}.type
            val playlists: MutableList<Playlist> = Gson().fromJson(playlistsJson, type)
            
            // Find and update the Liked playlist (ID: "1")
            val favoritesPlaylist = playlists.find { it.id == "1" && it.name == "Liked" }
            if (favoritesPlaylist != null) {
                val updatedPlaylist = if (isAdding) {
                    // Add song to favorites playlist if not already there
                    if (!favoritesPlaylist.songs.any { it.id == songId }) {
                        // Find the song to add (would need access to all songs, this is a limitation)
                        Log.d(TAG, "Would add song $songId to favorites playlist, but song details not available in service")
                        favoritesPlaylist
                    } else {
                        favoritesPlaylist
                    }
                } else {
                    // Remove song from favorites playlist
                    favoritesPlaylist.copy(
                        songs = favoritesPlaylist.songs.filter { it.id != songId },
                        dateModified = System.currentTimeMillis()
                    )
                }
                
                // Update the playlist in the list
                val updatedPlaylists = playlists.map { if (it.id == "1") updatedPlaylist else it }
                val updatedPlaylistsJson = Gson().toJson(updatedPlaylists)
                appSettings.setPlaylists(updatedPlaylistsJson)
                
                Log.d(TAG, "Updated favorites playlist: ${if (isAdding) "added" else "removed"} song $songId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating favorites playlist", e)
        }
    }
    
    private fun updateCustomLayout() {
        try {
            // Create a new instance of the favorite command to avoid reference issues
            val currentFavoriteCommand = getCurrentFavoriteCommand()
            val currentShuffleCommand = shuffleCommand
            val currentRepeatCommand = repeatCommand
            
            mediaSession?.setCustomLayout(ImmutableList.of(currentShuffleCommand, currentRepeatCommand))
            
            // Update state tracking after successful update
            lastShuffleState = controller?.shuffleModeEnabled ?: false
            lastRepeatMode = controller?.repeatMode ?: Player.REPEAT_MODE_OFF
            lastFavoriteState = isCurrentSongFavorite()
            
            val currentMediaItem = player.currentMediaItem
            Log.d(TAG, "Updated custom layout - Song: ${currentMediaItem?.mediaMetadata?.title}, " +
                      "Favorite state: ${lastFavoriteState}, " +
                      "Shuffle: ${lastShuffleState}, " +
                      "Repeat: ${lastRepeatMode}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating custom layout", e)
        }
    }
    
    private fun updateCustomLayoutSmart() {
        // Only update if layout actually needs to change
        // This helps prevent unnecessary recreations and flickering
        mediaSession?.let { session ->
            try {
                val currentShuffleState = controller?.shuffleModeEnabled ?: false
                val currentRepeatMode = controller?.repeatMode ?: Player.REPEAT_MODE_OFF
                val currentFavoriteState = isCurrentSongFavorite()
                
                // Check if anything actually changed
                if (currentShuffleState == lastShuffleState &&
                    currentRepeatMode == lastRepeatMode &&
                    currentFavoriteState == lastFavoriteState) {
                    Log.d(TAG, "Custom layout state unchanged, skipping update")
                    return
                }
                
                // Update state tracking
                lastShuffleState = currentShuffleState
                lastRepeatMode = currentRepeatMode
                lastFavoriteState = currentFavoriteState
                
                val currentFavoriteCommand = getCurrentFavoriteCommand()
                val currentShuffleCommand = shuffleCommand
                val currentRepeatCommand = repeatCommand
                
                // Create the layout
                session.setCustomLayout(ImmutableList.of(currentShuffleCommand, currentRepeatCommand))
                
                Log.d(TAG, "Smart updated custom layout - Favorite: $currentFavoriteState, " +
                          "Shuffle: $currentShuffleState, Repeat: $currentRepeatMode")
            } catch (e: Exception) {
                Log.e(TAG, "Error in smart custom layout update", e)
            }
        }
    }
    
    private fun scheduleCustomLayoutUpdate(delayMs: Long = 150) {
        // Cancel any pending update
        updateLayoutJob?.cancel()
        
        // Schedule a new update with debouncing
        updateLayoutJob = serviceScope.launch {
            kotlinx.coroutines.delay(delayMs)
            updateCustomLayoutSmart()
        }
    }
    
    private fun forceCustomLayoutUpdate() {
        // Force an immediate update without debouncing (for initial setup)
        serviceScope.launch {
            updateCustomLayout()
        }
    }
    
    private fun applyPlayerSettings() {
        player.apply {
            // Apply audio normalization
            if (appSettings.audioNormalization.value) {
                volume = 1.0f
            }

            // Apply replay gain if enabled
            if (appSettings.replayGain.value) {
                Log.d(TAG, "Replay gain enabled")
            }
        }

        // Apply gapless playback setting
        rhythmPlayerEngine.setGaplessPlayback(appSettings.gaplessPlayback.value)

        // Crossfade is now managed by TransitionController + RhythmPlayerEngine
        // Settings are read reactively from AppSettings by the controller

        Log.d(TAG, "Applied player settings: " +
                "HQ Audio=${appSettings.highQualityAudio.value}, " +
                "Gapless=${appSettings.gaplessPlayback.value}, " +
                "Crossfade=${appSettings.crossfade.value} (${appSettings.crossfadeDuration.value}s), " +
                "Normalization=${appSettings.audioNormalization.value}, " +
                "ReplayGain=${appSettings.replayGain.value}")
    }
    
    // Crossfade is now handled by RhythmPlayerEngine + TransitionController
    // See: infrastructure/service/player/RhythmPlayerEngine.kt
    // See: infrastructure/service/player/TransitionController.kt

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with command: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_UPDATE_SETTINGS -> {
                Log.d(TAG, "Updating service settings")
                applyPlayerSettings()
            }
            ACTION_PLAY_EXTERNAL_FILE -> {
                intent.data?.let { uri ->
                    playExternalFile(uri)
                }
            }
            ACTION_INIT_SERVICE -> {
                Log.d(TAG, "Service initialization requested")
                // Load and apply settings when service starts
                applyPlayerSettings()
            }
            ACTION_START_SLEEP_TIMER -> {
                val durationMs = intent.getLongExtra("duration", 0L)
                val fadeOut = intent.getBooleanExtra("fadeOut", true)
                val pauseOnly = intent.getBooleanExtra("pauseOnly", false)
                if (durationMs > 0) {
                    startSleepTimer(durationMs, fadeOut, pauseOnly)
                }
            }
            ACTION_STOP_SLEEP_TIMER -> {
                stopSleepTimer()
            }
            ACTION_SET_EQUALIZER_ENABLED -> {
                val enabled = intent.getBooleanExtra("enabled", false)
                Log.d(TAG, "Received intent to set equalizer enabled: $enabled")
                setEqualizerEnabled(enabled)
                
                // Broadcast current state back for UI verification
                val actualState = equalizer?.enabled ?: false
                if (actualState != enabled) {
                    Log.w(TAG, "Equalizer state verification failed. Requested: $enabled, Actual: $actualState")
                }
            }
            ACTION_SET_EQUALIZER_BAND -> {
                val band = intent.getShortExtra("band", 0)
                val level = intent.getShortExtra("level", 0)
                if (equalizer == null) {
                    Log.e(TAG, "Cannot set band level: equalizer is null")
                    return START_NOT_STICKY
                }
                setEqualizerBandLevel(band, level)
            }
            ACTION_SET_BASS_BOOST -> {
                val enabled = intent.getBooleanExtra("enabled", false)
                val strength = intent.getShortExtra("strength", 0)
                Log.d(TAG, "Received intent to set bass boost - enabled: $enabled, strength: $strength")
                
                if (rhythmBassBoostProcessor == null && player.audioSessionId != 0) {
                    Log.d(TAG, "Rhythm bass boost processor is null, attempting initialization")
                    initializeAudioEffects()
                }
                
                setBassBoostEnabled(enabled)
                if (enabled) setBassBoostStrength(strength)
            }
            ACTION_SET_VIRTUALIZER -> {
                val enabled = intent.getBooleanExtra("enabled", false)
                val strength = intent.getShortExtra("strength", 0)
                Log.d(TAG, "Received intent to set virtualizer - enabled: $enabled, strength: $strength")
                
                if (rhythmSpatializationProcessor == null && player.audioSessionId != 0) {
                    Log.d(TAG, "Rhythm spatialization processor is null, attempting initialization")
                    initializeAudioEffects()
                }
                
                setVirtualizerEnabled(enabled)
                if (enabled) setVirtualizerStrength(strength)
            }
            ACTION_APPLY_EQUALIZER_PRESET -> {
                val preset = intent.getStringExtra("preset") ?: ""
                val levels = intent.getFloatArrayExtra("levels")
                if (levels != null) {
                    if (equalizer == null) {
                        Log.e(TAG, "Cannot apply preset: equalizer is null")
                        // Try to initialize if session ID is available
                        if (player.audioSessionId != 0) {
                            Log.d(TAG, "Attempting to initialize equalizer before applying preset")
                            initializeAudioEffects()
                            // Try applying again after initialization
                            if (equalizer != null) {
                                applyEqualizerPreset(levels)
                                Log.d(TAG, "Applied equalizer preset after initialization: $preset with ${levels.size} bands")
                            } else {
                                Log.e(TAG, "Failed to initialize equalizer, cannot apply preset")
                            }
                        }
                    } else {
                        applyEqualizerPreset(levels)
                        Log.d(TAG, "Applied equalizer preset: $preset with ${levels.size} bands")
                    }
                }
            }
            ACTION_GET_EQUALIZER_DIAGNOSTICS -> {
                val diagnostics = getEqualizerDiagnostics()
                Log.i(TAG, diagnostics)
            }
            ACTION_PLAY_PAUSE -> {
                Log.d(TAG, "Widget play/pause action")
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
                // Update widget immediately after action
                updateWidgetFromMediaItem(player.currentMediaItem)
            }
            ACTION_SKIP_NEXT -> {
                Log.d(TAG, "Widget skip next action")
                player.seekToNext()
                // Update widget immediately after action
                serviceScope.launch {
                    kotlinx.coroutines.delay(100) // Small delay for track change
                    updateWidgetFromMediaItem(player.currentMediaItem)
                }
            }
            ACTION_SKIP_PREVIOUS -> {
                Log.d(TAG, "Widget skip previous action")
                player.seekToPrevious()
                // Update widget immediately after action
                serviceScope.launch {
                    kotlinx.coroutines.delay(100) // Small delay for track change
                    updateWidgetFromMediaItem(player.currentMediaItem)
                }
            }
            ACTION_TOGGLE_FAVORITE -> {
                Log.d(TAG, "Widget toggle favorite action")
                toggleCurrentSongFavorite()
                // Update widget immediately after favorite toggle
                serviceScope.launch {
                    kotlinx.coroutines.delay(150) // Small delay for state update
                    updateWidgetFromMediaItem(player.currentMediaItem)
                }
            }
            ACTION_MUTE -> {
                Log.d(TAG, "Mute action")
                mutePlayer()
            }
            ACTION_UNMUTE -> {
                Log.d(TAG, "Unmute action")
                unmutePlayer()
            }
            ACTION_TOGGLE_MUTE -> {
                Log.d(TAG, "Toggle mute action")
                toggleMute()
            }
        }
        
        // We make sure to call the super implementation
        return super.onStartCommand(intent, flags, startId)
    }
    
    /**
     * Play an external audio file
     */
    private fun playExternalFile(uri: Uri) {
        Log.d(TAG, "Playing external file: $uri")

        // Use service-scoped coroutine to handle operations without blocking the main thread
        serviceScope.launch {
            try {
                // Check if we've seen this URI before (on main thread - quick cache lookup)
                val cachedItem = externalUriCache[uri.toString()]
                if (cachedItem != null) {
                    Log.d(TAG, "Using cached media item for URI: $uri")
                    
                    // Clear the player first to avoid conflicts with existing items
                    player.clearMediaItems()
                    
                    // Play the media item
                    player.setMediaItem(cachedItem)
                    player.prepare()
                    player.play()
                    
                    return@launch
                }
                
                // Add a small delay before processing to allow previous operations to complete
                delay(500)
                
                // Extract metadata from the audio file in a background thread
                val mediaItem = withContext(Dispatchers.IO) {
                    try {
                        val song = chromahub.rhythm.app.util.MediaUtils.extractMetadataFromUri(this@MediaPlaybackService, uri)
                        Log.d(TAG, "Extracted metadata for external file: ${song.title} by ${song.artist}")
                        
                        // Create a media item with the extracted metadata
                        MediaItem.Builder()
                            .setUri(uri)
                            .setMediaId(uri.toString())
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(song.title)
                                    .setArtist(song.artist)
                                    .setAlbumTitle(song.album)
                                    .setArtworkUri(song.artworkUri)
                                    .build()
                            )
                            .build()
                            
                    } catch (e: Exception) {
                        Log.e(TAG, "Error extracting metadata from external file", e)
                        
                        // Fall back to basic playback if metadata extraction fails
                        val mimeType = contentResolver.getType(uri)
                        Log.d(TAG, "Falling back to basic playback with mime type: $mimeType")
                        
                        MediaItem.Builder()
                            .setUri(uri)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(uri.lastPathSegment ?: "Unknown")
                                    .build()
                            )
                            .build()
                    }
                }
                
                // Back on main thread - set up playback
                player.clearMediaItems()
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
                
                // Cache the media item
                externalUriCache[uri.toString()] = mediaItem
                
                // Force a recheck of playback state in case it doesn't start
                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            Log.d(TAG, "Playback ready, ensuring play is called")
                            player.play()
                            player.removeListener(this)
                        }
                    }
                })
                
            } catch (e: Exception) {
                Log.e(TAG, "Error in playExternalFile coroutine", e)
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service being destroyed")
        
        // Unregister BroadcastReceiver
        try {
            unregisterReceiver(favoriteChangeReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "Error unregistering favorite change receiver", e)
        }
        
        // Cancel all coroutines and pending jobs
        updateLayoutJob?.cancel()
        sleepTimerJob?.cancel()
        serviceScope.cancel()
        
        // Release crossfade engine and transition controller
        transitionController.release()
        rhythmPlayerEngine.release()
        
        // Release audio effects
        releaseAudioEffects()
        
        // Remove player listener before releasing player
        playerListener?.let { player.removeListener(it) }
        playerListener = null
        
        // Remove service as listener from controller
        controller?.removeListener(this)
        
        mediaSession?.run {
            player.release()
            controller?.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = mediaSession
    
    @OptIn(UnstableApi::class)
    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        // Let Media3 handle notification updates but ensure our icon is used
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    private inner class MediaSessionCallback : MediaLibrarySession.Callback {
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            Log.d(TAG, "onConnect: ${controller.packageName}")
            val availableCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS
                .buildUpon()
            if (session.isMediaNotificationController(controller) ||
                session.isAutoCompanionController(controller) ||
                session.isAutomotiveController(controller)
            ) {
                for (commandButton in customCommands) {
                    commandButton.sessionCommand?.let { availableCommands.add(it) }
                }
            }
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(availableCommands.build())
                .build()
        }

        @OptIn(UnstableApi::class)
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            val serviceController = this@MediaPlaybackService.controller
            if (serviceController == null) {
                Log.w(TAG, "Controller not ready for custom command: ${customCommand.customAction}")
                return Futures.immediateFuture(SessionResult(SessionError.ERROR_SESSION_DISCONNECTED))
            }
            
            return Futures.immediateFuture(
                when (customCommand.customAction) {
                    SHUFFLE_MODE_ON -> {
                        serviceController.shuffleModeEnabled = true
                        SessionResult(SessionResult.RESULT_SUCCESS)
                    }

                    SHUFFLE_MODE_OFF -> {
                        serviceController.shuffleModeEnabled = false
                        SessionResult(SessionResult.RESULT_SUCCESS)
                    }

                    REPEAT_MODE_OFF -> {
                        serviceController.repeatMode = Player.REPEAT_MODE_OFF
                        SessionResult(SessionResult.RESULT_SUCCESS)
                    }

                    REPEAT_MODE_ONE -> {
                        serviceController.repeatMode = Player.REPEAT_MODE_ONE
                        SessionResult(SessionResult.RESULT_SUCCESS)
                    }

                    REPEAT_MODE_ALL -> {
                        serviceController.repeatMode = Player.REPEAT_MODE_ALL
                        SessionResult(SessionResult.RESULT_SUCCESS)
                    }

                    FAVORITE_ON -> {
                        // Add current song to favorites
                        Log.d(TAG, "Favorite ON command received")
                        toggleCurrentSongFavorite()
                        // Immediate UI feedback for responsive feel
                        serviceScope.launch {
                            kotlinx.coroutines.delay(50) // Very short delay for immediate response
                            updateCustomLayoutSmart()
                        }
                        SessionResult(SessionResult.RESULT_SUCCESS)
                    }

                    FAVORITE_OFF -> {
                        // Remove current song from favorites  
                        Log.d(TAG, "Favorite OFF command received")
                        toggleCurrentSongFavorite()
                        // Immediate UI feedback for responsive feel
                        serviceScope.launch {
                            kotlinx.coroutines.delay(50) // Very short delay for immediate response
                            updateCustomLayoutSmart()
                        }
                        SessionResult(SessionResult.RESULT_SUCCESS)
                    }

                    else -> {
                        SessionResult(SessionError.ERROR_NOT_SUPPORTED)
                    }
                })
        }

        override fun onDisconnected(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ) {
            Log.d(TAG, "onDisconnected: ${controller.packageName}")
            super.onDisconnected(session, controller)
        }
        
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            Log.d(TAG, "onAddMediaItems: ${mediaItems.size} items")
            
            val updatedMediaItems = mediaItems.map { mediaItem ->
                if (mediaItem.requestMetadata.searchQuery != null) {
                    // This is a search request
                    Log.d(TAG, "Search request: ${mediaItem.requestMetadata.searchQuery}")
                    mediaItem
                } else if (mediaItem.mediaId.isNotEmpty()) {
                    // Check if this is an external URI that we've cached
                    val cachedItem = externalUriCache[mediaItem.mediaId]
                    cachedItem ?: mediaItem
                } else {
                    mediaItem
                }
            }
            
            return Futures.immediateFuture(updatedMediaItems)
        }
        
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<androidx.media3.session.LibraryResult<MediaItem>> {
            Log.d(TAG, "onGetLibraryRoot from ${browser.packageName}")
            
            // Create a root media item
            val rootItem = MediaItem.Builder()
                .setMediaId("root")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Rhythm Music Library")
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                        .setIsPlayable(false)
                        .setIsBrowsable(true)
                        .build()
                )
                .build()
                
            return Futures.immediateFuture(androidx.media3.session.LibraryResult.ofItem(rootItem, params))
        }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        Log.d(TAG, "Shuffle mode changed to: $shuffleModeEnabled")
        // Use debounced update to prevent rapid UI changes
        scheduleCustomLayoutUpdate(100)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super<Player.Listener>.onRepeatModeChanged(repeatMode)
        Log.d(TAG, "Repeat mode changed to: $repeatMode")
        // Use debounced update to prevent rapid UI changes
        scheduleCustomLayoutUpdate(100)
    }
    
    // Mute state tracking
    private var volumeBeforeMute: Float = 1.0f
    private var isMuted: Boolean = false
    
    /**
     * Mute the player while preserving the volume level
     * Manual implementation since mute()/unmute() require newer Media3 version
     */
    private fun mutePlayer() {
        if (!isMuted) {
            volumeBeforeMute = player.volume
            player.volume = 0f
            isMuted = true
            Log.d(TAG, "Player muted (volume $volumeBeforeMute preserved)")
        }
    }
    
    /**
     * Unmute the player and restore the previous volume
     */
    private fun unmutePlayer() {
        if (isMuted) {
            player.volume = volumeBeforeMute
            isMuted = false
            Log.d(TAG, "Player unmuted (volume $volumeBeforeMute restored)")
        }
    }
    
    /**
     * Toggle mute state
     */
    private fun toggleMute() {
        if (isMuted) {
            unmutePlayer()
        } else {
            mutePlayer()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        Log.d(TAG, "Media item transitioned: ${mediaItem?.mediaMetadata?.title}, reason=$reason")
        
        // Update custom layout when song changes to reflect correct favorite state
        scheduleCustomLayoutUpdate(50) // Shorter delay for song transitions
        
        // Update widget with new song info
        updateWidgetFromMediaItem(mediaItem)
    }
    
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d(TAG, "Is playing changed: $isPlaying")
        // Update widget when play/pause state changes
        updateWidgetFromMediaItem(player.currentMediaItem)
    }
    
    /**
     * Helper function to convert MediaItem to Song for scrobbling and widgets
     */
    private fun convertMediaItemToSong(mediaItem: MediaItem): Song? {
        return try {
            Song(
                id = mediaItem.mediaId,
                title = mediaItem.mediaMetadata.title?.toString() ?: "Unknown",
                artist = mediaItem.mediaMetadata.artist?.toString() ?: "Unknown",
                album = mediaItem.mediaMetadata.albumTitle?.toString() ?: "",
                uri = mediaItem.requestMetadata.mediaUri ?: Uri.EMPTY,
                artworkUri = mediaItem.mediaMetadata.artworkUri,
                duration = player.duration.takeIf { it > 0 } ?: 0L,
                trackNumber = 0,
                year = 0,
                genre = "",
                albumId = "",
                albumArtist = mediaItem.mediaMetadata.albumArtist?.toString()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting MediaItem to Song", e)
            null
        }
    }
    
    private fun updateWidgetFromMediaItem(mediaItem: MediaItem?) {
        if (mediaItem != null) {
            val song = convertMediaItemToSong(mediaItem)
            if (song != null) {
                val isFavorite = isCurrentSongFavorite()
                val hasPrevious = player.hasPreviousMediaItem()
                val hasNext = player.hasNextMediaItem()
                WidgetUpdater.updateWidget(this, song, player.isPlaying, hasPrevious, hasNext, isFavorite)
            } else {
                WidgetUpdater.updateWidget(this, null, false)
            }
        } else {
            WidgetUpdater.updateWidget(this, null, false)
        }
    }

    // Sleep Timer functionality
    fun startSleepTimer(durationMs: Long, fadeOut: Boolean = true, pauseOnly: Boolean = false) {
        Log.d(TAG, "Starting sleep timer: ${durationMs}ms, fadeOut: $fadeOut, pauseOnly: $pauseOnly")
        stopSleepTimer() // Stop any existing timer
        
        if (durationMs <= 0) {
            Log.e(TAG, "Invalid sleep timer duration: $durationMs")
            return
        }
        
        sleepTimerDurationMs = durationMs
        sleepTimerStartTime = System.currentTimeMillis()
        fadeOutEnabled = fadeOut
        pauseOnlyEnabled = pauseOnly
        
        // Broadcast initial status immediately
        broadcastSleepTimerStatus()
        
        sleepTimerJob = serviceScope.launch {
            try {
                if (fadeOut && durationMs > 10000) { // Only fade if duration > 10 seconds
                    // Regular updates until fade start time (last 10 seconds)
                    val fadeStartTime = durationMs - 10000
                    var remainingTime = durationMs
                    
                    while (remainingTime > 10000) {
                        delay(1000) // Update every second
                        remainingTime = durationMs - (System.currentTimeMillis() - sleepTimerStartTime)
                        if (remainingTime <= 0) break
                        broadcastSleepTimerStatus()
                    }
                    
                    // Fade out over 10 seconds
                    val originalVolume = player.volume
                    val fadeSteps = 100
                    val fadeInterval = 10000L / fadeSteps
                    
                    for (i in fadeSteps downTo 0) {
                        val volume = originalVolume * (i.toFloat() / fadeSteps)
                        player.volume = volume
                        delay(fadeInterval)
                        // Broadcast status every few steps during fade
                        if (i % 10 == 0) {
                            broadcastSleepTimerStatus()
                        }
                    }
                } else {
                    // No fade out, broadcast updates every second until completion
                    var remainingTime = durationMs
                    while (remainingTime > 0) {
                        delay(1000) // Update every second
                        remainingTime = durationMs - (System.currentTimeMillis() - sleepTimerStartTime)
                        if (remainingTime <= 0) break
                        broadcastSleepTimerStatus()
                    }
                }
                
                // Timer finished - pause or stop playback
                if (pauseOnly) {
                    player.pause()
                    Log.d(TAG, "Sleep timer paused playback")
                } else {
                    player.stop()
                    Log.d(TAG, "Sleep timer stopped playback")
                }
                
                // Reset volume if it was changed during fade
                if (fadeOut) {
                    player.volume = 1.0f
                }
                
                resetSleepTimer()
                
            } catch (e: CancellationException) {
                Log.d(TAG, "Sleep timer was cancelled")
                resetSleepTimer()
            } catch (e: Exception) {
                Log.e(TAG, "Error in sleep timer", e)
                resetSleepTimer()
            } finally {
                broadcastSleepTimerStatus()
            }
        }
        
        Log.d(TAG, "Sleep timer job started for ${durationMs}ms")
    }
    
    fun stopSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        
        // Reset volume if it was changed during fade
        if (fadeOutEnabled) {
            player.volume = 1.0f
        }
        
        resetSleepTimer()
        broadcastSleepTimerStatus()
        Log.d(TAG, "Sleep timer stopped")
    }
    
    fun getRemainingTimeMs(): Long {
        return if (sleepTimerJob?.isActive == true && sleepTimerDurationMs > 0 && sleepTimerStartTime > 0) {
            val elapsed = System.currentTimeMillis() - sleepTimerStartTime
            maxOf(0, sleepTimerDurationMs - elapsed)
        } else {
            0L
        }
    }
    
    private fun resetSleepTimer() {
        sleepTimerDurationMs = 0L
        sleepTimerStartTime = 0L
        fadeOutEnabled = true
        pauseOnlyEnabled = false
    }
    
    private fun broadcastSleepTimerStatus() {
        val intent = Intent(BROADCAST_SLEEP_TIMER_STATUS).apply {
            putExtra(EXTRA_TIMER_ACTIVE, isSleepTimerActive())
            putExtra(EXTRA_REMAINING_TIME, getRemainingTimeMs())
        }
        sendBroadcast(intent)
    }
    
    // Audio Effects (Equalizer) functionality
    fun getAudioSessionId(): Int {
        return try {
            player.audioSessionId
        } catch (e: Exception) {
            Log.e(TAG, "Error getting audio session ID", e)
            0
        }
    }
    
    fun initializeAudioEffects() {
        // Prevent concurrent initialization
        if (isInitializingAudioEffects) {
            Log.w(TAG, "Audio effects initialization already in progress, skipping")
            return
        }
        
        try {
            isInitializingAudioEffects = true
            val audioSessionId = player.audioSessionId
            Log.d(TAG, "Initializing audio effects with session ID: $audioSessionId (previously initialized: $audioEffectsInitialized)")
            
            // Skip initialization if session ID is invalid
            if (audioSessionId == 0) {
                Log.w(TAG, "Invalid audio session ID (0), skipping effects initialization")
                isInitializingAudioEffects = false
                return
            }
            
            // CRITICAL: Release ALL existing effects BEFORE creating new ones to prevent AudioFlinger error -38
            try {
                equalizer?.release()
                equalizer = null
                
                // Reset Rhythm processors
                rhythmBassBoostProcessor?.reset()
                rhythmSpatializationProcessor?.reset()
                
                Log.d(TAG, "Released existing audio effects before reinitialization")
                
                // Small delay to allow Android AudioFlinger to fully release resources
                Thread.sleep(50)
            } catch (e: Exception) {
                Log.w(TAG, "Error releasing existing effects: ${e.message}")
            }
            
            // Initialize equalizer directly (no dummy checks - they waste effect slots)
            try {
                equalizer = android.media.audiofx.Equalizer(0, audioSessionId).apply {
                    enabled = false
                }
                Log.d(TAG, "Equalizer initialized with ${equalizer?.numberOfBands} bands for session $audioSessionId")
            } catch (e: Exception) {
                Log.w(TAG, "Equalizer is not available on this device: ${e.message}")
                equalizer = null
            }
            
            // Initialize Rhythm audio processors (replaces Android BassBoost and Spatializer)
            // Processors are already created in onCreate(), just load their settings here
            Log.d(TAG, "Loading Rhythm processor settings")
            
            // Ensure processors are available (they should be created in onCreate)
            if (rhythmBassBoostProcessor == null) {
                Log.w(TAG, "Rhythm bass boost processor is null, creating new instance")
                try {
                    rhythmBassBoostProcessor = chromahub.rhythm.app.infrastructure.audio.RhythmBassBoostProcessor()
                    isBassBoostAvailable = true
                    appSettings.setBassBoostAvailable(true)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create bass boost processor", e)
                    isBassBoostAvailable = false
                    appSettings.setBassBoostAvailable(false)
                }
            }
            
            if (rhythmSpatializationProcessor == null) {
                Log.w(TAG, "Rhythm spatialization processor is null, creating new instance")
                try {
                    rhythmSpatializationProcessor = chromahub.rhythm.app.infrastructure.audio.RhythmSpatializationProcessor()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create spatialization processor", e)
                }
            }
            
            // Load saved settings and apply them
            loadSavedAudioEffects()
            
            // Mark as successfully initialized
            audioEffectsInitialized = true
            Log.d(TAG, "Audio effects initialization completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing audio effects", e)
            audioEffectsInitialized = false
        } finally {
            isInitializingAudioEffects = false
        }
    }
    
    private fun loadSavedAudioEffects() {
        try {
            // Verify equalizer is available before loading settings
            if (equalizer == null) {
                Log.w(TAG, "Cannot load saved audio effects: equalizer is null")
                return
            }
            
            val shouldBeEnabled = appSettings.equalizerEnabled.value
            Log.d(TAG, "Loading saved effects - EQ should be enabled: $shouldBeEnabled")
            
            // Load band levels (supports both 5-band legacy and 10-band)
            val bandLevelsString = appSettings.equalizerBandLevels.value
            val bandLevels = bandLevelsString.split(",").mapNotNull { it.toFloatOrNull() }
            if (bandLevels.isNotEmpty()) {
                // Apply band levels first, then enable
                // Use the same interpolation logic as applyEqualizerPreset
                applyEqualizerPreset(bandLevels.toFloatArray())
            }
            
            // Enable equalizer AFTER applying levels to avoid audio glitches
            equalizer?.enabled = shouldBeEnabled
            val actualState = equalizer?.enabled ?: false
            if (actualState != shouldBeEnabled) {
                Log.e(TAG, "EQ state mismatch after load! Expected: $shouldBeEnabled, Actual: $actualState")
            }
            
            // Load Rhythm bass boost settings
            val bassBoostShouldBeEnabled = appSettings.bassBoostEnabled.value
            if (rhythmBassBoostProcessor != null) {
                rhythmBassBoostProcessor?.setEnabled(bassBoostShouldBeEnabled)
                if (bassBoostShouldBeEnabled) {
                    rhythmBassBoostProcessor?.setStrength(appSettings.bassBoostStrength.value.toShort())
                }
                Log.d(TAG, "Rhythm bass boost loaded: enabled=$bassBoostShouldBeEnabled, strength=${rhythmBassBoostProcessor?.getStrength()}")
            } else {
                Log.w(TAG, "Cannot load bass boost settings: Rhythm processor is null")
            }
            
            // Load Rhythm spatialization settings
            val virtualizerEnabled = appSettings.virtualizerEnabled.value
            virtualizerStrength = appSettings.virtualizerStrength.value.toShort()
            if (rhythmSpatializationProcessor != null) {
                rhythmSpatializationProcessor?.setEnabled(virtualizerEnabled)
                rhythmSpatializationProcessor?.setStrength(virtualizerStrength)
                Log.d(TAG, "Rhythm spatialization loaded: enabled=$virtualizerEnabled, strength=$virtualizerStrength")
            } else {
                Log.d(TAG, "Cannot load spatialization settings: Rhythm processor is null")
            }
            
            Log.d(TAG, "Loaded saved audio effects - EQ: ${appSettings.equalizerEnabled.value}, Bass: ${appSettings.bassBoostEnabled.value}, Virtualizer: ${appSettings.virtualizerEnabled.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading saved audio effects", e)
        }
    }
    
    fun setEqualizerEnabled(enabled: Boolean) {
        if (equalizer == null) {
            Log.w(TAG, "Attempting to enable equalizer but equalizer is null. Will reinitialize.")
            // Try to initialize if we have a valid session ID
            if (player.audioSessionId != 0) {
                initializeAudioEffects()
            } else {
                Log.e(TAG, "Cannot enable equalizer: invalid audio session ID")
                return
            }
        }
        
        equalizer?.enabled = enabled
        val actualState = equalizer?.enabled ?: false
        Log.d(TAG, "Equalizer enabled: $enabled, actual state: $actualState")
        
        if (actualState != enabled) {
            Log.e(TAG, "Equalizer state mismatch! Requested: $enabled, Actual: $actualState")
        }
    }
    
    fun setEqualizerBandLevel(band: Short, level: Short) {
        try {
            // When a single band is changed in a 10-band UI but we only have 5 hardware bands,
            // we need to reload and re-interpolate all bands from saved settings
            val bandLevelsString = appSettings.equalizerBandLevels.value
            val bandLevels = bandLevelsString.split(",").mapNotNull { it.toFloatOrNull() }
            
            if (bandLevels.size == 10 && (equalizer?.numberOfBands?.toInt() ?: 0) < 10) {
                // Re-apply all bands with interpolation
                applyEqualizerPreset(bandLevels.toFloatArray())
                Log.d(TAG, "Re-applied 10-band EQ with interpolation after band $band change")
            } else {
                // Direct band setting when counts match
                equalizer?.setBandLevel(band, level)
                Log.d(TAG, "Set equalizer band $band to level $level")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting equalizer band level", e)
        }
    }
    
    fun getEqualizerBandLevel(band: Short): Short {
        return equalizer?.getBandLevel(band) ?: 0
    }
    
    fun getNumberOfBands(): Short {
        return equalizer?.numberOfBands ?: 0
    }
    
    fun getBandFreqRange(band: Short): IntArray? {
        return equalizer?.getBandFreqRange(band)
    }
    
    fun isEqualizerSupported(): Boolean {
        // All devices now support equalizer with software implementation
        return true
    }
    
    /**
     * Get diagnostic information about audio effects state for debugging
     */
    fun getEqualizerDiagnostics(): String {
        return buildString {
            appendLine("=== Audio Effects Diagnostics ===")
            appendLine("Audio effects initialized: $audioEffectsInitialized")
            appendLine("Currently initializing: $isInitializingAudioEffects")
            appendLine("Audio session ID: ${player.audioSessionId}")
            appendLine("")
            appendLine("--- Equalizer ---")
            appendLine("Equalizer object: ${if (equalizer != null) "initialized" else "null"}")
            equalizer?.let { eq ->
                appendLine("Enabled state: ${eq.enabled}")
                appendLine("Number of bands: ${eq.numberOfBands}")
                appendLine("Band levels: ${(0 until eq.numberOfBands.toInt()).map { eq.getBandLevel(it.toShort()) }}")
            }
            appendLine("Settings - Enabled: ${appSettings.equalizerEnabled.value}")
            appendLine("Settings - Preset: ${appSettings.equalizerPreset.value}")
            appendLine("Settings - AutoEQ: ${appSettings.autoEQProfile.value}")
            appendLine("Settings - Band levels: ${appSettings.equalizerBandLevels.value}")
            appendLine("")
            appendLine("--- Rhythm Bass Boost ---")
            appendLine("Processor: ${if (rhythmBassBoostProcessor != null) "initialized" else "null"}")
            rhythmBassBoostProcessor?.let { bb ->
                appendLine("Enabled state: ${bb.isEnabled()}")
                appendLine("Strength: ${bb.getStrength()}")
            }
            appendLine("Settings - Enabled: ${appSettings.bassBoostEnabled.value}")
            appendLine("Settings - Strength: ${appSettings.bassBoostStrength.value}")
            appendLine("Available: $isBassBoostAvailable")
            appendLine("")
            appendLine("--- Rhythm Spatialization ---")
            appendLine("Processor: ${if (rhythmSpatializationProcessor != null) "initialized" else "null"}")
            rhythmSpatializationProcessor?.let { sp ->
                appendLine("Enabled state: ${sp.isEnabled()}")
                appendLine("Strength: ${sp.getStrength()}")
            }
            appendLine("Settings - Enabled: ${appSettings.virtualizerEnabled.value}")
            appendLine("Settings - Strength: ${appSettings.virtualizerStrength.value}")
        }
    }
    
    fun isBassBoostSupported(): Boolean {
        return isBassBoostAvailable
    }
    
    fun applyEqualizerPreset(levels: FloatArray) {
        try {
            if (equalizer == null) {
                Log.w(TAG, "Cannot apply preset: equalizer is null")
                return
            }
            
            equalizer?.let { eq ->
                val numberOfBands = eq.numberOfBands.toInt()
                val inputBands = levels.size
                
                if (inputBands == numberOfBands) {
                    // Direct mapping if bands match
                    for (i in 0 until numberOfBands) {
                        val level = (levels[i] * 100).toInt().toShort()
                        eq.setBandLevel(i.toShort(), level)
                    }
                } else if (inputBands > numberOfBands) {
                    // Map 10 UI bands to available hardware bands using interpolation
                    // This handles the case where UI has 10 bands but hardware has 5
                    val mappedLevels = interpolateBands(levels, numberOfBands)
                    for (i in 0 until numberOfBands) {
                        val level = (mappedLevels[i] * 100).toInt().toShort()
                        eq.setBandLevel(i.toShort(), level)
                    }
                } else {
                    // If hardware has more bands than UI, apply what we have
                    for (i in 0 until inputBands) {
                        val level = (levels[i] * 100).toInt().toShort()
                        eq.setBandLevel(i.toShort(), level)
                    }
                }
                Log.d(TAG, "Applied equalizer preset: ${levels.size} UI bands -> $numberOfBands hardware bands")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error applying equalizer preset", e)
        }
    }
    
    /**
     * Interpolates 10-band EQ settings to the available hardware bands.
     * Uses weighted averaging based on frequency proximity.
     * 
     * Standard 10-band frequencies: 31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz
     * Standard 5-band frequencies: ~60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz (varies by device)
     */
    private fun interpolateBands(inputLevels: FloatArray, outputBands: Int): FloatArray {
        if (outputBands <= 0 || inputLevels.isEmpty()) return FloatArray(outputBands)
        
        val result = FloatArray(outputBands)
        val inputBands = inputLevels.size
        
        // Define the mapping of 10-band to 5-band (approximate frequency groupings)
        // Band 0 (60Hz): avg of 31Hz, 62Hz, 125Hz
        // Band 1 (230Hz): avg of 250Hz, 500Hz
        // Band 2 (910Hz): avg of 1kHz, 2kHz
        // Band 3 (3.6kHz): avg of 4kHz, 8kHz
        // Band 4 (14kHz): 16kHz
        
        if (outputBands == 5 && inputBands == 10) {
            // Optimized mapping for the common 10->5 case
            result[0] = (inputLevels[0] * 0.3f + inputLevels[1] * 0.4f + inputLevels[2] * 0.3f)
            result[1] = (inputLevels[3] * 0.5f + inputLevels[4] * 0.5f)
            result[2] = (inputLevels[5] * 0.5f + inputLevels[6] * 0.5f)
            result[3] = (inputLevels[7] * 0.5f + inputLevels[8] * 0.5f)
            result[4] = inputLevels[9]
        } else {
            // General linear interpolation for other cases
            val ratio = (inputBands - 1).toFloat() / (outputBands - 1).toFloat()
            for (i in 0 until outputBands) {
                val srcPos = i * ratio
                val lowerIndex = srcPos.toInt().coerceIn(0, inputBands - 1)
                val upperIndex = (lowerIndex + 1).coerceIn(0, inputBands - 1)
                val fraction = srcPos - lowerIndex
                result[i] = inputLevels[lowerIndex] * (1 - fraction) + inputLevels[upperIndex] * fraction
            }
        }
        
        return result
    }
    
    fun setBassBoostEnabled(enabled: Boolean) {
        if (rhythmBassBoostProcessor == null) {
            Log.w(TAG, "Attempting to enable bass boost but Rhythm processor is null. Will reinitialize.")
            if (player.audioSessionId != 0) {
                initializeAudioEffects()
            } else {
                Log.e(TAG, "Cannot enable bass boost: invalid audio session ID")
                return
            }
        }
        
        rhythmBassBoostProcessor?.setEnabled(enabled)
        Log.d(TAG, "Rhythm bass boost enabled: $enabled (applies to next audio buffer)")
    }
    
    fun setBassBoostStrength(strength: Short) {
        try {
            if (rhythmBassBoostProcessor == null) {
                Log.w(TAG, "Cannot set bass boost strength: Rhythm processor is null")
                return
            }
            rhythmBassBoostProcessor?.setStrength(strength)
            Log.d(TAG, "Rhythm bass boost strength set to $strength (applies to next audio buffer)")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting bass boost strength", e)
        }
    }
    
    fun getBassBoostStrength(): Short {
        return rhythmBassBoostProcessor?.getStrength() ?: 0
    }
    
    fun setVirtualizerEnabled(enabled: Boolean) {
        if (rhythmSpatializationProcessor == null && player.audioSessionId != 0) {
            Log.w(TAG, "Rhythm spatialization processor is null, attempting reinitialization")
            initializeAudioEffects()
        }
        
        rhythmSpatializationProcessor?.setEnabled(enabled)
        virtualizerStrength = if (enabled) virtualizerStrength else 0
        Log.d(TAG, "Rhythm spatialization enabled: $enabled (applies to next audio buffer)")
    }
    
    fun setVirtualizerStrength(strength: Short) {
        try {
            virtualizerStrength = strength
            rhythmSpatializationProcessor?.setStrength(strength)
            Log.d(TAG, "Rhythm spatialization strength set to $strength (applies to next audio buffer)")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting virtualizer strength", e)
        }
    }
    
    fun getVirtualizerStrength(): Short {
        return rhythmSpatializationProcessor?.getStrength() ?: virtualizerStrength
    }
    
    fun isSpatializationAvailable(): Boolean {
        // Rhythm spatialization is always available
        return rhythmSpatializationProcessor != null
    }
    
    fun getSpatializationStatus(): String {
        return when {
            rhythmSpatializationProcessor == null -> "Not initialized"
            !rhythmSpatializationProcessor!!.isEnabled() -> "Available (Rhythm-based)"
            else -> "Active (Rhythm-based)"
        }
    }
    
    // Public methods for external access
    fun getMediaSession(): MediaLibrarySession? = mediaSession
    
    fun getSleepTimerRemainingTime(): Long = sleepTimerDurationMs - (System.currentTimeMillis() - sleepTimerStartTime)
    
    fun isSleepTimerActive(): Boolean = sleepTimerJob?.isActive == true
    
    private fun releaseAudioEffects() {
        try {
            equalizer?.release()
            equalizer = null
            
            // Reset Rhythm processors
            rhythmBassBoostProcessor?.reset()
            rhythmSpatializationProcessor?.reset()
            rhythmBassBoostProcessor = null
            rhythmSpatializationProcessor = null
            
            Log.d(TAG, "Audio effects released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing audio effects", e)
        }
    }
    
    /**
     * Called when the app is removed from recents (swiped away).
     * Implements the "stop playback on app close" setting.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        val shouldStopPlayback = appSettings.stopPlaybackOnAppClose.value
        
        Log.d(TAG, "onTaskRemoved called - stopPlaybackOnAppClose: $shouldStopPlayback")
        
        if (shouldStopPlayback) {
            // User wants playback to stop when app is closed
            player.apply {
                playWhenReady = false
                stop()
                clearMediaItems()
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            super.onTaskRemoved(rootIntent)
            return
        }
        
        // If not stopping on close, check if we should keep the service alive
        // Only keep alive if actually playing or has media
        if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
            // Nothing playing, stop the service
            Log.d(TAG, "No active playback, stopping service")
            stopSelf()
        } else {
            Log.d(TAG, "Continuing playback in background")
        }
        
        super.onTaskRemoved(rootIntent)
    }
}
