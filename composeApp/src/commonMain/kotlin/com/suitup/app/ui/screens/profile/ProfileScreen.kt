package com.suitup.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Utilizador
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.ProfileHeaderCard
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitMenuRow

@Composable
fun ProfileScreen(
    user: Utilizador,
    cartItemCount: Int = 0,
    orderCount: Int = 0,
    onCartClick: () -> Unit = {},
    onMyData: () -> Unit = {},
    onAddresses: () -> Unit = {},
    onPaymentMethods: () -> Unit = {},
    onSavedMeasurements: () -> Unit = {},
    onOrders: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PremiumTopBar(title = "Perfil", onCart = onCartClick, cartBadgeCount = cartItemCount)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ProfileHeaderCard(user = user, orderCount = orderCount, cartCount = cartItemCount)
            SectionHeader(
                eyebrow = "CONTA",
                title = "Gerir perfil",
                description = "Dados, medidas e preferências da sua conta.",
            )
            PremiumCard(padding = 12.dp) {
                Column {
                    val future = "Disponível numa próxima fase"
                    SuitMenuRow("Dados pessoais", onMyData, enabled = false, supportingText = future)
                    SuitMenuRow("Endereços", onAddresses, enabled = false, supportingText = future)
                    SuitMenuRow("Medidas", onSavedMeasurements, enabled = false, supportingText = future)
                    SuitMenuRow("Pedidos", onOrders, supportingText = "Consultar e acompanhar encomendas")
                    SuitMenuRow("Pagamentos", onPaymentMethods, enabled = false, supportingText = future)
                    SuitMenuRow("Preferências", onSettings, enabled = false, supportingText = future)
                    SuitMenuRow("Notificações", onNotifications, enabled = false, supportingText = future)
                }
            }
            PremiumCard(padding = 12.dp) {
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
