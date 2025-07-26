package com.warriortech.resb.model

data class Voucher(
    val id: Long = 0,
    val name: String,
    val code: String,
    val type: String, // "percentage", "fixed"
    val value: Double,
    val minAmount: Double = 0.0,
    val maxDiscount: Double = 0.0,
    val validFrom: String,
    val validTo: String,
    val isActive: Boolean = true,
    val usageLimit: Int = -1, // -1 for unlimited
    val usedCount: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)
