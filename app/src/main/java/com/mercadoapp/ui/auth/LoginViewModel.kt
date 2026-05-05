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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    /** Compatibilidad con código anterior que usa state.error */
    val error: String? get() = generalError
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChanged(value: String) {
        val error = when {
            value.isBlank()               -> null // no mostrar error mientras vacío
            !EMAIL_REGEX.matches(value)   -> "El correo no es válido"
            else                          -> null
        }
        _state.value = _state.value.copy(email = value, emailError = error, generalError = null)
    }

    fun onPasswordChanged(value: String) {
        val error = when {
            value.isBlank()    -> null
            value.length < 6   -> "Mínimo 6 caracteres"
            else               -> null
        }
        _state.value = _state.value.copy(password = value, passwordError = error, generalError = null)
    }

    fun login() {
        val s = _state.value

        // Validar todos los campos antes de enviar
        val emailErr = when {
            s.email.isBlank()              -> "Este campo es obligatorio"
            !EMAIL_REGEX.matches(s.email)  -> "El correo no es válido"
            else                           -> null
        }
        val passErr = when {
            s.password.isBlank()  -> "Este campo es obligatorio"
            s.password.length < 6 -> "Mínimo 6 caracteres"
            else                  -> null
        }

        if (emailErr != null || passErr != null) {
            _state.value = s.copy(emailError = emailErr, passwordError = passErr)
            return
        }

        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, generalError = null)
            val result = authRepository.login(s.email, s.password)
            _state.value = if (result.isSuccess) {
                s.copy(isLoading = false, isSuccess = true)
            } else {
                s.copy(
                    isLoading = false,
                    generalError = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                )
            }
        }
    }
}
