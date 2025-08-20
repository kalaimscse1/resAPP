package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.warriortech.resb.model.GeneralSettings
import kotlinx.coroutines.launch

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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.generalSettings) { setting ->

                            GeneralSettingDialog(
                                setting = setting,
                                onDismiss = {
                                    showAddDialog = false
                                    editingTable = null
                                },
                                onSave = { newSetting ->
                                    scope.launch {
                                        viewModel.updateSettings(newSetting)
                                        snackbarHostState.showSnackbar("General Settings updated successfully")
                                    }
                                    showAddDialog = false
                                    editingTable = null
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
       }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    var isGstSummary by remember { mutableStateOf(setting?.is_gst_summary ?: false) }
    var isReceipt by remember { mutableStateOf(setting?.is_receipt ?: false) }
    var isKot by remember { mutableStateOf(setting?.is_kot ?: false) }
    var isLogo by remember { mutableStateOf(setting?.is_logo ?: false) }
    var logoPath by remember { mutableStateOf(setting?.logo_path ?: "") }
    var cess by remember { mutableStateOf(setting?.is_cess ?: false) }
    var deliveryCharge by remember { mutableStateOf(setting?.is_delivery_charge ?: false) }
    var isTableAllowed by remember { mutableStateOf(setting?.is_table_allowed ?: true) }
    var isWaiterAllowed by remember { mutableStateOf(setting?.is_waiter_allowed ?: true) }
    var menuShowInTime by remember { mutableStateOf(setting?.menu_show_in_time ?: true) }
    var tamilReceiptPrint by remember { mutableStateOf(setting?.tamil_receipt_print ?: false) }

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
            Text("Tax Applicable")
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
            Text("Tax Included")
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
            Text("Discount Allowed")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = discBy,
            onValueChange = { discBy = it },
            label = { Text("Discount By % OR Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = discAmt,
            onValueChange = { discAmt = it },
            label = { Text("Discount Amount") },
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
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isGstSummary,
                onCheckedChange = { isGstSummary = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tax Summary in Bill")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isReceipt,
                onCheckedChange = { isReceipt = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Print Bill")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isKot,
                onCheckedChange = { isKot = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("KOT")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isLogo,
                onCheckedChange = { isLogo = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logo In Bill")
        }
        OutlinedTextField(
            value = logoPath,
            onValueChange = { logoPath = it },
            label = { Text("LOGO PATH") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = cess,
                onCheckedChange = { cess = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cess Applicable")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = deliveryCharge,
                onCheckedChange = { deliveryCharge = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delivery Charge Applicable")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isWaiterAllowed,
                onCheckedChange = { isWaiterAllowed = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Waiter Validation")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isTableAllowed,
                onCheckedChange = { isTableAllowed = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Table Validation")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = menuShowInTime,
                onCheckedChange = { menuShowInTime = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Menu Show In Time")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = tamilReceiptPrint,
                onCheckedChange = { tamilReceiptPrint = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Receipt Print In Tamil")
        }
        Button(
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
                    is_tendered = isTendered,
                    is_gst_summary = isGstSummary,
                    is_receipt = isReceipt,
                    is_kot = isKot,
                    is_logo = isLogo,
                    logo_path = logoPath,
                    is_cess = cess,
                    is_delivery_charge = deliveryCharge,
                    is_table_allowed = isTableAllowed,
                    is_waiter_allowed = isWaiterAllowed,
                    menu_show_in_time = menuShowInTime,
                    tamil_receipt_print = tamilReceiptPrint
                )
                onSave(newSetting)
            }
        ){
            Text(
                text = if (setting == null) "Add Setting" else "Update ",
                fontWeight = FontWeight.Bold
            )
        }
    }
}