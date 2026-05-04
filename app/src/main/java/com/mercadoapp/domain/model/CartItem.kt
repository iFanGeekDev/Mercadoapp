package com.mercadoapp.domain.model

data class CartItem(
    val id: Int = 0,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val variant: ProductVariant,
    val quantity: Int = 1
) {
    val subtotal: Double get() = variant.price * quantity
}
