package com.suitup.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.CheckIcon
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors

/**
 * Badge circular grande com ícone check, usado em ecrãs de sucesso.
 *
 * Anima na entrada: scale 0.85 → 1.0 + alpha 0 → 1 com ease-out (600ms).
 * Sem bounce — coerente com compose_ui.md.
 */
@Composable
fun SuitSuccessBadge(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    background: Color = SuitColors.Gold,
    iconTint: Color = SuitColors.GoldInk,
    animateEntry: Boolean = true,
) {
    var visible by remember { mutableStateOf(!animateEntry) }

    LaunchedEffect(Unit) {
        if (animateEntry) visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = SuitAnim.reveal(),
        label = "success-scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = SuitAnim.reveal(),
        label = "success-alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        CheckIcon(tint = iconTint, size = size * 0.45f)
    }
}
