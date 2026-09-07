package com.example.st_client_mobile_tristanjooshowest.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Gauge
import com.composables.icons.lucide.Lucide
import com.example.st_client_mobile_tristanjooshowest.R

sealed class Screen(
    val route: String,
    @StringRes val labelResourceId: Int = 0,
    val icon: ImageVector = Icons.Default.Info
) {
    data object Home: Screen("home", R.string.home_label, Icons.Filled.Home)
    data object Tickets: Screen("tickets", R.string.tickets_label, Icons.Filled.DateRange)
    data object TicketDetail : Screen("ticket_detail/{ticketId}") {
        fun createRoute(ticketId: String) = "ticket_detail/$ticketId"
    }
    data object Shop: Screen("shop", R.string.shop_label, Icons.Filled.ShoppingCart)
    data object Map: Screen("map", R.string.map_label, Icons.Filled.LocationOn)
    data object Live: Screen("live", R.string.live_label, Lucide.Gauge)
}

val screens = listOf(
    Screen.Home,
    Screen.Tickets,
    Screen.Shop,
    Screen.Map,
    Screen.Live
)