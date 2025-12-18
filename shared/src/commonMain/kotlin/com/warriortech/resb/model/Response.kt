package com.warriortech.resb.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val staff: Staff
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
