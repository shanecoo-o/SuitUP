package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.CloseIcon
import com.suitup.app.ui.icons.ErrorIcon
import com.suitup.app.ui.icons.InfoIcon
import com.suitup.app.ui.icons.OfflineIcon
import com.suitup.app.ui.icons.CheckIcon
import com.suitup.app.ui.icons.WarningIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Alert/banner variants (Task 10). Purely visual — callers own any resulting
 * navigation or retry behavior triggered by [onAction]/[onDismiss].
 */
enum class SuitAlertVariant { Info, Success, Warning, Error, Offline }

private data class AlertColors(val bg: Color, val fg: Color)

private fun colorsFor(variant: SuitAlertVariant): AlertColors = when (variant) {
    SuitAlertVariant.Info -> AlertColors(SuitColors.PaleBlue, SuitColors.PaleBlueInk)
    SuitAlertVariant.Success -> AlertColors(SuitColors.PaleGreen, SuitColors.PaleGreenInk)
    SuitAlertVariant.Warning -> AlertColors(SuitColors.PaleAmber, SuitColors.PaleAmberInk)
    SuitAlertVariant.Error -> AlertColors(SuitColors.PaleRed, SuitColors.PaleRedInk)
    SuitAlertVariant.Offline -> AlertColors(SuitColors.SurfaceHigh, SuitColors.Smoke)
}

@Composable
fun SuitAlertBanner(
    variant: SuitAlertVariant,
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val colors = colorsFor(variant)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SuitTheme.shapes.md)
            .background(colors.bg)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        when (variant) {
            SuitAlertVariant.Info -> InfoIcon(size = SuitTheme.iconSizes.standard, tint = colors.fg)
            SuitAlertVariant.Success -> CheckIcon(size = SuitTheme.iconSizes.standard, tint = colors.fg)
            SuitAlertVariant.Warning -> WarningIcon(size = SuitTheme.iconSizes.standard, tint = colors.fg)
            SuitAlertVariant.Error -> ErrorIcon(size = SuitTheme.iconSizes.standard, tint = colors.fg)
            SuitAlertVariant.Offline -> OfflineIcon(size = SuitTheme.iconSizes.standard, tint = colors.fg)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (title != null) {
                Text(title, style = SuitTextStyles.titleMedium, color = colors.fg)
            }
            Text(message, style = SuitTextStyles.bodySmall, color = colors.fg)
            if (actionLabel != null && onAction != null) {
                Text(
                    text = actionLabel,
                    style = SuitTextStyles.labelMedium,
                    color = colors.fg,
                    modifier = Modifier.clickable(onClick = onAction),
                )
            }
        }
        if (onDismiss != null) {
            CloseIcon(
                size = SuitTheme.iconSizes.small,
                tint = colors.fg,
                modifier = Modifier.clickable(onClick = onDismiss),
            )
        }
    }
}
