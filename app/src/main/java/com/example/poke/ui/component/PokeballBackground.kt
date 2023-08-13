package com.example.poke.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poke.R
import com.example.poke.ui.theme.PokeTheme

@Composable
fun PokeBallBackground(modifier: Modifier = Modifier) {
    val imageSize = 158
    val colorMatrix = ColorMatrix()
    colorMatrix.setToSaturation(0f)

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.pokeball),
            modifier = Modifier
                .size(imageSize.dp)
                .align(Alignment.End)
                .offset(x = (imageSize * 0.4f).dp)
                .rotate(-15f),
            contentDescription = stringResource(
                R.string.pokeball_img_bg
            ),
            colorFilter = ColorFilter.colorMatrix(colorMatrix),
            alpha = 0.3f
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    PokeTheme() {
        Surface {
            PokeBallBackground()
        }
    }
}