
package com.warriortech.resb.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.warriortech.resb.screens.settings.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "settings_main") {
        composable("area_settings") {
            AreaSettingsScreen(onBackPressed = {
                navController.popBackStack()
            })
        }
        composable("table_settings") {
            TableSettingsScreen(onBackPressed = {
                navController.popBackStack()
            })
        }
        composable("menu_settings") {
            MenuSettingsScreen(onBackPressed = {
                navController.popBackStack()
            })
        }
        composable("menu_category_settings") {
            MenuCategorySettingsScreen()
        }
        composable("menu_item_settings") {
            MenuItemSettingsScreen()
        }
        composable("customer_settings") {
            CustomerSettingsScreen(onBackPressed = {
                navController.popBackStack()
            })
        }
        composable("staff_settings") {
            StaffSettingsScreen(onBackPressed = {
                navController.popBackStack()
            })
        }
        composable("language_settings") {
            LanguageSettingsScreen(navController)
        }
    }
}
