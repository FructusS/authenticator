package com.example.itplaneta.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import com.example.itplaneta.R
import androidx.compose.material.AlertDialog
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.itplaneta.camera.QrCodeAnalyzer
import com.example.itplaneta.ui.viewmodels.QrScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(viewModel: QrScannerViewModel, navController: NavHostController) {
    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    val context = LocalContext.current


    Column() {
        // Camera preview
        when (val status = cameraPermission.status) {
            is PermissionStatus.Denied -> {
                AlertDialog(

                    onDismissRequest = {},
                    text = {
                        if (status.shouldShowRationale) {
                            Text(stringResource(R.string.camera_permissions_required))
                        } else {
                            Text(stringResource(R.string.camera_permissions_not_granted))
                        }
                    },
                    buttons = {
                        Row(modifier = Modifier.padding(8.dp)){


                            TextButton(onClick = { navController.popBackStack() })
                            {

                                Text(stringResource(id = R.string.cancel))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = { cameraPermission.launchPermissionRequest() })
                            {
                                Text(stringResource(R.string.accept))
                            }
                        }
                    }

                )
            }
            is PermissionStatus.Granted -> {
                Scaffold() {
                    CameraPreview(
                        state = rememberCameraState(
                            context = context,
                            analysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also { analysis ->
                                    analysis.setAnalyzer(
                                        ContextCompat.getMainExecutor(context),
                                        QrCodeAnalyzer(
                                            onSuccess = {
                                                viewModel.parse(it.text)
                                                navController.navigate("main")
                                            },
                                            onFail = {

                                            }
                                        )
                                    )
                                }
                        )
                    )
                }

            }
        }
    }
}
