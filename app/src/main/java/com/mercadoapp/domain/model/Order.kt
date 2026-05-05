package com.mercadoapp.domain.model

import java.time.Instant

enum class OrderStatus {
    PROCESSING, SHIPPED, DELIVERED, CANCELLED;

    fun displayName(): String = when (this) {
        PROCESSING -> "En proceso"
        SHIPPED    -> "Enviado"
        DELIVERED  -> "Entregado"
        CANCELLED  -> "Cancelado"
    }
}

data class OrderItem(
    val productId:   String,
    val productName: String,
    val imageUrl:    String,
    val condition:   String,
    val color:       String,
    val storageGb:   Int,
    val quantity:    Int,
    val price:       Double
)

data class Order(
    val id:        String,
    val status:    OrderStatus,
    val createdAt: Instant,
    val total:     Double,
    val items:     List<OrderItem>
)
