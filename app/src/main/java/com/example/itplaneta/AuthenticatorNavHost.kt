package com.example.itplaneta

import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.itplaneta.ui.navigation.AccountDestination
import com.example.itplaneta.ui.navigation.HowItWorksDestination
import com.example.itplaneta.ui.navigation.MainDestination
import com.example.itplaneta.ui.navigation.QrScannerDestination
import com.example.itplaneta.ui.navigation.SettingsDestination
import com.example.itplaneta.ui.screens.account.AccountScreen
import com.example.itplaneta.ui.screens.howitworks.HowItWorksScreen
import com.example.itplaneta.ui.screens.mainscreen.MainScreen
import com.example.itplaneta.ui.screens.qrscanner.ScannerScreen
import com.example.itplaneta.ui.screens.settings.SettingsScreen

@Composable
fun AuthenticatorNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = MainDestination.route
) {
    val topLevelRoutes = setOf(
        MainDestination.route
    )

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(MainDestination.route) {
            MainScreen(
                navigateToSettings = { navController.navigate(SettingsDestination.route) },
                navigateToQrScanner = { navController.navigate(QrScannerDestination.route) },
                navigateToAccount = { accountId ->
                    navController.navigate(AccountDestination.createRoute(accountId)) {
                        launchSingleTop = true
                    }
                },
                canNavigateBack = false
            )
        }

        composable(
            route = AccountDestination.routeWithArgs, arguments = listOf(
                navArgument(AccountDestination.accountIdArg) {
                    type = NavType.IntType
                    defaultValue = -1
                })
        ) { backStackEntry ->
            val canNavigateBack =
                navController.previousBackStackEntry != null && navController.currentDestination?.route !in topLevelRoutes

            AccountScreen(
                navigateBack = navController::popBackStack,
                onNavigateUp = navController::navigateUp,
                canNavigateBack = canNavigateBack
            )
        }

        composable(QrScannerDestination.route) {
            val canNavigateBack =
                navController.previousBackStackEntry != null && navController.currentDestination?.route !in topLevelRoutes

            ScannerScreen(
                navigateBack = navController::popBackStack,
                onNavigateUp = navController::navigateUp,
                canNavigateBack = canNavigateBack
            )
        }

        composable(SettingsDestination.route) {
            val canNavigateBack =
                navController.previousBackStackEntry != null && navController.currentDestination?.route !in topLevelRoutes

            SettingsScreen(
                onNavigateUp = navController::navigateUp,
                onNavigateToHowItWorks = { navController.navigate(HowItWorksDestination.route) },
                canNavigateBack = canNavigateBack
            )
        }

        composable(HowItWorksDestination.route) {
            val canNavigateBack =
                navController.previousBackStackEntry != null && navController.currentDestination?.route !in topLevelRoutes
            HowItWorksScreen(
                onNavigateUp = navController::navigateUp, canNavigateBack = canNavigateBack
            )
        }
    }
}
