package com.example.itplaneta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.itplaneta.ui.navigation.Screens
import com.example.itplaneta.ui.screens.account.AccountScreen
import com.example.itplaneta.ui.screens.main.MainScreen
import com.example.itplaneta.ui.screens.qrscanner.ScannerScreen
import com.example.itplaneta.ui.screens.settings.AppTheme
import com.example.itplaneta.ui.screens.settings.SettingsManager
import com.example.itplaneta.ui.screens.settings.SettingsScreen
import com.example.itplaneta.ui.theme.AuthenticatorTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsManager = SettingsManager(LocalContext.current)
            val theme = settingsManager.getTheme.collectAsState(initial = AppTheme.Auto)
            AuthenticatorTheme(
                darkTheme = when (theme.value) {
                    AppTheme.Light -> false
                    AppTheme.Dark -> true
                    AppTheme.Auto -> isSystemInDarkTheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MyAppNavHost()
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun MyAppNavHost(
        modifier: Modifier = Modifier,
        navController: NavHostController = rememberAnimatedNavController(),
        startDestination: String = Screens.Main.route
    ) {
        AnimatedNavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screens.Main.route, enterTransition = {
                when (initialState.destination.route) {
                    Screens.AddAccount.route ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(350)
                        )
                    Screens.Settings.route -> {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(350)
                        )
                    }
                    Screens.EditAccount.route -> {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(350)
                        )
                    }
                    Screens.QrScanner.route -> {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(350)
                        )
                    }
                    else -> null
                }
            },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screens.AddAccount.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(350)
                            )
                        Screens.Settings.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(350)
                            )
                        Screens.QrScanner.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(350)
                            )
                        Screens.EditAccount.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(350)
                            )
                        else -> null
                    }
                }) {
                MainScreen(navController = navController)
            }

            composable(
                route = Screens.AddAccount.route,
            ) {
                AccountScreen(navController = navController)
            }
            composable(
                route = Screens.EditAccount.route,
                arguments = listOf(navArgument("accountId") { type = NavType.IntType }),
            ) { backStackEntry ->
                backStackEntry.arguments?.getInt("accountId")?.let {
                    AccountScreen(
                        navController = navController,
                        it
                    )
                }

            }
            composable(
                route = Screens.QrScanner.route,
            ) {
                ScannerScreen(navController)
            }
            composable(route = Screens.Settings.route) {
                SettingsScreen(navController)
            }
        }
    }
}

