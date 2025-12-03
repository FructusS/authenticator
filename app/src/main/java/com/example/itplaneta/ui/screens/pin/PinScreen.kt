package com.example.itplaneta.ui.screens.pin

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.R
import com.example.itplaneta.ui.components.AppTopBar
import com.example.itplaneta.ui.components.topBarConfig
import com.example.itplaneta.ui.screens.pin.component.NumericKeyboard
import kotlin.math.roundToInt

@Composable
fun PinScreen(
    onNavigateToMain: () -> Unit,
    onNavigateBackToSettings: () -> Unit,
    viewModel: PinViewModel = hiltViewModel(),
    canNavigateBack: Boolean,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val titleRes = when (state.scenario) {
        PinScenario.UNLOCK -> R.string.pin_enter
        PinScenario.DISABLE -> R.string.pin_confirm
        PinScenario.ENABLE -> when (state.stage) {
            PinStage.INPUT -> R.string.pin_setup
            PinStage.CONFIRM -> R.string.pin_confirm
        }
    }

    // 1) проверка, есть ли биометрия на устройстве и пользователь её настроил
    LaunchedEffect(Unit) {
        val manager = BiometricManager.from(context)
        val canAuth = manager.canAuthenticate(BIOMETRIC_STRONG)
        val available = canAuth == BiometricManager.BIOMETRIC_SUCCESS
        viewModel.onBiometricAvailability(available)

        // автозапуск при входе в приложение, если всё включено
        if (available) {
            viewModel.onBiometricRequested()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PinUiEvent.OpenApp -> onNavigateToMain()
                is PinUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(event.resId)
                    )
                }

                PinUiEvent.NavigateBackToSettings -> onNavigateBackToSettings()
                PinUiEvent.LaunchBiometric -> {
                    showBiometricPrompt(
                        activity = context,
                        onSuccess = { viewModel.onBiometricSuccess() },
                        onError = { viewModel.onBiometricError() })
                }
            }
        }
    }

    val offsetX by animateFloatAsState(
        targetValue = if (state.isError) 16f else 0f, animationSpec = keyframes {
            durationMillis = 300
            0f at 0
            16f at 50
            16f at 100
            8f at 150
            8f at 200
            0f at 300
        }, label = ""
    )

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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.offset { IntOffset(offsetX.roundToInt(), 0) }) {
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
    if (state.canUseBiometric && state.isBiometricEnabled && state.scenario == PinScenario.UNLOCK) {
        IconButton(onClick = { viewModel.onBiometricRequested() }) {
            Icon(
                Icons.Default.Fingerprint, contentDescription = null
            )
        }
    }
}

fun showBiometricPrompt(
    activity: Context, onSuccess: () -> Unit, onError: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onError()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            // один неверный палец — просто игнорируем, диалог остаётся
        }
    }

//    val prompt = BiometricPrompt(activity, executor, callback)
//
//    val promptInfo =
//        BiometricPrompt.PromptInfo.Builder().setTitle("R.string.biometric_prompt_title")
//            .setSubtitle("R.string.biometric_prompt_subtitle")
//            .setNegativeButtonText(activity.getString(R.string.cancel)).build()
//
//    prompt.authenticate(promptInfo)
}
