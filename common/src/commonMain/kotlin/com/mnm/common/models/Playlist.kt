package com.mnm.common.models

import kotlinx.serialization.Serializable

@Serializable
enum class PlayingState { PLAYING, STOP; }

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