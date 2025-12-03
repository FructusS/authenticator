package com.example.itplaneta.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R

/**
 * Централизованный TopAppBar с умной конфигурацией
 */
@Composable
fun AppTopBar(
    config: TopBarConfig,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val containerColor = colorScheme.surfaceColorAtElevation(3.dp)

    TopAppBar(
        title = {
            if (config.titleRes != null) {
                Text(stringResource(config.titleRes))
            } else {
                config.title?.invoke()
            }
        },
        navigationIcon = {
            if (config.showBackButton && config.onNavigateBack != null) {
                IconButton(onClick = config.onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = {
            config.actions.forEach { action ->
                when (action) {
                    is TopBarAction.Settings -> {
                        IconButton(onClick = action.onClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                    is TopBarAction.Custom -> {
                        action.content(this)
                    }
                }
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = colorScheme.onSurface,
            navigationIconContentColor = colorScheme.onSurface,
            actionIconContentColor = colorScheme.onSurface
        )
    )
}

/**
 * Конфигурация TopAppBar
 */
data class TopBarConfig(
    @StringRes val titleRes: Int? = null,
    val title: (@Composable () -> Unit)? = null,
    val showBackButton: Boolean = false,
    val onNavigateBack: (() -> Unit)? = null,
    val actions: List<TopBarAction> = emptyList()
)

/**
 * Типы действий в TopAppBar
 */
sealed class TopBarAction {
    data class Settings(val onClick: () -> Unit) : TopBarAction()
    data class Custom(val content: @Composable RowScope.() -> Unit) : TopBarAction()
}

/**
 * Билдер для удобного создания конфигураций
 */
class TopBarConfigBuilder {
    private var titleRes: Int? = null
    private var title: (@Composable () -> Unit)? = null
    private var showBackButton: Boolean = false
    private var onNavigateBack: (() -> Unit)? = null
    private val actions = mutableListOf<TopBarAction>()

    fun title(@StringRes resId: Int) {
        this.titleRes = resId
    }

    fun title(content: @Composable () -> Unit) {
        this.title = content
    }

    fun backButton(onNavigateBack: () -> Unit) {
        this.showBackButton = true
        this.onNavigateBack = onNavigateBack
    }

    fun settingsAction(onClick: () -> Unit) {
        actions.add(TopBarAction.Settings(onClick))
    }

    fun customAction(content: @Composable RowScope.() -> Unit) {
        actions.add(TopBarAction.Custom(content))
    }

    fun build() = TopBarConfig(
        titleRes = titleRes,
        title = title,
        showBackButton = showBackButton,
        onNavigateBack = onNavigateBack,
        actions = actions
    )
}

/**
 * DSL для создания конфигурации
 */
fun topBarConfig(builder: TopBarConfigBuilder.() -> Unit): TopBarConfig {
    return TopBarConfigBuilder().apply(builder).build()
}