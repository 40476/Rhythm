package chromahub.rhythm.app.infrastructure.audio

import android.content.Context
import android.media.AudioFormat
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.common.AudioAttributes
import androidx.media3.common.audio.AudioProcessor

/**
 * Factory for creating AudioSink instances configured for bit-perfect playback.
 * 
 * Bit-perfect playback means outputting audio at its native sample rate without resampling.
 * Android normally resamples all audio to 48kHz, but this factory configures AudioSink to use
 * the track's original sample rate (e.g., 44.1kHz for CD quality, 96kHz for Hi-Res).
 * 
 * This prevents quality loss from unnecessary resampling and provides the best possible audio quality.
 */
@OptIn(UnstableApi::class)
object BitPerfectAudioSink {
    
    private const val TAG = "BitPerfectAudioSink"
    
    // Rhythm audio processors
    private var rhythmBassBoostProcessor: RhythmBassBoostProcessor? = null
    private var rhythmSpatializationProcessor: RhythmSpatializationProcessor? = null
    
    
    /**
     * Create AudioSink with optional bit-perfect configuration and Rhythm audio effects.
     *
     * Bit-perfect mode:
     * - Enables float output so ExoPlayer preserves the native sample format
     *   instead of down-converting everything to 16-bit PCM.
     * - Strips ALL audio processors (bass boost, spatialization, etc.) so the
     *   decoded stream reaches the AudioTrack unmodified.
     *
     * NOTE: True bit-perfect playback on Android also requires bypassing the
     * platform audio mixer, which needs MIXER_BEHAVIOR_BIT_PERFECT (Android 14+)
     * or vendor-specific direct AudioTrack paths. This factory handles the
     * ExoPlayer side; the mixer bypass is a platform-level concern.
     */
    fun create(
        context: Context, 
        enableBitPerfect: Boolean,
        bassBoostProcessor: RhythmBassBoostProcessor? = null,
        spatializationProcessor: RhythmSpatializationProcessor? = null
    ): AudioSink {
        Log.d(TAG, "Creating AudioSink (bit-perfect: $enableBitPerfect, Rhythm effects: ${bassBoostProcessor != null || spatializationProcessor != null})")
        
        // Store processor references (available for later queries even if bit-perfect skips them)
        rhythmBassBoostProcessor = bassBoostProcessor
        rhythmSpatializationProcessor = spatializationProcessor
        
        val builder = DefaultAudioSink.Builder(context)
            .setEnableAudioTrackPlaybackParams(true)
        
        if (enableBitPerfect) {
            // Bit-perfect: preserve the native sample format (don't force 16-bit).
            // Float output lets ExoPlayer pass high-res PCM through without truncation.
            builder.setEnableFloatOutput(true)
            
            // No audio processors – the stream must reach the sink unmodified.
            Log.d(TAG, "Bit-perfect mode: float output enabled, no audio processors")
        } else {
            // Normal mode: 16-bit output is fine, add Rhythm DSP processors.
            builder.setEnableFloatOutput(false)
            
            val processors = mutableListOf<AudioProcessor>()
            
            if (bassBoostProcessor != null) {
                processors.add(bassBoostProcessor)
                Log.d(TAG, "Added Rhythm bass boost processor (enabled: ${bassBoostProcessor.isEnabled()})")
            }
            
            if (spatializationProcessor != null) {
                processors.add(spatializationProcessor)
                Log.d(TAG, "Added Rhythm spatialization processor (enabled: ${spatializationProcessor.isEnabled()})")
            }
            
            if (processors.isNotEmpty()) {
                Log.d(TAG, "Configuring audio processor chain with ${processors.size} Rhythm processors")
                builder.setAudioProcessorChain(
                    DefaultAudioSink.DefaultAudioProcessorChain(
                        *processors.toTypedArray()
                    )
                )
            }
        }
        
        return builder.build()
    }
    
    /**
     * Get the current bass boost processor
     */
    fun getBassBoostProcessor(): RhythmBassBoostProcessor? = rhythmBassBoostProcessor
    
    /**
     * Get the current spatialization processor
     */
    fun getSpatializationProcessor(): RhythmSpatializationProcessor? = rhythmSpatializationProcessor
    
    /**
     * Check if the device supports the requested sample rate
     */
    fun isSampleRateSupported(sampleRate: Int): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Before Android M, limited sample rate support
            return sampleRate in listOf(44100, 48000)
        }
        
        // Android M and above support a wider range
        val supported = sampleRate in listOf(44100, 48000, 88200, 96000, 176400, 192000)
        Log.d(TAG, "Sample rate ${sampleRate}Hz supported: $supported")
        return supported
    }
    
    /**
     * Log the current playback format for debugging
     */
    fun logPlaybackFormat(format: Format) {
        val sampleRate = if (format.sampleRate != Format.NO_VALUE) format.sampleRate else "unknown"
        val channels = if (format.channelCount != Format.NO_VALUE) format.channelCount else "unknown"
        val bitDepth = when (format.pcmEncoding) {
            C.ENCODING_PCM_8BIT -> "8-bit"
            C.ENCODING_PCM_16BIT -> "16-bit"
            C.ENCODING_PCM_24BIT -> "24-bit"
            C.ENCODING_PCM_32BIT -> "32-bit"
            C.ENCODING_PCM_FLOAT -> "32-bit float"
            else -> "unknown"
        }
        
        Log.i(TAG, "=== Bit-Perfect Playback ===")
        Log.i(TAG, "Sample Rate: ${sampleRate}Hz")
        Log.i(TAG, "Channels: $channels")
        Log.i(TAG, "Bit Depth: $bitDepth")
        Log.i(TAG, "Codec: ${format.sampleMimeType ?: "unknown"}")
        Log.i(TAG, "==========================")
    }
    
    /**
     * Get channel mask for the specified channel count
     */
    fun getChannelMask(channelCount: Int): Int {
        return when (channelCount) {
            1 -> AudioFormat.CHANNEL_OUT_MONO
            2 -> AudioFormat.CHANNEL_OUT_STEREO
            6 -> AudioFormat.CHANNEL_OUT_5POINT1
            8 -> AudioFormat.CHANNEL_OUT_7POINT1_SURROUND
            else -> AudioFormat.CHANNEL_OUT_STEREO
        }
    }
}
