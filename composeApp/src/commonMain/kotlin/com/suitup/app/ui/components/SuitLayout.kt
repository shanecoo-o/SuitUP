package com.suitup.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.suitup.app.ui.theme.SuitTheme

/**
 * Layout primitives built on responsive tokens (Task 20) — screens compose
 * these instead of hard-coding horizontal padding or a max content width.
 */

/** Horizontal content padding matching the current width class, applied to full-width content. */
@Composable
fun SuitContentColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val responsive = SuitTheme.responsive
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = responsive.horizontalContentPadding),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

/**
 * Caps content width on wide phones and centers it, per [SuitTheme.responsive]'s
 * `maxContentWidth` — a no-op on narrow/standard/medium widths.
 */
@Composable
fun SuitUpSection(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable () -> Unit,
) {
    val responsive = SuitTheme.responsive
    val maxWidth: Dp? = responsive.maxContentWidth
    Box(
        modifier = modifier
            .fillMaxWidth()
            .let { if (maxWidth != null) it.widthIn(max = maxWidth) else it },
        contentAlignment = contentAlignment,
    ) {
        content()
    }
}
