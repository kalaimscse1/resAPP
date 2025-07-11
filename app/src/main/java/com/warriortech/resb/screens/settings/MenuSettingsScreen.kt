
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
import com.warriortech.resb.model.Menu
import com.warriortech.resb.ui.viewmodel.MenuSettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSettingsScreen(
    viewModel: MenuSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMenu by remember { mutableStateOf<Menu?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadMenus()
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
            Text("Add Menu")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is MenuSettingsViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MenuSettingsViewModel.UiState.Success -> {
                LazyColumn {
                    items((uiState as MenuSettingsViewModel.UiState.Success).menus) { menu ->
                        MenuCard(
                            menu = menu,
                            onEdit = { editingMenu = it },
                            onDelete = { 
                                scope.launch { 
                                    viewModel.deleteMenu(it.id) 
                                }
                            }
                        )
                    }
                }
            }
            is MenuSettingsViewModel.UiState.Error -> {

                Text(
                    text = (uiState as MenuSettingsViewModel.UiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showAddDialog) {
        MenuDialog(
            menu = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description, isActive ->
                scope.launch {
                    viewModel.addMenu(name, description, isActive)
                    showAddDialog = false
                }
            }
        )
    }

    editingMenu?.let { menu ->
        MenuDialog(
            menu = menu,
            onDismiss = { editingMenu = null },
            onConfirm = { name, description, isActive ->
                scope.launch {
                    viewModel.updateMenu(menu.id, name, description, isActive)
                    editingMenu = null
                }
            }
        )
    }
}

@Composable
fun MenuCard(
    menu: Menu,
    onEdit: (Menu) -> Unit,
    onDelete: (Menu) -> Unit
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
                    text = menu.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = menu.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (menu.isActive) "Active" else "Inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (menu.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            IconButton(onClick = { onEdit(menu) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = { onDelete(menu) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun MenuDialog(
    menu: Menu?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(menu?.name ?: "") }
    var description by remember { mutableStateOf(menu?.description ?: "") }
    var isActive by remember { mutableStateOf(menu?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (menu == null) "Add Menu" else "Edit Menu") },
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Active")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, description, isActive) }
            ) {
                Text(if (menu == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
