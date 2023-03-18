package com.mnm.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.mnm.common.models.*
import com.mnm.common.networking.Http
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var playerState by remember { mutableStateOf<PlayerState?>(null) }

    val platformName = getPlatformName()
    val stateFlow = remember { MutableStateFlow<Command>(Play) }

    LaunchedEffect("") {
        Http.webSocket(Routes.api.playlist) {
            launch { stateFlow.collectLatest { sendSerialized(it) } }
            while(true) {
                try {
                    playerState = receiveDeserialized<PlayerState>()
                    println(playerState)
                }
                catch(e: Exception) {
                    println(e)
                }
            }
        }
    }

    Column {

        Text(playerState.toString())
        Button(onClick = {
            stateFlow.value = if(playerState?.state == PlayingState.PLAYING) Stop else Play
        }) {
            Text(text)
        }
    }
}
