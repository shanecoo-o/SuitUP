package com.suitup.app.ui.screens.editor

import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.mock.MockData
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.CorFato
import com.suitup.app.domain.model.EstiloBotao
import com.suitup.app.domain.model.EstiloForro
import com.suitup.app.domain.model.EstiloManga
import com.suitup.app.domain.model.PartesFato
import com.suitup.app.domain.model.Tecido
import com.suitup.app.domain.model.TipoBolso
import com.suitup.app.domain.model.TipoLapela
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.util.toComposeColorOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

data class EditorPartesUiState(
    val partes: PartesFato = PartesFato(),
    val parteSeleccionada: EditorPart = EditorPart.Lapela,
    val corFato: Color = SuitColors.Ink,
    val nomeModelo: String = "",
    val imagemKey: String = "",
    val precoBase: Int = 0,
    val contadorCarrinho: Int = 0,
)

sealed class EditorPartesUiEvent {
    data class ParteSeleccionada(val parte: EditorPart) : EditorPartesUiEvent()
    data class LapelaAlterada(val tipo: TipoLapela) : EditorPartesUiEvent()
    data class LarguraAlterada(val valor: Float) : EditorPartesUiEvent()
    data class BotoesAlterados(val estilo: EstiloBotao) : EditorPartesUiEvent()
    data class BolsoAlterado(val tipo: TipoBolso) : EditorPartesUiEvent()
    data class MangasAlteradas(val estilo: EstiloManga) : EditorPartesUiEvent()
    data class ForroAlterado(val estilo: EstiloForro) : EditorPartesUiEvent()
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
                    nomeModelo = draft.modelo.nome,
                    imagemKey = draft.modelo.urlImagemPrevia,
                    precoBase = draft.modelo.precoBase,
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
            is EditorPartesUiEvent.BotoesAlterados -> {
                _state.update { it.copy(partes = it.partes.copy(botoes = event.estilo)) }
                MockOrderStore.updatePartes(_state.value.partes)
            }
            is EditorPartesUiEvent.BolsoAlterado -> {
                _state.update { it.copy(partes = it.partes.copy(bolso = event.tipo)) }
                MockOrderStore.updatePartes(_state.value.partes)
            }
            is EditorPartesUiEvent.MangasAlteradas -> {
                _state.update { it.copy(partes = it.partes.copy(mangas = event.estilo)) }
                MockOrderStore.updatePartes(_state.value.partes)
            }
            is EditorPartesUiEvent.ForroAlterado -> {
                _state.update { it.copy(partes = it.partes.copy(forro = event.estilo)) }
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
    val nomeModelo: String = "",
    val precoBase: Int = 0,
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
                    nomeModelo = draft.modelo.nome,
                    precoBase = draft.modelo.precoBase,
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

/** Bounded tilt range for the real-photo pseudo-3D stage (Phase 9.5B) — see [Suit3DPreview]. */
internal const val Preview3DRotationLimit = 25f
internal val Preview3DRotationPresets = listOf(0f, 22f, -22f)
internal const val Preview3DScaleMin = 0.75f
internal const val Preview3DScaleMax = 1.6f
internal const val Preview3DZoomStep = 0.15f

data class Preview3DUiState(
    val estadoVisor: Preview3DState = Preview3DState(),
    val corFato: Color = SuitColors.Ink,
    val nomeModelo: String = "",
    val imagemKey: String = "",
    val precoEstimado: Int = 0,
    val detalhesConfiguracao: List<String> = emptyList(),
    val mostrarLuz: Boolean = false,
    val fundoEscuro: Boolean = true,
    val contadorCarrinho: Int = 0,
)

sealed class Preview3DUiEvent {
    data class EstadoAlterado(val estado: Preview3DState) : Preview3DUiEvent()
    data object GirarClicado : Preview3DUiEvent()
    data object ZoomInClicado : Preview3DUiEvent()
    data object ZoomOutClicado : Preview3DUiEvent()
    data object ResetClicado : Preview3DUiEvent()
    data object AlternarLuz : Preview3DUiEvent()
    data object AlternarFundo : Preview3DUiEvent()
}

/**
 * [vestIncluded]/[tieStyle] are a one-way snapshot handed off from
 * [EditorAccessoriesScreenModel] at the moment Editor pushes Preview (Task 8: state-only,
 * no backend field to read them back from) — Preview only displays them, it never mutates
 * or persists them, so no second state owner is introduced.
 */
class Preview3DScreenModel(
    private val modeloId: String,
    private val colorHex: String,
    private val vestIncluded: Boolean = false,
    private val tieStyle: TieStyle = TieStyle.None,
) : ScreenModel {

    private val _state = MutableStateFlow(Preview3DUiState())
    val state: StateFlow<Preview3DUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            val draft = MockOrderStore.ensureDraft(modeloId)
            val design = MockOrderStore.currentDesign(modeloId)
            val cor = design.cor.hex.toComposeColorOrNull() ?: colorHex.toComposeColorOrNull() ?: SuitColors.Ink
            val detalhes = buildList {
                add("Tecido: ${design.tecido.nome}")
                add("Cor: ${design.cor.nome}")
                add("Lapela: ${design.partes.lapela.label}")
                add("Botoes: ${design.partes.botoes.label}")
                add("Bolsos: ${design.partes.bolso.label()}")
                add("Mangas: ${design.partes.mangas.label()}")
                add("Forro: ${design.partes.forro.label}")
                add("Caimento: ${fitLabel(design.partes.ajusteLargura)}")
                add("Colete: ${if (vestIncluded) "Com colete" else "Sem colete"}")
                if (tieStyle != TieStyle.None) add("Gravata: ${tieStyle.label}")
            }
            _state.update {
                it.copy(
                    corFato = cor,
                    nomeModelo = design.nome,
                    imagemKey = draft.modelo.urlImagemPrevia,
                    precoEstimado = design.preco,
                    detalhesConfiguracao = detalhes,
                    contadorCarrinho = MockOrderStore.cartItemCount,
                )
            }
        }
    }

    fun onEvent(event: Preview3DUiEvent) {
        when (event) {
            is Preview3DUiEvent.EstadoAlterado ->
                _state.update { it.copy(estadoVisor = event.estado) }
            is Preview3DUiEvent.GirarClicado -> {
                val current = _state.value.estadoVisor.rotationY
                val currentIndex = Preview3DRotationPresets.indexOfFirst { abs(it - current) < 0.5f }
                val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % Preview3DRotationPresets.size
                _state.update { it.copy(estadoVisor = it.estadoVisor.copy(rotationY = Preview3DRotationPresets[nextIndex])) }
            }
            is Preview3DUiEvent.ZoomInClicado -> {
                val novaEscala = (_state.value.estadoVisor.scale + Preview3DZoomStep).coerceIn(Preview3DScaleMin, Preview3DScaleMax)
                _state.update { it.copy(estadoVisor = it.estadoVisor.copy(scale = novaEscala)) }
            }
            is Preview3DUiEvent.ZoomOutClicado -> {
                val novaEscala = (_state.value.estadoVisor.scale - Preview3DZoomStep).coerceIn(Preview3DScaleMin, Preview3DScaleMax)
                _state.update { it.copy(estadoVisor = it.estadoVisor.copy(scale = novaEscala)) }
            }
            is Preview3DUiEvent.ResetClicado ->
                _state.update { it.copy(estadoVisor = Preview3DState()) }
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

/**
 * UI-only accessory state for the Editor 2D stage (Phase 9.5A, Task 8: category C /
 * state-only). Vest inclusion and tie style have no [PartesFato] field and no visual
 * render on the stage photo — this model exists only so the choice persists across
 * sheet open/close and Editor<->Preview navigation (the screen instance survives on the
 * Voyager back stack), without inventing a fake domain/backend field for them.
 */
data class EditorAccessoriesUiState(
    val vestIncluded: Boolean = false,
    val tieStyle: TieStyle = TieStyle.None,
)

sealed class EditorAccessoriesUiEvent {
    data class VestToggled(val included: Boolean) : EditorAccessoriesUiEvent()
    data class TieStyleChanged(val style: TieStyle) : EditorAccessoriesUiEvent()
}

class EditorAccessoriesScreenModel : ScreenModel {

    private val _state = MutableStateFlow(EditorAccessoriesUiState())
    val state: StateFlow<EditorAccessoriesUiState> = _state.asStateFlow()

    fun onEvent(event: EditorAccessoriesUiEvent) {
        when (event) {
            is EditorAccessoriesUiEvent.VestToggled ->
                _state.update { it.copy(vestIncluded = event.included) }
            is EditorAccessoriesUiEvent.TieStyleChanged ->
                _state.update { it.copy(tieStyle = event.style) }
        }
    }
}
