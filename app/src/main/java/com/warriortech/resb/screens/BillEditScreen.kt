package com.warriortech.resb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.ui.components.ModernDivider
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.viewmodel.PaidBillsViewModel
import com.warriortech.resb.util.CurrencySettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillEditScreen(
    navController: NavHostController,
    viewModel: PaidBillsViewModel = hiltViewModel()
) {
    val selectedBill by viewModel.selectedBill.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var editedNote by remember { mutableStateOf("") }
    var editedDiscountAmt by remember { mutableStateOf("") }
    var editedOthersAmt by remember { mutableStateOf("") }

    LaunchedEffect(selectedBill) {
        selectedBill?.let { bill ->
            editedNote = bill.note
            editedDiscountAmt = bill.disc_amt.toString()
            editedOthersAmt = bill.others.toString()
        }
    }

//    if (selectedBill == null) {
//        LaunchedEffect(Unit) {
//            navController.navigateUp()
//        }
//        return
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Bill #${selectedBill!!.bill_no}") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelection()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SurfaceLight)
                .verticalScroll(rememberScrollState())
        ) {
            selectedBill?.let { bill ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Bill Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )

                        ModernDivider(modifier = Modifier.padding(vertical = 8.dp))

                        BillInfoRow("Bill Number", bill.bill_no)
                        BillInfoRow("Date", "${bill.bill_date} ${bill.bill_create_time}")
                        BillInfoRow("Customer", bill.customer.customer_name)
                        BillInfoRow("Staff", bill.staff.staff_name)
                        BillInfoRow("Order Amount", CurrencySettings.format(bill.order_amt))
                        BillInfoRow("Tax Amount", CurrencySettings.format(bill.tax_amt))
                        BillInfoRow("Grand Total", CurrencySettings.format(bill.grand_total))
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Payment Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )

                        ModernDivider(modifier = Modifier.padding(vertical = 8.dp))

                        BillInfoRow("Cash", CurrencySettings.format(bill.cash))
                        BillInfoRow("Card", CurrencySettings.format(bill.card))
                        BillInfoRow("UPI", CurrencySettings.format(bill.upi))
                        BillInfoRow("Received Amount", CurrencySettings.format(bill.received_amt))
                        BillInfoRow("Change", CurrencySettings.format(bill.change))
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Editable Fields",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )

                        ModernDivider(modifier = Modifier.padding(vertical = 8.dp))

                        OutlinedTextField(
                            value = editedDiscountAmt,
                            onValueChange = { editedDiscountAmt = it },
                            label = { Text("Discount Amount") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedOthersAmt,
                            onValueChange = { editedOthersAmt = it },
                            label = { Text("Others Amount") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedNote,
                            onValueChange = { editedNote = it },
                            label = { Text("Note") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }

                Button(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Save Changes", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BillInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
