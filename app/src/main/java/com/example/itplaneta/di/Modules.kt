package com.example.itplaneta.di

import android.content.Context
import com.example.itplaneta.data.QrCodeAnalyzerFactoryImpl
import com.example.itplaneta.data.backup.AccountBackupManager
import com.example.itplaneta.data.repository.AccountRepository
import com.example.itplaneta.data.backup.BackupRepository
import com.example.itplaneta.data.sources.database.AccountDao
import com.example.itplaneta.data.sources.database.AccountDatabase
import com.example.itplaneta.domain.IAccountBackupManager
import com.example.itplaneta.domain.IAccountRepository
import com.example.itplaneta.domain.IBackupRepository
import com.example.itplaneta.domain.QrCodeAnalyzerFactory
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
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
    @Singleton
    fun provideAccountRepository(
        impl: AccountRepository
    ): IAccountRepository

    @Binds
    @Singleton
    fun provideBackupRepository(
        impl: BackupRepository
    ): IBackupRepository

    @Binds
    @Singleton
    fun provideBackupAccountManager(
        impl: AccountBackupManager
    ): IAccountBackupManager
}

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    private const val TINK_PREF_FILE = "tink_keyset_prefs"
    private const val TINK_PREF_KEY = "tink_keyset"
    private const val MASTER_KEY_ALIAS = "backup_master_key"
    private const val MASTER_KEY_URI = "android-keystore://$MASTER_KEY_ALIAS"

    @Provides
    @Singleton
    fun provideAead(@ApplicationContext context: Context): Aead {
        TinkConfig.register()

        val manager = AndroidKeysetManager.Builder()
            .withSharedPref(context, TINK_PREF_KEY, TINK_PREF_FILE)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()

        return manager.keysetHandle.getPrimitive(Aead::class.java)
    }
}


@Module
@InstallIn(SingletonComponent::class)
interface CameraModule {

    @Binds
    fun bindQrCodeAnalyzerFactory(
        impl: QrCodeAnalyzerFactoryImpl
    ): QrCodeAnalyzerFactory
}