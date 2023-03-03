package com.example.itplaneta.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType
import com.example.itplaneta.ui.viewmodels.AccountViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AccountScreen(viewModel: AccountViewModel, navController: NavHostController) {

    var label by rememberSaveable { mutableStateOf("") }
    var secret by rememberSaveable { mutableStateOf("") }
    var issuer by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(

                value = issuer, onValueChange = {
                    issuer = it
                }, label = { Text("Платформа аккаунта") })
            OutlinedTextField(

                value = label, onValueChange = {
                    label = it
                }, label = { Text("Название аккаунта") })
            OutlinedTextField(

                value = secret, onValueChange = {
                    secret = it
                }, label = { Text("Код") })
            OutlinedButton(onClick = {
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
                navController.popBackStack(route = "main", inclusive = false, saveState = false)
            }) {
                Text("Сохранить", fontSize = 25.sp)
            }
        }
    }
}


