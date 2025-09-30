package com.warriortech.resb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.model.TblCustomer
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.components.PaymentMethodCard
import com.warriortech.resb.ui.components.PaymentSummaryCard
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SecondaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.BillingPaymentUiState
import com.warriortech.resb.ui.viewmodel.BillingViewModel
import com.warriortech.resb.ui.viewmodel.PaymentProcessingState
import com.warriortech.resb.util.AnimatedSnackbarDemo
import com.warriortech.resb.util.CurrencySettings
import com.warriortech.resb.util.StringDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavHostController,
    viewModel: BillingViewModel = hiltViewModel(),
    amountToPayFromRoute: Double? = null,
    orderMasterId: String? = null,
    billNo: String? = null,
    customerId: Long? = null,
    sessionManager: SessionManager
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val table = sessionManager.getGeneralSetting()?.is_table_allowed == true
    var showCustomerDialog by remember { mutableStateOf(false) }
    var customer by remember { mutableStateOf<TblCustomer?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCustomers()
    }

    LaunchedEffect(customerId, billNo) {
        if (customerId != null && billNo != null) {
            viewModel.updateCustomerId(customerId)
            viewModel.updateBillNo(billNo)
        }
    }

    LaunchedEffect(key1 = amountToPayFromRoute, key2 = orderMasterId) {
        amountToPayFromRoute?.let { viewModel.updateAmountToPay(it.toDouble()) }
        orderMasterId?.let { viewModel.updateOrderMasterId(it) }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.paymentProcessingState) {
        if (uiState.paymentProcessingState is PaymentProcessingState.Success) {
            viewModel.resetPaymentState()
            if (table)
                navController.navigate("selects")
            else
                navController.navigate("quick_bills")
        } else if (uiState.paymentProcessingState is PaymentProcessingState.Error) {
            val errorState = uiState.paymentProcessingState as PaymentProcessingState.Error
            snackbarHostState.showSnackbar("Payment Failed: ${errorState.message}")
            viewModel.resetPaymentState()
        }
    }

    Scaffold(
        snackbarHost = { AnimatedSnackbarDemo(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Complete Payment",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceLight,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.paymentProcessingState !is PaymentProcessingState.Processing) {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                ),
                actions = {
                    IconButton(onClick = {
                        showCustomerDialog = true
                    }) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Customer",
                            tint = SurfaceLight
                        )
                    }
                }
            )
        },
        bottomBar = {
            PaymentBottomBar(
                uiState = uiState,
                onConfirmPayment = {
                    viewModel.updateAmountToPay(amountToPayFromRoute ?: 0.0)
                    viewModel.processPayment()
                },
                customer = customers.find { it.customer_id == customer?.customer_id }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            if (uiState.paymentProcessingState is PaymentProcessingState.Processing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(48.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Processing Payment...",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Please wait while we process your payment",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            } else if (uiState.paymentProcessingState is PaymentProcessingState.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(48.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Payment Successful!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Your payment has been processed successfully",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        PaymentSummaryCard(uiState = uiState)
                    }

                    item {
                        PaymentMethodCard(
                            uiState = uiState,
                            onPaymentMethodChange = { viewModel.updatePaymentMethod(it) },
                            viewModel = viewModel,
                            onCustomer = {
                                customer = it
                                viewModel.setCustomer(it)
                            },
                            customers = customers
                        )
                    }
                }
            }
        }
    }
    if (showCustomerDialog) {
        CustomerSelectionDialog(
            customers = customers,
            onCustomerSelected = { it ->
                viewModel.setCustomer(it)
                showCustomerDialog = false
            },
            onDismissRequest = {
                showCustomerDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSelectionDialog(
    customers: List<TblCustomer>,
    onCustomerSelected: (TblCustomer) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Customer",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            StringDropdown(
                label = "Customer",
                options = customers.map { it.customer_name },
                onOptionSelected = { selectedName ->
                    val selectedCustomer = customers.find { it.customer_name == selectedName }
                    selectedCustomer?.let {
                        onCustomerSelected(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                selectedOption = {
                    customers.find { true }?.customer_name
                        ?: customers.firstOrNull()?.customer_name
                        ?: "Select Customer"
                }.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
fun PaymentBottomBar(
    uiState: BillingPaymentUiState,
    onConfirmPayment: () -> Unit,
    customer: TblCustomer? = null
) {
    val payingAmount = if (uiState.selectedPaymentMethod?.name == "OTHERS") {
        uiState.cashAmount + uiState.cardAmount + uiState.upiAmount
    } else {
        uiState.amountToPay
    }

    BottomAppBar(
        containerColor = SecondaryGreen
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                CurrencySettings.format(payingAmount),
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = onConfirmPayment,
                enabled = when (uiState.selectedPaymentMethod?.name) {
                    "DUE" -> customer?.customer_id != null &&
                            uiState.paymentProcessingState == PaymentProcessingState.Idle
                    else -> uiState.selectedPaymentMethod != null &&
                            payingAmount > 0 &&
                            uiState.paymentProcessingState == PaymentProcessingState.Idle
                }
            ) {
                Text("Confirm Payment")
            }
        }
    }
}
