package com.example.itplaneta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.itplaneta.ui.screens.account.AccountScreen
import com.example.itplaneta.ui.screens.howitworks.HowItWorksScreen
import com.example.itplaneta.ui.screens.mainscreen.MainScreen
import com.example.itplaneta.ui.screens.pin.PinScenario
import com.example.itplaneta.ui.screens.pin.PinScreen
import com.example.itplaneta.ui.screens.qrscanner.ScannerScreen
import com.example.itplaneta.ui.screens.settings.SettingsScreen

@Composable
fun AuthenticatorNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = PinDestination.route
) {
    val topLevelRoutes = setOf(
        MainDestination.route
    )

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(
            PinDestination.routeWithArgs, arguments = listOf(
                navArgument(PinDestination.modeArg) {
                    type = NavType.StringType
                    defaultValue = PinScenario.UNLOCK.name
                })
        ) { it ->

            val canNavigateBack =
                navController.previousBackStackEntry != null && navController.currentDestination?.route !in topLevelRoutes
            PinScreen(onNavigateToMain = {
                navController.navigate(MainDestination.route) {
                    popUpTo(PinDestination.route) { inclusive = true }
                }
            }, canNavigateBack = canNavigateBack, onNavigateBackToSettings = {
                navController.popBackStack()
            })
        }

        composable(MainDestination.route) {
            MainScreen(
                onNavigateToSettings = { navController.navigate(SettingsDestination.route) },
                onNavigateToQrScanner = { navController.navigate(QrScannerDestination.route) },
                onNavigateToAccount = { accountId ->
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
                onNavigateToPin = { navController.navigate(PinDestination.createRoute(it)) },
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
