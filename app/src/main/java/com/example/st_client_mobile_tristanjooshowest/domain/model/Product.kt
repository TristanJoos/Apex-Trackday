package com.example.st_client_mobile_tristanjooshowest.domain.model

data class Product (
    val id: Int = 0,
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String = "",
    val isFeatured: Boolean = false
)