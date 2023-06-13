package com.example.itplaneta.ui.screens.account.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R
import com.example.itplaneta.domain.RawAccount
import com.example.itplaneta.ui.screens.account.AccountViewModel
import com.example.itplaneta.ui.screens.account.ErrorType


@Composable
fun AccountTextFields(viewModel: AccountViewModel, onAccountValueChange: (RawAccount) -> Unit) {
    var shownSecret by rememberSaveable { mutableStateOf(false) }
    val state = viewModel.state.collectAsState()
    val account = state.value.account
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = account.issuer.toString(),
        onValueChange = { onAccountValueChange(account.copy(issuer = it)) },
        maxLines = 1,
        singleLine = true,
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_issuer),
                contentDescription = stringResource(id = R.string.issuer),
                tint = MaterialTheme.colors.onSecondary
            )
        },
        label = {
            Text(
                stringResource(id = R.string.issuer_account),
                color = MaterialTheme.colors.secondaryVariant
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.secondaryVariant,
            unfocusedBorderColor = MaterialTheme.colors.secondaryVariant
        )
    )

    OutlinedTextField(
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        value = account.label,
        onValueChange = {
            onAccountValueChange(account.copy(label = it))
        },
        maxLines = 1,
        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_label),
                contentDescription = stringResource(id = R.string.account),
                tint = MaterialTheme.colors.onSecondary
            )
        },
        label = {
            Text(
                stringResource(id = R.string.label_account),
                color = MaterialTheme.colors.secondaryVariant
            )
        },
        isError = state.value.errorType == ErrorType.LabelError,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.secondaryVariant,
            unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
        )
    )
    if (state.value.errorType == ErrorType.LabelError) {
        Text(
            text = stringResource(id = state.value.errorText),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        value = account.secret,
        maxLines = 1,
        trailingIcon = {
            IconButton(onClick = { shownSecret = !shownSecret }) {
                Icon(
                    painter = if (shownSecret) painterResource(id = R.drawable.ic_visibility) else painterResource(
                        id = R.drawable.ic_visibility_off
                    ), tint = MaterialTheme.colors.onSecondary, contentDescription = null
                )
            }
        },
        visualTransformation = if (shownSecret) VisualTransformation.None else PasswordVisualTransformation(),
        onValueChange = {
            onAccountValueChange(account.copy(secret = it))
        },

        leadingIcon = {
            Icon(
                painterResource(id = R.drawable.ic_secret),
                contentDescription = stringResource(id = R.string.secret),
                tint = MaterialTheme.colors.onSecondary
            )
        },
        isError = state.value.errorType == ErrorType.SecretError,

        label = {
            Text(
                stringResource(id = R.string.secret_key), color = MaterialTheme.colors.secondaryVariant
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.secondaryVariant,
            unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
        )
    )
    if (state.value.errorType == ErrorType.SecretError) {
        Text(
            text = stringResource(id = state.value.errorText),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp)
        )
    }

}