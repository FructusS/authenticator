package com.example.itplaneta.domain.model

import com.example.itplaneta.core.utils.Result

typealias AccountResult<T> = Result<T, String>
typealias BackupResult<T> = Result<T, BackupMessage>
