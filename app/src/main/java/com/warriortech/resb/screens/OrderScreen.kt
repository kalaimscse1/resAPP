
package com.warriortech.resb.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.ui.viewmodel.OrderScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    drawerState: DrawerState,
    viewModel: OrderScreenViewModel = hiltViewModel()
) {
    val dineInOrders by viewModel.dineInOrders.collectAsStateWithLifecycle()
    val takeawayOrders by viewModel.takeawayOrders.collectAsStateWithLifecycle()
    val deliveryOrders by viewModel.deliveryOrders.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OrderTypeSection(
                        title = "Dine-In",
                        icon = Icons.Default.Restaurant,
                        orders = dineInOrders,
                        onOrderClick = { order ->
                            // Handle order click
                            val orderId = order.orderId
                        }
                    )
                }

                item {
                    OrderTypeSection(
                        title = "Takeaway",
                        icon = Icons.Default.DirectionsCar,
                        orders = takeawayOrders,
                        onOrderClick = { order ->
                            // Handle order click

                        }
                    )
                }

                item {
                    OrderTypeSection(
                        title = "Delivery",
                        icon = Icons.Default.DeliveryDining,
                        orders = deliveryOrders,
                        onOrderClick = { order ->
                            // Handle order click
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderTypeSection(
    title: String,
    icon: ImageVector,
    orders: List<OrderDisplayItem>,
    onOrderClick: (OrderDisplayItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Badge {
                    Text(orders.size.toString())
                }
            }

            if (orders.isEmpty()) {
                Text(
                    text = "No orders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                orders.forEach { order ->
                    OrderItem(
                        order = order,
                        onClick = { onOrderClick(order) }
                    )
                    if (order != orders.last()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun OrderItem(
    order: OrderDisplayItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.labelMedium,
                    color = when (order.status) {
                        "PENDING" -> MaterialTheme.colorScheme.error
                        "PREPARING" -> MaterialTheme.colorScheme.primary
                        "READY" -> MaterialTheme.colorScheme.tertiary
                        "COMPLETED" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            if (order.areaName != null) {
                Text(
                    text = order.areaName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (order.tableName != null) {
                Text(
                    text = order.tableName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "₹${String.format("%.2f", order.totalAmount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = order.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class OrderDisplayItem(
    val orderId: Long,
    val areaName: String?,
    val tableName: String?,
    val totalAmount: Double,
    val status: String,
    val timestamp: String,
    val orderType: String
)
