package com.warriortech.resb.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.model.TblBillingResponse
import com.warriortech.resb.ui.components.ModernDivider
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.PaidBillsUiState
import com.warriortech.resb.ui.viewmodel.PaidBillsViewModel
import com.warriortech.resb.util.CurrencySettings
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaidBillsScreen(
    navController: NavHostController,
    viewModel: PaidBillsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedBill by viewModel.selectedBill.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var fromDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var toDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var billToDelete by remember { mutableStateOf<TblBillingResponse?>(null) }

    // Load paid bills on initial load
    LaunchedEffect(Unit) {
        viewModel.loadPaidBills(fromDate, toDate)
    }

    // Show error messages
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PaidBillsUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearError()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paid Bills") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date Range")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SurfaceLight)
        ) {
            // Date range display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "From: $fromDate",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "To: $toDate",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = { viewModel.loadPaidBills(fromDate, toDate) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Refresh", color = Color.White)
                    }
                }
            }

            // Content based on UI state
            when (val state = uiState) {
                is PaidBillsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }

                is PaidBillsUiState.Success -> {
                    if (state.bills.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No paid bills found for the selected date range",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.bills) { bill ->
                                PaidBillItem(
                                    bill = bill,
                                    onEditClick = {
                                        // Navigate to edit screen or show edit dialog
                                        viewModel.selectBill(bill)
                                        navController.navigate("bill_edit/${bill.bill_no}")
                                    },
                                    onDeleteClick = {
                                        billToDelete = bill
                                        showDeleteDialog = true
                                    },
                                    onPrintClick = { bill_no ->
                                        viewModel.printBill( bill_no)
                                    },
                                    onWhatsappClick = { bill_no ->
                                        viewModel.sendBillViaWhatsApp()
                                    }
                                )
                            }
                        }
                    }
                }

                is PaidBillsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is PaidBillsUiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select date range and tap refresh to load bills",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && billToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    billToDelete = null
                },
                title = { Text("Delete Bill") },
                text = { Text("Are you sure you want to delete bill ${billToDelete?.bill_no}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            billToDelete?.let { bill ->
                                viewModel.deleteBill(bill.bill_no)
                            }
                            showDeleteDialog = false
                            billToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        billToDelete = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PaidBillItem(
    bill: TblBillingResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPrintClick:(bill_no:String)-> Unit,
    onWhatsappClick:(bill_no:String)-> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bill #${bill.bill_no}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${bill.bill_date} ${bill.bill_create_time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Customer: ${bill.customer.customer_name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onPrintClick(bill.bill_no) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Print,
                            contentDescription = "Print",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { /* View details action */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Whatsapp,
                            contentDescription = "Whatsapp",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onEditClick() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = PrimaryGreen
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            ModernDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Order Amount: ${CurrencySettings.format(bill.order_amt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Discount: ${CurrencySettings.format(bill.disc_amt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Tax: ${CurrencySettings.format(bill.tax_amt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Grand Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = CurrencySettings.format(bill.grand_total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }

            if (bill.note.isNotEmpty()) {
                Text(
                    text = "Note: ${bill.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
