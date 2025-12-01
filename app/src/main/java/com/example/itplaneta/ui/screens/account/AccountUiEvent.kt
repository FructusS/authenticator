package com.example.itplaneta.ui.screens.account

import com.example.itplaneta.ui.base.UiEvent

sealed class AccountUiEvent : UiEvent {
    data object NavigateBack : AccountUiEvent()
}