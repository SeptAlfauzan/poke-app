package com.example.poke.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.poke.R
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular
import com.example.poke.ui.component.PokeCard
import com.example.poke.data.FavoritePokemon
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    contentPadding: Int = 32,
    getFavoritePokemon: () -> Unit,
    navToDetail: (Int, String) -> Unit,
    uiStateFavoritePokemons: StateFlow<UiState<Set<FavoritePokemon>>> = mutableStateOf(UiState.Loading) as StateFlow<UiState<Set<FavoritePokemon>>>,
){
    Column(modifier = modifier
        .fillMaxSize()
        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Favorite Pokemon", style = MaterialTheme.typography.h3)
        Spacer(modifier = Modifier.height(16.dp))

        uiStateFavoritePokemons.collectAsState(initial = UiState.Loading).value.let{ uiState ->
            when(uiState){
                is UiState.Loading -> {
                    getFavoritePokemon()
                    LoadingCircular(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UiState.Success -> {
                    FavoriteContent(contentPadding = contentPadding, uiState.data.toList(), navToDetail = navToDetail)
                }
                is UiState.Error -> {
                    Text(text = "Error ${uiState.errorMessage}", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}


@Composable
fun FavoriteContent(
    contentPadding: Int,
    pokemons: List<FavoritePokemon>,
    navToDetail: (Int, String) -> Unit,
){
    val spriteBaseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"

    if(pokemons.size == 0){
        Text(
            stringResource(R.string.no_favorite),
            style = MaterialTheme.typography.body1
        )
    }else{
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = contentPadding.dp)
        ){
            itemsIndexed( items = pokemons) { index, pokemon ->
                PokeCard(
                    modifier = Modifier.clickable { navToDetail(index + 1, pokemon.name) },
                    imageUrl = "$spriteBaseUrl${pokemon.id}.png",
                    name = pokemon.name ?: ""
                )
            }
        }
    }

}