package com.warriortech.resb.screens

import android.R
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScrollableTabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.ui.components.MobileOptimizedButton
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.components.ModernDivider
import com.warriortech.resb.ui.theme.DarkGreen
import com.warriortech.resb.ui.theme.ErrorRed
import com.warriortech.resb.ui.theme.GradientStart
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SecondaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.theme.TextPrimary
import com.warriortech.resb.ui.theme.ghostWhite
import com.warriortech.resb.util.AnimatedSnackbarDemo
import com.warriortech.resb.util.ensureLastItemVisible
import com.warriortech.resb.util.getDeviceInfo
import com.warriortech.resb.util.scrollToBottomSmooth

@SuppressLint("StateFlowValueCalledInComposition", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    onBackPressed: () -> Unit,
    onProceedToBilling: (orderDetailsResponse: List<TblOrderDetailsResponse>,orderId: String) -> Unit,
    viewModel: CounterViewModel = hiltViewModel(),
    drawerState: DrawerState,
    counterId: Long? = null
) {
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val showModifierDialog by viewModel.showModifierDialog.collectAsStateWithLifecycle()
    val selectedMenuItemForModifier by viewModel.selectedMenuItemForModifier.collectAsStateWithLifecycle()
    val modifierGroups by viewModel.modifierGroups.collectAsStateWithLifecycle()
    val orderDetailsResponse by viewModel.orderDetailsResponse.collectAsStateWithLifecycle()
    val orderId by viewModel.orderId.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
    }

    LaunchedEffect(counterId) {
        if (counterId != null) {
            // Load counter information by ID
            // This would typically come from a repository call
        }
    }

    if (showConfirmDialog) {
        BillConfirmationDialog(// Use effectiveStatus
            onConfirm = {
                // The tableId passed here is the one this screen was launched with.
                // For takeaway/delivery, it might be a specific ID like 0, 1, or 2 as per your existing logic.
                // For table orders, it's the actual tableId.
                onProceedToBilling(orderDetailsResponse,orderId?:"") // Use effectiveStatus
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val currentCounter by viewModel.currentCounter.collectAsStateWithLifecycle()
                    Text(
                        if (currentCounter != null)
                            "Counter Billing - ${currentCounter!!.code}"
                        else
                            "Counter Billing",
                        color = SurfaceLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu",
                            tint = SurfaceLight)
                    }
                },
                actions = {
//                    IconButton(onClick = {
//                        onBackPressed() // Reset category filter
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.Menu,
//                            contentDescription = "All Categories"
//                        )
//                    }
                    IconButton(onClick = {
                        viewModel.clearOrder()
                    }) {
                        Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = "Clear Cart",
                            tint = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val totalItemCount = selectedItems.values.sum()
                    val totalAmount = viewModel.getOrderTotal()

                    Column {
                        Text(
                            text = "Total Items: $totalItemCount",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total: ₹${String.format("%.2f", totalAmount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    MobileOptimizedButton(
                        onClick = {
                            viewModel.placeOrder(2,"")
                            showConfirmDialog=true
                            },
                        enabled = selectedItems.isNotEmpty(),
                        text = "Proceed to Bill",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        snackbarHost = {
            AnimatedSnackbarDemo(snackbarHostState)
        }
    ) { paddingValues ->

        when (val currentMenuState = menuState) {
            is CounterViewModel.MenuUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

//            is CounterViewModel.MenuUiState.Success -> {
//                val menuItems = currentMenuState.menuItems
//                val filteredMenuItems = if (selectedCategory != null) {
//                    menuItems.filter { it.item_cat_name == selectedCategory }
//                } else {
//                    menuItems
//                }
//
//                if (menuItems.isEmpty()) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValues),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text("No menu items available")
//                    }
//                } else {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValues)
//                            .background(color = Color.White)
//                    ) {
//                        // Categories TabRow
//                        if (categories.isNotEmpty()) {
//                            ScrollableTabRow(
//                                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
//                                backgroundColor = Color.White,
//                                contentColor = TextPrimary
//                            ) {
//                                categories.forEachIndexed { index, category ->
//                                    Tab(
//                                        selected = selectedCategory == category,
//                                        onClick = { viewModel.selectedCategory.value = category },
//                                        text = { androidx.compose.material.Text(category) }
//                                    )
//                                }
//                            }
//                        }
//
//                        LazyColumn(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(top = 2.dp)
//                                .background(color = Color.White),
//                            verticalArrangement = Arrangement.spacedBy(8.dp),
//                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
//                        ) {
//                            item { Spacer(modifier = Modifier.padding(top = 5.dp)) }
//
//                            items(filteredMenuItems, key = { it.menu_item_id }) { menuItem ->
//                                CounterMenuItemCard(
//                                    menuItem = menuItem,
//                                    quantity = selectedItems[menuItem] ?: 0,
//                                    onAddItem = { viewModel.addItemToOrder(menuItem) },
//                                    onRemoveItem = { viewModel.removeItemFromOrder(menuItem) },
//                                    onModifierClick = {
//                                        viewModel.showModifierDialog(menuItem)
//                                    }
//                                )
//                            }
//                            item { Spacer(modifier = Modifier.height(80.dp)) }
//                        }
//                    }
//                }
//            }

            is CounterViewModel.MenuUiState.Success -> {

                val menuItems = currentMenuState.menuItems
                val filteredMenuItems = if (selectedCategory != null && selectedCategory=="FAVOURITES") {
                    menuItems.filter { it.is_favourite == true }// Make sure selectedCategory is handled safely
                } else if (selectedCategory != null) {
                    menuItems.filter { it.item_cat_name == selectedCategory }
                }
                else {
                    menuItems
                }

                    if (menuItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No menu items available")
                    }
                } else {
                    val listState = rememberLazyListState()
                    val scope = rememberCoroutineScope()
                    val bottomBarHeight = 80.dp

                    // 👇 Reusable helper: auto-correct when last item overlaps BottomBar
                    listState.ensureLastItemVisible(bottomBarHeight)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(color = Color.White)
                    ) {
                        // Categories TabRow
                        if (categories.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                                backgroundColor = SecondaryGreen,
                                contentColor = SurfaceLight
                            ) {
                                categories.forEachIndexed { index, category ->
                                    Tab(
                                        selected = selectedCategory == category,
                                        onClick = { viewModel.selectedCategory.value = category },
                                        text = { androidx.compose.material.Text(category) }
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 2.dp)
                                .background(color = Color.White),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(5.dp)) }

                            items(filteredMenuItems, key = { it.menu_item_id }) { menuItem ->
                                CounterMenuItemCard(
                                    menuItem = menuItem,
                                    quantity = selectedItems[menuItem] ?: 0,
                                    onAddItem = {
                                        viewModel.addItemToOrder(menuItem)
                                        scope.scrollToBottomSmooth(listState) // 👈 reusable
                                    },
                                    onRemoveItem = {
                                        viewModel.removeItemFromOrder(menuItem)
                                        scope.scrollToBottomSmooth(listState) // 👈 reusable
                                    },
                                    onModifierClick = { viewModel.showModifierDialog(menuItem) }
                                )
                            }

                            // 👇 Spacer ensures last item never hides under BottomBar
                            item { Spacer(modifier = Modifier.height(bottomBarHeight)) }
                        }
                    }
                }
            }

            is CounterViewModel.MenuUiState.Error -> {
                val errorMessage = (menuState as CounterViewModel.MenuUiState.Error).message

                LaunchedEffect(errorMessage) {
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Failed to load menu items")
                }
            }
        }
    }
    if (showModifierDialog && selectedMenuItemForModifier != null) {
        ModifierSelectionDialog(
            menuItem = selectedMenuItemForModifier!!,
            availableModifiers = modifierGroups,
            selectedModifiers = emptyList(),
            onModifiersSelected = { selectedModifiers ->
                viewModel.addMenuItemWithModifiers(selectedMenuItemForModifier!!, selectedModifiers)
            },
            onDismiss = { viewModel.hideModifierDialog() }
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CounterMenuItemCard(
    menuItem: TblMenuItemResponse,
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    onModifierClick: () -> Unit,
) {
    val IconBoldA: ImageVector = ImageVector.Builder(
        name = "BoldA",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.NonZero
        ) {
            // Outer A shape
            moveTo(12f, 4f)      // Top point
            lineTo(3f, 20f)      // Bottom left
            lineTo(7f, 20f)      // Left foot
            lineTo(8.6f, 16.8f)  // Up left leg to crossbar
            lineTo(15.4f, 16.8f) // Across crossbar
            lineTo(17f, 20f)     // Down right leg to right foot
            lineTo(21f, 20f)     // Bottom right
            close()

            // Crossbar
            moveTo(9.6f, 14f)
            lineTo(14.4f, 14f)
            lineTo(12f, 8.8f)
            close()
        }
    }.build()

    val deviceInfo = getDeviceInfo()
    val cornerRadius = if (deviceInfo.isTablet) 24.dp else 20.dp
    Card(
        modifier = Modifier.fillMaxWidth()
            .border(2.dp, SecondaryGreen, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = ghostWhite
        ),

        ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Row {
                        Text(
                            text = menuItem.menu_item_name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (menuItem.menu_item_name_tamil.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = menuItem.menu_item_name_tamil,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                if (menuItem.is_available=="YES") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("₹${String.format("%.2f", menuItem.rate)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 22.dp))
                        IconButton(
                            onClick = onRemoveItem
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Remove",
                                tint = ErrorRed
                            )
                        }

                        if (quantity > 0) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = quantity.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(36.dp))
                        }

                        IconButton(
                            onClick = onAddItem
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = DarkGreen
                            )
                        }
                        IconButton(
                            onClick = onModifierClick
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Modifiers",
                                tint = Color.Blue
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Not Available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }


            AnimatedVisibility(visible = quantity > 0) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$quantity × ₹${String.format("%.2f", menuItem.rate)}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "₹${String.format("%.2f", quantity * menuItem.rate)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BillConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            MobileOptimizedButton(
                onClick = onConfirm,
                text = "Confirm",
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            MobileOptimizedButton(
                onClick = onDismiss,
                text = "Cancel",
                modifier = Modifier.fillMaxWidth()
            )
        },
        title = {
            Text("Confirm To Bill", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Text(
                "Are you sure you want to proceed with billing? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}