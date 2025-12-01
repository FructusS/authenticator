package com.example.itplaneta.ui.screens.mainscreen.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.itplaneta.R


@Composable
fun DeleteAccountDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.action_confirmation)) },
        text = { Text(text = stringResource(id = R.string.you_want_delete_selected_item)) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.delete))
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}