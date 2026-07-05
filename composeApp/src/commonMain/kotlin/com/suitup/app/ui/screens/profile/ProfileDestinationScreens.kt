package com.suitup.app.ui.screens.profile

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.Utilizador
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDetailScaffold
import com.suitup.app.ui.components.SuitDetailTopBar
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme

/**
 * Destinos de "Gerir perfil" (Fase 9.6C): Dados pessoais, Endereços, Medidas,
 * Notificações e Ajuda/Suporte. Nenhum destes ecrãs tem, neste repositório,
 * um backend de escrita (edição de perfil, morada persistente, notificações
 * ou contactos de suporte) — por isso mostram os dados reais disponíveis em
 * modo só-leitura ou um estado informativo honesto, em vez de simular
 * funcionalidade inexistente.
 */
@Composable
fun PersonalDataScreen(
    user: Utilizador,
    onBack: () -> Unit = {},
) {
    SuitDetailScaffold(
        topBar = { SuitDetailTopBar(title = "Dados pessoais", onBack = onBack) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SuitCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SuitTextField(value = user.nome, onValueChange = {}, label = "Nome", readOnly = true, enabled = false)
                    SuitTextField(value = user.email, onValueChange = {}, label = "Email", readOnly = true, enabled = false)
                    SuitTextField(value = user.telefone, onValueChange = {}, label = "Telefone", readOnly = true, enabled = false)
                }
            }
            Text(
                "A edição de dados pessoais ainda não está disponível.",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Smoke,
            )
        }
    }
}

@Composable
fun AddressesScreen(
    onBack: () -> Unit = {},
) {
    SuitDetailScaffold(
        topBar = { SuitDetailTopBar(title = "Endereços", onBack = onBack) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 18.dp),
        ) {
            EmptyStateCard(
                title = "Sem moradas guardadas",
                description = "As moradas que utilizar nas suas encomendas ficam associadas a cada pedido, mas ainda não existe um livro de endereços para consultar aqui.",
            )
        }
    }
}

@Composable
fun MeasurementsScreen(
    user: Utilizador,
    onBack: () -> Unit = {},
) {
    SuitDetailScaffold(
        topBar = { SuitDetailTopBar(title = "Medidas", onBack = onBack) },
    ) {
        val medidas = user.medidasGuardadas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (medidas == null) {
                EmptyStateCard(
                    title = "Sem medidas guardadas",
                    description = "Ainda não guardou as suas medidas. Elas ficam disponíveis aqui assim que forem indicadas num pedido.",
                )
            } else {
                SuitCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        MeasurementRow("Altura", medidas.alturaCm)
                        MeasurementRow("Peso", medidas.pesoKg, unit = "kg")
                        MeasurementRow("Ombros", medidas.ombrosCm)
                        MeasurementRow("Peito", medidas.peitoCm)
                        MeasurementRow("Cintura", medidas.cinturaCm)
                        MeasurementRow("Quadril", medidas.quadrilCm)
                        MeasurementRow("Manga", medidas.mangaCm)
                        MeasurementRow("Calça", medidas.calcaCm)
                        MeasurementRow("Casaco", medidas.casacoCm)
                        MeasurementRow("Pescoço", medidas.pescocoCm)
                    }
                }
                if (medidas.observacoes.isNotBlank()) {
                    SuitCard {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Observações", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                            Text(medidas.observacoes, style = SuitTextStyles.bodyMedium, color = SuitColors.Pearl)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
) {
    SuitDetailScaffold(
        topBar = { SuitDetailTopBar(title = "Notificações", onBack = onBack) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 18.dp),
        ) {
            EmptyStateCard(
                title = "Sem notificações",
                description = "Ainda não tem notificações. Vai encontrar aqui novidades sobre os seus pedidos assim que existirem.",
            )
        }
    }
}

@Composable
fun SupportScreen(
    onBack: () -> Unit = {},
) {
    SuitDetailScaffold(
        topBar = { SuitDetailTopBar(title = "Ajuda e suporte", onBack = onBack) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SuitTheme.responsive.horizontalContentPadding, vertical = 18.dp),
        ) {
            EmptyStateCard(
                title = "Contactos de suporte indisponíveis",
                description = "Ainda não existe um canal de contacto configurado nesta versão da aplicação. Acompanhe o estado do seu pedido em \"Pedidos\".",
            )
        }
    }
}

@Composable
private fun MeasurementRow(label: String, value: String, unit: String = "cm") {
    if (value.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Smoke)
        Text("$value $unit", style = SuitTextStyles.bodyMedium, color = SuitColors.Pearl)
    }
}
