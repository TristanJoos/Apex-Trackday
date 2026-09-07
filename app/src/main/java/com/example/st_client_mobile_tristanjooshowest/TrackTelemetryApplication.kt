package com.example.st_client_mobile_tristanjooshowest

import android.app.Application
import com.mapbox.common.MapboxOptions // <-- Make sure to add this import
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrackTelemetryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)
    }
}