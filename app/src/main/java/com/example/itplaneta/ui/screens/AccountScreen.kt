package com.example.itplaneta.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.Screens
import com.example.itplaneta.ui.viewmodels.AccountViewModel
import com.example.itplaneta.otp.OtpType

@OptIn(ExperimentalAnimationApi::class)
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
                backgroundColor = MaterialTheme.colors.primary,
                text = { Text(text = stringResource(id = R.string.save), color = Color.Black) },
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
                        contentDescription = stringResource(id = R.string.save),
                        tint = Color.Black

                    )
                })
        },

        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant) {
                IconButton(onClick = { navController.navigate(Screens.Main.route) }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = MaterialTheme.colors.secondary

                    )
                }
                Spacer(Modifier.weight(1f, true))

                IconButton(onClick = { navController.navigate(Screens.QrScanner.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_code_scanner),
                        contentDescription = "qrscanner",
                        tint = MaterialTheme.colors.secondary
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

                label = { Text(stringResource(id = R.string.issuer_account), color = MaterialTheme.colors.secondary) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.secondary,
                    unfocusedBorderColor = MaterialTheme.colors.secondary
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
                label = { Text(stringResource(id = R.string.label_account),color = MaterialTheme.colors.secondary) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.secondary,
                    unfocusedBorderColor = MaterialTheme.colors.secondary,
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
                                tint = MaterialTheme.colors.secondary,
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
                    label = { Text(stringResource(id = R.string.code), color = MaterialTheme.colors.secondary) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.secondary,
                        unfocusedBorderColor = MaterialTheme.colors.secondary,
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                item {
                    OtpType(viewModel = viewModel)
                }
                item {
                    OtpAlgorithm(viewModel = viewModel)
                }
                item {
                    CountDigits(viewModel = viewModel)

                }
                item {

                        AnimatedContent(targetState = viewModel.otpType,
                            transitionSpec = {
                                slideInVertically { height -> height } + fadeIn() with
                                        slideOutVertically { height -> -height } + fadeOut()
                        }) {
                            when(it){
                                OtpType.Hotp -> {
                                    OtpCounter(viewModel = viewModel)
                                }
                                OtpType.Totp ->{
                                    OtpPeriod(viewModel = viewModel)
                                }
                            }
                        }


                }
            }
        }
    }
}

@Composable
fun OtpPeriod(viewModel: AccountViewModel) {
    Column {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.wrapContentWidth(),
            value = viewModel.period,
            onValueChange = {
                viewModel.updatePeriod(it)
            },

            maxLines = 1,
            isError = viewModel.errorPeriod,
            label = {
                Text(
                    stringResource(id = R.string.period_otp_code),
                    color = MaterialTheme.colors.secondary
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.secondary,
                unfocusedBorderColor = MaterialTheme.colors.secondary,
            )
        )
    }
}

@Composable
fun OtpCounter(viewModel: AccountViewModel) {
    Column {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.wrapContentWidth(),
            value = viewModel.counter,
            onValueChange = {
                viewModel.updateCounter(it)
            },

            maxLines = 1,
            isError = viewModel.errorCounter,
            label = {
                Text(
                    stringResource(id = R.string.counter_otp_code),
                    color = MaterialTheme.colors.secondary
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.secondary,
                unfocusedBorderColor = MaterialTheme.colors.secondary,
            )
        )
    }
}

@Composable
fun CountDigits(viewModel: AccountViewModel) {
    Column {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.wrapContentWidth(),
            value = viewModel.digits,
            onValueChange = {
                viewModel.updateDigits(it)
            },

            maxLines = 1,
            isError = viewModel.errorDigits,
            label = {
                Text(
                    stringResource(id = R.string.length_otp_code),
                    color  = MaterialTheme.colors.secondary
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor =  MaterialTheme.colors.secondary,
                unfocusedBorderColor =   MaterialTheme.colors.secondary,
            )
        )
        if (viewModel.errorDigits) {
            Text(
                text = stringResource(id = R.string.length_code_from_6_to_10),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }
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
                value = when(viewModel.otpType){
                    OtpType.Totp -> stringResource(id = R.string.otp_type_by_time)
                    OtpType.Hotp -> stringResource(id = R.string.otp_type_by_counter)
                },
                onValueChange = { },
                label = {
                    Text(
                        text = stringResource(id = R.string.otp_algorithm),
                        color = MaterialTheme.colors.secondary
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.secondary,
                    unfocusedBorderColor = MaterialTheme.colors.secondary,
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
                        if (otpType == OtpType.Totp){
                            Text(text = stringResource(id = R.string.otp_type_by_time),color = MaterialTheme.colors.secondary)
                        }
                        if (otpType == OtpType.Hotp){
                            Text(text = stringResource(id = R.string.otp_type_by_counter), color = MaterialTheme.colors.secondary)
                        }
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
                        text = stringResource(id = R.string.otp_type),
                        color = MaterialTheme.colors.secondary
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.secondary,
                    unfocusedBorderColor = MaterialTheme.colors.secondary,
                )
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