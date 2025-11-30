package com.example.itplaneta.ui.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
inline fun <reified T : Enum<T>> DropdownSelector(
    selectedValue: T,
    options: List<T>,
    labelRes: Int,
    displayLabels: Map<T, String>,
    crossinline onSelectionChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(), expanded = expanded, onExpandedChange = { !expanded }) {
        val fillMaxWidth = Modifier.fillMaxWidth()
        OutlinedTextField(
            modifier = fillMaxWidth.menuAnchor(
                ExposedDropdownMenuAnchorType.SecondaryEditable, true
            ),
            readOnly = true,
            value = displayLabels[selectedValue] ?: "",
            onValueChange = {},
            label = {
                Text(
                    text = stringResource(id = labelRes)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            })

        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            options.forEach { otpType ->
                DropdownMenuItem(text = { Text(displayLabels[otpType] ?: "") }, onClick = {
                    onSelectionChange(otpType)
                    expanded = false
                })
            }
        }
    }
}
