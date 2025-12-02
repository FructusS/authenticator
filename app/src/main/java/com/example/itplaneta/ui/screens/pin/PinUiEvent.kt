package com.example.itplaneta.ui.screens.pin

import androidx.annotation.StringRes
import com.example.itplaneta.ui.base.UiEvent

sealed class PinUiEvent : UiEvent {
    object NavigateToMain : PinUiEvent()
    object NavigateBackToSettings : PinUiEvent()
    data class ShowMessage(@StringRes val resId: Int) : PinUiEvent()
    object OpenApp : PinUiEvent()
    object LaunchBiometric : PinUiEvent()
}