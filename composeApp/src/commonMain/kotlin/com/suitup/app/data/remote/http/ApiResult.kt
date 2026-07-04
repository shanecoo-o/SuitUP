package com.suitup.app.data.remote.http

sealed interface ApiError {
    val message: String

    data class NetworkUnavailable(override val message: String) : ApiError
    data class Unauthorized(override val message: String) : ApiError
    data class Forbidden(override val message: String) : ApiError
    data class NotFound(override val message: String) : ApiError
    data class ValidationError(
        override val message: String,
        val fieldErrors: Map<String, String>,
    ) : ApiError
    data class Conflict(override val message: String) : ApiError
    data class ServerError(override val message: String, val status: Int) : ApiError
    data class Unknown(override val message: String, val cause: Throwable? = null) : ApiError
}

sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    data class Failure(val error: ApiError) : ApiResult<Nothing>
}

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(value))
    is ApiResult.Failure -> this
}

class ApiException(val apiError: ApiError) : Exception(apiError.message)
