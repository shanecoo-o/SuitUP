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
 * Filter/selection chip — pill with selected/unselected states, used for both
 * filter chips (catalog category filters) and selection chips (multi-pick lists).
 * Not a status badge — this is an interactive control (see [SuitStatusBadge] for
 * the non-interactive status equivalent).
 *
 * Princípios aplicados:
 * - Ativo: fundo Gold, texto ink, sem border
 * - Inativo: fundo transparente, border hairline 1dp Mist, texto Slate
 * - Disabled: opacidade reduzida, não clicável
 * - Transição animada de cor (250ms ease-out)
 */
@Composable
fun SuitFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val bg by animateColorAsState(
        targetValue = if (selected) SuitColors.Gold else SuitColors.SurfaceLow,
        animationSpec = SuitAnim.normal(),
        label = "chip-bg"
    )
    val fg by animateColorAsState(
        targetValue = if (selected) SuitColors.GoldInk else SuitColors.Slate,
        animationSpec = SuitAnim.normal(),
        label = "chip-fg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) SuitColors.Gold else SuitColors.Mist,
        animationSpec = SuitAnim.normal(),
        label = "chip-border"
    )
    val contentAlpha = if (enabled) 1f else 0.38f

    Box(
        modifier = modifier
            .clip(SuitTheme.shapes.pill)
            .background(bg.copy(alpha = bg.alpha * contentAlpha))
            .border(1.dp, borderColor.copy(alpha = borderColor.alpha * contentAlpha), SuitTheme.shapes.pill)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = SuitTextStyles.labelMedium,
            color = fg.copy(alpha = fg.alpha * contentAlpha),
        )
    }
}
