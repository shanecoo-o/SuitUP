package com.suitup.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.ForwardChevronIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Item de menu para listas tipo settings/profile.
 *
 * Layout: ícone (opcional) + texto à esquerda, chevron (opcional) à direita.
 * Sem background, sem border — a separação visual vem do HorizontalDivider acima/abaixo.
 */
@Composable
fun SuitMenuRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    showChevron: Boolean = true,
    emphasized: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier.size(22.dp),
                contentAlignment = Alignment.Center
            ) {
                leadingIcon()
            }
        }

        Text(
            text = text,
            style = SuitTextStyles.bodyLarge.copy(
                fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (emphasized) SuitColors.Gold else SuitColors.Ink,
            modifier = Modifier.weight(1f),
        )

        if (showChevron) {
            ForwardChevronIcon(tint = SuitColors.Slate, size = 18.dp)
        }
    }
}
