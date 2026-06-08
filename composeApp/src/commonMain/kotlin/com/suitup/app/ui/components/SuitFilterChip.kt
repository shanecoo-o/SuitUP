package com.suitup.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Chip de filtro horizontal — pill com estados ativo/inativo.
 *
 * Princípios aplicados:
 * - Ativo: fundo Ink, texto branco, sem border
 * - Inativo: fundo transparente, border hairline 1dp Mist, texto Slate
 * - Transição animada de cor (250ms ease-out)
 */
@Composable
fun SuitFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg by animateColorAsState(
        targetValue = if (selected) SuitColors.Ink else SuitColors.SurfaceWhite,
        animationSpec = SuitAnim.normal(),
        label = "chip-bg"
    )
    val fg by animateColorAsState(
        targetValue = if (selected) SuitColors.SurfaceWhite else SuitColors.Slate,
        animationSpec = SuitAnim.normal(),
        label = "chip-fg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) SuitColors.Ink else SuitColors.Mist,
        animationSpec = SuitAnim.normal(),
        label = "chip-border"
    )

    Box(
        modifier = modifier
            .clip(SuitTheme.shapes.pill)
            .background(bg)
            .border(1.dp, borderColor, SuitTheme.shapes.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = SuitTextStyles.labelMedium,
            color = fg,
        )
    }
}
