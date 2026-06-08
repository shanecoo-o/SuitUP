package com.suitup.app.ui.screens.catalog

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.domain.model.CategoriaFato
import com.suitup.app.domain.model.ModeloFato
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SelecionarModeloUiState(
    val modelos: List<ModeloFato> = emptyList(),
    val categoriaSeleccionada: CategoriaFato? = null,
    val contadorCarrinho: Int = 0,
) {
    val modelosFiltrados: List<ModeloFato>
        get() = if (categoriaSeleccionada == null) modelos
                else modelos.filter { it.categoria == categoriaSeleccionada }
}

sealed class SelecionarModeloUiEvent {
    data class CategoriaSeleccionada(val categoria: CategoriaFato?) : SelecionarModeloUiEvent()
    data class ModeloClicado(val modelo: ModeloFato) : SelecionarModeloUiEvent()
}

class SelecionarModeloScreenModel : ScreenModel {

    private val _state = MutableStateFlow(SelecionarModeloUiState())
    val state: StateFlow<SelecionarModeloUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    modelos = MockData.modelosFato,
                    contadorCarrinho = MockData.itensCarrinho.sumOf { item -> item.quantidade },
                )
            }
        }
    }

    fun onEvent(event: SelecionarModeloUiEvent) {
        when (event) {
            is SelecionarModeloUiEvent.CategoriaSeleccionada -> {
                val novaCategoria = if (_state.value.categoriaSeleccionada == event.categoria) null
                                   else event.categoria
                _state.update { it.copy(categoriaSeleccionada = novaCategoria) }
            }
            is SelecionarModeloUiEvent.ModeloClicado -> {
                // Navegação feita pelo VoyagerScreen.
            }
        }
    }
}
