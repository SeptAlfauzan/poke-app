package com.example.poke.data

import com.example.poke.config.ApiConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository {
    private val favoritePokemons: MutableSet<FavoritePokemon> = mutableSetOf()
    private val apiService = ApiConfig.getApiService()

    suspend fun getAllPokemons(): Flow<GetPokemonsResponse> = flowOf(apiService.getAll(limit = 100))

    suspend fun getDetailPokemon(id: Int): Flow<DetailPokemonResponse> = flowOf(apiService.getDetail(id))

    fun addFavoritePokemon(pokemon: FavoritePokemon): Flow<Boolean> = flowOf(favoritePokemons.add(pokemon))

    fun removeFavoritePokemon(pokemon: FavoritePokemon): Flow<Boolean> = flowOf(favoritePokemons.remove(pokemon))

    fun getFavoritesPokemon(): Flow<Set<FavoritePokemon>> = flowOf(favoritePokemons)

    fun checkIsFavorite(pokemon: FavoritePokemon): Boolean = favoritePokemons.contains(pokemon)

    companion object {

        @Volatile
        private var instance: Repository? = null

        fun getInstance(): Repository = instance ?: synchronized(this){
            Repository().apply {
                instance = this
            }
        }
    }
}