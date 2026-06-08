package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Eyebrow — pequeno tag em uppercase com letter-spacing largo.
 * Usado antes de títulos importantes.
 */
@Composable
fun SuitEyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = SuitColors.Slate,
) {
    Text(
        text = text.uppercase(),
        style = SuitTextStyles.eyebrow,
        color = color,
        modifier = modifier,
    )
}

enum class SuitStatusKind { Success, Pendente, Error, Info, Neutral }

/**
 * Status badge — pill com fundo pastel desaturado e texto na cor "ink" do mesmo tom.
 */
@Composable
fun SuitStatusBadge(
    text: String,
    kind: SuitStatusKind,
    modifier: Modifier = Modifier,
) {
    val (bg, fg) = when (kind) {
        SuitStatusKind.Success -> SuitColors.PaleGreen to SuitColors.PaleGreenInk
        SuitStatusKind.Pendente -> SuitColors.PaleAmber to SuitColors.PaleAmberInk
        SuitStatusKind.Error -> SuitColors.PaleRed to SuitColors.PaleRedInk
        SuitStatusKind.Info -> SuitColors.PaleBlue to SuitColors.PaleBlueInk
        SuitStatusKind.Neutral -> SuitColors.Pearl to SuitColors.Slate
    }

    Text(
        text = text.uppercase(),
        style = SuitTextStyles.eyebrow,
        color = fg,
        modifier = modifier
            .clip(SuitTheme.shapes.pill)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
