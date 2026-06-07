package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.UserDto
import turfbeat.com.data.remote.TokenManager
import turfbeat.com.data.repository.AuthRepository

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean? = null,
    val user: UserDto? = null,
    val userName: String? = null,
    val error: String? = null,
    val otpSent: Boolean = false,
    val registrationComplete: Boolean = false,
    val phoneAvailable: Boolean? = null,
    val emailAvailable: Boolean? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            tokenManager.isLoggedIn.collect { loggedIn ->
                _uiState.update { it.copy(isLoggedIn = loggedIn) }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(email, password)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = result.success,
                    user = result.data?.user,
                    userName = result.data?.user?.fullName,
                    error = result.error
                )
            }
            if (result.success) loadProfile()
        }
    }

    fun register(email: String, password: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.verifyOtp(email, otp)
            if (result.success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        registrationComplete = true,
                        user = result.data?.user,
                        userName = result.data?.user?.fullName,
                        error = null
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.error) }
            }
        }
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.sendOtp(email)
            _uiState.update {
                it.copy(isLoading = false, otpSent = result.success, error = result.error)
            }
        }
    }

    suspend fun checkPhone(phone: String): Boolean {
        val result = authRepository.checkPhone(phone)
        _uiState.update { it.copy(phoneAvailable = result.success, error = if (result.success) null else result.error) }
        return result.success
    }

    fun sendForgotOtp(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.forgotPassword(email)
            _uiState.update {
                it.copy(isLoading = false, otpSent = result.success, error = result.error)
            }
        }
    }

    fun resetPassword(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.resetPassword(email, otp, newPassword)
            _uiState.update {
                it.copy(isLoading = false, error = result.error)
            }
        }
    }

    private suspend fun loadProfile() {
        val result = authRepository.getProfile()
        if (result.success) {
            _uiState.update { it.copy(user = result.data, userName = result.data?.fullName) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { AuthUiState(isLoggedIn = false) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetRegistrationState() {
        _uiState.update { it.copy(otpSent = false, registrationComplete = false, phoneAvailable = null, emailAvailable = null) }
    }
}
