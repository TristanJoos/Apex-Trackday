package com.example.st_client_mobile_tristanjooshowest.domain.repository

interface WeatherRepository {
    suspend fun fetchTrackTemperature(lat: Double, lon: Double): Float?
}