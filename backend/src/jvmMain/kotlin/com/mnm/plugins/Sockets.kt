package com.mnm.plugins

import com.mnm.common.models.Routes
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*

val playerState = PlayerState()

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket(Routes.api.playlist) {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    println(text)
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    // read commands
                    // do command
                }
            }

            // emit player state
            send(playerState)
        }
    }
}
