package com.example.itplaneta.ui.screens.account.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.domain.RawAccount
import com.example.itplaneta.ui.screens.account.AccountViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OtpSettings(viewModel: AccountViewModel, onAccountValueChange: (RawAccount) -> Unit) {
    val state = viewModel.state.collectAsState()
    val account = state.value.account
    AnimatedContent(targetState = account.tokenType, transitionSpec = {
        slideInVertically { height -> height } + fadeIn() with slideOutVertically { height -> -height } + fadeOut()
    }) {
        when (it) {
            OtpType.Hotp -> {
                OtpCounter(viewModel, onAccountValueChange)
            }

            OtpType.Totp -> {
                    OtpPeriod(viewModel, onAccountValueChange)
            }
        }
    }
}