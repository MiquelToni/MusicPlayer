package com.mnm.common.models

import kotlinx.serialization.Serializable

@Serializable
enum class PlayingState { IDLE, READY, PLAYING, PAUSED; }

@Serializable
data class Song(
    val fileName: String,
    val durationMS: Long
)

@Serializable
data class PlayerState(
    val playlist: List<Song>,
    val playingSongAt: Int?,
    val currentTime: Int?, // seek
    val state: PlayingState,
    val emittedAt: Long,
)

// https://docs.tizen.org/application/native/api/mobile/5.0/capi_media_player_state_diagram.png
@Serializable
sealed class Command()
@Serializable
data class PrepareSong(val songName: String, val autoplay: Boolean?, val initialSeek: Int?): Command()
@Serializable
data class SeekTo(val currentTime: Int): Command()
@Serializable
data class Pause(val currentTime: Int) : Command()
@Serializable
object Play : Command()
@Serializable
object Stop : Command()