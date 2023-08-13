package com.example.poke.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poke.data.DetailPokemonResponse
import com.example.poke.data.EvolutionChainResponse
import com.example.poke.data.PokemonSpeciesResponse
import com.example.poke.data.Repository
import com.example.poke.data.database.FavoritePokemonWithTypes
import com.example.poke.data.database.Pokemon
import com.example.poke.data.database.Types
import com.example.poke.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val repository: Repository): ViewModel() {
    private val _uiStatePokemons: MutableStateFlow<UiState<List<DetailPokemonEssentialsResponse>>> = MutableStateFlow(UiState.Loading)
    private val _uiStateFavoritePokemonsWithTypes: MutableStateFlow<UiState<List<FavoritePokemonWithTypes>>> = MutableStateFlow(UiState.Loading)
    private val _uiStateDetailPokemon: MutableStateFlow<UiState<DetailPokemonResponse>> = MutableStateFlow(UiState.Loading)
    private val _uiStateSpecies: MutableStateFlow<UiState<PokemonSpeciesResponse>> = MutableStateFlow(UiState.Loading)
    private val _uiStateEvolutionChain: MutableStateFlow<UiState<EvolutionChainResponse>> = MutableStateFlow(UiState.Loading)
    private val _uiStateIsFavorite: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Loading)

    val uiStatePokemons: StateFlow<UiState<List<DetailPokemonEssentialsResponse>>> get() = _uiStatePokemons
    val uiStateFavoritePokemonsWithTypes: StateFlow<UiState<List<FavoritePokemonWithTypes>>> get() = _uiStateFavoritePokemonsWithTypes
    val uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>> get() = _uiStateDetailPokemon
    val uiStateSpecies: StateFlow<UiState<PokemonSpeciesResponse>> get() = _uiStateSpecies
    val uiStateEvolutionChain: StateFlow<UiState<EvolutionChainResponse>> get() = _uiStateEvolutionChain
    val uiStateIsFavoritePokemon: StateFlow<UiState<Boolean>> get() = _uiStateIsFavorite

    fun getAllPokemons(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllPokemon()
                    .catch {
                        _uiStatePokemons.value = UiState.Error("error: ${it.message}")
                    }
                    .collect { pokemons->
                        _uiStatePokemons.value = UiState.Success(pokemons)
                    }
            }catch (e: java.lang.Exception){
                _uiStatePokemons.value = UiState.Error("error: ${e.message}")
            }
        }
    }

    fun getFavoritePokemon(){
        viewModelScope.launch {
            repository.getFavoritesPokemon()
                .catch {
                    _uiStateFavoritePokemonsWithTypes.value = UiState.Error("Error retrieving favorite pokemon: ${it.message}")
                }
                .collect{ pokemons ->
                    _uiStateFavoritePokemonsWithTypes.value = UiState.Success(pokemons)
                }
        }
    }

    fun addFavorite(newPokemon: Pokemon, types: List<Types>){
        viewModelScope.launch {
            repository.addFavoritePokemon(newPokemon, types)
                .catch {
                    _uiStateIsFavorite.value = UiState.Error(it.message.toString())
                }
                .collect{
                    _uiStateIsFavorite.value = UiState.Success(true)
                }

        }
    }

    fun removeFavorite(pokemon: Pokemon, types: List<Types>){
        viewModelScope.launch {
            repository.removeFavoritePokemon(pokemon, types)
                .catch {
                    _uiStateIsFavorite.value = UiState.Error(it.message.toString())
                }
                .collect{
                    _uiStateIsFavorite.value = UiState.Success(false)
                }

        }
    }

    fun checkFavoritePokemon(pokemon: Pokemon){
        viewModelScope.launch {
            repository.checkIsFavorite(pokemon)
                .catch {
                    _uiStateIsFavorite.value = UiState.Error(it.message.toString())
                }
                .collect{
                    _uiStateIsFavorite.value = UiState.Success(it != null)
                }
        }
    }

    fun getDetail(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getDetailPokemon(id)
                    .catch {
                        Log.d(this::class.java.simpleName, "getDetail: ${it.message}")
                        _uiStateDetailPokemon.value = UiState.Error("error: ${it.message}")
                    }
                    .collect{pokemon->
                        _uiStateDetailPokemon.value = UiState.Success(pokemon)
                    }
            }catch (e: java.lang.Exception){
                _uiStateDetailPokemon.value = UiState.Error("error: ${e.message}")
            }
        }
    }

    fun getSpecies(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "getSpecies: ")
            try {
                repository.getPokemonSpecies(id)
                    .catch {
                        _uiStateSpecies.value = UiState.Error("error: ${it.message}")
                    }
                    .collect{pokemon->
                        _uiStateSpecies.value = UiState.Success(pokemon)
                    }
            }catch (e: java.lang.Exception){
                _uiStateSpecies.value = UiState.Error("error: ${e.message}")
            }
        }
    }
    fun getEvolutionChain(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getPokemonEvolutionChain(id)
                    .catch {
                        _uiStateEvolutionChain.value = UiState.Error("error: ${it.message}")
                    }
                    .collect{pokemon->
                        _uiStateEvolutionChain.value = UiState.Success(pokemon)
                    }
            }catch (e: java.lang.Exception){
                _uiStateEvolutionChain.value = UiState.Error("error: ${e.message}")
            }
        }
    }
}