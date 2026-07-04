package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn

@Composable
fun EditorStepIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    val labels = listOf("Partes", "Cores e tecidos", "Preview")
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        labels.forEachIndexed { index, label ->
            val step = index + 1
            val active = step == currentStep
            val complete = step < currentStep
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (active || complete) SuitColors.Gold
                            else SuitColors.SurfaceLow
                        )
                        .border(
                            width = 1.dp,
                            color = if (active || complete) SuitColors.Gold else SuitColors.Mist,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = step.toString(),
                        style = SuitTextStyles.labelMedium,
                        color = if (active || complete) SuitColors.GoldInk else SuitColors.Smoke,
                    )
                }
                Text(
                    text = label,
                    style = SuitTextStyles.labelSmall,
                    color = if (active) SuitColors.GoldChampagne else SuitColors.Smoke,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
fun EditorOptionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
) {
    Column(
        modifier = modifier
            .heightIn(min = 64.dp)
            .clip(SuitTheme.shapes.md)
            .background(if (selected) SuitColors.SurfaceHigh else SuitColors.SurfaceLow)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) SuitColors.Gold else SuitColors.Mist,
                shape = SuitTheme.shapes.md,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = SuitTextStyles.labelLarge.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (selected) SuitColors.GoldChampagne else SuitColors.Pearl,
        )
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = SuitTextStyles.labelSmall,
                color = SuitColors.Smoke,
            )
        }
    }
}

@Composable
fun CustomizationSummaryCard(
    modelName: String,
    details: List<Pair<String, String>>,
    totalMzn: Int,
    modifier: Modifier = Modifier,
) {
    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Resumo da configuração",
                style = SuitTextStyles.titleLarge,
                color = SuitColors.Pearl,
            )
            SummaryLine("Modelo", modelName)
            details.forEach { (label, value) -> SummaryLine(label, value) }
            HorizontalDivider(color = SuitColors.Mist)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Total estimado", style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
                Text(
                    formatMzn(totalMzn),
                    style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SuitColors.GoldChampagne,
                )
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        Text(
            text = value,
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Pearl,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
