package chromahub.rhythm.app.features.streaming.presentation.screens

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import chromahub.rhythm.app.R
import chromahub.rhythm.app.features.local.presentation.screens.settings.TunerAnimatedSwitch
import chromahub.rhythm.app.features.streaming.presentation.model.StreamingServiceOptions
import chromahub.rhythm.app.features.streaming.presentation.viewmodel.StreamingMusicViewModel
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.shared.presentation.components.Material3SettingsGroup
import chromahub.rhythm.app.shared.presentation.components.Material3SettingsItem
import chromahub.rhythm.app.shared.presentation.components.common.CollapsibleHeaderScreen
import chromahub.rhythm.app.util.HapticUtils

@Composable
fun StreamingSettingsScreen(
    viewModel: StreamingMusicViewModel,
    onOpenGlobalSettings: () -> Unit,
    onConfigureService: (String) -> Unit,
    onSwitchToLocalMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val appSettings = remember { AppSettings.getInstance(context) }

    val selectedService by appSettings.streamingService.collectAsState()
    val appMode by appSettings.appMode.collectAsState()
    val sessions by viewModel.serviceSessions.collectAsState()
    val streamingQuality by appSettings.streamingQuality.collectAsState()
    val allowCellularStreaming by appSettings.allowCellularStreaming.collectAsState()
    val offlineMode by appSettings.offlineMode.collectAsState()
    val selectedServiceConnected = sessions[selectedService]?.isConnected == true

    var showServiceSheet by remember { mutableStateOf(false) }
    var showQualitySheet by remember { mutableStateOf(false) }

    fun setGoMode(enabled: Boolean) {
        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
        if (enabled) {
            appSettings.setAppMode("STREAMING")
        } else {
            onSwitchToLocalMode()
        }
    }

    CollapsibleHeaderScreen(
        title = stringResource(id = R.string.streaming_settings_title),
        headerDisplayMode = 1
    ) { contentModifier ->
        LazyColumn(
            modifier = modifier
                .then(contentModifier)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Material3SettingsGroup(
                    title = stringResource(id = R.string.streaming_settings_group_services),
                    items = listOf(
                        Material3SettingsItem(
                            icon = Icons.Default.CloudQueue,
                            title = {
                                Text(text = stringResource(id = R.string.streaming_settings_preferred_service))
                            },
                            description = {
                                Text(text = selectedServiceLabel(selectedService, context))
                            },
                            onClick = { showServiceSheet = true }
                        ),
                        Material3SettingsItem(
                            icon = Icons.Default.HighQuality,
                            title = {
                                Text(text = stringResource(id = R.string.streaming_settings_quality))
                            },
                            description = {
                                Text(text = streamingQualityLabel(streamingQuality, context))
                            },
                            onClick = { showQualitySheet = true }
                        ),
                        Material3SettingsItem(
                            icon = Icons.Default.Settings,
                            title = {
                                Text(text = stringResource(id = R.string.streaming_manage_service))
                            },
                            description = {
                                Text(
                                    text = if (selectedServiceConnected) {
                                        stringResource(id = R.string.streaming_status_connected)
                                    } else {
                                        stringResource(id = R.string.streaming_status_not_connected)
                                    }
                                )
                            },
                            onClick = { onConfigureService(selectedService) }
                        )
                    )
                )
            }

            item {
                Material3SettingsGroup(
                    title = stringResource(id = R.string.streaming_settings_group_network),
                    items = listOf(
                        Material3SettingsItem(
                            icon = Icons.Default.MobileFriendly,
                            title = {
                                Text(text = stringResource(id = R.string.settings_allow_cellular_streaming))
                            },
                            description = {
                                Text(text = stringResource(id = R.string.settings_allow_cellular_streaming_desc))
                            },
                            trailingContent = {
                                TunerAnimatedSwitch(
                                    checked = allowCellularStreaming,
                                    onCheckedChange = {
                                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                                        appSettings.setAllowCellularStreaming(it)
                                    }
                                )
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                                appSettings.setAllowCellularStreaming(!allowCellularStreaming)
                            }
                        ),
                        Material3SettingsItem(
                            icon = Icons.Default.OfflineBolt,
                            title = {
                                Text(text = stringResource(id = R.string.streaming_settings_offline_mode))
                            },
                            description = {
                                Text(text = stringResource(id = R.string.streaming_settings_offline_mode_desc))
                            },
                            trailingContent = {
                                TunerAnimatedSwitch(
                                    checked = offlineMode,
                                    onCheckedChange = {
                                        HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                                        appSettings.setOfflineMode(it)
                                    }
                                )
                            },
                            onClick = {
                                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                                appSettings.setOfflineMode(!offlineMode)
                            }
                        )
                    )
                )
            }

            item {
                Material3SettingsGroup(
                    title = stringResource(id = R.string.settings_music_mode),
                    items = listOf(
                        Material3SettingsItem(
                            icon = Icons.Default.CloudQueue,
                            title = {
                                Text(text = stringResource(id = R.string.exp_go_mode))
                            },
                            description = {
                                Text(text = stringResource(id = R.string.exp_go_mode_desc))
                            },
                            trailingContent = {
                                TunerAnimatedSwitch(
                                    checked = appMode == "STREAMING",
                                    onCheckedChange = { enabled -> setGoMode(enabled) }
                                )
                            },
                            onClick = { setGoMode(appMode != "STREAMING") }
                        )
                    )
                )
            }

            item {
                Material3SettingsGroup(
                    title = stringResource(id = R.string.streaming_settings_group_actions),
                    items = listOf(
                        Material3SettingsItem(
                            icon = Icons.Default.Settings,
                            title = {
                                Text(text = stringResource(id = R.string.streaming_open_full_settings))
                            },
                            description = {
                                Text(text = stringResource(id = R.string.streaming_open_full_settings_desc))
                            },
                            onClick = onOpenGlobalSettings
                        ),
                        Material3SettingsItem(
                            icon = Icons.Default.SwapHoriz,
                            title = {
                                Text(text = stringResource(id = R.string.streaming_switch_to_local_mode))
                            },
                            description = {
                                Text(text = stringResource(id = R.string.streaming_switch_to_local_mode_desc))
                            },
                            onClick = onSwitchToLocalMode
                        )
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }

    if (showServiceSheet) {
        ServiceSelectionBottomSheet(
            selectedService = selectedService,
            sessions = sessions,
            onDismiss = { showServiceSheet = false },
            onSelect = { serviceId ->
                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                appSettings.setStreamingService(serviceId)
                showServiceSheet = false
            }
        )
    }

    if (showQualitySheet) {
        QualitySelectionBottomSheet(
            selectedQuality = normalizeStreamingQuality(streamingQuality),
            onDismiss = { showQualitySheet = false },
            onSelect = { quality ->
                HapticUtils.performHapticFeedback(context, hapticFeedback, HapticFeedbackType.TextHandleMove)
                appSettings.setStreamingQuality(quality)
                showQualitySheet = false
            }
        )
    }
}

@Composable
private fun ServiceSelectionBottomSheet(
    selectedService: String,
    sessions: Map<String, chromahub.rhythm.app.features.streaming.data.repository.StreamingServiceSession>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.streaming_settings_preferred_service),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.streaming_settings_service_sheet_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            StreamingServiceOptions.defaults.forEach { option ->
                val isSelected = selectedService == option.id
                val isConnected = sessions[option.id]?.isConnected == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    ),
                    onClick = { onSelect(option.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = option.nameRes),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            Text(
                                text = if (isConnected) {
                                    stringResource(id = R.string.streaming_status_connected)
                                } else {
                                    stringResource(id = R.string.streaming_status_not_connected)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QualitySelectionBottomSheet(
    selectedQuality: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.streaming_settings_quality),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.streaming_settings_quality_sheet_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            streamingQualityOptions.forEach { option ->
                val isSelected = selectedQuality == option.value

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    ),
                    onClick = { onSelect(option.value) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HighQuality,
                            contentDescription = null,
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = option.titleRes),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            Text(
                                text = stringResource(id = option.descriptionRes),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun selectedServiceLabel(selectedService: String, context: Context): String {
    val matching = StreamingServiceOptions.defaults.firstOrNull { it.id == selectedService }
    return matching?.let { context.getString(it.nameRes) } ?: context.getString(R.string.streaming_not_selected)
}

private fun normalizeStreamingQuality(rawValue: String): String {
    val normalized = rawValue.uppercase()
    return if (streamingQualityOptions.any { it.value == normalized }) normalized else "HIGH"
}

private fun streamingQualityLabel(quality: String, context: Context): String {
    return when (normalizeStreamingQuality(quality)) {
        "LOW" -> context.getString(R.string.streaming_quality_low)
        "NORMAL" -> context.getString(R.string.streaming_quality_normal)
        "HIGH" -> context.getString(R.string.streaming_quality_high)
        "LOSSLESS" -> context.getString(R.string.streaming_quality_lossless)
        else -> quality
    }
}

private data class StreamingQualityOption(
    val value: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
)

private val streamingQualityOptions = listOf(
    StreamingQualityOption(
        value = "LOW",
        titleRes = R.string.streaming_quality_low,
        descriptionRes = R.string.streaming_quality_low_desc
    ),
    StreamingQualityOption(
        value = "NORMAL",
        titleRes = R.string.streaming_quality_normal,
        descriptionRes = R.string.streaming_quality_normal_desc
    ),
    StreamingQualityOption(
        value = "HIGH",
        titleRes = R.string.streaming_quality_high,
        descriptionRes = R.string.streaming_quality_high_desc
    ),
    StreamingQualityOption(
        value = "LOSSLESS",
        titleRes = R.string.streaming_quality_lossless,
        descriptionRes = R.string.streaming_quality_lossless_desc
    )
)
