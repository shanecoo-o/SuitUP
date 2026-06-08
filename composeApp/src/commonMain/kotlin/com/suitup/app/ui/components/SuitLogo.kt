package com.suitup.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Mark "papillon" (gravata-borboleta).
 * Desenhado proporcionalmente — duas cunhas + nó central + colarinho subtil.
 */
@Composable
fun SuitLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    tint: Color = SuitColors.Ink,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Bow tie - duas cunhas triangulares + retângulo central
        val bow = Path().apply {
            // Cunha esquerda
            moveTo(w * 0.50f, h * 0.42f)
            lineTo(w * 0.10f, h * 0.30f)
            lineTo(w * 0.10f, h * 0.60f)
            lineTo(w * 0.50f, h * 0.50f)
            close()
            // Cunha direita
            moveTo(w * 0.50f, h * 0.42f)
            lineTo(w * 0.90f, h * 0.30f)
            lineTo(w * 0.90f, h * 0.60f)
            lineTo(w * 0.50f, h * 0.50f)
            close()
        }
        drawPath(bow, tint)

        // Nó central
        val knot = Path().apply {
            moveTo(w * 0.43f, h * 0.40f)
            lineTo(w * 0.57f, h * 0.40f)
            lineTo(w * 0.57f, h * 0.52f)
            lineTo(w * 0.43f, h * 0.52f)
            close()
        }
        drawPath(knot, tint)

        // Colarinho (V subtle abaixo)
        val gola = Path().apply {
            moveTo(w * 0.30f, h * 0.65f)
            lineTo(w * 0.50f, h * 0.85f)
            lineTo(w * 0.70f, h * 0.65f)
        }
        val s = w / 22f * 1.4f
        drawPath(gola, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Logo completo: mark + wordmark "Suit Up" / "SuitUP" abaixo.
 */
@Composable
fun SuitLogoStack(
    modifier: Modifier = Modifier,
    markSize: Dp = 64.dp,
    showWordmark: Boolean = true,
    tint: Color = SuitColors.Ink,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SuitLogoMark(size = markSize, tint = tint)
        if (showWordmark) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Suit Up",
                style = SuitTextStyles.brand.copy(fontWeight = FontWeight.Normal),
                color = tint,
            )
        }
    }
}

/**
 * Versão horizontal compacta: mark à esquerda + "SuitUP" à direita.
 * Para top bars e header pequenos.
 */
@Composable
fun SuitLogoInline(
    modifier: Modifier = Modifier,
    markSize: Dp = 28.dp,
    tint: Color = SuitColors.Ink,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SuitLogoMark(size = markSize, tint = tint)
        Spacer(Modifier.height(2.dp))
        Text(
            text = "SuitUP",
            style = SuitTextStyles.titleMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp),
            color = tint,
        )
    }
}
