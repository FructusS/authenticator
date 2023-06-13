package com.example.itplaneta.ui.screens.qrscanner

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.core.camera.QrCodeAnalyzer
import com.example.itplaneta.ui.navigation.QrScannerDestination
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    val context = LocalContext.current
    //var scanResult by rememberSaveable { mutableStateOf("") }
    Column {
        when (val status = cameraPermission.status) {
            is PermissionStatus.Denied -> {
                AlertDialog(

                    onDismissRequest = {}, text = {
                        if (status.shouldShowRationale) {
                            Text(stringResource(R.string.camera_permissions_required))
                        } else {
                            Text(stringResource(R.string.camera_permissions_not_granted))
                        }
                    }, buttons = {
                        Row(modifier = Modifier.padding(8.dp)) {

                            TextButton(onClick = { navigateBack() }) {

                                Text(stringResource(id = R.string.cancel))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                cameraPermission.launchPermissionRequest()
                            }) {
                                Text(stringResource(R.string.ok))
                            }
                        }
                    }

                )
            }

            is PermissionStatus.Granted -> {
                Scaffold(topBar = {
                    AuthenticatorTopAppBar(
                        title = stringResource(id = QrScannerDestination.titleScreen),
                        canNavigateBack = canNavigateBack,
                        navigateUp = onNavigateUp
                    )
                }) {
                    if (viewModel.hasReadCode.value) {
                        onNavigateUp()
                    } else {
                        CameraPreview(
                            state = rememberCameraState(context = context,
                                analysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST).build()
                                    .also { analysis ->
                                        analysis.setAnalyzer(
                                            ContextCompat.getMainExecutor(context),
                                            QrCodeAnalyzer(onSuccess = {
                                                if (it.text.isNotBlank()) {
                                                    viewModel.parse(it.text)
                                                }

                                            }, onFail = {
                                                Log.i("123", "fail")
                                            })
                                        )
                                    })
                        )
                    }
                }
            }
        }
    }
}
