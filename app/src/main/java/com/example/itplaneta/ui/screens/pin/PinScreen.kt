package com.example.itplaneta.ui.screens.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.screens.pin.component.NumericKeyboard

@Composable
fun PinScreen(
    onNavigateToMain: () -> Unit,
    onNavigateBackToSettings: () -> Unit,
    viewModel: PinViewModel = hiltViewModel(),
    canNavigateBack: Boolean,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val titleRes = when (state.scenario) {
        PinScenario.UNLOCK -> R.string.pin_enter
        PinScenario.DISABLE -> R.string.pin_confirm
        PinScenario.ENABLE -> when (state.stage) {
            PinStage.INPUT -> R.string.pin_setup
            PinStage.CONFIRM -> R.string.pin_confirm
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PinUiEvent.NavigateToMain -> onNavigateToMain()
                is PinUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(event.resId)
                    )
                }

                PinUiEvent.NavigateBackToSettings -> onNavigateBackToSettings()
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                config = topBarConfig {
                    title(titleRes)
                    if (canNavigateBack) {
                        backButton(onNavigateBackToSettings)
                    }
                })
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(6) { index ->
                        val filled = index < state.value.length
                        val baseColor = if (filled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant

                        val color = if (state.isError) MaterialTheme.colorScheme.error
                        else baseColor

                        Box(
                            modifier = Modifier
                                .height(12.dp)
                                .width(12.dp)
                                .background(
                                    color = color, shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                NumericKeyboard(
                    onDigitClick = { viewModel.onDigitClick(it) },
                    onBackspaceClick = { viewModel.onBackspaceClick() },
                    onBackspaceLongClick = { viewModel.onBackspaceLongClick() },
                    onEnterClick = { viewModel.onSubmit() },
                )
            }
        }
    }
}