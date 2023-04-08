package com.example.itplaneta.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.Screens
import com.example.itplaneta.ui.viewmodels.AccountViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AccountScreen(
    navController: NavController,
    accountId: Int? = null,
    viewModel: AccountViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = null) {
        if (accountId != null) {
            viewModel.updateAccountField(accountId)
        }
    }

    Scaffold(

        floatingActionButton = {
            ExtendedFloatingActionButton(
                backgroundColor = colorResource(id = R.color.bg_floatingbutton),
                text = { Text(text = stringResource(id = R.string.save)) },
                onClick = {
                    if (accountId == null) {
                        if (viewModel.addAccount()) {
                            navController.navigate(Screens.Main.route)
                        }
                    } else {
                        viewModel.updateAccount(accountId)
                        navController.navigate(Screens.Main.route)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = stringResource(id = R.string.save)
                    )
                })
        },

        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(backgroundColor = colorResource(id = R.color.bg_toolbar)) {
                IconButton(onClick = { navController.navigate(Screens.Main.route) }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
                Spacer(Modifier.weight(1f, true))

                IconButton(onClick = { navController.navigate(Screens.QrScanner.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_code_scanner),
                        contentDescription = "qrscanner"
                    )
                }
            }
        }
    ) {
        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp, 5.dp),
            verticalArrangement = Arrangement.Top,


            ) {
            var shownSecret by rememberSaveable { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.issuer,
                onValueChange = {
                    viewModel.updateIssuer(it)
                },
                maxLines = 1,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_issuer),
                        contentDescription = stringResource(id = R.string.issuer),
                        tint = Color.Black
                    )
                },

                label = { Text(stringResource(id = R.string.issuer_account), color = Color.Black) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            OutlinedTextField(
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.label,
                onValueChange = {
                    viewModel.updateLabel(it)
                },
                maxLines = 1,
                isError = viewModel.errorLabel,
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_label),
                        contentDescription = stringResource(id = R.string.account),
                        tint = Color.Black
                    )
                },
                label = { Text(stringResource(id = R.string.label_account), color = Color.Black) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.border),
                    unfocusedBorderColor = colorResource(id = R.color.border),
                )
            )
            if (viewModel.errorLabel) {
                Text(
                    text = viewModel.errorLabelText,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            if (accountId == null) {
                OutlinedTextField(
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.secret,
                    maxLines = 1,
                    trailingIcon = {
                        IconButton(onClick = { shownSecret = !shownSecret }) {
                            Icon(
                                painter = if (shownSecret) painterResource(id = R.drawable.ic_visibility) else painterResource(
                                    id = R.drawable.ic_visibility_off
                                ),
                                tint = Color.Black,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (shownSecret) VisualTransformation.None else PasswordVisualTransformation(),
                    onValueChange = {
                        viewModel.updateSecret(it)
                    },
                    isError = viewModel.errorSecret,
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_secret),
                            contentDescription = stringResource(id = R.string.secret),
                            tint = Color.Black
                        )
                    },
                    label = { Text(stringResource(id = R.string.code), color = Color.Black) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.border),
                        unfocusedBorderColor = colorResource(id = R.color.border),
                    )
                )
                if (viewModel.errorSecret) {
                    Text(
                        text = viewModel.errorSecretText,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                //  LengthCodeRadioButton()
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                OtpType(viewModel = viewModel)
                OtpAlgorithm(viewModel = viewModel)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                CountDigits(viewModel = viewModel)
                OtpAlgorithm(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CountDigits(viewModel: AccountViewModel) {
    Column() {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.wrapContentWidth(),
            value = viewModel.digits,
            onValueChange = {
                viewModel.updateDigits(it)
            },

            maxLines = 1,
            isError = viewModel.errorLabel,
            label = {
                Text(
                    stringResource(id = R.string.digits),
                    color = if (viewModel.errorDigits) Color.Red else Color.Black
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.border),
                unfocusedBorderColor = colorResource(id = R.color.border),
            )
        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OtpType(viewModel: AccountViewModel) {

    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.wrapContentWidth()) {
        ExposedDropdownMenuBox(

            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = viewModel.otpType.name.uppercase(),
                onValueChange = { },
                label = {
                    Text(
                        text = stringResource(id = R.string.otpalgorithm),
                        color = Color.Black
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.border),
                    unfocusedBorderColor = colorResource(id = R.color.border),
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                viewModel.otpTypeList.forEach { otpType ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.updateOtpType(otpType)
                            expanded = false
                        }
                    ) {
                        Text(text = otpType.name.uppercase())
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OtpAlgorithm(viewModel: AccountViewModel) {

    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.wrapContentWidth()) {
        ExposedDropdownMenuBox(

            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = viewModel.otpAlgorithm.name.uppercase(),
                onValueChange = { },
                label = {
                    Text(
                        text = stringResource(id = R.string.otptype),
                        color = Color.Black
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                viewModel.otpAlgorithmLists.forEach { otpDigest ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.updateOtpAlgorithm(otpDigest)
                            expanded = false
                        }
                    ) {
                        Text(text = otpDigest.name.uppercase())
                    }
                }
            }
        }
    }
}