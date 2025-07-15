
package com.warriortech.resb.model

data class TaxSplit(
    val id: Long = 0,
    val name: String,
    val description: String,
    val splitType: String, // "equal", "percentage", "amount"
    val percentage: Double = 0.0,
    val amount: Double = 0.0,
    val taxIds: List<Int> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)
