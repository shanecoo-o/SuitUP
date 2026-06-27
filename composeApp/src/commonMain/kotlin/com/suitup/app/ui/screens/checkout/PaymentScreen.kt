package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.CheckoutStepIndicator
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.StatusChip
import com.suitup.app.ui.components.StatusChipType
import com.suitup.app.ui.components.SuitUploadCard
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn

@Composable
fun PaymentScreen(
    numeroMpesa: String,
    mpesaTitleHolder: String,
    uploadedFileName: String?,
    totalMzn: Int = 0,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onCopyNumber: (() -> Unit)? = null,
    onPickFile: () -> Unit = {},
    onRemoveFile: () -> Unit = {},
    onSubmit: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PremiumTopBar(title = "Pagamento", onBack = onBack, onCart = onCartClick, cartBadgeCount = cartItemCount)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CheckoutStepIndicator(currentStep = 4)
            SectionHeader(
                eyebrow = "PAGAMENTO",
                title = "Confirme a sua encomenda",
                description = "Transfira o valor por M-Pesa e anexe o comprovativo.",
            )
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total a pagar", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    Text(
                        formatMzn(totalMzn),
                        style = SuitTextStyles.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = SuitColors.GoldChampagne,
                    )
                    StatusChip(status = StatusChipType.Pending, label = "Confirmação manual")
                }
            }
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("M-Pesa", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            numeroMpesa,
                            style = SuitTextStyles.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                            ),
                            color = SuitColors.Pearl,
                        )
                        if (onCopyNumber != null) {
                            Text(
                                "Copiar",
                                style = SuitTextStyles.labelMedium,
                                color = SuitColors.GoldChampagne,
                                modifier = Modifier.clickable(onClick = onCopyNumber).padding(8.dp),
                            )
                        }
                    }
                    Text("Titular: $mpesaTitleHolder", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Comprovativo", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                SuitUploadCard(
                    uploadedFileName = uploadedFileName,
                    onPickFile = onPickFile,
                    onRemove = onRemoveFile,
                    title = "Enviar comprovativo",
                    hint = "PNG, JPG ou PDF até 10MB",
                )
            }
            PremiumCard {
                Text(
                    "O pagamento será confirmado pelo administrador antes da produção.",
                    style = SuitTextStyles.bodyMedium,
                    color = SuitColors.Slate,
                )
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Total", style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
                Text(formatMzn(totalMzn), style = SuitTextStyles.titleLarge, color = SuitColors.GoldChampagne)
            }
            PrimaryGoldButton(
                text = "Enviar comprovativo",
                onClick = onSubmit,
                enabled = uploadedFileName != null,
            )
            SecondaryDarkButton(text = "Voltar", onClick = onBack)
        }
    }
}
