package com.mercadoapp.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null
)

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}
