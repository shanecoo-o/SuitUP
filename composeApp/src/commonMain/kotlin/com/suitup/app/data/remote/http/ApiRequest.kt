package com.suitup.app.data.remote.http

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

suspend inline fun <reified T> safeApiCall(
    json: Json,
    noinline request: suspend () -> HttpResponse,
): ApiResult<T> = safeApiCall(json, request) { response -> response.body<T>() }

suspend fun <T> safeApiCall(
    json: Json,
    request: suspend () -> HttpResponse,
    decodeSuccess: suspend (HttpResponse) -> T,
): ApiResult<T> {
    return try {
        val response = request()
        if (response.status.isSuccess()) {
            ApiResult.Success(decodeSuccess(response))
        } else {
            val rawBody = response.bodyAsText()
            val errorResponse = runCatching {
                json.decodeFromString<ApiErrorResponse>(rawBody)
            }.getOrNull()
            ApiResult.Failure(mapHttpError(response.status.value, errorResponse))
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (failure: ApiException) {
        ApiResult.Failure(failure.apiError)
    } catch (failure: Throwable) {
        val typeName = failure::class.simpleName.orEmpty()
        val networkFailure = listOf("Connect", "Socket", "Timeout", "IOException", "UnresolvedAddress")
            .any(typeName::contains)
        ApiResult.Failure(if (networkFailure) {
            ApiError.NetworkUnavailable(failure.message ?: "Não foi possível contactar o servidor")
        } else {
            ApiError.Unknown(failure.message ?: "Erro inesperado ao processar a resposta", failure)
        })
    }
}

private fun mapHttpError(status: Int, response: ApiErrorResponse?): ApiError {
    val message = response?.message ?: when (status) {
        400 -> "Pedido inválido"
        401 -> "Autenticação necessária"
        403 -> "Sem permissão para esta operação"
        404 -> "Recurso não encontrado"
        409 -> "Conflito com dados existentes"
        else -> "Erro do servidor"
    }
    return when (status) {
        400 -> ApiError.ValidationError(message, response?.fieldErrors.orEmpty())
        401 -> ApiError.Unauthorized(message)
        403 -> ApiError.Forbidden(message)
        404 -> ApiError.NotFound(message)
        409 -> ApiError.Conflict(message)
        in 500..599 -> ApiError.ServerError(message, status)
        else -> ApiError.Unknown(message)
    }
}
