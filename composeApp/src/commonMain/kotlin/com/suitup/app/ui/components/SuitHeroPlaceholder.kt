package com.suitup.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.suitup.app.ui.theme.SuitColors

/**
 * Placeholder visual para imagens hero ainda não disponíveis.
 *
 * Desenha uma silhueta abstrata de figura de fato (busto + colarinho + lapelas + gravata)
 * sobre fundo escuro. Quando os assets reais chegarem, substituir por AsyncImage do Coil.
 */
@Composable
fun SuitHeroPlaceholder(
    modifier: Modifier = Modifier,
    background: Color = SuitColors.Black,
    strokeColor: Color = SuitColors.Gold.copy(alpha = 0.26f),
) {
    Box(
        modifier = modifier.background(background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f

            val s = (w / 360f).coerceAtLeast(1f) * 1.2f

            // Cabeça (oval)
            val headCenterY = h * 0.32f
            val headRadiusX = w * 0.13f
            val headRadiusY = h * 0.10f
            drawOval(
                color = strokeColor,
                topLeft = androidx.compose.ui.geometry.Offset(cx - headRadiusX, headCenterY - headRadiusY),
                size = androidx.compose.ui.geometry.Size(headRadiusX * 2, headRadiusY * 2),
                style = Stroke(s)
            )

            // Pescoço
            drawLine(
                color = strokeColor,
                start = androidx.compose.ui.geometry.Offset(cx - w * 0.04f, h * 0.42f),
                end = androidx.compose.ui.geometry.Offset(cx - w * 0.04f, h * 0.50f),
                strokeWidth = s,
                cap = StrokeCap.Round
            )
            drawLine(
                color = strokeColor,
                start = androidx.compose.ui.geometry.Offset(cx + w * 0.04f, h * 0.42f),
                end = androidx.compose.ui.geometry.Offset(cx + w * 0.04f, h * 0.50f),
                strokeWidth = s,
                cap = StrokeCap.Round
            )

            // Ombros + colarinho da camisa
            val gola = Path().apply {
                moveTo(cx - w * 0.30f, h * 0.62f)
                lineTo(cx - w * 0.13f, h * 0.55f)
                lineTo(cx - w * 0.04f, h * 0.50f)
                lineTo(cx, h * 0.62f)
                lineTo(cx + w * 0.04f, h * 0.50f)
                lineTo(cx + w * 0.13f, h * 0.55f)
                lineTo(cx + w * 0.30f, h * 0.62f)
            }
            drawPath(
                path = gola,
                color = strokeColor,
                style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Lapelas do fato (V triangular)
            val lapelLeft = Path().apply {
                moveTo(cx - w * 0.32f, h * 0.66f)
                lineTo(cx - w * 0.04f, h * 0.62f)
                lineTo(cx, h * 1.0f)
            }
            val lapelRight = Path().apply {
                moveTo(cx + w * 0.32f, h * 0.66f)
                lineTo(cx + w * 0.04f, h * 0.62f)
                lineTo(cx, h * 1.0f)
            }
            drawPath(lapelLeft, strokeColor, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(lapelRight, strokeColor, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Gravata (estreita, longa)
            val tie = Path().apply {
                moveTo(cx - w * 0.025f, h * 0.62f)
                lineTo(cx + w * 0.025f, h * 0.62f)
                lineTo(cx + w * 0.04f, h * 0.95f)
                lineTo(cx, h * 1.0f)
                lineTo(cx - w * 0.04f, h * 0.95f)
                close()
            }
            drawPath(tie, strokeColor, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Linha do casaco esq/dir (extensão dos ombros para baixo)
            drawLine(
                color = strokeColor,
                start = androidx.compose.ui.geometry.Offset(cx - w * 0.32f, h * 0.66f),
                end = androidx.compose.ui.geometry.Offset(cx - w * 0.36f, h * 1.0f),
                strokeWidth = s,
                cap = StrokeCap.Round
            )
            drawLine(
                color = strokeColor,
                start = androidx.compose.ui.geometry.Offset(cx + w * 0.32f, h * 0.66f),
                end = androidx.compose.ui.geometry.Offset(cx + w * 0.36f, h * 1.0f),
                strokeWidth = s,
                cap = StrokeCap.Round
            )
        }
    }
}
