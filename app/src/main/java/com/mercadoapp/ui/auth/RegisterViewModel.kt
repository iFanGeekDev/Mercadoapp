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
    // Errores por campo
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    /** Compatibilidad con código anterior */
    val error: String? get() = generalError
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChanged(v: String) {
        val error = if (v.isNotBlank() && v.length < 2) "El nombre es muy corto" else null
        update { copy(name = v, nameError = error, generalError = null) }
    }

    fun onEmailChanged(v: String) {
        val error = when {
            v.isBlank()               -> null
            !EMAIL_REGEX.matches(v)   -> "El correo no es válido"
            else                      -> null
        }
        update { copy(email = v, emailError = error, generalError = null) }
    }

    fun onPasswordChanged(v: String) {
        val passErr = when {
            v.isBlank()  -> null
            v.length < 6 -> "Mínimo 6 caracteres"
            else         -> null
        }
        val s = _state.value
        val confirmErr = when {
            s.confirmPassword.isBlank() -> null
            v != s.confirmPassword      -> "Las contraseñas no coinciden"
            else                        -> null
        }
        update { copy(password = v, passwordError = passErr, confirmPasswordError = confirmErr, generalError = null) }
    }

    fun onConfirmPasswordChanged(v: String) {
        val error = when {
            v.isBlank()                        -> null
            v != _state.value.password         -> "Las contraseñas no coinciden"
            else                               -> null
        }
        update { copy(confirmPassword = v, confirmPasswordError = error, generalError = null) }
    }

    fun register() {
        val s = _state.value

        val nameErr = when {
            s.name.isBlank()  -> "Este campo es obligatorio"
            s.name.length < 2 -> "El nombre es muy corto"
            else              -> null
        }
        val emailErr = when {
            s.email.isBlank()             -> "Este campo es obligatorio"
            !EMAIL_REGEX.matches(s.email) -> "El correo no es válido"
            else                          -> null
        }
        val passErr = when {
            s.password.isBlank()  -> "Este campo es obligatorio"
            s.password.length < 6 -> "Mínimo 6 caracteres"
            else                  -> null
        }
        val confirmErr = when {
            s.confirmPassword.isBlank()     -> "Este campo es obligatorio"
            s.password != s.confirmPassword -> "Las contraseñas no coinciden"
            else                            -> null
        }

        if (nameErr != null || emailErr != null || passErr != null || confirmErr != null) {
            update { copy(nameError = nameErr, emailError = emailErr, passwordError = passErr, confirmPasswordError = confirmErr) }
            return
        }

        viewModelScope.launch {
            update { copy(isLoading = true, generalError = null) }
            val result = authRepository.register(s.name, s.email, s.password)
            _state.value = if (result.isSuccess) {
                s.copy(isLoading = false, isSuccess = true)
            } else {
                s.copy(isLoading = false, generalError = result.exceptionOrNull()?.message ?: "Error al registrarse")
            }
        }
    }

    private fun update(t: RegisterUiState.() -> RegisterUiState) { _state.value = _state.value.t() }
}
