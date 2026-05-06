package com.mercadoapp.data.remote.api

import com.mercadoapp.data.remote.dto.LoginRequestDto
import com.mercadoapp.data.remote.dto.PagedOrdersDto
import com.mercadoapp.data.remote.dto.PagedProductsDto
import com.mercadoapp.data.remote.dto.ProductDto
import com.mercadoapp.data.remote.dto.RegisterRequestDto
import com.mercadoapp.data.remote.dto.TokenDto
import com.mercadoapp.data.remote.dto.UbigeoDto
import com.mercadoapp.data.remote.dto.UserDto
import com.mercadoapp.data.remote.dto.UpdateProfileRequestDto
import com.mercadoapp.data.remote.dto.ChangePasswordRequestDto
import retrofit2.http.*
import okhttp3.MultipartBody

interface MercadoApiService {

    // ── User Management ─────────────────────────────────────────────────────────

    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): UserDto

    @PUT("users/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Any

    @Multipart
    @POST("users/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): UserDto

    // ── Products ───────────────────────────────────────────────────────────────

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): PagedProductsDto

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto

    // ── Favorites ──────────────────────────────────────────────────────────────

    @GET("favorites")
    suspend fun getFavorites(): List<ProductDto>

    @POST("favorites")
    suspend fun addFavorite(@Body request: Map<String, String>): Any

    @DELETE("favorites/{productId}")
    suspend fun removeFavorite(@Path("productId") productId: String): Any

    // ── Auth ───────────────────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): TokenDto

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): TokenDto

    @GET("auth/me")
    suspend fun getMe(): UserDto

    // ── Orders ─────────────────────────────────────────────────────────────────

    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): PagedOrdersDto

    // ── Ubigeo (Perú) ──────────────────────────────────────────────────────────

    @GET("ubigeo/departments")
    suspend fun getDepartments(): List<UbigeoDto>

    @GET("ubigeo/provinces/{departmentId}")
    suspend fun getProvinces(@Path("departmentId") departmentId: String): List<UbigeoDto>

    @GET("ubigeo/districts/{provinceId}")
    suspend fun getDistricts(@Path("provinceId") provinceId: String): List<UbigeoDto>
}
