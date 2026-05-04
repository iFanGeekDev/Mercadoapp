package com.mercadoapp.data.repository

import com.mercadoapp.data.local.dao.CartDao
import com.mercadoapp.data.local.entity.CartItemEntity
import com.mercadoapp.data.local.mapper.toDomain
import com.mercadoapp.domain.model.CartItem
import com.mercadoapp.domain.model.ProductVariant
import com.mercadoapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override val cartItems: Flow<List<CartItem>> =
        cartDao.observeCart().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addItem(
        productId: String,
        productName: String,
        productImageUrl: String,
        variant: ProductVariant,
        quantity: Int
    ) {
        val existing = cartDao.findExistingItem(
            productId = productId,
            condition = variant.condition.name,
            ram = variant.ramGb,
            storage = variant.storageGb,
            color = variant.color
        )
        if (existing != null) {
            cartDao.updateItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            cartDao.insertItem(
                CartItemEntity(
                    productId = productId,
                    productName = productName,
                    productImageUrl = productImageUrl,
                    condition = variant.condition.name,
                    processor = variant.processor,
                    ramGb = variant.ramGb,
                    storageGb = variant.storageGb,
                    color = variant.color,
                    price = variant.price,
                    quantity = quantity
                )
            )
        }
    }

    override suspend fun removeItem(itemId: Int) = cartDao.removeById(itemId)

    override suspend fun updateQuantity(itemId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.removeById(itemId)
        } else {
            // Fetch, copy, update
            cartDao.observeCart().collect { items ->
                items.find { it.id == itemId }?.let {
                    cartDao.updateItem(it.copy(quantity = newQuantity))
                }
                return@collect
            }
        }
    }

    override suspend fun clearCart() = cartDao.clearCart()
}
