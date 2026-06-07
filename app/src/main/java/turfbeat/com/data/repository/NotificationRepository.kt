package turfbeat.com.data.repository

import turfbeat.com.data.model.*
import turfbeat.com.data.remote.ApiService

class NotificationRepository(
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

    suspend fun getNotifications(): ApiResponse<List<NotificationDto>> {
        return safeApiCall { apiService.getNotifications() }
    }

    suspend fun markRead(id: Int): ApiResponse<Unit> {
        return safeApiCall { apiService.markNotificationRead(id) }
    }

    suspend fun markAllRead(): ApiResponse<Unit> {
        return safeApiCall { apiService.markAllNotificationsRead() }
    }
}
