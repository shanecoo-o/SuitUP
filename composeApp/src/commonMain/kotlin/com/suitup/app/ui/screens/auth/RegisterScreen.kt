package com.suitup.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTextField
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onEvent: (RegisterUiEvent) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.InkBlack),
    ) {
        PremiumTopBar(title = "Criar conta", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("A sua conta SuitUP", style = SuitTextStyles.headlineLarge, color = SuitColors.Pearl)
                Text(
                    "Registe-se para guardar a sua sessão e continuar a personalização.",
                    style = SuitTextStyles.bodyMedium,
                    color = SuitColors.Slate,
                )
            }

            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PremiumTextField(
                        value = state.nome,
                        onValueChange = { onEvent(RegisterUiEvent.NomeAlterado(it)) },
                        label = "Nome completo",
                        error = state.erroNome,
                        enabled = !state.carregando,
                    )
                    PremiumTextField(
                        value = state.email,
                        onValueChange = { onEvent(RegisterUiEvent.EmailAlterado(it)) },
                        label = "Email",
                        placeholder = "seu@email.com",
                        keyboardType = KeyboardType.Email,
                        error = state.erroEmail,
                        enabled = !state.carregando,
                    )
                    PremiumTextField(
                        value = state.telefone,
                        onValueChange = { onEvent(RegisterUiEvent.TelefoneAlterado(it)) },
                        label = "Telefone (opcional)",
                        placeholder = "+258 84 000 0000",
                        keyboardType = KeyboardType.Phone,
                        enabled = !state.carregando,
                    )
                    PremiumTextField(
                        value = state.palavraPasse,
                        onValueChange = { onEvent(RegisterUiEvent.PalavraPasseAlterada(it)) },
                        label = "Palavra-passe",
                        isPassword = true,
                        error = state.erroPalavraPasse,
                        enabled = !state.carregando,
                    )
                    PremiumTextField(
                        value = state.confirmarPalavraPasse,
                        onValueChange = { onEvent(RegisterUiEvent.ConfirmacaoAlterada(it)) },
                        label = "Confirmar palavra-passe",
                        isPassword = true,
                        error = state.erroConfirmacao,
                        enabled = !state.carregando,
                    )
                    if (state.erroGeral != null) {
                        Text(
                            text = state.erroGeral,
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Error,
                        )
                    }
                    PrimaryGoldButton(
                        text = if (state.carregando) "A criar conta…" else "Criar conta",
                        onClick = { onEvent(RegisterUiEvent.RegistarClicado) },
                        enabled = state.podeRegistar,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
