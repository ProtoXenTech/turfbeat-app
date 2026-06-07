package turfbeat.com.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("division") val division: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("area") val area: String? = null,
    @SerializedName("position") val position: String? = null,
    @SerializedName("skillLevel") val skillLevel: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("isSuperAdmin") val isSuperAdmin: Boolean = false,
    @SerializedName("isVenueOwner") val isVenueOwner: Boolean = false,
    @SerializedName("isLookingForMoreClubs") val isLookingForMoreClubs: Boolean = false,
    @SerializedName("isInjured") val isInjured: Boolean = false,
    @SerializedName("lastMatchPlayed") val lastMatchPlayed: String? = null,
    @SerializedName("emergencyContact") val emergencyContact: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SendOtpRequest(
    @SerializedName("email") val email: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String
)

data class CheckPhoneRequest(
    @SerializedName("phone") val phone: String
)

data class CheckEmailRequest(
    @SerializedName("email") val email: String
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("password") val password: String
)

data class RegisterStepProfileRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("division") val division: String,
    @SerializedName("district") val district: String,
    @SerializedName("area") val area: String,
    @SerializedName("position") val position: String,
    @SerializedName("skillLevel") val skillLevel: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String?
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)
