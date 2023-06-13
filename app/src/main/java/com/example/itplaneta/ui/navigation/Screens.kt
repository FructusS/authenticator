package com.example.itplaneta.ui.navigation

import com.example.itplaneta.R

//sealed class Screens(val route  : String, val titleScreen : String){
//
//    object HowItWorks : Screens("how_it_works", "asd")
//    object Main : Screens("main_screen")
//    object AddAccount : Screens("account_screen")
//    object Settings : Screens("settings_screen")
//    object EditAccount : Screens("account_screen/{accountId}"){
//        fun passAccountId(accountId: Int) = "account_screen/$accountId"
//    }
//    object QrScanner : Screens("qrscanner_screen")
//}

interface NavigationDestination{
    val route: String
    val titleScreen: Int?
}


object HowItWorksDestination : NavigationDestination {
    override val route = "how_it_works_screen"
    override val titleScreen = null
}

object MainDestination : NavigationDestination {
    override val route = "main_screen"
    override val titleScreen = R.string.app_name
}

object EditAccountDestination : NavigationDestination{
    override val route = "edit_account_screen"
    override val titleScreen = R.string.edit
    const val accountIdArg = "itemId"
    val routeWithArgs = "$route/{$accountIdArg}"
}

object AddAccountDestination: NavigationDestination{
    override val route = "add_account_screen"
    override val titleScreen = R.string.add
}

object QrScannerDestination : NavigationDestination{
    override val route = "qr_scanner_screen"
    override val titleScreen = R.string.scanning
}
object SettingsDestination : NavigationDestination{
    override val route = "settings_screen"
    override val titleScreen = R.string.settings
}