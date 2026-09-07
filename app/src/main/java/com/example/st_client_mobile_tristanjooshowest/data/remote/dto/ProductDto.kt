package com.example.st_client_mobile_tristanjooshowest.data.remote.dto

import com.example.st_client_mobile_tristanjooshowest.domain.model.Product
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "category") val category: String,
    @Json(name = "price") val price: Double,
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "is_featured") val isFeatured: Boolean
)

fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        category = this.category,
        price = this.price,
        imageUrl = this.imageUrl,
        isFeatured = this.isFeatured
    )
}