package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.MatchDto
import turfbeat.com.data.repository.MatchRepository

data class MatchListState(
    val upcoming: List<MatchDto> = emptyList(),
    val completed: List<MatchDto> = emptyList(),
    val openListings: List<MatchDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MatchDetailState(
    val match: MatchDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MatchViewModel(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(MatchListState())
    val listState: StateFlow<MatchListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(MatchDetailState())
    val detailState: StateFlow<MatchDetailState> = _detailState.asStateFlow()

    fun loadMatches() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }

            val upcoming = matchRepository.getMatches(status = "pending,confirmed")
            val completed = matchRepository.getMatches(status = "completed")
            val openListings = matchRepository.getOpenMatches()

            _listState.update {
                it.copy(
                    upcoming = upcoming.data ?: emptyList(),
                    completed = completed.data ?: emptyList(),
                    openListings = openListings.data ?: emptyList(),
                    isLoading = false,
                    error = upcoming.error ?: completed.error ?: openListings.error
                )
            }
        }
    }

    fun loadMatchDetail(matchId: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val result = matchRepository.getMatchDetail(matchId)
            _detailState.update {
                it.copy(match = result.data, isLoading = false, error = result.error)
            }
        }
    }
}
