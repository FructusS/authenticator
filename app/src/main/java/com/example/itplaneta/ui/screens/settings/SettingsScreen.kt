package com.example.itplaneta.ui.screens.settings

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.R
import com.example.itplaneta.ui.components.AppTopBar
import com.example.itplaneta.ui.components.topBarConfig
import com.example.itplaneta.ui.screens.pin.showBiometricPrompt
import com.example.itplaneta.ui.screens.settings.component.ThemeOptions
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToHowItWorks: () -> Unit,
    onNavigateToPin: (String) -> Unit,
    canNavigateBack: Boolean,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null){
            viewModel.saveBackupToExternal(uri)
        }
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
                        context.resources.getQuantityString(event.resId, event.arg, event.arg)
                    } else {
                        context.getString(event.resId)
                    }
                    snackbarScope.launch {
                        snackbarHostState.showSnackbar(text)
                    }
                }

                is SettingsUiEvent.NavigateToPinScreen -> onNavigateToPin(event.mode.name)
                SettingsUiEvent.LaunchBiometricToEnable -> {
                    val activity = context.findFragmentActivity()
                    if (activity == null) {
                        viewModel.onBiometricEnableFailed()
                    } else {
                        showBiometricPrompt(
                            activity = activity,
                            onSuccess = { viewModel.onBiometricEnableAuthenticated() },
                            onError = { viewModel.onBiometricEnableFailed() }
                        )
                    }
                }

                SettingsUiEvent.LaunchBiometricToDisable -> {
                    val activity = context.findFragmentActivity()
                    if (activity == null) {
                        viewModel.onBiometricDisableFailed()
                    } else {
                        showBiometricPrompt(
                            activity = activity,
                            onSuccess = { viewModel.onBiometricDisableAuthenticated() },
                            onError = { viewModel.onBiometricDisableFailed() }
                        )
                    }
                }
            }
        }
    }

    Scaffold(topBar = {
        AppTopBar(
            config = topBarConfig {
                title(R.string.settings)
                backButton(onNavigateUp)
            })
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
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
                    val fileName =
                        "backup-authenticator-${LocalDateTime.now().format(formatter)}.json"
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
                    text = if (msg.arg != null) pluralStringResource(msg.resId, msg.arg, msg.arg)
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

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = stringResource(id = R.string.security),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.pin_lock_title)) },
                supportingContent = { Text(stringResource(id = R.string.pin_lock_desc)) },
                trailingContent = {
                    Switch(
                        checked = uiState.isPinEnabled,
                        onCheckedChange = { viewModel.onPinCheckedChange(it) },
                        thumbContent = if (uiState.isPinEnabled) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        }
                    )
                    }
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.biometric_unlock_title)) },
                supportingContent = {
                    val textRes = when {
                        !uiState.biometricAvailability.isAvailable -> R.string.biometric_unavailable_desc
                        !uiState.isPinEnabled -> R.string.biometric_requires_pin_desc
                        else -> R.string.biometric_unlock_desc
                    }
                    Text(stringResource(id = textRes))
                },
                trailingContent = {
                    Switch(
                        checked = uiState.isBiometricEnabled && uiState.canToggleBiometric,
                        enabled = uiState.canToggleBiometric,
                        onCheckedChange = { viewModel.onBiometricCheckedChange(it) },
                        thumbContent = if (uiState.isBiometricEnabled && uiState.canToggleBiometric) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            )
        }
    }
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity? {
    return when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.findFragmentActivity()
        else -> null
    }
}
