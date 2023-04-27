package com.example.poke.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poke.ui.theme.PokeTheme

@Composable
fun LoadingCircular(
    modifier: Modifier = Modifier
){
    val progress by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        )
    )

    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        )
    )

    CircularProgressIndicator(
        progress = progress,
        color = MaterialTheme.colors.secondary,
        modifier = modifier
            .size(32.dp)
            .rotate(rotation)
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview(){
    PokeTheme {
        LoadingCircular()
    }
}