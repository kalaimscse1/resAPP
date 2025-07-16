package com.warriortech.resb

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.warriortech.resb.data.sync.SyncManager
import com.warriortech.resb.model.Table
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.screens.LoginScreen
import com.warriortech.resb.screens.MenuScreen
import com.warriortech.resb.screens.SelectionScreen
import com.warriortech.resb.ui.theme.ResbTheme
import com.warriortech.resb.util.ConnectionState
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.NetworkStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.screens.BillingScreen
import com.warriortech.resb.screens.PaymentScreen
import com.warriortech.resb.screens.OrderScreen
import com.warriortech.resb.screens.SettingsScreen
import com.warriortech.resb.screens.DashboardScreen
import com.warriortech.resb.screens.CounterScreen
import com.warriortech.resb.screens.KitchenScreen
import com.warriortech.resb.screens.ReportScreen
import com.warriortech.resb.screens.AIAssistantScreen
import com.warriortech.resb.screens.CounterSelectionScreen
import com.warriortech.resb.screens.settings.AreaSettingsScreen
import com.warriortech.resb.screens.settings.CounterSettingsScreen
import com.warriortech.resb.screens.settings.CustomerSettingsScreen
import com.warriortech.resb.screens.settings.GeneralSettingsScreen
import com.warriortech.resb.screens.settings.LanguageSettingsScreen
import com.warriortech.resb.screens.settings.MenuCategorySettingsScreen
import com.warriortech.resb.screens.settings.MenuItemSettingsScreen
import com.warriortech.resb.screens.settings.MenuSettingsScreen
import com.warriortech.resb.screens.settings.PrinterSettingsScreen
import com.warriortech.resb.screens.settings.RestaurantProfileScreen
import com.warriortech.resb.screens.settings.RoleSettingsScreen
import com.warriortech.resb.screens.settings.StaffSettingsScreen
import com.warriortech.resb.screens.settings.TableSettingsScreen
import com.warriortech.resb.screens.settings.TaxSettingsScreen
import com.warriortech.resb.screens.settings.TaxSplitSettingsScreen
import com.warriortech.resb.screens.settings.VoucherSettingsScreen
import com.warriortech.resb.screens.TemplateScreen
import com.warriortech.resb.screens.TemplateEditorScreen
import com.warriortech.resb.screens.TemplatePreviewScreen
import com.warriortech.resb.util.LocaleHelper
import com.warriortech.resb.screens.PaidBillsScreen
import com.warriortech.resb.screens.EditPaidBillScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase ?: this))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleHelper.onAttach(this)
        recreate()
    }

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var syncManager: SyncManager


    @SuppressLint("ConfigurationScreenWidthHeight")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        // Initialize sync when app starts
        lifecycleScope.launch {
            networkMonitor.isOnline.collect { connectionState ->
                if (connectionState == ConnectionState.Available) {
                    syncManager.scheduleSyncWork()
                }
            }
        }
        setContent {
            ResbTheme(darkTheme = isSystemInDarkTheme()) {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val context = LocalContext.current
                val connectionState by networkMonitor.isOnline.collectAsState(initial = ConnectionState.Available)
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp
                val screenHeight = configuration.screenHeightDp
                val isLargeScreen = screenWidth >= 600
                val isTablet = screenWidth >= 600 && screenHeight >= 960
                val isLandscape = screenWidth > screenHeight
                val isCollapsed = remember { mutableStateOf(false) }

                // Responsive drawer width based on screen size
                val drawerWidth = when {
                    isTablet -> if (isCollapsed.value) 80.dp else 320.dp
                    isLargeScreen -> if (isCollapsed.value) 72.dp else 280.dp
                    isLandscape -> if (isCollapsed.value) 72.dp else (screenWidth * 0.6f).dp.coerceAtMost(300.dp)
                    else -> if (isCollapsed.value) 72.dp else (screenWidth * 0.8f).dp.coerceAtMost(300.dp)
                }
                val animatedDrawerWidth by animateDpAsState(targetValue = drawerWidth)

                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val showDrawer = currentRoute != "login"

                val drawerContent = @Composable {
                    DrawerContent(
                        isCollapsed = isCollapsed.value,
                        drawerWidth = animatedDrawerWidth,
                        onCollapseToggle = { isCollapsed.value = !isCollapsed.value },
                        onDestinationClicked = { route ->
                            scope.launch { drawerState.close() }
                            if (route == "logout") {
                                scope.launch {
                                    val sharedPref = context.getSharedPreferences("user_prefs",
                                        MODE_PRIVATE
                                    )
                                    sharedPref.edit { clear() }
                                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            } else {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        },
                        navController = navController
                    )
                }

                if (showDrawer) {
                    if (isLargeScreen) {
                        PermanentNavigationDrawer(drawerContent = drawerContent) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                elevation = 4.dp,
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colors.background
                            ) {
                                Column {
                                    NetworkStatusBar(connectionState = connectionState)
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        AppNavigation(drawerState, navController)
                                    }
                                }
                            }
                        }
                    } else {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = drawerContent,
                            gesturesEnabled = true
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                elevation = 2.dp,
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colors.background
                            ) {
                                Column {
                                    NetworkStatusBar(connectionState = connectionState)
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        AppNavigation(drawerState, navController)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        elevation = 2.dp,
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colors.background
                    ) {
                        Column {
                            NetworkStatusBar(connectionState = connectionState)
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                AppNavigation(drawerState, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(drawerState: DrawerState, navController: NavHostController) {
    var selectedTable by remember { mutableStateOf<Table?>(null) }
    var isTakeaway by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf(listOf<TblOrderDetailsResponse>()) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var selectedOrderId by remember { mutableStateOf<Long?>(null) }


    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("selects") {
            SelectionScreen(
//                onDinePressed = { navController.navigate("selects") },
//                onTakeAwayPressed = {
//                    navController.navigate("menu")
//                    isTakeaway = "TAKEAWAY"
//                },
//                onDeliverPressed = {
//                    navController.navigate("menu")
//                    isTakeaway = "DELIVERY"
//                },
                onTableSelected = { table ->
                    isTakeaway = "TABLE"
                    selectedTable = table
                    navController.navigate("menu")
                },
                drawerState = drawerState
            )
        }

        composable("menu") {
            val tableId = selectedTable?.table_id ?: 1L
            val tableStatId = selectedTable != null || isTakeaway == "TABLE"

            MenuScreen(
                isTakeaway = isTakeaway,
                tableStatId = tableStatId,
                tableId = tableId,
                onBackPressed = { navController.popBackStack() },
                onOrderPlaced = { navController.popBackStack() },
                drawerState = drawerState,
                onBillPlaced = { items,orderId->
                    selectedItems=items
                    selectedOrderId = orderId
                    navController.navigate("billing_screen/${orderId}") },
                navController = navController
            )
        }

        composable("takeaway_menu") {
            MenuScreen(
                isTakeaway = "TAKEAWAY",
                tableStatId = false,
                tableId = 1L,
                onBackPressed = { navController.popBackStack() },
                onOrderPlaced = { navController.popBackStack()
                                selectedTable=null},
                drawerState = drawerState,
                onBillPlaced = { items,orderId->
                    selectedItems=items
                    selectedOrderId = orderId
                    navController.navigate("billing_screen/${orderId}") },
                navController = navController
            )
        }
        composable("billing_screen/{orderMasterId}") { backStackEntry ->
            BillingScreen(
                navController = navController,
                orderDetailsResponse = selectedItems,
                orderMasterId = backStackEntry.arguments?.getString("orderMasterId")?.toLong() ?: 0L
            )
        }
        composable("payment_screen/{amountToPayFromRoute}") {
            PaymentScreen(navController = navController,
                amountToPayFromRoute = it.arguments?.getString("amountToPayFromRoute")?.toDoubleOrNull() ?: 0.0
            )
        }
        composable("report_screen") {
            ReportScreen(navController = navController)
        }

        composable("kitchen") {
            KitchenScreen(
                navController = navController,
                drawerState = drawerState
            )
        }

        composable("orders") {
            OrderScreen(
                drawerState = drawerState,
                onNavigateToBilling = { items, orderId ->
                    Log.d("BillingScreen", "order Items: $items")
                    selectedItems = items
                    selectedOrderId = orderId
                    navController.navigate("billing_screen/${orderId}") {
                        popUpTo("orders") { inclusive = true }
                    }
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBackPressed = { navController.popBackStack() },
                drawerState = drawerState,
                navController= navController
            )
        }
        composable("area_setting") {
            AreaSettingsScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable("table_setting") {
            TableSettingsScreen(onBackPressed = {
                navController.popBackStack()
            })
        }
        composable("menu_setting"){
            MenuSettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable("menu_item_setting") {
            MenuItemSettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable ("menu_Category_setting") {
            MenuCategorySettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable("staff_setting") {
            StaffSettingsScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable("customer_setting") {
           CustomerSettingsScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }


        composable("counter_selection") {
            CounterSelectionScreen(
                onCounterSelected = { counter ->
                    navController.navigate("counter/${counter.id}") {
                        popUpTo("counter_selection") { inclusive = true }
                    }
                },
                drawerState = drawerState
            )
        }

        composable("counter/{counterId}") { backStackEntry ->
            val counterId = backStackEntry.arguments?.getString("counterId")?.toLongOrNull() ?: 1L
            CounterScreen(
                onBackPressed = { navController.popBackStack() },
                onProceedToBilling = { items ->
                    // Convert counter items to billing format
                    val billingItems = items.mapKeys { it.key }.mapValues { it.value }
                    // Navigate to billing with counter items
                    navController.navigate("billing_screen/${billingItems.values ?: 0L}") {
                        popUpTo("counter/${counterId}") { inclusive = true }
                    }
                },
                drawerState = drawerState,
                counterId = counterId
            )
        }

        composable("counter") {
            CounterScreen(
                onBackPressed = { navController.popBackStack() },
                onProceedToBilling = { items ->
                    // Convert counter items to billing format
                    val billingItems = items.mapKeys { it.key }.mapValues { it.value }
                    // Navigate to billing with counter items
                    navController.navigate("billing_screen/${billingItems.values ?: 0L}") {
                        popUpTo("counter") { inclusive = true }
                    }
                },
                drawerState = drawerState
            )
        }

        composable("dashboard") {
            DashboardScreen(
                drawerState = drawerState,
                onNavigateToOrders = {navController.navigate("orders")},
                onNavigateToMenu = {navController.navigate("menu")},
                onNavigateToSettings = {navController.navigate("settings")},
                onNavigateToBilling = {navController.navigate("counter")}
            )
        }
        composable("report_screen") {
            ReportScreen(navController = navController)
        }

        composable("ai_assistant") {
            AIAssistantScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable("template_screen") {
            TemplateScreen(navController = navController)
        }

        composable("template_editor/{templateId}") {
            val templateId = it.arguments?.getString("templateId") ?: ""
            TemplateEditorScreen(navController = navController,templateId=templateId)
        }

        composable("template_preview/{templateId}") { backStackEntry ->
            val templateId = backStackEntry.arguments?.getString("templateId") ?: ""
            TemplatePreviewScreen(
                navController = navController,
                templateId = templateId
            )
        }
        composable("language_setting") {
            LanguageSettingsScreen(navController = navController)
        }
        composable("role_setting") {
            RoleSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("printer_setting") {
            PrinterSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("tax_setting") {
            TaxSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("tax_split_setting") {
            TaxSplitSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("restaurant_profile_setting") {
            RestaurantProfileScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("general_settings") {
            GeneralSettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable("paid_bills") {
            PaidBillsScreen(navController = navController)
        }

        composable("edit_paid_bill/{billId}") { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")?.toLongOrNull() ?: 0L
            EditPaidBillScreen(navController = navController, billId = billId)
        }

        composable("view_paid_bill/{billId}") { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")?.toLongOrNull() ?: 0L
            // You can create a ViewPaidBillScreen similar to EditPaidBillScreen but read-only
            EditPaidBillScreen(navController = navController, billId = billId) // For now, reuse edit screen
        }
        composable("voucher_setting") {
            VoucherSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("counter_setting") {
            CounterSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
    }
}
@Composable
fun DrawerContent(
    isCollapsed: Boolean,
    drawerWidth: Dp,
    onCollapseToggle: () -> Unit,
    onDestinationClicked: (String) -> Unit,
    navController: NavHostController
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    ModalDrawerSheet(
        modifier = Modifier.width(drawerWidth),
        drawerContainerColor = MaterialTheme.colors.surface,
        drawerTonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            // Profile Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colors.primary
                    )
                    if (!isCollapsed) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(SessionManager.getUser()?.staff_name?:"", fontWeight = FontWeight.Bold)
                            Text(SessionManager.getUser()?.role?:"", style = MaterialTheme.typography.body1)
                        }
                    }
                }
            }

            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Collapse Drawer") else Text("") },
                icon = {
                    Icon(
                        imageVector = if (isCollapsed) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Toggle Collapse"
                    )
                },
                selected = false,
                onClick = onCollapseToggle,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Dashboard") else Text("") },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                selected = currentDestination?.route == "dashboard",
                onClick = { onDestinationClicked("dashboard") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Dine In") else Text("") },
                icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                selected = currentDestination?.route == "selects",
                onClick = {
                    onDestinationClicked("selects")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Takeaway") else Text("") },
                icon = { Icon(Icons.Default.Fastfood, contentDescription = null) },
                selected = currentDestination?.route == "takeaway_menu",
                onClick = {
                    onDestinationClicked("takeaway_menu")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Orders") else Text("") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                selected = currentDestination?.route == "orders",
                onClick = {
                    onDestinationClicked("orders")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Paid Bills") else Text("") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                selected = currentDestination?.route == "paid_bills",
                onClick = {
                    onDestinationClicked("paid_bills")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Settings") else Text("") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                selected = currentDestination?.route == "settings",
                onClick = {
                    onDestinationClicked("settings")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Counter Billing") else Text("") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                selected = currentDestination?.route == "counter",
                onClick = {
                    onDestinationClicked("counter")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Reports") else Text("") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                selected = currentDestination?.route == "report_screen",
                onClick = {
                    onDestinationClicked("report_screen")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("AI Assistant") else Text("") },
                icon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
                selected = currentDestination?.route == "ai_assistant",
                onClick = {
                    onDestinationClicked("ai_assistant")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            // Show Kitchen for chef, admin and superadmin roles
            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Kitchen") else Text("") },
                icon = { Icon(Icons.Default.Kitchen, contentDescription = null) },
                selected = currentDestination?.route == "kitchen",
                onClick = {
                    onDestinationClicked("kitchen")
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )



            Spacer(modifier = Modifier.weight(1f))

            Divider()

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Logout") else Text("") },
                icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                selected = false,
                onClick = { onDestinationClicked("logout") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}