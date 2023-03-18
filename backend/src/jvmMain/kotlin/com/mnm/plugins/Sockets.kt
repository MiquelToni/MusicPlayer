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
                    val newPlayerState: PlayerState? = when (val command = receiveDeserialized<Command>()) {
                        is PrepareSong -> playerStateFlow.value.copy(
                            state = if (command.autoplay) PlayingState.PLAYING else PlayingState.READY,
                            playingSongAt = command.songAtPlaylistIndex,
                            currentTime = command.initialSeek,
                            emittedAt = System.currentTimeMillis()
                        )

                        is Play -> playerStateFlow.value.let {
                            if (it.state in listOf(PlayingState.PAUSED, PlayingState.READY)) it.copy(
                                state = PlayingState.PLAYING, emittedAt = System.currentTimeMillis()
                            )
                            else null
                        }

                        is Pause -> playerStateFlow.value.let {
                            if (it.state == PlayingState.PLAYING) it.copy(
                                state = PlayingState.PAUSED,
                                currentTime = command.currentTime,
                                emittedAt = System.currentTimeMillis()
                            )
                            else null
                        }

                        is SeekTo -> playerStateFlow.value.let {
                            if (it.state != PlayingState.IDLE) it.copy(
                                currentTime = command.currentTime, emittedAt = System.currentTimeMillis()
                            )
                            else null
                        }

                        is Stop -> idlePlayerState

                        is Next -> playerStateFlow.value.playingSongAt?.let {
                            playerStateFlow.value.copy(
                                playingSongAt = (it + 1) % playerStateFlow.value.playlist.count(),
                                emittedAt = System.currentTimeMillis()
                            )
                        }

                        is Previous -> playerStateFlow.value.playingSongAt?.let {
                            println(it)
                            playerStateFlow.value.copy(
                                playingSongAt = (it + playerStateFlow.value.playlist.count() - 1) % playerStateFlow.value.playlist.count(),
                                emittedAt = System.currentTimeMillis()
                            )
                        }
                    }
                    newPlayerState?.let {
                        playerStateFlow.value = newPlayerState
                    }
                } catch (e: Exception) {
                    println("WebSocket error: '${e.message}'.")
                    send("Bad Request. Not a Command.")
                }
            }
        }
    }
}
