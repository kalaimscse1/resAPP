package com.warriortech.resb.model


data class TblVoucherRequest(
    var voucher_id: Long,
    var counter_id:Long,
    var voucher_name:String,
    var voucher_prefix:String,
    var voucher_suffix:String,
    var starting_no:String,
    var is_active:Long
)

data class TblVoucherResponse(
    var voucher_id: Long,
    var counter:TblCounter,
    var voucher_name:String,
    var voucher_prefix:String,
    var voucher_suffix:String,
    var starting_no:String,
    var is_active:Long
)

data class TblCounter(
    var counter_id: Long,
    var counter_name:String,
    var ip_address:String,
    var is_active:Long
)

data class TblVoucher(
    var voucher_id: Long,
    var counter:TblCounter,
    var voucher_name:String,
    var voucher_prefix:String,
    var voucher_suffix:String,
    var starting_no:String,
    var is_active:Long
)