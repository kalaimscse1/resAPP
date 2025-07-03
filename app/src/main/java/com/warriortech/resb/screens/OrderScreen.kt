package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.ui.viewmodel.OrderScreenViewModel
import kotlinx.coroutines.launch
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblOrderDetailsResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    drawerState: DrawerState,
    viewModel: OrderScreenViewModel = hiltViewModel(),
    onNavigateToBilling: (List<TblOrderDetailsResponse>) -> Unit,
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
                title = {
                    Text(
                        "Orders Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading orders...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    OrderSection(
                        title = "Dine In",
                        icon = Icons.Default.Restaurant,
                        orders = dineInOrders,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onOrderClick = { order ->
                            // Handle order click
                            val orderId = order.orderId
                            onNavigateToBilling(viewModel.getOrdersByOrderId(orderId))
                        }
                    )
                }

                item {
                    OrderSection(
                        title = "Takeaway",
                        icon = Icons.Default.DirectionsCar,
                        orders = takeawayOrders,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onOrderClick = { order ->
                            // Handle order click
                            val orderId = order.orderId
                            onNavigateToBilling(viewModel.getOrdersByOrderId(orderId))
                        }

                    )
                }

                item {
                    OrderSection(
                        title = "Delivery",
                        icon = Icons.Default.DeliveryDining,
                        orders = deliveryOrders,
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        onOrderClick = { order ->
                            // Handle order click
                            val orderId = order.orderId
                            onNavigateToBilling(viewModel.getOrdersByOrderId(orderId))
                        }
                    )
                }
            }
        }
    }
}



@Composable
private fun OrderSection(
    title: String,
    icon: ImageVector,
    orders: List<OrderDisplayItem>,
    onOrderClick: (OrderDisplayItem) -> Unit,
    backgroundColor: Color,
    contentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = contentColor.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "${orders.size}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }

            if (orders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                orders.forEach { order ->
                    OrderItem(
                        order = order,
                        contentColor = contentColor,
                        onClick = { onOrderClick(order) }
                    )
                    if (order != orders.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No orders yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderItem(
    order: OrderDisplayItem,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = contentColor.copy(alpha = 0.1f)
        ),
        onClick= onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.orderId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = "${order.areaName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${order.tableName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${order.totalAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (order.status) {
                            "PENDING" -> Color(0xFFFF9800)
                            "PREPARING" -> Color(0xFF2196F3)
                            "READY" -> Color(0xFF4CAF50)
                            else -> Color(0xFF757575)
                        }
                    )
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

//            if (order.items.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Items: ${order.items.joinToString(", ")}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = contentColor.copy(alpha = 0.7f),
//                    maxLines = 2
//                )
//            }
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