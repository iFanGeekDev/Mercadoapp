package com.mercadoapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChanged(v: String)            = update { copy(name = v, error = null) }
    fun onEmailChanged(v: String)           = update { copy(email = v, error = null) }
    fun onPasswordChanged(v: String)        = update { copy(password = v, error = null) }
    fun onConfirmPasswordChanged(v: String) = update { copy(confirmPassword = v, error = null) }

    fun register() {
        val s = _state.value
        when {
            s.name.isBlank()     -> update { copy(error = "El nombre es requerido") }
            s.email.isBlank()    -> update { copy(error = "El email es requerido") }
            s.password.length < 6 -> update { copy(error = "La contraseña debe tener al menos 6 caracteres") }
            s.password != s.confirmPassword -> update { copy(error = "Las contraseñas no coinciden") }
            else -> viewModelScope.launch {
                update { copy(isLoading = true, error = null) }
                val result = authRepository.register(s.name, s.email, s.password)
                _state.value = if (result.isSuccess) {
                    s.copy(isLoading = false, isSuccess = true)
                } else {
                    s.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Error al registrarse")
                }
            }
        }
    }

    private fun update(t: RegisterUiState.() -> RegisterUiState) { _state.value = _state.value.t() }
}
