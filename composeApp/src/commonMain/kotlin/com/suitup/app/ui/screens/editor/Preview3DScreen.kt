package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.CustomizationSummaryCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SuitImmersiveScaffold
import com.suitup.app.ui.components.SuitImmersiveTopBar
import com.suitup.app.ui.components.Suit3DControlButton
import com.suitup.app.ui.icons.BulbIcon
import com.suitup.app.ui.icons.CartIcon
import com.suitup.app.ui.icons.MinusIcon
import com.suitup.app.ui.icons.PictureIcon
import com.suitup.app.ui.icons.PlusIcon
import com.suitup.app.ui.icons.RotateIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitHeightClass
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn

/**
 * Real Preview 3D experience (Phase 9.5B). See [Suit3DPreview] for the renderer-reality
 * decision (real product photo + bounded perspective tilt, not fabricated 3D geometry).
 * Same [imageKey]/[SuitImmersiveScaffold]/[SuitImmersiveTopBar] conventions as the Editor
 * 2D stage (Phase 9.5A) so the two immersive screens read as one continuous experience.
 */
@Composable
fun Preview3DScreen(
    state: Preview3DState,
    garmentColor: Color,
    imageKey: String,
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
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
    onReset: () -> Unit = {},
    onToggleLight: () -> Unit = {},
    onToggleBackground: () -> Unit = {},
    onEditAgain: () -> Unit = {},
    onOrder: () -> Unit = {},
) {
    val summaryDetails = configurationDetails.mapNotNull { detail ->
        val parts = detail.split(":", limit = 2)
        if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
    }
    val responsive = SuitTheme.responsive
    val stageHeightFraction = when {
        responsive.isShortHeight -> 0.36f
        responsive.isWideLayout || responsive.heightClass == SuitHeightClass.TALL -> 0.50f
        else -> 0.44f
    }

    SuitImmersiveScaffold(
        topBar = {
            SuitImmersiveTopBar(
                onBack = onBack,
                title = "Preview 3D",
                trailing = { PreviewCartTrailing(cartItemCount = cartItemCount, onCartClick = onCartClick) },
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    PreviewHero(
                        state = state,
                        garmentColor = garmentColor,
                        imageKey = imageKey,
                        showLight = showLight,
                        backgroundDark = backgroundDark,
                        onStateChange = onStateChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(responsive.screenHeight * stageHeightFraction),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(
                                horizontal = responsive.horizontalContentPadding,
                                vertical = if (responsive.isShortHeight) 12.dp else 18.dp,
                            ),
                        verticalArrangement = Arrangement.spacedBy(if (responsive.isShortHeight) 14.dp else 20.dp),
                    ) {
                        PreviewControls(
                            onRotate = onRotate,
                            onZoomIn = onZoomIn,
                            onZoomOut = onZoomOut,
                            onReset = onReset,
                            onToggleLight = onToggleLight,
                            onToggleBackground = onToggleBackground,
                            centered = responsive.isWideLayout,
                        )
                        ConfigurationSummarySection(
                            modelName = modelName.ifBlank { "Fato personalizado" },
                            details = summaryDetails,
                            totalMzn = estimatedPriceMzn,
                            startExpanded = !responsive.isCompactLayout,
                        )
                        PremiumCard {
                            Text(
                                text = "A pré-visualização é demonstrativa. A equipa de alfaiataria confirma os detalhes antes da produção.",
                                style = SuitTextStyles.bodySmall,
                                color = SuitColors.Slate,
                            )
                        }
                        Spacer(modifier = Modifier.height(140.dp))
                    }
                }
                PreviewCtaBar(
                    onOrder = onOrder,
                    onEditAgain = onEditAgain,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        },
    )
}

@Composable
private fun PreviewCartTrailing(cartItemCount: Int, onCartClick: () -> Unit) {
    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onCartClick),
            contentAlignment = Alignment.Center,
        ) {
            CartIcon(tint = SuitColors.Pearl)
        }
        if (cartItemCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(SuitColors.Gold),
                contentAlignment = Alignment.Center,
            ) {
                Text(cartItemCount.toString(), style = SuitTextStyles.labelSmall, color = SuitColors.GoldInk)
            }
        }
    }
}

@Composable
private fun PreviewHero(
    state: Preview3DState,
    garmentColor: Color,
    imageKey: String,
    showLight: Boolean,
    backgroundDark: Boolean,
    onStateChange: (Preview3DState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(SuitColors.WarmBlack)) {
        ShowroomBackground(
            modifier = Modifier.fillMaxSize(),
            bright = showLight,
            lightBackground = !backgroundDark,
        )
        Suit3DPreview(
            state = state,
            onStateChange = onStateChange,
            imageKey = imageKey,
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
        PreviewColorSwatch(
            color = garmentColor,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
    }
}

/**
 * Class B (controlled overlay) indicator for the selected color, matching the Editor
 * stage's [ColorBadge] convention — a floating swatch, not a full-photo tint (no
 * per-garment alpha mask exists to isolate the jacket from skin/shirt/background).
 */
@Composable
private fun PreviewColorSwatch(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, SuitColors.Pearl, CircleShape),
    )
}

@Composable
private fun PreviewControls(
    onRotate: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit,
    onToggleLight: () -> Unit,
    onToggleBackground: () -> Unit,
    centered: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Controlos", style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .let { if (centered) it.widthIn(max = 420.dp) else it },
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top,
        ) {
            Suit3DControlButton(
                label = "Girar",
                onClick = onRotate,
                icon = { RotateIcon(tint = SuitColors.Gold, size = 20.dp) },
            )
            Suit3DControlButton(
                label = "Zoom +",
                onClick = onZoomIn,
                icon = { PlusIcon(tint = SuitColors.Gold, size = 20.dp) },
            )
            Suit3DControlButton(
                label = "Zoom -",
                onClick = onZoomOut,
                icon = { MinusIcon(tint = SuitColors.Gold, size = 20.dp) },
            )
            Suit3DControlButton(
                label = "Repor",
                onClick = onReset,
                icon = {
                    Text("↺", style = SuitTextStyles.titleMedium, color = SuitColors.Gold)
                },
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

/**
 * Task 6/9: honest configuration recap, collapsible on narrow/compact widths so it never
 * outgrows the preview stage above it.
 */
@Composable
private fun ConfigurationSummarySection(
    modelName: String,
    details: List<Pair<String, String>>,
    totalMzn: Int,
    startExpanded: Boolean,
) {
    var expanded by remember { mutableStateOf(startExpanded) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (expanded) {
            CustomizationSummaryCard(modelName = modelName, details = details, totalMzn = totalMzn)
        } else {
            PremiumCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Resumo da configuração", style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
                        Text(modelName, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
                    }
                    Text(formatMzn(totalMzn), style = SuitTextStyles.titleMedium, color = SuitColors.GoldChampagne)
                }
            }
        }
        Text(
            text = if (expanded) "Ocultar detalhes" else "Ver detalhes",
            style = SuitTextStyles.labelMedium,
            color = SuitColors.Gold,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { expanded = !expanded },
        )
    }
}

@Composable
private fun PreviewCtaBar(
    onOrder: () -> Unit,
    onEditAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(SuitColors.WarmBlack.copy(alpha = 0f), SuitColors.WarmBlack)))
            .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PrimaryGoldButton(text = "Adicionar ao carrinho", onClick = onOrder)
        SecondaryDarkButton(text = "Editar novamente", onClick = onEditAgain)
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
