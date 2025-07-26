package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Voucher
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllVouchers(): List<Voucher> {
        return try {
            apiService.getVouchers(SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVoucherById(id: Int): Voucher? {
        return try {
            apiService.getVoucherById(id,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createVoucher(voucher: Voucher): Voucher? {
        return try {
            apiService.createVoucher(voucher,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateVoucher(voucher: Voucher): Voucher? {
        return try {
            apiService.updateVoucher(voucher.id, voucher,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteVoucher(id: Long): Boolean {
        return try {
            apiService.deleteVoucher(id,SessionManager.getCompanyCode()?:"")
            true
        } catch (e: Exception) {
            false
        }
    }
}
