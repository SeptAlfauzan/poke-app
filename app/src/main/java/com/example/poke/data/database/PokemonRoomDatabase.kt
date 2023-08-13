package com.example.poke.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Pokemon::class, Types::class],
    version = 1,
    exportSchema = false
)

abstract class PokemonRoomDatabase : RoomDatabase(){

    abstract fun favoritePokemonDao(): FavPokemonDao

    companion object {
        @Volatile
        private var INSTANCE: PokemonRoomDatabase? = null

        fun getInstance(context: Context): PokemonRoomDatabase {
            synchronized(this){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PokemonRoomDatabase::class.java,
                        "pokemon_database"
                    ).build()
                }
            }
            return INSTANCE as PokemonRoomDatabase
        }
    }
}