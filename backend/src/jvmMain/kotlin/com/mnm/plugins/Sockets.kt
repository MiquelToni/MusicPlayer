package com.mnm.plugins

import com.mnm.common.models.*
import com.mnm.common.networking.jsonSerializer
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
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


fun PlayerState.computeNewState(command: Command) =
    when (command) {
        is PrepareSong -> copy(
            state = if (command.autoplay) PlayingState.PLAYING else PlayingState.READY,
            playingSongAt = command.songAtPlaylistIndex,
            currentTime = command.initialSeek
        )

        is Play ->
            if (state in listOf(PlayingState.PAUSED, PlayingState.READY))
                copy(state = PlayingState.PLAYING)
            else
                null

        is Pause ->
            if (state == PlayingState.PLAYING)
                copy(state = PlayingState.PAUSED, currentTime = command.currentTime)
            else
                null

        is SeekTo ->
            if (state != PlayingState.IDLE)
                copy(currentTime = command.currentTime)
            else
                null

        is Stop -> idlePlayerState
        is Next ->
            playingSongAt
                ?.let { currentSong -> (currentSong + 1) % playlist.size }
                ?.let { copy(playingSongAt = it) }

        is Previous ->
            playingSongAt
                ?.let { currentSong -> (currentSong - 1 + playlist.size) % playlist.size }
                ?.let { copy(playingSongAt = it) }
    }
    ?.run { copy(emittedAt = System.currentTimeMillis()) }
    ?:this


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
                playerStateFlow.collectLatest(::sendSerialized)
            }
            while (isActive && incoming.isClosedForReceive.not()) {
                try {
                    val command = receiveDeserialized<Command>().also { println("received command $it") }
                    playerStateFlow.value = playerStateFlow.value.computeNewState(command)
                } catch (e: Exception) {
                    println(e)
                    println("WebSocket error: '${e.message}'.")
                }
            }
        }
    }
}
