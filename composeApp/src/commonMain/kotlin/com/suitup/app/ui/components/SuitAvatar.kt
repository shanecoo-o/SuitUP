package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Avatar circular com iniciais.
 *
 * Por defeito Ink background + iniciais brancas — mantém a estética alfaiataria.
 * Aceita variant com background customizado para diferenciação visual em listas.
 */
@Composable
fun SuitAvatar(
    iniciais: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    background: Color = SuitColors.Ink,
    foreground: Color = SuitColors.SurfaceWhite,
) {
    // Escala da font baseada no tamanho (rule: ~38% do diameter)
    val fontScale = (size.value * 0.38f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciais.uppercase().take(2),
            style = SuitTextStyles.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = fontScale,
            ),
            color = foreground,
        )
    }
}
