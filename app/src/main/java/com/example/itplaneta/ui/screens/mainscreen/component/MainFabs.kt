package com.example.itplaneta.ui.screens.mainscreen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R

data class FabsAction(
    val icon: ImageVector, val onClick: () -> Unit
)

@Composable
fun MainFabs(
    expanded: Boolean,
    onToggle: () -> Unit,
    actions: List<FabsAction>,
    fabSize: Dp = 56.dp,
    actionFabSize: Dp = 44.dp,
    spacing: Dp = 12.dp,
    elevation: Dp = 6.dp
) {
    val density = LocalDensity.current
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f, animationSpec = tween(250)
    )
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        actions.forEachIndexed { index, action ->
            val delay = index * 30
            AnimatedVisibility(
                visible = expanded,
                enter = slideInVertically(
                    initialOffsetY = { with(density) { (it + (fabSize - actionFabSize).roundToPx() / 2) } },
                    animationSpec = tween(durationMillis = 270, delayMillis = delay)
                ) + fadeIn(animationSpec = tween(200, delayMillis = delay)),
                exit = slideOutVertically(
                    targetOffsetY = { with(density) { (it + (fabSize - actionFabSize).roundToPx() / 2) } },
                    animationSpec = tween(durationMillis = 200)
                ) + fadeOut(animationSpec = tween(150))
            ) {
                SmallFloatingActionButton(
                    onClick = action.onClick,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(actionFabSize)
                ) {
                    Icon(action.icon, contentDescription = null)
                }
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
            modifier = Modifier.size(fabSize)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.rotate(rotation),
            )
        }
    }
}
