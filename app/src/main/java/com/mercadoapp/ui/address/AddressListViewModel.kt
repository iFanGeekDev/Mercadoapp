package com.mercadoapp.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.Address
import com.mercadoapp.domain.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddressListUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddressListUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            addressRepository.getAddresses().collect { addresses ->
                _state.update { it.copy(addresses = addresses, isLoading = false) }
            }
        }
    }

    fun setDefault(id: String) {
        viewModelScope.launch {
            addressRepository.setDefaultAddress(id)
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            addressRepository.deleteAddress(id)
        }
    }
}
