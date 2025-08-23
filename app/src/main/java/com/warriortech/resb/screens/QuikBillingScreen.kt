package com.warriortech.resb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.ui.viewmodel.CounterViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.warriortech.resb.ui.theme.PrimaryGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemWiseBillScreen(
    viewModel: CounterViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Wise Bill", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
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
                    .background(Color(0xFFFF9800))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ITEM", color = Color.White, fontWeight = FontWeight.Bold)
                Text("QTY", color = Color.White, fontWeight = FontWeight.Bold)
                Text("RATE", color = Color.White, fontWeight = FontWeight.Bold)
                Text("TOTAL", color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Cart Table
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(selectedItems.entries.toList()) { entry ->
                    val item = entry.key
                    val qty = entry.value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.menu_item_name)
                        Text("$qty")
                        Text("₹${item.rate}")
                        Text("₹${qty * item.rate}")
                    }
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton("Clear", Color.Red) { viewModel.clearOrder() }
                ActionButton("Add Product", Color(0xFFFF9800)) { /* Add */ }
                ActionButton("Print", Color(0xFFFF9800)) { /* Print */ }
                ActionButton("Save", Color.Gray) { /* Save */ }
                ActionButton("Search", Color(0xFFFF9800)) { /* Search */ }
            }

            // Category Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                categories.forEach { category ->
                    Button(
                        onClick = { viewModel.selectedCategory.value = category },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategory == category) Color(0xFFFF9800) else Color.DarkGray
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    ) { Text(category) }
                }
            }

            // Product Grid
            when (menuState) {
                is CounterViewModel.MenuUiState.Success -> {
                    val item = (menuState as CounterViewModel.MenuUiState.Success).menuItems
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    )
                    {
                        items(item) { product ->
                            Card(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(product.menu_item_name, fontWeight = FontWeight.Bold)
                                    Text("₹${product.rate}")
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
        Text(text)
    }
}
