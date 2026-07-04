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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTextField
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SuitDividerWithText
import com.suitup.app.ui.components.SuitLogoStack
import com.suitup.app.ui.components.SuitSocialButton
import com.suitup.app.ui.icons.AppleIcon
import com.suitup.app.ui.icons.EyeIcon
import com.suitup.app.ui.icons.GoogleIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

@Composable
fun LoginScreen(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean = false,
    emailError: String? = null,
    passwordError: String? = null,
    generalError: String? = null,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onForgotPassword: (() -> Unit)? = null,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onAdminLogin: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.InkBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))

        SuitLogoStack(
            markSize = 54.dp,
            tint = SuitColors.GoldChampagne,
        )

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Bem-vindo de volta",
                style = SuitTextStyles.headlineLarge,
                color = SuitColors.Pearl,
            )
            Text(
                text = "Entre para personalizar o seu próximo fato à medida.",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
            )
        }

        Spacer(Modifier.height(22.dp))

        PremiumCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PremiumTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    placeholder = "seu@email.com",
                    keyboardType = KeyboardType.Email,
                    error = emailError,
                )

                if (generalError != null) {
                    Text(
                        text = generalError,
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Error,
                    )
                }

                PremiumTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Palavra-passe",
                    placeholder = "••••••••",
                    isPassword = !passwordVisible,
                    error = passwordError,
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .clickable(onClick = onTogglePasswordVisibility)
                                .padding(8.dp),
                        ) {
                            EyeIcon(open = passwordVisible)
                        }
                    },
                )

                if (onForgotPassword != null) {
                    Text(
                        text = "Esqueceu a palavra-passe?",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.GoldChampagne,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable(onClick = onForgotPassword)
                            .padding(6.dp),
                    )
                }

                PrimaryGoldButton(
                    text = if (isLoading) "A entrar…" else "Entrar",
                    onClick = onLogin,
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                )

                if (onAdminLogin != null) {
                    SecondaryDarkButton(
                        text = "Entrar como Administrador",
                        onClick = onAdminLogin,
                        enabled = !isLoading,
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        SuitDividerWithText("ou continue com")

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SuitSocialButton(onClick = onGoogleLogin, icon = { GoogleIcon() })
            SuitSocialButton(onClick = onAppleLogin, icon = { AppleIcon() })
        }

        Spacer(Modifier.height(26.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Ainda não tem conta?",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
            )
            Text(
                text = "Criar conta",
                style = SuitTextStyles.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SuitColors.GoldChampagne,
                modifier = Modifier
                    .clickable(onClick = onCreateAccount)
                    .padding(6.dp),
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
