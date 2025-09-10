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
import androidx.navigation.NavController
import com.warriortech.resb.R
import com.warriortech.resb.model.TblVoucherType
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.components.ReusableBottomSheet
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.VoucherTypeSettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherTypeSettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: VoucherTypeSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingVoucherType by remember { mutableStateOf<TblVoucherType?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadVoucherTypes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voucher Type Settings", color = SurfaceLight) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = SurfaceLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Voucher Type",
                            tint = SurfaceLight)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is VoucherTypeSettingsViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is VoucherTypeSettingsViewModel.UiState.Success -> {
                if (state.voucherTypes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No voucher types available", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.voucherTypes) { voucherType ->
                            VoucherTypeCard(
                                voucherType = voucherType,
                                onEdit = {
                                    editingVoucherType = voucherType
                                    showAddDialog = true
                                },
                                onDelete = {
                                    scope.launch {
                                        viewModel.deleteVoucherType(voucherType.voucher_Type_id)
                                        snackbarHostState.showSnackbar("Voucher type deleted")
                                    }
                                }
                            )
                        }
                    }
                }
            }
            is VoucherTypeSettingsViewModel.UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.loadVoucherTypes() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        if (showAddDialog || editingVoucherType != null) {
            ReusableBottomSheet(
                isVisible = showAddDialog || editingVoucherType != null,
                onDismiss = {
                    showAddDialog = false
                    editingVoucherType = null
                },
                title = if (editingVoucherType == null) "Add Voucher Type" else "Edit Voucher Type"
            ) {
                var voucherTypeName by remember { mutableStateOf(editingVoucherType?.voucher_type_name ?: "") }
                var isActive by remember { mutableStateOf(editingVoucherType?.is_active ?: true) }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = voucherTypeName,
                        onValueChange = { voucherTypeName = it.uppercase() },
                        label = { Text("Voucher Type Name") },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showAddDialog = false
                                editingVoucherType = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val newVoucherType = TblVoucherType(
                                    voucher_Type_id = editingVoucherType?.voucher_Type_id ?: 0,
                                    voucher_type_name = voucherTypeName,
                                    is_active = isActive
                                )
                                if (editingVoucherType == null) {
                                    scope.launch {
                                        viewModel.addVoucherType(newVoucherType)
                                        snackbarHostState.showSnackbar("Voucher type added successfully")
                                    }
                                } else {
                                    scope.launch {
                                        viewModel.updateVoucherType(newVoucherType)
                                        snackbarHostState.showSnackbar("Voucher type updated successfully")
                                    }
                                }
                                showAddDialog = false
                                editingVoucherType = null
                            },
                            modifier = Modifier.weight(1f),
                            enabled = voucherTypeName.isNotBlank()
                        ) {
                            Text(if (editingVoucherType != null) "Update" else "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherTypeCard(
    voucherType: TblVoucherType,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    MobileOptimizedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit // Set onClick to trigger edit
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
                    text = "Type: ${voucherType.voucher_type_name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (voucherType.is_active) "Active" else "Inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (voucherType.is_active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            Row {
                // Edit icon is now handled by the card's onClick
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}