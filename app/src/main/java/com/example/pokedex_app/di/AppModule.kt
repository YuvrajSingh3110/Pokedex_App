package com.example.pokedex_app.di

import com.example.pokedex_app.data.remote.PokeApi
import com.example.pokedex_app.repository.PokemonRepository
import com.example.pokedex_app.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//di is dependency injection

@Module
@InstallIn(SingletonComponent::class)
//singletonComponent means that the dependencies in this module will live as long as the app is running
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokeApi
    ) = PokemonRepository(api)

    @Singleton
    @Provides
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) //to convert json code to data classes
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }
}