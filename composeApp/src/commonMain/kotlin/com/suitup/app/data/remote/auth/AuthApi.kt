package com.suitup.app.data.remote.auth

import com.suitup.app.data.remote.config.ApiConfig
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.RemoteJson
import com.suitup.app.data.remote.http.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.Json

class AuthApi(
    private val client: HttpClient,
    private val config: ApiConfig,
    private val json: Json = RemoteJson.instance,
) {
    suspend fun register(request: RegisterRequestDto): ApiResult<AuthResponseDto> =
        safeApiCall(json) { client.post(config.url("/api/auth/register")) { setBody(request) } }

    suspend fun login(request: LoginRequestDto): ApiResult<AuthResponseDto> =
        safeApiCall(json) { client.post(config.url("/api/auth/login")) { setBody(request) } }

    suspend fun refresh(refreshToken: String): ApiResult<AuthResponseDto> =
        safeApiCall(json) {
            client.post(config.url("/api/auth/refresh")) {
                setBody(RefreshRequestDto(refreshToken))
            }
        }

    suspend fun me(): ApiResult<UserDto> =
        safeApiCall(json) { client.get(config.url("/api/auth/me")) }
}
