package com.suitup.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTheme

/**
 * Silhueta mini de jaqueta de fato — desenhada em Canvas.
 *
 * Usada como thumbnail em listas de pedidos, cards do catálogo e hero pequeno.
 * O parâmetro [garmentColor] controla a cor da jaqueta (preview do tecido escolhido).
 *
 * Quando houver assets de imagens reais, substituir por AsyncImage do Coil.
 */
@Composable
fun SuitGarmentMini(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    garmentColor: Color = SuitColors.SurfaceHighest,
    background: Color = SuitColors.SurfaceLow,
    showShirt: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(SuitTheme.shapes.md)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.5f

            // Camisa branca atrás (V do colarinho + corpo)
            if (showShirt) {
                val shirt = Path().apply {
                    moveTo(cx - w * 0.18f, h * 0.20f)
                    lineTo(cx, h * 0.42f)
                    lineTo(cx + w * 0.18f, h * 0.20f)
                    lineTo(cx + w * 0.18f, h * 0.95f)
                    lineTo(cx - w * 0.18f, h * 0.95f)
                    close()
                }
                drawPath(shirt, SuitColors.SurfaceWhite)
            }

            // Jaqueta — duas formas que se cruzam no centro
            val leftJacket = Path().apply {
                moveTo(cx - w * 0.42f, h * 0.18f)
                lineTo(cx - w * 0.18f, h * 0.20f)
                lineTo(cx, h * 0.42f)
                lineTo(cx, h * 0.95f)
                lineTo(cx - w * 0.42f, h * 0.95f)
                close()
            }
            val rightJacket = Path().apply {
                moveTo(cx + w * 0.42f, h * 0.18f)
                lineTo(cx + w * 0.18f, h * 0.20f)
                lineTo(cx, h * 0.42f)
                lineTo(cx, h * 0.95f)
                lineTo(cx + w * 0.42f, h * 0.95f)
                close()
            }
            drawPath(leftJacket, garmentColor)
            drawPath(rightJacket, garmentColor)

            // Pequeno detalhe: gravata centrada (apenas se camisa visível)
            if (showShirt) {
                val tie = Path().apply {
                    moveTo(cx - w * 0.04f, h * 0.42f)
                    lineTo(cx + w * 0.04f, h * 0.42f)
                    lineTo(cx + w * 0.05f, h * 0.78f)
                    lineTo(cx, h * 0.84f)
                    lineTo(cx - w * 0.05f, h * 0.78f)
                    close()
                }
                drawPath(tie, SuitColors.Black)
            }

            // Linha subtil dos botões da jaqueta (lateral interna esquerda)
            val s = (w / 64f) * 1.2f
            val buttonsLine = Path().apply {
                moveTo(cx - w * 0.02f, h * 0.50f)
                lineTo(cx - w * 0.02f, h * 0.85f)
            }
            // só se a cor da jaqueta for escura — não desenha em jaquetas claras
            if (garmentColor.luminance() < 0.5f) {
                drawPath(
                    buttonsLine,
                    SuitColors.SurfaceWhite.copy(alpha = 0.25f),
                    style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
