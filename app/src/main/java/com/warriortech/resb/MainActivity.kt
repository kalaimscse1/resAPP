package com.warriortech.resb

//import com.warriortech.resb.data.sync.SyncManager
import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.warriortech.resb.data.sync.SyncManager
import com.warriortech.resb.model.KotData
import com.warriortech.resb.model.Table
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.screens.LoginScreen
import com.warriortech.resb.screens.MenuScreen
import com.warriortech.resb.screens.TableScreen
import com.warriortech.resb.ui.theme.ResbTheme
import com.warriortech.resb.util.ConnectionState
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.NetworkStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
import androidx.compose.ui.Alignment


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var syncManager: SyncManager

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
            ResbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material.MaterialTheme.colors.background
                ) {
                    val connectionState by networkMonitor.isOnline.collectAsState(
                        initial = ConnectionState.Available
                    )

                    Column {
                        // Network status bar at the top
                        NetworkStatusBar(connectionState = connectionState)

                        // Main navigation/content
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // MainNavigation() or appropriate navigation component
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // State to hold the selected table
    var selectedTable by remember { mutableStateOf<Table?>(null) }

    // State to hold the KOT data for printing
    var kotData by remember { mutableStateOf<KotData?>(null) }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("tables") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("tables") {
            TableScreen(
                onTableSelected = { table ->
                    selectedTable = table
                    navController.navigate("menu")
                }
            )
        }

        composable("menu") {
            selectedTable?.let { table ->
                MenuScreen(
                    tableId = table.table_id.toInt(),
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onOrderPlaced = { navController.popBackStack() }
                )
            }
        }
    }
}
