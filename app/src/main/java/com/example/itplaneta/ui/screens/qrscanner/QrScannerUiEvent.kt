package com.example.itplaneta.ui.screens.qrscanner

import androidx.annotation.StringRes
import com.example.itplaneta.ui.base.UiEvent

sealed class QrScannerUiEvent : UiEvent {
    data object NavigateBack : QrScannerUiEvent()
    data class ShowMessage(@StringRes val resId: Int) : QrScannerUiEvent()
}