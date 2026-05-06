package com.mercadoapp.domain.repository

import com.mercadoapp.domain.model.User
import java.io.File

interface UserRepository {
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(name: String, email: String): Result<User>
    suspend fun changePassword(old: String, new: String): Result<Unit>
    suspend fun uploadAvatar(file: File): Result<User>
}
