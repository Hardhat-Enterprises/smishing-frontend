package com.example.smishingdetectionapp.riskmeter

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment


@Composable
fun Pulsing() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PulsingCirclesAnimation()
    }
}

@Composable
fun PulsingCirclesAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")

    val sizes = listOf(80.dp, 120.dp, 160.dp)

    val scaleFactors = sizes.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 6000
                    1.0f at 0
                    1.2f at (2000 + index * 500)
                    1.4f at 4000
                    1.6f at 6000
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "scale$index"
        )
    }

    val alphaValues = sizes.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 6000
                    0.0f at 0
                    0.4f at (1000 + index * 500)
                    0.1f at (2000 + index * 500)
                    0.0f at 6000
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha$index"
        )
    }

    Canvas(modifier = Modifier.size(250.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val center = Offset(canvasWidth / 2, canvasHeight / 2)

        sizes.forEachIndexed { index, size ->
            val radius = size.toPx() / 2 * scaleFactors[index].value
            val alpha = alphaValues[index].value

            drawCircle(
                color = Color(0xB34B74F0),
                radius = radius,
                center = center,
                alpha = alpha
            )
        }

        drawCircle(
            color = Color(0xE64B74F0),
            radius = 100f,
            center = center
        )
    }
}
