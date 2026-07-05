package com.suitup.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.suitup.app.data.admin.AdminOperationsRuntime
import com.suitup.app.data.catalog.CatalogRuntime
import com.suitup.app.data.remote.auth.InMemoryTokenStore
import com.suitup.app.data.remote.auth.TokenStore
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.data.order.OrderRuntime
import com.suitup.app.data.payment.PaymentTrackingRuntime
import com.suitup.app.ui.navigation.SplashVoyagerScreen
import com.suitup.app.ui.theme.SuitResponsiveRoot
import com.suitup.app.ui.theme.SuitTheme

/**
 * App raiz.
 *
 * Voyager Navigator. O Splash restaura a sessão e encaminha para Login,
 * MainShell ou AdminDashboard conforme o estado e os papéis do backend.
 */
@Composable
fun App(tokenStore: TokenStore = InMemoryTokenStore()) {
    AuthRuntime.initialize(tokenStore)
    CatalogRuntime.initialize(AuthRuntime.remoteModule.catalogRepository)
    OrderRuntime.initialize(AuthRuntime.remoteModule.orderRepository)
    PaymentTrackingRuntime.initialize(
        paymentRepository = AuthRuntime.remoteModule.paymentRepository,
        fileRepository = AuthRuntime.remoteModule.fileRepository,
        orderRepository = AuthRuntime.remoteModule.orderRepository,
    )
    AdminOperationsRuntime.initialize(
        adminRepository = AuthRuntime.remoteModule.adminRepository,
        orderRepository = AuthRuntime.remoteModule.orderRepository,
        paymentRepository = AuthRuntime.remoteModule.paymentRepository,
    )

    SuitTheme {
        SuitResponsiveRoot {
            Navigator(SplashVoyagerScreen()) { navigator ->
                FadeTransition(navigator)
            }
        }
    }
}
