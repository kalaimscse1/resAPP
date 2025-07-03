package com.warriortech.resb.screens


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScrollableTabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.ui.theme.TextPrimary
import com.warriortech.resb.ui.viewmodel.MenuViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.components.ResponsiveGrid

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    isTakeaway: String, // "TABLE", "TAKEAWAY", "DELIVERY"
    tableStatId: Boolean, // True if it's a table order and tableId is relevant for fetching existing order
    tableId: Long,       // Actual table ID for table orders, or a placeholder for others
    onBackPressed: () -> Unit,
    onOrderPlaced: () -> Unit,
    onBillPlaced: (orderDetailsResponse: List<TblOrderDetailsResponse>) -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
    drawerState: DrawerState
) {
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()
    val orderState by viewModel.orderState.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val newselectedItems by viewModel.newselectedItems.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    // No longer lateinit, it will be derived inside the Success state
    // lateinit var filteredMenuItems:List<MenuItem>
    val tableStatusFromVM by viewModel.tableStatus.collectAsStateWithLifecycle() // Assuming tableStatus is part of the table info

    var showConfirmDialog by remember { mutableStateOf(false) }


    // Determine the effective status string for pricing and display
    val effectiveStatus = remember(isTakeaway, tableStatusFromVM) {
        when (isTakeaway) {
            "TABLE" -> tableStatusFromVM // Use status from ViewModel (e.g., "AC", "Non-AC")
            "TAKEAWAY" -> "TAKEAWAY"
            "DELIVERY" -> "DELIVERY"
            else -> tableStatusFromVM // Fallback or default
        }
    }

    LaunchedEffect(key1 = isTakeaway, key2 = tableId, key3 = tableStatId) {
        // This effect will run when these key parameters change, or on initial composition.
        // viewModel.setTableId(tableId) // This seems to be more for *placing* the order.
        // The ViewModel should know the tableId for *loading* from initializeScreen.
        viewModel.setTableId(tableId)
        val isTableOrderScenario = isTakeaway == "TABLE" && tableStatId
        viewModel.initializeScreen(isTableOrder = isTableOrderScenario, currentTableId = tableId)

    }

    LaunchedEffect(orderState) {
        when (val currentOrderState = orderState) { // Use a stable val
            is MenuViewModel.OrderUiState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Order placed successfully and KOT sent to kitchen")
                    onOrderPlaced() // This should navigate away or reset the screen
                }
            }
            is MenuViewModel.OrderUiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(currentOrderState.message)
                }
            }
            else -> { /* do nothing for Loading or Idle */ }
        }
    }

    if (showConfirmDialog) {
        OrderConfirmationDialog(
            selectedItems = if (viewModel.isExistingOrderLoaded.value && newselectedItems.isNotEmpty()) newselectedItems else selectedItems,
            totalAmount = viewModel.getOrderTotal(effectiveStatus.toString()), // Use effectiveStatus
            onConfirm = {
                // The tableId passed here is the one this screen was launched with.
                // For takeaway/delivery, it might be a specific ID like 0, 1, or 2 as per your existing logic.
                // For table orders, it's the actual tableId.
                viewModel.placeOrder(tableId, effectiveStatus) // Use effectiveStatus
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false },
            tableStatus = effectiveStatus.toString() // Use effectiveStatus
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu Selection") }, // Indicate table
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
//                    IconButton(onClick = {onBackPressed}) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
                },
                actions = {
                    androidx.compose.material.IconButton(onClick = {
                        viewModel.clearOrder()
                    }) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = "Clear Cart"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val itemCount = if(viewModel.isExistingOrderLoaded.value) newselectedItems.values.sum()+selectedItems.values.sum() else selectedItems.values.sum()
                    val totalAmount = viewModel.getOrderTotal(effectiveStatus.toString()) // Use effectiveStatus

                    Column {
                        Text(
                            text = "Items: $itemCount",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total: ₹${String.format("%.2f", totalAmount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (viewModel.isExistingOrderLoaded.value) {
                        MobileOptimizedButton(
                            onClick = {
                                onBillPlaced(viewModel.orderDetailsResponse.value) },
                            enabled = selectedItems.isNotEmpty() && orderState !is MenuViewModel.OrderUiState.Loading,
                            text = "Bill",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    MobileOptimizedButton(
                        onClick = { showConfirmDialog = true },
                        enabled = selectedItems.isNotEmpty() && orderState !is MenuViewModel.OrderUiState.Loading,
                        text = if (isTakeaway == "TABLE" && viewModel.isExistingOrderLoaded.value) "Update Order" else "Place Order",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(data.visuals.message)
                }
            }
        },
        // ... (snackbarHost and floatingActionButton remain similar, ensure floatingActionButton also uses effectiveStatus if needed for display)
        floatingActionButton = {
            if (selectedItems.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showConfirmDialog = true }
                ) {
                    // Consider if the FAB text needs to change based on new vs. existing order
                    Text("${if(viewModel.isExistingOrderLoaded.value) newselectedItems.values.sum()+selectedItems.values.sum() else selectedItems.values.sum()}")
                }
            }
        }
    ) { paddingValues ->

        when (val currentMenuState = menuState) { // Use stable val
            is MenuViewModel.MenuUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MenuViewModel.MenuUiState.Success -> {
                val menuItems = currentMenuState.menuItems
                val filteredMenuItems = if (selectedCategory != null) { // Make sure selectedCategory is handled safely
                    menuItems.filter { it.item_cat_name == selectedCategory }
                } else {
                    menuItems
                }

                if (menuItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No menu items available")
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(color = Color.White)
                    ) {
                        // ... (Categories TabRow)
                        if (categories.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0), // Ensure index is not -1
                                backgroundColor = Color.White,
                                contentColor = TextPrimary
                            ) {
                                categories.forEachIndexed { index, category ->
                                    Tab(
                                        selected = selectedCategory == category,
                                        onClick = { viewModel.selectedCategory.value = category }, // Assuming a selectCategory method in VM
                                        text = { androidx.compose.material.Text(category) }
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 2.dp)
                                .background(color = Color.White),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp) // Add horizontal for consistency
                        ) {
                            item { Spacer(modifier = Modifier.padding(top = 5.dp)) }

                            items(filteredMenuItems, key = { it.menu_item_id }) { menuItem -> // Add a key for better performance
                                MenuItemCard(
                                    menuItem = menuItem,
                                    quantity = if (viewModel.isExistingOrderLoaded.value) newselectedItems[menuItem] ?: 0 else selectedItems[menuItem] ?: 0,
                                    onAddItem = { viewModel.addItemToOrder(menuItem) },
                                    onRemoveItem = { viewModel.removeItemFromOrder(menuItem) },
                                    tableStatus = effectiveStatus.toString() // Use effectiveStatus
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) } // For bottom bar
                        }
                    }
                }
            }


            is MenuViewModel.MenuUiState.Error -> {
                val errorMessage = (menuState as MenuViewModel.MenuUiState.Error).message

                LaunchedEffect(errorMessage) {
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Failed to load menu items")
                }
            }
        }

        // Show loading overlay when placing an order
        if (orderState is MenuViewModel.OrderUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Processing order...")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    tableStatus: String
) {

    MobileOptimizedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = menuItem.menu_item_name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (menuItem.menu_item_name_tamil.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = menuItem.menu_item_name_tamil,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (tableStatus=="AC")"₹${String.format("%.2f", menuItem.ac_rate)}" else if(tableStatus=="TAKEAWAY"||tableStatus=="DELIVERY") "₹${String.format("%.2f", menuItem.parcel_rate)}" else "₹${String.format("%.2f", menuItem.rate)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (menuItem.is_available=="YES") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onRemoveItem
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Remove",
                                tint = Color.Red
                            )
                        }

                        if (quantity > 0) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = quantity.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(36.dp))
                        }

                        IconButton(
                            onClick = onAddItem
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.Green
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Not Available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            AnimatedVisibility(visible = quantity > 0) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (tableStatus=="AC")"$quantity ×₹${String.format("%.2f", menuItem.ac_rate)}" else if(tableStatus=="TAKEAWAY"||tableStatus=="DELIVERY") "$quantity ×₹${String.format("%.2f", menuItem.parcel_rate)}" else "$quantity ×₹${String.format("%.2f", menuItem.rate)}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = if (tableStatus=="AC")"₹${String.format("%.2f", quantity * menuItem.ac_rate)}" else if(tableStatus=="TAKEAWAY"||tableStatus=="DELIVERY") "₹${String.format("%.2f", quantity * menuItem.parcel_rate)}" else "₹${String.format("%.2f", quantity * menuItem.rate)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun OrderConfirmationDialog(
    selectedItems: Map<MenuItem, Int>,
    totalAmount: Double,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    tableStatus: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            MobileOptimizedButton(
                onClick = onConfirm,
                text = "Confirm",
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            MobileOptimizedButton(
                onClick = onDismiss,
                text = "Cancel",
                modifier = Modifier.fillMaxWidth()
            )
        },
        title = {
            Text("Confirm Order", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column {
                Text("Review your order before placing it:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))

                selectedItems.forEach { (item, qty) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.menu_item_name} × $qty", style = MaterialTheme.typography.bodySmall)

                        val rate = when (tableStatus.uppercase()) {
                            "AC" -> item.ac_rate
                            "TAKEAWAY", "DELIVERY" -> item.parcel_rate
                            else -> item.rate
                        }

                        Text("₹%.2f".format(rate * qty), style = MaterialTheme.typography.bodySmall)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("₹%.2f".format(totalAmount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "A KOT will be generated and sent to the kitchen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )
}