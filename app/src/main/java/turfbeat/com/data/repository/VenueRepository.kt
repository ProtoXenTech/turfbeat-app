package turfbeat.com.data.repository

import turfbeat.com.data.model.*
import turfbeat.com.data.remote.ApiService

class VenueRepository(
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

    suspend fun getVenues(area: String? = null, search: String? = null): ApiResponse<List<VenueDto>> {
        return safeApiCall { apiService.getVenues(area, search = search) }
    }

    suspend fun getVenueDetail(id: Int): ApiResponse<VenueDto> {
        return safeApiCall { apiService.getVenueDetail(id) }
    }
}
