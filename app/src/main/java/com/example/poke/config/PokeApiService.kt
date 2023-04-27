package com.example.poke.config

import com.example.poke.BuildConfig
import com.example.poke.data.DetailPokemonResponse
import com.example.poke.data.GetPokemonsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getAll(@Query("limit") limit: Int): GetPokemonsResponse

    @GET("pokemon/{id}")
    suspend fun getDetail(@Path("id") id: Int): DetailPokemonResponse
}

class ApiConfig{
    companion object{
        fun getApiService(): PokeApiService{
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder().apply {
                baseUrl(BuildConfig.API_BASE_URL)
                addConverterFactory(GsonConverterFactory.create())

                client(client)
            }.build()

            return retrofit.create(PokeApiService::class.java)
        }
    }
}