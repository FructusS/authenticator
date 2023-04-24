package com.example.itplaneta.ui.navigation

sealed class Screens(val route  : String){

    object HowItWorks : Screens("how_it_works")
    object Main : Screens("main_screen")
    object AddAccount : Screens("account_screen")
    object Settings : Screens("settings_screen")
    object EditAccount : Screens("account_screen/{accountId}"){
        fun passAccountId(accountId: Int) = "account_screen/$accountId"
    }
    object QrScanner : Screens("qrscanner_screen")
}
