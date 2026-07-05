package com.suitup.app.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors

/**
 * Ícones SuitUP — desenhados com Canvas para controlo total e zero dependências.
 * Stroke 1.6dp consistente, line-cap round, line-join round.
 */

private val DefaultStroke = 1.6f

@Composable
fun BackChevronIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.625f, h * 0.25f)
            lineTo(w * 0.375f, h * 0.5f)
            lineTo(w * 0.625f, h * 0.75f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = DefaultStroke * (w / 22f), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun ForwardChevronIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.4f, h * 0.25f)
            lineTo(w * 0.65f, h * 0.5f)
            lineTo(w * 0.4f, h * 0.75f)
        }
        drawPath(path, tint, style = Stroke(DefaultStroke * (w / 22f), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun CartIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        // Cesto
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.28f)
            lineTo(w * 0.30f, h * 0.28f)
            lineTo(w * 0.40f, h * 0.65f)
            lineTo(w * 0.80f, h * 0.65f)
            lineTo(w * 0.86f, h * 0.40f)
            lineTo(w * 0.34f, h * 0.40f)
        }
        drawPath(path, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Rodas
        drawCircle(tint, radius = s * 1.5f, center = Offset(w * 0.45f, h * 0.80f), style = Stroke(s))
        drawCircle(tint, radius = s * 1.5f, center = Offset(w * 0.75f, h * 0.80f), style = Stroke(s))
    }
}

@Composable
fun HomeIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink, filled: Boolean = false) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.22f)
            lineTo(w * 0.82f, h * 0.50f)
            lineTo(w * 0.82f, h * 0.80f)
            lineTo(w * 0.18f, h * 0.80f)
            close()
        }
        if (filled) drawPath(path, tint)
        else drawPath(path, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ShirtIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink, filled: Boolean = false) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.20f)
            lineTo(w * 0.42f, h * 0.30f)
            lineTo(w * 0.58f, h * 0.30f)
            lineTo(w * 0.70f, h * 0.20f)
            lineTo(w * 0.85f, h * 0.32f)
            lineTo(w * 0.78f, h * 0.45f)
            lineTo(w * 0.74f, h * 0.42f)
            lineTo(w * 0.74f, h * 0.82f)
            lineTo(w * 0.26f, h * 0.82f)
            lineTo(w * 0.26f, h * 0.42f)
            lineTo(w * 0.22f, h * 0.45f)
            lineTo(w * 0.15f, h * 0.32f)
            close()
        }
        if (filled) drawPath(path, tint)
        else drawPath(path, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun BagIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink, filled: Boolean = false) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val body = Path().apply {
            moveTo(w * 0.22f, h * 0.36f)
            lineTo(w * 0.78f, h * 0.36f)
            lineTo(w * 0.74f, h * 0.84f)
            lineTo(w * 0.26f, h * 0.84f)
            close()
        }
        if (filled) drawPath(body, tint)
        else drawPath(body, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Asa
        val handle = Path().apply {
            moveTo(w * 0.36f, h * 0.36f)
            lineTo(w * 0.36f, h * 0.24f)
            lineTo(w * 0.64f, h * 0.24f)
            lineTo(w * 0.64f, h * 0.36f)
        }
        drawPath(handle, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PersonIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink, filled: Boolean = false) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        // cabeça
        if (filled) drawCircle(tint, radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.32f))
        else drawCircle(tint, radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(s))
        // ombros
        val body = Path().apply {
            moveTo(w * 0.20f, h * 0.85f)
            cubicTo(w * 0.20f, h * 0.62f, w * 0.80f, h * 0.62f, w * 0.80f, h * 0.85f)
        }
        if (filled) drawPath(body, tint)
        else drawPath(body, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun EyeIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Slate, open: Boolean = true) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val outer = Path().apply {
            moveTo(w * 0.10f, h * 0.50f)
            cubicTo(w * 0.30f, h * 0.20f, w * 0.70f, h * 0.20f, w * 0.90f, h * 0.50f)
            cubicTo(w * 0.70f, h * 0.80f, w * 0.30f, h * 0.80f, w * 0.10f, h * 0.50f)
            close()
        }
        drawPath(outer, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        if (open) drawCircle(tint, radius = w * 0.10f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(s))
    }
}

@Composable
fun CheckIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.SurfaceWhite) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f) * 1.4f
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.70f)
            lineTo(w * 0.78f, h * 0.32f)
        }
        drawPath(path, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PlusIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        drawLine(tint, Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.78f), s, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.22f, h * 0.5f), Offset(w * 0.78f, h * 0.5f), s, StrokeCap.Round)
    }
}

@Composable
fun MinusIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        drawLine(tint, Offset(w * 0.22f, h * 0.5f), Offset(w * 0.78f, h * 0.5f), s, StrokeCap.Round)
    }
}

@Composable
fun CloseIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        drawLine(tint, Offset(w * 0.25f, h * 0.25f), Offset(w * 0.75f, h * 0.75f), s, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.75f, h * 0.25f), Offset(w * 0.25f, h * 0.75f), s, StrokeCap.Round)
    }
}

@Composable
fun GoogleIcon(modifier: Modifier = Modifier, size: Dp = 22.dp) {
    // G simplificado em monochrome, sem cores oficiais (evitamos branding marks reais)
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f) * 1.3f
        val tint = SuitColors.Ink
        // Arco em G
        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.32f)
            cubicTo(w * 0.55f, h * 0.20f, w * 0.30f, h * 0.22f, w * 0.22f, h * 0.45f)
            cubicTo(w * 0.18f, h * 0.65f, w * 0.35f, h * 0.82f, w * 0.55f, h * 0.78f)
            cubicTo(w * 0.70f, h * 0.74f, w * 0.78f, h * 0.62f, w * 0.74f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.50f)
        }
        drawPath(path, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun AppleIcon(modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val tint = SuitColors.Ink
        // Maçã estilizada (silhueta)
        val apple = Path().apply {
            moveTo(w * 0.5f, h * 0.30f)
            cubicTo(w * 0.30f, h * 0.30f, w * 0.18f, h * 0.50f, w * 0.30f, h * 0.78f)
            cubicTo(w * 0.40f, h * 0.90f, w * 0.50f, h * 0.84f, w * 0.55f, h * 0.84f)
            cubicTo(w * 0.60f, h * 0.84f, w * 0.70f, h * 0.90f, w * 0.80f, h * 0.78f)
            cubicTo(w * 0.92f, h * 0.50f, w * 0.80f, h * 0.30f, w * 0.60f, h * 0.30f)
            close()
        }
        drawPath(apple, tint)
        // Folha
        val leaf = Path().apply {
            moveTo(w * 0.55f, h * 0.30f)
            cubicTo(w * 0.55f, h * 0.18f, w * 0.62f, h * 0.16f, w * 0.66f, h * 0.18f)
            cubicTo(w * 0.66f, h * 0.26f, w * 0.60f, h * 0.30f, w * 0.55f, h * 0.30f)
            close()
        }
        drawPath(leaf, tint)
    }
}

@Composable
fun TruckIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val box = Path().apply {
            moveTo(w * 0.10f, h * 0.35f)
            lineTo(w * 0.55f, h * 0.35f)
            lineTo(w * 0.55f, h * 0.70f)
            lineTo(w * 0.10f, h * 0.70f)
            close()
        }
        drawPath(box, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val cab = Path().apply {
            moveTo(w * 0.55f, h * 0.45f)
            lineTo(w * 0.75f, h * 0.45f)
            lineTo(w * 0.88f, h * 0.55f)
            lineTo(w * 0.88f, h * 0.70f)
            lineTo(w * 0.55f, h * 0.70f)
            close()
        }
        drawPath(cab, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(tint, w * 0.05f, Offset(w * 0.30f, h * 0.78f), style = Stroke(s))
        drawCircle(tint, w * 0.05f, Offset(w * 0.72f, h * 0.78f), style = Stroke(s))
    }
}

@Composable
fun PinIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val pin = Path().apply {
            moveTo(w * 0.5f, h * 0.85f)
            cubicTo(w * 0.20f, h * 0.55f, w * 0.20f, h * 0.30f, w * 0.5f, h * 0.20f)
            cubicTo(w * 0.80f, h * 0.30f, w * 0.80f, h * 0.55f, w * 0.5f, h * 0.85f)
            close()
        }
        drawPath(pin, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(tint, w * 0.07f, Offset(w * 0.5f, h * 0.42f), style = Stroke(s))
    }
}

@Composable
fun UploadIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        // Tray
        val tray = Path().apply {
            moveTo(w * 0.18f, h * 0.78f)
            lineTo(w * 0.18f, h * 0.62f)
            moveTo(w * 0.82f, h * 0.62f)
            lineTo(w * 0.82f, h * 0.78f)
            lineTo(w * 0.18f, h * 0.78f)
        }
        drawPath(tray, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Arrow up
        val arrow = Path().apply {
            moveTo(w * 0.5f, h * 0.20f)
            lineTo(w * 0.5f, h * 0.60f)
            moveTo(w * 0.32f, h * 0.36f)
            lineTo(w * 0.5f, h * 0.20f)
            lineTo(w * 0.68f, h * 0.36f)
        }
        drawPath(arrow, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun BellIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val bell = Path().apply {
            moveTo(w * 0.25f, h * 0.65f)
            lineTo(w * 0.25f, h * 0.50f)
            cubicTo(w * 0.25f, h * 0.30f, w * 0.40f, h * 0.20f, w * 0.5f, h * 0.20f)
            cubicTo(w * 0.60f, h * 0.20f, w * 0.75f, h * 0.30f, w * 0.75f, h * 0.50f)
            lineTo(w * 0.75f, h * 0.65f)
            lineTo(w * 0.85f, h * 0.72f)
            lineTo(w * 0.15f, h * 0.72f)
            close()
        }
        drawPath(bell, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Badger
        drawLine(tint, Offset(w * 0.45f, h * 0.80f), Offset(w * 0.55f, h * 0.80f), s, StrokeCap.Round)
    }
}

@Composable
fun RotateIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.SurfaceWhite) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val arc = Path().apply {
            moveTo(w * 0.78f, h * 0.30f)
            cubicTo(w * 0.62f, h * 0.18f, w * 0.30f, h * 0.22f, w * 0.22f, h * 0.55f)
            cubicTo(w * 0.18f, h * 0.78f, w * 0.50f, h * 0.85f, w * 0.70f, h * 0.75f)
        }
        drawPath(arc, tint, style = Stroke(s, cap = StrokeCap.Round))
        // Ponta
        val tip = Path().apply {
            moveTo(w * 0.78f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.34f)
            lineTo(w * 0.62f, h * 0.34f)
        }
        drawPath(tip, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ZoomIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.SurfaceWhite) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        drawCircle(tint, w * 0.22f, Offset(w * 0.42f, h * 0.42f), style = Stroke(s))
        drawLine(tint, Offset(w * 0.62f, h * 0.62f), Offset(w * 0.82f, h * 0.82f), s, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.30f, h * 0.42f), Offset(w * 0.54f, h * 0.42f), s, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.42f, h * 0.30f), Offset(w * 0.42f, h * 0.54f), s, StrokeCap.Round)
    }
}

@Composable
fun BulbIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.SurfaceWhite) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val bulb = Path().apply {
            moveTo(w * 0.32f, h * 0.55f)
            cubicTo(w * 0.32f, h * 0.32f, w * 0.68f, h * 0.32f, w * 0.68f, h * 0.55f)
            cubicTo(w * 0.68f, h * 0.62f, w * 0.62f, h * 0.66f, w * 0.60f, h * 0.72f)
            lineTo(w * 0.40f, h * 0.72f)
            cubicTo(w * 0.38f, h * 0.66f, w * 0.32f, h * 0.62f, w * 0.32f, h * 0.55f)
            close()
        }
        drawPath(bulb, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(tint, Offset(w * 0.42f, h * 0.78f), Offset(w * 0.58f, h * 0.78f), s, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.44f, h * 0.84f), Offset(w * 0.56f, h * 0.84f), s, StrokeCap.Round)
    }
}

@Composable
fun PictureIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.SurfaceWhite) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val rect = Path().apply {
            moveTo(w * 0.18f, h * 0.25f)
            lineTo(w * 0.82f, h * 0.25f)
            lineTo(w * 0.82f, h * 0.75f)
            lineTo(w * 0.18f, h * 0.75f)
            close()
        }
        drawPath(rect, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(tint, w * 0.05f, Offset(w * 0.34f, h * 0.40f), style = Stroke(s))
        val mountain = Path().apply {
            moveTo(w * 0.20f, h * 0.72f)
            lineTo(w * 0.40f, h * 0.55f)
            lineTo(w * 0.55f, h * 0.65f)
            lineTo(w * 0.70f, h * 0.48f)
            lineTo(w * 0.82f, h * 0.65f)
        }
        drawPath(mountain, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun InfoIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        drawCircle(tint, radius = w * 0.36f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(s))
        drawCircle(tint, radius = s * 0.9f, center = Offset(w * 0.5f, h * 0.32f))
        drawLine(tint, Offset(w * 0.5f, h * 0.46f), Offset(w * 0.5f, h * 0.70f), s, StrokeCap.Round)
    }
}

@Composable
fun WarningIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val triangle = Path().apply {
            moveTo(w * 0.5f, h * 0.16f)
            lineTo(w * 0.88f, h * 0.80f)
            lineTo(w * 0.12f, h * 0.80f)
            close()
        }
        drawPath(triangle, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(tint, Offset(w * 0.5f, h * 0.40f), Offset(w * 0.5f, h * 0.60f), s, StrokeCap.Round)
        drawCircle(tint, radius = s * 0.9f, center = Offset(w * 0.5f, h * 0.70f))
    }
}

@Composable
fun ErrorIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        drawCircle(tint, radius = w * 0.36f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(s))
        drawLine(tint, Offset(w * 0.38f, h * 0.38f), Offset(w * 0.62f, h * 0.62f), s, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.62f, h * 0.38f), Offset(w * 0.38f, h * 0.62f), s, StrokeCap.Round)
    }
}

@Composable
fun OfflineIcon(modifier: Modifier = Modifier, size: Dp = 22.dp, tint: Color = SuitColors.Ink) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val s = DefaultStroke * (w / 22f)
        val cloud = Path().apply {
            moveTo(w * 0.30f, h * 0.62f)
            cubicTo(w * 0.16f, h * 0.62f, w * 0.16f, h * 0.42f, w * 0.32f, h * 0.40f)
            cubicTo(w * 0.34f, h * 0.24f, w * 0.62f, h * 0.22f, w * 0.68f, h * 0.38f)
            cubicTo(w * 0.84f, h * 0.36f, w * 0.86f, h * 0.62f, w * 0.70f, h * 0.62f)
            close()
        }
        drawPath(cloud, tint, style = Stroke(s, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(tint, Offset(w * 0.18f, h * 0.20f), Offset(w * 0.82f, h * 0.84f), s, StrokeCap.Round)
    }
}
