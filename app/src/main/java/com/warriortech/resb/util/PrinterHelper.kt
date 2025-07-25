package com.warriortech.resb.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.warriortech.resb.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for handling communication with physical printer hardware.
 *
 * Note: This is a placeholder implementation. To connect with actual printer hardware,
 * you would need to:
 *
 * 1. Add the appropriate printer SDK/library to your project
 * 2. Implement the connection and printing logic specific to your printer model
 * 3. Request the necessary Bluetooth or USB permissions in the AndroidManifest.xml
 */
class PrinterHelper(private val context: Context) {

    companion object {
        private const val TAG = "PrinterHelper"
    }

    /**
     * Connect to the printer.
     *
     * @return true if connection successful, false otherwise
     */
    fun connectPrinter(): Boolean {
        // Placeholder for actual printer connection code
        Log.d(TAG, "Connecting to printer...")
        return true
    }

    /**
     * Print a KOT ticket to the kitchen printer with template.
     *
     * @param kotData The KOT data to be printed
     * @param template The receipt template to use
     * @return true if print successful, false otherwise
     */
    fun printKot(kotData: KotData, template: ReceiptTemplate): Boolean {
        Log.d(TAG, "Printing KOT #${kotData.kotNumber} for Table ${kotData.tableNumber}")

        try {
            // 1. Connect to printer (if not already connected)
            if (!connectPrinter()) {
                Log.e(TAG, "Failed to connect to printer")
                return false
            }

            // 2. Format KOT data for printing using template
            val printData = formatKotForPrinting(kotData, template)

            // 3. Send data to printer
            // This is where you would use your printer's SDK to send the actual data
            Log.d(TAG, "Sending KOT data to printer: $printData")

            // 4. Disconnect printer
            disconnectPrinter()

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error printing KOT: ${e.message}")
            return false
        }
    }

    /**
     * Print a KOT ticket to the kitchen printer (without template).
     *
     * @param kotData The KOT data to be printed
     * @return true if print successful, false otherwise
     */
    fun printKot(kotData: KotData): Boolean {
        Log.d(TAG, "Printing KOT #${kotData.kotNumber} for Table ${kotData.tableNumber}")

        try {
            // 1. Connect to printer (if not already connected)
            if (!connectPrinter()) {
                Log.e(TAG, "Failed to connect to printer")
                return false
            }

            // 2. Format KOT data for printing
            val printData = formatKotForPrinting(kotData)

            // 3. Send data to printer
            // This is where you would use your printer's SDK to send the actual data
            Log.d(TAG, "Sending data to printer: $printData")

            // 4. Disconnect printer
            disconnectPrinter()

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error printing KOT: ${e.message}")
            return false
        }
    }

    /**
     * Print a bill with template.
     *
     * @param billData The bill data to be printed
     * @param template The receipt template to use
     * @return true if print successful, false otherwise
     */
    fun printBill(billData: Bill, template: ReceiptTemplate): Boolean {
        Log.d(TAG, "Printing Bill #${billData.billNo}")

        try {
            // 1. Connect to printer (if not already connected)
            if (!connectPrinter()) {
                Log.e(TAG, "Failed to connect to printer")
                return false
            }

            // 2. Format bill data for printing using template
            val printData = formatBillForPrinting(billData, template)

            // 3. Send data to printer
            Log.d(TAG, "Sending bill data to printer: $printData")

            // 4. Disconnect printer
            disconnectPrinter()

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error printing bill: ${e.message}")
            return false
        }
    }

    /**
     * Format KOT data into a proper format for printing using template.
     */
    fun formatKotForPrinting(kotData: KotData, template: ReceiptTemplate): String {
        val stringBuilder = StringBuilder()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Header with template settings
        if (template.headerSettings.showLogo) {
            stringBuilder.append(centerText("RESTAURANT", template.paperSettings.characterWidth))
            stringBuilder.append("\n")
        }
        
        stringBuilder.append(centerText("KITCHEN ORDER TICKET", template.paperSettings.characterWidth))
        stringBuilder.append("\n")
        stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
        stringBuilder.append("\n")
        
        // Order details
        stringBuilder.append("KOT #: ${kotData.kotNumber}\n")
        stringBuilder.append("Table: ${kotData.tableNumber}\n")
        stringBuilder.append("Time: ${dateFormat.format(Date())}\n")
        stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
        stringBuilder.append("\n\n")

        // Items with template body settings
        if (template.bodySettings.showItemDetails) {
            stringBuilder.append("ITEMS")
            if (template.bodySettings.showQuantity) {
                stringBuilder.append("          QTY")
            }
            stringBuilder.append("\n")
            stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
            stringBuilder.append("\n")

            kotData.items.forEach { item ->
                val itemName = item.menuItem.menu_item_name.take(15).padEnd(15)
                stringBuilder.append(itemName)
                if (template.bodySettings.showQuantity) {
                    stringBuilder.append(" ${item.quantity}")
                }
                stringBuilder.append("\n")
            }
        }

        // Footer with template settings
        stringBuilder.append("\n")
        stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
        stringBuilder.append("\n")
        
        if (template.footerSettings.showThankYou) {
            stringBuilder.append(centerText("THANK YOU", template.paperSettings.characterWidth))
            stringBuilder.append("\n")
        }
        
        if (template.footerSettings.showDateTime) {
            stringBuilder.append(centerText(dateFormat.format(Date()), template.paperSettings.characterWidth))
            stringBuilder.append("\n")
        }

        return stringBuilder.toString()
    }

    /**
     * Format bill data into a proper format for printing using template.
     */
    @SuppressLint("DefaultLocale")
    fun formatBillForPrinting(billData: Bill, template: ReceiptTemplate): String {
        val stringBuilder = StringBuilder()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Header with template settings
        if (template.headerSettings.showLogo) {
            stringBuilder.append(centerText("RESTAURANT", template.paperSettings.characterWidth))
            stringBuilder.append("\n")
        }
        
        stringBuilder.append(centerText("BILL", template.paperSettings.characterWidth))
        stringBuilder.append("\n")
        stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
        stringBuilder.append("\n")
        
        // Bill details
        stringBuilder.append("Bill #: ${billData.billNo}\n")
        stringBuilder.append("Table: ${billData.tableNo}\n")
        stringBuilder.append("Date: ${dateFormat.format(Date())}\n")
        stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
        stringBuilder.append("\n")

        // Items with template body settings
        if (template.bodySettings.showItemDetails) {
            stringBuilder.append("ITEM")
            if (template.bodySettings.showQuantity) {
                stringBuilder.append("     QTY")
            }
            if (template.bodySettings.showPrice) {
                stringBuilder.append("   PRICE")
            }
            if (template.bodySettings.showTotal) {
                stringBuilder.append("   TOTAL")
            }
            stringBuilder.append("\n")
            stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
            stringBuilder.append("\n")

            billData.items.forEach { item ->
                val itemName = item.itemName.take(12)
                stringBuilder.append(itemName.padEnd(12))
                
                if (template.bodySettings.showQuantity) {
                    stringBuilder.append(" ${item.qty.toString().padStart(3)}")
                }
                if (template.bodySettings.showPrice) {
                    stringBuilder.append(" ${String.format("%.2f", item.price).padStart(6)}")
                }
                if (template.bodySettings.showTotal) {
                    stringBuilder.append(" ${String.format("%.2f", item.amount).padStart(6)}")
                }
                stringBuilder.append("\n")
            }
        }

        // Totals
        stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
        stringBuilder.append("\n")
        stringBuilder.append("Subtotal: ${String.format("%.2f", billData.subtotal)}\n")
//        stringBuilder.append("Tax: ${String.format("%.2f", billData.+billData.sgst)}\n")
        stringBuilder.append("Total: ${String.format("%.2f", billData.total)}\n")

        // Footer with template settings
        stringBuilder.append("\n")
        stringBuilder.append(repeatChar('-', template.paperSettings.characterWidth))
        stringBuilder.append("\n")
        
        if (template.footerSettings.showThankYou) {
            val message = template.footerSettings.customMessage ?: "THANK YOU"
            stringBuilder.append(centerText(message, template.paperSettings.characterWidth))
            stringBuilder.append("\n")
        }
        
        if (template.footerSettings.showDateTime) {
            stringBuilder.append(centerText(dateFormat.format(Date()), template.paperSettings.characterWidth))
            stringBuilder.append("\n")
        }

        return stringBuilder.toString()
    }

    /**
     * Format KOT data into a proper format for printing (without template).
     */
    private fun formatKotForPrinting(kotData: KotData): String {
        val sectionName = when (kotData.section) {
            "ac" -> "AC Hall"
            "non-ac" -> "Non-AC Hall"
            "outdoor" -> "Outdoor"
            else -> kotData.section
        }

        val stringBuilder = StringBuilder()

        // Header
        stringBuilder.append("KITCHEN ORDER TICKET\n")
        stringBuilder.append("--------------------\n")
        stringBuilder.append("KOT #: ${kotData.kotNumber}\n")
        stringBuilder.append("Table: ${kotData.tableNumber} ($sectionName)\n")
        stringBuilder.append("Time: ${kotData.createdAt}\n")
        stringBuilder.append("--------------------\n\n")

        // Items
        stringBuilder.append("ITEMS          QTY\n")
        stringBuilder.append("--------------------\n")

        kotData.items.forEach { item ->
            val itemName = item.menuItem.menu_item_name.take(15).padEnd(15)
            stringBuilder.append("$itemName ${item.quantity}\n")
        }

        // Footer
        stringBuilder.append("\n--------------------\n")
        stringBuilder.append("     THANK YOU     \n")

        return stringBuilder.toString()
    }

    /**
     * Helper function to center text
     */
    private fun centerText(text: String, width: Int): String {
        val padding = (width - text.length) / 2
        return " ".repeat(maxOf(0, padding)) + text
    }

    /**
     * Helper function to repeat character
     */
    private fun repeatChar(char: Char, count: Int): String {
        return char.toString().repeat(count)
    }

    /**
     * Disconnect from the printer.
     */
    private fun disconnectPrinter() {
        // Placeholder for actual printer disconnection code
        Log.d(TAG, "Disconnecting from printer...")
    }
}
