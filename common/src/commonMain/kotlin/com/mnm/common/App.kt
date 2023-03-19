package com.mnm.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mnm.common.components.FileChooser
import com.mnm.common.models.*
import com.mnm.common.networking.Http
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddMusicButton(onClick: () -> Unit) = FloatingActionButton(onClick = onClick ) {
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
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    fun closeDialog() { showDialog = false }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var playerState by remember { mutableStateOf<PlayerState?>(null) }
    val stateFlow = remember { MutableStateFlow<Command>(Play) }

    DisposableEffect(Unit) {
        val job = scope.launch coroutine@{
            Http.webSocket(Routes.api.playlist) {
                launch { stateFlow.collectLatest { sendSerialized(it) } }
                do {
                    try {
                        playerState = receiveDeserialized<PlayerState>()
                        println(playerState)
                    }
                    catch(e: Exception) {
                        println(e)
                    }
                } while(this@coroutine.isActive)
            }
        }

        onDispose { job.cancel() }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AddMusicButton(
                onClick= { showDialog=true })
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row {
                Button(onClick = {
                    stateFlow.value = Play
                }) {
                    Text("Play")
                }
                Button(onClick = {
                    stateFlow.value = Stop
                }) {
                    Text("Stop")
                }
                Button(onClick = {
                    stateFlow.value = PrepareSong(songAtPlaylistIndex = 0)
                }) {
                    Text("Prepare song")
                }
                Button(onClick = {
                    stateFlow.value = SeekTo(currentTime = 1000L)
                }) {
                    Text("SeekTo")
                }
                Button(onClick = {
                    stateFlow.value = Pause(currentTime = 2000L)
                }) {
                    Text("Pause")
                }
            }
        }
        if(isLoading) {
            CircularProgressIndicator(Modifier.fillMaxSize())
        } else {
            Row {
                Icon(
                    tint = Color.Green,
                    imageVector = Icons.Default.Check, contentDescription = null)
                Text(playerState.toString())
            }
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