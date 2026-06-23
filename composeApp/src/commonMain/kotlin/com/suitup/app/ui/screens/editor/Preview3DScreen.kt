package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.Suit3DControlButton
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.icons.BackChevronIcon
import com.suitup.app.ui.icons.BulbIcon
import com.suitup.app.ui.icons.CartIcon
import com.suitup.app.ui.icons.PictureIcon
import com.suitup.app.ui.icons.RotateIcon
import com.suitup.app.ui.icons.ZoomIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import org.jetbrains.compose.resources.painterResource
import suitup.composeapp.generated.resources.Res
import suitup.composeapp.generated.resources.preview_3d_placeholder

/**
 * Ecrã 08 — Preview 3D (versão fiel ao mockup).
 *
 * Showroom escuro como hero ocupando ~85% do ecrã, com:
 * - Top: costas + título "Visualização 3D" + cart, todos flutuantes sobre a imagem
 * - Esquerda: 4 controles circulares (Girar/Zoom/Luz/Fundo) sobrepostos
 * - Centro: fato em pedestal (interativo: drag para rodar, pinch para zoom)
 * - Fundo: closet escuro com fatos pendurados ao longe
 * - Footer: faixa branca com 2 botões lado a lado
 *
 * Step 7: substituir o Suit3DPreview vetorial por SuitRenderer3D nativo (Filament/SceneKit)
 *         e o ShowroomBackground por AsyncImage de foto real.
 */
@Composable
fun Preview3DScreen(
    state: Preview3DState,
    garmentColor: Color,
    modelName: String = "",
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        // Showroom (hero) — ocupa todo o espaço disponível menos a footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            // Background do showroom (escuro com closet, ou claro)
            ShowroomBackground(
                modifier = Modifier.fillMaxSize(),
                bright = showLight,
                lightBackground = !backgroundDark,
            )

            Image(
                painter = painterResource(Res.drawable.preview_3d_placeholder),
                contentDescription = "Pré-visualização 3D do fato",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 72.dp, end = 12.dp, top = 56.dp, bottom = 32.dp)
                    .graphicsLayer { alpha = if (backgroundDark) 0.46f else 0.82f },
                contentScale = ContentScale.Fit,
            )

            // Suit preview interativo no centro
            Suit3DPreview(
                state = state,
                onStateChange = onStateChange,
                garmentColor = garmentColor,
                showLight = showLight,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 76.dp, end = 16.dp, top = 56.dp, bottom = 32.dp),
            )

            // Top floating bar: costas + título centrado + cart
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FloatingCircleButton(onClick = onBack) {
                    BackChevronIcon(tint = SuitColors.Pearl, size = 20.dp)
                }

                Text(
                    text = "Visualização 3D",
                    style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (backgroundDark) SuitColors.SurfaceWhite else SuitColors.Ink,
                )

                Box {
                    FloatingCircleButton(onClick = onCartClick) {
                        CartIcon(tint = SuitColors.Pearl, size = 20.dp)
                    }
                    if (cartItemCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 2.dp, end = 2.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(SuitColors.Gold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartItemCount.toString(),
                                style = SuitTextStyles.labelSmall,
                                color = SuitColors.GoldInk,
                            )
                        }
                    }
                }
            }

            // Controles laterais à esquerda — overlay sobre a imagem
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Suit3DControlButton(
                    label = "Girar",
                    onClick = onRotate,
                    icon = { RotateIcon(tint = SuitColors.Gold, size = 20.dp) }
                )
                Suit3DControlButton(
                    label = "Zoom",
                    onClick = onZoom,
                    icon = { ZoomIcon(tint = SuitColors.Gold, size = 20.dp) }
                )
                Suit3DControlButton(
                    label = "Luz",
                    onClick = onToggleLight,
                    icon = { BulbIcon(tint = SuitColors.Gold, size = 20.dp) }
                )
                Suit3DControlButton(
                    label = "Fundo",
                    onClick = onToggleBackground,
                    icon = { PictureIcon(tint = SuitColors.Gold, size = 20.dp) }
                )
            }

            if (modelName.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 16.dp, bottom = 18.dp)
                        .background(SuitColors.Surface.copy(alpha = 0.94f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = modelName,
                        style = SuitTextStyles.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = SuitColors.Ink,
                    )
                    configurationDetails.take(3).forEach { detail ->
                        Text(
                            text = detail,
                            style = SuitTextStyles.bodySmall,
                            color = SuitColors.Slate,
                        )
                    }
                }
            }
        }

        // Footer branca com ações lado a lado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SuitColors.Bone)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SuitButton(
                text = "Editar novamente",
                onClick = onEditAgain,
                variant = SuitButtonVariant.Secondary,
                fullWidth = false,
                modifier = Modifier.weight(1f),
            )
            SuitButton(
                text = "Encomendar",
                onClick = onOrder,
                variant = SuitButtonVariant.Primary,
                fullWidth = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Botão circular flutuante (background branco semi-translúcido) sobreposto à imagem.
 */
@Composable
private fun FloatingCircleButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
            .background(SuitColors.SurfaceLow.copy(alpha = 0.96f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

/**
 * Showroom — closet escuro com fatos pendurados + piso de pedestal.
 * Quando AsyncImage estiver disponível com foto real, substituir por:
 *   AsyncImage(model = "showroom_url.jpg", contentScale = ContentScale.Crop)
 */
@Composable
private fun ShowroomBackground(
    modifier: Modifier = Modifier,
    bright: Boolean = false,
    lightBackground: Boolean = false,
) {
    val baseColor = when {
        lightBackground -> SuitColors.Pearl
        bright -> SuitColors.Charcoal
        else -> Color(0xFF1F1F1F)  // closet escuro
    }

    Box(
        modifier = modifier.background(baseColor),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val ghostAlpha = if (lightBackground) 0.20f else 0.18f
            val rodAlpha = if (lightBackground) 0.30f else 0.28f
            val ghostColor = (if (lightBackground) SuitColors.Slate else SuitColors.Smoke).copy(alpha = ghostAlpha)
            val rodColor = (if (lightBackground) SuitColors.Slate else SuitColors.Smoke).copy(alpha = rodAlpha)

            // Vara horizontal do cabide
            drawRect(
                color = rodColor,
                topLeft = Offset(0f, h * 0.18f),
                size = Size(w, 1.5f)
            )

            // Silhuetas de fatos pendurados (apenas nas extremidades, deixar centro livre p/ manequim)
            val ghostPositions = listOf(0.08f, 0.22f, 0.78f, 0.92f)
            val ghostWidthFactor = 0.09f
            ghostPositions.forEach { px ->
                val cx = w * px
                val gWidth = w * ghostWidthFactor
                val top = h * 0.18f
                val bottom = h * 0.62f
                val ghost = Path().apply {
                    moveTo(cx - gWidth * 0.4f, top)
                    lineTo(cx + gWidth * 0.4f, top)
                    lineTo(cx + gWidth * 0.7f, bottom)
                    lineTo(cx - gWidth * 0.7f, bottom)
                    close()
                }
                drawPath(ghost, ghostColor)

                // Gancho do cabide
                val hook = Path().apply {
                    moveTo(cx - 3f, top)
                    lineTo(cx + 3f, top)
                    lineTo(cx, top - 6f)
                    close()
                }
                drawPath(hook, rodColor)
            }

            // Pedestal circular (piso achatado debaixo do manequim)
            val pedestalY = h * 0.86f
            val pedestalCenterX = w * 0.5f
            val pedestalWidth = w * 0.55f
            val pedestalHeight = h * 0.06f

            // Sombra
            drawOval(
                color = Color.Black.copy(alpha = 0.30f),
                topLeft = Offset(pedestalCenterX - pedestalWidth / 2f, pedestalY),
                size = Size(pedestalWidth, pedestalHeight)
            )
            // Topo
            val topAccent = if (lightBackground) SuitColors.Mist else SuitColors.Slate.copy(alpha = 0.3f)
            drawOval(
                color = topAccent,
                topLeft = Offset(pedestalCenterX - pedestalWidth * 0.45f, pedestalY - 2f),
                size = Size(pedestalWidth * 0.9f, pedestalHeight * 0.4f)
            )
        }
    }
}
