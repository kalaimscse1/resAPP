import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "template_screen") {
        composable("template_screen") {
            TemplateScreen(
                onBackPressed = { navController.popBackStack() },
                navController = navController
            )
        }

        composable("modifier_setting") {
            ModifierSettingsScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun TemplateScreen(onBackPressed: () -> Unit, navController: NavController) {
    // Placeholder for TemplateScreen content
}

@Composable
fun ModifierSettingsScreen(onBackPressed: () -> Unit) {
    // Placeholder for ModifierSettingsScreen content
}