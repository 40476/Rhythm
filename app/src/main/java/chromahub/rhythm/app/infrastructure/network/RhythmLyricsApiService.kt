package chromahub.rhythm.app.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Service interface for Rhythm lyrics API
 * API Documentation: https://paxsenix.alwaysdata.net/
 */
interface RhythmLyricsApiService {
    /**
     * Search for songs by query string
     * @param query Search query (artist + song name)
     * @return List of matching songs
     */
    @GET("searchAppleMusic.php")
    suspend fun searchSongs(
        @Query("q") query: String
    ): List<RhythmLyricsSearchResult>
    
    /**
     * Get word-by-word synchronized lyrics for a specific song
     * @param id Lyrics source song ID
     * @return Lyrics response with word-level timing
     */
    @GET("getAppleMusicLyrics.php")
    suspend fun getLyrics(
        @Query("id") id: String
    ): RhythmLyricsResponse
}
