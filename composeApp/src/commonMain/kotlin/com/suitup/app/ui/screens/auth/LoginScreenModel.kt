package com.suitup.app.ui.screens.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.data.session.AuthSessionManager
import com.suitup.app.data.session.authErrorMessage
import com.suitup.app.domain.model.AppUserRole
import com.suitup.app.domain.model.AuthSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ScreenModel do ecrã de Login.
 *
 * Valida os campos, autentica no backend e expõe o destino derivado
 * do papel ADMIN ou CUSTOMER devolvido pelo servidor.
 */
class LoginScreenModel(
    private val sessionManager: AuthSessionManager = AuthRuntime.sessionManager,
) : ScreenModel {

    private val _state = MutableStateFlow(
        LoginUiState(
            erroGeral = (sessionManager.state.value as? AuthSessionState.Unauthenticated)?.message,
        )
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    // One-shot: true indica que a navegação para o shell deve acontecer.
    // Resetado após ser consumido pelo VoyagerScreen.
    private val _navigationTarget = MutableStateFlow<AuthNavigationTarget?>(null)
    val navigationTarget: StateFlow<AuthNavigationTarget?> = _navigationTarget.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailAlterado -> {
                _state.update { it.copy(email = event.valor, erroEmail = null, erroGeral = null) }
            }
            is LoginUiEvent.PalavraPasseAlterada -> {
                _state.update { it.copy(palavraPasse = event.valor, erroPalavraPasse = null, erroGeral = null) }
            }
            is LoginUiEvent.AlternarVisibilidadePalavraPasse -> {
                _state.update { it.copy(palavraPasseVisivel = !it.palavraPasseVisivel) }
            }
            is LoginUiEvent.EntrarClicado -> tentarEntrar()
        }
    }

    fun navegacaoConsumida() {
        _navigationTarget.value = null
    }

    private fun tentarEntrar() {
        val s = _state.value
        var valido = true

        val erroEmail = if (s.email.isBlank()) {
            valido = false; "Email obrigatório"
        } else if (!s.email.contains("@")) {
            valido = false; "Email inválido"
        } else null

        val erroPasse = if (s.palavraPasse.isBlank()) {
            valido = false; "Palavra-passe obrigatória"
        } else if (s.palavraPasse.length < 4) {
            valido = false; "Mínimo 4 caracteres"
        } else null

        _state.update { it.copy(erroEmail = erroEmail, erroPalavraPasse = erroPasse) }

        if (!valido) return

        screenModelScope.launch {
            _state.update { it.copy(carregando = true, erroGeral = null) }
            when (val result = sessionManager.login(s.email, s.palavraPasse)) {
                is ApiResult.Success -> {
                    _state.update { it.copy(carregando = false) }
                    _navigationTarget.value = if (result.value.primaryRole == AppUserRole.ADMIN) {
                        AuthNavigationTarget.AdminDashboard
                    } else {
                        AuthNavigationTarget.CustomerHome
                    }
                }
                is ApiResult.Failure -> {
                    _state.update {
                        it.copy(
                            carregando = false,
                            erroGeral = authErrorMessage(result.error),
                        )
                    }
                }
            }
        }
    }
}
