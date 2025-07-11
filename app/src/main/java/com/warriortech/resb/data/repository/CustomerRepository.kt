
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Customer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor() {

    private val customers = mutableListOf<Customer>()

    suspend fun getAllCustomers(): List<Customer> {
        return customers.toList()
    }

    suspend fun insertCustomer(customer: Customer): Long {
        val newId = (customers.maxOfOrNull { it.customer_id } ?: 0) + 1
        val newCustomer = customer.copy(customer_id = newId)
        customers.add(newCustomer)
        return newId
    }

    suspend fun updateCustomer(customer: Customer) {
        val index = customers.indexOfFirst { it.customer_id == customer.customer_id }
        if (index != -1) {
            customers[index] = customer
        }
    }

    suspend fun deleteCustomer(id: Long) {
        customers.removeAll { it.customer_id == id }
    }

    suspend fun getCustomerById(id: Long): Customer? {
        return customers.find { it.customer_id == id }
    }
}
