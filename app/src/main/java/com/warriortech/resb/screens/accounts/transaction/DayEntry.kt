package com.warriortech.resb.screens.accounts.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.warriortech.resb.ui.theme.*
import com.warriortech.resb.util.AnimatedSnackbarDemo
import com.warriortech.resb.util.MessageBox
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScrollableTabRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.ui.viewmodel.LedgerDetailsViewModel
import com.warriortech.resb.util.SuccessDialog
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEntryScreen(
    viewModel: LedgerDetailsViewModel = hiltViewModel(),
    drawerState: DrawerState,
    navController: NavHostController,
) {
    val ledgerDetailsState by viewModel.ledgerDetailsState.collectAsStateWithLifecycle()
    val transactionUiState by viewModel.transactionState.collectAsStateWithLifecycle()
    val selectedLedger by viewModel.selectedLedger.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var showDialog by remember { mutableStateOf(false) }
    var showBillDialog by remember { mutableStateOf(false) }
    var cartOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    var success by remember { mutableStateOf(false) }
    var isProcessingCash by remember { mutableStateOf(false) }
    var isProcessingOthers by remember { mutableStateOf(false) }
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<String>("") }



    var values by remember { mutableStateOf<PaddingValues>(PaddingValues(0.dp)) }


    Scaffold(
        snackbarHost = { AnimatedSnackbarDemo(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Day Entry", color = Color.White) },
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
                actions = {

                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = SecondaryGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SecondaryGreen, SecondaryGreen)
                        ),
                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton("Clear", Color.Red, enabled = true) { showDialog = true }
                    ActionButton(
                        text = if (isProcessingCash) "Processing..." else "Save",
                        color = Color(0xFF4CAF50),
                        enabled = !isProcessingCash
                    ) {

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
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryGreen)
                .padding(vertical = 8.dp, horizontal = 8.dp)
            ){
                Column {
                    RadioButton(
                        selected = true,
                        onClick = { },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White
                        )
                    )
                    Text("CASH", color = Color.White)
                }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Column {
                    RadioButton(
                        selected = false,
                        onClick = { },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White
                        )
                    )
                    Text("CARD", color = Color.White)
                }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Column {
                    RadioButton(
                        selected = false,
                        onClick = { },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White
                        )
                    )
                    Text("UPI", color = Color.White)
                }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Column{
                    RadioButton(
                        selected = false,
                        onClick = { },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White
                        )
                    )
                    Text("OTHERS", color = Color.White)
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryGreen)
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                Text(
                    "LedgerName",
                    modifier = Modifier.weight(3f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
//                Text(
//                    "LedgerFullName",
//                    modifier = Modifier.weight(2f),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    textAlign = TextAlign.Center
//                )
                Text(
                    "Debit",
                    modifier = Modifier.weight(2f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
                Text(
                    "Credit",
                    modifier = Modifier.weight(2f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                state = listState
            ) {

                items(selectedLedger) { entry ->
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                .padding(start = 4.dp, end = 4.dp)
                                .onGloballyPositioned { coords ->
                                    val pos = coords.localToWindow(Offset.Zero)
                                    cartOffset = with(density) { Offset(pos.x, pos.y) }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                entry.ledger_name,
                                maxLines = 1,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(3f)
                            )
//                            Row(
//                                modifier = Modifier.weight(2f),
//                                horizontalArrangement = Arrangement.Center,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(32.dp)
//                                        .border(1.dp, Color.Red, RoundedCornerShape(4.dp))
//                                        .pointerInput(Unit) {
//                                            detectTapGestures { viewModel.removeItemFromOrder(item) }
//                                        },
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        "-",
//                                        color = Color.Red,
//                                        fontSize = 14.sp,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//
//                                Text(
//                                    "$qty",
//                                    fontSize = 14.sp,
//                                    modifier = Modifier.padding(horizontal = 4.dp)
//                                )
//
//                                Box(
//                                    modifier = Modifier
//                                        .size(32.dp)
//                                        .border(1.dp, DarkGreen, RoundedCornerShape(4.dp))
//                                        .pointerInput(Unit) {
//                                            detectTapGestures { viewModel.addItemToOrder(item) }
//                                        },
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        "+",
//                                        color = DarkGreen,
//                                        fontSize = 14.sp,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//                            }
//                            Text(
//                                "${item.rate}",
//                                textAlign = TextAlign.End,
//                                fontSize = 12.sp,
//                                modifier = Modifier.weight(2f)
//                            )
//                            Text(
//                                "${qty * item.rate}",
//                                textAlign = TextAlign.End,
//                                fontSize = 12.sp,
//                                modifier = Modifier.weight(2f)
//                            )
                        }
                        Divider(color = Color.LightGray, thickness = 0.5.dp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryGreen)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Entries: ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "Total: ",
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                }
            }
            when (val state = ledgerDetailsState){
                is LedgerDetailsViewModel.LedgerDetailsUiState.Loading->{
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LedgerDetailsViewModel.LedgerDetailsUiState.Success-> {
                    val groups = state.groups
                    val ledgerDetails = state.ledgers
                    val filteredLedgerDetails = if (selectedCategory.isNotEmpty())
                        ledgerDetails.filter { it.group.group_nature.g_nature_name == selectedCategory }
                    else
                        ledgerDetails

                    if (ledgerDetails.isEmpty()) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Ledger available")
                        }
                    } else {
                        if (categories.isNotEmpty()) {
                            val selectedIndex =
                                categories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0
                            ScrollableTabRow(
                                selectedTabIndex = selectedIndex,
                                backgroundColor = SecondaryGreen,
                                contentColor = SurfaceLight
                            ) {
                                categories.forEachIndexed { index, category ->
                                    Tab(
                                        selected = selectedCategory == category,
                                        onClick = { selectedCategory = category },
                                        text = { Text(category) }
                                    )
                                }
                            }
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .padding(6.dp)
                        ) {
                            itemsIndexed(
                                filteredLedgerDetails,
                                key = { index, product -> "${product.ledger_name}_${product.ledger_id}_$index" }
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
                                                FlyToCartControllers.current?.invoke(
                                                    product,
                                                    start,
                                                    end
                                                )
                                                viewModel.addItemToOrder(product)
                                            }
                                        },
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
                                                product.ledger_name,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is LedgerDetailsViewModel.LedgerDetailsUiState.Error->{
                    Text(
                        "Error: ${state.message}",
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
    FlyToCartOverlay()
    if (showDialog) {
        ClearDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
//                viewModel.clearOrder()
                showDialog = false
            }
        )
    }
    if (showBillDialog) {
        MessageBox(
            title = "Alert",
            message = "Please select items to proceed billing.",
            onDismiss = { showBillDialog = false }
        )
    }
    if (success) {
        SuccessDialog(
            title = "Bill Successful",
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
    onConfirm: () -> Unit
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

object FlyToCartControllers {
    var current: ((TblLedgerDetails, Offset, Offset) -> Unit)? = null
}

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

    FlyToCartControllers.current = { label, start, end ->
        animItem = label.ledger_name
        scope.launch {
            offsetX.snapTo(start.x)
            offsetY.snapTo(start.y)
            offsetX.animateTo(end.x, animationSpec = tween(600))
            offsetY.animateTo(end.y, animationSpec = tween(600))
            animItem = null
        }
    }
}