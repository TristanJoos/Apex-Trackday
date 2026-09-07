package com.example.st_client_mobile_tristanjooshowest.ui.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class MapUiState(
    val hasLocationPermission: Boolean = false,
    val isTicketReady: Boolean = true,
    val isVehiclePrepared: Boolean = true,
    val isTechInspectionDone: Boolean = false,
    val distanceToTrack: String = "12.4 mi",
    val estimatedArrival: String = "20 minutes",
    val checkInTime: String = "7:00 AM",
    val firstSessionTime: String = "9:00 AM",
    val parkingArea: String = "Paddock Area A",
    val weather: String = "☀️ 72°F, Clear"
)

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun setLocationPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }
    }

    fun toggleTicketReady(checked: Boolean) {
        _uiState.update { it.copy(isTicketReady = checked) }
    }

    fun toggleVehiclePrepared(checked: Boolean) {
        _uiState.update { it.copy(isVehiclePrepared = checked) }
    }

    fun toggleTechInspection(checked: Boolean) {
        _uiState.update { it.copy(isTechInspectionDone = checked) }
    }
}