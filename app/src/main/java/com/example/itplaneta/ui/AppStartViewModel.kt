package com.example.itplaneta.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.domain.IPinRepository
import com.example.itplaneta.ui.navigation.MainDestination
import com.example.itplaneta.ui.navigation.PinDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppStartViewModel @Inject constructor(
    private val pinRepository: IPinRepository
) : ViewModel() {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isPinEnabled = pinRepository.isPinEnabledFlow.first()
            _startDestination.value = if (isPinEnabled) {
                PinDestination.route
            } else {
                MainDestination.route
            }
        }
    }
}
