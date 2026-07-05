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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Segmented control for 2-4 short, mutually exclusive options (e.g. delivery vs
 * pickup, short filter modes). Selection is communicated by both fill color AND
 * a border/weight change on the label, not color alone.
 *
 * Container com border 1dp Mist e shape md.
 * Opção ativa: fundo Gold + texto ink + semibold.
 * Opção inativa: fundo transparente + texto Slate.
 *
 * Labels never wrap or clip: they render on a single line and ellipsize if the
 * available segment width is too narrow. If a caller's labels routinely
 * ellipsize, that is a signal to use a different interaction pattern (e.g. a
 * dropdown) rather than force long labels into this control.
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
                    .clickable { onSelect(option) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionLabel(option),
                    style = SuitTextStyles.button.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else SuitTextStyles.button.fontWeight,
                    ),
                    color = fg,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                )
            }
        }
    }
}
