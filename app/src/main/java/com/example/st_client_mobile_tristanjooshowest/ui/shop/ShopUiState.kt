package com.example.st_client_mobile_tristanjooshowest.ui.shop

import com.example.st_client_mobile_tristanjooshowest.domain.model.Product

data class ShopUiState(
    val products: List<Product> = emptyList(),
    val cartItems: List<Product> = emptyList(),
    val isLoading: Boolean = false
)