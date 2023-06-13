package com.example.itplaneta.ui.screens.account.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R
import com.example.itplaneta.domain.RawAccount
import com.example.itplaneta.ui.screens.account.AccountViewModel
import com.example.itplaneta.ui.screens.account.ErrorType


@Composable
fun OtpCounter(viewModel: AccountViewModel, onAccountValueChange: (RawAccount) -> Unit) {
    val state = viewModel.state.collectAsState()
    val account = state.value.account
    Column {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.wrapContentWidth(),
            value = account.counter,
            onValueChange = {
                onAccountValueChange(account.copy(counter = it))
            },
            isError = state.value.errorType == ErrorType.CounterError,
            maxLines = 1,
            label = {
                Text(
                    stringResource(id = R.string.counter_otp_code),
                    color = MaterialTheme.colors.secondaryVariant
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.secondaryVariant,
                unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
            )
        )
        if (state.value.errorType == ErrorType.CounterError){
                Text(
                    text = stringResource(id = state.value.errorText),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 16.dp)
                )
        }
    }
}