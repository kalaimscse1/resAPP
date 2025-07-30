package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Voucher
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getAllVouchers(): List<Voucher> {
        return try {
            apiService.getVouchers(sessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVoucherById(id: Int): Voucher? {
        return try {
            apiService.getVoucherById(id,sessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createVoucher(voucher: Voucher): Voucher? {
        return try {
            apiService.createVoucher(voucher,sessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateVoucher(voucher: Voucher): Voucher? {
        return try {
            apiService.updateVoucher(voucher.id, voucher,sessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteVoucher(id: Long): Boolean {
        return try {
            apiService.deleteVoucher(id,sessionManager.getCompanyCode()?:"")
            true
        } catch (e: Exception) {
            false
        }
    }
}
