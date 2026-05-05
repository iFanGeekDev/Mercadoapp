package com.mercadoapp.data.repository

import com.mercadoapp.data.remote.api.MercadoApiService
import com.mercadoapp.data.remote.mapper.toDomain
import com.mercadoapp.domain.model.Order
import com.mercadoapp.domain.repository.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteOrderRepository @Inject constructor(
    private val api: MercadoApiService
) : OrderRepository {

    override suspend fun getOrders(page: Int, size: Int): Result<List<Order>> {
        return try {
            val response = api.getOrders(page, size)
            val domainOrders = response.items.map { it.toDomain() }
            Result.success(domainOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
