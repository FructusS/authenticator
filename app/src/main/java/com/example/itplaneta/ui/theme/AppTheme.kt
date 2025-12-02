package com.example.itplaneta.ui.theme

enum class AppTheme {
    Light, Dark, Auto;

    companion object {
        fun fromName(name: String): AppTheme = entries.firstOrNull { it.name == name } ?: Auto
    }
}