package com.warriortech.resb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SecondaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.theme.YellowPrimary
import com.warriortech.resb.ui.theme.YellowSecondary
import com.warriortech.resb.ui.theme.ghostWhite
import com.warriortech.resb.ui.viewmodel.CounterViewModel
import com.warriortech.resb.util.AnimatedSnackbarDemo
import com.warriortech.resb.util.CurrencySettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
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
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(YellowPrimary, YellowSecondary)
                        ),
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton("Clear", Color.Red) { viewModel.clearOrder() }
                    ActionButton("Cash", Color(0xFF4CAF50)) {
                        viewModel.cashPrintBill()
                        scope.launch {
                            delay(3000)
                            snackbarHostState.showSnackbar("Bill Paid in Cash")
                        }
                    }
                    ActionButton("Others", Color.Gray) {
                        viewModel.placeOrder(2, null)
                        scope.launch {
                            // Simulate a delay for order processing
                            delay(3000)
                            navController.navigate("billing_screen/${orderId ?: ""}") {
                                launchSingleTop = true
                            }
                            onProceedToBilling(orderDetailsResponse, orderId ?: "")
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
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryGreen)
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                Text(
                    "ITEM",
                    modifier = Modifier.weight(3f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "QTY",
                    modifier = Modifier.weight(2f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "RATE",
                    modifier = Modifier.weight(2f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
                Text(
                    "TOTAL",
                    modifier = Modifier.weight(2f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }

           // Cart Table
            LazyColumn(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
            ) {
                items(
                    selectedItems.entries.toList(),
                    key = { entry -> "${entry.key.menu_id}_${entry.hashCode()}" }
                ) { entry ->
                    val item = entry.key
                    val qty = entry.value

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ITEM NAME
                        Text(
                            item.menu_item_name,
                            maxLines = 1,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(3f)
                        )
                        // QTY with + / - buttons
                        Row(modifier = Modifier.weight(2f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.removeItemFromOrder(item) }) {
                                Text("-")
                            }
                            Text("$qty",
                                fontSize = 12.sp)
                            IconButton(onClick = { viewModel.addItemToOrder(item) }) {
                                Text("+")
                            }
                        }
                        // RATE
                        Text(
                            "${item.rate}",
                            textAlign = TextAlign.End,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(2f)
                        )
                        // TOTAL
                        Text(
                            "${qty * item.rate}",
                            textAlign = TextAlign.End,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(2f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YellowPrimary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Items: ${selectedItems.values.sum()}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Total: ₹${selectedItems.entries.sumOf { it.key.rate * it.value }}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Product Grid
            when (val state = menuState) {
                is CounterViewModel.MenuUiState.Success -> {
                    val menuItems = state.menuItems
                    val filteredMenuItems =
                        if (selectedCategory != null && selectedCategory == "FAVOURITES") {
                            menuItems.filter { it.is_favourite == true }// Make sure selectedCategory is handled safely
                        } else if (selectedCategory != null) {
                            menuItems.filter { it.item_cat_name == selectedCategory }
                        } else {
                            menuItems
                        }
                    if (menuItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No menu items available")
                        }
                    } else {
                        if (categories.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = categories.indexOf(selectedCategory)
                                    .coerceAtLeast(0),
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
                                key = { index, product -> "${product.menu_id}_$index" }
                            ) { _, product ->
                                Card(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = { viewModel.addItemToOrder(product) }
                                            )
                                        },
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = ghostWhite
                                    )
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
                                                "₹${product.rate}",
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
}

@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(text, color = Color.White)
    }
}
