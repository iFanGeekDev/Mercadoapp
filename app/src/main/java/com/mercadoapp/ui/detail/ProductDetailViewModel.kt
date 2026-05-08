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

    init { 
        loadProduct()
        checkFavoriteStatus()
    }

    private fun checkFavoriteStatus() = viewModelScope.launch {
        val isFav = repository.isProductFavorite(productId)
        _state.value = _state.value.copy(isFavorite = isFav)
    }

    private fun loadProduct() = viewModelScope.launch {
        val product = repository.getProductById(productId)
        var initialSelection = ProductSelection()
        if (product != null) {
            initialSelection = autoSelectFields(product, initialSelection)
        }
        _state.value = ProductDetailUiState(
            isLoading = false,
            product = product,
            selection = initialSelection,
            optionsState = product?.let { ProductOptionResolver.resolve(it, initialSelection) }
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

    fun toggleFavorite() = viewModelScope.launch {
        val current = _state.value
        val newStatus = !current.isFavorite
        _state.value = current.copy(isFavorite = newStatus)
        repository.toggleFavorite(productId, newStatus)
    }

    private fun updateSelection(anchorField: String, value: Any) {
        val current = _state.value
        val product = current.product ?: return
        
        val rawSelection = current.selection.copyField(anchorField, value)
        var updatedSelection = sanitizeSelection(product, rawSelection, anchorField)
        updatedSelection = autoSelectFields(product, updatedSelection)
        
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

private fun autoSelectFields(product: Product, selection: ProductSelection): ProductSelection {
    var current = selection
    var changed = true
    val fields = listOf("condition", "processor", "ram", "storage", "color")
    
    while (changed) {
        changed = false
        for (field in fields) {
            if (current.getField(field) == null) {
                val matchingVariants = product.variants.filter { variant ->
                    (current.condition == null || variant.condition == current.condition) &&
                    (current.processor == null || variant.processor == current.processor) &&
                    (current.ramGb == null || variant.ramGb == current.ramGb) &&
                    (current.storageGb == null || variant.storageGb == current.storageGb) &&
                    (current.color == null || variant.color == current.color) &&
                    variant.stock > 0
                }
                
                val uniqueValues = when (field) {
                    "condition" -> matchingVariants.map { it.condition }.toSet()
                    "processor" -> matchingVariants.map { it.processor }.toSet()
                    "ram" -> matchingVariants.map { it.ramGb }.toSet()
                    "storage" -> matchingVariants.map { it.storageGb }.toSet()
                    "color" -> matchingVariants.map { it.color }.toSet()
                    else -> emptySet()
                }
                
                if (uniqueValues.isNotEmpty()) {
                    current = current.copyField(field, uniqueValues.first()!!)
                    changed = true
                }
            }
        }
    }
    return current
}

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val selection: ProductSelection = ProductSelection(),
    val optionsState: ProductOptionsState? = null,
    val cartAdded: Boolean = false,
    val isFavorite: Boolean = false
)

val EmptyOptions = ProductOptionsState(
    conditions = emptyList<SelectableOption<Condition>>(),
    processors = emptyList(),
    rams = emptyList(),
    storages = emptyList(),
    colors = emptyList(),
    selectedVariant = null
)
