
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Tax
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaxRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllTaxes(): List<Tax> {
        return try {
            apiService.getTaxes()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTaxById(id: Int): Tax? {
        return try {
            apiService.getTaxById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createTax(tax: Tax): Tax? {
        return try {
            apiService.createTax(tax)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateTax(tax: Tax): Tax? {
        return try {
            apiService.updateTax(tax.id, tax)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteTax(id: Int): Boolean {
        return try {
            apiService.deleteTax(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
