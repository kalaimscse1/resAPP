
package com.warriortech.resb.screens.settings

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.warriortech.resb.model.Customer
import com.warriortech.resb.ui.viewmodel.CustomerSettingsViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSettingsScreen(
    viewModel: CustomerSettingsViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadCustomers()
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
            Text("Add Customer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is CustomerSettingsViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CustomerSettingsViewModel.UiState.Success -> {
                LazyColumn {
                    items((uiState as CustomerSettingsViewModel.UiState.Success).customers) { customer ->
                        CustomerCard(
                            customer = customer,
                            onEdit = { editingCustomer = it },
                            onDelete = { 
                                scope.launch { 
                                    viewModel.deleteCustomer(it.customer_id)
                                }
                            }
                        )
                    }
                }
            }
            is CustomerSettingsViewModel.UiState.Error -> {

                Text(
                    text = (uiState as CustomerSettingsViewModel.UiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showAddDialog) {
        CustomerDialog(
            customer = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, email, address ->
                scope.launch {
                    viewModel.addCustomer(name, phone, email, address)
                    showAddDialog = false
                }
            }
        )
    }

    editingCustomer?.let { customer ->
        CustomerDialog(
            customer = customer,
            onDismiss = { editingCustomer = null },
            onConfirm = { name, phone, email, address ->
                scope.launch {
                    viewModel.updateCustomer(customer.customer_id, name, phone, email, address)
                    editingCustomer = null
                }
            }
        )
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onEdit: (Customer) -> Unit,
    onDelete: (Customer) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.customer_name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = customer.customer_phone,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = customer.customer_email.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = customer.customer_address.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { onEdit(customer) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = { onDelete(customer) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun CustomerDialog(
    customer: Customer?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(customer?.customer_name ?: "") }
    var phone by remember { mutableStateOf(customer?.customer_phone ?: "") }
    var email by remember { mutableStateOf(customer?.customer_email ?: "") }
    var address by remember { mutableStateOf(customer?.customer_address ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (customer == null) "Add Customer" else "Edit Customer") },
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
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, phone, email, address) }
            ) {
                Text(if (customer == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
