package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors

/**
 * Footer com 2 botões lado a lado (tipicamente Voltar + Continuar).
 *
 * Usado nos ecrãs do flow checkout (Tipo de entrega, Endereço, Pagamento).
 * Background Bone para destacar dos cards brancos acima.
 */
@Composable
fun SuitDualBottomBar(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String = "Voltar",
    onSecondaryClick: () -> Unit = {},
    primaryEnabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SuitColors.Bone)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SuitButton(
            text = secondaryText,
            onClick = onSecondaryClick,
            variant = SuitButtonVariant.Secondary,
            fullWidth = false,
            modifier = Modifier.weight(1f),
        )
        SuitButton(
            text = primaryText,
            onClick = onPrimaryClick,
            variant = SuitButtonVariant.Primary,
            fullWidth = false,
            enabled = primaryEnabled,
            modifier = Modifier.weight(1f),
        )
    }
}
