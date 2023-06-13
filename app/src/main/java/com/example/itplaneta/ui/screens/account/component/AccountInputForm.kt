package com.example.itplaneta.ui.screens.account.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itplaneta.domain.RawAccount
import com.example.itplaneta.ui.screens.account.AccountViewModel


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AccountInputForm(viewModel: AccountViewModel, onAccountValueChange: (RawAccount) -> Unit) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp, 5.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        AccountTextFields(viewModel, onAccountValueChange)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item {
                OtpType(viewModel, onAccountValueChange)
            }
            item {
                OtpAlgorithm(viewModel, onAccountValueChange)
            }
            item {
                OtpDigits(viewModel, onAccountValueChange)

            }
            item {
                OtpSettings(viewModel, onAccountValueChange)
            }

        }

    }


}