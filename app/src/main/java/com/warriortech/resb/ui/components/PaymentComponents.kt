package com.warriortech.resb.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import com.warriortech.resb.ui.viewmodel.BillingPaymentUiState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.warriortech.resb.ui.viewmodel.BillingViewModel
import com.warriortech.resb.util.CurrencySettings

@Composable
fun PaymentSummaryCard(uiState: BillingPaymentUiState) {
    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = "Payment Summary",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Payment Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

//            PaymentSummaryRow(label = "Subtotal", amount = "₹${uiState.subtotal}")
//            PaymentSummaryRow(label = "Tax", amount = "₹${uiState.taxAmount}")
//
//            Divider(
//                modifier = Modifier.padding(vertical = 12.dp),
//                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
//            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    CurrencySettings.format(uiState.amountToPay),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PaymentSummaryRow(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            amount,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PaymentMethodCard(
    uiState: BillingPaymentUiState,
    onPaymentMethodChange: (String) -> Unit,
    viewModel : BillingViewModel
) {
    // Cache the payment methods list to prevent recreation on every recomposition
    val paymentMethods = remember {
        listOf(
            "CASH" to Icons.Default.Money,
            "CARD" to Icons.Default.CreditCard,
            "UPI" to Icons.Default.QrCode,
            "OTHERS" to Icons.Default.MoreHoriz
        )
    }
    ModernCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Payment,
                    contentDescription = "Payment Method",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Payment Method",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            paymentMethods.forEach { (method, icon) ->
                // Use remember to create stable callback to prevent unnecessary recompositions
                val onSelectMethod = remember(method) { { onPaymentMethodChange(method) } }
                PaymentMethodOption(
                    method = method,
                    icon = icon,
                    isSelected = uiState.selectedPaymentMethod?.name == method,
                    onSelect = onSelectMethod
                )

                if (method != paymentMethods.last().first) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            if (uiState.selectedPaymentMethod?.name == "OTHERS") {
                Spacer(modifier = Modifier.height(16.dp))

                // Memoize the text field values to prevent unnecessary string conversions
                val cashValue = remember(uiState.cashAmount) { 
                    if (uiState.cashAmount == 0.0) "" else uiState.cashAmount.toString()
                }
                val cardValue = remember(uiState.cardAmount) { 
                    if (uiState.cardAmount == 0.0) "" else uiState.cardAmount.toString()
                }
                val upiValue = remember(uiState.upiAmount) { 
                    if (uiState.upiAmount == 0.0) "" else uiState.upiAmount.toString()
                }

                OutlinedTextField(
                    value = cashValue,
                    onValueChange = { viewModel.updateCashAmount(it.toDoubleOrNull() ?: 0.0) },
                    label = { Text("Cash") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cardValue,
                    onValueChange = { viewModel.updateCardAmount(it.toDoubleOrNull() ?: 0.0) },
                    label = { Text("Card") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = upiValue,
                    onValueChange = { viewModel.updateUpiAmount(it.toDoubleOrNull() ?: 0.0) },
                    label = { Text("UPI") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    method: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) 
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else 
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = method,
                tint = if (isSelected) MaterialTheme.colorScheme.primary 
                      else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                method,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}