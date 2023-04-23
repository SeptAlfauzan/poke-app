package com.example.poke.ui.screen

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.ui.component.PokeCard
import com.example.poke.ui.component.SearchBar
import com.example.poke.ui.theme.PokeTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.poke.R
import com.example.poke.config.ViewModelFactory
import com.example.poke.data.GetPokemonsResponse
import com.example.poke.data.PokemonItem
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular
import kotlinx.coroutines.flow.StateFlow


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiStatePokemons: StateFlow<UiState<GetPokemonsResponse>>,
    getAllPokemon: () -> Unit,
    contentPadding: Int = 32,
    navToDetail: (Int, String) -> Unit = { i: Int, s: String -> },
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Poke", style = MaterialTheme.typography.h3)
        Text(text = "Simple app to help you expand your knowledge about pokemon.", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(16.dp))

        uiStatePokemons.collectAsState(initial = UiState.Loading).value.let{uiState ->
            when(uiState){
                is UiState.Loading -> {
                    getAllPokemon()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    contentPadding: Int,
    pokemon: List<PokemonItem>,
    navToDetail: (Int, String) -> Unit = { i: Int, s: String -> },
){
    var pokemons by rememberSaveable {
        mutableStateOf(pokemon)
    }
    SearchBar(
        onChange = { keyword ->
            pokemons = pokemon.filter { it.name.contains(keyword.lowercase())  }
            if(keyword.isEmpty()) pokemons = pokemon
        }
    )
    if(pokemons.size == 0){
        Text(
            stringResource(R.string.no_pokemon),
            style = MaterialTheme.typography.body1
        )
    }else{
        val spriteBaseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = contentPadding.dp)
        ){
            items( items = pokemons, key={ it.name }) { pokemon ->

                val id = pokemon.url.split("/").let { it[it.size - 2] }.toInt()
                Log.d("TAG", "HomeContent: ${pokemon.url.split("/")}")

                PokeCard(
                    modifier = Modifier
                        .clickable { navToDetail(id, pokemon.name) }
                        .animateItemPlacement(tween(durationMillis = 100)),
                    imageUrl = "$spriteBaseUrl$id.png",
                    name = pokemon.name ?: ""
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun HomeScreenPreview(){
    PokeTheme {
        val viewModel: PokemonViewModel = viewModel(
            factory = ViewModelFactory(Injection.provideRepository())
        )
        HomeScreen(
            uiStatePokemons = viewModel.uiStatePokemons,
            getAllPokemon = { viewModel.getAllPokemons() },
            contentPadding = 32,
            navToDetail = { _, _ -> }
        )
    }
}
