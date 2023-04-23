package com.example.poke.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.poke.R
import com.example.poke.ui.theme.PokeTheme

@Composable
fun PokeCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    name: String
){
    Card(
        modifier = modifier
            .widthIn(min = 240.dp, max = 320.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
        ,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(
                    horizontal = 32.dp,
                    vertical = 16.dp
                )
        ){
            AsyncImage(
                model = imageUrl,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1f)
                ,
                contentDescription = name,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name.capitalize(),
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview(){
    PokeTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ){
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 32.dp),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ){
                items(10) {
                    PokeCard(
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$it.png",
                        name = "test"
                    )
                }
            }
        }
    }
}