package com.mercadoapp.ui.product_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.repository.CartRepository
import com.mercadoapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    val favoriteIds: StateFlow<Set<String>> = repository.observeFavorites()
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val cartCount: StateFlow<Int> = cartRepository.cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val searchQuery: String = savedStateHandle.get<String>("search") ?: ""
    val category: String? = savedStateHandle.get<String>("category")?.let { if (it == "ALL") null else it }

    val productsPaged: Flow<PagingData<Product>> = repository.getProductsPaged(
        category = category,
        search = searchQuery.ifBlank { null }
    ).cachedIn(viewModelScope)

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            val isCurrentlyFavorite = favoriteIds.value.contains(product.id)
            repository.toggleFavorite(product.id, !isCurrentlyFavorite)
        }
    }
}
