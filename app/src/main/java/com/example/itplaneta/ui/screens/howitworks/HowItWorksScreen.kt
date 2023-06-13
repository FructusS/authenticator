package com.example.itplaneta.ui.screens.howitworks

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun HowItWorksScreen(
    onNavigateUp: () -> Unit,
) {
    val pagerState = rememberPagerState()
    val items = createItems()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        HorizontalPager(
            count = items.size, state = pagerState, verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = items[it].image),
                    alignment = Alignment.Center,
                    contentDescription = "image"
                )
                Text(
                    text = stringResource(id = items[it].description), textAlign = TextAlign.Center
                )
            }

        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            if (pagerState.currentPage == items.size - 1) {
                onNavigateUp()

            } else {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                }
            }

        }) {

            if (pagerState.currentPage == items.size - 1) {
                Text(text = stringResource(id = R.string.exit))
            } else {
                Text(text = stringResource(id = R.string.continue_))
            }
        }
    }
}


data class HorizontalPagerContent(
    @StringRes val description: Int, @DrawableRes val image: Int
)

fun createItems() = listOf(
    HorizontalPagerContent(
        description = R.string.first_pager, image = R.drawable.ic_qr_scanner_phone
    ), HorizontalPagerContent(
        description = R.string.second_pager, image = R.drawable.ic_correct_otp_code
    )
)