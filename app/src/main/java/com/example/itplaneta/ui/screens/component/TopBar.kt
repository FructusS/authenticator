package com.example.itplaneta.ui.screens.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.Screens

@Composable
fun TopBar(navController : NavController){
    TopAppBar(backgroundColor = MaterialTheme.colors.primary) {
        IconButton(onClick = {
            navController.navigate(Screens.Main.route) {
                popUpTo(Screens.Main.route) {
                    inclusive = true
                }
            }
        }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.back)
            )
        }
    }

}