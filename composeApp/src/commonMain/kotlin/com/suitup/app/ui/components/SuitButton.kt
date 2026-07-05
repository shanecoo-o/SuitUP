package com.suitup.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * SuitUP button system — one shared foundation, semantic variants/sizes on top.
 *
 * Variants:
 * - Primary: restrained AccentGold fill, strong contrast — the single major action per screen.
 * - Secondary: dark surface + hairline border — secondary/lower-hierarchy action.
 * - SecondaryOnDark: transparent + hairline border, for use directly on dark hero/photo surfaces.
 * - Destructive: error-toned fill — irreversible/dangerous actions only, never gold.
 * - Text: transparent, no border, low-emphasis inline action.
 *
 * "Compact" from the design brief is not a separate variant — it is [SuitButtonSize.Small]
 * combined with Secondary/Text, since the only real difference is footprint, not color language.
 * Icon-only actions use the dedicated [SuitIconButton] below, sharing the same press/shape
 * foundation instead of overloading this component with an icon-only text-less mode.
 */
enum class SuitButtonVariant { Primary, Secondary, SecondaryOnDark, Destructive, Text }

/** Small(36dp) doubles as the "Compact" utility-action size (edit/view/apply chips of a card). */
enum class SuitButtonSize { Small, Medium, Large }

/** Minimum accessible touch target regardless of visual button height. */
private val MinTouchTarget = 44.dp

@Composable
fun SuitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SuitButtonVariant = SuitButtonVariant.Primary,
    size: SuitButtonSize = SuitButtonSize.Large,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    fullWidth: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isInteractive = enabled && !loading

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = SuitAnim.fast(),
        label = "press-scale"
    )

    val (bg, fg, border) = colorsFor(variant)
    val effectiveBg = if (enabled) bg else SuitColors.SurfaceHigh
    val effectiveFg = if (enabled) fg else SuitColors.Smoke

    val (height, hPadding, textStyle) = when (size) {
        SuitButtonSize.Small -> Triple(36.dp, 14.dp, SuitTextStyles.labelMedium)
        SuitButtonSize.Medium -> Triple(44.dp, 18.dp, SuitTextStyles.button)
        SuitButtonSize.Large -> Triple(52.dp, 22.dp, SuitTextStyles.button)
    }

    val shape = SuitTheme.shapes.button

    Box(
        modifier = modifier.let { if (fullWidth) it.fillMaxWidth() else it }.heightIn(min = MinTouchTarget),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .let { if (fullWidth) it.fillMaxWidth() else it }
                .scale(pressScale)
                .heightIn(min = height)
                .clip(shape)
                .background(effectiveBg)
                .let { m -> border?.let { m.border(it, shape) } ?: m }
                .clickable(
                    enabled = isInteractive,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = hPadding, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                SuitInlineLoading(size = textStyle.lineHeight.value.dp, color = effectiveFg)
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    leadingIcon?.invoke()
                    Text(text = text, style = textStyle, color = effectiveFg)
                    trailingIcon?.invoke()
                }
            }
        }
    }
}

private fun colorsFor(variant: SuitButtonVariant): Triple<Color, Color, BorderStroke?> = when (variant) {
    SuitButtonVariant.Primary -> Triple(SuitColors.Gold, SuitColors.GoldInk, null)
    SuitButtonVariant.Secondary -> Triple(SuitColors.WarmBlack, SuitColors.Pearl, BorderStroke(1.dp, SuitColors.BronzeSubtle))
    SuitButtonVariant.SecondaryOnDark -> Triple(Color.Transparent, SuitColors.Pearl, BorderStroke(1.dp, SuitColors.BronzeSubtle))
    SuitButtonVariant.Destructive -> Triple(SuitColors.Error, SuitColors.PearlText, null)
    SuitButtonVariant.Text -> Triple(Color.Transparent, SuitColors.GoldChampagne, null)
}

/**
 * Icon-only button — standard touch target, semantic icon size, shares the same
 * press-scale feedback as [SuitButton]. Used for compact utility actions (close,
 * back-on-toolbar, favorite) where a text label would be redundant.
 */
@Composable
fun SuitIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    selected: Boolean = false,
    size: Dp = MinTouchTarget,
    iconSize: Dp = SuitTheme.iconSizes.standard,
    icon: @Composable (tint: Color) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isInteractive = enabled && !loading

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = SuitAnim.fast(),
        label = "icon-press-scale"
    )

    val bg = when {
        !enabled -> Color.Transparent
        selected -> SuitColors.SurfaceHigh
        else -> Color.Transparent
    }
    val tint = when {
        !enabled -> SuitColors.Smoke
        selected -> SuitColors.Gold
        else -> SuitColors.Ink
    }

    Box(
        modifier = modifier
            .widthIn(min = size)
            .heightIn(min = size)
            .scale(pressScale)
            .clip(SuitTheme.shapes.md)
            .background(bg)
            .clickable(
                enabled = isInteractive,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            SuitInlineLoading(size = iconSize, color = tint)
        } else {
            Box(modifier = Modifier.size(iconSize), contentAlignment = Alignment.Center) {
                icon(tint)
            }
        }
    }
}
