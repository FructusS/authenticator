package com.example.itplaneta.ui.screens.howitworks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R
import kotlinx.coroutines.launch
import com.example.itplaneta.AuthenticatorTopAppBar
import com.example.itplaneta.ui.components.AppTopBar
import com.example.itplaneta.ui.components.topBarConfig
import com.example.itplaneta.ui.navigation.AccountDestination

data class PagerItem(
    @StringRes val description: Int, @DrawableRes val image: Int
)

@Composable
fun HowItWorksScreen(
    onNavigateUp: () -> Unit, canNavigateBack: Boolean, modifier: Modifier = Modifier
) {
    val items = remember { createPagerItems() }
    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(), topBar = {
            AppTopBar(
                config = topBarConfig {
                    title(R.string.how_it_works)
                    backButton(onNavigateUp)
                })
        }) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 12.dp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->
                    PagerPageContent(items[page])
                }

                PagerIndicator(
                    itemCount = items.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp)
                )

                NavigationButton(
                    isLastPage = pagerState.currentPage == items.size - 1, onNext = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }, onFinish = onNavigateUp
                )
            }
        }
    }
}


@Composable
private fun PagerPageContent(item: PagerItem) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = item.image),
                contentDescription = stringResource(id = item.description),
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = item.description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


@Composable
private fun PagerIndicator(
    itemCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(itemCount) { index ->
            val isSelected = index == currentPage
            val size = if (isSelected) 10.dp else 8.dp
            val color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }

            Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun NavigationButton(
    isLastPage: Boolean,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    FilledTonalButton(
        onClick = if (isLastPage) onFinish else onNext,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = if (isLastPage)
                stringResource(id = R.string.exit)
            else
                stringResource(id = R.string.continue_)
        )
    }
}

private fun createPagerItems() = listOf(
    PagerItem(
        description = R.string.first_pager,
        image = R.drawable.ic_qr_scanner_phone
    ),
    PagerItem(
        description = R.string.second_pager,
        image = R.drawable.ic_correct_otp_code
    )
)