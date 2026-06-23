package com.suitup.app.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.CategoriaFato
import com.suitup.app.domain.model.ModeloFato
import com.suitup.app.ui.components.SuitDropdown
import com.suitup.app.ui.components.SuitGarmentMini
import com.suitup.app.ui.components.SuitTopBar
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import suitup.composeapp.generated.resources.Res
import suitup.composeapp.generated.resources.suit_casual_linen
import suitup.composeapp.generated.resources.suit_classic_black
import suitup.composeapp.generated.resources.suit_grey_slim
import suitup.composeapp.generated.resources.suit_navy_business

/**
 * Ecrã 05 — Selecionar Modelo.
 *
 * Top bar com costas + cart, título Playfair, filtros chip horizontais,
 * grid 2 colunas de cards de modelos.
 */
@Composable
fun SelectModelScreen(
    models: List<ModeloFato>,
    selectedCategory: CategoriaFato? = null,
    cartItemCount: Int = 0,
    onBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onCategorySelect: (CategoriaFato?) -> Unit = {},
    onModelClick: (ModeloFato) -> Unit = {},
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
        )

        // Title — centralizado
        Text(
            text = "Modelos de Fatos",
            style = SuitTextStyles.headlineLarge,
            color = SuitColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        )

        // Filtro: dropdown com todas as categorias + "Todos" como primeira opção
        SuitDropdown(
            options = CategoryFilterOption.all(),
            selectedOption = CategoryFilterOption.from(selectedCategory),
            onSelect = { onCategorySelect(it.categoria) },
            optionLabel = { it.label },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        )

        // Filtered models grid
        val visible = remember(models, selectedCategory) {
            if (selectedCategory == null) models
            else models.filter { it.categoria == selectedCategory }
        }

        if (visible.isEmpty()) {
            EmptyModels(modifier = Modifier.weight(1f))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(visible, key = { it.id }) { model ->
                    ModelCard(
                        model = model,
                        onClick = { onModelClick(model) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelCard(
    model: ModeloFato,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.78f)
                .clip(SuitTheme.shapes.lg)
                .background(SuitColors.SurfaceLow),
            contentAlignment = Alignment.Center
        ) {
            val image = imageForModel(model)
            if (image != null) {
                Image(
                    painter = painterResource(image),
                    contentDescription = model.nome,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                SuitGarmentMini(
                    size = 132.dp,
                    garmentColor = colorForModel(model),
                    background = Color.Transparent,
                    showShirt = true,
                )
            }
        }

        // Name
        Text(
            text = model.nome,
            style = SuitTextStyles.titleMedium,
            color = SuitColors.Ink,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun EmptyModels(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Sem modelos nesta categoria",
            style = SuitTextStyles.titleMedium,
            color = SuitColors.Ink,
        )
        Text(
            text = "Tente outra categoria ou volte mais tarde.",
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Slate,
        )
    }
}

/**
 * Mapping pragmático nome → cor para o thumbnail enquanto não há fotos reais.
 * Detecta keywords nos nomes dos modelos.
 */
private fun colorForModel(model: ModeloFato): Color {
    val lower = model.nome.lowercase()
    return when {
        "preto" in lower -> SuitColors.Black
        "azul marinho" in lower -> Color(0xFF1F2A44)
        "azul" in lower -> Color(0xFF2D4A6B)
        "cinza" in lower || "antracite" in lower -> Color(0xFF3B3B3B)
        "grafite" in lower -> Color(0xFF2C2C2C)
        else -> SuitColors.Charcoal
    }
}

private fun imageForModel(model: ModeloFato): DrawableResource? {
    val lower = model.nome.lowercase()
    return when {
        "preto" in lower -> Res.drawable.suit_classic_black
        "cinza" in lower || "grafite" in lower || "antracite" in lower -> Res.drawable.suit_grey_slim
        "azul" in lower -> Res.drawable.suit_navy_business
        "linho" in lower || "linen" in lower -> Res.drawable.suit_casual_linen
        model.id == "m4" -> Res.drawable.suit_navy_business
        model.id == "m6" -> Res.drawable.suit_grey_slim
        else -> Res.drawable.suit_classic_black
    }
}


/**
 * Wrapper que combina "Todos" + as CategoriaFato reais para uso no dropdown.
 * Mantém o domain limpo (CategoriaFato não precisa de saber sobre "Todos" como conceito).
 */
private data class CategoryFilterOption(
    val categoria: CategoriaFato?,
    val label: String,
) {
    companion object {
        private val ALL = CategoryFilterOption(null, "Todos os modelos")

        fun all(): List<CategoryFilterOption> =
            listOf(ALL) + CategoriaFato.all().map { CategoryFilterOption(it, it.label) }

        fun from(categoria: CategoriaFato?): CategoryFilterOption =
            if (categoria == null) ALL
            else CategoryFilterOption(categoria, categoria.label)
    }
}
