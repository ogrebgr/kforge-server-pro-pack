package org.example.kforgepro.modules.admin.data

import com.google.gson.annotations.SerializedName

data class SessionInfoAdmin(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("super_admin") val superAdmin: Boolean
)