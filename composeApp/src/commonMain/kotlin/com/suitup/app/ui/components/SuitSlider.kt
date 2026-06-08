package com.suitup.app.ui.components

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.suitup.app.ui.theme.SuitColors

/**
 * Slider SuitUP — minimalista, track 1dp, thumb pequeno preto.
 *
 * Wrapper sobre o Material3 Slider com cores override para a estética alfaiataria.
 */
@Composable
fun SuitSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = SuitColors.Ink,
            activeTrackColor = SuitColors.Ink,
            inactiveTrackColor = SuitColors.Mist,
            activeTickColor = SuitColors.Ink,
            inactiveTickColor = SuitColors.Mist,
        ),
    )
}
