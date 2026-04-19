package chromahub.rhythm.app.features.local.presentation.components.bottomsheets

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.unit.dp
import chromahub.rhythm.app.shared.data.model.Song
import chromahub.rhythm.app.shared.presentation.components.common.M3PlaceholderType
import chromahub.rhythm.app.shared.presentation.components.common.ButtonGroupStyle
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveButtonGroup
import chromahub.rhythm.app.shared.presentation.components.common.ExpressiveGroupButton
import chromahub.rhythm.app.util.ImageUtils
import chromahub.rhythm.app.util.MediaUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private fun resolveBatchEditArtworkUri(context: android.content.Context, song: Song): Uri? {
    val currentArtworkUri = song.artworkUri

    if (currentArtworkUri != null &&
        !isMediaStoreAlbumArtworkUriForBatch(currentArtworkUri) &&
        isUsableArtworkUriForBatch(currentArtworkUri)
    ) {
        return currentArtworkUri
    }

    val cachedLossless = MediaUtils.getCachedEmbeddedAlbumArtUri(
        cacheDir = context.cacheDir,
        songUri = song.uri,
        lossless = true
    )
    if (cachedLossless != null) {
        return cachedLossless
    }

    val cachedLossy = MediaUtils.getCachedEmbeddedAlbumArtUri(
        cacheDir = context.cacheDir,
        songUri = song.uri,
        lossless = false
    )
    if (cachedLossy != null) {
        return cachedLossy
    }

    val extractedEmbedded = MediaUtils.extractEmbeddedAlbumArt(
        context = context,
        songUri = song.uri,
        cacheDir = context.cacheDir,
        lossless = false
    )
    if (extractedEmbedded != null) {
        return extractedEmbedded
    }

    return currentArtworkUri
}

private fun isMediaStoreAlbumArtworkUriForBatch(uri: Uri): Boolean {
    val value = uri.toString().lowercase()
    return value.startsWith("content://media/") && value.contains("/audio/albumart")
}

private fun isUsableArtworkUriForBatch(uri: Uri): Boolean {
    return when (uri.scheme) {
        "file", null -> uri.path?.let { File(it).exists() } == true
        else -> true
    }
}

/**
 * Bottom sheet for batch editing metadata tags on multiple selected songs.
 * Each field has a checkbox to enable/disable it. Only enabled fields are applied.
 * UI matches the SongInfoBottomSheet metadata editor style.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchEditTagsSheet(
    selectedSongs: List<Song>,
    onDismiss: () -> Unit,
    onSave: (artist: String?, album: String?, genre: String?, year: Int?, artworkUri: Uri?, removeArtwork: Boolean) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var editArtist by remember { mutableStateOf(false) }
    var editAlbum by remember { mutableStateOf(false) }
    var editGenre by remember { mutableStateOf(false) }
    var editYear by remember { mutableStateOf(false) }
    var editArtwork by remember { mutableStateOf(false) }

    var artist by remember { mutableStateOf("") }
    var album by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var removeArtwork by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        if (uri != null) {
            removeArtwork = false
        }
    }

    val enabledFieldCount = listOf(
        editArtist,
        editAlbum,
        editGenre,
        editYear,
        editArtwork
    ).count { it }

    val previewSong = selectedSongs.firstOrNull()
    var resolvedArtworkPreviewUri by remember(previewSong?.id, previewSong?.artworkUri) {
        mutableStateOf(previewSong?.artworkUri)
    }

    LaunchedEffect(previewSong?.id, previewSong?.artworkUri, previewSong?.uri) {
        resolvedArtworkPreviewUri = if (previewSong == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                resolveBatchEditArtworkUri(context, previewSong)
            }
        }
    }

    val artworkPreviewUri = when {
        removeArtwork -> null
        selectedImageUri != null -> selectedImageUri
        else -> resolvedArtworkPreviewUri
    }

    fun submitBatchChanges() {
        if (!editArtist && !editAlbum && !editGenre && !editYear && !editArtwork) {
            Toast.makeText(context, "Enable at least one field to edit", Toast.LENGTH_SHORT).show()
            return
        }
        val hasValidInput = (editArtist && artist.trim().isNotBlank()) ||
            (editAlbum && album.trim().isNotBlank()) ||
            (editGenre && genre.trim().isNotBlank()) ||
            (editYear && year.toIntOrNull() != null) ||
            (editArtwork && (selectedImageUri != null || removeArtwork))

        if (!hasValidInput) {
            Toast.makeText(context, "Please enter a value for at least one enabled field", Toast.LENGTH_SHORT).show()
            return
        }

        isSaving = true
        onSave(
            if (editArtist) artist.trim().takeIf { it.isNotBlank() } else null,
            if (editAlbum) album.trim().takeIf { it.isNotBlank() } else null,
            if (editGenre) genre.trim().takeIf { it.isNotBlank() } else null,
            if (editYear) year.toIntOrNull() else null,
            if (editArtwork) selectedImageUri else null,
            if (editArtwork) removeArtwork else false
        )
    }

    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .navigationBarsPadding()
                .padding(vertical = 8.dp)
        ) {
            StandardBottomSheetHeader(
                title = "Batch Edit Tags",
                subtitle = "${selectedSongs.size} songs selected • $enabledFieldCount fields enabled",
                visible = true,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Enable fields you want to update. Disabled fields stay unchanged.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Tags",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            BatchEditField(
                                label = "Artist",
                                icon = Icons.Rounded.Person,
                                enabled = editArtist,
                                value = artist,
                                onEnabledChange = { editArtist = it },
                                onValueChange = { artist = it }
                            )

                            BatchEditField(
                                label = "Album",
                                icon = Icons.Rounded.Album,
                                enabled = editAlbum,
                                value = album,
                                onEnabledChange = { editAlbum = it },
                                onValueChange = { album = it }
                            )

                            BatchEditField(
                                label = "Genre",
                                icon = Icons.Rounded.Category,
                                enabled = editGenre,
                                value = genre,
                                onEnabledChange = { editGenre = it },
                                onValueChange = { genre = it }
                            )

                            BatchEditField(
                                label = "Year",
                                icon = Icons.Rounded.DateRange,
                                enabled = editYear,
                                value = year,
                                onEnabledChange = { editYear = it },
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() } && input.length <= 4) {
                                        year = input
                                    }
                                },
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = when {
                                    removeArtwork -> "Artwork will be removed"
                                    selectedImageUri != null -> "New artwork selected"
                                    else -> "No artwork change"
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Artwork") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Image,
                                        contentDescription = null,
                                        tint = if (editArtwork) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                trailingIcon = {
                                    Checkbox(
                                        checked = editArtwork,
                                        onCheckedChange = { enabled ->
                                            editArtwork = enabled
                                            if (!enabled) {
                                                selectedImageUri = null
                                                removeArtwork = false
                                            }
                                        }
                                    )
                                },
                                enabled = editArtwork,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true
                            )

                            AnimatedVisibility(visible = editArtwork) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(96.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .apply(
                                                    ImageUtils.buildImageRequest(
                                                        artworkPreviewUri,
                                                        selectedSongs.firstOrNull()?.title ?: "Batch",
                                                        context.cacheDir,
                                                        M3PlaceholderType.TRACK
                                                    )
                                                )
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Batch artwork preview",
                                            modifier = Modifier.fillMaxWidth(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { imagePickerLauncher.launch("image/*") },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Image,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (selectedImageUri != null) "Change" else "Select")
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                selectedImageUri = null
                                                removeArtwork = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Delete,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Remove")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress
            AnimatedVisibility(visible = isSaving) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Saving... ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Buttons
            ExpressiveButtonGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                style = ButtonGroupStyle.Tonal
            ) {
                ExpressiveGroupButton(
                    onClick = onDismiss,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                    isStart = true
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancel")
                }

                ExpressiveGroupButton(
                    onClick = { submitBatchChanges() },
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                    isEnd = true
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun BatchEditField(
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    value: String,
    onEnabledChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            Checkbox(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
