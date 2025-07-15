package com.warriortech.resb.screens // Or your preferred screen package

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.viewmodel.BillingViewModel
import com.warriortech.resb.ui.viewmodel.BillingPaymentUiState
import java.text.NumberFormat
import java.util.Locale
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.ui.theme.GradientStart

//fun Double.format(digits: Int) = "%.${digits}f".format(this)


@Composable
fun KotSelectionDialog(
    orderDetails: List<TblOrderDetailsResponse>,
    onKotSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val kotNumbers = orderDetails.map { it.kot_number }.distinct().sorted()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            MobileOptimizedButton(
                onClick = onDismiss,
                text = "Cancel",
                modifier = Modifier.fillMaxWidth()
            )
        },
        title = {
            Text("Select KOT Number", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column {
                Text("Choose a KOT to view its items:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    item {
                        MobileOptimizedButton(
                            onClick = { 
                                onKotSelected(-1) // -1 means show all items
                            },
                            text = "Show All Items",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(kotNumbers) { kotNumber ->
                        MobileOptimizedButton(
                            onClick = { onKotSelected(kotNumber) },
                            text = "KOT #$kotNumber",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    navController: NavHostController,
    viewModel: BillingViewModel = hiltViewModel(),
    orderDetailsResponse: List<TblOrderDetailsResponse>? = null,
    orderMasterId: Long? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedKotNumber by remember { mutableStateOf<Int?>(null) }
    var showKotSelectionDialog by remember { mutableStateOf(false) }

    // Load billing details from TblOrderDetailsResponse or initial items
    LaunchedEffect(key1 = orderDetailsResponse, key2 = orderMasterId) {
        Log.d("BillingScreen", "Loading billing details for orderMasterId: $orderMasterId,$orderDetailsResponse")
        when {
            orderDetailsResponse != null && orderMasterId != null -> {
                viewModel.setBillingDetailsFromOrderResponse(orderDetailsResponse, orderMasterId,)
            }
        }
    }

    // KOT Selection Dialog
    if (showKotSelectionDialog && orderDetailsResponse != null) {
        KotSelectionDialog(
            orderDetails = orderDetailsResponse,
            onKotSelected = { kotNumber ->
                selectedKotNumber = kotNumber
                showKotSelectionDialog = false
                viewModel.filterByKotNumber(kotNumber)
            },
            onDismiss = { showKotSelectionDialog = false }
        )
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (selectedKotNumber != null) "Bill Summary - KOT #$selectedKotNumber" 
                        else "Bill Summary"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (orderDetailsResponse != null) {
                        IconButton(onClick = { showKotSelectionDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Select KOT"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        },
        bottomBar = {
            BillingBottomBar(uiState = uiState,orderMasterId = orderMasterId) {
                // Navigate to Payment Screen, passing necessary info
                // Option 1: Pass total amount via route (simple)
                // navController.navigate("payment/${uiState.totalAmount}")
                // Option 2: ViewModel holds state, PaymentScreen also uses it (better for complex state)
                navController.navigate("payment_screen/${uiState.totalAmount}")
            }
        }
    ) { paddingValues ->
        BillingContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onUpdateTax = { viewModel.updateTaxPercentage(it) },
            onUpdateDiscount = { viewModel.updateDiscountFlat(it) }
        )
    }
}

@Composable
fun BillingContent(
    modifier: Modifier = Modifier,
    uiState: BillingPaymentUiState,
    onUpdateTax: (Double) -> Unit,
    onUpdateDiscount: (Double) -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Display Billed Items
        if (uiState.billedItems.isNotEmpty()) {
            item {
                Text("Items", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            items(uiState.billedItems.toList()) { (menuItem, quantity) ->
                BilledItemRow(
                    menuItem = menuItem,
                    quantity = quantity,
                    tableStatus = uiState.tableStatus,
                    currencyFormatter = currencyFormatter
                )
            }
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
        } else {
            item {
                Text(
                    "No items in the bill.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Subtotal
        item {
            BillingSummaryRow(
                label = "Subtotal",
                amount = uiState.subtotal,
                currencyFormatter = currencyFormatter
            )
        }

        // Tax (Allow editing if needed)
        item {
            EditableBillingRow(
                label = "Tax Amount",
                amount = uiState.taxAmount,
                currencyFormatter = currencyFormatter
            )
        }
        if (uiState.cessAmount>0) {
            item {
                EditableBillingRow(
                    label = "Cess Amount",
                    amount = uiState.cessAmount,
                    currencyFormatter = currencyFormatter
                )
            }
        }
        if (uiState.cessSpecific>0) {
            item {
                EditableBillingRow(
                    label = "Cess Specific",
                    amount = uiState.cessSpecific,
                    currencyFormatter = currencyFormatter
                )
            }
        }

        // Discount (Allow editing)
        item {
            EditableBillingRow(
                label = "Discount",
                amount = uiState.discountFlat,
                currencyFormatter = currencyFormatter
            )
        }

        // Total
        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            BillingSummaryRow(
                label = "Total Amount",
                amount = uiState.totalAmount,
                currencyFormatter = currencyFormatter,
                isTotal = true
            )
        }
    }
}

@Composable
fun BilledItemRow(
    menuItem: MenuItem,
    quantity: Int,
    tableStatus: String,
    currencyFormatter: NumberFormat,
    kotNumber: Int? = null
) {
    val itemPrice = when (tableStatus) {
        "AC" -> menuItem.ac_rate
        "TAKEAWAY", "DELIVERY" -> menuItem.parcel_rate
        else -> menuItem.rate
    }
    val itemTotal = itemPrice * quantity

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("${menuItem.menu_item_name} x $quantity")
            if (kotNumber != null) {
                Text(
                    "KOT #$kotNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Text(currencyFormatter.format(itemTotal))
    }
}

@Composable
fun OrderDetailRow(
    orderDetail: TblOrderDetailsResponse,
    currencyFormatter: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("${orderDetail.menuItem.menu_item_name} x ${orderDetail.qty}")
            Text(
                "KOT #${orderDetail.kot_number}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Text(currencyFormatter.format(orderDetail.total))
    }
}

@Composable
fun BillingSummaryRow(
    label: String,
    amount: Double,
    currencyFormatter: NumberFormat,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            currencyFormatter.format(amount),
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableBillingRow(
    label: String,
    amount: Double,
    currencyFormatter: NumberFormat
) {
//    var textValue by remember(currentValue) { mutableStateOf(currentValue.format(if (isCurrencyInput) 2 else 1)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            currencyFormatter.format(amount),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun BillingBottomBar(
    uiState: BillingPaymentUiState,
    orderMasterId: Long? = null,
    onProceedToPayment: () -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Due", style = MaterialTheme.typography.labelMedium)
                Text(
                    currencyFormatter.format(uiState.totalAmount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MobileOptimizedButton(
                    onClick = {
                        if (orderMasterId != null) {
                            // Handle payment processing
                            onProceedToPayment()
                        }
                    },
                    text = "Proceed to Payment",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Helper to format doubles
fun Double.format(digits: Int) = "%.${digits}f".format(this)