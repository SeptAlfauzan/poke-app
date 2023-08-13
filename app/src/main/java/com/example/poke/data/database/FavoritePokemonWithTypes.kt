package com.example.poke.data.database

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.poke.data.TypesItem
import kotlinx.parcelize.Parcelize
import java.util.UUID


@Parcelize
@Entity(tableName = "type")
data class Types(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @NonNull
    @ColumnInfo(name = "pokemon_id")
    val pokemonId: Int,

    @NonNull
    @ColumnInfo(name = "name")
    val name: String,

    @NonNull
    @ColumnInfo(name = "url")
    val url: String
) : Parcelable

fun TypesItem.toTypes(pokemonId: Int) = Types(
    pokemonId = pokemonId,
    name = this.type?.name ?: "None",
    url = this.type?.url ?: "None",
    id = "$pokemonId-${this.type?.name ?: "-"}"
)

@Parcelize
@Entity(tableName = "pokemons")
data class Pokemon(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,
) : Parcelable

data class FavoritePokemonWithTypes(
    @Embedded val pokemon: Pokemon,
    @Relation(
        parentColumn = "id",
        entity = Types::class,
        entityColumn = "pokemon_id"
    )
    val types: List<Types>
)