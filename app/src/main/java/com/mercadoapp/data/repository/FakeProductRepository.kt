package com.mercadoapp.data.repository

import androidx.paging.PagingData
import com.mercadoapp.domain.model.Condition
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.model.ProductVariant
import com.mercadoapp.domain.model.TechnicalSpecs
import com.mercadoapp.domain.repository.ProductRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeProductRepository @Inject constructor() : ProductRepository {

    private val products = listOf(
        Product(
            id = "iphone-13",
            name = "iPhone 13",
            imageUrl = "https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=400",
            shortDescription = "Pantalla OLED 6.1\" y gran rendimiento.",
            technicalSpecs = TechnicalSpecs(
                processor = listOf("A15 Bionic"),
                ramGb = listOf(4),
                storageGb = listOf(128, 256),
                colors = listOf("Midnight", "Blue", "Red")
            ),
            variants = listOf(
                ProductVariant(Condition.FAIR,      "A15 Bionic", 4, 128, "Midnight", 349.0, 3),
                ProductVariant(Condition.NORMAL,    "A15 Bionic", 4, 128, "Blue",     419.0, 8),
                ProductVariant(Condition.EXCELLENT, "A15 Bionic", 4, 256, "Red",      519.0, 4)
            ),
            isOffer = true,
            isNewArrival = true,
            category = "PHONES"
        ),
        Product(
            id = "s22",
            name = "Samsung S22",
            imageUrl = "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400",
            shortDescription = "Compacto, potente y excelente cámara.",
            technicalSpecs = TechnicalSpecs(
                processor = listOf("Snapdragon 8 Gen 1"),
                ramGb = listOf(8),
                storageGb = listOf(128, 256),
                colors = listOf("Black", "White")
            ),
            variants = listOf(
                ProductVariant(Condition.FAIR,   "Snapdragon 8 Gen 1", 8, 128, "Black", 309.0, 5),
                ProductVariant(Condition.NORMAL, "Snapdragon 8 Gen 1", 8, 256, "White", 399.0, 7)
            ),
            isOffer = false,
            isNewArrival = true,
            category = "PHONES"
        ),
        Product(
            id = "pixel-7",
            name = "Google Pixel 7",
            imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=400",
            shortDescription = "La mejor cámara computacional del mercado.",
            technicalSpecs = TechnicalSpecs(
                processor = listOf("Google Tensor G2"),
                ramGb = listOf(8),
                storageGb = listOf(128, 256),
                colors = listOf("Obsidian", "Snow", "Lemongrass")
            ),
            variants = listOf(
                ProductVariant(Condition.NORMAL,    "Google Tensor G2", 8, 128, "Obsidian",   449.0, 6),
                ProductVariant(Condition.EXCELLENT, "Google Tensor G2", 8, 256, "Snow",        549.0, 2)
            ),
            isOffer = true,
            isNewArrival = false,
            category = "PHONES"
        ),
        Product(
            id = "xiaomi-13",
            name = "Xiaomi 13",
            imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400",
            shortDescription = "Leica optics, flagship performance.",
            technicalSpecs = TechnicalSpecs(
                processor = listOf("Snapdragon 8 Gen 2"),
                ramGb = listOf(8, 12),
                storageGb = listOf(256),
                colors = listOf("Black", "White", "Flora Green")
            ),
            variants = listOf(
                ProductVariant(Condition.FAIR,      "Snapdragon 8 Gen 2", 8,  256, "Black",       389.0, 4),
                ProductVariant(Condition.NORMAL,    "Snapdragon 8 Gen 2", 8,  256, "White",       469.0, 3),
                ProductVariant(Condition.EXCELLENT, "Snapdragon 8 Gen 2", 12, 256, "Flora Green", 549.0, 1)
            ),
            isOffer = false,
            isNewArrival = true,
            category = "PHONES"
        )
    )

    /** Used by HomeScreen via Paging 3 — returns a single page with all fake products */
    override fun getProductsPaged(category: String?, search: String?): Flow<PagingData<Product>> {
        val filtered = products.filter { product ->
            val matchesCategory = if (category != null && category != "ALL") {
                product.category == category
            } else true

            val matchesSearch = if (!search.isNullOrBlank()) {
                product.name.contains(search, ignoreCase = true) ||
                        product.shortDescription.contains(search, ignoreCase = true)
            } else true

            matchesCategory && matchesSearch
        }
        return flowOf(PagingData.from(filtered))
    }

    override suspend fun getHomeFeed(): List<Product> {
        delay(250)
        return products
    }

    override suspend fun getProductById(id: String): Product? {
        delay(150)
        return products.firstOrNull { it.id == id }
    }

    override suspend fun getFavorites(): List<Product> = emptyList()
    override suspend fun toggleFavorite(productId: String, isFavorite: Boolean) {}
    override suspend fun isProductFavorite(productId: String): Boolean = false
}
