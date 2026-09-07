package com.example.st_client_mobile_tristanjooshowest.ui.navigation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.st_client_mobile_tristanjooshowest.ui.home.HomeScreen
import com.example.st_client_mobile_tristanjooshowest.ui.home.HomeViewModel
import com.example.st_client_mobile_tristanjooshowest.ui.shop.ShopScreen
import com.example.st_client_mobile_tristanjooshowest.ui.live.LiveDataScreen
import com.example.st_client_mobile_tristanjooshowest.ui.live.LiveViewModel
import com.example.st_client_mobile_tristanjooshowest.ui.map.MapScreen
import com.example.st_client_mobile_tristanjooshowest.ui.map.MapViewModel
import com.example.st_client_mobile_tristanjooshowest.ui.shop.ShopViewModel
import com.example.st_client_mobile_tristanjooshowest.ui.tickets.TicketDetailScreen
import com.example.st_client_mobile_tristanjooshowest.ui.tickets.TicketListScreen
import com.example.st_client_mobile_tristanjooshowest.ui.tickets.TicketViewModel

@Composable
fun ApexTrackDayNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    shopViewModel: ShopViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
    liveViewModel: LiveViewModel = hiltViewModel(),
    ticketViewModel: TicketViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val shopUiState by shopViewModel.uiState.collectAsStateWithLifecycle()
    val mapUiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    val ticketUiState by ticketViewModel.uiState.collectAsStateWithLifecycle()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            Toast.makeText(context, "Location access granted.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Location permission denied. Map might not follow you.", Toast.LENGTH_LONG).show()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                uiState = homeUiState,
                upcomingCount = ticketUiState.upcomingTickets.size,
                onEmailChanged = { homeViewModel.onEmailChanged(it) },
                onSubscribeClick = {
                    homeViewModel.onSubscribeClicked()
                    Toast.makeText(context, "Subscribed to newsletter.", Toast.LENGTH_SHORT).show()
                },
                onCalendarClick = {
                    navController.navigate(Screen.Tickets.route)
                },
                onShopClick = {
                    navController.navigate(Screen.Shop.route)
                },
                onProfilePictureChanged = { homeViewModel.onProfilePictureChanged(it) }
            )
        }

        composable(Screen.Tickets.route) {
            TicketListScreen(
                uiState = ticketUiState,
                viewModel = ticketViewModel,
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail.createRoute(ticketId))
                }
            )
        }

        composable(
            route = Screen.TicketDetail.route,
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            TicketDetailScreen(
                ticketId = ticketId,
                viewModel = ticketViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Shop.route) {
            ShopScreen(
                products = shopUiState.products,
                cartItems = shopUiState.cartItems,
                onAddToBuildClick = { product -> shopViewModel.addToBuild(product) },
                onRemoveFromBuildClick = { product -> shopViewModel.removeFromBuild(product) },
                onPlaceOrderClick = {
                    shopViewModel.placeOrder()
                    Toast.makeText(context, "Order sent to paddock locker!", Toast.LENGTH_LONG).show()
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                tickets = ticketUiState.upcomingTickets,
                onRequestLocationPermission = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                onNavigateClick = {
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.5841,-121.7529?q=Laguna+Seca+Raceway"))
                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=36.5841,-121.7529"))
                        context.startActivity(browserIntent)
                    }
                }
            )
        }

        composable(Screen.Live.route) {
            LiveDataScreen(
                viewModel = liveViewModel
            )
        }
    }
}