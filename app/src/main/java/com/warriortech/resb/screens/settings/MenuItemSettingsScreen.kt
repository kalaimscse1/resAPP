
package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.warriortech.resb.ui.viewmodel.MenuItemSettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun MenuItemSettingsScreen(
    viewModel: MenuItemSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMenuItem by remember { mutableStateOf<MenuItem?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                    items(uiState.menuItems) { menuItem ->
                        MenuItemCard(
                            menuItem = menuItem,
                            onEdit = { editingMenuItem = menuItem },
                            onDelete = { 
                                scope.launch {
                                    viewModel.deleteMenuItem(menuItem.id)
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
                        text = "Error: ${uiState.message}",
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
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${menuItem.rate}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (menuItem.description.isNotEmpty()) {
                    Text(
                        text = menuItem.description,
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
    var name by remember { mutableStateOf(menuItem?.name ?: "") }
    var nameTamil by remember { mutableStateOf(menuItem?.nameTamil ?: "") }
    var description by remember { mutableStateOf(menuItem?.description ?: "") }
    var rate by remember { mutableStateOf(menuItem?.rate?.toString() ?: "") }
    var acRate by remember { mutableStateOf(menuItem?.acRate?.toString() ?: "") }
    var parcelRate by remember { mutableStateOf(menuItem?.parcelRate?.toString() ?: "") }

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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                        id = menuItem?.id ?: 0,
                        name = name,
                        nameTamil = nameTamil,
                        categoryId = menuItem?.categoryId ?: 1,
                        rate = rate.toDoubleOrNull() ?: 0.0,
                        acRate = acRate.toDoubleOrNull() ?: 0.0,
                        parcelRate = parcelRate.toDoubleOrNull() ?: 0.0,
                        description = description
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
