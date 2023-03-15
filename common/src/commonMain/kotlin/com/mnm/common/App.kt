package com.mnm.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import com.mnm.common.models.Command
import com.mnm.common.models.PlayingState
import com.mnm.common.models.Routes
import com.mnm.common.networking.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var command by remember { mutableStateOf<Command?>(null) }

    val platformName = getPlatformName()
    val stateFlow = remember { MutableStateFlow(PlayingState.PLAYING) }

    LaunchedEffect("") {
        Http.webSocket(Routes.api.playlist) {
            launch { stateFlow.collectLatest { sendSerialized(it) } }
            while(true) {
                try {
                    command = receiveDeserialized<Command>()
                    println(command)
                }
                catch(e: Exception) {
                    println(e)
                }
            }
        }
    }

    Column {

        Text(command.toString())
        Button(onClick = {
            stateFlow.value = if(stateFlow.value == PlayingState.PLAYING) PlayingState.STOP else PlayingState.PLAYING
        }) {
            Text(text)
        }
    }
}
