package com.mercadoapp.data.repository

import com.mercadoapp.domain.model.Address
import com.mercadoapp.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAddressRepository @Inject constructor() : AddressRepository {
    private val addresses = mutableListOf<Address>()
    private val _addressesFlow = MutableStateFlow<List<Address>>(emptyList())

    override fun getAddresses(): Flow<List<Address>> = _addressesFlow.asStateFlow()

    override suspend fun getAddressById(id: String): Address? {
        return addresses.find { it.id == id }
    }

    override suspend fun saveAddress(address: Address) {
        val finalAddress = if (address.id.isEmpty()) address.copy(id = UUID.randomUUID().toString()) else address
        
        val existing = addresses.indexOfFirst { it.id == finalAddress.id }
        if (existing >= 0) {
            addresses[existing] = finalAddress
        } else {
            addresses.add(finalAddress)
        }
        
        if (finalAddress.isDefault || addresses.size == 1) {
            setDefaultAddress(finalAddress.id)
        } else {
            _addressesFlow.update { addresses.toList() }
        }
    }

    override suspend fun deleteAddress(id: String) {
        addresses.removeAll { it.id == id }
        _addressesFlow.update { addresses.toList() }
    }

    override suspend fun setDefaultAddress(id: String) {
        for (i in addresses.indices) {
            addresses[i] = addresses[i].copy(isDefault = addresses[i].id == id)
        }
        _addressesFlow.update { addresses.toList() }
    }
}
