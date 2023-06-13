package com.example.itplaneta.ui.screens.settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.SettingsDestination
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToHowItWorks: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = {
            if (it != null) {
                viewModel.saveBackupToExternal(it)
            }
        }
    )
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                viewModel.restoreBackupFromExternal(it)
            }
        }
    )
    Scaffold(topBar = {
        AuthenticatorTopAppBar(title = stringResource(id = SettingsDestination.titleScreen), canNavigateBack = canNavigateBack, navigateUp = onNavigateUp)
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = stringResource(id = R.string.main_settings),
                    style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
                    color = MaterialTheme.colors.secondary
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToHowItWorks() }) {
                    Text(
                        text = stringResource(id = R.string.how_it_works),
                        modifier = Modifier.padding(0.dp, 10.dp)
                    )
                }


                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm")
                        val current = LocalDateTime.now().format(formatter)
                        backupLauncher.launch("backup-authenticator")
                    }) {
                    Text(
                        text = stringResource(id = R.string.save_accounts),
                        modifier = Modifier.padding(0.dp, 10.dp)
                    )

                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        restoreLauncher.launch(arrayOf("application/json"))
                    }) {
                    Text(
                        text = stringResource(id = R.string.load_accounts),
                        modifier = Modifier.padding(0.dp, 10.dp)
                    )
                }


            }

            Divider(
                color = Color.Black, thickness = 0.4.dp, modifier = Modifier.padding(0.dp, 15.dp)
            )
            ThemeOptions(viewModel = viewModel)
        }

    }
}

@Composable
fun ThemeOptions(viewModel: SettingsViewModel) {
    val selected = viewModel.themeState.collectAsState(initial = AppTheme.Auto).value
    val themeItems = listOf(AppTheme.Dark, AppTheme.Light, AppTheme.Auto)
    Text(
        text = stringResource(id = R.string.theme),
        style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
        color = MaterialTheme.colors.secondary
    )
    themeItems.forEach { itemTheme ->
        Row(

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                viewModel.saveTheme(itemTheme)
            }) {
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
                selected = selected == itemTheme, onClick = {
                    viewModel.saveTheme(itemTheme)
                }, enabled = true, colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Magenta
                )
            )
        }
    }
}
