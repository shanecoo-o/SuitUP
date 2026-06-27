package com.suitup.app.ui.screens.checkout

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
import com.suitup.app.ui.components.CheckoutStepIndicator
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTextField
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitSwitch
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

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
    Column(modifier = Modifier.fillMaxSize()) {
        PremiumTopBar(title = "Checkout", onBack = onBack, onCart = onCartClick, cartBadgeCount = cartItemCount)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CheckoutStepIndicator(currentStep = 1)
            SectionHeader(
                eyebrow = "DADOS DO CLIENTE",
                title = "Como podemos contactá-lo?",
                description = "Confirme os dados associados à sua encomenda.",
            )
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    PremiumTextField(
                        value = fullName,
                        onValueChange = onFullNameChange,
                        label = "Nome completo",
                        placeholder = "João da Silva",
                        error = nameError,
                    )
                    PremiumTextField(
                        value = telefone,
                        onValueChange = onPhoneChange,
                        label = "Telefone",
                        placeholder = "+258 84 000 0000",
                        keyboardType = KeyboardType.Phone,
                        error = phoneError,
                    )
                    PremiumTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = "Email",
                        placeholder = "seu@email.com",
                        keyboardType = KeyboardType.Email,
                        error = emailError,
                    )
                }
            }
            PremiumCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Usar medidas guardadas", style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
                        Text(
                            "Preenche automaticamente os valores do seu perfil.",
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                        )
                    }
                    SuitSwitch(checked = useSavedMeasurements, onCheckedChange = onToggleMeasurements)
                }
            }
        }
        PrimaryGoldButton(
            text = "Continuar para medidas",
            onClick = onContinue,
            enabled = fullName.isNotBlank() && telefone.isNotBlank() && email.isNotBlank(),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
        )
    }
}
