package com.warriortech.resb.model

data class TblGroupNature(
    val g_nature_id: Int,
    val g_nature_name: String,
    val is_active: Boolean
)

data class TblBankDetails(
    val ledger_code: String,
    val bank_name: String,
    val account_no: String,
    val ifsc_code: String,
    val upi_id: String
)

data class TblLedgerDetails(
    val ledger_code: String,
    val ledger_name: String,
    val order_by: Int,
    val group: TblGroupDetails,
    val address: String,
    val address1: String,
    val place: String,
    val pincode: String,
    val country: String,
    val contact_no: String,
    val email: String,
    val gst_no: String,
    val pan_no: String,
    val state_code: String,
    val state_name: String,
    val sac_code: String,
    val igst_status: String,
    val opening_balance: Double,
    val due_date: String,
    val bank_details: String,
    val tamil_text: String,
    val ledger_group: String,
    val distance: Double,
    val is_active: Boolean
)

data class TblGroupDetails(
    val group_id: Int,
    val group_code: String,
    val group_name: String,
    val group_order: Int,
    val sub_group: String,
    val group_nature: TblGroupNature,
    val gross_profit: String,
    val tamil_text: String,
    val is_active: Boolean,
    val group_by: String
)