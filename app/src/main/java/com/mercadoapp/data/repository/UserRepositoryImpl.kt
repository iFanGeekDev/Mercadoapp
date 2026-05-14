package com.mercadoapp.data.repository

import com.mercadoapp.data.remote.api.MercadoApiService
import com.mercadoapp.data.remote.dto.ErrorResponseDto
import com.mercadoapp.data.remote.dto.ChangePasswordRequestDto
import com.mercadoapp.data.remote.dto.UpdateProfileRequestDto
import com.mercadoapp.domain.repository.AuthRepository
import com.mercadoapp.domain.repository.UserRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: MercadoApiService,
    private val authRepository: AuthRepository
) : UserRepository {

    override suspend fun getProfile(): Result<User> = runCatching {
        val dto = apiService.getMe() 
        val user = User(dto.id, dto.email, dto.name, dto.avatarUrl)
        authRepository.updateUser(user)
        user
    }

    override suspend fun updateProfile(name: String, email: String): Result<User> {
        return try {
            val dto = apiService.updateProfile(UpdateProfileRequestDto(name, email))
            val user = User(dto.id, dto.email, dto.name, dto.avatarUrl)
            authRepository.updateUser(user)
            Result.success(user)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(old: String, new: String): Result<Unit> {
        return try {
            apiService.changePassword(ChangePasswordRequestDto(old, new))
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception(parseError(e)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val json = Json { ignoreUnknownKeys = true }
            val errorDto = json.decodeFromString<ErrorResponseDto>(errorBody ?: "")
            errorDto.error
        } catch (ex: Exception) {
            "Error inesperado (${e.code()})"
        }
    }

    override suspend fun uploadAvatar(file: File): Result<User> = runCatching {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
        val dto = apiService.uploadAvatar(body)
        val user = User(dto.id, dto.email, dto.name, dto.avatarUrl)
        authRepository.updateUser(user)
        user
    }
}
