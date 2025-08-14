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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavHostController,
    viewModel: BillingViewModel = hiltViewModel(), // Shared ViewModel
    amountToPayFromRoute: Double? = null,
    orderMasterId: String? = null // If passing amount via route
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
            // Navigate to an order success/confirmation screen or back
            val successState = uiState.paymentProcessingState as PaymentProcessingState.Success
            // navController.navigate("order_success/${successState.transactionId}") {
            //     popUpTo("billing_screen_route_or_graph_start") { inclusive = true } // Clear back stack
            // }
            // For now, just show a snackbar and allow manual dismissal or pop back
//            snackbarHostState.showSnackbar("Payment Successful! TXN ID: ${successState.transactionId}")
             viewModel.resetPaymentState() // Reset for next payment
            navController.navigate("selects")
        } else if (uiState.paymentProcessingState is PaymentProcessingState.Error) {
            val errorState = uiState.paymentProcessingState as PaymentProcessingState.Error
            snackbarHostState.showSnackbar("Payment Failed: ${errorState.message}")
             viewModel.resetPaymentState() // Allow retry
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Complete Payment",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
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
                            onPaymentMethodChange = { viewModel.updatePaymentMethod(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentContent(
    modifier: Modifier = Modifier,
    uiState: BillingPaymentUiState,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    onAmountChange: (Double) -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    var amountText by remember(uiState.amountToPay) { mutableStateOf(uiState.amountToPay.format(2)) }


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Amount Due: ${currencyFormatter.format(uiState.totalAmount)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    it.toDoubleOrNull()?.let { numVal -> onAmountChange(numVal) }
                },
                label = { Text("Amount to Pay") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Text(currencyFormatter.currency?.symbol ?: "₹") }
            )
        }

        item {
            Text("Select Payment Method", style = MaterialTheme.typography.titleMedium)
        }

        if (uiState.availablePaymentMethods.isEmpty()) {
            item { Text("No payment methods available.") }
        } else {
            items(uiState.availablePaymentMethods) { method ->
                PaymentMethodItem(
                    method = method,
                    isSelected = uiState.selectedPaymentMethod?.id == method.id,
                    onClick = { onSelectPaymentMethod(method) }
                )
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(method.name, style = MaterialTheme.typography.bodyLarge)
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun PaymentBottomBar(
    uiState: BillingPaymentUiState,
    onConfirmPayment: () -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Paying: ${currencyFormatter.format(uiState.amountToPay)}",
                style = MaterialTheme.typography.titleMedium
            )
            MobileOptimizedButton(
                onClick = onConfirmPayment,
                enabled = uiState.selectedPaymentMethod != null &&
                        uiState.amountToPay > 0 &&
                        uiState.paymentProcessingState == PaymentProcessingState.Idle,
                text = "Confirm Payment"
            )
        }
    }
}
// Helper from BillingScreen (can be moved to a common util file)
// fun Double.format(digits: Int) = "%.${digits}f".format(this)