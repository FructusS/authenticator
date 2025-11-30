package com.example.itplaneta.ui.screens.account.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.itplaneta.R
import com.example.itplaneta.domain.validation.AccountFieldError
import com.example.itplaneta.ui.base.BaseTextField

@Composable
fun OtpLabel(
    label: String,
    onValueChange: (String) -> Unit,
    error: AccountFieldError? = null,
    modifier: Modifier = Modifier
) {
    BaseTextField(
        value = label,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = stringResource(id = R.string.label_account),
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_label),
                contentDescription = stringResource(id = R.string.account),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        error = error
    )
}