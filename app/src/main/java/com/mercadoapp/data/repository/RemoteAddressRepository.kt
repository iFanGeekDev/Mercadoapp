package com.mercadoapp.data.repository

import com.mercadoapp.data.remote.api.MercadoApiService
import com.mercadoapp.data.remote.mapper.toDomain
import com.mercadoapp.data.remote.mapper.toDto
import com.mercadoapp.domain.model.Address
import com.mercadoapp.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAddressRepository @Inject constructor(
    private val api: MercadoApiService
) : AddressRepository {

    private val _addressesFlow = MutableStateFlow<List<Address>>(emptyList())
    
    // Initial fetch trigger
    private var hasFetched = false

    override fun getAddresses(): Flow<List<Address>> {
        refreshAddresses()
        return _addressesFlow.asStateFlow()
    }

    private fun refreshAddresses() {
        kotlinx.coroutines.GlobalScope.launch {
            try {
                val remote = api.getAddresses()
                _addressesFlow.update { remote.map { it.toDomain() } }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getAddressById(id: String): Address? {
        // We can fetch all or just one. For simplicity with the existing UI:
        return try {
            val remote = api.getAddresses()
            val list = remote.map { it.toDomain() }
            _addressesFlow.update { list }
            list.find { it.id == id }
        } catch (e: Exception) {
            _addressesFlow.value.find { it.id == id }
        }
    }

    override suspend fun saveAddress(address: Address) {
        try {
            val dto = address.toDto()
            if (address.id.isEmpty()) {
                api.saveAddress(dto)
            } else {
                api.updateAddress(address.id, dto)
            }
            // Refresh list
            val remote = api.getAddresses()
            _addressesFlow.update { remote.map { it.toDomain() } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteAddress(id: String) {
        try {
            api.deleteAddress(id)
            val remote = api.getAddresses()
            _addressesFlow.update { remote.map { it.toDomain() } }
        } catch (e: Exception) {}
    }

    override suspend fun setDefaultAddress(id: String) {
        // The backend handles default address logic when saving/updating
        // If we want a separate call, we'd need another endpoint
        // For now, we update via the save logic if needed, or just refresh
        try {
            val list = api.getAddresses()
            val address = list.find { it.id == id }?.toDomain()
            if (address != null) {
                saveAddress(address.copy(isDefault = true))
            }
        } catch (e: Exception) {}
    }
    
    /** Trigger a manual refresh from ViewModels */
    suspend fun fetchAll() {
        try {
            val remote = api.getAddresses()
            _addressesFlow.update { remote.map { it.toDomain() } }
        } catch (e: Exception) {}
    }
}
