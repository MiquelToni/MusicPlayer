package com.mnm.plugins

import com.mnm.common.models.*
import com.mnm.common.networking.jsonSerializer
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.time.Duration

val idlePlayerState = PlayerState(
    playlist = generatePlaylist(),
    playingSongAt = null,
    currentTime = null,
    state = PlayingState.IDLE,
    emittedAt = System.currentTimeMillis()
)

val playerStateFlow = MutableStateFlow(idlePlayerState)

fun generatePlaylist() = getMusicLibrary().shuffled().take(100)

fun getMusicLibrary() = File(musicPath).walk().filter { it.isFile && it.extension == "mp3" }.map {
    Song(
        fileName = it.name, durationMS = 3000L
    )
}.toList()

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
            sendSerialized(playerStateFlow.value)

            launch { playerStateFlow.collectLatest { sendSerialized(it) } }

            while (true) {
                try {
                    val newPlayerState = when (val command = receiveDeserialized<Command>()) {
                        is PrepareSong ->
                            playerStateFlow.value.copy(
                                state = if (command.autoplay == true) PlayingState.READY else PlayingState.PLAYING,
                                playingSongAt = playerStateFlow.value.playlist.map { it.fileName }
                                    .indexOf(command.songName),
                                currentTime = 0,
                                emittedAt = System.currentTimeMillis()
                            )

                        is Play -> playerStateFlow.value.copy(
                            state = PlayingState.PLAYING,
                            emittedAt = System.currentTimeMillis()
                        )

                        is Pause -> playerStateFlow.value.copy(
                            state = PlayingState.PAUSED,
                            currentTime = command.currentTime,
                            emittedAt = System.currentTimeMillis()
                        )

                        is SeekTo -> playerStateFlow.value.copy(
                            currentTime = command.currentTime,
                            emittedAt = System.currentTimeMillis()
                        )

                        is Stop -> idlePlayerState
                    }
                    playerStateFlow.value = newPlayerState
                } catch (e: Exception) {
                    println("WebSocket error: '${e.message}'.")
                    send("Bad Request. Not a Command.")
                }
            }
        }
    }
}
