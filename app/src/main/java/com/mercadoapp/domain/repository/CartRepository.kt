package com.mercadoapp.domain.repository

import com.mercadoapp.domain.model.CartItem
import com.mercadoapp.domain.model.ProductVariant
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    val cartItems: Flow<List<CartItem>>
    suspend fun addItem(
        productId: String,
        productName: String,
        productImageUrl: String,
        variant: ProductVariant,
        quantity: Int = 1
    )
    suspend fun removeItem(itemId: Int)
    suspend fun updateQuantity(itemId: Int, newQuantity: Int)
    suspend fun clearCart()
}
