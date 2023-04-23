package com.example.poke.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poke.data.DetailPokemonResponse
import com.example.poke.data.GetPokemonsResponse
import com.example.poke.data.Repository
import com.example.poke.data.PokemonItem
import com.example.poke.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PokemonViewModel(val repository: Repository): ViewModel() {
    private val _uiStatePokemons: MutableStateFlow<UiState<GetPokemonsResponse>> = MutableStateFlow(UiState.Loading)
    private val _uiStateFavoritePokemons: MutableStateFlow<UiState<Set<PokemonItem>>> = MutableStateFlow(UiState.Loading)
    private val _uiStateDetailPokemon: MutableStateFlow<UiState<DetailPokemonResponse>> = MutableStateFlow(UiState.Loading)
    private val _uiStateIsFavorite: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Loading)
    private val _uiStatePokemonTransaction: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Loading)

    val uiStatePokemons: StateFlow<UiState<GetPokemonsResponse>> get() = _uiStatePokemons
    val uiStateFavoritePokemons: StateFlow<UiState<Set<PokemonItem>>> get() = _uiStateFavoritePokemons
    val uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>> get() = _uiStateDetailPokemon
    val uiStateIsFavorite: StateFlow<UiState<Boolean>> get() = _uiStateIsFavorite
    val uiStatePokemonTransaction: StateFlow<UiState<Boolean>> get() = _uiStatePokemonTransaction

    fun getAllPokemons(){
        viewModelScope.launch {
            repository.getAllPokemons()
                .catch {
                    _uiStatePokemons.value = UiState.Error(it.message.toString())
                }
                .collect {pokemons->
                    _uiStatePokemons.value = UiState.Success(pokemons)
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

    fun addFavorite(newPokemon: PokemonItem){
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

    fun removeFavorite(newPokemon: PokemonItem){
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

    fun getDetail(id: Int){
        viewModelScope.launch {
            repository.getDetailPokemon(id)
                .catch {
                    _uiStateDetailPokemon.value = UiState.Error(it.message.toString())
                }
                .collect{pokemon->
                    _uiStateDetailPokemon.value = UiState.Success(pokemon)
                }
        }
    }


}