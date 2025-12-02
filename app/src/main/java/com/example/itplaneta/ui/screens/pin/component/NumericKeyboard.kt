package com.example.itplaneta.ui.screens.pin.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardTab
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardTab
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumericKeyboard(
    onDigitClick: (Char) -> Unit,
    onBackspaceClick: () -> Unit,
    onBackspaceLongClick: () -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp,
    minKeySize: Dp = 36.dp,
    maxKeySize: Dp = 78.dp,
    minSpacing: Dp = 4.dp,
    maxSpacing: Dp = 12.dp,
    buttonShape: Shape = RoundedCornerShape(12.dp)
) {
    BoxWithConstraints(modifier = modifier.padding(horizontal = horizontalPadding), content = {
        val totalWidth = maxWidth

        val spacing = (totalWidth * 0.03f).coerceIn(minSpacing, maxSpacing)

        val computedKeySize = ((totalWidth - spacing * 2f) / 3f).coerceIn(minKeySize, maxKeySize)

        var fontSize = (computedKeySize.value / 3.8f).sp
        if (fontSize < 12.sp) {
            fontSize = 12.sp
        }
        if (fontSize > 20.sp) {
            fontSize = 20.sp
        }
        val rows = remember {
            listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("back", "0", "enter")
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(spacing), modifier = Modifier.fillMaxWidth()
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        spacing, Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { key ->
                        when (key) {
                            "back" -> CircleKey(
                                onClick = onBackspaceClick, size = computedKeySize, content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Backspace"
                                    )
                                })

                            "enter" -> CircleKey(
                                onClick = onEnterClick, size = computedKeySize, content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardTab,
                                        contentDescription = "Enter"
                                    )
                                })

                            else -> {
                                val digit = key.first()
                                CircleKey(
                                    onClick = { onDigitClick(digit) },
                                    size = computedKeySize,
                                    content = {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = fontSize,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    })
                            }
                        }
                    }
                }
            }
        }
    })
}

@Composable
private fun CircleKey(
    onClick: () -> Unit, size: Dp, content: @Composable BoxScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .semantics { contentDescription = "PinKey" },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
