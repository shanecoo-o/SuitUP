package com.suitup.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.suitup.app.ui.navigation.SplashVoyagerScreen
import com.suitup.app.ui.theme.SuitTheme

/**
 * App raiz.
 *
 * Step 2: Voyager Navigator. Entry: SplashVoyagerScreen → Onboarding → Login → MainShell.
 *
 * Step 5 vai adicionar uma RootScreen que verifica auth state (token guardado) e
 * decide entre forward para Splash (1ª vez) ou MainShellScreen direto (já logado).
 */
@Composable
fun App() {
    SuitTheme {
        Navigator(SplashVoyagerScreen()) { navigator ->
            FadeTransition(navigator)
        }
    }
}
