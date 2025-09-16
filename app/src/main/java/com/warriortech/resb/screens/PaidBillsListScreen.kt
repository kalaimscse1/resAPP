package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.warriortech.resb.R
import com.warriortech.resb.model.PaidBillSummary
import com.warriortech.resb.ui.components.EnhancedComponents
import com.warriortech.resb.ui.components.MobileOptimizedComponents
import com.warriortech.resb.ui.viewmodel.PaidBillsViewModel
import com.warriortech.resb.util.CurrencyUtil
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaidBillsListScreen(
    navController: NavHostController,
    viewModel: PaidBillsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.paid_bills)) },
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
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshPaidBills() },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Search Bar
                SearchBar(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::searchPaidBills,
                    onClearSearch = viewModel::clearSearch,
                    isSearching = uiState.isSearching,
                    modifier = Modifier.padding(16.dp)
                )

                // Content
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = uiState.isRefreshing),
                        onRefresh = { viewModel.refreshPaidBills() }
                    ) {
                        when {
                            uiState.isLoading && uiState.bills.isEmpty() -> {
                                LoadingContent()
                            }
                            uiState.bills.isEmpty() && !uiState.isLoading -> {
                                EmptyContent(
                                    isSearching = uiState.searchQuery.isNotEmpty()
                                )
                            }
                            else -> {
                                PaidBillsList(
                                    bills = uiState.bills,
                                    onEditClick = { bill ->
                                        navController.navigate("edit_paid_bill/${bill.id}")
                                    },
                                    onDeleteClick = viewModel::showDeleteDialog,
                                    onViewClick = { bill ->
                                        navController.navigate("view_paid_bill/${bill.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteDialog && uiState.billToDelete != null) {
        DeleteConfirmationDialog(
            bill = uiState.billToDelete,
            onConfirm = { 
                viewModel.deletePaidBill(uiState.billToDelete.id)
            },
            onDismiss = viewModel::hideDeleteDialog
        )
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search bills by customer name, bill number...") },
        leadingIcon = { 
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            Row {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            }
        },
        singleLine = true
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(isSearching: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (isSearching) Icons.Default.SearchOff else Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = if (isSearching) "No bills found for your search" else "No paid bills yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PaidBillsList(
    bills: List<PaidBillSummary>,
    onEditClick: (PaidBillSummary) -> Unit,
    onDeleteClick: (PaidBillSummary) -> Unit,
    onViewClick: (PaidBillSummary) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(bills) { bill ->
            PaidBillCard(
                bill = bill,
                onEditClick = { onEditClick(bill) },
                onDeleteClick = { onDeleteClick(bill) },
                onViewClick = { onViewClick(bill) }
            )
        }
    }
}

@Composable
private fun PaidBillCard(
    bill: PaidBillSummary,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onViewClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with bill number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bill #${bill.billNo}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = bill.customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Status badge
                Surface(
                    color = when (bill.status) {
                        "PAID" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        "REFUNDED" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = bill.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (bill.status) {
                            "PAID" -> Color(0xFF2E7D32)
                            "REFUNDED" -> Color(0xFFE65100)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount and payment method
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = CurrencyUtil.formatCurrency(bill.totalAmount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = bill.paymentMethod,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = dateFormatter.format(bill.paymentDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View")
                }

                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    bill: PaidBillSummary,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Bill")
        },
        text = {
            Text("Are you sure you want to delete bill #${bill.billNo} for ${bill.customerName}? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Placeholder for GradientStart, assuming it's defined elsewhere or a simple color
val GradientStart: Color = Color(0xFF007BFF) // Example color

// Placeholder for PullToRefreshBox, assuming it's a custom component or from a library
@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // This is a simplified placeholder. In a real app, you might use a library
    // like Accompanist SwipeRefresh or build a more sophisticated component.
    Box(modifier = modifier) {
        content()
        // A very basic visual indicator for demonstration
        if (isRefreshing) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter).padding(16.dp))
        }
    }
}