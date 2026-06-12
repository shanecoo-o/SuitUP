package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Tecido
import com.suitup.app.domain.model.CorFato
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonSize
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitStepIndicator
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.toComposeColorOrNull

/**
 * Ecrã 07 — Editor 2D · Cores e Tecidos.
 *
 * Mesma estrutura split do ecrã 06 (peças/preview), em baixo grids de cores e tecidos.
 */
@Composable
fun Editor2DColorsScreen(
    selectedPart: EditorPart,
    coresFato: List<CorFato>,
    tecidos: List<Tecido>,
    selectedColor: CorFato,
    selectedFabric: Tecido,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onPartSelect: (EditorPart) -> Unit = {},
    onColorSelect: (CorFato) -> Unit = {},
    onFabricSelect: (Tecido) -> Unit = {},
    onNext: () -> Unit = {},
) {
    val previewColor = remember(selectedColor) {
        selectedColor.hex.toComposeColorOrNull() ?: SuitColors.Ink
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
            centerContent = { SuitStepIndicator(currentStep = 3) },
        )

        // Split: peças (esquerda) + preview (direita)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            ColorPartsList(
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
                    garmentColor = previewColor,
                    background = Color.Transparent,
                    showShirt = true,
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Cores
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Cores",
                    style = SuitTextStyles.titleMedium,
                    color = SuitColors.Ink,
                )
                ColorSwatchGrid(
                    coresFato = coresFato,
                    selected = selectedColor,
                    onSelect = onColorSelect,
                )
            }

            // Tecidos
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tecidos",
                    style = SuitTextStyles.titleMedium,
                    color = SuitColors.Ink,
                )
                FabricSwatchGrid(
                    tecidos = tecidos,
                    selected = selectedFabric,
                    onSelect = onFabricSelect,
                )
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

/**
 * Lista de peças idêntica ao ecrã 06. Duplicada aqui (em vez de extraída para componente)
 * porque os dois ecrãs vão divergir no Step 3 quando cada um tiver o seu ScreenModel.
 */
@Composable
private fun ColorPartsList(
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
        Column {
            EditorPart.all().forEach { part ->
                val isSelected = part == selected
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onSelect(part) })
                        .padding(horizontal = 4.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = part.label,
                        style = if (isSelected) {
                            SuitTextStyles.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                        } else {
                            SuitTextStyles.bodyMedium
                        },
                        color = if (isSelected) SuitColors.Gold else SuitColors.Slate,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatchGrid(
    coresFato: List<CorFato>,
    selected: CorFato,
    onSelect: (CorFato) -> Unit,
) {
    // 4 colunas × N linhas (chunked)
    val linhas = coresFato.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        linhas.forEach { linha ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                linha.forEach { corFato ->
                    ColorSwatch(
                        corFato = corFato,
                        isSelected = corFato.id == selected.id,
                        onClick = { onSelect(corFato) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    corFato: CorFato,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val swatchColor = remember(corFato.hex) {
        corFato.hex.toComposeColorOrNull() ?: SuitColors.Ink
    }

    // Wrapper com border externa só quando selecionado, espaçamento interno
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) SuitColors.Gold else SuitColors.Mist,
                shape = CircleShape
            )
            .padding(if (isSelected) 3.dp else 2.dp)
            .clip(CircleShape)
            .background(swatchColor)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun FabricSwatchGrid(
    tecidos: List<Tecido>,
    selected: Tecido,
    onSelect: (Tecido) -> Unit,
) {
    val linhas = tecidos.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        linhas.forEach { linha ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                linha.forEach { tecido ->
                    FabricSwatch(
                        tecido = tecido,
                        isSelected = tecido.id == selected.id,
                        onClick = { onSelect(tecido) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FabricSwatch(
    tecido: Tecido,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val swatchColor = remember(tecido.hexAmostra) {
        tecido.hexAmostra.toComposeColorOrNull() ?: SuitColors.Charcoal
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(36.dp)
                .clip(SuitTheme.shapes.sm)
                .background(swatchColor)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) SuitColors.Gold else SuitColors.Mist,
                    shape = SuitTheme.shapes.sm
                )
                .clickable(onClick = onClick)
        )
    }
}
