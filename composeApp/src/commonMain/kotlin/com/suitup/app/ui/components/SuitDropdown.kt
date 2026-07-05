package com.suitup.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.ForwardChevronIcon
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Dropdown / combobox SuitUP — used for category, status, delivery-location and
 * admin-filter selection wherever a native Compose menu (not an HTML-style
 * select) is appropriate.
 *
 * Botão com border 1dp Mist + label + chevron rotativo + menu suspenso por baixo.
 * O menu é controlado internamente (open/close) — o componente expõe apenas
 * [selectedLabel], [options] e [onSelect].
 *
 * Estado de "expanded" é UI-only, fica dentro do componente. State management
 * (qual opção está selecionada) é hoisted via [selectedLabel].
 *
 * States: DEFAULT, FOCUSED/expanded (gold border), SELECTED (option row highlight),
 * ERROR (red border + message), DISABLED (muted surface, non-interactive).
 */
@Composable
fun <T> SuitDropdown(
    options: List<T>,
    selectedOption: T,
    onSelect: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val updatedOnSelect by rememberUpdatedState(onSelect)

    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = SuitAnim.normal(),
        label = "chevron-rotation"
    )

    val borderColor = when {
        !enabled -> SuitColors.Mist.copy(alpha = 0.5f)
        error != null -> SuitColors.PaleRedInk
        expanded -> SuitColors.GoldChampagne
        else -> SuitColors.Mist
    }
    val backgroundColor = if (enabled) SuitColors.WarmBlack else SuitColors.SurfaceHigh

    Box(modifier = modifier) {
        Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(SuitTheme.shapes.input)
                .background(backgroundColor)
                .border(1.dp, borderColor, SuitTheme.shapes.input)
                .clickable(enabled = enabled) { expanded = !expanded }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = optionLabel(selectedOption),
                style = SuitTextStyles.bodyMedium,
                color = if (enabled) SuitColors.Ink else SuitColors.Smoke,
            )
            ForwardChevronIcon(
                tint = SuitColors.Slate,
                size = 18.dp,
                modifier = Modifier.rotate(chevronRotation),
            )
        }
        if (error != null) {
            Text(
                text = error,
                style = SuitTextStyles.bodySmall,
                color = SuitColors.PaleRedInk,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(SuitColors.Surface)
                .widthIn(min = 200.dp),
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = option == selectedOption
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel(option),
                            style = SuitTextStyles.bodyMedium,
                            color = if (isSelected) SuitColors.Gold else SuitColors.Slate,
                        )
                    },
                    onClick = {
                        expanded = false
                        updatedOnSelect(option)
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = SuitColors.Ink,
                    ),
                    modifier = Modifier.background(
                        if (isSelected) SuitColors.SurfaceHigh else Color.Transparent
                    ),
                )
                if (index < options.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)
                }
            }
        }
    }
}
