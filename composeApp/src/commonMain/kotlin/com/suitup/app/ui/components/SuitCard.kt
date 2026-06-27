package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTheme

/**
 * Card SuitUP.
 *
 * Princípios:
 * - Border 1px Mist (sem shadows pesadas)
 * - Background branco sobre fundo Bone para criar separação subtil
 * - Padding generoso (20dp default)
 */
@Composable
fun SuitCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = 20.dp,
    background: Color = SuitColors.Surface,
    border: Boolean = true,
    shape: RoundedCornerShape = SuitTheme.shapes.card,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .let { m -> if (border) m.border(1.dp, SuitColors.BronzeSubtle.copy(alpha = 0.38f), shape) else m }
            .let { m -> if (onClick != null) m.clickable(onClick = onClick) else m }
            .padding(padding)
    ) {
        content()
    }
}
