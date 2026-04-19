package chromahub.rhythm.app.features.streaming.domain.model

object StreamingServiceId {
    const val SUBSONIC = "SUBSONIC"
    const val JELLYFIN = "JELLYFIN"
    const val NETEASE_CLOUD_MUSIC = "NETEASE_CLOUD_MUSIC"
    const val QQ_MUSIC = "QQ_MUSIC"

    val all = listOf(
        SUBSONIC,
        JELLYFIN,
        NETEASE_CLOUD_MUSIC,
        QQ_MUSIC
    )
}

object StreamingServiceRules {
    fun requiresServerUrl(serviceId: String): Boolean {
        return serviceId == StreamingServiceId.SUBSONIC || serviceId == StreamingServiceId.JELLYFIN
    }
}