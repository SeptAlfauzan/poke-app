package com.example.poke.di

import android.content.Context
import androidx.room.Room
import com.example.poke.data.database.FavPokemonDao
import com.example.poke.data.database.PokemonRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun providesFavPokemonDao(appDatabase: PokemonRoomDatabase): FavPokemonDao{
        return appDatabase.favoritePokemonDao()
    }

    @Provides
    @Singleton
    fun providePokemonDatabase(@ApplicationContext appContext: Context): PokemonRoomDatabase{
        return Room.databaseBuilder(
            appContext,
            PokemonRoomDatabase::class.java,
            "pokemon_database"
        ).build()
    }
}