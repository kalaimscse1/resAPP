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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.warriortech.resb.ui.theme.GradientStart

/**
 * DashboardScreen displays the main dashboard with real-time metrics,
 * quick actions, running orders, and recent activity.
 * It provides an overview of the restaurant's operations,
 * including today's metrics, quick actions for common tasks,
 * a list of running orders, and recent activity logs.
 * This screen is designed to be responsive and optimized for mobile devices,
 * allowing restaurant staff to quickly access important information
 * and perform actions efficiently.
 * It includes sections for:
 * - Today's Metrics: Displays key metrics like running orders, pending bills, total sales, and pending due.
 * - Quick Actions: Provides buttons for common tasks like creating a new order, viewing orders, billing, and settings.
 * - Running Orders: Lists currently active orders with details like order ID, table info, status, item count, and amount.
 * - Recent Activity: Shows a log of recent activities in the restaurant, such as order updates or billing actions.
 * This screen uses a [Scaffold] to provide a top app bar and handles loading states,
 * error states, and success states using a [LazyColumn] for displaying content.
 * It also includes a top app bar with a title, navigation icon, and refresh action.
 * @param drawerState The state of the navigation drawer, used to open/close the drawer.
 * @param onNavigateToOrders Callback to navigate to the orders screen.
 * @param onNavigateToMenu Callback to navigate to the menu screen.
 * @param onNavigateToSettings Callback to navigate to the settings screen.
 * @param onNavigateToBilling Callback to navigate to the billing screen.
 * @param viewModel The [DashboardViewModel] instance to manage the dashboard data.
 * This screen is part of the restaurant management application,
 * providing a centralized view for staff to monitor and manage daily operations.
 * @author WarriorTech
 * @version 1.0
 * @date 2025-07-17
 */

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun DashboardScreen(
    drawerState: DrawerState,
    onNavigateToOrders: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToBilling: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
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
                    Column {
                        Text(
                            stringResource(R.string.dashboard),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Real-time overview",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshDashboard() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
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
                     * Quick Actions Section
                     */
                    item {
                        QuickActionsSection(
                            onNavigateToMenu = onNavigateToMenu,
                            onNavigateToOrders = onNavigateToOrders,
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToBilling = onNavigateToBilling
                        )
                    }

                    /**
                     * Running Orders Section
                     */
                    item {
                        RunningOrdersSection(
                            runningOrders = state.runningOrders,
                            onOrderClick = { orderId ->
                                // Navigate to specific order details
                                onNavigateToOrders()
                            }
                        )
                    }

                    /**
                     * Recent Activity Section
                     */
                    item {
                        RecentActivitySection(
                            recentActivity = state.recentActivity
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

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                MetricCard(
                    title = "Running Orders",
                    value = metrics.runningOrders.toString(),
                    icon = Icons.Default.Restaurant,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onNavigateToOrders
                )
            }
            item {
                MetricCard(
                    title = "Pending Bills",
                    value = metrics.pendingBills.toString(),
                    icon = Icons.Default.Receipt,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = onNavigateToBilling
                )
            }
            item {
                MetricCard(
                    title = "Total Sales",
                    value = "₹${String.format("%.2f", metrics.totalSales)}",
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFF4CAF50)
                )
            }
            item {
                MetricCard(
                    title = "Pending Due",
                    value = "₹${String.format("%.2f", metrics.pendingDue)}",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    MobileOptimizedCard(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .let { if (onClick != null) it else it },
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
    onNavigateToBilling: () -> Unit
) {
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
                onClick = onNavigateToMenu,
                text = "New Order",
                modifier = Modifier.weight(1f)
            )
            MobileOptimizedButton(
                onClick = onNavigateToOrders,
                text = "View Orders",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                onClick = onNavigateToSettings,
                text = "Settings",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RunningOrdersSection(
    runningOrders: List<RunningOrder>,
    onOrderClick: (Long) -> Unit
) {
    Column {
        Text(
            "Running Orders",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (runningOrders.isEmpty()) {
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
            runningOrders.forEach { order ->
                RunningOrderCard(
                    order = order,
                    onClick = { onOrderClick(order.orderId) }
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
    recentActivity: List<String>
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
                if (recentActivity.isEmpty()) {
                    Text(
                        "No recent activity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    recentActivity.forEach { activity ->
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