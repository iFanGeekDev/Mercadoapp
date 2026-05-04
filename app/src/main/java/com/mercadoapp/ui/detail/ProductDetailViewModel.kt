package com.mercadoapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.Condition
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.model.ProductOptionResolver
import com.mercadoapp.domain.model.ProductOptionsState
import com.mercadoapp.domain.model.ProductSelection
import com.mercadoapp.domain.model.SelectableOption
import com.mercadoapp.domain.repository.CartRepository
import com.mercadoapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(ProductDetailUiState(isLoading = true))
    val state: StateFlow<ProductDetailUiState> = _state.asStateFlow()

    init { loadProduct() }

    private fun loadProduct() = viewModelScope.launch {
        val product = repository.getProductById(productId)
        _state.value = ProductDetailUiState(
            isLoading = false,
            product = product,
            selection = ProductSelection(),
            optionsState = product?.let { ProductOptionResolver.resolve(it, ProductSelection()) }
        )
    }

    fun onConditionChanged(value: Condition) = updateSelection { copy(condition = value) }
    fun onProcessorChanged(value: String)   = updateSelection { copy(processor = value) }
    fun onRamChanged(value: Int)            = updateSelection { copy(ramGb = value) }
    fun onStorageChanged(value: Int)        = updateSelection { copy(storageGb = value) }
    fun onColorChanged(value: String)       = updateSelection { copy(color = value) }

    fun addToCart() {
        val current = _state.value
        val product = current.product ?: return
        val variant = current.optionsState?.selectedVariant ?: return
        viewModelScope.launch {
            cartRepository.addItem(
                productId = product.id,
                productName = product.name,
                productImageUrl = product.imageUrl,
                variant = variant
            )
            _state.value = current.copy(cartAdded = true)
            delay(2_000)
            _state.value = _state.value.copy(cartAdded = false)
        }
    }

    private fun updateSelection(transform: ProductSelection.() -> ProductSelection) {
        val current = _state.value
        val product = current.product ?: return
        val updatedSelection = current.selection.transform().sanitizeAgainst(product)
        _state.value = current.copy(
            selection = updatedSelection,
            optionsState = ProductOptionResolver.resolve(product, updatedSelection),
            cartAdded = false
        )
    }
}

private fun ProductSelection.sanitizeAgainst(product: Product): ProductSelection {
    var sanitized = this
    val options = ProductOptionResolver.resolve(product, sanitized)
    if (sanitized.condition != null && options.conditions.none { it.value == sanitized.condition && it.enabled })
        sanitized = sanitized.copy(condition = null)
    if (sanitized.processor != null && options.processors.none { it.value == sanitized.processor && it.enabled })
        sanitized = sanitized.copy(processor = null)
    if (sanitized.ramGb != null && options.rams.none { it.value == sanitized.ramGb && it.enabled })
        sanitized = sanitized.copy(ramGb = null)
    if (sanitized.storageGb != null && options.storages.none { it.value == sanitized.storageGb && it.enabled })
        sanitized = sanitized.copy(storageGb = null)
    if (sanitized.color != null && options.colors.none { it.value == sanitized.color && it.enabled })
        sanitized = sanitized.copy(color = null)
    return sanitized
}

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val selection: ProductSelection = ProductSelection(),
    val optionsState: ProductOptionsState? = null,
    val cartAdded: Boolean = false
)

val EmptyOptions = ProductOptionsState(
    conditions = emptyList<SelectableOption<Condition>>(),
    processors = emptyList(),
    rams = emptyList(),
    storages = emptyList(),
    colors = emptyList(),
    selectedVariant = null
)
