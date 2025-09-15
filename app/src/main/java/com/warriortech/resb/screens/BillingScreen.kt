package com.warriortech.resb.screens

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
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
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.viewmodel.BillingViewModel
import com.warriortech.resb.ui.viewmodel.BillingPaymentUiState
import java.text.NumberFormat
import java.util.Locale
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.ui.components.ModernDivider
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.util.CurrencySettings


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
    orderMasterId: String? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedKotNumber by remember { mutableStateOf<Int?>(null) }
    var showKotSelectionDialog by remember { mutableStateOf(false) }

    // Load billing details from TblOrderDetailsResponse or initial items
    LaunchedEffect(key1 = orderDetailsResponse, key2 = orderMasterId) {
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
                        else "Bill Summary", color = SurfaceLight
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                            tint = SurfaceLight)
                    }
                },
                actions = {
                    if (orderDetailsResponse != null) {
                        IconButton(onClick = { showKotSelectionDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Select KOT",
                                tint = SurfaceLight
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            BillingBottomBar(uiState = uiState,orderMasterId = orderMasterId) {
                navController.navigate("payment_screen/${uiState.totalAmount}/${uiState.orderMasterId}") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) { paddingValues ->
        BillingContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onUpdateQuantity = { menuItem, newQuantity -> 
                viewModel.updateItemQuantity(menuItem, newQuantity)
            },
            onRemoveItem = { menuItem ->
                viewModel.removeItem(menuItem)
            }
        )
    }
}

@Composable
fun BillingContent(
    modifier: Modifier = Modifier,
    uiState: BillingPaymentUiState,
    onUpdateQuantity: (TblMenuItemResponse, Int) -> Unit,
    onRemoveItem: (TblMenuItemResponse) -> Unit
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
                    currencyFormatter = currencyFormatter,
                    onQuantityChange = { newQuantity ->
                        onUpdateQuantity(menuItem, newQuantity)
                    },
                    onRemoveItem = {
                        onRemoveItem(menuItem)
                    }
                )
            }
            item { ModernDivider(modifier = Modifier.padding(vertical = 8.dp)) }
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
            EditableBillingRow(
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

        item {
            EditableBillingRow(
                label = "Other Charges",
                amount = uiState.otherChrages,
                currencyFormatter = currencyFormatter
            )
        }

        // Total
        item {
            ModernDivider(modifier = Modifier.padding(vertical = 8.dp))
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
    menuItem: TblMenuItemResponse,
    quantity: Int,
    tableStatus: String,
    currencyFormatter: NumberFormat,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = menuItem.menu_item_name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (tableStatus == "AC") {
                    Text(
                        text = "Table Service",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = CurrencySettings.format(menuItem.actual_rate * quantity),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                    IconButton(
//                        onClick = {
//                            if (quantity > 1) {
//                                onQuantityChange(quantity - 1)
//                            }
//                        },
//                        modifier = Modifier.size(32.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Remove,
//                            contentDescription = "Decrease quantity"
//                        )
//                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = TextAlign.Center
                    )

//                    IconButton(
//                        onClick = { onQuantityChange(quantity + 1) },
//                        modifier = Modifier.size(32.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = "Increase quantity"
//                        )
//                    }
                }

                // Remove item button
//                IconButton(
//                    onClick = onRemoveItem,
//                    modifier = Modifier.size(32.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Remove item",
//                        tint = MaterialTheme.colorScheme.error
//                    )
//                }
            }
        }
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
            CurrencySettings.format(amount),
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            CurrencySettings.formatPlain(amount),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun BillingBottomBar(
    uiState: BillingPaymentUiState,
    orderMasterId: String? = null,
    onProceedToPayment: () -> Unit
) {
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
                    CurrencySettings.format(uiState.totalAmount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = 25.dp))
            Column {
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