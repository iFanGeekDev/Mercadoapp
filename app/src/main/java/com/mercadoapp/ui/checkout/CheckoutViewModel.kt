package com.mercadoapp.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.CartItem
import com.mercadoapp.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CheckoutStep {
    object Summary   : CheckoutStep()
    object Payment   : CheckoutStep()
    object Confirmed : CheckoutStep()
}

data class CheckoutUiState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val step: CheckoutStep = CheckoutStep.Summary,
    val isLoading: Boolean = false,
    val error: String? = null,
    // Shipping form
    val fullName: String = "",
    val address: String = "",
    val city: String = "",
    val phone: String = "",
    // Payment form
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val cardCvc: String = ""
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val items = cartRepository.cartItems.first()
            _state.value = _state.value.copy(
                items = items,
                total = items.sumOf { it.subtotal }
            )
        }
    }

    // ── Shipping fields ────────────────────────────────────────────────────────
    fun onFullNameChanged(v: String) = update { copy(fullName = v, error = null) }
    fun onAddressChanged(v: String)  = update { copy(address = v, error = null) }
    fun onCityChanged(v: String)     = update { copy(city = v, error = null) }
    fun onPhoneChanged(v: String)    = update { copy(phone = v, error = null) }

    // ── Payment fields ─────────────────────────────────────────────────────────
    fun onCardNumberChanged(v: String) = update { copy(cardNumber = v.filter(Char::isDigit).take(16), error = null) }
    fun onCardExpiryChanged(v: String) = update { copy(cardExpiry = formatExpiry(v), error = null) }
    fun onCardCvcChanged(v: String)    = update { copy(cardCvc = v.filter(Char::isDigit).take(4), error = null) }

    // ── Navigation between steps ───────────────────────────────────────────────
    fun proceedToPayment() {
        update { copy(step = CheckoutStep.Payment, error = null) }
    }

    fun confirmOrder() {
        val s = _state.value
        if (s.fullName.isBlank() || s.address.isBlank() || s.city.isBlank() || s.phone.isBlank()) {
            update { copy(error = "Completá todos los campos de envío") }
            return
        }
        if (s.cardNumber.length < 16 || s.cardExpiry.length < 5 || s.cardCvc.length < 3) {
            update { copy(error = "Datos de tarjeta inválidos") }
            return
        }
        viewModelScope.launch {
            update { copy(isLoading = true, error = null) }
            // Simulate network call
            kotlinx.coroutines.delay(1_500)
            cartRepository.clearCart()
            update { copy(isLoading = false, step = CheckoutStep.Confirmed) }
        }
    }

    fun goBack() {
        when (_state.value.step) {
            is CheckoutStep.Payment -> update { copy(step = CheckoutStep.Summary) }
            else -> { /* handled by navigation */ }
        }
    }

    private fun update(transform: CheckoutUiState.() -> CheckoutUiState) {
        _state.value = _state.value.transform()
    }

    private fun formatExpiry(raw: String): String {
        val digits = raw.filter(Char::isDigit).take(4)
        return if (digits.length >= 3) "${digits.take(2)}/${digits.drop(2)}" else digits
    }
}
