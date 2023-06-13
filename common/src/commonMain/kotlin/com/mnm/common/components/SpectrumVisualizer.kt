package com.mnm.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform


fun Float.asPercentage() = this / 0xFF

@Composable
fun BoxScope.SpectrumVisualizer(frequencyBands: FloatArray) {
    Canvas(
        Modifier
            .align(Alignment.Center)
            .fillMaxWidth(.9f)
            .fillMaxHeight(.5f)) {
        val usedBands = frequencyBands//.take(128)
        val bandWidth = size.width / usedBands.size

        withTransform(
            transformBlock = { rotate(180f); translate(top=size.height/2) }
        ) {
            for ((index, bandValue) in usedBands.withIndex()){
                val xStart = index * bandWidth
                val normalizedHeight =  bandValue.asPercentage() * size.height
                val green = 255-(bandValue.asPercentage()*255).toInt()
                drawRect(
                    color = Color(
                        red  = (bandValue.asPercentage()*255).toInt(),
                        green= green,
                        blue = (((System.currentTimeMillis()%255) + green) % 255).toInt(),
                        alpha = 0xFF
                    ),
                    topLeft = Offset.Zero.copy(xStart, 0f),
                    size = Size(bandWidth, normalizedHeight)
                )
            }
        }
        withTransform(
            transformBlock = { translate(top=size.height/2) }
        ) {
            for ((index, bandValue) in usedBands.withIndex()){
                val xStart = index * bandWidth
                val normalizedHeight =  bandValue.asPercentage() * size.height
                val green = 255-(bandValue.asPercentage()*255).toInt()
                drawRect(
                    color = Color(
                        red  = (bandValue.asPercentage()*255).toInt(),
                        green= green,
                        blue = (((System.currentTimeMillis()%255) + green) % 255).toInt(),
                        alpha = 0xFF
                    ),
                    topLeft = Offset.Zero.copy(xStart, 0f),
                    size = Size(bandWidth, normalizedHeight)
                )
            }
        }

    }
}