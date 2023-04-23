package com.example.poke.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.poke.data.Repository
import com.example.poke.data.viewmodel.PokemonViewModel

class ViewModelFactory(private val repository: Repository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(PokemonViewModel::class.java)){
            return PokemonViewModel(repository) as T
        }

        throw java.lang.IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}