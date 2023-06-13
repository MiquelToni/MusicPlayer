package com.mnm.common.components

import SongController
import SongState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mnm.common.models.PlayerState
import com.mnm.common.models.PlayingState
import empty




fun doNothing() = Unit
@Composable
fun BoxScope.MusicPlayer(controller: SongController) {
    val songState by remember { controller.songState }.collectAsState(empty)

    if(controller.albumCover!=null) {
        Image(
            modifier= Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp)),
            contentDescription = null,
            bitmap=controller.albumCover)
    }
    SpectrumVisualizer(songState.bands)
    Playbar(
        progress = songState.positionMillis.toFloat() / controller.durationMillis.toFloat(),
        onPreviousSong=::doNothing,
        onNextSong=::doNothing,
        onPauseSong=::doNothing,
        onPlaySong=::doNothing,
    )
}