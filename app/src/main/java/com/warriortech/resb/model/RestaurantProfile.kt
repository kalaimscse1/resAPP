
package com.warriortech.resb.model

data class RestaurantProfile(
    val id: Int = 1,
    val name: String,
    val address: String,
    val phone: String,
    val email: String = "",
    val website: String = "",
    val logo: String = "",
    val gstNumber: String = "",
    val fssaiNumber: String = "",
    val currency: String = "INR",
    val timezone: String = "Asia/Kolkata",
    val createdAt: String = "",
    val updatedAt: String = ""
)
