package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.VenueDto
import turfbeat.com.data.repository.VenueRepository

data class VenueListState(
    val venues: List<VenueDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedArea: String? = null
)

data class VenueDetailState(
    val venue: VenueDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class VenueViewModel(
    private val venueRepository: VenueRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(VenueListState())
    val listState: StateFlow<VenueListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(VenueDetailState())
    val detailState: StateFlow<VenueDetailState> = _detailState.asStateFlow()

    fun loadVenues() {
        viewModelScope.launch {
            val s = _listState.value
            _listState.update { it.copy(isLoading = true, error = null) }
            val result = venueRepository.getVenues(
                area = s.selectedArea,
                search = s.searchQuery.ifBlank { null }
            )
            _listState.update {
                it.copy(
                    venues = result.data ?: emptyList(),
                    isLoading = false,
                    error = result.error
                )
            }
        }
    }

    fun searchVenues(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        loadVenues()
    }

    fun filterByArea(area: String?) {
        _listState.update { it.copy(selectedArea = area) }
        loadVenues()
    }

    fun loadVenueDetail(venueId: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            val result = venueRepository.getVenueDetail(venueId)
            _detailState.update {
                it.copy(venue = result.data, isLoading = false, error = result.error)
            }
        }
    }
}
