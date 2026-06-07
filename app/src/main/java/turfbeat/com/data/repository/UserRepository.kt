package turfbeat.com.data.repository

import turfbeat.com.data.model.*
import turfbeat.com.data.remote.ApiService

class UserRepository(
    private val apiService: ApiService
) {
    private suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<ApiResponse<T>>): ApiResponse<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body() ?: ApiResponse(success = false, error = "Empty response")
            } else {
                ApiResponse(success = false, error = "HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResponse(success = false, error = e.message ?: "Network error")
        }
    }

    suspend fun getAvailablePlayers(
        area: String? = null,
        position: String? = null,
        skillLevel: String? = null,
        search: String? = null
    ): ApiResponse<List<UserDto>> {
        return safeApiCall { apiService.getAvailablePlayers(area, position, skillLevel, search) }
    }

    suspend fun getPlayerDetail(id: Int): ApiResponse<UserDto> {
        return safeApiCall { apiService.getPlayerDetail(id) }
    }

    suspend fun getMyProfile(): ApiResponse<UserDto> {
        return safeApiCall { apiService.getMyProfile() }
    }

    suspend fun updateProfile(data: Map<String, Any>): ApiResponse<UserDto> {
        return safeApiCall { apiService.updateProfile(data) }
    }
}
