package chromahub.rhythm.app.features.local.presentation.components.bottomsheets

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import chromahub.rhythm.app.shared.data.model.Song

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
    onSave: (artist: String?, album: String?, genre: String?, year: Int?) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var editArtist by remember { mutableStateOf(false) }
    var editAlbum by remember { mutableStateOf(false) }
    var editGenre by remember { mutableStateOf(false) }
    var editYear by remember { mutableStateOf(false) }

    var artist by remember { mutableStateOf("") }
    var album by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 0.dp, vertical = 0.dp)
        ) {
            // Header - matches standard bottom sheet pattern
            StandardBottomSheetHeader(
                title = "Batch Edit Tags",
                subtitle = "${selectedSongs.size} songs selected",
                visible = true,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Check a field to enable editing. Only checked fields will be applied.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Artist field
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BatchEditField(
                    label = "Artist",
                    icon = Icons.Rounded.Person,
                    enabled = editArtist,
                    value = artist,
                    onEnabledChange = { editArtist = it },
                    onValueChange = { artist = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Album field
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BatchEditField(
                    label = "Album",
                    icon = Icons.Rounded.Album,
                    enabled = editAlbum,
                    value = album,
                    onEnabledChange = { editAlbum = it },
                    onValueChange = { album = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Genre field
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BatchEditField(
                    label = "Genre",
                    icon = Icons.Rounded.Category,
                    enabled = editGenre,
                    value = genre,
                    onEnabledChange = { editGenre = it },
                    onValueChange = { genre = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Year field
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
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

            Spacer(modifier = Modifier.height(16.dp))

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (!editArtist && !editAlbum && !editGenre && !editYear) {
                            Toast.makeText(context, "Enable at least one field to edit", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // Check if at least one enabled field has a non-empty value
                        val hasValidInput = (editArtist && artist.trim().isNotBlank()) ||
                                           (editAlbum && album.trim().isNotBlank()) ||
                                           (editGenre && genre.trim().isNotBlank()) ||
                                           (editYear && year.toIntOrNull() != null)
                        if (!hasValidInput) {
                            Toast.makeText(context, "Please enter a value for at least one enabled field", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSaving = true
                        onSave(
                            if (editArtist) artist.trim().takeIf { it.isNotBlank() } else null,
                            if (editAlbum) album.trim().takeIf { it.isNotBlank() } else null,
                            if (editGenre) genre.trim().takeIf { it.isNotBlank() } else null,
                            if (editYear) year.toIntOrNull() else null
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
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

            Spacer(modifier = Modifier.height(24.dp))
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
