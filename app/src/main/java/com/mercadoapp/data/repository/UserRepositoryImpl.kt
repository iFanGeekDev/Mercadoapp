package com.mercadoapp.data.repository

import com.mercadoapp.data.remote.api.MercadoApiService
import com.mercadoapp.data.remote.dto.ChangePasswordRequestDto
import com.mercadoapp.data.remote.dto.UpdateProfileRequestDto
import com.mercadoapp.domain.model.User
import com.mercadoapp.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: MercadoApiService
) : UserRepository {

    override suspend fun getProfile(): Result<User> = runCatching {
        val dto = apiService.getMe() 
        User(dto.id, dto.name, dto.email, dto.avatarUrl)
    }

    override suspend fun updateProfile(name: String, email: String): Result<User> = runCatching {
        val dto = apiService.updateProfile(UpdateProfileRequestDto(name, email))
        User(dto.id, dto.name, dto.email, dto.avatarUrl)
    }

    override suspend fun changePassword(old: String, new: String): Result<Unit> = runCatching {
        apiService.changePassword(ChangePasswordRequestDto(old, new))
        Unit
    }

    override suspend fun uploadAvatar(file: File): Result<User> = runCatching {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
        val dto = apiService.uploadAvatar(body)
        User(dto.id, dto.name, dto.email, dto.avatarUrl)
    }
}
