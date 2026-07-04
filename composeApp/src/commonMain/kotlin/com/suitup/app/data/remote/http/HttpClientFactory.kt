package com.suitup.app.data.remote.http

import com.suitup.app.data.remote.auth.AuthResponseDto
import com.suitup.app.data.remote.auth.RefreshRequestDto
import com.suitup.app.data.remote.auth.TokenStore
import com.suitup.app.data.remote.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object RemoteJson {
    val instance = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        explicitNulls = false
    }
}

object HttpClientFactory {
    fun create(
        config: ApiConfig,
        tokenStore: TokenStore,
        json: Json = RemoteJson.instance,
    ): HttpClient {
        val apiHost = Url(config.baseUrl).host
        return HttpClient {
            expectSuccess = false

            install(ContentNegotiation) {
                json(json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = config.requestTimeoutMillis
                connectTimeoutMillis = config.connectTimeoutMillis
                socketTimeoutMillis = config.socketTimeoutMillis
            }
            if (config.enableLogging) {
                install(Logging) {
                    level = LogLevel.INFO
                    sanitizeHeader("<redacted>") { header ->
                        header.equals(HttpHeaders.Authorization, ignoreCase = true)
                    }
                }
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenStore.getAccessToken()
                        val refresh = tokenStore.getRefreshToken()
                        if (access.isNullOrBlank() || refresh.isNullOrBlank()) null
                        else BearerTokens(access, refresh)
                    }
                    sendWithoutRequest { request -> request.url.host == apiHost }
                    refreshTokens {
                        val refresh = oldTokens?.refreshToken ?: tokenStore.getRefreshToken()
                        if (refresh.isNullOrBlank()) {
                            null
                        } else {
                            val response = client.post(config.url("/api/auth/refresh")) {
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                setBody(RefreshRequestDto(refresh))
                            }
                            if (!response.status.isSuccess()) {
                                tokenStore.clearTokens()
                                null
                            } else {
                                val refreshed = response.body<AuthResponseDto>()
                                tokenStore.saveTokens(refreshed.accessToken, refreshed.refreshToken)
                                BearerTokens(refreshed.accessToken, refreshed.refreshToken)
                            }
                        }
                    }
                }
            }
            defaultRequest {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }
        }
    }
}
