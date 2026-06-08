package com.suitup.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
 * Card selecionável com ícone + título + descrição + indicator de seleção.
 *
 * Usado para escolher tipo de entrega (Entrega/Levantamento), endereços salvos,
 * métodos de pagamento, etc.
 *
 * Princípios:
 * - Border 1dp Mist (unselected) → 1.5dp Ink (selected)
 * - Indicator radio à direita: círculo vazio → preto preenchido com check-dot branco
 * - Sem shadows, sem fundos cinza — só border e borderWidth diferenciam estados
 */
@Composable
fun SuitSelectableCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) SuitColors.Ink else SuitColors.Mist,
        animationSpec = SuitAnim.normal(),
        label = "card-border"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (selected) 1.5.dp else 1.dp,
        animationSpec = SuitAnim.normal(),
        label = "card-border-width"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SuitTheme.shapes.card)
            .background(SuitColors.SurfaceWhite)
            .border(borderWidth, borderColor, SuitTheme.shapes.card)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                leadingIcon()
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = SuitTextStyles.titleMedium,
                color = SuitColors.Ink,
            )
            Text(
                text = description,
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }

        SelectionIndicator(selected = selected)
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (selected) SuitColors.Ink else SuitColors.SurfaceWhite)
            .border(
                width = if (selected) 0.dp else 1.5.dp,
                color = if (selected) SuitColors.Ink else SuitColors.Mist,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            // Dot branco interno
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SuitColors.SurfaceWhite)
            )
        }
    }
}
