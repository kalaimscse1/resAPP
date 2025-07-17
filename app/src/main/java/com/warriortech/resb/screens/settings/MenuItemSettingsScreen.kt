package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.viewmodel.MenuItemSettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemSettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: MenuItemSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMenuItem by remember { mutableStateOf<MenuItem?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MenuCategory Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Area")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Menu Item")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is MenuItemSettingsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MenuItemSettingsUiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items((uiState as MenuItemSettingsUiState.Success).menuItems) { menuItem ->
                            MenuItemCard(
                                menuItem = menuItem,
                                onEdit = { editingMenuItem = menuItem },
                                onDelete = {
                                    scope.launch {
                                        viewModel.deleteMenuItem(menuItem.menu_item_id.toInt())
                                    }
                                }
                            )
                        }
                    }
                }

                is MenuItemSettingsUiState.Error -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${(uiState as MenuItemSettingsUiState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.loadMenuItems() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        if (showAddDialog || editingMenuItem != null) {
            MenuItemDialog(
                menuItem = editingMenuItem,
                onDismiss = {
                    showAddDialog = false
                    editingMenuItem = null
                },
                onSave = { menuItem ->
                    scope.launch {
                        if (editingMenuItem != null) {
                            viewModel.updateMenuItem(menuItem)
                        } else {
                            viewModel.addMenuItem(menuItem)
                        }
                        showAddDialog = false
                        editingMenuItem = null
                    }
                }
            )
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    MobileOptimizedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menuItem.menu_item_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${menuItem.rate}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (menuItem.menu_item_name_tamil.isNotEmpty()) {
                    Text(
                        text = menuItem.menu_item_name_tamil,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun MenuItemDialog(
    menuItem: MenuItem?,
    onDismiss: () -> Unit,
    onSave: (MenuItem) -> Unit
) {
    var name by remember { mutableStateOf(menuItem?.menu_item_name ?: "") }
    var nameTamil by remember { mutableStateOf(menuItem?.menu_item_name_tamil ?: "") }
//    var description by remember { mutableStateOf(menuItem?.description ?: "") }
    var rate by remember { mutableStateOf(menuItem?.rate?.toString() ?: "") }
    var acRate by remember { mutableStateOf(menuItem?.ac_rate?.toString() ?: "") }
    var parcelRate by remember { mutableStateOf(menuItem?.parcel_rate?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (menuItem != null) "Edit Menu Item" else "Add Menu Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nameTamil,
                    onValueChange = { nameTamil = it },
                    label = { Text("Name (Tamil)") },
                    modifier = Modifier.fillMaxWidth()
                )
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedTextField(
//                    value = description,
//                    onValueChange = { description = it },
//                    label = { Text("Description") },
//                    modifier = Modifier.fillMaxWidth()
//                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Rate") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = acRate,
                    onValueChange = { acRate = it },
                    label = { Text("AC Rate") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = parcelRate,
                    onValueChange = { parcelRate = it },
                    label = { Text("Parcel Rate") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newMenuItem = MenuItem(
                        menu_item_id = menuItem?.menu_item_id ?: 0,
                        menu_item_name = name,
                        menu_item_name_tamil = nameTamil,
                        item_cat_id = menuItem?.item_cat_id ?: 1,
                        item_cat_name = menuItem?.item_cat_name ?: "",
                        rate = rate.toDoubleOrNull() ?: 0.0,
                        ac_rate = acRate.toDoubleOrNull() ?: 0.0,
                        parcel_rate = parcelRate.toDoubleOrNull() ?: 0.0,
                        parcel_charge = menuItem?.parcel_charge?: 0.0,
                        tax_id = menuItem?.tax_id ?: 1,
                        tax_name = menuItem?.tax_name ?: "",
                        tax_percentage = menuItem?.tax_percentage ?: 0.0.toString(),
                        cess_per = TODO(),
                        cess_specific = TODO(),
                        kitchen_cat_id = TODO(),
                        kitchen_cat_name = TODO(),
                        stock_maintain = TODO(),
                        rate_lock = TODO(),
                        unit_id = TODO(),
                        unit_name = TODO(),
                        min_stock = TODO(),
                        hsn_code = TODO(),
                        order_by = TODO(),
                        is_inventory = TODO(),
                        is_raw = TODO(),
                        is_available = TODO(),
                        image = TODO(),
                        qty = TODO()
                    )
                    onSave(newMenuItem)
                },
                enabled = name.isNotBlank() && rate.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

sealed class MenuItemSettingsUiState {
    object Loading : MenuItemSettingsUiState()
    data class Success(val menuItems: List<MenuItem>) : MenuItemSettingsUiState()
    data class Error(val message: String) : MenuItemSettingsUiState()
}
