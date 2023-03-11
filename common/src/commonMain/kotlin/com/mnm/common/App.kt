package com.mnm.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import com.mnm.common.models.PlayerState
import com.mnm.common.models.PlayingState
import com.mnm.common.models.Routes
import com.mnm.common.networking.client
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

const val HOST = "827d-85-58-29-37.eu.ngrok.io"
const val HTTP = "https://$HOST"
const val WS = "wss://$HOST"

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var playerState by remember { mutableStateOf<PlayerState?>(null) }

    val platformName = getPlatformName()
    val stateFlow = remember { MutableStateFlow(PlayingState.PLAYING) }

    LaunchedEffect("") {
        client.webSocket("$WS${Routes.api.playlist}") {
            launch { stateFlow.collectLatest { sendSerialized(it) } }
            while(true) {
                playerState = receiveDeserialized<PlayerState>()
            }
        }
    }

    Column {

        Text(playerState.toString())
        Button(onClick = {
            stateFlow.value = if(stateFlow.value == PlayingState.PLAYING) PlayingState.STOP else PlayingState.PLAYING
        }) {
            Text(text)
        }
    }
}
