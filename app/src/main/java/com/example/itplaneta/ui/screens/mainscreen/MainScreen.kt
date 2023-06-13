package com.example.itplaneta.ui.screens.mainscreen

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.launch
import kotlin.math.ceil


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
    navigateToQrScanner: () -> Unit,
    navigateToAddAccount: () -> Unit,
    navigateToEditAccount: (Int) -> Unit,

    ) {
    val coroutineScope = rememberCoroutineScope()
    var isSettingButtonClick by remember {
        mutableStateOf(false)
    }
    var isAllFABsVisible by remember {
        mutableStateOf(false)
    }

    val iconSettingsRotation by animateFloatAsState(if (isSettingButtonClick) 45f else 0f)
    val iconAddRotation by animateFloatAsState(if (isAllFABsVisible) 45f else 0f)
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var openDialog = remember { mutableStateOf(false) }

    var selectedAccount = remember {
        mutableStateOf(Account(0,"","",OtpType.Totp,OtpAlgorithm.Sha1,"",6,0,30))
    }

    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = colors.primary) {
                IconButton(onClick = {
                    isSettingButtonClick = !isSettingButtonClick
                    navigateToSettings()
                }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.settings),
                        modifier = Modifier.rotate(iconSettingsRotation)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                AnimatedVisibility(
                    visible = isAllFABsVisible,
                    enter = slideInVertically() + expandVertically(expandFrom = Alignment.Bottom),
                    exit = slideOutVertically(targetOffsetY = { fullWidth -> fullWidth }) + shrinkVertically(),

                    ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SmallFloatingActionButton(
                            onClick = {
                                navigateToQrScanner()
                            }, containerColor = colors.secondary
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_qr_code_scanner),
                                contentDescription = "add"
                            )
                        }

                        SmallFloatingActionButton(

                            onClick = {
                                navigateToAddAccount()
                            }, containerColor = colors.secondary

                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "add"
                            )
                        }
                    }


                }

                FloatingActionButton(onClick = {
                    isAllFABsVisible = !isAllFABsVisible
                }

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "add",
                        modifier = Modifier.rotate(iconAddRotation)
                    )
                }

            }
        },

        ) { paddingValues ->
        val list = viewModel.accounts.collectAsState(initial = emptyList())
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(list.value) { account ->


                val code = viewModel.codes[account.id]
                Account(deleteClick = {
                    selectedAccount.value = account
                    openDialog.value = true
                }, copyClick = {
                    clipboardManager.setText(AnnotatedString(code.toString()))
                    Toast.makeText(
                        context, context.getText(R.string.copied), Toast.LENGTH_SHORT
                    ).show()
                }, issuer = if (account.issuer != "") { ->
                    Text(
                        overflow = TextOverflow.Ellipsis,
                        text = account.issuer.toString(),
                        maxLines = 1,
                        fontSize = 20.sp,
                        color = colors.primary
                    )
                } else null, label = {
                    Text(
                        text = account.label,
                        maxLines = 1,
                        fontSize = 25.sp,
                        color = colors.onSurface,
                        overflow = TextOverflow.Ellipsis
                    )
                },

                    indicator = {
                        if (OtpType.Totp == account.tokenType) {
                            Box(
                                modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center
                            ) {
                                val timerProgress = viewModel.timerProgresses[account.id]
                                val timerValue = viewModel.timerValues[account.id]
                                val period = account.period.times(0.33).also { ceil(it) }
                                if (timerProgress != null) {
                                    val animatedTimerProgress by animateFloatAsState(
                                        targetValue = timerProgress,
                                        animationSpec = tween(durationMillis = 500)
                                    )
                                    CircularProgressIndicator(
                                        progress = animatedTimerProgress,
                                        color = if (timerValue?.div(period)!! >= 2.0) {
                                            colorResource(
                                                id = R.color.green
                                            )
                                        } else if (timerValue.div(period) in 1.0..1.99) {
                                            colorResource(
                                                id = R.color.yellow
                                            )
                                        } else {
                                            colors.error
                                        }
                                    )
                                }
                                if (timerValue != null) {
                                    Text(timerValue.toString(), color = colors.onSurface)
                                }
                            }
                        }
                        if (OtpType.Hotp == account.tokenType) {
                            OutlinedButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.incrementHotpCounter(
                                        account
                                    )
                                }
                            }) {
                                Text(account.counter.toString(), color = colors.onSurface)
                            }
                        }
                    }, code = {
                        AnimatedContent(targetState = code, transitionSpec = {
                            slideIntoContainer(
                                towards = AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(500)
                            ) + fadeIn() with slideOutOfContainer(
                                towards = AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(500)
                            ) + fadeOut()
                        }) { animatedCode ->
                            if (animatedCode != null) {

                                Text(animatedCode, color = colors.onSurface)

                            }
                        }
                    }, editClick = {
                        navigateToEditAccount(account.id)
                    })

                if (openDialog.value) {
                    AlertDialog(
                        containerColor = colors.secondaryVariant,
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = { Text(text = stringResource(id = R.string.action_confirmation)) },
                        text = { Text(text = stringResource(id = R.string.you_want_delete_selected_item)) },
                        confirmButton = {
                            Button(onClick = {
                                openDialog.value = false
                                coroutineScope.launch {
                                    viewModel.deleteAccount(account = selectedAccount.value)
                                }
                            }) {
                                Text(text = stringResource(id = R.string.delete))
                            }
                        },
                        dismissButton = {


                            Button(onClick = { openDialog.value = false }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        },
                    )
                }
            }
        }
    }
}


@Composable
private fun Account(
    deleteClick: () -> Unit,
    copyClick: () -> Unit,
    editClick: () -> Unit,
    issuer: (@Composable () -> Unit)?,
    label: @Composable () -> Unit,
    indicator: @Composable () -> Unit,
    code: @Composable () -> Unit,

    ) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)

    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                if (issuer != null) {
                    val color = LocalContentColor.current.copy(alpha = 0.7f)
                    CompositionLocalProvider(LocalContentColor provides color) {
                        ProvideTextStyle(MaterialTheme.typography.h5) {
                            issuer()
                        }
                    }
                }
                ProvideTextStyle(MaterialTheme.typography.h5) {
                    label()
                }


            }

            AnimatedVisibility(
                visible = true,
            ) {
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)

                    ) {
                        indicator()
                        ProvideTextStyle(MaterialTheme.typography.h5) {
                            code()
                        }
                        Spacer(Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(0.dp)
                        ) {
                            IconButton(onClick = copyClick) {
                                Icon(
                                    painterResource(id = R.drawable.ic_content_copy),
                                    contentDescription = "copy",

                                    )
                            }
                            IconButton(onClick = deleteClick) {
                                Icon(Icons.Default.Delete, contentDescription = "delete")
                            }

                            IconButton(onClick = editClick) {
                                Icon(
                                    painterResource(id = R.drawable.ic_edit),
                                    contentDescription = "edit"
                                )
                            }
                        }

                    }
                }
            }

        }
    }
    Divider(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth(),
    )
}

