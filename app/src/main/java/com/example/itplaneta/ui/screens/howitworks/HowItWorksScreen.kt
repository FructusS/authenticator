package com.example.itplaneta.ui.screens.howitworks

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.itplaneta.R
import com.example.itplaneta.ui.navigation.Screens
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun HowItWorksScreen(navController: NavHostController) {
    val pagerState = rememberPagerState()
    val items = createItems()
    Scaffold() {
        Column() {
            HorizontalPager(
                modifier = Modifier.padding(it),
                count = items.size,
                state = pagerState
            ) {
                Column() {
                    Image(painter = painterResource(id = items[it].image), contentDescription = "image")
                    Text(text = items[it].description)
                }

            }
            val coroutineScope = rememberCoroutineScope()
            Button(onClick = {
                if (pagerState.currentPage == items.size - 1) {
                    navController.navigate(Screens.Settings.route) {
                        popUpTo(Screens.Settings.route) {
                            inclusive = true
                        }
                    }
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                    }
                }

            }) {

                if (pagerState.currentPage == items.size - 1) {
                    Text(text = "vihod")

                } else {
                    Text(text = "dalee")
                }
            }
        }
    }
}

data class HorizontalPagerContent(
    val description: String,
    @DrawableRes
    val image : Int
)

fun createItems() = listOf(

    HorizontalPagerContent(description = "Description1", image = R.drawable.ic_qr_scanner_phone),

)