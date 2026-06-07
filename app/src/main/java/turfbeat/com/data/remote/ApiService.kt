package turfbeat.com.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*
import turfbeat.com.data.model.*

interface ApiService {

    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<ApiResponse<Unit>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<ApiResponse<TokenResponse>>

    @POST("auth/login-password")
    suspend fun loginPassword(@Body request: LoginRequest): Response<ApiResponse<TokenResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<TokenResponse>>

    @POST("auth/me")
    suspend fun me(): Response<ApiResponse<UserDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("auth/check-phone")
    suspend fun checkPhone(@Body request: CheckPhoneRequest): Response<ApiResponse<Unit>>

    @POST("auth/check-email")
    suspend fun checkEmail(@Body request: CheckEmailRequest): Response<ApiResponse<Unit>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @GET("users/me")
    suspend fun getMyProfile(): Response<ApiResponse<UserDto>>

    @PATCH("users/me")
    suspend fun updateProfile(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<UserDto>>

    @GET("users/available-players")
    suspend fun getAvailablePlayers(
        @Query("area") area: String? = null,
        @Query("position") position: String? = null,
        @Query("skillLevel") skillLevel: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<UserDto>>>

    @GET("users/available-players/{id}")
    suspend fun getPlayerDetail(@Path("id") id: Int): Response<ApiResponse<UserDto>>

    @GET("clubs")
    suspend fun getClubs(
        @Query("area") area: String? = null,
        @Query("district") district: String? = null,
        @Query("search") search: String? = null,
        @Query("recruiting") recruiting: Boolean? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<List<ClubDto>>>

    @GET("clubs/{id}")
    suspend fun getClubDetail(@Path("id") id: Int): Response<ApiResponse<ClubDto>>

    @GET("clubs/{id}/members")
    suspend fun getClubMembers(@Path("id") id: Int): Response<ApiResponse<List<ClubMemberDto>>>

    @GET("clubs/dashboard/me")
    suspend fun getMyClubDashboard(): Response<ApiResponse<ClubDashboardDto>>

    @POST("clubs")
    suspend fun createClub(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<ClubDto>>

    @PATCH("clubs/dashboard/me")
    suspend fun updateMyClub(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<ClubDto>>

    @POST("clubs/player/me")
    suspend fun joinClub(@Query("clubId") clubId: Int): Response<ApiResponse<ClubJoinRequestDto>>

    @GET("clubs/player/me")
    suspend fun getMyClubs(): Response<ApiResponse<List<ClubDto>>>

    @DELETE("clubs/player/me/clubs/{clubId}")
    suspend fun leaveClub(@Path("clubId") clubId: Int): Response<ApiResponse<Unit>>

    @PATCH("clubs/dashboard/me/members/{memberId}")
    suspend fun updateMember(
        @Path("memberId") memberId: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<ClubMemberDto>>

    @DELETE("clubs/dashboard/me/members/{memberId}")
    suspend fun removeMember(@Path("memberId") memberId: Int): Response<ApiResponse<Unit>>

    @PATCH("clubs/dashboard/me/player-requests/{requestId}")
    suspend fun handleJoinRequest(
        @Path("requestId") requestId: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<ClubJoinRequestDto>>

    @POST("clubs/dashboard/me/approval-request")
    suspend fun submitApprovalRequest(): Response<ApiResponse<Unit>>

    @POST("clubs/dashboard/me/matches")
    suspend fun createClubMatch(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<MatchDto>>

    @GET("matches")
    suspend fun getMatches(
        @Query("status") status: String? = null,
        @Query("area") area: String? = null
    ): Response<ApiResponse<List<MatchDto>>>

    @GET("matches/open")
    suspend fun getOpenMatches(
        @Query("area") area: String? = null,
        @Query("format") format: String? = null
    ): Response<ApiResponse<List<MatchDto>>>

    @GET("matches/me")
    suspend fun getMyMatches(): Response<ApiResponse<List<MatchDto>>>

    @GET("matches/{id}")
    suspend fun getMatchDetail(@Path("id") id: Int): Response<ApiResponse<MatchDto>>

    @POST("matches")
    suspend fun createMatch(@Body request: CreateMatchRequest): Response<ApiResponse<MatchDto>>

    @PATCH("matches/{id}/status")
    suspend fun updateMatchStatus(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<MatchDto>>

    @POST("matches/{id}/interests")
    suspend fun expressInterest(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @POST("matches/{id}/interests/{interestId}/approve")
    suspend fun approveInterest(
        @Path("id") id: Int,
        @Path("interestId") interestId: Int
    ): Response<ApiResponse<Unit>>

    @POST("matches/{id}/rate")
    suspend fun rateMatch(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<Unit>>

    @GET("matches/{id}/ratings")
    suspend fun getMatchRatings(@Path("id") id: Int): Response<ApiResponse<List<MatchRatingDto>>>

    @GET("venues")
    suspend fun getVenues(
        @Query("area") area: String? = null,
        @Query("district") district: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<VenueDto>>>

    @GET("venues/{id}")
    suspend fun getVenueDetail(@Path("id") id: Int): Response<ApiResponse<VenueDto>>

    @GET("venues/owner/my")
    suspend fun getMyVenues(): Response<ApiResponse<List<VenueDto>>>

    @POST("venues")
    suspend fun createVenue(@Body request: CreateVenueRequest): Response<ApiResponse<VenueDto>>

    @GET("chat/matches/{matchId}")
    suspend fun getMatchChat(@Path("matchId") matchId: Int): Response<ApiResponse<List<ChatMessageDto>>>

    @POST("chat/matches/{matchId}")
    suspend fun sendChatMessage(
        @Path("matchId") matchId: Int,
        @Body request: SendMessageRequest
    ): Response<ApiResponse<ChatMessageDto>>

    @GET("notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<NotificationDto>>>

    @PATCH("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @POST("notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<ApiResponse<Unit>>

    @POST("upload/avatar")
    suspend fun uploadAvatar(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<String>>

    @POST("upload/club-logo")
    suspend fun uploadClubLogo(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<String>>

    @GET("venues/admin/pending")
    suspend fun getPendingVenues(): Response<ApiResponse<List<VenueDto>>>

    @PATCH("venues/{id}/approve")
    suspend fun approveVenue(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @DELETE("venues/{id}")
    suspend fun deleteVenue(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @DELETE("clubs/{id}")
    suspend fun deleteClub(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @PATCH("clubs/{id}/approve")
    suspend fun approveClub(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @DELETE("matches/{id}")
    suspend fun deleteMatch(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
