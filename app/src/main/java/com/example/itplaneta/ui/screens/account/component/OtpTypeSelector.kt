package com.example.itplaneta.ui.screens.account.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.domain.AccountInputDto
import com.example.itplaneta.ui.components.DropdownSelector


@Composable
fun OtpType.getLabel(): String = stringResource(
    id = when (this) {
        OtpType.Totp -> R.string.otp_type_by_time
        OtpType.Hotp -> R.string.otp_type_by_counter
    }
)

@Composable
fun OtpTypeSelector(
    account: AccountInputDto, onSelectionChange: (OtpType) -> Unit, modifier: Modifier = Modifier
) {
    val otpTypeOptions = listOf(OtpType.Totp, OtpType.Hotp)
    val otpTypeLabels = otpTypeOptions.associateWith { otpType ->
        stringResource(
            id = when (otpType) {
                OtpType.Totp -> R.string.otp_type_by_time
                OtpType.Hotp -> R.string.otp_type_by_counter
            }
        )
    }

    DropdownSelector(
        selectedValue = account.tokenType,
        options = otpTypeOptions,
        labelRes = R.string.otp_type,
        displayLabels = otpTypeLabels,
        onSelectionChange = onSelectionChange,
        modifier = modifier
    )
}
