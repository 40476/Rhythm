package chromahub.rhythm.app.features.streaming.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import chromahub.rhythm.app.R
import chromahub.rhythm.app.features.streaming.domain.model.StreamingServiceRules
import chromahub.rhythm.app.features.streaming.presentation.model.StreamingServiceOptions
import chromahub.rhythm.app.features.streaming.presentation.viewmodel.StreamingMusicViewModel
import chromahub.rhythm.app.shared.presentation.components.common.CollapsibleHeaderScreen

@Composable
fun StreamingServiceSetupScreen(
    serviceId: String,
    viewModel: StreamingMusicViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.serviceSessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val option = remember(serviceId) {
        StreamingServiceOptions.defaults.firstOrNull { it.id == serviceId }
    }
    val optionName = option?.let { stringResource(id = it.nameRes) } ?: serviceId
    val optionDescription = option?.let { stringResource(id = it.descriptionRes) }
    val session = sessions[serviceId] ?: viewModel.getServiceSession(serviceId)
    val requiresServerUrl = remember(serviceId) { StreamingServiceRules.requiresServerUrl(serviceId) }

    var serverUrl by remember(serviceId) { mutableStateOf(session.serverUrl) }
    var username by remember(serviceId) { mutableStateOf(session.username) }
    var password by remember(serviceId) { mutableStateOf("") }

    CollapsibleHeaderScreen(
        title = stringResource(id = R.string.streaming_service_setup_title, optionName),
        showBackButton = true,
        onBackClick = onBackClick,
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
                Text(
                    text = optionDescription ?: stringResource(id = R.string.streaming_service_setup_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (requiresServerUrl) {
                item {
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(id = R.string.streaming_service_setup_server_url)) },
                        placeholder = { Text(text = stringResource(id = R.string.streaming_service_setup_server_url_hint)) },
                        singleLine = true,
                        enabled = !isLoading
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.streaming_service_setup_username)) },
                    placeholder = { Text(text = stringResource(id = R.string.streaming_service_setup_username_hint)) },
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.streaming_service_setup_password)) },
                    placeholder = { Text(text = stringResource(id = R.string.streaming_service_setup_password_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            item {
                Text(
                    text = if (session.isConnected) {
                        stringResource(id = R.string.streaming_status_connected)
                    } else {
                        stringResource(id = R.string.streaming_status_not_connected)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (session.isConnected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (session.isConnected && session.username.isNotBlank()) {
                item {
                    Text(
                        text = stringResource(
                            id = R.string.streaming_service_setup_connected_as,
                            session.username
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (error != null) {
                item {
                    Text(
                        text = error.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.connectService(
                                serviceId = serviceId,
                                serverUrl = serverUrl,
                                username = username,
                                password = password
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = if (session.isConnected) {
                                stringResource(id = R.string.streaming_service_setup_reconnect)
                            } else {
                                stringResource(id = R.string.streaming_connect)
                            }
                        )
                    }

                    if (session.isConnected) {
                        OutlinedButton(
                            onClick = { viewModel.disconnectService(serviceId) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            Text(text = stringResource(id = R.string.streaming_service_setup_disconnect))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}
