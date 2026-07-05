package com.suitup.app.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.app.domain.model.CategoriaFato
import com.suitup.app.domain.model.ModeloFato
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.PremiumCard
import com.suitup.app.ui.components.SuitPrimaryTopBar
import com.suitup.app.ui.components.SecondaryDarkButton
import com.suitup.app.ui.components.SectionHeader
import com.suitup.app.ui.components.SuitFilterChip
import com.suitup.app.ui.components.SuitImageCard
import com.suitup.app.ui.components.SuitSkeletonBlock
import com.suitup.app.ui.components.SuitSkeletonLine
import com.suitup.app.ui.components.rememberSuitNavDensity
import com.suitup.app.ui.navigation.LocalSuitNavDensity
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitGridPolicy
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.suitImageResource

@Composable
fun SelectModelScreen(
    models: List<ModeloFato>,
    selectedCategory: CategoriaFato? = null,
    cartItemCount: Int = 0,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isUsingMockFallback: Boolean = false,
    onCartClick: () -> Unit = {},
    onCategorySelect: (CategoriaFato?) -> Unit = {},
    onModelClick: (ModeloFato) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val visible = remember(models, selectedCategory) {
        if (selectedCategory == null) models
        else models.filter { it.categoria == selectedCategory }
    }
    val horizontalPadding = SuitTheme.responsive.horizontalContentPadding
    val columns = SuitGridPolicy.productColumns(SuitTheme.responsive.widthClass)

    val gridState = rememberLazyGridState()
    val navDensity = rememberSuitNavDensity(gridState)
    val sharedNavDensity = LocalSuitNavDensity.current
    LaunchedEffect(navDensity) { sharedNavDensity.value = navDensity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.InkBlack),
    ) {
        SuitPrimaryTopBar(
            title = "Catálogo",
            onCart = onCartClick,
            cartBadgeCount = cartItemCount,
        )

        SectionHeader(
            title = "Escolha o seu modelo",
            eyebrow = "Fatos à medida",
            description = "Cada modelo é o ponto de partida para uma peça única.",
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 14.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalPadding),
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

        if (isUsingMockFallback && errorMessage != null) {
            PremiumCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 12.dp),
                padding = 14.dp,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(errorMessage, style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
                    Text(
                        "A mostrar modelos locais em modo demo.",
                        style = SuitTextStyles.bodySmall,
                        color = SuitColors.Slate,
                    )
                    SecondaryDarkButton(
                        text = "Tentar novamente",
                        onClick = onRetry,
                        fullWidth = false,
                    )
                }
            }
        }

        when {
            isLoading && visible.isEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    state = gridState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(6) { ProductCardSkeleton() }
                }
            }

            visible.isEmpty() -> {
                EmptyStateCard(
                    title = when {
                        errorMessage != null -> errorMessage
                        else -> "Nenhum modelo disponível no momento."
                    },
                    description = when {
                        errorMessage != null -> "Verifique a ligação e tente novamente."
                        selectedCategory != null -> "Não existem modelos activos para esta categoria."
                        else -> "Volte a verificar o catálogo mais tarde."
                    },
                    actionLabel = if (errorMessage != null) "Tentar novamente" else null,
                    onAction = if (errorMessage != null) onRetry else null,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontalPadding),
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    state = gridState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 18.dp),
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
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCardSkeleton(modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier, padding = 12.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SuitSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.82f),
                shape = SuitTheme.shapes.md,
            )
            SuitSkeletonLine(modifier = Modifier.fillMaxWidth(0.7f))
            SuitSkeletonLine(modifier = Modifier.fillMaxWidth(0.4f))
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
