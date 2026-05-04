package com.mercadoapp.ui.cart

import com.mercadoapp.domain.model.CartItem
import com.mercadoapp.domain.model.Condition
import com.mercadoapp.domain.model.ProductVariant
import com.mercadoapp.domain.repository.CartRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var cartRepository: CartRepository
    private lateinit var viewModel: CartViewModel

    private val fakeVariant = ProductVariant(
        condition = Condition.NORMAL,
        processor = "A15",
        ramGb = 4,
        storageGb = 128,
        color = "Black",
        price = 299.0,
        stock = 5
    )

    private val fakeItems = listOf(
        CartItem(id = 1, productId = "p1", productName = "iPhone 13",
            productImageUrl = "", variant = fakeVariant, quantity = 2)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cartRepository = mockk()
        every { cartRepository.cartItems } returns MutableStateFlow(fakeItems)
        coEvery { cartRepository.removeItem(any()) }  returns Unit
        coEvery { cartRepository.updateQuantity(any(), any()) } returns Unit
        coEvery { cartRepository.clearCart() } returns Unit
        viewModel = CartViewModel(cartRepository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `state reflects items from repository`() = runTest {
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(1, state.items.size)
        assertEquals("iPhone 13", state.items.first().productName)
    }

    @Test
    fun `total is correctly computed`() = runTest {
        advanceUntilIdle()
        // 2 items × 299.0 = 598.0
        assertEquals(598.0, viewModel.state.value.total, 0.001)
    }

    @Test
    fun `itemCount sums all quantities`() = runTest {
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.itemCount)
    }

    @Test
    fun `removeItem delegates to repository`() = runTest {
        viewModel.removeItem(1)
        advanceUntilIdle()
        coVerify { cartRepository.removeItem(1) }
    }

    @Test
    fun `clearCart delegates to repository`() = runTest {
        viewModel.clearCart()
        advanceUntilIdle()
        coVerify { cartRepository.clearCart() }
    }

    @Test
    fun `empty cart has zero total and zero count`() = runTest {
        every { cartRepository.cartItems } returns flowOf(emptyList())
        val vm = CartViewModel(cartRepository)
        advanceUntilIdle()
        assertEquals(0.0, vm.state.value.total, 0.0)
        assertEquals(0, vm.state.value.itemCount)
    }
}
