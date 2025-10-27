//package com.warriortech.resb.screens.accounts.transaction
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.Icon
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.IconButton
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.Text
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import com.warriortech.resb.ui.theme.*
//import com.warriortech.resb.util.AnimatedSnackbarDemo
//import com.warriortech.resb.util.MessageBox
//import kotlinx.coroutines.launch
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.itemsIndexed
//import androidx.compose.foundation.lazy.items
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.ScrollableTabRow
//import androidx.compose.material.icons.filled.CalendarMonth
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.IntOffset
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.warriortech.resb.model.TblLedgerDetails
//import com.warriortech.resb.ui.components.ModernDivider
//import com.warriortech.resb.ui.viewmodel.LedgerDetailsViewModel
//import com.warriortech.resb.util.LedgerDropdown
//import com.warriortech.resb.util.SuccessDialog
//import java.time.LocalDate
//import kotlin.math.roundToInt
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DayEntryScreen(
//    viewModel: LedgerDetailsViewModel = hiltViewModel(),
//    drawerState: DrawerState,
//    navController: NavHostController,
//) {
//    val ledgerDetailsState by viewModel.ledgerDetailsState.collectAsStateWithLifecycle()
//    val transactionUiState by viewModel.transactionState.collectAsStateWithLifecycle()
//    val ledgerList by viewModel.ledgerList.collectAsStateWithLifecycle()
//    val selectedLedger by viewModel.selectedLedger.collectAsStateWithLifecycle()
//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val listState = rememberLazyListState()
//    var showDialog by remember { mutableStateOf(false) }
//    var showBillDialog by remember { mutableStateOf(false) }
//    var cartOffset by remember { mutableStateOf(Offset.Zero) }
//    val density = LocalDensity.current
//    var success by remember { mutableStateOf(false) }
//    var isProcessingCash by remember { mutableStateOf(false) }
//    var isProcessingOthers by remember { mutableStateOf(false) }
//    val categories by viewModel.categories.collectAsStateWithLifecycle()
//    var selectedCategory by remember { mutableStateOf<String>("") }
//    var cashRadio by remember { mutableStateOf(true) }
//    var cardRadio by remember { mutableStateOf(false) }
//    var upiRadio by remember { mutableStateOf(false) }
//    var otherRadio by remember { mutableStateOf(false) }
//    var showDatePicker by remember { mutableStateOf(false) }
//    var values by remember { mutableStateOf<PaddingValues>(PaddingValues(0.dp)) }
//    var selcetsDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
//    var partyId by remember { mutableStateOf(1) }
//    var remark by remember { mutableStateOf("") }
//    var debit by remember { mutableStateOf("0") }
//    var cerdit by remember { mutableStateOf("0") }
//
//    Scaffold(
//        snackbarHost = { AnimatedSnackbarDemo(snackbarHostState) },
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row {
//                        Column {
//                            Text(
//                                "Day Entry",
//                                style = MaterialTheme.typography.headlineSmall,
//                                fontWeight = FontWeight.Bold,
//                                color = SurfaceLight
//                            )
//                        }
//                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
//                        Column {
//                            Text(
//                                "Entry No:",
//                                style = ResbTypography.headlineSmall,
//                                color = SurfaceLight,
//                            )
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = PrimaryGreen
//                ),
//                navigationIcon = {
//                    IconButton(onClick = {
//                        scope.launch { drawerState.open() }
//                    }) {
//                        Icon(
//                            Icons.Default.Menu, contentDescription = "Menu",
//                            tint = SurfaceLight
//                        )
//                    }
//                },
//                actions = {
//                    Column {
//                        Row {
//                            Text(
//                                "$selcetsDate",
//                                color = SurfaceLight
//                            )
//                        }
//                        Row {
//                            IconButton(onClick = {
//                                showDatePicker = true
//                            }) {
//                                Icon(
//                                    Icons.Default.CalendarMonth, contentDescription = "Calender",
//                                    tint = SurfaceLight
//                                )
//                            }
//                        }
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            BottomAppBar(
//                containerColor = SecondaryGreen,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(SecondaryGreen, SecondaryGreen)
//                        ),
//                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
//                    )
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    ActionButton("Clear", Color.Red, enabled = true) { showDialog = true }
//                    ActionButton(
//                        text = if (isProcessingCash) "Processing..." else "Save",
//                        color = Color(0xFF4CAF50),
//                        enabled = !isProcessingCash
//                    ) {
//
//                    }
//                }
//            }
//
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            values = padding
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(SecondaryGreen)
//                    .padding(vertical = 8.dp, horizontal = 8.dp)
//            ) {
//                Column {
//                    RadioButton(
//                        selected = cashRadio,
//                        onClick = {
//                            otherRadio = false
//                            cashRadio = true
//                            cardRadio = false
//                            upiRadio = false
//                        },
//                        colors = RadioButtonDefaults.colors(
//                            selectedColor = Color.White,
//                            unselectedColor = Color.White
//                        )
//                    )
//                    Text("CASH", color = Color.White)
//                }
//                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
//                Column {
//                    RadioButton(
//                        selected = cardRadio,
//                        onClick = {
//                            otherRadio = false
//                            cashRadio = false
//                            cardRadio = true
//                            upiRadio = false
//                        },
//                        colors = RadioButtonDefaults.colors(
//                            selectedColor = Color.White,
//                            unselectedColor = Color.White
//                        )
//                    )
//                    Text("CARD", color = Color.White)
//                }
//                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
//                Column {
//                    RadioButton(
//                        selected = upiRadio,
//                        onClick = {
//                            otherRadio = false
//                            cashRadio = false
//                            cardRadio = false
//                            upiRadio = true
//                        },
//                        colors = RadioButtonDefaults.colors(
//                            selectedColor = Color.White,
//                            unselectedColor = Color.White
//                        )
//                    )
//                    Text("UPI", color = Color.White)
//                }
//                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
//                Column {
//                    RadioButton(
//                        selected = otherRadio,
//                        onClick = {
//                            otherRadio = true
//                            cashRadio = false
//                            cardRadio = false
//                            upiRadio = false
//                        },
//                        colors = RadioButtonDefaults.colors(
//                            selectedColor = Color.White,
//                            unselectedColor = Color.White
//                        )
//                    )
//                    Text("OTHERS", color = Color.White)
//                }
//                Column {
//                    if (otherRadio) {
//                        LedgerDropdown(
//                            ledgers = ledgerList,
//                            selectedLedger = ledgerList.find { it.ledger_id == partyId },
//                            onLedgerSelected = {
//                                partyId = it.ledger_id
//                            },
//                            modifier = Modifier.fillMaxWidth(),
//                            label = "Select Ledger"
//                        )
//                    }
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(SecondaryGreen)
//                    .padding(vertical = 8.dp, horizontal = 4.dp)
//            ) {
//                Text(
//                    "LedgerName",
//                    modifier = Modifier.weight(3f),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    "Remarks",
//                    modifier = Modifier.weight(2f),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    textAlign = TextAlign.Center
//                )
//                Text(
//                    "Debit",
//                    modifier = Modifier.weight(2f),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    textAlign = TextAlign.End
//                )
//                Text(
//                    "Credit",
//                    modifier = Modifier.weight(2f),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    textAlign = TextAlign.End
//                )
//            }
//
//            LazyColumn(
//                modifier = Modifier
//                    .weight(0.5f)
//                    .fillMaxWidth(),
//                state = listState
//            ) {
//
//                items(selectedLedger) { entry ->
//                    Column {
//                        Row(
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 2.dp, vertical = 2.dp)
//                                .onGloballyPositioned { coords ->
//                                    val pos = coords.localToWindow(Offset.Zero)
//                                    cartOffset = with(density) { Offset(pos.x, pos.y) }
//                                },
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                entry.ledger_name,
//                                maxLines = 1,
//                                fontSize = 12.sp,
//                                modifier = Modifier.weight(1f)
//                            )
//                            OutlinedTextField(
//                                value = remark,
//                                onValueChange = {
//                                    remark = it
//                                },
//                                label = { androidx.compose.material3.Text("Remarks") },
//                                modifier = Modifier.weight(2f),
//                                singleLine = true
//                            )
//                            OutlinedTextField(
//                                value = debit,
//                                onValueChange = {
//                                    debit = it
//                                },
//                                label = { androidx.compose.material3.Text("Debit") },
//                                modifier = Modifier.weight(2f),
//                                singleLine = true
//                            )
//                            OutlinedTextField(
//                                value = cerdit,
//                                onValueChange = {
//                                    cerdit = it
//                                },
//                                label = { androidx.compose.material3.Text("Credit") },
//                                modifier = Modifier.weight(2f),
//                                singleLine = true
//                            )
//                        }
//                    }
//                    ModernDivider(color = Color.LightGray, thickness = 0.5.dp)
//                }
//            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(SecondaryGreen)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        "Total Entries: ${selectedLedger.size}",
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp
//                    )
//                    Text(
//                        "Total:  ",
//                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
//                    )
//                }
//            }
//            when (val state = ledgerDetailsState) {
//                is LedgerDetailsViewModel.LedgerDetailsUiState.Loading -> {
//                    Box(
//                        Modifier
//                            .fillMaxSize()
//                            .padding(padding),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//
//                is LedgerDetailsViewModel.LedgerDetailsUiState.Success -> {
//                    val groups = state.groups
//                    val ledgerDetails = state.ledgers
//                    val filteredLedgerDetails = if (selectedCategory.isNotEmpty())
//                        ledgerDetails.filter { it.group.group_nature.g_nature_name == selectedCategory }
//                    else
//                        ledgerDetails
//
//                    if (ledgerDetails.isEmpty()) {
//                        Box(
//                            Modifier
//                                .fillMaxSize()
//                                .padding(padding),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("No Ledger available")
//                        }
//                    } else {
//                        if (categories.isNotEmpty()) {
//                            val selectedIndex =
//                                categories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0
//                            ScrollableTabRow(
//                                selectedTabIndex = selectedIndex,
//                                backgroundColor = SecondaryGreen,
//                                contentColor = SurfaceLight
//                            ) {
//                                categories.forEachIndexed { index, category ->
//                                    Tab(
//                                        selected = selectedCategory == category,
//                                        onClick = { selectedCategory = category },
//                                        text = { Text(category) }
//                                    )
//                                }
//                            }
//                        }
//                        LazyVerticalGrid(
//                            columns = GridCells.Fixed(3),
//                            modifier = Modifier
//                                .fillMaxHeight(0.5f)
//                                .padding(6.dp)
//                        ) {
//                            itemsIndexed(
//                                filteredLedgerDetails,
//                                key = { index, product -> "${product.ledger_name}_${product.ledger_id}_$index" }
//                            ) { _, product ->
//                                Card(
//                                    modifier = Modifier
//                                        .padding(4.dp)
//                                        .fillMaxWidth()
//                                        .clip(MaterialTheme.shapes.medium)
//                                        .pointerInput(Unit) {
//                                            detectTapGestures { tapOffset ->
//                                                val start = tapOffset
//                                                val end = cartOffset
//                                                FlyToCartControllers.current?.invoke(
//                                                    product,
//                                                    start,
//                                                    end
//                                                )
//                                                viewModel.addItemToOrder(product)
//                                            }
//                                        },
//                                    elevation = CardDefaults.cardElevation(4.dp),
//                                    shape = RoundedCornerShape(6.dp),
//                                    colors = CardDefaults.cardColors(containerColor = ghostWhite)
//                                ) {
//                                    Column(
//                                        modifier = Modifier.padding(10.dp),
//                                        horizontalAlignment = Alignment.CenterHorizontally
//                                    ) {
//                                        Row {
//                                            Text(
//                                                product.ledger_name,
//                                                fontWeight = FontWeight.Bold,
//                                                maxLines = 1,
//                                                textAlign = TextAlign.Center,
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                is LedgerDetailsViewModel.LedgerDetailsUiState.Error -> {
//                    Text(
//                        "Error: ${state.message}",
//                        modifier = Modifier
//                            .padding(padding)
//                            .padding(16.dp)
//                    )
//                }
//            }
//        }
//    }
//    if (showDatePicker) {
//        val datePickerState = rememberDatePickerState()
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        datePickerState.selectedDateMillis?.let { millis ->
//                            val selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
//                            selcetsDate = selectedDate
//                        }
//                        showDatePicker = false
//                    }
//                ) {
//                    androidx.compose.material3.Text("OK")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDatePicker = false }) {
//                    androidx.compose.material3.Text("Cancel")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//    FlyToCartOverlay()
//    if (showDialog) {
//        ClearDialog(
//            onDismiss = { showDialog = false },
//            onConfirm = {
//                viewModel.clear()
//                remark =""
//                debit = "0"
//                cerdit = "0"
//                partyId = 1
//                cardRadio = false
//                upiRadio = false
//                otherRadio = false
//                cashRadio = true
//                showDialog = false
//            }
//        )
//    }
//    if (showBillDialog) {
//        MessageBox(
//            title = "Alert",
//            message = "Please select items to proceed billing.",
//            onDismiss = { showBillDialog = false }
//        )
//    }
//    if (success) {
//        SuccessDialog(
//            title = "Bill Successful",
//            description = "Payment Done Successfully",
//            paddingValues = values
//        )
//    }
//}
//
//@Composable
//fun ActionButton(
//    text: String,
//    color: Color,
//    enabled: Boolean = true,
//    onClick: () -> Unit
//) {
//    Button(
//        onClick = onClick,
//        enabled = enabled,
//        colors = ButtonDefaults.buttonColors(
//            containerColor = color,
//            disabledContainerColor = color.copy(alpha = 0.6f)
//        ),
//        modifier = Modifier.padding(horizontal = 2.dp)
//    ) {
//        Text(text, color = Color.White)
//    }
//}
//
//@Composable
//fun ClearDialog(
//    onDismiss: () -> Unit,
//    onConfirm: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { androidx.compose.material3.Text("Clear Items") },
//        text = { androidx.compose.material3.Text("Are you sure you want to Clear Items? ") },
//        confirmButton = {
//            Button(
//                onClick = { onConfirm() },
//                enabled = true
//            ) {
//                androidx.compose.material3.Text("Ok")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                androidx.compose.material3.Text("Cancel")
//            }
//        }
//    )
//}
//
//object FlyToCartControllers {
//    var current: ((TblLedgerDetails, Offset, Offset) -> Unit)? = null
//}
//
//@Composable
//fun FlyToCartOverlay() {
//    val scope = rememberCoroutineScope()
//    var animItem by remember { mutableStateOf<String?>(null) }
//    val offsetX = remember { Animatable(0f) }
//    val offsetY = remember { Animatable(0f) }
//
//    if (animItem != null) {
//        Box(
//            modifier = Modifier
//                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
//                .background(Color.White, RoundedCornerShape(6.dp))
//                .border(1.dp, Color.Gray, RoundedCornerShape(6.dp))
//                .padding(6.dp)
//        ) {
//            Text(animItem ?: "", fontWeight = FontWeight.Bold, color = Color.Black)
//        }
//    }
//
//    FlyToCartControllers.current = { label, start, end ->
//        animItem = label.ledger_name
//        scope.launch {
//            offsetX.snapTo(start.x)
//            offsetY.snapTo(start.y)
//            offsetX.animateTo(end.x, animationSpec = tween(600))
//            offsetY.animateTo(end.y, animationSpec = tween(600))
//            animItem = null
//        }
//    }
//}

package com.warriortech.resb.screens.accounts.transaction

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.warriortech.resb.model.TblLedgerDetailIdRequest
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.screens.ActionButton
import com.warriortech.resb.screens.ClearDialog
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.ResbTypography
import com.warriortech.resb.ui.theme.SecondaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.ui.theme.ghostWhite
import com.warriortech.resb.ui.viewmodel.LedgerDetailsViewModel
import com.warriortech.resb.util.AnimatedSnackbarDemo
import com.warriortech.resb.util.LedgerDropdown
import com.warriortech.resb.util.SuccessDialogWithButton
import com.warriortech.resb.util.getCurrentDateModern
import com.warriortech.resb.util.getCurrentTimeModern
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Refactored DayEntryScreen with improved UI/UX, validation, totals and save hook.
 *
 * Hooks to implement in LedgerDetailsViewModel:
 *  - fun addItemToOrder(item: TblLedgerDetails)
 *  - fun clear()
 *  - suspend fun saveDayEntries(date: LocalDate, paymentMode: PaymentMode, entries: List<TransactionEntry>, remark: String, partyId: Int): Result<Unit>
 *
 * Adjust method names if your viewModel uses different naming.
 */

//@SuppressLint("ConfigurationScreenWidthHeight")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DayEntryScreen(
//    viewModel: LedgerDetailsViewModel = hiltViewModel(),
//    drawerState: DrawerState,
//    navController: NavHostController,
//) {
//    val ledgerDetailsState by viewModel.ledgerDetailsState.collectAsStateWithLifecycle()
//    val transactionState by viewModel.transactionState.collectAsStateWithLifecycle()
//    val ledgerList by viewModel.ledgerList.collectAsStateWithLifecycle()
//    val entryNo by viewModel.entryNo.collectAsStateWithLifecycle()
//    val categories by viewModel.categories.collectAsStateWithLifecycle()
//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val voucher by viewModel.voucher.collectAsStateWithLifecycle()
//    val density = LocalDensity.current
//
//    // --- Screen state ---
//    var selectedCategory by remember { mutableStateOf<String>("") }
//    var showDatePicker by remember { mutableStateOf(false) }
//    var selectedDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
//
//    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
//    var partyId by remember { mutableStateOf<Int>(1) }
//    var otherPartyId by remember { mutableStateOf<Int?>(null) } // ledger id when OTHERS selected
//    var globalRemark by remember { mutableStateOf("") }
//
//    // Entries: map ledgerId -> TransactionEntry (keeps amounts per ledger)
//    val entriesState = remember { mutableStateMapOf<Long, TblLedgerDetailIdRequest>() }
//
//    // derived totals (efficient recomposition)
//    val totals by remember {
//        derivedStateOf {
//            val list = entriesState.values.toList()
//            val totalDebit = list.sumOf { it.amount_out }
//            val totalCredit = list.sumOf { it.amount_in }
//            Totals(totalDebit, totalCredit)
//        }
//    }
//
//    // Processing states
//    var isSaving by remember { mutableStateOf(false) }
//    var showClearDialog by remember { mutableStateOf(false) }
//    var showBillDialog by remember { mutableStateOf(false) }
//    var showSuccess by remember { mutableStateOf(false) }
//
//    // For fly-to-cart animation
//    var cartOffset by remember { mutableStateOf(Offset.Zero) }
//    FlyToCartControllers.reset()
//
//    // Layout responsiveness
//    val configuration = LocalConfiguration.current
//    val narrowScreen = configuration.screenWidthDp < 600
//
//    LaunchedEffect(transactionState) {
//        when(val state = transactionState){
//            is LedgerDetailsViewModel.TransactionUiState.Success -> {
//                showSuccess = true
//            }
//            is LedgerDetailsViewModel.TransactionUiState.Error ->{
//                scope.launch { snackbarHostState.showSnackbar("Entry Failed To Add")}
//            }
//            is LedgerDetailsViewModel.TransactionUiState.Loading ->{
//
//            }
//            is LedgerDetailsViewModel.TransactionUiState.Idle ->{
//
//            }
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { AnimatedSnackbarDemo(snackbarHostState) },
//        topBar = {
//            TopAppBar(
//                title = {
//                    Column {
//                        Text(
//                            "Day Entry",
//                            style = MaterialTheme.typography.headlineSmall,
//                            fontWeight = FontWeight.Bold,
//                            color = SurfaceLight
//                        )
//                        Text("Entry No: $entryNo ", style = ResbTypography.labelSmall, color = SurfaceLight)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen),
//                navigationIcon = {
//                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
//                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = SurfaceLight)
//                    }
//                },
//                actions = {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Text(selectedDate.toString(), color = SurfaceLight)
//                        IconButton(onClick = { showDatePicker = true }) {
//                            Icon(
//                                Icons.Default.CalendarMonth,
//                                contentDescription = "Calendar",
//                                tint = SurfaceLight
//                            )
//                        }
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            BottomAppBar(containerColor = SecondaryGreen) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                ) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Text(
//                            "Entries: ${entriesState.size}",
//                            color = SurfaceLight,
//                            fontWeight = FontWeight.SemiBold
//                        )
//                        Spacer(Modifier.width(12.dp))
//                        Text(
//                            "Debit: ${totals.debit}",
//                            color = SurfaceLight,
//                            fontWeight = FontWeight.SemiBold
//                        )
//                        Spacer(Modifier.width(8.dp))
//                        Text(
//                            "Credit: ${totals.credit}",
//                            color = SurfaceLight,
//                            fontWeight = FontWeight.SemiBold
//                        )
//                    }
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceEvenly
//                    ) {
//                        ActionButton("Clear", Color.Red, enabled = true) { showClearDialog = true }
////                        Spacer(Modifier.width(8.dp))
//                        ActionButton(
//                            if (isSaving) "Saving..." else "Save", enabled = !isSaving,
//                            color = Color(0xFF4CAF50)
//                        ) {
//                            if (entriesState.isEmpty()) {
//                                scope.launch { snackbarHostState.showSnackbar("Add at least one ledger entry") }
//
//                            }
//                            // call save
//                            scope.launch {
//                                isSaving = true
//                                val entries = entriesState.values.toList()
//                                viewModel.addLedgerDetails(entries)
//                                isSaving = false
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//    ) { padding ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(8.dp)
//        ) {
//            // Payment mode row
//            PaymentModeRow(
//                paymentMode = paymentMode,
//                onModeChange = { paymentMode = it },
//                ledgerList = ledgerList,
//                otherPartyId = otherPartyId,
//                onOtherSelected = { otherPartyId = it },
//                party = partyId,
//                onPartySelected = { partyId = it }
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(0.35f),
//                shape = RoundedCornerShape(8.dp)
//            ) {
//                Column(modifier = Modifier.padding(8.dp)) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text("Ledger", fontWeight = FontWeight.Bold)
//                        Text("Remarks", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
//                        Text("Debit", fontWeight = FontWeight.Bold)
//                        Text("Credit", fontWeight = FontWeight.Bold)
//                        Spacer(modifier = Modifier.width(24.dp))
//                    }
//                    ModernDivider(color = Color.LightGray, thickness = 0.5.dp)
//                    LazyColumn {
//                        items(entriesState.keys.toList(), key = { it }) { ledgerId ->
//                            val entry = entriesState[ledgerId] ?: return@items
//                            val ledgerName = ledgerList.find { it.ledger_id.toLong() == ledgerId }?.ledger_name ?: ""
//                            EntryRow(
//                                entry = entry,
//                                onDebitChange = { new ->
//                                    entriesState[ledgerId] =
//                                        entry.copy(amount_out = new.coerceAtLeast(0.0))
//                                },
//                                onCreditChange = { new ->
//                                    entriesState[ledgerId] =
//                                        entry.copy(amount_in = new.coerceAtLeast(0.0))
//                                },
//                                onRemarkChange = { r ->
//                                    entriesState[ledgerId] = entry.copy(purpose = r)
//                                },
//                                onRemove = {
//                                    entriesState.remove(ledgerId)
//                                },
//                                ledgerName = ledgerName
//                            )
//                            ModernDivider(color = Color.LightGray, thickness = 0.5.dp)
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        Text("Total Debit: ${totals.debit}", fontWeight = FontWeight.SemiBold)
//                        Spacer(Modifier.width(12.dp))
//                        Text("Total Credit: ${totals.credit}", fontWeight = FontWeight.SemiBold)
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Category tabs
//            if (ledgerDetailsState is LedgerDetailsViewModel.LedgerDetailsUiState.Success && categories.isNotEmpty()) {
//                val cats = categories
//                val selectedIndex = cats.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0
//                ScrollableTabRow(
//                    selectedTabIndex = selectedIndex,
//                    containerColor = SecondaryGreen,
//                    contentColor = SurfaceLight
//                ) {
//                    cats.forEachIndexed { idx, cat ->
//                        Tab(
//                            selected = selectedCategory == cat,
//                            onClick = { selectedCategory = cat },
//                            text = { Text(cat) })
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Ledgers area (grid or list)
//            when (val state = ledgerDetailsState) {
//                is LedgerDetailsViewModel.LedgerDetailsUiState.Loading -> Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//
//                is LedgerDetailsViewModel.LedgerDetailsUiState.Error -> Text(
//                    "Error: ${state.message}",
//                    modifier = Modifier.padding(16.dp)
//                )
//
//                is LedgerDetailsViewModel.LedgerDetailsUiState.Success -> {
//                    val ledgerDetails = state.ledgers
//                    val filtered =
//                        if (selectedCategory.isNotEmpty()) ledgerDetails.filter { it.group.group_nature.g_nature_name == selectedCategory } else ledgerDetails
//
//                    if (filtered.isEmpty()) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(120.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("No ledger found")
//                        }
//                    } else {
//                        LazyVerticalGrid(
//                            columns = GridCells.Fixed(3),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .weight(0.45f)
//                        ) {
//                            itemsIndexed(
//                                filtered,
//                                key = { index, product -> "${product.ledger_id}_$index" }) { _, product ->
//                                Card(
//                                    modifier = Modifier
//                                        .padding(6.dp)
//                                        .fillMaxWidth()
//                                        .clip(MaterialTheme.shapes.medium)
//                                        .pointerInput(Unit) {
//                                            detectTapGestures { _ ->
//                                                addOrFocusEntry(
//                                                    product,
//                                                    entriesState,
//                                                    viewModel,
//                                                    entryNo,
//                                                    voucher,
//                                                    partyId = otherPartyId?:partyId
//                                                )
//                                            }
//                                        },
//                                    elevation = CardDefaults.cardElevation(4.dp),
//                                    shape = RoundedCornerShape(6.dp),
//                                    colors = CardDefaults.cardColors(containerColor = ghostWhite)
//                                ) {
//                                    Column(
//                                        modifier = Modifier.padding(10.dp),
//                                        horizontalAlignment = Alignment.CenterHorizontally
//                                    ) {
//                                        Text(
//                                            product.ledger_name,
//                                            fontWeight = FontWeight.Bold,
//                                            maxLines = 1,
//                                            textAlign = TextAlign.Center
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }

//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Entries list (editable)
//
//        }
//    }
//
//    // Date picker dialog
//    if (showDatePicker) {
//        val datePickerState = rememberDatePickerState()
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(onClick = {
//                    datePickerState.selectedDateMillis?.let { millis ->
//                        val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
//                            .toLocalDate()
//                        selectedDate = selected
//                    }
//                    showDatePicker = false
//                }) { Text("OK") }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//
//    // Clear confirm dialog
//    if (showClearDialog) {
//        ClearDialog(
//            onDismiss = { showClearDialog = false },
//            onConfirm = {
//                viewModel.clear()
//                entriesState.clear()
//                globalRemark = ""
//                showClearDialog = false
//            }
//        )
//    }
//
//    // Success dialog
//    if (showSuccess) {
//        SuccessDialogWithButton(
//            title = "Saved",
//            description = "Day entries saved successfully",
//            paddingValues = PaddingValues(0.dp),
//            onClick = {
//                viewModel.clear()
//                entriesState.clear()
//                viewModel.loadData()
//                showSuccess = false
//            },
//        )
//    }
//
//    // Bill dialog (generic)
//    if (showBillDialog) {
//        MessageBox(
//            title = "Alert",
//            message = "Please select items to proceed billing.",
//            onDismiss = { showBillDialog = false })
//    }
//
//    // Hook Fly-to-cart overlay for the simple animation
//    FlyToCartOverlay()
//}
//
///** ---------- Small composables & helpers ---------- **/
//
//private fun addOrFocusEntry(
//    product: TblLedgerDetails,
//    entries: MutableMap<Long, TblLedgerDetailIdRequest>,
//    viewModel: LedgerDetailsViewModel,
//    entryNo:String,
//    voucher: TblVoucherResponse?,
//    partyId: Int
//) {
//    val id = product.ledger_id
//    if (!entries.containsKey(id.toLong())) {
//        entries[id.toLong()] = TblLedgerDetailIdRequest(
//            id = id.toLong(),
//            date = getCurrentDateModern(),
//            party_member = voucher?.voucher_name?:"",
//            party_id = partyId.toLong(),
//            member = voucher?.voucher_id.toString(),
//            member_id = entryNo,
//            purpose = "",
//            amount_in = 0.0,
//            amount_out = 0.0,
//            bill_no = "",
//            time = getCurrentTimeModern(),
//        )
//        // Optionally notify ViewModel
//        viewModel.addItemToOrder(product)
//    }
//}
//
/** Payment mode row with ledger dropdown when OTHERS selected */
@Composable
private fun PaymentModeRow(
    paymentMode: PaymentMode,
    onModeChange: (PaymentMode) -> Unit,
    ledgerList: List<TblLedgerDetails>,
    otherPartyId: Int?,
    onOtherSelected: (Int) -> Unit,
    party: Int?,
    onPartySelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryGreen)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PaymentMode.entries.forEach { mode ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                RadioButton(
                    selected = paymentMode == mode,
                    onClick = {
                        when (paymentMode) {
                            PaymentMode.CASH -> {
                                val cash = ledgerList.find { it.ledger_name == "CASH" }
                                onPartySelected(cash?.ledger_id ?: 1)
                                onModeChange(mode)
                            }

                            PaymentMode.CARD -> {
                                val cash = ledgerList.find { it.ledger_name == "CARD" }
                                onPartySelected(cash?.ledger_id ?: 1)
                                onModeChange(mode)
                            }

                            PaymentMode.UPI -> {
                                val cash = ledgerList.find { it.ledger_name == "UPI" }
                                onPartySelected(cash?.ledger_id ?: 1)
                                onModeChange(mode)
                            }

                            else -> {
                                onPartySelected(party ?: 1)
                                onModeChange(mode)
                            }
                        }


                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White
                    )
                )
                Text(mode.label, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        if (paymentMode == PaymentMode.OTHERS) {
            LedgerDropdown(
                ledgers = ledgerList,
                selectedLedger = ledgerList.find { it.ledger_id == (otherPartyId ?: -1) },
                onLedgerSelected = { onOtherSelected(it.ledger_id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "Select Ledger"
            )
        }
    }
}

@Composable
private fun EntryRow(
    entry: TblLedgerDetailIdRequest,
    onDebitChange: (Double) -> Unit,
    onCreditChange: (Double) -> Unit,
    onRemarkChange: (String) -> Unit,
    onRemove: () -> Unit,
    ledgerName: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(ledgerName ?: "", modifier = Modifier.weight(1f), maxLines = 1)
        OutlinedTextField(
            value = entry.purpose,
            onValueChange = onRemarkChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            label = { Text("Remark") }
        )
        OutlinedTextField(
            value = entry.amount_out.toString().trimEnd('.'),
            onValueChange = {
                val v = it.toDoubleOrNull() ?: 0.0
                onDebitChange(v)
            },
            modifier = Modifier.weight(0.7f),
            singleLine = true,
            label = { Text("Debit") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = entry.amount_in.toString().trimEnd('.'),
            onValueChange = {
                val v = it.toDoubleOrNull() ?: 0.0
                onCreditChange(v)
            },
            modifier = Modifier.weight(0.7f),
            singleLine = true,
            label = { Text("Credit") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}

private data class Totals(val debit: Double, val credit: Double)

private enum class PaymentMode(val label: String) {
    CASH("CASH"),
    CARD("CARD"),
    UPI("UPI"),
    OTHERS("OTHERS")
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEntryScreen(
    viewModel: LedgerDetailsViewModel = hiltViewModel(),
    drawerState: DrawerState,
    navController: NavHostController,
) {
    val ledgerDetailsState by viewModel.ledgerDetailsState.collectAsStateWithLifecycle()
    val transactionState by viewModel.transactionState.collectAsStateWithLifecycle()
    val ledgerList by viewModel.ledgerList.collectAsStateWithLifecycle()
    val entryNo by viewModel.entryNo.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val voucher by viewModel.voucher.collectAsStateWithLifecycle()

    // --- Screen state ---
    var selectedCategory by remember { mutableStateOf<String>("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var partyId by remember { mutableStateOf<Int>(1) }
    var otherPartyId by remember { mutableStateOf<Int?>(null) }
    var globalRemark by remember { mutableStateOf("") }

    // Entries
    val entriesState = remember { mutableStateMapOf<Long, TblLedgerDetailIdRequest>() }

    val totals by remember {
        derivedStateOf {
            val list = entriesState.values.toList()
            Totals(
                debit = list.sumOf { it.amount_out },
                credit = list.sumOf { it.amount_in }
            )
        }
    }

    // UI state
    var isSaving by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    // Dialog state
    var showLedgerDialog by remember { mutableStateOf(false) }
    var selectedLedger by remember { mutableStateOf<TblLedgerDetails?>(null) }

    LaunchedEffect(transactionState) {
        when (val state = transactionState) {
            is LedgerDetailsViewModel.TransactionUiState.Success -> showSuccess = true
            is LedgerDetailsViewModel.TransactionUiState.Error -> {
                scope.launch { snackbarHostState.showSnackbar("Entry Failed To Add") }
            }

            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { AnimatedSnackbarDemo(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Day Entry",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceLight
                        )
                        Text(
                            "Entry No: $entryNo",
                            style = ResbTypography.labelSmall,
                            color = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen),
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = SurfaceLight)
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedDate.toString(), color = SurfaceLight)
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = SurfaceLight
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = SecondaryGreen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Entries: ${entriesState.size}",
                            color = SurfaceLight,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Debit: ${totals.debit}",
                            color = SurfaceLight,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Credit: ${totals.credit}",
                            color = SurfaceLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ActionButton("Clear", Color.Red, enabled = true) { showClearDialog = true }
                        ActionButton(
                            if (isSaving) "Saving..." else "Save",
                            enabled = !isSaving,
                            color = Color(0xFF4CAF50)
                        ) {
                            if (entriesState.isEmpty()) {
                                scope.launch { snackbarHostState.showSnackbar("Add at least one ledger entry") }
                            } else {
                                scope.launch {
                                    isSaving = true
                                    val entries = entriesState.values.toList()
                                    viewModel.addLedgerDetails(entries)
                                    isSaving = false
                                }
                            }
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
                .padding(8.dp)
        ) {
            PaymentModeRow(
                paymentMode = paymentMode,
                onModeChange = { paymentMode = it },
                ledgerList = ledgerList,
                otherPartyId = otherPartyId,
                onOtherSelected = { otherPartyId = it },
                party = partyId,
                onPartySelected = { partyId = it }
            )

            Spacer(modifier = Modifier.height(8.dp))


            // Entry list
            LazyColumn {
                items(entriesState.keys.toList(), key = { it }) { ledgerId ->
                    val entry = entriesState[ledgerId] ?: return@items
                    val ledgerName =
                        ledgerList.find { it.ledger_id.toLong() == ledgerId }?.ledger_name ?: ""
                    EntryRow(
                        entry = entry,
                        onDebitChange = { new ->
                            entriesState[ledgerId] = entry.copy(amount_out = new.coerceAtLeast(0.0))
                        },
                        onCreditChange = { new ->
                            entriesState[ledgerId] = entry.copy(amount_in = new.coerceAtLeast(0.0))
                        },
                        onRemarkChange = { r ->
                            entriesState[ledgerId] = entry.copy(purpose = r)
                        },
                        onRemove = { entriesState.remove(ledgerId) },
                        ledgerName = ledgerName
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            // Category tabs
            if (ledgerDetailsState is LedgerDetailsViewModel.LedgerDetailsUiState.Success && categories.isNotEmpty()) {
                val cats = categories
                val selectedIndex = cats.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0
                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    containerColor = SecondaryGreen,
                    contentColor = SurfaceLight
                ) {
                    cats.forEachIndexed { idx, cat ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            text = { Text(cat) })
                    }
                }
            }
//            Spacer(modifier = Modifier.height(8.dp))
            when (val state = ledgerDetailsState) {
                is LedgerDetailsViewModel.LedgerDetailsUiState.Success -> {
                    val filtered =
                        if (selectedCategory.isNotEmpty())
                            state.ledgers.filter { it.group.group_nature.g_nature_name == selectedCategory }
                        else state.ledgers
                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No ledger found")
                        }
                    } else {
                        LazyVerticalGrid(columns = GridCells.Fixed(3)) {

                            items(filtered) { product ->
                                Card(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .pointerInput(Unit) {
                                            detectTapGestures { _ ->
                                                selectedLedger = product
                                                showLedgerDialog = true
                                            }
                                        },
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = ghostWhite)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            product.ledger_name,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is LedgerDetailsViewModel.LedgerDetailsUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is LedgerDetailsViewModel.LedgerDetailsUiState.Error ->
                    Text("Error: ${state.message}", modifier = Modifier.padding(16.dp))

            }


        }
    }

    // Dialogs
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        selectedDate = selected
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showClearDialog) {
        ClearDialog(
            onDismiss = { showClearDialog = false },
            onConfirm = {
                viewModel.clear()
                entriesState.clear()
                globalRemark = ""
                showClearDialog = false
            }
        )
    }

    if (showSuccess) {
        SuccessDialogWithButton(
            title = "Saved",
            description = "Day entries saved successfully",
            paddingValues = PaddingValues(0.dp),
            onClick = {
                viewModel.clear()
                entriesState.clear()
                viewModel.loadData()
                showSuccess = false
            },
        )
    }

    // Ledger popup
    if (showLedgerDialog && selectedLedger != null) {
        LedgerEntryDialog(
            ledger = selectedLedger!!,
            onDismiss = { showLedgerDialog = false },
            onAdd = { remark, amount, isPayment ->
                if (amount <= 0) {
                    scope.launch { snackbarHostState.showSnackbar("Amount must be greater than zero") }
                    return@LedgerEntryDialog
                }
                val id = selectedLedger!!.ledger_id.toLong()
                entriesState[id] = TblLedgerDetailIdRequest(
                    id = id,
                    date = getCurrentDateModern(),
                    party_member = voucher?.voucher_name ?: "",
                    party_id = (otherPartyId ?: partyId).toLong(),
                    member = voucher?.voucher_id.toString(),
                    member_id = entryNo,
                    purpose = remark,
                    amount_in = if (isPayment) 0.0 else amount,
                    amount_out = if (isPayment) amount else 0.0,
                    bill_no = "",
                    time = getCurrentTimeModern(),
                )
                viewModel.addItemToOrder(selectedLedger!!)
                showLedgerDialog = false
            }
        )
    }
}

/** Ledger popup for remark, amount, payment/receipt with autofocus on Amount */
@Composable
fun LedgerEntryDialog(
    ledger: TblLedgerDetails,
    onDismiss: () -> Unit,
    onAdd: (remark: String, amount: Double, isPayment: Boolean) -> Unit
) {
    var remark by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isPayment by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            delay(200) // slight delay to allow dialog to appear
            focusRequester.requestFocus()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull() ?: 0.0
                onAdd(remark, amt, isPayment)
                remark = ""
                amount = ""
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Add Entry for ${ledger.ledger_name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = remark,
                    onValueChange = { remark = it },
                    label = { Text("Remark") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isPayment, onClick = { isPayment = true })
                    Text("Payment")
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = !isPayment, onClick = { isPayment = false })
                    Text("Receipt")
                }
            }
        }
    )
}
