package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.TipoLapela
import com.suitup.app.domain.model.PartesFato
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonSize
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitSlider
import com.suitup.app.ui.components.SuitStepIndicator
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 06 — Editor 2D · Partes.
 *
 * Layout split: lista de peças à esquerda, preview do fato à direita.
 * Em baixo: editor da peça selecionada (opções + slider) + botão Próximo.
 *
 * Stateless: recebe estado completo via [partes], [selectedPart], [garmentColor],
 * todas as alterações via callbacks.
 */
@Composable
fun Editor2DPartsScreen(
    partes: PartesFato,
    selectedPart: EditorPart,
    garmentColor: Color,
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
        SuitTopBar(
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
            centerContent = { SuitStepIndicator(currentStep = 2) },
        )

        // Split: peças (esquerda) + preview (direita)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            PartsList(
                selected = selectedPart,
                onSelect = onPartSelect,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.32f)
                    .padding(start = 12.dp, top = 8.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.68f)
                    .padding(end = 16.dp, start = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                SuitGarmentMini(
                    size = 200.dp,
                    garmentColor = garmentColor,
                    background = Color.Transparent,
                    showShirt = true,
                )
            }
        }

        // Editor da peça selecionada
        HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (selectedPart) {
                EditorPart.Lapela -> LapelEditor(
                    selected = partes.lapela,
                    width = partes.ajusteLargura,
                    onLapelChange = onLapelChange,
                    onWidthChange = onWidthChange,
                )
                else -> PlaceholderEditor(part = selectedPart)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                SuitButton(
                    text = "Próximo",
                    onClick = onNext,
                    size = SuitButtonSize.Medium,
                    fullWidth = false,
                )
            }
        }
    }
}

@Composable
private fun PartsList(
    selected: EditorPart,
    onSelect: (EditorPart) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Peças",
            style = SuitTextStyles.eyebrow,
            color = SuitColors.Slate,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            EditorPart.all().forEach { part ->
                PartRow(
                    part = part,
                    isSelected = part == selected,
                    onClick = { onSelect(part) },
                )
            }
        }
    }
}

@Composable
private fun PartRow(
    part: EditorPart,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 7.dp)
    ) {
        Text(
            text = part.label,
            style = if (isSelected) {
                SuitTextStyles.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            } else {
                SuitTextStyles.bodyMedium
            },
            color = if (isSelected) SuitColors.Ink else SuitColors.Slate,
        )
    }
}

@Composable
private fun LapelEditor(
    selected: TipoLapela,
    width: Float,
    onLapelChange: (TipoLapela) -> Unit,
    onWidthChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Lapela",
            style = SuitTextStyles.headlineMedium,
            color = SuitColors.Ink,
        )

        // Tipo
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Tipo",
                style = SuitTextStyles.labelMedium,
                color = SuitColors.Slate,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TipoLapela.entries.forEach { type ->
                    LapelPreviewCard(
                        type = type,
                        selected = type == selected,
                        onClick = { onLapelChange(type) },
                        size = 52.dp,
                    )
                }
            }
        }

        // Largura
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Largura",
                    style = SuitTextStyles.labelMedium,
                    color = SuitColors.Slate,
                )
                Text(
                    text = widthLabel(width),
                    style = SuitTextStyles.labelMedium,
                    color = SuitColors.Ink,
                )
            }
            SuitSlider(
                value = width,
                onValueChange = onWidthChange,
            )
        }
    }
}

@Composable
private fun PlaceholderEditor(part: EditorPart) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = part.label,
            style = SuitTextStyles.headlineMedium,
            color = SuitColors.Ink,
        )
        Text(
            text = "Opções para \"${part.label}\" disponíveis em breve.",
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Slate,
        )
    }
}

private fun widthLabel(value: Float): String = when {
    value < 0.34f -> "Estreita"
    value < 0.67f -> "Média"
    else -> "Larga"
}
