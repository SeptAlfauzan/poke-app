package com.example.poke.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.poke.R
import com.example.poke.data.database.FavoritePokemonWithTypes
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular
import com.example.poke.ui.component.PokeBallBackground
import com.example.poke.ui.component.PokeCard
import com.example.poke.ui.component.SearchBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    contentPadding: Int = 32,
    getFavoritePokemon: () -> Unit,
    uiStateFavoritePokemonsWithTypes: StateFlow<UiState<List<FavoritePokemonWithTypes>>> = MutableStateFlow(
        UiState.Loading
    ),
    navToDetail: (Int, String) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp)
                .zIndex(2f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Favorite Pokemon",
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            uiStateFavoritePokemonsWithTypes.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        getFavoritePokemon()
                        LoadingCircular(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    is UiState.Success -> {
                        FavoriteContent(
                            contentPadding = contentPadding,
                            uiState.data.toList(),
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


@Composable
fun FavoriteContent(
    contentPadding: Int,
    favPokemons: List<FavoritePokemonWithTypes>,
    navToDetail: (Int, String) -> Unit,
) {
    var filteredFavorite by rememberSaveable {
        mutableStateOf(favPokemons)
    }
    val spriteBaseUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
    Column {
        SearchBar(onChange = {keyword ->
            filteredFavorite = favPokemons.filter { it.pokemon.name.contains(keyword.lowercase()) }
            if (keyword.isEmpty()) filteredFavorite = favPokemons
        })

        if (filteredFavorite.isEmpty()) {
            Text(
                stringResource(R.string.no_favorite),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.surface.copy(alpha = 0.3f),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = contentPadding.dp)
            ) {
                items(items = filteredFavorite, key = { it.pokemon.id }) { item ->
                    PokeCard(
                        isLarge = false,
                        modifier = Modifier.clickable {
                            navToDetail(
                                item.pokemon.id,
                                item.pokemon.name
                            )
                        },
                        imageUrl = "$spriteBaseUrl${item.pokemon.id}.png",
                        name = item.pokemon.name,
                        types = item.types.map { it.name }
                    )
                }
            }
        }
    }
}