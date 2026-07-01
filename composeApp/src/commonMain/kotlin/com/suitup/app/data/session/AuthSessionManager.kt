package com.suitup.app.data.session

import com.suitup.app.data.remote.SuitUpRemoteModule
import com.suitup.app.data.remote.auth.RegisterRequestDto
import com.suitup.app.data.remote.auth.TokenStore
import com.suitup.app.data.remote.http.ApiError
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.repository.remote.RemoteAuthRepository
import com.suitup.app.domain.model.AuthSessionState
import com.suitup.app.domain.model.AuthenticatedUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthSessionManager(
    private val repository: RemoteAuthRepository,
    private val tokenStore: TokenStore,
) {
    private val _state = MutableStateFlow<AuthSessionState>(AuthSessionState.Checking)
    val state: StateFlow<AuthSessionState> = _state.asStateFlow()

    val isAuthenticated: Boolean
        get() = _state.value is AuthSessionState.Authenticated

    suspend fun restoreSession() {
        _state.value = AuthSessionState.Checking
        val hasAccessToken = !tokenStore.getAccessToken().isNullOrBlank()
        val hasRefreshToken = !tokenStore.getRefreshToken().isNullOrBlank()

        if (!hasAccessToken && !hasRefreshToken) {
            _state.value = AuthSessionState.Unauthenticated()
            return
        }

        when (val current = repository.currentUser()) {
            is ApiResult.Success -> authenticate(current.value)
            is ApiResult.Failure -> {
                if (current.error is ApiError.Unauthorized && hasRefreshToken) {
                    when (val refreshed = repository.refresh()) {
                        is ApiResult.Success -> authenticate(refreshed.value)
                        is ApiResult.Failure -> clearExpiredSession(refreshed.error)
                    }
                } else if (current.error is ApiError.NetworkUnavailable) {
                    _state.value = AuthSessionState.Unauthenticated(authErrorMessage(current.error))
                } else {
                    clearExpiredSession(current.error)
                }
            }
        }
    }

    suspend fun login(email: String, password: String): ApiResult<AuthenticatedUser> =
        accept(repository.login(email.trim(), password))

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String?,
    ): ApiResult<AuthenticatedUser> = accept(
        repository.register(
            RegisterRequestDto(
                fullName = fullName.trim(),
                email = email.trim(),
                phone = phone?.trim()?.takeIf(String::isNotBlank),
                password = password,
            )
        )
    )

    suspend fun refresh(): ApiResult<AuthenticatedUser> = accept(repository.refresh())

    suspend fun me(): ApiResult<AuthenticatedUser> = accept(repository.currentUser())

    suspend fun logout() {
        repository.logout()
        _state.value = AuthSessionState.Unauthenticated()
    }

    private fun authenticate(account: AuthenticatedUser) {
        _state.value = AuthSessionState.Authenticated(account)
    }

    private suspend fun clearExpiredSession(error: ApiError) {
        repository.logout()
        _state.value = AuthSessionState.Unauthenticated(authErrorMessage(error, sessionCheck = true))
    }

    private suspend fun accept(
        result: ApiResult<AuthenticatedUser>,
    ): ApiResult<AuthenticatedUser> {
        when (result) {
            is ApiResult.Success -> authenticate(result.value)
            is ApiResult.Failure -> Unit
        }
        return result
    }
}

fun authErrorMessage(error: ApiError, sessionCheck: Boolean = false): String = when (error) {
    is ApiError.NetworkUnavailable -> "Não foi possível ligar ao servidor."
    is ApiError.Unauthorized -> if (sessionCheck) {
        "Sessão expirada. Faça login novamente."
    } else {
        "Credenciais inválidas."
    }
    is ApiError.Forbidden -> "A sua conta não tem permissão para esta área."
    is ApiError.NotFound -> "Serviço de autenticação não encontrado."
    is ApiError.ValidationError -> error.fieldErrors.values.firstOrNull()
        ?: "Verifique os dados introduzidos."
    is ApiError.Conflict -> "Email já registado."
    is ApiError.ServerError -> "O servidor não conseguiu concluir o pedido. Tente novamente."
    is ApiError.Unknown -> "Erro inesperado. Tente novamente."
}

object AuthRuntime {
    val remoteModule: SuitUpRemoteModule by lazy { SuitUpRemoteModule() }
    val sessionManager: AuthSessionManager by lazy {
        AuthSessionManager(remoteModule.authRepository, remoteModule.tokenStore)
    }
}
