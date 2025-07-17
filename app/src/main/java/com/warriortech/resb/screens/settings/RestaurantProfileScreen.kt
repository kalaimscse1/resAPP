package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.R
import com.warriortech.resb.model.RestaurantProfile
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.viewmodel.RestaurantProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantProfileScreen(
    viewModel: RestaurantProfileViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.restaurant_profile)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        }
    ) { paddingValues ->
        Column( modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            when (val state=uiState){
                is RestaurantProfileViewModel.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is RestaurantProfileViewModel.UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            CompanySettingDialog(
                                setting = state.profile,
                                onSave = { newSetting ->
                                    scope.launch {
                                        viewModel.updateProfile(newSetting)
                                        snackbarHostState.showSnackbar("General Settings updated successfully")
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                    }
                }
                is RestaurantProfileViewModel.UiState.Error -> {
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
                            onClick = { viewModel.loadProfile() },
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
fun CompanySettingDialog(
    setting: RestaurantProfile?,
    onSave: (RestaurantProfile) -> Unit
) {
    var companyName by remember { mutableStateOf(setting?.company_name ?: "") }
    var ownerName by remember { mutableStateOf(setting?.owner_name ?: "") }
    var address1 by remember { mutableStateOf(setting?.address1 ?: "") }
    var address2 by remember { mutableStateOf(setting?.address2 ?: "") }
    var place by remember { mutableStateOf(setting?.place ?: "") }
    var pincode by remember { mutableStateOf(setting?.pincode ?: "") }
    var contactNo by remember { mutableStateOf(setting?.contact_no ?: "") }
    var mailId by remember { mutableStateOf(setting?.mail_id ?: "") }
    var country by remember { mutableStateOf(setting?.country ?: "") }
    var state by remember { mutableStateOf(setting?.state ?: "") }
    var currency by remember { mutableStateOf(setting?.currency ?: "") }
    var taxNo by remember { mutableStateOf(setting?.tax_no ?: "") }
    var decimalPoint by remember { mutableStateOf(setting?.decimal_point?.toString() ?: "2") }

    Column {
        OutlinedTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = { Text("Company Name ") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Owner Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address1,
            onValueChange = { address1 = it },
            label = { Text("Address Line1") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address2,
            onValueChange = { address2 = it },
            label = { Text("Address Line2") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = place,
            onValueChange = { place = it },
            label = { Text("Place") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pincode,
            onValueChange = { pincode = it },
            label = { Text("Pincode") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = contactNo,
            onValueChange = { contactNo = it },
            label = { Text("Contact No") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = mailId,
            onValueChange = { mailId = it },
            label = { Text("Mail Id") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Currency") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = taxNo,
            onValueChange = { taxNo = it },
            label = { Text("Tax No") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = decimalPoint,
            onValueChange = { decimalPoint = it },
            label = { Text("Decimal Point") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val newSetting = RestaurantProfile(
                    company_code = setting?.company_code ?: "",
                    company_name = companyName,
                    owner_name = ownerName,
                    address1 = address1,
                    address2 = address2,
                    place = place,
                    pincode = pincode,
                    contact_no = contactNo,
                    mail_id = mailId,
                    country = country,
                    state = state,
                    currency = currency,
                    tax_no = taxNo,
                    decimal_point = decimalPoint.toLongOrNull() ?: 2L
                )
                onSave(newSetting)
            }
        ){
            Text(
                text = if (setting == null) "Add" else "Update",
                fontWeight = FontWeight.Bold
            )
        }
    }
}