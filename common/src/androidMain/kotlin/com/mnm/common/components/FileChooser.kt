package com.mnm.common.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import java.io.File

@Composable
actual fun FileChooser(
    visible: Boolean,
    title: String,
    onFile: (File) -> Unit,
    onCancel: () -> Unit,
    onError: () -> Unit
) = Text("TODO")