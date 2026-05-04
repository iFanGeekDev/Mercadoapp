package com.mercadoapp.di

import com.mercadoapp.data.repository.CartRepositoryImpl
import com.mercadoapp.data.repository.FakeAuthRepository        // ← DEMO MODE
import com.mercadoapp.data.repository.FakeProductRepository     // ← DEMO MODE
import com.mercadoapp.domain.repository.AuthRepository
import com.mercadoapp.domain.repository.CartRepository
import com.mercadoapp.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule — DEMO / DEVELOPMENT mode
 *
 * Auth:     FakeAuthRepository  →  email: demo@mercadoapp.dev  |  password: demo1234
 * Products: FakeProductRepository → datos de ejemplo sin backend
 *
 * Para conectar el backend real, cambiar:
 *   FakeAuthRepository    → AuthRepositoryImpl
 *   FakeProductRepository → RemoteProductRepository
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: FakeProductRepository): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FakeAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
}
