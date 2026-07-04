package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.PartesFato
import com.suitup.app.domain.model.TipoLapela
import com.suitup.app.ui.components.EditorStepIndicator
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumDropdown
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitSlider
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn

@Composable
fun Editor2DPartsScreen(
    partes: PartesFato,
    selectedPart: EditorPart,
    garmentColor: Color,
    modelName: String = "",
    basePriceMzn: Int = 0,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onPartSelect: (EditorPart) -> Unit = {},
    onLapelChange: (TipoLapela) -> Unit = {},
    onWidthChange: (Float) -> Unit = {},
    onNext: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(
            title = "Editor 2D",
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                SectionHeader(
                    eyebrow = "PASSO 1 DE 3",
                    title = "Personalize os detalhes",
                    description = "Escolha cada acabamento do seu fato.",
                )
            }
            item { EditorStepIndicator(currentStep = 1) }
            item {
                ProductPreview(
                    modelName = modelName,
                    basePriceMzn = basePriceMzn,
                    garmentColor = garmentColor,
                )
            }
            item {
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Personalizar", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                        PremiumDropdown(
                            options = EditorPart.all(),
                            selectedOption = selectedPart,
                            onSelect = onPartSelect,
                            optionLabel = { it.label },
                        )
                        Text(
                            if (selectedPart == EditorPart.Lapela) {
                                "Opções de lapela disponíveis para configuração."
                            } else {
                                "Pré-visualização. A edição completa será adicionada no próximo passe."
                            },
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Smoke,
                        )
                    }
                }
            }
            item {
                if (selectedPart == EditorPart.Lapela) {
                    LapelEditor(
                        selected = partes.lapela,
                        width = partes.ajusteLargura,
                        onLapelChange = onLapelChange,
                        onWidthChange = onWidthChange,
                    )
                } else {
                    FuturePartPanel(selectedPart)
                }
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PrimaryGoldButton(text = "Continuar para cores", onClick = onNext)
            SecondaryDarkButton(text = "Voltar ao catálogo", onClick = onBack)
        }
    }
}

@Composable
private fun ProductPreview(
    modelName: String,
    basePriceMzn: Int,
    garmentColor: Color,
) {
    PremiumCard(padding = 0.dp) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(SuitColors.WarmBlack),
                contentAlignment = Alignment.Center,
            ) {
                SuitGarmentMini(
                    size = 184.dp,
                    garmentColor = garmentColor,
                    background = SuitColors.WarmBlack,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = modelName.ifBlank { "Modelo seleccionado" },
                        style = SuitTextStyles.titleLarge,
                        color = SuitColors.Pearl,
                    )
                    Text(
                        text = formatMzn(basePriceMzn),
                        style = SuitTextStyles.bodyMedium,
                        color = SuitColors.GoldChampagne,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(garmentColor, SuitTheme.shapes.sm)
                        .border(1.dp, SuitColors.Mist, SuitTheme.shapes.sm),
                )
            }
        }
    }
}

@Composable
private fun LapelEditor(
    selected: TipoLapela,
    width: Float,
    onLapelChange: (TipoLapela) -> Unit,
    onWidthChange: (Float) -> Unit,
) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Lapela", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                Text(
                    "Escolha o corte e ajuste a largura.",
                    style = SuitTextStyles.bodySmall,
                    color = SuitColors.Smoke,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TipoLapela.entries.forEach { type ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        LapelPreviewCard(
                            type = type,
                            selected = type == selected,
                            onClick = { onLapelChange(type) },
                            size = 64.dp,
                        )
                        Text(
                            text = type.label,
                            style = SuitTextStyles.labelSmall,
                            color = if (type == selected) SuitColors.GoldChampagne else SuitColors.Smoke,
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Largura", style = SuitTextStyles.labelMedium, color = SuitColors.Slate)
                    Text(
                        widthLabel(width),
                        style = SuitTextStyles.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SuitColors.GoldChampagne,
                    )
                }
                SuitSlider(value = width, onValueChange = onWidthChange)
            }
        }
    }
}

@Composable
private fun FuturePartPanel(part: EditorPart) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(part.label, style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
            Text(
                "Esta opção está preparada para uma fase posterior. A configuração actual será mantida.",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Smoke,
            )
        }
    }
}

private fun widthLabel(value: Float): String = when {
    value < 0.34f -> "Estreita"
    value < 0.67f -> "Média"
    else -> "Larga"
}
