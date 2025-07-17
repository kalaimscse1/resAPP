
package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.warriortech.resb.ui.viewmodel.CounterViewModel
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.R
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.components.ResponsiveText
import com.warriortech.resb.ui.components.OptimizedInit
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.util.getDeviceInfo
import com.warriortech.resb.util.getScreenSizeInfo

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    onNavigateToPayment: () -> Unit,
    viewModel: CounterViewModel = hiltViewModel()
) {
    OptimizedInit()
    
    val deviceInfo = getDeviceInfo()
    val screenInfo = getScreenSizeInfo()
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    // Adaptive layout based on screen size
    if (screenInfo.isExpanded && screenInfo.isLandscape) {
        // Large tablet landscape: Side-by-side layout
        Row(modifier = Modifier.fillMaxSize()) {
            // Menu section
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                MenuSection(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    menuItems = uiState.menuItems,
                    cartItems = uiState.cartItems,
                    onCategorySelected = viewModel::selectCategory,
                    onAddToCart = viewModel::addToCart,
                    onRemoveFromCart = viewModel::removeFromCart,
                    deviceInfo = deviceInfo,
                    isCompactLayout = false
                )
            }
            
            // Cart section
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(start = 8.dp)
            ) {
                CartSection(
                    cartItems = uiState.cartItems,
                    totalAmount = uiState.totalAmount,
                    onRemoveFromCart = viewModel::removeFromCart,
                    onNavigateToPayment = onNavigateToPayment,
                    deviceInfo = deviceInfo,
                    isCompactLayout = false
                )
            }
        }
    } else {
        // Mobile and tablet portrait: Tabbed layout
        var selectedTab by remember { mutableIntStateOf(0) }
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        ResponsiveText(
                            text = stringResource(R.string.menu),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        ResponsiveText(
                            text = "${stringResource(R.string.cart)} (${uiState.cartItems.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
            
            // Tab content
            AnimatedVisibility(
                visible = selectedTab == 0,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                MenuSection(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    menuItems = uiState.menuItems,
                    cartItems = uiState.cartItems,
                    onCategorySelected = viewModel::selectCategory,
                    onAddToCart = viewModel::addToCart,
                    onRemoveFromCart = viewModel::removeFromCart,
                    deviceInfo = deviceInfo,
                    isCompactLayout = true
                )
            }
            
            AnimatedVisibility(
                visible = selectedTab == 1,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                CartSection(
                    cartItems = uiState.cartItems,
                    totalAmount = uiState.totalAmount,
                    onRemoveFromCart = viewModel::removeFromCart,
                    onNavigateToPayment = onNavigateToPayment,
                    deviceInfo = deviceInfo,
                    isCompactLayout = true
                )
            }
        }
    }
}

@Composable
private fun MenuSection(
    categories: List<String>,
    selectedCategory: String,
    menuItems: List<MenuItem>,
    cartItems: Map<String, Int>,
    onCategorySelected: (String) -> Unit,
    onAddToCart: (MenuItem) -> Unit,
    onRemoveFromCart: (MenuItem) -> Unit,
    deviceInfo: com.warriortech.resb.util.DeviceInfo,
    isCompactLayout: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = deviceInfo.optimalSpacing)
    ) {
        // Categories
        if (categories.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.height(if (deviceInfo.isTablet) 60.dp else 50.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        onClick = { onCategorySelected(category) },
                        label = {
                            ResponsiveText(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        selected = category == selectedCategory,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Menu items grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(deviceInfo.optimalColumnCount),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(deviceInfo.optimalSpacing),
            verticalArrangement = Arrangement.spacedBy(deviceInfo.optimalSpacing)
        ) {
            items(menuItems) { item ->
                MenuItemCard(
                    item = item,
                    quantity = cartItems[item.id] ?: 0,
                    onAddToCart = { onAddToCart(item) },
                    onRemoveFromCart = { onRemoveFromCart(item) },
                    deviceInfo = deviceInfo
                )
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    quantity: Int,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    deviceInfo: com.warriortech.resb.util.DeviceInfo
) {
    MobileOptimizedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (deviceInfo.isTablet) 200.dp else 180.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Item info
            Column {
                ResponsiveText(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                ResponsiveText(
                    text = "₹${item.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GradientStart,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (item.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ResponsiveText(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Quantity controls
            if (quantity > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onRemoveFromCart,
                        modifier = Modifier.size(if (deviceInfo.isTablet) 48.dp else 40.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    ResponsiveText(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(if (deviceInfo.isTablet) 48.dp else 40.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = GradientStart
                        )
                    }
                }
            } else {
                MobileOptimizedButton(
                    onClick = onAddToCart,
                    text = stringResource(R.string.add),
                    icon = Icons.Default.Add,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CartSection(
    cartItems: Map<String, Int>,
    totalAmount: Double,
    onRemoveFromCart: (MenuItem) -> Unit,
    onNavigateToPayment: () -> Unit,
    deviceInfo: com.warriortech.resb.util.DeviceInfo,
    isCompactLayout: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = deviceInfo.optimalSpacing)
    ) {
        if (cartItems.isEmpty()) {
            // Empty cart state
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.RemoveShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(if (deviceInfo.isTablet) 72.dp else 64.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ResponsiveText(
                    text = stringResource(R.string.empty_cart),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        } else {
            // Cart items
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cart items would be displayed here
                // Implementation depends on your cart item structure
            }
            
            // Total and checkout
            Column {
                MobileOptimizedCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResponsiveText(
                            text = stringResource(R.string.total),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        ResponsiveText(
                            text = "₹${String.format("%.2f", totalAmount)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GradientStart
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                MobileOptimizedButton(
                    onClick = onNavigateToPayment,
                    text = stringResource(R.string.proceed_to_payment),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cartItems.isNotEmpty()
                )
            }
        }
    }
}
