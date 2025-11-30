package com.example.itplaneta.ui.screens.mainscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
    navigateToQrScanner: () -> Unit,
    navigateToAddAccount: () -> Unit,
    navigateToEditAccount: (Int?) -> Unit,
    canNavigateBack: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    var isAllFABsVisible by remember { mutableStateOf(false) }

    val iconAddRotation by animateFloatAsState(if (isAllFABsVisible) 45f else 0f)

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var openDialog by remember { mutableStateOf(false) }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }

    val accountsState by viewModel.accounts.collectAsState(initial = emptyList())
    val codesState by viewModel.codes.collectAsState(initial = emptyMap())
    val timerProgresses by viewModel.timerProgresses.collectAsState(initial = emptyMap())
    val timerValues by viewModel.timerValues.collectAsState(initial = emptyMap())

    Scaffold(topBar = {
        AuthenticatorTopAppBar(
            title = { /* можно показать логотип/пусто */ },
            canNavigateBack = canNavigateBack,
            actions = {
                IconButton(onClick = {
                    navigateToSettings()
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.settings),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            })
    }, floatingActionButton = {
        MainFabs(
            expanded = isAllFABsVisible,
            onToggle = { !isAllFABsVisible },
            actions = listOf({ navigateToQrScanner() } to R.drawable.ic_qr_code_scanner,
                { navigateToAddAccount() } to R.drawable.ic_edit),
            rotation = iconAddRotation)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(accountsState, key = { it.id }) { account ->
                val code = codesState[account.id]
                AccountRow(
                    account = account,
                    code = code,
                    timerProgress = timerProgresses[account.id],
                    timerValue = timerValues[account.id],
                    onDelete = {
                        selectedAccount = account
                        openDialog = true
                    },
                    onCopy = {
                        val text = code ?: ""
                        clipboardManager.setText(AnnotatedString(text))
                        // Toast можно показать через Context/rememberToast helper
                    },
                    onEdit = { navigateToEditAccount(account.id) },
                    onHotpIncrement = {
                        coroutineScope.launch {
                            viewModel.incrementHotpCounter(account)
                        }
                    })
            }
        }

        if (openDialog && selectedAccount != null) {
            AlertDialog(
                onDismissRequest = { openDialog = false },
                title = { Text(text = stringResource(id = R.string.action_confirmation)) },
                text = { Text(text = stringResource(id = R.string.you_want_delete_selected_item)) },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        openDialog = false
                        selectedAccount?.let { acc ->
                            coroutineScope.launch { viewModel.deleteAccount(acc) }
                        }
                    }) {
                        Text(text = stringResource(id = R.string.delete))
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { openDialog = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}


@Composable
private fun MainFabs(
    expanded: Boolean,
    onToggle: () -> Unit,
    actions: List<Pair<() -> Unit, Int>>, // Pair<action, iconRes>
    rotation: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = slideInVertically() + expandVertically(expandFrom = Alignment.Bottom),
            exit = slideOutVertically() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                actions.forEach { (action, iconRes) ->
                    SmallFloatingActionButton(
                        onClick = action, containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(painter = painterResource(id = iconRes), contentDescription = null)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FloatingActionButton(
            onClick = onToggle, containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun AccountRow(
    account: Account,
    code: String?,
    timerProgress: Float?,
    timerValue: Long?,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onHotpIncrement: () -> Unit,
) {
    Column {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
                ) {
                    if (!account.issuer.isNullOrBlank()) {
                        val faded = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        Text(
                            text = account.issuer,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = faded
                        )
                    }

                    Text(
                        text = account.label,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Убрана бессмысленная AnimatedVisibility(true)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // TOTP индикатор
                        if (account.tokenType == OtpType.Totp) {
                            Box(
                                modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center
                            ) {
                                timerProgress?.let { prog ->
                                    val animProgress by animateFloatAsState(
                                        targetValue = prog,
                                        animationSpec = tween(durationMillis = 500)
                                    )
                                    // Цвета — примеры. Можно заменить на theme.
                                    val color = when {
                                        timerValue == null -> MaterialTheme.colorScheme.primary
                                        (timerValue) > (account.period * 0.66).toLong() -> MaterialTheme.colorScheme.secondary
                                        (timerValue) > (account.period * 0.33).toLong() -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                    androidx.compose.material3.CircularProgressIndicator(
                                        progress = animProgress,
                                        color = color,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                timerValue?.let {
                                    Text(it.toString(), color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        if (account.tokenType == OtpType.Hotp) {
                            OutlinedButton(onClick = onHotpIncrement) {
                                Text(
                                    account.counter.toString(),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        AnimatedContent(targetState = code, transitionSpec = {
                            (slideInVertically(
                                initialOffsetY = { it }, animationSpec = tween(500)
                            ) + fadeIn()).togetherWith(
                                slideOutVertically(
                                    targetOffsetY = { -it }, animationSpec = tween(500)
                                ) + fadeOut()
                            )
                        }) { animatedCode ->
                            Text(
                                text = animatedCode ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Row(modifier = Modifier.wrapContentWidth()) {
                            IconButton(onClick = onCopy) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_content_copy),
                                    contentDescription = "copy",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = onDelete) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "delete",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = onEdit) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = "edit",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    }
}