package chromahub.rhythm.app.infrastructure.widget.glance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import chromahub.rhythm.app.activities.MainActivity
import chromahub.rhythm.app.R
import androidx.glance.appwidget.ImageProvider as AppWidgetImageProvider

/**
 * Modern Glance-based Music Widget with Material 3 Expressive Design
 * 
 * Features:
 * - SizeMode.Exact for pixel-perfect responsive layouts
 * - Material 3 Expressive theming with dynamic colors
 * - Dynamic play/pause button corner radius (rounded when paused, more square when playing)
 * - Adaptive layouts for every widget size (1x1 to 5x5+)
 * - Album art with LruCache bitmap management
 * - Expressive rounded corners and spacing
 */
class RhythmMusicWidget : GlanceAppWidget() {
    
    companion object {
        // Widget state keys
        const val KEY_SONG_TITLE = "song_title"
        const val KEY_ARTIST_NAME = "artist_name"
        const val KEY_ALBUM_NAME = "album_name"
        const val KEY_IS_PLAYING = "is_playing"
        const val KEY_ARTWORK_URI = "artwork_uri"
        const val KEY_HAS_PREVIOUS = "has_previous"
        const val KEY_HAS_NEXT = "has_next"
        const val KEY_IS_FAVORITE = "is_favorite"

        // Layout size breakpoints for responsive widget sizing
        private val VERY_THIN_SIZE = DpSize(200.dp, 60.dp)
        private val THIN_SIZE = DpSize(250.dp, 80.dp)
        private val SMALL_HORIZONTAL_SIZE = DpSize(110.dp, 60.dp)
        private val ONE_BY_ONE_SIZE = DpSize(110.dp, 110.dp)
        private val GABE_SIZE = DpSize(110.dp, 220.dp)
        private val GABE_TWO_HEIGHT_SIZE = DpSize(110.dp, 200.dp)
        private val SMALL_SIZE = DpSize(120.dp, 100.dp)
        private val MEDIUM_SIZE = DpSize(250.dp, 150.dp)
        private val LARGE_SIZE = DpSize(300.dp, 180.dp)
        private val EXTRA_LARGE_SIZE = DpSize(300.dp, 220.dp)
        private val EXTRA_LARGE_PLUS_SIZE = DpSize(350.dp, 260.dp)
        private val HUGE_SIZE = DpSize(400.dp, 300.dp)

        // LruCache for album art bitmaps
        private object AlbumArtCache {
            private const val CACHE_SIZE = 4 * 1024 * 1024 // 4 MiB
            private val cache = object : LruCache<String, Bitmap>(CACHE_SIZE) {
                override fun sizeOf(key: String, value: Bitmap) = value.byteCount
            }
            fun get(key: String): Bitmap? = cache.get(key)
            fun put(key: String, bitmap: Bitmap) { if (get(key) == null) cache.put(key, bitmap) }
            fun keyFor(data: ByteArray): String = data.contentHashCode().toString()
        }
    }
    
    // Use preferences-based state definition for reactive updates
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    
    // Exact size mode for pixel-perfect responsive layouts
    override val sizeMode = SizeMode.Exact
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appSettings = try {
            chromahub.rhythm.app.shared.data.model.AppSettings.getInstance(context)
        } catch (e: Exception) {
            null
        }
        
        provideContent {
            val prefs = currentState<Preferences>()
            val widgetData = try {
                getWidgetData(prefs, appSettings)
            } catch (e: Exception) {
                android.util.Log.e("RhythmMusicWidget", "Widget data error, using fallback", e)
                WidgetData(
                    songTitle = "Rhythm",
                    artistName = "",
                    albumName = "",
                    isPlaying = false,
                    artworkUri = null,
                    hasPrevious = false,
                    hasNext = false,
                    isFavorite = false
                )
            }
            val currentSize = LocalSize.current
            GlanceTheme {
                WidgetUi(widgetData, currentSize)
            }
        }
    }
    
    @Composable
    private fun WidgetUi(data: WidgetData, size: DpSize) {
        val aspectRatio = size.height.value / size.width.value
        val minWidth = size.width.value.toInt()
        val minHeight = size.height.value.toInt()
        
        val baseModifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionStartActivity<MainActivity>())
        
        Box(GlanceModifier.fillMaxSize()) {
            when {
                // 5x5+ (350+ width x 350+ height): Premium largest size
                minWidth >= 350 && minHeight >= 350 -> HugeWidgetLayout(baseModifier, data)
                
                // 5x4 or 4x5 (350+ width x 280+ height): Tall wide layout
                minWidth >= 350 && minHeight >= 280 -> ExtraLargeWidgetLayout(baseModifier, data)
                
                // 4x4+ (280+ width x 280+ height): Extra large square
                minWidth >= 280 && minHeight >= 280 -> ExtraLargeWidgetLayout(baseModifier, data)
                
                // 5x3 or 4x3 (300+ width x 210+ height): Large horizontal
                minWidth >= 300 && minHeight >= 210 -> LargeWidgetLayout(baseModifier, data)
                
                // 3x4 or 4x3 (210+ width x 300+ height): Large vertical
                minWidth >= 210 && minHeight >= 300 -> LargeWidgetLayout(baseModifier, data)
                
                // 3x3 (210+ width x 210+ height): Large square
                minWidth >= 210 && minHeight >= 210 -> LargeWidgetLayout(baseModifier, data)
                
                // 5x2 or 4x2 (320+ width x 140+ height): Wide horizontal
                minWidth >= 320 && minHeight < 210 -> WideLayout(baseModifier, data)
                
                // 3x2 (180-320dp width x 100-210dp height): Medium horizontal
                minWidth >= 180 && minHeight >= 100 && minHeight < 210 && aspectRatio < 1.2f -> MediumWidgetLayout(baseModifier, data)
                
                // 2x3 (100-180dp width x 210+ height): Medium vertical
                minWidth >= 100 && minWidth < 180 && minHeight >= 210 && aspectRatio > 1.5f -> VerticalLayout(baseModifier, data)
                
                // 2x2 (100-180dp width x 100-210dp height): Small square
                minWidth >= 100 && minWidth < 180 && minHeight >= 100 -> SmallWidgetLayout(baseModifier, data)
                
                // 3x1 or 2x1 (110-320dp width x 40-100dp height): Extra small horizontal strip
                minHeight < 100 -> SmallHorizontalLayout(baseModifier, data)
                
                // Default fallback
                else -> MediumWidgetLayout(baseModifier, data)
            }
        }
    }
    
    // ==================== 1x1 Layout: Play/Pause only ====================
    @Composable
    private fun OneByOneLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(60.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            PlayPauseButton(
                modifier = GlanceModifier.fillMaxSize(),
                isPlaying = data.isPlaying,
                iconSize = 36.dp,
                cornerRadius = 30.dp
            )
        }
    }
    
    // ==================== Gabe Two Height Layout: Art + Buttons vertical ====================
    @Composable
    private fun GabeTwoHeightLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(60.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                AlbumArtImage(
                    modifier = GlanceModifier.defaultWeight().height(48.dp),
                    artworkUri = data.artworkUri,
                    cornerRadius = 64.dp
                )
                Spacer(GlanceModifier.height(14.dp))
                Column(
                    modifier = GlanceModifier.defaultWeight().cornerRadius(60.dp)
                ) {
                    PlayPauseButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                        isPlaying = data.isPlaying,
                        iconSize = 26.dp
                    )
                    Spacer(GlanceModifier.height(10.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                        iconSize = 26.dp
                    )
                }
            }
        }
    }
    
    // ==================== Gabe Layout: Art + Prev/Play/Next vertical ====================
    @Composable
    private fun GabeLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(60.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                AlbumArtImage(
                    modifier = GlanceModifier.defaultWeight().fillMaxWidth().height(48.dp),
                    artworkUri = data.artworkUri,
                    cornerRadius = 64.dp
                )
                Spacer(GlanceModifier.height(14.dp))
                Column(modifier = GlanceModifier.defaultWeight().cornerRadius(60.dp)) {
                    PreviousButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                        iconSize = 26.dp
                    )
                    Spacer(GlanceModifier.height(10.dp))
                    PlayPauseButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                        isPlaying = data.isPlaying,
                        iconSize = 26.dp
                    )
                    Spacer(GlanceModifier.height(10.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                        iconSize = 26.dp
                    )
                }
            }
        }
    }
    
    // ==================== Vertical Layout: Tall widget with centered content ====================
    @Composable
    private fun VerticalLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val subtextColor = GlanceTheme.colors.onSurfaceVariant
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(32.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                // Album Art
                AlbumArtImage(
                    modifier = GlanceModifier.size(120.dp),
                    artworkUri = data.artworkUri,
                    cornerRadius = 20.dp
                )
                
                Spacer(GlanceModifier.height(16.dp))
                
                // Song Info
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.songTitle,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        ),
                        maxLines = 2
                    )
                    
                    if (data.showArtist && data.artistName.isNotEmpty()) {
                        Spacer(GlanceModifier.height(4.dp))
                        Text(
                            text = data.artistName,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = subtextColor
                            ),
                            maxLines = 1
                        )
                    }
                    
                    if (data.showAlbum && data.albumName.isNotEmpty()) {
                        Spacer(GlanceModifier.height(2.dp))
                        Text(
                            text = data.albumName,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = subtextColor
                            ),
                            maxLines = 1
                        )
                    }
                }
                
                Spacer(GlanceModifier.height(20.dp))
                
                // Control buttons
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.fillMaxWidth().height(48.dp),
                        iconSize = 24.dp,
                        cornerRadius = 24.dp
                    )
                    
                    Spacer(GlanceModifier.height(8.dp))
                    
                    PlayPauseButton(
                        modifier = GlanceModifier.fillMaxWidth().height(56.dp),
                        isPlaying = data.isPlaying,
                        iconSize = 28.dp,
                        cornerRadius = if (data.isPlaying) 16.dp else 28.dp
                    )
                    
                    Spacer(GlanceModifier.height(8.dp))
                    
                    NextButton(
                        modifier = GlanceModifier.fillMaxWidth().height(48.dp),
                        iconSize = 24.dp,
                        cornerRadius = 24.dp
                    )
                }
            }
        }
    }
    
    // ==================== Wide Layout: Very wide horizontal strip ====================
    @Composable
    private fun WideLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val subtextColor = GlanceTheme.colors.onSurfaceVariant
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(32.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art
                AlbumArtImage(
                    modifier = GlanceModifier.size(64.dp),
                    artworkUri = data.artworkUri,
                    cornerRadius = 16.dp
                )
                
                Spacer(GlanceModifier.width(16.dp))
                
                // Song Info
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = data.songTitle,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        ),
                        maxLines = 1
                    )
                    
                    if (data.showArtist && data.artistName.isNotEmpty()) {
                        Spacer(GlanceModifier.height(2.dp))
                        Text(
                            text = data.artistName,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = subtextColor
                            ),
                            maxLines = 1
                        )
                    }
                }
                
                Spacer(GlanceModifier.width(16.dp))
                
                // Control buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.size(48.dp),
                        iconSize = 24.dp,
                        cornerRadius = 24.dp
                    )
                    
                    Spacer(GlanceModifier.width(8.dp))
                    
                    PlayPauseButton(
                        modifier = GlanceModifier.size(56.dp),
                        isPlaying = data.isPlaying,
                        iconSize = 28.dp,
                        cornerRadius = if (data.isPlaying) 16.dp else 28.dp
                    )
                    
                    Spacer(GlanceModifier.width(8.dp))
                    
                    NextButton(
                        modifier = GlanceModifier.size(48.dp),
                        iconSize = 24.dp,
                        cornerRadius = 24.dp
                    )
                }
            }
        }
    }
    
    // ==================== Small Horizontal: Art + Play (strip) ====================
    @Composable
    private fun SmallHorizontalLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(60.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize().cornerRadius(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                AlbumArtImage(
                    modifier = GlanceModifier.padding(vertical = 6.dp),
                    artworkUri = data.artworkUri,
                    size = 48.dp,
                    cornerRadius = 64.dp
                )
                Spacer(GlanceModifier.width(14.dp))
                PlayPauseButton(
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                    isPlaying = data.isPlaying,
                    iconSize = 26.dp
                )
            }
        }
    }
    
    // ==================== Very Thin: Art + Title + Play + Next ====================
    @Composable
    private fun VeryThinLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val size = LocalSize.current
        val albumArtSize = size.height - 32.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(60.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize().cornerRadius(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                AlbumArtImage(
                    modifier = GlanceModifier.size(albumArtSize),
                    artworkUri = data.artworkUri,
                    cornerRadius = 60.dp
                )
                Spacer(GlanceModifier.width(10.dp))
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = data.songTitle,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor),
                        maxLines = 1
                    )
                    if (data.showArtist && data.artistName.isNotEmpty()) {
                        Text(
                            text = data.artistName,
                            style = TextStyle(fontSize = 14.sp, color = textColor),
                            maxLines = 1
                        )
                    }
                }
                Spacer(GlanceModifier.width(8.dp))
                PlayPauseButton(
                    modifier = GlanceModifier.defaultWeight().size(48.dp).fillMaxHeight(),
                    isPlaying = data.isPlaying,
                    iconSize = 26.dp
                )
                Spacer(GlanceModifier.width(10.dp))
                NextButton(
                    modifier = GlanceModifier.defaultWeight().size(48.dp).fillMaxHeight(),
                    iconSize = 26.dp
                )
            }
        }
    }
    
    // ==================== Thin: Full strip with art, info, buttons ====================
    @Composable
    private fun ThinLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val size = LocalSize.current
        val albumArtSize = size.height - 32.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(60.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize().cornerRadius(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                AlbumArtImage(
                    modifier = GlanceModifier.size(albumArtSize),
                    artworkUri = data.artworkUri,
                    cornerRadius = 60.dp
                )
                Spacer(GlanceModifier.width(14.dp))
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = data.songTitle,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor),
                        maxLines = 1
                    )
                    if (data.showArtist && data.artistName.isNotEmpty()) {
                        Text(
                            text = data.artistName,
                            style = TextStyle(fontSize = 14.sp, color = textColor),
                            maxLines = 1
                        )
                    }
                }
                Spacer(GlanceModifier.width(8.dp))
                PlayPauseButton(
                    modifier = GlanceModifier.defaultWeight().size(48.dp).fillMaxHeight(),
                    isPlaying = data.isPlaying,
                    iconSize = 26.dp
                )
                Spacer(GlanceModifier.width(10.dp))
                NextButton(
                    modifier = GlanceModifier.defaultWeight().size(48.dp).fillMaxHeight(),
                    iconSize = 26.dp
                )
            }
        }
    }
    
    // ==================== Small Widget: Art + Play + Prev/Next ====================
    @Composable
    private fun SmallWidgetLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val playButtonCornerRadius = if (data.isPlaying) 20.dp else 60.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(32.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Album Art with shadow effect
                Box(
                    modifier = GlanceModifier.defaultWeight().fillMaxWidth()
                ) {
                    AlbumArtImage(
                        modifier = GlanceModifier.fillMaxSize(),
                        artworkUri = data.artworkUri,
                        cornerRadius = 20.dp
                    )
                }
                Spacer(GlanceModifier.height(12.dp))
                // Play/Pause button with fixed height
                PlayPauseButton(
                    modifier = GlanceModifier.fillMaxWidth().height(52.dp),
                    isPlaying = data.isPlaying,
                    iconSize = 28.dp,
                    cornerRadius = playButtonCornerRadius
                )
                Spacer(GlanceModifier.height(10.dp))
                // Previous and Next buttons row with fixed height
                Row(
                    modifier = GlanceModifier.fillMaxWidth().height(52.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 28.dp,
                        cornerRadius = 26.dp
                    )
                    Spacer(GlanceModifier.width(10.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 28.dp,
                        cornerRadius = 26.dp
                    )
                }
            }
        }
    }
    
    // ==================== Medium Widget: Art + Info + Controls row ====================
    @Composable
    private fun MediumWidgetLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val subtextColor = GlanceTheme.colors.onSurfaceVariant
        val playButtonCornerRadius = if (data.isPlaying) 22.dp else 60.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(32.dp)
                .padding(18.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                // Top: Album Art + Title/Artist with improved spacing
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AlbumArtImage(
                        artworkUri = data.artworkUri,
                        size = 84.dp,
                        cornerRadius = 18.dp
                    )
                    Spacer(GlanceModifier.width(14.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = data.songTitle,
                            style = TextStyle(
                                fontSize = 17.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = textColor
                            ),
                            maxLines = 2
                        )
                        Spacer(GlanceModifier.height(6.dp))
                        if (data.showArtist && data.artistName.isNotEmpty()) {
                            Text(
                                text = data.artistName,
                                style = TextStyle(
                                    fontSize = 14.sp, 
                                    color = subtextColor
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
                
                Spacer(GlanceModifier.height(16.dp))
                
                // Bottom: Control buttons row with fixed height
                Row(
                    modifier = GlanceModifier.fillMaxWidth().height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 26.dp,
                        cornerRadius = 26.dp
                    )
                    Spacer(GlanceModifier.width(10.dp))
                    PlayPauseButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        isPlaying = data.isPlaying,
                        iconSize = 28.dp,
                        cornerRadius = playButtonCornerRadius
                    )
                    Spacer(GlanceModifier.width(10.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 26.dp,
                        cornerRadius = 26.dp
                    )
                }
            }
        }
    }
    
    // ==================== Large Widget: Art row + info + full controls ====================
    @Composable
    private fun LargeWidgetLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val subtextColor = GlanceTheme.colors.onSurfaceVariant
        val accentColor = GlanceTheme.colors.tertiary
        val playButtonCornerRadius = if (data.isPlaying) 24.dp else 60.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(32.dp)
                .padding(20.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header row with album art and info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    AlbumArtImage(
                        artworkUri = data.artworkUri,
                        size = 72.dp,
                        cornerRadius = 20.dp
                    )
                    Spacer(GlanceModifier.width(16.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = data.songTitle,
                            style = TextStyle(
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = textColor
                            ),
                            maxLines = 2
                        )
                        Spacer(GlanceModifier.height(6.dp))
                        if (data.showArtist && data.artistName.isNotEmpty()) {
                            Text(
                                text = data.artistName,
                                style = TextStyle(
                                    fontSize = 15.sp, 
                                    color = subtextColor
                                ),
                                maxLines = 1
                            )
                        }
                    }
                    if (data.showFavoriteButton) {
                        Spacer(GlanceModifier.width(8.dp))
                        FavoriteButton(
                            isFavorite = data.isFavorite,
                            iconColor = if (data.isFavorite) accentColor else textColor
                        )
                        Spacer(GlanceModifier.width(4.dp))
                    }
                }
                
                Spacer(GlanceModifier.height(18.dp))
                
                // Controls row with fixed height to prevent stretching
                Row(
                    modifier = GlanceModifier.fillMaxWidth().height(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 28.dp,
                        cornerRadius = 28.dp
                    )
                    Spacer(GlanceModifier.width(12.dp))
                    PlayPauseButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        isPlaying = data.isPlaying,
                        iconSize = 30.dp,
                        cornerRadius = playButtonCornerRadius
                    )
                    Spacer(GlanceModifier.width(12.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 28.dp,
                        cornerRadius = 28.dp
                    )
                }
            }
        }
    }
    
    // ==================== Extra Large Widget: Full art + info + big controls ====================
    @Composable
    private fun ExtraLargeWidgetLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val subtextColor = GlanceTheme.colors.onSurfaceVariant
        val separatorColor = GlanceTheme.colors.outline
        val playButtonCornerRadius = if (data.isPlaying) 24.dp else 60.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(32.dp)
                .padding(20.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top: Premium Album Art & Info section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    AlbumArtImage(
                        artworkUri = data.artworkUri,
                        size = 76.dp,
                        cornerRadius = 20.dp
                    )
                    Spacer(GlanceModifier.width(18.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = data.songTitle,
                            style = TextStyle(
                                fontSize = 21.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = textColor
                            ),
                            maxLines = 2
                        )
                        Spacer(GlanceModifier.height(8.dp))
                        if (data.showArtist && data.artistName.isNotEmpty()) {
                            Text(
                                text = data.artistName,
                                style = TextStyle(
                                    fontSize = 17.sp, 
                                    color = subtextColor
                                ),
                                maxLines = 1
                            )
                        }
                    }
                    if (data.showFavoriteButton) {
                        Spacer(GlanceModifier.width(8.dp))
                        FavoriteButton(
                            isFavorite = data.isFavorite,
                            iconColor = if (data.isFavorite) GlanceTheme.colors.tertiary else textColor
                        )
                        Spacer(GlanceModifier.width(4.dp))
                    }
                }
                
                Spacer(GlanceModifier.height(18.dp))
                
                // Control buttons with fixed height to prevent excessive stretching
                Row(
                    modifier = GlanceModifier.fillMaxWidth().height(60.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 30.dp,
                        cornerRadius = 30.dp
                    )
                    Spacer(GlanceModifier.width(12.dp))
                    PlayPauseButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        isPlaying = data.isPlaying,
                        iconSize = 32.dp,
                        cornerRadius = playButtonCornerRadius
                    )
                    Spacer(GlanceModifier.width(12.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 30.dp,
                        cornerRadius = 30.dp
                    )
                }
                
                // Flexible spacer that grows as needed
                Spacer(GlanceModifier.defaultWeight())
                
                // Elegant separator line
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .background(separatorColor)
                        .height(1.dp)
                        .cornerRadius(60.dp)
                ) {}
                
                Spacer(GlanceModifier.height(14.dp))
                
                // Album info at bottom
                if (data.showAlbum && data.albumName.isNotEmpty()) {
                    Text(
                        text = data.albumName,
                        style = TextStyle(
                            fontSize = 15.sp, 
                            color = subtextColor
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
    
    // ==================== Extra Large Plus Widget: 5x3 widget (350x260) ====================
    @Composable
    private fun ExtraLargePlusWidgetLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val playButtonCornerRadius = if (data.isPlaying) 22.dp else 60.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(28.dp)
                .padding(18.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top: Large Album Art & Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    AlbumArtImage(
                        artworkUri = data.artworkUri,
                        size = 80.dp,
                        cornerRadius = 18.dp
                    )
                    Spacer(GlanceModifier.width(18.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = data.songTitle,
                            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor),
                            maxLines = 2
                        )
                        if (data.showArtist) {
                            Spacer(GlanceModifier.height(4.dp))
                            Text(
                                text = data.artistName,
                                style = TextStyle(fontSize = 17.sp, color = textColor),
                                maxLines = 1
                            )
                        }
                    }
                    if (data.showFavoriteButton) {
                        Spacer(GlanceModifier.width(8.dp))
                        FavoriteButton(
                            isFavorite = data.isFavorite,
                            iconColor = textColor
                        )
                    }
                }
                
                Spacer(GlanceModifier.height(16.dp))
                
                // Controls row with larger buttons
                Row(
                    modifier = GlanceModifier.defaultWeight().fillMaxWidth().height(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 30.dp
                    )
                    Spacer(GlanceModifier.width(12.dp))
                    PlayPauseButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        isPlaying = data.isPlaying,
                        iconSize = 34.dp,
                        cornerRadius = playButtonCornerRadius
                    )
                    Spacer(GlanceModifier.width(12.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 30.dp
                    )
                }
                
                Spacer(GlanceModifier.defaultWeight())
                
                // Separator line
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .background(GlanceTheme.colors.onSurfaceVariant)
                        .height(2.dp)
                        .cornerRadius(60.dp)
                ) {}
                
                Spacer(GlanceModifier.height(14.dp))
                
                // Album info at bottom
                if (data.showAlbum && data.albumName.isNotEmpty()) {
                    Text(
                        text = data.albumName,
                        style = TextStyle(fontSize = 15.sp, color = textColor),
                        maxLines = 1
                    )
                }
            }
        }
    }
    
    // ==================== Huge Widget: 5x5 widget (400x300) ====================
    @Composable
    private fun HugeWidgetLayout(modifier: GlanceModifier, data: WidgetData) {
        val bgColor = GlanceTheme.colors.surface
        val textColor = GlanceTheme.colors.onSurface
        val playButtonCornerRadius = if (data.isPlaying) 24.dp else 60.dp
        
        Box(
            modifier = modifier
                .background(bgColor)
                .cornerRadius(32.dp)
                .padding(20.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top: Extra Large Album Art & Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    AlbumArtImage(
                        artworkUri = data.artworkUri,
                        size = 96.dp,
                        cornerRadius = 20.dp
                    )
                    Spacer(GlanceModifier.width(20.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = data.songTitle,
                            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor),
                            maxLines = 2
                        )
                        if (data.showArtist) {
                            Spacer(GlanceModifier.height(6.dp))
                            Text(
                                text = data.artistName,
                                style = TextStyle(fontSize = 18.sp, color = textColor),
                                maxLines = 2
                            )
                        }
                    }
                    if (data.showFavoriteButton) {
                        Spacer(GlanceModifier.width(10.dp))
                        FavoriteButton(
                            isFavorite = data.isFavorite,
                            iconColor = textColor
                        )
                    }
                }
                
                Spacer(GlanceModifier.height(20.dp))
                
                // Large controls row
                Row(
                    modifier = GlanceModifier.defaultWeight().fillMaxWidth().height(72.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviousButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 32.dp
                    )
                    Spacer(GlanceModifier.width(14.dp))
                    PlayPauseButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        isPlaying = data.isPlaying,
                        iconSize = 38.dp,
                        cornerRadius = playButtonCornerRadius
                    )
                    Spacer(GlanceModifier.width(14.dp))
                    NextButton(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        iconSize = 32.dp
                    )
                }
                
                Spacer(GlanceModifier.defaultWeight())
                
                // Separator line
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                        .background(GlanceTheme.colors.onSurfaceVariant)
                        .height(2.dp)
                        .cornerRadius(60.dp)
                ) {}
                
                Spacer(GlanceModifier.height(16.dp))
                
                // Album info at bottom with larger text
                if (data.showAlbum && data.albumName.isNotEmpty()) {
                    Text(
                        text = data.albumName,
                        style = TextStyle(fontSize = 16.sp, color = textColor),
                        maxLines = 2
                    )
                }
            }
        }
    }
    
    // ==================== Shared UI Components ====================
    
    @Composable
    private fun AlbumArtImage(
        modifier: GlanceModifier = GlanceModifier,
        artworkUri: android.net.Uri?,
        size: Dp? = null,
        cornerRadius: Dp = 20.dp
    ) {
        val sizingModifier = if (size != null) modifier.size(size) else modifier
        val placeholderSizeDp = if (size != null) {
            val raw = size.value * 0.6f
            raw.coerceIn(28f, 96f).dp
        } else {
            43.dp // 72 * 0.6 = 43.2
        }
        
        Box(modifier = sizingModifier) {
            if (artworkUri != null) {
                Image(
                    provider = AppWidgetImageProvider(artworkUri),
                    contentDescription = "Album Art",
                    modifier = GlanceModifier.fillMaxSize().cornerRadius(cornerRadius),
                    contentScale = ContentScale.Crop
                )
            } else {
                AlbumArtPlaceholder(cornerRadius, placeholderSizeDp)
            }
        }
    }
    
    @Composable
    private fun AlbumArtPlaceholder(cornerRadius: Dp, placeholderSize: Dp) {
        // Enhanced placeholder with gradient-like effect
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(cornerRadius)
                .background(GlanceTheme.colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_music_note),
                contentDescription = "Album Art Placeholder",
                modifier = GlanceModifier.size(placeholderSize),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimaryContainer)
            )
        }
    }
    
    @Composable
    private fun PlayPauseButton(
        modifier: GlanceModifier = GlanceModifier,
        isPlaying: Boolean,
        iconColor: ColorProvider = GlanceTheme.colors.onPrimary,
        backgroundColor: ColorProvider = GlanceTheme.colors.primary,
        iconSize: Dp = 24.dp,
        cornerRadius: Dp = 24.dp
    ) {
        Box(
            modifier = modifier
                .background(backgroundColor)
                .cornerRadius(cornerRadius)
                .clickable(actionRunCallback<PlayPauseAction>()),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow),
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = GlanceModifier.size(iconSize),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
    }
    
    @Composable
    private fun NextButton(
        modifier: GlanceModifier = GlanceModifier,
        iconColor: ColorProvider = GlanceTheme.colors.onTertiary,
        backgroundColor: ColorProvider = GlanceTheme.colors.tertiary,
        iconSize: Dp = 24.dp,
        cornerRadius: Dp = 24.dp
    ) {
        Box(
            modifier = modifier
                .background(backgroundColor)
                .cornerRadius(cornerRadius)
                .clickable(actionRunCallback<SkipNextAction>()),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_skip_next),
                contentDescription = "Next",
                modifier = GlanceModifier.size(iconSize),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
    }
    
    @Composable
    private fun PreviousButton(
        modifier: GlanceModifier = GlanceModifier,
        iconColor: ColorProvider = GlanceTheme.colors.onTertiary,
        backgroundColor: ColorProvider = GlanceTheme.colors.tertiary,
        iconSize: Dp = 24.dp,
        cornerRadius: Dp = 24.dp
    ) {
        Box(
            modifier = modifier
                .background(backgroundColor)
                .cornerRadius(cornerRadius)
                .clickable(actionRunCallback<SkipPreviousAction>()),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_skip_previous),
                contentDescription = "Previous",
                modifier = GlanceModifier.size(iconSize),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
    }
    
    @Composable
    private fun FavoriteButton(
        modifier: GlanceModifier = GlanceModifier,
        isFavorite: Boolean,
        iconColor: ColorProvider
    ) {
        Box(
            modifier = modifier
                .size(36.dp)
                .cornerRadius(18.dp)
                .background(GlanceTheme.colors.surfaceVariant)
                .clickable(actionRunCallback<ToggleFavoriteAction>()),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(
                    if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                ),
                contentDescription = "Favorite",
                modifier = GlanceModifier.size(20.dp),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
    }
    
    private fun getWidgetData(prefs: Preferences, appSettings: chromahub.rhythm.app.shared.data.model.AppSettings?): WidgetData {
        return try {
            WidgetData(
                songTitle = prefs[stringPreferencesKey(KEY_SONG_TITLE)] ?: "Rhythm",
                artistName = prefs[stringPreferencesKey(KEY_ARTIST_NAME)] ?: "",
                albumName = prefs[stringPreferencesKey(KEY_ALBUM_NAME)] ?: "",
                isPlaying = prefs[booleanPreferencesKey(KEY_IS_PLAYING)] ?: false,
                artworkUri = prefs[stringPreferencesKey(KEY_ARTWORK_URI)]?.takeIf { it.isNotBlank() }?.let { 
                    try { 
                        android.net.Uri.parse(it) 
                    } catch (e: Exception) { 
                        null 
                    } 
                },
                hasPrevious = prefs[booleanPreferencesKey(KEY_HAS_PREVIOUS)] ?: false,
                hasNext = prefs[booleanPreferencesKey(KEY_HAS_NEXT)] ?: false,
                isFavorite = prefs[booleanPreferencesKey(KEY_IS_FAVORITE)] ?: false,
                showAlbumArt = appSettings?.widgetShowAlbumArt?.value ?: true,
                showArtist = appSettings?.widgetShowArtist?.value ?: true,
                showAlbum = appSettings?.widgetShowAlbum?.value ?: true,
                showFavoriteButton = appSettings?.widgetShowFavoriteButton?.value ?: true,
                cornerRadius = appSettings?.widgetCornerRadius?.value ?: 28
            )
        } catch (e: Exception) {
            android.util.Log.e("RhythmMusicWidget", "Error getting widget data", e)
            // Return default data if anything fails
            WidgetData(
                songTitle = "Rhythm",
                artistName = "",
                albumName = "",
                isPlaying = false,
                artworkUri = null,
                hasPrevious = false,
                hasNext = false,
                isFavorite = false
            )
        }
    }
}

/**
 * Widget data class with M3 Expressive properties
 */
data class WidgetData(
    val songTitle: String,
    val artistName: String,
    val albumName: String,
    val isPlaying: Boolean,
    val artworkUri: android.net.Uri?,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val isFavorite: Boolean = false,
    val showAlbumArt: Boolean = true,
    val showArtist: Boolean = true,
    val showAlbum: Boolean = true,
    val showFavoriteButton: Boolean = true,
    val cornerRadius: Int = 28
)
