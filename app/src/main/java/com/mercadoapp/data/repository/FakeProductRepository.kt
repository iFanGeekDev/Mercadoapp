package com.mercadoapp.data.repository

import com.mercadoapp.domain.model.Condition
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.model.ProductVariant
import com.mercadoapp.domain.model.TechnicalSpecs
import com.mercadoapp.domain.repository.ProductRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeProductRepository @Inject constructor() : ProductRepository {

    private val products = listOf(
        Product(
            id = "iphone-13",
            name = "iPhone 13",
            imageUrl = "https://example.com/iphone13.jpg",
            shortDescription = "Pantalla OLED 6.1 y gran rendimiento.",
            technicalSpecs = TechnicalSpecs(
                processor = listOf("A15 Bionic"),
                ramGb = listOf(4),
                storageGb = listOf(128, 256),
                colors = listOf("Midnight", "Blue", "Red")
            ),
            variants = listOf(
                ProductVariant(Condition.FAIR, "A15 Bionic", 4, 128, "Midnight", 349.0, 3),
                ProductVariant(Condition.NORMAL, "A15 Bionic", 4, 128, "Blue", 419.0, 8),
                ProductVariant(Condition.EXCELLENT, "A15 Bionic", 4, 256, "Red", 519.0, 4)
            ),
            isOffer = true,
            isNewArrival = true
        ),
        Product(
            id = "s22",
            name = "Samsung S22",
            imageUrl = "https://example.com/s22.jpg",
            shortDescription = "Compacto, potente y excelente cámara.",
            technicalSpecs = TechnicalSpecs(
                processor = listOf("Snapdragon 8 Gen 1"),
                ramGb = listOf(8),
                storageGb = listOf(128, 256),
                colors = listOf("Black", "White")
            ),
            variants = listOf(
                ProductVariant(Condition.FAIR, "Snapdragon 8 Gen 1", 8, 128, "Black", 309.0, 5),
                ProductVariant(Condition.NORMAL, "Snapdragon 8 Gen 1", 8, 256, "White", 399.0, 7)
            ),
            isOffer = false,
            isNewArrival = true
        )
    )

    override suspend fun getHomeFeed(): List<Product> {
        delay(250)
        return products
    }

    override suspend fun getProductById(id: String): Product? {
        delay(150)
        return products.firstOrNull { it.id == id }
    }
}
