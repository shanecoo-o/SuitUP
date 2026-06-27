package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.CustomizationSummaryCard
import com.suitup.app.ui.components.EditorStepIndicator
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.Suit3DControlButton
import com.suitup.app.ui.icons.BulbIcon
import com.suitup.app.ui.icons.PictureIcon
import com.suitup.app.ui.icons.RotateIcon
import com.suitup.app.ui.icons.ZoomIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

@Composable
fun Preview3DScreen(
    state: Preview3DState,
    garmentColor: Color,
    modelName: String = "",
    estimatedPriceMzn: Int = 0,
    configurationDetails: List<String> = emptyList(),
    showLight: Boolean = false,
    backgroundDark: Boolean = true,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onStateChange: (Preview3DState) -> Unit = {},
    onRotate: () -> Unit = {},
    onZoom: () -> Unit = {},
    onToggleLight: () -> Unit = {},
    onToggleBackground: () -> Unit = {},
    onEditAgain: () -> Unit = {},
    onOrder: () -> Unit = {},
) {
    val summaryDetails = configurationDetails.mapNotNull { detail ->
        val parts = detail.split(":", limit = 2)
        if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        PremiumTopBar(
            title = "Preview 3D",
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                SectionHeader(
                    eyebrow = "PASSO 3 DE 3",
                    title = "Veja o resultado",
                    description = "Confirme a configuração antes de encomendar.",
                )
            }
            item { EditorStepIndicator(currentStep = 3) }
            item {
                PreviewHero(
                    state = state,
                    garmentColor = garmentColor,
                    showLight = showLight,
                    backgroundDark = backgroundDark,
                    onStateChange = onStateChange,
                )
            }
            item {
                PreviewControls(
                    onRotate = onRotate,
                    onZoom = onZoom,
                    onToggleLight = onToggleLight,
                    onToggleBackground = onToggleBackground,
                )
            }
            item {
                CustomizationSummaryCard(
                    modelName = modelName.ifBlank { "Fato personalizado" },
                    details = summaryDetails,
                    totalMzn = estimatedPriceMzn,
                )
            }
            item {
                PremiumCard {
                    Text(
                        text = "A pré-visualização é demonstrativa. A equipa de alfaiataria confirma os detalhes antes da produção.",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Slate,
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PrimaryGoldButton(text = "Adicionar ao carrinho", onClick = onOrder)
            SecondaryDarkButton(text = "Editar novamente", onClick = onEditAgain)
        }
    }
}

@Composable
private fun PreviewHero(
    state: Preview3DState,
    garmentColor: Color,
    showLight: Boolean,
    backgroundDark: Boolean,
    onStateChange: (Preview3DState) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(SuitColors.WarmBlack),
    ) {
        ShowroomBackground(
            modifier = Modifier.fillMaxSize(),
            bright = showLight,
            lightBackground = !backgroundDark,
        )
        Suit3DPreview(
            state = state,
            onStateChange = onStateChange,
            garmentColor = garmentColor,
            showLight = showLight,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 18.dp),
        )
        Text(
            text = "PRÉ-VISUALIZAÇÃO INTERACTIVA",
            style = SuitTextStyles.eyebrow,
            color = SuitColors.GoldChampagne,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
        )
    }
}

@Composable
private fun PreviewControls(
    onRotate: () -> Unit,
    onZoom: () -> Unit,
    onToggleLight: () -> Unit,
    onToggleBackground: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Controlos", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top,
        ) {
            Suit3DControlButton(
                label = "Girar",
                onClick = onRotate,
                icon = { RotateIcon(tint = SuitColors.Gold, size = 20.dp) },
            )
            Suit3DControlButton(
                label = "Zoom",
                onClick = onZoom,
                icon = { ZoomIcon(tint = SuitColors.Gold, size = 20.dp) },
            )
            Suit3DControlButton(
                label = "Luz",
                onClick = onToggleLight,
                icon = { BulbIcon(tint = SuitColors.Gold, size = 20.dp) },
            )
            Suit3DControlButton(
                label = "Fundo",
                onClick = onToggleBackground,
                icon = { PictureIcon(tint = SuitColors.Gold, size = 20.dp) },
            )
        }
    }
}

@Composable
private fun ShowroomBackground(
    modifier: Modifier = Modifier,
    bright: Boolean = false,
    lightBackground: Boolean = false,
) {
    val baseColor = when {
        lightBackground -> SuitColors.SurfaceHigh
        bright -> SuitColors.Charcoal
        else -> SuitColors.WarmBlack
    }

    Box(modifier = modifier.background(baseColor)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val ghostAlpha = if (lightBackground) 0.16f else 0.12f
            val rodAlpha = if (lightBackground) 0.24f else 0.20f
            val ghostColor = SuitColors.Smoke.copy(alpha = ghostAlpha)
            val rodColor = SuitColors.BronzeSubtle.copy(alpha = rodAlpha)

            drawRect(
                color = rodColor,
                topLeft = Offset(0f, h * 0.18f),
                size = Size(w, 1.5f),
            )

            listOf(0.08f, 0.22f, 0.78f, 0.92f).forEach { position ->
                val centerX = w * position
                val ghostWidth = w * 0.09f
                val top = h * 0.18f
                val bottom = h * 0.62f
                val ghost = Path().apply {
                    moveTo(centerX - ghostWidth * 0.4f, top)
                    lineTo(centerX + ghostWidth * 0.4f, top)
                    lineTo(centerX + ghostWidth * 0.7f, bottom)
                    lineTo(centerX - ghostWidth * 0.7f, bottom)
                    close()
                }
                drawPath(ghost, ghostColor)
            }

            val pedestalY = h * 0.88f
            val pedestalWidth = w * 0.58f
            drawOval(
                color = Color.Black.copy(alpha = 0.28f),
                topLeft = Offset((w - pedestalWidth) / 2f, pedestalY),
                size = Size(pedestalWidth, h * 0.05f),
            )
            drawOval(
                color = SuitColors.BronzeSubtle.copy(alpha = 0.18f),
                topLeft = Offset((w - pedestalWidth * 0.88f) / 2f, pedestalY - 2f),
                size = Size(pedestalWidth * 0.88f, h * 0.018f),
            )
        }
    }
}
