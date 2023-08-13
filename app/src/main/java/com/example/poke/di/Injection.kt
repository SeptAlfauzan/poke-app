package com.example.poke.di

import android.content.Context
import com.example.poke.data.Repository
import com.example.poke.data.database.PokemonRoomDatabase

object Injection {
    fun provideRepository(context: Context): Repository{
        val database = PokemonRoomDatabase.getInstance(context)
        val favPokemonDao = database.favoritePokemonDao()
        return Repository.getInstance(favPokemonDao)
    }
}