package com.mercadoapp.di

import android.content.Context
import androidx.room.Room
import com.mercadoapp.data.local.dao.CartDao
import com.mercadoapp.data.local.dao.ProductDao
import com.mercadoapp.data.local.db.MercadoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMercadoDatabase(@ApplicationContext context: Context): MercadoDatabase =
        Room.databaseBuilder(context, MercadoDatabase::class.java, "mercado.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProductDao(db: MercadoDatabase): ProductDao = db.productDao()

    @Provides
    fun provideCartDao(db: MercadoDatabase): CartDao = db.cartDao()
}
