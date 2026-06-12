package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.SuitDualBottomBar
import com.suitup.app.ui.components.SuitEyebrow
import com.suitup.app.ui.components.SuitStepIndicator
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.components.SuitUploadCard
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Ecrã 12 — Checkout · Pagamento Manual M-Pesa.
 *
 * Step 3 de 4. Mostra o número M-Pesa para transferência manual + dropzone
 * para upload do comprovativo (screenshot da transferência).
 *
 * O picker real (galeria/câmara) é injetado via [onPickFile] — expect/actual
 * será implementado no Step 5 (data layer) para Android (MediaStore) e iOS (UIImagePickerController).
 */
@Composable
fun PaymentScreen(
    numeroMpesa: String,
    mpesaTitleHolder: String,
    uploadedFileName: String?,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onCopyNumber: () -> Unit = {},
    onPickFile: () -> Unit = {},
    onRemoveFile: () -> Unit = {},
    onSubmit: () -> Unit = {},
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
            centerContent = { SuitStepIndicator(currentStep = 4, totalSteps = 5) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Pagamento Manual",
                    style = SuitTextStyles.headlineMedium,
                    color = SuitColors.Ink,
                )
                Text(
                    text = "Efetue o pagamento para o número abaixo e envie o comprovativo.",
                    style = SuitTextStyles.bodyMedium,
                    color = SuitColors.Slate,
                )
            }

            // Card destacado com número M-Pesa
            MpesaNumberCard(
                numero = numeroMpesa,
                titular = mpesaTitleHolder,
                onCopyNumber = onCopyNumber,
            )

            // Upload do comprovativo
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Upload do comprovativo",
                    style = SuitTextStyles.titleMedium,
                    color = SuitColors.Ink,
                )
                SuitUploadCard(
                    uploadedFileName = uploadedFileName,
                    onPickFile = onPickFile,
                    onRemove = onRemoveFile,
                )
            }
        }

        SuitDualBottomBar(
            primaryText = "Enviar comprovativo",
            onPrimaryClick = onSubmit,
            onSecondaryClick = onBack,
            primaryEnabled = uploadedFileName != null,
        )
    }
}

@Composable
private fun MpesaNumberCard(
    numero: String,
    titular: String,
    onCopyNumber: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SuitTheme.shapes.card)
            .background(SuitColors.Surface)
            .border(1.dp, SuitColors.Mist, SuitTheme.shapes.card)
            .padding(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SuitEyebrow("Número M-Pesa")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = numero,
                    style = SuitTextStyles.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    ),
                    color = SuitColors.Ink,
                )
                Text(
                    text = "Copiar",
                    style = SuitTextStyles.labelMedium,
                    color = SuitColors.Gold,
                    modifier = Modifier
                        .clickable(onClick = onCopyNumber)
                        .padding(8.dp),
                )
            }

            Text(
                text = "Titular: $titular",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
    }
}
