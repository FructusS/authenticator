package com.example.itplaneta.data.sources.database

import androidx.room.*
import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    /**
     * Добавить новый account в БД
     * Если exist - заменить (OnConflictStrategy.REPLACE)
     *
     * @param account Account для добавления
     * @throws Exception если ошибка БД
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAccount(account: Account)

    /**
     * Получить все accounts как Flow (рекомендуется!)
     * Автоматически обновляется при изменении
     *
     * @return Flow<List<Account>> всех accounts
     */
    @Query("SELECT * FROM accounts ORDER BY id DESC")
    fun getAllAccountsFlow(): Flow<List<Account>>

    /**
     * Получить все accounts (один раз)
     * Используйте getAllAccountsFlow() для отслеживания изменений
     *
     * @return List<Account> всех accounts
     * @throws Exception если ошибка БД
     */
    @Query("SELECT * FROM accounts ORDER BY id DESC")
    suspend fun getAllAccounts(): List<Account>

    /**
     * Обновить существующий account
     *
     * @param account Account с новыми данными
     * @throws Exception если account не найден или ошибка БД
     */
    @Update
    suspend fun updateAccount(account: Account)

    /**
     * Удалить account из БД
     *
     * @param account Account для удаления
     * @throws Exception если ошибка БД
     */
    @Delete
    suspend fun deleteAccount(account: Account)

    /**
     * Получить account по secret (уникальный ключ)
     *
     * @param secret Secret key для поиска
     * @return Account если найден, иначе null
     */
    @Query("SELECT * FROM accounts WHERE secret = :secret LIMIT 1")
    suspend fun getAccountBySecret(secret: String): Account?

    /**
     * Получить account по ID
     *
     * @param id Account ID
     * @return Account если найден, иначе null
     */
    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Int): Account?

    /**
     * Получить количество accounts в БД
     *
     * @return Flow<Int> количество accounts
     */
    @Query("SELECT COUNT(*) FROM accounts")
    fun getAccountCountFlow(): Flow<Int>

    /**
     * Проверить exist ли account с таким secret
     *
     * @param secret Secret key для проверки
     * @return true если существует
     */
    @Query("SELECT EXISTS(SELECT 1 FROM accounts WHERE secret = :secret)")
    suspend fun existsWithSecret(secret: String): Boolean

    /**
     * Удалить все accounts (осторожно!)
     * Используйте с осторожностью!
     */
    @Query("DELETE FROM accounts")
    suspend fun deleteAllAccounts()

    /**
     * Получить accounts по issuer (имя сервиса)
     *
     * @param issuer Имя сервиса
     * @return Flow<List<Account>> accounts с этим issuer
     */
    @Query("SELECT * FROM accounts WHERE issuer = :issuer ORDER BY label ASC")
    fun getAccountsByIssuerFlow(issuer: String): Flow<List<Account>>

    /**
     * Получить account по label и issuer (более специфичный поиск)
     *
     * @param label Account label
     * @param issuer Account issuer
     * @return Account если найден
     */
    @Query("SELECT * FROM accounts WHERE label = :label AND issuer = :issuer LIMIT 1")
    suspend fun getAccountByLabelAndIssuer(label: String, issuer: String): Account?

    /**
     * Обновить counter для HOTP account
     *
     * @param id Account ID
     * @param newCounter Новое значение counter
     */
    @Query("UPDATE accounts SET counter = :newCounter WHERE id = :id")
    suspend fun updateHotpCounter(id: Int, newCounter: Long)

    /**
     * Получить все accounts как Flow с фильтром по типу
     *
     * @param tokenType Тип токена (TOTP или HOTP)
     * @return Flow<List<Account>> отфильтрованные accounts
     */
    @Query("SELECT * FROM accounts WHERE tokenType = :tokenType ORDER BY id DESC")
    fun getAccountsByTypeFlow(tokenType: String): Flow<List<Account>>
}