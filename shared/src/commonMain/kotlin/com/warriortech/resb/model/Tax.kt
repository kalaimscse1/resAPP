package com.warriortech.resb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tax(
    @SerialName("tax_id") val taxId: Long,
    @SerialName("tax_name") val taxName: String,
    @SerialName("tax_percentage") val taxPercentage: Double,
    @SerialName("cess_percentage") val cessPercentage: Double,
    @SerialName("is_active") val isActive: Boolean
)
