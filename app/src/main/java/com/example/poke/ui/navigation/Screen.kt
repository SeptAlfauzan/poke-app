package com.example.poke.ui.navigation

sealed class Screen(val route: String){
    object Home: Screen("home")
    object Detail: Screen("detail/{pokemonId}/{pokemonName}"){
        fun createRoute(pokemonId: Int, pokemonName: String) = "detail/$pokemonId/$pokemonName"
    }
    object About: Screen("about")
}
