package com.example.poke.data.viewmodel

import com.example.poke.data.*
import com.google.gson.annotations.SerializedName


data class DetailPokemonEssentialsResponse(

    @field:SerializedName("types")
    val types: List<TypesItem>,

    @field:SerializedName("sprites")
    val sprites: Sprites? = null,

    @field:SerializedName("species")
    val species: Species? = null,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: Int,
)
