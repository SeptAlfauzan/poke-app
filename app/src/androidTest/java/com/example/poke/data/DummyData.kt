//package com.example.poke.data
//
//object DummyData {
//    val data = listOf(
//        PokemonItem(
//            name = "bulbasaur",
//            url = "https://pokeapi.co/api/v2/pokemon/1/"
//        ),
//        PokemonItem(
//            name = "ivysaur",
//            url = "https://pokeapi.co/api/v2/pokemon/2/"
//        ),
//        PokemonItem(
//            name = "venusaur",
//            url = "https://pokeapi.co/api/v2/pokemon/3/"
//        ),
//        PokemonItem(
//            name = "charmander",
//            url = "https://pokeapi.co/api/v2/pokemon/4/"
//        ),
//        PokemonItem(
//            name = "charmeleon",
//            url = "https://pokeapi.co/api/v2/pokemon/5/"
//        ),
//        PokemonItem(
//            name = "charizard",
//            url = "https://pokeapi.co/api/v2/pokemon/6/"
//        ),
//        PokemonItem(
//            name = "squirtle",
//            url = "https://pokeapi.co/api/v2/pokemon/7/"
//        ),
//    )
//    val favoriteData = listOf(
//        FavoritePokemon(
//            2,
//            "bulbasaur"
//        )
//    )
//    val listSize = data.size
//    val favoriteListSize = favoriteData.size
//
//    fun getEmpty(): GetPokemonsResponse = GetPokemonsResponse(
//        results = listOf()
//    )
//
//    fun getAll(): GetPokemonsResponse = GetPokemonsResponse(
//        results = data
//    )
//
//    fun getAllFavorite(): Set<FavoritePokemon> = favoriteData.toSet()
//
//    fun getDetail(): DetailPokemonResponse = DetailPokemonResponse(
//        types = listOf(
//            TypesItem(
//                slot = 1,
//                type = Type(
//                    name = "grass",
//                    url = "https://pokeapi.co/api/v2/type/12/"
//                )
//            ),
//            TypesItem(
//                slot = 2,
//                type = Type(
//                    name = "poison",
//                    url = "https://pokeapi.co/api/v2/type/4/"
//                )
//            ),
//        ),
//        stats = listOf(),
//        name = "Bulbasaur",
//        sprites = Sprites(
//            other = Other(
//                officialArtwork = OfficialArtwork(
//                    frontDefault = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png"
//                )
//            )
//        )
//    )
//}
