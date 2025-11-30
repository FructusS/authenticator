package com.example.itplaneta.ui.screens.qrscanner

import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import com.example.itplaneta.core.utils.Result // если вам всё ещё нужен — не используйте в этом файле

/**
 * Асинхронный await для ListenableFuture (используется для ProcessCameraProvider.getInstance)
 */
//private suspend fun <T> androidx.concurrent.futures.ListenableFuture<T>.await(): T =
//    suspendCancellableCoroutine { cont ->
//        addListener({
//            try {
//                cont.resume(get())
//            } catch (e: Exception) {
//                cont.resumeWithException(e)
//            }
//        }, ContextCompat.getMainExecutor(null))
//        cont.invokeOnCancellation {
//            // ничего не делаем; ListenableFuture не поддерживает отмену здесь
//        }
//    }

/**
 * Preview + ImageAnalysis composable.
 *
 * onFrameAnalyzed: здесь вы получаете ImageProxy — анализируйте и не забывайте close() после обработки.
 * onError: callback при ошибках инициализации / bind.
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    // анализатор: получает ImageProxy. Обязательно close() внутри обработчика после анализа.
    onFrameAnalyzed: (ImageProxy) -> Unit = {},
    // опциональный callback для ошибок
    onError: ((Throwable) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // internal state
    var isInitializing by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf<Throwable?>(null) }

    // executor для анализатора (один поток)
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    // ensure executor shutdown when composable disposed
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraExecutor.shutdown()
            } catch (e: Exception) {
                Timber.w(e, "Failed to shutdown camera executor")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (hasError != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera initialization failed")
                Text(hasError?.localizedMessage ?: "Unknown error")
            }
            LaunchedEffect(hasError) {
                hasError?.let { onError?.invoke(it) }
            }
            return@Box
        }

        // PreviewView in AndroidView
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            }
        ) { previewView ->
            // LaunchedEffect to setup CameraX binding asynchronously
//            LaunchedEffect(previewView, cameraSelector) {
//                try {
//                    isInitializing = true
//
//                    // 1) get cameraProvider asynchronously
//                    val provider = suspendCancellableCoroutine<ProcessCameraProvider> { cont ->
//                        val future = ProcessCameraProvider.getInstance(context)
//                        future.addListener({
//                            try {
//                                cont.resume(future.get())
//                            } catch (e: Exception) {
//                                cont.resumeWithException(e)
//                            }
//                        }, ContextCompat.getMainExecutor(context))
//                    }
//
//                    // 2) create Preview and ImageAnalysis instances
//                    val preview = Preview.Builder().build()
//                        .also { it.surfaceProvider = previewView.surfaceProvider }
//
//                    val analysis = ImageAnalysis.Builder()
//                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
//                        .build()
//                        .also { ia ->
//                            // Use rememberUpdatedState pattern via local var of lambda
//                            ia.setAnalyzer(cameraExecutor) { imageProxy ->
//                                try {
//                                    onFrameAnalyzed(imageProxy)
//                                } catch (e: Throwable) {
//                                    Timber.e(e, "Analyzer exception")
//                                    // ensure we still close to avoid buffer leak
//                                    try {
//                                        imageProxy.close()
//                                    } catch (ignore: Throwable) {
//                                    }
//                                }
//                            }
//                        }
//
//                    // 3) unbind previous and bind this lifecycle
//                    provider.unbindAll()
//                    provider.bindToLifecycle(
//                        lifecycleOwner,
//                        cameraSelector,
//                        preview,
//                        analysis
//                    )
//
//                    isInitializing = false
//                } catch (e: Exception) {
//                    Timber.e(e, "Error initializing camera")
//                    hasError = e
//                }
//            }
        }
    }
}
