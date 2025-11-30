package com.example.itplaneta.ui.screens.qrscanner

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import timber.log.Timber
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.core.camera.QrCodeAnalyzer
import com.example.itplaneta.ui.navigation.QrScannerDestination

@Composable
fun ScannerScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // camera permission state via Accompanist Permissions
   // val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)

    // Launcher to open app settings (if user denied permanently)
    val openSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    // When the viewModel signals that code was read — navigate up once.
    LaunchedEffect(viewModel.hasReadCode.value) {
        if (viewModel.hasReadCode.value) {
            onNavigateUp()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
//        when (val status = cameraPermission.status) {
//            is PermissionStatus.Denied -> {
//                // Show Material3 AlertDialog explaining permission & actions
//                AlertDialog(
//                    onDismissRequest = { /* don't dismiss by outside touch */ },
//                    title = { Text(text = if (status.shouldShowRationale) "Требуется доступ к камере" else "Камера не разрешена") },
//                    text = {
//                        Text(
//                            text = if (status.shouldShowRationale)
//                            // Пользователь ранее отклонил — покажем обоснование
//                                "Приложению нужен доступ к камере для сканирования QR-кодов."
//                            else
//                                "Чтобы сканировать QR-коды, разрешите доступ к камере в настройках или нажмите Разрешить."
//                        )
//                    },
//                    confirmButton = {
//                        TextButton(onClick = {
//                            // Если можно — запросим разрешение, иначе предложим открыть настройки
//                            if (status.shouldShowRationale) {
//                                cameraPermission.launchPermissionRequest()
//                            } else {
//                                // Если пользователь окончательно отклонил (no rationale),
//                                // даём путь в настройки приложения
//                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                                    data = android.net.Uri.fromParts("package", context.packageName, null)
//                                }
//                                openSettingsLauncher.launch(intent)
//                            }
//                        }) {
//                            Text(text = if (status.shouldShowRationale) "Разрешить" else "Открыть настройки")
//                        }
//                    },
//                    dismissButton = {
//                        TextButton(onClick = { navigateBack() }) {
//                            Text(text = "Отмена")
//                        }
//                    }
//                )
//            }
//
//            is PermissionStatus.Granted -> {
//                Scaffold(
//                    topBar = {
//                        AuthenticatorTopAppBar(
//                            title = stringResource(id = QrScannerDestination.titleScreen),
//                            canNavigateBack = canNavigateBack,
//                            navigateUp = onNavigateUp
//                        )
//                    }
//                ) { innerPadding ->
//                    Box(modifier = Modifier
//                        .fillMaxSize()
//                        .padding(innerPadding)
//                    ) {
//                        // Create ImageAnalysis and set its analyzer using remember + DisposableEffect
//                        val analysis = rememberImageAnalysis(context = context, onQrFound = { qrText ->
//                            // parse on background thread via ViewModel
//                            if (qrText.isNotBlank()) {
//                                viewModel.parse(qrText)
//                            }
//                        }, onFail = {
//                            Timber.i("QR analysis failed")
//                        })
//
//                        // Ensure analyzers are cleared when this composable leaves
//                        DisposableEffect(analysis) {
//                            onDispose {
//                                runCatching { analysis.clearAnalyzer() }.onFailure {
//                                    Timber.w(it, "Failed to clear analyzer")
//                                }
//                            }
//                        }
//
//                        // Provide camera state with analysis to CameraPreview (custom composable)
//                        CameraPreview(
//                            modifier = Modifier.fillMaxSize(),
//                            onFrameAnalyzed = { imageProxy ->
//                                // передаём в QrCodeAnalyzer или обрабатываем прямо здесь
//                                // важно: если вы используете QrCodeAnalyzer, он сам должен close() imageProxy
//                                qrCodeAnalyzer.analyze(imageProxy) // пример
//                            },
//                            onError = { throwable ->
//                                // показать Snackbar / логировать
//                            }
//                        )
//                    }
//                }
//            }
//        }
    }
}

/**
 * Создает ImageAnalysis с установленным QrCodeAnalyzer. Важно вызывать из Composable,
 * чтобы analyzer можно было корректно удалить в DisposableEffect.
 */
@Composable
private fun rememberImageAnalysis(
    context: android.content.Context,
    onQrFound: (String) -> Unit,
    onFail: (Throwable) -> Unit
): ImageAnalysis {
    return remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QrCodeAnalyzer(onSuccess = { result ->
                        // результат типа Barcode/Result — адаптируйте под реализацию вашего QrCodeAnalyzer
                        //onQrFound(result.text)
                    }, onFail = { throwable ->
                        onFail(throwable)
                    })
                )
            }
    }
}
