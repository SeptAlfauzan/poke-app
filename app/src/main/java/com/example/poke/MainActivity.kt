package com.example.poke

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.poke.config.ViewModelFactory
import com.example.poke.data.DetailPokemonResponse
import com.example.poke.data.EvolutionChain
import com.example.poke.data.EvolutionChainResponse
import com.example.poke.data.PokemonSpeciesResponse
import com.example.poke.data.database.FavoritePokemonWithTypes
import com.example.poke.data.database.Pokemon
import com.example.poke.data.database.Types
import com.example.poke.data.viewmodel.DetailPokemonEssentialsResponse
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.navigation.Screen
import com.example.poke.ui.screen.AboutScreen
import com.example.poke.ui.screen.DetailScreen
import com.example.poke.ui.screen.FavoriteScreen
import com.example.poke.ui.screen.HomeScreen
import com.example.poke.ui.theme.PokeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val pokemonViewModel: PokemonViewModel by viewModels()

                    PokeApp(
                        uiStatePokemon = pokemonViewModel.uiStatePokemons,
                        uiStateDetailPokemon = pokemonViewModel.uiStateDetailPokemon,
                        uiStatesPokemonSpecies = pokemonViewModel.uiStateSpecies,
                        uiStatesPokemonEvolutionChain = pokemonViewModel.uiStateEvolutionChain,
                        uiStateFavoritePokemonWithTypes = pokemonViewModel.uiStateFavoritePokemonsWithTypes,
                        getAllPokemon = { pokemonViewModel.getAllPokemons() },
                        getFavoritePokemon = { pokemonViewModel.getFavoritePokemon() },
                        getDetail = { id -> pokemonViewModel.getDetail(id) },
                        getSpecies = { id -> pokemonViewModel.getSpecies(id) },
                        getEvoChain = { id -> pokemonViewModel.getEvolutionChain(id) },
                        addFavorite = { pokemon, types ->
                            pokemonViewModel.addFavorite(
                                pokemon,
                                types
                            )
                        },
                        removeFavorite = { pokemon, types ->
                            pokemonViewModel.removeFavorite(
                                pokemon,
                                types
                            )
                        },
                        uiStatesFavoritePokemon = pokemonViewModel.uiStateIsFavoritePokemon,
                        checkIsFavoritePokemon = { pokemon ->
                            pokemonViewModel.checkFavoritePokemon(
                                pokemon
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun PokeApp(
    navController: NavHostController = rememberNavController(),
    uiStatePokemon: StateFlow<UiState<List<DetailPokemonEssentialsResponse>>>,
    uiStateFavoritePokemonWithTypes: StateFlow<UiState<List<FavoritePokemonWithTypes>>>,
    uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>>,
    uiStatesFavoritePokemon: StateFlow<UiState<Boolean>>,
    uiStatesPokemonSpecies: StateFlow<UiState<PokemonSpeciesResponse>>,
    uiStatesPokemonEvolutionChain: StateFlow<UiState<EvolutionChainResponse>>,
    checkIsFavoritePokemon: (Pokemon) -> Unit,
    getAllPokemon: () -> Unit,
    getFavoritePokemon: () -> Unit,
    getDetail: (Int) -> Unit,
    getSpecies: (Int) -> Unit,
    getEvoChain: (Int) -> Unit,
    addFavorite: (Pokemon, List<Types>) -> Unit,
    removeFavorite: (Pokemon, List<Types>) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedScreen by rememberSaveable { mutableStateOf(Screen.Home.route) }//for screen between home and favorite only
    val bottomBarHeight = 56
    val isHomeOrFavoriteScreen =
        (currentRoute == Screen.Home.route || currentRoute == Screen.Favorite.route)

    Scaffold(
        topBar = {
            if (isHomeOrFavoriteScreen) TopBar(navController = navController)
        },
        bottomBar = {
            if (isHomeOrFavoriteScreen) BottomNav(
                activeMenu = selectedScreen,
                setActiveMenu = { selectedScreen = it },
                navController = navController,
                modifier = Modifier.height(bottomBarHeight.dp)
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiStatePokemons = uiStatePokemon,
                    getAllPokemon = { getAllPokemon() },
                    navToDetail = { id, name ->
                        navController.navigate(Screen.Detail.createRoute(id, name))
                    }
                )
            }
            composable(Screen.Favorite.route) {
                FavoriteScreen(
                    uiStateFavoritePokemonsWithTypes = uiStateFavoritePokemonWithTypes,
                    getFavoritePokemon = { getFavoritePokemon() },
                    contentPadding = bottomBarHeight,
                    navToDetail = { id, name ->
                        navController.navigate(Screen.Detail.createRoute(id, name))
                    }
                )
            }
            composable(Screen.About.route) {
                AboutScreen()
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("pokemonId") {
                        type = NavType.IntType
                    },
                    navArgument("pokemonName") {
                        type = NavType.StringType
                    },

                    )
            ) {
                val id = it.arguments?.getInt("pokemonId") ?: 2
                val name = it.arguments?.getString("pokemonName") ?: ""
                val currentPokemon = Pokemon(id, name)

                Log.d("TAG", "PokeApp: $id")
                DetailScreen(
                    checkIsFavoritePokemon = { checkIsFavoritePokemon(currentPokemon) },
                    uiStateDetailPokemon = uiStateDetailPokemon,
                    uiStateSpeciesPokemon = uiStatesPokemonSpecies,
                    uiStateEvolutionChain = uiStatesPokemonEvolutionChain,
                    getDetail = { getDetail(id) },
                    getSpecies = { getSpecies(id) },
                    getEvoChain = { urlId -> getEvoChain(urlId) },
                    addFavorite = { pokemonTypes -> addFavorite(currentPokemon, pokemonTypes) },
                    removeFavorite = { pokemonTypes ->
                        removeFavorite(
                            currentPokemon,
                            pokemonTypes
                        )
                    },
                    navigateBack = { navController.navigateUp() },
                    isFavoriteState = uiStatesFavoritePokemon,
                )
            }
        }
    }
}

val listMenu = listOf(
    Pair(Screen.Home.route, Icons.Default.Home),
    Pair(Screen.Favorite.route, Icons.Default.Favorite)
)

@Composable
fun BottomNav(
    modifier: Modifier = Modifier,
    activeMenu: String = Screen.Home.route,
    navController: NavHostController,
    setActiveMenu: (String) -> Unit
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier,
        elevation = 8.dp
    ) {
        listMenu.forEachIndexed { _, menu ->
            BottomNavigationItem(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${menu.first}-Nav"
                    }
                    .clip(RoundedCornerShape(32.dp))
                    .background(if (activeMenu == menu.first) MaterialTheme.colors.primary else Color.Transparent)
                ,
                selected = activeMenu == menu.first,
                onClick = {
                    setActiveMenu(menu.first)
                    navController.navigate(menu.first)
                },
                label = {
                    Text(menu.first)
                },
                icon = {
                    Icon(imageVector = menu.second, contentDescription = menu.first)
                }
            )
        }
    }
}

@Composable
fun TopBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = "menu",
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            contentDescription = "about_page"
                        },
                    onClick = {
                        expanded = false
                        navController.navigate(Screen.About.route)
                    },
                ) {
                    Text("About")
                }
            }
        },
        title = {},
        elevation = 0.dp
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PokeTheme {
        val viewModel: PokemonViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository(LocalContext.current))
    )
        PokeApp(
            uiStatePokemon = viewModel.uiStatePokemons,
            uiStateDetailPokemon = viewModel.uiStateDetailPokemon,
            uiStateFavoritePokemonWithTypes = viewModel.uiStateFavoritePokemonsWithTypes,
            getAllPokemon = { viewModel.getAllPokemons() },
            getFavoritePokemon = { viewModel.getFavoritePokemon() },
            getDetail = { id -> viewModel.getDetail(id) },
            addFavorite = { pokemon, types -> viewModel.addFavorite(pokemon, types) },
            removeFavorite = { pokemon, types -> viewModel.removeFavorite(pokemon, types) },
            checkIsFavoritePokemon = { pokemon -> viewModel.checkFavoritePokemon(pokemon) },
            uiStatesFavoritePokemon = viewModel.uiStateIsFavoritePokemon,
            uiStatesPokemonSpecies = viewModel.uiStateSpecies,
            uiStatesPokemonEvolutionChain = viewModel.uiStateEvolutionChain,
            getSpecies = { viewModel.getSpecies(it) },
            getEvoChain = { viewModel.getEvolutionChain(it) },
        )
    }
}