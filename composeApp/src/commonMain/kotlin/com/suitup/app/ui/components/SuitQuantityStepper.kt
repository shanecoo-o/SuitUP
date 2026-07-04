package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.MinusIcon
import com.suitup.app.ui.icons.PlusIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Stepper de quantidade: [-] qty [+]
 *
 * Container com border 1dp Mist, 3 zonas. Botões − e + têm scale 0.92 em pressed
 * (via Modifier.clickable default).
 *
 * Decrementar para 0 é prevenido — clique no − em qty=1 não faz nada (callback não chama).
 * Usar [minValue] e [maxValue] para controlar limites.
 */
@Composable
fun SuitQuantityStepper(
    quantidade: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 1,
    maxValue: Int = 99,
) {
    val canDecrement = quantidade > minValue
    val canIncrement = quantidade < maxValue

    Row(
        modifier = modifier
            .clip(SuitTheme.shapes.input)
            .border(1.dp, SuitColors.Mist, SuitTheme.shapes.input)
            .background(SuitColors.SurfaceLow)
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        StepperButton(
            enabled = canDecrement,
            onClick = { if (canDecrement) onQuantityChange(quantidade - 1) },
        ) {
            MinusIcon(
                tint = if (canDecrement) SuitColors.Gold else SuitColors.Smoke,
                size = 14.dp,
            )
        }

        Box(
            modifier = Modifier
                .width(36.dp)
                .height(36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = quantidade.toString(),
                style = SuitTextStyles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = SuitColors.Ink,
            )
        }

        StepperButton(
            enabled = canIncrement,
            onClick = { if (canIncrement) onQuantityChange(quantidade + 1) },
        ) {
            PlusIcon(
                tint = if (canIncrement) SuitColors.Gold else SuitColors.Smoke,
                size = 14.dp,
            )
        }
    }
}

@Composable
private fun StepperButton(
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .background(SuitColors.SurfaceLow),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
