package com.example.itplaneta.ui.screens

import android.Manifest
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
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(viewModel: QrScannerViewModel, navController: NavHostController) {

    var qrCodeResult by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )


    Column() {
        // Camera preview
        when (val status = cameraPermission.status) {
            is PermissionStatus.Denied -> {
                AlertDialog(

                    onDismissRequest = { /*TODO*/ },
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
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(
                                Size(
                                    previewView.width, previewView.height
                                )
                            )
                            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer { result ->
                                qrCodeResult = result
                                viewModel.parse(result)
                                navController.popBackStack()
                            }
                        )
                        try {
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Toast.makeText(context,"QR code: $qrCodeResult",Toast.LENGTH_SHORT).show()
    }
}
