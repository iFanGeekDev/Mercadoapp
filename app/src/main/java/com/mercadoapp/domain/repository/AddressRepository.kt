package com.mercadoapp.domain.repository

import com.mercadoapp.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getAddresses(): Flow<List<Address>>
    suspend fun getAddressById(id: String): Address?
    suspend fun saveAddress(address: Address)
    suspend fun deleteAddress(id: String)
    suspend fun setDefaultAddress(id: String)
}
