package com.suitup.app.ui.screens.product

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.catalog.CatalogRuntime
import com.suitup.app.data.catalog.CustomerCatalogRepository
import com.suitup.app.data.mock.MockCatalogStore
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.SuitModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val model: SuitModel? = null,
    val cartItemCount: Int = 0,
    val isLoading: Boolean = false,
)

/**
 * Reads the richer [SuitModel] directly from [CustomerCatalogRepository] (already
 * loaded by Home/Catalog) instead of the flattened [com.suitup.app.domain.model.ModeloFato],
 * with a [MockCatalogStore] fallback for a model that hasn't landed in the shared
 * repository state yet — mirrors the pattern in [MockOrderStore.ensureDraft].
 */
class ProductDetailScreenModel(
    private val modeloId: String,
    private val catalogRepository: CustomerCatalogRepository = CatalogRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state: StateFlow<ProductDetailUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(catalogRepository.state, MockOrderStore.cart) { catalog, cart ->
                catalog to cart
            }.collect { (catalog, cart) ->
                val resolvedModel = catalog.models.firstOrNull { it.id == modeloId }
                    ?: MockCatalogStore.getSuitModelById(modeloId)
                _state.update {
                    it.copy(
                        model = resolvedModel,
                        cartItemCount = cart.sumOf { item -> item.quantidade },
                        isLoading = catalog.isLoading,
                    )
                }
            }
        }
    }
}
