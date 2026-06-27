package com.suitup.app.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.CategoriaFato
import com.suitup.app.domain.model.ModeloFato
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.PremiumTopBar
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitFilterChip
import com.suitup.app.ui.components.SuitImageCard
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.util.suitImageResource

@Composable
fun SelectModelScreen(
    models: List<ModeloFato>,
    selectedCategory: CategoriaFato? = null,
    cartItemCount: Int = 0,
    onBack: (() -> Unit)? = null,
    onCartClick: () -> Unit = {},
    onCategorySelect: (CategoriaFato?) -> Unit = {},
    onModelClick: (ModeloFato) -> Unit = {},
) {
    val visible = remember(models, selectedCategory) {
        if (selectedCategory == null) models
        else models.filter { it.categoria == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.InkBlack),
    ) {
        PremiumTopBar(
            title = "Catálogo",
            onBack = onBack,
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        SectionHeader(
            title = "Escolha o seu modelo",
            eyebrow = "Fatos à medida",
            description = "Cada modelo é o ponto de partida para uma peça única.",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(CategoryFilterOption.all(), key = { it.label }) { option ->
                SuitFilterChip(
                    text = option.label,
                    selected = selectedCategory == option.categoria,
                    onClick = { onCategorySelect(option.categoria) },
                )
            }
        }

        if (visible.isEmpty()) {
            EmptyStateCard(
                title = "Nenhum fato disponível",
                description = "Não existem modelos activos para esta categoria.",
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(visible, key = { it.id }) { model ->
                    SuitImageCard(
                        image = suitImageResource(model.urlImagemPrevia),
                        title = model.nome,
                        subtitle = model.categoria.label,
                        priceMzn = model.precoBase,
                        onClick = { onModelClick(model) },
                        actionLabel = "Personalizar",
                        onAction = { onModelClick(model) },
                    )
                }
            }
        }
    }
}

private data class CategoryFilterOption(
    val categoria: CategoriaFato?,
    val label: String,
) {
    companion object {
        fun all(): List<CategoryFilterOption> =
            listOf(CategoryFilterOption(null, "Todos")) +
                CategoriaFato.all().map { CategoryFilterOption(it, it.label) }
    }
}
