package com.example.itplaneta.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.Screens


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
            Card(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { }) {
                Text(
                    text = stringResource(id = R.string.how_it_works),
                    modifier = Modifier.padding(5.dp, 10.dp)
                )
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { }) {
                Text(
                    text = stringResource(id = R.string.save_accounts),
                    modifier = Modifier.padding(5.dp, 10.dp)
                )
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { }) {
                Text(
                    text = stringResource(id = R.string.load_accounts),
                    modifier = Modifier.padding(5.dp, 10.dp)
                )
            }
        }

    }
}

