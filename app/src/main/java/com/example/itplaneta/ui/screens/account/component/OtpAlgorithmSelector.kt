package com.example.itplaneta.ui.screens.account.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.ui.components.DropdownSelector

@Composable
fun OtpAlgorithmSelector(
    algorithm: OtpAlgorithm,
    onSelectionChange: (OtpAlgorithm) -> Unit,
    modifier: Modifier = Modifier
) {
    val algorithmOptions = listOf(
        OtpAlgorithm.Sha1, OtpAlgorithm.Sha256, OtpAlgorithm.Sha512
    )
    val algorithmLabels = algorithmOptions.associateWith { it.name }

    DropdownSelector(
        selectedValue = algorithm,
        options = algorithmOptions,
        labelRes = R.string.otp_algorithm,
        displayLabels = algorithmLabels,
        onSelectionChange = onSelectionChange,
        modifier = modifier
    )
}