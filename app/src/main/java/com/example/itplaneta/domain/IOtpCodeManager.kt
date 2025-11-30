package com.example.itplaneta.domain

import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IOtpCodeManager {
    val codes: StateFlow<Map<Int, String>>
    val timerProgresses: StateFlow<Map<Int, Float>>
    val timerValues: StateFlow<Map<Int, Long>>
    fun start(scope: CoroutineScope, accountsFlow: Flow<List<Account>>, secretsFlow: Flow<Map<String, ByteArray>>)
    fun stop()
}