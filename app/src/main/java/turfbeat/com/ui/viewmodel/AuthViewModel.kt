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
    val error: String? = null
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
                    error = result.error
                )
            }
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
}
