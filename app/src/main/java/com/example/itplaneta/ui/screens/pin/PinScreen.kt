package com.example.itplaneta.ui.screens.pin

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.R
import com.example.itplaneta.ui.components.AppTopBar
import com.example.itplaneta.ui.components.topBarConfig
import com.example.itplaneta.ui.screens.pin.component.NumericKeyboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val snackbarScope = rememberCoroutineScope()
    val context = LocalContext.current

    val titleRes = when (state.scenario) {
        PinScenario.UNLOCK -> R.string.pin_enter
        PinScenario.DISABLE -> R.string.pin_confirm
        PinScenario.ENABLE -> when (state.stage) {
            PinStage.INPUT -> R.string.pin_setup
            PinStage.CONFIRM -> R.string.pin_confirm
        }
    }

    LaunchedEffect(state.screenState) {
        if (state.screenState == PinCodeScreenState.Success && state.scenario == PinScenario.UNLOCK) {
            delay(PinAnimationConstants.SUCCESS_DELAY_MS)
            onNavigateToMain()
        }
    }

    LaunchedEffect(state.biometricPromptRequest) {
        if (state.biometricPromptRequest <= 0) {
            return@LaunchedEffect
        }

        val activity = context.findFragmentActivity()
        if (activity == null) {
            viewModel.onBiometricError()
        } else {
            showBiometricPrompt(
                activity = activity,
                onSuccess = { viewModel.onBiometricSuccess() },
                onError = { viewModel.onBiometricError() }
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PinUiEvent.OpenApp -> onNavigateToMain()
                is PinUiEvent.ShowMessage -> {
                    snackbarScope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(event.resId)
                        )
                    }
                }

                PinUiEvent.NavigateBackToSettings -> onNavigateBackToSettings()
            }
        }
    }

    val offsetX by animateFloatAsState(
        targetValue = if (state.isError) PinAnimationConstants.ERROR_SHAKE_FULL_OFFSET else 0f,
        animationSpec = keyframes {
            durationMillis = PinAnimationConstants.ERROR_SHAKE_DURATION_MS
            0f at 0
            PinAnimationConstants.ERROR_SHAKE_FULL_OFFSET at
                PinAnimationConstants.ERROR_SHAKE_FIRST_FULL_MS
            PinAnimationConstants.ERROR_SHAKE_FULL_OFFSET at
                PinAnimationConstants.ERROR_SHAKE_SECOND_FULL_MS
            PinAnimationConstants.ERROR_SHAKE_HALF_OFFSET at
                PinAnimationConstants.ERROR_SHAKE_FIRST_HALF_MS
            PinAnimationConstants.ERROR_SHAKE_HALF_OFFSET at
                PinAnimationConstants.ERROR_SHAKE_SECOND_HALF_MS
            0f at PinAnimationConstants.ERROR_SHAKE_DURATION_MS
        }, label = ""
    )

    val dotsScale by animateFloatAsState(
        targetValue = if (state.screenState == PinCodeScreenState.Success) {
            PinAnimationConstants.SUCCESS_SCALE_PEAK
        } else {
            PinAnimationConstants.SUCCESS_SCALE_START
        },
        animationSpec = keyframes {
            durationMillis = PinAnimationConstants.SUCCESS_PULSE_DURATION_MS
            PinAnimationConstants.SUCCESS_SCALE_START at 0
            PinAnimationConstants.SUCCESS_SCALE_PEAK at PinAnimationConstants.SUCCESS_PULSE_PEAK_MS
            PinAnimationConstants.SUCCESS_SCALE_END at
                PinAnimationConstants.SUCCESS_PULSE_DURATION_MS
        },
        label = ""
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
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                        .graphicsLayer {
                            scaleX = dotsScale
                            scaleY = dotsScale
                        }) {
                    repeat(6) { index ->
                        val filled = index < state.value.length
                        val baseColor = if (filled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant

                        val targetColor = when {
                            state.screenState == PinCodeScreenState.Success -> Color(0xFF2E7D32)
                            state.isError -> MaterialTheme.colorScheme.error
                            else -> baseColor
                        }
                        val color by animateColorAsState(
                            targetValue = targetColor,
                            animationSpec = tween(
                                durationMillis = PinAnimationConstants.COLOR_TRANSITION_MS
                            ),
                            label = ""
                        )

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

                if (state.canUseBiometric &&
                    state.isBiometricEnabled &&
                    state.scenario == PinScenario.UNLOCK
                ) {
                    Spacer(Modifier.height(12.dp))
                    IconButton(onClick = { viewModel.onBiometricRequested() }) {
                        Icon(
                            Icons.Default.Fingerprint,
                            contentDescription = stringResource(
                                R.string.biometric_button_content_description
                            )
                        )
                    }
                }
            }
        }
    }
}

fun showBiometricPrompt(
    activity: FragmentActivity, onSuccess: () -> Unit, onError: () -> Unit
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
        }
    }

    val prompt = BiometricPrompt(activity, executor, callback)
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(activity.getString(R.string.biometric_prompt_title))
        .setSubtitle(activity.getString(R.string.biometric_prompt_subtitle))
        .setNegativeButtonText(activity.getString(R.string.cancel))
        .setAllowedAuthenticators(BIOMETRIC_STRONG)
        .build()

    prompt.authenticate(promptInfo)
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity? {
    return when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.findFragmentActivity()
        else -> null
    }
}
