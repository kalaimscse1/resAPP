package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.model.*
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.viewmodel.ReportViewModel
import com.warriortech.resb.ui.viewmodel.ReportUiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.forEach
import kotlin.collections.last

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val pullToRefreshState = rememberPullToRefreshState()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val snackbarHostState = remember { SnackbarHostState() }

//    LaunchedEffect(pullToRefreshState.isRefreshing) {
//        if (pullToRefreshState.isRefreshing) {
//            viewModel.refreshReports()
//            pullToRefreshState.endRefresh()
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Reports") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshReports() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                        IconButton(onClick = {
                            // TODO: Implement export functionality
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Export")
                        }
                    }
                )
            }
        ) { paddingValues ->
            when (uiState) {
                is ReportUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ReportUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (uiState as ReportUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        MobileOptimizedButton(
                            text = "Retry",
                            onClick = { viewModel.refreshReports() }
                        )
                    }
                }

                is ReportUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        this.item {
                            DateHeaderCard(
                                selectedDate = dateFormatter.format(Date()),
                                onDateClick = { showDatePicker = true }
                            )
                        }

                        val successState = uiState as ReportUiState.Success
//
//                        successState.todaySalesMetrics?.let { todaySalesMetrics ->
//                            item {
//                                TodaySalesCard(
//                                    metrics = todaySalesMetrics,
//                                    currencyFormatter = currencyFormatter
//                                )
//                            }
//                        }
//
//                        successState.todaySalesReport?.let { todaySalesReport ->
//                            item {
//                                GstBreakdownCard(
//                                    gstBreakdown = todaySalesReport.gstBreakdown,
//                                    currencyFormatter = currencyFormatter
//                                )
//                            }
//                            item {
//                                CessCard(
//                                    cessTotal = todaySalesReport.cessTotal,
//                                    currencyFormatter = currencyFormatter
//                                )
//                            }
//                            item {
//                                HsnSummaryCard(
//                                    hsnList = todaySalesReport.hsnSummary,
//                                    currencyFormatter = currencyFormatter
//                                )
//                            }
//                        }
                    }
                }
            }
        }

//        PullToRefreshContainer(
//            modifier = Modifier.align(Alignment.TopCenter),
//            state = pullToRefreshState,
//        )
    }
//
//    if (showDatePicker) {
//        DatePickerDialog(
//            onDateSelected = { date ->
//                viewModel.loadReportsForDate(dateFormatter.format(date))
//                showDatePicker = false
//            },
//            onDismiss = { showDatePicker = false }
//        )
//    }
}

@Composable
fun DateHeaderCard(
    selectedDate: String,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Report Date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedDate,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDateClick) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
            }
        }
    }
}

@Composable
fun TodaySalesCard(
    metrics: TodaySalesMetrics,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Today's Sales Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Total Revenue",
                    value = currencyFormatter.format(metrics.totalRevenue),
                    color = MaterialTheme.colorScheme.primary
                )
                MetricItem(
                    label = "Total Orders",
                    value = metrics.totalOrders.toString(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Avg Order Value",
                    value = currencyFormatter.format(metrics.averageOrderValue),
                    color = MaterialTheme.colorScheme.tertiary
                )
                MetricItem(
                    label = "Tax Collected",
                    value = currencyFormatter.format(metrics.taxCollected),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun MetricItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GstBreakdownCard(
    gstBreakdown: GstBreakdown,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "GST Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            TaxRow(
                label = "CGST",
                amount = gstBreakdown.cgst,
                currencyFormatter = currencyFormatter
            )
            TaxRow(
                label = "SGST",
                amount = gstBreakdown.sgst,
                currencyFormatter = currencyFormatter
            )
            TaxRow(
                label = "IGST",
                amount = gstBreakdown.igst,
                currencyFormatter = currencyFormatter
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            TaxRow(
                label = "Total GST",
                amount = gstBreakdown.totalGst,
                currencyFormatter = currencyFormatter,
                isTotal = true
            )
        }
    }
}

@Composable
fun CessCard(
    cessTotal: Double,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "CESS Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            TaxRow(
                label = "Total CESS Collected",
                amount = cessTotal,
                currencyFormatter = currencyFormatter,
                isTotal = true
            )
        }
    }
}

@Composable
fun HsnSummaryCard(
    hsnList: List<HsnSummary>,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "HSN Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            hsnList.forEach { hsn ->
                HsnSummaryItem(hsn = hsn, currencyFormatter = currencyFormatter)
                if (hsn != hsnList.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun HsnSummaryItem(
    hsn: HsnSummary,
    currencyFormatter: NumberFormat
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HSN: ${hsn.hsnCode}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = hsn.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Qty: ${hsn.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Taxable Value",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = currencyFormatter.format(hsn.taxableValue),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (hsn.cgst > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "CGST", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = currencyFormatter.format(hsn.cgst),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (hsn.sgst > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "SGST", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = currencyFormatter.format(hsn.sgst),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (hsn.igst > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "IGST", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = currencyFormatter.format(hsn.igst),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (hsn.cess > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "CESS", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = currencyFormatter.format(hsn.cess),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Tax",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.format(hsn.totalTax),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TaxRow(
    label: String,
    amount: Double,
    currencyFormatter: NumberFormat,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = currencyFormatter.format(amount),
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}