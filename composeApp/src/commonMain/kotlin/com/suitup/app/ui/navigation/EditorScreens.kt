package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.suitup.app.ui.screens.editor.Editor2DColorsScreen
import com.suitup.app.ui.screens.editor.Editor2DPartsScreen
import com.suitup.app.ui.screens.editor.EditorCoresScreenModel
import com.suitup.app.ui.screens.editor.EditorCoresUiEvent
import com.suitup.app.ui.screens.editor.EditorPartesScreenModel
import com.suitup.app.ui.screens.editor.EditorPartesUiEvent
import com.suitup.app.ui.screens.editor.Preview3DScreen
import com.suitup.app.ui.screens.editor.Preview3DScreenModel
import com.suitup.app.ui.screens.editor.Preview3DUiEvent

class Editor2DPartsVoyagerScreen(private val modeloId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { EditorPartesScreenModel(modeloId) }
        val state by screenModel.state.collectAsState()

        Editor2DPartsScreen(
            partes = state.partes,
            selectedPart = state.parteSeleccionada,
            garmentColor = state.corFato,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onPartSelect = { screenModel.onEvent(EditorPartesUiEvent.ParteSeleccionada(it)) },
            onLapelChange = { screenModel.onEvent(EditorPartesUiEvent.LapelaAlterada(it)) },
            onWidthChange = { screenModel.onEvent(EditorPartesUiEvent.LarguraAlterada(it)) },
            onNext = { navigator.push(Editor2DColorsVoyagerScreen(modeloId)) }
        )
    }
}

class Editor2DColorsVoyagerScreen(private val modeloId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { EditorCoresScreenModel(modeloId) }
        val state by screenModel.state.collectAsState()

        Editor2DColorsScreen(
            selectedPart = state.parteSeleccionada,
            coresFato = state.coresFato,
            tecidos = state.tecidos,
            selectedColor = state.corActual,
            selectedFabric = state.tecidoActual,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onPartSelect = { screenModel.onEvent(EditorCoresUiEvent.ParteSeleccionada(it)) },
            onColorSelect = { screenModel.onEvent(EditorCoresUiEvent.CorSeleccionada(it)) },
            onFabricSelect = { screenModel.onEvent(EditorCoresUiEvent.TecidoSeleccionado(it)) },
            onNext = {
                navigator.push(
                    Preview3DVoyagerScreen(modeloId, state.corActual.hex)
                )
            }
        )
    }
}

class Preview3DVoyagerScreen(
    private val modeloId: String,
    private val colorHex: String,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { Preview3DScreenModel(modeloId, colorHex) }
        val state by screenModel.state.collectAsState()

        Preview3DScreen(
            state = state.estadoVisor,
            garmentColor = state.corFato,
            modelName = state.nomeModelo,
            configurationDetails = state.detalhesConfiguracao,
            showLight = state.mostrarLuz,
            backgroundDark = state.fundoEscuro,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onStateChange = { screenModel.onEvent(Preview3DUiEvent.EstadoAlterado(it)) },
            onRotate = { screenModel.onEvent(Preview3DUiEvent.GirarClicado) },
            onZoom = { screenModel.onEvent(Preview3DUiEvent.ZoomClicado) },
            onToggleLight = { screenModel.onEvent(Preview3DUiEvent.AlternarLuz) },
            onToggleBackground = { screenModel.onEvent(Preview3DUiEvent.AlternarFundo) },
            onEditAgain = { navigator.pop() },
            onOrder = {
                screenModel.adicionarAoCarrinho()
                navigator.push(CartVoyagerScreen())
            }
        )
    }
}
