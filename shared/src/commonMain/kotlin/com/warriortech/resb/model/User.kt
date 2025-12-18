package com.warriortech.resb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val password: String,
    val name: String,
    val role: String,
    val companyCode: String
)

@Serializable
data class Staff(
    @SerialName("staff_id") var staffId: Long,
    @SerialName("staff_name") var staffName: String,
    @SerialName("contact_no") var contactNo: String,
    var address: String,
    @SerialName("user_name") var userName: String,
    var password: String,
    @SerialName("role_id") var roleId: Long,
    var role: String,
    @SerialName("last_login") var lastLogin: String,
    @SerialName("is_block") var isBlock: Boolean,
    @SerialName("counter_id") var counterId: Long,
    @SerialName("counter_name") var counterName: String,
    @SerialName("area_id") var areaId: Long,
    @SerialName("area_name") var areaName: String,
    var commission: Double = 0.0,
    @SerialName("is_active") var isActive: Long
)

@Serializable
data class LoginRequest(
    val companyCode: String,
    @SerialName("user_name") val userName: String,
    val password: String
)

@Serializable
data class StaffRequest(
    @SerialName("staff_id") val staffId: Long,
    @SerialName("staff_name") val staffName: String,
    @SerialName("contact_no") val contactNo: String,
    val address: String,
    @SerialName("user_name") val userName: String,
    val password: String,
    @SerialName("role_id") val roleId: Long,
    @SerialName("last_login") val lastLogin: String,
    @SerialName("is_block") val isBlock: Boolean,
    val commission: Double,
    @SerialName("counter_id") val counterId: Long,
    @SerialName("area_id") val areaId: Long,
    @SerialName("is_active") val isActive: Long
)

@Serializable
data class CompanyMaster(
    val id: Long = 0,
    @SerialName("company_master_code") var companyMasterCode: String,
    @SerialName("company_name") var companyName: String,
    @SerialName("owner_name") var ownerName: String,
    var address1: String,
    var address2: String,
    var place: String,
    var pincode: String,
    @SerialName("contact_no") var contactNo: String,
    @SerialName("mail_id") var mailId: String,
    var country: String,
    var state: String,
    var year: String,
    @SerialName("database_name") var databaseName: String,
    @SerialName("order_plan") var orderPlan: String,
    @SerialName("install_date") var installDate: String,
    @SerialName("subscription_days") var subscriptionDays: Long,
    @SerialName("expiry_date") var expiryDate: String,
    @SerialName("is_block") var isBlock: Boolean = false,
    @SerialName("is_active") var isActive: Boolean = true
)
