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
    val currentTime: Long?, // seek
    val state: PlayingState,
    val emittedAt: Long,
)

// https://docs.tizen.org/application/native/api/mobile/5.0/capi_media_player_state_diagram.png
@Serializable
sealed class Command()
@Serializable
data class PrepareSong(val songAtPlaylistIndex: Int, val autoplay: Boolean = false, val initialSeek: Long = 0L): Command()
@Serializable
data class SeekTo(val currentTime: Long): Command()
@Serializable
data class Pause(val currentTime: Long) : Command()
@Serializable
object Play : Command()
@Serializable
object Stop : Command()
@Serializable
object Next : Command()
@Serializable
object Previous : Command()