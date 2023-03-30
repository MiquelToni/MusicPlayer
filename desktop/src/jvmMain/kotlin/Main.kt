import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.swing.UIManager

const val musicFolder = "/Users/miquel/Desktop/Music/"
fun main() = application {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val player = MinimPlayer(musicFolder)
    Window(
        title = "Distributed music player",
        alwaysOnTop = true,
        onCloseRequest = ::exitApplication) {

        val bands by
            remember { player.start("1.wav")  }
            .collectAsState(FloatArray(0))

        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color.Black)
            val width = 20
            for ((index, band) in bands.withIndex()){
                val xStart = index * width
                val xEnd = xStart + width
                drawRect(
                    color = Color.Green,
                    topLeft = Offset.Zero.copy(xStart.toFloat(), 0f),
                    size = Size(20f, band)
                )

            }
        }
        // ProvideWindow { App() }
    }
}
