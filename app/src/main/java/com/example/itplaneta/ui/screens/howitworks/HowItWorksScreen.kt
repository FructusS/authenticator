package com.example.itplaneta.ui.screens.howitworks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.example.itplaneta.ui.navigation.AccountDestination

data class PagerItem(
    @StringRes val description: Int,
    @DrawableRes val image: Int
)

/**
 * Screen showing how the authenticator app works
 * Uses Material 3 HorizontalPager (not deprecated Accompanist)
 */
@Composable
fun HowItWorksScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier
) {
    val items = createPagerItems()
    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AuthenticatorTopAppBar(
                title = { Text(stringResource(id = AccountDestination.titleRes)) },
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                PagerPageContent(items[page])
            }

            // Indicator
            PagerIndicator(
                itemCount = items.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            // Navigation Button
            NavigationButton(
                isLastPage = pagerState.currentPage == items.size - 1,
                onNext = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onFinish = onNavigateUp
            )
        }
    }
}

@Composable
private fun PagerPageContent(item: PagerItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = stringResource(id = item.description),
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = item.description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
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
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 12.dp else 8.dp)
                    .background(
                        if (index == currentPage)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(4.dp)
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
    Button(
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
    ),
    // Add more items as needed
)
