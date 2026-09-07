package com.example.st_client_mobile_tristanjooshowest.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.st_client_mobile_tristanjooshowest.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String,
    val isFeatured: Boolean
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    category = category,
    price = price,
    imageUrl = imageUrl,
    isFeatured = isFeatured
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    category = category,
    price = price,
    imageUrl = imageUrl,
    isFeatured = isFeatured
)
