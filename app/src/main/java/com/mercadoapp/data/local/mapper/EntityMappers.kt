package com.mercadoapp.data.local.mapper

import com.mercadoapp.data.local.entity.CartItemEntity
import com.mercadoapp.data.local.entity.ProductEntity
import com.mercadoapp.domain.model.CartItem
import com.mercadoapp.domain.model.Condition
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.model.ProductVariant
import com.mercadoapp.domain.model.TechnicalSpecs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// ── ProductEntity ↔ Domain ─────────────────────────────────────────────────────

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    shortDescription = shortDescription,
    technicalSpecsJson = json.encodeToString(
        TechnicalSpecsSerializable(
            processor = technicalSpecs.processor,
            ramGb = technicalSpecs.ramGb,
            storageGb = technicalSpecs.storageGb,
            colors = technicalSpecs.colors
        )
    ),
    variantsJson = json.encodeToString(
        variants.map {
            VariantSerializable(
                condition = it.condition.name,
                processor = it.processor,
                ramGb = it.ramGb,
                storageGb = it.storageGb,
                color = it.color,
                price = it.price,
                stock = it.stock
            )
        }
    ),
    isOffer = isOffer,
    isNewArrival = isNewArrival
)

fun ProductEntity.toDomain(): Product {
    val specs = json.decodeFromString<TechnicalSpecsSerializable>(technicalSpecsJson)
    val variantsList = json.decodeFromString<List<VariantSerializable>>(variantsJson)
    return Product(
        id = id,
        name = name,
        imageUrl = imageUrl,
        shortDescription = shortDescription,
        technicalSpecs = TechnicalSpecs(
            processor = specs.processor,
            ramGb = specs.ramGb,
            storageGb = specs.storageGb,
            colors = specs.colors
        ),
        variants = variantsList.map {
            ProductVariant(
                condition = Condition.valueOf(it.condition),
                processor = it.processor,
                ramGb = it.ramGb,
                storageGb = it.storageGb,
                color = it.color,
                price = it.price,
                stock = it.stock
            )
        },
        isOffer = isOffer,
        isNewArrival = isNewArrival
    )
}

// ── CartItemEntity ↔ Domain ────────────────────────────────────────────────────

fun CartItemEntity.toDomain(): CartItem = CartItem(
    id = id,
    productId = productId,
    productName = productName,
    productImageUrl = productImageUrl,
    variant = ProductVariant(
        condition = Condition.valueOf(condition),
        processor = processor,
        ramGb = ramGb,
        storageGb = storageGb,
        color = color,
        price = price,
        stock = 0   // stock not relevant in cart context
    ),
    quantity = quantity
)

fun CartItem.toEntity(): CartItemEntity = CartItemEntity(
    id = id,
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

// ── Internal serializable helpers (avoid adding @Serializable to domain) ───────

@kotlinx.serialization.Serializable
private data class TechnicalSpecsSerializable(
    val processor: List<String>,
    val ramGb: List<Int>,
    val storageGb: List<Int>,
    val colors: List<String>
)

@kotlinx.serialization.Serializable
private data class VariantSerializable(
    val condition: String,
    val processor: String,
    val ramGb: Int,
    val storageGb: Int,
    val color: String,
    val price: Double,
    val stock: Int
)
