package com.suitup.app.ui.screens.editor

import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.CorFato
import com.suitup.app.domain.model.PartesFato
import com.suitup.app.domain.model.Tecido
import com.suitup.app.domain.model.TipoLapela
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.util.toComposeColorOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorPartesUiState(
    val partes: PartesFato = PartesFato(),
    val parteSeleccionada: EditorPart = EditorPart.Lapela,
    val corFato: Color = SuitColors.Ink,
    val contadorCarrinho: Int = 0,
)

sealed class EditorPartesUiEvent {
    data class ParteSeleccionada(val parte: EditorPart) : EditorPartesUiEvent()
    data class LapelaAlterada(val tipo: TipoLapela) : EditorPartesUiEvent()
    data class LarguraAlterada(val valor: Float) : EditorPartesUiEvent()
}

class EditorPartesScreenModel(private val modeloId: String) : ScreenModel {

    private val _state = MutableStateFlow(EditorPartesUiState())
    val state: StateFlow<EditorPartesUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            val draft = MockOrderStore.ensureDraft(modeloId)
            _state.update {
                it.copy(
                    partes = draft.partes,
                    corFato = draft.cor.hex.toComposeColorOrNull() ?: SuitColors.Ink,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: EditorPartesUiEvent) {
        when (event) {
            is EditorPartesUiEvent.ParteSeleccionada ->
                _state.update { it.copy(parteSeleccionada = event.parte) }
            is EditorPartesUiEvent.LapelaAlterada -> {
                _state.update { it.copy(partes = it.partes.copy(lapela = event.tipo)) }
                MockOrderStore.updatePartes(_state.value.partes)
            }
            is EditorPartesUiEvent.LarguraAlterada -> {
                _state.update { it.copy(partes = it.partes.copy(ajusteLargura = event.valor)) }
                MockOrderStore.updatePartes(_state.value.partes)
            }
        }
    }
}

data class EditorCoresUiState(
    val parteSeleccionada: EditorPart = EditorPart.Mangas,
    val coresFato: List<CorFato> = emptyList(),
    val tecidos: List<Tecido> = emptyList(),
    val corSeleccionada: CorFato? = null,
    val tecidoSeleccionado: Tecido? = null,
    val contadorCarrinho: Int = 0,
) {
    val corActual: CorFato get() = corSeleccionada ?: coresFato.firstOrNull() ?: CorFato("", "", "#1F2A44")
    val tecidoActual: Tecido get() = tecidoSeleccionado ?: tecidos.firstOrNull() ?: Tecido("", "", "#1F2A44")
}

sealed class EditorCoresUiEvent {
    data class ParteSeleccionada(val parte: EditorPart) : EditorCoresUiEvent()
    data class CorSeleccionada(val cor: CorFato) : EditorCoresUiEvent()
    data class TecidoSeleccionado(val tecido: Tecido) : EditorCoresUiEvent()
}

class EditorCoresScreenModel(private val modeloId: String) : ScreenModel {

    private val _state = MutableStateFlow(EditorCoresUiState())
    val state: StateFlow<EditorCoresUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            val draft = MockOrderStore.ensureDraft(modeloId)
            _state.update {
                it.copy(
                    coresFato = MockData.coresFato,
                    tecidos = MockData.tecidos,
                    corSeleccionada = draft.cor,
                    tecidoSeleccionado = draft.tecido,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: EditorCoresUiEvent) {
        when (event) {
            is EditorCoresUiEvent.ParteSeleccionada ->
                _state.update { it.copy(parteSeleccionada = event.parte) }
            is EditorCoresUiEvent.CorSeleccionada -> {
                _state.update { it.copy(corSeleccionada = event.cor) }
                MockOrderStore.updateCor(event.cor)
            }
            is EditorCoresUiEvent.TecidoSeleccionado -> {
                _state.update { it.copy(tecidoSeleccionado = event.tecido) }
                MockOrderStore.updateTecido(event.tecido)
            }
        }
    }
}

data class Preview3DUiState(
    val estadoVisor: Preview3DState = Preview3DState(),
    val corFato: Color = SuitColors.Ink,
    val nomeModelo: String = "",
    val detalhesConfiguracao: List<String> = emptyList(),
    val mostrarLuz: Boolean = false,
    val fundoEscuro: Boolean = true,
    val contadorCarrinho: Int = 0,
)

sealed class Preview3DUiEvent {
    data class EstadoAlterado(val estado: Preview3DState) : Preview3DUiEvent()
    data object GirarClicado : Preview3DUiEvent()
    data object ZoomClicado : Preview3DUiEvent()
    data object AlternarLuz : Preview3DUiEvent()
    data object AlternarFundo : Preview3DUiEvent()
}

class Preview3DScreenModel(private val modeloId: String, private val colorHex: String) : ScreenModel {

    private val _state = MutableStateFlow(Preview3DUiState())
    val state: StateFlow<Preview3DUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            val design = MockOrderStore.currentDesign(modeloId)
            val cor = design.cor.hex.toComposeColorOrNull() ?: colorHex.toComposeColorOrNull() ?: SuitColors.Ink
            _state.update {
                it.copy(
                    corFato = cor,
                    nomeModelo = design.nome,
                    detalhesConfiguracao = listOf(
                        "Tecido: ${design.tecido.nome}",
                        "Cor: ${design.cor.nome}",
                        "Lapela: ${design.partes.lapela.label}",
                        "Botoes: ${design.partes.botoes.label}",
                    ),
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: Preview3DUiEvent) {
        when (event) {
            is Preview3DUiEvent.EstadoAlterado ->
                _state.update { it.copy(estadoVisor = event.estado) }
            is Preview3DUiEvent.GirarClicado ->
                _state.update { it.copy(estadoVisor = it.estadoVisor.copy(rotationY = it.estadoVisor.rotationY + 30f)) }
            is Preview3DUiEvent.ZoomClicado -> {
                val novaEscala = if (_state.value.estadoVisor.scale >= 1.5f) 0.8f
                                 else _state.value.estadoVisor.scale + 0.2f
                _state.update { it.copy(estadoVisor = it.estadoVisor.copy(scale = novaEscala)) }
            }
            is Preview3DUiEvent.AlternarLuz ->
                _state.update { it.copy(mostrarLuz = !it.mostrarLuz) }
            is Preview3DUiEvent.AlternarFundo ->
                _state.update { it.copy(fundoEscuro = !it.fundoEscuro) }
        }
    }

    fun adicionarAoCarrinho() {
        MockOrderStore.addCurrentDesignToCart(modeloId)
        _state.update { it.copy(contadorCarrinho = MockOrderStore.cartItemCount) }
    }
}
