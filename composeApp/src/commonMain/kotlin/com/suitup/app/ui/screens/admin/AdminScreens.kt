package com.suitup.app.ui.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.SuitModel
import com.suitup.app.ui.components.SuitButton
import com.suitup.app.ui.components.SuitButtonSize
import com.suitup.app.ui.components.SuitButtonVariant
import com.suitup.app.ui.components.SuitCard
import com.suitup.app.ui.components.SuitDropdown
import com.suitup.app.ui.components.SuitStatusBadge
import com.suitup.app.ui.components.SuitStatusKind
import com.suitup.app.ui.components.SuitTextField
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMetical
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import suitup.composeapp.generated.resources.Res
import suitup.composeapp.generated.resources.suit_casual_linen
import suitup.composeapp.generated.resources.suit_classic_black
import suitup.composeapp.generated.resources.suit_grey_slim
import suitup.composeapp.generated.resources.suit_navy_business

data class AdminDashboardStats(
    val totalModels: Int,
    val activeModels: Int,
    val inactiveModels: Int,
    val estimatedRevenueMt: Int,
    val totalOrders: Int,
    val pendingPayments: Int,
)

data class AdminSuitFormState(
    val id: String = "",
    val name: String = "",
    val category: String = AdminCatalogOptions.categories.first(),
    val description: String = "",
    val basePrice: String = "",
    val fabricType: String = AdminCatalogOptions.fabrics.first(),
    val color: String = "",
    val available: Boolean = true,
    val imageKey: String = AdminCatalogOptions.imageKeys.first(),
)

object AdminCatalogOptions {
    val categories = listOf("Clássico", "Executivo", "Slim Fit", "Casual", "Premium", "Gala")
    val fabrics = listOf("Lã Premium", "Algodão", "Linho", "Cashmere", "Tweed", "Veludo")
    val availability = listOf(true, false)
    val imageKeys = listOf(
        "suit_classic_black",
        "suit_navy_business",
        "suit_grey_slim",
        "suit_casual_linen",
    )
}

@Composable
fun AdminDashboardScreen(
    stats: AdminDashboardStats,
    onBack: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onAddSuitClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(title = "Admin", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Painel administrativo", style = SuitTextStyles.headlineLarge, color = SuitColors.Ink)
                    Text("Resumo local para demonstração.", style = SuitTextStyles.bodyMedium, color = SuitColors.Slate)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        StatCard("Total de modelos", stats.totalModels.toString(), Modifier.weight(1f))
                        StatCard("Modelos activos", stats.activeModels.toString(), Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        StatCard("Modelos inactivos", stats.inactiveModels.toString(), Modifier.weight(1f))
                        StatCard("Receita estimada demo", formatMetical(stats.estimatedRevenueMt), Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        StatCard("Pedidos totais demo", stats.totalOrders.toString(), Modifier.weight(1f))
                        StatCard("Pagamentos pendentes demo", stats.pendingPayments.toString(), Modifier.weight(1f))
                    }
                }
            }

            item {
                SuitCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Acções rápidas", style = SuitTextStyles.titleLarge, color = SuitColors.Ink)
                        SuitButton("Gerir Catálogo", onClick = onCatalogClick)
                        SuitButton("Adicionar Fato", onClick = onAddSuitClick, variant = SuitButtonVariant.Secondary)
                        SuitButton("Ver Pedidos · em breve", onClick = {}, enabled = false, variant = SuitButtonVariant.Secondary)
                        SuitButton("Confirmar Pagamentos · em breve", onClick = {}, enabled = false, variant = SuitButtonVariant.Secondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    SuitCard(modifier = modifier, padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
            Text(value, style = SuitTextStyles.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = SuitColors.Gold)
        }
    }
}

@Composable
fun AdminCatalogScreen(
    models: List<SuitModel>,
    onBack: () -> Unit = {},
    onAddSuit: () -> Unit = {},
    onEditSuit: (String) -> Unit = {},
    onDeactivate: (String) -> Unit = {},
    onReactivate: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(title = "Catálogo Admin", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SuitButton("Adicionar Fato", onClick = onAddSuit)
            }

            items(models, key = { it.id }) { model ->
                AdminSuitCard(
                    model = model,
                    onEdit = { onEditSuit(model.id) },
                    onToggleAvailability = {
                        if (model.available) onDeactivate(model.id) else onReactivate(model.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun AdminSuitCard(
    model: SuitModel,
    onEdit: () -> Unit,
    onToggleAvailability: () -> Unit,
) {
    SuitCard(padding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            CatalogImage(model.imageKey, model.name)

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(model.name, style = SuitTextStyles.titleMedium, color = SuitColors.Ink, modifier = Modifier.weight(1f))
                    SuitStatusBadge(
                        text = if (model.available) "Activo" else "Inactivo",
                        kind = if (model.available) SuitStatusKind.Success else SuitStatusKind.Neutral,
                    )
                }

                Text(model.category, style = SuitTextStyles.bodySmall, color = SuitColors.Gold)
                Text(formatMetical(model.basePrice), style = SuitTextStyles.titleMedium, color = SuitColors.Ink)
                Text("Tecido: ${model.fabricType}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                Text("Cor: ${model.color}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuitButton(
                        text = "Editar",
                        onClick = onEdit,
                        size = SuitButtonSize.Small,
                        variant = SuitButtonVariant.Secondary,
                        fullWidth = false,
                    )
                    SuitButton(
                        text = if (model.available) "Desactivar" else "Reactivar",
                        onClick = onToggleAvailability,
                        size = SuitButtonSize.Small,
                        variant = if (model.available) SuitButtonVariant.Secondary else SuitButtonVariant.Gold,
                        fullWidth = false,
                    )
                }
            }
        }
    }
}

@Composable
fun AdminSuitFormScreen(
    title: String,
    state: AdminSuitFormState,
    isEditMode: Boolean,
    onStateChange: (AdminSuitFormState) -> Unit,
    onCancel: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        SuitTopBar(title = title, onBack = onCancel)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SuitCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SuitTextField(
                        value = state.name,
                        onValueChange = { onStateChange(state.copy(name = it)) },
                        label = "Nome do fato",
                        placeholder = "Ex: Fato Azul Executivo",
                    )
                    SuitDropdown(
                        options = AdminCatalogOptions.categories,
                        selectedOption = state.category,
                        onSelect = { onStateChange(state.copy(category = it)) },
                        optionLabel = { it },
                    )
                    SuitTextField(
                        value = state.description,
                        onValueChange = { onStateChange(state.copy(description = it)) },
                        label = "Descrição",
                        placeholder = "Descrição breve para o catálogo",
                    )
                    SuitTextField(
                        value = state.basePrice,
                        onValueChange = { onStateChange(state.copy(basePrice = it.filter(Char::isDigit))) },
                        label = "Preço base",
                        placeholder = "8500",
                        keyboardType = KeyboardType.Number,
                    )
                    SuitDropdown(
                        options = AdminCatalogOptions.fabrics,
                        selectedOption = state.fabricType,
                        onSelect = { onStateChange(state.copy(fabricType = it)) },
                        optionLabel = { it },
                    )
                    SuitTextField(
                        value = state.color,
                        onValueChange = { onStateChange(state.copy(color = it)) },
                        label = "Cor",
                        placeholder = "Preto",
                    )
                    SuitDropdown(
                        options = AdminCatalogOptions.availability,
                        selectedOption = state.available,
                        onSelect = { onStateChange(state.copy(available = it)) },
                        optionLabel = { if (it) "Disponível: Sim" else "Disponível: Não" },
                    )
                }
            }

            SuitCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Seleccionar foto", style = SuitTextStyles.titleMedium, color = SuitColors.Ink)
                    CatalogImage(state.imageKey, state.name.ifBlank { "Foto seleccionada" }, size = 132)
                    Text("Foto seleccionada: ${state.imageKey}", style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    SuitDropdown(
                        options = AdminCatalogOptions.imageKeys,
                        selectedOption = state.imageKey,
                        onSelect = { onStateChange(state.copy(imageKey = it)) },
                        optionLabel = { it },
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SuitButton(
                text = "Cancelar",
                onClick = onCancel,
                variant = SuitButtonVariant.Secondary,
                fullWidth = false,
                modifier = Modifier.weight(1f),
            )
            SuitButton(
                text = if (isEditMode) "Actualizar fato" else "Guardar fato",
                onClick = onSave,
                enabled = state.name.isNotBlank() && state.basePrice.isNotBlank(),
                fullWidth = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CatalogImage(imageKey: String, description: String, size: Int = 88) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(SuitTheme.shapes.md)
            .background(SuitColors.SurfaceLow),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(imageResourceForKey(imageKey)),
            contentDescription = description,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

fun imageResourceForKey(key: String): DrawableResource = when (key) {
    "suit_navy_business" -> Res.drawable.suit_navy_business
    "suit_grey_slim" -> Res.drawable.suit_grey_slim
    "suit_casual_linen" -> Res.drawable.suit_casual_linen
    else -> Res.drawable.suit_classic_black
}

fun SuitModel.toAdminFormState(): AdminSuitFormState = AdminSuitFormState(
    id = id,
    name = name,
    category = category,
    description = description,
    basePrice = basePrice.toString(),
    fabricType = fabricType,
    color = color,
    available = available,
    imageKey = imageKey,
)

fun AdminSuitFormState.toSuitModel(generatedId: String = id): SuitModel = SuitModel(
    id = generatedId,
    name = name.trim(),
    category = category,
    description = description.trim(),
    basePrice = basePrice.toIntOrNull() ?: 0,
    imageKey = imageKey,
    fabricType = fabricType,
    color = color.trim(),
    available = available,
)
