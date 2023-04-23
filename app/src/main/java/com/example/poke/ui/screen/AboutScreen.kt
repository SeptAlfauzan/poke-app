package com.example.poke.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.poke.ui.theme.PokeTheme

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "About",
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "Poke is a pokedex like app that will provide information about pokemons especially gen 1 pokemons",
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface.copy(
                    alpha = 0.5f
                )
            )
        )
        Text(text = "Developed by")
        AsyncImage(
            model = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/small/avatar/dos:307a0cb78e2dc8d480dd6060695a98e620230302221826.png",
            contentScale = ContentScale.Crop,
            contentDescription = "image avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        TextWithLabel(label = "Name", text = "Septa Alfauzan")
        TextWithLabel(label = "Email", text = "alfauzansepta@gmail")
    }
}

@Composable
fun TextWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.5f),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface.copy(
                    alpha = 0.5f
                ),
                textAlign = TextAlign.End
            )
        )
        Box(modifier = Modifier
            .width(2.dp)
            .height(16.dp)
            .background(MaterialTheme.colors.secondary))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.secondary
            ),
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun AboutScreenPreview(){
    PokeTheme {
        AboutScreen()
    }
}