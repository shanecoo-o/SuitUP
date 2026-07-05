package com.suitup.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Utilizador
import com.suitup.app.ui.components.ProfileHeaderCard
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitMenuRow
import com.suitup.app.ui.components.SuitPrimaryTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Ecrã "Perfil" (Fase 9.6C). Migrado para a linguagem Stitch. Mantém o
 * ScreenModel/estado existentes (PerfilScreenModel) — apenas apresentação.
 *
 * Dados pessoais, Endereços, Medidas, Notificações e Ajuda/Suporte deixam de
 * ser placeholders "Disponível numa próxima fase" nesta fase — cada um tem
 * agora um destino real (ver ProfileDestinationScreens.kt). Pagamentos e
 * Preferências permanecem fora do âmbito desta fase e continuam desactivados.
 */
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
    onSupport: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    val horizontalPadding = SuitTheme.responsive.horizontalContentPadding
    Column(modifier = Modifier.fillMaxSize().background(SuitColors.Bone)) {
        SuitPrimaryTopBar(title = "Perfil", onCart = onCartClick, cartBadgeCount = cartItemCount)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileHeaderCard(user = user, orderCount = orderCount, cartCount = cartItemCount)
            Text("Gerir perfil", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
            SuitCard(padding = 12.dp) {
                Column {
                    val future = "Disponível numa próxima fase"
                    SuitMenuRow("Dados pessoais", onMyData, supportingText = "Nome, email e telefone")
                    SuitMenuRow("Endereços", onAddresses, supportingText = "Moradas usadas nos seus pedidos")
                    SuitMenuRow("Medidas", onSavedMeasurements, supportingText = "As suas medidas guardadas")
                    SuitMenuRow("Pedidos", onOrders, supportingText = "Consultar e acompanhar encomendas")
                    SuitMenuRow("Pagamentos", onPaymentMethods, enabled = false, supportingText = future)
                    SuitMenuRow("Preferências", onSettings, enabled = false, supportingText = future)
                    SuitMenuRow("Notificações", onNotifications, supportingText = "Estado das suas notificações")
                    SuitMenuRow(
                        "Ajuda e suporte",
                        onSupport,
                        showChevron = true,
                        supportingText = "Contactos e perguntas frequentes",
                    )
                }
            }
            SuitCard(padding = 12.dp) {
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
