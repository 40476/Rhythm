package chromahub.rhythm.app.features.streaming.presentation.model

import androidx.annotation.StringRes
import chromahub.rhythm.app.R
import chromahub.rhythm.app.features.streaming.domain.model.StreamingServiceId

data class StreamingServiceOption(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int
)

object StreamingServiceOptions {
    const val SUBSONIC = StreamingServiceId.SUBSONIC
    const val JELLYFIN = StreamingServiceId.JELLYFIN
    const val NETEASE_CLOUD_MUSIC = StreamingServiceId.NETEASE_CLOUD_MUSIC
    const val QQ_MUSIC = StreamingServiceId.QQ_MUSIC

    val defaults: List<StreamingServiceOption> = listOf(
        StreamingServiceOption(
            id = SUBSONIC,
            nameRes = R.string.streaming_service_subsonic,
            descriptionRes = R.string.streaming_service_subsonic_desc
        ),
        StreamingServiceOption(
            id = JELLYFIN,
            nameRes = R.string.streaming_service_jellyfin,
            descriptionRes = R.string.streaming_service_jellyfin_desc
        ),
        StreamingServiceOption(
            id = NETEASE_CLOUD_MUSIC,
            nameRes = R.string.streaming_service_netease_cloud_music,
            descriptionRes = R.string.streaming_service_netease_cloud_music_desc
        ),
        StreamingServiceOption(
            id = QQ_MUSIC,
            nameRes = R.string.streaming_service_qq_music,
            descriptionRes = R.string.streaming_service_qq_music_desc
        )
    )
}