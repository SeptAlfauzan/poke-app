package com.example.poke.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavPokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPokemon(pokemon: Pokemon): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPokemonType(types: List<Types>)

    @Delete
    suspend fun deletePokemon(pokemon: Pokemon): Int

    @Delete
    suspend fun deletePokemonType(pokemon: List<Types>)

    @Query("SELECT * FROM pokemons")
    fun getAll(): Flow<List<FavoritePokemonWithTypes>>

    @Query("SELECT * FROM pokemons WHERE id = :id")
    fun findPokemonById(id: Int): Flow<FavoritePokemonWithTypes?>
}