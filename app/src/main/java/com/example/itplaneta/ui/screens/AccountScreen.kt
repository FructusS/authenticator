package com.example.itplaneta.ui.screens

import android.annotation.SuppressLint
import android.widget.RadioGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.itplaneta.R
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType
import com.example.itplaneta.ui.viewmodels.AccountViewModel
import com.example.itplaneta.data.database.Account

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AccountScreen(viewModel: AccountViewModel, navController: NavHostController) {
    Scaffold(
        backgroundColor = colorResource(id = R.color.bg_main),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.save)) },
                backgroundColor = colorResource(id = R.color.bg_account),
                onClick = {
                    if (viewModel.addAccount()) {
                        navController.popBackStack()
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = ""
                    )
                })
        },

        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(backgroundColor = colorResource(id = R.color.bg_toolbar)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
                Spacer(Modifier.weight(1f, true))

                IconButton(onClick = { navController.navigate("qrscanner") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_code_scanner),
                        contentDescription = ""
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
                        contentDescription = "Платформа",
                        tint = Color.Black
                    )
                },

                label = { Text("Платформа аккаунта", color = Color.Black) },
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
                        contentDescription = "аккаунт",
                        tint = Color.Black
                    )
                },
                label = { Text("Название аккаунта", color = Color.Black) },
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
                        contentDescription = "Плаформа",
                        tint = Color.Black
                    )
                },
                label = { Text("Код", color = Color.Black) },
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
        }
    }
}


