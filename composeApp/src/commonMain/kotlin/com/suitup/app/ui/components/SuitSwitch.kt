package com.suitup.app.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.suitup.app.ui.theme.SuitColors

/**
 * Switch SuitUP — track Mist/Ink, thumb branco.
 *
 * Wrapper sobre Material3 Switch com cores override para a estética alfaiataria.
 */
@Composable
fun SuitSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = SuitColors.SurfaceWhite,
            checkedTrackColor = SuitColors.Ink,
            checkedBorderColor = SuitColors.Ink,
            uncheckedThumbColor = SuitColors.SurfaceWhite,
            uncheckedTrackColor = SuitColors.Mist,
            uncheckedBorderColor = SuitColors.Mist,
            disabledCheckedThumbColor = SuitColors.Smoke,
            disabledCheckedTrackColor = SuitColors.Mist,
            disabledUncheckedThumbColor = SuitColors.Smoke,
            disabledUncheckedTrackColor = SuitColors.Mist,
        ),
    )
}
