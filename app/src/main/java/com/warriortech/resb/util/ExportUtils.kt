
package com.warriortech.resb.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.warriortech.resb.model.*
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {
    
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
    
    fun exportToPDF(
        context: Context,
        todaySales: TodaySalesReport?,
        gstSummary: GSTSummaryReport?,
        salesSummary: SalesSummaryReport?,
        selectedDate: String
    ): File? {
        return try {
            val file = File(context.getExternalFilesDir(null), "sales_report_${selectedDate.replace(" ", "_")}.pdf")
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            
            // Title
            document.add(
                Paragraph("Sales Report - $selectedDate")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18f)
                    .setBold()
            )
            
            document.add(Paragraph(" ")) // Space
            
            // Today's Sales Section
            todaySales?.let { sales ->
                document.add(
                    Paragraph("Today's Sales Summary")
                        .setFontSize(14f)
                        .setBold()
                )
                
                val salesTable = Table(2)
                salesTable.addCell("Total Orders")
                salesTable.addCell(sales.totalOrders.toString())
                salesTable.addCell("Total Amount")
                salesTable.addCell(currencyFormatter.format(sales.totalAmount))
                salesTable.addCell("Cash Sales")
                salesTable.addCell(currencyFormatter.format(sales.cashSales))
                salesTable.addCell("Card Sales")
                salesTable.addCell(currencyFormatter.format(sales.cardSales))
                salesTable.addCell("UPI Sales")
                salesTable.addCell(currencyFormatter.format(sales.upiSales))
                
                document.add(salesTable)
                document.add(Paragraph(" "))
            }
            
            // GST Summary Section
            gstSummary?.let { gst ->
                document.add(
                    Paragraph("GST Summary")
                        .setFontSize(14f)
                        .setBold()
                )
                
                val gstTable = Table(3)
                gstTable.addCell("GST Rate")
                gstTable.addCell("Taxable Amount")
                gstTable.addCell("Tax Amount")
                
                gst.gstBreakdown.forEach { breakdown ->
                    gstTable.addCell("${breakdown.gstRate}%")
                    gstTable.addCell(currencyFormatter.format(breakdown.taxableAmount))
                    gstTable.addCell(currencyFormatter.format(breakdown.taxAmount))
                }
                
                gstTable.addCell("Total")
                gstTable.addCell(currencyFormatter.format(gst.totalTaxableAmount))
                gstTable.addCell(currencyFormatter.format(gst.totalTaxAmount))
                
                document.add(gstTable)
                document.add(Paragraph(" "))
            }
            
            // Sales Summary Section
            salesSummary?.let { summary ->
                document.add(
                    Paragraph("Detailed Sales")
                        .setFontSize(14f)
                        .setBold()
                )
                
                val summaryTable = Table(4)
                summaryTable.addCell("Bill No")
                summaryTable.addCell("Time")
                summaryTable.addCell("Amount")
                summaryTable.addCell("Payment Method")
                
                summary.sales.forEach { sale ->
                    summaryTable.addCell(sale.billNo)
                    summaryTable.addCell(timeFormatter.format(sale.timestamp))
                    summaryTable.addCell(currencyFormatter.format(sale.amount))
                    summaryTable.addCell(sale.paymentMethod)
                }
                
                document.add(summaryTable)
            }
            
            document.add(
                Paragraph("Generated on: ${timeFormatter.format(Date())}")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10f)
            )
            
            document.close()
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
        salesSummary: SalesSummaryReport?,
        selectedDate: String
    ): File? {
        return try {
            val workbook = XSSFWorkbook()
            val file = File(context.getExternalFilesDir(null), "sales_report_${selectedDate.replace(" ", "_")}.xlsx")
            
            // Summary Sheet
            val summarySheet = workbook.createSheet("Summary")
            var rowIndex = 0
            
            // Title
            val titleRow = summarySheet.createRow(rowIndex++)
            titleRow.createCell(0).setCellValue("Sales Report - $selectedDate")
            rowIndex++ // Empty row
            
            // Today's Sales
            todaySales?.let { sales ->
                summarySheet.createRow(rowIndex++).createCell(0).setCellValue("Today's Sales Summary")
                summarySheet.createRow(rowIndex++).apply {
                    createCell(0).setCellValue("Total Orders")
                    createCell(1).setCellValue(sales.totalOrders.toDouble())
                }
                summarySheet.createRow(rowIndex++).apply {
                    createCell(0).setCellValue("Total Amount")
                    createCell(1).setCellValue(sales.totalAmount)
                }
                summarySheet.createRow(rowIndex++).apply {
                    createCell(0).setCellValue("Cash Sales")
                    createCell(1).setCellValue(sales.cashSales)
                }
                summarySheet.createRow(rowIndex++).apply {
                    createCell(0).setCellValue("Card Sales")
                    createCell(1).setCellValue(sales.cardSales)
                }
                summarySheet.createRow(rowIndex++).apply {
                    createCell(0).setCellValue("UPI Sales")
                    createCell(1).setCellValue(sales.upiSales)
                }
                rowIndex++ // Empty row
            }
            
            // GST Summary
            gstSummary?.let { gst ->
                summarySheet.createRow(rowIndex++).createCell(0).setCellValue("GST Summary")
                summarySheet.createRow(rowIndex++).apply {
                    createCell(0).setCellValue("GST Rate")
                    createCell(1).setCellValue("Taxable Amount")
                    createCell(2).setCellValue("Tax Amount")
                }
                
                gst.gstBreakdown.forEach { breakdown ->
                    summarySheet.createRow(rowIndex++).apply {
                        createCell(0).setCellValue("${breakdown.gstRate}%")
                        createCell(1).setCellValue(breakdown.taxableAmount)
                        createCell(2).setCellValue(breakdown.taxAmount)
                    }
                }
                
                summarySheet.createRow(rowIndex++).apply {
                    createCell(0).setCellValue("Total")
                    createCell(1).setCellValue(gst.totalTaxableAmount)
                    createCell(2).setCellValue(gst.totalTaxAmount)
                }
            }
            
            // Detailed Sales Sheet
            salesSummary?.let { summary ->
                val salesSheet = workbook.createSheet("Detailed Sales")
                var salesRowIndex = 0
                
                // Headers
                salesSheet.createRow(salesRowIndex++).apply {
                    createCell(0).setCellValue("Bill No")
                    createCell(1).setCellValue("Time")
                    createCell(2).setCellValue("Amount")
                    createCell(3).setCellValue("Payment Method")
                }
                
                // Data
                summary.sales.forEach { sale ->
                    salesSheet.createRow(salesRowIndex++).apply {
                        createCell(0).setCellValue(sale.billNo)
                        createCell(1).setCellValue(timeFormatter.format(sale.timestamp))
                        createCell(2).setCellValue(sale.amount)
                        createCell(3).setCellValue(sale.paymentMethod)
                    }
                }
            }
            
            // Write to file
            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when (file.extension) {
                    "pdf" -> "application/pdf"
                    "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    else -> "*/*"
                }
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            
            context.startActivity(Intent.createChooser(intent, "Share Report"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
