package com.suitup.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.ForwardChevronIcon
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Botão "Seguinte" estilo editorial — texto + arrow circular.
 *
 * Usado em ecrãs onde o botão Primary cheio é demasiado pesado:
 * onboarding, steps de configuração, calls-to-action subtis.
 *
 * Versões light (texto Ink, arrow circle Ink) e dark (texto branco, arrow circle branco).
 */
@Composable
fun SuitNextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = SuitAnim.fast(),
        label = "next-press"
    )

    val fg: Color = when {
        !enabled -> SuitColors.Smoke
        dark -> SuitColors.SurfaceWhite
        else -> SuitColors.Ink
    }
    val circleBg: Color = when {
        !enabled -> SuitColors.Mist
        dark -> SuitColors.SurfaceWhite
        else -> SuitColors.Ink
    }
    val arrowTint: Color = when {
        !enabled -> SuitColors.Smoke
        dark -> SuitColors.Ink
        else -> SuitColors.SurfaceWhite
    }

    Row(
        modifier = modifier
            .scale(pressScale)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = text,
            style = SuitTextStyles.button,
            color = fg,
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(circleBg),
            contentAlignment = Alignment.Center
        ) {
            ForwardChevronIcon(size = 20.dp, tint = arrowTint)
        }
    }
}
