package com.mercadoapp.di

import com.mercadoapp.data.repository.AuthRepositoryImpl
import com.mercadoapp.data.repository.CartRepositoryImpl
import com.mercadoapp.data.repository.RemoteProductRepository
import com.mercadoapp.domain.repository.AuthRepository
import com.mercadoapp.domain.repository.CartRepository
import com.mercadoapp.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}
