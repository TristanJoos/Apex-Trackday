package com.example.st_client_mobile_tristanjooshowest.ui.live

data class LiveUiState(
    val totalGForce: Float = 0.00f,
    val lateralGForce: Float = 0.00f,
    val longitudinalGForce: Float = 0.00f,
    val maxGForceToday: Float = 0.00f,

    val speedMph: Int = 0,
    val rpm: Int = 0,
    val speedProgress: Float = 0.0f,
    val rpmProgress: Float = 0.0f,

    val lightSensorPercentage: Int = 0,
    val lightSensorProgress: Float = 0.0f,

    val lapsCompleted: Int = 0,
    val bestLapTime: String = "--:--.-",
    val avgSpeedMph: Int = 0,
    val trackTempFahrenheit: Int? = null,

    val isAtTrack: Boolean = false,
    val currentTrackName: String = "No track detected",
    
    val isFlashlightOn: Boolean = false,

    // Auto-flashlight settings
    val autoFlashlightAtTrack: Boolean = false,
    val autoFlashlightLowLight: Boolean = false,
    val autoFlashlightMovement: Boolean = false
)