package com.suitup.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Utilizador
import com.suitup.app.ui.components.SuitAvatar
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitMenuRow
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecra 15 - Perfil.
 *
 * Bottom nav lives in MainShellScreen. This composable only renders the tab content.
 */
@Composable
fun ProfileScreen(
    user: Utilizador,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {},
    onMyData: () -> Unit = {},
    onAddresses: () -> Unit = {},
    onPaymentMethods: () -> Unit = {},
    onSavedMeasurements: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SuitCard(padding = 16.dp) {
                ProfileHeader(user = user)
            }

            SuitCard(padding = 8.dp) {
                Column {
                    SuitMenuRow(text = "Meus dados", onClick = onMyData)
                    SuitMenuRow(text = "Enderecos", onClick = onAddresses)
                    SuitMenuRow(text = "Metodos de pagamento", onClick = onPaymentMethods)
                    SuitMenuRow(text = "Medidas salvas", onClick = onSavedMeasurements)
                    SuitMenuRow(text = "Notificacoes", onClick = onNotifications)
                    SuitMenuRow(text = "Configuracoes", onClick = onSettings)
                }
            }

            SuitCard(padding = 8.dp) {
                SuitMenuRow(
                    text = "Sair",
                    onClick = onSignOut,
                    showChevron = false,
                    emphasized = true,
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: Utilizador) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SuitAvatar(
            iniciais = user.iniciais,
            size = 52.dp,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = user.nome,
                style = SuitTextStyles.titleLarge,
                color = SuitColors.Ink,
            )
            Text(
                text = user.email,
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
    }
}
