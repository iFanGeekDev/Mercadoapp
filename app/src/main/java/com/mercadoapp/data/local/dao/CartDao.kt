package com.mercadoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mercadoapp.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun observeCart(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity)

    @Update
    suspend fun updateItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    /** Returns the existing row for a specific variant combination (for quantity merge) */
    @Query(
        """SELECT * FROM cart_items
           WHERE productId = :productId
             AND condition = :condition
             AND ramGb = :ram
             AND storageGb = :storage
             AND color = :color
           LIMIT 1"""
    )
    suspend fun findExistingItem(
        productId: String,
        condition: String,
        ram: Int,
        storage: Int,
        color: String
    ): CartItemEntity?
}
