package com.mercadoapp.domain.repository

import androidx.paging.PagingData
import com.mercadoapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    /** Paged catalog — primary data source for HomeScreen */
    fun getProductsPaged(): Flow<PagingData<Product>>

    /** Non-paged list for legacy/offline use */
    suspend fun getHomeFeed(): List<Product>

    suspend fun getProductById(id: String): Product?

    suspend fun getFavorites(): List<Product>
    suspend fun toggleFavorite(productId: String, isFavorite: Boolean)
    suspend fun isProductFavorite(productId: String): Boolean
}
