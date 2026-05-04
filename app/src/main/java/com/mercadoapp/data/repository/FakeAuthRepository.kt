package com.mercadoapp.data.repository

import com.mercadoapp.domain.model.AuthState
import com.mercadoapp.domain.model.User
import com.mercadoapp.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake AuthRepository for development/demo purposes.
 * Accepts: email = "demo@mercadoapp.dev" / password = "demo1234"
 * Does NOT call any network endpoint.
 */
@Singleton
class FakeAuthRepository @Inject constructor() : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    override suspend fun login(email: String, password: String): Result<User> {
        delay(800) // simulate network
        return if (email == DEMO_EMAIL && password == DEMO_PASSWORD) {
            val user = demoUser()
            _authState.value = AuthState.Authenticated(user)
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciales inválidas. Usa $DEMO_EMAIL / $DEMO_PASSWORD"))
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        delay(800)
        val user = User(id = "new-user", email = email, name = name)
        _authState.value = AuthState.Authenticated(user)
        return Result.success(user)
    }

    override suspend fun logout() {
        _authState.value = AuthState.Unauthenticated
    }

    private fun demoUser() = User(
        id = "demo-001",
        email = DEMO_EMAIL,
        name = "Diego Demo",
        avatarUrl = null
    )

    companion object {
        const val DEMO_EMAIL    = "demo@mercadoapp.dev"
        const val DEMO_PASSWORD = "demo1234"
    }
}
