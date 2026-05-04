package com.mercadoapp.data.remote.interceptor

import com.mercadoapp.data.local.datastore.AuthDataStore
import com.mercadoapp.data.remote.dto.LoginRequestDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject

/**
 * Intercepts 401 responses, attempts a token refresh using the stored refreshToken,
 * saves the new tokens, and retries the original request once.
 */
class TokenRefreshInterceptor @Inject constructor(
    private val authDataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code != 401) return response

        // Try to refresh
        val refreshToken = runBlocking { authDataStore.refreshToken.firstOrNull() }
            ?: return response   // no token stored → return 401 as-is

        response.close()

        val refreshed = runBlocking {
            try {
                val body = JSONObject().apply { put("refresh_token", refreshToken) }
                    .toString()
                    .toRequestBody("application/json".toMediaType())

                val refreshRequest = Request.Builder()
                    .url(chain.request().url.newBuilder()
                        .encodedPath("/v1/auth/refresh")
                        .build())
                    .post(body)
                    .build()

                val refreshResponse = chain.proceed(refreshRequest)
                if (refreshResponse.isSuccessful) {
                    val json = JSONObject(refreshResponse.body!!.string())
                    val newAccess  = json.getString("access_token")
                    val newRefresh = json.getString("refresh_token")
                    authDataStore.saveTokens(newAccess, newRefresh)
                    newAccess
                } else {
                    authDataStore.clearTokens()
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        return if (refreshed != null) {
            // Retry original request with the new token
            val retryRequest = chain.request().newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $refreshed")
                .build()
            chain.proceed(retryRequest)
        } else {
            // Refresh failed → propagate the 401
            chain.proceed(chain.request())
        }
    }
}
