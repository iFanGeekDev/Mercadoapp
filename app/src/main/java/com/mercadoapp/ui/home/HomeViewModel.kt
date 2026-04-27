package com.mercadoapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadHome()
    }

    private fun loadHome() = viewModelScope.launch {
        _state.value = HomeUiState(isLoading = true)
        val products = repository.getHomeFeed()
        _state.value = HomeUiState(
            isLoading = false,
            products = products,
            offers = products.filter { it.isOffer },
            newArrivals = products.filter { it.isNewArrival }
        )
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val offers: List<Product> = emptyList(),
    val newArrivals: List<Product> = emptyList()
)
