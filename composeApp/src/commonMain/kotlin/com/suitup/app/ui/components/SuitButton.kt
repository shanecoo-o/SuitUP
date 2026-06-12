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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

enum class SuitButtonVariant { Primary, Secondary, SecondaryOnDark, Ghost, Gold }
enum class SuitButtonSize { Small, Medium, Large }

/**
 * Botão SuitUP.
 *
 * Princípios:
 * - Primary: fundo Ink (off-black), texto branco, sem shadow
 * - Tactile feedback: scale(0.98) em :active
 * - Bordas crisp 8px
 */
@Composable
fun SuitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SuitButtonVariant = SuitButtonVariant.Primary,
    size: SuitButtonSize = SuitButtonSize.Large,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    fullWidth: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = SuitAnim.fast(),
        label = "press-scale"
    )

    val (bg, fg, border) = when (variant) {
        SuitButtonVariant.Primary -> Triple<Color, Color, BorderStroke?>(SuitColors.Gold, SuitColors.GoldInk, null)
        SuitButtonVariant.Gold -> Triple<Color, Color, BorderStroke?>(SuitColors.Gold, SuitColors.GoldInk, null)
        SuitButtonVariant.Secondary -> Triple<Color, Color, BorderStroke?>(SuitColors.SurfaceLow, SuitColors.Ink, BorderStroke(1.dp, SuitColors.Mist))
        SuitButtonVariant.SecondaryOnDark -> Triple<Color, Color, BorderStroke?>(Color.Transparent, SuitColors.Ink, BorderStroke(1.dp, SuitColors.Mist))
        SuitButtonVariant.Ghost -> Triple<Color, Color, BorderStroke?>(Color.Transparent, SuitColors.Ink, null)
    }

    val effectiveBg = if (enabled) bg else SuitColors.SurfaceHigh
    val effectiveFg = if (enabled) fg else SuitColors.Smoke

    val (height, hPadding, textStyle) = when (size) {
        SuitButtonSize.Small -> Triple(36.dp, 14.dp, SuitTextStyles.labelMedium)
        SuitButtonSize.Medium -> Triple(44.dp, 18.dp, SuitTextStyles.button)
        SuitButtonSize.Large -> Triple(52.dp, 22.dp, SuitTextStyles.button)
    }

    val shape = SuitTheme.shapes.button

    Box(
        modifier = modifier
            .let { if (fullWidth) it.fillMaxWidth() else it }
            .scale(pressScale)
            .heightIn(min = height)
            .clip(shape)
            .background(effectiveBg)
            .let { m -> border?.let { m.border(it, shape) } ?: m }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = hPadding, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
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
