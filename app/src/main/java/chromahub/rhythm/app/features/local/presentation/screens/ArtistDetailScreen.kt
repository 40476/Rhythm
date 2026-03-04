@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package chromahub.rhythm.app.features.local.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import chromahub.rhythm.app.R
import chromahub.rhythm.app.features.local.presentation.components.player.PlayingEqIcon
import chromahub.rhythm.app.features.local.presentation.components.player.formatDuration
import chromahub.rhythm.app.features.local.presentation.viewmodel.MusicViewModel
import chromahub.rhythm.app.shared.data.model.Album
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.data.model.Artist
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.shared.presentation.components.common.ArtistCollapsibleHeaderScreen
import chromahub.rhythm.app.shared.presentation.components.common.M3PlaceholderType
import chromahub.rhythm.app.shared.presentation.components.icons.RhythmIcons
import chromahub.rhythm.app.util.HapticUtils
import chromahub.rhythm.app.util.ImageUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ArtistDetailScreen(
    artistName: String,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onPlayAll: (List<Song>) -> Unit,
    onShufflePlay: (List<Song>) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddSongToPlaylist: (Song) -> Unit,
    onPlayerClick: () -> Unit,
    onPlayNext: (Song) -> Unit = {},
    onToggleFavorite: (Song) -> Unit = {},
    favoriteSongs: Set<String> = emptySet(),
    onShowSongInfo: (Song) -> Unit = {},
    currentSong: Song? = null,
    isPlaying: Boolean = false
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val viewModel: MusicViewModel = viewModel()
    val appSettings = remember { AppSettings.getInstance(context) }
    val groupByAlbumArtist by appSettings.groupByAlbumArtist.collectAsState()
    val useHoursFormat by appSettings.useHoursInTimeFormat.collectAsState()
    
    // Get songs and albums from viewModel
    val allSongs by viewModel.songs.collectAsState()
    val allAlbums by viewModel.albums.collectAsState()
    val allArtists by viewModel.artists.collectAsState()
    
    // Find the artist
    val artist = remember(allArtists, artistName) {
        allArtists.find { it.name == artistName }
    }
    
    // Helper function to split artist names
    val splitArtistNames: (String) -> List<String> = remember {
        { artistNameStr ->
            // Character-level delimiters from settings
            val artistSeparatorEnabled = appSettings.artistSeparatorEnabled.value
            val charDelimiters = if (artistSeparatorEnabled) {
                appSettings.artistSeparatorDelimiters.value.toList().map { it.toString() }
            } else emptyList()
            
            val wordSeparators = listOf(
                " & ", " and ", ", ", " feat. ", " feat ", " ft. ", " ft ",
                " featuring ", " x ", " X ", " vs ", " vs. ", " with "
            )
            var names = listOf(artistNameStr)
            for (delimiter in charDelimiters) {
                names = names.flatMap { it.split(delimiter) }
            }
            for (separator in wordSeparators) {
                names = names.flatMap { it.split(separator, ignoreCase = true) }
            }
            names.map { it.trim() }.filter { it.isNotBlank() }
        }
    }
    
    // Filter songs and albums for this artist based on grouping preference
    val artistSongs = remember(allSongs, artistName, groupByAlbumArtist) {
        allSongs.filter { song ->
            if (groupByAlbumArtist) {
                val songArtistName = (song.albumArtist?.takeIf { it.isNotBlank() } ?: song.artist).trim()
                songArtistName == artistName
            } else {
                splitArtistNames(song.artist).any { it.equals(artistName, ignoreCase = true) }
            }
        }
    }
    
    val artistAlbums = remember(allAlbums, allSongs, artistName, groupByAlbumArtist) {
        if (groupByAlbumArtist) {
            allAlbums.filter { album ->
                allSongs.any { song ->
                    song.album == album.title &&
                    song.albumId == album.id &&
                    (song.albumArtist?.takeIf { it.isNotBlank() } ?: song.artist).trim() == artistName
                }
            }
        } else {
            allAlbums.filter { album ->
                allSongs.any { song ->
                    song.album == album.title &&
                    song.albumId == album.id &&
                    splitArtistNames(song.artist).any { it.equals(artistName, ignoreCase = true) }
                }
            }
        }
    }
    
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showContent = true
    }

    ArtistCollapsibleHeaderScreen(
        title = artistName,
        artist = artist,
        artworkUri = artist?.artworkUri?.toString(),
        artistName = artistName,
        artistSongsCount = artistSongs.size,
        artistAlbumsCount = artistAlbums.size,
        showBackButton = true,
        onBackClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onBack()
        }
    ) { modifier ->
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Action Buttons Section
            item {
                Spacer(modifier = Modifier.height(38.dp)) // Extra spacing from header
                ArtistActionButtons(
                    artistSongs = artistSongs,
                    onPlayAll = {
                        if (artistSongs.isNotEmpty()) {
                            onPlayAll(artistSongs)
                            onPlayerClick()
                        }
                    },
                    onShufflePlay = {
                        if (artistSongs.isNotEmpty()) {
                            onShufflePlay(artistSongs)
                            onPlayerClick()
                        }
                    },
                    haptics = haptics
                )
            }
            
            // Albums Section
            if (artistAlbums.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    ArtistAlbumsSection(
                        artistAlbums = artistAlbums,
                        viewModel = viewModel,
                        onAlbumClick = onAlbumClick,
                        haptics = haptics
                    )
                }
            }
            
            // Songs Section
            if (artistSongs.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Songs Section Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = context.getString(R.string.bottomsheet_songs),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = RhythmIcons.Music.Song,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = "${artistSongs.size}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                            
                            // Songs List
                            artistSongs.forEach { song ->
                                ArtistSongItem(
                                    song = song,
                                    onClick = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        onSongClick(song)
                                        onPlayerClick()
                                    },
                                    onAddToQueue = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        onAddToQueue(song)
                                    },
                                    onAddToPlaylist = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        onAddSongToPlaylist(song)
                                    },
                                    onPlayNext = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        onPlayNext(song)
                                    },
                                    onToggleFavorite = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        onToggleFavorite(song)
                                    },
                                    isFavorite = favoriteSongs.contains(song.id),
                                    onShowSongInfo = {
                                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                                        onShowSongInfo(song)
                                    },
                                    currentSong = currentSong,
                                    isPlaying = isPlaying,
                                    useHoursFormat = useHoursFormat,
                                    haptics = haptics
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
private fun ArtistHeroHeader(
    artist: Artist?,
    artistName: String,
    artistSongsCount: Int,
    artistAlbumsCount: Int,
    showContent: Boolean
) {
    val context = LocalContext.current
    val headerAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(600),
        label = "headerAlpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .graphicsLayer { alpha = headerAlpha }
    ) {
        // Background Image with Gradient
        AsyncImage(
            model = ImageRequest.Builder(context)
                .apply(
                    ImageUtils.buildImageRequest(
                        artist?.artworkUri,
                        artistName,
                        context.cacheDir,
                        M3PlaceholderType.ARTIST
                    )
                )
                .build(),
            contentDescription = "Artist artwork for $artistName",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Multi-layer gradient overlay for better readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )
        
        // Artist Info - Bottom aligned
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = artistName,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Album,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$artistAlbumsCount albums",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Music.Song,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "$artistSongsCount songs",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistActionButtons(
    artistSongs: List<Song>,
    onPlayAll: () -> Unit,
    onShufflePlay: () -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    var shufflePressed by remember { mutableStateOf(false) }
    var playAllPressed by remember { mutableStateOf(false) }
    
    val shuffleScale by animateFloatAsState(
        targetValue = if (shufflePressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "shuffleScale"
    )
    
    val playAllScale by animateFloatAsState(
        targetValue = if (playAllPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "playAllScale"
    )
    
    LaunchedEffect(shufflePressed) {
        if (shufflePressed) {
            delay(150)
            shufflePressed = false
        }
    }
    
    LaunchedEffect(playAllPressed) {
        if (playAllPressed) {
            delay(150)
            playAllPressed = false
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Play All Button
        Button(
            onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                playAllPressed = true
                onPlayAll()
            },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .graphicsLayer {
                    scaleX = playAllScale
                    scaleY = playAllScale
                },
            shape = ButtonGroupDefaults.connectedLeadingButtonShapes().shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = artistSongs.isNotEmpty()
        ) {
            Icon(
                imageVector = RhythmIcons.Play,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Play All",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Shuffle Button
        FilledTonalButton(
            onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                shufflePressed = true
                onShufflePlay()
            },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .graphicsLayer {
                    scaleX = shuffleScale
                    scaleY = shuffleScale
                },
            shape = ButtonGroupDefaults.connectedTrailingButtonShapes().shape,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            enabled = artistSongs.isNotEmpty()
        ) {
            Icon(
                imageVector = RhythmIcons.Shuffle,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Shuffle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ArtistAlbumsSection(
    artistAlbums: List<Album>,
    viewModel: MusicViewModel,
    onAlbumClick: (Album) -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = context.getString(R.string.bottomsheet_albums),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp)) // Add spacing between header and cards
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = artistAlbums,
                key = { "artistalbum_${it.id}" }
            ) { album ->
                ArtistAlbumCard(
                    album = album,
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                        onAlbumClick(album)
                    },
                    onPlay = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        viewModel.playAlbum(album)
                    },
                    haptics = haptics
                )
            }
        }
    }
}

@Composable
private fun ArtistAlbumCard(
    album: Album,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    
    Card(
        onClick = {
            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .apply(
                            ImageUtils.buildImageRequest(
                                album.artworkUri,
                                album.title,
                                context.cacheDir,
                                M3PlaceholderType.ALBUM
                            )
                        )
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Play button overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.TextHandleMove)
                            onPlay()
                        },
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = RhythmIcons.Play,
                            contentDescription = "Play album",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${album.numberOfSongs} ${if (album.numberOfSongs == 1) "song" else "songs"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ArtistSongItem(
    song: Song,
    onClick: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onPlayNext: () -> Unit,
    onToggleFavorite: () -> Unit,
    isFavorite: Boolean,
    onShowSongInfo: () -> Unit,
    currentSong: Song?,
    isPlaying: Boolean,
    useHoursFormat: Boolean,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }
    
    val isCurrentSong = currentSong?.id == song.id
    
    val titleColor by animateColorAsState(
        targetValue = if (isCurrentSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(300),
        label = "titleColor"
    )
    
    val albumColor by animateColorAsState(
        targetValue = if (isCurrentSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "albumColor"
    )
    
    val containerColor by animateColorAsState(
        targetValue = if (isCurrentSong) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent,
        animationSpec = tween(300),
        label = "containerColor"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = titleColor
                )
            },
            supportingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = song.album,
                        style = MaterialTheme.typography.bodySmall,
                        color = albumColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    if (song.duration > 0) {
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = albumColor
                        )
                        
                        val durationText = formatDuration(song.duration, useHoursFormat)
                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.bodySmall,
                            color = albumColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            leadingContent = {
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp),
                        tonalElevation = 2.dp
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .apply(
                                    ImageUtils.buildImageRequest(
                                        song.artworkUri,
                                        song.title,
                                        context.cacheDir,
                                        M3PlaceholderType.TRACK
                                    )
                                )
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    if (isCurrentSong && isPlaying) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(22.dp)
                                .offset(x = 4.dp, y = 4.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 0.dp
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                PlayingEqIcon(
                                    modifier = Modifier.size(width = 13.dp, height = 11.dp),
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
                FilledIconButton(
                    onClick = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        showDropdown = true
                    },
                    modifier = Modifier.size(width = 42.dp, height = 38.dp),
                    shape = RoundedCornerShape(19.dp),
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
                
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = {
                        HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                        showDropdown = false
                    },
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(6.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    // Play next
                    SongItemDropdownMenuItem(
                        text = "Play next",
                        icon = Icons.Rounded.SkipNext,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onPlayNext()
                        }
                    )
                    
                    // Add to queue
                    SongItemDropdownMenuItem(
                        text = "Add to queue",
                        icon = RhythmIcons.Queue,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onAddToQueue()
                        }
                    )
                    
                    // Toggle favorite
                    SongItemDropdownMenuItem(
                        text = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        icon = if (isFavorite) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onToggleFavorite()
                        }
                    )
                    
                    // Add to playlist
                    SongItemDropdownMenuItem(
                        text = "Add to playlist",
                        icon = RhythmIcons.AddToPlaylist,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onAddToPlaylist()
                        }
                    )
                    
                    // Song info
                    SongItemDropdownMenuItem(
                        text = "Song info",
                        icon = Icons.Rounded.Info,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                            showDropdown = false
                            onShowSongInfo()
                        }
                    )
                }
            },
            modifier = Modifier.clickable(onClick = {
                HapticUtils.performHapticFeedback(context, haptics, HapticFeedbackType.LongPress)
                onClick()
            }),
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SongItemDropdownMenuItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Surface(
                    color = containerColor.copy(alpha = 0.7f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            },
            onClick = onClick
        )
    }
}
