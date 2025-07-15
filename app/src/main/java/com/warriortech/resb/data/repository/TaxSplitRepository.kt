
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.TaxSplit
import com.warriortech.resb.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaxSplitRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllTaxSplits(): List<TaxSplit> {
        return try {
            apiService.getTaxSplits()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTaxSplitById(id: Int): TaxSplit? {
        return try {
            apiService.getTaxSplitById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createTaxSplit(taxSplit: TaxSplit): TaxSplit? {
        return try {
            apiService.createTaxSplit(taxSplit)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateTaxSplit(taxSplit: TaxSplit): TaxSplit? {
        return try {
            apiService.updateTaxSplit(taxSplit.id, taxSplit)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteTaxSplit(id: Long): Boolean {
        return try {
            apiService.deleteTaxSplit(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
