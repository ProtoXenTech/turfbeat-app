package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.ClubDto
import turfbeat.com.data.model.MatchDto
import turfbeat.com.data.model.UserDto
import turfbeat.com.data.model.VenueDto
import turfbeat.com.data.repository.ClubRepository
import turfbeat.com.data.repository.MatchRepository
import turfbeat.com.data.repository.UserRepository
import turfbeat.com.data.repository.VenueRepository

data class AdminDashboardState(
    val clubs: List<ClubDto> = emptyList(),
    val venues: List<VenueDto> = emptyList(),
    val pendingVenues: List<VenueDto> = emptyList(),
    val players: List<UserDto> = emptyList(),
    val matches: List<MatchDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminDashboardViewModel(
    private val clubRepository: ClubRepository,
    private val venueRepository: VenueRepository,
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val clubs = clubRepository.getClubs()
            val venues = venueRepository.getPendingVenues()
            val players = userRepository.getAvailablePlayers()
            val matches = matchRepository.getMatches()

            _state.update {
                it.copy(
                    clubs = clubs.data ?: emptyList(),
                    pendingVenues = venues.data ?: emptyList(),
                    players = players.data ?: emptyList(),
                    matches = matches.data ?: emptyList(),
                    isLoading = false,
                    error = clubs.error ?: venues.error ?: players.error ?: matches.error
                )
            }
        }
    }

    fun approveVenue(id: Int) {
        viewModelScope.launch {
            venueRepository.approveVenue(id)
            load()
        }
    }

    fun deleteVenue(id: Int) {
        viewModelScope.launch {
            venueRepository.deleteVenue(id)
            load()
        }
    }

    fun deleteClub(id: Int) {
        viewModelScope.launch {
            clubRepository.deleteClub(id)
            load()
        }
    }

    fun deleteMatch(id: Int) {
        viewModelScope.launch {
            matchRepository.deleteMatch(id)
            load()
        }
    }
}
