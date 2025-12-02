package com.example.itplaneta.ui.screens.qrscanner

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.R
import com.example.itplaneta.core.camera.QrCodeAnalyzer
import com.example.itplaneta.core.otp.parser.UriOtpParser
import com.example.itplaneta.domain.IAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.domain.QrCodeAnalyzerFactory
import com.example.itplaneta.ui.base.BaseViewModel
import com.example.itplaneta.ui.base.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val accountRepository: IAccountRepository,
    private val uriOtpParser: UriOtpParser,
    qrCodeAnalyzerFactory: QrCodeAnalyzerFactory
) : BaseViewModel<QrScannerUiState, QrScannerUiEvent>() {

    companion object {
        private var lastScanErrorAt: Long = 0L
        private const val scanErrorIntervalMs = 5_000L
    }

    override val _uiState = MutableStateFlow(QrScannerUiState())

    val analyzer: QrCodeAnalyzer = qrCodeAnalyzerFactory.create(onSuccess = { text ->
        if (text.isNotBlank() && !_uiState.value.hasReadCode) {
            parse(text)
        }
    }, onFail = {
        Timber.v("QR not found in frame")
    })

    private fun onScanError() {
        val now = System.currentTimeMillis()
        if (now - lastScanErrorAt >= scanErrorIntervalMs) {
            lastScanErrorAt = now
            postEvent(QrScannerUiEvent.ShowMessage(R.string.fail_scan_qr_code))
        }
    }

    fun onPermissionResult(granted: Boolean) {
        updateState {
            it.copy(
                hasCameraPermission = granted, shouldShowRationale = !granted
            )
        }
    }

    fun onCameraReady() {
        updateState { it.copy(isCameraReady = true) }
    }

    fun onCameraError(e: Throwable) {
        Timber.e(e, "Camera error")
        postEvent(QrScannerUiEvent.ShowMessage(R.string.camera_error))
    }

    fun parse(uri: String) {
        viewModelScope.launch {
            try {
                when (val result = uriOtpParser.parse(uri)) {
                    is Result.Success -> {
                        accountRepository.addAccount(result.data)
                        updateState { it.copy(hasReadCode = true) }
                    }

                    is Result.Error -> {
                        onScanError()
                        analyzer.reset()
                    }

                    Result.Loading -> Unit
                }
            } catch (e: Exception) {
                Timber.e(e, "QR parse failed")
                onScanError()

            }
        }
    }
}