package com.example.itplaneta.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.SettingsDestination
import com.example.itplaneta.ui.screens.settings.component.ThemeOptions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToHowItWorks: () -> Unit,
    canNavigateBack: Boolean,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) viewModel.saveBackupToExternal(uri)
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) viewModel.restoreBackupFromExternal(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SettingsUiEvent.ShowMessage -> {
                    val text = if (event.arg != null) {
                        context.getString(event.resId, event.arg)
                    } else {
                        context.getString(event.resId)
                    }
                    snackbarHostState.showSnackbar(text)
                }
            }
        }
    }

    Scaffold(topBar = {
        AuthenticatorTopAppBar(
            title = { Text(stringResource(id = SettingsDestination.titleRes)) },
            canNavigateBack = canNavigateBack,
            navigateUp = onNavigateUp
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.main_settings),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.how_it_works)) },
                modifier = Modifier.clickable { onNavigateToHowItWorks() })

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.save_accounts)) },
                supportingContent = {
                    Text(text = stringResource(id = R.string.save_accounts_desc))
                },
                modifier = Modifier.clickable {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm")
                    val fileName = "auth-backup-${LocalDateTime.now().format(formatter)}.json"
                    backupLauncher.launch(fileName)
                })

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.load_accounts)) },
                supportingContent = {
                    Text(text = stringResource(id = R.string.load_accounts_desc))
                },
                modifier = Modifier.clickable {
                    restoreLauncher.launch(arrayOf("application/json"))
                })

            uiState.lastBackupMessage?.let { msg ->
                Text(
                    text = if (msg.arg != null) stringResource(msg.resId, msg.arg)
                    else stringResource(msg.resId), color = when (uiState.screenState) {
                        is SettingsScreenState.BackupError -> MaterialTheme.colorScheme.error

                        else -> MaterialTheme.colorScheme.primary
                    }, style = MaterialTheme.typography.bodySmall
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            ThemeOptions(
                selectedTheme = uiState.selectedTheme,
                onThemeSelected = { viewModel.saveTheme(it) })
        }
    }
}
