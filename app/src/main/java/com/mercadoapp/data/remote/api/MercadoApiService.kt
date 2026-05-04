package com.mercadoapp.data.remote.api

import com.mercadoapp.data.remote.dto.LoginRequestDto
import com.mercadoapp.data.remote.dto.PagedProductsDto
import com.mercadoapp.data.remote.dto.ProductDto
import com.mercadoapp.data.remote.dto.RegisterRequestDto
import com.mercadoapp.data.remote.dto.TokenDto
import com.mercadoapp.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MercadoApiService {

    // ── Products ───────────────────────────────────────────────────────────────

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): PagedProductsDto

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto

    // ── Auth ───────────────────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): TokenDto

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): TokenDto

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") bearerToken: String): UserDto
}
