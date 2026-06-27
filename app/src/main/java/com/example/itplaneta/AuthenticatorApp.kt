package com.example.itplaneta

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.itplaneta.ui.AppStartViewModel
import com.example.itplaneta.ui.navigation.AuthenticatorNavHost

@Composable
fun AuthenticatorApp(
    navController: NavHostController = rememberNavController(),
    viewModel: AppStartViewModel = hiltViewModel()
) {
    val startDestination by viewModel.startDestination.collectAsState()

    startDestination?.let {
        AuthenticatorNavHost(
            navController = navController,
            startDestination = it
        )
    }
}
