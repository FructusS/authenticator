package com.example.itplaneta.ui.screens.account

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.AddAccountDestination
import com.example.itplaneta.ui.screens.account.component.AccountInputForm
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddAccountScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel(),
    canNavigateBack: Boolean = true,
) {
    val state = viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AuthenticatorTopAppBar(
                title = stringResource(id = AddAccountDestination.titleScreen),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text(text = stringResource(id = R.string.save)) },
                onClick = {
                    coroutineScope.launch {

                        viewModel.addAccount(

                            navigateBack

                        )

                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = stringResource(id = R.string.save)

                    )
                })
        },
    ) {

        AccountInputForm(
            viewModel = viewModel, onAccountValueChange = viewModel::updateUiState,
        )

    }
}










