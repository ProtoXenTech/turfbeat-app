package turfbeat.com.data.model

import com.google.gson.annotations.SerializedName

data class ClubDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("slug") val slug: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("logoUrl") val logoUrl: String? = null,
    @SerializedName("coverUrl") val coverUrl: String? = null,
    @SerializedName("division") val division: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("area") val area: String? = null,
    @SerializedName("matchFormats") val matchFormats: List<String>? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("ratingCount") val ratingCount: Int = 0,
    @SerializedName("isLookingForPlayers") val isLookingForPlayers: Boolean = false,
    @SerializedName("neededPositions") val neededPositions: List<String>? = null,
    @SerializedName("memberCount") val memberCount: Int = 0,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("approvalStatus") val approvalStatus: String? = null,
    @SerializedName("playStyle") val playStyle: String? = null,
    @SerializedName("turfPreference") val turfPreference: String? = null,
    @SerializedName("ageGroup") val ageGroup: String? = null,
    @SerializedName("availableDays") val availableDays: List<String>? = null,
    @SerializedName("availableTime") val availableTime: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class ClubMemberDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("role") val role: String = "member",
    @SerializedName("status") val status: String = "active"
)

data class ClubJoinRequestDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("club") val club: ClubDto? = null,
    @SerializedName("status") val status: String = "pending",
    @SerializedName("initiatedByClub") val initiatedByClub: Boolean = false,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class ClubDashboardDto(
    @SerializedName("club") val club: ClubDto? = null,
    @SerializedName("members") val members: List<ClubMemberDto> = emptyList(),
    @SerializedName("joinRequests") val joinRequests: List<ClubJoinRequestDto> = emptyList(),
    @SerializedName("pendingApproval") val pendingApproval: Boolean = false
)
