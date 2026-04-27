package com.mercadoapp.domain.repository

import com.mercadoapp.domain.model.Product

interface ProductRepository {
    suspend fun getHomeFeed(): List<Product>
    suspend fun getProductById(id: String): Product?
}
