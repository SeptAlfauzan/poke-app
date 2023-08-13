package com.example.poke.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.poke.R
import com.example.poke.config.ViewModelFactory
import com.example.poke.data.*
import com.example.poke.data.database.Types
import com.example.poke.data.database.toTypes
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.component.LoadingCircular
import com.example.poke.ui.component.PokemonType
import com.example.poke.ui.theme.PokeTheme
import com.example.poke.utils.getTwoDominantColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    checkIsFavoritePokemon: () -> Unit = {},
    isFavoriteState: StateFlow<UiState<Boolean>>,
    getDetail: () -> Unit,
    getSpecies: () -> Unit,
    getEvoChain: (id: Int) -> Unit,
    addFavorite: (types: List<Types>) -> Unit = {},
    removeFavorite: (types: List<Types>) -> Unit = {},
    uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>>,
    uiStateSpeciesPokemon: StateFlow<UiState<PokemonSpeciesResponse>>,
    uiStateEvolutionChain: StateFlow<UiState<EvolutionChainResponse>>,
    navigateBack: () -> Unit = {},
) {
    var topBarColor: Int? by remember { mutableStateOf(null) }
    var evolutionChainId: Int? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopBar(
                navigateBack = navigateBack,
                modifier = Modifier.background(
                    color = Color(
                        topBarColor ?: MaterialTheme.colors.background.hashCode()
                    )
                ),
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            uiStateDetailPokemon.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        getDetail()
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LoadingCircular()
                        }
                    }
                    is UiState.Success -> DetailContent(
                        id = uiState.data.id ?: -1,
                        name = uiState.data.name ?: "",
                        imageUrl = uiState.data.sprites?.other?.officialArtwork?.frontDefault ?: "",
                        stats = uiState.data.stats as List<StatsItem>,
                        types = uiState.data.types as List<TypesItem>,
                        updateTopBarColor = { topBarColor = it },
                        evoChainId = evolutionChainId,
                        getEvoChain = getEvoChain,
                        evoChainUiState = uiStateEvolutionChain,
                        isFavoriteState = isFavoriteState,
                        checkIsFavoritePokemon = checkIsFavoritePokemon,
                        addFavorite = addFavorite,
                        removeFavorite = removeFavorite,
                    )
                    is UiState.Error -> Text(text = "Error ${uiState.errorMessage}")
                }
            }

            uiStateSpeciesPokemon.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        getSpecies()
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Get additional data...")
                        }
                    }
                    is UiState.Success -> {
                        val url = uiState.data.evolutionChain?.url
                        val segments = url?.split("/")
                        val id = segments?.get(segments.size - 2)?.toInt()
                        Log.d("TAG", "DetailScreen evo url: $id")
                        evolutionChainId = id
                    }
                    is UiState.Error -> Text(text = "Error ${uiState.errorMessage}")
                }
            }
        }
    }
}

data class PagerContentData(val name: String, val content: @Composable () -> Unit)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailContent(
    name: String,
    imageUrl: String,
    stats: List<StatsItem>,
    types: List<TypesItem>,
    updateTopBarColor: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    evoChainId: Int?,
    getEvoChain: (id: Int) -> Unit,
    evoChainUiState: StateFlow<UiState<EvolutionChainResponse>>,
    isFavoriteState: StateFlow<UiState<Boolean>>,
    addFavorite: (List<Types>) -> Unit,
    removeFavorite: (types: List<Types>) -> Unit,
    id: Int,
    checkIsFavoritePokemon: () -> Unit
) {
    var dominantColor: Int? by remember {
        mutableStateOf(null)
    }
//    var isFavorite by remember{ mutableStateOf(isFavoriteState.value) }
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0)
    val pagerContent = listOf<PagerContentData>(
        PagerContentData(stringResource(R.string.base_stats)) { StatsContainer(stats) },
        PagerContentData(stringResource(R.string.evolution_chain)) {
            EvolutionChainContainer(
                evoChainId = evoChainId,
                getEvoChain = getEvoChain,
                evoChainUiState = evoChainUiState
            )
        },
    )

    LaunchedEffect(imageUrl) {
        getTwoDominantColors(
            imageUrl = imageUrl,
            context = context,
            updateState = {
                dominantColor = it
                updateTopBarColor(it)
            })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(
                                    dominantColor ?: MaterialTheme.colors.primaryVariant.hashCode()
                                ),
                                MaterialTheme.colors.background,
                            )
                        )
                    )
            )
            Box(
                Modifier
                    .size(224.dp)
                    .align(Alignment.BottomCenter)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier.fillMaxSize()
                )
                isFavoriteState.collectAsState(initial = UiState.Loading).value.let { uiState ->
                    when (uiState) {
                        is UiState.Success -> {
                            Log.d("TAG", "DetailContent: ${uiState.data}")
                            FloatingActionButton(
                                onClick = {
                                    val typesItem = types.map { it.toTypes(id) }
                                    if (uiState.data) removeFavorite(typesItem) else addFavorite(typesItem)
                                },
                                backgroundColor = MaterialTheme.colors.primary,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.data) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "favorite icon"
                                )
                            }
                        }
                        is UiState.Loading -> checkIsFavoritePokemon()
                        is UiState.Error -> Toast.makeText(
                            context,
                            uiState.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

        Text(
            text = name.capitalize(),
            style = MaterialTheme.typography.h4.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 112.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(types) {
                val name = it.type!!.name
                PokemonType(type = name, iconUrl = pokemonTypes[name]!!.iconImageUrl)
            }
        }

        PagerIndicator(
            listPagerContentData = pagerContent,
            pagerState = pagerState,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 40.dp)
        )
        HorizontalPager(pageCount = 2, state = pagerState) { index ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                pagerContent[index].content()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerIndicator(
    listPagerContentData: List<PagerContentData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Row(modifier.fillMaxWidth()) {
        for ((index, pagerContentData) in listPagerContentData.withIndex()) {
            Text(
                text = pagerContentData.name,
                style = MaterialTheme.typography.body1.copy(
                    textAlign = TextAlign.Center,
                    color = if (pagerState.currentPage == index) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface.copy(
                        alpha = 0.3f
                    )
                ),
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (pagerState.currentPage == index) MaterialTheme.colors.primary else Color.Transparent)
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun StatsContainer(stats: List<StatsItem>, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.base_stats),
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp, bottom = 16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stats) {
                Stats(statName = it.stat?.name.toString(), value = it.baseStat ?: 0)
            }
        }
    }
}

@Composable
private fun EvolutionChainContainer(
    modifier: Modifier = Modifier,
    evoChainId: Int?,
    getEvoChain: (id: Int) -> Unit,
    evoChainUiState: StateFlow<UiState<EvolutionChainResponse>>
) {

    Column(modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.evolution_chain),
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp, bottom = 16.dp)
        )

        evoChainUiState.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    evoChainId?.let(getEvoChain)
                    LoadingCircular(Modifier.align(Alignment.CenterHorizontally))
                }
                is UiState.Success -> {

                    var evoChain = uiState.data.chain

                    val evoChainList = getEvoChainList(listOf(evoChain))
                    Log.d("TAG", "EvolutionChainContainer: $evoChainList")

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(evoChainList) {

                            val url = it.second
                            val id = url.getIdFromUrl()
                            val currentIndex = evoChainList.indexOf(it)
                            val isNotLastIndex = currentIndex != evoChainList.size - 1
                            val isNextItemAvailable = currentIndex + 1 != evoChainList.size

                            if (isNotLastIndex) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SpeciesCards(id, it.first)

                                    if (isNextItemAvailable) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = stringResource(R.string.arrow_evolution),
                                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                                        )
                                        val item = evoChainList[currentIndex + 1]
                                        val id = item.second.getIdFromUrl()
                                        val name = item.first
                                        SpeciesCards(id, item.first)
                                    }

                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(text = uiState.errorMessage)
                }
            }
        }
    }
}

fun String.getIdFromUrl(): Int? {
    val segments = this?.split("/")
    val id = segments?.get(segments.size - 2)?.toInt()
    return id
}

fun getEvoChainList(
    chain: List<EvolvesToItem>,
    listChain: MutableList<Pair<String, String>> = mutableListOf()
): List<Pair<String, String>> {
    val species = chain[0].species
    val item = Pair(species.name, species.url)
    listChain.add(item)

    if (chain[0].evolvesTo.isEmpty()) return listChain
    //recursive
    return getEvoChainList(chain[0].evolvesTo, listChain)
}

@Composable
private fun SpeciesCards(id: Int?, name: String) {
    val spriteBaseUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "$spriteBaseUrl$id.png",
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .size(124.dp)
            )
            Text(text = name, style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier.background(Color.Transparent).padding(top = 32.dp),
        navigationIcon = {
            IconButton(
                modifier = Modifier.semantics {
                    contentDescription = "nav-back"
                },
                onClick = { navigateBack() },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = "back",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        title = { Text(text = "") },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}

@Composable
private fun Stats(
    modifier: Modifier = Modifier,
    statName: String,
    value: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.secondary.copy(alpha = 0.1f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = statName,
            style = MaterialTheme.typography.body1.copy(
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .weight(1.5f)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colors.secondary.copy(0.3f))
                .padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.secondaryVariant
            ),
            modifier = Modifier
                .weight(0.5f)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewDetailScreen(
) {
    PokeTheme() {
        val pokemonViewModel: PokemonViewModel = viewModel(
            factory = ViewModelFactory(Injection.provideRepository(LocalContext.current))
        )
        DetailScreen(
            isFavoriteState = MutableStateFlow(UiState.Success(false)),
            getDetail = { pokemonViewModel.getDetail(1) },
            getSpecies = { pokemonViewModel.getSpecies(1) },
            getEvoChain = { pokemonViewModel.getEvolutionChain(it) },
            uiStateSpeciesPokemon = pokemonViewModel.uiStateSpecies,
            uiStateDetailPokemon = pokemonViewModel.uiStateDetailPokemon,
            uiStateEvolutionChain = pokemonViewModel.uiStateEvolutionChain,
        )
    }
}