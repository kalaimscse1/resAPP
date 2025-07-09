
package com.warriortech.resb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.warriortech.resb.model.*
import com.warriortech.resb.ui.viewmodel.TemplateViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePreviewScreen(
    navController: NavController,
    templateId: String,
    viewModel: TemplateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Find the template by ID
    val template = uiState.templates.find { it.id == templateId }
    
    LaunchedEffect(templateId) {
        if (template == null) {
            viewModel.loadTemplates()
        }
    }
    
    if (template == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview: ${template.name}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // TODO: Implement print functionality
                        }
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Preview container with background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                when (template.type) {
                    ReceiptType.KOT -> KotPreview(template)
                    ReceiptType.BILL -> BillPreview(template)
                }
            }
            
            // Template info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Template Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Type:", fontWeight = FontWeight.Medium)
                        Text(template.type.name)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Paper Size:", fontWeight = FontWeight.Medium)
                        Text(template.paperSettings.paperSize.displayName)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Default:", fontWeight = FontWeight.Medium)
                        Text(if (template.isDefault) "Yes" else "No")
                    }
                }
            }
        }
    }
}

@Composable
fun KotPreview(template: ReceiptTemplate) {
    val sampleKotData = createSampleKotData()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(template.paperSettings.margins.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = when (template.headerSettings.alignment) {
            TextAlignment.LEFT -> Alignment.Start
            TextAlignment.CENTER -> Alignment.CenterHorizontally
            TextAlignment.RIGHT -> Alignment.End
        }
    ) {
        // Header
        Text(
            text = template.headerSettings.text,
            fontSize = template.headerSettings.fontSize.sp,
            fontWeight = when (template.headerSettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            },
            textAlign = when (template.headerSettings.alignment) {
                TextAlignment.LEFT -> TextAlign.Start
                TextAlignment.CENTER -> TextAlign.Center
                TextAlignment.RIGHT -> TextAlign.End
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // KOT Details
        Text(
            text = "KOT #: ${sampleKotData.kotNumber}",
            fontSize = template.bodySettings.fontSize.sp,
            fontWeight = when (template.bodySettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            }
        )
        
        Text(
            text = "Table: ${sampleKotData.tableNumber}",
            fontSize = template.bodySettings.fontSize.sp,
            fontWeight = when (template.bodySettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            }
        )
        
        Text(
            text = "Time: ${sampleKotData.timestamp}",
            fontSize = template.bodySettings.fontSize.sp,
            fontWeight = when (template.bodySettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Items
        sampleKotData.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.quantity}x ${item.itemName}",
                    fontSize = template.bodySettings.fontSize.sp,
                    fontWeight = when (template.bodySettings.fontWeight) {
                        FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                        FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                        FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
                    }
                )
            }
            if (item.notes.isNotEmpty()) {
                Text(
                    text = "  Note: ${item.notes}",
                    fontSize = (template.bodySettings.fontSize - 2).sp,
                    fontWeight = when (template.bodySettings.fontWeight) {
                        FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                        FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                        FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Footer
        if (template.footerSettings.text.isNotEmpty()) {
            Text(
                text = template.footerSettings.text,
                fontSize = template.footerSettings.fontSize.sp,
                fontWeight = when (template.footerSettings.fontWeight) {
                    FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                    FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                    FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
                },
                textAlign = when (template.footerSettings.alignment) {
                    TextAlignment.LEFT -> TextAlign.Start
                    TextAlignment.CENTER -> TextAlign.Center
                    TextAlignment.RIGHT -> TextAlign.End
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BillPreview(template: ReceiptTemplate) {
    val sampleBillData = createSampleBillData()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(template.paperSettings.margins.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = when (template.headerSettings.alignment) {
            TextAlignment.LEFT -> Alignment.Start
            TextAlignment.CENTER -> Alignment.CenterHorizontally
            TextAlignment.RIGHT -> Alignment.End
        }
    ) {
        // Header
        Text(
            text = template.headerSettings.text,
            fontSize = template.headerSettings.fontSize.sp,
            fontWeight = when (template.headerSettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            },
            textAlign = when (template.headerSettings.alignment) {
                TextAlignment.LEFT -> TextAlign.Start
                TextAlignment.CENTER -> TextAlign.Center
                TextAlignment.RIGHT -> TextAlign.End
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bill Details
        Text(
            text = "Bill #: ${sampleBillData.billNumber}",
            fontSize = template.bodySettings.fontSize.sp,
            fontWeight = when (template.bodySettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            }
        )
        
        Text(
            text = "Date: ${sampleBillData.date}",
            fontSize = template.bodySettings.fontSize.sp,
            fontWeight = when (template.bodySettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            }
        )
        
        Text(
            text = "Table: ${sampleBillData.tableNumber}",
            fontSize = template.bodySettings.fontSize.sp,
            fontWeight = when (template.bodySettings.fontWeight) {
                FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Items header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Item",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "Qty",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "Price",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "Total",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
        
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        
        // Items
        sampleBillData.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.itemName,
                    fontSize = template.bodySettings.fontSize.sp,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "${item.quantity}",
                    fontSize = template.bodySettings.fontSize.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "₹${item.price}",
                    fontSize = template.bodySettings.fontSize.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "₹${item.total}",
                    fontSize = template.bodySettings.fontSize.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Totals
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Subtotal:",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "₹${sampleBillData.subtotal}",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tax (${sampleBillData.taxPercentage}%):",
                fontSize = template.bodySettings.fontSize.sp
            )
            Text(
                text = "₹${sampleBillData.taxAmount}",
                fontSize = template.bodySettings.fontSize.sp
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total:",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "₹${sampleBillData.total}",
                fontSize = template.bodySettings.fontSize.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Footer
        if (template.footerSettings.text.isNotEmpty()) {
            Text(
                text = template.footerSettings.text,
                fontSize = template.footerSettings.fontSize.sp,
                fontWeight = when (template.footerSettings.fontWeight) {
                    FontWeight.Normal -> androidx.compose.ui.text.font.FontWeight.Normal
                    FontWeight.Bold -> androidx.compose.ui.text.font.FontWeight.Bold
                    FontWeight.Light -> androidx.compose.ui.text.font.FontWeight.Light
                },
                textAlign = when (template.footerSettings.alignment) {
                    TextAlignment.LEFT -> TextAlign.Start
                    TextAlignment.CENTER -> TextAlign.Center
                    TextAlignment.RIGHT -> TextAlign.End
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Sample data functions
fun createSampleKotData(): KotData {
    return KotData(
        kotNumber = "KOT001",
        tableNumber = "Table 5",
        timestamp = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date()),
        items = listOf(
            KotItem("Margherita Pizza", 2, "Extra cheese"),
            KotItem("Caesar Salad", 1, "No croutons"),
            KotItem("Coca Cola", 2, "")
        )
    )
}

fun createSampleBillData(): Bill {
    return Bill(
        billNumber = "BILL001",
        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
        tableNumber = "Table 5",
        items = listOf(
            BillItem("Margherita Pizza", 2, 350.0, 700.0),
            BillItem("Caesar Salad", 1, 180.0, 180.0),
            BillItem("Coca Cola", 2, 50.0, 100.0)
        ),
        subtotal = 980.0,
        taxPercentage = 18.0,
        taxAmount = 176.4,
        total = 1156.4
    )
}

// Data classes for sample data
data class KotData(
    val kotNumber: String,
    val tableNumber: String,
    val timestamp: String,
    val items: List<KotItem>
)

data class KotItem(
    val itemName: String,
    val quantity: Int,
    val notes: String
)

data class BillItem(
    val itemName: String,
    val quantity: Int,
    val price: Double,
    val total: Double
)
