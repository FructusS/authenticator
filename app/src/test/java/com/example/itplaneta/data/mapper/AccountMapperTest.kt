package com.example.itplaneta.data.mapper

import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account as AccountEntity
import com.example.itplaneta.domain.model.Account as DomainAccount
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountMapperTest {

    @Test
    fun entityToDomainPreservesAllFields() {
        val entity = AccountEntity(
            id = 7,
            issuer = "Issuer",
            label = "Label",
            tokenType = OtpType.Hotp,
            algorithm = OtpAlgorithm.Sha256,
            secret = "JBSWY3DPEHPK3PXP",
            digits = 8,
            counter = 42L,
            period = 45
        )

        val domain = entity.toDomain()

        assertEquals(
            DomainAccount(
                id = 7,
                issuer = "Issuer",
                label = "Label",
                tokenType = OtpType.Hotp,
                algorithm = OtpAlgorithm.Sha256,
                secret = "JBSWY3DPEHPK3PXP",
                digits = 8,
                counter = 42L,
                period = 45
            ),
            domain
        )
    }

    @Test
    fun domainToEntityPreservesAllFields() {
        val domain = DomainAccount(
            id = 11,
            issuer = null,
            label = "Account",
            tokenType = OtpType.Totp,
            algorithm = OtpAlgorithm.Sha512,
            secret = "JBSWY3DPEHPK3PXP",
            digits = 6,
            counter = 0L,
            period = 30
        )

        val entity = domain.toEntity()

        assertEquals(
            AccountEntity(
                id = 11,
                issuer = null,
                label = "Account",
                tokenType = OtpType.Totp,
                algorithm = OtpAlgorithm.Sha512,
                secret = "JBSWY3DPEHPK3PXP",
                digits = 6,
                counter = 0L,
                period = 30
            ),
            entity
        )
    }
}
