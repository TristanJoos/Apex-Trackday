package com.example.st_client_mobile_tristanjooshowest.data.repository

import com.example.st_client_mobile_tristanjooshowest.data.remote.api.WeatherApiService
import com.example.st_client_mobile_tristanjooshowest.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {
    override suspend fun fetchTrackTemperature(lat: Double, lon: Double): Float? {
        return try {
            val response = weatherApiService.getWeather(lat, lon)
            response.current.temperature_2m
        } catch (e: Exception) {
            null
        }
    }
}