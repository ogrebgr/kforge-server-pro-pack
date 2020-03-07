package org.example.kforgepro.modules.user

import com.google.gson.annotations.SerializedName

data class SessionInfoUser(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("screen_name") val screenName: String?
)