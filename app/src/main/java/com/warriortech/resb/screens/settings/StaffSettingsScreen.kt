
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.Staff
import com.warriortech.resb.ui.viewmodel.StaffSettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSettingsScreen(
    viewModel: StaffSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStaff by remember { mutableStateOf<Staff?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadStaff()
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
            Text("Add Staff")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is StaffSettingsViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is StaffSettingsViewModel.UiState.Success -> {
                LazyColumn {
                    items(uiState.staff) { staff ->
                        StaffCard(
                            staff = staff,
                            onEdit = { editingStaff = it },
                            onDelete = { 
                                scope.launch { 
                                    viewModel.deleteStaff(it.id) 
                                }
                            }
                        )
                    }
                }
            }
            is StaffSettingsViewModel.UiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showAddDialog) {
        StaffDialog(
            staff = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, email, role, hireDate ->
                scope.launch {
                    viewModel.addStaff(name, phone, email, role, hireDate)
                    showAddDialog = false
                }
            }
        )
    }

    editingStaff?.let { staff ->
        StaffDialog(
            staff = staff,
            onDismiss = { editingStaff = null },
            onConfirm = { name, phone, email, role, hireDate ->
                scope.launch {
                    viewModel.updateStaff(staff.id, name, phone, email, role, hireDate)
                    editingStaff = null
                }
            }
        )
    }
}

@Composable
fun StaffCard(
    staff: Staff,
    onEdit: (Staff) -> Unit,
    onDelete: (Staff) -> Unit
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
                    text = staff.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = staff.phone,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = staff.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Role: ${staff.role} | Hired: ${staff.hireDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { onEdit(staff) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = { onDelete(staff) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun StaffDialog(
    staff: Staff?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(staff?.name ?: "") }
    var phone by remember { mutableStateOf(staff?.phone ?: "") }
    var email by remember { mutableStateOf(staff?.email ?: "") }
    var role by remember { mutableStateOf(staff?.role ?: "") }
    var hireDate by remember { mutableStateOf(staff?.hireDate ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (staff == null) "Add Staff" else "Edit Staff") },
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
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hireDate,
                    onValueChange = { hireDate = it },
                    label = { Text("Hire Date") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, phone, email, role, hireDate) }
            ) {
                Text(if (staff == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
