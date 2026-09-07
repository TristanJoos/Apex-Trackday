package com.example.st_client_mobile_tristanjooshowest

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.st_client_mobile_tristanjooshowest.ui.navigation.ApexTrackDayBottomBar
import com.example.st_client_mobile_tristanjooshowest.ui.navigation.ApexTrackDayNavGraph
import com.example.st_client_mobile_tristanjooshowest.ui.permissions.MainViewModel
import com.example.st_client_mobile_tristanjooshowest.ui.permissions.PermissionManager
import com.example.st_client_mobile_tristanjooshowest.ui.shop.ShopViewModel
import com.example.st_client_mobile_tristanjooshowest.ui.theme.StclientmobileTristanJoosHowestTheme

@Composable
fun TrackTelemetryApp(
    shopViewModel: ShopViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    StclientmobileTristanJoosHowestTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val context = LocalContext.current

        val shopUiState by shopViewModel.uiState.collectAsState()
        val permissionRequest by mainViewModel.permissionRequest.collectAsState()

        PermissionManager(
            permissionRequest = permissionRequest,
            onPermissionResult = { permission, granted ->
                mainViewModel.onPermissionResult(permission, granted)
            }
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                ApexTrackDayBottomBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    cartItemCount = shopUiState.cartItems.size
                )
            }
        ) { innerPadding ->
            ApexTrackDayNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

fun shareTrackSession(context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Performance Track Telemetry")
        putExtra(Intent.EXTRA_TEXT, "Checking out my live telemetry, lap records, and vehicle build diagnostics!")
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            "Share tracking info"
        )
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_5,
)
@Composable
fun TrackTelemetryAppPreview() {
    StclientmobileTristanJoosHowestTheme {
        TrackTelemetryApp()
    }
}