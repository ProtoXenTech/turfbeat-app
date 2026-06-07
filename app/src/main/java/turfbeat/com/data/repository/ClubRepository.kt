package turfbeat.com.data.repository

import turfbeat.com.data.model.*
import turfbeat.com.data.remote.ApiService

class ClubRepository(
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

    suspend fun getClubs(
        area: String? = null,
        district: String? = null,
        search: String? = null,
        recruiting: Boolean? = null,
        sort: String? = null
    ): ApiResponse<List<ClubDto>> {
        return safeApiCall { apiService.getClubs(area, district, search, recruiting, sort) }
    }

    suspend fun getClubDetail(id: Int): ApiResponse<ClubDto> {
        return safeApiCall { apiService.getClubDetail(id) }
    }

    suspend fun getClubMembers(id: Int): ApiResponse<List<ClubMemberDto>> {
        return safeApiCall { apiService.getClubMembers(id) }
    }

    suspend fun getMyClubDashboard(): ApiResponse<ClubDashboardDto> {
        return safeApiCall { apiService.getMyClubDashboard() }
    }

    suspend fun createClub(data: Map<String, Any>): ApiResponse<ClubDto> {
        return safeApiCall { apiService.createClub(data) }
    }

    suspend fun updateMyClub(data: Map<String, Any>): ApiResponse<ClubDto> {
        return safeApiCall { apiService.updateMyClub(data) }
    }

    suspend fun getMyClubs(): ApiResponse<List<ClubDto>> {
        return safeApiCall { apiService.getMyClubs() }
    }

    suspend fun joinClub(clubId: Int): ApiResponse<ClubJoinRequestDto> {
        return safeApiCall { apiService.joinClub(clubId) }
    }

    suspend fun leaveClub(clubId: Int): ApiResponse<Unit> {
        return safeApiCall { apiService.leaveClub(clubId) }
    }

    suspend fun updateMember(memberId: Int, data: Map<String, Any>): ApiResponse<ClubMemberDto> {
        return safeApiCall { apiService.updateMember(memberId, data) }
    }

    suspend fun removeMember(memberId: Int): ApiResponse<Unit> {
        return safeApiCall { apiService.removeMember(memberId) }
    }

    suspend fun handleJoinRequest(requestId: Int, status: String): ApiResponse<ClubJoinRequestDto> {
        return safeApiCall { apiService.handleJoinRequest(requestId, mapOf("status" to status)) }
    }

    suspend fun submitApprovalRequest(): ApiResponse<Unit> {
        return safeApiCall { apiService.submitApprovalRequest() }
    }

    suspend fun createClubMatch(data: Map<String, Any>): ApiResponse<MatchDto> {
        return safeApiCall { apiService.createClubMatch(data) }
    }

    suspend fun deleteClub(id: Int): ApiResponse<Unit> {
        return safeApiCall { apiService.deleteClub(id) }
    }

    suspend fun approveClub(id: Int): ApiResponse<Unit> {
        return safeApiCall { apiService.approveClub(id) }
    }
}
