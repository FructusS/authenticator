package com.example.itplaneta.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.base.UiEvent
import com.example.itplaneta.ui.screens.account.component.AccountInputForm
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel(),
    canNavigateBack: Boolean = true,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
                else -> {}
            }
        }
    }

    val titleRes = if (viewModel.isEditMode) {
        R.string.edit
    } else {
        R.string.add
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        AuthenticatorTopAppBar(
            title = { Text(stringResource(id = titleRes)) },
            canNavigateBack = canNavigateBack,
            navigateUp = onNavigateUp
        )
    }, floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text(text = stringResource(id = R.string.save)) },
            onClick = {
                coroutineScope.launch {
                    viewModel.saveAccount()
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_save),
                    contentDescription = stringResource(id = R.string.save)

                )
            })
    }, content = { paddingValues ->


        when (val screenState = uiState.screenState) {
            is AccountScreenState.Error -> {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.clearError()
                    },
                    title = { Text("error") },
                    text = { Text(screenState.message) },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearError()
                            }) {
                            Text("ОК")
                        }
                    })
            }

            AccountScreenState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AccountInputForm(
                        viewModel = viewModel
                    )
                }
            }

            AccountScreenState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is AccountScreenState.Success -> {
                // toast success save
                navigateBack()

            }
        }
    })
}