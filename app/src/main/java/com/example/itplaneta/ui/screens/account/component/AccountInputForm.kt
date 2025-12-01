package com.example.itplaneta.ui.screens.account.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.domain.validation.FieldType
import com.example.itplaneta.ui.screens.account.AccountViewModel

@Composable
fun AccountInputForm(
    viewModel: AccountViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top
    ) {

        val state by viewModel.uiState.collectAsState()
        val account = state.currentAccount
        val errors = state.errors

        OtpIssuer(modifier, account.issuer, onValueChange = { issuer ->
            viewModel.updateAccountInputDto(FieldType.ISSUER, issuer)
        }, error = errors[FieldType.ISSUER])
        OtpLabel(account.label, onValueChange = { label ->
            viewModel.updateAccountInputDto(FieldType.LABEL, label)
        }, error = errors[FieldType.LABEL])
        OtpSecret(modifier, account.secret, onValueChange = { secret ->
            viewModel.updateAccountInputDto(FieldType.SECRET, secret)
        }, error = errors[FieldType.SECRET])

        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                OtpTypeSelector(account, onSelectionChange = { otpType ->
                    viewModel.updateOtpTypeAccount(otpType)
                }, modifier.weight(1f))
                OtpAlgorithmSelector(account.algorithm, onSelectionChange = { algorithm ->
                    viewModel.updateAlgorithmAccount(algorithm)
                }, modifier.weight(1f))
            }
        }
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                OtpDigits(
                    account.digits, onValueChange = { digits ->
                        viewModel.updateAccountInputDto(FieldType.DIGITS, digits)
                    }, errors[FieldType.DIGITS], modifier = Modifier.weight(1f)
                )
                AnimatedContent(
                    modifier = modifier.weight(1f),
                    targetState = account.tokenType,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut())
                    }) { tokenType ->
                    when (tokenType) {
                        OtpType.Hotp -> {
                            OtpCounter(account.counter, onValueChange = { counter ->
                                viewModel.updateAccountInputDto(FieldType.COUNTER, counter)
                            }, errors[FieldType.COUNTER])
                        }

                        OtpType.Totp -> {
                            OtpPeriod(
                                account.period, onValueChange = { period ->
                                    viewModel.updateAccountInputDto(FieldType.PERIOD, period)
                                }, errors[FieldType.PERIOD]
                            )
                        }
                    }
                }
            }
        }
    }
}

