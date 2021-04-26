package bazaar.tech.com.auth.model


import com.google.gson.annotations.SerializedName

data class LoginGetEmailResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String, // User has been found
    @SerializedName("success")
    val success: Boolean // true
) {
    data class Data(
        @SerializedName("email")
        val email: String, // narek1994t@gmail.com
        @SerializedName("id")
        val id: Long,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("first_name")
        val first_name: String,
        @SerializedName("last_name")
        val last_name: String,
        @SerializedName("company")
        val company: String
    )
}