package chromahub.rhythm.app.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WavingHand // New import for Welcome screen icon
import androidx.compose.material.icons.filled.DarkMode // New import for Dark Mode icon
import chromahub.rhythm.app.shared.presentation.navigation.RhythmNavigation
import chromahub.rhythm.app.ui.theme.RhythmTheme
import chromahub.rhythm.app.ui.theme.festive.FestiveOverlayFromSettings
import chromahub.rhythm.app.shared.presentation.viewmodel.ThemeViewModel
import chromahub.rhythm.app.shared.presentation.viewmodel.AppUpdaterViewModel
import chromahub.rhythm.app.util.CrashReporter // Import CrashReporter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.util.MediaUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner // Corrected import for LocalLifecycleOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.animation.Crossfade
import androidx.compose.ui.text.font.FontWeight
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
//import chromahub.rhythm.app.ui.annotations.RhythmAnimation
import android.provider.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import chromahub.rhythm.app.shared.data.model.AppSettings // Import AppSettings
import java.util.Locale // Import Locale
import androidx.compose.material.icons.filled.Public // Import Public icon
import androidx.compose.material.icons.filled.BugReport // Import BugReport icon
import androidx.compose.material.icons.filled.Check // Import Check icon
import androidx.compose.material.icons.filled.MusicNote // Import MusicNote icon
import androidx.compose.material.icons.filled.Palette // Import Palette icon
import androidx.compose.material.icons.filled.Security // New import for Security icon
import androidx.compose.material.icons.filled.SystemUpdate // Import SystemUpdate icon
import androidx.compose.material.icons.filled.KeyboardArrowDown // Import KeyboardArrowDown icon
import androidx.compose.material.icons.filled.FormatListNumbered // Import FormatListNumbered icon
import androidx.compose.material.icons.filled.SortByAlpha // Import SortByAlpha icon
import androidx.compose.material.icons.filled.AccessTime // Import AccessTime icon
import androidx.compose.material.icons.filled.GridView // Import GridView icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.rememberCoroutineScope
import chromahub.rhythm.app.shared.presentation.components.common.M3LinearLoader // Import M3LinearLoader
import chromahub.rhythm.app.shared.presentation.components.common.M3FourColorCircularLoader // Import M3FourColorCircularLoader
import androidx.compose.ui.platform.LocalHapticFeedback // Import LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType // Import HapticFeedbackType
import androidx.compose.material3.ButtonDefaults // Import ButtonDefaults
import chromahub.rhythm.app.features.local.presentation.screens.SplashScreen
import chromahub.rhythm.app.shared.presentation.components.PermissionHandler
import chromahub.rhythm.app.features.local.presentation.components.dialogs.BetaProgramPopup
import chromahub.rhythm.app.features.local.presentation.screens.OnboardingScreen
import chromahub.rhythm.app.features.local.presentation.screens.onboarding.OnboardingStep
import chromahub.rhythm.app.features.local.presentation.screens.onboarding.PermissionScreenState

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val musicViewModel: MusicViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()
    private val appUpdaterViewModel: AppUpdaterViewModel by viewModels() // Inject AppUpdaterViewModel
    private lateinit var appSettings: AppSettings // Declare AppSettings
    
    companion object {
        const val DISPLAY_AUDIO_EFFECT_CONTROL_PANEL_REQUEST = 1002
    }
    
    // Track coroutine jobs to prevent memory leaks
    private val lifecycleScopeJobs = mutableListOf<kotlinx.coroutines.Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appSettings = AppSettings.getInstance(applicationContext) // Initialize AppSettings
        
        // Initialize NetworkClient with AppSettings for dynamic API keys
        chromahub.rhythm.app.network.NetworkClient.initialize(appSettings)

        // Initialize CrashReporter
        CrashReporter.init(application)
        
        // We'll delay intent handling until after initialization
        val startupIntent = intent
        
        setContent {
            val useSystemTheme by themeViewModel.useSystemTheme.collectAsState()
            val darkMode by themeViewModel.darkMode.collectAsState()
            val amoledTheme by appSettings.amoledTheme.collectAsState()
            val useDynamicColors by themeViewModel.useDynamicColors.collectAsState()
            val customColorScheme by appSettings.customColorScheme.collectAsState()
            val customFont by appSettings.customFont.collectAsState()
            val fontSource by appSettings.fontSource.collectAsState()
            val customFontPath by appSettings.customFontPath.collectAsState()
            val colorSource by appSettings.colorSource.collectAsState()
            val extractedAlbumColors by appSettings.extractedAlbumColors.collectAsState()
            
            // Determine the theme based on settings
            val isDarkTheme = if (useSystemTheme) {
                // Use system default
                androidx.compose.foundation.isSystemInDarkTheme()
            } else {
                // Use app setting
                darkMode
            }
            
            RhythmTheme(
                darkTheme = isDarkTheme,
                amoledTheme = amoledTheme && isDarkTheme,
                // Use dynamic colors (Monet) when system theme is enabled
                dynamicColor = useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                customColorScheme = customColorScheme,
                customFont = customFont,
                fontSource = fontSource,
                customFontPath = customFontPath,
                colorSource = colorSource,
                extractedAlbumColorsJson = extractedAlbumColors
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FestiveOverlayFromSettings {
                        // Show splash screen first, then transition to the app
                        var showSplash by rememberSaveable { mutableStateOf(true) }
                        val hasShownBetaPopup by appSettings.hasShownBetaPopup.collectAsState()
                        var showBetaPopup by remember { mutableStateOf(false) }
                        val currentAppVersion by appUpdaterViewModel.currentVersion.collectAsState() // Observe current version
                        val updateChannel by appUpdaterViewModel.updateChannel.collectAsState() // Observe update channel

                    // State for permission handling and app initialization
                    var shouldShowSettingsRedirect by remember { mutableStateOf(false) }
                    var isLoading by remember { mutableStateOf(true) } // Start as true to show loading during splash/initial checks
                    var isInitializingApp by remember { mutableStateOf(false) }
                    val lastCrashLog by appSettings.lastCrashLog.collectAsState() // Observe last crash log

                    // Handle splash screen completion and post-init tasks
                    LaunchedEffect(Unit) {
                        // Wait for splash screen to complete (it will call onMediaScanComplete when ready)
                        // This LaunchedEffect will handle post-splash tasks like beta popup and intent handling
                    }
                    
                    // Function to handle splash completion
                    fun onSplashComplete() {
                        showSplash = false
                        isLoading = false // Stop initial loading after splash

                        // Show beta popup if it hasn't been shown before AND the current version is a pre-release
                        if (!hasShownBetaPopup && currentAppVersion.isPreRelease) {
                            showBetaPopup = true
                        }

                        // Check for previous crash logs
                        lastCrashLog?.let {
                            // CrashActivity is now responsible for showing the dialog
                        }
                        
                        // Handle intent after splash screen disappears and app is initialized
                        if (startupIntent?.action == Intent.ACTION_VIEW && startupIntent.data != null) {
                            // Small delay to ensure view models are ready, then handle intent
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                kotlinx.coroutines.delay(500)
                                handleIntent(startupIntent)
                            }
                        }
                    }
                    
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedVisibility(
                            visible = !showSplash,
                            enter = fadeIn(animationSpec = tween(1000, easing = androidx.compose.animation.core.EaseOutCubic)) + 
                                   scaleIn(initialScale = 0.92f, animationSpec = tween(1000, easing = androidx.compose.animation.core.EaseOutCubic)),
                        ) {
                            PermissionHandler(
                                onPermissionsGranted = {
                                    // RhythmNavigation handles mode switching between Local and Streaming
                                    RhythmNavigation(
                                        musicViewModel = musicViewModel,
                                        themeViewModel = themeViewModel
                                    )
                                },
                                themeViewModel = themeViewModel,
                                appSettings = appSettings,
                                isLoading = isLoading,
                                isInitializingApp = isInitializingApp,
                                onSetIsLoading = { isLoading = it },
                                onSetIsInitializingApp = { isInitializingApp = it },
                                musicViewModel = musicViewModel
                            )
                        }
                        
                        AnimatedVisibility(
                            visible = showSplash,
                            exit = fadeOut(animationSpec = tween(1000, easing = androidx.compose.animation.core.EaseInCubic))
                        ) {
                            SplashScreen(
                                musicViewModel = musicViewModel,
                                onMediaScanComplete = { onSplashComplete() }
                            )
                        }

                        // Beta Program Popup
                        AnimatedVisibility(
                            visible = showBetaPopup,
                            enter = fadeIn() + scaleIn(initialScale = 0.8f),
                            exit = fadeOut() + scaleOut(targetScale = 0.8f)
                        ) {
                            BetaProgramPopup(onDismiss = {
                                showBetaPopup = false
                                appSettings.setHasShownBetaPopup(true)
                            })
                        }
                    }
                    }
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Handle the new intent
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        
        Log.d(TAG, "Handling intent: ${intent.action}, data: ${intent.data}")
        
        try {
            // Check if we should open player (coming from ExternalPlaybackActivity)
            if (intent.getBooleanExtra("OPEN_PLAYER", false)) {
                Log.d(TAG, "Opening player from external playback activity")
                // The player should automatically show since the song is already playing
                // No additional action needed as the navigation will handle it
                return
            }
            
            when (intent.action) {
                Intent.ACTION_VIEW -> {
                    // Handle external audio file with validation
                    intent.data?.let { uri ->
                        Log.d(TAG, "Received ACTION_VIEW intent with URI: $uri")
                        
                        if (isValidAndSafeUri(uri)) {
                            handleExternalAudioFile(uri)
                        } else {
                            Log.w(TAG, "Invalid or unsafe URI rejected: $uri")
                            val errorMsg = when {
                                uri.scheme == null -> "Invalid file format"
                                uri.scheme !in listOf("content", "file", "android.resource") -> "Unsupported file location type"
                                else -> "Cannot access file location"
                            }
                            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Log.w(TAG, "ACTION_VIEW intent received without data")
                    }
                }
                else -> {
                    Log.d(TAG, "Unhandled intent action: ${intent.action}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling intent", e)
            Toast.makeText(this, "Error opening file", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun isValidAndSafeUri(uri: Uri): Boolean {
        return try {
            // Validate URI scheme - be more permissive
            val scheme = uri.scheme?.lowercase()
            if (scheme != "content" && scheme != "file" && scheme != "android.resource") {
                Log.w(TAG, "Unsupported URI scheme: $scheme for URI: $uri")
                return false
            }
            
            // For content URIs, be more permissive with authorities
            if (scheme == "content") {
                val authority = uri.authority
                Log.d(TAG, "Content URI authority: $authority")
                
                // Allow more authorities, including third-party file managers
                val suspiciousAuthorities = setOf(
                    "com.malicious.app",
                    "suspicious.authority"
                )
                
                if (authority != null && suspiciousAuthorities.any { authority.contains(it) }) {
                    Log.w(TAG, "Potentially malicious content authority: $authority")
                    return false
                }
            }
            
            // Validate file path for file URIs - be more permissive
            if (scheme == "file") {
                val path = uri.path
                if (path == null) {
                    Log.w(TAG, "File URI with null path")
                    return false
                }
                
                try {
                    // Check for path traversal attempts but be less strict
                    val file = java.io.File(path)
                    if (!file.exists()) {
                        Log.w(TAG, "File does not exist: $path")
                        return false
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error checking file existence: $path", e)
                    return false
                }
            }
            
            // Try to access the URI to verify it exists and is readable
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    // URI is accessible, try reading a few bytes to ensure it's valid
                    val buffer = ByteArray(8)
                    inputStream.read(buffer)
                    Log.d(TAG, "URI is accessible and readable: $uri")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Cannot read from URI: $uri", e)
                return false
            }
            
            true
        } catch (e: SecurityException) {
            Log.w(TAG, "Security exception accessing URI: $uri", e)
            false
        } catch (e: Exception) {
            Log.w(TAG, "Error validating URI: $uri", e)
            false
        }
    }
    
    private fun handleExternalAudioFile(uri: Uri) {
        Log.d(TAG, "Handling external audio file: $uri")
        
        // Validate URI and check if it's an audio file
        if (!isValidAudioUri(uri)) {
            Log.e(TAG, "Invalid or unsupported audio file: $uri")
            Toast.makeText(applicationContext, "Unsupported file format", Toast.LENGTH_SHORT).show()
            return
        }
        
        val mimeType = MediaUtils.getMimeType(applicationContext, uri)
        Log.d(TAG, "File is recognized as audio with mime type: $mimeType")
        
        // Extract metadata from the audio file with proper error handling
        val job = lifecycleScope.launch {
            try {
                // Start the service with proper initialization waiting
                val serviceStarted = startMediaServiceAndWait()
                if (!serviceStarted) {
                    Log.e(TAG, "Failed to start media service")
                    Toast.makeText(applicationContext, "Failed to initialize media player", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Extract metadata on a background thread
                val song = withContext(Dispatchers.IO) {
                    MediaUtils.extractMetadataFromUri(applicationContext, uri)
                }
                
                Log.d(TAG, "Extracted song metadata: ${song.title} by ${song.artist} from ${song.album}")
                
                // Ensure service connection with timeout
                val serviceConnected = waitForServiceConnection(timeoutMs = 5000)
                if (!serviceConnected) {
                    Log.w(TAG, "Service connection timeout, attempting fallback")
                    fallbackPlayExternalFile(uri)
                    return@launch
                }
                
                // Play the external file
                musicViewModel.playExternalAudioFile(song)
                
                // Verify playback started with timeout
                val playbackStarted = waitForPlaybackStart(timeoutMs = 3000)
                if (!playbackStarted) {
                    Log.w(TAG, "Playback didn't start, using fallback method")
                    fallbackPlayExternalFile(uri)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing external audio file", e)
                val errorMessage = when (e) {
                    is SecurityException -> "Permission denied accessing file"
                    is IllegalArgumentException -> "Invalid audio file format"
                    else -> "Error playing audio file: ${e.message}"
                }
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        
        // Track the job for cleanup
        lifecycleScopeJobs.add(job)
    }
    
    private fun isValidAudioUri(uri: Uri): Boolean {
        return try {
            val mimeType = MediaUtils.getMimeType(applicationContext, uri)
            mimeType?.startsWith("audio/") == true || 
            uri.toString().let { uriStr ->
                uriStr.endsWith(".mp3", ignoreCase = true) ||
                uriStr.endsWith(".m4a", ignoreCase = true) ||
                uriStr.endsWith(".alac", ignoreCase = true) ||
                uriStr.endsWith(".wav", ignoreCase = true) ||
                uriStr.endsWith(".ogg", ignoreCase = true) ||
                uriStr.endsWith(".flac", ignoreCase = true) ||
                uriStr.endsWith(".aac", ignoreCase = true) ||
                uriStr.endsWith(".opus", ignoreCase = true) ||
                uriStr.endsWith(".wma", ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error validating URI: $uri", e)
            false
        }
    }
    
    private suspend fun startMediaServiceAndWait(): Boolean {
        return try {
            val serviceIntent = Intent(this, chromahub.rhythm.app.infrastructure.service.MediaPlaybackService::class.java)
            serviceIntent.action = chromahub.rhythm.app.infrastructure.service.MediaPlaybackService.ACTION_INIT_SERVICE
            
            // Media3's MediaLibraryService handles foreground state automatically in onCreate()
            // so startService() is sufficient for all SDK versions
            startService(serviceIntent)
            
            // Wait for service to be ready
            delay(1000)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start media service", e)
            false
        }
    }
    
    private suspend fun waitForServiceConnection(timeoutMs: Long): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (musicViewModel.isServiceConnected()) {
                return true
            }
            if (!musicViewModel.isServiceConnected()) {
                musicViewModel.connectToMediaService()
            }
            delay(100)
        }
        return false
    }
    
    private suspend fun waitForPlaybackStart(timeoutMs: Long): Boolean {
        val startTime = System.currentTimeMillis()
        delay(500) // Initial delay to let playback initialize
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (musicViewModel.isPlaying()) {
                return true
            }
            delay(100)
        }
        return false
    }
    
    private suspend fun fallbackPlayExternalFile(uri: Uri) {
        try {
            Log.d(TAG, "Using direct service intent as fallback")
            val playIntent = Intent(applicationContext, chromahub.rhythm.app.infrastructure.service.MediaPlaybackService::class.java)
            playIntent.action = chromahub.rhythm.app.infrastructure.service.MediaPlaybackService.ACTION_PLAY_EXTERNAL_FILE
            playIntent.data = uri
            
            // Media3's MediaLibraryService handles foreground state automatically
            startService(playIntent)
            
            // Give fallback some time to start
            delay(1000)
            if (!musicViewModel.isPlaying()) {
                Toast.makeText(applicationContext, "Unable to play audio file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fallback playback also failed", e)
            Toast.makeText(applicationContext, "Failed to play audio file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Handle permission results for the explorer (requestCode 1001 is used by explorer)
        if (requestCode == 1001) {
            // The PermissionHandler will automatically handle permission state changes
            // through its LaunchedEffect watching permissionsState.allPermissionsGranted
            // So we don't need to do anything special here - the UI will refresh automatically
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "MainActivity onDestroy - cleaning up resources")
        
        // Cancel all tracked coroutine jobs to prevent memory leaks
        lifecycleScopeJobs.forEach { job ->
            if (job.isActive) {
                job.cancel()
                Log.d(TAG, "Cancelled pending job: $job")
            }
        }
        lifecycleScopeJobs.clear()
        
        // Perform cache cleanup if enabled
        lifecycleScope.launch {
            try {
                // Get MusicRepository instance from the ViewModel to clear in-memory caches
                val musicRepository = musicViewModel.getMusicRepository()
                appSettings.performCacheCleanupOnExit(applicationContext, musicRepository)
            } catch (e: Exception) {
                Log.e(TAG, "Error during cache cleanup on app destroy", e)
            }
        }
        
        super.onDestroy()
    }
    
    // Helper function to get step name for accessibility
    private fun getStepName(step: OnboardingStep): String {
        return when (step) {
            OnboardingStep.WELCOME -> "Welcome"
            OnboardingStep.PERMISSIONS -> "Permissions"
            OnboardingStep.NOTIFICATIONS -> "Notifications"
            OnboardingStep.BACKUP_RESTORE -> "Backup & Restore"
            OnboardingStep.AUDIO_PLAYBACK -> "Audio & Playback"
            OnboardingStep.THEMING -> "Theming"
            OnboardingStep.GESTURES -> "Gestures"
            OnboardingStep.LIBRARY_SETUP -> "Library Setup"
            OnboardingStep.MEDIA_SCAN -> "Media Scan"
            OnboardingStep.WIDGETS -> "Widgets"
            OnboardingStep.INTEGRATIONS -> "Integrations"
            OnboardingStep.RHYTHM_STATS -> "Rhythm Stats"
            OnboardingStep.UPDATER -> "Updates"
            OnboardingStep.SETUP_FINISHED -> "Setup Finished"
            OnboardingStep.COMPLETE -> "Complete"
        }
}}
