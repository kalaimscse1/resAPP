
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Printer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrinterRepository @Inject constructor() {
    private val _printers = MutableStateFlow<List<Printer>>(emptyList())
    val printers: Flow<List<Printer>> = _printers.asStateFlow()

    init {
        // Initialize with sample data
        _printers.value = listOf(
            Printer(1, "Kitchen Printer", "192.168.1.100", 33, "EPSON"),
            Printer(2, "Receipt Printer", "192.168.1.101", 89, "EPSON"),
            Printer(3, "Bar Printer", "192.168.1.102", 89, "EPSON")
        )
    }

    suspend fun getAllPrinters(): List<Printer> {
        return _printers.value
    }

    suspend fun addPrinter(printer: Printer) {
        val newId = (_printers.value.maxOfOrNull { it.id } ?: 0) + 1
        val newPrinter = printer.copy(id = newId)
        _printers.value = _printers.value + newPrinter
    }

    suspend fun updatePrinter(printer: Printer) {
        _printers.value = _printers.value.map { if (it.id == printer.id) printer else it }
    }

    suspend fun deletePrinter(printerId: Long) {
        _printers.value = _printers.value.filter { it.id != printerId }
    }
}
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Printer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrinterRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllPrinters(): List<Printer> {
        return try {
            apiService.getPrinters()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPrinterById(id: Int): Printer? {
        return try {
            apiService.getPrinterById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createPrinter(printer: Printer): Printer? {
        return try {
            apiService.createPrinter(printer)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updatePrinter(printer: Printer): Printer? {
        return try {
            apiService.updatePrinter(printer.id, printer)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletePrinter(id: Int): Boolean {
        return try {
            apiService.deletePrinter(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
