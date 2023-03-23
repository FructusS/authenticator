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
import com.example.itplaneta.ui.theme.ItplanetaTheme
import com.example.itplaneta.ui.screens.MainScreen
import com.example.itplaneta.ui.screens.AccountScreen
import com.example.itplaneta.ui.screens.ScannerScreen
import com.example.itplaneta.ui.viewmodels.AccountViewModel
import com.example.itplaneta.ui.viewmodels.MainViewModel
import com.example.itplaneta.ui.viewmodels.QrScannerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ItplanetaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.bg_main)
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
        startDestination: String = "main"
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable(route = "main") {
                val viewModel = hiltViewModel<MainViewModel>()
                MainScreen(viewModel,
                    navController,
                    onNavigateToAccount = {navController.navigate("account") }
                )
            }
            composable(route ="account") {
                val viewModel = hiltViewModel<AccountViewModel>()
                AccountScreen(
                    viewModel,
                    onNavigateToMain =  { navController.popBackStack() },
                    onNavigateToScanner = {navController.navigate("qrscanner")},
                )
            }
            composable(route ="account/{accountId}",
                arguments = listOf(navArgument("accountId"){ type = NavType.IntType})
            ) {
                    backStackEntry ->
                val viewModel = hiltViewModel<AccountViewModel>()
                AccountScreen(viewModel,
                    onNavigateToMain =  { navController.popBackStack() },
                    onNavigateToScanner = {navController.navigate("qrscanner")},
                    backStackEntry.arguments?.getInt("accountId")
                )
            }
            composable(route ="qrscanner") {
                val viewModel = hiltViewModel<QrScannerViewModel>()
                ScannerScreen(viewModel,navController)
            }
        }
    }
}

