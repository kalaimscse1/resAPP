package com.warriortech.resb.model

//
//data class GeneralSettings(
//    val id: Int = 1,
//    val dateFormat: String = "dd/MM/yyyy",
//    val timeFormat: String = "24",
//    val language: String = "en",
//    val currency: String = "INR",
//    val currencySymbol: String = "₹",
//    val decimalPlaces: Int = 2,
//    val taxCalculation: String = "exclusive", // "inclusive", "exclusive"
//    val autoBackup: Boolean = true,
//    val backupFrequency: String = "daily", // "daily", "weekly", "monthly"
//    val printReceipt: Boolean = true,
//    val printKot: Boolean = true,
//    val updatedAt: String = "",
//    val timezone: String
//)
data class GeneralSettings(
    var id:Int = 1,
    var company_name_font:Int,
    var address_font:Int,
    var is_tax:Boolean,
    var is_tax_included:Boolean,
    var is_round_off:Boolean,
    var is_allowed_disc:Boolean,
    var disc_by:Int,
    var disc_amt:Double,
    var is_tendered:Boolean,
    var is_gst_summary: Boolean,
    var is_receipt: Boolean,
    var is_kot: Boolean,
    var is_logo: Boolean,
    var logo_path: String,
    var is_cess:Boolean,
    var is_delivery_charge:Boolean,
    var is_table_allowed:Boolean,
    var is_waiter_allowed:Boolean,
    var menu_show_in_time:Boolean,
    var tamil_receipt_print:Boolean
)