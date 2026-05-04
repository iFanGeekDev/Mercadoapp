package com.mercadoapp.domain.model

import org.junit.Assert.*
import org.junit.Test

/** Extended unit tests for ProductOptionResolver covering more edge cases. */
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
            ProductVariant(Condition.FAIR,      "P1", 8,  128, "Black", 100.0, 2),
            ProductVariant(Condition.NORMAL,    "P1", 8,  256, "Blue",  120.0, 1),
            ProductVariant(Condition.EXCELLENT, "P2", 12, 256, "Black", 160.0, 0)  // out of stock
        )
    )

    // ── Existing tests (kept) ──────────────────────────────────────────────────

    @Test
    fun resolve_disablesOutOfStockVariants() {
        val state = ProductOptionResolver.resolve(product, ProductSelection(condition = Condition.EXCELLENT))
        assertFalse(state.conditions.first { it.value == Condition.EXCELLENT }.enabled)
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
            ProductSelection(condition = Condition.FAIR, processor = "P1", ramGb = 8, storageGb = 128, color = "Black")
        )
        assertEquals(100.0, state.selectedVariant?.price ?: 0.0, 0.0)
    }

    // ── New edge-case tests ────────────────────────────────────────────────────

    @Test
    fun resolve_emptySelection_allAvailableOptionsEnabled() {
        val state = ProductOptionResolver.resolve(product, ProductSelection())
        // EXCELLENT is stock=0 so it should be disabled even with no selection
        assertFalse(state.conditions.first { it.value == Condition.EXCELLENT }.enabled)
        // FAIR and NORMAL are in stock
        assertTrue(state.conditions.first { it.value == Condition.FAIR }.enabled)
        assertTrue(state.conditions.first { it.value == Condition.NORMAL }.enabled)
    }

    @Test
    fun resolve_noMatch_returnsNullVariant() {
        val state = ProductOptionResolver.resolve(
            product,
            ProductSelection(condition = Condition.EXCELLENT, processor = "P2", ramGb = 12, storageGb = 256, color = "Black")
        )
        // EXCELLENT exists but stock=0, selectedVariant should be found structurally but stock should be 0
        assertEquals(0, state.selectedVariant?.stock)
    }

    @Test
    fun resolve_partialSelection_doesNotLockUnrelatedOptions() {
        val state = ProductOptionResolver.resolve(product, ProductSelection(processor = "P1"))
        // Both FAIR and NORMAL are valid for P1
        assertTrue(state.conditions.first { it.value == Condition.FAIR }.enabled)
        assertTrue(state.conditions.first { it.value == Condition.NORMAL }.enabled)
    }

    @Test
    fun resolve_colorOptions_correctlyFiltered() {
        // Selecting FAIR locks color to Black (only FAIR variant has Black, Blue is NORMAL)
        val state = ProductOptionResolver.resolve(product, ProductSelection(condition = Condition.FAIR))
        assertTrue(state.colors.first { it.value == "Black" }.enabled)
        assertFalse(state.colors.first { it.value == "Blue" }.enabled)
    }

    @Test
    fun resolve_allConditionsHaveCorrectCount() {
        val state = ProductOptionResolver.resolve(product, ProductSelection())
        assertEquals(Condition.entries.size, state.conditions.size)
    }
}
