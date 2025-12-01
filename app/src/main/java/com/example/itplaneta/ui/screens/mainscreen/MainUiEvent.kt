package com.example.itplaneta.ui.screens.mainscreen

import androidx.annotation.StringRes
import com.example.itplaneta.ui.base.UiEvent

sealed class MainUiEvent : UiEvent{
    data class ShowMessage(@StringRes val resId: Int) : MainUiEvent()
}