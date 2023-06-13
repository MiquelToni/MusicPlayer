import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mnm.common.App
import com.mnm.common.components.ProvideWindow
import kotlinx.coroutines.delay
import javax.swing.UIManager


const val musicFolder = "/Users/miquel/Desktop/Music/"
const val song = "Steff Da Campo & SMACK - Renegade (Official Music Video) [sFbkSuEGl0U].mp3"




val price: Int get() = 2 * price



fun main() = application {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    Window(
        title = "Distributed music player",
        alwaysOnTop = true,
        onCloseRequest = ::exitApplication) {

        ProvideWindow { App("$musicFolder/$song") }
    }
}
