package com.example.st_client_mobile_tristanjooshowest.data.remote

import android.util.Log
import com.example.st_client_mobile_tristanjooshowest.data.remote.api.ShopApiService
import com.example.st_client_mobile_tristanjooshowest.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopApiClient @Inject constructor(
    private val apiService: ShopApiService
) {
    suspend fun getProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.fetchProducts()
            } catch (e: Exception) {
                Log.e("ShopApiClient", "Error fetching products from network", e)
                emptyList()
            }
        }
    }
}