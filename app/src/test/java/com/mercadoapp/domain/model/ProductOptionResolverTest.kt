package com.mercadoapp.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductOptionResolverTest {

    private val product = Product(
        id = "test",
        name = "Test Phone",
        imageUrl = "https://example.com/test.jpg",
        shortDescription = "desc",
        technicalSpecs = TechnicalSpecs(
            processor = listOf("P1", "P2"),
            ramGb = listOf(8, 12),
            storageGb = listOf(128, 256),
            colors = listOf("Black", "Blue")
        ),
        variants = listOf(
            ProductVariant(Condition.FAIR, "P1", 8, 128, "Black", 100.0, 2),
            ProductVariant(Condition.NORMAL, "P1", 8, 256, "Blue", 120.0, 1),
            ProductVariant(Condition.EXCELLENT, "P2", 12, 256, "Black", 160.0, 0)
        )
    )

    @Test
    fun resolve_disablesOutOfStockVariants() {
        val state = ProductOptionResolver.resolve(product, ProductSelection(condition = Condition.EXCELLENT))

        val excellentOption = state.conditions.first { it.value == Condition.EXCELLENT }
        assertFalse(excellentOption.enabled)
    }

    @Test
    fun resolve_filtersCrossOptionsByCurrentSelection() {
        val state = ProductOptionResolver.resolve(product, ProductSelection(condition = Condition.NORMAL))

        assertTrue(state.storages.first { it.value == 256 }.enabled)
        assertFalse(state.storages.first { it.value == 128 }.enabled)
    }

    @Test
    fun resolve_returnsMatchedVariant() {
        val state = ProductOptionResolver.resolve(
            product,
            ProductSelection(
                condition = Condition.FAIR,
                processor = "P1",
                ramGb = 8,
                storageGb = 128,
                color = "Black"
            )
        )

        assertEquals(100.0, state.selectedVariant?.price ?: 0.0, 0.0)
    }
}
