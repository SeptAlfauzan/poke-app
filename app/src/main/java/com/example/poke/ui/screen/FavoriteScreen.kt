package com.example.poke.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.poke.config.ViewModelFactory
import com.example.poke.data.PokemonItem
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular
import com.example.poke.ui.component.PokeCard
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    contentPadding: Int = 32,
    navToDetail: (Int, String) -> Unit,
    viewModel: PokemonViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository())
    )
){
    Column {
        viewModel.uiStateFavoritePokemons.collectAsState(initial = UiState.Loading).value.let{ uiState ->
            when(uiState){
                is UiState.Loading -> {
                    viewModel.getFavoritePokemon()
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
    pokemons: List<PokemonItem>,
    navToDetail: (Int, String) -> Unit,
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = contentPadding.dp)
    ){
        itemsIndexed( items = pokemons) { index, pokemon ->
            PokeCard(
                modifier = Modifier.clickable { navToDetail(index + 1, pokemon.name) },
                imageUrl = pokemon.url,
                name = pokemon.name ?: ""
            )
        }
    }
}