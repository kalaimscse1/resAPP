package com.warriortech.resb.model

data class Menu(
    val menu_id: Long,
    val menu_name: String,
    val order_by: String,
    val is_active: Boolean
)

//data class Customer(
//    val id: Long,
//    val name: String,
//    val phone: String,
//    val email: String,
//    val address: String
//)



data class Printer(
    val printer_id: Long,
    val printer_name: String,
    val kitchen_cat_id: Long,
    val ip_address: String,
    val is_active: Boolean
)




