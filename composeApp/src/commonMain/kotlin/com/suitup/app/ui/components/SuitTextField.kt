package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Input field SuitUP.
 *
 * Princípio: label acima do input, helper abaixo, error state inline.
 * Bordas hairline, focus altera para Ink.
 */
@Composable
fun SuitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    helper: String? = null,
    error: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        error != null -> SuitColors.PaleRedInk
        isFocused -> SuitColors.GoldChampagne
        else -> SuitColors.Mist
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = SuitTextStyles.labelMedium,
            color = SuitTheme.colors.slate,
        )

        Row(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(52.dp)
                .clip(SuitTheme.shapes.input)
                .background(SuitColors.WarmBlack)
                .border(1.dp, borderColor, SuitTheme.shapes.input)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon?.invoke()
            Column(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = singleLine,
                    cursorBrush = SolidColor(SuitColors.Gold),
                    interactionSource = interactionSource,
                    textStyle = SuitTextStyles.bodyMedium.copy(color = SuitColors.Ink),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = if (isPassword) PasswordVisualTransformation() else visualTransformation,
                    decorationBox = { inner ->
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(placeholder, style = SuitTextStyles.bodyMedium, color = SuitColors.Smoke)
                        }
                        inner()
                    }
                )
            }
            trailingIcon?.invoke()
        }

        if (error != null) {
            Text(
                text = error,
                style = SuitTextStyles.bodySmall,
                color = SuitColors.PaleRedInk,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else if (helper != null) {
            Text(
                text = helper,
                style = SuitTextStyles.bodySmall,
                color = SuitTheme.colors.slate,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
