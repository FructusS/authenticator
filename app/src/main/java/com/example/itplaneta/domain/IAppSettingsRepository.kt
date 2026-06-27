package com.example.itplaneta.domain

import com.example.itplaneta.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow

interface IAppSettingsRepository {
    val themeFlow: Flow<AppTheme>

    suspend fun saveTheme(theme: AppTheme)
}
