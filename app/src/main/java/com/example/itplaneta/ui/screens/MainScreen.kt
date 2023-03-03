package com.example.itplaneta.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itplaneta.ui.viewmodels.MainViewModel
import com.example.itplaneta.R
import com.example.itplaneta.otp.OtpType

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onNavigateToAccount: () -> Unit) {
    Scaffold(
        floatingActionButton = {
        FloatingActionButton(onClick = {onNavigateToAccount()}) {
            Image(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = "add")
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
        ){
            items(list.value){account ->
                var visible = true
                val code = viewModel.codes[account.id]
                Account(
                    issuer = if (account.issuer != "") { ->
                        Text(account.issuer, maxLines = 1,fontSize = 25.sp)
                    } else null,
                    label = { Text(account.label, maxLines = 1, fontSize = 25.sp) },

                    indicator = {
                        if (OtpType.Totp == account.tokenType){
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
                        if (OtpType.Hotp == account.tokenType){
                                FilledIconButton(onClick = {

                                }) {
                                    Text(account.counter.toString())
                                }
                        }
                    },
                    code =  {
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
                    }
                )
            }
        }

    }
}

@Composable
private fun Account(

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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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

                    }
                }
            }
        }
    }
}
