package chromahub.rhythm.app.shared.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Objects

@Parcelize
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: String = "",
    val duration: Long,
    val uri: Uri,
    val artworkUri: Uri? = null,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val genre: String? = null,
    val dateAdded: Long = System.currentTimeMillis(), // New field for date added
    val albumArtist: String? = null, // Album artist for grouping
    // Audio quality metadata
    val bitrate: Int? = null, // Bitrate in bps
    val sampleRate: Int? = null, // Sample rate in Hz
    val channels: Int? = null, // Number of audio channels (1=mono, 2=stereo, 6=5.1, etc.)
    val codec: String? = null, // Audio codec (AAC, MP3, FLAC, etc.)
    val discNumber: Int = 1 // Multi-disc support
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (albumId != other.albumId) return false
        if (duration != other.duration) return false
        if (uri != other.uri) return false
        if (artworkUri != other.artworkUri) return false
        if (trackNumber != other.trackNumber) return false
        if (year != other.year) return false
        if (!Objects.equals(genre, other.genre)) return false
        if (dateAdded != other.dateAdded) return false
        if (!Objects.equals(albumArtist, other.albumArtist)) return false
        if (!Objects.equals(bitrate, other.bitrate)) return false
        if (!Objects.equals(sampleRate, other.sampleRate)) return false
        if (!Objects.equals(channels, other.channels)) return false
        if (!Objects.equals(codec, other.codec)) return false
        if (discNumber != other.discNumber) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(
            id,
            title,
            artist,
            album,
            albumId,
            duration,
            uri,
            artworkUri,
            trackNumber,
            year,
            genre,
            dateAdded,
            albumArtist,
            bitrate,
            sampleRate,
            channels,
            codec,
            discNumber
        )
    }
}

@Parcelize
data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artworkUri: Uri? = null,
    val year: Int = 0,
    val songs: List<Song> = emptyList(),
    val numberOfSongs: Int = 0,
    val dateModified: Long = System.currentTimeMillis()
) : Parcelable
@Parcelize
data class Artist(
    val id: String,
    val name: String,
    val artworkUri: Uri? = null,
    val albums: List<Album> = emptyList(),
    val songs: List<Song> = emptyList(),
    val numberOfAlbums: Int = 0,
    val numberOfTracks: Int = 0
) : Parcelable

@Parcelize
data class Playlist(
    val id: String,
    val name: String,
    val songs: List<Song> = emptyList(),
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val artworkUri: Uri? = null
) : Parcelable {
    val isDefault: Boolean
        get() = id == "1" || id == "2" || id == "3"  // Favorites, Recently Added, Most Played
}

// Represents the current playback queue
data class Queue(
    val songs: List<Song>,
    val currentIndex: Int = 0
)

// Represents a location in the app (for the "Living room" feature)
data class PlaybackLocation(
    val id: String,
    val name: String,
    val icon: Int
)
