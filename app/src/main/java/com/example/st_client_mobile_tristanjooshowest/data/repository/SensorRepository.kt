package com.example.st_client_mobile_tristanjooshowest.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.example.st_client_mobile_tristanjooshowest.data.remote.api.WeatherApiService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorRepository(
    context: Context,
    private val weatherApiService: WeatherApiService
) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private var cameraId: String? = null

    init {
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull()
        } catch (e: Exception) {
            android.util.Log.e("SensorRepository", "Error getting cameraId", e)
        }
    }

    fun toggleFlashlight(enabled: Boolean) {
        cameraId?.let { id ->
            try {
                cameraManager.setTorchMode(id, enabled)
            } catch (e: Exception) {
                android.util.Log.e("SensorRepository", "Error toggling flashlight", e)
            }
        }
    }

    fun getAccelerometerFlow(): Flow<FloatArray> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let { trySend(it.clone()) }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    fun getLightSensorFlow(): Flow<Float> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.get(0)?.let { trySend(it) }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        lightSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun getGpsLocationFlow(): Flow<Location> = callbackFlow {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location)
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500L,
                0f,
                listener
            )
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    suspend fun fetchTrackTemperature(lat: Double, lon: Double): Float {
        return try {
            val response = weatherApiService.getWeather(lat, lon)
            response.current.temperature_2m
        } catch (e: Exception) {
            84f
        }
    }
}
