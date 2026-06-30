package com.suitup.app.data.repository.remote

import com.suitup.app.data.mapper.toDomain
import com.suitup.app.data.remote.auth.AuthApi
import com.suitup.app.data.remote.auth.AuthResponseDto
import com.suitup.app.data.remote.auth.LoginRequestDto
import com.suitup.app.data.remote.auth.RegisterRequestDto
import com.suitup.app.data.remote.auth.TokenStore
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.map
import com.suitup.app.domain.model.Utilizador

class RemoteAuthRepository(
    private val api: AuthApi,
    private val tokenStore: TokenStore,
    private val clearCachedBearerTokens: () -> Unit = {},
) {
    suspend fun register(request: RegisterRequestDto): ApiResult<Utilizador> =
        persistAuthentication(api.register(request))

    suspend fun login(email: String, password: String): ApiResult<Utilizador> =
        persistAuthentication(api.login(LoginRequestDto(email, password)))

    suspend fun refresh(): ApiResult<Utilizador> {
        val refreshToken = tokenStore.getRefreshToken()
            ?: return ApiResult.Failure(
                com.suitup.app.data.remote.http.ApiError.Unauthorized("Não existe refresh token"),
            )
        return persistAuthentication(api.refresh(refreshToken))
    }

    suspend fun currentUser(): ApiResult<Utilizador> = api.me().map { it.toDomain() }

    suspend fun logout() {
        tokenStore.clearTokens()
        clearCachedBearerTokens()
    }

    private suspend fun persistAuthentication(
        result: ApiResult<AuthResponseDto>,
    ): ApiResult<Utilizador> = when (result) {
        is ApiResult.Success -> {
            tokenStore.saveTokens(result.value.accessToken, result.value.refreshToken)
            clearCachedBearerTokens()
            ApiResult.Success(result.value.user.toDomain())
        }
        is ApiResult.Failure -> result
    }
}
