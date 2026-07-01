package com.suitup.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.suitup.app.ui.navigation.SplashVoyagerScreen
import com.suitup.app.ui.theme.SuitTheme

/**
 * App raiz.
 *
 * Voyager Navigator. O Splash restaura a sessão e encaminha para Login,
 * MainShell ou AdminDashboard conforme o estado e os papéis do backend.
 */
@Composable
fun App() {
    SuitTheme {
        Navigator(SplashVoyagerScreen()) { navigator ->
            FadeTransition(navigator)
        }
    }
}
