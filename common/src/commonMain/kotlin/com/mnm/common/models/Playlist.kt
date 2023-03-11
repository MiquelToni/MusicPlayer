package com.mnm.common.models

import kotlinx.serialization.Serializable

@Serializable
enum class PlayingState { PLAYING, STOP; }

@Serializable
enum class CommandAction { PLAY, STOP; }

@Serializable
data class Song(
    val fileName: String,
    val src: String,
    val durationMS: Long
)

@Serializable
data class PlayerState(
    val playlist: List<Song>,
    val playingSongAt: Int?,
    val state: PlayingState,
    val emittedAt: Long,
)

@Serializable
sealed class Command(val action: CommandAction) {
    @Serializable
    class Stop : Command(action = CommandAction.STOP)

    @Serializable
    class Play(val payload: PlayPayload) : Command(action = CommandAction.PLAY)
}

@Serializable
data class PlayPayload(val at: Int);