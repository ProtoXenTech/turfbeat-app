package turfbeat.com.data.repository

import turfbeat.com.data.model.*
import turfbeat.com.data.remote.ApiService
import turfbeat.com.data.remote.TokenManager

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
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

    suspend fun login(email: String, password: String): ApiResponse<TokenResponse> {
        val result = safeApiCall { apiService.loginPassword(LoginRequest(email, password)) }
        if (result.success && result.data != null) {
            tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
            result.data.user?.let { user ->
                tokenManager.saveUserInfo(user.id, user.role ?: "player")
            }
        }
        return result
    }

    suspend fun sendOtp(email: String): ApiResponse<Unit> {
        return safeApiCall { apiService.sendOtp(SendOtpRequest(email)) }
    }

    suspend fun verifyOtp(email: String, otp: String): ApiResponse<TokenResponse> {
        val result = safeApiCall { apiService.verifyOtp(VerifyOtpRequest(email, otp)) }
        if (result.success && result.data != null) {
            tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
            result.data.user?.let { user ->
                tokenManager.saveUserInfo(user.id, user.role ?: "player")
            }
        }
        return result
    }

    suspend fun checkPhone(phone: String): ApiResponse<Unit> {
        return safeApiCall { apiService.checkPhone(CheckPhoneRequest(phone)) }
    }

    suspend fun checkEmail(email: String): ApiResponse<Unit> {
        return safeApiCall { apiService.checkEmail(CheckEmailRequest(email)) }
    }

    suspend fun forgotPassword(email: String): ApiResponse<Unit> {
        return safeApiCall { apiService.forgotPassword(ForgotPasswordRequest(email)) }
    }

    suspend fun resetPassword(email: String, otp: String, password: String): ApiResponse<Unit> {
        return safeApiCall { apiService.resetPassword(ResetPasswordRequest(email, otp, password)) }
    }

    suspend fun refreshToken(): ApiResponse<TokenResponse> {
        val refreshToken = tokenManager.getRefreshToken() ?: return ApiResponse(success = false, error = "No refresh token")
        val result = safeApiCall { apiService.refreshToken(RefreshTokenRequest(refreshToken)) }
        if (result.success && result.data != null) {
            tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
        }
        return result
    }

    suspend fun getProfile(): ApiResponse<UserDto> {
        return safeApiCall { apiService.me() }
    }

    suspend fun logout() {
        safeApiCall { apiService.logout() }
        tokenManager.clearSession()
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }
}
