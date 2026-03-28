package chromahub.rhythm.app.features.local.presentation.screens

import android.Manifest
import android.os.Build
import android.provider.Settings
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwipeVertical
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.Swipe
import androidx.compose.material.icons.rounded.SwipeDown
import androidx.compose.material.icons.rounded.SwipeLeft
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.data.model.AlbumViewType
import chromahub.rhythm.app.shared.data.model.ArtistViewType
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.presentation.components.common.DataProcessingLoader
import chromahub.rhythm.app.shared.presentation.components.common.InitializationLoader
import chromahub.rhythm.app.shared.presentation.components.common.NetworkOperationLoader
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveButtonGroup
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.features.local.presentation.components.settings.LanguageSwitcherDialog
import chromahub.rhythm.app.features.local.presentation.components.settings.LibraryTabOrderBottomSheet
import chromahub.rhythm.app.features.local.presentation.screens.onboarding.OnboardingStep
import chromahub.rhythm.app.features.local.presentation.screens.onboarding.PermissionScreenState
import chromahub.rhythm.app.shared.presentation.viewmodel.AppUpdaterViewModel
import chromahub.rhythm.app.shared.presentation.viewmodel.AppVersion
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import chromahub.rhythm.app.shared.presentation.viewmodel.ThemeViewModel
import chromahub.rhythm.app.util.HapticUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import java.util.Locale
import kotlin.math.absoluteValue
import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun OnboardingScreen(
    currentStep: OnboardingStep,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onContinueFullTour: () -> Unit,
    onSkipFullTour: () -> Unit,
    onRequestAgain: () -> Unit,
    permissionScreenState: PermissionScreenState,
    isParentLoading: Boolean,
    themeViewModel: ThemeViewModel,
    appSettings: AppSettings,
    musicViewModel: MusicViewModel,
    updaterViewModel: AppUpdaterViewModel = viewModel(),
    onFinish: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    // Bottom sheet states
    var showLibraryTabOrderBottomSheet by remember { mutableStateOf(false) }

    // Responsive sizing
    val isTablet = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium || windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val contentMaxWidth = if (isTablet) 600.dp else androidx.compose.ui.unit.Dp.Infinity
    val horizontalPadding = if (isTablet) 40.dp else 20.dp
    val cardPadding = if (isTablet) 32.dp else 20.dp

    // Notification and legacy library setup are removed from the visible flow.
    val stepIndex = when (currentStep) {
        OnboardingStep.WELCOME -> 0
        OnboardingStep.PERMISSIONS -> 1
        OnboardingStep.RHYTHM_GUARD -> 2
        OnboardingStep.MEDIA_SCAN -> 3
        OnboardingStep.UPDATER -> 4
        OnboardingStep.FULL_TOUR_PROMPT -> 5
        OnboardingStep.NOTIFICATIONS -> 5
        OnboardingStep.BACKUP_RESTORE -> 6
        OnboardingStep.AUDIO_PLAYBACK -> 7
        OnboardingStep.THEMING -> 8
        OnboardingStep.GESTURES -> 9
        OnboardingStep.LIBRARY_SETUP -> 9
        OnboardingStep.WIDGETS -> 10
        OnboardingStep.INTEGRATIONS -> 11
        OnboardingStep.RHYTHM_STATS -> 12
        OnboardingStep.SETUP_FINISHED -> 13
        OnboardingStep.COMPLETE -> 14
    }

    val totalSteps = 14

    // Create pager state
    val pagerState = rememberPagerState(
        initialPage = stepIndex,
        pageCount = { totalSteps }
    )

    // Sync pager with step changes
    LaunchedEffect(stepIndex) {
        if (pagerState.currentPage != stepIndex) {
            val pageJump = (pagerState.currentPage - stepIndex).absoluteValue
            if (pageJump > 1) {
                pagerState.scrollToPage(stepIndex)
            } else {
                pagerState.animateScrollToPage(stepIndex)
            }
        }
    }

    // Sync step with pager changes.
    LaunchedEffect(pagerState.currentPage) {
        val newStep = when (pagerState.currentPage) {
            0 -> OnboardingStep.WELCOME
            1 -> OnboardingStep.PERMISSIONS
            2 -> OnboardingStep.RHYTHM_GUARD
            3 -> OnboardingStep.MEDIA_SCAN
            4 -> OnboardingStep.UPDATER
            5 -> OnboardingStep.FULL_TOUR_PROMPT
            6 -> OnboardingStep.BACKUP_RESTORE
            7 -> OnboardingStep.AUDIO_PLAYBACK
            8 -> OnboardingStep.THEMING
            9 -> OnboardingStep.GESTURES
            10 -> OnboardingStep.WIDGETS
            11 -> OnboardingStep.INTEGRATIONS
            12 -> OnboardingStep.RHYTHM_STATS
            13 -> OnboardingStep.SETUP_FINISHED
            else -> OnboardingStep.COMPLETE
        }
        if (newStep != currentStep && pagerState.currentPage < stepIndex) {
            onPrevStep()
        } else if (newStep != currentStep && pagerState.currentPage > stepIndex) {
            onNextStep()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .let { if (isTablet) it.width(contentMaxWidth) else it }
    ) {
        // Single onboarding card container for all pager content
        OnboardingCard(
            isTablet = isTablet,
            modifier = Modifier.weight(1f)
        ) {
            // HorizontalPager for smooth sliding animations
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = when {
                    currentStep != OnboardingStep.PERMISSIONS && currentStep != OnboardingStep.GESTURES -> true
                    permissionScreenState == PermissionScreenState.PermissionsGranted -> true
                    else -> true // Allow scrolling to let user review info before granting
                },
                modifier = Modifier.fillMaxSize(),
                key = { page -> page } // Add key to preserve page state
            ) { page ->
                val step = when (page) {
                    0 -> OnboardingStep.WELCOME
                    1 -> OnboardingStep.PERMISSIONS
                    2 -> OnboardingStep.RHYTHM_GUARD
                    3 -> OnboardingStep.MEDIA_SCAN
                    4 -> OnboardingStep.UPDATER
                    5 -> OnboardingStep.FULL_TOUR_PROMPT
                    6 -> OnboardingStep.BACKUP_RESTORE
                    7 -> OnboardingStep.AUDIO_PLAYBACK
                    8 -> OnboardingStep.THEMING
                    9 -> OnboardingStep.GESTURES
                    10 -> OnboardingStep.WIDGETS
                    11 -> OnboardingStep.INTEGRATIONS
                    12 -> OnboardingStep.RHYTHM_STATS
                    13 -> OnboardingStep.SETUP_FINISHED
                    else -> OnboardingStep.COMPLETE
                }
                // Container for step-specific content - positioned at top within pager page
                Box(
                    modifier = Modifier.fillMaxSize().padding(top=54.dp, start = horizontalPadding, end = horizontalPadding, bottom = cardPadding),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Use key to preserve composable state across recompositions
                    androidx.compose.runtime.key(step) {
                        when (step) {
                            OnboardingStep.WELCOME -> {
                                // Welcome screen without card
                                EnhancedWelcomeContent(onNextStep = onNextStep, isTablet = isTablet, contentMaxWidth = contentMaxWidth)
                            }
                            OnboardingStep.PERMISSIONS -> {
                                EnhancedPermissionContent(
                                    permissionScreenState = permissionScreenState,
                                    onGrantAccess = {
                                        onNextStep() // Trigger permission request
                                    },
                                    onOpenSettings = {
                                        val intent = android.content.Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                                        context.startActivity(intent)
                                        onRequestAgain() // Set loading state
                                    },
                                    isButtonLoading = isParentLoading,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                // For permission step, handle based on state
                                                when (permissionScreenState) {
                                                    PermissionScreenState.RedirectToSettings -> {
                                                        val intent = android.content.Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                        intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                                                        context.startActivity(intent)
                                                        onRequestAgain()
                                                    }
                                                    PermissionScreenState.PermissionsGranted -> onNextStep()
                                                    PermissionScreenState.Loading -> { /* Do nothing while loading */ }
                                                    else -> onNextStep() // Trigger permission request
                                                }
                                            },
                                            enabled = !isParentLoading && permissionScreenState != PermissionScreenState.Loading,
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = when (permissionScreenState) {
                                                    PermissionScreenState.PermissionsGranted -> MaterialTheme.colorScheme.primary
                                                    PermissionScreenState.RedirectToSettings -> MaterialTheme.colorScheme.error
                                                    else -> MaterialTheme.colorScheme.primary
                                                },
                                                contentColor = when (permissionScreenState) {
                                                    PermissionScreenState.PermissionsGranted -> MaterialTheme.colorScheme.onPrimary
                                                    PermissionScreenState.RedirectToSettings -> MaterialTheme.colorScheme.onError
                                                    else -> MaterialTheme.colorScheme.onPrimary
                                                }
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Crossfade(
                                                targetState = isParentLoading,
                                                animationSpec = tween(300),
                                                label = "buttonContent"
                                            ) { loading ->
                                                if (loading) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        DataProcessingLoader(
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            context.getString(R.string.onboarding_checking),
                                                            style = MaterialTheme.typography.labelLarge
                                                        )
                                                    }
                                                } else {
                                                    Row(
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        val buttonText = when (permissionScreenState) {
                                                            PermissionScreenState.PermissionsGranted -> context.getString(R.string.onboarding_continue)
                                                            PermissionScreenState.RedirectToSettings -> context.getString(R.string.onboarding_open_settings)
                                                            else -> context.getString(R.string.onboarding_grant_access)
                                                        }
                                                        val buttonIcon = when (permissionScreenState) {
                                                            PermissionScreenState.PermissionsGranted -> Icons.AutoMirrored.Filled.ArrowForward
                                                            PermissionScreenState.RedirectToSettings -> Icons.Filled.Security
                                                            else -> Icons.Filled.Security
                                                        }

                                                        Text(
                                                            buttonText,
                                                            style = MaterialTheme.typography.labelLarge.copy(
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Icon(
                                                            imageVector = buttonIcon,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                            OnboardingStep.RHYTHM_GUARD -> {
                                EnhancedRhythmGuardContent(
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.BACKUP_RESTORE -> {
                                EnhancedBackupRestoreContent(
                                    onNextStep = onNextStep,
                                    onSkip = onNextStep,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.AUDIO_PLAYBACK -> {
                                EnhancedAudioPlaybackContent(
                                    onNextStep = onNextStep,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.THEMING -> {
                                EnhancedThemingContent(
                                    onNextStep = onNextStep,
                                    onSkip = onNextStep,
                                    themeViewModel = themeViewModel,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.GESTURES -> {
                                EnhancedGesturesContent(
                                    onNextStep = onNextStep,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            // LIBRARY_SETUP step skipped
                            // OnboardingStep.LIBRARY_SETUP -> { ... }
                            OnboardingStep.MEDIA_SCAN -> {
                                EnhancedMediaScanContent(
                                    onNextStep = onNextStep,
                                    onSkip = onNextStep,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.WIDGETS -> {
                                EnhancedWidgetsContent(
                                    onNextStep = onNextStep,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.INTEGRATIONS -> {
                                EnhancedIntegrationsContent(
                                    onNextStep = onNextStep,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.RHYTHM_STATS -> {
                                EnhancedRhythmStatsContent(
                                    onNextStep = onNextStep,
                                    appSettings = appSettings,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.UPDATER -> {
                                EnhancedUpdaterContent(
                                    onNextStep = onNextStep,
                                    appSettings = appSettings,
                                    updaterViewModel = updaterViewModel,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onNextStep()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_next),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.FULL_TOUR_PROMPT -> {
                                EnhancedFullTourPromptContent(
                                    onContinueFullTour = onContinueFullTour,
                                    onSkipFullTour = onSkipFullTour,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null
                                )
                            }
                            OnboardingStep.SETUP_FINISHED -> {
                                EnhancedSetupFinishedContent(
                                    onFinish = onFinish,
                                    isTablet = isTablet,
                                    backButton = if (stepIndex > 0) {
                                        {
                                            val buttonScale = remember { Animatable(1f) }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                                        buttonScale.animateTo(1f, animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessHigh
                                                        ))
                                                    }
                                                    onPrevStep()
                                                },
                                                modifier = Modifier
                                                    .height(56.dp)
                                                    .graphicsLayer {
                                                        scaleX = buttonScale.value
                                                        scaleY = buttonScale.value
                                                    },
                                                shape = RoundedCornerShape(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    } else null,
                                    nextButton = {
                                        val nextButtonScale = remember { Animatable(1f) }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                                    nextButtonScale.animateTo(1f, animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessHigh
                                                    ))
                                                }
                                                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                                                onFinish()
                                            },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .graphicsLayer {
                                                    scaleX = nextButtonScale.value
                                                    scaleY = nextButtonScale.value
                                                },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            Text(
                                                context.getString(R.string.onboarding_finish_setup),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                )
                            }
                            OnboardingStep.COMPLETE -> {
                                // This should not be visible as we transition to the main app
                                Box(modifier = Modifier.fillMaxSize())
                            }
                            OnboardingStep.NOTIFICATIONS,
                            OnboardingStep.LIBRARY_SETUP -> {
                                // Legacy steps not shown in the current onboarding flow.
                                Box(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }

        // Bottom navigation bar
        AnimatedVisibility(
            visible = currentStep != OnboardingStep.WELCOME && currentStep != OnboardingStep.FULL_TOUR_PROMPT && !isTablet,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = if (isTablet) 48.dp else 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button with spring animation
                    AnimatedVisibility(
                        visible = stepIndex > 0,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + expandHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                        exit = fadeOut(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + shrinkHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    ) {
                        val buttonScale = remember { Animatable(1f) }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    buttonScale.animateTo(0.92f, animationSpec = tween(100))
                                    buttonScale.animateTo(1f, animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessHigh
                                    ))
                                }
                                onPrevStep()
                            },
                            modifier = Modifier
                                .height(if (isTablet) 56.dp else 48.dp)
                                .graphicsLayer {
                                    scaleX = buttonScale.value
                                    scaleY = buttonScale.value
                                },
                            shape = RoundedCornerShape(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(context.getString(R.string.onboarding_back), style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    // App logo and step count - centered between back and next buttons
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rhythm_splash_logo),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        androidx.compose.animation.AnimatedContent(
                            targetState = stepIndex,
                            transitionSpec = {
                                (slideInVertically { height -> height / 2 } + fadeIn()).togetherWith(
                                    slideOutVertically { height -> -height / 2 } + fadeOut()
                                )
                            },
                            modifier = Modifier.padding(top = 4.dp),
                            label = "progressText"
                        ) { step ->
                            Text(
                                text = context.getString(R.string.onboarding_step_progress, step + 1, totalSteps),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Next/Finish button with spring animation
                    val nextButtonScale = remember { Animatable(1f) }

                    Button(
                        onClick = {
                            scope.launch {
                                nextButtonScale.animateTo(0.92f, animationSpec = tween(100))
                                nextButtonScale.animateTo(1f, animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                ))
                            }
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                            when (currentStep) {
                                OnboardingStep.PERMISSIONS -> {
                                    // For permission step, handle based on state
                                    when (permissionScreenState) {
                                        PermissionScreenState.RedirectToSettings -> {
                                            val intent = android.content.Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                                            context.startActivity(intent)
                                            onRequestAgain()
                                        }
                                        PermissionScreenState.PermissionsGranted -> onNextStep()
                                        PermissionScreenState.Loading -> { /* Do nothing while loading */ }
                                        else -> onNextStep() // Trigger permission request
                                    }
                                }
                                else -> onNextStep() // All other steps just go next
                            }
                        },
                        enabled = when (currentStep) {
                            OnboardingStep.PERMISSIONS -> !isParentLoading && permissionScreenState != PermissionScreenState.Loading
                            else -> true
                        },
                        modifier = Modifier
                            .height(if (isTablet) 56.dp else 48.dp)
                            .graphicsLayer {
                                scaleX = nextButtonScale.value
                                scaleY = nextButtonScale.value
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (currentStep) {
                                OnboardingStep.PERMISSIONS -> when (permissionScreenState) {
                                    PermissionScreenState.PermissionsGranted -> MaterialTheme.colorScheme.primary
                                    PermissionScreenState.RedirectToSettings -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.primary
                                }
                                else -> MaterialTheme.colorScheme.primary
                            },
                            contentColor = when (currentStep) {
                                OnboardingStep.PERMISSIONS -> when (permissionScreenState) {
                                    PermissionScreenState.PermissionsGranted -> MaterialTheme.colorScheme.onPrimary
                                    PermissionScreenState.RedirectToSettings -> MaterialTheme.colorScheme.onError
                                    else -> MaterialTheme.colorScheme.onPrimary
                                }
                                else -> MaterialTheme.colorScheme.onPrimary
                            }
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Crossfade(
                            targetState = currentStep == OnboardingStep.PERMISSIONS && isParentLoading,
                            animationSpec = tween(300),
                            label = "buttonContent"
                        ) { loading ->
                            if (loading) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DataProcessingLoader(
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        context.getString(R.string.onboarding_checking),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val buttonText = when {
                                        currentStep == OnboardingStep.SETUP_FINISHED -> context.getString(R.string.onboarding_lets_go)
                                        currentStep == OnboardingStep.PERMISSIONS -> when (permissionScreenState) {
                                            PermissionScreenState.PermissionsGranted -> context.getString(R.string.onboarding_continue)
                                            PermissionScreenState.RedirectToSettings -> context.getString(R.string.onboarding_open_settings)
                                            else -> context.getString(R.string.onboarding_grant_access)
                                        }
                                        else -> context.getString(R.string.onboarding_next)
                                    }
                                    val buttonIcon = when {
                                        currentStep == OnboardingStep.SETUP_FINISHED -> Icons.Filled.Check
                                        currentStep == OnboardingStep.PERMISSIONS -> when (permissionScreenState) {
                                            PermissionScreenState.PermissionsGranted -> Icons.AutoMirrored.Filled.ArrowForward
                                            PermissionScreenState.RedirectToSettings -> Icons.Filled.Security
                                            else -> Icons.Filled.Security
                                        }
                                        else -> Icons.AutoMirrored.Filled.ArrowForward
                                    }

                                    Text(
                                        buttonText,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = buttonIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom sheets for advanced configuration
    if (showLibraryTabOrderBottomSheet) {
        LibraryTabOrderBottomSheet(
            onDismiss = { showLibraryTabOrderBottomSheet = false },
            appSettings = appSettings,
            haptics = haptic
        )
    }
}

/**
 * Unified card container for all onboarding steps (except welcome)
 * Provides consistent Material You styling with rounded corners and elevated surface
 */
@Composable
private fun OnboardingCard(
    isTablet: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val contentMaxWidth = 600.dp
    val cardPadding = if (isTablet) 20.dp else 18.dp

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = if (isTablet) 4.dp else 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .let { if (isTablet) it.width(contentMaxWidth) else it }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        // Remove vertical scroll since we're not constraining height anymore
        // and let pager handle its own sizing and scrolling behavior
        Column(
            modifier = Modifier.padding(cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun EnhancedWelcomeContent(onNextStep: () -> Unit, isTablet: Boolean = false, contentMaxWidth: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Infinity) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showLanguageSwitcher by remember { mutableStateOf(false) }

    // Animated scale for logo
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_animations")
    val logoGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .let { if (isTablet) it.width(contentMaxWidth) else it },
        contentAlignment = Alignment.Center
    ) {
        // Language switcher button at top-right
        FilledTonalButton(
            onClick = { showLanguageSwitcher = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "Change Language",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = context.getString(R.string.onboarding_language),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Logo and App Name - Adaptive Layout
            if (isTablet) {
                // Tablet: Logo and name on the same line
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // App logo with glowing effect
                    AnimatedVisibility(
                        visible = true,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(
                            animationSpec = tween(1000)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rhythm_splash_logo),
                                contentDescription = "Rhythm Logo",
                                modifier = Modifier.size(140.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // App name with staggered animation
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                initialOffsetY = { it / 2 }
                            ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
                        ) {
                            Text(
                                text = context.getString(R.string.onboarding_welcome_title),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 64.sp,
                                    letterSpacing = 0.8.sp
                                ),
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Subtitle with modern styling and delayed animation
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                initialOffsetY = { it / 2 }
                            ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
                        ) {
                            Text(
                                text = context.getString(R.string.onboarding_welcome_subtitle),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    letterSpacing = 0.4.sp
                                ),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                // Phone: Vertical layout (original)
                // App logo with glowing effect
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(1000)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rhythm_splash_logo),
                            contentDescription = "Rhythm Logo",
                            modifier = Modifier.size(130.dp)
                        )
                    }
                }

                // App name with staggered animation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { it / 2 }
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
                    ) {
                        Text(
                            text = context.getString(R.string.onboarding_welcome_title),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 48.sp,
                                letterSpacing = 0.8.sp
                            ),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Subtitle with modern styling and delayed animation
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { it / 2 }
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
                    ) {
                        Text(
                            text = context.getString(R.string.onboarding_welcome_subtitle),
                            style = MaterialTheme.typography.titleSmall.copy(
                                letterSpacing = 0.4.sp
                            ),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 34.dp))

            // Minimal description with better typography and animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 600)) +
                        slideInVertically(
                            animationSpec = tween(800, delayMillis = 600),
                            initialOffsetY = { it / 3 }
                        )
            ) {
                Text(
                    text = context.getString(R.string.onboarding_welcome_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = if (isTablet) 16.sp else 12.sp,
                        lineHeight = if (isTablet) 26.sp else 22.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = if (isTablet) 64.dp else 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isTablet) 56.dp else 44.dp))

            // Enhanced Get Started button with modern design and animation
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(800, delayMillis = 800))
            ) {
                Button(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                        onNextStep()
                    },
                    modifier = Modifier
                        .then(
                            if (isTablet) Modifier.width(500.dp) else Modifier.fillMaxWidth()
                        )
                        .height(if (isTablet) 72.dp else 68.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(if (isTablet) 28.dp else 24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animateContentSize()
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Play,
                            contentDescription = null,
                            modifier = Modifier.size(if (isTablet) 32.dp else 28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = context.getString(R.string.onboarding_get_started),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = if (isTablet) 22.sp else 20.sp
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Feature highlights with animated appearance
//            AnimatedVisibility(
//                visible = true,
//                enter = fadeIn(animationSpec = tween(800, delayMillis = 1000)) +
//                        expandVertically(animationSpec = tween(600, delayMillis = 1000))
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 12.dp)
//                ) {
//                    WelcomeFeatureChip(
//                        icon = Icons.Filled.MusicNote,
//                        text = context.getString(R.string.onboarding_offline)
//                    )
//                    WelcomeFeatureChip(
//                        icon = Icons.Filled.Palette,
//                        text = context.getString(R.string.onboarding_customizable)
//                    )
//                    WelcomeFeatureChip(
//                        icon = Icons.Filled.Security,
//                        text = context.getString(R.string.onboarding_private)
//                    )
//                }
//            }

            Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 24.dp))

            // Version info or subtle additional info with animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 1200))
            ) {
                Text(
                    text = context.getString(R.string.onboarding_tagline),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        letterSpacing = 0.3.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Language switcher dialog
        if (showLanguageSwitcher) {
            LanguageSwitcherDialog(
                onDismiss = { showLanguageSwitcher = false }
            )
        }
    }
}

@Composable
private fun WelcomeFeatureChip(
    icon: ImageVector,
    text: String
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EnhancedPermissionContent(
    permissionScreenState: PermissionScreenState,
    onGrantAccess: () -> Unit,
    onOpenSettings: () -> Unit,
    isButtonLoading: Boolean,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Define permissions based on Android version within the composable
    val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val notificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyList()
    }

    val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    val essentialPermissions = storagePermissions + bluetoothPermissions + notificationPermissions
    val permissionsState = rememberMultiplePermissionsState(essentialPermissions)
    val scrollState = rememberScrollState()

    if (isTablet) {
        // Tablet layout: Left side - icon, description, permission tips; Right side - permission cards and Android 13 notice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, description, and permission tips
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced icon with dynamic state
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (permissionScreenState) {
                                PermissionScreenState.PermissionsGranted -> Icons.Filled.Check
                                PermissionScreenState.RedirectToSettings -> Icons.Filled.Security
                                else -> Icons.Filled.Security
                            },
                            contentDescription = "Permissions",
                            tint = when (permissionScreenState) {
                                PermissionScreenState.PermissionsGranted -> MaterialTheme.colorScheme.primary
                                PermissionScreenState.RedirectToSettings -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = when (permissionScreenState) {
                        PermissionScreenState.PermissionsGranted -> context.getString(R.string.onboarding_permissions_granted_title)
                        PermissionScreenState.RedirectToSettings -> context.getString(R.string.onboarding_action_required_settings)
                        PermissionScreenState.ShowRationale -> context.getString(R.string.onboarding_permissions_needed)
                        else -> context.getString(R.string.onboarding_grant_permissions)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = when (permissionScreenState) {
                        PermissionScreenState.PermissionsGranted -> context.getString(R.string.onboarding_permissions_granted_desc)
                        PermissionScreenState.RedirectToSettings -> context.getString(R.string.onboarding_redirect_settings_desc)
                        PermissionScreenState.ShowRationale -> context.getString(R.string.onboarding_rationale_desc)
                        else -> context.getString(R.string.onboarding_permissions_required_desc)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Permission tips card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
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
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_permission_tips),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        PermissionTipItem(
                            icon = Icons.Filled.CheckCircle,
                            text = context.getString(R.string.onboarding_permission_tip_1)
                        )
                        PermissionTipItem(
                            icon = Icons.Filled.Settings,
                            text = context.getString(R.string.onboarding_permission_tip_2)
                        )
                        PermissionTipItem(
                            icon = Icons.Filled.Security,
                            text = context.getString(R.string.onboarding_permission_tip_3)
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Permission cards and Android 13 notice
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced permission explanation cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EnhancedPermissionCard(
                        icon = Icons.Filled.MusicNote,
                        title = context.getString(R.string.onboarding_permission_music_title),
                        description = context.getString(R.string.onboarding_permission_music_desc),
                        isGranted = storagePermissions.all { permission ->
                            permissionsState.permissions.find { it.permission == permission }?.status?.isGranted == true
                        }
                    )

                    EnhancedPermissionCard(
                        icon = RhythmIcons.Devices.Bluetooth,
                        title = context.getString(R.string.onboarding_permission_bluetooth_title),
                        description = context.getString(R.string.onboarding_permission_bluetooth_desc),
                        isGranted = bluetoothPermissions.all { permission ->
                            permissionsState.permissions.find { it.permission == permission }?.status?.isGranted == true
                        }
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        EnhancedPermissionCard(
                            icon = RhythmIcons.Notifications,
                            title = context.getString(R.string.onboarding_permission_notifications_title),
                            description = context.getString(R.string.onboarding_permission_notifications_desc),
                            isGranted = notificationPermissions.all { permission ->
                                permissionsState.permissions.find { it.permission == permission }?.status?.isGranted == true
                            }
                        )
                    }
                }

                // Android 13 permission notice
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permissionScreenState == PermissionScreenState.PermissionsRequired) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.BugReport,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = context.getString(R.string.onboarding_android13_notice),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = context.getString(R.string.onboarding_android13_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Enhanced icon with dynamic state
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (permissionScreenState) {
                            PermissionScreenState.PermissionsGranted -> Icons.Filled.Check
                            PermissionScreenState.RedirectToSettings -> Icons.Filled.Security
                            else -> Icons.Filled.Security
                        },
                        contentDescription = "Permissions",
                        tint = when (permissionScreenState) {
                            PermissionScreenState.PermissionsGranted -> MaterialTheme.colorScheme.primary
                            PermissionScreenState.RedirectToSettings -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when (permissionScreenState) {
                    PermissionScreenState.PermissionsGranted -> context.getString(R.string.onboarding_permissions_granted_title)
                    PermissionScreenState.RedirectToSettings -> context.getString(R.string.onboarding_action_required_settings)
                    PermissionScreenState.ShowRationale -> context.getString(R.string.onboarding_permissions_needed)
                    else -> context.getString(R.string.onboarding_grant_permissions)
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = when (permissionScreenState) {
                    PermissionScreenState.PermissionsGranted -> context.getString(R.string.onboarding_permissions_granted_desc)
                    PermissionScreenState.RedirectToSettings -> context.getString(R.string.onboarding_redirect_settings_desc)
                    PermissionScreenState.ShowRationale -> context.getString(R.string.onboarding_rationale_desc)
                    else -> context.getString(R.string.onboarding_permissions_required_desc)
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) 16.dp else 32.dp)
            )

            // Android 13+ permission notice
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permissionScreenState == PermissionScreenState.PermissionsRequired) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BugReport,
                                contentDescription = null,
                                
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = context.getString(R.string.onboarding_android13_notice),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = context.getString(R.string.onboarding_android13_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Enhanced permission explanation cards
            Column(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EnhancedPermissionCard(
                    icon = Icons.Filled.MusicNote,
                    title = context.getString(R.string.onboarding_permission_music_title),
                    description = context.getString(R.string.onboarding_permission_music_desc),
                    isGranted = storagePermissions.all { permission ->
                        permissionsState.permissions.find { it.permission == permission }?.status?.isGranted == true
                    }
                )

                EnhancedPermissionCard(
                    icon = RhythmIcons.Devices.Bluetooth,
                    title = context.getString(R.string.onboarding_permission_bluetooth_title),
                    description = context.getString(R.string.onboarding_permission_bluetooth_desc),
                    isGranted = bluetoothPermissions.all { permission ->
                        permissionsState.permissions.find { it.permission == permission }?.status?.isGranted == true
                    }
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    EnhancedPermissionCard(
                        icon = RhythmIcons.Notifications,
                        title = context.getString(R.string.onboarding_permission_notifications_title),
                        description = context.getString(R.string.onboarding_permission_notifications_desc),
                        isGranted = notificationPermissions.all { permission ->
                            permissionsState.permissions.find { it.permission == permission }?.status?.isGranted == true
                        }
                    )
                }
            }

            // Button removed - now handled by bottom navigation bar
        }
    }
}

@Composable
fun EnhancedPermissionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isGranted: Boolean = false
) {
    val context = LocalContext.current
    // Animated state changes
    val containerColor by animateColorAsState(
        targetValue = if (isGranted)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "containerColor"
    )

    val iconBackgroundColor by animateColorAsState(
        targetValue = if (isGranted)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "iconBackgroundColor"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isGranted)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "iconTint"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = if (isGranted)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                // Crossfade between icon and checkmark
                Crossfade(
                    targetState = isGranted,
                    animationSpec = tween(400),
                    label = "iconCrossfade"
                ) { granted ->
                    Icon(
                        imageVector = if (granted) Icons.Filled.Check else icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Success badge with animation
                    AnimatedVisibility(
                        visible = isGranted,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = context.getString(R.string.onboarding_granted),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionTipItem(
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
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
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
fun EnhancedBackupRestoreContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    onSkip: () -> Unit = {},
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val musicViewModel: MusicViewModel = viewModel()
    val scrollState = rememberScrollState()

    // State for backup settings
    val autoBackupEnabled by appSettings.autoBackupEnabled.collectAsState()
    val lastBackupTimestamp by appSettings.lastBackupTimestamp.collectAsState()

    // Local UI state
    var isCreatingBackup by remember { mutableStateOf(false) }
    var isRestoringFromClipboard by remember { mutableStateOf(false) }
    var isRestoringFromFile by remember { mutableStateOf(false) }
    var backupStatusMessage by remember { mutableStateOf<String?>(null) }
    var backupStatusIsError by remember { mutableStateOf(false) }
    var showRestartHint by remember { mutableStateOf(false) }

    val isBusy = isCreatingBackup || isRestoringFromClipboard || isRestoringFromFile

    fun restartApp() {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        (context as? Activity)?.finish()
        Runtime.getRuntime().exit(0)
    }

    fun handleRestorePayload(backupJson: String?) {
        if (backupJson.isNullOrEmpty()) {
            backupStatusIsError = true
            backupStatusMessage = "Unable to read backup data"
            showRestartHint = false
            return
        }

        if (appSettings.restoreFromBackup(backupJson)) {
            musicViewModel.reloadPlaylistsFromSettings()
            backupStatusIsError = false
            backupStatusMessage = "Backup restored successfully. Restart the app to apply all changes."
            showRestartHint = true
        } else {
            backupStatusIsError = true
            backupStatusMessage = "Invalid backup format or corrupted data"
            showRestartHint = false
        }
    }

    val backupLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    try {
                        isCreatingBackup = true
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                        musicViewModel.ensurePlaylistsSaved()

                        val backupJson = appSettings.createBackup()
                        val outputStream = context.contentResolver.openOutputStream(uri)
                            ?: throw IllegalStateException("Unable to open backup destination")
                        outputStream.use { stream ->
                            stream.write(backupJson.toByteArray())
                            stream.flush()
                        }

                        appSettings.setLastBackupTimestamp(System.currentTimeMillis())
                        appSettings.setBackupLocation(uri.toString())

                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Rhythm Backup", backupJson)
                        clipboard.setPrimaryClip(clip)

                        backupStatusIsError = false
                        backupStatusMessage = "Backup created and copied to clipboard."
                        showRestartHint = false
                    } catch (e: Exception) {
                        backupStatusIsError = true
                        backupStatusMessage = "Failed to create backup: ${e.message}"
                        showRestartHint = false
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

    val restoreFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    try {
                        isRestoringFromFile = true
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                        val backupJson = context.contentResolver.openInputStream(uri)
                            ?.bufferedReader()
                            ?.use { it.readText() }
                        handleRestorePayload(backupJson)
                    } catch (e: Exception) {
                        backupStatusIsError = true
                        backupStatusMessage = "Failed to restore from file: ${e.message}"
                        showRestartHint = false
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

    fun restoreFromClipboard() {
        scope.launch {
            try {
                isRestoringFromClipboard = true
                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.LongPress)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                val backupJson = if (clip != null && clip.itemCount > 0) {
                    clip.getItemAt(0).coerceToText(context)?.toString()
                } else {
                    null
                }

                if (backupJson == null) {
                    backupStatusIsError = true
                    backupStatusMessage = "No backup found in clipboard"
                    showRestartHint = false
                } else {
                    handleRestorePayload(backupJson)
                }
            } catch (e: Exception) {
                backupStatusIsError = true
                backupStatusMessage = "Failed to restore from clipboard: ${e.message}"
                showRestartHint = false
            } finally {
                isRestoringFromClipboard = false
            }
        }
    }

    fun launchCreateBackup() {
        if (isBusy) return
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(
                Intent.EXTRA_TITLE,
                "rhythm_backup_${java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())}.json"
            )
        }
        backupLocationLauncher.launch(intent)
    }

    fun launchRestoreFile() {
        if (isBusy) return
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "text/plain", "*/*"))
        }
        restoreFileLauncher.launch(intent)
    }

    if (isTablet) {
        // Tablet layout: Left side - icon, title, description, tips, action buttons; Right side - toggles and cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, title, description, tips, and action buttons
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced icon with animation
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Backup,
                            contentDescription = "Backup & Restore",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_backup_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_backup_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Backup features info card
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
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_what_backed_up),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        BackupFeatureTipItem(
                            icon = Icons.Filled.Save,
                            text = context.getString(R.string.onboarding_backed_up_1)
                        )
                        BackupFeatureTipItem(
                            icon = Icons.Filled.RestoreFromTrash,
                            text = context.getString(R.string.onboarding_backed_up_2)
                        )
                        BackupFeatureTipItem(
                            icon = Icons.Filled.Security,
                            text = context.getString(R.string.onboarding_backed_up_3)
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Toggles and cards
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Auto-backup toggle card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    OnboardingSettingRow(
                        icon = Icons.Filled.Autorenew,
                        title = context.getString(R.string.onboarding_auto_backup),
                        description = context.getString(R.string.onboarding_auto_backup_desc),
                        isEnabled = autoBackupEnabled,
                        onToggle = { 
                            HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                            appSettings.setAutoBackupEnabled(it)
                        }
                    )
                }

                OnboardingBackupActionCard(
                    isCreatingBackup = isCreatingBackup,
                    isRestoringFromClipboard = isRestoringFromClipboard,
                    isRestoringFromFile = isRestoringFromFile,
                    onCreateBackup = {
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                        launchCreateBackup()
                    },
                    onRestoreFromClipboard = {
                        if (isBusy) return@OnboardingBackupActionCard
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                        restoreFromClipboard()
                    },
                    onRestoreFromFile = {
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                        launchRestoreFile()
                    }
                )

                AnimatedVisibility(
                    visible = backupStatusMessage != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    BackupRestoreStatusCard(
                        message = backupStatusMessage ?: "",
                        isError = backupStatusIsError,
                        showRestart = showRestartHint,
                        onRestart = {
                            HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                            restartApp()
                        }
                    )
                }

                // Tip card
                AnimatedVisibility(
                    visible = autoBackupEnabled,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_manual_backup_info),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with icon and title
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Enhanced icon with animation
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Backup,
                            contentDescription = "Backup & Restore",
                            
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = context.getString(R.string.onboarding_backup_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_backup_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            // Vertically centered content area
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                // Auto-backup toggle card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    OnboardingSettingRow(
                        icon = Icons.Filled.Autorenew,
                        title = context.getString(R.string.onboarding_auto_backup),
                        description = context.getString(R.string.onboarding_auto_backup_desc),
                        isEnabled = autoBackupEnabled,
                        onToggle = { 
                            HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                            appSettings.setAutoBackupEnabled(it)
                        }
                    )
                }

                OnboardingBackupActionCard(
                    isCreatingBackup = isCreatingBackup,
                    isRestoringFromClipboard = isRestoringFromClipboard,
                    isRestoringFromFile = isRestoringFromFile,
                    onCreateBackup = {
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                        launchCreateBackup()
                    },
                    onRestoreFromClipboard = {
                        if (isBusy) return@OnboardingBackupActionCard
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                        restoreFromClipboard()
                    },
                    onRestoreFromFile = {
                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                        launchRestoreFile()
                    }
                )

                AnimatedVisibility(
                    visible = backupStatusMessage != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    BackupRestoreStatusCard(
                        message = backupStatusMessage ?: "",
                        isError = backupStatusIsError,
                        showRestart = showRestartHint,
                        onRestart = {
                            HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                            restartApp()
                        }
                    )
                }

                // Tip card
                AnimatedVisibility(
                    visible = autoBackupEnabled,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_manual_backup_info),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Backup features info card
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
                                imageVector = Icons.Filled.Info,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_what_backed_up),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        BackupFeatureTipItem(
                            icon = Icons.Filled.Save,
                            text = context.getString(R.string.onboarding_backed_up_1)
                        )
                        BackupFeatureTipItem(
                            icon = Icons.Filled.RestoreFromTrash,
                            text = context.getString(R.string.onboarding_backed_up_2)
                        )
                        BackupFeatureTipItem(
                            icon = Icons.Filled.Security,
                            text = context.getString(R.string.onboarding_backed_up_3)
                        )
                    }
                }
            } // End vertically centered content

            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}

@Composable
private fun OnboardingBackupActionCard(
    isCreatingBackup: Boolean,
    isRestoringFromClipboard: Boolean,
    isRestoringFromFile: Boolean,
    onCreateBackup: () -> Unit,
    onRestoreFromClipboard: () -> Unit,
    onRestoreFromFile: () -> Unit
) {
    val context = LocalContext.current
    val isBusy = isCreatingBackup || isRestoringFromClipboard || isRestoringFromFile

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = context.getString(R.string.onboarding_backup_center),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(10.dp))

            OnboardingBackupActionRow(
                icon = Icons.Filled.Save,
                title = context.getString(R.string.settings_create_backup),
                description = context.getString(R.string.settings_create_backup_desc),
                inProgress = isCreatingBackup,
                enabled = !isBusy,
                onClick = onCreateBackup
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
            )

            OnboardingBackupActionRow(
                icon = Icons.Filled.ContentCopy,
                title = context.getString(R.string.settings_restore_clipboard),
                description = context.getString(R.string.settings_restore_clipboard_desc),
                inProgress = isRestoringFromClipboard,
                enabled = !isBusy,
                onClick = onRestoreFromClipboard
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
            )

            OnboardingBackupActionRow(
                icon = Icons.Filled.FolderOpen,
                title = context.getString(R.string.settings_restore_file),
                description = context.getString(R.string.settings_restore_file_desc),
                inProgress = isRestoringFromFile,
                enabled = !isBusy,
                onClick = onRestoreFromFile
            )
        }
    }
}

@Composable
private fun OnboardingBackupActionRow(
    icon: ImageVector,
    title: String,
    description: String,
    inProgress: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && !inProgress, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (inProgress) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun BackupRestoreStatusCard(
    message: String,
    isError: Boolean,
    showRestart: Boolean,
    onRestart: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isError) Icons.Filled.Error else Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isError) context.getString(R.string.ui_error) else "Success",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer
            )

            if (!isError && showRestart) {
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = onRestart,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(context.getString(R.string.settings_restart_now))
                }
            }
        }
    }
}

@Composable
private fun BackupFeatureTipItem(
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
private fun LibraryTipItem(
    icon: ImageVector,
    text: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Composable
fun EnhancedAudioPlaybackContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val useSystemVolume by appSettings.useSystemVolume.collectAsState()
    val stopPlaybackOnZeroVolume by appSettings.stopPlaybackOnZeroVolume.collectAsState()
    val resumeOnDeviceReconnect by appSettings.resumeOnDeviceReconnect.collectAsState()
    val autoAddToQueue by appSettings.autoAddToQueue.collectAsState()
    val showLyrics by appSettings.showLyrics.collectAsState()
    val lyricsSourcePreference by appSettings.lyricsSourcePreference.collectAsState()
    val scrollState = rememberScrollState()

    if (isTablet) {
        // Tablet layout: Left side - icon, title, description, tips, action buttons; Right side - toggles and dropdowns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, title, description, tips, and action buttons
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced audio icon
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Player.VolumeUp,
                            contentDescription = "Audio & Playback Settings",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_audio_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_audio_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Equalizer and Sleep Timer info card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
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
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_additional_features),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        LibraryTipItem(
                            icon = Icons.Filled.GraphicEq,
                            text = context.getString(R.string.onboarding_equalizer_desc)
                        )
                        LibraryTipItem(
                            icon = Icons.Filled.AccessTime,
                            text = context.getString(R.string.onboarding_sleep_timer_desc)
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Consolidated settings card
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Consolidated Audio Settings Card
                AudioPlaybackSettingsCard(
                    useSystemVolume = useSystemVolume,
                    stopPlaybackOnZeroVolume = stopPlaybackOnZeroVolume,
                    resumeOnDeviceReconnect = resumeOnDeviceReconnect,
                    autoAddToQueue = autoAddToQueue,
                    showLyrics = showLyrics,
                    onSystemVolumeChange = { appSettings.setUseSystemVolume(it) },
                    onStopPlaybackOnZeroVolumeChange = { appSettings.setStopPlaybackOnZeroVolume(it) },
                    onResumeOnReconnectChange = { appSettings.setResumeOnDeviceReconnect(it) },
                    onAutoQueueChange = { appSettings.setAutoAddToQueue(it) },
                    onShowLyricsChange = { appSettings.setShowLyrics(it) }
                )

                // Lyrics Source Priority dropdown (shown when lyrics are enabled)
                AnimatedVisibility(
                    visible = showLyrics,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingsDropdownItem(
                            title = context.getString(R.string.onboarding_lyrics_source_title),
                            description = context.getString(R.string.onboarding_lyrics_source_desc),
                            selectedOption = lyricsSourcePreference.displayName,
                            icon = Icons.Filled.Cloud,
                            options = chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.values().map { it.displayName },
                            onOptionSelected = { displayName ->
                                val preference = chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.values()
                                    .find { it.displayName == displayName }
                                if (preference != null) {
                                    appSettings.setLyricsSourcePreference(preference)
                                }
                            }
                        )

                        // Lyrics sources info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = context.getString(R.string.onboarding_lyrics_sources),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Enhanced audio icon
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RhythmIcons.Player.VolumeUp,
                        contentDescription = "Audio & Playback Settings",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_audio_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_audio_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Vertically centered content area
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                // Consolidated Audio Settings Card
                AudioPlaybackSettingsCard(
                    useSystemVolume = useSystemVolume,
                    stopPlaybackOnZeroVolume = stopPlaybackOnZeroVolume,
                    resumeOnDeviceReconnect = resumeOnDeviceReconnect,
                    autoAddToQueue = autoAddToQueue,
                    showLyrics = showLyrics,
                    onSystemVolumeChange = { appSettings.setUseSystemVolume(it) },
                    onStopPlaybackOnZeroVolumeChange = { appSettings.setStopPlaybackOnZeroVolume(it) },
                    onResumeOnReconnectChange = { appSettings.setResumeOnDeviceReconnect(it) },
                    onAutoQueueChange = { appSettings.setAutoAddToQueue(it) },
                    onShowLyricsChange = { appSettings.setShowLyrics(it) }
                )

                // Lyrics Source Priority dropdown (shown when lyrics are enabled)
                AnimatedVisibility(
                    visible = showLyrics,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingsDropdownItem(
                            title = context.getString(R.string.onboarding_lyrics_source_title),
                            description = context.getString(R.string.onboarding_lyrics_source_desc),
                            selectedOption = lyricsSourcePreference.displayName,
                            icon = Icons.Filled.Cloud,
                            options = chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.values().map { it.displayName },
                            onOptionSelected = { displayName ->
                                val preference = chromahub.rhythm.app.shared.data.model.LyricsSourcePreference.values()
                                    .find { it.displayName == displayName }
                                if (preference != null) {
                                    appSettings.setLyricsSourcePreference(preference)
                                }
                            }
                        )

                        // Lyrics sources info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = context.getString(R.string.onboarding_lyrics_sources),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

            } // End vertically centered content

            Spacer(modifier = Modifier.height(16.dp))

            // Equalizer and Sleep Timer info card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
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
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(R.string.onboarding_additional_features),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    LibraryTipItem(
                        icon = Icons.Filled.GraphicEq,
                        text = context.getString(R.string.onboarding_equalizer_desc)
                    )
                    LibraryTipItem(
                        icon = Icons.Filled.AccessTime,
                        text = context.getString(R.string.onboarding_sleep_timer_desc)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedLibrarySetupContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    onOpenTabOrderBottomSheet: () -> Unit = {},
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val albumViewType by appSettings.albumViewType.collectAsState()
    val artistViewType by appSettings.artistViewType.collectAsState()
    val albumSortOrder by appSettings.albumSortOrder.collectAsState()
    val showLyrics by appSettings.showLyrics.collectAsState()
    val scrollState = rememberScrollState()
    
    val albumViewIsGrid = albumViewType == AlbumViewType.GRID
    val artistViewIsGrid = artistViewType == ArtistViewType.GRID

    if (isTablet) {
        // Tablet layout: Left side - icon, title, description, tips, action buttons; Right side - toggles and cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, title, description, tips, and action buttons
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced library icon
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LibraryMusic,
                            contentDescription = "Library Setup",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_library_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_library_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // How it Works info card
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
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_library_how_works),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        LibraryTipItem(
                            icon = Icons.Filled.Reorder,
                            text = context.getString(R.string.onboarding_library_1)
                        )
                        LibraryTipItem(
                            icon = Icons.Filled.Queue,
                            text = context.getString(R.string.onboarding_library_2)
                        )
                        LibraryTipItem(
                            icon = Icons.Filled.LibraryMusic,
                            text = context.getString(R.string.onboarding_library_4)
                        )
                        LibraryTipItem(
                            icon = Icons.Filled.Tune,
                            text = context.getString(R.string.onboarding_library_3)
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Library settings
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LibrarySettingsCard(
                    albumViewIsGrid = albumViewIsGrid,
                    artistViewIsGrid = artistViewIsGrid,
                    showLyrics = showLyrics,
                    onAlbumViewChange = { isGrid ->
                        appSettings.setAlbumViewType(if (isGrid) AlbumViewType.GRID else AlbumViewType.LIST)
                    },
                    onArtistViewChange = { isGrid ->
                        appSettings.setArtistViewType(if (isGrid) ArtistViewType.GRID else ArtistViewType.LIST)
                    },
                    onShowLyricsChange = { appSettings.setShowLyrics(it) }
                )
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Enhanced library icon
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LibraryMusic,
                        contentDescription = "Library Setup",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_library_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_library_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LibrarySettingsCard(
                albumViewIsGrid = albumViewIsGrid,
                artistViewIsGrid = artistViewIsGrid,
                showLyrics = showLyrics,
                onAlbumViewChange = { isGrid ->
                    appSettings.setAlbumViewType(if (isGrid) AlbumViewType.GRID else AlbumViewType.LIST)
                },
                onArtistViewChange = { isGrid ->
                    appSettings.setArtistViewType(if (isGrid) ArtistViewType.GRID else ArtistViewType.LIST)
                },
                onShowLyricsChange = { appSettings.setShowLyrics(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // How it Works info card
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
                            imageVector = Icons.Filled.Info,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(R.string.onboarding_library_how_works),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    LibraryTipItem(
                        icon = Icons.Filled.Reorder,
                        text = context.getString(R.string.onboarding_library_1)
                    )
                    LibraryTipItem(
                        icon = Icons.Filled.Queue,
                        text = context.getString(R.string.onboarding_library_2)
                    )
                    LibraryTipItem(
                        icon = Icons.Filled.LibraryMusic,
                        text = context.getString(R.string.onboarding_library_4)
                    )
                    LibraryTipItem(
                        icon = Icons.Filled.Tune,
                        text = context.getString(R.string.onboarding_library_3)
                    )
                }
            }
        }
    }
}

@Composable
private fun LibrarySettingsCard(
    albumViewIsGrid: Boolean,
    artistViewIsGrid: Boolean,
    showLyrics: Boolean,
    onAlbumViewChange: (Boolean) -> Unit,
    onArtistViewChange: (Boolean) -> Unit,
    onShowLyricsChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            OnboardingSettingRow(
                icon = Icons.Default.GridView,
                title = context.getString(R.string.onboarding_library_album_grid),
                description = context.getString(R.string.onboarding_library_album_grid_desc),
                isEnabled = albumViewIsGrid,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onAlbumViewChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Default.PersonOutline,
                title = context.getString(R.string.onboarding_library_artist_grid),
                description = context.getString(R.string.onboarding_library_artist_grid_desc),
                isEnabled = artistViewIsGrid,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onArtistViewChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Default.Lyrics,
                title = context.getString(R.string.onboarding_library_show_lyrics),
                description = context.getString(R.string.onboarding_library_show_lyrics_desc),
                isEnabled = showLyrics,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onShowLyricsChange(it) 
                }
            )
        }
    }
}

@Composable
private fun FolderManagementCard(
    isBlacklistMode: Boolean,
    blacklistedFolders: List<String>,
    whitelistedFolders: List<String>,
    onModeChange: (Boolean) -> Unit,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentFolders = if (isBlacklistMode) blacklistedFolders else whitelistedFolders

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Mode toggle row
            OnboardingSettingRow(
                icon = if (isBlacklistMode) Icons.Default.Block else Icons.Default.CheckCircle,
                title = if (isBlacklistMode) "Blacklist Mode" else "Whitelist Mode",
                description = if (isBlacklistMode) 
                    "Exclude selected folders from library" 
                else 
                    "Only include selected folders in library",
                isEnabled = isBlacklistMode,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onModeChange(it) 
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Add folder button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddFolder() }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Add Folder",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isBlacklistMode) 
                            "Select folders to block from library"
                        else
                            "Select folders to include in library",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    
                    modifier = Modifier.size(24.dp)
                )
            }

            // Folder count info
            if (currentFolders.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${currentFolders.size} ${if (isBlacklistMode) "blocked" else "whitelisted"} folders",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // List folders with remove option
                currentFolders.forEach { folder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = folder.substringAfterLast("/"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onRemoveFolder(folder) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AudioPlaybackSettingsCard(
    useSystemVolume: Boolean,
    stopPlaybackOnZeroVolume: Boolean,
    resumeOnDeviceReconnect: Boolean,
    autoAddToQueue: Boolean,
    showLyrics: Boolean,
    onSystemVolumeChange: (Boolean) -> Unit,
    onStopPlaybackOnZeroVolumeChange: (Boolean) -> Unit,
    onResumeOnReconnectChange: (Boolean) -> Unit,
    onAutoQueueChange: (Boolean) -> Unit,
    onShowLyricsChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            OnboardingSettingRow(
                icon = RhythmIcons.Player.VolumeUp,
                title = context.getString(R.string.onboarding_system_volume_title),
                description = context.getString(R.string.onboarding_system_volume_desc),
                isEnabled = useSystemVolume,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onSystemVolumeChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = RhythmIcons.Player.Stop,
                title = context.getString(R.string.settings_stop_playback_on_zero_volume),
                description = context.getString(R.string.settings_stop_playback_on_zero_volume_desc),
                isEnabled = stopPlaybackOnZeroVolume,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onStopPlaybackOnZeroVolumeChange(it)
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = RhythmIcons.Devices.Bluetooth,
                title = context.getString(R.string.settings_resume_on_device_reconnect),
                description = context.getString(R.string.settings_resume_on_device_reconnect_desc),
                isEnabled = resumeOnDeviceReconnect,
                onToggle = {
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onResumeOnReconnectChange(it)
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = RhythmIcons.Queue,
                title = context.getString(R.string.onboarding_auto_queue_title),
                description = context.getString(R.string.onboarding_auto_queue_desc),
                isEnabled = autoAddToQueue,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onAutoQueueChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Filled.Lyrics,
                title = context.getString(R.string.onboarding_show_lyrics_title),
                description = context.getString(R.string.onboarding_show_lyrics_desc),
                isEnabled = showLyrics,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onShowLyricsChange(it) 
                }
            )
        }
    }
}

@Composable
private fun ThemeSettingsCard(
    useSystemTheme: Boolean,
    darkMode: Boolean,
    useDynamicColors: Boolean,
    festiveTheme: Boolean,
    onSystemThemeChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onDynamicColorsChange: (Boolean) -> Unit,
    onFestiveThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Column(modifier = Modifier.padding(20.dp)) {
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
                            0 -> onSystemThemeChange(true)
                            1 -> {
                                onSystemThemeChange(false)
                                onDarkModeChange(false)
                            }
                            2 -> {
                                onSystemThemeChange(false)
                                onDarkModeChange(true)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Dynamic colors (Material You) - only on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                OnboardingSettingRow(
                    icon = Icons.Filled.Palette,
                    title = context.getString(R.string.onboarding_dynamic_colors_title),
                    description = context.getString(R.string.onboarding_dynamic_colors_desc),
                    isEnabled = useDynamicColors,
                    onToggle = { 
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        onDynamicColorsChange(it) 
                    }
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Filled.AutoAwesome,
                title = "Festive Theme",
                description = "Enable festive decorations and seasonal themes",
                isEnabled = festiveTheme,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onFestiveThemeChange(it) 
                }
            )
        }
    }
}

@Composable
private fun LibraryFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: (() -> Unit)? = null,
    usePrimaryStyle: Boolean = false,
    useTertiaryStyle: Boolean = false
) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        colors = CardDefaults.cardColors(
            containerColor = when {
                useTertiaryStyle -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                usePrimaryStyle -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceContainerLow
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick?.invoke() } else Modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    useTertiaryStyle -> MaterialTheme.colorScheme.onTertiaryContainer
                    usePrimaryStyle -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        useTertiaryStyle -> MaterialTheme.colorScheme.onTertiaryContainer
                        usePrimaryStyle -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        useTertiaryStyle -> MaterialTheme.colorScheme.onTertiaryContainer
                        usePrimaryStyle -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    lineHeight = 16.sp
                )
            }
            if (onClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = if (usePrimaryStyle)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EnhancedThemingContent(
    onNextStep: () -> Unit,
    themeViewModel: ThemeViewModel,
    appSettings: AppSettings,
    onSkip: () -> Unit = {},
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val useSystemTheme by themeViewModel.useSystemTheme.collectAsState()
    val darkMode by themeViewModel.darkMode.collectAsState()
    val useDynamicColors by themeViewModel.useDynamicColors.collectAsState()
    val festiveTheme by appSettings.festiveThemeEnabled.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Font selection state
    var showFontSelectionDialog by remember { mutableStateOf(false) }

    if (isTablet) {
        // Tablet layout: Left side - icon, title, description, tips, action buttons; Right side - toggles and cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, title, description, tips, and action buttons
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced icon with animation
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Theming",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_theme_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_theme_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Guide to Tuner settings
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
                                imageVector = Icons.Filled.Settings,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_more_tuner),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        LibraryTipItem(
                            icon = Icons.Filled.Palette,
                            text = context.getString(R.string.onboarding_tuner_1)
                        )
                        LibraryTipItem(
                            icon = Icons.Filled.FontDownload,
                            text = context.getString(R.string.onboarding_tuner_2)
                        )
                        LibraryTipItem(
                            icon = Icons.Filled.AutoAwesome,
                            text = context.getString(R.string.onboarding_tuner_3)
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Preview card, toggles, and font selection
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                /* // Enhanced live theme preview card
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(600)) + expandVertically(animationSpec = tween(600))
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = context.getString(R.string.onboarding_live_preview),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Theme preview sample UI
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Primary button preview
                                Button(
                                    onClick = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = false
                                ) {
                                    Text(
                                        "Sample Button",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                // Color swatches
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Primary color
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.MusicNote,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Primary",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Secondary color
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.secondary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Album,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSecondary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Secondary",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Tertiary color
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.tertiary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Palette,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onTertiary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Tertiary",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                } */

                ThemeSettingsCard(
                    useSystemTheme = useSystemTheme,
                    darkMode = darkMode,
                    useDynamicColors = useDynamicColors,
                    festiveTheme = festiveTheme,
                    onSystemThemeChange = { enabled ->
                        scope.launch {
                            themeViewModel.setUseSystemTheme(enabled)
                        }
                    },
                    onDarkModeChange = { enabled ->
                        scope.launch {
                            themeViewModel.setDarkMode(enabled)
                        }
                    },
                    onDynamicColorsChange = { enabled ->
                        scope.launch {
                            themeViewModel.setUseDynamicColors(enabled)
                        }
                    },
                    onFestiveThemeChange = { enabled ->
                        appSettings.setFestiveThemeEnabled(enabled)
                    }
                )

//                // Font selection card
//                Card(
//                    onClick = { showFontSelectionDialog = true },
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
//                    ),
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(18.dp)
//                ) {
//                    Row(
//                        modifier = Modifier.padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.FontDownload,
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Spacer(modifier = Modifier.width(12.dp))
//                        Column(modifier = Modifier.weight(1f)) {
//                            Text(
//                                text = context.getString(R.string.theme_font_selection),
//                                style = MaterialTheme.typography.labelLarge,
//                                fontWeight = FontWeight.SemiBold,
//                                color = MaterialTheme.colorScheme.onPrimaryContainer
//                            )
//                            Text(
//                                text = context.getString(R.string.theme_font_selection_desc),
//                                style = MaterialTheme.typography.bodySmall,
//                                color = MaterialTheme.colorScheme.onPrimaryContainer
//                            )
//                        }
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                            contentDescription = null,
//                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }

                // Default Landing Screen dropdown
                SettingsDropdownItem(
                    title = context.getString(R.string.onboarding_default_screen_title),
                    description = context.getString(R.string.onboarding_default_screen_desc),
                    selectedOption = if (appSettings.defaultScreen.collectAsState().value == "library") context.getString(R.string.option_library) else context.getString(R.string.option_home),
                    icon = Icons.Filled.Home,
                    options = listOf(context.getString(R.string.option_home), context.getString(R.string.option_library)),
                    onOptionSelected = { selectedOption ->
                        appSettings.setDefaultScreen(selectedOption.lowercase())
                    }
                )
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Enhanced icon with animation
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Palette,
                        contentDescription = "Theming",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_theme_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_theme_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            /* // Live theme preview card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = context.getString(R.string.onboarding_live_preview),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Theme preview sample UI
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Primary button preview
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        ) {
                            Text(
                                "Sample Button",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        // Color swatches
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Primary color
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MusicNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Primary",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Secondary color
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.secondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Album,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Secondary",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Tertiary color
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.tertiary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Palette,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Tertiary",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) */

            // Theme options - consolidated settings card
            ThemeSettingsCard(
                useSystemTheme = useSystemTheme,
                darkMode = darkMode,
                useDynamicColors = useDynamicColors,
                festiveTheme = festiveTheme,
                onSystemThemeChange = { enabled ->
                    scope.launch {
                        themeViewModel.setUseSystemTheme(enabled)
                    }
                },
                onDarkModeChange = { enabled ->
                    scope.launch {
                        themeViewModel.setDarkMode(enabled)
                    }
                },
                onDynamicColorsChange = { enabled ->
                    scope.launch {
                        themeViewModel.setUseDynamicColors(enabled)
                    }
                },
                onFestiveThemeChange = { enabled ->
                    appSettings.setFestiveThemeEnabled(enabled)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Default Landing Screen dropdown
            SettingsDropdownItem(
                title = context.getString(R.string.onboarding_default_screen_title),
                description = context.getString(R.string.onboarding_default_screen_desc),
                selectedOption = if (appSettings.defaultScreen.collectAsState().value == "library") context.getString(R.string.option_library) else context.getString(R.string.option_home),
                icon = Icons.Filled.Home,
                options = listOf(context.getString(R.string.option_home), context.getString(R.string.option_library)),
                onOptionSelected = { selectedOption ->
                    appSettings.setDefaultScreen(selectedOption.lowercase())
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* // Font selection card - commented out
            Card(
                onClick = { showFontSelectionDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.FontDownload,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = context.getString(R.string.theme_font_selection),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = context.getString(R.string.theme_font_selection_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } */

            Spacer(modifier = Modifier.height(16.dp))

            // Guide to Tuner settings
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
                            imageVector = Icons.Filled.Settings,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(R.string.onboarding_more_tuner),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    LibraryTipItem(
                        icon = Icons.Filled.Palette,
                        text = context.getString(R.string.onboarding_tuner_1)
                    )
                    LibraryTipItem(
                        icon = Icons.Filled.FontDownload,
                        text = context.getString(R.string.onboarding_tuner_2)
                    )
                    LibraryTipItem(
                        icon = Icons.Filled.AutoAwesome,
                        text = context.getString(R.string.onboarding_tuner_3)
                    )
                }
            }
        }
    }
    
    // Font Selection Dialog - Simple implementation
    // Note: Full font selection dialog is available in app settings
    // This is a simplified version for onboarding
    if (showFontSelectionDialog) {
        // For now, just close the dialog and navigate user to settings
        // In a future update, this could show a proper font selection UI
        LaunchedEffect(Unit) {
            showFontSelectionDialog = false
            // TODO: Navigate to theme settings for full font customization
        }
    }
}

@Composable
fun EnhancedThemeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                onToggle(!isEnabled)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.surfaceContainerLow
            else
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            OnboardingAnimatedSwitch(
                checked = isEnabled,
                onCheckedChange = { enabled ->
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                    onToggle(enabled)
                }
            )
        }
    }
}

@Composable
fun OnboardingDropdownOption(
    icon: ImageVector,
    title: String,
    description: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                showDropdown = true
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Current selection badge
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = selectedOption,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Show options",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Dropdown Menu
        Box {
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                shape = RoundedCornerShape(12.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = when (option) {
                                        "API" -> Icons.Filled.Cloud
                                        "Embedded" -> Icons.Filled.MusicNote
                                        "Local" -> Icons.Filled.Folder
                                        else -> Icons.Filled.MusicNote
                                    },
                                    contentDescription = null,
                                    tint = if (selectedOption == option)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selectedOption == option) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedOption == option)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (selectedOption == option) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            onOptionSelected(option)
                            showDropdown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedRhythmGuardContent(
    appSettings: AppSettings,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    val rhythmGuardMode by appSettings.rhythmGuardMode.collectAsState()
    val rhythmGuardAge by appSettings.rhythmGuardAge.collectAsState()
    val rhythmGuardEnabled = rhythmGuardMode != AppSettings.RHYTHM_GUARD_MODE_OFF

    fun setMode(mode: String) {
        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
        appSettings.setRhythmGuardMode(mode)
    }

    @Composable
    fun ModeSelectionCard() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OnboardingSettingRow(
                    icon = Icons.Filled.Security,
                    title = context.getString(R.string.settings_rhythm_guard),
                    description = context.getString(R.string.settings_rhythm_guard_mode_desc),
                    isEnabled = rhythmGuardEnabled,
                    onToggle = { enabled ->
                        setMode(if (enabled) AppSettings.RHYTHM_GUARD_MODE_AUTO else AppSettings.RHYTHM_GUARD_MODE_OFF)
                    }
                )

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    if (rhythmGuardEnabled) {
                        ExpressiveButtonGroup(
                            items = listOf(
                                context.getString(R.string.settings_rhythm_guard_mode_auto),
                                context.getString(R.string.settings_rhythm_guard_mode_manual)
                            ),
                            selectedIndex = if (rhythmGuardMode == AppSettings.RHYTHM_GUARD_MODE_MANUAL) 1 else 0,
                            onItemClick = { index ->
                                when (index) {
                                    0 -> setMode(AppSettings.RHYTHM_GUARD_MODE_AUTO)
                                    else -> setMode(AppSettings.RHYTHM_GUARD_MODE_MANUAL)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = context.getString(R.string.settings_rhythm_guard_age_search_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            IconButton(onClick = { appSettings.setRhythmGuardAge((rhythmGuardAge - 1).coerceAtLeast(8)) }) {
                                Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
                            }
                        }
                        Text(
                            text = rhythmGuardAge.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            IconButton(onClick = { appSettings.setRhythmGuardAge((rhythmGuardAge + 1).coerceAtMost(80)) }) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                            }
                        }
                    }
                    Text(
                        text = context.getString(R.string.onboarding_rhythm_guard_age_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_rhythm_guard_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_rhythm_guard_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OnboardingTipItem(
                            icon = Icons.Filled.CheckCircle,
                            text = context.getString(R.string.onboarding_rhythm_guard_tip_1)
                        )
                        OnboardingTipItem(
                            icon = Icons.Filled.AccessTime,
                            text = context.getString(R.string.onboarding_rhythm_guard_tip_2)
                        )
                        OnboardingTipItem(
                            icon = Icons.Filled.Tune,
                            text = context.getString(R.string.onboarding_rhythm_guard_tip_3)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModeSelectionCard()
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_rhythm_guard_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_rhythm_guard_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            ModeSelectionCard()

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OnboardingTipItem(
                        icon = Icons.Filled.CheckCircle,
                        text = context.getString(R.string.onboarding_rhythm_guard_tip_1)
                    )
                    OnboardingTipItem(
                        icon = Icons.Filled.AccessTime,
                        text = context.getString(R.string.onboarding_rhythm_guard_tip_2)
                    )
                    OnboardingTipItem(
                        icon = Icons.Filled.Tune,
                        text = context.getString(R.string.onboarding_rhythm_guard_tip_3)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EnhancedFullTourPromptContent(
    onContinueFullTour: () -> Unit,
    onSkipFullTour: () -> Unit,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
            Box(
                modifier = Modifier
                    .size(if (isTablet) 96.dp else 80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(if (isTablet) 48.dp else 40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = context.getString(R.string.onboarding_full_tour_prompt_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = context.getString(R.string.onboarding_full_tour_prompt_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OnboardingTipItem(
                    icon = Icons.Filled.Tune,
                    text = context.getString(R.string.onboarding_full_tour_prompt_tip_1)
                )
                OnboardingTipItem(
                    icon = Icons.Filled.LibraryMusic,
                    text = context.getString(R.string.onboarding_full_tour_prompt_tip_2)
                )
                OnboardingTipItem(
                    icon = Icons.Filled.Info,
                    text = context.getString(R.string.onboarding_full_tour_prompt_tip_3)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.rhythm_splash_logo),
                contentDescription = context.getString(R.string.updates_rhythm_logo_cd),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = context.getString(R.string.common_rhythm),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isTablet) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                backButton?.invoke()
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                onContinueFullTour()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(
                text = context.getString(R.string.onboarding_continue_full_tour),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                onSkipFullTour()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(
                text = context.getString(R.string.onboarding_finish_now),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun EnhancedUpdaterContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    updaterViewModel: AppUpdaterViewModel = viewModel(),
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val autoCheckForUpdates by appSettings.autoCheckForUpdates.collectAsState()
    val updateNotificationsEnabled by appSettings.updateNotificationsEnabled.collectAsState()
    val useSmartUpdatePolling by appSettings.useSmartUpdatePolling.collectAsState()
    val updateChannel by appSettings.updateChannel.collectAsState()
    val updateCheckIntervalHours by appSettings.updateCheckIntervalHours.collectAsState()
    val updatesEnabled by appSettings.updatesEnabled.collectAsState() // NEW
    val scope = rememberCoroutineScope()

    // Collect updater states
    val isCheckingForUpdates by updaterViewModel.isCheckingForUpdates.collectAsState()
    val updateAvailable by updaterViewModel.updateAvailable.collectAsState()
    val latestVersion by updaterViewModel.latestVersion.collectAsState()
    val currentVersion by updaterViewModel.currentVersion.collectAsState()
    val isDownloading by updaterViewModel.isDownloading.collectAsState()
    val downloadProgress by updaterViewModel.downloadProgress.collectAsState()
    val downloadedFile by updaterViewModel.downloadedFile.collectAsState()
    val error by updaterViewModel.error.collectAsState()

    // Auto-check for updates once when this step is opened and updates are enabled
    var hasCheckedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(updatesEnabled) {
        if (updatesEnabled && !hasCheckedOnce) {
            hasCheckedOnce = true
            updaterViewModel.checkForUpdates(force = true)
        }
    }
    val scrollState = rememberScrollState()

    // Infinite transition for continuous animations
    val infiniteTransition = rememberInfiniteTransition(label = "update_animations")

    // Rotating icon for checking state
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Breathing glow animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Success scale animation
    val successScale = remember { Animatable(0.7f) }
    LaunchedEffect(downloadedFile) {
        if (downloadedFile != null) {
            successScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    if (isTablet) {
        // Tablet layout: Left side - icon, title, description, update actions, action buttons; Right side - toggles and dropdowns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, title, description, update actions, and action buttons
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced icon with animation - shows status
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    error != null -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                    downloadedFile != null -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                    updateAvailable -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCheckingForUpdates) {
                            NetworkOperationLoader(
                                modifier = Modifier
                                    .size(48.dp)
                                    .alpha(glowAlpha)
                            )
                        } else {
                            Icon(
                                imageVector = when {
                                    error != null -> Icons.Filled.BugReport
                                    downloadedFile != null -> Icons.Filled.CheckCircle
                                    updateAvailable -> RhythmIcons.Download
                                    isDownloading -> Icons.Filled.Autorenew
                                    else -> Icons.Filled.SystemUpdate
                                },
                                contentDescription = "App Updates",
                                tint = when {
                                    error != null -> MaterialTheme.colorScheme.error
                                    downloadedFile != null -> MaterialTheme.colorScheme.tertiary
                                    updateAvailable -> MaterialTheme.colorScheme.primary
                                    isDownloading -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier
                                    .size(48.dp)
//                                    .rotate(if (isDownloading) rotationAngle else 0f)
                                    .scale(
                                        when {
                                            downloadedFile != null -> successScale.value
                                            else -> 1f
                                        }
                                    )
                            )
                        }
                    }
                }

                // Title shows status
                Text(
                    text = when {
                        error != null -> context.getString(R.string.onboarding_update_check_failed)
                        downloadedFile != null -> context.getString(R.string.onboarding_ready_to_install)
                        isDownloading -> context.getString(R.string.onboarding_downloading_update)
                        isCheckingForUpdates -> context.getString(R.string.onboarding_checking_updates)
                        updateAvailable -> context.getString(R.string.onboarding_update_available)
                        else -> context.getString(R.string.onboarding_stay_up_to_date)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        error != null -> MaterialTheme.colorScheme.error
                        downloadedFile != null -> MaterialTheme.colorScheme.tertiary
                        updateAvailable -> MaterialTheme.colorScheme.primary
                        isCheckingForUpdates || isDownloading -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Description shows version info or default text
                Text(
                    text = when {
                        error != null -> error ?: "An error occurred"
                        downloadedFile != null -> "Version ${latestVersion?.versionName ?: "?"} is ready to install"
                        isDownloading -> "${downloadProgress.toInt()}% • ${((latestVersion?.apkSize ?: 0) * downloadProgress / 100).toLong().let { updaterViewModel.getReadableFileSize(it) }} / ${latestVersion?.let { updaterViewModel.getReadableFileSize(it.apkSize) } ?: ""}"
                        isCheckingForUpdates -> context.getString(R.string.fetching_latest_version)
                        updateAvailable -> "Version ${latestVersion?.versionName ?: "?"} • ${latestVersion?.let { updaterViewModel.getReadableFileSize(it.apkSize) } ?: ""}"
                        else -> context.getString(R.string.onboarding_update_default_desc)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        error != null -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        downloadedFile != null -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                        updateAvailable -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        isCheckingForUpdates || isDownloading -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Update Actions UI - Only buttons and progress
                val showUpdateActions = isDownloading || updateAvailable || downloadedFile != null || error != null

                AnimatedVisibility(
                    visible = showUpdateActions,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.9f),
                    exit = shrinkVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeOut() + scaleOut(targetScale = 0.9f),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    OnboardingExpressiveUpdateStatus(
                        isDownloading = isDownloading,
                        downloadProgress = downloadProgress,
                        downloadedFile = downloadedFile,
                        error = error,
                        updateAvailable = updateAvailable,
                        latestVersion = latestVersion,
                        updaterViewModel = updaterViewModel,
                        successScale = successScale,
                        onDownload = { updaterViewModel.downloadUpdate() },
                        onInstall = { updaterViewModel.installDownloadedApk() },
                        onCancelDownload = { updaterViewModel.cancelDownload() },
                        onDismissError = { updaterViewModel.clearError() },
                        onRetry = { updaterViewModel.checkForUpdates(force = true) }
                    )
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Update options (toggles and dropdowns)
            val haptic = LocalHapticFeedback.current
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Enable Updates toggle (NEW) - in Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    OnboardingSettingRow(
                        icon = Icons.Filled.SystemUpdate,
                        title = context.getString(R.string.onboarding_enable_updates_title),
                        description = context.getString(R.string.onboarding_enable_updates_desc),
                        isEnabled = updatesEnabled,
                        onToggle = { enabled ->
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            scope.launch {
                                appSettings.setUpdatesEnabled(enabled)
                            }
                        }
                    )
                }

                // Animated visibility for other update options based on updatesEnabled
                AnimatedVisibility(
                    visible = updatesEnabled,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Group all toggles and dropdowns in single card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column {
                                // Auto check for updates toggle
                                OnboardingSettingRow(
                                    icon = Icons.Filled.Autorenew,
                                    title = context.getString(R.string.onboarding_periodic_check_title),
                                    description = context.getString(R.string.onboarding_periodic_check_desc),
                                    isEnabled = autoCheckForUpdates,
                                    onToggle = { enabled ->
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        scope.launch {
                                            appSettings.setAutoCheckForUpdates(enabled)
                                        }
                                    }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )

                                // Update Notifications toggle
                                OnboardingSettingRow(
                                    icon = Icons.Filled.Notifications,
                                    title = context.getString(R.string.onboarding_update_notifications_title),
                                    description = context.getString(R.string.onboarding_update_notifications_desc),
                                    isEnabled = updateNotificationsEnabled,
                                    onToggle = { enabled ->
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        scope.launch {
                                            appSettings.setUpdateNotificationsEnabled(enabled)
                                        }
                                    }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )

                                // Smart Polling toggle
                                OnboardingSettingRow(
                                    icon = Icons.Filled.CloudSync,
                                    title = context.getString(R.string.onboarding_smart_polling_title),
                                    description = context.getString(R.string.onboarding_smart_polling_desc),
                                    isEnabled = useSmartUpdatePolling,
                                    onToggle = { enabled ->
                                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                                        scope.launch {
                                            appSettings.setUseSmartUpdatePolling(enabled)
                                        }
                                    }
                                )
                            }
                        }

                        // Check Interval dropdown
                        SettingsDropdownItem(
                            title = context.getString(R.string.onboarding_check_interval_title),
                            description = context.getString(R.string.onboarding_check_interval_desc),
                            selectedOption = when (updateCheckIntervalHours) {
                                1 -> context.getString(R.string.option_every_hour)
                                3 -> context.getString(R.string.option_every_3_hours)
                                6 -> context.getString(R.string.option_every_6_hours)
                                12 -> context.getString(R.string.option_every_12_hours)
                                24 -> context.getString(R.string.option_daily)
                                else -> context.getString(R.string.option_every_6_hours)
                            },
                            icon = Icons.Filled.Schedule,
                            options = listOf(
                                context.getString(R.string.option_every_hour),
                                context.getString(R.string.option_every_3_hours),
                                context.getString(R.string.option_every_6_hours),
                                context.getString(R.string.option_every_12_hours),
                                context.getString(R.string.option_daily)
                            ),
                            onOptionSelected = { selectedOption ->
                                val everyHour = context.getString(R.string.option_every_hour)
                                val every3Hours = context.getString(R.string.option_every_3_hours)
                                val every6Hours = context.getString(R.string.option_every_6_hours)
                                val every12Hours = context.getString(R.string.option_every_12_hours)
                                val daily = context.getString(R.string.option_daily)
                                val hours = when (selectedOption) {
                                    everyHour -> 1
                                    every3Hours -> 3
                                    every6Hours -> 6
                                    every12Hours -> 12
                                    daily -> 24
                                    else -> 6
                                }
                                scope.launch {
                                    appSettings.setUpdateCheckIntervalHours(hours)
                                }
                            }
                        )

                        // Update channel selection dropdown
                        SettingsDropdownItem(
                            title = context.getString(R.string.onboarding_update_channel_title),
                            description = context.getString(R.string.onboarding_update_channel_desc),
                            selectedOption = when (updateChannel) {
                                "stable" -> context.getString(R.string.option_stable)
                                "beta" -> context.getString(R.string.option_beta)
                                else -> context.getString(R.string.option_stable)
                            },
                            icon = when (updateChannel) {
                                "stable" -> Icons.Filled.Public
                                "beta" -> Icons.Filled.BugReport
                                else -> Icons.Filled.Public
                            },
                            options = listOf(context.getString(R.string.option_stable), context.getString(R.string.option_beta)),
                            onOptionSelected = { selectedOption ->
                                val stableOption = context.getString(R.string.option_stable)
                                val betaOption = context.getString(R.string.option_beta)
                                val channel = when (selectedOption) {
                                    stableOption -> "stable"
                                    betaOption -> "beta"
                                    else -> "stable"
                                }
                                scope.launch {
                                    appSettings.setUpdateChannel(channel)
                                }
                            }
                        )
                    }
                }
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Enhanced icon with animation - shows status
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                error != null -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                downloadedFile != null -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                updateAvailable -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCheckingForUpdates) {
                        NetworkOperationLoader(
                            modifier = Modifier
                                .size(40.dp)
                                .alpha(glowAlpha)
                        )
                    } else {
                        Icon(
                            imageVector = when {
                                error != null -> Icons.Filled.BugReport
                                downloadedFile != null -> Icons.Filled.CheckCircle
                                updateAvailable -> RhythmIcons.Download
                                isDownloading -> Icons.Filled.Autorenew
                                else -> Icons.Filled.SystemUpdate
                            },
                            contentDescription = "App Updates",
                            tint = when {
                                error != null -> MaterialTheme.colorScheme.error
                                downloadedFile != null -> MaterialTheme.colorScheme.tertiary
                                updateAvailable -> MaterialTheme.colorScheme.primary
                                isDownloading -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier
                                .size(40.dp)
//                                .rotate(if (isDownloading) rotationAngle else 0f)
                                .scale(
                                    when {
                                        downloadedFile != null -> successScale.value
                                        else -> 1f
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title shows status
            Text(
                text = when {
                    error != null -> context.getString(R.string.onboarding_update_check_failed)
                    downloadedFile != null -> context.getString(R.string.onboarding_ready_to_install)
                    isDownloading -> context.getString(R.string.onboarding_downloading_update)
                    isCheckingForUpdates -> context.getString(R.string.onboarding_checking_updates)
                    updateAvailable -> context.getString(R.string.onboarding_update_available)
                    else -> context.getString(R.string.onboarding_stay_up_to_date)
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    error != null -> MaterialTheme.colorScheme.error
                    downloadedFile != null -> MaterialTheme.colorScheme.tertiary
                    updateAvailable -> MaterialTheme.colorScheme.primary
                    isCheckingForUpdates || isDownloading -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Description shows version info or default text
            Text(
                text = when {
                    error != null -> error ?: "An error occurred"
                    downloadedFile != null -> "Version ${latestVersion?.versionName ?: "?"} is ready to install"
                    isDownloading -> "${downloadProgress.toInt()}% • ${((latestVersion?.apkSize ?: 0) * downloadProgress / 100).toLong().let { updaterViewModel.getReadableFileSize(it) }} / ${latestVersion?.let { updaterViewModel.getReadableFileSize(it.apkSize) } ?: ""}"
                    isCheckingForUpdates -> context.getString(R.string.fetching_latest_version)
                    updateAvailable -> "Version ${latestVersion?.versionName ?: "?"} • ${latestVersion?.let { updaterViewModel.getReadableFileSize(it.apkSize) } ?: ""}"
                    else -> context.getString(R.string.onboarding_update_default_desc)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    error != null -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    downloadedFile != null -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                    updateAvailable -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    isCheckingForUpdates || isDownloading -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Update Actions UI - Only buttons and progress
            val showUpdateActions = isDownloading || updateAvailable || downloadedFile != null || error != null

            AnimatedVisibility(
                visible = showUpdateActions,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn() + scaleIn(initialScale = 0.9f),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut() + scaleOut(targetScale = 0.9f),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                OnboardingExpressiveUpdateStatus(
                    isDownloading = isDownloading,
                    downloadProgress = downloadProgress,
                    downloadedFile = downloadedFile,
                    error = error,
                    updateAvailable = updateAvailable,
                    latestVersion = latestVersion,
                    updaterViewModel = updaterViewModel,
                    successScale = successScale,
                    onDownload = { updaterViewModel.downloadUpdate() },
                    onInstall = { updaterViewModel.installDownloadedApk() },
                    onCancelDownload = { updaterViewModel.cancelDownload() },
                    onDismissError = { updaterViewModel.clearError() },
                    onRetry = { updaterViewModel.checkForUpdates(force = true) }
                )
            }

            // Update options - using Card + OnboardingSettingRow pattern like gesture step
            val haptic = LocalHapticFeedback.current
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Enable Updates toggle in Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    OnboardingSettingRow(
                        icon = Icons.Filled.SystemUpdate,
                        title = context.getString(R.string.onboarding_enable_updates_title),
                        description = context.getString(R.string.onboarding_enable_updates_desc),
                        isEnabled = updatesEnabled,
                        onToggle = { enabled ->
                            scope.launch {
                                appSettings.setUpdatesEnabled(enabled)
                            }
                        }
                    )
                }

                // Animated visibility for other update options based on updatesEnabled
                AnimatedVisibility(
                    visible = updatesEnabled,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Toggles grouped in one Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column {
                                // Auto check for updates toggle
                                OnboardingSettingRow(
                                    icon = Icons.Filled.Autorenew,
                                    title = context.getString(R.string.onboarding_periodic_check_title),
                                    description = context.getString(R.string.onboarding_periodic_check_desc),
                                    isEnabled = autoCheckForUpdates,
                                    onToggle = { enabled ->
                                        scope.launch {
                                            appSettings.setAutoCheckForUpdates(enabled)
                                        }
                                    }
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                
                                // Update Notifications toggle
                                OnboardingSettingRow(
                                    icon = Icons.Filled.Notifications,
                                    title = context.getString(R.string.onboarding_update_notifications_title),
                                    description = context.getString(R.string.onboarding_update_notifications_desc),
                                    isEnabled = updateNotificationsEnabled,
                                    onToggle = { enabled ->
                                        scope.launch {
                                            appSettings.setUpdateNotificationsEnabled(enabled)
                                        }
                                    }
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                
                                // Smart Polling toggle
                                OnboardingSettingRow(
                                    icon = Icons.Filled.CloudSync,
                                    title = context.getString(R.string.onboarding_smart_polling_title),
                                    description = context.getString(R.string.onboarding_smart_polling_desc),
                                    isEnabled = useSmartUpdatePolling,
                                    onToggle = { enabled ->
                                        scope.launch {
                                            appSettings.setUseSmartUpdatePolling(enabled)
                                        }
                                    }
                                )
                            }
                        }

                        // Check Interval dropdown
                        SettingsDropdownItem(
                            title = context.getString(R.string.onboarding_check_interval_title),
                            description = context.getString(R.string.onboarding_check_interval_desc),
                            selectedOption = when (updateCheckIntervalHours) {
                                1 -> context.getString(R.string.option_every_hour)
                                3 -> context.getString(R.string.option_every_3_hours)
                                6 -> context.getString(R.string.option_every_6_hours)
                                12 -> context.getString(R.string.option_every_12_hours)
                                24 -> context.getString(R.string.option_daily)
                                else -> context.getString(R.string.option_every_6_hours)
                            },
                            icon = Icons.Filled.Schedule,
                            options = listOf(
                                context.getString(R.string.option_every_hour),
                                context.getString(R.string.option_every_3_hours),
                                context.getString(R.string.option_every_6_hours),
                                context.getString(R.string.option_every_12_hours),
                                context.getString(R.string.option_daily)
                            ),
                            onOptionSelected = { selectedOption ->
                                val everyHour = context.getString(R.string.option_every_hour)
                                val every3Hours = context.getString(R.string.option_every_3_hours)
                                val every6Hours = context.getString(R.string.option_every_6_hours)
                                val every12Hours = context.getString(R.string.option_every_12_hours)
                                val daily = context.getString(R.string.option_daily)
                                val hours = when (selectedOption) {
                                    everyHour -> 1
                                    every3Hours -> 3
                                    every6Hours -> 6
                                    every12Hours -> 12
                                    daily -> 24
                                    else -> 6
                                }
                                scope.launch {
                                    appSettings.setUpdateCheckIntervalHours(hours)
                                }
                            }
                        )

                        // Update channel selection dropdown
                        SettingsDropdownItem(
                            title = context.getString(R.string.onboarding_update_channel_title),
                            description = context.getString(R.string.onboarding_update_channel_desc),
                            selectedOption = when (updateChannel) {
                                "stable" -> context.getString(R.string.option_stable)
                                "beta" -> context.getString(R.string.option_beta)
                                else -> context.getString(R.string.option_stable)
                            },
                            icon = when (updateChannel) {
                                "stable" -> Icons.Filled.Public
                                "beta" -> Icons.Filled.BugReport
                                else -> Icons.Filled.Public
                            },
                            options = listOf(context.getString(R.string.option_stable), context.getString(R.string.option_beta)),
                            onOptionSelected = { selectedOption ->
                                val stableOption = context.getString(R.string.option_stable)
                                val betaOption = context.getString(R.string.option_beta)
                                val channel = when (selectedOption) {
                                    stableOption -> "stable"
                                    betaOption -> "beta"
                                    else -> "stable"
                                }
                                scope.launch {
                                    appSettings.setUpdateChannel(channel)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedUpdateOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                onToggle(!isEnabled)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.surfaceContainerLow
            else
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            OnboardingAnimatedSwitch(
                checked = isEnabled,
                onCheckedChange = { enabled ->
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                    onToggle(enabled)
                }
            )
        }
    }
}

@Composable
fun EnhancedUpdateChannelOption(
    channel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        onClick = onSelect
    )
}

@Composable
fun SettingsDropdownItem(
    title: String,
    description: String,
    selectedOption: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                showDropdown = true
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Selected option badge
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = selectedOption,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Show options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        // Enhanced Dropdown Menu
        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            options.forEach { option ->
                Surface(
                    color = if (selectedOption == option)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    else
                        androidx.compose.ui.graphics.Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedOption == option) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedOption == option)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when {
                                    option.contains("Track Number") -> Icons.Filled.FormatListNumbered
                                    option.contains("Title A-Z") || option.contains("Title Z-A") -> Icons.Filled.SortByAlpha
                                    option.contains("Duration") -> Icons.Filled.AccessTime
                                    option.contains("List") -> RhythmIcons.Actions.List
                                    option.contains("Grid") -> Icons.Filled.GridView
                                    option.contains("Hour") -> Icons.Filled.AccessTime
                                    option.contains("Stable") -> Icons.Filled.Public
                                    option.contains("Beta") -> Icons.Filled.BugReport
                                    option == "Home" -> Icons.Filled.Home
                                    option == "Library" -> Icons.Filled.LibraryMusic
                                    option == "API" -> Icons.Filled.Cloud
                                    option == "Embedded" -> Icons.Filled.LibraryMusic
                                    option == "Local" -> Icons.Filled.Folder
                                    else -> Icons.Filled.Check // Fallback
                                },
                                contentDescription = null,
                                tint = if (selectedOption == option)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                            onOptionSelected(option)
                            showDropdown = false
                        },
                        colors = androidx.compose.material3.MenuDefaults.itemColors(
                            textColor = if (selectedOption == option)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Step indicator dots with enhanced animations
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { index ->
                val isCompleted = index < currentStep
                val isCurrent = index == currentStep

                // Animated dot size and color
                val dotSize by animateDpAsState(
                    targetValue = when {
                        isCurrent -> 14.dp
                        isCompleted -> 10.dp
                        else -> 8.dp
                    },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "dotSize_$index"
                )

                val dotColor by animateColorAsState(
                    targetValue = when {
                        isCompleted -> MaterialTheme.colorScheme.primary
                        isCurrent -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    },
                    animationSpec = tween(300),
                    label = "dotColor_$index"
                )

                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(dotColor)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Show checkmark for completed steps
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isCompleted && !isCurrent,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(6.dp)
                        )
                    }

                    // Pulsing ring for current step
                    if (isCurrent) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse_$index")
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.4f,
                            animationSpec = infiniteRepeatable<Float>(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseScale_$index"
                        )

                        Box(
                            modifier = Modifier
                                .size(dotSize * pulseScale)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Animated progress text with smooth transitions
        androidx.compose.animation.AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                (slideInVertically { height -> height / 2 } + fadeIn()).togetherWith(
                    slideOutVertically { height -> -height / 2 } + fadeOut()
                )
            },
            label = "progressText"
        ) { step ->
            Text(
                text = "Step ${step + 1} of $totalSteps",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EnhancedMediaScanContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    onSkip: () -> Unit = {},
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Get current media scan mode preference
    val mediaScanMode by appSettings.mediaScanMode.collectAsState()
    val isBlacklistMode = mediaScanMode == "blacklist"
    val blacklistedFolders by appSettings.blacklistedFolders.collectAsState()
    val whitelistedFolders by appSettings.whitelistedFolders.collectAsState()

    // Folder picker launcher
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val docId = DocumentsContract.getTreeDocumentId(uri)
                    val split = docId.split(":")

                    if (split.size >= 2) {
                        val storageType = split[0]
                        val relativePath = split[1]

                        val fullPath = when (storageType) {
                            "primary" -> "/storage/emulated/0/$relativePath"
                            "home" -> "/storage/emulated/0/$relativePath"
                            else -> {
                                if (storageType.contains("-")) {
                                    "/storage/$storageType/$relativePath"
                                } else {
                                    "/storage/emulated/0/$relativePath"
                                }
                            }
                        }

                        if (isBlacklistMode) {
                            appSettings.addFolderToBlacklist(fullPath)
                        } else {
                            appSettings.addFolderToWhitelist(fullPath)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("OnboardingMediaScan", "Error parsing folder path", e)
                }
            }
        }
    }

    if (isTablet) {
        // Tablet layout: Left side - icon, description, media scan tips, action buttons; Right side - storage info and configuration
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, description, media scan tips, and action buttons
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enhanced icon with animation
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Media Scan Filtering",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_media_scan_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_media_scan_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Media scan tips card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
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
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.onboarding_how_it_works),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        MediaScanTipItem(
                            icon = Icons.Filled.Block,
                            text = context.getString(R.string.onboarding_media_scan_blacklist)
                        )
                        MediaScanTipItem(
                            icon = Icons.Filled.CheckCircle,
                            text = context.getString(R.string.onboarding_media_scan_whitelist)
                        )
                        MediaScanTipItem(
                            icon = Icons.Filled.Settings,
                            text = context.getString(R.string.onboarding_media_scan_configure_in_tuner)
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Scan mode settings and folder management
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FolderManagementCard(
                    isBlacklistMode = isBlacklistMode,
                    blacklistedFolders = blacklistedFolders,
                    whitelistedFolders = whitelistedFolders,
                    onModeChange = { useBlacklist ->
                        appSettings.setMediaScanMode(if (useBlacklist) "blacklist" else "whitelist")
                    },
                    onAddFolder = {
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        folderPickerLauncher.launch(intent)
                    },
                    onRemoveFolder = { folder ->
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        if (isBlacklistMode) {
                            appSettings.removeFolderFromBlacklist(folder)
                        } else {
                            appSettings.removeFolderFromWhitelist(folder)
                        }
                    }
                )
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Enhanced icon with animation
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Media Scan Filtering",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_media_scan_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_media_scan_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            FolderManagementCard(
                isBlacklistMode = isBlacklistMode,
                blacklistedFolders = blacklistedFolders,
                whitelistedFolders = whitelistedFolders,
                onModeChange = { useBlacklist ->
                    appSettings.setMediaScanMode(if (useBlacklist) "blacklist" else "whitelist")
                },
                onAddFolder = {
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    folderPickerLauncher.launch(intent)
                },
                onRemoveFolder = { folder ->
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    if (isBlacklistMode) {
                        appSettings.removeFolderFromBlacklist(folder)
                    } else {
                        appSettings.removeFolderFromWhitelist(folder)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Media scan tips card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(R.string.onboarding_how_it_works),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    MediaScanTipItem(
                        icon = Icons.Filled.Block,
                        text = context.getString(R.string.onboarding_media_scan_blacklist)
                    )
                    MediaScanTipItem(
                        icon = Icons.Filled.CheckCircle,
                        text = context.getString(R.string.onboarding_media_scan_whitelist)
                    )
                    MediaScanTipItem(
                        icon = Icons.Filled.Settings,
                        text = context.getString(R.string.onboarding_media_scan_configure_in_tuner)
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaScanSettingsCard(
    isBlacklistMode: Boolean,
    onModeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            OnboardingSettingRow(
                icon = Icons.Default.Block,
                title = context.getString(R.string.settings_blacklist_mode),
                description = context.getString(R.string.settings_blacklist_mode_desc),
                isEnabled = isBlacklistMode,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onModeChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Default.CheckCircle,
                title = context.getString(R.string.settings_whitelist_mode),
                description = context.getString(R.string.settings_whitelist_mode_desc),
                isEnabled = !isBlacklistMode,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onModeChange(!it) 
                }
            )
        }
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
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
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
fun MediaScanModeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    example: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Animated scale for press effect
    val cardScale = remember { Animatable(1f) }

    // Animated colors
    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        else
            MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "containerColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(300),
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale.value
                scaleY = cardScale.value
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                scope.launch {
                    cardScale.animateTo(0.95f, tween(100))
                    cardScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.LongPress)
                onSelect()
            },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(if (isSelected) 3.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
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
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = example,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EnhancedSetupFinishedContent(
    onFinish: () -> Unit,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    if (isTablet) {
        // Tablet layout: Left side - icon, description, next steps, action buttons; Right side - feature highlights and reminder
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Icon, description, next steps, and action buttons
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Success icon with animation
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(1000)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Setup Complete",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_complete_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_complete_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Next steps card
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
                                text = context.getString(R.string.onboarding_whats_next),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        NextStepItem(
                            icon = Icons.Filled.LibraryMusic,
                            text = context.getString(R.string.onboarding_next_browse)
                        )
                        NextStepItem(
                            icon = Icons.Filled.Queue,
                            text = context.getString(R.string.onboarding_next_create)
                        )
                        NextStepItem(
                            icon = Icons.Filled.GraphicEq,
                            text = context.getString(R.string.onboarding_next_finetune)
                        )
                        NextStepItem(
                            icon = Icons.Filled.Settings,
                            text = context.getString(R.string.onboarding_next_explore)
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side: Feature highlights and reminder
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Feature highlights
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SetupCompleteFeature(
                        icon = Icons.Filled.LibraryMusic,
                        title = context.getString(R.string.onboarding_library_configured),
                        description = context.getString(R.string.onboarding_library_configured_desc)
                    )

                    SetupCompleteFeature(
                        icon = Icons.Filled.Palette,
                        title = context.getString(R.string.onboarding_theme_applied),
                        description = context.getString(R.string.onboarding_theme_applied_desc)
                    )

                    SetupCompleteFeature(
                        icon = Icons.Filled.Backup,
                        title = context.getString(R.string.onboarding_backup_options),
                        description = context.getString(R.string.onboarding_backup_options_desc)
                    )
                }

                // Reminder text
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Text(
                        text = context.getString(R.string.onboarding_settings_change),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    } else {
        // Mobile layout: Single column
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Success icon with animation
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(
                    animationSpec = tween(1000)
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Setup Complete",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_complete_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_complete_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Feature highlights - vertically centered
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                SetupCompleteFeature(
                    icon = Icons.Filled.LibraryMusic,
                    title = context.getString(R.string.onboarding_library_configured),
                    description = context.getString(R.string.onboarding_library_configured_desc)
                )

                SetupCompleteFeature(
                    icon = Icons.Filled.Palette,
                    title = context.getString(R.string.onboarding_theme_applied),
                    description = context.getString(R.string.onboarding_theme_applied_desc)
                )

                SetupCompleteFeature(
                    icon = Icons.Filled.Backup,
                    title = context.getString(R.string.onboarding_backup_options),
                    description = context.getString(R.string.onboarding_backup_options_desc)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Next steps card
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
                            text = context.getString(R.string.onboarding_whats_next),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    NextStepItem(
                        icon = Icons.Filled.LibraryMusic,
                        text = context.getString(R.string.onboarding_next_browse)
                    )
                    NextStepItem(
                        icon = Icons.Filled.Queue,
                        text = context.getString(R.string.onboarding_next_create)
                    )
                    NextStepItem(
                        icon = Icons.Filled.GraphicEq,
                        text = context.getString(R.string.onboarding_next_finetune)
                    )
                    NextStepItem(
                        icon = Icons.Filled.Settings,
                        text = context.getString(R.string.onboarding_next_explore)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Reminder text
            Text(
                text = context.getString(R.string.onboarding_settings_change),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NextStepItem(
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
 * Material 3 Expressive Update Actions UI - Shows only buttons and progress
 * Simplified to display action buttons and download progress, status is shown in main heading
 */
@Composable
private fun OnboardingExpressiveUpdateStatus(
    isDownloading: Boolean,
    downloadProgress: Float,
    downloadedFile: java.io.File?,
    error: String?,
    updateAvailable: Boolean,
    latestVersion: AppVersion?,
    updaterViewModel: AppUpdaterViewModel,
    successScale: Animatable<Float, AnimationVector1D>,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onCancelDownload: () -> Unit,
    onDismissError: () -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    // Main Column - NO BOX OR CARD WRAPPING
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Download progress section - expressive, no containers
        AnimatedVisibility(
            visible = isDownloading,
            enter = expandVertically(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Progress header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InitializationLoader(
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = context.getString(R.string.onboarding_in_progress),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = "${downloadProgress.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp
                    )
                }

                // Plain accent color progress bar using Canvas - no Box container
                val accentColor = MaterialTheme.colorScheme.primary
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                ) {
                    val cornerRadius = 8.dp.toPx()
                    val progressWidth = size.width * (downloadProgress / 100f)

                    // Background track
                    drawRoundRect(
                        color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.2f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                    )

                    // Plain accent progress
                    if (progressWidth > 0) {
                        drawRoundRect(
                            color = accentColor,
                            size = androidx.compose.ui.geometry.Size(progressWidth, size.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                        )
                    }
                }
            }
        }

        // Action buttons - expressive, no containers
        AnimatedVisibility(
            visible = error != null || downloadedFile != null || updateAvailable || isDownloading,
            enter = expandVertically(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn() + scaleIn(initialScale = 0.9f),
            exit = shrinkVertically(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    error != null -> {
                        OutlinedButton(
                            onClick = onDismissError,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = context.getString(R.string.onboarding_dismiss),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Button(
                            onClick = onRetry,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = context.getString(R.string.onboarding_retry),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }

                    downloadedFile != null -> {
                        Button(
                            onClick = onInstall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(successScale.value),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = RoundedCornerShape(24.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 12.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = context.getString(R.string.install_update_now),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    isDownloading -> {
                        OutlinedButton(
                            onClick = onCancelDownload,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Block,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = context.getString(R.string.cancel_download),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }

                    updateAvailable && latestVersion?.apkAssetName?.isNotEmpty() == true -> {
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 12.dp
                            )
                        ) {
                            Icon(
                                imageVector = RhythmIcons.Download,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = context.getString(R.string.download_update),
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleMedium,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = latestVersion.let { updaterViewModel.getReadableFileSize(it.apkSize) },
                                    fontWeight = FontWeight.Normal,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subtle gradient divider - no Spacer container
        AnimatedVisibility(
            visible = isDownloading || updateAvailable || downloadedFile != null || error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            ) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color.Transparent,
                            androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.3f),
                            androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun SetupCompleteFeature(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Completed",
                
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// =====================================================
// NOTIFICATIONS ONBOARDING STEP
// =====================================================

@Composable
fun EnhancedNotificationsContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // Notification settings
    val useCustomNotification by appSettings.useCustomNotification.collectAsState()

    if (isTablet) {
        // Tablet layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left side
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_notifications_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_notifications_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Info card
                NotificationInfoCard()

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            // Right side - settings
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NotificationSettingsCard(
                    useCustomNotification = useCustomNotification,
                    onCustomNotificationChange = { appSettings.setUseCustomNotification(it) }
                )
            }
        }
    } else {
        // Mobile layout
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_notifications_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_notifications_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            NotificationSettingsCard(
                useCustomNotification = useCustomNotification,
                onCustomNotificationChange = { appSettings.setUseCustomNotification(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationInfoCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NotificationSettingsCard(
    useCustomNotification: Boolean,
    onCustomNotificationChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            OnboardingSettingRow(
                icon = Icons.Filled.Style,
                title = context.getString(R.string.onboarding_custom_notifications),
                description = context.getString(R.string.onboarding_custom_notifications_desc),
                isEnabled = useCustomNotification,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onCustomNotificationChange(it) 
                }
            )
        }
    }
}

@Composable
private fun NotificationInfoCard() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = context.getString(R.string.onboarding_notifications_info_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OnboardingTipItem(
                icon = Icons.Filled.MusicNote,
                text = context.getString(R.string.onboarding_notification_tip_1)
            )
            OnboardingTipItem(
                icon = Icons.Filled.SkipNext,
                text = context.getString(R.string.onboarding_notification_tip_2)
            )
            OnboardingTipItem(
                icon = Icons.Filled.Palette,
                text = context.getString(R.string.onboarding_notification_tip_3)
            )
        }
    }
}

// =====================================================
// GESTURES ONBOARDING STEP
// =====================================================

@Composable
fun EnhancedGesturesContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // Gesture settings
    val miniPlayerSwipeGestures by appSettings.miniPlayerSwipeGestures.collectAsState()
    val gesturePlayerSwipeDismiss by appSettings.gesturePlayerSwipeDismiss.collectAsState()
    val gesturePlayerSwipeTracks by appSettings.gesturePlayerSwipeTracks.collectAsState()
    val gestureArtworkDoubleTap by appSettings.gestureArtworkDoubleTap.collectAsState()
    val hapticFeedbackEnabled by appSettings.hapticFeedbackEnabled.collectAsState()

    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Gesture,
                            contentDescription = "Gestures",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_gestures_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_gestures_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                GestureTipsCard()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GestureSettingsCards(
                    miniPlayerSwipeGestures = miniPlayerSwipeGestures,
                    gesturePlayerSwipeDismiss = gesturePlayerSwipeDismiss,
                    gesturePlayerSwipeTracks = gesturePlayerSwipeTracks,
                    gestureArtworkDoubleTap = gestureArtworkDoubleTap,
                    hapticFeedbackEnabled = hapticFeedbackEnabled,
                    onMiniPlayerSwipeChange = { appSettings.setMiniPlayerSwipeGestures(it) },
                    onSwipeDismissChange = { appSettings.setGesturePlayerSwipeDismiss(it) },
                    onSwipeTracksChange = { appSettings.setGesturePlayerSwipeTracks(it) },
                    onDoubleTapChange = { appSettings.setGestureArtworkDoubleTap(it) },
                    onHapticFeedbackChange = { appSettings.setHapticFeedbackEnabled(it) }
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Gesture,
                        contentDescription = "Gestures",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_gestures_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_gestures_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            GestureSettingsCards(
                miniPlayerSwipeGestures = miniPlayerSwipeGestures,
                gesturePlayerSwipeDismiss = gesturePlayerSwipeDismiss,
                gesturePlayerSwipeTracks = gesturePlayerSwipeTracks,
                gestureArtworkDoubleTap = gestureArtworkDoubleTap,
                hapticFeedbackEnabled = hapticFeedbackEnabled,
                onMiniPlayerSwipeChange = { appSettings.setMiniPlayerSwipeGestures(it) },
                onSwipeDismissChange = { appSettings.setGesturePlayerSwipeDismiss(it) },
                onSwipeTracksChange = { appSettings.setGesturePlayerSwipeTracks(it) },
                onDoubleTapChange = { appSettings.setGestureArtworkDoubleTap(it) },
                onHapticFeedbackChange = { appSettings.setHapticFeedbackEnabled(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GestureTipsCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GestureSettingsCards(
    miniPlayerSwipeGestures: Boolean,
    gesturePlayerSwipeDismiss: Boolean,
    gesturePlayerSwipeTracks: Boolean,
    gestureArtworkDoubleTap: Boolean,
    hapticFeedbackEnabled: Boolean,
    onMiniPlayerSwipeChange: (Boolean) -> Unit,
    onSwipeDismissChange: (Boolean) -> Unit,
    onSwipeTracksChange: (Boolean) -> Unit,
    onDoubleTapChange: (Boolean) -> Unit,
    onHapticFeedbackChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // General Interaction Settings
        Text(
            text = "Interaction",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            OnboardingSettingRow(
                icon = Icons.Filled.TouchApp,
                title = "Haptic Feedback",
                description = "Enable vibration feedback for interactions",
                isEnabled = hapticFeedbackEnabled,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onHapticFeedbackChange(it) 
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mini Player Section
        Text(
            text = context.getString(R.string.onboarding_gestures_miniplayer),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            OnboardingSettingRow(
                icon = Icons.Rounded.Swipe,
                title = context.getString(R.string.onboarding_gesture_swipe),
                description = context.getString(R.string.onboarding_gesture_swipe_desc),
                isEnabled = miniPlayerSwipeGestures,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onMiniPlayerSwipeChange(it) 
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Full Player Section
        Text(
            text = context.getString(R.string.onboarding_gestures_player),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column {
                OnboardingSettingRow(
                    icon = Icons.Rounded.SwipeDown,
                    title = context.getString(R.string.onboarding_gesture_dismiss),
                    description = context.getString(R.string.onboarding_gesture_dismiss_desc),
                    isEnabled = gesturePlayerSwipeDismiss,
                    onToggle = { 
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        onSwipeDismissChange(it) 
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                OnboardingSettingRow(
                    icon = Icons.Rounded.SwipeLeft,
                    title = context.getString(R.string.onboarding_gesture_tracks),
                    description = context.getString(R.string.onboarding_gesture_tracks_desc),
                    isEnabled = gesturePlayerSwipeTracks,
                    onToggle = { 
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        onSwipeTracksChange(it) 
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                OnboardingSettingRow(
                    icon = Icons.Rounded.TouchApp,
                    title = context.getString(R.string.onboarding_gesture_doubletap),
                    description = context.getString(R.string.onboarding_gesture_doubletap_desc),
                    isEnabled = gestureArtworkDoubleTap,
                    onToggle = { 
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        onDoubleTapChange(it) 
                    }
                )
            }
        }
    }
}

@Composable
private fun GestureTipsCard() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                    
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = context.getString(R.string.onboarding_gestures_tips_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OnboardingTipItem(
                icon = Icons.Default.SwipeVertical,
                text = context.getString(R.string.onboarding_gesture_tip_1)
            )
            OnboardingTipItem(
                icon = Icons.Default.Speed,
                text = context.getString(R.string.onboarding_gesture_tip_2)
            )
        }
    }
}

// =====================================================
// WIDGETS ONBOARDING STEP
// =====================================================

@Composable
fun EnhancedWidgetsContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // Widget settings
    val showAlbumArt by appSettings.widgetShowAlbumArt.collectAsState()
    val showArtist by appSettings.widgetShowArtist.collectAsState()
    val showAlbum by appSettings.widgetShowAlbum.collectAsState()
    val autoUpdate by appSettings.widgetAutoUpdate.collectAsState()

    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Widgets,
                            contentDescription = "Widgets",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_widgets_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_widgets_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                WidgetTipsCard()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WidgetSettingsCard(
                    showAlbumArt = showAlbumArt,
                    showArtist = showArtist,
                    showAlbum = showAlbum,
                    autoUpdate = autoUpdate,
                    onAlbumArtChange = { appSettings.setWidgetShowAlbumArt(it) },
                    onArtistChange = { appSettings.setWidgetShowArtist(it) },
                    onAlbumChange = { appSettings.setWidgetShowAlbum(it) },
                    onAutoUpdateChange = { appSettings.setWidgetAutoUpdate(it) }
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Widgets,
                        contentDescription = "Widgets",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_widgets_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_widgets_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            WidgetSettingsCard(
                showAlbumArt = showAlbumArt,
                showArtist = showArtist,
                showAlbum = showAlbum,
                autoUpdate = autoUpdate,
                onAlbumArtChange = { appSettings.setWidgetShowAlbumArt(it) },
                onArtistChange = { appSettings.setWidgetShowArtist(it) },
                onAlbumChange = { appSettings.setWidgetShowAlbum(it) },
                onAutoUpdateChange = { appSettings.setWidgetAutoUpdate(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            WidgetTipsCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WidgetSettingsCard(
    showAlbumArt: Boolean,
    showArtist: Boolean,
    showAlbum: Boolean,
    autoUpdate: Boolean,
    onAlbumArtChange: (Boolean) -> Unit,
    onArtistChange: (Boolean) -> Unit,
    onAlbumChange: (Boolean) -> Unit,
    onAutoUpdateChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            OnboardingSettingRow(
                icon = Icons.Default.Image,
                title = context.getString(R.string.onboarding_widget_album_art),
                description = context.getString(R.string.onboarding_widget_album_art_desc),
                isEnabled = showAlbumArt,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onAlbumArtChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Default.Person,
                title = context.getString(R.string.onboarding_widget_artist),
                description = context.getString(R.string.onboarding_widget_artist_desc),
                isEnabled = showArtist,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onArtistChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Default.Album,
                title = context.getString(R.string.onboarding_widget_album),
                description = context.getString(R.string.onboarding_widget_album_desc),
                isEnabled = showAlbum,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onAlbumChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Default.Refresh,
                title = context.getString(R.string.onboarding_widget_auto_update),
                description = context.getString(R.string.onboarding_widget_auto_update_desc),
                isEnabled = autoUpdate,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onAutoUpdateChange(it) 
                }
            )
        }
    }
}

@Composable
private fun WidgetTipsCard() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = context.getString(R.string.onboarding_widgets_tips_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OnboardingTipItem(
                icon = Icons.Default.TouchApp,
                text = context.getString(R.string.onboarding_widget_tip_1)
            )
            OnboardingTipItem(
                icon = Icons.Default.AspectRatio,
                text = context.getString(R.string.onboarding_widget_tip_2)
            )
            OnboardingTipItem(
                icon = Icons.Default.Refresh,
                text = context.getString(R.string.onboarding_widget_tip_3)
            )
        }
    }
}

// =====================================================
// INTEGRATIONS ONBOARDING STEP
// =====================================================

@Composable
fun EnhancedIntegrationsContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // Integration settings
    val deezerApiEnabled by appSettings.deezerApiEnabled.collectAsState()
    val lrclibApiEnabled by appSettings.lrclibApiEnabled.collectAsState()
    val ytMusicApiEnabled by appSettings.ytMusicApiEnabled.collectAsState()
    val spotifyApiEnabled by appSettings.spotifyApiEnabled.collectAsState()
    val scrobblingEnabled by appSettings.scrobblingEnabled.collectAsState()
    val discordRichPresenceEnabled by appSettings.discordRichPresenceEnabled.collectAsState()
    val broadcastStatusEnabled by appSettings.broadcastStatusEnabled.collectAsState()

    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Api,
                            contentDescription = "Integrations",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_integrations_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_integrations_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                IntegrationsInfoCard()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IntegrationsSettingsCards(
                    deezerApiEnabled = deezerApiEnabled,
                    lrclibApiEnabled = lrclibApiEnabled,
                    ytMusicApiEnabled = ytMusicApiEnabled,
                    spotifyApiEnabled = spotifyApiEnabled,
                    scrobblingEnabled = scrobblingEnabled,
                    discordRichPresenceEnabled = discordRichPresenceEnabled,
                    broadcastStatusEnabled = broadcastStatusEnabled,
                    onDeezerChange = { appSettings.setDeezerApiEnabled(it) },
                    onLrcLibChange = { appSettings.setLrcLibApiEnabled(it) },
                    onYtMusicChange = { appSettings.setYTMusicApiEnabled(it) },
                    onSpotifyChange = { appSettings.setSpotifyApiEnabled(it) },
                    onScrobblingChange = { appSettings.setScrobblingEnabled(it) },
                    onDiscordChange = { appSettings.setDiscordRichPresenceEnabled(it) },
                    onBroadcastChange = { appSettings.setBroadcastStatusEnabled(it) }
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Api,
                        contentDescription = "Integrations",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_integrations_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_integrations_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            IntegrationsSettingsCards(
                deezerApiEnabled = deezerApiEnabled,
                lrclibApiEnabled = lrclibApiEnabled,
                ytMusicApiEnabled = ytMusicApiEnabled,
                spotifyApiEnabled = spotifyApiEnabled,
                scrobblingEnabled = scrobblingEnabled,
                discordRichPresenceEnabled = discordRichPresenceEnabled,
                broadcastStatusEnabled = broadcastStatusEnabled,
                onDeezerChange = { appSettings.setDeezerApiEnabled(it) },
                onLrcLibChange = { appSettings.setLrcLibApiEnabled(it) },
                onYtMusicChange = { appSettings.setYTMusicApiEnabled(it) },
                onSpotifyChange = { appSettings.setSpotifyApiEnabled(it) },
                onScrobblingChange = { appSettings.setScrobblingEnabled(it) },
                onDiscordChange = { appSettings.setDiscordRichPresenceEnabled(it) },
                onBroadcastChange = { appSettings.setBroadcastStatusEnabled(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            IntegrationsInfoCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IntegrationsSettingsCards(
    deezerApiEnabled: Boolean,
    lrclibApiEnabled: Boolean,
    ytMusicApiEnabled: Boolean,
    spotifyApiEnabled: Boolean,
    scrobblingEnabled: Boolean,
    discordRichPresenceEnabled: Boolean,
    broadcastStatusEnabled: Boolean,
    onDeezerChange: (Boolean) -> Unit,
    onLrcLibChange: (Boolean) -> Unit,
    onYtMusicChange: (Boolean) -> Unit,
    onSpotifyChange: (Boolean) -> Unit,
    onScrobblingChange: (Boolean) -> Unit,
    onDiscordChange: (Boolean) -> Unit,
    onBroadcastChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // API Services
        Text(
            text = context.getString(R.string.onboarding_integrations_apis),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column {
                // Deezer API - Only show if enabled in BuildConfig
                if (chromahub.rhythm.app.BuildConfig.ENABLE_DEEZER) {
                    OnboardingSettingRow(
                        icon = Icons.Default.Public,
                        title = context.getString(R.string.onboarding_integration_deezer),
                        description = context.getString(R.string.onboarding_integration_deezer_desc),
                        isEnabled = deezerApiEnabled,
                        onToggle = { 
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            onDeezerChange(it) 
                        }
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
                    OnboardingSettingRow(
                        icon = Icons.Default.Lyrics,
                        title = context.getString(R.string.onboarding_integration_lrclib),
                        description = context.getString(R.string.onboarding_integration_lrclib_desc),
                        isEnabled = lrclibApiEnabled,
                        onToggle = { 
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            onLrcLibChange(it) 
                        }
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
                    OnboardingSettingRow(
                        icon = Icons.Default.MusicVideo,
                        title = context.getString(R.string.onboarding_integration_ytmusic),
                        description = context.getString(R.string.onboarding_integration_ytmusic_desc),
                        isEnabled = ytMusicApiEnabled,
                        onToggle = { 
                            HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                            onYtMusicChange(it) 
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Social Features
        Text(
            text = context.getString(R.string.onboarding_integrations_social),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column {
                OnboardingSettingRow(
                    icon = Icons.Default.Share,
                    title = context.getString(R.string.onboarding_integration_scrobbling),
                    description = context.getString(R.string.onboarding_integration_scrobbling_desc),
                    isEnabled = scrobblingEnabled,
                    onToggle = { 
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        onScrobblingChange(it) 
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                OnboardingSettingRow(
                    icon = Icons.Default.Gamepad,
                    title = context.getString(R.string.onboarding_integration_discord),
                    description = context.getString(R.string.onboarding_integration_discord_desc),
                    isEnabled = discordRichPresenceEnabled,
                    onToggle = { 
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        onDiscordChange(it) 
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                OnboardingSettingRow(
                    icon = Icons.Default.Cast,
                    title = context.getString(R.string.onboarding_integration_broadcast),
                    description = context.getString(R.string.onboarding_integration_broadcast_desc),
                    isEnabled = broadcastStatusEnabled,
                    onToggle = { 
                        HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                        onBroadcastChange(it) 
                    }
                )
            }
        }
    }
}

@Composable
private fun IntegrationsInfoCard() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                    
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = context.getString(R.string.onboarding_integrations_info_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = context.getString(R.string.onboarding_integrations_info_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 20.sp
            )
        }
    }
}

// =====================================================
// RHYTHM STATS ONBOARDING STEP
// =====================================================

@Composable
fun EnhancedRhythmStatsContent(
    onNextStep: () -> Unit,
    appSettings: AppSettings,
    isTablet: Boolean = false,
    backButton: @Composable (() -> Unit)? = null,
    nextButton: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // Stats settings
    val homeShowListeningStats by appSettings.homeShowListeningStats.collectAsState()
    val enableRatingSystem by appSettings.enableRatingSystem.collectAsState()

    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoGraph,
                            contentDescription = "Rhythm Stats",
                            
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    text = context.getString(R.string.onboarding_stats_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = context.getString(R.string.onboarding_stats_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                StatsFeaturesAndInfoCard()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    backButton?.invoke()
                    nextButton()
                }
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsSettingsCard(
                    showOnHome = homeShowListeningStats,
                    enableRating = enableRatingSystem,
                    onShowOnHomeChange = { appSettings.setHomeShowListeningStats(it) },
                    onEnableRatingChange = { appSettings.setEnableRatingSystem(it) }
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoGraph,
                        contentDescription = "Rhythm Stats",
                        
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = context.getString(R.string.onboarding_stats_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = context.getString(R.string.onboarding_stats_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            StatsSettingsCard(
                showOnHome = homeShowListeningStats,
                enableRating = enableRatingSystem,
                onShowOnHomeChange = { appSettings.setHomeShowListeningStats(it) },
                onEnableRatingChange = { appSettings.setEnableRatingSystem(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatsFeaturesAndInfoCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatsSettingsCard(
    showOnHome: Boolean,
    enableRating: Boolean,
    onShowOnHomeChange: (Boolean) -> Unit,
    onEnableRatingChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            OnboardingSettingRow(
                icon = Icons.Default.Home,
                title = context.getString(R.string.onboarding_stats_show_home),
                description = context.getString(R.string.onboarding_stats_show_home_desc),
                isEnabled = showOnHome,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onShowOnHomeChange(it) 
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            OnboardingSettingRow(
                icon = Icons.Default.Star,
                title = context.getString(R.string.onboarding_stats_rating),
                description = context.getString(R.string.onboarding_stats_rating_desc),
                isEnabled = enableRating,
                onToggle = { 
                    HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                    onEnableRatingChange(it) 
                }
            )
        }
    }
}

@Composable
private fun StatsFeaturesAndInfoCard() {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Features section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Stars,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = context.getString(R.string.onboarding_stats_features_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OnboardingTipItem(
                icon = Icons.Default.AccessTime,
                text = context.getString(R.string.onboarding_stats_feature_1)
            )
            OnboardingTipItem(
                icon = Icons.Default.MusicNote,
                text = context.getString(R.string.onboarding_stats_feature_2)
            )
            OnboardingTipItem(
                icon = Icons.Default.Person,
                text = context.getString(R.string.onboarding_stats_feature_3)
            )
            OnboardingTipItem(
                icon = Icons.Default.Album,
                text = context.getString(R.string.onboarding_stats_feature_4)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
//            HorizontalDivider(
//                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // How it works section
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = Icons.Filled.Info,
//                    contentDescription = null,
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//                Text(
//                    text = context.getString(R.string.onboarding_stats_info_title),
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            }
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                text = context.getString(R.string.onboarding_stats_info_desc),
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onPrimaryContainer,
//                lineHeight = 20.sp
//            )
        }
    }
}

// =====================================================
// SHARED COMPOSABLES
// =====================================================

@Composable
fun OnboardingSettingRow(
    icon: ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Animation states
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "onboarding_setting_scale"
    )
    
    val iconBackgroundColor by animateColorAsState(
        targetValue = when {
            isEnabled -> MaterialTheme.colorScheme.primaryContainer
            isPressed -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "onboarding_icon_bg_color"
    )
    
    val iconTintColor by animateColorAsState(
        targetValue = when {
            isEnabled -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "onboarding_icon_tint_color"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container with expressive design
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(34.dp),
            color = iconBackgroundColor,
            tonalElevation = if (isEnabled) 2.dp else 0.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
        
        OnboardingAnimatedSwitch(
            checked = isEnabled,
            onCheckedChange = { 
                HapticUtils.performHapticFeedback(context, haptic, HapticFeedbackType.TextHandleMove)
                onToggle(it) 
            }
        )
    }
}

@Composable
fun OnboardingAnimatedSwitch(
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
fun OnboardingTipItem(
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