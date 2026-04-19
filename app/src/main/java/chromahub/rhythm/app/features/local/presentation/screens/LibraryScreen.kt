// Experimental API opt-ins required for:
// - Material3 APIs (ModalBottomSheet, ExtendedFloatingActionButton behaviors)
// - Foundation APIs (HorizontalPager, stickyHeader in LazyColumn)
// These will become stable in future Compose releases
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
package chromahub.rhythm.app.features.local.presentation.screens

import kotlin.math.abs

import android.widget.Toast
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import java.io.File
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import kotlin.collections.sortedBy
import kotlin.collections.mutableListOf
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import android.net.Uri
import android.util.Log
import chromahub.rhythm.app.util.PlaylistImportExportUtils
import chromahub.rhythm.app.util.AppRestarter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import chromahub.rhythm.app.ui.UiConstants
import chromahub.rhythm.app.ui.theme.MusicDimensions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import chromahub.rhythm.app.ui.LocalMiniPlayerPadding
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import chromahub.rhythm.app.R
import chromahub.rhythm.app.shared.data.model.Album
import chromahub.rhythm.app.shared.data.model.Artist
import chromahub.rhythm.app.shared.data.model.Playlist
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.shared.data.model.AlbumViewType
import chromahub.rhythm.app.shared.data.model.ArtistViewType
import chromahub.rhythm.app.shared.data.model.PlaylistViewType
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.AddToPlaylistBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.dialogs.CreatePlaylistDialog
import chromahub.rhythm.app.features.local.presentation.components.player.MiniPlayer
import chromahub.rhythm.app.shared.presentation.components.common.M3PlaceholderType
import chromahub.rhythm.app.shared.presentation.components.common.rememberExpressiveShapeFor
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveShapeTarget
import chromahub.rhythm.app.features.local.presentation.components.dialogs.BulkPlaylistExportDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.PlaylistImportDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.PlaylistOperationProgressDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.PlaylistOperationResultDialog
import chromahub.rhythm.app.features.local.presentation.components.dialogs.AppRestartDialog
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.SongInfoBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.AlbumBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.ArtistBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.settings.LibraryTabOrderBottomSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.BatchEditTagsSheet
import chromahub.rhythm.app.features.local.presentation.components.bottomsheets.MultiSelectionBottomSheet
import chromahub.rhythm.app.util.ImageUtils
import chromahub.rhythm.app.util.M3ImageUtils
import chromahub.rhythm.app.util.HapticUtils
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import androidx.compose.material3.ListItemDefaults
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material.icons.rounded.ArrowCircleDown
import androidx.compose.material.icons.rounded.ArrowCircleUp
import androidx.compose.ui.text.font.FontFamily
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.features.local.presentation.components.player.PlayingEqIcon
import chromahub.rhythm.app.shared.presentation.components.common.ContentLoadingIndicator
import chromahub.rhythm.app.shared.presentation.components.common.DataProcessingLoader
import chromahub.rhythm.app.shared.presentation.components.common.AlphabetBar
import chromahub.rhythm.app.shared.presentation.components.common.ScrollToTopButton
import chromahub.rhythm.app.shared.presentation.components.common.TabAnimation
import chromahub.rhythm.app.util.AudioFormatDetector
import chromahub.rhythm.app.util.AudioQualityDetector
import chromahub.rhythm.app.shared.presentation.components.common.ActionProgressLoader
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveButtonGroup
import chromahub.rhythm.app.shared.presentation.components.common.ButtonGroupStyle
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveGroupButton
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveFilledIconButton
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveShapes


enum class LibraryTab { SONGS, PLAYLISTS, ALBUMS, ARTISTS, EXPLORER }

// Playlist sort order enum for library tab
enum class LibraryPlaylistSortOrder {
    NAME_ASC,
    NAME_DESC,
    DATE_CREATED_ASC,
    DATE_CREATED_DESC,
    SONG_COUNT_ASC,
    SONG_COUNT_DESC
}

@Composable
fun LibraryScreen(
    songs: List<Song>,
    albums: List<Album>,
    playlists: List<Playlist>,
    artists: List<Artist>,
    currentSong: Song?,
    isPlaying: Boolean,
    progress: Float,
    onSongClick: (Song) -> Unit,
    onPlayPause: () -> Unit,
    onPlayerClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onAddPlaylist: () -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onAlbumShufflePlay: (Album) -> Unit = { _ -> },
    onPlayQueue: (List<Song>) -> Unit = { _ -> }, // Added for playing a list of songs with queue replacement
    onPlayQueueFromIndex: (List<Song>, Int) -> Unit = { _, _ -> }, // Added for playing from specific index
    onShuffleQueue: (List<Song>) -> Unit = { _ -> }, // Added for shuffling and playing a list of songs
    onAlbumBottomSheetClick: (Album) -> Unit = { _ -> }, // Added for opening album bottom sheet
    onSort: () -> Unit = {},
    onRefreshClick: () -> Unit, // Changed from onSearchClick to onRefreshClick
    onAddSongToPlaylist: (Song, String) -> Unit = { _, _ -> },
    onCreatePlaylist: (String) -> Unit = { _ -> },
    sortOrder: MusicViewModel.SortOrder = MusicViewModel.SortOrder.TITLE_ASC,
    onSkipNext: () -> Unit = {},
    onAddToQueue: (Song) -> Unit,
    initialTab: LibraryTab = LibraryTab.SONGS,
    musicViewModel: MusicViewModel, // Add MusicViewModel as a parameter
    onExportAllPlaylists: ((PlaylistImportExportUtils.PlaylistExportFormat, Boolean, Uri?, (Result<String>) -> Unit) -> Unit)? = null,
    onImportPlaylist: ((Uri, (Result<String>) -> Unit, (() -> Unit)?) -> Unit)? = null,
    onRestartApp: (() -> Unit)? = null,
    onNavigateToArtist: (Artist) -> Unit = {}
) {
    val context = LocalContext.current
    val appSettings = remember { AppSettings.getInstance(context) }
    val tabOrder by appSettings.libraryTabOrder.collectAsState()
    val hiddenTabs by appSettings.hiddenLibraryTabs.collectAsState()
    val enableRatingSystem by appSettings.enableRatingSystem.collectAsState()
    
    // Map tab IDs to display names, filtering out hidden tabs
    val tabs = remember(tabOrder, hiddenTabs) {
        tabOrder
            .filter { !hiddenTabs.contains(it) }
            .map { tabId ->
                when (tabId) {
                    "SONGS" -> "Songs"
                    "PLAYLISTS" -> "Playlists"
                    "ALBUMS" -> "Albums"
                    "ARTISTS" -> "Artists"
                    "EXPLORER" -> "Explorer"
                    else -> tabId
                }
            }
    }
    
    // Create a list of visible tab IDs (after filtering hidden tabs)
    val visibleTabIds = remember(tabOrder, hiddenTabs) {
        tabOrder.filter { !hiddenTabs.contains(it) }
    }
    
    // Find initial tab index based on the visible tabs
    val initialTabIndex = remember(visibleTabIds, initialTab) {
        val tabId = initialTab.name
        visibleTabIds.indexOf(tabId).takeIf { it >= 0 } ?: 0
    }
    
    var selectedTabIndex by rememberSaveable { mutableStateOf(initialTabIndex) }
    val pagerState = rememberPagerState(initialPage = selectedTabIndex) { tabs.size }
    val tabRowState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    
    // Track previous tab order to detect changes
    var previousVisibleTabIds by remember { mutableStateOf(visibleTabIds) }
    
    // Sync pager with selected tab when tabs change (hide/unhide/reorder)
    LaunchedEffect(tabs.size, visibleTabIds) {
        // Check if tab configuration has changed (not just on initial composition)
        val hasTabsChanged = previousVisibleTabIds != visibleTabIds
        
        if (hasTabsChanged) {
            // Tab configuration has changed - always reset to first tab
            selectedTabIndex = 0
            pagerState.scrollToPage(0)
            // Scroll tab row to start
            tabRowState.animateScrollToItem(0)
            previousVisibleTabIds = visibleTabIds
        } else if (selectedTabIndex >= tabs.size) {
            // If current selected tab is out of bounds, reset to first tab
            selectedTabIndex = 0
            pagerState.scrollToPage(0)
        }
    }
    
    // Sync pager state with selected tab index
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }
    
    // Auto-scroll tab row to show selected tab when returning to this screen
    LaunchedEffect(selectedTabIndex) {
        tabRowState.animateScrollToItem(selectedTabIndex)
    }
    
    // Dialog and bottom sheet states
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistSheet by remember { mutableStateOf(false) }
    var showAlbumBottomSheet by remember { mutableStateOf(false) }
    var showSongInfoSheet by remember { mutableStateOf(false) }
    var showBulkExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showOperationProgress by remember { mutableStateOf(false) }
    var operationInProgress by remember { mutableStateOf("") }
    var operationResult by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    
    // Pending write request for metadata editing (Android 11+)
    val pendingWriteRequest by musicViewModel.pendingWriteRequest.collectAsState()
    
    // Write permission launcher for Android 11+ metadata editing
    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // User granted permission, complete the write
            musicViewModel.completeMetadataWriteAfterPermission(
                onSuccess = {
                    Toast.makeText(context, "Metadata saved successfully!", Toast.LENGTH_SHORT).show()
                },
                onError = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            // User denied permission
            musicViewModel.cancelPendingMetadataWrite()
            Toast.makeText(context, "Permission denied. Changes saved to library only.", Toast.LENGTH_LONG).show()
        }
    }
    
    // Import/Export related state
    var operationProgressText by remember { mutableStateOf("") }
    var operationError by remember { mutableStateOf<String?>(null) }
    var showExportResultDialog by remember { mutableStateOf(false) }
    var exportResultsData by remember { mutableStateOf<List<Pair<String, Boolean>>?>(null) }
    var showImportResultDialog by remember { mutableStateOf(false) }
    var importResult by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }
    
    // Explorer reload trigger
    var explorerReloadTrigger by remember { mutableStateOf(0) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    val addToPlaylistSheetState = rememberModalBottomSheetState()
    val albumBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    
    // Multi-selection state
    val multiSelectionState = remember { chromahub.rhythm.app.features.local.presentation.viewmodel.MultiSelectionStateHolder() }
    val selectedSongs by multiSelectionState.selectedSongs.collectAsState()
    val isSelectionMode by multiSelectionState.isSelectionMode.collectAsState()
    val selectedSongIds by multiSelectionState.selectedSongIds.collectAsState()
    var showMultiSelectionSheet by remember { mutableStateOf(false) }
    var showBatchEditSheet by remember { mutableStateOf(false) }
    
    // Multi-selection callbacks
    val onSongLongPress: (Song) -> Unit = remember(multiSelectionState) {
        { song -> multiSelectionState.toggleSelection(song) }
    }
    
    val onSongSelectionToggle: (Song) -> Unit = remember(multiSelectionState) {
        { song -> multiSelectionState.toggleSelection(song) }
    }
    
    val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()
    
    // TopAppBar scroll behavior
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // FAB visibility based on scroll
    val fabVisibility by remember {
        derivedStateOf {
            scrollBehavior.state.collapsedFraction < 0.5f
        }
    }

    // FAB menu state
    var showPlaylistFabMenu by remember { mutableStateOf(false) }

    // Function to close FAB menu from other places
    val closeFabMenu = {
        showPlaylistFabMenu = false
    }

    // Handle FAB menu item clicks - close menu after action
    val onCreatePlaylistFromFab: () -> Unit = {
        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
        showCreatePlaylistDialog = true
        showPlaylistFabMenu = false
    }

    val onImportPlaylistFromFab: (() -> Unit)? = if (onImportPlaylist != null) {
        {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            showImportDialog = true
            showPlaylistFabMenu = false
        }
    } else null

    val onExportPlaylistsFromFab: (() -> Unit)? = if (onExportAllPlaylists != null) {
        {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            showBulkExportDialog = true
            showPlaylistFabMenu = false
        }
    } else null

    // Lambda to pass to PlaylistFabMenu for import
    val onImportPlaylistForFab: (() -> Unit)? = if (onImportPlaylist != null) {
        {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            showImportDialog = true
        }
    } else null

    // Lambda to pass to PlaylistFabMenu for manage - navigates to Tuner > Playlists
    val onManagePlaylists: (() -> Unit) = {
        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
        // Navigation to Tuner Playlists should be handled by parent (RhythmNavigation)
        // This is a placeholder that should be replaced with actual navigation
    }

    // Sync tabs with pager - only animate when tab button is clicked
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
    }
    


    // Update selectedTabIndex when pager settles on a new page (handles swiping)
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && selectedTabIndex != pagerState.currentPage) {
            selectedTabIndex = pagerState.currentPage
            // Auto-scroll tab buttons to show selected tab
            tabRowState.animateScrollToItem(pagerState.currentPage)
        }
    }

    // Handle dialogs
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onConfirm = { name ->
                onCreatePlaylist(name)
                showCreatePlaylistDialog = false
            }
        )
    }
    
    if (showSongInfoSheet && selectedSong != null) {
        // Get the latest version of the song from the songs list
        val displaySong = songs.find { it.id == selectedSong!!.id } ?: selectedSong
        
        SongInfoBottomSheet(
            song = displaySong!!,
            onDismiss = { showSongInfoSheet = false },
            appSettings = appSettings,
            onEditSong = { title, artist, album, genre, year, trackNumber, artworkUri, removeArtwork ->
                // Use the ViewModel's new metadata saving function with callbacks
                musicViewModel.saveMetadataChanges(
                    song = displaySong!!,
                    title = title,
                    artist = artist,
                    album = album,
                    genre = genre,
                    year = year,
                    trackNumber = trackNumber,
                    artworkUri = artworkUri,
                    removeArtwork = removeArtwork,
                    onSuccess = { fileWriteSucceeded ->
                        if (fileWriteSucceeded) {
                            Toast.makeText(context, "Metadata saved successfully to file!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Don't show error here - permission request may be triggered
                        }
                        // Don't close the sheet - let the user see the updated info
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    },
                    onPermissionRequired = { pendingRequest ->
                        // Launch the system permission dialog for Android 11+
                        try {
                            val intentSenderRequest = androidx.activity.result.IntentSenderRequest.Builder(
                                pendingRequest.intentSender
                            ).build()
                            writePermissionLauncher.launch(intentSenderRequest)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Failed to request permission: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            musicViewModel.cancelPendingMetadataWrite()
                        }
                    }
                )
            }
        )
    }
    
    // Use bottom sheet instead of dialog
    if (showAddToPlaylistSheet && selectedSong != null) {
        AddToPlaylistBottomSheet(
            song = selectedSong!!,
            playlists = playlists,
            onDismissRequest = { showAddToPlaylistSheet = false },
            onAddToPlaylist = { playlist ->
                onAddSongToPlaylist(selectedSong!!, playlist.id)
                scope.launch {
                    addToPlaylistSheetState.hide()
                }.invokeOnCompletion {
                    if (!addToPlaylistSheetState.isVisible) {
                        showAddToPlaylistSheet = false
                    }
                }
            },
            onCreateNewPlaylist = {
                scope.launch {
                    addToPlaylistSheetState.hide()
                }.invokeOnCompletion {
                    if (!addToPlaylistSheetState.isVisible) {
                        showAddToPlaylistSheet = false
                        showCreatePlaylistDialog = true
                    }
                }
            },
            sheetState = addToPlaylistSheetState
        )
    }
    
    // Album bottom sheet
    if (showAlbumBottomSheet && selectedAlbum != null) {
        AlbumBottomSheet(
            album = selectedAlbum!!,
            onDismiss = { showAlbumBottomSheet = false },
            onSongClick = onSongClick,
            onPlayAll = { songs ->
                // Play the sorted album songs using proper queue replacement
                if (songs.isNotEmpty()) {
                    onPlayQueue(songs) // Use the new queue replacement callback
                } else {
                    selectedAlbum?.let { onAlbumClick(it) }
                }
            },
            onShufflePlay = { songs ->
                // Play shuffled sorted album songs with proper queue replacement
                if (songs.isNotEmpty()) {
                    onShuffleQueue(songs) // Use the new shuffle queue callback
                } else {
                    selectedAlbum?.let { onAlbumShufflePlay(it) }
                }
            },
            onAddToQueue = onAddToQueue,
            onAddSongToPlaylist = { song ->
                selectedSong = song
                scope.launch {
                    albumBottomSheetState.hide()
                }.invokeOnCompletion {
                    if (!albumBottomSheetState.isVisible) {
                        showAlbumBottomSheet = false
                        showAddToPlaylistSheet = true
                    }
                }
            },
            onPlayerClick = onPlayerClick,
            sheetState = albumBottomSheetState,
            haptics = haptics,
            onPlayNext = { song -> musicViewModel.playNext(song) },
            onToggleFavorite = { song -> musicViewModel.toggleFavorite(song) },
            favoriteSongs = musicViewModel.favoriteSongs.collectAsState().value,
            onShowSongInfo = { song ->
                selectedSong = song
                showSongInfoSheet = true
            },
            onAddToBlacklist = { song ->
                appSettings.addToBlacklist(song.id)
            },
            currentSong = currentSong,
            isPlaying = isPlaying
        )
    }
    
    // Playlist Management now handled in Tuner > Playlists settings
    
    // Track library refreshing state for pull-to-refresh
    val isLibraryRefreshing by musicViewModel.isLibraryRefreshing.collectAsState()
    val scanProgress by musicViewModel.scanProgress.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val isTabletLayout = LocalConfiguration.current.screenWidthDp >= 600
    val libraryBottomOverlayPadding =
        if (isTabletLayout) 0.dp else (MusicDimensions.bottomNavigationHeight + 16.dp)
    
    // Update refreshing state based on library refreshing
    LaunchedEffect(isLibraryRefreshing) {
        isRefreshing = isLibraryRefreshing
    }
    
    // BackHandler for selection mode
    BackHandler(enabled = isSelectionMode) {
        multiSelectionState.clearSelection()
    }
    
    // Multi-selection bottom sheet - only show when explicitly requested
    if (showMultiSelectionSheet && selectedSongs.isNotEmpty()) {
        MultiSelectionBottomSheet(
            selectedSongs = selectedSongs,
            favoriteSongIds = favoriteSongs.toSet(),
            onDismiss = {
                showMultiSelectionSheet = false
                multiSelectionState.clearSelection()
            },
            onPlayAll = {
                onPlayQueue(selectedSongs)
                multiSelectionState.clearSelection()
            },
            onAddToQueue = {
                selectedSongs.forEach { song -> onAddToQueue(song) }
                multiSelectionState.clearSelection()
            },
            onPlayNext = {
                selectedSongs.reversed().forEach { song -> musicViewModel.playNext(song) }
                multiSelectionState.clearSelection()
            },
            onAddToPlaylist = {
                // Open playlist picker with first selected song for now
                // In future, could support adding multiple songs
                selectedSong = selectedSongs.firstOrNull()
                showMultiSelectionSheet = false
                showAddToPlaylistSheet = true
            },
            onToggleLikeAll = { shouldLike ->
                selectedSongs.forEach { song ->
                    val isFavorited = favoriteSongs.contains(song.id)
                    if (shouldLike != isFavorited) {
                        musicViewModel.toggleFavorite(song)
                    }
                }
            },
            onAddToBlacklist = {
                // Add all selected songs to blacklist
                selectedSongs.forEach { song ->
                    appSettings.addToBlacklist(song.id)
                }
            },
            onBatchEditTags = {
                showMultiSelectionSheet = false
                showBatchEditSheet = true
            }
        )
    }

    // Batch edit tags sheet
    if (showBatchEditSheet && selectedSongs.isNotEmpty()) {
        BatchEditTagsSheet(
            selectedSongs = selectedSongs,
            onDismiss = {
                showBatchEditSheet = false
                multiSelectionState.clearSelection()
            },
            onSave = { artist, album, genre, year, artworkUri, removeArtwork ->
                musicViewModel.batchEditMetadata(
                    songs = selectedSongs,
                    artist = artist,
                    album = album,
                    genre = genre,
                    year = year,
                    artworkUri = artworkUri,
                    removeArtwork = removeArtwork,
                    onProgress = { _, _ -> },
                    onComplete = { successCount, failCount ->
                        showBatchEditSheet = false
                        multiSelectionState.clearSelection()
                        val msg = if (failCount == 0) "Updated $successCount songs"
                                  else "Updated $successCount songs, $failCount failed"
                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(5.dp)) // Add more padding before the header starts
                
                LargeTopAppBar(
                navigationIcon = { },
                title = {
                    val collapsedFraction = scrollBehavior.state.collapsedFraction
                    val fontSize = (24 + (32 - 24) * (1 - collapsedFraction)).sp // Interpolate between 24sp and 32sp

                    Text(
                        text = context.getString(R.string.library_title),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = fontSize
                        ),
                        modifier = Modifier.padding(start = 14.dp) // Adjust start padding for title
                    )
                },
                actions = {
                    // Tab-specific actions moved from section headers
                    when (visibleTabIds.getOrNull(selectedTabIndex)) {
                        "ALBUMS" -> {
                            // Enhanced Album view toggle
                            val albumViewType by appSettings.albumViewType.collectAsState()
                            
                            // Animation for button press
                            val buttonScale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "albumToggleScale"
                            )
                            
                            FilledTonalIconButton(
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    val newViewType = if (albumViewType == AlbumViewType.LIST) AlbumViewType.GRID else AlbumViewType.LIST
                                    appSettings.setAlbumViewType(newViewType)
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                modifier = Modifier
                                    .size(42.dp)
                                    .graphicsLayer {
                                        scaleX = buttonScale
                                        scaleY = buttonScale
                                    }
                            ) {
                                Icon(
                                    imageVector = if (albumViewType == AlbumViewType.LIST) Icons.Default.GridView else Icons.AutoMirrored.Rounded.ViewList,
                                    contentDescription = if (albumViewType == AlbumViewType.LIST) "Switch to Grid View" else "Switch to List View",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        "ARTISTS" -> {
                            // Enhanced Artist view toggle  
                            val artistViewType by appSettings.artistViewType.collectAsState()
                            
                            // Animation for button press
                            val buttonScale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "artistToggleScale"
                            )
                            
                            FilledTonalIconButton(
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    val newViewType = if (artistViewType == ArtistViewType.LIST) ArtistViewType.GRID else ArtistViewType.LIST
                                    appSettings.setArtistViewType(newViewType)
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                modifier = Modifier
                                    .size(42.dp)
                                    .graphicsLayer {
                                        scaleX = buttonScale
                                        scaleY = buttonScale
                                    }
                            ) {
                                Icon(
                                    imageVector = if (artistViewType == ArtistViewType.LIST) Icons.Default.GridView else Icons.AutoMirrored.Rounded.ViewList,
                                    contentDescription = if (artistViewType == ArtistViewType.LIST) "Switch to Grid View" else "Switch to List View",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        "PLAYLISTS" -> {
                            // Enhanced Playlist view toggle
                            val playlistViewType by appSettings.playlistViewType.collectAsState()
                            
                            // Animation for button press
                            val buttonScale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "playlistToggleScale"
                            )
                            
                            FilledTonalIconButton(
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    val newViewType = if (playlistViewType == PlaylistViewType.LIST) PlaylistViewType.GRID else PlaylistViewType.LIST
                                    appSettings.setPlaylistViewType(newViewType)
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                modifier = Modifier
                                    .size(42.dp)
                                    .graphicsLayer {
                                        scaleX = buttonScale
                                        scaleY = buttonScale
                                    }
                            ) {
                                Icon(
                                    imageVector = if (playlistViewType == PlaylistViewType.LIST) Icons.Default.GridView else Icons.AutoMirrored.Rounded.ViewList,
                                    contentDescription = if (playlistViewType == PlaylistViewType.LIST) "Switch to Grid View" else "Switch to List View",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        

                    }
                    
                    // Sort dropdown like AlbumBottomSheet (only show for Songs, Albums, and Playlists)
                    val currentTabId = visibleTabIds.getOrNull(selectedTabIndex)
                    if (currentTabId == "SONGS" || currentTabId == "ALBUMS") {
                        var showSortMenu by remember { mutableStateOf(false) }
                        var pendingSortOrder by remember { mutableStateOf<MusicViewModel.SortOrder?>(null) }
                        
                        // Clear pending sort order when actual sort order changes
                        LaunchedEffect(sortOrder) {
                            pendingSortOrder = null
                        }
                        
                        Box {
                        // Enhanced sort button
                        val sortButtonScale by animateFloatAsState(
                            targetValue = if (showSortMenu) 0.95f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "sortButtonScale"
                        )
                        
                        FilledTonalButton(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showSortMenu = true
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            modifier = Modifier.graphicsLayer {
                                scaleX = sortButtonScale
                                scaleY = sortButtonScale
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Sort order text
                            val sortText = when (sortOrder) {
                                MusicViewModel.SortOrder.TITLE_ASC, MusicViewModel.SortOrder.TITLE_DESC -> "Title"
                                MusicViewModel.SortOrder.ARTIST_ASC, MusicViewModel.SortOrder.ARTIST_DESC -> "Artist"
                                MusicViewModel.SortOrder.DATE_ADDED_ASC, MusicViewModel.SortOrder.DATE_ADDED_DESC -> "Date Added"
                                MusicViewModel.SortOrder.DATE_MODIFIED_ASC, MusicViewModel.SortOrder.DATE_MODIFIED_DESC -> "Date Modified"
                            }

                            Text(
                                text = sortText,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            val sortArrowIcon = when (sortOrder) {
                                MusicViewModel.SortOrder.TITLE_ASC, MusicViewModel.SortOrder.ARTIST_ASC, MusicViewModel.SortOrder.DATE_ADDED_ASC, MusicViewModel.SortOrder.DATE_MODIFIED_ASC -> Icons.Default.ArrowUpward
                                MusicViewModel.SortOrder.TITLE_DESC, MusicViewModel.SortOrder.ARTIST_DESC, MusicViewModel.SortOrder.DATE_ADDED_DESC, MusicViewModel.SortOrder.DATE_MODIFIED_DESC -> Icons.Default.ArrowDownward
                            }
                            
                            Icon(
                                imageVector = sortArrowIcon,
                                contentDescription = if (sortOrder.name.endsWith("_ASC")) "Ascending" else "Descending",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            MusicViewModel.SortOrder.values().forEach { order ->
                                val isSelected = (pendingSortOrder ?: sortOrder) == order
                                Surface(
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                                    else 
                                        Color.Transparent,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = when (order) {
                                                    MusicViewModel.SortOrder.TITLE_ASC, MusicViewModel.SortOrder.TITLE_DESC -> "Title"
                                                    MusicViewModel.SortOrder.ARTIST_ASC, MusicViewModel.SortOrder.ARTIST_DESC -> "Artist"
                                                    MusicViewModel.SortOrder.DATE_ADDED_ASC, MusicViewModel.SortOrder.DATE_ADDED_DESC -> "Date Added"
                                                    MusicViewModel.SortOrder.DATE_MODIFIED_ASC, MusicViewModel.SortOrder.DATE_MODIFIED_DESC -> "Date Modified"
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected)
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                else
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when (order) {
                                                    MusicViewModel.SortOrder.TITLE_ASC, MusicViewModel.SortOrder.TITLE_DESC -> Icons.Filled.SortByAlpha
                                                    MusicViewModel.SortOrder.ARTIST_ASC, MusicViewModel.SortOrder.ARTIST_DESC -> Icons.Filled.Person
                                                    MusicViewModel.SortOrder.DATE_ADDED_ASC, MusicViewModel.SortOrder.DATE_ADDED_DESC -> Icons.Filled.DateRange
                                                    MusicViewModel.SortOrder.DATE_MODIFIED_ASC, MusicViewModel.SortOrder.DATE_MODIFIED_DESC -> Icons.Filled.EditCalendar
                                                },
                                                contentDescription = null,
                                                tint = if (isSelected)
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                else
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        trailingIcon = {
                                            when (order) {
                                                MusicViewModel.SortOrder.TITLE_ASC, MusicViewModel.SortOrder.ARTIST_ASC, MusicViewModel.SortOrder.DATE_ADDED_ASC, MusicViewModel.SortOrder.DATE_MODIFIED_ASC -> {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowUpward,
                                                        contentDescription = "Ascending",
                                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                MusicViewModel.SortOrder.TITLE_DESC, MusicViewModel.SortOrder.ARTIST_DESC, MusicViewModel.SortOrder.DATE_ADDED_DESC, MusicViewModel.SortOrder.DATE_MODIFIED_DESC -> {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = "Descending",
                                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                else -> {}
                                            }
                                        },
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                            pendingSortOrder = order
                                            showSortMenu = false
                                            // Set the specific sort order instead of cycling
                                            if (sortOrder != order) {
                                                musicViewModel.setSortOrder(order)
                                            }
                                        },
                                        colors = androidx.compose.material3.MenuDefaults.itemColors(
                                            textColor = if (isSelected) 
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
                    
                    // Playlist Sort dropdown
                    if (currentTabId == "PLAYLISTS") {
                        val playlistSortOrderString by appSettings.playlistSortOrder.collectAsState()
                        val playlistSortOrder = try {
                            LibraryPlaylistSortOrder.valueOf(playlistSortOrderString)
                        } catch (e: Exception) {
                            LibraryPlaylistSortOrder.NAME_ASC
                        }
                        var showPlaylistSortMenu by remember { mutableStateOf(false) }
                        
                        Box {
                            // Enhanced sort button
                            val sortButtonScale by animateFloatAsState(
                                targetValue = if (showPlaylistSortMenu) 0.95f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "playlistSortButtonScale"
                            )
                            
                            FilledTonalButton(
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    showPlaylistSortMenu = true
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                modifier = Modifier.graphicsLayer {
                                    scaleX = sortButtonScale
                                    scaleY = sortButtonScale
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Sort,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Sort order text
                                val sortText = when (playlistSortOrder) {
                                    LibraryPlaylistSortOrder.NAME_ASC, LibraryPlaylistSortOrder.NAME_DESC -> context.getString(R.string.sort_name)
                                    LibraryPlaylistSortOrder.DATE_CREATED_ASC, LibraryPlaylistSortOrder.DATE_CREATED_DESC -> context.getString(R.string.sort_date_created)
                                    LibraryPlaylistSortOrder.SONG_COUNT_ASC, LibraryPlaylistSortOrder.SONG_COUNT_DESC -> context.getString(R.string.sort_song_count)
                                }

                                Text(
                                    text = sortText,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                val sortArrowIcon = if (playlistSortOrder.name.endsWith("_ASC")) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                                
                                Icon(
                                    imageVector = sortArrowIcon,
                                    contentDescription = if (playlistSortOrder.name.endsWith("_ASC")) "Ascending" else "Descending",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showPlaylistSortMenu,
                                onDismissRequest = { showPlaylistSortMenu = false },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                LibraryPlaylistSortOrder.values().forEach { order ->
                                    val isSelected = playlistSortOrder == order
                                    Surface(
                                        color = if (isSelected) 
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                                        else 
                                            Color.Transparent,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        DropdownMenuItem(
                                            text = { 
                                                Text(
                                                    text = when (order) {
                                                        LibraryPlaylistSortOrder.NAME_ASC, LibraryPlaylistSortOrder.NAME_DESC -> context.getString(R.string.sort_name)
                                                        LibraryPlaylistSortOrder.DATE_CREATED_ASC, LibraryPlaylistSortOrder.DATE_CREATED_DESC -> context.getString(R.string.sort_date_created)
                                                        LibraryPlaylistSortOrder.SONG_COUNT_ASC, LibraryPlaylistSortOrder.SONG_COUNT_DESC -> context.getString(R.string.sort_song_count)
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected)
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    else
                                                        MaterialTheme.colorScheme.onSurface
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = when (order) {
                                                        LibraryPlaylistSortOrder.NAME_ASC, LibraryPlaylistSortOrder.NAME_DESC -> Icons.Filled.SortByAlpha
                                                        LibraryPlaylistSortOrder.DATE_CREATED_ASC, LibraryPlaylistSortOrder.DATE_CREATED_DESC -> Icons.Filled.DateRange
                                                        LibraryPlaylistSortOrder.SONG_COUNT_ASC, LibraryPlaylistSortOrder.SONG_COUNT_DESC -> Icons.Filled.MusicNote
                                                    },
                                                    contentDescription = null,
                                                    tint = if (isSelected)
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = if (order.name.endsWith("_ASC")) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                                    contentDescription = if (order.name.endsWith("_ASC")) "Ascending" else "Descending",
                                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                                showPlaylistSortMenu = false
                                                if (playlistSortOrder != order) {
                                                    appSettings.setPlaylistSortOrder(order.name)
                                                }
                                            },
                                            colors = androidx.compose.material3.MenuDefaults.itemColors(
                                                textColor = if (isSelected) 
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
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior,
                modifier = Modifier.padding(horizontal = 8.dp) // Added padding
            )
            }
        },
        bottomBar = {},
        floatingActionButton = {
            // Only show FAB on playlists tab
            if (visibleTabIds.getOrNull(selectedTabIndex) == "PLAYLISTS") {
                PlaylistFabMenu(
                    expanded = showPlaylistFabMenu,
                    onCreatePlaylist = onCreatePlaylistFromFab,
                    onImportPlaylist = onImportPlaylistFromFab,
                onExportPlaylists = onExportPlaylistsFromFab,
//                    onManagePlaylists = onManagePlaylists
                    haptics = haptics // Pass haptics to PlaylistFabMenu
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Scrollable Navigation Buttons - Stats screen style
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                LazyRow(
                    state = tabRowState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(
                        count = tabs.size,
                        key = { index -> tabOrder.getOrNull(index) ?: "tab_$index" }
                    ) { index ->
                        val isSelected = selectedTabIndex == index
                        
                        TabAnimation(
                            index = index,
                            selectedIndex = selectedTabIndex,
                            title = tabs[index],
                            selectedColor = MaterialTheme.colorScheme.primary,
                            onSelectedColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedColor = MaterialTheme.colorScheme.surfaceContainer,
                            onUnselectedColor = MaterialTheme.colorScheme.onSurface,
                            onClick = {
                                selectedTabIndex = index
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                    tabRowState.animateScrollToItem(index)
                                }
                            },
                            modifier = Modifier.padding(all = 2.dp),
                            content = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Get the actual tab ID from the visible tabs list
                                    val currentTabId = visibleTabIds.getOrNull(index)
                                    Icon(
                                        imageVector = when (currentTabId) {
                                            "SONGS" -> RhythmIcons.Relax
                                            "PLAYLISTS" -> RhythmIcons.PlaylistFilled
                                            "ALBUMS" -> RhythmIcons.Music.Album
                                            "ARTISTS" -> RhythmIcons.Artist
                                            "EXPLORER" -> Icons.Default.Folder
                                            else -> RhythmIcons.Music.Song
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = tabs[index],
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        )
                    }
                    
                    // Edit button at the end to open LibraryTabReorderBottomSheet
                    item {
                        var showLibraryTabOrderSheet by remember { mutableStateOf(false) }

                        TabAnimation(
                            index = tabs.size, // Use tabs.size as index since it's after all tabs
                            selectedIndex = -1, // Never selected
                            title = "Edit",
                            selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                            onSelectedColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedColor = MaterialTheme.colorScheme.surfaceContainer,
                            onUnselectedColor = MaterialTheme.colorScheme.onSurface,
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                showLibraryTabOrderSheet = true
                            },
                            modifier = Modifier.padding(all = 2.dp),
                            content = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Reorder tabs",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Edit",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        )

                        if (showLibraryTabOrderSheet) {
                            LibraryTabOrderBottomSheet(
                                onDismiss = { showLibraryTabOrderSheet = false },
                                appSettings = appSettings,
                                haptics = haptics
                            )
                        }
                    }
                }
            }
            
            // Background Processing Loader - shown between tabs and content
            val isBackgroundProcessing by musicViewModel.isBackgroundProcessing.collectAsState()
            val isMediaScanning by musicViewModel.isMediaScanning.collectAsState()
            val isGenreDetectionRunning by musicViewModel.isGenreDetectionRunning.collectAsState()
            val isFetchingArtwork by musicViewModel.isFetchingArtwork.collectAsState()
            val isExtractingMetadata by musicViewModel.isExtractingMetadata.collectAsState()
            
            AnimatedVisibility(
                visible = isBackgroundProcessing,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 8.dp)
                ) {
                    // Wavy animated progress indicator for library screen with transparent track
                    androidx.compose.material3.LinearWavyProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        trackColor = Color.Transparent
                    )
                    
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Processing status text with animated icon
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        modifier = Modifier.padding(start = 4.dp)
//                    ) {
//                        // Animated status icon
//                        val infiniteTransition = rememberInfiniteTransition(label = "statusIconAnimation")
//                        val iconRotation by infiniteTransition.animateFloat(
//                            initialValue = 0f,
//                            targetValue = 360f,
//                            animationSpec = infiniteRepeatable(
//                                animation = tween(2000, easing = LinearEasing),
//                                repeatMode = RepeatMode.Restart
//                            ),
//                            label = "iconRotation"
//                        )
//
//                        Icon(
//                            imageVector = when {
//                                isMediaScanning -> Icons.Default.Refresh
//                                isExtractingMetadata -> Icons.Default.Analytics
//                                isFetchingArtwork -> Icons.Default.Image
//                                !isGenreDetectionRunning -> Icons.Default.Category
//                                else -> Icons.Default.Sync
//                            },
//                            contentDescription = null,
//                            
//                            modifier = Modifier
//                                .size(16.dp)
//                                .graphicsLayer {
//                                    rotationZ = iconRotation
//                                }
//                        )
//
//                        val statusText = remember(isMediaScanning, isGenreDetectionRunning, isFetchingArtwork, isExtractingMetadata) {
//                            when {
//                                isMediaScanning -> context.getString(R.string.scanning_media)
//                                isExtractingMetadata -> context.getString(R.string.extracting_metadata)
//                                isFetchingArtwork -> context.getString(R.string.fetching_artwork)
//                                !isGenreDetectionRunning -> context.getString(R.string.detecting_genres)
//                                else -> context.getString(R.string.processing_library)
//                            }
//                        }
//
//                        Text(
//                            text = statusText,
//                            style = MaterialTheme.typography.bodySmall.copy(
//                                fontWeight = FontWeight.Medium
//                            ),
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
                }
            }
            
            // Single Big Card Container with Pull-to-Refresh
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = libraryBottomOverlayPadding),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shadowElevation = 0.dp
            ) {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        // If on Explorer tab, reload explorer; otherwise, trigger media scan
                        if (visibleTabIds.getOrNull(selectedTabIndex) == "EXPLORER") {
                            explorerReloadTrigger++
                        } else {
                            onRefreshClick()
                        }
                    },
                    state = pullToRefreshState,
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        PullToRefreshDefaults.LoadingIndicator(
                            state = pullToRefreshState,
                            isRefreshing = isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Content with animation
                        HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ) { page ->
                    // Dynamically show tab content based on visible tab order (filtered)
                    when (visibleTabIds.getOrNull(page)) {
                        "SONGS" -> {
                            // Sort songs according to current sort order
                            val sortedSongs = remember(songs, sortOrder) {
                                when (sortOrder) {
                                    MusicViewModel.SortOrder.TITLE_ASC -> songs.sortedBy { it.title.lowercase() }
                                    MusicViewModel.SortOrder.TITLE_DESC -> songs.sortedByDescending { it.title.lowercase() }
                                    MusicViewModel.SortOrder.ARTIST_ASC -> songs.sortedBy { it.artist.lowercase() }
                                    MusicViewModel.SortOrder.ARTIST_DESC -> songs.sortedByDescending { it.artist.lowercase() }
                                    MusicViewModel.SortOrder.DATE_ADDED_ASC -> songs.sortedBy { it.dateAdded }
                                    MusicViewModel.SortOrder.DATE_ADDED_DESC -> songs.sortedByDescending { it.dateAdded }
                                    MusicViewModel.SortOrder.DATE_MODIFIED_ASC -> songs.sortedBy { it.dateAdded } // Song doesn't have dateModified, use dateAdded
                                    MusicViewModel.SortOrder.DATE_MODIFIED_DESC -> songs.sortedByDescending { it.dateAdded } // Song doesn't have dateModified, use dateAdded
                                }
                            }
                            SingleCardSongsContent(
                            songs = sortedSongs,
                            albums = albums,
                            artists = artists,
                            onSongClick = onSongClick,
                            onAddToPlaylist = { song ->
                                selectedSong = song
                                showAddToPlaylistSheet = true
                            },
                            onAddToQueue = onAddToQueue,
                            onPlayNext = { song -> musicViewModel.playNext(song) },
                            onToggleFavorite = { song -> musicViewModel.toggleFavorite(song) },
                            favoriteSongs = musicViewModel.favoriteSongs.collectAsState().value,
                            onGoToArtist = onArtistClick,
                            onGoToAlbum = onAlbumClick,
                            onShowSongInfo = { song ->
                                selectedSong = song
                                showSongInfoSheet = true
                            },
                            onAddToBlacklist = { song ->
                                appSettings.addToBlacklist(song.id)
                            },
                            onPlayQueue = onPlayQueue,
                            onPlayQueueFromIndex = onPlayQueueFromIndex,
                            onShuffleQueue = onShuffleQueue,
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            haptics = haptics,
                            enableRatingSystem = enableRatingSystem,
                            isSelectionMode = isSelectionMode,
                            selectedSongIds = selectedSongIds,
                            multiSelectionState = multiSelectionState,
                            onSongLongPress = onSongLongPress,
                            onSongSelectionToggle = onSongSelectionToggle,
                            onShowMultiSelectionSheet = { showMultiSelectionSheet = true },
                            onRefreshClick = onRefreshClick
                        )
                        }
                        "PLAYLISTS" -> SingleCardPlaylistsContent(
                            playlists = playlists,
                            onPlaylistClick = onPlaylistClick,
                            haptics = haptics,
                            onCreatePlaylist = { showCreatePlaylistDialog = true },
                            onImportPlaylist = { showImportDialog = true },
                            onExportPlaylists = { showBulkExportDialog = true },
                            appSettings = appSettings,
                            onRefreshClick = onRefreshClick
                        )
                        "ALBUMS" -> SingleCardAlbumsContent(
                            albums = albums,
                            onAlbumClick = onAlbumClick,
                            onSongClick = onSongClick,
                            onAlbumBottomSheetClick = { album ->
                                selectedAlbum = album
                                showAlbumBottomSheet = true
                            },
                            haptics = haptics,
                            appSettings = appSettings,
                            onPlayQueue = onPlayQueue,
                            onShuffleQueue = onShuffleQueue,
                            onRefreshClick = onRefreshClick
                        )
                        "ARTISTS" -> SingleCardArtistsContent(
                            artists = artists,
                            onArtistClick = { artist ->
                                onNavigateToArtist(artist)
                            },
                            haptics = haptics,
                            onPlayQueue = onPlayQueue,
                            onShuffleQueue = onShuffleQueue,
                            onRefreshClick = onRefreshClick
                        )
                        "EXPLORER" -> SingleCardExplorerContent(
                            songs = songs,
                            onSongClick = onSongClick,
                            onAddToPlaylist = { song ->
                                selectedSong = song
                                showAddToPlaylistSheet = true
                            },
                            onAddToQueue = onAddToQueue,
                            onShowSongInfo = { song ->
                                selectedSong = song
                                showSongInfoSheet = true
                            },
                            onPlayQueue = onPlayQueue,
                            onPlayQueueFromIndex = onPlayQueueFromIndex,
                            onShuffleQueue = onShuffleQueue,
                            haptics = haptics,
                            appSettings = appSettings,
                            reloadTrigger = explorerReloadTrigger,
                            onCreatePlaylist = onCreatePlaylist,
                            musicViewModel = musicViewModel,
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            enableRatingSystem = enableRatingSystem
                        )
                    }
                }
                        
                        // Show media scanning progress overlay on top
                        androidx.compose.animation.AnimatedVisibility(
                            visible = isMediaScanning,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Animated scanning icon
                                    val infiniteTransition = rememberInfiniteTransition(label = "scanIconRotation")
                                    val rotation by infiniteTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 360f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(2000, easing = LinearEasing),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "rotation"
                                    )
                                    
                                    Icon(
                                        imageVector = RhythmIcons.Refresh,
                                        contentDescription = "Scanning",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .graphicsLayer { rotationZ = rotation }
                                    )
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = when (scanProgress.stage) {
                                                "Songs" -> "Scanning songs..."
                                                "Albums" -> "Processing albums..."
                                                "Artists" -> "Processing artists..."
                                                "Genres" -> "Detecting genres..."
                                                else -> "Scanning media..."
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        if (scanProgress.total > 0) {
                                            Text(
                                                text = "${scanProgress.current} / ${scanProgress.total}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                    
                                    // Show circular progress indicator
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Playlist import/export dialogs
    if (showBulkExportDialog && onExportAllPlaylists != null) {
        BulkPlaylistExportDialog(
            playlistCount = playlists.size,
            onDismiss = { 
                showBulkExportDialog = false
                operationError = null
            },
            onExport = { format, includeDefault ->
                showBulkExportDialog = false
                showOperationProgress = true
                operationProgressText = context.getString(R.string.exporting_playlists)
                
                onExportAllPlaylists(format, includeDefault, null) { result ->
                    showOperationProgress = false
                    result.fold(
                        onSuccess = { message ->
                            // Success will be shown via snackbar from navigation layer
                        },
                        onFailure = { error ->
                            operationError = error.message ?: "Export failed"
                        }
                    )
                }
            },
            onExportToCustomLocation = { format, includeDefault, directoryUri ->
                showBulkExportDialog = false
                showOperationProgress = true
                operationProgressText = context.getString(R.string.exporting_to_location)
                
                onExportAllPlaylists(format, includeDefault, directoryUri) { result ->
                    showOperationProgress = false
                    result.fold(
                        onSuccess = { message ->
                            // Success will be shown via snackbar from navigation layer
                        },
                        onFailure = { error ->
                            operationError = error.message ?: "Export failed"
                        }
                    )
                }
            }
        )
    }
    
    if (showImportDialog && onImportPlaylist != null) {
        PlaylistImportDialog(
            onDismiss = { 
                showImportDialog = false
                operationError = null
            },
            onImport = { uri, onResult, onRestartRequired ->
                showImportDialog = false
                showOperationProgress = true
                operationProgressText = context.getString(R.string.importing_playlist)
                onImportPlaylist(uri, { result ->
                    showOperationProgress = false
                    result.fold(
                        onSuccess = { message ->
                            operationResult = Pair(message, true)
                            showRestartDialog = true
                        },
                        onFailure = { error ->
                            operationError = error.message ?: "Import failed"
                        }
                    )
                    onResult(result)
                }, onRestartRequired)
            }
        )
    }

    // App Restart Dialog
    if (showRestartDialog && onRestartApp != null) {
        AppRestartDialog(
            onDismiss = { showRestartDialog = false },
            onRestart = {
                showRestartDialog = false
                onRestartApp()
            },
            onContinue = {
                showRestartDialog = false
                // Continue without restart
            }
        )
    }

    // Progress dialog for long operations
    if (showOperationProgress) {
        PlaylistOperationProgressDialog(
            operation = operationProgressText,
            onDismiss = {
                showOperationProgress = false
                operationProgressText = ""
            }
        )
    }
    
    // Simple success/error dialogs for now
    if (operationError != null) {
        AlertDialog(
            onDismissRequest = { operationError = null },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Error") },
            text = { Text(operationError!!) },
            confirmButton = {
                Button(onClick = { operationError = null }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OK")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Dialog to show import result and offer restart
    if (showImportResultDialog && importResult != null) {
        AlertDialog(
            onDismissRequest = { showImportResultDialog = false; importResult = null },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Import Complete") },
            text = {
                val (count, message) = importResult!!
                Text("Successfully imported $count playlists.\n$message\n\nRestart the app to apply changes.")
            },
            confirmButton = {
                Button(onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    showImportResultDialog = false
                    importResult = null
                    AppRestarter.restartApp(context)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restart App")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    showImportResultDialog = false
                    importResult = null
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Later")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun SingleCardSongsContent(
    songs: List<Song>,
    albums: List<Album> = emptyList(),
    artists: List<Artist> = emptyList(),
    onSongClick: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onPlayNext: (Song) -> Unit = {},
    onToggleFavorite: (Song) -> Unit = {},
    favoriteSongs: Set<String> = emptySet(),
    onGoToArtist: (Artist) -> Unit = {},
    onGoToAlbum: (Album) -> Unit = {},
    onShowSongInfo: (Song) -> Unit,
    onAddToBlacklist: (Song) -> Unit,
    onPlayQueue: (List<Song>) -> Unit = { _ -> },
    onPlayQueueFromIndex: (List<Song>, Int) -> Unit = { _, _ -> }, // New parameter for playing from specific index
    onShuffleQueue: (List<Song>) -> Unit = { _ -> },
    currentSong: Song? = null, // Add current song parameter
    isPlaying: Boolean = false, // Add playing state
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    enableRatingSystem: Boolean = true,
    // Multi-selection parameters
    isSelectionMode: Boolean = false,
    selectedSongIds: Set<String> = emptySet(),
    multiSelectionState: chromahub.rhythm.app.features.local.presentation.viewmodel.MultiSelectionStateHolder? = null,
    onSongLongPress: (Song) -> Unit = {},
    onSongSelectionToggle: (Song) -> Unit = {},
    onShowMultiSelectionSheet: () -> Unit = {},
    onRefreshClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val appSettings = remember { AppSettings.getInstance(context) }
    val groupByAlbumArtist by appSettings.groupByAlbumArtist.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    
    // Multi-selection state - get selected songs from state holder
    val selectedSongs = multiSelectionState?.selectedSongs?.collectAsState()?.value ?: emptyList()
    
    // Loading state for async category computation
    var isLoading by remember { mutableStateOf(true) }
    var preparedSongs by remember { mutableStateOf(songs) }
    var categories by remember { mutableStateOf<List<String>>(listOf("All")) }
    
    // Helper function to split artist names
    val splitArtistNames: (String) -> List<String> = remember {
        { artistName ->
            // Character-level delimiters from settings
            val libAppSettings = AppSettings.getInstance(context)
            val artistSeparatorEnabled = libAppSettings.artistSeparatorEnabled.value
            val charDelimiters = if (artistSeparatorEnabled) {
                libAppSettings.artistSeparatorDelimiters.value.toList().map { it.toString() }
            } else emptyList()

            if (charDelimiters.isEmpty()) {
                listOf(artistName.trim()).filter { it.isNotBlank() }
            } else {
                val selectedDelimiterChars = charDelimiters.mapNotNull { it.firstOrNull() }.toSet()
                val wordSeparators = mutableListOf<String>().apply {
                    if (selectedDelimiterChars.contains('&')) add(" & ")
                    add(" and ")
                    if (selectedDelimiterChars.contains(',')) add(", ")
                    add(" feat. ")
                    add(" feat ")
                    add(" ft. ")
                    add(" ft ")
                    add(" featuring ")
                    add(" x ")
                    add(" X ")
                    add(" vs ")
                    add(" vs. ")
                    add(" with ")
                }

                var names = listOf(artistName)
                for (delimiter in charDelimiters) {
                    names = names.flatMap { it.split(delimiter) }
                }
                for (separator in wordSeparators) {
                    names = names.flatMap { it.split(separator, ignoreCase = true) }
                }
                names.map { it.trim() }.filter { it.isNotBlank() }
            }
        }
    }
    
    // Cache for audio quality detection to avoid re-computation
    val audioQualityCache = remember { mutableMapOf<String, AudioQualityDetector.AudioQuality>() }
    
    // Helper function to get or compute audio quality using AudioFormatDetector and AudioQualityDetector
    // This matches the same logic used in AudioQualityBadges for consistent quality detection
    suspend fun getAudioQuality(song: Song): AudioQualityDetector.AudioQuality {
        // Check cache first
        audioQualityCache[song.id]?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                // Use AudioFormatDetector for accurate codec and format detection
                val formatInfo = AudioFormatDetector.detectFormat(context, song.uri, song)
                
                // Prefer Song's metadata when available (more reliable)
                val bitrateKbps = if (song.bitrate != null && song.bitrate!! > 0) {
                    song.bitrate!! / 1000
                } else if (formatInfo.bitrateKbps > 0) {
                    formatInfo.bitrateKbps
                } else {
                    0
                }
                
                val sampleRateHz = if (song.sampleRate != null && song.sampleRate!! > 0) {
                    song.sampleRate!!
                } else if (formatInfo.sampleRateHz > 0) {
                    formatInfo.sampleRateHz
                } else {
                    0
                }
                
                val channelCount = if (song.channels != null && song.channels!! > 0) {
                    song.channels!!
                } else if (formatInfo.channelCount > 0) {
                    formatInfo.channelCount
                } else {
                    2
                }
                
                val codec = formatInfo.codec.ifEmpty { song.codec ?: "Unknown" }
                val bitDepth = formatInfo.bitDepth
                
                // Use AudioQualityDetector for accurate quality classification
                val quality = AudioQualityDetector.detectQuality(
                    codec = codec,
                    sampleRateHz = sampleRateHz,
                    bitrateKbps = bitrateKbps,
                    bitDepth = bitDepth,
                    channelCount = channelCount
                )
                
                // Cache the result
                audioQualityCache[song.id] = quality
                quality
            } catch (e: Exception) {
                android.util.Log.w("SongsTab", "Error detecting audio quality for ${song.title}: ${e.message}")
                // Return a default quality on error
                AudioQualityDetector.AudioQuality(
                    qualityType = AudioQualityDetector.QualityType.UNKNOWN,
                    isLossless = false,
                    isDolby = false,
                    isDTS = false,
                    isHiRes = false,
                    qualityLabel = "Unknown",
                    qualityDescription = "Quality could not be determined",
                    bitDepthEstimate = 0,
                    category = "Unknown"
                )
            }
        }
    }
    
    // Synchronous helper functions for filtering (use cached metadata when possible, fallback to fresh extraction)
    fun isLosslessAudio(song: Song): Boolean {
        val codec = song.codec?.uppercase() ?: ""

        // If we have cached codec data, use it
        if (codec.isNotEmpty()) {
            // Check if it's explicitly a LOSSY codec - these are NEVER lossless
            val isLossyCodec = codec.contains("MP3") || codec.contains("AAC") ||
                              codec.contains("OGG") || codec.contains("OPUS") ||
                              codec.contains("VORBIS") || (codec.contains("WMA") && !codec.contains("LOSSLESS"))

            if (isLossyCodec) return false

            // Check if it's explicitly a LOSSLESS codec
            val isLosslessCodec = codec in listOf("ALAC", "FLAC", "PCM", "WAV", "APE", "DSD", "TRUEHD", "DOLBY ATMOS", "DTS-HD MA", "AIFF", "WV", "TAK", "TTA") ||
                                 codec.contains("LOSSLESS", ignoreCase = true) ||
                                 codec.contains("APPLE LOSSLESS", ignoreCase = true)

            if (isLosslessCodec) return true
        }

        // Fallback: Check file extension for known lossless formats
        val uri = song.uri.toString()
        val isLosslessExtension = uri.endsWith(".flac", ignoreCase = true) ||
                                  uri.endsWith(".wav", ignoreCase = true) ||
                                  uri.endsWith(".alac", ignoreCase = true) ||
                                  uri.endsWith(".ape", ignoreCase = true) ||
                                  uri.endsWith(".aiff", ignoreCase = true) ||
                                  uri.endsWith(".aif", ignoreCase = true) ||
                                  uri.endsWith(".dsd", ignoreCase = true) ||
                                  uri.endsWith(".wv", ignoreCase = true) ||
                                  uri.endsWith(".tta", ignoreCase = true) ||
                                  uri.endsWith(".tak", ignoreCase = true)

        if (isLosslessExtension) return true

        return false
    }

    fun isHiResLossless(song: Song): Boolean {
        if (!isLosslessAudio(song)) {
            android.util.Log.d("SongsTab", "Song ${song.title} is not lossless")
            return false
        }

        val sampleRate = song.sampleRate ?: 0
        val bitrate = song.bitrate ?: 0
        val channels = song.channels ?: 2

        // Hi-Res Lossless requires ≥48kHz sample rate
        if (sampleRate < 48000) {
            android.util.Log.d("SongsTab", "Song ${song.title} sample rate $sampleRate < 48000, not Hi-Res")
            return false
        }

        // For known Hi-Res sample rates, consider them Hi-Res even without bitrate calculation
        if (sampleRate >= 88200) {
            android.util.Log.d("SongsTab", "Song ${song.title} has Hi-Res sample rate $sampleRate")
            return true // 88.2kHz, 96kHz, 176.4kHz, 192kHz etc.
        }

        // Calculate bit depth using improved AudioFormatDetector logic
        if (bitrate > 0 && sampleRate > 0 && channels > 0) {
            val bitrateKbps = bitrate / 1000
            val calculatedBitDepth = (bitrateKbps * 1000) / (sampleRate * channels)
            // Use AudioFormatDetector thresholds: >= 20 bits/sample = 24-bit
            // But be more lenient for Hi-Res detection
            android.util.Log.d("SongsTab", "Song ${song.title} bit depth calculation: bitrate=${bitrateKbps}kbps, sampleRate=$sampleRate, channels=$channels, calculatedBitDepth=$calculatedBitDepth")
            if (calculatedBitDepth >= 18) {
                android.util.Log.d("SongsTab", "Song ${song.title} qualifies as Hi-Res with bit depth $calculatedBitDepth")
                return true // Allow some margin for calculation errors
            }
        }

        // Fallback: High bitrate lossless at 48kHz or higher is likely Hi-Res
        if (bitrate >= 2000000 && sampleRate >= 48000) {
            android.util.Log.d("SongsTab", "Song ${song.title} qualifies as Hi-Res with high bitrate $bitrate")
            return true // 2Mbps+ at 48kHz+
        }

        android.util.Log.d("SongsTab", "Song ${song.title} does not qualify as Hi-Res")
        return false
    }
    
    fun isRegularLossless(song: Song): Boolean {
        // Regular Lossless = Lossless but NOT Hi-Res
        // This ensures mutual exclusivity between "Lossless" and "Hi-Res Lossless" filters
        val lossless = isLosslessAudio(song)
        if (!lossless) return false
        
        // IMPORTANT: Check if it's Hi-Res and exclude it
        val hiRes = isHiResLossless(song)
        if (hiRes) {
            android.util.Log.d("SongsTab", "Song ${song.title} is Hi-Res, excluding from Regular Lossless")
            return false
        }
        
        android.util.Log.d("SongsTab", "Song ${song.title} qualifies as Regular Lossless (not Hi-Res)")
        return true
    }

    fun isDolbyOrSurround(song: Song): Boolean {
        val codec = song.codec?.uppercase() ?: ""
        return (song.channels ?: 2) > 2 || // Multi-channel audio
               codec.contains("AC-3") ||
               codec.contains("E-AC-3") ||
               codec.contains("DOLBY") ||
               codec.contains("TRUEHD") ||
               codec.contains("ATMOS") ||
               codec.contains("DTS")
    }

    // Async category computation to avoid blocking UI on tab switch
    LaunchedEffect(songs, enableRatingSystem) {
        isLoading = true
        val result = withContext(Dispatchers.Default) {
            val allCategories = mutableListOf("All")

            android.util.Log.d("SongsTab", "Recomputing categories for ${songs.size} songs")

// Favorites filter - show if there are any favorite songs
        val favoriteSongsList = songs.filter { it.id in favoriteSongs }
        if (favoriteSongsList.isNotEmpty()) {
            allCategories.add("❤️ Favorites")
            }

            // Audio Quality Filters (Mutually Exclusive) - Most specific first

            // Hi-Res Lossless (≥48 kHz + 24-bit lossless)
            val hiResLosslessSongs = songs.filter { isHiResLossless(it) && !isDolbyOrSurround(it) }
            android.util.Log.d("SongsTab", "Found ${hiResLosslessSongs.size} Hi-Res Lossless songs")
            if (hiResLosslessSongs.isNotEmpty()) allCategories.add("Hi-Res Lossless")

            // Regular Lossless (CD quality: 44.1kHz/16-bit or 48kHz/16-bit)
            val regularLosslessSongs = songs.filter { isRegularLossless(it) && !isDolbyOrSurround(it) }
            android.util.Log.d("SongsTab", "Found ${regularLosslessSongs.size} Lossless (CD Quality) songs")
            if (regularLosslessSongs.isNotEmpty()) allCategories.add("Lossless")

            // Dolby (includes AC-3, E-AC-3/D+, TrueHD, Atmos, DTS in 5.1, 7.1, etc.)
            val dolbySongs = songs.filter { isDolbyOrSurround(it) }
            android.util.Log.d("SongsTab", "Found ${dolbySongs.size} Dolby/Surround songs")
            if (dolbySongs.isNotEmpty()) allCategories.add("Dolby")
            
            // Stereo (standard 2-channel, non-quality filtered) - HIDDEN per user request
            val stereoSongs = songs.filter { song ->
                (song.channels ?: 2) == 2 && !isDolbyOrSurround(song)
            }
            android.util.Log.d("SongsTab", "Found ${stereoSongs.size} Stereo songs")
            // if (stereoSongs.isNotEmpty()) allCategories.add("Stereo")  // HIDDEN - user requested to hide stereo filter
            
            val monoSongs = songs.filter { song ->
                (song.channels ?: 2) == 1
            }
            android.util.Log.d("SongsTab", "Found ${monoSongs.size} Mono songs")
            if (monoSongs.isNotEmpty()) allCategories.add("Mono")
            
            // Log sample metadata for debugging
            if (songs.isNotEmpty()) {
                val sampleSong = songs.first()
                android.util.Log.d("SongsTab", "Sample song metadata: ${sampleSong.title} - bitrate=${sampleSong.bitrate}, sampleRate=${sampleSong.sampleRate}, channels=${sampleSong.channels}, codec=${sampleSong.codec}")
            }

            // Rating-based categories (5★ = Absolute Favorite, 4★ = Loved, 3★ = Great, 2★ = Good, 1★ = Liked)
            // Only show if rating system is enabled
            if (enableRatingSystem) {
                val appSettings = chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context)
                val ratingDistribution = appSettings.getRatingDistribution()
                
                if ((ratingDistribution[5] ?: 0) > 0) {
                    allCategories.add("⭐⭐⭐⭐⭐ Absolute Favorites")
                }
                if ((ratingDistribution[4] ?: 0) > 0) {
                    allCategories.add("⭐⭐⭐⭐ Loved")
                }
                if ((ratingDistribution[3] ?: 0) > 0) {
                    allCategories.add("⭐⭐⭐ Great")
                }
                if ((ratingDistribution[2] ?: 0) > 0) {
                    allCategories.add("⭐⭐ Good")
                }
                if ((ratingDistribution[1] ?: 0) > 0) {
                    allCategories.add("⭐ Liked")
                }
            }

            // Quality-based categories for lossy audio
            val highQualitySongs = songs.filter { song ->
                val bitrate = song.bitrate ?: 0
                bitrate >= 320000 && !isLosslessAudio(song) && !isDolbyOrSurround(song)
            }
            if (highQualitySongs.isNotEmpty()) allCategories.add("High Quality")

            val standardSongs = songs.filter { song ->
                val bitrate = song.bitrate ?: 0
                bitrate in 128000..319999 && !isLosslessAudio(song) && !isDolbyOrSurround(song)
            }
            if (standardSongs.isNotEmpty()) allCategories.add("Standard")

            // Duration-based categories
            val shortSongs = songs.filter { it.duration < 3 * 60 * 1000 }
            if (shortSongs.isNotEmpty()) allCategories.add("Short (< 3 min)")

            val mediumSongs = songs.filter { it.duration in (3 * 60 * 1000)..(5 * 60 * 1000) }
            if (mediumSongs.isNotEmpty()) allCategories.add("Medium (3-5 min)")

            val longSongs = songs.filter { it.duration > 5 * 60 * 1000 }
            if (longSongs.isNotEmpty()) allCategories.add("Long (> 5 min)")

            allCategories
        }
        categories = result
        preparedSongs = songs
        isLoading = false
    }

    // Filter songs based on selected category - computed asynchronously
    // Initialize with all songs (not emptyList) so LazyListState can restore scroll position correctly
    var filteredSongs by remember { mutableStateOf<List<Song>>(songs) }
    
    LaunchedEffect(preparedSongs, selectedCategory, favoriteSongs) {
        filteredSongs = withContext(Dispatchers.Default) {
            when (selectedCategory) {
                "All" -> preparedSongs
                "❤️ Favorites" -> preparedSongs.filter { it.id in favoriteSongs }
                
                // Rating-based filters
                "⭐⭐⭐⭐⭐ Absolute Favorites" -> {
                    val ratedSongIds = chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context).getSongsByRating(5)
                    preparedSongs.filter { it.id in ratedSongIds }
                }
                "⭐⭐⭐⭐ Loved" -> {
                    val ratedSongIds = chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context).getSongsByRating(4)
                    preparedSongs.filter { it.id in ratedSongIds }
                }
                "⭐⭐⭐ Great" -> {
                    val ratedSongIds = chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context).getSongsByRating(3)
                    preparedSongs.filter { it.id in ratedSongIds }
                }
                "⭐⭐ Good" -> {
                    val ratedSongIds = chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context).getSongsByRating(2)
                    preparedSongs.filter { it.id in ratedSongIds }
                }
                "⭐ Liked" -> {
                    val ratedSongIds = chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context).getSongsByRating(1)
                    preparedSongs.filter { it.id in ratedSongIds }
                }
                
                "Short (< 3 min)" -> preparedSongs.filter { it.duration < 3 * 60 * 1000 }
                "Medium (3-5 min)" -> preparedSongs.filter { it.duration in (3 * 60 * 1000)..(5 * 60 * 1000) }
                "Long (> 5 min)" -> preparedSongs.filter { it.duration > 5 * 60 * 1000 }

                // Audio Quality Filters (Mutually Exclusive)
                "Hi-Res Lossless" -> preparedSongs.filter { isHiResLossless(it) && !isDolbyOrSurround(it) }
                "Lossless" -> preparedSongs.filter { isRegularLossless(it) && !isDolbyOrSurround(it) }
                "Dolby" -> preparedSongs.filter { isDolbyOrSurround(it) }
                "Stereo" -> preparedSongs.filter { (it.channels ?: 2) == 2 && !isDolbyOrSurround(it) }
                "Mono" -> preparedSongs.filter { (it.channels ?: 2) == 1 }
                
                "High Quality" -> preparedSongs.filter { song ->
                    val bitrate = song.bitrate ?: 0
                    bitrate >= 320000 && !isLosslessAudio(song) && !isDolbyOrSurround(song)
                }

                "Standard" -> preparedSongs.filter { song ->
                    val bitrate = song.bitrate ?: 0
                    bitrate in 128000..319999 && !isLosslessAudio(song) && !isDolbyOrSurround(song)
                }

                else -> preparedSongs // Default to showing all songs for any unrecognized category
            }
        }
    }
    
    // Show loading indicator while preparing
    if (isLoading && preparedSongs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContentLoadingIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = context.getString(R.string.library_loading_songs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    if (preparedSongs.isEmpty()) {
        EmptyState(
            message = context.getString(R.string.library_no_songs),
            icon = RhythmIcons.Music.Song,
            onRefresh = onRefreshClick
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Section Header (Scrollable, not sticky) - Shows selection mode or normal mode
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelectionMode) 
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                            else 
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            AnimatedContent(
                                targetState = isSelectionMode,
                                transitionSpec = {
                                    fadeIn(animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )) togetherWith
                                    fadeOut(animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ))
                                },
                                label = "SectionHeaderAnimation"
                            ) { isInSelectionMode ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    if (isInSelectionMode) {
                                        // Selection Mode: Show Close button and selection count
                                        FilledTonalIconButton(
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                                multiSelectionState?.clearSelection()
                                            },
                                            shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = "Clear selection"
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${selectedSongs.size} selected",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "from ${filteredSongs.size} tracks",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                        }
                                    } else {
                                        // Normal Mode: Show icon and track count
                                        Surface(
                                            modifier = Modifier.size(48.dp),
                                            shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                                            color = MaterialTheme.colorScheme.primary,
                                            shadowElevation = 0.dp
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = RhythmIcons.Relax,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = context.getString(R.string.library_your_music),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "${filteredSongs.size} of ${preparedSongs.size} tracks",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                        }

                                        // Expressive Shuffle Button with modern design
                                        if (filteredSongs.isNotEmpty()) {
                                            ExpressiveFilledIconButton(
                                                onClick = {
                                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                                    onShuffleQueue(filteredSongs)
                                                },
                                                modifier = Modifier.size(44.dp),
                                                shape = ExpressiveShapes.SquircleMedium,
                                                colors = IconButtonDefaults.filledIconButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Shuffle,
                                                    contentDescription = context.getString(R.string.cd_shuffle),
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            }
                            
                            // Selection Mode Actions: Show ExpressiveButtonGroup with quick actions
                            if (isSelectionMode && selectedSongs.isNotEmpty()) {
                                // Quick Actions using ExpressiveButtonGroup
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Primary action - Play
                                    ExpressiveButtonGroup(
                                        modifier = Modifier.weight(1f),
                                        style = ButtonGroupStyle.Filled
                                    ) {
                                        ExpressiveGroupButton(
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                                // Play all selected
                                                onPlayQueueFromIndex(selectedSongs, 0)
                                                multiSelectionState?.clearSelection()
                                            },
                                            isStart = true,
                                            isEnd = false,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "Play",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        
                                        ExpressiveGroupButton(
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                                // Shuffle all selected
                                                onShuffleQueue(selectedSongs)
                                                multiSelectionState?.clearSelection()
                                            },
                                            isStart = false,
                                            isEnd = true
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Shuffle,
                                                contentDescription = "Shuffle selected",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    
                                    // Secondary actions
                                    FilledTonalIconButton(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            // Toggle favorite for all
                                            val allAreLiked = selectedSongs.all { favoriteSongs.contains(it.id) }
                                            selectedSongs.forEach { onToggleFavorite(it) }
                                            if (allAreLiked) {
                                                Toast.makeText(context, "Removed ${selectedSongs.size} from favorites", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Added ${selectedSongs.size} to favorites", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        val allAreLiked = selectedSongs.all { favoriteSongs.contains(it.id) }
                                        Icon(
                                            imageVector = if (allAreLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                            contentDescription = if (allAreLiked) "Unlike all" else "Like all",
                                            tint = if (allAreLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    
                                    FilledTonalIconButton(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                            onShowMultiSelectionSheet()
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.MoreVert,
                                            contentDescription = "More actions"
                                        )
                                    }
                                }
                            }
                        }
            }

            // Sticky Filter Chips
            if (categories.size > 1) {
                stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    items = categories,
                                    key = { it }
                                ) { category ->
                                    val isSelected = selectedCategory == category
                                    
                                    // Add TabAnimation-like effects for filter chips
                                    val scaleAnimatable = remember { Animatable(1f) }
                                    val offsetAnimatable = remember { Animatable(0f) }
                                    
                                    // Pop animation for selected filter
                                    LaunchedEffect(isSelected) {
                                        if (isSelected) {
                                            launch {
                                                scaleAnimatable.animateTo(1.05f, animationSpec = tween<Float>(durationMillis = 250, easing = FastOutSlowInEasing))
                                                scaleAnimatable.animateTo(1f, animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing))
                                            }
                                        } else {
                                            scaleAnimatable.snapTo(1f)
                                        }
                                    }
                                    
                                    // Offset animation for neighboring filters
                                    LaunchedEffect(selectedCategory) {
                                        if (!isSelected && selectedCategory != null) {
                                            val currentIndex = categories.indexOf(category)
                                            val selectedIndex = categories.indexOf(selectedCategory)
                                            if (currentIndex >= 0 && selectedIndex >= 0) {
                                                val distance = currentIndex - selectedIndex
                                                if (abs(distance) == 1) { // Only affect direct neighbors
                                                    val direction = if (distance > 0) 1 else -1
                                                    val offsetValue = 8f * direction
                                                    launch {
                                                        offsetAnimatable.animateTo(offsetValue, animationSpec = tween<Float>(durationMillis = 250, easing = FastOutSlowInEasing))
                                                        offsetAnimatable.animateTo(0f, animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing))
                                                    }
                                                } else {
                                                    offsetAnimatable.snapTo(0f)
                                                }
                                            }
                                        } else {
                                            offsetAnimatable.snapTo(0f)
                                        }
                                    }

                                    val containerColor by animateColorAsState(
                                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "chipContainerColor"
                                    )
                                    val labelColor by animateColorAsState(
                                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "chipLabelColor"
                                    )
                                    val borderColor by animateColorAsState(
                                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(
                                            alpha = 0.6f
                                        ),
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "chipBorderColor"
                                    )
                                    val borderWidth by animateDpAsState(
                                        targetValue = if (isSelected) 2.dp else 1.dp,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "chipBorderWidth"
                                    )

                                    FilterChip(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(
                                                context,
                                                haptics,
                                                HapticFeedbackType.LongPress
                                            )
                                            selectedCategory = category
                                        },
                                        label = {
                                            Text(
                                                text = category,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        },
                                        selected = isSelected,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = containerColor,
                                            selectedLabelColor = labelColor,
                                            containerColor = containerColor,
                                            labelColor = labelColor
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = borderColor,
                                            selectedBorderColor = borderColor,
                                            borderWidth = borderWidth
                                        ),
                                        shape = RoundedCornerShape(50.dp),
                                        modifier = Modifier.graphicsLayer {
                                            scaleX = scaleAnimatable.value
                                            scaleY = scaleAnimatable.value
                                            translationX = offsetAnimatable.value
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

            // Songs Items
            items(
                items = filteredSongs,
                key = { "song_${it.id}_${it.uri}" },
                contentType = { "song" }
            ) { song ->
                    AnimateIn(modifier = Modifier.animateItem()) {
                        val isSelected = selectedSongIds.contains(song.id)
                        val selectionIndex = multiSelectionState?.getSelectionIndex(song.id)
                        
                        LibrarySongItemWrapper(
                            song = song,
                            onClick = {
                                if (isSelectionMode) {
                                    // In selection mode, tap toggles selection
                                    onSongSelectionToggle(song)
                                } else {
                                    // Normal mode - play from this position
                                    val songIndex = filteredSongs.indexOf(song)
                                    if (songIndex >= 0) {
                                        onPlayQueueFromIndex(filteredSongs, songIndex)
                                    } else {
                                        onSongClick(song)
                                    }
                                }
                            },
                            onMoreClick = { onAddToPlaylist(song) },
                            onAddToQueue = { onAddToQueue(song) },
                            onPlayNext = { onPlayNext(song) },
                            onToggleFavorite = { onToggleFavorite(song) },
                            isFavorite = favoriteSongs.contains(song.id),
                            onGoToArtist = { 
                                // Find the artist from the list - respect groupByAlbumArtist setting
                                val artist = if (groupByAlbumArtist) {
                                    // When grouping by album artist, match split albumArtist (with split track fallback).
                                    val explicitAlbumArtist = song.albumArtist?.trim().orEmpty()
                                    val songArtistNames = if (explicitAlbumArtist.isNotBlank() && !explicitAlbumArtist.equals("<unknown>", ignoreCase = true)) {
                                        splitArtistNames(explicitAlbumArtist)
                                    } else {
                                        splitArtistNames(song.artist)
                                    }
                                    artists.find { artist ->
                                        songArtistNames.any { it.equals(artist.name, ignoreCase = true) }
                                    }
                                } else {
                                    // When not grouping, check if any split artist name matches
                                    val songArtistNames = splitArtistNames(song.artist)
                                    artists.find { artist ->
                                        songArtistNames.any { it.equals(artist.name, ignoreCase = true) }
                                    }
                                }
                                artist?.let { onGoToArtist(it) }
                            },
                            onGoToAlbum = { 
                                // Find the album from the list
                                val album = albums.find { 
                                    it.title.equals(song.album, ignoreCase = true) && 
                                    it.artist.equals(song.artist, ignoreCase = true)
                                }
                                album?.let { onGoToAlbum(it) }
                            },
                        onShowSongInfo = { onShowSongInfo(song) },
                        onAddToBlacklist = { onAddToBlacklist(song) },
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        haptics = haptics,
                        enableRatingSystem = enableRatingSystem,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        selectionIndex = selectionIndex,
                        onLongPress = { onSongLongPress(song) }
                    )
                }
            }
        }
    }
}

@Composable
fun SingleCardPlaylistsContent(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onCreatePlaylist: (() -> Unit)? = null,
    onImportPlaylist: (() -> Unit)? = null,
    onExportPlaylists: (() -> Unit)? = null,
    appSettings: AppSettings,
    onRefreshClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val playlistViewType by appSettings.playlistViewType.collectAsState()
    val playlistSortOrderString by appSettings.playlistSortOrder.collectAsState()
    val playlistSortOrder = try {
        LibraryPlaylistSortOrder.valueOf(playlistSortOrderString)
    } catch (e: Exception) {
        LibraryPlaylistSortOrder.NAME_ASC
    }
    
    // Loading state for initial render
    var isLoading by remember { mutableStateOf(true) }
    var preparedPlaylists by remember { mutableStateOf(playlists) }
    
    // Prepare and sort playlists asynchronously to avoid blocking UI on tab switch
    LaunchedEffect(playlists, playlistSortOrder) {
        isLoading = true
        preparedPlaylists = withContext(Dispatchers.Default) {
            // Sort playlists based on selected order
            when (playlistSortOrder) {
                LibraryPlaylistSortOrder.NAME_ASC -> playlists.sortedBy { it.name.lowercase() }
                LibraryPlaylistSortOrder.NAME_DESC -> playlists.sortedByDescending { it.name.lowercase() }
                LibraryPlaylistSortOrder.DATE_CREATED_ASC -> playlists.sortedBy { it.id.toLongOrNull() ?: 0L }
                LibraryPlaylistSortOrder.DATE_CREATED_DESC -> playlists.sortedByDescending { it.id.toLongOrNull() ?: 0L }
                LibraryPlaylistSortOrder.SONG_COUNT_ASC -> playlists.sortedBy { it.songs.size }
                LibraryPlaylistSortOrder.SONG_COUNT_DESC -> playlists.sortedByDescending { it.songs.size }
            }
        }
        isLoading = false
    }
    
    // Show loading indicator while preparing
    if (isLoading && preparedPlaylists.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContentLoadingIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = context.getString(R.string.library_loading_playlists),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    if (preparedPlaylists.isEmpty()) {
        EmptyState(
            message = "No playlists yet\nCreate your first playlist using the + button",
            icon = RhythmIcons.Music.Playlist,
            onRefresh = onRefreshClick
        )
    } else {
        if (playlistViewType == PlaylistViewType.GRID) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp // Simple spacing - Scaffold handles rest
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sticky Section Header
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = RhythmIcons.PlaylistFilled,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.library_your_playlists),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${preparedPlaylists.size} ${if (preparedPlaylists.size == 1) "playlist" else "playlists"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(60.dp),
                                shape = RoundedCornerShape(1.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                            ) {}
                        }
                    }
                }
                
                // Playlist Grid Items
                items(
                    items = preparedPlaylists,
                    key = { it.id },
                    contentType = { "playlist" }
                ) { playlist ->
                    AnimateIn(modifier = Modifier.animateItem()) {
                        PlaylistGridItem(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist) },
                            haptics = haptics
                        )
                    }
                }
            }
        } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 16.dp // Simple spacing - Scaffold handles rest
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Section Header (not sticky in list view)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = RhythmIcons.PlaylistFilled,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column (modifier = Modifier.weight(1f)) {
                            Text(
                                text = context.getString(R.string.library_your_playlists),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${preparedPlaylists.size} ${if (preparedPlaylists.size == 1) "playlist" else "playlists"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Surface(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(60.dp),
                                shape = RoundedCornerShape(1.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                        ) {}
                    }
                }
            }

            // Playlist Items
            itemsIndexed(
                items = preparedPlaylists,
                key = { _, playlist -> playlist.id },
                contentType = { _, _ -> "playlist" }
            ) { index, playlist ->
                AnimateIn(modifier = Modifier.animateItem()) {
                    PlaylistItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) },
                        haptics = haptics,
                        itemShape = groupedLibraryItemShape(index, preparedPlaylists.size)
                    )
                }
            }
        }
        }
    }
}

@Composable
fun SingleCardAlbumsContent(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumBottomSheetClick: (Album) -> Unit = {},
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    appSettings: AppSettings,
    onPlayQueue: (List<Song>) -> Unit = { _ -> },
    onShuffleQueue: (List<Song>) -> Unit = { _ -> },
    onRefreshClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val albumViewType by appSettings.albumViewType.collectAsState()
    
    // Loading state for initial render
    var isLoading by remember { mutableStateOf(true) }
    var preparedAlbums by remember { mutableStateOf(albums) }
    
    // Prepare albums asynchronously to avoid blocking UI on tab switch
    LaunchedEffect(albums) {
        isLoading = true
        preparedAlbums = withContext(Dispatchers.Default) {
            // Pre-process albums (sorting, etc.) in background
            albums.toList()
        }
        isLoading = false
    }
    
    // Show loading indicator while preparing
    if (isLoading && preparedAlbums.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContentLoadingIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = context.getString(R.string.library_loading_albums),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    if (preparedAlbums.isEmpty()) {
        EmptyState(
            message = "No albums yet",
            icon = RhythmIcons.Music.Album,
            onRefresh = onRefreshClick
        )
    } else {
                        if (albumViewType == AlbumViewType.GRID) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp // Simple spacing - Scaffold handles rest
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sticky Section Header
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = RhythmIcons.Music.Album,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.library_your_albums),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${preparedAlbums.size} ${if (preparedAlbums.size == 1) "album" else "albums"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.weight(0.1f))

                            // Expressive Shuffle Button
                            if (preparedAlbums.isNotEmpty()) {
                                ExpressiveFilledIconButton(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                        // Shuffle album order but keep each album's songs in their correct order
                                        val shuffledAlbums = preparedAlbums.shuffled()
                                        val allSongs = shuffledAlbums.flatMap { it.songs }
                                        if (allSongs.isNotEmpty()) {
                                            onPlayQueue(allSongs)
                                        }
                                    },
                                    modifier = Modifier.size(44.dp),
                                    shape = ExpressiveShapes.SquircleMedium,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Shuffle,
                                        contentDescription = context.getString(R.string.cd_shuffle),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }                // Album Grid Items
                items(
                    items = preparedAlbums,
                    key = { it.id },
                    contentType = { "album" }
                ) { album ->
                    AnimateIn(modifier = Modifier.animateItem()) {
                        AlbumGridItem(
                            album = album,
                            onClick = { onAlbumBottomSheetClick(album) },
                            onPlayClick = { onAlbumClick(album) },
                            haptics = haptics
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = 16.dp // Simple spacing - Scaffold handles rest
                ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Section Header (not sticky in list view)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = RhythmIcons.Music.Album,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = context.getString(R.string.library_your_albums),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${preparedAlbums.size} ${if (preparedAlbums.size == 1) "album" else "albums"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.weight(0.1f))

                            // Expressive Shuffle Button
                            if (preparedAlbums.isNotEmpty()) {
                                ExpressiveFilledIconButton(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(
                                            context,
                                            haptics,
                                            HapticFeedbackType.LongPress
                                        )
                                        // Shuffle album order but keep each album's songs in their correct order
                                        val shuffledAlbums = preparedAlbums.shuffled()
                                        val allSongs = shuffledAlbums.flatMap { it.songs }
                                        if (allSongs.isNotEmpty()) {
                                            onPlayQueue(allSongs)
                                        }
                                    },
                                    modifier = Modifier.size(44.dp),
                                    shape = ExpressiveShapes.SquircleMedium,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Shuffle,
                                        contentDescription = context.getString(R.string.cd_shuffle),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Album List Items
                itemsIndexed(
                    items = preparedAlbums,
                    key = { _, album -> album.id },
                    contentType = { _, _ -> "album" }
                ) { index, album ->
                    AnimateIn(modifier = Modifier.animateItem()) {
                        LibraryAlbumItem(
                            album = album,
                            onClick = { onAlbumBottomSheetClick(album) },
                            onPlayClick = { onAlbumClick(album) },
                            haptics = haptics,
                            itemShape = groupedLibraryItemShape(index, preparedAlbums.size)
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Deprecated("Use SingleCardPlaylistsContent instead")
fun PlaylistsTab(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    if (playlists.isEmpty()) {
        EmptyState(
            message = "No playlists yet\nCreate your first playlist using the + button",
            icon = RhythmIcons.Music.Playlist
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Enhanced Playlists Section Header (Sticky)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = RhythmIcons.PlaylistFilled,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = context.getString(R.string.library_your_playlists),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "${playlists.size} ${if (playlists.size == 1) "playlist" else "playlists"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Surface(
                        modifier = Modifier
                            .height(2.dp)
                            .width(60.dp),
                        shape = RoundedCornerShape(1.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                    ) {}
                }
            }

            // Scrollable Playlists List
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take remaining space
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = 8.dp, // Start below the sticky elements
                        bottom = 16.dp // Simple spacing - Scaffold handles rest
                    ),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(
                        items = playlists,
                        key = { it.id }
                    ) { playlist ->
                        AnimateIn {
                            PlaylistItem(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist) },
                                haptics = haptics // Pass haptics
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumsTab(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumBottomSheetClick: (Album) -> Unit = {},
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback // Add haptics parameter
) {
    val context = LocalContext.current
    val appSettings = remember { AppSettings.getInstance(context) }
    val albumViewType by appSettings.albumViewType.collectAsState()

    if (albums.isEmpty()) {
        EmptyState(
            message = "No albums yet",
            icon = RhythmIcons.Music.Album
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Enhanced Albums Section Header (Sticky)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = RhythmIcons.Music.Album,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = context.getString(R.string.library_your_albums),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${albums.size} ${if (albums.size == 1) "album" else "albums"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // View type toggle button
                    FilledIconButton(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            val newViewType = if (albumViewType == AlbumViewType.LIST) AlbumViewType.GRID else AlbumViewType.LIST
                            appSettings.setAlbumViewType(newViewType)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (albumViewType == AlbumViewType.LIST) RhythmIcons.AppsGrid else RhythmIcons.List,
                            contentDescription = "Toggle view type",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Surface(
                        modifier = Modifier
                            .height(2.dp)
                            .width(60.dp),
                        shape = RoundedCornerShape(1.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                    ) {}
                }
            }

            // Scrollable Albums Content
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take remaining space
            ) {
                if (albumViewType == AlbumViewType.GRID) {
                    AlbumsGrid(
                        albums = albums,
                        onAlbumClick = { album ->
                            onAlbumBottomSheetClick(album)
                        },
                        onAlbumPlay = onAlbumClick, // This plays the album
                        onSongClick = onSongClick,
                        haptics = haptics // Pass haptics
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 8.dp, // Start below the sticky elements
                            bottom = 16.dp // Simple spacing - Scaffold handles rest
                        ),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(
                            items = albums,
                            key = { it.id }
                        ) { album ->
                            AnimateIn {
                                LibraryAlbumItem(
                                    album = album,
                                    onClick = { onAlbumBottomSheetClick(album) }, // Changed to open bottom sheet
                                    onPlayClick = {
                                        // Play the entire album
                                        onAlbumClick(album)
                                    },
                                    haptics = haptics // Pass haptics
                                )
                            }
                        }
                    }
                }
            }
        }
    }
                    }


@Composable
fun LibrarySongItem(
    song: Song,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    isFavorite: Boolean = false,
    onGoToArtist: () -> Unit = {},
    onGoToAlbum: () -> Unit = {},
    onShowSongInfo: () -> Unit,
    onAddToBlacklist: () -> Unit, // Add blacklist callback
    currentSong: Song? = null, // Add current song parameter
    isPlaying: Boolean = false, // Add playing state
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    enableRatingSystem: Boolean = true, // Add rating system enabled flag
    // Multi-selection parameters
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    selectionIndex: Int? = null,
    onLongPress: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }
    // Track rating state for immediate UI updates
    val appSettings = remember { chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context) }
    var currentRating by remember(song.id) { mutableStateOf(appSettings.getSongRating(song.id)) }
    val isCurrentSong = currentSong?.id == song.id

    // Animated colors for text
    val titleColor by animateColorAsState(
        targetValue = if (isCurrentSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(300),
        label = "titleColor"
    )
    val supportingColor by animateColorAsState(
        targetValue = if (isCurrentSong) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "supportingColor"
    )

    // Selection animations
    val selectionScale by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "selectionScaleAnimation"
    )

    val containerColorForSelection by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.secondaryContainer
            isCurrentSong -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300),
        label = "containerColorAnimation"
    )

    ListItem(
        headlineContent = {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrentSong) FontWeight.Bold else FontWeight.Medium,
                color = titleColor
            )
        },
        supportingContent = {
            Text(
                text = buildString {
                    append(song.artist)
                    append(" • ")
                    append(song.album)
                },
                style = MaterialTheme.typography.bodySmall,
                color = supportingColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Box {
                Surface(
                    shape = rememberExpressiveShapeFor(
                        ExpressiveShapeTarget.SONG_ART,
                        fallbackShape = MaterialTheme.shapes.large
                    ),
                    modifier = Modifier.size(56.dp),
                    border = if (isCurrentSong && !isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    M3ImageUtils.TrackImage(
                        imageUrl = song.artworkUri,
                        trackName = song.title,
                        modifier = Modifier.fillMaxSize(),
                        applyExpressiveShape = false
                    )
                }
                
                // Selection check overlay on album art
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                shape = rememberExpressiveShapeFor(
                                    ExpressiveShapeTarget.SONG_ART,
                                    fallbackShape = MaterialTheme.shapes.large
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectionIndex != null && selectionIndex >= 0) {
                            Text(
                                text = "${selectionIndex + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                } else if (isCurrentSong && isPlaying) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .offset(x = 4.dp, y = 4.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            PlayingEqIcon(
                                modifier = Modifier.size(width = 12.dp, height = 10.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                isPlaying = isPlaying,
                                bars = 3
                            )
                        }
                    }
                }
            }
        },
        trailingContent = {
            // Hide more options button in selection mode
            if (!isSelectionMode) {
                FilledIconButton(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        showDropdown = true
                    },
                    modifier = Modifier
                        .size(width = 40.dp, height = 36.dp),
                    shape = RoundedCornerShape(18.dp), // Pill shape like Android 16
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = RhythmIcons.More,
                        contentDescription = "More options",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    showDropdown = false
                },
                modifier = Modifier
                    .widthIn(min = 220.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(5.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                // Play next
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Play next",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SkipNext,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onPlayNext()
                        }
                    )
                }

                // Add to queue
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Add to queue",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.Queue,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onAddToQueue()
                        }
                    )
                }

                // Toggle favorite
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isFavorite) "Remove from favorites" else "Add to favorites",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = null,
                                    
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onToggleFavorite()
                        }
                    )
                }

                // Add to playlist
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Add to playlist",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onMoreClick()
                        }
                    )
                }

                // Go to artist
                // Surface(
                //     color = MaterialTheme.colorScheme.surfaceContainerHigh,
                //     shape = RoundedCornerShape(12.dp),
                //     modifier = Modifier
                //         .fillMaxWidth()
                //         .padding(horizontal = 8.dp, vertical = 2.dp)
                // ) {
                //     DropdownMenuItem(
                //         text = {
                //             Text(
                //                 "Go to artist",
                //                 style = MaterialTheme.typography.bodyMedium,
                //                 fontWeight = FontWeight.Medium,
                //                 color = MaterialTheme.colorScheme.onSurface
                //             )
                //         },
                //         leadingIcon = {
                //             Surface(
                //                 color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                //                 shape = CircleShape,
                //                 modifier = Modifier.size(32.dp)
                //             ) {
                //                 Icon(
                //                     imageVector = Icons.Rounded.Person,
                //                     contentDescription = null,
                //                     tint = MaterialTheme.colorScheme.onSecondaryContainer,
                //                     modifier = Modifier
                //                         .fillMaxSize()
                //                         .padding(6.dp)
                //                 )
                //             }
                //         },
                //         onClick = {
                //             HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                //             showDropdown = false
                //             onGoToArtist()
                //         }
                //     )
                // }

                // Go to album
                // Surface(
                //     color = MaterialTheme.colorScheme.surfaceContainerHigh,
                //     shape = RoundedCornerShape(12.dp),
                //     modifier = Modifier
                //         .fillMaxWidth()
                //         .padding(horizontal = 8.dp, vertical = 2.dp)
                // ) {
                //     DropdownMenuItem(
                //         text = {
                //             Text(
                //                 "Go to album",
                //                 style = MaterialTheme.typography.bodyMedium,
                //                 fontWeight = FontWeight.Medium,
                //                 color = MaterialTheme.colorScheme.onSurface
                //             )
                //         },
                //         leadingIcon = {
                //             Surface(
                //                 color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                //                 shape = CircleShape,
                //                 modifier = Modifier.size(32.dp)
                //             ) {
                //                 Icon(
                //                     imageVector = Icons.Rounded.Album,
                //                     contentDescription = null,
                //                     tint = MaterialTheme.colorScheme.onSecondaryContainer,
                //                     modifier = Modifier
                //                         .fillMaxSize()
                //                         .padding(6.dp)
                //                 )
                //             }
                //         },
                //         onClick = {
                //             HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                //             showDropdown = false
                //             onGoToAlbum()
                //         }
                //     )
                // }

                // Rate song - only show if rating system is enabled
                if (enableRatingSystem) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp)
                                        )
                                    }
                                    Text(
                                        "Rate Song",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            chromahub.rhythm.app.shared.presentation.components.RatingStars(
                                rating = currentRating,
                                onRatingChanged = { newRating ->
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    currentRating = newRating  // Update UI immediately
                                    appSettings.setSongRating(song.id, newRating)
                                    if (newRating > 0 && !isFavorite) {
                                        onToggleFavorite()
                                    }
                                },
                                enabled = true,
                                size = 24.dp
                            )
                        }
                    }
                }

                // Song info
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Song info",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onShowSongInfo()
                        }
                    )
                }

                // Add to blacklist
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Add to blacklist",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Block,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                )
                            }
                        },
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onAddToBlacklist()
                        }
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
}

/**
 * Wrapper composable for LibrarySongItem with rounded corners and border for active song
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibrarySongItemWrapper(
    song: Song,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    isFavorite: Boolean = false,
    onGoToArtist: () -> Unit = {},
    onGoToAlbum: () -> Unit = {},
    onShowSongInfo: () -> Unit,
    onAddToBlacklist: () -> Unit,
    currentSong: Song? = null,
    isPlaying: Boolean = false,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    enableRatingSystem: Boolean = true,
    itemShape: RoundedCornerShape = RoundedCornerShape(12.dp),
    // Multi-selection parameters
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    selectionIndex: Int? = null,
    onLongPress: () -> Unit = {}
) {
    val context = LocalContext.current
    val isCurrentSong = currentSong?.id == song.id
    
    // Selection animations
    val selectionScale by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "selectionScaleAnimation"
    )
    
    // Animated colors
    val containerColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            isCurrentSong -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "containerColor"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .graphicsLayer {
                scaleX = selectionScale
                scaleY = selectionScale
            }
            .combinedClickable(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onClick()
                },
                onLongClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onLongPress()
                }
            ),
        shape = itemShape,
        color = containerColor
    ) {
        LibrarySongItem(
            song = song,
            onClick = {}, // Click handled by combinedClickable
            onMoreClick = onMoreClick,
            onAddToQueue = onAddToQueue,
            onPlayNext = onPlayNext,
            onToggleFavorite = onToggleFavorite,
            isFavorite = isFavorite,
            onGoToArtist = onGoToArtist,
            onGoToAlbum = onGoToAlbum,
            onShowSongInfo = onShowSongInfo,
            onAddToBlacklist = onAddToBlacklist,
            currentSong = currentSong,
            isPlaying = isPlaying,
            haptics = haptics,
            enableRatingSystem = enableRatingSystem,
            isSelected = isSelected,
            isSelectionMode = isSelectionMode,
            selectionIndex = selectionIndex,
            onLongPress = onLongPress
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    itemShape: RoundedCornerShape = RoundedCornerShape(20.dp)
) {
    val context = LocalContext.current
    
    // Get unique album arts from playlist songs (up to 4)
    val albumArts = remember(playlist.songs) {
        playlist.songs
            .distinctBy { it.albumId }
            .take(4)
    }
    
    Surface(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = itemShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stylish playlist artwork with collage
            Surface(
                modifier = Modifier.size(72.dp),
                shape = rememberExpressiveShapeFor(
                    ExpressiveShapeTarget.PLAYLIST_ART,
                    fallbackShape = RoundedCornerShape(16.dp)
                ),
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (playlist.artworkUri != null) {
                        // Use custom playlist artwork if available
                        M3ImageUtils.PlaylistImage(
                            imageUrl = playlist.artworkUri,
                            playlistName = playlist.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (albumArts.isNotEmpty()) {
                        // Create collage from album arts
                        PlaylistArtCollage(
                            songs = albumArts,
                            playlistName = playlist.name
                        )
                    } else {
                        // Fallback to playlist icon with solid background (matching artwork corner radius)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = RhythmIcons.PlaylistFilled,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Playlist info with better typography
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Enhanced metadata display with pill shape
                    Surface(
                        shape = RoundedCornerShape(50), // Pill shape
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${playlist.songs.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    if (playlist.songs.isNotEmpty()) {
                        val totalDurationMs = playlist.songs.sumOf { it.duration }
                        val totalMinutes = (totalDurationMs / (1000 * 60)).toInt()
                        val durationText = if (totalMinutes >= 60) {
                            val hours = totalMinutes / 60
                            val minutes = totalMinutes % 60
                            "${hours}h ${minutes}m"
                        } else {
                            "${totalMinutes}m"
                        }

                        Surface(
                            shape = RoundedCornerShape(50), // Pill shape
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = durationText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Stylish forward arrow with animation hint
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 0.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Open playlist",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun groupedLibraryItemShape(index: Int, totalCount: Int): RoundedCornerShape {
    return when {
        totalCount <= 1 -> RoundedCornerShape(24.dp)
        index == 0 -> RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 6.dp,
            bottomEnd = 6.dp
        )
        index == totalCount - 1 -> RoundedCornerShape(
            topStart = 6.dp,
            topEnd = 6.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )
        else -> RoundedCornerShape(6.dp)
    }
}

@Composable
fun PlaylistArtCollage(
    songs: List<Song>,
    playlistName: String
) {
    when (songs.size) {
        1 -> {
            // Single album art
            M3ImageUtils.AlbumArt(
                imageUrl = songs[0].artworkUri,
                albumName = songs[0].album,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(0.dp),
                applyExpressiveShape = false
            )
        }
        2 -> {
            // Two album arts side by side
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()) {
                    M3ImageUtils.AlbumArt(
                        imageUrl = songs[0].artworkUri,
                        albumName = songs[0].album,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp),
                        applyExpressiveShape = false
                    )
                }
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()) {
                    M3ImageUtils.AlbumArt(
                        imageUrl = songs[1].artworkUri,
                        albumName = songs[1].album,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp),
                        applyExpressiveShape = false
                    )
                }
            }
        }
        3 -> {
            // Three album arts: one large on left, two stacked on right
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()) {
                    M3ImageUtils.AlbumArt(
                        imageUrl = songs[0].artworkUri,
                        albumName = songs[0].album,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp),
                        applyExpressiveShape = false
                    )
                }
                Column(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()) {
                        M3ImageUtils.AlbumArt(
                            imageUrl = songs[1].artworkUri,
                            albumName = songs[1].album,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp),
                            applyExpressiveShape = false
                        )
                    }
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()) {
                        M3ImageUtils.AlbumArt(
                            imageUrl = songs[2].artworkUri,
                            albumName = songs[2].album,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp),
                            applyExpressiveShape = false
                        )
                    }
                }
            }
        }
        else -> {
            // Four album arts in a 2x2 grid
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Row(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()) {
                        M3ImageUtils.AlbumArt(
                            imageUrl = songs[0].artworkUri,
                            albumName = songs[0].album,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp),
                            applyExpressiveShape = false
                        )
                    }
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()) {
                        M3ImageUtils.AlbumArt(
                            imageUrl = songs[1].artworkUri,
                            albumName = songs[1].album,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp),
                            applyExpressiveShape = false
                        )
                    }
                }
                Row(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()) {
                        M3ImageUtils.AlbumArt(
                            imageUrl = songs[2].artworkUri,
                            albumName = songs[2].album,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp),
                            applyExpressiveShape = false
                        )
                    }
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()) {
                        M3ImageUtils.AlbumArt(
                            imageUrl = songs[3].artworkUri,
                            albumName = songs[3].album,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp),
                            applyExpressiveShape = false
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryAlbumItem(
    album: Album,
    onClick: () -> Unit,
    onPlayClick: () -> Unit = {},
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    itemShape: RoundedCornerShape = RoundedCornerShape(20.dp)
) {
    val context = LocalContext.current
    val artworkShape = rememberExpressiveShapeFor(
        ExpressiveShapeTarget.ALBUM_ART,
        fallbackShape = RoundedCornerShape(18.dp)
    )
    
    Surface(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = itemShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced album artwork
            Surface(
                modifier = Modifier.size(68.dp),
                shape = artworkShape,
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (album.artworkUri != null) Color.Transparent
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (album.artworkUri != null) {
                        M3ImageUtils.AlbumArt(
                            imageUrl = album.artworkUri,
                            albumName = album.title,
                            modifier = Modifier.fillMaxSize(),
                            shape = artworkShape
                        )
                    } else {
                        Icon(
                            imageVector = RhythmIcons.Album,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(18.dp))
            
            // Enhanced album info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp) // Add padding to prevent text from being cut off
            ) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(6.dp)) // Increase spacing
                
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp)) // Increase spacing
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Song count pill
                    Surface(
                        shape = RoundedCornerShape(50), // Pill shape
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${album.numberOfSongs} Songs",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Year pill
                    if (album.year > 0) {
                        Surface(
                            shape = RoundedCornerShape(50), // Pill shape
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${album.year}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }
            
            // Enhanced play button
            FilledIconButton(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onPlayClick()
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = RhythmIcons.Play,
                    contentDescription = "Play album",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onRefresh: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 0.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(48.dp)
            ) {
                val context = LocalContext.current
                val haptics = LocalHapticFeedback.current
                val animatedSize by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.6f,
                        stiffness = 100f
                    ),
                    label = "iconAnimation"
                )
                
                val animatedAlpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 800,
                        delayMillis = 200
                    ),
                    label = "alphaAnimation"
                )
                
                // Enhanced icon container with gradient background
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer { alpha = animatedAlpha }
                        )
                    }
                }
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * 1.2,
                    modifier = Modifier.graphicsLayer { alpha = animatedAlpha }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = context.getString(R.string.library_start_collection),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { alpha = animatedAlpha * 0.8f }
                )

                if (onRefresh != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                            onRefresh()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Refresh")
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimateIn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = 50),
        label = "alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.graphicsLayer(
            alpha = alpha,
            scaleX = scale,
            scaleY = scale
        )
    ) {
        content()
    }
}

@Composable
fun AlbumsGrid(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    onAlbumPlay: (Album) -> Unit,
    onSongClick: (Song) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback // Add haptics parameter
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 16.dp // Simple spacing - Scaffold handles rest
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = albums,
            key = { it.id }
        ) { album ->
            AnimateIn {
                AlbumGridItem(
                    album = album,
                    onClick = { onAlbumClick(album) }, // Card click opens bottom sheet
                    onPlayClick = { onAlbumPlay(album) }, // Play button plays album
                    haptics = haptics // Pass haptics
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistGridItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Playlist artwork - maintain square ratio
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = rememberExpressiveShapeFor(
                    ExpressiveShapeTarget.PLAYLIST_ART,
                    fallbackShape = RoundedCornerShape(16.dp)
                ),
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (playlist.songs.isNotEmpty()) {
                        PlaylistArtCollage(
                            songs = playlist.songs,
                            playlistName = playlist.name
                        )
                    } else {
                        Icon(
                            imageVector = RhythmIcons.PlaylistFilled,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Playlist name
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Song count pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50), // Pill shape
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = RhythmIcons.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "${playlist.songs.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumGridItem(
    album: Album,
    onClick: () -> Unit,
    onPlayClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback // Add haptics parameter
) {
    val context = LocalContext.current
    
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Album artwork - maintain square ratio
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = rememberExpressiveShapeFor(
                        ExpressiveShapeTarget.ALBUM_ART,
                        fallbackShape = RoundedCornerShape(16.dp)
                    ),
                    tonalElevation = 0.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (album.artworkUri != null) Color.Transparent
                                else MaterialTheme.colorScheme.secondaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (album.artworkUri != null) {
                            M3ImageUtils.AlbumArt(
                                imageUrl = album.artworkUri,
                                albumName = album.title,
                                modifier = Modifier.fillMaxSize(),
                                applyExpressiveShape = false
                            )
                        } else {
                            Icon(
                                imageVector = RhythmIcons.Album,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Album title
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Artist name
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Pills row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    // Song count pill
                    Surface(
                        shape = RoundedCornerShape(50), // Pill shape
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "${album.numberOfSongs}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Year pill
                    if (album.year > 0) {
                        Surface(
                            shape = RoundedCornerShape(50), // Pill shape
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "${album.year}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }
            
            // Play button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                FilledIconButton(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        onPlayClick()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = RhythmIcons.Play,
                        contentDescription = "Play album",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SingleCardArtistsContent(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onPlayQueue: (List<Song>) -> Unit = { _ -> },
    onShuffleQueue: (List<Song>) -> Unit = { _ -> },
    onRefreshClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val viewModel = viewModel<chromahub.rhythm.app.viewmodel.MusicViewModel>()
    val appSettings = remember { AppSettings.getInstance(context) }
    
    // Get artist view type from settings
    val artistViewType by appSettings.artistViewType.collectAsState()
    
    var selectedCategory by remember { mutableStateOf("All") }
    var currentSortOption by remember { mutableStateOf(ArtistSortOption.NAME_ASC) }
    var showSortOptions by remember { mutableStateOf(false) }
    
    // Loading state for async sorting
    var isLoading by remember { mutableStateOf(true) }
    var sortedArtists by remember { mutableStateOf(artists) }
    
    // Define categories for artists
    val categories = remember(artists) {
        listOf("All")
    }
    
    // Sort artists asynchronously to avoid blocking UI
    LaunchedEffect(artists, currentSortOption) {
        isLoading = true
        sortedArtists = withContext(Dispatchers.Default) {
            when (currentSortOption) {
                ArtistSortOption.NAME_ASC -> artists.sortedBy { it.name.lowercase() }
                ArtistSortOption.NAME_DESC -> artists.sortedByDescending { it.name.lowercase() }
                ArtistSortOption.TRACK_COUNT_DESC -> artists.sortedByDescending { it.numberOfTracks }
                ArtistSortOption.ALBUM_COUNT_DESC -> artists.sortedByDescending { it.numberOfAlbums }
            }
        }
        isLoading = false
    }
    
    // Determine if we should use grid or list view
    val isGridView = artistViewType == ArtistViewType.GRID
    
    // Show loading indicator while sorting
    if (isLoading && sortedArtists.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContentLoadingIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = context.getString(R.string.library_loading_artists),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }
    
    // Check for empty state
    if (sortedArtists.isEmpty()) {
        EmptyState(
            message = "No artists yet",
            icon = RhythmIcons.Artist,
            onRefresh = onRefreshClick
        )
        return
    }
    
    if (isGridView) {
        // Grid view using LazyVerticalGrid as main container
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 0.dp,
                bottom = 16.dp // Simple spacing - Scaffold handles rest
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sticky header for grid view
            item(span = { GridItemSpan(maxLineSpan) }) {
                ArtistSectionHeader(
                    artistCount = sortedArtists.size,
                    artists = sortedArtists,
                    applyOuterHorizontalPadding = false,
                    onPlayAll = {
                        val allSongs = sortedArtists.flatMap { it.songs }
                        if (allSongs.isNotEmpty()) {
                            onPlayQueue(allSongs)
                        }
                    },
                    onShuffleAll = {
                        val allSongs = sortedArtists.flatMap { it.songs }
                        if (allSongs.isNotEmpty()) {
                            onShuffleQueue(allSongs)
                        }
                    },
                    haptics = haptics
                )
            }
            
            if (sortedArtists.isNotEmpty()) {
                items(
                    items = sortedArtists,
                    key = { "gridartist_${it.id}" },
                    contentType = { "artist" }
                ) { artist ->
                    AnimateIn(modifier = Modifier.animateItem()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ArtistGridCard(
                                artist = artist,
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    onArtistClick(artist)
                                },
                                onPlayClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    viewModel.playArtist(artist)
                                }
                            )
                        }
                    }
                }
            }
        }
    } else {
        // List view using LazyColumn as main container
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 16.dp // Simple spacing - Scaffold handles rest
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Section header (not sticky in list view)
            item {
                ArtistSectionHeader(
                    artistCount = sortedArtists.size,
                    artists = sortedArtists,
                    onPlayAll = {
                        val allSongs = sortedArtists.flatMap { it.songs }
                        if (allSongs.isNotEmpty()) {
                            onPlayQueue(allSongs)
                        }
                    },
                    onShuffleAll = {
                        val allSongs = sortedArtists.flatMap { it.songs }
                        if (allSongs.isNotEmpty()) {
                            onShuffleQueue(allSongs)
                        }
                    },
                    haptics = haptics
                )
            }
            
            if (sortedArtists.isNotEmpty()) {
                itemsIndexed(
                    items = sortedArtists,
                    key = { _, artist -> "listartist_${artist.id}" },
                    contentType = { _, _ -> "artist" }
                ) { index, artist ->
                    AnimateIn(modifier = Modifier.animateItem()) {
                        ArtistListCard(
                            artist = artist,
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                onArtistClick(artist)
                            },
                            onPlayClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                viewModel.playArtist(artist)
                            },
                            itemShape = groupedLibraryItemShape(index, sortedArtists.size)
                        )
                    }
                }
            }
        }
    }

    // Sort options bottom sheet
    if (showSortOptions) {
        ModalBottomSheet(
            onDismissRequest = { showSortOptions = false },
            sheetState = rememberModalBottomSheetState(),
            dragHandle = { 
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = context.getString(R.string.library_sort_artists),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                ArtistSortOption.entries.forEach { sortOption ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                HapticUtils.performHapticFeedback(
                                    context,
                                    haptics,
                                    HapticFeedbackType.LongPress
                                )
                                currentSortOption = sortOption
                                showSortOptions = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sortOption.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (currentSortOption == sortOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (currentSortOption == sortOption) {
                            Icon(
                                imageVector = RhythmIcons.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ArtistSectionHeader(
    artistCount: Int,
    artists: List<Artist> = emptyList(),
    applyOuterHorizontalPadding: Boolean = true,
    onPlayAll: () -> Unit = {},
    onShuffleAll: () -> Unit = {},
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback? = null
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (applyOuterHorizontalPadding) 20.dp else 0.dp,
                vertical = 8.dp
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = RhythmIcons.Artist,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = context.getString(R.string.library_your_artists),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$artistCount ${if (artistCount == 1) "artist" else "artists"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Surface(
                modifier = Modifier
                .height(2.dp)
                .width(60.dp),
                shape = RoundedCornerShape(1.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            ) {}
        }
    }
}

enum class ArtistSortOption(val label: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
    TRACK_COUNT_DESC("Songs (High to Low)"),
    ALBUM_COUNT_DESC("Albums (High to Low)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistGridCard(
    artist: Artist,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val artworkShape = rememberExpressiveShapeFor(ExpressiveShapeTarget.ARTIST_ART)
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Artist image with expressive shape container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = rememberExpressiveShapeFor(
                    ExpressiveShapeTarget.ARTIST_ART,
                    fallbackShape = RoundedCornerShape(16.dp)
                ),
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (artist.artworkUri != null) Color.Transparent
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    M3ImageUtils.ArtistImage(
                        imageUrl = artist.artworkUri,
                        artistName = artist.name,
                        modifier = Modifier.fillMaxSize(),
                        applyExpressiveShape = false
                    )
                    
                    // Play button overlay positioned at bottom right
                    Surface(
                        onClick = onPlayClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = RhythmIcons.Play,
                                contentDescription = "Play ${artist.name}",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                // Artist name
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Artist info row with pills (centered)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Track count pill
                    Surface(
                        shape = RoundedCornerShape(50), // Pill shape
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "${artist.numberOfTracks}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    
                    // Album count pill
                    if (artist.numberOfAlbums > 0) {
                        Surface(
                            shape = RoundedCornerShape(50), // Pill shape
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.Album,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "${artist.numberOfAlbums}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
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
private fun ArtistListCard(
    artist: Artist,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    itemShape: RoundedCornerShape = RoundedCornerShape(20.dp)
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = itemShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artist image with expressive shape
            M3ImageUtils.ArtistImage(
                imageUrl = artist.artworkUri,
                artistName = artist.name,
                modifier = Modifier
                    .size(68.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Artist name
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Artist info with pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Track count pill
                    Surface(
                        shape = RoundedCornerShape(50), // Pill shape
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = RhythmIcons.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${artist.numberOfTracks}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Album count pill
                    if (artist.numberOfAlbums > 0) {
                        Surface(
                            shape = RoundedCornerShape(50), // Pill shape
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.Album,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${artist.numberOfAlbums} Albums",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Play button
            FilledIconButton(
                onClick = onPlayClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = RhythmIcons.Play,
                    contentDescription = "Play ${artist.name}",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SingleCardExplorerContent(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onShowSongInfo: (Song) -> Unit,
    onPlayQueue: (List<Song>) -> Unit,
    onPlayQueueFromIndex: (List<Song>, Int) -> Unit,
    onShuffleQueue: (List<Song>) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    appSettings: AppSettings,
    reloadTrigger: Int = 0,
    onCreatePlaylist: (String) -> Unit = { _ -> },
    musicViewModel: MusicViewModel,
    currentSong: Song? = null, // Add current song parameter
    isPlaying: Boolean = false, // Add playing state
    enableRatingSystem: Boolean = true
) {
    val context = LocalContext.current
    val activity = context as Activity
    
    // State for creating playlist from folder
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var folderSongsForPlaylist by remember { mutableStateOf<List<Song>>(emptyList()) }
    var playlistNamePrefix by remember { mutableStateOf("") }
    
    val playlists by musicViewModel.playlists.collectAsState()

    // Handle back gesture to go level up - currentPath is defined inside SingleCardExplorerContent, this code may be in wrong scope
    // Removing duplicated back handler that references undefined currentPath
    // Code should be inside the composable where currentPath is defined

    // Check storage permission based on Android version
    val hasStoragePermission = remember {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13, 14, 15, 16+ (API 33+) - granular media permissions
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                // Android 12 and below (API 32 and lower) - legacy READ_EXTERNAL_STORAGE
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var currentPath by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoadingDirectory by remember { mutableStateOf(false) }

    // Handle permission result in a LaunchedEffect
    LaunchedEffect(hasStoragePermission) {
        if (!hasStoragePermission) {
            showPermissionDialog = true
        }
    }

    // Permission not granted - show request UI
    if (!hasStoragePermission || showPermissionDialog) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Permission request card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Permission icon
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Title
                    Text(
                        text = context.getString(R.string.storage_permission_required),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    // Description
                    Text(
                        text = context.getString(R.string.storage_permission_desc),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grant permission button
                    Button(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)

                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                                    // Android 13, 14, 15, 16+ (API 33+) - granular media permissions
                                    ActivityCompat.requestPermissions(
                                        activity,
                                        arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                                        1001
                                    )
                                }
                                else -> {
                                    // Android 12 and below (API 32 and lower) - legacy permission
                                    ActivityCompat.requestPermissions(
                                        activity,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        1001
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (hasStoragePermission) Icons.Default.Check else Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasStoragePermission) "Permission Granted" else "Grant Permission",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Alternative info text
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = context.getString(R.string.storage_permission_audio_only),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2
                                )
                            }
                        }
                    }
                }
            }
        }
        return
    }

    // Permission granted - show file explorer

    // Get audio file extensions to filter
    val audioExtensions = remember {
        setOf("mp3", "flac", "m4a", "aac", "ogg", "wav", "wma", "aiff", "opus")
    }

    // Pre-compute song path map for fast lookups (computed asynchronously to avoid blocking UI)
    var songPathMap by remember { mutableStateOf<Map<String, Song>>(emptyMap()) }
    var isPathMapLoading by remember { mutableStateOf(true) }
    var songPathMapVersion by remember { mutableStateOf(0) } // Track version for cache invalidation
    
    LaunchedEffect(songs) {
        isPathMapLoading = true
        songPathMap = withContext(Dispatchers.Default) {
            val map = mutableMapOf<String, Song>()
            songs.forEach { song ->
                try {
                    val path = getFilePathFromUri(song.uri, context)
                    if (path != null && path.isNotEmpty()) {
                        // Normalize path: remove double slashes and trailing slashes
                        val normalizedPath = path.replace("//", "/").trimEnd('/')
                        map[normalizedPath] = song
                    }
                } catch (e: Exception) {
                    // Skip problematic songs
                }
            }
            android.util.Log.d("LibraryScreen", "Pre-computed path map with ${map.size} songs out of ${songs.size} total")
            if (map.isNotEmpty()) {
                // Log a sample path for debugging
                val samplePath = map.keys.firstOrNull()
                android.util.Log.d("LibraryScreen", "Sample path in map: $samplePath")
            }
            map
        }
        songPathMapVersion++ // Increment version when map changes
        isPathMapLoading = false
    }

    // Directory items state - loaded asynchronously to prevent ANR
    var currentItems by remember { mutableStateOf<List<ExplorerItem>>(emptyList()) }
    
    // Initial loading state for first tab open
    var isInitialLoading by remember { mutableStateOf(true) }

    // Pinned folders state
    val pinnedFolders by appSettings.pinnedFolders.collectAsState()

    // Breadcrumb scroll state
    val breadcrumbScrollState = rememberLazyListState()

    // Reset loading state if content is already available (defensive fix for navigation issues)
    LaunchedEffect(currentItems, currentPath) {
        if (currentItems.isNotEmpty()) {
            isLoadingDirectory = false
            isInitialLoading = false
        }
    }

    // Cache for directory contents to improve performance
    val directoryCache = remember { mutableMapOf<String?, List<ExplorerItem>>() }
    var lastCacheVersion by remember { mutableStateOf(-1) } // Track which songPathMap version the cache was built from
    
    // Debounce key to prevent rapid recompositions causing ANR
    var debounceJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Handle reload trigger - clear cache and reload current directory
    LaunchedEffect(reloadTrigger) {
        if (reloadTrigger > 0) {
            // Clear the entire cache
            directoryCache.clear()
            
            // Force reload of current directory by removing it from cache and triggering reload
            val cacheKey = currentPath
            directoryCache.remove(cacheKey)
            
            // Cancel any pending load operation
            debounceJob?.cancel()
            
            // Reload current directory
            if (currentPath == null) {
                val storageItems = getStorageRoots(context)
                currentItems = storageItems
                directoryCache[cacheKey] = storageItems
                isLoadingDirectory = false
            } else {
                // Wait for songPathMap computation to complete
                if (isPathMapLoading) {
                    isLoadingDirectory = true
                    return@LaunchedEffect
                }
                
                isLoadingDirectory = true
                try {
                    val items = withContext(Dispatchers.IO) {
                        getDirectoryContentsOptimized(currentPath!!, songPathMap, context)
                    }
                    // Show all items immediately for fast navigation
                    val sortedItems = items.sortedWith(
                        compareBy<ExplorerItem> { it.type != ExplorerItemType.FOLDER }
                            .thenBy { it.name.lowercase() }
                    )
                    currentItems = sortedItems
                    directoryCache[cacheKey] = sortedItems
                } catch (e: Exception) {
                    currentItems = emptyList()
                } finally {
                    isLoadingDirectory = false
                }
            }
        }
    }

    // Load directory contents asynchronously with caching and debouncing
    // CRITICAL: Must depend on songPathMap to reload when path map is ready
    LaunchedEffect(currentPath, songPathMap) {
        // Cancel any pending load operation
        debounceJob?.cancel()
        
        val cacheKey = currentPath
        
        if (currentPath == null) {
            // Show device storage roots - run in IO to prevent blocking
            isLoadingDirectory = true
            val storageItems = withContext(Dispatchers.IO) {
                getStorageRoots(context)
            }
            currentItems = storageItems
            directoryCache[cacheKey] = storageItems
            isLoadingDirectory = false
            isInitialLoading = false
        } else {
            // Wait for songPathMap computation to complete before loading directory contents
            // This prevents showing empty folders when the path map hasn't loaded yet
            // Note: We check isPathMapLoading, not isEmpty(), because user might have no songs
            if (isPathMapLoading) {
                isLoadingDirectory = true
                // Don't proceed - the effect will re-run when songPathMap changes
                return@LaunchedEffect
            }
            
            // Clear entire cache when songPathMap version changes (songs were reloaded)
            // This ensures we get fresh data with the new song list
            if (lastCacheVersion != songPathMapVersion) {
                android.util.Log.d("LibraryScreen", "SongPathMap version changed from $lastCacheVersion to $songPathMapVersion, clearing cache")
                directoryCache.clear()
                lastCacheVersion = songPathMapVersion
            }
            
            // Check cache first to avoid unnecessary reloads
            // Use cache if it exists (even if empty, as it might be a legitimately empty folder)
            val cached = directoryCache[cacheKey]
            if (cached != null) {
                // Use cached results immediately
                isLoadingDirectory = false
                currentItems = cached
            } else {
                // Set loading state FIRST before clearing items to ensure loading indicator shows
                isLoadingDirectory = true
                
                // Clear current items to show loading indicator (prevents showing stale content during navigation)
                currentItems = emptyList()
                
                // Load without debounce to make navigation feel more responsive
                debounceJob = launch {
                    try {
                        val items = withContext(Dispatchers.IO) {
                            getDirectoryContentsOptimized(currentPath!!, songPathMap, context)
                        }
                        
                        // PERFORMANCE: Show all folders immediately without filtering
                        // This matches behavior of other FOSS music players for instant results
                        // Users can still see folder contents when they navigate into them
                        val sortedItems = items.sortedWith(
                            compareBy<ExplorerItem> { it.type != ExplorerItemType.FOLDER }
                                .thenBy { it.name.lowercase() }
                        )
                        
                        // Only update if this job wasn't cancelled (i.e., user didn't navigate away)
                        if (isActive) {
                            currentItems = sortedItems
                            
                            // Cache the results (including empty results for legitimately empty folders)
                            if (directoryCache.size >= 20) {
                                // Remove oldest entry if cache is full
                                directoryCache.remove(directoryCache.keys.first())
                            }
                            directoryCache[cacheKey] = sortedItems
                        }
                    } catch (e: Exception) {
                        // Only update if this job wasn't cancelled
                        if (isActive) {
                            android.util.Log.e("LibraryScreen", "Error loading directory $currentPath", e)
                            isInitialLoading = false
                            // Check if we have cached data from a previous successful load
                            val previousCache = directoryCache[cacheKey]
                            if (previousCache != null) {
                                // Use previous cache on error
                                currentItems = previousCache
                            } else {
                                // Show empty state only if no cache exists
                                currentItems = emptyList()
                            }
                        }
                    } finally {
                        if (isActive) {
                            isLoadingDirectory = false
                        }
                    }
                }
            }
        }
    }

    // Get current folder songs for play all/shuffle buttons
    val currentFolderSongs = remember(currentItems) {
        currentItems.filter { it.type == ExplorerItemType.FILE && it.song != null }
            .mapNotNull { it.song }
    }

    // Handle back gesture to navigate up directory levels
    BackHandler(enabled = currentPath != null) {
        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
        currentPath = getParentPath(currentPath!!)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Sticky Section Header for Explorer with breadcrumb navigation
            stickyHeader {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = rememberExpressiveShapeFor(ExpressiveShapeTarget.PLAYER_CONTROLS),
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = context.getString(R.string.library_explore),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (currentPath == null) "${currentItems.size} locations" else "${currentItems.size} items",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Grouped back + shuffle controls in folder view
                        if (currentPath != null) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalIconButton(
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                        currentPath = getParentPath(currentPath!!)
                                    },
                                    shape = ButtonGroupDefaults.connectedLeadingButtonShapes().shape,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    ),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "Go back",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                if (currentFolderSongs.isNotEmpty()) {
                                    FilledTonalIconButton(
                                        onClick = {
                                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                            onShuffleQueue(currentFolderSongs)
                                        },
                                        shape = ButtonGroupDefaults.connectedTrailingButtonShapes().shape,
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        ),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Shuffle,
                                            contentDescription = context.getString(R.string.cd_shuffle),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Breadcrumb navigation
                    if (currentPath != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ExplorerBreadcrumb(
                            path = currentPath!!,
                            onNavigateTo = { newPath ->
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                currentPath = newPath
                            },
                            onGoHome = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                currentPath = null
                            },
                            scrollState = breadcrumbScrollState
                        )
                    }
                }
            }
        }

                // Single unified loading indicator
                // Show "Initializing" only at root on first load, otherwise show "Loading directory"
                // Also show when pathMap is loading (required to show directory contents)
                if ((isLoadingDirectory || isInitialLoading || (isPathMapLoading && currentPath != null)) && currentItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ContentLoadingIndicator(
                                    modifier = Modifier.size(48.dp)
                                )

                                Text(
                                    text = if (isInitialLoading && currentPath == null) {
                                        "Initializing Explorer..."
                                    } else if (isPathMapLoading) {
                                        "Indexing music files..."
                                    } else {
                                        "Loading directory..."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Storage Locations - Show at root level
                if (!isInitialLoading && currentPath == null && currentItems.any { it.type == ExplorerItemType.STORAGE }) {
                    // Filter only storage items
                    val storageItems = currentItems.filter { it.type == ExplorerItemType.STORAGE }

                    // Header for storage locations
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = "Storage",
                                
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = context.getString(R.string.library_storage_locations),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Storage items
                    itemsIndexed(
                        items = storageItems,
                        key = { _, item -> "storage_${item.path}" }
                    ) { index, item ->
                        AnimateIn {
                            ExplorerItemCard(
                                item = item,
                                onItemClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    currentPath = item.path
                                },
                                onSongClick = onSongClick,
                                onAddToPlaylist = onAddToPlaylist,
                                onAddToQueue = onAddToQueue,
                                onShowSongInfo = onShowSongInfo,
                                haptics = haptics,
                                isPinned = false, // Storages can't be pinned
                                onPinToggle = null, // No pin toggle for storages
                                onPlayFolder = null, // Storages don't have play option
                                onAddFolderToQueue = null, // Storages don't have queue option
                                currentSong = currentSong,
                                isPlaying = isPlaying,
                                enableRatingSystem = enableRatingSystem,
                                itemShape = groupedLibraryItemShape(index, storageItems.size)
                            )
                        }
                    }
                }

                // Pinned Folders - only show when at root and have pinned folders
                if (!isInitialLoading && currentPath == null && pinnedFolders.isNotEmpty()) {
                    // Filter pinned folders that exist
                    val existingPinnedFolders = pinnedFolders.filter { pinnedPath ->
                        try {
                            val file = File(pinnedPath)
                            file.exists() && file.isDirectory && file.canRead()
                        } catch (e: Exception) {
                            false
                        }
                    }

                    if (existingPinnedFolders.isNotEmpty()) {
                        // Create ExplorerItem for each pinned folder
                        val pinnedFolderItems = existingPinnedFolders.map { folderPath ->
                            val folderName = File(folderPath).name
                            val itemCount = countAudioFilesInDirectoryShallow(File(folderPath), audioExtensions)
                            ExplorerItem(
                                name = folderName,
                                path = folderPath,
                                isDirectory = true,
                                itemCount = itemCount,
                                type = ExplorerItemType.FOLDER,
                                song = null
                            )
                        }

                        // Header for pinned folders
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.Pushpin,
                                    contentDescription = "Pinned",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = context.getString(R.string.library_pinned_folders),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        // Pinned folder items
                        itemsIndexed(
                            items = pinnedFolderItems,
                            key = { _, item -> "pinned_${item.path}" }
                        ) { index, item ->
                            AnimateIn {
                                ExplorerItemCard(
                                    item = item,
                                    onItemClick = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                        currentPath = item.path
                                    },
                                    onSongClick = onSongClick,
                                    onAddToPlaylist = onAddToPlaylist,
                                    onAddToQueue = onAddToQueue,
                                    onShowSongInfo = onShowSongInfo,
                                    haptics = haptics,
                                    isPinned = true,
                                    onPinToggle = {
                                        appSettings.removeFolderFromPinned(item.path)
                                    },
                                    onPlayFolder = { folderItem ->
                                        // Get songs in this folder and create playlist
                                        val folderSongs = songs.filter { song ->
                                            try {
                                                val songPath = getFilePathFromUri(song.uri, context) ?: ""
                                                val normalizedSongPath = songPath.replace("//", "/")
                                                val normalizedFolderPath = folderItem.path.replace("//", "/").trimEnd('/')
                                                normalizedSongPath.startsWith("$normalizedFolderPath/")
                                            } catch (e: Exception) {
                                                false
                                            }
                                        }
                                        if (folderSongs.isNotEmpty()) {
                                            folderSongsForPlaylist = folderSongs
                                            playlistNamePrefix = folderItem.name
                                            showCreatePlaylistDialog = true
                                        }
                                    },
                                    onAddFolderToQueue = { folderItem ->
                                        // Get songs in this folder and add to queue
                                        val folderSongs = songs.filter { song ->
                                            try {
                                                val songPath = getFilePathFromUri(song.uri, context) ?: ""
                                                val normalizedSongPath = songPath.replace("//", "/")
                                                val normalizedFolderPath = folderItem.path.replace("//", "/").trimEnd('/')
                                                normalizedSongPath.startsWith("$normalizedFolderPath/")
                                            } catch (e: Exception) {
                                                false
                                            }
                                        }
                                        if (folderSongs.isNotEmpty()) {
                                            folderSongs.forEach { song -> onAddToQueue(song) }
                                        }
                                    },
                                    currentSong = currentSong,
                                    isPlaying = isPlaying,
                                    enableRatingSystem = enableRatingSystem,
                                    itemShape = groupedLibraryItemShape(index, pinnedFolderItems.size)
                                )
                            }
                        }
                    }
                }

                // Explorer Items - only show when not loading and not at root level
                if (!isLoadingDirectory && currentPath != null) {
                    itemsIndexed(
                        items = currentItems,
                        key = { _, item -> 
                            // Use song ID if it's a file with a song, otherwise use path + name + type
                            if (item.type == ExplorerItemType.FILE && item.song != null) {
                                "song_${item.song.id}"
                            } else {
                                "${item.type}_${item.path}_${item.name}"
                            }
                        }
                    ) { index, item ->
                        AnimateIn {
                            ExplorerItemCard(
                                item = item,
                                onItemClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)

                                    when (item.type) {
                                        ExplorerItemType.STORAGE, ExplorerItemType.FOLDER -> {
                                            // Navigate into directory
                                            currentPath = item.path
                                        }
                                        ExplorerItemType.FILE -> {
                                            // Play from current folder context to keep queue + start index deterministic.
                                            item.song?.let { song ->
                                                val songIndex = currentFolderSongs.indexOfFirst { it.id == song.id }
                                                if (songIndex >= 0) {
                                                    onPlayQueueFromIndex(currentFolderSongs, songIndex)
                                                } else {
                                                    onSongClick(song)
                                                }
                                            }
                                        }
                                    }
                                },
                                onSongClick = onSongClick,
                                onAddToPlaylist = onAddToPlaylist,
                                onAddToQueue = onAddToQueue,
                                onShowSongInfo = onShowSongInfo,
                                haptics = haptics,
                                isPinned = pinnedFolders.contains(item.path),
                                onPinToggle = if (item.type == ExplorerItemType.FOLDER) {
                                    {
                                        if (pinnedFolders.contains(item.path)) {
                                            appSettings.removeFolderFromPinned(item.path)
                                        } else {
                                            appSettings.addFolderToPinned(item.path)
                                        }
                                    }
                                } else null,
                                onPlayFolder = if (item.type == ExplorerItemType.FOLDER) {
                                    { folderItem ->
                                        // Get all songs in this folder and create playlist
                                        val folderSongs = songs.filter { song ->
                                            try {
                                                val songPath = getFilePathFromUri(song.uri, context) ?: ""
                                                val normalizedSongPath = songPath.replace("//", "/")
                                                val normalizedFolderPath = folderItem.path.replace("//", "/").trimEnd('/')
                                                normalizedSongPath.startsWith("$normalizedFolderPath/")
                                            } catch (e: Exception) {
                                                false
                                            }
                                        }
                                        if (folderSongs.isNotEmpty()) {
                                            folderSongsForPlaylist = folderSongs
                                            playlistNamePrefix = folderItem.name
                                            showCreatePlaylistDialog = true
                                        }
                                    }
                                } else null,
                                onAddFolderToQueue = if (item.type == ExplorerItemType.FOLDER) {
                                    { folderItem ->
                                        // Get all songs in this folder and add to queue
                                        val folderSongs = songs.filter { song ->
                                            try {
                                                val songPath = getFilePathFromUri(song.uri, context) ?: ""
                                                val normalizedSongPath = songPath.replace("//", "/")
                                                val normalizedFolderPath = folderItem.path.replace("//", "/").trimEnd('/')
                                                normalizedSongPath.startsWith("$normalizedFolderPath/")
                                            } catch (e: Exception) {
                                                false
                                            }
                                        }
                                        if (folderSongs.isNotEmpty()) {
                                            folderSongs.forEach { song -> onAddToQueue(song) }
                                        }
                                    }
                                } else null,
                                currentSong = currentSong,
                                isPlaying = isPlaying,
                                enableRatingSystem = enableRatingSystem,
                                itemShape = groupedLibraryItemShape(index, currentItems.size)
                            )
                        }
                    }
                }

                // Empty state - show when no items and not loading, with single retry option
                if (!isInitialLoading && currentItems.isEmpty() && !isLoadingDirectory) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                // Empty state illustration with multiple elements
                                Box(
                                    modifier = Modifier.size(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Background circle
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        shadowElevation = 0.dp
                                    ) {}

                                    // Main empty folder icon
                                    Icon(
                                        imageVector = Icons.Default.FolderOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(48.dp)
                                    )

                                    // Subtle secondary icons
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                            .offset(x = 16.dp, y = (-8).dp)
                                    )

                                    Icon(
                                        imageVector = Icons.Default.LibraryMusic,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.BottomStart)
                                            .offset(x = (-12).dp, y = 12.dp)
                                    )
                                }

                                // Main message
                                Text(
                                    text = if (currentPath == null) "No storage found" else "Empty folder",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                // Secondary description
                                Text(
                                    text = if (currentPath == null)
                                        "Connect storage devices or check permissions to explore your music files"
                                    else
                                        "This folder doesn't contain any audio files",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                                )

                                // Action buttons
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    // Refresh button
                                    // FilledTonalButton(
                                    //     onClick = {
                                    //         HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    //         // Clear cache and reload directory
                                    //         directoryCache.clear()
                                    //         // Trigger reload by setting path to itself
                                    //         val temp = currentPath
                                    //         currentPath = null
                                    //         currentPath = temp
                                    //     },
                                    //     shape = RoundedCornerShape(12.dp)
                                    // ) {
                                    //     Icon(
                                    //         imageVector = Icons.Default.Refresh,
                                    //         contentDescription = null,
                                    //         modifier = Modifier.size(18.dp)
                                    //     )
                                    //     Spacer(modifier = Modifier.width(8.dp))
                                    //     Text(
                                    //         text = "Refresh",
                                    //         style = MaterialTheme.typography.labelLarge,
                                    //         fontWeight = FontWeight.Medium
                                    //     )
                                    // }

                                    // Permission info
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        shape = RoundedCornerShape(12.dp),
                                        tonalElevation = 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                    imageVector = Icons.Default.Info,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = context.getString(R.string.library_check_permissions),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    // Third row: Go back button if we're not at root
                                    if (currentPath != null) {
                                        OutlinedButton(
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                                currentPath = getParentPath(currentPath!!)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = context.getString(R.string.library_go_back),
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        
        // Bottom Floating Button Group - only show when there are songs in current folder with animations
        AnimatedVisibility(
            visible = currentFolderSongs.isNotEmpty() && currentPath != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeOut(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            // Floating button group removed - buttons now in section header
        }
    }
    
    // Create playlist dialog for folder
    if (showCreatePlaylistDialog) {
        val scope = rememberCoroutineScope()
        var playlistName by remember { mutableStateOf(playlistNamePrefix) }
        var isCreating by remember { mutableStateOf(false) }
        var isError by remember { mutableStateOf(false) }
        
        AlertDialog(
            onDismissRequest = {
                if (!isCreating) {
                    showCreatePlaylistDialog = false
                    folderSongsForPlaylist = emptyList()
                    playlistNamePrefix = ""
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = null,
                    
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    "Create Playlist from Folder",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (isCreating) {
                        // Show loading state
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            DataProcessingLoader(
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = context.getString(R.string.library_creating_playlist, folderSongsForPlaylist.size),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            "Add ${folderSongsForPlaylist.size} ${if (folderSongsForPlaylist.size == 1) "song" else "songs"} to a new playlist",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = playlistName,
                            onValueChange = { 
                                playlistName = it
                                isError = it.isBlank()
                            },
                            label = { Text("Playlist name") },
                            isError = isError,
                            supportingText = {
                                if (isError) {
                                    Text(
                                        text = context.getString(R.string.library_playlist_name_empty),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (!isCreating) {
                    Button(
                        onClick = {
                            if (playlistName.isBlank()) {
                                isError = true
                            } else {
                                isCreating = true
                                // Create the playlist using coroutine scope
                                scope.launch {
                                    try {
                                        // Create the playlist
                                        onCreatePlaylist(playlistName)
                                        
                                        // Wait for playlist to be created and available in the list
                                        var attempts = 0
                                        var newPlaylist: chromahub.rhythm.app.shared.data.model.Playlist? = null
                                        while (attempts < 20 && newPlaylist == null) {
                                            kotlinx.coroutines.delay(100)
                                            newPlaylist = playlists.firstOrNull { it.name == playlistName }
                                            attempts++
                                        }
                                        
                                        if (newPlaylist != null) {
                                            // Add all songs to the playlist with proper error handling
                                            folderSongsForPlaylist.forEach { song ->
                                                musicViewModel.addSongToPlaylist(song, newPlaylist.id) { _ -> }
                                                kotlinx.coroutines.delay(10) // Small delay between adds to avoid race conditions
                                            }
                                            Log.d("LibraryScreen", "Successfully added ${folderSongsForPlaylist.size} songs to playlist: $playlistName")
                                        } else {
                                            Log.e("LibraryScreen", "Failed to find newly created playlist: $playlistName")
                                        }
                                        
                                        // Close dialog
                                        showCreatePlaylistDialog = false
                                        folderSongsForPlaylist = emptyList()
                                        playlistNamePrefix = ""
                                        isCreating = false
                                    } catch (e: Exception) {
                                        Log.e("LibraryScreen", "Error creating playlist", e)
                                        isCreating = false
                                    }
                                }
                            }
                        },
                        enabled = playlistName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create")
                    }
                }
            },
            dismissButton = {
                if (!isCreating) {
                    OutlinedButton(
                        onClick = {
                            showCreatePlaylistDialog = false
                            folderSongsForPlaylist = emptyList()
                            playlistNamePrefix = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancel")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// Helper function to get device storage roots
fun getStorageRoots(context: android.content.Context): List<ExplorerItem> {
    val items = mutableListOf<ExplorerItem>()

    try {
        // For Android 9/10, use different approach due to storage access restrictions
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            android.util.Log.d("LibraryScreen", "Using Android 9/10 specific storage access method")
            
            // Method 1: Try using getExternalFilesDir to get storage roots
            val externalFilesDirs = ContextCompat.getExternalFilesDirs(context, null)
            externalFilesDirs.forEachIndexed { index, dir ->
                if (dir != null) {
                    try {
                        // Navigate up to find the storage root
                        var storageRoot = dir
                        var depth = 0
                        while (storageRoot.parent != null && depth < 10) {
                            val parent = storageRoot.parentFile
                            if (parent == null || parent.name == "storage" || parent.absolutePath == "/storage") {
                                break
                            }
                            storageRoot = parent
                            depth++
                        }
                        
                        // Verify we can actually read this storage
                        val canAccess = try {
                            storageRoot.exists() && (storageRoot.canRead() || storageRoot.list() != null)
                        } catch (e: SecurityException) {
                            android.util.Log.w("LibraryScreen", "Cannot access storage at ${storageRoot.absolutePath}", e)
                            false
                        }
                        
                        if (canAccess && !items.any { it.path == storageRoot.absolutePath }) {
                            val storageName = if (index == 0) "Internal Storage" else "SD Card ${if (index > 1) index else ""}".trim()
                            items.add(ExplorerItem(
                                name = storageName,
                                path = storageRoot.absolutePath,
                                isDirectory = true,
                                itemCount = 0,
                                type = ExplorerItemType.STORAGE,
                                song = null
                            ))
                            android.util.Log.d("LibraryScreen", "Added storage root: $storageName at ${storageRoot.absolutePath}")
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("LibraryScreen", "Error processing external dir: ${dir.absolutePath}", e)
                    }
                }
            }
            
            // Method 2: Try to use Environment.getExternalStorageDirectory() as fallback for primary storage
            // This might still work on Android 9
            if (items.isEmpty() || Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                try {
                    @Suppress("DEPRECATION")
                    val primaryExternal = Environment.getExternalStorageDirectory()
                    if (primaryExternal.exists() && !items.any { it.path == primaryExternal.absolutePath }) {
                        items.add(0, ExplorerItem(
                            name = "Internal Storage",
                            path = primaryExternal.absolutePath,
                            isDirectory = true,
                            itemCount = 0,
                            type = ExplorerItemType.STORAGE,
                            song = null
                        ))
                        android.util.Log.d("LibraryScreen", "Added primary external storage via Environment API")
                    }
                } catch (e: Exception) {
                    android.util.Log.w("LibraryScreen", "Cannot access primary external storage via Environment API", e)
                }
            }
        } else {
            // Android 11+ or Android 8 and below - use standard method
            @Suppress("DEPRECATION")
            val internalStorage = Environment.getExternalStorageDirectory()
            if (internalStorage.exists()) {
                items.add(ExplorerItem(
                    name = "Internal Storage",
                    path = internalStorage.absolutePath,
                    isDirectory = true,
                    itemCount = 0,
                    type = ExplorerItemType.STORAGE,
                    song = null
                ))
            }

            // Get external storage directories (SD cards, etc.)
            val externalDirs = ContextCompat.getExternalFilesDirs(context, null)
            
            externalDirs.forEachIndexed { index, dir ->
                if (dir != null && index > 0) {
                    // Navigate up to get the actual SD card root
                    var sdCardRoot = dir
                    var depth = 0
                    while (sdCardRoot.parent != null && depth < 10) {
                        sdCardRoot = sdCardRoot.parentFile ?: break
                        depth++
                        if (sdCardRoot.parent == "/storage" || sdCardRoot.parentFile?.name == "storage") {
                            break
                        }
                    }
                    
                    if (sdCardRoot.exists() && sdCardRoot.canRead()) {
                        val storageName = "SD Card ${if (index > 1) index else ""}"
                        items.add(ExplorerItem(
                            name = storageName.trim(),
                            path = sdCardRoot.absolutePath,
                            isDirectory = true,
                            itemCount = 0,
                            type = ExplorerItemType.STORAGE,
                            song = null
                        ))
                    }
                }
            }
            
            // Alternative method: Check /storage directory directly
            val storageDir = File("/storage")
            if (storageDir.exists() && storageDir.isDirectory) {
                storageDir.listFiles()?.forEach { file ->
                    if (file.isDirectory && 
                        file.name != "emulated" && 
                        file.name != "self" && 
                        !file.name.startsWith(".") &&
                        file.canRead() &&
                        !items.any { it.path == file.absolutePath }) {
                        
                        items.add(ExplorerItem(
                            name = "Removable Storage (${file.name})",
                            path = file.absolutePath,
                            isDirectory = true,
                            itemCount = 0,
                            type = ExplorerItemType.STORAGE,
                            song = null
                        ))
                    }
                }
            }
        }
        
        android.util.Log.d("LibraryScreen", "Found ${items.size} storage roots")
    } catch (e: Exception) {
        android.util.Log.e("LibraryScreen", "Error getting storage roots", e)
    }

    return items
}

// Data classes for explorer functionality
data class ExplorerItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val itemCount: Int,
    val type: ExplorerItemType,
    val song: Song? = null
)

enum class ExplorerItemType {
    STORAGE, FOLDER, FILE
}

// Helper functions
fun getParentDirectory(uriString: String): String {
    return try {
        val uri = android.net.Uri.parse(uriString)
        val path = uri.path ?: ""
        val lastSlashIndex = path.lastIndexOf('/')
        if (lastSlashIndex > 0) {
            path.substring(0, lastSlashIndex)
        } else {
            path
        }
    } catch (e: Exception) {
        ""
    }
}

fun getParentPath(path: String): String? {
    val lastSlashIndex = path.lastIndexOf('/')
    return if (lastSlashIndex > 0) {
        path.substring(0, lastSlashIndex)
    } else {
        null // At root
    }
}

// Helper function to get root directories from songs
fun getRootDirectories(songs: List<Song>): List<ExplorerItem> {
    val directories = mutableListOf<String>()

    songs.forEach { song ->
        try {
            val uri = android.net.Uri.parse(song.uri.toString())
            val path = uri.path ?: ""
            val dirPath = path.substringBeforeLast('/', "")

            if (dirPath.isNotEmpty()) {
                val normalizedDir = dirPath.replace("//", "/")
                if (!directories.contains(normalizedDir)) {
                    directories.add(normalizedDir)
                }
            }
        } catch (e: Exception) {
            // Skip this song if parsing fails
        }
    }

    return directories.map { dirPath ->
        val itemCount = songs.count { song ->
            try {
                val songPath = android.net.Uri.parse(song.uri.toString()).path ?: ""
                val songDir = songPath.substringBeforeLast('/', "")
                songDir == dirPath
            } catch (e: Exception) {
                false
            }
        }

        val dirName = dirPath.substringAfterLast('/').takeIf { it.isNotEmpty() } ?: dirPath

        ExplorerItem(
            name = dirName,
            path = dirPath,
            isDirectory = true,
            itemCount = itemCount,
            type = ExplorerItemType.FOLDER,
            song = null
        )
    }
}

// Helper function to count audio files in a directory based on songs
fun getAudioFileCountSongsInDirectory(
    songs: List<Song>,
    directoryPath: String,
    audioExtensions: Set<String>
): Int {
    return songs.count { song ->
        try {
            val songPath = android.net.Uri.parse(song.uri.toString()).path ?: ""
            val normalizedSongPath = songPath.replace("//", "/")
            val normalizedDirPath = directoryPath.replace("//", "/")

            // Check if the song path starts with the directory path
            normalizedSongPath.startsWith(normalizedDirPath) && normalizedSongPath != normalizedDirPath
        } catch (e: Exception) {
            false
        }
    }
}

// Fast MediaStore-only implementation - no filesystem operations
fun getDirectoryContentsOptimized(directoryPath: String, songPathMap: Map<String, Song>, context: android.content.Context): List<ExplorerItem> {
    val startTime = System.currentTimeMillis()
    android.util.Log.d("LibraryScreen", "Loading directory: $directoryPath (Android ${Build.VERSION.SDK_INT})")
    
    val items = mutableListOf<ExplorerItem>()
    val normalizedDirPath = directoryPath.replace("//", "/").trimEnd('/')
    val audioExtensions = setOf("mp3", "flac", "m4a", "aac", "ogg", "wav", "wma", "aiff", "opus")
    
    // Pre-compute song counts for subdirectories from songPathMap for fast lookup
    // This includes ALL nested songs (recursive count)
    val subdirectorySongCounts = mutableMapOf<String, Int>()
    
    // Also build a set of all directories that contain songs (directly or nested)
    val directoriesWithSongs = mutableSetOf<String>()
    
    // Songs directly in the current directory
    val songsInCurrentDir = mutableListOf<Pair<String, Song>>()
    
    songPathMap.forEach { (normalizedSongPath, song) ->
        try {
            val parentDir = File(normalizedSongPath).parent?.replace("//", "/")?.trimEnd('/') ?: return@forEach
            
            // Check if this song is directly in the current directory
            if (parentDir == normalizedDirPath) {
                songsInCurrentDir.add(normalizedSongPath to song)
            }
            // Check if this song is in a subdirectory of current path
            else if (parentDir.startsWith("$normalizedDirPath/")) {
                val relativePath = parentDir.removePrefix("$normalizedDirPath/")
                val firstSlash = relativePath.indexOf('/')
                val immediateChild = if (firstSlash > 0) {
                    relativePath.substring(0, firstSlash)
                } else {
                    relativePath
                }
                val childPath = "$normalizedDirPath/$immediateChild"
                subdirectorySongCounts[childPath] = (subdirectorySongCounts[childPath] ?: 0) + 1
                directoriesWithSongs.add(childPath)
            }
        } catch (e: Exception) {
            // Skip problematic paths
        }
    }
    
    android.util.Log.d("LibraryScreen", "Found ${songsInCurrentDir.size} songs in current dir, ${subdirectorySongCounts.size} subdirs with songs")
    
    try {
        val directory = File(directoryPath)
        if (!directory.exists()) {
            android.util.Log.d("LibraryScreen", "Directory does not exist: $directoryPath")
            return items
        }
        
        // Try to list files from file system
        val files = try {
            directory.listFiles()
        } catch (e: SecurityException) {
            android.util.Log.w("LibraryScreen", "Cannot list files for $directoryPath due to permissions (Android ${Build.VERSION.SDK_INT}), using MediaStore fallback", e)
            null
        } catch (e: Exception) {
            android.util.Log.e("LibraryScreen", "Error listing files for $directoryPath (Android ${Build.VERSION.SDK_INT})", e)
            null
        }
        
        if (files != null) {
            // Normal file system listing succeeded
            android.util.Log.d("LibraryScreen", "File system listing succeeded, found ${files.size} items")
            files.forEach { file ->
                try {
                    if (file.isDirectory) {
                        // Skip hidden directories
                        if (!file.name.startsWith(".")) {
                            val folderPath = file.absolutePath.replace("//", "/").trimEnd('/')
                            // Get pre-computed song count for this subdirectory (includes nested)
                            var audioCount = subdirectorySongCounts[folderPath] ?: 0
                            
                            // If not found with exact path, try canonical path
                            if (audioCount == 0) {
                                try {
                                    val canonicalPath = file.canonicalPath.replace("//", "/").trimEnd('/')
                                    audioCount = subdirectorySongCounts[canonicalPath] ?: 0
                                } catch (e: Exception) {
                                    // Ignore canonical path errors
                                }
                            }
                            
                            // If still not found, check if any song paths start with this folder
                            // This handles cases where path normalization differs
                            if (audioCount == 0) {
                                val folderPrefix = "$folderPath/"
                                audioCount = songPathMap.keys.count { it.startsWith(folderPrefix) }
                            }
                            
                            // Only show folders that have songs (directly or nested)
                            if (audioCount > 0) {
                                items.add(ExplorerItem(
                                    name = file.name,
                                    path = file.absolutePath,
                                    isDirectory = true,
                                    itemCount = audioCount,
                                    type = ExplorerItemType.FOLDER,
                                    song = null
                                ))
                            }
                        }
                    } else if (file.isFile) {
                        val extension = file.extension.lowercase()
                        if (extension in audioExtensions) {
                            val normalizedPath = file.absolutePath.replace("//", "/")
                            // Look up song in pre-computed map - try multiple path formats
                            var song = songPathMap[normalizedPath]
                            
                            // Also try without trailing slash normalization
                            if (song == null) {
                                song = songPathMap[file.absolutePath]
                            }
                            
                            // Try case-insensitive lookup as fallback
                            if (song == null) {
                                val lowercasePath = normalizedPath.lowercase()
                                song = songPathMap.entries.find { 
                                    it.key.lowercase() == lowercasePath 
                                }?.value
                            }
                            
                            if (song != null) {
                                items.add(ExplorerItem(
                                    name = song.title, // Use actual metadata title
                                    path = file.absolutePath,
                                    isDirectory = false,
                                    itemCount = 1,
                                    type = ExplorerItemType.FILE,
                                    song = song
                                ))
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.d("LibraryScreen", "Skipping file ${file.name}: ${e.message}")
                }
            }
            
            // If file system scan found no songs but we know songs exist in this directory,
            // use the pre-computed songs list (handles permission edge cases)
            if (items.none { !it.isDirectory } && songsInCurrentDir.isNotEmpty()) {
                android.util.Log.d("LibraryScreen", "File system scan missed songs, using pre-computed list")
                songsInCurrentDir.forEach { (path, song) ->
                    items.add(ExplorerItem(
                        name = song.title,
                        path = path,
                        isDirectory = false,
                        itemCount = 1,
                        type = ExplorerItemType.FILE,
                        song = song
                    ))
                }
            }
            
            // Also add any folders with songs that might have been missed by file system scan
            // (handles symlinks, mount points, and path normalization differences)
            val existingFolderPaths = items.filter { it.isDirectory }.map { it.path.replace("//", "/").trimEnd('/') }.toSet()
            directoriesWithSongs.forEach { subdirPath ->
                val normalizedSubdirPath = subdirPath.replace("//", "/").trimEnd('/')
                if (normalizedSubdirPath !in existingFolderPaths) {
                    val subdirName = File(subdirPath).name
                    val songCount = subdirectorySongCounts[subdirPath] ?: 0
                    if (songCount > 0) {
                        android.util.Log.d("LibraryScreen", "Adding missed folder: $subdirPath with $songCount songs")
                        items.add(ExplorerItem(
                            name = subdirName,
                            path = subdirPath,
                            isDirectory = true,
                            itemCount = songCount,
                            type = ExplorerItemType.FOLDER,
                            song = null
                        ))
                    }
                }
            }
        } else {
            // MediaStore fallback for restricted directories (e.g., SD card root on Android 9/10)
            android.util.Log.w("LibraryScreen", "Using MediaStore-only fallback for $directoryPath (Android ${Build.VERSION.SDK_INT})")
            
            // Add songs directly in this directory
            songsInCurrentDir.forEach { (path, song) ->
                items.add(ExplorerItem(
                    name = song.title,
                    path = path,
                    isDirectory = false,
                    itemCount = 1,
                    type = ExplorerItemType.FILE,
                    song = song
                ))
            }
            
            // Add subdirectories that have songs (from pre-computed data)
            directoriesWithSongs.sorted().forEach { subdirPath ->
                val subdirName = File(subdirPath).name
                val songCount = subdirectorySongCounts[subdirPath] ?: 0
                
                // Only show folders with songs
                if (songCount > 0) {
                    items.add(ExplorerItem(
                        name = subdirName,
                        path = subdirPath,
                        isDirectory = true,
                        itemCount = songCount,
                        type = ExplorerItemType.FOLDER,
                        song = null
                    ))
                }
            }
            
            android.util.Log.d("LibraryScreen", "MediaStore fallback found ${items.filter { !it.isDirectory }.size} songs and ${items.filter { it.isDirectory }.size} folders")
        }
    } catch (e: Exception) {
        android.util.Log.e("LibraryScreen", "Error reading directory: $directoryPath", e)
    }
    
    val elapsed = System.currentTimeMillis() - startTime
    android.util.Log.d("LibraryScreen", "Loaded ${items.size} items in ${elapsed}ms")
    
    return items
}

// Legacy implementation kept for reference - DO NOT USE
fun getDirectoryContentsOptimized_OLD(directoryPath: String, audioExtensions: Set<String>, songs: List<Song>, context: android.content.Context): List<ExplorerItem> {
    val items = mutableListOf<ExplorerItem>()

    try {
        val directory = File(directoryPath)
        if (!directory.exists()) {
            return items
        }

        // Build a map of file paths to songs for quick lookup - use concurrent collection for better performance
        val songsByPath = java.util.concurrent.ConcurrentHashMap<String, Song>()
        
        // Filter songs to only those that could be in this directory or subdirectories first
        val dirPath = directoryPath.replace("//", "/").trimEnd('/')
        
        // Quick pre-filter: only check songs that start with the directory path
        val relevantSongs = songs.asSequence()
            .filter { song ->
                try {
                    // Quick check using URI path first (faster than full resolution)
                    val uriPath = song.uri.path
                    uriPath != null && uriPath.contains(dirPath, ignoreCase = true)
                } catch (e: Exception) {
                    false
                }
            }
            .toList()
        
        // Only build map for relevant songs to save memory and time
        relevantSongs.forEach { song ->
            try {
                val filePath = getFilePathFromUri(song.uri, context)
                if (filePath != null) {
                    val normalizedPath = filePath.replace("//", "/")
                    songsByPath[normalizedPath] = song
                }
            } catch (e: Exception) {
                // Skip if path cannot be resolved
            }
        }

        // Try to list files - may fail on SD card root due to permissions
        val files = try {
            directory.listFiles()
        } catch (e: SecurityException) {
            android.util.Log.d("LibraryScreen", "Cannot list files directly for $directoryPath, using MediaStore fallback")
            // If we can't list files directly (e.g., SD card root), fall back to MediaStore approach
            val songsInDir = relevantSongs
            
            android.util.Log.d("LibraryScreen", "Found ${songsInDir.size} songs in $dirPath using MediaStore")
            
            // Build directory structure from MediaStore songs
            val subdirs = mutableSetOf<String>()
            songsInDir.forEach { song ->
                try {
                    val songPath = getFilePathFromUri(song.uri, context) ?: return@forEach
                    val normalizedSongPath = songPath.replace("//", "/")
                    val normalizedDirPath = dirPath.trimEnd('/')
                    
                    // Get the path relative to current directory
                    val relativePath = if (normalizedSongPath.startsWith("$normalizedDirPath/")) {
                        normalizedSongPath.removePrefix("$normalizedDirPath/")
                    } else {
                        normalizedSongPath.removePrefix(normalizedDirPath).removePrefix("/")
                    }
                    
                    val firstSlash = relativePath.indexOf('/')
                    if (firstSlash > 0) {
                        // This song is in a subdirectory
                        subdirs.add(relativePath.substring(0, firstSlash))
                    } else if (firstSlash < 0 && relativePath.isNotEmpty()) {
                        // This song is directly in this directory
                        val extension = File(songPath).extension.lowercase()
                        if (extension in audioExtensions) {
                            items.add(ExplorerItem(
                                name = song.title,
                                path = songPath,
                                isDirectory = false,
                                itemCount = 1,
                                type = ExplorerItemType.FILE,
                                song = song
                            ))
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("LibraryScreen", "Error processing song ${song.title}", e)
                }
            }
            
            // Add subdirectories
            subdirs.forEach { subdir ->
                val subdirPath = "$dirPath/$subdir"
                val audioCount = songsInDir.count { song ->
                    try {
                        val songPath = getFilePathFromUri(song.uri, context)
                        songPath != null && songPath.replace("//", "/").startsWith("$subdirPath/")
                    } catch (e: Exception) {
                        false
                    }
                }
                if (audioCount > 0) {
                    items.add(ExplorerItem(
                        name = subdir,
                        path = subdirPath,
                        isDirectory = true,
                        itemCount = audioCount,
                        type = ExplorerItemType.FOLDER,
                        song = null
                    ))
                }
            }
            
            android.util.Log.d("LibraryScreen", "MediaStore fallback returned ${items.size} items (${items.count { !it.isDirectory }} files, ${items.count { it.isDirectory }} folders)")
            return items
        }
        
        // Normal file listing succeeded - optimize by limiting number of files processed
        val maxFiles = 200 // Reduced limit for better performance
        val filesToProcess = files?.let { fileArray ->
            if (fileArray.size > maxFiles) {
                android.util.Log.d("LibraryScreen", "Large directory with ${fileArray.size} files, limiting to first $maxFiles for performance")
                // Take first maxFiles, sorted to show audio files first
                fileArray.sortedWith(compareBy(
                    { !it.isDirectory }, // Directories first
                    { !audioExtensions.contains(it.extension.lowercase()) } // Audio files first
                )).take(maxFiles)
            } else {
                fileArray.toList()
            }
        } ?: emptyList()
        
        filesToProcess.forEach { file ->
            try {
                if (file.isDirectory) {
                    // Skip hidden directories for performance
                    if (!file.name.startsWith(".")) {
                        // Use shallow count only (much faster) for better performance
                        // Count only files that are in MediaStore
                        val audioCount = countMediaStoreAudioFilesInDirectoryShallow(file, songsByPath)
                        // Always add folders - let the filter decide if they should be shown
                        // This prevents hiding folders that have nested audio
                        items.add(ExplorerItem(
                            name = file.name,
                            path = file.absolutePath,
                            isDirectory = true,
                            itemCount = audioCount,
                            type = ExplorerItemType.FOLDER,
                            song = null
                        ))
                    }
                } else if (file.isFile) {
                    val extension = file.extension.lowercase()
                    if (extension in audioExtensions) {
                        // Try to find song by absolute path first
                        val normalizedPath = file.absolutePath.replace("//", "/")
                        var song = songsByPath[normalizedPath]
                        
                        // If not found with normalized path, try original
                        if (song == null) {
                            song = songsByPath[file.absolutePath]
                        }
                        
                        if (song != null) {
                            items.add(ExplorerItem(
                                name = song.title, // Use actual metadata title instead of filename
                                path = file.absolutePath,
                                isDirectory = false,
                                itemCount = 1,
                                type = ExplorerItemType.FILE,
                                song = song
                            ))
                        }
                    }
                }
            } catch (e: Exception) {
                // Skip problematic files
                android.util.Log.d("LibraryScreen", "Skipping file ${file.name}: ${e.message}")
            }
        }
    } catch (e: Exception) {
        // Handle permission or access errors gracefully
        android.util.Log.e("LibraryScreen", "Error reading directory: $directoryPath", e)
    }

    return items
}

// Helper function to build a map of file paths to songs from MediaStore
fun buildSongPathMap(songs: List<Song>, context: android.content.Context): Map<String, Song> {
    val pathMap = mutableMapOf<String, Song>()
    
    songs.forEach { song ->
        try {
            // Try to get the actual file path from the content URI
            val path = getFilePathFromUri(song.uri, context)
            if (path != null && path.isNotEmpty()) {
                pathMap[path] = song
            }
        } catch (e: Exception) {
            // Skip songs that can't be mapped to file paths
        }
    }
    
    return pathMap
}

// Helper function to get file path from MediaStore content URI
fun getFilePathFromUri(uri: android.net.Uri, context: android.content.Context): String? {
    return try {
        // MediaStore content URIs are in format: content://media/external/audio/media/{id}
        // We need to query MediaStore to get the actual file path
        val projection = arrayOf(android.provider.MediaStore.Audio.Media.DATA)
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
                it.getString(columnIndex)
            } else null
        }
    } catch (e: Exception) {
        null
    }
}

// Helper function to count audio files in a directory recursively
fun countAudioFilesInDirectory(directory: File, audioExtensions: Set<String>): Int {
    var count = 0

    try {
        val files = directory.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                count += countAudioFilesInDirectory(file, audioExtensions)
            } else if (file.isFile) {
                val extension = file.extension.lowercase()
                if (extension in audioExtensions) {
                    count++
                }
            }
        }
    } catch (e: Exception) {
        // Handle permission errors
    }

    return count
}

// Helper function to count audio files in a directory (shallow - first level only)
fun countAudioFilesInDirectoryShallow(directory: File, audioExtensions: Set<String>): Int {
    var count = 0

    try {
        val files = directory.listFiles()
        files?.forEach { file ->
            if (file.isFile) {
                val extension = file.extension.lowercase()
                if (extension in audioExtensions) {
                    count++
                }
            }
            // Don't recurse into subdirectories for performance
        }
    } catch (e: Exception) {
        // Handle permission errors
    }

    return count
}

// Helper function to count MediaStore audio files in a directory (shallow - first level only)
fun countMediaStoreAudioFilesInDirectoryShallow(directory: File, songsByPath: Map<String, Song>): Int {
    var count = 0

    try {
        val files = directory.listFiles()
        files?.forEach { file ->
            if (file.isFile) {
                // Only count files that exist in MediaStore
                if (songsByPath.containsKey(file.absolutePath)) {
                    count++
                }
            }
            // Don't recurse into subdirectories for performance
        }
    } catch (e: Exception) {
        // Handle permission errors
    }

    return count
}

// Helper function to check if a folder has audio content (recursively checks nested folders)
fun hasAudioContentRecursive(path: String, songs: List<Song>, context: android.content.Context, maxDepth: Int = 3): Boolean {
    if (maxDepth <= 0) return false
    
    return try {
        val directory = File(path)
        if (!directory.exists() || !directory.isDirectory) {
            return false
        }
        
        // Check if any songs in the songs list are inside this directory or its subdirectories
        val normalizedDirPath = path.replace("//", "/").trimEnd('/')
        songs.any { song ->
            try {
                val songPath = getFilePathFromUri(song.uri, context) ?: return@any false
                val normalizedSongPath = songPath.replace("//", "/")
                normalizedSongPath.startsWith("$normalizedDirPath/")
            } catch (e: Exception) {
                false
            }
        }
    } catch (e: Exception) {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderItem(
    folderName: String,
    songCount: Int,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder icon with enhanced styling
            Surface(
                modifier = Modifier.size(68.dp),
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Folder",
                        
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Folder info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = RhythmIcons.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "$songCount ${if (songCount == 1) "track" else "tracks"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Forward arrow
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Open folder",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistFabMenuContent(
    onCreatePlaylist: () -> Unit,
    onImportPlaylist: (() -> Unit)?,
    onExportPlaylists: (() -> Unit)?,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback // Added haptics parameter
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Export playlists item
        if (onExportPlaylists != null) {
            FloatingActionButton(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    scope.launch {
                        onExportPlaylists()
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = "Export playlists",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Import playlist item
        if (onImportPlaylist != null) {
            FloatingActionButton(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    scope.launch {
                        onImportPlaylist()
                    }
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = RhythmIcons.Actions.Download,
                    contentDescription = "Import playlist",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Create playlist item (always shown)
        FloatingActionButton(
            onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                scope.launch {
                    onCreatePlaylist()
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = RhythmIcons.Add,
                contentDescription = "Create playlist",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun PlaylistFabMenu(
    expanded: Boolean,
    onCreatePlaylist: () -> Unit,
    onImportPlaylist: (() -> Unit)?,
    onExportPlaylists: (() -> Unit)?,
    modifier: Modifier = Modifier,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback // Added haptics parameter
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val miniPlayerPadding = LocalMiniPlayerPadding.current

    // Animate FAB expansion
    val fabScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabScale"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        val miniPlayerPadding = LocalMiniPlayerPadding.current

        // Calculate consistent spacing from main FAB (bottom = 50.dp, FAB size = 56.dp)
        // Base position above main FAB: 50 + 56 + 16 = 122.dp
        val fabBaseBottom = 50.dp + 56.dp + 16.dp
        val menuItemHeight = 56.dp // Approximate FabMenuItem height
        val spacing = 16.dp

        // FAB Menu Items with corrected positioning
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 300)) +
                   slideInHorizontally(
                       animationSpec = tween(300, delayMillis = 300),
                       initialOffsetX = { it / 2 }
                   ),
            exit = fadeOut(animationSpec = tween(200, delayMillis = 200)) +
                  slideOutHorizontally(
                      animationSpec = tween(200, delayMillis = 200),
                      targetOffsetX = { it / 2 }
                  )
        ) {
            FabMenuItem(
                label = "New playlist",
                icon = RhythmIcons.Add,
                contentDescription = "Create new playlist",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    scope.launch {
                        onCreatePlaylist()
                        isExpanded = false
                    }
                },
                animationDelay = 100,
                haptics = haptics, // Pass haptics
                modifier = Modifier.padding(
                    bottom = fabBaseBottom, // Directly above main FAB
                    end = 16.dp
                )
            )
        }

        AnimatedVisibility(
            visible = isExpanded && onImportPlaylist != null,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 150)) +
                   slideInHorizontally(
                       animationSpec = tween(300, delayMillis = 150),
                       initialOffsetX = { it / 2 }
                   ),
            exit = fadeOut(animationSpec = tween(200, delayMillis = 100)) +
                  slideOutHorizontally(
                      animationSpec = tween(200, delayMillis = 100),
                      targetOffsetX = { it / 2 }
                  )
        ) {
            FabMenuItem(
                label = "Import playlist",
                icon = RhythmIcons.Actions.Download,
                contentDescription = "Import playlist",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    scope.launch {
                        onImportPlaylist?.invoke()
                        isExpanded = false
                    }
                },
                animationDelay = 50,
                haptics = haptics, // Pass haptics
                modifier = Modifier.padding(
                    bottom = fabBaseBottom + menuItemHeight + spacing, // Above "New playlist"
                    end = 16.dp
                )
            )
        }

        AnimatedVisibility(
            visible = isExpanded && onExportPlaylists != null,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 0)) +
                   slideInHorizontally(
                       animationSpec = tween(300, delayMillis = 0),
                       initialOffsetX = { it / 2 }
                   ),
            exit = fadeOut(animationSpec = tween(200, delayMillis = 0)) +
                  slideOutHorizontally(
                      animationSpec = tween(200, delayMillis = 0),
                      targetOffsetX = { it / 2 }
                  )
        ) {
            FabMenuItem(
                label = "Export playlists",
                icon = Icons.Default.FileUpload,
                contentDescription = "Export playlists",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    scope.launch {
                        onExportPlaylists?.invoke()
                        isExpanded = false
                    }
                },
                animationDelay = 0,
                haptics = haptics, // Pass haptics
                modifier = Modifier.padding(
                    bottom = fabBaseBottom + (menuItemHeight + spacing) * 2, // Above "Import playlist"
                    end = 16.dp
                )
            )
        }

        // Main FAB with simple rotation animation
        val fabRotation by animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "fabRotation"
        )

        FloatingActionButton(
            onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                isExpanded = !isExpanded
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 12.dp
            ),
            modifier = Modifier
                .padding(bottom = 10.dp) // Simple fixed spacing
                .size(56.dp)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else RhythmIcons.Add,
                contentDescription = if (isExpanded) "Close FAB menu" else "Open FAB menu",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        rotationZ = fabRotation
                    }
            )
        }
    }
}

@Composable
fun ExplorerBreadcrumb(
    path: String,
    onNavigateTo: (String) -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val rawSegments = path.split("/").filter { it.isNotEmpty() }

    // Collapse Android internal storage path (/storage/emulated/0) into a single 'Internal Storage' breadcrumb
    val displaySegments: List<Pair<String, String>> = run {
        if (rawSegments.size >= 3 && rawSegments[0].equals("storage", true)
            && rawSegments[1].equals("emulated", true) && rawSegments[2] == "0"
        ) {
            val basePath = "/storage/emulated/0"
            val rest = if (rawSegments.size > 3) rawSegments.subList(3, rawSegments.size) else emptyList()
            val segments = mutableListOf<Pair<String, String>>()
            // Add the collapsed internal storage segment
            segments.add("Internal Storage" to basePath)
            // Add remaining segments with full paths
            var current = basePath
            for (s in rest) {
                current = "$current/$s"
                segments.add(s to current)
            }
            segments
        } else {
            // Normal path -> each segment maps to its accumulated path
            val segments = mutableListOf<Pair<String, String>>()
            var current = ""
            for (s in rawSegments) {
                current = "$current/$s"
                segments.add(s to current)
            }
            segments
        }
    }

    // Auto-scroll to the active breadcrumb (last segment)
    LaunchedEffect(displaySegments) {
        if (displaySegments.isNotEmpty()) {
            val lastIndex = (displaySegments.size * 2) + 1 // Account for chevrons and segments
            scrollState.animateScrollToItem(lastIndex.coerceAtLeast(0))
        }
    }

    // Add animations to path chips
    LazyRow(
        state = scrollState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        // Home button with enhanced styling
        item {
            val homeScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "homeScale"
            )

            Surface(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onGoHome()
                },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .height(36.dp)
                    .graphicsLayer {
                        scaleX = homeScale
                        scaleY = homeScale
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = context.getString(R.string.library_home),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Path segments with enhanced styling
        displaySegments.forEachIndexed { index, pair ->
            val (segmentDisplay, segmentPath) = pair

            item {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            val currentPath = segmentPath
            val isLastSegment = index == displaySegments.lastIndex

            item {
                val chipScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "chipScale_$index"
                )

                val chipBackgroundColor by animateColorAsState(
                    targetValue = if (isLastSegment)
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
                    else
                        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f),
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "chipBackground_$index"
                )

                Surface(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        onNavigateTo(currentPath)
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = chipBackgroundColor,
                    border = if (isLastSegment) BorderStroke(
                        1.5.dp,
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                    ) else null,
                    modifier = Modifier
                        .height(36.dp)
                        .graphicsLayer {
                            scaleX = chipScale
                            scaleY = chipScale
                        }
                ) {
                    // Handle long segment names with truncation
                    val displayText = if (segmentDisplay.length > 15) {
                        segmentDisplay.take(12) + "..."
                    } else {
                        segmentDisplay
                    }

                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Optional icon for folders/directories
                        if (isLastSegment) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isLastSegment) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isLastSegment)
                                MaterialTheme.colorScheme.onTertiaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Subtle indicator for current location
                        if (isLastSegment) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Current location",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerItemCard(
    item: ExplorerItem,
    onItemClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onShowSongInfo: (Song) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    modifier: Modifier = Modifier,
    itemShape: RoundedCornerShape? = null,
    isPinned: Boolean = false,
    onPinToggle: (() -> Unit)? = null,
    onPlayFolder: ((ExplorerItem) -> Unit)? = null,
    onAddFolderToQueue: ((ExplorerItem) -> Unit)? = null,
    currentSong: Song? = null, // Add current song parameter
    isPlaying: Boolean = false, // Add playing state
    enableRatingSystem: Boolean = true
) {
    val context = LocalContext.current

    when (item.type) {
        ExplorerItemType.STORAGE -> {
            Card(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onItemClick()
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = itemShape ?: RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Storage icon
                    Surface(
                        modifier = Modifier.size(68.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 0.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (item.name) {
                                    "Internal Storage" -> Icons.Default.Storage
                                    else -> Icons.Default.SdStorage
                                },
                                contentDescription = "${item.name} icon",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    // Storage info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = context.getString(R.string.library_tap_browse),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Forward arrow
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = "Open storage",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        ExplorerItemType.FOLDER -> {
            var showDropdown by remember { mutableStateOf(false) }
            
            Card(
                onClick = {
                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                    onItemClick()
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = itemShape ?: RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Folder icon - reduced size from 68.dp to 56.dp
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Folder",
                                
                                modifier = Modifier.size(28.dp) // Reduced proportionally
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Folder info
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleSmall, // Smaller text to fit reduced height
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )

            // Pin indicator for pinned folders - smaller and more prominent
                            if (isPinned) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(14.dp) // Smaller size
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = RhythmIcons.Pushpin,
                                            contentDescription = "Pinned",
                                            tint = MaterialTheme.colorScheme.onTertiary,
                                            modifier = Modifier.size(8.dp) // Smaller icon
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = RhythmIcons.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp) // Slightly smaller
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "${item.itemCount} ${if (item.itemCount == 1) "track" else "tracks"}",
                                style = MaterialTheme.typography.bodySmall, // Smaller text
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Pin button (only show for folders in root directory)
                    if (onPinToggle != null) {
                        FilledTonalIconButton(
                            onClick = {
                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                onPinToggle()
                            },
                            modifier = Modifier.size(32.dp), // Smaller button
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = if (isPinned) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = if (isPinned) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = if (isPinned) RhythmIcons.Pushpin else RhythmIcons.PinOutline,
                                contentDescription = if (isPinned) "Unpin folder" else "Pin folder",
                                modifier = Modifier.size(16.dp) // Smaller icon
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Options menu button for folders with songs
                    if (item.itemCount > 0 && (onPlayFolder != null || onAddFolderToQueue != null)) {
                        Box {
                            FilledIconButton(
                                onClick = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    showDropdown = true
                                },
                                modifier = Modifier.size(width = 40.dp, height = 36.dp),
                                shape = RoundedCornerShape(18.dp), // Pill shape like songs tab
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = RhythmIcons.More,
                                    contentDescription = "Folder options",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showDropdown,
                                onDismissRequest = {
                                    HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                    showDropdown = false
                                },
                                modifier = Modifier
                                    .widthIn(min = 220.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(5.dp),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                // Create playlist from folder
                                onPlayFolder?.let {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "Create playlist",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            },
                                            leadingIcon = {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                                    shape = CircleShape,
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(6.dp)
                                                    )
                                                }
                                            },
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                                showDropdown = false
                                                it(item)
                                            }
                                        )
                                    }
                                }
                                
                                // Add all to queue
                                onAddFolderToQueue?.let {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "Add all to queue",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            },
                                            leadingIcon = {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                                    shape = CircleShape,
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = RhythmIcons.Queue,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(6.dp)
                                                    )
                                                }
                                            },
                                            onClick = {
                                                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                                                showDropdown = false
                                                it(item)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Forward arrow - commented out for now
                    // Surface(
                    //     modifier = Modifier.size(38.dp),
                    //     shape = CircleShape,
                    //     color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    // ) {
                    //     Box(contentAlignment = Alignment.Center) {
                    //         Icon(
                    //             imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    //             contentDescription = "Open folder",
                    //             modifier = Modifier.size(18.dp)
                    //         )
                    //     }
                    // }
                }
            }
        }

        ExplorerItemType.FILE -> {
            val song = item.song
            if (song != null) {
                // Use the existing LibrarySongItemWrapper for files
                LibrarySongItemWrapper(
                    song = song,
                    onClick = { onItemClick() },
                    onMoreClick = { onAddToPlaylist(song) },
                    onAddToQueue = { onAddToQueue(song) },
                    onShowSongInfo = { onShowSongInfo(song) },
                    onAddToBlacklist = { /* Files in explorer don't have blacklist functionality */ },
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    haptics = haptics,
                    enableRatingSystem = enableRatingSystem,
                    itemShape = itemShape ?: RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun FabMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    containerColor: Color, // Added containerColor
    contentColor: Color,   // Added contentColor
    onClick: () -> Unit,
    animationDelay: Int = 0,
    modifier: Modifier = Modifier,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback // Added haptics parameter
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Define scope here

    // Tap animation state
    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh),
        label = "pressedScale_$label"
    )

    // Staggered entrance animation
    val entranceScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "entranceScale_$label"
    )

    val entranceAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = animationDelay
        ),
        label = "entranceAlpha_$label"
    )

    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
            isPressed = true
            onClick()
            // Reset pressed state after animation
            scope.launch { // Use the local scope
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        },
        shape = RoundedCornerShape(50.dp), // Pill shape
        colors = CardDefaults.cardColors( // Use CardDefaults.cardColors
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 8.dp
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = entranceScale * pressedScale
                scaleY = entranceScale * pressedScale
                alpha = entranceAlpha
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        awaitRelease()
                        isPressed = false
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Bottom Floating Button Group Component
@Composable
fun BottomFloatingButtonGroup(
    modifier: Modifier = Modifier,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Loading states
    var isPlayAllLoading by remember { mutableStateOf(false) }
    var isShuffleLoading by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Play All Button
            Button(
                onClick = {
                    if (!isPlayAllLoading && !isShuffleLoading) {
                        isPlayAllLoading = true
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        // Must call on Main thread for MediaController
                        scope.launch {
                            try {
                                onPlayAll()
                            } finally {
                                kotlinx.coroutines.delay(500)
                                isPlayAllLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
                enabled = !isPlayAllLoading && !isShuffleLoading
            ) {
                if (isPlayAllLoading) {
                    ActionProgressLoader(
                        size = 20.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = RhythmIcons.Play,
                        contentDescription = "Play all",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = context.getString(R.string.library_play_all),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Expressive Shuffle Button
            ExpressiveFilledIconButton(
                onClick = {
                    if (!isPlayAllLoading && !isShuffleLoading) {
                        isShuffleLoading = true
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        // Must call on Main thread for MediaController
                        scope.launch {
                            try {
                                onShuffle()
                            } finally {
                                kotlinx.coroutines.delay(500)
                                isShuffleLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.size(52.dp),
                shape = ExpressiveShapes.SquircleMedium,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                enabled = !isPlayAllLoading && !isShuffleLoading
            ) {
                if (isShuffleLoading) {
                    ActionProgressLoader(
                        size = 24.dp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                } else {
                    Icon(
                        imageVector = RhythmIcons.Shuffle,
                        contentDescription = "Shuffle",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
