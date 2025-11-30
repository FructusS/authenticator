package com.example.itplaneta.di

import android.content.Context
import com.example.itplaneta.core.otp.OtpCodeManager
import com.example.itplaneta.core.otp.models.OtpGenerator
import com.example.itplaneta.core.otp.parser.Base32
import com.example.itplaneta.data.repository.AccountRepository
import com.example.itplaneta.data.sources.database.AccountDao
import com.example.itplaneta.data.sources.database.AccountDatabase
import com.example.itplaneta.domain.IAccountRepository
import com.example.itplaneta.domain.validation.AccountValidator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AccountDatabase {
        return AccountDatabase.getDatabase(context)
    }

    @Provides
    fun provideAccountDao(accountDatabase: AccountDatabase): AccountDao {
        return accountDatabase.accountDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun provideAccountRepository(
        impl: AccountRepository
    ): IAccountRepository
}

@Module
@InstallIn(SingletonComponent::class)
class ValidationModule {
    @Provides
    fun provideAccountValidator(base32: Base32): AccountValidator {
        return AccountValidator(base32)
    }
}

@Module
@InstallIn(SingletonComponent::class)
class OtpModule {
    @Provides
    fun provideOtpCodeService(otpGenerator: OtpGenerator) : OtpCodeManager {
        return OtpCodeManager(otpGenerator)
    }
}