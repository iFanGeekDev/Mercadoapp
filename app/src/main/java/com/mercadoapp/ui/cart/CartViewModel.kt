package com.mercadoapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.CartItem
import com.mercadoapp.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val itemCount: Int = 0
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val state: StateFlow<CartUiState> = cartRepository.cartItems
        .map { items ->
            CartUiState(
                items = items,
                total = items.sumOf { it.subtotal },
                itemCount = items.sumOf { it.quantity }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartUiState()
        )

    fun removeItem(itemId: Int) = viewModelScope.launch {
        cartRepository.removeItem(itemId)
    }

    fun updateQuantity(itemId: Int, newQuantity: Int) = viewModelScope.launch {
        cartRepository.updateQuantity(itemId, newQuantity)
    }

    fun clearCart() = viewModelScope.launch {
        cartRepository.clearCart()
    }
}
