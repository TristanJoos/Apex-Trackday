package com.example.st_client_mobile_tristanjooshowest.domain.repository

import kotlinx.coroutines.flow.Flow

interface HardwareSensorRepository {
    fun getAccelerometerFlow(): Flow<FloatArray>
    fun getLightSensorFlow(): Flow<Float>
    fun toggleFlashlight(enabled: Boolean)
}