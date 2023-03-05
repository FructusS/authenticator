package com.example.itplaneta.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.itplaneta.ui.viewmodels.MainViewModel
import com.example.itplaneta.R
import com.example.itplaneta.otp.OtpType

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavHostController) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Scaffold(
        backgroundColor = colorResource(id = R.color.bg_main),
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                var expanded by remember { mutableStateOf(false) }
                val iconAddRotation by animateFloatAsState(if (expanded) 45f else 0f)

                AnimatedVisibility(
                    visible = expanded,
                    enter = slideInVertically() + expandVertically(expandFrom = Alignment.Bottom),
                    exit = slideOutVertically(targetOffsetY = { fullWidth -> fullWidth })
                            + shrinkVertically(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FloatingActionButton(
                            onClick = { navController.navigate("qrscanner") },
                            backgroundColor = colorResource(id = R.color.bg_account)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_qr_code_scanner),
                                contentDescription = "qr scanner"
                            )
                        }
                        FloatingActionButton(
                            onClick = { navController.navigate("account") },
                            backgroundColor = colorResource(id = R.color.bg_account)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_keyboard),
                                contentDescription = "keyboard")
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { expanded = !expanded },
                    backgroundColor = colorResource(id = R.color.bg_account)
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
                var visible = true
                val code = viewModel.codes[account.id]
                Account(
                    deleteClick = {
                        viewModel.deleteAccount(account = account)
                    },
                    copyClick = {
                        clipboardManager.setText(AnnotatedString(code.toString()))
                        Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
                    },
                    issuer = if (account.issuer != "") { ->
                        Text(account.issuer, maxLines = 1, fontSize = 25.sp)
                    } else null,
                    label = { Text(account.label, maxLines = 1, fontSize = 25.sp) },

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
                                    CircularProgressIndicator(progress = animatedTimerProgress)
                                }
                                if (timerValue != null) {
                                    Text(timerValue.toString())
                                }
                            }
                        }
                        if (OtpType.Hotp == account.tokenType) {
                            FilledIconButton(onClick = {
                            }) {
                                Text(account.counter.toString())
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
                                if (visible) {
                                    Text(animatedCode)
                                } else {
                                    Text("\u2022".repeat(animatedCode.length))
                                }
                            }
                        }
                    },
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 700,
                            easing = LinearOutSlowInEasing,
                            delayMillis = 500
                        )
                    )
                )
            }
        }

    }
}

@Composable
private fun Account(
    deleteClick: () -> Unit,
    copyClick: () -> Unit,
    modifier: Modifier = Modifier,
    issuer: (@Composable () -> Unit)?,
    label: @Composable () -> Unit,
    indicator: @Composable () -> Unit,
    code: @Composable () -> Unit,

    ) {
    val localDensity = LocalDensity.current
    val shape by animateValueAsState(
        targetValue = MaterialTheme.shapes.large,
        typeConverter = TwoWayConverter(
            convertToVector = {
                AnimationVector(
                    v1 = it.topStart.toPx(Size.Unspecified, localDensity),
                    v2 = it.topEnd.toPx(Size.Unspecified, localDensity),
                    v3 = it.bottomStart.toPx(Size.Unspecified, localDensity),
                    v4 = it.bottomEnd.toPx(Size.Unspecified, localDensity)
                )
            },
            convertFromVector = {
                RoundedCornerShape(
                    topStart = it.v1,
                    topEnd = it.v2,
                    bottomStart = it.v3,
                    bottomEnd = it.v4
                )
            }
        )
    )
    ElevatedCard(
        modifier = modifier,
        shape = shape
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.large
                ) {
                }
                Row(
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
            }
            AnimatedVisibility(
                visible = true,
            ) {
                Column {
                    Divider(Modifier.padding(vertical = 12.dp))
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
                        IconButton(onClick = copyClick) {
                            Icon(
                                painterResource(id = R.drawable.ic_content_copy),
                                contentDescription = "copy"
                            )
                        }
                    }
                }
            }
        }
    }
}
