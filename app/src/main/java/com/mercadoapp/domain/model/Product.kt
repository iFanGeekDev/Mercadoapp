package com.mercadoapp.domain.model

data class Product(
    val id: String,
    val name: String,
    val imageUrl: String,
    val shortDescription: String,
    val technicalSpecs: TechnicalSpecs,
    val variants: List<ProductVariant>,
    val isOffer: Boolean = false,
    val isNewArrival: Boolean = false
)

data class TechnicalSpecs(
    val processor: List<String>,
    val ramGb: List<Int>,
    val storageGb: List<Int>,
    val colors: List<String>
)

data class ProductVariant(
    val condition: Condition,
    val processor: String,
    val ramGb: Int,
    val storageGb: Int,
    val color: String,
    val price: Double,
    val stock: Int
)

enum class Condition { FAIR, NORMAL, EXCELLENT }
