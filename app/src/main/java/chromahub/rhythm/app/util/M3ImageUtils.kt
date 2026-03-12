package chromahub.rhythm.app.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import chromahub.rhythm.app.shared.presentation.components.common.AlbumPlaceholder
import chromahub.rhythm.app.shared.presentation.components.common.ArtistPlaceholder
import chromahub.rhythm.app.shared.presentation.components.common.M3PlaceholderType
import chromahub.rhythm.app.shared.presentation.components.common.PlaylistPlaceholder
import chromahub.rhythm.app.shared.presentation.components.common.TrackPlaceholder
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveShapeTarget
import chromahub.rhythm.app.shared.presentation.components.common.rememberExpressiveShapeFor

/**
 * Modern Material 3 style utilities for image handling using Compose and Coil
 */
object M3ImageUtils {

    /**
     * Display a media image with appropriate Material 3 style placeholder
     */
    @Composable
    fun M3MediaImage(
        data: Any?,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        shape: Shape? = null,
        type: M3PlaceholderType = M3PlaceholderType.GENERAL,
        name: String? = null,
        expressiveShape: Shape? = null
    ) {
        val context = LocalContext.current
        
        val imageRequest = remember(data) {
            ImageRequest.Builder(context)
                .data(data)
                .crossfade(150)
                .memoryCacheKey(data?.toString())
                .diskCacheKey(data?.toString())
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
        }
        
        var showPlaceholder by remember { mutableStateOf(true) }
        
        Box(modifier = modifier) {
            AsyncImage(
                model = imageRequest,
                contentDescription = contentDescription,
                modifier = if (shape != null) Modifier.fillMaxSize().clip(shape) else Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                onState = { state ->
                    showPlaceholder = state is AsyncImagePainter.State.Loading || 
                                     state is AsyncImagePainter.State.Error
                }
            )
            
            // Show appropriate placeholder based on loading state
            AnimatedVisibility(
                visible = showPlaceholder,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (type) {
                        M3PlaceholderType.ALBUM -> AlbumPlaceholder(name, Modifier.fillMaxSize(), expressiveShape)
                        M3PlaceholderType.ARTIST -> ArtistPlaceholder(name, Modifier.fillMaxSize(), expressiveShape)
                        M3PlaceholderType.TRACK -> TrackPlaceholder(name, Modifier.fillMaxSize(), expressiveShape)
                        M3PlaceholderType.PLAYLIST -> PlaylistPlaceholder(name, Modifier.fillMaxSize(), expressiveShape)
                        M3PlaceholderType.GENERAL -> AlbumPlaceholder(name, Modifier.fillMaxSize(), expressiveShape)
                    }
                }
            }
        }
    }
    
    /**
     * Album art with Material 3 placeholder and expressive shape support
     */
    @Composable
    fun AlbumArt(
        imageUrl: Any?,
        albumName: String?,
        modifier: Modifier = Modifier,
        shape: Shape? = null,
        applyExpressiveShape: Boolean = true
    ) {
        val expressiveShape = if (applyExpressiveShape) {
            rememberExpressiveShapeFor(
                ExpressiveShapeTarget.ALBUM_ART,
                MaterialTheme.shapes.large
            )
        } else {
            null // Don't apply any shape when applyExpressiveShape is false
        }
        val finalShape = shape ?: expressiveShape
        
        M3MediaImage(
            data = imageUrl,
            contentDescription = "Album art for $albumName",
            modifier = modifier,
            shape = finalShape,
            type = M3PlaceholderType.ALBUM,
            name = albumName,
            expressiveShape = if (applyExpressiveShape) expressiveShape else null
        )
    }
    
    /**
     * Artist image with Material 3 placeholder and expressive shape support
     */
    @Composable
    fun ArtistImage(
        imageUrl: Any?,
        artistName: String?,
        modifier: Modifier = Modifier,
        shape: Shape? = null,
        applyExpressiveShape: Boolean = true
    ) {
        val expressiveShape = if (applyExpressiveShape) {
            rememberExpressiveShapeFor(
                ExpressiveShapeTarget.ARTIST_ART,
                MaterialTheme.shapes.large
            )
        } else {
            null // Don't apply any shape when applyExpressiveShape is false
        }
        val finalShape = shape ?: expressiveShape
        
        M3MediaImage(
            data = imageUrl,
            contentDescription = "Image of artist $artistName",
            modifier = modifier,
            shape = finalShape,
            type = M3PlaceholderType.ARTIST,
            name = artistName,
            expressiveShape = if (applyExpressiveShape) expressiveShape else null
        )
    }
    
    /**
     * Track image with Material 3 placeholder and expressive shape support
     */
    @Composable
    fun TrackImage(
        imageUrl: Any?,
        trackName: String?,
        modifier: Modifier = Modifier,
        shape: Shape? = null,
        applyExpressiveShape: Boolean = true
    ) {
        val expressiveShape = if (applyExpressiveShape) {
            rememberExpressiveShapeFor(
                ExpressiveShapeTarget.SONG_ART,
                MaterialTheme.shapes.large
            )
        } else {
            null // Don't apply any shape when applyExpressiveShape is false
        }
        val finalShape = shape ?: expressiveShape
        
        M3MediaImage(
            data = imageUrl,
            contentDescription = "Image for track $trackName",
            modifier = modifier,
            shape = finalShape,
            type = M3PlaceholderType.TRACK,
            name = trackName,
            expressiveShape = if (applyExpressiveShape) expressiveShape else null
        )
    }
    
    /**
     * Playlist image with Material 3 placeholder and expressive shape support
     */
    @Composable
    fun PlaylistImage(
        imageUrl: Any?,
        playlistName: String?,
        modifier: Modifier = Modifier,
        shape: Shape? = null,
        applyExpressiveShape: Boolean = true
    ) {
        val expressiveShape = if (applyExpressiveShape) {
            rememberExpressiveShapeFor(
                ExpressiveShapeTarget.PLAYLIST_ART,
                MaterialTheme.shapes.large
            )
        } else {
            null // Don't apply any shape when applyExpressiveShape is false
        }
        val finalShape = shape ?: expressiveShape
        
        M3MediaImage(
            data = imageUrl,
            contentDescription = "Image for playlist $playlistName",
            modifier = modifier,
            shape = finalShape,
            type = M3PlaceholderType.PLAYLIST,
            name = playlistName,
            expressiveShape = if (applyExpressiveShape) expressiveShape else null
        )
    }
} 