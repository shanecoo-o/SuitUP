package com.suitup.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Shared loading primitives (Task 17). No third-party dependency — everything
 * is built on Material3's CircularProgressIndicator and core Compose animation APIs.
 */

/** Small indeterminate spinner for inline use (inside buttons, list rows, inline refresh). */
@Composable
fun SuitInlineLoading(
    modifier: Modifier = Modifier,
    size: Dp = SuitTheme.iconSizes.standard,
    color: Color = SuitColors.Gold,
    strokeWidth: Dp = 2.dp,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth,
    )
}

/**
 * Full-content loading state — centered spinner (+ optional message) filling the
 * available space. Use for whole-screen or whole-section loading, not inline actions.
 */
@Composable
fun SuitContentLoading(
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SuitInlineLoading(size = SuitTheme.iconSizes.feature, strokeWidth = 3.dp)
            if (message != null) {
                Text(message, style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
            }
        }
    }
}

/**
 * Single skeleton placeholder block with a subtle pulsing alpha. Composable by
 * later screen phases into any skeleton shape (rows of blocks, card outlines, etc.)
 * — this component intentionally has no opinion on layout, only on the pulse.
 */
@Composable
fun SuitSkeletonBlock(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = SuitTheme.shapes.sm,
    color: Color = SuitColors.SurfaceHigh,
) {
    val transition = rememberInfiniteTransition(label = "skeleton-pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "skeleton-alpha",
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = color.alpha * alpha)),
    )
}

/** Convenience skeleton line matching a single line of text at a given width fraction. */
@Composable
fun SuitSkeletonLine(
    modifier: Modifier = Modifier,
    height: Dp = 14.dp,
) {
    SuitSkeletonBlock(modifier = modifier.fillMaxWidth().height(height))
}
