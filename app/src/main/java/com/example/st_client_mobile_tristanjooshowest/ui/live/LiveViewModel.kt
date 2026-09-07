package com.example.st_client_mobile_tristanjooshowest.ui.live

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.st_client_mobile_tristanjooshowest.domain.repository.HardwareSensorRepository
import com.example.st_client_mobile_tristanjooshowest.domain.repository.LocationRepository
import com.example.st_client_mobile_tristanjooshowest.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt
import java.util.Locale

data class TrackGeofence(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val hardwareSensorRepository: HardwareSensorRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveUiState())
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    private val alpha = 0.2f
    private var currentLat = 0f
    private var currentLong = 0f
    private var isGpsObserving = false

    private var speedCount = 0
    private var totalSpeedSum = 0f
    private var lapStartTime = System.currentTimeMillis()
    private var bestLapMillis = Long.MAX_VALUE
    private var hasLeftFinishLine = true

    private val allowedTracks = listOf(
        TrackGeofence("Laguna Seca", 36.5841, -121.7529),
        TrackGeofence("Nürburgring Nordschleife", 50.3341, 6.9427),
        TrackGeofence("Circuit Zolder", 50.9889, 5.2581),
        TrackGeofence("Circuit de Spa-Francorchamps", 50.4372, 5.9714)
    )

    private var activeTrackLocation: Location? = null

    init {
        observeStandardSensors()
    }

    private fun observeStandardSensors() {
        viewModelScope.launch {
            hardwareSensorRepository.getAccelerometerFlow().collect { values ->
                if (!_uiState.value.isAtTrack) return@collect

                val rawLat = values[0] / 9.81f
                val rawLong = values[1] / 9.81f
                currentLat = currentLat + alpha * (rawLat - currentLat)
                currentLong = currentLong + alpha * (rawLong - currentLong)
                val totalG = sqrt((currentLat * currentLat) + (currentLong * currentLong))

                _uiState.update { currentState ->
                    val newMax = if (totalG > currentState.maxGForceToday) totalG else currentState.maxGForceToday
                    
                    if (currentState.autoFlashlightMovement && totalG > 0.3f && !currentState.isFlashlightOn) {
                        hardwareSensorRepository.toggleFlashlight(true)
                        currentState.copy(
                            lateralGForce = -currentLat,
                            longitudinalGForce = currentLong,
                            totalGForce = totalG,
                            maxGForceToday = newMax,
                            isFlashlightOn = true
                        )
                    } else {
                        currentState.copy(
                            lateralGForce = -currentLat,
                            longitudinalGForce = currentLong,
                            totalGForce = totalG,
                            maxGForceToday = newMax
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            hardwareSensorRepository.getLightSensorFlow().collect { luxValue ->
                if (!_uiState.value.isAtTrack) return@collect

                val maxLuxCheck = 400f
                val progress = (luxValue / maxLuxCheck).coerceIn(0f, 1f)
                val percentage = (progress * 100).toInt()

                _uiState.update { currentState ->
                    if (currentState.autoFlashlightLowLight && luxValue < 10f && !currentState.isFlashlightOn) {
                        hardwareSensorRepository.toggleFlashlight(true)
                        currentState.copy(
                            lightSensorPercentage = percentage,
                            lightSensorProgress = progress,
                            isFlashlightOn = true
                        )
                    } else if (currentState.autoFlashlightLowLight && luxValue > 20f && currentState.isFlashlightOn) {
                        hardwareSensorRepository.toggleFlashlight(false)
                        currentState.copy(
                            lightSensorPercentage = percentage,
                            lightSensorProgress = progress,
                            isFlashlightOn = false
                        )
                    } else {
                        currentState.copy(
                            lightSensorPercentage = percentage,
                            lightSensorProgress = progress
                        )
                    }
                }
            }
        }
    }

    fun toggleAutoFeature(feature: String) {
        _uiState.update { currentState ->
            when (feature) {
                "track" -> currentState.copy(autoFlashlightAtTrack = !currentState.autoFlashlightAtTrack)
                "light" -> currentState.copy(autoFlashlightLowLight = !currentState.autoFlashlightLowLight)
                "movement" -> currentState.copy(autoFlashlightMovement = !currentState.autoFlashlightMovement)
                else -> currentState
            }
        }
    }

    fun toggleFlashlight() {
        val newState = !_uiState.value.isFlashlightOn
        hardwareSensorRepository.toggleFlashlight(newState)
        _uiState.update { it.copy(isFlashlightOn = newState) }
    }

    fun startGpsTelemetry() {
        if (isGpsObserving) return
        isGpsObserving = true

        viewModelScope.launch {
            try {
                locationRepository.getGpsLocationFlow().collect { location ->

                    var detectedTrack: TrackGeofence? = null
                    for (track in allowedTracks) {
                        val trackLoc = Location("").apply {
                            latitude = track.latitude
                            longitude = track.longitude
                        }
                        if (location.distanceTo(trackLoc) < 5000f) {
                            detectedTrack = track
                            activeTrackLocation = trackLoc
                            break
                        }
                    }

                    if (detectedTrack == null) {
                        _uiState.update {
                            LiveUiState(isAtTrack = false, currentTrackName = "Telemetrie Vergrendeld: Go to track")
                        }
                        return@collect
                    }

                    val wasAtTrack = _uiState.value.isAtTrack
                    if (!wasAtTrack && _uiState.value.autoFlashlightAtTrack) {
                        hardwareSensorRepository.toggleFlashlight(true)
                    }

                    _uiState.update { it.copy(
                        isAtTrack = true, 
                        currentTrackName = detectedTrack.name,
                        isFlashlightOn = if (!wasAtTrack && it.autoFlashlightAtTrack) true else it.isFlashlightOn
                    ) }

                    if (_uiState.value.trackTempFahrenheit == 84 || _uiState.value.currentTrackName != detectedTrack.name) {
                        viewModelScope.launch {
                            val liveTemp = weatherRepository.fetchTrackTemperature(detectedTrack.latitude, detectedTrack.longitude)
                            liveTemp?.let { temp ->
                                _uiState.update { it.copy(trackTempFahrenheit = temp.toInt()) }
                            }
                        }
                    }

                    val gpsSpeedMph = location.speed * 2.23694f
                    val simulatedRpm = 900f + ((gpsSpeedMph % 30f) / 30f) * 5000f

                    if (gpsSpeedMph > 2f) {
                        speedCount++
                        totalSpeedSum += gpsSpeedMph
                    }
                    val currentAvgSpeed = if (speedCount > 0) (totalSpeedSum / speedCount).toInt() else 0

                    activeTrackLocation?.let { trackLoc ->
                        val distanceToFinish = location.distanceTo(trackLoc)
                        if (distanceToFinish < 30f && hasLeftFinishLine) {
                            hasLeftFinishLine = false
                            val currentLapMillis = System.currentTimeMillis() - lapStartTime
                            lapStartTime = System.currentTimeMillis()

                            _uiState.update { currentState ->
                                val nextLapCount = currentState.lapsCompleted + 1
                                if (currentLapMillis < bestLapMillis && currentState.lapsCompleted > 0) {
                                    bestLapMillis = currentLapMillis
                                    currentState.copy(lapsCompleted = nextLapCount, bestLapTime = formatLapTime(bestLapMillis))
                                } else {
                                    currentState.copy(lapsCompleted = nextLapCount)
                                }
                            }
                        } else if (distanceToFinish > 45f) {
                            hasLeftFinishLine = true
                        }
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            speedMph = gpsSpeedMph.toInt(),
                            speedProgress = (gpsSpeedMph / 155f).coerceIn(0f, 1f),
                            rpm = simulatedRpm.toInt(),
                            rpmProgress = ((simulatedRpm - 900f) / (6000f - 900f)).coerceIn(0f, 1f),
                            avgSpeedMph = currentAvgSpeed
                        )
                    }
                }
            } catch (e: Exception) {
                isGpsObserving = false
            }
        }
    }

    private fun formatLapTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        val tenths = (millis % 1000) / 100
        return String.format(Locale.US, "%d:%02d.%d", minutes, seconds, tenths)
    }

    override fun onCleared() {
        super.onCleared()
        hardwareSensorRepository.toggleFlashlight(false)
    }
}