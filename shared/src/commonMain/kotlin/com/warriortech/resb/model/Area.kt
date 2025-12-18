package com.warriortech.resb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Area(
    @SerialName("area_id") val areaId: Long,
    @SerialName("area_name") val areaName: String,
    @SerialName("is_active") val isActive: Boolean
)
