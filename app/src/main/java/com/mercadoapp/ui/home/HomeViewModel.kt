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

    /** Paged product stream for the full catalog */
    @OptIn(ExperimentalCoroutinesApi::class)
    val productsPaged: Flow<PagingData<Product>> = _selectedCategory.flatMapLatest { category ->
        repository.getProductsPaged(if (category == "ALL") null else category)
    }.cachedIn(viewModelScope)

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}
