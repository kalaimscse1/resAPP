package com.warriortech.resb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    @SerialName("customer_id") val customerId: Long,
    @SerialName("customer_name") val customerName: String,
    @SerialName("customer_phone") val customerPhone: String,
    @SerialName("customer_email") val customerEmail: String? = null,
    @SerialName("customer_address") val customerAddress: String? = null,
    @SerialName("created_date") val createdDate: String,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class CustomerSearchResponse(
    val success: Boolean,
    val message: String,
    val data: List<Customer>
)

@Serializable
data class CreateCustomerRequest(
    @SerialName("customer_name") val customerName: String,
    @SerialName("customer_phone") val customerPhone: String,
    @SerialName("customer_email") val customerEmail: String? = null,
    @SerialName("customer_address") val customerAddress: String? = null
)

@Serializable
data class CreateCustomerResponse(
    val success: Boolean,
    val message: String,
    val data: Customer? = null
)

@Serializable
data class CustomerDetails(
    @SerialName("customer_id") var customerId: Long,
    @SerialName("customer_name") var customerName: String,
    @SerialName("contact_no") var contactNo: String,
    var address: String,
    @SerialName("email_address") var emailAddress: String,
    @SerialName("gst_no") var gstNo: String,
    @SerialName("igst_status") var igstStatus: Boolean,
    @SerialName("is_active") var isActive: Long
)
