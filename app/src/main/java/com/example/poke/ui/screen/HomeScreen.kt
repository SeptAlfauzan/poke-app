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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.ui.component.PokeCard
import com.example.poke.ui.component.SearchBar
import com.example.poke.ui.theme.PokeTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.poke.config.ViewModelFactory
import com.example.poke.data.PokemonItem
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular


@Composable
fun HomeScreen(
    contentPadding: Int = 32,
    navToDetail: (Int, String) -> Unit = { i: Int, s: String -> },
    viewModel: PokemonViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository())
    )
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Poke", style = MaterialTheme.typography.h3)
        Text(text = "Simple app to help you expand your knowledge about pokemon.", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar()


        viewModel.uiStatePokemons.collectAsState(initial = UiState.Loading).value.let{uiState ->
            when(uiState){
                is UiState.Loading -> {
                    viewModel.getAllPokemons()
                    LoadingCircular(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UiState.Success -> {
                    HomeContent(contentPadding = contentPadding, uiState.data.results as List<PokemonItem>, navToDetail = navToDetail)
                }
                is UiState.Error -> {
                    Text(text = "Error ${uiState.errorMessage}", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }


    }
}

@Composable
fun HomeContent(
    contentPadding: Int,
    pokemons: List<PokemonItem>,
    navToDetail: (Int, String) -> Unit = { i: Int, s: String -> },
){
    val spriteBaseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = contentPadding.dp)
    ){
        itemsIndexed( items = pokemons) { index, pokemon ->
            PokeCard(
                modifier = Modifier.clickable { navToDetail(index + 1, pokemon.name) },
                imageUrl = "$spriteBaseUrl${index + 1}.png",
                name = pokemon.name ?: ""
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun HomeScreenPreview(){
    PokeTheme {
        HomeScreen()
    }
}
