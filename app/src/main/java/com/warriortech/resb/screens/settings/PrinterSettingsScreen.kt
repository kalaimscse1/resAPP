package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.R
import com.warriortech.resb.model.Printer
import com.warriortech.resb.model.TblPrinterResponse
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.PrinterSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterSettingsScreen(
    viewModel: PrinterSettingsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPrinter by remember { mutableStateOf<TblPrinterResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPrinters()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.printer_settings),
                    color = SurfaceLight
                ) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back),
                            tint = SurfaceLight)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_printer),
                            tint = SurfaceLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        }
    ) { paddingValues ->
            when(val state= uiState) {
                is PrinterSettingsViewModel.PrinterSettingsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is PrinterSettingsViewModel.PrinterSettingsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: ${state.message}")
                    }
                }
                is PrinterSettingsViewModel.PrinterSettingsUiState.Success -> {
                    if (state.printers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Printer available", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else{
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.printers) { printer ->
                            MobileOptimizedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = printer.printer_name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = printer.ip_address,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row {
                                        IconButton(onClick = { editingPrinter = printer }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.edit)
                                            )
                                        }
                                        IconButton(onClick = { viewModel.deletePrinter(printer.printer_id) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
    }

    if (showAddDialog) {
        PrinterDialog(
            printer = null,
            onDismiss = { showAddDialog = false },
            onSave = { printer ->
                viewModel.addPrinter(printer)
                showAddDialog = false
            }
        )
    }

    editingPrinter?.let { printer ->
        PrinterDialog(
            printer = printer,
            onDismiss = { editingPrinter = null },
            onSave = { updatedPrinter ->
                viewModel.updatePrinter(updatedPrinter)
                editingPrinter = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterDialog(
    printer: TblPrinterResponse?,
    onDismiss: () -> Unit,
    onSave: (Printer) -> Unit
) {
    var name by remember { mutableStateOf(printer?.printer_name ?: "") }
    var ipAddress by remember { mutableStateOf(printer?.ip_address ?: "") }
    var isActive by remember { mutableStateOf(printer?.is_active ?: 1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (printer == null) stringResource(R.string.add_printer) else stringResource(R.string.edit_printer)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.printer)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text(stringResource(R.string.ip_address)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isActive.toInt() ==1,
                        onCheckedChange = { isActive = if(it) 1 else 0 }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Active")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newPrinter =  Printer(
                       printer_id = printer?.printer_id?:0, // Use 0 for new printers
                        printer_name = name,
                        kitchen_cat_id = printer?.kitchen_cat?.kitchen_cat_id?.toLong()?:0L, // Default or selected category ID
                        ip_address = ipAddress,
                        is_active = isActive
                    )
                    onSave(newPrinter)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
