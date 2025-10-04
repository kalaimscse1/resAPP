package com.warriortech.resb.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.warriortech.resb.model.KitchenCategory
import com.warriortech.resb.model.Menu
import com.warriortech.resb.model.MenuCategory
import com.warriortech.resb.model.Tax
import com.warriortech.resb.model.TblMenuItemRequest
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.viewmodel.MenuItemSettingsViewModel
import com.warriortech.resb.util.StringDropdown
import kotlinx.coroutines.launch
import com.warriortech.resb.model.TblUnit
import com.warriortech.resb.ui.theme.PrimaryGreen
import com.warriortech.resb.ui.theme.SurfaceLight
import com.warriortech.resb.util.CurrencySettings
import com.warriortech.resb.util.KitchenGroupDropdown
import com.warriortech.resb.util.MenuCategoryDropdown
import com.warriortech.resb.util.MenuDropdown
import com.warriortech.resb.util.ReusableBottomSheet
import com.warriortech.resb.util.TaxDropdown
import com.warriortech.resb.util.UnitDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemSettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: MenuItemSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMenuItem by remember { mutableStateOf<TblMenuItemResponse?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val menus by viewModel.menus.collectAsStateWithLifecycle()
    val menuCategories by viewModel.menuCategories.collectAsStateWithLifecycle()
    val kitchenCategories by viewModel.kitchenCategories.collectAsStateWithLifecycle()
    val taxes by viewModel.taxes.collectAsStateWithLifecycle()
    val units by viewModel.units.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMenuItems()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MenuItem Settings", color = SurfaceLight) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = SurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            Icons.Default.Add, contentDescription = "Add MenuItem",
                            tint = SurfaceLight
                        )
                    }
                },
            )
        },

//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { showAddDialog = true }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Area")
//            }
//        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when (val state = uiState) {
            is MenuItemSettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MenuItemSettingsUiState.Success -> {
                if (state.menuItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No menu items available",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.menuItems) { menuItem ->
                            MenuItemCard(
                                menuItem = menuItem,
                                onEdit = { editingMenuItem = menuItem },
                                onDelete = {
                                    scope.launch {
                                        viewModel.deleteMenuItem(menuItem.menu_item_id.toInt())
                                    }
                                }
                            )
                        }
                    }
                }
            }

            is MenuItemSettingsUiState.Error -> {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.loadMenuItems() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }


        if (showAddDialog || editingMenuItem != null) {
            MenuItemDialog(
                menuItem = editingMenuItem,
                onDismiss = {
                    showAddDialog = false
                    editingMenuItem = null
                },
                onSave = { menuItem ->
                    scope.launch {
                        if (editingMenuItem != null) {
                            viewModel.updateMenuItem(menuItem)
                        } else {
                            viewModel.addMenuItem(menuItem)
                        }
                        showAddDialog = false
                        editingMenuItem = null
                    }
                },
                menus = menus,
                menuCategories = menuCategories,
                kitchenCategories = kitchenCategories,
                taxes = taxes,
                units = units
            )
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: TblMenuItemResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    MobileOptimizedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menuItem.menu_item_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = CurrencySettings.format(menuItem.rate),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (menuItem.menu_item_name_tamil.isNotEmpty()) {
                    Text(
                        text = menuItem.menu_item_name_tamil,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun MenuItemDialog(
    menuItem: TblMenuItemResponse?,
    onDismiss: () -> Unit,
    onSave: (TblMenuItemRequest) -> Unit,
    menus: List<Menu>,
    menuCategories: List<MenuCategory>,
    kitchenCategories: List<KitchenCategory>,
    taxes: List<Tax>,
    units: List<TblUnit>,
) {
    val rateOptions = listOf("YES", "NO")

    val nameFocus = remember { FocusRequester() }
    val nameTamilFocus = remember { FocusRequester() }
    val rateFocus = remember { FocusRequester() }
    val acRateFocus = remember { FocusRequester() }
    val parcelRateFocus = remember { FocusRequester() }
    val parcelChargeFocus = remember { FocusRequester() }
    val preparationTimeFocus = remember { FocusRequester() }
    val hsnCodeFocus = remember { FocusRequester() }
    val minStockFocus = remember { FocusRequester() }
    val orderByFocus = remember { FocusRequester() }
    // Menu fields
    var name by remember { mutableStateOf(menuItem?.menu_item_name ?: "") }
    var nameTamil by remember { mutableStateOf(menuItem?.menu_item_name_tamil ?: "") }
    var menuId by remember { mutableStateOf(menuItem?.menu_id ?: 1) }
    var menuItemCatId by remember { mutableStateOf(menuItem?.item_cat_id ?: 1) }
    var kitchenCatId by remember { mutableStateOf(menuItem?.kitchen_cat_id ?: 1) }
    var isAvailable by remember { mutableStateOf(menuItem?.is_available ?: "YES") }
    var taxId by remember { mutableStateOf(menuItem?.tax_id ?: 1) }
    var rate by remember { mutableStateOf(menuItem?.rate?.toString() ?: "") }
    var rateLock by remember { mutableStateOf(menuItem?.rate_lock ?: rateOptions.first()) }
    var orderBy by remember { mutableStateOf(menuItem?.order_by ?: 1) }
    var acRate by remember { mutableStateOf(menuItem?.ac_rate?.toString() ?: "") }
    var parcelRate by remember { mutableStateOf(menuItem?.parcel_rate?.toString() ?: "") }
    var parcelCharge by remember { mutableStateOf(menuItem?.parcel_charge?.toString() ?: "") }
    var preparationTime by remember { mutableStateOf(menuItem?.preparation_time ?: 0) }
    var isFavourite by remember { mutableStateOf(menuItem?.is_favourite == true) }

    // Inventory fields
    var isInventory by remember { mutableStateOf(menuItem?.is_inventory ?: 0L) }
    var hsnCode by remember { mutableStateOf(menuItem?.hsn_code ?: "") }
    var minStock by remember { mutableStateOf(menuItem?.min_stock ?: 0) }
    var isRaw by remember { mutableStateOf(menuItem?.is_raw ?: "NO") }
    var stockMaintain by remember { mutableStateOf(menuItem?.stock_maintain ?: "NO") }
    var unitId by remember { mutableStateOf(menuItem?.unit_id ?: 1) }
    var isActive by remember { mutableStateOf(menuItem?.is_active ?: 1) }

    ReusableBottomSheet(
        onDismiss = onDismiss,
        title = if (menuItem != null) "Edit Menu Item" else "Add Menu Item",
        onSave = {
            val newMenuItem = TblMenuItemRequest(
                menu_item_id = menuItem?.menu_item_id ?: 0,
                menu_item_name = name,
                menu_item_name_tamil = nameTamil,
                item_cat_id = menuItemCatId,
                rate = rate.toDoubleOrNull() ?: 0.0,
                ac_rate = acRate.toDoubleOrNull() ?: 0.0,
                parcel_rate = parcelRate.toDoubleOrNull() ?: 0.0,
                parcel_charge = parcelCharge.toDoubleOrNull() ?: 0.0,
                tax_id = taxId,
                cess_specific = 0.0,
                kitchen_cat_id = kitchenCatId,
                stock_maintain = stockMaintain,
                rate_lock = rateLock,
                unit_id = unitId,
                min_stock = minStock,
                hsn_code = hsnCode,
                order_by = orderBy,
                is_inventory = isInventory,
                is_raw = isRaw,
                is_available = isAvailable,
                menu_item_code = menuItem?.menu_item_code ?: "",
                menu_id = menuId,
                is_favourite = isFavourite,
                is_active = isActive,
                image = "",
                preparation_time = preparationTime
            )
            onSave(newMenuItem)
        },
        isSaveEnabled = name.isNotBlank() && rate.isNotBlank(),
        buttonText = if (menuItem != null) "Update" else "Add"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp) // limit dialog height
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Common Menu Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.uppercase() },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(nameFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { nameTamilFocus.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nameTamil,
                onValueChange = { nameTamil = it },
                label = { Text("Name (Tamil)") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(nameTamilFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { rateFocus.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = rate,
                onValueChange = { rate = it },
                label = { Text("Rate") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(rateFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { acRateFocus.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = acRate,
                onValueChange = { acRate = it },
                label = { Text("AC Rate") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(acRateFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { parcelRateFocus.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = parcelRate,
                onValueChange = { parcelRate = it },
                label = { Text("Parcel Rate") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(parcelRateFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { parcelChargeFocus.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(8.dp))


            // Dropdowns
            StringDropdown(
                options = rateOptions,
                selectedOption = rateLock,
                onOptionSelected = { rateLock = it },
                label = "Rate Lock",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TaxDropdown(
                taxes = taxes,
                onTaxSelected = { taxId = it.tax_id },
                label = "Select Tax",
                modifier = Modifier.fillMaxWidth(),
                selectedTax = taxes.find { it.tax_id == taxId }
            )
            Spacer(modifier = Modifier.height(8.dp))

            MenuDropdown(
                menus = menus,
                onMenuSelected = { menuId = it.menu_id },
                label = "Select Menu",
                modifier = Modifier.fillMaxWidth(),
                selectedMenu = menus.find { it.menu_id == menuId }
            )
            Spacer(modifier = Modifier.height(8.dp))

            MenuCategoryDropdown(
                menus = menuCategories,
                onMenuCategorySelected = { menuItemCatId = it.item_cat_id },
                label = "Select Menu Category",
                modifier = Modifier.fillMaxWidth(),
                selectedMenuCategory = menuCategories.find { it.item_cat_id == menuItemCatId }
            )
            Spacer(modifier = Modifier.height(8.dp))

            KitchenGroupDropdown(
                menus = kitchenCategories,
                onKitchenCategorySelected = { kitchenCatId = it.kitchen_cat_id },
                label = "Select Kitchen Category",
                modifier = Modifier.fillMaxWidth(),
                selectedKitchenCategory = kitchenCategories.find { it.kitchen_cat_id == kitchenCatId }
            )
            Spacer(modifier = Modifier.height(8.dp))

            StringDropdown(
                options = listOf("YES", "NO"),
                selectedOption = isAvailable,
                onOptionSelected = { isAvailable = it },
                label = "Is Available",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isFavourite,
                    onCheckedChange = { isFavourite = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Is Favourite")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Switch to enable Inventory
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isInventory == 1L,
                    onCheckedChange = { isInventory = if (it) 1L else 0L }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inventory Enabled")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Inventory-only fields
            if (isInventory == 1L) {
                OutlinedTextField(
                    value = hsnCode,
                    onValueChange = { hsnCode = it },
                    label = { Text("HSN Code") },
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(hsnCodeFocus),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { minStockFocus.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = minStock.toString(),
                    onValueChange = { minStock = it.toLong() },
                    label = { Text("Minimum Stock") },
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(minStockFocus),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { orderByFocus.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                UnitDropdown(
                    menus = units,
                    onUnitSelected = { unitId = it.unit_id },
                    label = "Select Unit",
                    modifier = Modifier.fillMaxWidth(),
                    selectedUnit = units.find { it.unit_id == unitId }
                )
                Spacer(modifier = Modifier.height(8.dp))

                StringDropdown(
                    options = listOf("YES", "NO"),
                    selectedOption = isRaw,
                    onOptionSelected = { isRaw = it },
                    label = "Is Raw",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                StringDropdown(
                    options = listOf("YES", "NO"),
                    selectedOption = stockMaintain,
                    onOptionSelected = { stockMaintain = it },
                    label = "Stock Maintain",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }
}


sealed class MenuItemSettingsUiState {
    object Loading : MenuItemSettingsUiState()
    data class Success(val menuItems: List<TblMenuItemResponse>) : MenuItemSettingsUiState()
    data class Error(val message: String) : MenuItemSettingsUiState()
}
