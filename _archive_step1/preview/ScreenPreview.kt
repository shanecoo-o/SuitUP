package com.suitup.app.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suitup.app.data.mock.MockData
import com.suitup.app.domain.model.DeliveryAddress
import com.suitup.app.domain.model.DeliveryType
import com.suitup.app.domain.model.LapelType
import com.suitup.app.domain.model.PickupPoint
import com.suitup.app.domain.model.SuitCategory
import com.suitup.app.domain.model.SuitParts
import com.suitup.app.ui.components.SuitEyebrow
import com.suitup.app.ui.screens.auth.LoginScreen
import com.suitup.app.ui.screens.auth.OnboardingScreen
import com.suitup.app.ui.screens.auth.SplashScreen
import com.suitup.app.ui.screens.cart.CartScreen
import com.suitup.app.ui.screens.catalog.SelectModelScreen
import com.suitup.app.ui.screens.checkout.AddressScreen
import com.suitup.app.ui.screens.checkout.CheckoutScreen
import com.suitup.app.ui.screens.checkout.ConfirmationScreen
import com.suitup.app.ui.screens.checkout.DeliveryTypeScreen
import com.suitup.app.ui.screens.checkout.PaymentScreen
import com.suitup.app.ui.screens.editor.Editor2DColorsScreen
import com.suitup.app.ui.screens.editor.Editor2DPartsScreen
import com.suitup.app.ui.screens.editor.EditorPart
import com.suitup.app.ui.screens.editor.Preview3DScreen
import com.suitup.app.ui.screens.editor.Preview3DState
import com.suitup.app.ui.screens.home.HomeScreen
import com.suitup.app.ui.screens.orders.TrackOrderScreen
import com.suitup.app.ui.screens.profile.ProfileScreen
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Seletor temporário de ecrãs — apenas para o Step 1 (UI estática).
 *
 * Step 2 substitui isto por Voyager Navigator com Screen classes navegáveis.
 *
 * Para usar:
 * 1. Abre o app — vê uma lista de ecrãs disponíveis
 * 2. Toca num para abrir
 * 3. Usa o back chevron no topo para voltar à lista
 */

enum class PreviewScreen(val displayName: String, val number: String) {
    Splash("Splash", "01"),
    Onboarding("Onboarding", "02"),
    Login("Login", "03"),
    Home("Home", "04"),
    SelectModel("Selecionar Modelo", "05"),
    Editor2DParts("Editor — Partes", "06"),
    Editor2DColors("Editor — Cores e Tecidos", "07"),
    Preview3D("Preview 3D", "08"),
    Checkout("Checkout — Dados", "09"),
    DeliveryType("Tipo de Entrega", "10"),
    Address("Endereço / Ponto", "11"),
    Payment("Pagamento M-Pesa", "12"),
    Confirmation("Confirmação", "13"),
    TrackOrder("Acompanhar Pedido", "14"),
    Profile("Perfil", "15"),
    Cart("Carrinho", "16"),
}

@Composable
fun ScreenPreviewApp() {
    var current by remember { mutableStateOf<PreviewScreen?>(null) }

    if (current == null) {
        ScreenSelector(onSelect = { current = it })
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            RenderScreen(screen = current!!)

            // Floating back button (only on the preview selector, not on the rendered screen)
            Box(
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp)
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SuitColors.Gold)
                    .clickable { current = null },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✕",
                    style = SuitTextStyles.titleLarge,
                    color = SuitColors.Ink,
                )
            }
        }
    }
}

@Composable
private fun ScreenSelector(onSelect: (PreviewScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SuitColors.Ink)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SuitEyebrow("Step 1 · UI estática", color = SuitColors.Gold)
            Text(
                text = "SuitUP — Screens",
                style = SuitTextStyles.displaySmall,
                color = SuitColors.SurfaceWhite,
            )
            Text(
                text = "16 ecrãs · toca para abrir",
                style = SuitTextStyles.bodyMedium,
                color = SuitColors.Smoke,
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(PreviewScreen.values()) { screen ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(screen) }
                        .padding(horizontal = 4.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        text = screen.number,
                        style = SuitTextStyles.labelLarge,
                        color = SuitColors.Slate,
                    )
                    Text(
                        text = screen.displayName,
                        style = SuitTextStyles.titleMedium,
                        color = SuitColors.Ink,
                        modifier = Modifier.weight(1f),
                    )
                    com.suitup.app.ui.icons.ForwardChevronIcon(
                        tint = SuitColors.Slate,
                        size = 16.dp,
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = SuitColors.Mist)
            }
        }
    }
}

@Composable
private fun RenderScreen(screen: PreviewScreen) {
    when (screen) {
        PreviewScreen.Splash -> SplashScreen(onTimeout = {})

        PreviewScreen.Onboarding -> {
            var page by remember { mutableStateOf(0) }
            OnboardingScreen(
                currentPage = page,
                onSkip = {},
                onNext = { page = (page + 1).coerceAtMost(2) },
            )
        }

        PreviewScreen.Login -> {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            LoginScreen(
                email = email,
                password = password,
                passwordVisible = passwordVisible,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                onLogin = {},
            )
        }

        PreviewScreen.Home -> HomeScreen(
            recentOrders = MockData.recentOrders,
            cartItemCount = 1,
        )

        PreviewScreen.SelectModel -> {
            var category by remember { mutableStateOf<SuitCategory?>(null) }
            SelectModelScreen(
                models = MockData.suitModels,
                selectedCategory = category,
                cartItemCount = 1,
                onCategorySelect = { category = it },
            )
        }

        PreviewScreen.Editor2DParts -> {
            var parts by remember { mutableStateOf(SuitParts(lapel = LapelType.Notch)) }
            var selectedPart by remember { mutableStateOf(EditorPart.Lapel) }
            Editor2DPartsScreen(
                parts = parts,
                selectedPart = selectedPart,
                garmentColor = SuitColors.Ink,
                cartItemCount = 1,
                onPartSelect = { selectedPart = it },
                onLapelChange = { parts = parts.copy(lapel = it) },
                onWidthChange = { parts = parts.copy(widthAdjust = it) },
            )
        }

        PreviewScreen.Editor2DColors -> {
            var selectedPart by remember { mutableStateOf(EditorPart.Sleeves) }
            var selectedColor by remember { mutableStateOf(MockData.colors[0]) }
            var selectedFabric by remember { mutableStateOf(MockData.fabrics[0]) }
            Editor2DColorsScreen(
                selectedPart = selectedPart,
                colors = MockData.colors,
                fabrics = MockData.fabrics,
                selectedColor = selectedColor,
                selectedFabric = selectedFabric,
                cartItemCount = 1,
                onPartSelect = { selectedPart = it },
                onColorSelect = { selectedColor = it },
                onFabricSelect = { selectedFabric = it },
            )
        }

        PreviewScreen.Preview3D -> {
            var preview3D by remember { mutableStateOf(Preview3DState()) }
            var lightOn by remember { mutableStateOf(false) }
            var darkBg by remember { mutableStateOf(true) }
            Preview3DScreen(
                state = preview3D,
                garmentColor = SuitColors.Ink,
                showLight = lightOn,
                backgroundDark = darkBg,
                cartItemCount = 1,
                onStateChange = { preview3D = it },
                onRotate = { preview3D = preview3D.copy(rotationY = preview3D.rotationY + 30f) },
                onZoom = {
                    val nextScale = if (preview3D.scale >= 1.5f) 0.8f else preview3D.scale + 0.2f
                    preview3D = preview3D.copy(scale = nextScale)
                },
                onToggleLight = { lightOn = !lightOn },
                onToggleBackground = { darkBg = !darkBg },
            )
        }

        PreviewScreen.Checkout -> {
            val u = MockData.currentUser
            var name by remember { mutableStateOf(u.name) }
            var phone by remember { mutableStateOf(u.phone) }
            var email by remember { mutableStateOf(u.email) }
            var useSaved by remember { mutableStateOf(false) }
            CheckoutScreen(
                fullName = name,
                phone = phone,
                email = email,
                useSavedMeasurements = useSaved,
                cartItemCount = 1,
                onFullNameChange = { name = it },
                onPhoneChange = { phone = it },
                onEmailChange = { email = it },
                onToggleMeasurements = { useSaved = it },
            )
        }

        PreviewScreen.DeliveryType -> {
            var selected by remember { mutableStateOf(DeliveryType.Delivery) }
            DeliveryTypeScreen(
                selected = selected,
                cartItemCount = 1,
                onSelect = { selected = it },
            )
        }

        PreviewScreen.Address -> {
            var mode by remember { mutableStateOf(DeliveryType.Delivery) }
            var address by remember {
                mutableStateOf(
                    DeliveryAddress(
                        city = "Maputo",
                        neighborhood = "Polana",
                        street = "Av. Julius Nyerere, 123",
                        reference = "Próximo ao Shopping Polana",
                    )
                )
            }
            var selectedPoint by remember { mutableStateOf<PickupPoint?>(null) }
            AddressScreen(
                mode = mode,
                address = address,
                cities = MockData.mozambicanCities,
                neighborhoods = MockData.maputoNeighborhoods,
                pickupPoints = MockData.pickupPoints,
                selectedPickupPoint = selectedPoint,
                cartItemCount = 1,
                onModeChange = { mode = it },
                onCityChange = { address = address.copy(city = it) },
                onNeighborhoodChange = { address = address.copy(neighborhood = it) },
                onStreetChange = { address = address.copy(street = it) },
                onReferenceChange = { address = address.copy(reference = it) },
                onPickupPointSelect = { selectedPoint = it },
            )
        }

        PreviewScreen.Payment -> {
            var uploaded by remember { mutableStateOf<String?>(null) }
            PaymentScreen(
                mpesaNumber = MockData.mpesaNumber,
                mpesaTitleHolder = MockData.mpesaTitle,
                uploadedFileName = uploaded,
                cartItemCount = 1,
                onPickFile = { uploaded = "comprovativo_mpesa.jpg" },
                onRemoveFile = { uploaded = null },
            )
        }

        PreviewScreen.Confirmation -> ConfirmationScreen(
            orderNumber = MockData.newOrder.number,
        )

        PreviewScreen.TrackOrder -> TrackOrderScreen(
            order = MockData.newOrder,
            cartItemCount = 1,
        )

        PreviewScreen.Profile -> ProfileScreen(
            user = MockData.currentUser,
            cartItemCount = 1,
        )

        PreviewScreen.Cart -> {
            var items by remember { mutableStateOf(MockData.cartItems) }
            CartScreen(
                items = items,
                deliveryFeeMt = MockData.deliveryFeeMt,
                cartItemCount = items.sumOf { it.quantity },
                onItemRemove = { rem -> items = items.filter { it.id != rem.id } },
                onQuantityChange = { item, qty ->
                    items = items.map { if (it.id == item.id) it.copy(quantity = qty) else it }
                },
            )
        }
    }
}
