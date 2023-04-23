package com.example.poke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.poke.data.FavoritePokemon
import com.example.poke.data.viewmodel.PokemonViewModel
import com.example.poke.di.Injection
import com.example.poke.ui.navigation.Screen
import com.example.poke.ui.screen.AboutScreen
import com.example.poke.ui.screen.DetailScreen
import com.example.poke.ui.screen.FavoriteScreen
import com.example.poke.ui.screen.HomeScreen
import com.example.poke.ui.theme.PokeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PokeApp()
                }
            }
        }
    }
}

@Composable
fun PokeApp(
    navController: NavHostController = rememberNavController(),
    pokemonViewModel: PokemonViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository())
    )
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedScreen by rememberSaveable{ mutableStateOf(0) }
    val bottomBarHeight = 56

    Scaffold(
        topBar = {
            if(currentRoute == Screen.Home.route) TopBar(navController = navController)
        },
        bottomBar = {
             if(currentRoute == Screen.Home.route) BottomNav(activeMenu = selectedScreen, setActiveMenu = { selectedScreen = it }, modifier = Modifier.height(bottomBarHeight.dp))
        }
    ) {innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(Screen.Home.route){
                Column{
                    when (selectedScreen) {
                        0 -> HomeScreen(
                            uiStatePokemons = pokemonViewModel.uiStatePokemons,
                            getAllPokemon = { pokemonViewModel.getAllPokemons() },
                            contentPadding = bottomBarHeight,
                            navToDetail = {id, name ->
                                navController.navigate(Screen.Detail.createRoute(id, name))
                            }
                        )
                        1 -> FavoriteScreen(
                            uiStateFavoritePokemons = pokemonViewModel.uiStateFavoritePokemons,
                            getFavoritePokemon = { pokemonViewModel.getFavoritePokemon() },
                            contentPadding = bottomBarHeight,
                            navToDetail = { id, name ->
                                navController.navigate(Screen.Detail.createRoute(id, name))
                            }
                        )
                    }
                }
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
                    uiStateDetailPokemon = pokemonViewModel.uiStateDetailPokemon,
                    getDetail = { pokemonViewModel.getDetail(id) },
                    addFavorite = { pokemonViewModel.addFavorite(currentPokemon) },
                    removeFavorite = { pokemonViewModel.removeFavorite(currentPokemon) },
                    navigateBack = { navController.navigateUp() },
                    isFavorite = pokemonViewModel.isFavoritePokemon(currentPokemon)
                )
            }
        }
    }
}

val listMenu = listOf(
    Pair("Home", Icons.Default.Home),
    Pair("Favorite", Icons.Default.Favorite)
)

@Composable
fun BottomNav(
    modifier: Modifier = Modifier,
    activeMenu: Int = 0,
    setActiveMenu: (Int) -> Unit
){
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier,
        elevation = 8.dp
    ) {
        listMenu.forEachIndexed{index, menu ->
            BottomNavigationItem(
                selected = activeMenu == index,
                onClick = {
                    setActiveMenu(index)
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
    navController: NavHostController
){
    var expanded by remember {
        mutableStateOf(false)
    }
    TopAppBar(
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
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.About.route)
                        }
                    ) {
                        Text("About")
                    }
            }
        },
        title = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PokeTheme {
        PokeApp()
    }
}