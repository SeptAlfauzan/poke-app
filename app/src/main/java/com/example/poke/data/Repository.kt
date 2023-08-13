package com.example.poke.data

import com.example.poke.config.ApiConfig
import com.example.poke.data.database.FavPokemonDao
import com.example.poke.data.database.FavoritePokemonWithTypes
import com.example.poke.data.database.Types
import com.example.poke.data.viewmodel.DetailPokemonEssentialsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class Repository @Inject constructor(private val favPokemonDao: FavPokemonDao) {
    private val favoritePokemonWithTypes: MutableSet<FavoritePokemonWithTypes> = mutableSetOf()
    private val apiService = ApiConfig.getApiService()
    private val TAG = this::class.java.simpleName

    suspend fun getAllPokemon(): Flow<List<DetailPokemonEssentialsResponse>> = flow {
        val response = apiService.getAll(limit = 20, offset = 0)
        val mappedResult = response.results!!.map { item ->
            apiService.getDetailEssentials(item!!.url.split("/").let { it[it.size - 2] }.toInt())
        }
        emit(mappedResult)
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailPokemon(id: Int): Flow<DetailPokemonResponse> = flowOf(apiService.getDetail(id))
    suspend fun getPokemonSpecies(id: Int): Flow<PokemonSpeciesResponse> = flowOf(apiService.getSpecies(id))
    suspend fun getPokemonEvolutionChain(id: Int): Flow<EvolutionChainResponse> = flowOf(apiService.getEvolutionChain(id))

    suspend fun addFavoritePokemon(pokemon: com.example.poke.data.database.Pokemon, types: List<Types>): Flow<Boolean> {
        val insertedRow = favPokemonDao.addPokemon(pokemon)
        val insertType = favPokemonDao.addPokemonType(types)
        return flowOf(insertedRow > 0)
    }

    suspend fun removeFavoritePokemon(pokemon: com.example.poke.data.database.Pokemon, types: List<Types>): Flow<Boolean> {
        val deletedRow = favPokemonDao.deletePokemon(pokemon)
        val deletedPokemonTypes = favPokemonDao.deletePokemonType(types)
        return flowOf(deletedRow > 0)
    }

    fun getFavoritesPokemon(): Flow<List<FavoritePokemonWithTypes>> = favPokemonDao.getAll()

    fun checkIsFavorite(pokemon: com.example.poke.data.database.Pokemon): Flow<FavoritePokemonWithTypes?> =
        favPokemonDao.findPokemonById(pokemon.id)

    companion object {

        @Volatile
        private var instance: Repository? = null

        fun getInstance(favPokemonDao: FavPokemonDao): Repository = instance ?: synchronized(this) {
            Repository(favPokemonDao).apply {
                instance = this
            }
        }
    }
}