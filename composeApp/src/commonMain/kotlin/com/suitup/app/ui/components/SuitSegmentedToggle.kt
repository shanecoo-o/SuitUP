package com.suitup.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
 * Segmented toggle horizontal com 2+ opções.
 *
 * Container com border 1dp Mist e shape md.
 * Opção ativa: fundo Ink + texto branco.
 * Opção inativa: fundo transparente + texto Slate.
 */
@Composable
fun <T> SuitSegmentedToggle(
    options: List<T>,
    selectedOption: T,
    onSelect: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(SuitTheme.shapes.md)
            .background(SuitColors.SurfaceLow)
            .border(1.dp, SuitColors.Mist, SuitTheme.shapes.md)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            val bg by animateColorAsState(
                targetValue = if (isSelected) SuitColors.Gold else SuitColors.SurfaceLow,
                animationSpec = SuitAnim.normal(),
                label = "seg-bg"
            )
            val fg by animateColorAsState(
                targetValue = if (isSelected) SuitColors.GoldInk else SuitColors.Slate,
                animationSpec = SuitAnim.normal(),
                label = "seg-fg"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(SuitTheme.shapes.sm)
                    .background(bg)
                    .clickable { onSelect(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionLabel(option),
                    style = SuitTextStyles.button,
                    color = fg,
                )
            }
        }
    }
}
