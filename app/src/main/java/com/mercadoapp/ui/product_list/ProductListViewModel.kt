package com.mercadoapp.ui.product_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery: String = savedStateHandle["search"] ?: ""
    val category: String? = savedStateHandle["category"]?.let { if (it == "ALL") null else it }

    val productsPaged: Flow<PagingData<Product>> = repository.getProductsPaged(
        category = category,
        search = searchQuery.ifBlank { null }
    ).cachedIn(viewModelScope)
}
