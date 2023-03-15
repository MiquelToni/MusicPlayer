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

var playerState = PlayerState(emptyList(), null, PlayingState.STOP, 0)

val commandStop = StopCommand
val commandPlay = PlayCommand(at = 15)

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
                while(true) {
                    sendSerialized<Command>(commandStop)
                    delay(1000)
                    println(commandPlay)
                    sendSerialized<Command>(commandPlay)
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
