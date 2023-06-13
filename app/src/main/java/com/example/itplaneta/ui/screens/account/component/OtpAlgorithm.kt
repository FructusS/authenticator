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
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.ui.screens.account.AccountViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OtpAlgorithm(viewModel: AccountViewModel, onAccountValueChange: (RawAccount) -> Unit) {
    val state = viewModel.state.collectAsState()
    val account = state.value.account
    var expanded by remember { mutableStateOf(false) }
    val otpAlgorithmLists = listOf(
        OtpAlgorithm.Sha1, OtpAlgorithm.Sha256, OtpAlgorithm.Sha512)

    Row(modifier = Modifier.wrapContentWidth()) {
        ExposedDropdownMenuBox(

            expanded = expanded, onExpandedChange = {
                expanded = !expanded
            }) {
            OutlinedTextField(readOnly = true,
                value = account.algorithm.name,
                onValueChange = { },
                label = {
                    Text(
                        text = stringResource(id = R.string.otp_algorithm),
                        color = MaterialTheme.colors.secondaryVariant
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.secondaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
                expanded = false
            }) {
                otpAlgorithmLists.forEach { otpDigest ->
                    DropdownMenuItem(onClick = {
                        onAccountValueChange(account.copy(algorithm = otpDigest))
                        expanded = false
                    }) {
                        Text(text = otpDigest.name.uppercase())
                    }
                }
            }
        }
    }
}
