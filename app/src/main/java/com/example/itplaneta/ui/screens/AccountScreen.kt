package com.example.itplaneta.ui.screens

import android.annotation.SuppressLint
import android.widget.RadioGroup
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.itplaneta.R
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType
import com.example.itplaneta.ui.viewmodels.AccountViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AccountScreen(viewModel: AccountViewModel, navController: NavHostController) {

        var label by rememberSaveable { mutableStateOf("") }
        var secret by rememberSaveable { mutableStateOf("") }
        var issuer by rememberSaveable { mutableStateOf("") }
        var selected by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(backgroundColor = colorResource(id = R.color.bg_toolbar)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.back))
                }
                Spacer(Modifier.weight(1f, true))

                TextButton(
                    onClick = {
                            viewModel.addAccount(
                                com.example.itplaneta.data.database.Account(
                                    0,
                                    label = label,
                                    issuer = issuer,
                                    tokenType = OtpType.Totp,
                                    algorithm = OtpDigest.Sha1,
                                    secret = secret,
                                    digits = 6,
                                    counter = 0,
                                    period = 30
                                )
                            )
                            navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {

                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 5.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = issuer,
                onValueChange = {
                    issuer = it
                },
                label = { Text("Платформа аккаунта") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = label,
                onValueChange = {
                    label = it
                },
                label = { Text("Название аккаунта") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = secret,
                onValueChange = {
                    secret = it
                },
                label = { Text("Код") }
            )

            val otpList =  stringArrayResource(R.array.test)

            otpList.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selected == item,
                        onClick = {

                        },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta
                        )
                    )
                    Text(text = item, modifier = Modifier.padding(start = 8.dp))
                }
            }


        }
    }
}


