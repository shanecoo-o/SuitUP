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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = 20.dp,
    content: @Composable () -> Unit,
) {
    SuitCard(
        modifier = modifier,
        onClick = onClick,
        background = SuitColors.SlateSurface,
        padding = padding,
        content = content,
    )
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    accent: Color = SuitColors.GoldChampagne,
) {
    PremiumCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
            Text(value, style = SuitTextStyles.titleLarge, color = accent)
            if (supportingText != null) {
                Text(supportingText, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    description: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (eyebrow != null) SuitEyebrow(eyebrow, color = SuitColors.GoldChampagne)
            Text(title, style = SuitTextStyles.headlineMedium, color = SuitColors.Pearl)
            if (description != null) {
                Text(description, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
            }
        }
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = SuitTextStyles.labelMedium,
                color = SuitColors.GoldChampagne,
                modifier = Modifier
                    .clickable(onClick = onAction)
                    .padding(8.dp),
            )
        }
    }
}

@Composable
fun PrimaryGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
) {
    SuitButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        fullWidth = fullWidth,
        variant = SuitButtonVariant.Primary,
    )
}

@Composable
fun SecondaryDarkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
) {
    SuitButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        fullWidth = fullWidth,
        variant = SuitButtonVariant.Secondary,
    )
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    helper: String? = null,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    SuitTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        helper = helper,
        error = error,
        keyboardType = keyboardType,
        enabled = enabled,
        isPassword = isPassword,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
    )
}

@Composable
fun <T> PremiumDropdown(
    options: List<T>,
    selectedOption: T,
    onSelect: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SuitDropdown(
        options = options,
        selectedOption = selectedOption,
        onSelect = onSelect,
        optionLabel = optionLabel,
        modifier = modifier,
        enabled = enabled,
    )
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            icon?.invoke()
            Text(title, style = SuitTextStyles.titleLarge, color = SuitColors.Pearl)
            Text(description, style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
            if (actionLabel != null && onAction != null) {
                PrimaryGoldButton(
                    text = actionLabel,
                    onClick = onAction,
                    fullWidth = false,
                )
            }
        }
    }
}

@Composable
fun AdminActionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
) {
    PremiumCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(SuitTheme.shapes.md)
                        .background(SuitColors.WarmBlack)
                        .border(1.dp, SuitColors.BronzeSubtle.copy(alpha = 0.5f), SuitTheme.shapes.md),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
                Text(description, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
            }
        }
    }
}

@Composable
fun PremiumTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    onCart: (() -> Unit)? = null,
    cartBadgeCount: Int = 0,
    trailing: (@Composable () -> Unit)? = null,
) {
    SuitTopBar(
        modifier = modifier.background(SuitColors.Charcoal),
        title = title,
        onBack = onBack,
        onCart = onCart,
        cartBadgeCount = cartBadgeCount,
        dark = true,
        trailing = trailing,
    )
}

@Composable
fun PremiumBottomNavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = if (selected) SuitColors.GoldChampagne else SuitColors.Smoke
    Column(
        modifier = modifier
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        icon(tint)
        Text(label, style = SuitTextStyles.labelSmall, color = tint)
    }
}
