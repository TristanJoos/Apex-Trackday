package com.example.st_client_mobile_tristanjooshowest.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.st_client_mobile_tristanjooshowest.data.local.room.dao.ProductDao
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.toDomain
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.toEntity
import com.example.st_client_mobile_tristanjooshowest.data.messaging.MessagePublisher
import com.example.st_client_mobile_tristanjooshowest.data.remote.ShopApiClient
import com.example.st_client_mobile_tristanjooshowest.domain.model.Product
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val apiClient: ShopApiClient,
    private val productDao: ProductDao,
    private val messagePublisher: MessagePublisher,
    private val moshi: Moshi
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val localProducts = productDao.getAllProducts().first().map { it.toDomain() }
            if (localProducts.isNotEmpty()) {
                _uiState.update { it.copy(products = localProducts, isLoading = false) }
            }

            try {
                val productsFromNetwork = apiClient.getProducts()
                
                productDao.clearAll()
                productDao.insertProducts(productsFromNetwork.map { it.toEntity() })
                
                _uiState.update { currentState ->
                    currentState.copy(products = productsFromNetwork, isLoading = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("SHOP_API", "Fout bij laden via API Client", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addToBuild(product: Product) {
        _uiState.update { currentState ->
            currentState.copy(cartItems = currentState.cartItems + product)
        }
    }

    fun removeFromBuild(product: Product) {
        _uiState.update { currentState ->
            val list = currentState.cartItems.toMutableList()
            val index = list.indexOfFirst { it.id == product.id }
            if (index != -1) {
                list.removeAt(index)
            }
            currentState.copy(cartItems = list)
        }
    }

    fun placeOrder() {
        val currentCart = _uiState.value.cartItems
        if (currentCart.isEmpty()) return

        viewModelScope.launch {
            try {
                val adapter = moshi.adapter(List::class.java)
                val orderJson = adapter.toJson(currentCart)
                messagePublisher.publishMessage(orderJson ?: "{}")

                _uiState.update { it.copy(cartItems = emptyList()) }
                android.util.Log.d("ShopViewModel", "Order placed successfully")
            } catch (e: Exception) {
                android.util.Log.e("ShopViewModel", "Failed to place order", e)
            }
        }
    }
}
