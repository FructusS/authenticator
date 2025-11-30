package com.example.itplaneta.ui.screens.account.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.itplaneta.R
import com.example.itplaneta.domain.validation.AccountFieldError
import com.example.itplaneta.ui.base.BaseTextField

@Composable
fun OtpIssuer(
    issuer: String?,
    onValueChange: (String) -> Unit,
    error: AccountFieldError? = null,
    modifier: Modifier = Modifier
) {
    BaseTextField(
        value = issuer.orEmpty(),
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = stringResource(id = R.string.issuer_account),
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_issuer),
                contentDescription = stringResource(id = R.string.issuer)
            )
        },
        error = error
    )
}

