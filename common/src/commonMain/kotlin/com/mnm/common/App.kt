package com.mnm.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mnm.common.components.FileChooser
import com.mnm.common.networking.Http
import java.io.File



@Composable
fun AddMusicButton(
    onClick: () -> Unit
) = FloatingActionButton(
    onClick = onClick,
) {
    Icon(
        modifier=Modifier.offset(x=2.dp),
        imageVector = Icons.Default.MusicNote,
        contentDescription = null)
    Icon(
        modifier = Modifier
            .offset(
                x=(-6).dp,
                y=(-4).dp
            )
            .size(14.dp),
        imageVector = Icons.Default.Add,
        contentDescription = null)
}

@Composable
fun App() {
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    fun closeDialog() { showDialog = false }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AddMusicButton(
                onClick= { showDialog=true })
        }
    ) {
        if(isLoading) {
            CircularProgressIndicator(Modifier.fillMaxSize())
        } else {
            Icon(
                tint = Color.Green,
                imageVector = Icons.Default.Check, contentDescription = null)
        }
    }
    FileChooser(
        visible = showDialog,
        onFile = {
            selectedFile = it
            closeDialog()
        },
        onCancel = ::closeDialog,
        onError = ::closeDialog)
    LaunchedEffect(selectedFile) effect@{
        val file = selectedFile ?: return@effect
        isLoading = true
        val res = kotlin.runCatching {  Http.uploadFile(file.name, file.readBytes()) }
        isLoading = false
    }
}