package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import kotlin.math.abs
import kotlin.math.cos

/**
 * Estado do preview 3D fake.
 * Mantido fora do composable para que o pai possa controlar reset/persistência.
 */
data class Preview3DState(
    val rotationY: Float = 0f,    // graus
    val scale: Float = 1f,        // 0.5 .. 2.0
)

/**
 * Preview 3D simulado via Compose Canvas + transformações 2D.
 *
 * Ilusão de rotação Y: a silhueta é desenhada com largura proporcional a |cos(rotationY)|
 * e perspetiva subtil ao virar de lado. Pinch zoom altera scaleX/Y.
 *
 * Quando o Step 7 for implementado com Filament/SceneKit reais, este composable é
 * substituído por SuitRenderer3D (expect/actual) e a API mantém-se quase igual.
 */
@Composable
fun Suit3DPreview(
    state: Preview3DState,
    onStateChange: (Preview3DState) -> Unit,
    garmentColor: Color,
    modifier: Modifier = Modifier,
    showLight: Boolean = false,
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newRotation = state.rotationY + dragAmount.x * 0.5f
                    onStateChange(state.copy(rotationY = newRotation))
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    val newScale = (state.scale * zoom).coerceIn(0.6f, 1.8f)
                    onStateChange(state.copy(scale = newScale))
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Fato com rotação Y simulada
        Garment3DSilhouette(
            rotationY = state.rotationY,
            scale = state.scale,
            garmentColor = garmentColor,
            showLight = showLight,
            modifier = Modifier.size(220.dp)
        )
    }
}

@Composable
private fun Garment3DSilhouette(
    rotationY: Float,
    scale: Float,
    garmentColor: Color,
    showLight: Boolean,
    modifier: Modifier = Modifier,
) {
    // Normalizar rotação para 0..360
    val normalizedRotation = ((rotationY % 360f) + 360f) % 360f
    // |cos| dá-nos o "achatamento" — 1 quando frontal/traseiro, 0 quando perfil
    val rotationRad = (normalizedRotation * kotlin.math.PI / 180f).toFloat()
    val widthFactor = abs(cos(rotationRad)).coerceAtLeast(0.18f)

    // Quando virado de lado (90/270), mostrar perfil em vez de frontal
    val isProfile = widthFactor < 0.45f

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f

            if (isProfile) {
                drawProfileSilhouette(cx, w, h, garmentColor, showLight)
            } else {
                drawFrontSilhouette(cx, w, h, widthFactor, garmentColor, showLight, normalizedRotation)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFrontSilhouette(
    cx: Float, w: Float, h: Float, widthFactor: Float,
    garmentColor: Color, showLight: Boolean, rotation: Float,
) {
    val isBack = rotation in 90f..270f
    val sleeveOffset = w * 0.18f * widthFactor

    val lightOverlay = if (showLight) Color.White.copy(alpha = 0.08f) else Color.Transparent

    // Camisa branca atrás (V do colarinho)
    if (!isBack) {
        val shirt = Path().apply {
            moveTo(cx - w * 0.10f * widthFactor, h * 0.20f)
            lineTo(cx, h * 0.32f)
            lineTo(cx + w * 0.10f * widthFactor, h * 0.20f)
            lineTo(cx + w * 0.18f * widthFactor, h * 0.20f)
            lineTo(cx + w * 0.18f * widthFactor, h * 0.92f)
            lineTo(cx - w * 0.18f * widthFactor, h * 0.92f)
            lineTo(cx - w * 0.18f * widthFactor, h * 0.20f)
            close()
        }
        drawPath(shirt, SuitColors.SurfaceWhite)
    }

    // Jaqueta — corpo principal com offset baseado em widthFactor
    val leftJacket = Path().apply {
        moveTo(cx - sleeveOffset - w * 0.20f * widthFactor, h * 0.18f)
        lineTo(cx - sleeveOffset + w * 0.05f * widthFactor, h * 0.20f)
        lineTo(cx, h * 0.32f)
        lineTo(cx, h * 0.92f)
        lineTo(cx - sleeveOffset - w * 0.22f * widthFactor, h * 0.92f)
        close()
    }
    val rightJacket = Path().apply {
        moveTo(cx + sleeveOffset + w * 0.20f * widthFactor, h * 0.18f)
        lineTo(cx + sleeveOffset - w * 0.05f * widthFactor, h * 0.20f)
        lineTo(cx, h * 0.32f)
        lineTo(cx, h * 0.92f)
        lineTo(cx + sleeveOffset + w * 0.22f * widthFactor, h * 0.92f)
        close()
    }
    drawPath(leftJacket, garmentColor)
    drawPath(rightJacket, garmentColor)

    // Light overlay sobre toda a silhueta
    if (showLight) {
        drawPath(leftJacket, lightOverlay)
        drawPath(rightJacket, lightOverlay)
    }

    // Gravata (apenas frontal)
    if (!isBack) {
        val tie = Path().apply {
            moveTo(cx - w * 0.025f * widthFactor, h * 0.32f)
            lineTo(cx + w * 0.025f * widthFactor, h * 0.32f)
            lineTo(cx + w * 0.035f * widthFactor, h * 0.78f)
            lineTo(cx, h * 0.84f)
            lineTo(cx - w * 0.035f * widthFactor, h * 0.78f)
            close()
        }
        drawPath(tie, SuitColors.Ink)
    }

    // Calça (parte de baixo, mesma cor da jaqueta)
    val trousers = Path().apply {
        moveTo(cx - w * 0.16f * widthFactor, h * 0.92f)
        lineTo(cx + w * 0.16f * widthFactor, h * 0.92f)
        lineTo(cx + w * 0.13f * widthFactor, h * 1.0f)
        lineTo(cx - w * 0.13f * widthFactor, h * 1.0f)
        close()
    }
    drawPath(trousers, garmentColor)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProfileSilhouette(
    cx: Float, w: Float, h: Float, garmentColor: Color, showLight: Boolean,
) {
    // Vista de perfil — silhueta lateral mais estreita
    val profileWidth = w * 0.16f

    val body = Path().apply {
        moveTo(cx - profileWidth, h * 0.18f)
        lineTo(cx + profileWidth * 0.6f, h * 0.18f)
        lineTo(cx + profileWidth * 0.6f, h * 0.92f)
        lineTo(cx - profileWidth, h * 0.92f)
        close()
    }
    drawPath(body, garmentColor)

    // Calça
    val trousers = Path().apply {
        moveTo(cx - profileWidth * 0.9f, h * 0.92f)
        lineTo(cx + profileWidth * 0.5f, h * 0.92f)
        lineTo(cx + profileWidth * 0.5f, h * 1.0f)
        lineTo(cx - profileWidth * 0.9f, h * 1.0f)
        close()
    }
    drawPath(trousers, garmentColor)

    // Sombra subtil para sugerir profundidade
    val shadow = Path().apply {
        moveTo(cx + profileWidth * 0.5f, h * 0.18f)
        lineTo(cx + profileWidth * 0.6f, h * 0.18f)
        lineTo(cx + profileWidth * 0.6f, h * 0.92f)
        lineTo(cx + profileWidth * 0.5f, h * 0.92f)
        close()
    }
    drawPath(shadow, SuitColors.Ink.copy(alpha = 0.4f))

    if (showLight) {
        drawPath(body, Color.White.copy(alpha = 0.08f))
    }
}
