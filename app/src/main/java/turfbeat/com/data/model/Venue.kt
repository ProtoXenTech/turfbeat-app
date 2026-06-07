package turfbeat.com.data.model

import com.google.gson.annotations.SerializedName

data class VenueDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("division") val division: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("area") val area: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("images") val images: List<String> = emptyList(),
    @SerializedName("mapLink") val mapLink: String? = null,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("availableSlots") val availableSlots: List<String>? = null,
    @SerializedName("ownerId") val ownerId: Int? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CreateVenueRequest(
    @SerializedName("name") val name: String,
    @SerializedName("area") val area: String,
    @SerializedName("division") val division: String,
    @SerializedName("district") val district: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("mapLink") val mapLink: String? = null
)
