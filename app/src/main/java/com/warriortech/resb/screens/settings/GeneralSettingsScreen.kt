
package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.warriortech.resb.R
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.viewmodel.GeneralSettingsViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.Area
import com.warriortech.resb.model.GeneralSettings
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TblTable
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.util.AreaDropdown
import com.warriortech.resb.util.StringDropdown
import kotlinx.coroutines.launch
import kotlin.collections.find

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    viewModel: GeneralSettingsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTable by remember { mutableStateOf<GeneralSettings?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.general_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
     Column( modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                ) {
          when (val state=uiState){
            is GeneralSettingsViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is GeneralSettingsViewModel.UiState.Success -> {
                if (state.generalSettings.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No General Setting found", style = MaterialTheme.typography.bodyLarge)
                    }
                    return@Column
                } else{
                    LazyColumn {
                        items(state.generalSettings) { setting ->
                          GeneralSettingItem(
                                setting = setting,
                                onEdit = {
                                    editingTable = setting
                                    showAddDialog = true
                                }
                            )
                        }
                    }
                }
            }
            is GeneralSettingsViewModel.UiState.Error -> {
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
                        onClick = { viewModel.loadSettings() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
          }
         if (showAddDialog || editingTable != null) {
            GeneralSettingDialog(
                setting = editingTable,
                onDismiss = {
                    showAddDialog = false
                    editingTable = null
                },
                onSave = { newSetting ->
                    scope.launch {
                        try {
                            if (editingTable != null) {
                                viewModel.updateSettings(newSetting)
                                snackbarHostState.showSnackbar("General Settings updated successfully")
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Error: ${e.message}")
                        }
                    }
                    showAddDialog = false
                    editingTable = null
                }
            )
         }

       }
    }
}

@Composable
fun GeneralSettingItem(
    setting: GeneralSettings,
    onEdit: () -> Unit
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
                    text = "Company Name Font :${setting.company_name_font}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Address Font : ${setting.address_font} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Is Tax : ${setting.is_tax} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Is Tax Included : ${setting.is_tax_included} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Is RoundOff : ${setting.is_round_off} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Is Allowed Discount : ${setting.is_allowed_disc} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Discount By : ${setting.disc_by} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Discount Amount : ${setting.disc_amt} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Is Tendered : ${setting.is_tendered} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}

@Composable
fun GeneralSettingDialog(
    setting: GeneralSettings?,
    onDismiss: () -> Unit,
    onSave: (GeneralSettings) -> Unit
) {
    var companyNameFont by remember { mutableStateOf(setting?.company_name_font?.toString() ?: "") }
    var addressFont by remember { mutableStateOf(setting?.address_font?.toString() ?: "") }
    var isTax by remember { mutableStateOf(setting?.is_tax ?: false) }
    var isTaxIncluded by remember { mutableStateOf(setting?.is_tax_included ?: false) }
    var isRoundOff by remember { mutableStateOf(setting?.is_round_off ?: false) }
    var isAllowedDisc by remember { mutableStateOf(setting?.is_allowed_disc ?: false) }
    var discBy by remember { mutableStateOf(setting?.disc_by?.toString() ?: "") }
    var discAmt by remember { mutableStateOf(setting?.disc_amt?.toString() ?: "") }
    var isTendered by remember { mutableStateOf(setting?.is_tendered ?: false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (setting != null) "Edit Setting" else "Add Setting") },
        text = {
            Column {
                OutlinedTextField(
                    value = companyNameFont,
                    onValueChange = { companyNameFont = it },
                    label = { Text("Company Name Font") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = addressFont,
                    onValueChange = { addressFont = it },
                    label = { Text("Address Font") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isTax,
                        onCheckedChange = { isTax = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Is Tax")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isTaxIncluded,
                        onCheckedChange = { isTaxIncluded = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("IS Tax Included")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isRoundOff,
                        onCheckedChange = { isRoundOff = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("IS Round Off")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isAllowedDisc,
                        onCheckedChange = { isAllowedDisc = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("IS Allowed Discount")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = discBy,
                    onValueChange = { discBy = it },
                    label = { Text("Address Font") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = discAmt,
                    onValueChange = { discAmt = it },
                    label = { Text("Address Font") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isTendered,
                        onCheckedChange = { isTendered = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("IS Tendered")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newSetting = GeneralSettings(
                        id = setting?.id ?: 0,
                        company_name_font = companyNameFont.toInt(),
                        address_font = addressFont.toInt(),
                        is_tax = isTax,
                        is_tax_included = isTaxIncluded,
                        is_round_off = isRoundOff,
                        is_allowed_disc = isAllowedDisc,
                        disc_by = discBy.toIntOrNull() ?: 0,
                        disc_amt = discAmt.toDoubleOrNull() ?: 0.0,
                        is_tendered = isTendered
                    )
                    onSave(newSetting)
                },
                enabled = companyNameFont.isNotBlank() && addressFont.isNotBlank()
            ) {
                Text(if (setting != null) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}