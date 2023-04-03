package com.example.itplaneta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.itplaneta.ui.navigation.Screens
import com.example.itplaneta.ui.screens.AccountScreen
import com.example.itplaneta.ui.screens.MainScreen
import com.example.itplaneta.ui.screens.ScannerScreen
import com.example.itplaneta.ui.theme.ItplanetaTheme
import com.example.itplaneta.ui.viewmodels.QrScannerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ItplanetaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MyAppNavHost()
                }
            }
        }
    }

    @Composable
    fun MyAppNavHost(
        modifier: Modifier = Modifier,
        navController: NavHostController = rememberNavController(),
        startDestination: String = Screens.Main.route
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screens.Main.route) {
                MainScreen(navController = navController)
            }

            composable(route = Screens.AddAccount.route) {
                AccountScreen(navController = navController)
            }
            composable(
                route = Screens.EditAccount.route,
                arguments = listOf(navArgument("accountId") { type = NavType.IntType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getInt("accountId")?.let {
                    AccountScreen(
                        navController = navController,
                        it
                    )
                }

            }
            composable(route = Screens.QrScanner.route) {
                ScannerScreen(navController)
            }
        }
    }
}

