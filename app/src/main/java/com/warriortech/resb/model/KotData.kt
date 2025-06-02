package com.warriortech.resb.model

data class KotData(
    val tableId: Long,
    val tableNumber: String,
    val section: String,
    val kotNumber: String,
    val items: List<CartItem>,
    val createdAt: String
)