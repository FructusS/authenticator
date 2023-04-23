package com.example.itplaneta.ui.screens.settings

import android.content.res.Resources.Theme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.Screens
import com.example.itplaneta.ui.theme.AuthenticatorTheme


@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant) {
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
        },
    ) {
        Column(Modifier.padding(it)) {
            val currentTheme = viewModel.themeState.collectAsState().value
            val (selected, setSelected) = remember { mutableStateOf(currentTheme) }
            val themeItems = listOf(AppTheme.Dark,AppTheme.Light,AppTheme.Auto)
//            Card(modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .clickable { viewModel.saveTheme(AppTheme.Light)}) {
//                Text(
//                    text = stringResource(id = R.string.how_it_works),
//                    modifier = Modifier.padding(5.dp, 10.dp)
//                )
//            }
//            Card(modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .clickable { viewModel.saveTheme(AppTheme.Dark)}) {
//                Text(
//                    text = stringResource(id = R.string.save_accounts),
//                    modifier = Modifier.padding(5.dp, 10.dp)
//                )
//            }
//            Card(modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .clickable {viewModel.saveTheme(AppTheme.Auto) }) {
//                Text(
//                    text = stringResource(id = R.string.load_accounts),
//                    modifier = Modifier.padding(5.dp, 10.dp)
//                )
//            }
            themeItems.forEach { itemTheme ->
                Row(

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = itemTheme.name, modifier = Modifier.padding(start = 8.dp))

                    RadioButton(
                        selected = selected == itemTheme,
                        onClick = {
                            viewModel.saveTheme(itemTheme)
                        },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta
                        )
                    )

                }
            }
        }

    }
}

