package com.warriortech.resb.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.warriortech.resb.model.CategoryReport
import com.warriortech.resb.model.ItemReport
import com.warriortech.resb.model.TblBillingResponse
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object ReportExport {
    fun exportToPdf(context: Context, bills: List<TblBillingResponse>) {
        try {
            val pdfDir = File(context.getExternalFilesDir(null), "reports")
            if (!pdfDir.exists()) pdfDir.mkdirs()

            val file = File(pdfDir, "SalesReport.pdf")
            val document = Document(PageSize.A4, 10f, 10f, 20f, 20f)
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Title
            val titleFont = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD)
            val title = Paragraph("Sales Report\n\n", titleFont)
            title.alignment = Element.ALIGN_CENTER
            document.add(title)

            document.add(Paragraph("Generated on: ${Date()}\n\n"))

            // ✅ Define columns
            val table = PdfPTable(8)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(2f, 2f, 3f, 3f, 2.5f, 2f, 2f, 2.5f))

            val headerFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
            val headers = listOf(
                "Order No", "Bill No", "Date", "Customer",
                "Pay Mode", "Bill Amount", "Discount", "Received"
            )

            // Add headers
            headers.forEach {
                val cell = PdfPCell(Phrase(it, headerFont))
                cell.backgroundColor = BaseColor.LIGHT_GRAY
                cell.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(cell)
            }

            // Totals
            var totalBillAmount = 0.0
            var totalDiscount = 0.0
            var totalReceived = 0.0

            // ✅ Date formatter
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

            // Add rows
            for (bill in bills) {
                val payMode = when {
                    bill.cash > 0.0 -> "CASH"
                    bill.card > 0.0 -> "CARD"
                    bill.upi > 0.0 -> "UPI"
                    bill.due > 0.0 -> "DUE"
                    else -> "OTHERS"
                }

                // Format date safely
                val formattedDate = try {
                    LocalDate.parse(bill.bill_date, inputFormatter).format(outputFormatter)
                } catch (e: Exception) {
                    bill.bill_date // fallback to raw if parsing fails
                }

                table.addCell(bill.order_master.order_master_id.toString())
                table.addCell(bill.bill_no.toString())
                table.addCell(formattedDate)
                table.addCell(bill.customer.customer_name)
                table.addCell(payMode)

                val billAmtCell = PdfPCell(Phrase("₹${bill.rounded_amt}"))
                billAmtCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(billAmtCell)

                val discCell = PdfPCell(Phrase("${bill.disc_amt}"))
                discCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(discCell)

                val receivedCell = PdfPCell(Phrase("${bill.received_amt}"))
                receivedCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(receivedCell)

                // accumulate totals
                totalBillAmount += bill.rounded_amt
                totalDiscount += bill.disc_amt
                totalReceived += bill.received_amt
            }

            // ✅ Add Summary Row
            val boldFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)

            val summaryCell = PdfPCell(Phrase("TOTAL", boldFont))
            summaryCell.colspan = 5
            summaryCell.horizontalAlignment = Element.ALIGN_RIGHT
            summaryCell.backgroundColor = BaseColor.YELLOW
            table.addCell(summaryCell)

            val totalBillCell = PdfPCell(Phrase("₹$totalBillAmount", boldFont))
            totalBillCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalBillCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalBillCell)

            val totalDiscCell = PdfPCell(Phrase("$totalDiscount", boldFont))
            totalDiscCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalDiscCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalDiscCell)

            val totalReceivedCell = PdfPCell(Phrase("$totalReceived", boldFont))
            totalReceivedCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalReceivedCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalReceivedCell)

            document.add(table)
            document.close()

            shareFile(context, file, "application/pdf")
            Toast.makeText(context, "PDF Exported Successfully!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(context, "PDF Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun exportToExcel(context: Context, bills: List<TblBillingResponse>) {
        try {
            val excelDir = File(context.getExternalFilesDir(null), "reports")
            if (!excelDir.exists()) excelDir.mkdirs()

            val file = File(excelDir, "SalesReport.xlsx")
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Sales Report")

            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Order No")
            headerRow.createCell(1).setCellValue("Bill No")
            headerRow.createCell(2).setCellValue("Date")
            headerRow.createCell(3).setCellValue("Customer")
            headerRow.createCell(4).setCellValue("Pay Mode")
            headerRow.createCell(5).setCellValue("Bill Amount")
            headerRow.createCell(6).setCellValue("Discount")
            headerRow.createCell(7).setCellValue("Received")

            bills.forEachIndexed { index, bill ->
                val payMode = when {
                    bill.cash > 0.0 -> "CASH"
                    bill.card > 0.0 -> "CARD"
                    bill.upi > 0.0 -> "UPI"
                    bill.due > 0.0 -> "DUE"
                    else -> "OTHERS"
                }
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(bill.order_master.order_master_id)
                row.createCell(1).setCellValue(bill.bill_no)
                row.createCell(2).setCellValue(bill.bill_date)
                row.createCell(3).setCellValue(bill.customer.customer_name)
                row.createCell(4).setCellValue(payMode)
                row.createCell(5).setCellValue(bill.rounded_amt)
                row.createCell(6).setCellValue(bill.disc_amt)
                row.createCell(7).setCellValue(bill.received_amt)
            }

            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()

            shareFile(
                context,
                file,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Excel Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = mimeType
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Share Report"))
    }

    fun viewReport(context: Context, fileName: String, mimeType: String) {
        val reportDir = File(context.getExternalFilesDir(null), "reports")
        val file = File(reportDir, fileName)

        if (!file.exists()) {
            Toast.makeText(context, "No report found. Export first!", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Open Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    fun itemExportToPdf(context: Context, bills: List<ItemReport>) {
        try {
            val pdfDir = File(context.getExternalFilesDir(null), "reports")
            if (!pdfDir.exists()) pdfDir.mkdirs()

            val file = File(pdfDir, "ItemSalesReport.pdf")
            val document = Document(PageSize.A4, 10f, 10f, 20f, 20f)
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Title
            val titleFont = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD)
            val title = Paragraph("Item Sales Report\n\n", titleFont)
            title.alignment = Element.ALIGN_CENTER
            document.add(title)

            document.add(Paragraph("Generated on: ${Date()}\n\n"))

            // ✅ Table with columns
            val table = PdfPTable(9)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(3f, 3f, 2f, 2f, 2.5f, 2.5f, 2f, 2.5f, 3f))

            val headerFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
            val headers = listOf(
                "Item Name", "Category", "Rate", "Qty Sold",
                "Total", "Tax Amount", "Cess", "Cess Specific", "Grand Total"
            )

            // Add header row
            headers.forEach {
                val cell = PdfPCell(Phrase(it, headerFont))
                cell.backgroundColor = BaseColor.LIGHT_GRAY
                cell.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(cell)
            }

            // Totals
            var totalRate = 0.0
            var totalQty = 0.0
            var totalAmount = 0.0
            var totalTax = 0.0
            var totalCess = 0.0
            var totalCessSpecific = 0.0
            var totalGrand = 0.0

            // Data rows
            for (bill in bills) {
                table.addCell(bill.menu_item_name)
                table.addCell(bill.item_cat_name)

                val rateCell = PdfPCell(Phrase("₹${bill.rate}"))
                rateCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(rateCell)

                val qtyCell = PdfPCell(Phrase("${bill.qty}"))
                qtyCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(qtyCell)

                val totalCell = PdfPCell(Phrase("₹${bill.total}"))
                totalCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(totalCell)

                val taxCell = PdfPCell(Phrase("₹${bill.tax_amount}"))
                taxCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(taxCell)

                val cessCell = PdfPCell(Phrase("₹${bill.cess}"))
                cessCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(cessCell)

                val cessSpecCell = PdfPCell(Phrase("₹${bill.cess_specific}"))
                cessSpecCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(cessSpecCell)

                val grandCell = PdfPCell(Phrase("₹${bill.grand_total}"))
                grandCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(grandCell)

                // accumulate totals
                totalRate += bill.rate
                totalQty += bill.qty
                totalAmount += bill.total
                totalTax += bill.tax_amount
                totalCess += bill.cess
                totalCessSpecific += bill.cess_specific
                totalGrand += bill.grand_total
            }

            // ✅ Summary Row
            val boldFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)

            val summaryCell = PdfPCell(Phrase("TOTAL", boldFont))
            summaryCell.colspan = 2
            summaryCell.horizontalAlignment = Element.ALIGN_RIGHT
            summaryCell.backgroundColor = BaseColor.YELLOW
            table.addCell(summaryCell)

            val totalRateCell = PdfPCell(Phrase("₹$totalRate", boldFont))
            totalRateCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalRateCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalRateCell)

            val totalQtyCell = PdfPCell(Phrase("$totalQty", boldFont))
            totalQtyCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalQtyCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalQtyCell)

            val totalAmountCell = PdfPCell(Phrase("₹$totalAmount", boldFont))
            totalAmountCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalAmountCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalAmountCell)

            val totalTaxCell = PdfPCell(Phrase("₹$totalTax", boldFont))
            totalTaxCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalTaxCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalTaxCell)

            val totalCessCell = PdfPCell(Phrase("₹$totalCess", boldFont))
            totalCessCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalCessCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalCessCell)

            val totalCessSpecCell = PdfPCell(Phrase("₹$totalCessSpecific", boldFont))
            totalCessSpecCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalCessSpecCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalCessSpecCell)

            val totalGrandCell = PdfPCell(Phrase("₹$totalGrand", boldFont))
            totalGrandCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalGrandCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalGrandCell)

            // Add table to document
            document.add(table)
            document.close()

            shareFile(context, file, "application/pdf")
            Toast.makeText(context, "Item Sales PDF Exported Successfully!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(context, "PDF Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    fun categoryExportToPdf(context: Context, bills: List<CategoryReport>) {
        try {
            val pdfDir = File(context.getExternalFilesDir(null), "reports")
            if (!pdfDir.exists()) pdfDir.mkdirs()

            val file = File(pdfDir, "CategorySalesReport.pdf")
            val document = Document(PageSize.A4, 10f, 10f, 20f, 20f)
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Title
            val titleFont = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD)
            val title = Paragraph("Category Sales Report\n\n", titleFont)
            title.alignment = Element.ALIGN_CENTER
            document.add(title)

            document.add(Paragraph("Generated on: ${Date()}\n\n"))

            // ✅ Table with 3 columns
            val table = PdfPTable(3)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(4f, 2f, 3f))

            val headerFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
            val headers = listOf("Category", "Qty Sold", "Grand Total")

            // Add header row
            headers.forEach {
                val cell = PdfPCell(Phrase(it, headerFont))
                cell.backgroundColor = BaseColor.LIGHT_GRAY
                cell.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(cell)
            }

            // Totals
            var totalQty = 0.0
            var totalGrand = 0.0

            // Data rows
            for (bill in bills) {
                table.addCell(bill.item_cat_name)

                val qtyCell = PdfPCell(Phrase("${bill.qty}"))
                qtyCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(qtyCell)

                val grandCell = PdfPCell(Phrase("₹${bill.grand_total}"))
                grandCell.horizontalAlignment = Element.ALIGN_RIGHT
                table.addCell(grandCell)

                totalQty += bill.qty
                totalGrand += bill.grand_total
            }

            // ✅ Summary Row
            val boldFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)

            val summaryCell = PdfPCell(Phrase("TOTAL", boldFont))
            summaryCell.horizontalAlignment = Element.ALIGN_RIGHT
            summaryCell.backgroundColor = BaseColor.YELLOW
            table.addCell(summaryCell)

            val totalQtyCell = PdfPCell(Phrase("$totalQty", boldFont))
            totalQtyCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalQtyCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalQtyCell)

            val totalGrandCell = PdfPCell(Phrase("₹$totalGrand", boldFont))
            totalGrandCell.horizontalAlignment = Element.ALIGN_RIGHT
            totalGrandCell.backgroundColor = BaseColor.YELLOW
            table.addCell(totalGrandCell)

            // Add table to document
            document.add(table)
            document.close()

            shareFile(context, file, "application/pdf")
            Toast.makeText(context, "Category Sales PDF Exported Successfully!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(context, "PDF Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun itemExportToExcel(context: Context, bills: List<ItemReport>) {
        try {
            val excelDir = File(context.getExternalFilesDir(null), "reports")
            if (!excelDir.exists()) excelDir.mkdirs()

            val file = File(excelDir, "SalesReport.xlsx")
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Sales Report")

            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Menu Item Name")
            headerRow.createCell(1).setCellValue("Item Category")
            headerRow.createCell(2).setCellValue("Rate")
            headerRow.createCell(3).setCellValue("Qty Sold")
            headerRow.createCell(4).setCellValue("Total")
            headerRow.createCell(5).setCellValue("Tax Amount")
            headerRow.createCell(6).setCellValue("Cess")
            headerRow.createCell(7).setCellValue("Cess Specific")
            headerRow.createCell(8).setCellValue("Grand Total")

            bills.forEachIndexed { index, bill ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(bill.menu_item_name)
                row.createCell(1).setCellValue(bill.item_cat_name)
                row.createCell(2).setCellValue(bill.rate)
                row.createCell(3).setCellValue(bill.qty.toString())
                row.createCell(4).setCellValue(bill.total)
                row.createCell(5).setCellValue(bill.tax_amount)
                row.createCell(6).setCellValue(bill.cess)
                row.createCell(7).setCellValue(bill.cess_specific)
                row.createCell(8).setCellValue(bill.grand_total)
            }

            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()

            shareFile(
                context,
                file,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Excel Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun categoryExportToExcel(context: Context, bills: List<CategoryReport>) {
        try {
            val excelDir = File(context.getExternalFilesDir(null), "reports")
            if (!excelDir.exists()) excelDir.mkdirs()

            val file = File(excelDir, "SalesReport.xlsx")
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Sales Report")

            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Item Category Name")
            headerRow.createCell(1).setCellValue("Qty Sold")
            headerRow.createCell(2).setCellValue("Grand Total")

            bills.forEachIndexed { index, bill ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(bill.item_cat_name)
                row.createCell(1).setCellValue(bill.qty.toString())
                row.createCell(2).setCellValue(bill.grand_total)
            }

            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()

            shareFile(
                context,
                file,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Excel Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}
