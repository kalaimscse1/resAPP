package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.components.PaymentMethodCard
import com.warriortech.resb.ui.components.PaymentSummaryCard
import com.warriortech.resb.ui.viewmodel.BillingViewModel
import com.warriortech.resb.ui.viewmodel.BillingPaymentUiState
import com.warriortech.resb.ui.viewmodel.PaymentMethod
import com.warriortech.resb.ui.viewmodel.PaymentProcessingState
import java.text.NumberFormat
import java.util.Locale
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SecondaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.util.AnimatedSnackbarDemo
import com.warriortech.resb.util.CurrencySettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavHostController,
    viewModel: BillingViewModel = hiltViewModel(), // Shared ViewModel
    amountToPayFromRoute: Double? = null,
    orderMasterId: String? = null,
    billNo: String?=null,
    customerId:Long?=null,
    sessionManager: SessionManager// If passing amount via route
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val table = sessionManager.getGeneralSetting()?.is_table_allowed == true

    LaunchedEffect(Unit) {
        viewModel.loadCustomers()
    }
    LaunchedEffect(customerId, billNo) {
        if (customerId != null && billNo != null) {
            viewModel.updateCustomerId(customerId)
            viewModel.updateBillNo(billNo)
        }
    }
    // If amount was passed via route, you might set it in the ViewModel once
     LaunchedEffect(key1 = amountToPayFromRoute, key2 =  orderMasterId) {
         amountToPayFromRoute?.let { viewModel.updateAmountToPay(it.toDouble()) }
            orderMasterId?.let { viewModel.updateOrderMasterId(it) }
     }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage() // Clear after showing
        }
    }

    LaunchedEffect(uiState.paymentProcessingState) {
        if (uiState.paymentProcessingState is PaymentProcessingState.Success) {
             viewModel.resetPaymentState() // Reset for next payment
            if (table)
            navController.navigate("selects")
            else
                navController.navigate("quick_bills")
        } else if (uiState.paymentProcessingState is PaymentProcessingState.Error) {
            val errorState = uiState.paymentProcessingState as PaymentProcessingState.Error
            snackbarHostState.showSnackbar("Payment Failed: ${errorState.message}")
             viewModel.resetPaymentState() // Allow retry
        }
    }

    Scaffold(
        snackbarHost = { AnimatedSnackbarDemo(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Complete Payment",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceLight
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
                )
            )
        },
        bottomBar = {
            PaymentBottomBar(
                uiState = uiState,
                onConfirmPayment = {
                    viewModel.updateAmountToPay(amountToPayFromRoute?:0.0)
                    viewModel.processPayment() }
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
                                viewModel.setCustomer(it)
                            },
                            customers = customers
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentBottomBar(
    uiState: BillingPaymentUiState,
    onConfirmPayment: () -> Unit
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(CurrencySettings.format(payingAmount), style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onConfirmPayment,
                enabled = uiState.selectedPaymentMethod != null && payingAmount > 0 &&
                        uiState.paymentProcessingState == PaymentProcessingState.Idle
            ) {
                Text("Confirm Payment")
            }
        }
    }
}
// Helper from BillingScreen (can be moved to a common util file)
// fun Double.format(digits: Int) = "%.${digits}f".format(this)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PaymentScreen(
//    billNo: String,
//    navController: NavHostController,
//    viewModel: PaymentViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val selectedBill by viewModel.selectedBill.collectAsStateWithLifecycle()
//
//    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
//    var paymentAmount by remember { mutableStateOf("") }
//    var showProcessing by remember { mutableStateOf(false) }
//
//    LaunchedEffect(billNo) {
//        viewModel.loadBillDetails(billNo)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Process Payment") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = PrimaryGreen,
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White
//                )
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(SurfaceLight)
//                .verticalScroll(rememberScrollState())
//        ) {
//            when (uiState) {
//                is PaymentUiState.Loading -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(color = PrimaryGreen)
//                    }
//                }
//                is PaymentUiState.Success -> {
//                    selectedBill?.let { bill ->
//                        // Bill Details Card
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                        ) {
//                            Column(modifier = Modifier.padding(16.dp)) {
//                                Text(
//                                    text = "Bill Details",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    fontWeight = FontWeight.Bold
//                                )
//                                Spacer(modifier = Modifier.height(12.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text("Bill No:", style = MaterialTheme.typography.bodyMedium)
//                                    Text(
//                                        text = bill.bill_no,
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        fontWeight = FontWeight.Medium
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text("Customer:", style = MaterialTheme.typography.bodyMedium)
//                                    Text(
//                                        text = bill.customer.customer_name ?: "Walk-in Customer",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        fontWeight = FontWeight.Medium
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.height(8.dp))
//                                ModernDivider()
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text("Total Amount:", style = MaterialTheme.typography.bodyMedium)
//                                    Text(
//                                        text = CurrencySettings.formatCurrency(bill.grand_total),
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        fontWeight = FontWeight.Medium
//                                    )
//                                }
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text("Paid Amount:", style = MaterialTheme.typography.bodyMedium)
//                                    Text(
//                                        text = CurrencySettings.formatCurrency(bill.received_amt),
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        fontWeight = FontWeight.Medium
//                                    )
//                                }
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(
//                                        text = "Pending Amount:",
//                                        style = MaterialTheme.typography.titleMedium,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                    Text(
//                                        text = CurrencySettings.formatCurrency(bill.pending_amt),
//                                        style = MaterialTheme.typography.titleMedium,
//                                        fontWeight = FontWeight.Bold,
//                                        color = Color.Red
//                                    )
//                                }
//                            }
//                        }
//
//                        // Payment Method Selection
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                        ) {
//                            Column(modifier = Modifier.padding(16.dp)) {
//                                Text(
//                                    text = "Payment Method",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    fontWeight = FontWeight.Bold
//                                )
//                                Spacer(modifier = Modifier.height(12.dp))
//
//                                PaymentMethod.values().forEach { method ->
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .selectable(
//                                                selected = selectedPaymentMethod == method,
//                                                onClick = { selectedPaymentMethod = method }
//                                            )
//                                            .padding(vertical = 8.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        RadioButton(
//                                            selected = selectedPaymentMethod == method,
//                                            onClick = { selectedPaymentMethod = method },
//                                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryGreen)
//                                        )
//                                        Spacer(modifier = Modifier.width(8.dp))
//                                        Text(
//                                            text = method.name,
//                                            style = MaterialTheme.typography.bodyMedium
//                                        )
//                                    }
//                                }
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        // Payment Amount Input
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                        ) {
//                            Column(modifier = Modifier.padding(16.dp)) {
//                                Text(
//                                    text = "Payment Amount",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    fontWeight = FontWeight.Bold
//                                )
//                                Spacer(modifier = Modifier.height(12.dp))
//
//                                OutlinedTextField(
//                                    value = paymentAmount,
//                                    onValueChange = { paymentAmount = it },
//                                    label = { Text("Amount") },
//                                    placeholder = { Text("Enter payment amount") },
//                                    modifier = Modifier.fillMaxWidth(),
//                                    singleLine = true
//                                )
//
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                                ) {
//                                    Button(
//                                        onClick = { paymentAmount = bill.pending_amt.toString() },
//                                        modifier = Modifier.weight(1f),
//                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
//                                    ) {
//                                        Text("Full Amount")
//                                    }
//
//                                    OutlinedButton(
//                                        onClick = { paymentAmount = "" },
//                                        modifier = Modifier.weight(1f)
//                                    ) {
//                                        Text("Clear")
//                                    }
//                                }
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(24.dp))
//
//                        // Process Payment Button
//                        Button(
//                            onClick = {
//                                val amount = paymentAmount.toDoubleOrNull()
//                                if (amount != null && amount > 0) {
//                                    showProcessing = true
//                                    viewModel.processPayment(
//                                        bill = bill,
//                                        paymentMethod = selectedPaymentMethod,
//                                        amount = amount
//                                    )
//                                }
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp)
//                                .height(56.dp),
//                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
//                            enabled = paymentAmount.toDoubleOrNull()?.let { it > 0 } == true && !showProcessing
//                        ) {
//                            if (showProcessing) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier.size(20.dp),
//                                    color = Color.White
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text("Processing...")
//                            } else {
//                                Icon(
//                                    Icons.Default.Payment,
//                                    contentDescription = null,
//                                    modifier = Modifier.size(20.dp)
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text("Process Payment")
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//                }
//                is PaymentUiState.Error -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text(
//                                text = "Error: ${uiState.message}",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.error
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Button(
//                                onClick = { viewModel.loadBillDetails(billNo) },
//                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
//                            ) {
//                                Text("Retry")
//                            }
//                        }
//                    }
//                }
//                is PaymentUiState.PaymentSuccess -> {
//                    LaunchedEffect(Unit) {
//                        navController.popBackStack()
//                    }
//                }
//            }
//        }
//    }
//}
