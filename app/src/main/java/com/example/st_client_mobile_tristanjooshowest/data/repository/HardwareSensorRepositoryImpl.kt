package com.example.st_client_mobile_tristanjooshowest.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.util.Log
import com.example.st_client_mobile_tristanjooshowest.domain.repository.HardwareSensorRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HardwareSensorRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : HardwareSensorRepository {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private var cameraId: String? = null

    init {
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull()
        } catch (e: Exception) {
            Log.e("HardwareSensorRepo", "Error getting cameraId", e)
        }
    }

    override fun getAccelerometerFlow(): Flow<FloatArray> = callbackFlow {
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

    override fun getLightSensorFlow(): Flow<Float> = callbackFlow {
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

    override fun toggleFlashlight(enabled: Boolean) {
        cameraId?.let { id ->
            try {
                cameraManager.setTorchMode(id, enabled)
            } catch (e: Exception) {
                Log.e("HardwareSensorRepo", "Error toggling flashlight", e)
            }
        }
    }
}