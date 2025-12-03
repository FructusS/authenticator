package com.example.itplaneta.ui.screens.account.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.itplaneta.R
import com.example.itplaneta.domain.validation.AccountFieldError
import com.example.itplaneta.ui.components.BaseTextField

@Composable
fun OtpSecret(
    modifier: Modifier = Modifier,
    secret: String,
    onValueChange: (String) -> Unit,
    error: AccountFieldError? = null
) {

    var isSecretVisible by rememberSaveable { mutableStateOf(false) }

    BaseTextField(
        value = secret,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = stringResource(id = R.string.secret_key),
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_secret),
                contentDescription = stringResource(id = R.string.secret),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = { isSecretVisible = !isSecretVisible }) {
                Icon(
                    painter = painterResource(
                        id = if (isSecretVisible) {
                            R.drawable.ic_visibility
                        } else {
                            R.drawable.ic_visibility_off
                        }
                    ), contentDescription = if (isSecretVisible) {
                        "hide_secret"
                    } else {
                        "show_secret"
                    }
                )
            }
        },
        visualTransformation = if (isSecretVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        error = error
    )
}