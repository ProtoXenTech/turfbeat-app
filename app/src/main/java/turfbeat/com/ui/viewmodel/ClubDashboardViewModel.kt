package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.*
import turfbeat.com.data.repository.ClubRepository

data class ClubDashboardState(
    val dashboard: ClubDashboardDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ClubDashboardViewModel(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClubDashboardState())
    val state: StateFlow<ClubDashboardState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = clubRepository.getMyClubDashboard()
            _state.update {
                it.copy(dashboard = result.data, isLoading = false, error = result.error)
            }
        }
    }

    fun updateMember(memberId: Int, role: String? = null, status: String? = null) {
        viewModelScope.launch {
            val data = mutableMapOf<String, Any>()
            if (role != null) data["role"] = role
            if (status != null) data["status"] = status
            if (data.isNotEmpty()) {
                val result = clubRepository.updateMember(memberId, data)
                if (result.success) load()
            }
        }
    }

    fun removeMember(memberId: Int) {
        viewModelScope.launch {
            val result = clubRepository.removeMember(memberId)
            if (result.success) load()
            else _state.update { it.copy(error = result.error) }
        }
    }

    fun handleJoinRequest(requestId: Int, accept: Boolean) {
        viewModelScope.launch {
            val result = clubRepository.handleJoinRequest(requestId, if (accept) "accepted" else "rejected")
            if (result.success) load()
            else _state.update { it.copy(error = result.error) }
        }
    }

    fun submitApproval() {
        viewModelScope.launch {
            val result = clubRepository.submitApprovalRequest()
            if (result.success) load()
            else _state.update { it.copy(error = result.error) }
        }
    }
}
