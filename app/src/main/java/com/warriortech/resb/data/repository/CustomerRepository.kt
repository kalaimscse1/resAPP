
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Customer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllCustomers(): List<Customer> {
        val response = apiService.getAllCustomers()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch customers: ${response.message()}")
        }
    }

    suspend fun insertCustomer(customer: Customer): Customer {
        val response = apiService.createCustomer(customer)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create customer")
        } else {
            throw Exception("Failed to create customer: ${response.message()}")
        }
    }

    suspend fun updateCustomer(customer: Customer): Customer {
        val response = apiService.updateCustomer(customer.id, customer)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update customer")
        } else {
            throw Exception("Failed to update customer: ${response.message()}")
        }
    }

    suspend fun deleteCustomer(customerId: Int) {
        val response = apiService.deleteCustomer(customerId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete customer: ${response.message()}")
        }
    }
}
