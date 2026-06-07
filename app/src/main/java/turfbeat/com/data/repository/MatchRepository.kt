package turfbeat.com.data.repository

import turfbeat.com.data.model.*
import turfbeat.com.data.remote.ApiService

class MatchRepository(
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

    suspend fun getMatches(status: String? = null): ApiResponse<List<MatchDto>> {
        return safeApiCall { apiService.getMatches(status = status) }
    }

    suspend fun getOpenMatches(area: String? = null, format: String? = null): ApiResponse<List<MatchDto>> {
        return safeApiCall { apiService.getOpenMatches(area, format) }
    }

    suspend fun getMatchDetail(id: Int): ApiResponse<MatchDto> {
        return safeApiCall { apiService.getMatchDetail(id) }
    }

    suspend fun getMyMatches(): ApiResponse<List<MatchDto>> {
        return safeApiCall { apiService.getMyMatches() }
    }

    suspend fun getMatchChat(matchId: Int): ApiResponse<List<ChatMessageDto>> {
        return safeApiCall { apiService.getMatchChat(matchId) }
    }

    suspend fun sendChatMessage(matchId: Int, message: String): ApiResponse<ChatMessageDto> {
        return safeApiCall { apiService.sendChatMessage(matchId, SendMessageRequest(message)) }
    }
}
