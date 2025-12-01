package com.example.itplaneta.core.utils

sealed class Result<out T, out E> {

    data class Success<T, E>(val data: T) : Result<T, E>()

    data class Error<T, E>(
        val exception: Exception,
        val error: E
    ) : Result<T, E>()

    data object Loading : Result<Nothing, Nothing>()

    /**
     * Execute a block if the result is Success
     */
    inline fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success<R, E>(transform(data))
        is Error -> Error<R, E>(exception, error)
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


    fun errorOrNull(): E? = (this as? Error)?.error
}

/**
 * Extension to fold result handling
 */
inline fun <T, E, R> Result<T, E>.fold(
    onSuccess: (T) -> R,
    onError: (Exception, E) -> R,
    onLoading: () -> R = { throw UnsupportedOperationException() }
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Error -> onError(exception, error)
    is Result.Loading -> onLoading()
}