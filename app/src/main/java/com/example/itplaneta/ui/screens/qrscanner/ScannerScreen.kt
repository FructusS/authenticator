package com.example.itplaneta.ui.screens.qrscanner

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.base.UiEvent
import com.example.itplaneta.ui.components.AppTopBar
import com.example.itplaneta.ui.components.topBarConfig
import com.example.itplaneta.ui.navigation.QrScannerDestination
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun ScannerScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(state.hasReadCode) {
        if (state.hasReadCode) {
            onNavigateUp()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is QrScannerUiEvent.NavigateBack -> navigateBack()
                is QrScannerUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        context.getString(event.resId)
                    )
                }
            }
        }
    }

    Scaffold(topBar = {
        AppTopBar(
            config = topBarConfig {
                title(R.string.scanning)
                backButton(onNavigateUp)
            })
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.hasCameraPermission -> {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        analyzer = viewModel.analyzer,
                        onError = { throwable ->
                            viewModel.onCameraError(throwable)
                        },
                        onCameraReady = { viewModel.onCameraReady() })

                    if (!state.isCameraReady) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                                )
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = stringResource(id = R.string.qr_hint_title),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.qr_hint_subtitle),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                state.shouldShowRationale -> {
                    AlertDialog(
                        onDismissRequest = { navigateBack() },
                        title = { Text(stringResource(id = R.string.camera_permission_title)) },
                        text = {
                            Text(stringResource(id = R.string.camera_permission_rationale))
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }) {
                                Text(stringResource(id = R.string.allow))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = navigateBack) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        })
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.requesting_camera_permission),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
