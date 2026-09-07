package com.example.st_client_mobile_tristanjooshowest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val route = intent.getStringExtra("route")
        val location = intent.getStringExtra("location")

        if (route != null || location != null) {
            Log.d("MainActivity", "Received Intent Extras - Route: $route, Location: $location")
        }

        enableEdgeToEdge()
        setContent {
            TrackTelemetryApp()
        }
    }
}