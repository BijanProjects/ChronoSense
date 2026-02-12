package com.chronosense.core.common

/**
 * A discriminated-union result type for operations that can fail.
 *
 * Preferred over nullable returns when the caller needs to distinguish
 * between "no data" and "error". Supports [map], [flatMap], safe extraction,
 * and a convenient [of] builder.
 */
sealed class ChronoResult<out T> {
    data class Success<T>(val data: T) : ChronoResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : ChronoResult<Nothing>()
    data object Loading : ChronoResult<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error

    inline fun <R> map(transform: (T) -> R): ChronoResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    inline fun <R> flatMap(transform: (T) -> ChronoResult<R>): ChronoResult<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
        is Loading -> this
    }

    fun getOrNull(): T? = (this as? Success)?.data
    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default

    companion object {
        inline fun <T> of(block: () -> T): ChronoResult<T> = try {
            Success(block())
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error", e)
        }
    }
}
