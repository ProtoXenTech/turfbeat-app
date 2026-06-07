package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.VenueDto
import turfbeat.com.data.repository.VenueRepository

data class VenueDashboardState(
    val venues: List<VenueDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class VenueDashboardViewModel(
    private val venueRepository: VenueRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VenueDashboardState())
    val state: StateFlow<VenueDashboardState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = venueRepository.getMyVenues()
            _state.update {
                it.copy(venues = result.data ?: emptyList(), isLoading = false, error = result.error)
            }
        }
    }

    fun deleteVenue(id: Int) {
        viewModelScope.launch {
            val result = venueRepository.deleteVenue(id)
            if (result.success) load()
            else _state.update { it.copy(error = result.error) }
        }
    }
}
