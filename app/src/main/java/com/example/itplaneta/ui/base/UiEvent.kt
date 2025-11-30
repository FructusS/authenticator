package com.example.itplaneta.ui.base

/**
 * Marker interface for UI events
 * Use sealed class for type-safe event handling
 */
sealed class UiEvent {
    data object NavigateBack : UiEvent()
    data object ShowMessage : UiEvent()
}
