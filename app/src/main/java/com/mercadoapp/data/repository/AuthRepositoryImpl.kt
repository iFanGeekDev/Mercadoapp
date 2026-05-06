package com.mercadoapp.data.repository

import com.mercadoapp.data.local.datastore.AuthDataStore
import com.mercadoapp.data.remote.api.MercadoApiService
import com.mercadoapp.data.remote.dto.LoginRequestDto
import com.mercadoapp.data.remote.dto.RegisterRequestDto
import com.mercadoapp.data.remote.mapper.toDomain
import com.mercadoapp.domain.model.AuthState
import com.mercadoapp.domain.model.User
import com.mercadoapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: MercadoApiService,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override val authState: Flow<AuthState> = authDataStore.accessToken.map { token ->
        if (token.isNullOrBlank()) AuthState.Unauthenticated
        else {
            try {
                val user = api.getMe().toDomain()
                AuthState.Authenticated(user)
            } catch (e: Exception) {
                AuthState.Unauthenticated
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val tokenDto = api.login(LoginRequestDto(email, password))
            authDataStore.saveTokens(tokenDto.accessToken, tokenDto.refreshToken)
            val user = api.getMe().toDomain()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val tokenDto = api.register(RegisterRequestDto(name, email, password))
            authDataStore.saveTokens(tokenDto.accessToken, tokenDto.refreshToken)
            val user = api.getMe().toDomain()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        authDataStore.clearTokens()
    }
}
