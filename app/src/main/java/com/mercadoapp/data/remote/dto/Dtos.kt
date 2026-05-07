package com.mercadoapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("short_description") val shortDescription: String,
    @SerialName("technical_specs") val technicalSpecs: TechnicalSpecsDto,
    val variants: List<ProductVariantDto>,
    @SerialName("is_offer") val isOffer: Boolean = false,
    @SerialName("is_new_arrival") val isNewArrival: Boolean = false,
    val category: String = "PHONES"
)

@Serializable
data class TechnicalSpecsDto(
    val processor: List<String>,
    @SerialName("ram_gb") val ramGb: List<Int>,
    @SerialName("storage_gb") val storageGb: List<Int>,
    val colors: List<String>
)

@Serializable
data class ProductVariantDto(
    val condition: String,          // "FAIR" | "NORMAL" | "EXCELLENT"
    val processor: String,
    @SerialName("ram_gb") val ramGb: Int,
    @SerialName("storage_gb") val storageGb: Int,
    val color: String,
    val price: Double,
    val stock: Int
)

/** Wrapper de respuesta paginada */
@Serializable
data class PagedProductsDto(
    val items: List<ProductDto>,
    val page: Int,
    @SerialName("total_pages") val totalPages: Int
)

/** DTOs de autenticación */
@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class TokenDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

@Serializable
data class UpdateProfileRequestDto(
    val name: String,
    val email: String
)

@Serializable
data class ChangePasswordRequestDto(
    @SerialName("old_password") val oldPassword: String,
    @SerialName("new_password") val newPassword: String
)

@Serializable
data class AddressDto(
    val id: String? = null,
    val alias: String,
    val street: String,
    val number: String? = null,
    @SerialName("department_id") val departmentId: String? = null,
    @SerialName("province_id") val provinceId: String? = null,
    @SerialName("district_id") val districtId: String? = null,
    @SerialName("department_name") val departmentName: String,
    @SerialName("province_name") val provinceName: String,
    @SerialName("district_name") val districtName: String,
    @SerialName("is_default") val isDefault: Boolean = false
)

@Serializable
data class MessageResponseDto(
    val message: String
)

@Serializable
data class ErrorResponseDto(
    val error: String
)
