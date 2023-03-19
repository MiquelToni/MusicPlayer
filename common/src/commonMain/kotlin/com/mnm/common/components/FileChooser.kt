package com.mnm.common.components

import androidx.compose.runtime.Composable
import java.io.File


@Composable
expect fun FileChooser(
    visible: Boolean = false,
    title: String = "Choose a music file",
    onFile: (File) -> Unit={},
    onCancel: () -> Unit={},
    onError: () -> Unit={}
)