package com.warriortech.resb.data.repository

import com.warriortech.resb.model.TblLedgerDetailIdRequest
import com.warriortech.resb.model.TblLedgerDetailsIdResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import javax.inject.Inject

class LedgerDetailsRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
){
    suspend fun getLedgerDetails():List<TblLedgerDetailsIdResponse>?{
        return try {
            apiService.getLedgerdetails(sessionManager.getCompanyCode() ?: "").body()
        }catch (e:Exception){
            null
        }
    }

    suspend fun addLedgerDetails(ledgerDetails:TblLedgerDetailIdRequest): TblLedgerDetailsIdResponse?{
        return try {
            apiService.addLedgerDetails(ledgerDetails,sessionManager.getCompanyCode() ?: "").body()
        }catch (e:Exception){
            null
        }
    }

    suspend fun updateLedgerDetails(ledgerDetailsId:Long,ledgerDetails:TblLedgerDetailIdRequest): Int? {
        return try {
            apiService.updateLedgerDetails(ledgerDetailsId,ledgerDetails,sessionManager.getCompanyCode()?:"").body()
        }catch (e: Exception){
            null
        }
    }

    suspend fun deleteLedgerDetails(ledgerDetailId: Long):Int?{
        return try {
            apiService.deleteLedgerDetails(ledgerDetailId,sessionManager.getCompanyCode()?:"").body()
        }catch (e:Exception){
            null
        }
    }

    suspend fun getEntryNo():Map<String, String>{
        return try {
            apiService.getEntryNo(
                sessionManager.getUser()?.counter_id?:0,
                "ACCOUNTS",
                sessionManager.getCompanyCode()?:"").body()!!
        }catch (e: Exception){
            emptyMap()
        }
    }
}