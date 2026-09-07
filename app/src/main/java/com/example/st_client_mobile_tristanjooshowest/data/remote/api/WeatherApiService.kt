package com.example.st_client_mobile_tristanjooshowest.data.remote.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "current") val current: CurrentWeatherData
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherData(
    @Json(name = "temperature_2m") val temperature_2m: Float
)

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m",
        @Query("temperature_unit") unit: String = "fahrenheit"
    ): WeatherResponse
}