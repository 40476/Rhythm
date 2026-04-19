package chromahub.rhythm.app.features.streaming.data.provider

/**
 * Lightweight song model used by provider API clients before mapping to UI/domain models.
 */
data class ProviderSong(
    val providerId: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val artworkUrl: String? = null
)

/**
 * Result of a successful provider connection/authentication.
 */
data class ProviderConnectionResult(
    val displayName: String,
    val serverUrl: String = ""
)
