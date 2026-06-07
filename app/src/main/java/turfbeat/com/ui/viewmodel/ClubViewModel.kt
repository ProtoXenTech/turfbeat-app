package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.ClubDto
import turfbeat.com.data.model.ClubMemberDto
import turfbeat.com.data.repository.ClubRepository

data class ClubListState(
    val clubs: List<ClubDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedArea: String? = null,
    val recruitingOnly: Boolean = false
)

data class ClubDetailState(
    val club: ClubDto? = null,
    val members: List<ClubMemberDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ClubViewModel(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(ClubListState())
    val listState: StateFlow<ClubListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ClubDetailState())
    val detailState: StateFlow<ClubDetailState> = _detailState.asStateFlow()

    fun loadClubs() {
        viewModelScope.launch {
            val s = _listState.value
            _listState.update { it.copy(isLoading = true, error = null) }
            val result = clubRepository.getClubs(
                area = s.selectedArea,
                search = s.searchQuery.ifBlank { null },
                recruiting = if (s.recruitingOnly) true else null
            )
            _listState.update {
                it.copy(
                    clubs = result.data ?: emptyList(),
                    isLoading = false,
                    error = result.error
                )
            }
        }
    }

    fun searchClubs(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        loadClubs()
    }

    fun filterByArea(area: String?) {
        _listState.update { it.copy(selectedArea = area) }
        loadClubs()
    }

    fun toggleRecruiting() {
        _listState.update { it.copy(recruitingOnly = !it.recruitingOnly) }
        loadClubs()
    }

    fun loadClubDetail(clubId: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val clubResult = clubRepository.getClubDetail(clubId)
            if (clubResult.success) {
                val membersResult = clubRepository.getClubMembers(clubId)
                _detailState.update {
                    it.copy(
                        club = clubResult.data,
                        members = membersResult.data ?: emptyList(),
                        isLoading = false,
                        error = membersResult.error
                    )
                }
            } else {
                _detailState.update {
                    it.copy(isLoading = false, error = clubResult.error)
                }
            }
        }
    }
}
