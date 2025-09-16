package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.R
import com.warriortech.resb.model.PaidBill
import com.warriortech.resb.ui.theme.GradientStart

import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaidBillScreen(
    navController: NavHostController,
    billId: Long,
) {
    val snackbarHostState = remember { SnackbarHostState() }



    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_bill)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Edit Bill Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // TODO: Add form fields for editing bill
                    Text(
                        text = "Bill ID: $billId",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    // Placeholder for form implementation
                    Text(
                        text = "Form fields will be implemented here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EditBillContent(
    bill: PaidBill,
    modifier: Modifier = Modifier,
    onSave: (PaidBill) -> Unit
) {
    var customerName by remember { mutableStateOf(bill.customerName) }
    var customerPhone by remember { mutableStateOf(bill.customerPhone) }
    var notes by remember { mutableStateOf(bill.notes ?: "") }
    
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bill Information (Read-only)
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.bill_information),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                BillInfoRow(stringResource(R.string.bill_number), bill.billNo)
                BillInfoRow(stringResource(R.string.order_id), bill.orderMasterId.toString())
                BillInfoRow(stringResource(R.string.table_number), bill.tableNo)
                BillInfoRow(stringResource(R.string.payment_date), dateFormatter.format(bill.paymentDate))
                BillInfoRow(stringResource(R.string.payment_method), bill.paymentMethod)
                BillInfoRow(stringResource(R.string.status), bill.status)
            }
        }

        // Editable Customer Information
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.customer_information),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(stringResource(R.string.customer_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text(stringResource(R.string.customer_phone)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Bill Items (Read-only)
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.bill_items),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                bill.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.itemName} x ${item.qty}",
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = "₹${item.amount}")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                BillInfoRow(stringResource(R.string.subtotal), "₹${bill.subtotal}")
                BillInfoRow(stringResource(R.string.tax), "₹${bill.taxAmount}")
                BillInfoRow(stringResource(R.string.discount), "₹${bill.discountAmount}")
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${bill.totalAmount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Notes
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.notes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.add_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        }

        // Save Button
        Button(
            onClick = {
                val updatedBill = bill.copy(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    notes = notes.takeIf { it.isNotBlank() }
                )
                onSave(updatedBill)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.save_changes))
        }
    }
}

@Composable
fun BillInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
