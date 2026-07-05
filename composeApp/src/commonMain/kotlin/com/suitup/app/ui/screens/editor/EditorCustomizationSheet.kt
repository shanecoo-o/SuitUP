package com.suitup.app.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.SuitFilterChip
import com.suitup.app.ui.components.SuitIconButton
import com.suitup.app.ui.icons.CloseIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Adaptive bottom customization sheet for the Editor 2D stage (Phase 9.5A, Task 6).
 * Height is a fixed fraction of the actual measured screen height (not [heightIn] max)
 * so the internal scrollable [body] can use `Modifier.weight(1f)` correctly — 85% on
 * short-height devices, 70% otherwise, per [SuitResponsiveInfo.isShortHeight].
 */
@Composable
fun EditorCustomizationSheet(
    selectedCategory: EditorHotspotCategory,
    onCategorySelect: (EditorHotspotCategory) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    body: @Composable () -> Unit,
) {
    val responsive = SuitTheme.responsive
    val heightFraction = if (responsive.isShortHeight) 0.85f else 0.70f
    val sheetHeight = responsive.screenHeight * heightFraction

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(sheetHeight)
            .clip(SuitTheme.shapes.sheet)
            .background(SuitColors.SurfaceRaised),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(SuitTheme.shapes.pill)
                    .background(SuitColors.Mist),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = responsive.horizontalContentPadding, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(selectedCategory.label, style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
            SuitIconButton(onClick = onClose) { tint -> CloseIcon(tint = tint) }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = responsive.horizontalContentPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(EditorHotspotCategory.all()) { category ->
                SuitFilterChip(
                    text = category.label,
                    selected = category == selectedCategory,
                    onClick = { onCategorySelect(category) },
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = SuitColors.Mist)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = responsive.horizontalContentPadding, vertical = 16.dp),
        ) {
            body()
        }
    }
}
