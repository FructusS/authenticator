package com.example.itplaneta.ui.screens.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.base.UiEvent
import com.example.itplaneta.ui.components.AppTopBar
import com.example.itplaneta.ui.components.topBarConfig
import com.example.itplaneta.ui.screens.account.component.AccountInputForm
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel(),
    canNavigateBack: Boolean = true,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = uiState.hasUnsavedChanges) {
        focusManager.clearFocus()
        viewModel.onBackPressed()
    }


    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AccountUiEvent.NavigateBack -> navigateBack()
            }
        }
    }

    }, floatingActionButton = {
        if (uiState.hasUnsavedChanges) {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.save)) },
                onClick = {
                    coroutineScope.launch {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                config = topBarConfig {
                    title(if (viewModel.isEditMode) R.string.edit else R.string.add)
                    backButton {
                        if (uiState.hasUnsavedChanges) {
                            viewModel.onBackPressed()
                        } else {
                            onNavigateUp()
                        }
                    }
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }, content = {
            when (val screenState = uiState.screenState) {
                is AccountScreenState.Error -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.clearError() },
                        title = { Text("error") },
                        text = { Text(screenState.message) },
                        confirmButton = {
                            Button(onClick = { viewModel.clearError() }) {
                                Text("ОК")
                            }
                        })
                }

                AccountScreenState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AccountInputForm(
                            viewModel = viewModel
                        )
                    }
                }

                AccountScreenState.Loading -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is AccountScreenState.Success -> {
                }
            }
        })
        if (uiState.showUnsavedChangesDialog) {
            UnsavedChangesDialog(
                isSecretChanged = uiState.isSecretChanged,
                onSave = {
                viewModel.dismissUnsavedChangesDialog()
                coroutineScope.launch {
                    viewModel.saveAccount()
                }
            },
                onDiscard = { viewModel.discardChanges() },
                onDismiss = { viewModel.dismissUnsavedChangesDialog() })
        }
    })
}

@Composable
private fun UnsavedChangesDialog(
    isSecretChanged: Boolean,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.unsaved_changes)) },
        text = {
            val base = stringResource(id = R.string.unsaved_changes_message)
            val secretWarning = if (isSecretChanged) {
                "\n\n" + stringResource(id = R.string.secret_change_warning)
            } else {
                ""
            }
            Text(base + secretWarning)
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDiscard) {
                Text(stringResource(id = R.string.discard))
            }
        }
    )
}