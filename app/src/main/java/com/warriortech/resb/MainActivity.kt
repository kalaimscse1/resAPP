package com.warriortech.resb

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Receipt
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
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.screens.BillingScreen
import com.warriortech.resb.screens.PaymentScreen
import com.warriortech.resb.screens.OrderScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var syncManager: SyncManager

    @SuppressLint("ConfigurationScreenWidthHeight")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    else -> if (isCollapsed.value) 72.dp else (screenWidth * 0.8f).dp.coerceAtMost(300.dp)
                }
                val animatedDrawerWidth by animateDpAsState(targetValue = drawerWidth)

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
                        }
                    )
                }

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
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(drawerState: DrawerState, navController: NavHostController) {
    var selectedTable by remember { mutableStateOf<Table?>(null) }
    var isTakeaway by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf(mapOf<MenuItem, Int>()) }
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("selects") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("selects") {
            SelectionScreen(
                onDinePressed = { navController.navigate("selects") },
                onTakeAwayPressed = {
                    navController.navigate("menu")
                    isTakeaway = "TAKEAWAY"
                },
                onDeliverPressed = {
                    navController.navigate("menu")
                    isTakeaway = "DELIVERY"
                },
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
                onBillPlaced = { items->
                    selectedItems=items
                    navController.navigate("billing_screen") }
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
                onBillPlaced = { items->
                    Log.d("BillingScreen", "NavigationSelected Items: $items")
                    selectedItems=items
                    navController.navigate("billing_screen") }
            )
        }
        composable("billing_screen") {
            Log.d("BillingScreen", "Selected Items: $selectedItems")
            BillingScreen(
                navController = navController,
                initialItems = selectedItems,
                tableStatus = if (isTakeaway == "TABLE") selectedTable?.is_ac else isTakeaway
            )
        }

        composable("payment_screen") {
            PaymentScreen(
                navController = navController
            )
        }

        composable("orders") {
            OrderScreen(
                drawerState = drawerState
            )
        }
    }
}
@Composable
fun DrawerContent(
    isCollapsed: Boolean,
    drawerWidth: Dp,
    onCollapseToggle: () -> Unit,
    onDestinationClicked: (String) -> Unit
) {
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
                elevation = 1.dp
            ) {
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
                            Text(SessionManager.getUser()?.role_name?:"", style = MaterialTheme.typography.body1)
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
                label = { if (!isCollapsed) Text("Select Order Type") else Text("") },
                icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                selected = false,
                onClick = { onDestinationClicked("selects") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Takeaway Menu") else Text("") },
                icon = { Icon(Icons.Default.Fastfood, contentDescription = null) },
                selected = false,
                onClick = { onDestinationClicked("takeaway_menu") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { if (!isCollapsed) Text("Orders") else Text("") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                selected = false,
                onClick = { onDestinationClicked("orders") },
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