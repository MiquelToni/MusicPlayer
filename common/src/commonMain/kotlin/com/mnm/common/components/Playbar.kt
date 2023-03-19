package com.mnm.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mnm.common.models.PlayerState
import com.mnm.common.models.PlayingState

@Composable
fun PlaybarButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(100),
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        shape = shape,
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}

@Composable
fun BoxScope.Playbar(
    state: PlayerState,
    onPreviousSong: () -> Unit,
    onNextSong: () -> Unit,
    onPauseSong: () -> Unit,
    onPlaySong: () -> Unit,
) {
    val isPlaying = state.state == PlayingState.PLAYING
    val isNotPlaying = !isPlaying
    val spacerModifier = Modifier.size(16.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .align(Alignment.BottomCenter),
    ) {
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value=.5f,
            onValueChange={}
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            PlaybarButton(
                icon = Icons.Default.SkipPrevious,
                onClick = onPreviousSong)

            Spacer(spacerModifier)

            AnimatedVisibility(isNotPlaying) {
                PlaybarButton(
                    icon = Icons.Default.PlayArrow,
                    onClick = onPlaySong)
            }

            AnimatedVisibility(isPlaying) {
                PlaybarButton(
                    icon = Icons.Default.Pause,
                    onClick = onPauseSong)
            }


            Spacer(spacerModifier)

            PlaybarButton(
                icon = Icons.Default.SkipNext,
                onClick = onNextSong)
        }
    }
}