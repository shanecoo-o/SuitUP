package com.suitup.app.ui.screens.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.remote.http.ApiResult
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.data.session.AuthSessionManager
import com.suitup.app.data.session.authErrorMessage
import com.suitup.app.domain.model.AppUserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterScreenModel(
    private val sessionManager: AuthSessionManager = AuthRuntime.sessionManager,
) : ScreenModel {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    private val _navigationTarget = MutableStateFlow<AuthNavigationTarget?>(null)
    val navigationTarget: StateFlow<AuthNavigationTarget?> = _navigationTarget.asStateFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.NomeAlterado -> _state.update {
                it.copy(nome = event.valor, erroNome = null, erroGeral = null)
            }
            is RegisterUiEvent.EmailAlterado -> _state.update {
                it.copy(email = event.valor, erroEmail = null, erroGeral = null)
            }
            is RegisterUiEvent.TelefoneAlterado -> _state.update {
                it.copy(telefone = event.valor, erroGeral = null)
            }
            is RegisterUiEvent.PalavraPasseAlterada -> _state.update {
                it.copy(palavraPasse = event.valor, erroPalavraPasse = null, erroGeral = null)
            }
            is RegisterUiEvent.ConfirmacaoAlterada -> _state.update {
                it.copy(confirmarPalavraPasse = event.valor, erroConfirmacao = null, erroGeral = null)
            }
            RegisterUiEvent.RegistarClicado -> registar()
        }
    }

    fun navegacaoConsumida() {
        _navigationTarget.value = null
    }

    private fun registar() {
        val current = _state.value
        if (current.carregando) return

        val nomeError = if (current.nome.trim().length < 2) "Nome obrigatório" else null
        val emailError = if (!current.email.contains("@")) "Email inválido" else null
        val passwordError = if (current.palavraPasse.length < 8) "Mínimo 8 caracteres" else null
        val confirmationError = if (current.confirmarPalavraPasse != current.palavraPasse) {
            "As palavras-passe não coincidem"
        } else null

        _state.update {
            it.copy(
                erroNome = nomeError,
                erroEmail = emailError,
                erroPalavraPasse = passwordError,
                erroConfirmacao = confirmationError,
            )
        }
        if (listOf(nomeError, emailError, passwordError, confirmationError).any { it != null }) return

        screenModelScope.launch {
            _state.update { it.copy(carregando = true, erroGeral = null) }
            when (
                val result = sessionManager.register(
                    fullName = current.nome,
                    email = current.email,
                    password = current.palavraPasse,
                    phone = current.telefone,
                )
            ) {
                is ApiResult.Success -> {
                    _state.update { it.copy(carregando = false) }
                    _navigationTarget.value = if (result.value.primaryRole == AppUserRole.ADMIN) {
                        AuthNavigationTarget.AdminDashboard
                    } else {
                        AuthNavigationTarget.CustomerHome
                    }
                }
                is ApiResult.Failure -> _state.update {
                    it.copy(carregando = false, erroGeral = authErrorMessage(result.error))
                }
            }
        }
    }
}
