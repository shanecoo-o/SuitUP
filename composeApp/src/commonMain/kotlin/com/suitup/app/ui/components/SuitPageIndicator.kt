package com.suitup.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitAnim
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTheme

/**
 * Page indicator — dots horizontais.
 *
 * O dot ativo é mais largo (pill 24x6dp), os inativos circulares (6x6dp).
 * Anima a transição de largura.
 */
@Composable
fun SuitPageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
) {
    val activeColor: Color = if (dark) SuitColors.SurfaceWhite else SuitColors.Ink
    val inactiveColor: Color = if (dark) SuitColors.SurfaceWhite.copy(alpha = 0.30f) else SuitColors.Smoke

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(totalPages) { index ->
            val isActive = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 6.dp,
                animationSpec = SuitAnim.normal(),
                label = "dot-$index-width"
            )
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(width)
                    .clip(SuitTheme.shapes.pill)
                    .background(if (isActive) activeColor else inactiveColor)
            )
        }
    }
}
