package com.example.itplaneta.ui.screens.qrscanner

import com.example.itplaneta.ui.base.UiState

sealed class QrScannerScreenState : UiState {
    object Idle : QrScannerScreenState()
    object Loading : QrScannerScreenState()
    object Success : QrScannerScreenState()

    data class Error(val message: String) : QrScannerScreenState()
}

data class QrScannerUiState(
    val screenState: QrScannerScreenState = QrScannerScreenState.Idle,
    val hasReadCode: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val shouldShowRationale: Boolean = false,
    val isCameraReady: Boolean = false
) : UiState