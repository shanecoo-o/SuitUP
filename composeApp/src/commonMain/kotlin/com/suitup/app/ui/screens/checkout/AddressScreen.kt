package com.suitup.app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.EnderecoEntrega
import com.suitup.app.domain.model.TipoEntrega
import com.suitup.app.domain.model.PontoLevantamento
import com.suitup.app.ui.components.SuitDropdown
import com.suitup.app.ui.components.SuitDualBottomBar
import com.suitup.app.ui.components.SuitSegmentedToggle
import com.suitup.app.ui.components.SuitSelectableCard
import com.suitup.app.ui.components.SuitStepIndicator
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.icons.PinIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Ecrã 11 — Checkout · Endereço / Ponto.
 *
 * Step 2 de 4 (continuação do tipo de entrega). Toggle no topo permite
 * alternar entre Entregar (form de endereço) e Levantar (lista de pontos).
 *
 * Quando [mode] = Entrega: mostra dropdowns Cidade/Bairro + inputs Rua/Referência
 * Quando [mode] = Levantamento: mostra lista de PontoLevantamento selecionáveis
 */
@Composable
fun AddressScreen(
    mode: TipoEntrega,
    endereco: EnderecoEntrega,
    cities: List<String>,
    neighborhoods: List<String>,
    pontosLevantamento: List<PontoLevantamento>,
    selectedPickupPoint: PontoLevantamento?,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onModeChange: (TipoEntrega) -> Unit = {},
    onCityChange: (String) -> Unit = {},
    onNeighborhoodChange: (String) -> Unit = {},
    onStreetChange: (String) -> Unit = {},
    onReferenceChange: (String) -> Unit = {},
    onPickupPointSelect: (PontoLevantamento) -> Unit = {},
    onContinue: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
            centerContent = { SuitStepIndicator(currentStep = 3, totalSteps = 5) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Toggle Entregar / Levantar
            SuitSegmentedToggle(
                options = listOf(TipoEntrega.Entrega, TipoEntrega.Levantamento),
                selectedOption = mode,
                onSelect = onModeChange,
                optionLabel = {
                    when (it) {
                        TipoEntrega.Entrega -> "Entregar"
                        TipoEntrega.Levantamento -> "Levantar"
                    }
                },
            )

            when (mode) {
                TipoEntrega.Entrega -> DeliveryForm(
                    endereco = endereco,
                    cities = cities,
                    neighborhoods = neighborhoods,
                    onCityChange = onCityChange,
                    onNeighborhoodChange = onNeighborhoodChange,
                    onStreetChange = onStreetChange,
                    onReferenceChange = onReferenceChange,
                )
                TipoEntrega.Levantamento -> PickupForm(
                    points = pontosLevantamento,
                    selected = selectedPickupPoint,
                    onSelect = onPickupPointSelect,
                )
            }
        }

        val canContinue = when (mode) {
            TipoEntrega.Entrega -> endereco.cidade.isNotBlank() && endereco.bairro.isNotBlank() && endereco.rua.isNotBlank()
            TipoEntrega.Levantamento -> selectedPickupPoint != null
        }

        SuitDualBottomBar(
            primaryText = "Continuar",
            onPrimaryClick = onContinue,
            onSecondaryClick = onBack,
            primaryEnabled = canContinue,
        )
    }
}

@Composable
private fun DeliveryForm(
    endereco: EnderecoEntrega,
    cities: List<String>,
    neighborhoods: List<String>,
    onCityChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onReferenceChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Endereço de entrega",
            style = SuitTextStyles.titleMedium,
            color = SuitColors.Ink,
            modifier = Modifier.padding(top = 4.dp),
        )

        // Cidade
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Cidade",
                style = SuitTextStyles.labelMedium,
                color = SuitColors.Slate,
            )
            SuitDropdown(
                options = cities,
                selectedOption = endereco.cidade.ifBlank { cities.first() },
                onSelect = onCityChange,
                optionLabel = { it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Bairro
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Bairro",
                style = SuitTextStyles.labelMedium,
                color = SuitColors.Slate,
            )
            SuitDropdown(
                options = neighborhoods,
                selectedOption = endereco.bairro.ifBlank { neighborhoods.first() },
                onSelect = onNeighborhoodChange,
                optionLabel = { it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Rua / Av.
        SuitTextField(
            value = endereco.rua,
            onValueChange = onStreetChange,
            label = "Rua / Av.",
            placeholder = "Av. Julius Nyerere, 123",
        )

        // Referência (opcional)
        SuitTextField(
            value = endereco.referencia.orEmpty(),
            onValueChange = onReferenceChange,
            label = "Referência (opcional)",
            placeholder = "Próximo ao Shopping Polana",
        )
    }
}

@Composable
private fun PickupForm(
    points: List<PontoLevantamento>,
    selected: PontoLevantamento?,
    onSelect: (PontoLevantamento) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Pontos de levantamento",
            style = SuitTextStyles.titleMedium,
            color = SuitColors.Ink,
            modifier = Modifier.padding(top = 4.dp),
        )

        if (points.isEmpty()) {
            Text(
                text = "Sem pontos disponíveis na sua área.",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Slate,
            )
        } else {
            points.forEach { point ->
                SuitSelectableCard(
                    title = point.nome,
                    description = point.endereco,
                    selected = selected?.id == point.id,
                    onClick = { onSelect(point) },
                    leadingIcon = { PinIcon(size = 22.dp) }
                )
            }
        }
    }
}
