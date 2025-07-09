package com.warriortech.resb.model

data class User(
    val id: Long,
    val username: String,
    val password: String,  // Note: In a real app, this would be hashed and not stored in the model
    val name: String,
    val role: String,      // e.g., "manager", "waiter", "chef"
    val companyCode: String // Restaurant's unique company code for multi-tenant system
)

data class TblStaff(
    var staff_id: Long,
    var staff_name:String,
    var contact_no:String,
    var address:String,
    var user_name:String,
    var password:String,
    var role_id:Long,
    var role:String,
    var last_login:String,
    var is_block:Boolean,
    var counter_id:Long,
    var counter_name:String,
    var is_active:Long
)
data class LoginRequest(
    val companyCode: String,
    val user_name: String,
    val password: String
)