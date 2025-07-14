
package com.warriortech.resb.model

data class GeneralSettings(
    val id: Int = 1,
    val dateFormat: String = "dd/MM/yyyy",
    val timeFormat: String = "24",
    val language: String = "en",
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val decimalPlaces: Int = 2,
    val taxCalculation: String = "exclusive", // "inclusive", "exclusive"
    val autoBackup: Boolean = true,
    val backupFrequency: String = "daily", // "daily", "weekly", "monthly"
    val printReceipt: Boolean = true,
    val printKot: Boolean = true,
    val updatedAt: String = ""
)
