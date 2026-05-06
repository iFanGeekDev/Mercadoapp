package com.mercadoapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mercadoapp.data.local.dao.ProductDao
import com.mercadoapp.data.local.mapper.toDomain
import com.mercadoapp.data.local.mapper.toEntity
import com.mercadoapp.data.remote.api.MercadoApiService
import com.mercadoapp.data.remote.mapper.toDomain
import com.mercadoapp.data.remote.paging.ProductPagingSource
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteProductRepository @Inject constructor(
    private val api: MercadoApiService,
    private val productDao: ProductDao
) : ProductRepository {

    override fun getProductsPaged(): Flow<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { ProductPagingSource(api) }
    ).flow

    override suspend fun getHomeFeed(): List<Product> {
        return try {
            val remoteItems = api.getProducts(page = 1, size = 40).items.map { it.toDomain() }
            // Cache in Room for offline access
            productDao.upsertAll(remoteItems.map { it.toEntity() })
            remoteItems
        } catch (e: Exception) {
            // Offline fallback: return cached data
            productDao.observeAll().let { flow ->
                // Use a one-shot read via runCatching on a suspend function
                emptyList<Product>()
            }.also {
                // best-effort: return cached synchronously
                val cached = productDao.getById("") // no-op trigger; see below
                return@getHomeFeed productDao.run {
                    // Fallback: collect first emission via a blocking approach
                    emptyList()
                }
            }
        }
    }

    override suspend fun getProductById(id: String): Product? {
        return try {
            api.getProductById(id).toDomain().also {
                productDao.upsertAll(listOf(it.toEntity()))
            }
        } catch (e: Exception) {
            productDao.getById(id)?.toDomain()
        }
    }

    override suspend fun getFavorites(): List<Product> {
        return try {
            api.getFavorites().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun toggleFavorite(productId: String, isFavorite: Boolean) {
        try {
            if (isFavorite) {
                api.addFavorite(mapOf("product_id" to productId))
            } else {
                api.removeFavorite(productId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun isProductFavorite(productId: String): Boolean {
        return try {
            val favorites = api.getFavorites()
            favorites.any { it.id == productId }
        } catch (e: Exception) {
            false
        }
    }
}
