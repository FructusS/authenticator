package com.example.itplaneta.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.itplaneta.R
import com.example.itplaneta.ui.screens.component.TopBar


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopBar(navController = navController)
        }

    ) {
        Column(
            Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {

                    Text(
                        text = stringResource(id = R.string.how_it_works),
                        modifier = Modifier.padding(5.dp, 10.dp)
                    )


                    Text(
                        text = stringResource(id = R.string.save_accounts),
                        modifier = Modifier.padding(5.dp, 10.dp)
                    )


                    Text(
                        text = stringResource(id = R.string.load_accounts),
                        modifier = Modifier.padding(5.dp, 10.dp)
                    )

            }

            Divider(color = Color.Black, thickness = 0.4.dp)

            ThemeOptions(viewModel = viewModel)
        }

    }
}

@Composable
fun ThemeOptions(viewModel: SettingsViewModel) {
    val selected = viewModel.themeState.collectAsState(initial = AppTheme.Auto).value
    val themeItems = listOf(AppTheme.Dark, AppTheme.Light, AppTheme.Auto)
    Text(text = stringResource(id = R.string.theme))
    themeItems.forEach { itemTheme ->
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                viewModel.saveTheme(itemTheme)
            }
        ) {
            when (itemTheme) {
                AppTheme.Auto -> {
                    Text(
                        text = stringResource(id = R.string.theme_auto), modifier = Modifier

                            .weight(1f)
                    )
                }
                AppTheme.Dark -> {
                    Text(
                        text = stringResource(id = R.string.theme_dark), modifier = Modifier

                            .weight(1f)
                    )
                }
                AppTheme.Light -> {
                    Text(
                        text = stringResource(id = R.string.theme_light), modifier = Modifier

                            .weight(1f)
                    )
                }
            }


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
