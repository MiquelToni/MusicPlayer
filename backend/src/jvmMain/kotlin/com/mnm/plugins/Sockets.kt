package com.mnm.plugins

import com.mnm.common.models.*
import com.mnm.common.networking.jsonSerializer
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.delay
import java.io.File

var playerState = PlayerState(
    playlist = emptyList(),
    playingSongAt = null,
    state = PlayingState.STOP,
    emittedAt = 0,
)

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(jsonSerializer)
    }
    routing {
        webSocket(Routes.api.playlist) {
            launch {
                while (true) {
                    val playlist = File(musicPath)
                        .walk()
                        .filter { it.extension == "mp3" }
                        .map {
                            Song(
                                fileName = it.name,
                                src = "",
                                durationMS = 3000L
                            )
                        }
                        .toList()

                    playerState = playerState.copy(playlist = playlist)

                    sendSerialized(playerState)
                    delay(1000)
                }
            }
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    println(text)
//                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    // read commands
                    // do command
                }
            }
        }
    }
}
