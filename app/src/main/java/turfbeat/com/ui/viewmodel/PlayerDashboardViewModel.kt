package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.ClubDto
import turfbeat.com.data.model.MatchDto
import turfbeat.com.data.model.UserDto
import turfbeat.com.data.repository.ClubRepository
import turfbeat.com.data.repository.MatchRepository
import turfbeat.com.data.repository.UserRepository

data class PlayerDashboardState(
    val profile: UserDto? = null,
    val myClubs: List<ClubDto> = emptyList(),
    val myMatches: List<MatchDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PlayerDashboardViewModel(
    private val userRepository: UserRepository,
    private val clubRepository: ClubRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerDashboardState())
    val state: StateFlow<PlayerDashboardState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val profile = userRepository.getMyProfile()
            val clubs = clubRepository.getMyClubs()
            val matches = matchRepository.getMyMatches()

            _state.update {
                it.copy(
                    profile = profile.data,
                    myClubs = clubs.data ?: emptyList(),
                    myMatches = matches.data ?: emptyList(),
                    isLoading = false,
                    error = profile.error ?: clubs.error ?: matches.error
                )
            }
        }
    }

    fun leaveClub(clubId: Int) {
        viewModelScope.launch {
            val result = clubRepository.leaveClub(clubId)
            if (result.success) {
                _state.update { it.copy(myClubs = it.myClubs.filter { c -> c.id != clubId }) }
            } else {
                _state.update { it.copy(error = result.error) }
            }
        }
    }
}
