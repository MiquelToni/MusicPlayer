import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mnm.common.App
import com.mnm.common.components.ProvideWindow
import javax.swing.UIManager

fun main() = application {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    Window(
        title = "Distributed music player",
        alwaysOnTop = true,
        onCloseRequest = ::exitApplication) {
        ProvideWindow { App() }
    }
}
