package com.suitup.app.ui.screens.auth

data class LoginUiState(
    val email: String = "",
    val palavraPasse: String = "",
    val palavraPasseVisivel: Boolean = false,
    val carregando: Boolean = false,
    val erroEmail: String? = null,
    val erroPalavraPasse: String? = null,
    val erroGeral: String? = null,
) {
    val podeEntrar: Boolean
        get() = email.isNotBlank() && palavraPasse.isNotBlank() && !carregando
}

sealed class LoginUiEvent {
    data class EmailAlterado(val valor: String) : LoginUiEvent()
    data class PalavraPasseAlterada(val valor: String) : LoginUiEvent()
    data object AlternarVisibilidadePalavraPasse : LoginUiEvent()
    data object EntrarClicado : LoginUiEvent()
}
