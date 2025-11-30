package com.example.itplaneta.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base class for ViewModels with a StateFlow state and SharedFlow events.
 *
 * State should be a data class representing the whole UI state.
 * Event is a sealed class for one-off UI events (navigation, snackbars).
 */
abstract class BaseViewModel<State : Any, Event : Any> : ViewModel() {

    // Protected mutable backing state — subclasses update via setState { } or updateState(...)
    protected abstract val _uiState: MutableStateFlow<State>

    // Public read-only StateFlow
    val uiState: StateFlow<State> get() = _uiState.asStateFlow()

    // Protected mutable events flow
    private val _uiEvent = MutableSharedFlow<Event>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // Public read-only SharedFlow for UI to collect
    val uiEvent: SharedFlow<Event> get() = _uiEvent.asSharedFlow()

    // Helper: update state atomically
    protected fun setState(reducer: State.() -> State) {
        // Use viewModelScope to avoid accidental blocking; update happens synchronously but safe to call from coroutines
        _uiState.value = _uiState.value.reducer()
    }

    // Helper: update state using current value (safer style)
    protected fun updateState(update: (State) -> State) {
        _uiState.value = update(_uiState.value)
    }

    // Emit an event (suspend-safe)
    protected suspend fun emitEvent(event: Event) {
        _uiEvent.emit(event)
    }

    // Try emit (non-suspending) — returns true if emitted
    protected fun tryEmitEvent(event: Event): Boolean = _uiEvent.tryEmit(event)

    // Convenience wrapper to emit from non-suspend context within ViewModel
    protected fun postEvent(event: Event) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}