import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap

import ddf.minim.Minim
import ddf.minim.AudioPlayer as MinimAudioPlayer
import ddf.minim.analysis.FFT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import java.io.File
import java.io.InputStream
import kotlin.math.sqrt

// Vaja flipada BRODEERRR
fun `√`(x: Float) = sqrt(x)
inline val FFT.spectrum: FloatArray get() {
    val realSpec = spectrumReal
    val imagSpec = spectrumImaginary
    return realSpec
        .indices
        .map { index -> `√`(realSpec[index] * realSpec[index] + imagSpec[index] * imagSpec[index]) }
        .toFloatArray()
}


fun AudioPlayer(audioPlayer: MinimAudioPlayer): AudioPlayer = object : AudioPlayer {
    override val bufferSize get() = audioPlayer.bufferSize()
    override val sampleRate get() = audioPlayer.sampleRate()
    override var volume: Float
        set(value) {
            audioPlayer.volume = value
        }
        get() = audioPlayer.volume
    override var muted: Boolean
        set(value) {
             audioPlayer.apply {
                 if(value)
                     mute()
                 else
                     unmute()
            }
        }
        get() = audioPlayer.isMuted

    override fun cue(millis: Int) = audioPlayer.cue(millis)
    override fun play() = audioPlayer.play()
}


actual object LocalSongLoader {

    private val minim = Minim(this)
    actual suspend fun loadSong(path: String): SongController = withContext(Dispatchers.IO) {
        println("loadSong started")

        val songFile = File(path)
        if(!songFile.exists()) error("File not found")
        val albumCover = async { AudioFileIO.read(songFile)?.tag?.firstArtwork?.image?.toComposeImageBitmap() }
        val musicPlayer = minim.loadFile(path) ?: error("Minim was unable to load the file")
        val fft = FFT(musicPlayer.bufferSize(), musicPlayer.sampleRate())
        val songState =
            flow {
                while (musicPlayer.isPlaying) {
                    emit(Unit)
                    delay(16) // 60 fps
                }
            }
                .map {
                    fft.forward(musicPlayer.mix)
                    SongState(
                        positionMillis = musicPlayer.position(),
                        bands = fft.spectrum
                    )
                }
                .flowOn(Dispatchers.Default)
        SongController(
            player     = AudioPlayer(musicPlayer),
            albumCover = albumCover.await(),
            songState  = songState,
            durationMillis = musicPlayer.length()
        )
    }

    fun createInput(path: String): InputStream = File(path).inputStream()
}