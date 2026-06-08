package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.suitup.app.ui.components.SuitStepIndicator
import com.suitup.app.ui.components.SuitSwitch
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 09 — Checkout · Dados do Cliente.
 *
 * Step 1 de 4 do flow de checkout. Form pré-preenchido com dados do user
 * autenticado, opcional toggle "Usar medidas salvas".
 */
@Composable
fun CheckoutScreen(
    fullName: String,
    telefone: String,
    email: String,
    useSavedMeasurements: Boolean,
    cartItemCount: Int = 0,
    nameError: String? = null,
    phoneError: String? = null,
    emailError: String? = null,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onFullNameChange: (String) -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onToggleMeasurements: (Boolean) -> Unit = {},
    onContinue: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
            centerContent = { SuitStepIndicator(currentStep = 1, totalSteps = 5) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Dados do Cliente",
                style = SuitTextStyles.headlineMedium,
                color = SuitColors.Ink,
            )

            // Form
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SuitTextField(
                    value = fullName,
                    onValueChange = onFullNameChange,
                    label = "Nome completo",
                    placeholder = "Ex: João da Silva",
                    error = nameError,
                )
                SuitTextField(
                    value = telefone,
                    onValueChange = onPhoneChange,
                    label = "Telefone",
                    placeholder = "+258 84 000 0000",
                    keyboardType = KeyboardType.Phone,
                    error = phoneError,
                )
                SuitTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    placeholder = "seu@email.com",
                    keyboardType = KeyboardType.Email,
                    error = emailError,
                )
            }

            // Medidas section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Medidas",
                    style = SuitTextStyles.titleMedium,
                    color = SuitColors.Ink,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Usar medidas salvas",
                        style = SuitTextStyles.bodyMedium,
                        color = SuitColors.Slate,
                    )
                    SuitSwitch(
                        checked = useSavedMeasurements,
                        onCheckedChange = onToggleMeasurements,
                    )
                }
            }
        }

        // Bottom CTA fixed
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            SuitButton(
                text = "Continuar",
                onClick = onContinue,
                enabled = fullName.isNotBlank() && telefone.isNotBlank() && email.isNotBlank(),
            )
        }
    }
}
