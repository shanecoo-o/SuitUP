package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.suitup.app.data.mock.MockOrderStore
import com.suitup.app.domain.model.TipoEntrega
import com.suitup.app.ui.screens.checkout.AddressScreen
import com.suitup.app.ui.screens.checkout.CheckoutMedidasScreen
import com.suitup.app.ui.screens.checkout.CheckoutMedidasScreenModel
import com.suitup.app.ui.screens.checkout.CheckoutMedidasUiEvent
import com.suitup.app.ui.screens.checkout.CheckoutScreen
import com.suitup.app.ui.screens.checkout.CheckoutScreenModel
import com.suitup.app.ui.screens.checkout.CheckoutUiEvent
import com.suitup.app.ui.screens.checkout.ConfirmationScreen
import com.suitup.app.ui.screens.checkout.DeliveryTypeScreen
import com.suitup.app.ui.screens.checkout.EnderecoScreenModel
import com.suitup.app.ui.screens.checkout.EnderecoUiEvent
import com.suitup.app.ui.screens.checkout.PagamentoScreenModel
import com.suitup.app.ui.screens.checkout.PagamentoUiEvent
import com.suitup.app.ui.screens.checkout.PaymentScreen
import com.suitup.app.ui.screens.checkout.TipoEntregaScreenModel
import com.suitup.app.ui.screens.checkout.TipoEntregaUiEvent

/**
 * Step 1 — Dados do Cliente.
 * Navega para CheckoutMedidasVoyagerScreen passando a preferência de medidas guardadas.
 */
class CheckoutVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CheckoutScreenModel() }
        val state by screenModel.state.collectAsState()
        val podeAvancar by screenModel.podeAvancar.collectAsState()

        LaunchedEffect(podeAvancar) {
            if (podeAvancar) {
                screenModel.avancarConsumido()
                navigator.push(CheckoutMedidasVoyagerScreen(state.usarMedidasGuardadas))
            }
        }

        CheckoutScreen(
            fullName = state.nomeCompleto,
            telefone = state.telefone,
            email = state.email,
            useSavedMeasurements = state.usarMedidasGuardadas,
            cartItemCount = state.contadorCarrinho,
            nameError = state.erroNome,
            phoneError = state.erroTelefone,
            emailError = state.erroEmail,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onFullNameChange = { screenModel.onEvent(CheckoutUiEvent.NomeAlterado(it)) },
            onPhoneChange = { screenModel.onEvent(CheckoutUiEvent.TelefoneAlterado(it)) },
            onEmailChange = { screenModel.onEvent(CheckoutUiEvent.EmailAlterado(it)) },
            onToggleMeasurements = { screenModel.onEvent(CheckoutUiEvent.MedidasGuardadasAlteradas(it)) },
            onContinue = { screenModel.onEvent(CheckoutUiEvent.ContinuarClicado) }
        )
    }
}

/**
 * Step 2 — Medidas do Cliente.
 * Recebe [usarMedidasGuardadas]: se true, pré-preenche com medidas guardadas do utilizador.
 * Navega para TipoEntregaVoyagerScreen após validação.
 */
class CheckoutMedidasVoyagerScreen(
    private val usarMedidasGuardadas: Boolean = false,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CheckoutMedidasScreenModel(usarMedidasGuardadas) }
        val state by screenModel.state.collectAsState()
        val podeAvancar by screenModel.podeAvancar.collectAsState()

        LaunchedEffect(podeAvancar) {
            if (podeAvancar) {
                screenModel.avancarConsumido()
                navigator.push(DeliveryTypeVoyagerScreen())
            }
        }

        CheckoutMedidasScreen(
            medidas = state.medidas,
            erro = state.erro,
            podeContinuar = state.podeContinuar,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onAlturaChange = { screenModel.onEvent(CheckoutMedidasUiEvent.AlturaAlterada(it)) },
            onPesoChange = { screenModel.onEvent(CheckoutMedidasUiEvent.PesoAlterado(it)) },
            onOmbrosChange = { screenModel.onEvent(CheckoutMedidasUiEvent.OmbrosAlterados(it)) },
            onPeitoChange = { screenModel.onEvent(CheckoutMedidasUiEvent.PeitoAlterado(it)) },
            onCinturaChange = { screenModel.onEvent(CheckoutMedidasUiEvent.CinturaAlterada(it)) },
            onQuadrilChange = { screenModel.onEvent(CheckoutMedidasUiEvent.QuadrilAlterado(it)) },
            onMangaChange = { screenModel.onEvent(CheckoutMedidasUiEvent.MangaAlterada(it)) },
            onCalcaChange = { screenModel.onEvent(CheckoutMedidasUiEvent.CalcaAlterada(it)) },
            onCasacoChange = { screenModel.onEvent(CheckoutMedidasUiEvent.CasacoAlterado(it)) },
            onPescocoChange = { screenModel.onEvent(CheckoutMedidasUiEvent.PescocoAlterado(it)) },
            onObservacoesChange = { screenModel.onEvent(CheckoutMedidasUiEvent.ObservacoesAlteradas(it)) },
            onContinue = { screenModel.onEvent(CheckoutMedidasUiEvent.ContinuarClicado) }
        )
    }
}

/**
 * Step 3 — Tipo de Entrega.
 */
class DeliveryTypeVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { TipoEntregaScreenModel() }
        val state by screenModel.state.collectAsState()

        DeliveryTypeScreen(
            selected = state.tipoSeleccionado,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onSelect = { screenModel.onEvent(TipoEntregaUiEvent.TipoSeleccionado(it)) },
            onContinue = { navigator.push(AddressVoyagerScreen(state.tipoSeleccionado)) }
        )
    }
}

/**
 * Step 3 (sub) — Endereço / Ponto de Levantamento.
 */
class AddressVoyagerScreen(private val modoInicial: TipoEntrega) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { EnderecoScreenModel(modoInicial) }
        val state by screenModel.state.collectAsState()
        val podeAvancar by screenModel.podeAvancar.collectAsState()

        LaunchedEffect(podeAvancar) {
            if (podeAvancar) {
                screenModel.avancarConsumido()
                navigator.push(PaymentVoyagerScreen())
            }
        }

        AddressScreen(
            mode = state.modo,
            endereco = state.endereco,
            cities = state.cidadesDisponiveis,
            neighborhoods = state.bairrosDisponiveis,
            pontosLevantamento = state.pontosLevantamento,
            selectedPickupPoint = state.pontoSeleccionado,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onModeChange = { screenModel.onEvent(EnderecoUiEvent.ModoAlterado(it)) },
            onCityChange = { screenModel.onEvent(EnderecoUiEvent.CidadeAlterada(it)) },
            onNeighborhoodChange = { screenModel.onEvent(EnderecoUiEvent.BairroAlterado(it)) },
            onStreetChange = { screenModel.onEvent(EnderecoUiEvent.RuaAlterada(it)) },
            onReferenceChange = { screenModel.onEvent(EnderecoUiEvent.ReferenciaAlterada(it)) },
            onPickupPointSelect = { screenModel.onEvent(EnderecoUiEvent.PontoSeleccionado(it)) },
            onContinue = { screenModel.onEvent(EnderecoUiEvent.ContinuarClicado) }
        )
    }
}

/**
 * Step 4 — Pagamento M-Pesa.
 */
class PaymentVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { PagamentoScreenModel() }
        val state by screenModel.state.collectAsState()
        val podeAvancar by screenModel.podeAvancar.collectAsState()

        LaunchedEffect(podeAvancar) {
            if (podeAvancar) {
                screenModel.avancarConsumido()
                navigator.push(ConfirmationVoyagerScreen(state.numeroPedidoCriado ?: "1025"))
            }
        }

        PaymentScreen(
            numeroMpesa = state.numeroMpesa,
            mpesaTitleHolder = state.titularMpesa,
            uploadedFileName = state.nomeFicheiroCarregado,
            totalMzn = state.totalPedidoMt,
            cartItemCount = state.contadorCarrinho,
            onBack = { navigator.pop() },
            onCartClick = { navigator.push(CartVoyagerScreen()) },
            onPickFile = { screenModel.onEvent(PagamentoUiEvent.EscolherFicheiroClicado) },
            onRemoveFile = { screenModel.onEvent(PagamentoUiEvent.RemoverFicheiroClicado) },
            onSubmit = { screenModel.onEvent(PagamentoUiEvent.EnviarComprovativoClicado) }
        )
    }
}

/**
 * Step 5 — Confirmação.
 */
class ConfirmationVoyagerScreen(private val numeroPedido: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current

        ConfirmationScreen(
            orderNumber = numeroPedido,
            order = MockOrderStore.getAllOrders().firstOrNull { it.numero == numeroPedido },
            onSeeOrders = {
                navigator.popUntilRoot()
                tabNavigator.current = OrdersTab
            },
            onBackToHome = {
                navigator.popUntilRoot()
                tabNavigator.current = HomeTab
            }
        )
    }
}
