package com.example.poke.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poke.data.DetailPokemonResponse
import com.example.poke.data.FavoritePokemon
import com.example.poke.data.GetPokemonsResponse
import com.example.poke.data.Repository
import com.example.poke.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PokemonViewModel(val repository: Repository): ViewModel() {
    private val _uiStatePokemons: MutableStateFlow<UiState<GetPokemonsResponse>> = MutableStateFlow(UiState.Loading)
    private val _uiStateFavoritePokemons: MutableStateFlow<UiState<Set<FavoritePokemon>>> = MutableStateFlow(UiState.Loading)
    private val _uiStateDetailPokemon: MutableStateFlow<UiState<DetailPokemonResponse>> = MutableStateFlow(UiState.Loading)
    private val _uiStateIsFavorite: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Loading)
    private val _uiStatePokemonTransaction: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Loading)

    val uiStatePokemons: StateFlow<UiState<GetPokemonsResponse>> get() = _uiStatePokemons
    val uiStateFavoritePokemons: StateFlow<UiState<Set<FavoritePokemon>>> get() = _uiStateFavoritePokemons
    val uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>> get() = _uiStateDetailPokemon

    fun getAllPokemons(){
        viewModelScope.launch {
            try {
                repository.getAllPokemons()
                    .catch {
                        _uiStatePokemons.value = UiState.Error("error: ${it.message}")
                    }
                    .collect {pokemons->
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
                    _uiStateFavoritePokemons.value = UiState.Error("Error retrieving favorite pokemon: ${it.message}")
                }
                .collect{ pokemons ->
                    _uiStateFavoritePokemons.value = UiState.Success(pokemons)
                }
        }
    }

    fun addFavorite(newPokemon: FavoritePokemon){
        viewModelScope.launch {
            repository.addFavoritePokemon(newPokemon)
                .catch {
                    _uiStateIsFavorite.value = UiState.Error(it.message.toString())
                }
                .collect{
                    _uiStatePokemonTransaction.value = UiState.Success(it)
                }

        }
    }

    fun removeFavorite(newPokemon: FavoritePokemon){
        viewModelScope.launch {
            repository.removeFavoritePokemon(newPokemon)
                .catch {
                    _uiStateIsFavorite.value = UiState.Error(it.message.toString())
                }
                .collect{
                    _uiStatePokemonTransaction.value = UiState.Success(it)
                }

        }
    }

    fun isFavoritePokemon(pokemon: FavoritePokemon): Boolean = repository.checkIsFavorite(pokemon)

    fun getDetail(id: Int){
        viewModelScope.launch {
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
}