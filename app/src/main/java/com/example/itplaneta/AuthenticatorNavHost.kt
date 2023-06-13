package com.example.itplaneta

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.itplaneta.ui.navigation.AddAccountDestination
import com.example.itplaneta.ui.navigation.EditAccountDestination
import com.example.itplaneta.ui.navigation.HowItWorksDestination
import com.example.itplaneta.ui.navigation.MainDestination
import com.example.itplaneta.ui.navigation.QrScannerDestination
import com.example.itplaneta.ui.navigation.SettingsDestination
import com.example.itplaneta.ui.screens.account.AddAccountScreen
import com.example.itplaneta.ui.screens.account.EditAccountScreen
import com.example.itplaneta.ui.screens.howitworks.HowItWorksScreen
import com.example.itplaneta.ui.screens.mainscreen.MainScreen
import com.example.itplaneta.ui.screens.qrscanner.ScannerScreen
import com.example.itplaneta.ui.screens.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthenticatorNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = MainDestination.route
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = startDestination
    ) {
        composable(
            route = MainDestination.route
        ) {
            MainScreen(
                navigateToSettings = { navController.navigate(SettingsDestination.route) },
                navigateToQrScanner = { navController.navigate(QrScannerDestination.route) },
                navigateToAddAccount = { navController.navigate(AddAccountDestination.route) },
                navigateToEditAccount = {
                    navController.navigate("${EditAccountDestination.route}/${it}")
                },
            )
        }

        composable(
            route = AddAccountDestination.route,
        ) {
            AddAccountScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(
            route = EditAccountDestination.routeWithArgs,
            arguments = listOf(navArgument(EditAccountDestination.accountIdArg) {
                type = NavType.IntType
            }),
        ) { backStackEntry ->
            val accountId = requireNotNull(backStackEntry.arguments?.getInt(EditAccountDestination.accountIdArg)) { "Account id is required as an argument" }
            EditAccountScreen(accountId, navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(
            route = QrScannerDestination.route,
        ) {
            ScannerScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(onNavigateUp = { navController.navigateUp() },
                onNavigateToHowItWorks = { navController.navigate(HowItWorksDestination.route) })
        }

        composable(route = HowItWorksDestination.route) {
            HowItWorksScreen(onNavigateUp = { navController.navigateUp() })
        }

    }
}


