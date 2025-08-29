package com.warriortech.resb.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.warriortech.resb.model.GSTSummaryReport
import com.warriortech.resb.model.TodaySalesReport
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object ExportUtils {

    fun exportToPDF(
        context: Context,
        todaySales: TodaySalesReport?,
        gstSummary: GSTSummaryReport?,
        selectedDate: String
    ): File? {
        return try {
            val pdfDocument = PdfDocument()
            val paint = Paint()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            var y = 40
            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("Report - $selectedDate", 40f, y.toFloat(), paint)

            y += 30
            paint.textSize = 12f
            paint.isFakeBoldText = false

            todaySales?.let {
                canvas.drawText("Today's Sales: Total ${it.totalSales} | Orders ${it.totalOrders}", 40f, y.toFloat(), paint)
                y += 20
                canvas.drawText("Tax: ${it.totalTax} | Cess: ${it.totalCess}", 40f, y.toFloat(), paint)
                y += 25
            }

            gstSummary?.let {
                canvas.drawText("GST Summary: CGST ${it.totalCGST} | SGST ${it.totalSGST} | IGST ${it.totalIGST}", 40f, y.toFloat(), paint)
                y += 25
            }

//            salesSummary?.let {
//                canvas.drawText("Sales Summary: Total ${it.totalSales} | Orders ${it.totalOrders}", 40f, y.toFloat(), paint)
//                y += 25
//                if (it.topSellingItems.isNotEmpty()) {
//                    canvas.drawText("Top Items:", 40f, y.toFloat(), paint)
//                    y += 20
//                    it.topSellingItems.forEach { t ->
//                        canvas.drawText("${t.itemName} - Qty:${t.quantity} Rev:${t.revenue}", 50f, y.toFloat(), paint)
//                        y += 18
//                    }
//                }
//            }

            pdfDocument.finishPage(page)
            val file = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS), "Report_$selectedDate.pdf")
            FileOutputStream(file).use { out -> pdfDocument.writeTo(out) }
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportToExcel(
        context: Context,
        todaySales: TodaySalesReport?,
        gstSummary: GSTSummaryReport?,
//        salesSummary: SalesSummaryReport?,
        selectedDate: String
    ): File? {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Report")

            var rowIdx = 0
            fun createRow(vararg cells: String) {
                val row = sheet.createRow(rowIdx++)
                cells.forEachIndexed { i, v ->
                    row.createCell(i).setCellValue(v)
                }
            }

            createRow("Report Date", selectedDate)
            createRow("")

            todaySales?.let {
                createRow("Today's Sales", "Total", it.totalSales.toString())
                createRow("Orders", it.totalOrders.toString())
                createRow("Tax", it.totalTax.toString())
                createRow("")
            }

            gstSummary?.let {
                createRow("GST Summary", "CGST", it.totalCGST.toString(), "SGST", it.totalSGST.toString(), "IGST", it.totalIGST.toString())
                createRow("")
            }

//            salesSummary?.let {
//                createRow("Sales Summary", "Total", it.totalSales.toString(), "Orders", it.totalOrders.toString())
//                if (it.topSellingItems.isNotEmpty()) {
//                    createRow("Top Items")
//                    it.topSellingItems.forEach { t ->
//                        createRow(t.itemName, t.quantity.toString(), t.revenue.toString())
//                    }
//                }
//            }

            val file = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS), "Report_$selectedDate.xlsx")
            FileOutputStream(file).use { out -> workbook.write(out) }
            workbook.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = when (file.extension.lowercase()) {
                    "pdf" -> "application/pdf"
                    "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    else -> "*/*"
                }
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Share Report"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
