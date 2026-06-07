package turfbeat.com.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import turfbeat.com.data.model.ChatMessageDto
import turfbeat.com.data.remote.ChatSocketService
import turfbeat.com.data.remote.TokenManager
import turfbeat.com.data.repository.MatchRepository

data class ChatState(
    val messages: List<ChatMessageDto> = emptyList(),
    val isLoading: Boolean = false,
    val connected: Boolean = false,
    val currentUserId: Int? = null,
    val error: String? = null
)

class ChatViewModel(
    private val chatSocket: ChatSocketService,
    private val matchRepository: MatchRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private var currentMatchId: Int? = null

    fun connect(matchId: Int) {
        currentMatchId = matchId
        viewModelScope.launch {
            val userId = tokenManager.getUserId()
            _state.update { it.copy(isLoading = true, currentUserId = userId) }

            val result = matchRepository.getMatchChat(matchId)
            _state.update {
                it.copy(
                    messages = result.data ?: emptyList(),
                    isLoading = false,
                    error = result.error
                )
            }

            chatSocket.connect()
            chatSocket.chatEvents.collect { event ->
                when (event.type) {
                    "connected" -> {
                        _state.update { it.copy(connected = true) }
                        chatSocket.joinMatch(matchId)
                    }
                    "disconnected" -> _state.update { it.copy(connected = false) }
                    "message" -> {
                        event.message?.let { msg ->
                            _state.update { it.copy(messages = it.messages + msg) }
                        }
                    }
                    "error" -> _state.update { it.copy(error = event.error) }
                }
            }
        }
    }

    fun sendMessage(text: String) {
        val matchId = currentMatchId ?: return
        if (text.isBlank()) return

        chatSocket.sendMessage(matchId, text)
    }

    override fun onCleared() {
        super.onCleared()
        chatSocket.disconnect()
    }
}
