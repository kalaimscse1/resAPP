package com.warriortech.resb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScrollableTabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.ui.theme.*
import com.warriortech.resb.ui.viewmodel.CounterViewModel
import com.warriortech.resb.util.AnimatedSnackbarDemo
import com.warriortech.resb.util.CurrencySettings
import com.warriortech.resb.util.MessageBox
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.util.SuccessDialog
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemWiseBillScreen(
    viewModel: CounterViewModel = hiltViewModel(),
    drawerState: DrawerState,
    navController: NavHostController,
    onProceedToBilling: (orderDetailsResponse: List<TblOrderDetailsResponse>, orderId: String) -> Unit,
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val orderDetailsResponse by viewModel.orderDetailsResponse.collectAsStateWithLifecycle()
    val orderId by viewModel.orderId.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var showDialog by remember { mutableStateOf(false) }
    var showBillDialog by remember { mutableStateOf(false) }
    var cartOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    var success by remember { mutableStateOf(false) }
    var isProcessingCash by remember { mutableStateOf(false) }
    var isProcessingOthers by remember { mutableStateOf(false) }

    var values by remember { mutableStateOf<PaddingValues>(PaddingValues(0.dp)) }

    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
    }

    LaunchedEffect(selectedItems.size) {
        if (selectedItems.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(selectedItems.size - 1)
            }
        }
    }

    Scaffold(
        snackbarHost = { AnimatedSnackbarDemo(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Item Wise Bill", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(
                            Icons.Default.Menu, contentDescription = "Menu",
                            tint = SurfaceLight
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryGreen)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton("Clear", Color.Red, enabled = true) { showDialog = true }
                    ActionButton(
                        text = if (isProcessingCash) "Processing..." else "Cash",
                        color = Color(0xFF4CAF50),
                        enabled = !isProcessingCash
                    ) {
                        if (selectedItems.isNotEmpty()){
                            isProcessingCash = true
                            viewModel.cashPrintBill()
                            // Show immediate feedback without blocking delays
                            scope.launch {
                                success = true
                                delay(2000) // Reduced delay for success message
                                success = false
                                isProcessingCash = false
                            }
                        }
                        else{
                            showBillDialog = true
                        }
                    }
                    ActionButton(
                        text = if (isProcessingOthers) "Processing..." else "Others",
                        color = Color.Gray,
                        enabled = !isProcessingOthers
                    ) {
                        if(selectedItems.isNotEmpty()){
                            isProcessingOthers = true
                            viewModel.placeOrder(2, null)
                            // Place order first, then proceed to payment
                            scope.launch {
                                // Wait for order to be placed successfully
                                delay(2000) // Reduced delay for better UX
                                // Navigate to payment screen instead of billing screen
                                navController.navigate("payment_screen/${viewModel.getOrderTotal()}/${orderId ?: ""}") {
                                    launchSingleTop = true
                                }
                                isProcessingOthers = false
                            }
                        }
                        else{
                            showBillDialog = true
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            values = padding
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryGreen)
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                Text("ITEM", modifier = Modifier.weight(3f), color = Color.White, fontWeight = FontWeight.Bold)
                Text("QTY", modifier = Modifier.weight(2f), color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("RATE", modifier = Modifier.weight(2f), color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                Text("TOTAL", modifier = Modifier.weight(2f), color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            }

            // Cart Table
            LazyColumn(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                state = listState
            ) {
                items(
                    selectedItems.entries.toList(),
                    key = { entry -> "${entry.key.menu_item_id}_${entry.key.menu_id}" } // ✅ stable unique key
                ) { entry ->
                    val item = entry.key
                    val qty = entry.value

                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                .onGloballyPositioned { coords ->
                                    val pos = coords.localToWindow(Offset.Zero)
                                    cartOffset = with(density) { Offset(pos.x, pos.y) }
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.menu_item_name, maxLines = 1, fontSize = 12.sp, modifier = Modifier.weight(3f))
                            Row(
                                modifier = Modifier.weight(2f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Minus
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .border(1.dp, Color.Red, RoundedCornerShape(4.dp))
                                        .pointerInput(Unit) {
                                            detectTapGestures { viewModel.removeItemFromOrder(item) }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("-", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }

                                Text("$qty", fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))

                                // Plus
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .border(1.dp, DarkGreen, RoundedCornerShape(4.dp))
                                        .pointerInput(Unit) {
                                            detectTapGestures { viewModel.addItemToOrder(item) }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", color = DarkGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("${item.rate}", textAlign = TextAlign.End, fontSize = 12.sp, modifier = Modifier.weight(2f))
                            Text("${qty * item.rate}", textAlign = TextAlign.End, fontSize = 12.sp, modifier = Modifier.weight(2f))
                        }
                        Divider(color = Color.LightGray, thickness = 0.5.dp)
                    }
                }
            }

            // Total Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF9800))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Items: ${selectedItems.values.sum()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Total: ${CurrencySettings.format(selectedItems.entries.sumOf { it.key.rate * it.value })}",
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            // Product Grid + Tabs
            when (val state = menuState) {
                is CounterViewModel.MenuUiState.Loading -> {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is CounterViewModel.MenuUiState.Success -> {
                    val menuItems = state.menuItems
                    val filteredMenuItems = when {
                        selectedCategory == "FAVOURITES" -> menuItems.filter { it.is_favourite == true }
                        selectedCategory == "ALL" -> menuItems
                        selectedCategory != null -> menuItems.filter { it.item_cat_name == selectedCategory }
                        else -> menuItems
                    }

                    if (menuItems.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                            Text("No menu items available")
                        }
                    } else {
                        if (categories.isNotEmpty()) {
                            val selectedIndex = categories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0
                            ScrollableTabRow(
                                selectedTabIndex = selectedIndex,
                                backgroundColor = SecondaryGreen,
                                contentColor = SurfaceLight
                            ) {
                                categories.forEachIndexed { index, category ->
                                    Tab(
                                        selected = selectedCategory == category,
                                        onClick = { viewModel.selectedCategory.value = category },
                                        text = { Text(category) }
                                    )
                                }
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .padding(6.dp)
                        ) {
                            itemsIndexed(
                                filteredMenuItems,
                                key = { index, product -> "${product.menu_item_id}_${product.menu_id}_$index" } // ✅ unique key
                            ) { _, product ->
                                Card(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .pointerInput(Unit) {
                                            detectTapGestures { tapOffset ->
                                                val start = tapOffset
                                                val end = cartOffset
                                                FlyToCartController.current?.invoke(product, start, end)
                                                viewModel.addItemToOrder(product)
                                                // add item to cart in VM here
                                            }
                                        },
//                                        .pointerInput(Unit) {
//                                            detectTapGestures {  }
//                                        },
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(containerColor = ghostWhite)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row {
                                            Text(
                                                product.menu_item_name,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                        Row {
                                            Text(
                                                CurrencySettings.format(product.rate),
                                                maxLines = 1,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
    FlyToCartOverlay()
    if (showDialog){
        ClearDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.clearOrder()
                showDialog = false
            }
        )
    }
    if (showBillDialog){
        MessageBox(
            title = "Alert",
            message = "Please select items to proceed billing.",
            onDismiss = { showBillDialog = false }
        )
    }
    if (success) {
        SuccessDialog(
            title = "Payment Successful",
            description = "Payment Done Successfully",
            paddingValues = values
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    color: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color.copy(alpha = 0.6f)
        ),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun ClearDialog(
    onDismiss: () -> Unit,
    onConfirm:() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text("Clear Items") },
        text = { androidx.compose.material3.Text("Are you sure you want to Clear Items? ") },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                enabled = true
            ) {
                androidx.compose.material3.Text("Ok")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                androidx.compose.material3.Text("Cancel")
            }
        }
    )
}

object FlyToCartController {
    var current: ((TblMenuItemResponse, Offset, Offset) -> Unit)? = null
}

// Overlay composable that draws the flying item
@Composable
fun FlyToCartOverlay() {
    val scope = rememberCoroutineScope()
    var animItem by remember { mutableStateOf<String?>(null) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    if (animItem != null) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                .background(Color.White, RoundedCornerShape(6.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(6.dp))
                .padding(6.dp)
        ) {
            Text(animItem ?: "", fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }

    // expose controller
    FlyToCartController.current = { label, start, end ->
        animItem = label.menu_item_name
        scope.launch {
            offsetX.snapTo(start.x)
            offsetY.snapTo(start.y)
            offsetX.animateTo(end.x, animationSpec = tween(600))
            offsetY.animateTo(end.y, animationSpec = tween(600))
            animItem = null
        }
    }
}