package com.mercadoapp.di

import com.mercadoapp.data.repository.FakeProductRepository
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
    abstract fun bindProductRepository(repository: FakeProductRepository): ProductRepository
}
