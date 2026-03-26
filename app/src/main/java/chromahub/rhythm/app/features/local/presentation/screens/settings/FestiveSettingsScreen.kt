package chromahub.rhythm.app.features.local.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import chromahub.rhythm.app.shared.presentation.components.common.CollapsibleHeaderScreen
import chromahub.rhythm.app.shared.data.model.AppSettings
import chromahub.rhythm.app.ui.theme.festive.FestiveThemeType

@Composable
fun FestiveSettingsScreen(
    onBackClick: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val appSettings = remember { AppSettings.getInstance(context) }
    
    val festiveEnabled by appSettings.festiveThemeEnabled.collectAsState()
    val festiveType by appSettings.festiveThemeType.collectAsState()
    val festiveIntensity by appSettings.festiveThemeIntensity.collectAsState()
    val festiveAutoDetect by appSettings.festiveThemeAutoDetect.collectAsState()

    CollapsibleHeaderScreen(
        title = "Festive Theme",
        showBackButton = true,
        onBackClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onBackClick()
        }
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item {
                Text(
                    text = "Festive Decorations",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        FestiveSettingRow(
                            icon = Icons.Default.Celebration,
                            title = "Enable Festive Theme",
                            description = "Show festive decorations across the app",
                            checked = festiveEnabled,
                            onCheckedChange = { 
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                appSettings.setFestiveThemeEnabled(it) 
                            }
                        )
                        
                        if (festiveEnabled) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                            
                            FestiveSettingRow(
                                icon = Icons.Default.EventAvailable,
                                title = "Auto-Detect Holidays",
                                description = "Automatically show decorations for holidays",
                                checked = festiveAutoDetect,
                                onCheckedChange = { 
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    appSettings.setFestiveThemeAutoDetect(it) 
                                }
                            )
                        }
                    }
                }
            }
            
            if (festiveEnabled && !festiveAutoDetect) {
                item { Spacer(modifier = Modifier.height(24.dp)) }
                
                item {
                    Text(
                        text = "Festive Theme Type",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            FestiveTypeOption(
                                icon = Icons.Default.AcUnit,
                                title = "Christmas",
                                description = "Snowfall decorations",
                                selected = festiveType == "CHRISTMAS",
                                onClick = { 
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    appSettings.setFestiveThemeType("CHRISTMAS") 
                                }
                            )
                            
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                            
                            FestiveTypeOption(
                                icon = Icons.Default.Celebration,
                                title = "New Year",
                                description = "Festive snowfall and sparkles",
                                selected = festiveType == "NEW_YEAR",
                                onClick = { 
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    appSettings.setFestiveThemeType("NEW_YEAR") 
                                }
                            )
                        }
                    }
                }
            }
            
            if (festiveEnabled) {
                item { Spacer(modifier = Modifier.height(24.dp)) }
                
                item {
                    Text(
                        text = "Intensity",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Decoration Intensity",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${(festiveIntensity * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Slider(
                                value = festiveIntensity,
                                onValueChange = { 
                                    appSettings.setFestiveThemeIntensity(it) 
                                },
                                valueRange = 0.1f..1f,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Adjust the amount of festive decorations shown",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "About Festive Themes",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Festive decorations add a touch of celebration to your music experience. When auto-detect is enabled, decorations appear automatically during holidays like Christmas (Dec 15-31).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun FestiveSettingRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun FestiveTypeOption(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        onClick = if (enabled) onClick else { {} },
        modifier = Modifier.fillMaxWidth(),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = when {
                        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        selected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium),
                        color = when {
                            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            selected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                            selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
