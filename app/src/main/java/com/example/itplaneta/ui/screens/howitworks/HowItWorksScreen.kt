package com.example.itplaneta.ui.screens.howitworks

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HowItWorksScreen(navController: NavHostController) {
    val pagerState = rememberPagerState()
    Scaffold() { it->
        HorizontalPager(modifier = Modifier.padding(it), count = 5, state = pagerState){

        }
    }
}