package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.R
import com.warriortech.resb.model.TblCounter
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.viewmodel.CounterSettingsViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.font.FontWeight
import com.warriortech.resb.ui.components.MobileOptimizedCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterSettingsScreen(
    viewModel: CounterSettingsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCounter by remember { mutableStateOf<TblCounter?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadCounters()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.counter_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.add_counter))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is CounterSettingsViewModel.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CounterSettingsViewModel.UiState.Success -> {
                    if (state.counters.isEmpty()){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No counters available", style = MaterialTheme.typography.bodyLarge)
                        }
                    }else
                    {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.counters) { counter ->
                                CounterItem(
                                    counter = counter,
                                    onEdit = {
                                        editingCounter = counter
                                        showAddDialog = true
                                    },
                                    onDelete = {
                                        scope.launch {
                                            viewModel.deleteCounter(counter.counter_id)
                                            snackbarHostState.showSnackbar("Tax deleted")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is CounterSettingsViewModel.UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.message)
                    }
                }
            }

            if (showAddDialog || editingCounter != null) {
                AddEditCounterDialog(
                    counter = editingCounter,
                    onDismissRequest = {
                        showAddDialog = false
                        editingCounter = null
                    },
                    onSave = { newCounter ->
                        if (editingCounter == null) {
                            scope.launch {
                                viewModel.addCounter(newCounter)
                                snackbarHostState.showSnackbar("Counter added successfully")
                            }
                        } else {
                            scope.launch {
                                viewModel.updateCounter(newCounter)
                                snackbarHostState.showSnackbar("Counter updated successfully")
                            }
                        }
                        showAddDialog = false
                        editingCounter = null
                    }
                )
            }
        }
    }
}

@Composable
fun AddEditCounterDialog(
    counter: TblCounter?,
    onDismissRequest: () -> Unit,
    onSave: (TblCounter) -> Unit
) {
    var counterName by remember { mutableStateOf(counter?.counter_name ?: "") }
    var ipAddress by remember { mutableStateOf(counter?.ip_address ?: "") }
    var isActive by remember { mutableStateOf(counter?.is_active ?: true) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if (counter != null) "Edit Counter" else "Add Counter") },
        text = {
            Column {
                OutlinedTextField(
                    value = counterName,
                    onValueChange = { counterName = it },
                    label = { Text("Counter Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("IpAddress") },
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
                onClick = {
                    val newTable = TblCounter(
                        counter_id = counter?.counter_id ?: 0,
                        counter_name = counter?.counter_name ?: counterName,
                        ip_address = counter?.ip_address ?: ipAddress,
                        is_active = counter?.is_active ?: isActive
                    )
                    onSave(newTable)
                },
                enabled = counterName.isNotBlank() && ipAddress.isNotBlank()
            ) {
                Text(if (counter != null) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CounterItem(
    counter: TblCounter,
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
                    text = "Counter : ${counter.counter_name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "IpAddress : ${counter.ip_address}",
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
