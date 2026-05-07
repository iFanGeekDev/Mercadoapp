package com.mercadoapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val favorites = repository.getFavorites()
                _state.update { it.copy(products = favorites, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "No se pudieron cargar los favoritos") }
            }
        }
    }

    fun removeFavorite(productId: String) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(productId, false)
                // Optimistic update
                _state.update { currentState ->
                    currentState.copy(
                        products = currentState.products.filter { it.id != productId }
                    )
                }
            } catch (e: Exception) {
                // If failed, reload to be safe
                loadFavorites()
            }
        }
    }
}
