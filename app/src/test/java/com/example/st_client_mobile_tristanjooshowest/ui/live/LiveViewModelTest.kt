package com.example.st_client_mobile_tristanjooshowest.ui.live

import android.location.Location
import com.example.st_client_mobile_tristanjooshowest.domain.repository.HardwareSensorRepository
import com.example.st_client_mobile_tristanjooshowest.domain.repository.LocationRepository
import com.example.st_client_mobile_tristanjooshowest.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class LiveViewModelTest {

    @Mock
    private lateinit var hardwareSensorRepository: HardwareSensorRepository
    @Mock
    private lateinit var locationRepository: LocationRepository
    @Mock
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var viewModel: LiveViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Default flow behavior to avoid null pointer exceptions during init
        `when`(hardwareSensorRepository.getAccelerometerFlow()).thenReturn(flowOf(floatArrayOf(0f, 0f, 0f)))
        `when`(hardwareSensorRepository.getLightSensorFlow()).thenReturn(flowOf(0f))

        viewModel = LiveViewModel(hardwareSensorRepository, locationRepository, weatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isAtTrackIsFalse() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isAtTrack)
        assertEquals("No track detected", state.currentTrackName)
    }

    @Test
    fun startGpsTelemetry_updatesAtTrackWhenNearLagunaSeca() = runTest {
        // Mock location near Laguna Seca (36.5841, -121.7529)
        val mockLocation = Location("").apply {
            latitude = 36.5842
            longitude = -121.7530
            speed = 10f // ~22 mph
        }

        `when`(locationRepository.getGpsLocationFlow()).thenReturn(flowOf(mockLocation))
        `when`(weatherRepository.fetchTrackTemperature(any(), any())).thenReturn(75f)

        viewModel.startGpsTelemetry()

        val state = viewModel.uiState.value
        assertTrue(state.isAtTrack)
        assertEquals("Laguna Seca", state.currentTrackName)
        assertEquals(22, state.speedMph)
    }

    @Test
    fun startGpsTelemetry_remainsLockedWhenFarFromTracks() = runTest {
        // Mock location far away
        val mockLocation = Location("").apply {
            latitude = 0.0
            longitude = 0.0
            speed = 0f
        }

        `when`(locationRepository.getGpsLocationFlow()).thenReturn(flowOf(mockLocation))

        viewModel.startGpsTelemetry()

        val state = viewModel.uiState.value
        assertFalse(state.isAtTrack)
        assertEquals("Telemetrie Vergrendeld: Go to track", state.currentTrackName)
    }
}