package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import com.suitup.app.ui.util.suitImageResource
import org.jetbrains.compose.resources.painterResource

/**
 * Preview stage state — kept outside the composable so the parent screen model owns
 * reset/persistence, matching the rest of the Editor/Preview state pattern.
 */
data class Preview3DState(
    val rotationY: Float = 0f,    // graus, ver [Preview3DRotationLimit]
    val scale: Float = 1f,        // ver [Preview3DScaleMin]/[Preview3DScaleMax]
)

/**
 * Renderer reality (Phase 9.5B, Task 1): there is no real 3D geometry, GLB/glTF asset or
 * scene-graph renderer anywhere in this codebase — the previous implementation drew a
 * hand-authored, solid-color vector silhouette on a Canvas and called it "3D", which was
 * disconnected from both the real product photo and the selected color. That is FUTURE
 * ONLY work (tracked in the phase report, not attempted here).
 *
 * IMPLEMENTABLE NOW: the actual selected-suit photo (same [suitImageResource] mapping as
 * the Editor stage), given a bounded, honest interactive treatment — drag/tap tilts the
 * flat image in 3D space via [Modifier.graphicsLayer]'s `rotationY` + `cameraDistance`
 * (a real perspective transform of a real 2D asset, not a fabricated back/side view), and
 * pinch/buttons scale it. Rotation is clamped to +-[Preview3DRotationLimit] degrees —
 * enough for a believable "look from the side" parallax — because there is only one
 * front-facing photo per model; a full spin would have to invent a back view that does
 * not exist as an asset, which this phase's constraints explicitly forbid.
 */
@Composable
fun Suit3DPreview(
    state: Preview3DState,
    onStateChange: (Preview3DState) -> Unit,
    imageKey: String,
    modifier: Modifier = Modifier,
    showLight: Boolean = false,
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newRotation = (state.rotationY + dragAmount.x * 0.25f)
                        .coerceIn(-Preview3DRotationLimit, Preview3DRotationLimit)
                    onStateChange(state.copy(rotationY = newRotation))
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    val newScale = (state.scale * zoom).coerceIn(Preview3DScaleMin, Preview3DScaleMax)
                    onStateChange(state.copy(scale = newScale))
                }
            },
    ) {
        Image(
            painter = painterResource(suitImageResource(imageKey)),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = state.rotationY
                    scaleX = state.scale
                    scaleY = state.scale
                    cameraDistance = 14f * density
                },
        )
        if (showLight) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Color.White.copy(alpha = 0.14f), Color.Transparent),
                        ),
                    ),
            )
        }
    }
}
