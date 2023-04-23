package com.example.poke.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.poke.R
import com.example.poke.config.ViewModelFactory
import com.example.poke.data.*
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular
import com.example.poke.ui.theme.PokeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetailScreen(
    isFavorite: Boolean,
    getDetail: () -> Unit,
    navigateBack: () -> Unit = {},
    addFavorite: () -> Unit = {},
    removeFavorite: () -> Unit = {},
    uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>> = MutableStateFlow(UiState.Loading)
){
    Scaffold(
        topBar = {
            TopBar(
                navigateBack = navigateBack,
                setFavorite = { addFavorite() },
                removeFavorite = { removeFavorite() },
                isFavorite = isFavorite
            )
        }
    ) {innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            uiStateDetailPokemon.collectAsState(initial = UiState.Loading).value.let{uiState ->
                when(uiState){
                    is UiState.Loading -> {
                        getDetail()
                        LoadingCircular()
                    }
                    is UiState.Success -> DetailContent(
                        name = uiState.data.name ?: "",
                        imageUrl = uiState.data.sprites?.other?.officialArtwork?.frontDefault ?: "",
                        stats = uiState.data.stats as List<StatsItem>,
                        type = uiState.data.types as List<TypesItem>
                    )
                    is UiState.Error -> Text(text = "Error ${uiState.errorMessage}")
                }
            }
        }
    }
}

@Composable
fun DetailContent(
    name: String,
    imageUrl: String,
    stats: List<StatsItem>,
    type: List<TypesItem>,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box{
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(bottomStartPercent = 100, bottomEndPercent = 100))
                .background(MaterialTheme.colors.secondary)
            )
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .size(224.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        Text(
            text = name.capitalize(),
            style = MaterialTheme.typography.h6.copy(
                color = MaterialTheme.colors.onSecondary
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp, bottom = 16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ){
            items(type) {
                TypeCapsule(type = it.type?.name ?: "")
            }
        }

        Text(
            text = stringResource(R.string.base_stats),
            style = MaterialTheme.typography.h6.copy(
                color = MaterialTheme.colors.onSecondary
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp, bottom = 16.dp)
        )
        stats.map {
            Spacer(modifier = Modifier.height(8.dp))
            Stats(statName = it.stat?.name.toString(), value = it.baseStat ?: 0)
        }
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ){
//            items(stats) {
//                Stats(statName = it.stat?.name.toString(), value = it.baseStat ?: 0)
//            }
//        }
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    setFavorite: () -> Unit,
    removeFavorite: () -> Unit,
    navigateBack: () -> Unit = {},
    isFavorite: Boolean,
){
    var isFavorite by rememberSaveable{
        mutableStateOf(isFavorite)
    }

    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { navigateBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = "back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {
                isFavorite = !isFavorite
                if(isFavorite) setFavorite() else removeFavorite()
            }) {
                Icon(
                    painter = painterResource(id = if(isFavorite) R.drawable.baseline_star_24 else R.drawable.baseline_star_outline_24),
                    contentDescription = "favorite",
                    tint = Color.White
                )
            }
        },
        title = { Text(text = "") },
        backgroundColor = MaterialTheme.colors.secondary,
        elevation = 0.dp
    )
}

@Composable
fun TypeCapsule(
    modifier: Modifier = Modifier,
    type: String
){
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        elevation = 8.dp
    ) {
        Text(
            text = type,
            style = MaterialTheme.typography.h6.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun Stats(
    modifier: Modifier = Modifier,
    statName: String,
    value: Int
){
    Row(modifier = modifier
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = statName,
            style = MaterialTheme.typography.body1.copy(
                textAlign = TextAlign.End,
                color = MaterialTheme.colors.secondaryVariant
            ),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Box(modifier = Modifier
            .width(2.dp)
            .height(42.dp)
            .clip(RoundedCornerShape(30))
            .background(MaterialTheme.colors.secondary)
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.secondaryVariant
            ),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically))
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewDetailScreen(
    pokemonViewModel: PokemonViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository())
    )
){
    PokeTheme() {
        DetailScreen(isFavorite = false, getDetail = {pokemonViewModel.getDetail(2)})
    }
}