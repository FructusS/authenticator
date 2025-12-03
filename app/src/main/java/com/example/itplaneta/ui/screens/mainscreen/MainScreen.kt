package com.example.itplaneta.ui.screens.mainscreen

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.ui.components.AppTopBar
import com.example.itplaneta.ui.components.topBarConfig
import com.example.itplaneta.ui.screens.mainscreen.component.AccountRow
import com.example.itplaneta.ui.screens.mainscreen.component.DeleteAccountDialog
import com.example.itplaneta.ui.screens.mainscreen.component.FabsAction
import com.example.itplaneta.ui.screens.mainscreen.component.MainFabs
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onNavigateToAccount: (Int?) -> Unit,
    canNavigateBack: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is MainUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(context.getString(it.resId))
                }
            }
        }
    }

    Scaffold(topBar = {
        AppTopBar(
            config = topBarConfig {
                settingsAction(onNavigateToSettings)
            })
    }, floatingActionButton = {

        MainFabs(
            expanded = uiState.isFabExpanded, onToggle = viewModel::onFabToggle, actions = listOf(
                FabsAction(icon = Icons.Default.QrCode2) { onNavigateToQrScanner() },
                FabsAction(icon = Icons.Default.Edit) { onNavigateToAccount(null) })
        )

    }, modifier = Modifier.background(MaterialTheme.colorScheme.background)) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(uiState.accounts, key = { it.id }) { account ->
                val code = uiState.codes[account.id]
                AccountRow(
                    account = account,
                    code = code,
                    timerProgress = uiState.timerProgresses[account.id],
                    timerValue = uiState.timerValues[account.id],
                    onDelete = { viewModel.onRequestDelete(account) },
                    onCopy = {

                        scope.launch {
                            clipboardManager.setClipEntry(
                                ClipEntry(
                                    ClipData.newPlainText(
                                        code.orEmpty(), code.orEmpty()
                                    )
                                )
                            )
                        }
                        viewModel.onCodeCopied()
                    },
                    onEdit = { onNavigateToAccount(account.id) },
                    onHotpIncrement = { viewModel.incrementHotpCounter(account) })
            }
        }

        uiState.deleteDialogAccount?.let { _ ->
            DeleteAccountDialog(
                onConfirm = { viewModel.onConfirmDelete() },
                onDismiss = { viewModel.onDismissDeleteDialog() })
        }
    }
}