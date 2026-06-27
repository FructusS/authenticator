package com.example.itplaneta.data.mapper

import com.example.itplaneta.data.sources.Account as AccountEntity
import com.example.itplaneta.domain.model.Account as DomainAccount

fun AccountEntity.toDomain(): DomainAccount = DomainAccount(
    id = id,
    issuer = issuer,
    label = label,
    tokenType = tokenType,
    algorithm = algorithm,
    secret = secret,
    digits = digits,
    counter = counter,
    period = period
)

fun DomainAccount.toEntity(): AccountEntity = AccountEntity(
    id = id,
    issuer = issuer,
    label = label,
    tokenType = tokenType,
    algorithm = algorithm,
    secret = secret,
    digits = digits,
    counter = counter,
    period = period
)
