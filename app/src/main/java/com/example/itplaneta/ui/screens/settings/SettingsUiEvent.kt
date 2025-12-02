package com.example.itplaneta.ui.screens.settings

import androidx.annotation.StringRes
import com.example.itplaneta.ui.base.UiEvent
import com.example.itplaneta.ui.screens.pin.PinScenario

sealed class SettingsUiEvent : UiEvent {
    data class NavigateToPinScreen(val mode: PinScenario) : SettingsUiEvent()

    data class ShowMessage(@StringRes val resId: Int, val arg: Int? = null) : SettingsUiEvent()
}