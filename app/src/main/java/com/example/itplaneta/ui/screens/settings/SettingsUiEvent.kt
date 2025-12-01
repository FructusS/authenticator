package com.example.itplaneta.ui.screens.settings

import androidx.annotation.StringRes
import com.example.itplaneta.ui.base.UiEvent

sealed class SettingsUiEvent : UiEvent {
    data class ShowMessage(@StringRes val resId: Int, val arg: Int? = null) : SettingsUiEvent()
}