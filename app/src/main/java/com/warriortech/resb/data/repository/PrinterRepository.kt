package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Printer
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrinterRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllPrinters(): List<Printer> {
        return try {
            apiService.getPrinters(SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPrinterById(id: Int): Printer? {
        return try {
            apiService.getPrinterById(id,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createPrinter(printer: Printer): Printer? {
        return try {
            apiService.createPrinter(printer,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updatePrinter(printer: Printer): Printer? {
        return try {
            apiService.updatePrinter(printer.printer_id, printer,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletePrinter(id: Long): Boolean {
        return try {
            apiService.deletePrinter(id,SessionManager.getCompanyCode()?:"")
            true
        } catch (e: Exception) {
            false
        }
    }
}
