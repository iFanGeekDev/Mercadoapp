package com.mercadoapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val shortDescription: String,
    /** JSON-serialised TechnicalSpecs (stored as string for simplicity) */
    val technicalSpecsJson: String,
    /** JSON-serialised List<ProductVariant> */
    val variantsJson: String,
    val isOffer: Boolean,
    val isNewArrival: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    /** Serialized variant info */
    val condition: String,
    val processor: String,
    val ramGb: Int,
    val storageGb: Int,
    val color: String,
    val price: Double,
    val quantity: Int = 1
)
