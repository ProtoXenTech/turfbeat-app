package turfbeat.com.data.model

import com.google.gson.annotations.SerializedName

data class MatchDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("homeClub") val homeClub: ClubDto? = null,
    @SerializedName("awayClub") val awayClub: ClubDto? = null,
    @SerializedName("requestType") val requestType: String = "private_invite",
    @SerializedName("matchFormat") val matchFormat: String = "five_a_side",
    @SerializedName("matchDate") val matchDate: String? = null,
    @SerializedName("timeSlot") val timeSlot: String = "MIN60",
    @SerializedName("venue") val venue: VenueDto? = null,
    @SerializedName("venueManual") val venueManual: String? = null,
    @SerializedName("venueStatus") val venueStatus: String? = null,
    @SerializedName("costMode") val costMode: String? = null,
    @SerializedName("estimatedCost") val estimatedCost: Double? = null,
    @SerializedName("status") val status: String = "pending",
    @SerializedName("homeGoals") val homeGoals: Int? = null,
    @SerializedName("awayGoals") val awayGoals: Int? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class MatchInterestDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("club") val club: ClubDto? = null,
    @SerializedName("status") val status: String = "interested"
)

data class MatchRatingDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("sportsmanship") val sportsmanship: Int = 0,
    @SerializedName("punctuality") val punctuality: Int = 0,
    @SerializedName("communication") val communication: Int = 0,
    @SerializedName("organization") val organization: Int = 0,
    @SerializedName("competitiveness") val competitiveness: Int = 0,
    @SerializedName("overall") val overall: Double = 0.0,
    @SerializedName("feedback") val feedback: String? = null
)

data class MatchReportDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("clubId") val clubId: Int = 0,
    @SerializedName("homeGoals") val homeGoals: Int = 0,
    @SerializedName("awayGoals") val awayGoals: Int = 0,
    @SerializedName("scorers") val scorers: List<MatchScorerDto> = emptyList()
)

data class MatchScorerDto(
    @SerializedName("playerName") val playerName: String = "",
    @SerializedName("goals") val goals: Int = 0
)

data class CreateMatchRequest(
    @SerializedName("matchFormat") val matchFormat: String,
    @SerializedName("matchDate") val matchDate: String,
    @SerializedName("timeSlot") val timeSlot: String,
    @SerializedName("requestType") val requestType: String = "private_invite",
    @SerializedName("venueId") val venueId: Int? = null,
    @SerializedName("venueManual") val venueManual: String? = null,
    @SerializedName("costMode") val costMode: String? = null,
    @SerializedName("estimatedCost") val estimatedCost: Double? = null,
    @SerializedName("awayClubId") val awayClubId: Int? = null
)
