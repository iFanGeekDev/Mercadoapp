package com.mercadoapp.di

import com.mercadoapp.data.repository.AuthRepositoryImpl
import com.mercadoapp.data.repository.CartRepositoryImpl
import com.mercadoapp.data.repository.RemoteOrderRepository
import com.mercadoapp.data.repository.RemoteProductRepository
import com.mercadoapp.domain.repository.AuthRepository
import com.mercadoapp.domain.repository.CartRepository
import com.mercadoapp.domain.repository.OrderRepository
import com.mercadoapp.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule — MODO REAL (conectado al backend local en puerto 8080)
 *
 * Para volver al modo demo sin backend:
 *   AuthRepositoryImpl    → FakeAuthRepository
 *   RemoteProductRepository → FakeProductRepository
 *
 * Backend local:  cd backend && node server.js
 * URL emulador:   http://10.0.2.2:8080/v1
 * Credenciales:   demo@mercadoapp.dev / demo1234
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: RemoteProductRepository): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: com.mercadoapp.data.repository.FakeAddressRepository): com.mercadoapp.domain.repository.AddressRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: RemoteOrderRepository): OrderRepository
}
