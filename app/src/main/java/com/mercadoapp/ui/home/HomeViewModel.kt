package com.mercadoapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** Paged product stream for the full catalog */
    @OptIn(ExperimentalCoroutinesApi::class)
    val productsPaged: Flow<PagingData<Product>> = combine(
        _selectedCategory,
        _searchQuery.debounce(500)
    ) { category, query ->
        category to query
    }.flatMapLatest { (category, query) ->
        repository.getProductsPaged(
            category = if (category == "ALL") null else category,
            search = if (query.isBlank()) null else query
        )
    }.cachedIn(viewModelScope)

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
