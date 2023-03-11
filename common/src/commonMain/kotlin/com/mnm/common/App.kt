package com.mnm.common

import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import com.mnm.common.models.PlayingState
import com.mnm.common.models.Routes
import com.mnm.common.networking.client
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val platformName = getPlatformName()
    val stateFlow = remember { MutableStateFlow(PlayingState.PLAYING) }

    LaunchedEffect("") {
        client.webSocket("wss://b0e8-85-58-29-37.eu.ngrok.io${Routes.api.playlist}") {
            launch { stateFlow.collectLatest { sendSerialized(it) } }
            for (frame in incoming) {
                text = String(frame.readBytes())
            }
        }
    }

    Button(onClick = {
        stateFlow.value = if(stateFlow.value == PlayingState.PLAYING) PlayingState.STOP else PlayingState.PLAYING
    }) {
        Text(text)
    }
}
