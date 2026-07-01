package com.suitup.app.data.repository.remote

import com.suitup.app.data.mapper.toAuthenticatedUser
import com.suitup.app.data.remote.auth.AuthApi
import com.suitup.app.data.remote.auth.AuthResponseDto
import com.suitup.app.data.remote.auth.LoginRequestDto
import com.suitup.app.data.remote.auth.RegisterRequestDto
import com.suitup.app.data.remote.auth.TokenStore
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.remote.http.map
import com.suitup.app.domain.model.AuthenticatedUser

class RemoteAuthRepository(
    private val api: AuthApi,
    private val tokenStore: TokenStore,
    private val clearCachedBearerTokens: () -> Unit = {},
) {
    suspend fun register(request: RegisterRequestDto): ApiResult<AuthenticatedUser> =
        persistAuthentication(api.register(request))

    suspend fun login(email: String, password: String): ApiResult<AuthenticatedUser> =
        persistAuthentication(api.login(LoginRequestDto(email, password)))

    suspend fun refresh(): ApiResult<AuthenticatedUser> {
        val refreshToken = tokenStore.getRefreshToken()
            ?: return ApiResult.Failure(
                com.suitup.app.data.remote.http.ApiError.Unauthorized("Não existe refresh token"),
            )
        return persistAuthentication(api.refresh(refreshToken))
    }

    suspend fun currentUser(): ApiResult<AuthenticatedUser> = api.me().map { it.toAuthenticatedUser() }

    suspend fun logout() {
        tokenStore.clearTokens()
        clearCachedBearerTokens()
    }

    private suspend fun persistAuthentication(
        result: ApiResult<AuthResponseDto>,
    ): ApiResult<AuthenticatedUser> = when (result) {
        is ApiResult.Success -> {
            tokenStore.saveTokens(result.value.accessToken, result.value.refreshToken)
            clearCachedBearerTokens()
            ApiResult.Success(result.value.user.toAuthenticatedUser())
        }
        is ApiResult.Failure -> result
    }
}
