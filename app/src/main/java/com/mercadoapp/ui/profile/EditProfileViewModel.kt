package com.mercadoapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadoapp.domain.model.User
import com.mercadoapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.getProfile().onSuccess { user ->
                _state.update { it.copy(isLoading = false, user = user) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            userRepository.updateProfile(name, email).onSuccess { updatedUser ->
                _state.update { it.copy(isLoading = false, user = updatedUser, success = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.uploadAvatar(file).onSuccess { updatedUser ->
                _state.update { it.copy(isLoading = false, user = updatedUser) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    fun consumeSuccess() {
        _state.update { it.copy(success = false) }
    }
}
