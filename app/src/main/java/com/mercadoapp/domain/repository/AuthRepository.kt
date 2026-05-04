package com.mercadoapp.domain.repository

import com.mercadoapp.domain.model.AuthState
import com.mercadoapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun logout()
}
