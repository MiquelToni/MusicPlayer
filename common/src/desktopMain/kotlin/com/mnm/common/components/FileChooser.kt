package com.mnm.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.WindowScope
import java.awt.Window
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


val LocalWindow = compositionLocalOf<Window?> { null }

@Composable
fun WindowScope.ProvideWindow(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalWindow provides window, content=content)

@Composable
actual fun FileChooser(
    visible: Boolean,
    title: String,
    onFile: (File) -> Unit,
    onCancel: () -> Unit,
    onError: () -> Unit
) {
    val parent = LocalWindow.current
    LaunchedEffect(visible, parent) {
        if(visible && parent != null) {
            val chooser = JFileChooser().apply {
                fileFilter = FileNameExtensionFilter("MP3 files", "mp3")
                dialogTitle = title
                isMultiSelectionEnabled = false
                isVisible = true
            }

            when(chooser.showOpenDialog(parent)) {
                JFileChooser.APPROVE_OPTION -> {
                    val file = chooser.selectedFile
                    if(file.canRead()) { onFile(file) }
                    else { onCancel() }
                }
                JFileChooser.CANCEL_OPTION -> onCancel()
                JFileChooser.ERROR_OPTION -> onError()
            }
        }
    }
}