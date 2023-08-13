package com.example.poke.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.poke.R
import com.example.poke.data.Type
import com.example.poke.data.TypesItem
import com.example.poke.data.pokemonTypes
import com.example.poke.ui.theme.PokeTheme

@Composable
fun PokeCard(
    imageUrl: String,
    name: String,
    types: List<String>,
    isLarge: Boolean = true,
    modifier: Modifier = Modifier,
) {

    val colorMatrix = ColorMatrix()
    colorMatrix.setToSaturation(0f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .testTag("PokemonCard"),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if(isLarge) 32.dp else 12.dp,
                    vertical = 16.dp
                )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = if(isLarge) Alignment.Start else Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text(
                    text = name.capitalize(),
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if(!isLarge) AsyncImage(
                    model = imageUrl,
                    modifier = Modifier.size(124.dp).align(Alignment.CenterHorizontally),
                    contentDescription = name,
                    placeholder = painterResource(id = R.drawable.pokeball),
                )
                Spacer(modifier = Modifier.weight(1f))
                for (item in types){
                    PokemonType(type = item, iconUrl = pokemonTypes[item]?.iconImageUrl ?: "-")
                }
            }
            if(isLarge) AsyncImage(
                model = imageUrl,
                modifier = Modifier
                    .size(124.dp)
                    .align(Alignment.Bottom),
                contentDescription = name,
                placeholder = painterResource(id = R.drawable.pokeball),
            )
        }
    }
}

@Composable
fun PokemonType(type: String, iconUrl: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.secondary.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 4.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = iconUrl,
            placeholder = painterResource(id = R.drawable.pokeball),
            contentDescription = "type image icon",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = type, style = MaterialTheme.typography.body1)
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    PokeTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            LazyColumn(
                modifier = Modifier.padding(vertical = 32.dp),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                items(10) {
                    PokeCard(
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$it.png",
                        name = "name",
                        types = listOf("leaf")
                    )
                }
            }
        }
    }
}