
package com.warriortech.resb.model

data class Tax(
    val id: Int = 0,
    val name: String,
    val rate: Double,
    val type: String, // "percentage", "fixed"
    val isActive: Boolean = true,
    val isInclusive: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)
