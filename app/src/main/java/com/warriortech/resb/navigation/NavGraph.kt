composable(
            route = "edit_paid_bill/{billId}",
            arguments = listOf(navArgument("billId") { type = NavType.LongType })
        ) { backStackEntry ->
            val billId = backStackEntry.arguments?.getLong("billId") ?: 0L
            EditPaidBillScreen(navController, billId)
        }

        composable(
            route = "bill_template/{billId}",
            arguments = listOf(navArgument("billId") { type = NavType.LongType })
        ) { backStackEntry ->
            val billId = backStackEntry.arguments?.getLong("billId") ?: 0L
            BillTemplateScreen(navController, billId)
        }