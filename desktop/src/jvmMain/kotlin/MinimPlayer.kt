import ddf.minim.AudioPlayer
import ddf.minim.Minim
import ddf.minim.analysis.FFT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.InputStream
import kotlin.math.sqrt

inline val FFT.spectrum: FloatArray get() {
    val realSpec = spectrumReal
    val imagSpec = spectrumImaginary
    return realSpec
        .indices
        .map { index -> sqrt(realSpec[index] * realSpec[index] + imagSpec[index] * imagSpec[index]) }
        .toFloatArray()
}
data class MinimPlayer(
    val musicFolder: String,
) {
    private val minim = Minim(this)

    private var player: AudioPlayer?=null
    fun start(songName: String): Flow<FloatArray> {

        val musicPlayer = minim.loadFile("$musicFolder/$songName")?.also { player=it } ?: return emptyFlow()
        musicPlayer.metaData.album()
        musicPlayer.play()

        println("SampleRate: ${musicPlayer.sampleRate()}; BufferSize: ${musicPlayer.bufferSize()}")
        val fft = FFT(musicPlayer.bufferSize(), musicPlayer.sampleRate())



        return (
                flow {
                    while (musicPlayer.isPlaying) {
                        emit(Unit)
                        delay(16) // 60 fps
                    }
                }
                    .map {
                        fft.forward(musicPlayer.mix)
                        fft.spectrum
                    }
                    .flowOn(Dispatchers.Default)
                )
    }
    // Gets the full path
    fun sketchPath( fileName :String ) = "$musicFolder/$fileName"

    fun createInput( path :String ): InputStream = File(path).inputStream()
}