package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.UserDto
import turfbeat.com.data.repository.UserRepository

data class PlayerListState(
    val players: List<UserDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedPosition: String? = null,
    val selectedSkill: String? = null,
    val selectedArea: String? = null
)

data class PlayerDetailState(
    val player: UserDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PlayerViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(PlayerListState())
    val listState: StateFlow<PlayerListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(PlayerDetailState())
    val detailState: StateFlow<PlayerDetailState> = _detailState.asStateFlow()

    fun loadPlayers() {
        viewModelScope.launch {
            val s = _listState.value
            _listState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.getAvailablePlayers(
                area = s.selectedArea,
                position = s.selectedPosition,
                skillLevel = s.selectedSkill,
                search = s.searchQuery.ifBlank { null }
            )
            _listState.update {
                it.copy(
                    players = result.data ?: emptyList(),
                    isLoading = false,
                    error = result.error
                )
            }
        }
    }

    fun searchPlayers(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        loadPlayers()
    }

    fun filterByPosition(position: String?) {
        _listState.update { it.copy(selectedPosition = position) }
        loadPlayers()
    }

    fun filterBySkill(skill: String?) {
        _listState.update { it.copy(selectedSkill = skill) }
        loadPlayers()
    }

    fun filterByArea(area: String?) {
        _listState.update { it.copy(selectedArea = area) }
        loadPlayers()
    }

    fun loadPlayerDetail(playerId: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.getPlayerDetail(playerId)
            _detailState.update {
                it.copy(player = result.data, isLoading = false, error = result.error)
            }
        }
    }
}
