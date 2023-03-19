package com.mnm.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mnm.common.components.FileChooser
import com.mnm.common.components.Playbar
import com.mnm.common.models.*
import com.mnm.common.networking.Http
import com.mnm.common.state.playerStateFromServer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddMusicButton(onClick: () -> Unit) = FloatingActionButton(onClick = onClick) {
    Icon(
        modifier = Modifier.offset(x = 2.dp),
        imageVector = Icons.Default.MusicNote,
        contentDescription = null
    )
    Icon(
        modifier = Modifier
            .offset(
                x = (-6).dp,
                y = (-4).dp
            )
            .size(14.dp),
        imageVector = Icons.Default.Add,
        contentDescription = null
    )
}

@Composable
fun TopBar(hasConnection: Boolean) = Row(
    modifier=Modifier
        .fillMaxWidth()
        .padding(vertical=8.dp, horizontal = 16.dp),
    horizontalArrangement = Arrangement.End
) {
    Box {
        androidx.compose.animation.AnimatedVisibility(hasConnection) {
            Icon(
                tint = Color.Green,
                imageVector = Icons.Default.Wifi,
                contentDescription = null
            )
        }
        androidx.compose.animation.AnimatedVisibility(!hasConnection) {
            Icon(
                tint = Color.Red,
                imageVector = Icons.Default.WifiOff,
                contentDescription = null
            )
        }
    }

}

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var hasConnection by remember { mutableStateOf(false) }
    val commands = remember { Channel<Command>(1) }
    val playerState by playerStateFromServer(commands, onConnectionStateChanged = { hasConnection = it })
    fun sendCommand(c: Command) = scope.launch { commands.send(c) }

    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    fun closeDialog() { showDialog = false }

    var selectedFile by remember { mutableStateOf<File?>(null) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar= { TopBar(hasConnection = hasConnection) },
        floatingActionButton = { AddMusicButton(onClick = { showDialog = true }) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.size(200.dp).align(Alignment.Center)) {
                AnimatedVisibility(isLoading) { CircularProgressIndicator(Modifier.fillMaxSize()) }
                AnimatedVisibility(!isLoading) {
                    Icon(
                        tint = Color.Green,
                        imageVector = Icons.Default.Check, contentDescription = null
                    )
                    Text(playerState.toString())
                }
            }
            if(playerState!=null) {
                Playbar(
                    state=playerState!!,
                    onPreviousSong={ sendCommand(Previous)},
                    onNextSong={ sendCommand(Next) },
                    onPauseSong={ sendCommand(Stop) },
                    onPlaySong={ sendCommand(Play)}
                )
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
        onError = ::closeDialog
    )
    LaunchedEffect(selectedFile) effect@{
        val file = selectedFile ?: return@effect
        isLoading = true
        runCatching { Http.uploadFile(file.name, file.readBytes()) }
        isLoading = false
        selectedFile=null
    }
}