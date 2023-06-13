package com.example.itplaneta

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthenticatorApp(navController: NavHostController = rememberNavController()) {
    AuthenticatorNavHost(navController = navController)
}

@Composable
fun AuthenticatorTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(title = { Text(title) }, modifier = modifier, navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })
    } else {
        if (title.isNotBlank()){
            TopAppBar(title = { Text(title) }, modifier = modifier)
        }
    }
}