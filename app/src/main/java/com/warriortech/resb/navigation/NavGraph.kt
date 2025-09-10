import com.warriortech.resb.screens.settings.MenuCategorySettingsScreen
import com.warriortech.resb.screens.settings.MenuItemSettingsScreen
import com.warriortech.resb.screens.settings.MenuSettingsScreen
import com.warriortech.resb.screens.settings.ModifierSettingsScreen
import com.warriortech.resb.screens.settings.UnitSettingsScreen
import com.warriortech.resb.screens.settings.KitchenCategorySettingsScreen
import com.warriortech.resb.screens.settings.VoucherTypeSettingsScreen
import com.warriortech.resb.screens.settings.PrinterSettingsScreen
import com.warriortech.resb.screens.settings.RestaurantProfileScreen
import com.warriortech.resb.screens.settings.RoleSettingsScreen
        composable("modifier_setting") {
            ModifierSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("unit_setting") {
            UnitSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("kitchen_category_setting") {
            KitchenCategorySettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("voucher_type_setting") {
            VoucherTypeSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        composable("change_password") {
            ChangePasswordScreen(onBackPressed = { navController.popBackStack() })
        }