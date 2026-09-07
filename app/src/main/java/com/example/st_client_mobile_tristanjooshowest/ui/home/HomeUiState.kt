package com.example.st_client_mobile_tristanjooshowest.ui.home

data class HomeUiState(
    val username: String = "Driver",
    val emailAddress: String = "",
    val isSubscribed: Boolean = false,
    val profilePictureUri: String? = null
)