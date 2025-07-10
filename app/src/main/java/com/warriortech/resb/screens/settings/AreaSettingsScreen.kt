
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
import com.warriortech.resb.model.Area
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.viewmodel.AreaSettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun AreaSettingsScreen(
    viewModel: AreaSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingArea by remember { mutableStateOf<Area?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadAreas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Area")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is AreaSettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AreaSettingsUiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.areas) { area ->
                        AreaItem(
                            area = area,
                            onEdit = { editingArea = area },
                            onDelete = { 
                                scope.launch {
                                    viewModel.deleteArea(area.id)
                                }
                            }
                        )
                    }
                }
            }
            is AreaSettingsUiState.Error -> {
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
                        onClick = { viewModel.loadAreas() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingArea != null) {
        AreaDialog(
            area = editingArea,
            onDismiss = { 
                showAddDialog = false
                editingArea = null
            },
            onSave = { area ->
                scope.launch {
                    if (editingArea != null) {
                        viewModel.updateArea(area)
                    } else {
                        viewModel.addArea(area)
                    }
                    showAddDialog = false
                    editingArea = null
                }
            }
        )
    }
}

@Composable
fun AreaItem(
    area: Area,
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
                    text = area.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (area.description.isNotEmpty()) {
                    Text(
                        text = area.description,
                        style = MaterialTheme.typography.bodyMedium,
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
fun AreaDialog(
    area: Area?,
    onDismiss: () -> Unit,
    onSave: (Area) -> Unit
) {
    var name by remember { mutableStateOf(area?.name ?: "") }
    var description by remember { mutableStateOf(area?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (area != null) "Edit Area" else "Add Area") },
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newArea = Area(
                        id = area?.id ?: 0,
                        name = name,
                        description = description
                    )
                    onSave(newArea)
                },
                enabled = name.isNotBlank()
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

sealed class AreaSettingsUiState {
    object Loading : AreaSettingsUiState()
    data class Success(val areas: List<Area>) : AreaSettingsUiState()
    data class Error(val message: String) : AreaSettingsUiState()
}
