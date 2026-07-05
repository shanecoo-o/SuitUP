package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.ui.components.CheckoutStepIndicator
import com.suitup.app.ui.components.StatusChip
import com.suitup.app.ui.components.StatusChipType
import com.suitup.app.ui.components.SuitAlertBanner
import com.suitup.app.ui.components.SuitAlertVariant
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonSize
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDetailTopBar
import com.suitup.app.ui.components.SuitEyebrow
import com.suitup.app.ui.components.SuitFixedCtaBar
import com.suitup.app.ui.components.SuitFormFlowScaffold
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.components.SuitUploadCard
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.util.formatMzn

/**
 * Ecrã 12 — Checkout · Pagamento M-Pesa (Fase 9.6B).
 *
 * Submissão real do pagamento + upload real do comprovativo, usando o
 * [PagamentoScreenModel]/[com.suitup.app.data.payment.PaymentTrackingRepository]
 * já existentes. Este ecrã apenas apresenta o estado real devolvido pelo
 * backend — nunca assume sucesso antes da resposta remota.
 *
 * Hierarquia de estado (Task 17): A) por enviar · B) pendente sem comprovativo ·
 * C) pendente com comprovativo · D) confirmado · E) rejeitado. Os dois últimos
 * são geridos pelo administrador (fora do âmbito deste ecrã) mas são
 * apresentados honestamente caso o pedido já os tenha ao entrar neste ecrã.
 */
@Composable
fun PaymentScreen(
    numeroMpesa: String,
    mpesaTitleHolder: String,
    uploadedFileName: String?,
    uploadedFileType: String? = null,
    uploadedFileSizeBytes: Long? = null,
    paymentReference: String = "",
    paymentStatus: PaymentStatus? = null,
    isSubmitting: Boolean = false,
    successMessage: String? = null,
    errorMessage: String? = null,
    paymentSubmitted: Boolean = false,
    proofUploaded: Boolean = false,
    showDemoFallback: Boolean = false,
    totalMzn: Int = 0,
    orderReference: String = "",
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onCopyNumber: (() -> Unit)? = null,
    onPickFile: () -> Unit = {},
    onRemoveFile: () -> Unit = {},
    onPaymentReferenceChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onContinueDemo: () -> Unit = {},
) {
    val stage = resolvePaymentStage(paymentSubmitted, proofUploaded, paymentStatus)
    val canSubmit = uploadedFileName != null && paymentReference.isNotBlank() && !isSubmitting

    SuitFormFlowScaffold(
        topBar = {
            SuitDetailTopBar(onBack = onBack, title = "Pagamento", onCart = onCartClick, cartBadgeCount = cartItemCount)
        },
        fixedCta = {
            SuitFixedCtaBar {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Total", style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
                        Text(formatMzn(totalMzn), style = SuitTextStyles.titleLarge, color = SuitColors.GoldChampagne)
                    }
                    SuitButton(
                        text = when {
                            isSubmitting -> "A processar..."
                            paymentSubmitted && errorMessage != null -> "Tentar enviar comprovativo"
                            paymentSubmitted -> "Enviar comprovativo"
                            else -> "Submeter pagamento"
                        },
                        onClick = onSubmit,
                        enabled = canSubmit,
                        loading = isSubmitting,
                    )
                    SuitButton(
                        text = "Voltar",
                        onClick = onBack,
                        variant = SuitButtonVariant.Secondary,
                        enabled = !isSubmitting,
                    )
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CheckoutStepIndicator(currentStep = 4)
            SuitEyebrow(text = "Pagamento")
            Text("Confirme a sua encomenda", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
            Text(
                "Transfira o valor por M-Pesa e anexe o comprovativo.",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )

            // Secção 1 — Resumo do pedido/valor (Task 3).
            SuitCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Total a pagar", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                        StatusChip(status = stage.chip, label = stage.chipLabel)
                    }
                    Text(
                        formatMzn(totalMzn),
                        style = SuitTextStyles.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = SuitColors.GoldChampagne,
                    )
                    if (orderReference.isNotBlank()) {
                        Text("Pedido #$orderReference", style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                    }
                    Text(stage.guidance, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                }
            }

            // Secção 2 — Método de pagamento (Task 4: único método real, sem opções inventadas).
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SuitEyebrow(text = "Método de pagamento")
                SuitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("M-Pesa", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
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
                                color = SuitColors.Ink,
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
            }

            // Secção 3 — Referência da transacção (Task 5).
            SuitTextField(
                value = paymentReference,
                onValueChange = onPaymentReferenceChange,
                label = "Referência da transacção",
                placeholder = "MPESA-TEST-123456",
                helper = "Código de confirmação enviado pelo M-Pesa após a transferência.",
                enabled = !paymentSubmitted && !isSubmitting,
            )

            // Secção 4 — Estado da submissão (Task 8, 9, 16: erro/sucesso honestos, sem fallback genérico).
            if (errorMessage != null) {
                SuitAlertBanner(variant = SuitAlertVariant.Error, message = errorMessage)
            } else if (successMessage != null) {
                SuitAlertBanner(variant = SuitAlertVariant.Success, message = successMessage)
            }
            if (showDemoFallback) {
                Text(
                    "O modo demo não envia o pagamento nem o ficheiro ao servidor.",
                    style = SuitTextStyles.bodySmall,
                    color = SuitColors.Slate,
                )
                SuitButton(
                    text = "Continuar em modo demo",
                    onClick = onContinueDemo,
                    enabled = !isSubmitting,
                    variant = SuitButtonVariant.Secondary,
                    size = SuitButtonSize.Small,
                    fullWidth = false,
                )
            }

            // Secção 5 — Comprovativo (Task 10-16).
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Comprovativo", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                SuitUploadCard(
                    uploadedFileName = uploadedFileName,
                    onPickFile = onPickFile,
                    onRemove = onRemoveFile,
                    title = "Enviar comprovativo",
                    hint = "PNG, JPG ou PDF até 10MB",
                )
                if (uploadedFileName != null) {
                    Text(
                        formatProofMeta(uploadedFileType, uploadedFileSizeBytes),
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Slate,
                    )
                }
            }

            SuitCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "O pagamento será confirmado pelo administrador antes da produção.",
                    style = SuitTextStyles.bodyMedium,
                    color = SuitColors.Slate,
                )
            }
        }
    }
}

private data class PaymentStageInfo(
    val chip: StatusChipType,
    val chipLabel: String,
    val guidance: String,
)

private fun resolvePaymentStage(
    submitted: Boolean,
    proofUploaded: Boolean,
    status: PaymentStatus?,
): PaymentStageInfo = when {
    status == PaymentStatus.CONFIRMED -> PaymentStageInfo(
        chip = StatusChipType.Confirmed,
        chipLabel = "Confirmado",
        guidance = "Pagamento confirmado pelo administrador. O pedido pode avançar.",
    )
    status == PaymentStatus.REJECTED -> PaymentStageInfo(
        chip = StatusChipType.Rejected,
        chipLabel = "Rejeitado",
        guidance = "Pagamento rejeitado. Reveja os dados ou contacte o suporte.",
    )
    submitted && proofUploaded -> PaymentStageInfo(
        chip = StatusChipType.Pending,
        chipLabel = "Pendente",
        guidance = "Comprovativo enviado. Aguardando confirmação do administrador.",
    )
    submitted -> PaymentStageInfo(
        chip = StatusChipType.Pending,
        chipLabel = "Pendente",
        guidance = "Pagamento submetido. Envie o comprovativo para continuar.",
    )
    else -> PaymentStageInfo(
        chip = StatusChipType.Inactive,
        chipLabel = "Por enviar",
        guidance = "Preencha a referência e anexe o comprovativo para submeter o pagamento.",
    )
}

private fun formatProofMeta(contentType: String?, sizeBytes: Long?): String {
    val typeLabel = when (contentType) {
        "image/png" -> "PNG"
        "image/jpeg" -> "JPEG"
        "application/pdf" -> "PDF"
        else -> contentType?.substringAfterLast('/')?.uppercase() ?: "Ficheiro"
    }
    val sizeLabel = sizeBytes?.let(::formatFileSize)
    return if (sizeLabel != null) "$typeLabel · $sizeLabel" else typeLabel
}

private fun formatFileSize(bytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024
    return when {
        bytes >= mb -> {
            val tenths = (bytes / mb * 10).toLong()
            "${tenths / 10}.${tenths % 10} MB"
        }
        bytes >= kb -> "${(bytes / kb).toLong()} KB"
        else -> "$bytes B"
    }
}
