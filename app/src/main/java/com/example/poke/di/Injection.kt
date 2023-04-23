package com.example.poke.di

import com.example.poke.data.Repository

object Injection {
    fun provideRepository(): Repository = Repository.getInstance()
}