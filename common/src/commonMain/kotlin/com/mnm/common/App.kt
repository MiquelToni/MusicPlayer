package com.mnm.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import com.mnm.common.models.Command
import com.mnm.common.models.PlayCommand
import com.mnm.common.models.PlayingState
import com.mnm.common.models.Routes
import com.mnm.common.networking.client
import com.mnm.common.networking.jsonSerializer
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

const val HOST = "localhost:8080"
const val HTTP = "http://$HOST"
const val WS = "ws://$HOST"

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var command by remember { mutableStateOf<Command?>(null) }

    val platformName = getPlatformName()
    val stateFlow = remember { MutableStateFlow(PlayingState.PLAYING) }

    LaunchedEffect("") {
        client.webSocket("$WS${Routes.api.playlist}") {
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
