
package com.warriortech.resb.model

data class Menu(
    val id: Long,
    val name: String,
    val description: String,
    val isActive: Boolean
)

data class Customer(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String,
    val address: String
)

data class Staff(
    val id: Long,
    val name: String,
    val role: String,
    val phone: String,
    val email: String
)

data class Role(
    val id: Long,
    val name: String,
    val description: String,
    val permissions: String
)

data class Printer(
    val id: Long,
    val name: String,
    val ipAddress: String,
    val port: Int,
    val type: String
)

data class Tax(
    val id: Long,
    val name: String,
    val rate: Double,
    val type: String,
    val isActive: Boolean
)

data class TaxSplit(
    val id: Long,
    val name: String,
    val description: String,
    val splitType: String,
    val percentage: Double
)

data class RestaurantProfile(
    val name: String,
    val address: String,
    val phone: String,
    val email: String
)

data class GeneralSettings(
    val currency: String,
    val language: String,
    val timezone: String
)

data class Voucher(
    val id: Long,
    val code: String,
    val discount: Double,
    val expiryDate: String,
    val isActive: Boolean
)

data class Counter(
    val id: Long,
    val name: String,
    val isActive: Boolean
)
