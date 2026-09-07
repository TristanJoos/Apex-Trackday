package com.example.st_client_mobile_tristanjooshowest.di

import com.example.st_client_mobile_tristanjooshowest.BuildConfig
import com.example.st_client_mobile_tristanjooshowest.data.remote.ShopApiClient
import com.example.st_client_mobile_tristanjooshowest.data.remote.api.ShopApiService
import com.example.st_client_mobile_tristanjooshowest.data.remote.api.WeatherApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideShopApiService(moshi: Moshi): ShopApiService {
        val baseUrl = BuildConfig.API_BASE_URL.ifEmpty { "http://10.0.2.2:8080/" }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ShopApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideShopApiClient(apiService: ShopApiService): ShopApiClient {
        return ShopApiClient(apiService)
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(moshi: Moshi): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }
}