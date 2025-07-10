package com.warriortech.resb.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    drawerState: DrawerState,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedModule by remember { mutableStateOf<SettingsModule?>(null) }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (selectedModule != null) selectedModule!!.title 
                        else "Settings"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (selectedModule == null) {
            SettingsMainScreen(
                modifier = Modifier.padding(paddingValues),
                onModuleSelected = { module -> selectedModule = module }
            )
        } else {
            SettingsModuleScreen(
                module = selectedModule!!,
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun SettingsMainScreen(
    modifier: Modifier = Modifier,
    onModuleSelected: (SettingsModule) -> Unit
) {
    val settingsModules = remember {
        listOf(
            SettingsModule.Area,
            SettingsModule.Table,
            SettingsModule.Menu,
            SettingsModule.MenuCategory,
            SettingsModule.MenuItem,
            SettingsModule.Customer,
            SettingsModule.Staff,
            SettingsModule.Role,
            SettingsModule.Printer,
            SettingsModule.Tax,
            SettingsModule.TaxSplit,
			SettingsModule.RestaurantProfile,
			SettingsModule.GeneralSettings,
			SettingsModule.CreateVoucher,
			SettingsModule.Counter
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "System Configuration",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(settingsModules) { module ->
            SettingsModuleCard(
                module = module,
                onClick = { onModuleSelected(module) }
            )
        }
    }
}

@Composable
fun SettingsModuleCard(
    module: SettingsModule,
    onClick: () -> Unit
) {
    MobileOptimizedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = module.icon,
                contentDescription = module.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsModuleScreen(
    module: SettingsModule,
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(module) {
        viewModel.loadModuleData(module)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add ${module.title}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SettingsUiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items) { item ->
                        SettingsItemCard(
                            item = item,
                            onEdit = { viewModel.editItem(item) },
                            onDelete = { viewModel.deleteItem(item) }
                        )
                    }
                }
            }

            is SettingsUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            module = module,
            onDismiss = { showAddDialog = false },
            onConfirm = { data ->
                viewModel.addItem(module, data)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SettingsItemCard(
    item: SettingsItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (item.description.isNotEmpty()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun AddItemDialog(
    module: SettingsModule,
    onDismiss: () -> Unit,
    onConfirm: (Map<String, String>) -> Unit
) {
    var formData by remember { mutableStateOf(mutableMapOf<String, String>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add ${module.title}") },
        text = {
            LazyColumn {
                items(module.fields) { field ->
                    OutlinedTextField(
                        value = formData[field] ?: "",
                        onValueChange = { formData[field] = it },
                        label = { Text(field.replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(formData) }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

sealed class SettingsModule(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val fields: List<String>
) {
    object Area : SettingsModule(
        "area",
        "Area",
        "Manage dining areas and zones",
        Icons.Default.LocationOn,
        listOf("name","status")
    )

    object Table : SettingsModule(
        "table",
        "Table",
        "Manage tables and seating arrangements",
        Icons.Default.TableRestaurant,
        listOf("table_number", "area_id", "capacity", "status")
    )

    object Menu : SettingsModule(
        "menu",
        "Menu",
        "Manage restaurant menus",
        Icons.Default.MenuBook,
        listOf("name", "description", "is_active")
    )

    object MenuCategory : SettingsModule(
        "menu_category",
        "Menu Category",
        "Manage menu categories",
        Icons.Default.Category,
        listOf("name", "description", "sort_order")
    )

    object MenuItem : SettingsModule(
        "menu_item",
        "Menu Item",
        "Manage menu items and dishes",
        Icons.Default.Restaurant,
        listOf("name", "name_tamil", "category_id", "rate", "ac_rate", "parcel_rate", "description")
    )

    object Customer : SettingsModule(
        "customer",
        "Customer",
        "Manage customer information",
        Icons.Default.Person,
        listOf("name", "phone", "email", "address")
    )

    object Staff : SettingsModule(
        "staff",
        "Staff",
        "Manage staff members",
        Icons.Default.People,
        listOf("name", "phone", "email", "role_id", "hire_date")
    )

    object Role : SettingsModule(
        "role",
        "Role",
        "Manage user roles and permissions",
        Icons.Default.Security,
        listOf("name", "description", "permissions")
    )

    object Printer : SettingsModule(
        "printer",
        "Printer",
        "Manage printers and print settings",
        Icons.Default.Print,
        listOf("name", "ip_address", "port", "type", "location")
    )

    object Tax : SettingsModule(
        "tax",
        "Tax",
        "Manage tax rates and configurations",
        Icons.Default.Calculate,
        listOf("name", "rate", "type", "is_active")
    )

    object TaxSplit : SettingsModule(
        "tax_split",
        "Tax Split",
        "Manage tax splitting configurations",
        Icons.Default.CallSplit,
        listOf("name", "description", "split_type", "percentage")
    )
	object RestaurantProfile : SettingsModule(
        "restaurant_profile",
        "Restaurant Profile",
        "Manage restaurant profile",
        Icons.Default.Store,
        listOf("name", "address", "phone", "email")
    )

    object GeneralSettings : SettingsModule(
        "general_settings",
        "General Settings",
        "Manage general settings",
        Icons.Default.Settings,
        listOf("currency", "language", "timezone")
    )

    object CreateVoucher : SettingsModule(
        "create_voucher",
        "Create Voucher",
        "Create vouchers",
        Icons.Default.LocalOffer,
        listOf("code", "discount", "expiry_date")
    )

    object Counter : SettingsModule(
        "counter",
        "Counter",
        "Manage counter",
        Icons.Default.PointOfSale,
        listOf("name")
    )
}

data class SettingsItem(
    val id: String,
    val name: String,
    val description: String,
    val data: Map<String, String>
)

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(val items: List<SettingsItem>) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}