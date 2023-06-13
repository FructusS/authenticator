package com.example.itplaneta.ui.screens.account.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.itplaneta.R
import com.example.itplaneta.domain.RawAccount
import com.example.itplaneta.ui.screens.account.AccountViewModel
import com.example.itplaneta.core.otp.models.OtpType


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OtpType(viewModel: AccountViewModel, onAccountValueChange: (RawAccount) -> Unit) {
val state = viewModel.state.collectAsState()
    val account = state.value.account
    var expanded by remember { mutableStateOf(false) }
    val otpTypeList = listOf(OtpType.Totp, OtpType.Hotp)
    Row(modifier = Modifier.wrapContentWidth()) {
        ExposedDropdownMenuBox(

            expanded = expanded, onExpandedChange = {
                expanded = !expanded
            }) {
            OutlinedTextField(readOnly = true, value = when (account.tokenType) {
                OtpType.Totp -> stringResource(id = R.string.otp_type_by_time)
                OtpType.Hotp -> stringResource(id = R.string.otp_type_by_counter)
            }, onValueChange = { }, label = {
                Text(
                    text = stringResource(id = R.string.otp_type),
                    color = MaterialTheme.colors.secondaryVariant
                )
            }, trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }, colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.secondaryVariant,
                unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
            )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
                expanded = false
            }) {
                otpTypeList.forEach { otpType ->
                    DropdownMenuItem(onClick = {
                        onAccountValueChange(account.copy(tokenType = otpType))
                        expanded = false
                    }) {
                        if (otpType == OtpType.Totp) {
                            Text(
                                text = stringResource(id = R.string.otp_type_by_time),
                                color = MaterialTheme.colors.secondaryVariant
                            )
                        }
                        if (otpType == OtpType.Hotp) {
                            Text(
                                text = stringResource(id = R.string.otp_type_by_counter),
                                color = MaterialTheme.colors.secondaryVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
