package com.suitup.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitDividerWithText
import com.suitup.app.ui.components.SuitLogoStack
import com.suitup.app.ui.components.SuitSocialButton
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.icons.AppleIcon
import com.suitup.app.ui.icons.EyeIcon
import com.suitup.app.ui.icons.GoogleIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 03 — Login.
 *
 * Composable totalmente stateless. State (email, password, password visibility)
 * vem hoisted dos parâmetros. Validação e auth são responsabilidade do ScreenModel (Step 3).
 */
@Composable
fun LoginScreen(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean = false,
    emailError: String? = null,
    passwordError: String? = null,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onForgotPassword: (() -> Unit)? = null,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onAdminLogin: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        // Brand stack
        SuitLogoStack(markSize = 56.dp)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Bem-vindo de volta",
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Slate,
        )

        Spacer(Modifier.height(40.dp))

        // Form
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SuitTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "seu@email.com",
                keyboardType = KeyboardType.Email,
                error = emailError,
            )

            SuitTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Senha",
                placeholder = "••••••••",
                isPassword = !passwordVisible,
                error = passwordError,
                trailingIcon = {
                    Box(
                        modifier = Modifier.clickable(onClick = onTogglePasswordVisibility)
                    ) {
                        EyeIcon(open = passwordVisible)
                    }
                }
            )

            // Esqueceu a senha — alinhado à direita
            if (onForgotPassword != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "Esqueceu a senha?",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Gold,
                        modifier = Modifier
                            .clickable(onClick = onForgotPassword)
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        SuitButton(
            text = if (isLoading) "A entrar…" else "Entrar",
            onClick = onLogin,
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
        )

        Spacer(Modifier.height(12.dp))

        SuitButton(
            text = "Entrar como Admin",
            onClick = onAdminLogin,
            variant = SuitButtonVariant.Secondary,
        )

        Spacer(Modifier.height(24.dp))

        SuitDividerWithText("ou continue com")

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SuitSocialButton(onClick = onGoogleLogin, icon = { GoogleIcon() })
            SuitSocialButton(onClick = onAppleLogin, icon = { AppleIcon() })
        }

        Spacer(Modifier.height(60.dp))

        // Footer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Não tem conta?",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
            )
            Text(
                text = "Criar conta",
                style = SuitTextStyles.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                color = SuitColors.Gold,
                modifier = Modifier
                    .clickable(onClick = onCreateAccount)
                    .padding(4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
