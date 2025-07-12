
package com.warriortech.resb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.warriortech.resb.screens.settings.*

@Composable
fun SettingsNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "settings_main") {
        composable("area_settings") {
            AreaSettingsScreen(navController)
        }
        composable("table_settings") {
            TableSettingsScreen(navController)
        }
        composable("menu_settings") {
            MenuSettingsScreen(navController)
        }
        composable("menu_category_settings") {
            MenuCategorySettingsScreen(navController)
        }
        composable("menu_item_settings") {
            MenuItemSettingsScreen(navController)
        }
        composable("customer_settings") {
            CustomerSettingsScreen(navController)
        }
        composable("staff_settings") {
            StaffSettingsScreen(navController)
        }
        composable("language_settings") {
            LanguageSettingsScreen(navController)
        }
    }
}
