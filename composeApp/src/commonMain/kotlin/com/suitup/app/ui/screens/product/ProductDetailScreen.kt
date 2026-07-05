package com.suitup.app.ui.screens.product

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
import com.suitup.app.domain.model.SuitModel
import com.suitup.app.ui.components.EmptyStateCard
import com.suitup.app.ui.components.PrimaryGoldButton
import com.suitup.app.ui.components.SuitContentLoading
import com.suitup.app.ui.components.SuitDetailScaffold
import com.suitup.app.ui.components.SuitDetailTopBar
import com.suitup.app.ui.components.SuitEyebrow
import com.suitup.app.ui.components.SuitFixedCtaBar
import com.suitup.app.ui.components.SuitImageContainer
import com.suitup.app.ui.components.SuitImageContext
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import com.suitup.app.ui.util.formatMzn
import com.suitup.app.ui.util.suitImageResource

@Composable
fun ProductDetailScreen(
    model: SuitModel?,
    isLoading: Boolean,
    cartItemCount: Int,
    onBack: () -> Unit,
    onCartClick: () -> Unit = {},
    onCustomize: () -> Unit = {},
) {
    SuitDetailScaffold(
        topBar = {
            SuitDetailTopBar(
                onBack = onBack,
                title = model?.name,
                onCart = onCartClick,
                cartBadgeCount = cartItemCount,
            )
        },
        fixedCta = if (model != null) {
            {
                SuitFixedCtaBar {
                    PrimaryGoldButton(text = "Personalizar", onClick = onCustomize)
                }
            }
        } else null,
        content = {
            when {
                model != null -> ProductDetailContent(model)
                isLoading -> SuitContentLoading(message = "A carregar modelo...")
                else -> EmptyStateCard(
                    title = "Modelo não encontrado",
                    description = "Este modelo pode já não estar disponível.",
                    modifier = Modifier.padding(SuitTheme.responsive.horizontalContentPadding),
                )
            }
        },
    )
}

@Composable
private fun ProductDetailContent(model: SuitModel) {
    val responsive = SuitTheme.responsive
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = responsive.horizontalContentPadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SuitImageContainer(
            image = suitImageResource(model.imageKey),
            contentDescription = model.name,
            context = SuitImageContext.ProductDetail,
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SuitEyebrow(text = model.category, color = SuitColors.GoldChampagne)
            Text(
                text = model.name,
                style = SuitTextStyles.headlineLarge,
                color = SuitColors.Pearl,
            )
            Text(
                text = formatMzn(model.basePrice),
                style = SuitTextStyles.titleLarge,
                color = SuitColors.GoldChampagne,
            )
        }
        Text(
            text = model.description,
            style = SuitTextStyles.bodyMedium,
            color = SuitColors.Slate,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Tecido: ${model.fabricType}",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Smoke,
            )
            Text(
                text = "Cor: ${model.color}",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Smoke,
            )
        }
    }
}
