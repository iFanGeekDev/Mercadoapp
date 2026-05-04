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

    fun onConditionChanged(value: Condition) = updateSelection("condition", value)
    fun onProcessorChanged(value: String)    = updateSelection("processor", value)
    fun onRamChanged(value: Int)             = updateSelection("ram", value)
    fun onStorageChanged(value: Int)         = updateSelection("storage", value)
    fun onColorChanged(value: String)        = updateSelection("color", value)

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

    private fun updateSelection(anchorField: String, value: Any) {
        val current = _state.value
        val product = current.product ?: return
        
        val rawSelection = current.selection.copyField(anchorField, value)
        val updatedSelection = sanitizeSelection(product, rawSelection, anchorField)
        
        _state.value = current.copy(
            selection = updatedSelection,
            optionsState = ProductOptionResolver.resolve(product, updatedSelection),
            cartAdded = false
        )
    }
}

private fun ProductSelection.copyField(field: String, value: Any): ProductSelection {
    return when (field) {
        "condition" -> copy(condition = value as Condition)
        "processor" -> copy(processor = value as String)
        "ram" -> copy(ramGb = value as Int)
        "storage" -> copy(storageGb = value as Int)
        "color" -> copy(color = value as String)
        else -> this
    }
}

private fun ProductSelection.getField(field: String): Any? {
    return when (field) {
        "condition" -> condition
        "processor" -> processor
        "ram" -> ramGb
        "storage" -> storageGb
        "color" -> color
        else -> null
    }
}

private fun ProductSelection.withField(field: String, other: ProductSelection): ProductSelection {
    val value = other.getField(field) ?: return this
    return when (field) {
        "condition" -> copy(condition = value as Condition)
        "processor" -> copy(processor = value as String)
        "ram" -> copy(ramGb = value as Int)
        "storage" -> copy(storageGb = value as Int)
        "color" -> copy(color = value as String)
        else -> this
    }
}

private fun isValidSelection(product: Product, selection: ProductSelection): Boolean {
    return product.variants.any { variant ->
        (selection.condition == null || variant.condition == selection.condition) &&
        (selection.processor == null || variant.processor == selection.processor) &&
        (selection.ramGb == null || variant.ramGb == selection.ramGb) &&
        (selection.storageGb == null || variant.storageGb == selection.storageGb) &&
        (selection.color == null || variant.color == selection.color) &&
        variant.stock > 0
    }
}

private fun sanitizeSelection(product: Product, selection: ProductSelection, anchor: String): ProductSelection {
    if (isValidSelection(product, selection)) return selection

    // Start with just the anchor
    var result = ProductSelection().withField(anchor, selection)
    
    // If the anchor alone has no stock, drop the selection entirely
    if (!isValidSelection(product, result)) return ProductSelection()

    // Try adding other fields one by one
    val fields = listOf("condition", "processor", "ram", "storage", "color").filter { it != anchor }
    for (field in fields) {
        if (selection.getField(field) != null) {
            val candidate = result.withField(field, selection)
            if (isValidSelection(product, candidate)) {
                result = candidate
            }
        }
    }
    return result
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
