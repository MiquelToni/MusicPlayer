import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow


class SongState(
    val positionMillis: Int,
    val bands: FloatArray
)
val empty = SongState(positionMillis = 0, bands= floatArrayOf())
data class SongController(
    val player: AudioPlayer,
    val durationMillis: Int,
    val albumCover: ImageBitmap?,
    val songState: Flow<SongState>
)

interface AudioPlayer {
    val bufferSize: Int
    val sampleRate: Float
    var volume: Float
    var muted: Boolean
    fun cue(millis: Int)
    fun play()
}


expect object LocalSongLoader {
    suspend fun loadSong(path: String): SongController
}