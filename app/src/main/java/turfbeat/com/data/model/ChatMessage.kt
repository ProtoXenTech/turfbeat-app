package turfbeat.com.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("matchId") val matchId: Int = 0,
    @SerializedName("clubId") val clubId: Int = 0,
    @SerializedName("userId") val userId: Int = 0,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("message") val message: String = "",
    @SerializedName("createdAt") val createdAt: String? = null
)

data class SendMessageRequest(
    @SerializedName("message") val message: String
)
