package com.mnm.common.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import com.mnm.common.models.Command
import com.mnm.common.models.PlayerState
import com.mnm.common.models.Routes
import com.mnm.common.networking.Http
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@Composable
fun playerStateFromServer(
    commands: Channel<Command>,
    onConnectionStateChanged: (Boolean) -> Unit ={}
) = produceState<PlayerState?>(null) {
    fun connected() = onConnectionStateChanged(true)
    fun disconnected() = onConnectionStateChanged(false)
    while(true) {
        val res = runCatching {
            Http.webSocket(Routes.api.playlist) {
                connected()
                launch { commands.receiveAsFlow().collectLatest(::sendSerialized) }
                var channelIsOpen = true
                while (isActive && channelIsOpen) {
                    try {
                        val playerState = receiveDeserialized<PlayerState>()
                        value = playerState
                        println(playerState)
                    }
                    catch (e: ClosedReceiveChannelException) {
                        channelIsOpen = false
                        disconnected()
                    }
                    catch (e: Exception) {
                        println(e)
                    }
                }
            }
        }
        if(res.isFailure) {
            // Retry after one second
            delay(1000L)
            println("Attempting to reconnect…")
            disconnected()
        }
    }
}