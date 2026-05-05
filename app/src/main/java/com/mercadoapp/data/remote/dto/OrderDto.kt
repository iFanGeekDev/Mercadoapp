package com.mercadoapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemDto(
    @SerialName("product_id")   val productId:   String,
    @SerialName("product_name") val productName: String,
    @SerialName("image_url")    val imageUrl:    String,
    val condition:              String,
    val color:                  String,
    @SerialName("storage_gb")   val storageGb:   Int,
    val quantity:               Int,
    val price:                  Double
)

@Serializable
data class OrderDto(
    val id:                     String,
    val status:                 String,
    @SerialName("created_at")   val createdAt:   String,
    val total:                  Double,
    val items:                  List<OrderItemDto>
)

@Serializable
data class PagedOrdersDto(
    val items:                   List<OrderDto>,
    val page:                    Int,
    @SerialName("total_pages")   val totalPages:  Int,
    @SerialName("total_count")   val totalCount:  Int = 0
)
