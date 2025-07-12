
package com.warriortech.resb.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScrollableTabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.theme.TextPrimary
import com.warriortech.resb.ui.viewmodel.CounterViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    onBackPressed: () -> Unit,
    onProceedToBilling: (Map<MenuItem, Int>) -> Unit,
    viewModel: CounterViewModel = hiltViewModel(),
    drawerState: DrawerState,
    counterId: Long? = null
) {
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
    }
    
    LaunchedEffect(counterId) {
        if (counterId != null) {
            // Load counter information by ID
            // This would typically come from a repository call
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val currentCounter by viewModel.currentCounter.collectAsStateWithLifecycle()
                    Text(
                        if (currentCounter != null) 
                            "Counter Billing - ${currentCounter!!.code}" 
                        else 
                            "Counter Billing"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.clearOrder()
                    }) {
                        Icon(
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
                    val totalItemCount = selectedItems.values.sum()
                    val totalAmount = viewModel.getOrderTotal()

                    Column {
                        Text(
                            text = "Total Items: $totalItemCount",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total: ₹${String.format("%.2f", totalAmount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    MobileOptimizedButton(
                        onClick = { onProceedToBilling(selectedItems) },
                        enabled = selectedItems.isNotEmpty(),
                        text = "Proceed to Bill",
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
        floatingActionButton = {
            if (selectedItems.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onProceedToBilling(selectedItems) }
                ) {
                    val itemCount = selectedItems.values.sum()
                    Text("$itemCount")
                }
            }
        }
    ) { paddingValues ->

        when (val currentMenuState = menuState) {
            is CounterViewModel.MenuUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CounterViewModel.MenuUiState.Success -> {
                val menuItems = currentMenuState.menuItems
                val filteredMenuItems = if (selectedCategory != null) {
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
                        // Categories TabRow
                        if (categories.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                                backgroundColor = Color.White,
                                contentColor = TextPrimary
                            ) {
                                categories.forEachIndexed { index, category ->
                                    Tab(
                                        selected = selectedCategory == category,
                                        onClick = { viewModel.selectedCategory.value = category },
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
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            item { Spacer(modifier = Modifier.padding(top = 5.dp)) }

                            items(filteredMenuItems, key = { it.menu_item_id }) { menuItem ->
                                CounterMenuItemCard(
                                    menuItem = menuItem,
                                    quantity = selectedItems[menuItem] ?: 0,
                                    onAddItem = { viewModel.addItemToOrder(menuItem) },
                                    onRemoveItem = { viewModel.removeItemFromOrder(menuItem) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }

            is CounterViewModel.MenuUiState.Error -> {
                val errorMessage = (menuState as CounterViewModel.MenuUiState.Error).message

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
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CounterMenuItemCard(
    menuItem: MenuItem,
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
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
                        text = "₹${String.format("%.2f", menuItem.rate)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (menuItem.is_available == "YES") {
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
                            text = "$quantity × ₹${String.format("%.2f", menuItem.rate)}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "₹${String.format("%.2f", quantity * menuItem.rate)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
