package com.mercadoapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.AuthState
import com.mercadoapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChanged(value: String) { _state.value = _state.value.copy(email = value, error = null) }
    fun onPasswordChanged(value: String) { _state.value = _state.value.copy(password = value, error = null) }

    fun login() {
        val current = _state.value
        if (current.email.isBlank() || current.password.isBlank()) {
            _state.value = current.copy(error = "Email y contraseña requeridos")
            return
        }
        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)
            val result = authRepository.login(current.email, current.password)
            _state.value = if (result.isSuccess) {
                current.copy(isLoading = false, isSuccess = true)
            } else {
                current.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                )
            }
        }
    }
}
