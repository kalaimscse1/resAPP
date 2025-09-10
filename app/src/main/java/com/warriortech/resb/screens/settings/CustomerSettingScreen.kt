package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.Customer
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.CustomerSettingsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSettingsScreen(
    viewModel: CustomerSettingsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadCustomers()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Settings", color = SurfaceLight) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = SurfaceLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer",
                    tint = SurfaceLight)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ){ paddingValues ->
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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