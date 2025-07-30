package com.warriortech.resb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.warriortech.resb.ui.components.MobileOptimizedCard
import com.warriortech.resb.ui.theme.GradientStart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    drawerState: DrawerState,
    navController: NavController
) {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
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
                modifier = Modifier.padding(paddingValues),
                navController = navController
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
			SettingsModule.Counter,
            SettingsModule.Language,
            SettingsModule.PrinterSetting,
            SettingsModule.Modifiers
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
    modifier: Modifier = Modifier,
    navController: NavController
) {
    when (module) {
        is SettingsModule.Area -> {
           navController.navigate("area_setting")
        }

        is SettingsModule.Table -> {
            navController.navigate("table_setting")
        }

        is SettingsModule.MenuItem -> {
           navController.navigate("menu_item_setting")
        }

        is SettingsModule.Menu -> {
            navController.navigate("menu_setting")
        }

        is SettingsModule.MenuCategory -> {
            navController.navigate("menu_category_setting")
        }

        is SettingsModule.Customer -> {
            navController.navigate("customer_setting")
        }

        is SettingsModule.Staff -> {
            navController.navigate("staff_setting")
        }
        is SettingsModule.Role -> {
            navController.navigate("role_setting")
        }
        is SettingsModule.Printer -> {
            navController.navigate("printer_setting")
        }
        is SettingsModule.Tax -> {
            navController.navigate("tax_setting")
        }
        is SettingsModule.TaxSplit -> {
            navController.navigate("tax_split_setting")
        }
        is SettingsModule.RestaurantProfile -> {
            navController.navigate("restaurant_profile_setting")
        }
        is SettingsModule.GeneralSettings -> {
            navController.navigate("general_settings")
        }
        is SettingsModule.CreateVoucher -> {
            navController.navigate("voucher_setting")
        }
        is SettingsModule.Counter -> {
            navController.navigate("counter_setting")
        }
        is SettingsModule.Language -> {
            navController.navigate("language_setting")
        }
        is SettingsModule.PrinterSetting -> {
            navController.navigate("template_screen")
        }
        is SettingsModule.Modifiers -> {
            navController.navigate("modifier_setting")
        }
    }
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
    object Language : SettingsModule(
        "language",
        "Language",
        "Manage application languages",
        Icons.Default.Language,
        listOf("name", "code")
    )
    object PrinterSetting : SettingsModule(
        "printer_setting",
        "Receipt Template",
        "Manage receipt templates",
        Icons.Default.Kitchen,
        listOf("name", "ip_address", "port", "type", "location")
    )
    object Modifiers : SettingsModule(
        "modifiers",
        "Modifiers",
        "Manage menu item modifiers",
        Icons.Default.Add,
        listOf("name", "price", "category")
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