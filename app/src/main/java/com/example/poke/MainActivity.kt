package com.example.poke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
import com.example.poke.data.FavoritePokemon
import com.example.poke.data.GetPokemonsResponse
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.di.Injection
import com.example.poke.ui.common.UiState
import com.example.poke.ui.navigation.Screen
import com.example.poke.ui.screen.AboutScreen
import com.example.poke.ui.screen.DetailScreen
import com.example.poke.ui.screen.FavoriteScreen
import com.example.poke.ui.screen.HomeScreen
import com.example.poke.ui.theme.PokeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel: PokemonViewModel = viewModel(
                        factory = ViewModelFactory(Injection.provideRepository())
                    )
                    PokeApp(
                        uiStatePokemons = viewModel.uiStatePokemons,
                        uiStateDetailPokemon = viewModel.uiStateDetailPokemon,
                        uiStateFavoritePokemons = viewModel.uiStateFavoritePokemons,
                        getAllPokemon = { viewModel.getAllPokemons() },
                        getFavoritePokemon = { viewModel.getFavoritePokemon() },
                        getDetail = { id -> viewModel.getDetail(id) } ,
                        addFavorite = { pokemon -> viewModel.addFavorite(pokemon) } ,
                        removeFavorite =  { pokemon -> viewModel.removeFavorite(pokemon) } ,
                        isFavoritePokemon =  { pokemon -> viewModel.isFavoritePokemon(pokemon) } ,
                    )
                }
            }
        }
    }
}

@Composable
fun PokeApp(
    navController: NavHostController = rememberNavController(),
    uiStatePokemons: StateFlow<UiState<GetPokemonsResponse>>,
    uiStateFavoritePokemons: StateFlow<UiState<Set<FavoritePokemon>>> = mutableStateOf(UiState.Loading) as StateFlow<UiState<Set<FavoritePokemon>>>,
    uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>> = MutableStateFlow(UiState.Loading),
    getAllPokemon: () -> Unit = {},
    getFavoritePokemon: () -> Unit = {},
    getDetail: (Int) -> Unit = {},
    addFavorite: (FavoritePokemon) -> Unit = {},
    removeFavorite: (FavoritePokemon) -> Unit = {},
    isFavoritePokemon: (FavoritePokemon) -> Boolean,
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedScreen by rememberSaveable{ mutableStateOf(Screen.Home.route) }//for screen between home and favorite only
    val bottomBarHeight = 56
    val isHomeOrFavoriteScreen = (currentRoute == Screen.Home.route || currentRoute == Screen.Favorite.route)

    Scaffold(
        topBar = {
            if(isHomeOrFavoriteScreen) TopBar(navController = navController)
        },
        bottomBar = {
            if(isHomeOrFavoriteScreen) BottomNav(
                activeMenu = selectedScreen,
                setActiveMenu = { selectedScreen = it },
                navController = navController,
                modifier = Modifier.height(bottomBarHeight.dp))
        }
    ) {innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(Screen.Home.route){
                HomeScreen(
                    uiStatePokemons = uiStatePokemons,
                    getAllPokemon = { getAllPokemon() },
                    contentPadding = bottomBarHeight,
                    navToDetail = {id, name ->
                        navController.navigate(Screen.Detail.createRoute(id, name))
                    }
                )
            }
            composable(Screen.Favorite.route){
                FavoriteScreen(
                    uiStateFavoritePokemons = uiStateFavoritePokemons,
                    getFavoritePokemon = { getFavoritePokemon() },
                    contentPadding = bottomBarHeight,
                    navToDetail = { id, name ->
                        navController.navigate(Screen.Detail.createRoute(id, name))
                    }
                )
            }
            composable(Screen.About.route){
                AboutScreen()
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("pokemonId"){
                        type = NavType.IntType
                    },
                    navArgument("pokemonName"){
                        type = NavType.StringType
                    },

                )
            ){
                val id = it.arguments?.getInt("pokemonId") ?: 2
                val name = it.arguments?.getString("pokemonName") ?: ""
                val currentPokemon = FavoritePokemon(id, name)

                DetailScreen(
                    uiStateDetailPokemon = uiStateDetailPokemon,
                    getDetail = { getDetail(id) },
                    addFavorite = { addFavorite(currentPokemon) },
                    removeFavorite = { removeFavorite(currentPokemon) },
                    navigateBack = { navController.navigateUp() },
                    isFavorite = isFavoritePokemon(currentPokemon)
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
){
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier,
        elevation = 8.dp
    ) {
        listMenu.forEachIndexed{index, menu ->
            BottomNavigationItem(
                modifier = Modifier.semantics(mergeDescendants = true){
                      contentDescription = "${menu.first}-Nav"
                },
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
){
    var expanded by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.background ,
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
                            .semantics(mergeDescendants = true){
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
            factory = ViewModelFactory(Injection.provideRepository())
        )
        PokeApp(
            uiStatePokemons = viewModel.uiStatePokemons,
            uiStateDetailPokemon = viewModel.uiStateDetailPokemon,
            uiStateFavoritePokemons = viewModel.uiStateFavoritePokemons,
            getAllPokemon = { viewModel.getAllPokemons() },
            getFavoritePokemon = { viewModel.getFavoritePokemon() },
            getDetail = { id -> viewModel.getDetail(id) } ,
            addFavorite = { pokemon -> viewModel.addFavorite(pokemon) } ,
            removeFavorite =  { pokemon -> viewModel.removeFavorite(pokemon) } ,
            isFavoritePokemon =  { pokemon -> viewModel.isFavoritePokemon(pokemon) } ,
        )
    }
}