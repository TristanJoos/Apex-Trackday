package com.example.st_client_mobile_tristanjooshowest.data.remote.api

import com.example.st_client_mobile_tristanjooshowest.domain.model.Product
import retrofit2.http.GET

interface ShopApiService {

    @GET("api/products")
    suspend fun fetchProducts(): List<Product>
}