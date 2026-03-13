package chromahub.rhythm.app.infrastructure.widget.glance

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import chromahub.rhythm.app.infrastructure.service.MediaPlaybackService
import kotlinx.coroutines.delay

/**
 * Play/Pause action callback for widget
 */
class PlayPauseAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val intent = Intent(context, MediaPlaybackService::class.java).apply {
                action = MediaPlaybackService.ACTION_PLAY_PAUSE
            }
            context.startService(intent)
        } catch (e: Exception) {
            android.util.Log.w("WidgetAction", "Cannot start service for play/pause", e)
        }
        
        // Trigger immediate widget update after short delay for state change
        delay(100)
        try { RhythmMusicWidget().updateAll(context) } catch (_: Exception) {}
    }
}

/**
 * Skip to next track action callback for widget
 */
class SkipNextAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val intent = Intent(context, MediaPlaybackService::class.java).apply {
                action = MediaPlaybackService.ACTION_SKIP_NEXT
            }
            context.startService(intent)
        } catch (e: Exception) {
            android.util.Log.w("WidgetAction", "Cannot start service for skip next", e)
        }
        
        // Trigger immediate widget update after short delay for state change
        delay(100)
        try { RhythmMusicWidget().updateAll(context) } catch (_: Exception) {}
    }
}

/**
 * Skip to previous track action callback for widget
 */
class SkipPreviousAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val intent = Intent(context, MediaPlaybackService::class.java).apply {
                action = MediaPlaybackService.ACTION_SKIP_PREVIOUS
            }
            context.startService(intent)
        } catch (e: Exception) {
            android.util.Log.w("WidgetAction", "Cannot start service for skip previous", e)
        }
        
        // Trigger immediate widget update after short delay for state change
        delay(100)
        try { RhythmMusicWidget().updateAll(context) } catch (_: Exception) {}
    }
}

/**
 * Toggle favorite action callback for widget
 */
class ToggleFavoriteAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            // Send action to MediaPlaybackService to toggle favorite
            val intent = Intent(context, MediaPlaybackService::class.java).apply {
                action = MediaPlaybackService.ACTION_TOGGLE_FAVORITE
            }
            context.startService(intent)
        } catch (e: Exception) {
            android.util.Log.w("WidgetAction", "Cannot start service for toggle favorite", e)
        }
        
        // Trigger immediate widget update after short delay for state change
        delay(200)
        try { RhythmMusicWidget().updateAll(context) } catch (_: Exception) {}
    }
}
