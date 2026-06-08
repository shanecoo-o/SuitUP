package com.suitup.app.ui.screens.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ScreenModel do ecrã de Login.
 *
 * Valida campos localmente, expõe [navegarParaShell] como one-shot
 * que o [LoginVoyagerScreen] observa para chamar navigator.replaceAll.
 *
 * Step 4 vai substituir a lógica demo por AuthRepository.login(email, pass) → JWT.
 */
class LoginScreenModel : ScreenModel {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    // One-shot: true indica que a navegação para o shell deve acontecer.
    // Resetado após ser consumido pelo VoyagerScreen.
    private val _navegarParaShell = MutableStateFlow(false)
    val navegarParaShell: StateFlow<Boolean> = _navegarParaShell.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailAlterado -> {
                _state.update { it.copy(email = event.valor, erroEmail = null) }
            }
            is LoginUiEvent.PalavraPasseAlterada -> {
                _state.update { it.copy(palavraPasse = event.valor, erroPalavraPasse = null) }
            }
            is LoginUiEvent.AlternarVisibilidadePalavraPasse -> {
                _state.update { it.copy(palavraPasseVisivel = !it.palavraPasseVisivel) }
            }
            is LoginUiEvent.EntrarClicado -> tentarEntrar()
            is LoginUiEvent.EntrarDemoClicado -> entrarDemo()
        }
    }

    fun navegacaoConsumida() {
        _navegarParaShell.value = false
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

        // Demo: simula carregamento breve + navega.
        // Step 4: substituir por AuthRepository.login(email, pass).
        screenModelScope.launch {
            _state.update { it.copy(carregando = true) }
            kotlinx.coroutines.delay(600)
            _state.update { it.copy(carregando = false) }
            _navegarParaShell.value = true
        }
    }

    private fun entrarDemo() {
        _navegarParaShell.value = true
    }
}
