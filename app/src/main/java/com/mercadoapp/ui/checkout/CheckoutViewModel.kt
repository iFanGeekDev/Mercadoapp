package com.mercadoapp.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.Address
import com.mercadoapp.domain.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val selectedAddress: Address? = null,
    val isInitialLoading: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            addressRepository.getAddresses().collect { addresses ->
                val defaultOrFirst = addresses.find { it.isDefault } ?: addresses.firstOrNull()
                _state.update { it.copy(selectedAddress = defaultOrFirst, isInitialLoading = false) }
            }
        }
    }

    fun confirmOrder() {
        if (_state.value.selectedAddress == null) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1500)
            _state.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
}
