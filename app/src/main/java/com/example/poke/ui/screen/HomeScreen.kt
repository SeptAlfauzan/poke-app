package com.example.poke.ui.screen

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.poke.R
import com.example.poke.config.ViewModelFactory
import com.example.poke.data.DetailPokemonResponse
import com.example.poke.data.GetPokemonsResponse
import com.example.poke.data.PokemonItem
import com.example.poke.data.viewmodel.DetailPokemonEssentialsResponse
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular
import com.example.poke.ui.component.PokeBallBackground
import com.example.poke.ui.component.PokeCard
import com.example.poke.ui.component.SearchBar
import com.example.poke.ui.theme.PokeTheme
import kotlinx.coroutines.flow.StateFlow


@Composable
fun HomeScreen(
    uiStatePokemons: StateFlow<UiState<List<DetailPokemonEssentialsResponse>>>,
    getAllPokemon: () -> Unit,
    contentPadding: Int = 32,
    navToDetail: (Int, String) -> Unit = { i: Int, s: String -> },
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp)
                .zIndex(2f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            uiStatePokemons.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        getAllPokemon()
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            LoadingCircular()
                        }
                    }
                    is UiState.Success -> {
                        HomeContent(
                            contentPadding = contentPadding,
                            pokemon = uiState.data,
                            navToDetail = navToDetail
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = "Error ${uiState.errorMessage}",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
        PokeBallBackground(
            Modifier
                .clip(
                    RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp)
                )
                .background(MaterialTheme.colors.primary)
                .padding(vertical = 48.dp)

        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    contentPadding: Int,
    pokemon: List<DetailPokemonEssentialsResponse>,
    navToDetail: (Int, String) -> Unit = { Int, String -> },
) {
    var pokemons by rememberSaveable {
        mutableStateOf(pokemon)
    }

    val spriteBaseUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
    LazyColumn(
        modifier = Modifier.testTag("CardList"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = contentPadding.dp)
    ) {
        item {
            Column {
                Text(
                    text = stringResource(R.string.poke), style = MaterialTheme.typography.h3.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Simple app to help you expand your knowledge about pokemon.",
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.White
                    )
                )
            }
        }
        stickyHeader {
            SearchBar(
                onChange = { keyword ->
                    pokemons = pokemon.filter { it.name.contains(keyword.lowercase()) }
                    if (keyword.isEmpty()) pokemons = pokemon
                },
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        if (pokemons.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.no_pokemon),
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface.copy(0.3f)
                )
            }
        } else {
            items(items = pokemons, key = { it.name }) { pokemon ->
                val id = pokemon.id
                PokeCard(
                    imageUrl = "$spriteBaseUrl$id.png",
                    name = pokemon.name,
                    types = pokemon.types.map { it.type?.name ?: "unknown" },
                    modifier = Modifier
                        .clickable { navToDetail(id, pokemon.name) }
                        .fillMaxWidth()
                        .animateItemPlacement(tween(durationMillis = 100)),
                )
            }
        }
    }

}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun HomeScreenPreview() {
    PokeTheme {
        val viewModel: PokemonViewModel by viewModel()
        HomeScreen(
            uiStatePokemons = viewModel.uiStatePokemons,
            getAllPokemon = { viewModel.getAllPokemons() },
            contentPadding = 32,
            navToDetail = { _, _ -> }
        )
    }
}
