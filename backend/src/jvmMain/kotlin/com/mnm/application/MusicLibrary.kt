package com.mnm.application

import com.mnm.common.models.Song
import com.mnm.plugins.musicPath
import ws.schild.jave.MultimediaObject
import java.io.File

object MusicLibrary {
    private val _musicLibrary = File(musicPath).walk().filter { it.isFile && it.extension == "mp3" }.map {
        val multi = MultimediaObject(it)
        Song(
            fileName = it.name, durationMS = multi.info.duration
        )
    }.toMutableList()
    val musicLibrary get() = _musicLibrary

    fun addSong(songFile: File) {
        val multi = MultimediaObject(songFile)
        val song = Song(
            fileName = songFile.name, durationMS = multi.info.duration
        )
        _musicLibrary.add(song)
    }

    fun generatePlaylist(maxSize: Int = 100) = _musicLibrary.shuffled().take(maxSize)
}
