
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
import androidx.navigation.NavController
import com.warriortech.resb.model.Table
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.viewmodel.TableSettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun TableSettingsScreen(
    viewModel: TableSettingsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTable by remember { mutableStateOf<Table?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadTables()
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
            Text("Add Table")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is TableSettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TableSettingsUiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items((uiState as TableSettingsUiState.Success).tables) { table ->
                        TableItem(
                            table = table,
                            onEdit = { editingTable = table },
                            onDelete = { 
                                scope.launch {
                                    viewModel.deleteTable(table.table_id)
                                }
                            }
                        )
                    }
                }
            }
            is TableSettingsUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {

                    Text(
                        text = "Error: ${(uiState as TableSettingsUiState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.loadTables() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }

    if (showAddDialog || editingTable != null) {
        TableDialog(
            table = editingTable,
            onDismiss = { 
                showAddDialog = false
                editingTable = null
            },
            onSave = { table ->
                scope.launch {
                    if (editingTable != null) {
                        viewModel.updateTable(table)
                    } else {
                        viewModel.addTable(table)
                    }
                    showAddDialog = false
                    editingTable = null
                }
            }
        )
    }
}

@Composable
fun TableItem(
    table: Table,
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
                    text = "Table ${table.table_name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Capacity: ${table.seating_capacity} | Status: ${table.table_availability}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
fun TableDialog(
    table: Table?,
    onDismiss: () -> Unit,
    onSave: (Table) -> Unit
) {
    var tableNumber by remember { mutableStateOf(table?.table_name?.toString() ?: "") }
    var capacity by remember { mutableStateOf(table?.seating_capacity?.toString() ?: "") }
    var status by remember { mutableStateOf(table?.table_availability ?: "Available") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (table != null) "Edit Table" else "Add Table") },
        text = {
            Column {
                OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { tableNumber = it },
                    label = { Text("Table Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacity") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newTable = Table(
                        table_id = table?.table_id ?: 0,
                        area_id = table?.area_id ?:1,
                        area_name = table?.area_name ?: "Table Area",
                        table_name = table?.table_name ?: "Table Name",
                        seating_capacity = table?.seating_capacity?: 4,
                        is_ac = table?.is_ac ?: "",
                        table_status = table?.table_status ?: "",
                        table_availability = table?.table_availability ?: "Available",
                    )
                    onSave(newTable)
                },
                enabled = tableNumber.isNotBlank() && capacity.isNotBlank()
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

sealed class TableSettingsUiState {
    object Loading : TableSettingsUiState()
    data class Success(val tables: List<Table>) : TableSettingsUiState()
    data class Error(val message: String) : TableSettingsUiState()
}
