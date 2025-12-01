package com.example.itplaneta.ui.screens.mainscreen.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account

@Composable
fun AccountRow(
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

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                                    CircularProgressIndicator(
                                        progress = { animProgress },
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
