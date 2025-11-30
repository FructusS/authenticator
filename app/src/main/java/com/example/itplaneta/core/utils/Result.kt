package com.example.itplaneta.core.utils

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    /**
     * Execute a block if the result is Success
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception, message)
        Loading -> Loading
    }

    /**
     * Get data or null
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Get exception or null
     */
    fun exceptionOrNull(): Exception? = (this as? Error)?.exception
}

/**
 * Extension to fold result handling
 */
inline fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (Exception, String?) -> R,
    onLoading: () -> R = { throw UnsupportedOperationException() }
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Error -> onError(exception, message)
    is Result.Loading -> onLoading()
}