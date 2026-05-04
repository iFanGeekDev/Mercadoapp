package com.mercadoapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.AuthState
import com.mercadoapp.domain.model.User
import com.mercadoapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val state: StateFlow<ProfileUiState> = authRepository.authState
        .map { authState ->
            when (authState) {
                is AuthState.Authenticated -> ProfileUiState(user = authState.user, isLoading = false)
                is AuthState.Loading       -> ProfileUiState(isLoading = true)
                is AuthState.Unauthenticated -> ProfileUiState(isLoading = false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun logout() = viewModelScope.launch { authRepository.logout() }
}
