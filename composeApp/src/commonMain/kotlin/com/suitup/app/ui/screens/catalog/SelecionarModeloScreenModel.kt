package com.suitup.app.ui.screens.catalog

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.catalog.CatalogRuntime
import com.suitup.app.data.catalog.CustomerCatalogRepository
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.data.mock.toModeloFato
import com.suitup.app.domain.model.CategoriaFato
import com.suitup.app.domain.model.ModeloFato
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SelecionarModeloUiState(
    val modelos: List<ModeloFato> = emptyList(),
    val categoriaSeleccionada: CategoriaFato? = null,
    val contadorCarrinho: Int = 0,
    val carregando: Boolean = false,
    val erroCatalogo: String? = null,
    val usandoFallbackMock: Boolean = false,
) {
    val modelosFiltrados: List<ModeloFato>
        get() = if (categoriaSeleccionada == null) modelos
                else modelos.filter { it.categoria == categoriaSeleccionada }
}

sealed class SelecionarModeloUiEvent {
    data class CategoriaSeleccionada(val categoria: CategoriaFato?) : SelecionarModeloUiEvent()
    data class ModeloClicado(val modelo: ModeloFato) : SelecionarModeloUiEvent()
    data object TentarNovamente : SelecionarModeloUiEvent()
}

class SelecionarModeloScreenModel(
    private val catalogRepository: CustomerCatalogRepository = CatalogRuntime.repository,
) : ScreenModel {

    private val _state = MutableStateFlow(SelecionarModeloUiState())
    val state: StateFlow<SelecionarModeloUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(catalogRepository.state, MockOrderStore.cart) { catalog, cart ->
                catalog to cart
            }.collect { (catalog, cart) ->
                _state.update {
                    it.copy(
                        modelos = catalog.models.map { model -> model.toModeloFato() },
                        contadorCarrinho = cart.sumOf { item -> item.quantidade },
                        carregando = catalog.isLoading,
                        erroCatalogo = catalog.errorMessage,
                        usandoFallbackMock = catalog.isUsingMockFallback,
                    )
                }
            }
        }
        refreshCatalog()
    }

    fun onEvent(event: SelecionarModeloUiEvent) {
        when (event) {
            is SelecionarModeloUiEvent.CategoriaSeleccionada -> {
                val novaCategoria = if (_state.value.categoriaSeleccionada == event.categoria) null
                                   else event.categoria
                _state.update { it.copy(categoriaSeleccionada = novaCategoria) }
            }
            is SelecionarModeloUiEvent.ModeloClicado -> MockOrderStore.startDraft(event.modelo)
            SelecionarModeloUiEvent.TentarNovamente -> refreshCatalog()
        }
    }

    private fun refreshCatalog() {
        screenModelScope.launch { catalogRepository.refresh() }
    }
}
