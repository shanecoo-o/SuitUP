package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.suitup.app.ui.screens.editor.Editor2DStageScreen
import com.suitup.app.ui.screens.editor.EditorAccessoriesScreenModel
import com.suitup.app.ui.screens.editor.EditorAccessoriesUiEvent
import com.suitup.app.ui.screens.editor.EditorCoresScreenModel
import com.suitup.app.ui.screens.editor.EditorCoresUiEvent
import com.suitup.app.ui.screens.editor.EditorPartesScreenModel
import com.suitup.app.ui.screens.editor.EditorPartesUiEvent
import com.suitup.app.ui.screens.editor.Preview3DScreen
import com.suitup.app.ui.screens.editor.Preview3DScreenModel
import com.suitup.app.ui.screens.editor.Preview3DUiEvent
import com.suitup.app.ui.screens.editor.TieStyle

class Editor2DStageVoyagerScreen(private val modeloId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val partesModel = rememberScreenModel { EditorPartesScreenModel(modeloId) }
        val coresModel = rememberScreenModel { EditorCoresScreenModel(modeloId) }
        val accessoriesModel = rememberScreenModel { EditorAccessoriesScreenModel() }
        val partesState by partesModel.state.collectAsState()
        val coresState by coresModel.state.collectAsState()
        val accessoriesState by accessoriesModel.state.collectAsState()

        Editor2DStageScreen(
            imageKey = partesState.imagemKey,
            modelName = partesState.nomeModelo,
            basePriceMzn = partesState.precoBase,
            partes = partesState.partes,
            corFato = coresState.corActual,
            tecido = coresState.tecidoActual,
            coresFato = coresState.coresFato,
            tecidos = coresState.tecidos,
            vestIncluded = accessoriesState.vestIncluded,
            tieStyle = accessoriesState.tieStyle,
            cartItemCount = partesState.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onLapelChange = { partesModel.onEvent(EditorPartesUiEvent.LapelaAlterada(it)) },
            onColorSelect = { coresModel.onEvent(EditorCoresUiEvent.CorSeleccionada(it)) },
            onFabricSelect = { coresModel.onEvent(EditorCoresUiEvent.TecidoSeleccionado(it)) },
            onButtonsChange = { partesModel.onEvent(EditorPartesUiEvent.BotoesAlterados(it)) },
            onPocketChange = { partesModel.onEvent(EditorPartesUiEvent.BolsoAlterado(it)) },
            onSleevesChange = { partesModel.onEvent(EditorPartesUiEvent.MangasAlteradas(it)) },
            onLiningChange = { partesModel.onEvent(EditorPartesUiEvent.ForroAlterado(it)) },
            onFitChange = { partesModel.onEvent(EditorPartesUiEvent.LarguraAlterada(it)) },
            onVestToggle = { accessoriesModel.onEvent(EditorAccessoriesUiEvent.VestToggled(it)) },
            onTieStyleChange = { accessoriesModel.onEvent(EditorAccessoriesUiEvent.TieStyleChanged(it)) },
            onNext = {
                navigator.push(
                    Preview3DVoyagerScreen(
                        modeloId = modeloId,
                        colorHex = coresState.corActual.hex,
                        vestIncluded = accessoriesState.vestIncluded,
                        tieStyle = accessoriesState.tieStyle,
                    )
                )
            }
        )
    }
}

class Preview3DVoyagerScreen(
    private val modeloId: String,
    private val colorHex: String,
    private val vestIncluded: Boolean = false,
    private val tieStyle: TieStyle = TieStyle.None,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            Preview3DScreenModel(modeloId, colorHex, vestIncluded, tieStyle)
        }
        val state by screenModel.state.collectAsState()

        Preview3DScreen(
            state = state.estadoVisor,
            garmentColor = state.corFato,
            imageKey = state.imagemKey,
            modelName = state.nomeModelo,
            estimatedPriceMzn = state.precoEstimado,
            configurationDetails = state.detalhesConfiguracao,
            showLight = state.mostrarLuz,
            backgroundDark = state.fundoEscuro,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onStateChange = { screenModel.onEvent(Preview3DUiEvent.EstadoAlterado(it)) },
            onRotate = { screenModel.onEvent(Preview3DUiEvent.GirarClicado) },
            onZoomIn = { screenModel.onEvent(Preview3DUiEvent.ZoomInClicado) },
            onZoomOut = { screenModel.onEvent(Preview3DUiEvent.ZoomOutClicado) },
            onReset = { screenModel.onEvent(Preview3DUiEvent.ResetClicado) },
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
