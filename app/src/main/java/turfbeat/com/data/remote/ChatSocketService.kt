package turfbeat.com.data.remote

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.json.JSONObject
import turfbeat.com.BuildConfig
import turfbeat.com.data.model.ChatMessageDto
import turfbeat.com.data.model.UserDto
import com.google.gson.Gson

data class ChatEvent(
    val type: String,
    val message: ChatMessageDto? = null,
    val error: String? = null
)

class ChatSocketService(
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    private var socket: Socket? = null
    private val events = Channel<ChatEvent>(Channel.BUFFERED)
    val chatEvents: Flow<ChatEvent> = events.receiveAsFlow()

    private val onMessage = Emitter.Listener { args ->
        if (args.isNotEmpty()) {
            try {
                val json = (args[0] as? JSONObject)?.toString() ?: return@Listener
                val message = gson.fromJson(json, ChatMessageDto::class.java)
                events.trySend(ChatEvent("message", message = message))
            } catch (e: Exception) {
                Log.e("ChatSocket", "Parse error", e)
            }
        }
    }

    private val onConnect = Emitter.Listener {
        events.trySend(ChatEvent("connected"))
    }

    private val onDisconnect = Emitter.Listener {
        events.trySend(ChatEvent("disconnected"))
    }

    private val onError = Emitter.Listener { args ->
        val error = args.firstOrNull()?.toString() ?: "Socket error"
        events.trySend(ChatEvent("error", error = error))
    }

    suspend fun connect() {
        val token = tokenManager.getAccessToken() ?: return
        disconnect()

        try {
            val options = IO.Options().apply {
                auth = mapOf("token" to token)
                transports = arrayOf("websocket")
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 2000
            }

            socket = IO.socket(BuildConfig.SERVER_URL + "/chat", options).apply {
                on(Socket.EVENT_CONNECT, onConnect)
                on(Socket.EVENT_DISCONNECT, onDisconnect)
                on(Socket.EVENT_CONNECT_ERROR, onError)
                on("match:message", onMessage)
                connect()
            }
        } catch (e: Exception) {
            events.trySend(ChatEvent("error", error = e.message))
        }
    }

    fun disconnect() {
        socket?.off()
        socket?.disconnect()
        socket = null
    }

    fun joinMatch(matchId: Int) {
        socket?.emit("match:join", JSONObject().put("matchId", matchId))
    }

    fun leaveMatch(matchId: Int) {
        socket?.emit("match:leave", JSONObject().put("matchId", matchId))
    }

    fun sendMessage(matchId: Int, message: String) {
        socket?.emit("match:message", JSONObject().apply {
            put("matchId", matchId)
            put("message", message)
        })
    }
}
