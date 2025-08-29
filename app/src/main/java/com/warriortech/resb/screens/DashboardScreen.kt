package com.warriortech.resb.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.viewmodel.DashboardViewModel
import com.warriortech.resb.model.DashboardMetrics
import com.warriortech.resb.model.RunningOrder
import kotlinx.coroutines.launch
import com.warriortech.resb.R
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.components.PaymentModePieChart
import com.warriortech.resb.ui.components.WeeklySalesBarChart
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.ResbTypography
import com.warriortech.resb.ui.theme.SurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun DashboardScreen(
    drawerState: DrawerState,
    onNavigateToOrders: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onDineInSelected: () -> Unit,
    onTakeawaySelected: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    sessionManager: SessionManager,
    onQuickBill:()->Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Column {
                            Text(
                                stringResource(R.string.dashboard),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceLight
                            )
                        }
                        Column {
                            Text(
                                "Real-time overview",
                                style = ResbTypography.bodySmall,
                                color = SurfaceLight,
                                modifier = Modifier.padding(start = 2.dp, top = 15.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = SurfaceLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshDashboard() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is DashboardViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading dashboard...")
                    }
                }
            }

            is DashboardViewModel.UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {

                    /**
                     * Quick Actions Section
                     */
                    item {
                        QuickActionsSection(
                            onNavigateToMenu = onNavigateToMenu,
                            onNavigateToOrders = onNavigateToOrders,
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToBilling = onNavigateToBilling,
                            onDineInSelected = onDineInSelected,
                            onTakeawaySelected = onTakeawaySelected,
                            sessionManager,
                            onQuickBill
                        )
                    }

                    /**
                     * Today's Metrics Section
                     */
                    item {
                        MetricsSection(
                            metrics = state.metrics,
                            onNavigateToOrders = onNavigateToOrders,
                            onNavigateToBilling = onNavigateToBilling
                        )
                    }

                    /**
                     * Payment Mode Chart Section
                     */
                    item {
                            PaymentModePieChart(
                                data =  state.piechart,
                                modifier = Modifier.fillMaxWidth()
                            )
                    }

                    /**
                     * Weekly Sales Chart Section
                     */
                    item {
                            WeeklySalesBarChart(
                                data =  state.barchart,
                                modifier = Modifier.fillMaxWidth()
                            )
                    }
                }
            }

            is DashboardViewModel.UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Error loading dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        MobileOptimizedButton(
                            onClick = { viewModel.refreshDashboard() },
                            text = "Retry"
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun MetricsSection(
    metrics: DashboardMetrics,
    onNavigateToOrders: () -> Unit,
    onNavigateToBilling: () -> Unit
) {
    Column {
        Text(
            "Today's Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
                MetricCard(
                    title = "Running Orders",
                    value = metrics.runningOrders.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onNavigateToOrders
                )

                MetricCard(
                    title = "Pending Bills",
                    value = metrics.pendingBills.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = onNavigateToBilling
                )

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

                MetricCard(
                    title = "Total Sales",
                    value = "₹${String.format("%.2f", metrics.totalSales)}",
                    color = Color(0xFF4CAF50)
                )

                MetricCard(
                    title = "Pending Due",
                    value = "₹${String.format("%.2f", metrics.pendingDue)}",
                    color = Color(0xFFF44336)
                )

        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    MobileOptimizedCard(
        modifier = Modifier
            .width(160.dp)
            .height(130.dp)
            .let { if (onClick != null) it else it },
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                    Text(
                        value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onNavigateToMenu: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onDineInSelected: () -> Unit,
    onTakeawaySelected: () -> Unit,
    sessionManager: SessionManager,
    onQuickBill:()->Unit
) {
    val role = sessionManager.getUser()?.role?:""
    var showOrderTypeDialog by remember { mutableStateOf(false) }

    Column {
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MobileOptimizedButton(
                onClick = { showOrderTypeDialog = true },
                text = "New Order",
                modifier = Modifier.weight(0.5f)
            )
            MobileOptimizedButton(
                onClick = onNavigateToOrders,
                text = "View Orders",
                modifier = Modifier.weight(0.5f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (role == "RESBADMIN" || role == "ADMIN")
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MobileOptimizedButton(
                    onClick = onNavigateToBilling,
                    text = "Billing",
                    modifier = Modifier.weight(1f)
                )
                MobileOptimizedButton(
                    onClick = onQuickBill,
                    text = "Quick Bill",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    if (showOrderTypeDialog) {
        AlertDialog(
            onDismissRequest = { showOrderTypeDialog = false },
            title = { Text("Select Order Type") },
            text = { Text("Please choose order type:") },
            confirmButton = {
                TextButton(onClick = {
                    showOrderTypeDialog = false
                    onTakeawaySelected()
                }) {
                    Text("Takeaway")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showOrderTypeDialog = false
                    onDineInSelected()
                }) {
                    Text("Dine-In")
                }

            }
        )
    }
}

@Composable
fun RunningOrdersSection(
    orders: List<RunningOrder>,
    onNavigateToOrders: () -> Unit
) {
    Column {
        Text(
            "Running Orders",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (orders.isEmpty()) {
            MobileOptimizedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No running orders",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            orders.forEach { order ->
                RunningOrderCard(
                    order = order,
                    onClick = { onNavigateToOrders() }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun RunningOrderCard(
    order: RunningOrder,
    onClick: () -> Unit
) {
    MobileOptimizedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "Order #${order.orderId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        order.tableInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (order.status) {
                            "PREPARING" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            "READY" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        order.status,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Items: ${order.itemCount}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "₹${String.format("%.2f", order.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "Time: ${order.orderTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun RecentActivitySection(
    activities: List<String>
) {
    Column {
        Text(
            "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        MobileOptimizedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (activities.isEmpty()) {
                    Text(
                        "No recent activity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    activities.forEach { activity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = null,
                                modifier = Modifier.size(8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                activity,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}