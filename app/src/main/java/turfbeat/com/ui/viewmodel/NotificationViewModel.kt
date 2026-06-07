package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.NotificationDto
import turfbeat.com.data.repository.NotificationRepository

data class NotificationState(
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    private var polling = false

    fun startPolling() {
        if (polling) return
        polling = true
        viewModelScope.launch {
            load()
            while (polling) {
                delay(15_000)
                load()
            }
        }
    }

    fun stopPolling() {
        polling = false
    }

    private suspend fun load() {
        _state.update { it.copy(isLoading = true) }
        val result = notificationRepository.getNotifications()
        if (result.success) {
            val list = result.data ?: emptyList()
            _state.update {
                it.copy(
                    notifications = list,
                    unreadCount = list.count { n -> !n.isRead },
                    isLoading = false,
                    error = null
                )
            }
        } else {
            _state.update { it.copy(error = result.error) }
        }
    }

    fun markRead(id: Int) {
        viewModelScope.launch {
            notificationRepository.markRead(id)
            load()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            notificationRepository.markAllRead()
            load()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
