package com.mercadoapp.ui.address

import androidx.lifecycle.SavedStateHandle
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

data class AddressEditUiState(
    val id: String = "",
    val alias: String = "",
    val street: String = "",
    val distrito: String = "",
    val provincia: String = "",
    val departamento: String = "",
    val isDefault: Boolean = false,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class AddressEditViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddressEditUiState())
    val state = _state.asStateFlow()

    private val addressId: String? = savedStateHandle.get<String>("id")

    init {
        if (addressId != null && addressId != "new") {
            loadAddress(addressId)
        }
    }

    private fun loadAddress(id: String) {
        viewModelScope.launch {
            val address = addressRepository.getAddressById(id)
            if (address != null) {
                _state.update {
                    it.copy(
                        id = address.id,
                        alias = address.alias,
                        street = address.street,
                        distrito = address.distrito,
                        provincia = address.provincia,
                        departamento = address.departamento,
                        isDefault = address.isDefault
                    )
                }
            }
        }
    }

    fun onAliasChanged(value: String) { _state.update { it.copy(alias = value) } }
    fun onStreetChanged(value: String) { _state.update { it.copy(street = value) } }
    fun onDistritoChanged(value: String) { _state.update { it.copy(distrito = value) } }
    fun onProvinciaChanged(value: String) { _state.update { it.copy(provincia = value) } }
    fun onDepartamentoChanged(value: String) { _state.update { it.copy(departamento = value) } }
    fun onIsDefaultChanged(value: Boolean) { _state.update { it.copy(isDefault = value) } }

    fun saveAddress() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentState = _state.value
            val address = Address(
                id = currentState.id,
                alias = currentState.alias.ifEmpty { "Casa" },
                street = currentState.street,
                distrito = currentState.distrito,
                provincia = currentState.provincia,
                departamento = currentState.departamento,
                isDefault = currentState.isDefault
            )
            addressRepository.saveAddress(address)
            _state.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
}
