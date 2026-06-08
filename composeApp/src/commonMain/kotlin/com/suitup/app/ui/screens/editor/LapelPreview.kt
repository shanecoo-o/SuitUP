package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
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
import com.suitup.app.domain.model.TipoLapela
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Card seletor de tipo de lapela.
 *
 * Mini-illustration desenhada em Canvas + estado selected/unselected via border.
 * Selected = border 1.5dp Ink. Unselected = border 1dp Mist.
 */
@Composable
fun LapelPreviewCard(
    type: TipoLapela,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val borderWidth = if (selected) 1.5.dp else 1.dp
    val borderColor = if (selected) SuitColors.Ink else SuitColors.Mist

    Box(
        modifier = modifier
            .size(size)
            .clip(SuitTheme.shapes.md)
            .background(SuitColors.SurfaceWhite)
            .border(borderWidth, borderColor, SuitTheme.shapes.md)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        LapelMiniIllustration(
            type = type,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
    }
}

@Composable
fun LapelMiniIllustration(
    type: TipoLapela,
    modifier: Modifier = Modifier,
    color: Color = SuitColors.Ink,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = (w / 40f) * 1.4f

        when (type) {
            TipoLapela.Entalhada -> {
                // V típico com pequeno entalhe (notch) no encaixe
                val path = Path().apply {
                    moveTo(w * 0.20f, h * 0.20f)
                    lineTo(w * 0.42f, h * 0.50f)
                    lineTo(w * 0.36f, h * 0.55f)
                    lineTo(w * 0.50f, h * 0.55f)
                    lineTo(w * 0.50f, h * 1.00f)
                    moveTo(w * 0.80f, h * 0.20f)
                    lineTo(w * 0.58f, h * 0.50f)
                    lineTo(w * 0.64f, h * 0.55f)
                    lineTo(w * 0.50f, h * 0.55f)
                }
                drawPath(path, color, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            TipoLapela.Pontiaguda -> {
                // Lapela apontada para cima
                val path = Path().apply {
                    moveTo(w * 0.50f, h * 1.00f)
                    lineTo(w * 0.50f, h * 0.55f)
                    lineTo(w * 0.30f, h * 0.50f)
                    lineTo(w * 0.42f, h * 0.30f)
                    lineTo(w * 0.20f, h * 0.20f)
                    moveTo(w * 0.50f, h * 0.55f)
                    lineTo(w * 0.70f, h * 0.50f)
                    lineTo(w * 0.58f, h * 0.30f)
                    lineTo(w * 0.80f, h * 0.20f)
                }
                drawPath(path, color, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            TipoLapela.Xale -> {
                // Lapela arredondada contínua (smoking)
                val path = Path().apply {
                    moveTo(w * 0.50f, h * 1.00f)
                    cubicTo(
                        w * 0.10f, h * 0.80f,
                        w * 0.15f, h * 0.30f,
                        w * 0.40f, h * 0.18f
                    )
                    moveTo(w * 0.50f, h * 1.00f)
                    cubicTo(
                        w * 0.90f, h * 0.80f,
                        w * 0.85f, h * 0.30f,
                        w * 0.60f, h * 0.18f
                    )
                }
                drawPath(path, color, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}
