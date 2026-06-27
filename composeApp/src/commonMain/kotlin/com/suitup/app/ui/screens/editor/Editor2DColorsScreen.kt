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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.CorFato
import com.suitup.app.domain.model.Tecido
import com.suitup.app.ui.components.ColorSwatch
import com.suitup.app.ui.components.EditorStepIndicator
import com.suitup.app.ui.components.FabricSwatch
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumDropdown
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.toComposeColorOrNull

@Composable
fun Editor2DColorsScreen(
    selectedPart: EditorPart,
    coresFato: List<CorFato>,
    tecidos: List<Tecido>,
    selectedColor: CorFato,
    selectedFabric: Tecido,
    modelName: String = "",
    basePriceMzn: Int = 0,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onPartSelect: (EditorPart) -> Unit = {},
    onColorSelect: (CorFato) -> Unit = {},
    onFabricSelect: (Tecido) -> Unit = {},
    onNext: () -> Unit = {},
) {
    val previewColor = remember(selectedColor.hex) {
        selectedColor.hex.toComposeColorOrNull() ?: SuitColors.Ink
    }

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
                    eyebrow = "PASSO 2 DE 3",
                    title = "Cores e tecidos",
                    description = "Combine a tonalidade e o tecido do seu fato.",
                )
            }
            item { EditorStepIndicator(currentStep = 2) }
            item {
                ColorProductPreview(
                    modelName = modelName,
                    previewColor = previewColor,
                    colorName = selectedColor.nome,
                    fabricName = selectedFabric.nome,
                )
            }
            item {
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Aplicar em", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                        PremiumDropdown(
                            options = EditorPart.all(),
                            selectedOption = selectedPart,
                            onSelect = onPartSelect,
                            optionLabel = { it.label },
                        )
                    }
                }
            }
            item {
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Paleta de cores", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                            Text(
                                selectedColor.nome,
                                style = SuitTextStyles.bodySmall,
                                color = SuitColors.GoldChampagne,
                            )
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(coresFato, key = { it.id }) { color ->
                                ColorSwatch(
                                    color = color.hex.toComposeColorOrNull() ?: SuitColors.Ink,
                                    selected = color.id == selectedColor.id,
                                    label = color.nome,
                                    onClick = { onColorSelect(color) },
                                )
                            }
                        }
                    }
                }
            }
            item {
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Selecção de tecido", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
                            Text(
                                selectedFabric.nome,
                                style = SuitTextStyles.bodySmall,
                                color = SuitColors.GoldChampagne,
                            )
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(tecidos, key = { it.id }) { fabric ->
                                FabricSwatch(
                                    color = fabric.hexAmostra.toComposeColorOrNull() ?: SuitColors.Charcoal,
                                    label = fabric.nome,
                                    selected = fabric.id == selectedFabric.id,
                                    onClick = { onFabricSelect(fabric) },
                                )
                            }
                        }
                    }
                }
            }
            item { PriceSummary(basePriceMzn) }
        }
        PrimaryGoldButton(
            text = "Ver Preview 3D",
            onClick = onNext,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun ColorProductPreview(
    modelName: String,
    previewColor: Color,
    colorName: String,
    fabricName: String,
) {
    PremiumCard(padding = 0.dp) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(SuitColors.WarmBlack),
                contentAlignment = Alignment.Center,
            ) {
                SuitGarmentMini(
                    size = 174.dp,
                    garmentColor = previewColor,
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
                        modelName.ifBlank { "Modelo seleccionado" },
                        style = SuitTextStyles.titleMedium,
                        color = SuitColors.Pearl,
                    )
                    Text(
                        "$colorName · $fabricName",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Smoke,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(previewColor, SuitTheme.shapes.sm)
                        .border(1.dp, SuitColors.Gold, SuitTheme.shapes.sm),
                )
            }
        }
    }
}

@Composable
private fun PriceSummary(basePriceMzn: Int) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Estimativa", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
            PriceLine("Preço base", formatMzn(basePriceMzn))
            PriceLine("Personalização", "Incluída")
            HorizontalDivider(color = SuitColors.Mist)
            PriceLine(
                label = "Total estimado",
                value = formatMzn(basePriceMzn),
                emphasized = true,
            )
        }
    }
}

@Composable
private fun PriceLine(label: String, value: String, emphasized: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = if (emphasized) SuitTextStyles.titleMedium else SuitTextStyles.bodyMedium,
            color = if (emphasized) SuitColors.Pearl else SuitColors.Slate,
        )
        Text(
            value,
            style = if (emphasized) {
                SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.Bold)
            } else {
                SuitTextStyles.bodyMedium
            },
            color = if (emphasized) SuitColors.GoldChampagne else SuitColors.Pearl,
        )
    }
}
