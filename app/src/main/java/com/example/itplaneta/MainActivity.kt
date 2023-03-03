package com.example.itplaneta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itplaneta.ui.theme.ItplanetaTheme
import com.example.itplaneta.screens.MainScreen
import com.example.itplaneta.ui.screens.AccountScreen
import com.example.itplaneta.ui.viewmodels.AccountViewModel
import com.example.itplaneta.ui.viewmodels.MainViewModel
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
                    color = MaterialTheme.colors.background
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
                MainScreen(viewModel
                ) { navController.navigate("account") }
            }
            composable(route ="account") {
                val viewModel = hiltViewModel<AccountViewModel>()
                AccountScreen(viewModel,navController)
            }
        }
    }

}

