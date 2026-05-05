package com.mercadoapp.domain.repository

import com.mercadoapp.domain.model.Order

interface OrderRepository {
    suspend fun getOrders(page: Int = 1, size: Int = 20): Result<List<Order>>
}
