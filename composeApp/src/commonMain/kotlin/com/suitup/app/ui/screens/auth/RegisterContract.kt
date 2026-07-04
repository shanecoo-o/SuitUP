package com.suitup.app.ui.screens.auth

data class RegisterUiState(
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val palavraPasse: String = "",
    val confirmarPalavraPasse: String = "",
    val carregando: Boolean = false,
    val erroNome: String? = null,
    val erroEmail: String? = null,
    val erroPalavraPasse: String? = null,
    val erroConfirmacao: String? = null,
    val erroGeral: String? = null,
) {
    val podeRegistar: Boolean
        get() = nome.isNotBlank() && email.isNotBlank() && palavraPasse.isNotBlank() &&
            confirmarPalavraPasse.isNotBlank() && !carregando
}

sealed interface RegisterUiEvent {
    data class NomeAlterado(val valor: String) : RegisterUiEvent
    data class EmailAlterado(val valor: String) : RegisterUiEvent
    data class TelefoneAlterado(val valor: String) : RegisterUiEvent
    data class PalavraPasseAlterada(val valor: String) : RegisterUiEvent
    data class ConfirmacaoAlterada(val valor: String) : RegisterUiEvent
    data object RegistarClicado : RegisterUiEvent
}
