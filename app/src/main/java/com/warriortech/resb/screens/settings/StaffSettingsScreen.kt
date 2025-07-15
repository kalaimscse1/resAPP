package com.warriortech.resb.screens.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import com.warriortech.resb.model.Customer
import com.warriortech.resb.model.TblStaff
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.viewmodel.CustomerSettingsViewModel
import com.warriortech.resb.ui.viewmodel.StaffViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStaff by remember { mutableStateOf<TblStaff?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle messages
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Settings") },
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
                Icon(Icons.Default.Add, contentDescription = "Add Staff")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.staff) { staff ->
                    StaffCard(
                        staff = staff,
                        onEdit = { editingStaff = staff },
                        onDelete = { viewModel.deleteStaff(staff.staff_id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddStaffDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, role, email, phone ->
                viewModel.addStaff(name, role, email, phone)
                showAddDialog = false
            }
        )
    }

    editingStaff?.let { staff ->
        EditStaffDialog(
            staff = staff,
            onDismiss = { editingStaff = null },
            onUpdate = { updatedStaff ->
                viewModel.updateStaff(updatedStaff)
                editingStaff = null
            }
        )
    }
}

@Composable
fun StaffCard(
    staff: TblStaff,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = staff.staff_name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = staff.role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = staff.address,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = staff.contact_no,
                style = MaterialTheme.typography.bodySmall
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun AddStaffDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Staff") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(name, role, email, phone) },
                enabled = name.isNotBlank() && role.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditStaffDialog(
    staff: TblStaff,
    onDismiss: () -> Unit,
    onUpdate: (TblStaff) -> Unit
) {
    var name by remember { mutableStateOf(staff.staff_name) }
    var role by remember { mutableStateOf(staff.role) }
    var email by remember { mutableStateOf(staff.address) }
    var phone by remember { mutableStateOf(staff.contact_no) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Staff") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onUpdate(TblStaff(
                    1L, name, role, email, phone,
                    password = "",
                    role_id = 1L,
                    role = "",
                    last_login = "",
                    is_block = false,
                    counter_id = 1L,
                    counter_name = "",
                    is_active = 1L
                )) },
                enabled = name.isNotBlank() && role.isNotBlank()
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
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