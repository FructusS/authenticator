package com.example.itplaneta.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.itplaneta.R
import com.example.itplaneta.otp.OtpType
import com.example.itplaneta.ui.navigation.Screens
import com.example.itplaneta.ui.viewmodels.AccountViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AccountScreen(
    navController: NavController,
    accountId : Int? = null,
    viewModel : AccountViewModel = hiltViewModel()
    ) {

    LaunchedEffect(key1 = null){
        if (accountId != null){
            viewModel.updateAccountField(accountId)
        }
    }
    
    Scaffold(

        floatingActionButton = {
            ExtendedFloatingActionButton(
                backgroundColor = colorResource(id = R.color.bg_floatingbutton),
                text = { Text(text = stringResource(id = R.string.save)) },
                onClick = {
                    if (accountId == null){
                        if (viewModel.addAccount()) {
                            navController.navigate(Screens.Main.route)
                        }
                    }

                    else{
                        viewModel.updateAccount(accountId)
                        navController.navigate(Screens.Main.route)
                    }
                }
                                                            ,
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

                IconButton(onClick = {navController.navigate(Screens.QrScanner.route)}) {
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
                .fillMaxWidth()
                .padding(5.dp, 5.dp),
            verticalArrangement = Arrangement.Center
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

            if (accountId == null){
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
            OtpTypeRadioButtons(viewModel)
            if (viewModel.getCurrentOtpType() == OtpType.Hotp){
                LengthCodeRadioButton()
            }
        }
    }
}

@Composable
fun LengthCodeRadioButton() {
    val radioOptions = listOf(6,8,10)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0] ) }
    Column {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                        }
                    )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) }

                )
                Text(text.toString(), modifier = Modifier.padding(start = 8.dp))

            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OtpTypeRadioButtons(viewModel: AccountViewModel) {

    var expanded by remember { mutableStateOf(false) }

    Column() {
        viewModel.otpTypeList.forEach { otpType ->
            Row() {


                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedOptionText,
                        onValueChange = { },
                        label = { Text("Categories") },
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
                        options.forEach { selectionOption ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedOptionText = selectionOption
                                    expanded = false
                                }
                            ) {
                                Text(text = selectionOption)
                            }
                        }
                    }
                }
                RadioButton(
                    selected = (otpType == viewModel.getCurrentOtpType()),
                    onClick = { viewModel.updateOtpType(otpType) }

                )
                Text(otpType.name.uppercase(), modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically))

            }

        }
    }
}