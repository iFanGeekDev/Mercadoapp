package com.mercadoapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mercadoapp.data.local.dao.CartDao
import com.mercadoapp.data.local.dao.ProductDao
import com.mercadoapp.data.local.entity.CartItemEntity
import com.mercadoapp.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, CartItemEntity::class],
    version = 3,
    exportSchema = false
)
abstract class MercadoDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}
