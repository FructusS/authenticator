package com.example.itplaneta.ui.navigation

import com.example.itplaneta.R

interface NavigationDestination {
    val route: String
    val titleRes: Int?
}

object HowItWorksDestination : NavigationDestination {
    override val route = "how_it_works_screen"
    override val titleRes = null
}

object MainDestination : NavigationDestination {
    override val route = "main_screen"
    override val titleRes = R.string.app_name
}

object PinDestination : NavigationDestination {
    override val route = "pin_screen"
    override val titleRes = R.string.add
    const val modeArg = "mode"
    val routeWithArgs = "${route}?$modeArg={$modeArg}"

    fun createRoute(mode: String?): String = "${route}?$modeArg=${mode}"

}

object AccountDestination : NavigationDestination {
    override val route = "account_screen"
    const val accountIdArg = "accountId"

    val routeWithArgs = "$route?$accountIdArg={$accountIdArg}"

    override val titleRes = R.string.add

    fun createRoute(accountId: Int?): String = if (accountId == null) route
    else "$route?$accountIdArg=$accountId"
}

object QrScannerDestination : NavigationDestination {
    override val route = "qr_scanner_screen"
    override val titleRes = R.string.scanning
}

object SettingsDestination : NavigationDestination {
    override val route = "settings_screen"
    override val titleRes = R.string.settings
}
