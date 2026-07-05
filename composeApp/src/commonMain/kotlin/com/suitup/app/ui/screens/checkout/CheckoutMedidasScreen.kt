package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Medidas
import com.suitup.app.ui.components.CheckoutStepIndicator
import com.suitup.app.ui.components.SuitAlertBanner
import com.suitup.app.ui.components.SuitAlertVariant
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDetailTopBar
import com.suitup.app.ui.components.SuitEyebrow
import com.suitup.app.ui.components.SuitFixedCtaBar
import com.suitup.app.ui.components.SuitFormFlowScaffold
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 18 — Checkout · Medidas do Cliente.
 *
 * Inserido entre Dados do Cliente (step 1) e Tipo de Entrega (step 3).
 * Recolhe as medidas necessárias para produção do fato sob medida.
 *
 * Campos obrigatórios: alturaCm, ombrosCm, peitoCm, cinturaCm, mangaCm, calcaCm.
 * Campos opcionais: pesoKg, quadrilCm, casacoCm, pescocoCm, observacoes.
 */
@Composable
fun CheckoutMedidasScreen(
    medidas: Medidas,
    erro: String? = null,
    podeContinuar: Boolean = false,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onAlturaChange: (String) -> Unit = {},
    onPesoChange: (String) -> Unit = {},
    onOmbrosChange: (String) -> Unit = {},
    onPeitoChange: (String) -> Unit = {},
    onCinturaChange: (String) -> Unit = {},
    onQuadrilChange: (String) -> Unit = {},
    onMangaChange: (String) -> Unit = {},
    onCalcaChange: (String) -> Unit = {},
    onCasacoChange: (String) -> Unit = {},
    onPescocoChange: (String) -> Unit = {},
    onObservacoesChange: (String) -> Unit = {},
    onContinue: () -> Unit = {},
) {
    SuitFormFlowScaffold(
        topBar = {
            SuitDetailTopBar(onBack = onBack, title = "Checkout", onCart = onCartClick, cartBadgeCount = cartItemCount)
        },
        fixedCta = {
            SuitFixedCtaBar {
                SuitButton(text = "Continuar para entrega", onClick = onContinue, enabled = podeContinuar)
            }
        },
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CheckoutStepIndicator(currentStep = 2)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Medidas do Cliente",
                    style = SuitTextStyles.headlineMedium,
                    color = SuitColors.Ink,
                )
                Text(
                    text = "Informe as medidas necessárias para produzir o fato sob medida.",
                    style = SuitTextStyles.bodyMedium,
                    color = SuitColors.Slate,
                )
            }

            MedidasGrupo(titulo = "Medidas gerais") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MedidaField(
                        label = "Altura *",
                        valor = medidas.alturaCm,
                        unidade = "cm",
                        onValueChange = onAlturaChange,
                        modifier = Modifier.weight(1f),
                    )
                    MedidaField(
                        label = "Peso",
                        valor = medidas.pesoKg,
                        unidade = "kg",
                        onValueChange = onPesoChange,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            MedidasGrupo(titulo = "Tronco") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MedidaField(
                        label = "Ombros *",
                        valor = medidas.ombrosCm,
                        unidade = "cm",
                        onValueChange = onOmbrosChange,
                        modifier = Modifier.weight(1f),
                    )
                    MedidaField(
                        label = "Peito *",
                        valor = medidas.peitoCm,
                        unidade = "cm",
                        onValueChange = onPeitoChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MedidaField(
                        label = "Cintura *",
                        valor = medidas.cinturaCm,
                        unidade = "cm",
                        onValueChange = onCinturaChange,
                        modifier = Modifier.weight(1f),
                    )
                    MedidaField(
                        label = "Quadril",
                        valor = medidas.quadrilCm,
                        unidade = "cm",
                        onValueChange = onQuadrilChange,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            MedidasGrupo(titulo = "Comprimentos") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MedidaField(
                        label = "Manga *",
                        valor = medidas.mangaCm,
                        unidade = "cm",
                        onValueChange = onMangaChange,
                        modifier = Modifier.weight(1f),
                    )
                    MedidaField(
                        label = "Calça *",
                        valor = medidas.calcaCm,
                        unidade = "cm",
                        onValueChange = onCalcaChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MedidaField(
                        label = "Casaco",
                        valor = medidas.casacoCm,
                        unidade = "cm",
                        onValueChange = onCasacoChange,
                        modifier = Modifier.weight(1f),
                    )
                    MedidaField(
                        label = "Pescoço",
                        valor = medidas.pescocoCm,
                        unidade = "cm",
                        onValueChange = onPescocoChange,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            MedidasGrupo(titulo = "Observações") {
                SuitTextField(
                    value = medidas.observacoes,
                    onValueChange = onObservacoesChange,
                    label = "Observações adicionais",
                    placeholder = "Ex: prefiro corte mais largo na cintura",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (erro != null) {
                SuitAlertBanner(variant = SuitAlertVariant.Error, message = erro)
            }

            Text(
                text = "(*) Campo obrigatório. As medidas são usadas apenas para preparar a encomenda.",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
    }
}

@Composable
private fun MedidasGrupo(
    titulo: String,
    content: @Composable () -> Unit,
) {
    SuitCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SuitEyebrow(titulo)
            content()
        }
    }
}

@Composable
private fun MedidaField(
    label: String,
    valor: String,
    unidade: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SuitTextField(
        value = valor,
        onValueChange = onValueChange,
        label = label,
        placeholder = "0",
        trailingIcon = {
            Text(
                text = unidade,
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        },
        modifier = modifier,
        keyboardType = KeyboardType.Number,
    )
}
