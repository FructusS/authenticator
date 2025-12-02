package com.example.itplaneta

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.itplaneta.ui.navigation.AuthenticatorNavHost

@Composable
fun AuthenticatorApp(
    navController: NavHostController = rememberNavController()
) {
    AuthenticatorNavHost(navController = navController)
}
@Composable
fun AuthenticatorTopAppBar(
    title: @Composable () -> Unit,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val containerColor = colorScheme.surfaceColorAtElevation(3.dp)

    TopAppBar(
        title = title,
        navigationIcon = {
            if(canNavigateBack && navigateUp != null){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier.fillMaxWidth(),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = colorScheme.onSurface,
            navigationIconContentColor = colorScheme.onSurface,
            actionIconContentColor = colorScheme.onSurface
        )
    )
}