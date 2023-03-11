package com.mnm.plugins

import com.mnm.common.models.Command
import com.mnm.common.models.PlayerState
import com.mnm.common.models.PlayingState
import com.mnm.common.models.Routes
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.delay

var playerState = PlayerState(emptyList(), null, PlayingState.STOP, 0)

val commandStop = com.mnm.common.models.Command.Stop()
val commandPlay = com.mnm.common.models.Command.Play(payload = com.mnm.common.models.PlayPayload(at = 15))

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        webSocket(Routes.api.playlist) {
            launch {
                while(true) {
                    delay(1000)
                    sendSerialized(commandStop)
                    delay(1000)
                    sendSerialized(commandPlay)
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
