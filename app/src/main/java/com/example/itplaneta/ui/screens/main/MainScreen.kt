package com.example.itplaneta.ui.screens.main

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.itplaneta.R
import com.example.itplaneta.otp.OtpType
import com.example.itplaneta.ui.navigation.Screens



@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var isSettingButtonClick by remember {
        mutableStateOf(false)
    }
    val iconSettingsRotation by animateFloatAsState(if (isSettingButtonClick) 45f else 0f,)
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(backgroundColor = colors.primary) {
                androidx.compose.material.IconButton(onClick = {

                    isSettingButtonClick = !isSettingButtonClick
                    navController.navigate(Screens.Settings.route)
                }) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.settings), modifier = Modifier.rotate(iconSettingsRotation))
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Column {

                FloatingActionButton(
                    onClick =  {
                        navController.navigate(Screens.AddAccount.route)
                    } ,

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "add"
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
                Account(
                    deleteClick = {
                        viewModel.deleteAccount(account = account)
                    },
                    copyClick = {
                        clipboardManager.setText(AnnotatedString(code.toString()))
                        Toast.makeText(context, context.getText(R.string.copied), Toast.LENGTH_SHORT).show()
                    },
                    issuer = if (account.issuer != "") { ->
                        Text(account.issuer.toString(), maxLines = 1, fontSize = 25.sp, color = colors.onSurface)
                    } else null,
                    label = { Text(account.label, maxLines = 1, fontSize = 25.sp, color = colors.onSurface)},

                    indicator = {
                        if (OtpType.Totp == account.tokenType) {
                            Box(
                                modifier = Modifier.size(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val timerProgress = viewModel.timerProgresses[account.id]
                                val timerValue = viewModel.timerValues[account.id]
                                if (timerProgress != null) {
                                    val animatedTimerProgress by animateFloatAsState(
                                        targetValue = timerProgress,
                                        animationSpec = tween(durationMillis = 500)
                                    )
                                    CircularProgressIndicator(progress = animatedTimerProgress,color = if (timerValue!! <= 10) colors.error else colors.onPrimary)
                                }
                                if (timerValue != null) {
                                    Text(timerValue.toString(), color = colors.onSurface)
                                }
                            }
                        }
                        if (OtpType.Hotp == account.tokenType) {
                            OutlinedButton(onClick = { viewModel.incrementHotpCounter(account.id)}) {
                                Text(account.counter.toString(), color = colors.onSurface)
                            }
                        }
                    },
                    code = {
                        AnimatedContent(
                            targetState = code,
                            transitionSpec = {
                                slideIntoContainer(
                                    towards = AnimatedContentScope.SlideDirection.Up,
                                    animationSpec = tween(500)
                                ) + fadeIn() with
                                        slideOutOfContainer(
                                            towards = AnimatedContentScope.SlideDirection.Up,
                                            animationSpec = tween(500)
                                        ) + fadeOut()
                            }
                        ) { animatedCode ->
                            if (animatedCode != null) {

                                    Text(animatedCode, color = colors.onSurface)

                            }
                        }
                    },
                    editClick = {
                        navController.navigate(route = Screens.EditAccount.passAccountId(account.id))
                    }
                )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
//                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.large
                ) {
                }
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (issuer != null) {
                        val color = LocalContentColor.current.copy(alpha = 0.7f)
                        CompositionLocalProvider(LocalContentColor provides color) {
                            ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                                issuer()
                            }
                        }
                    }
                    ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                        label()
                    }

                }
                Spacer(Modifier.weight(1f))

                IconButton(onClick = copyClick) {
                    Icon(
                        painterResource(id = R.drawable.ic_content_copy),
                        contentDescription = "copy"
                    )
                }
            }

            AnimatedVisibility(
                visible = true,
            ) {
                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        indicator()
                        ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                            code()
                        }
                        Spacer(Modifier.weight(1f))
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
    Divider(modifier = Modifier
        .height(1.dp)
        .fillMaxWidth(),
)

}
