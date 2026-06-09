package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.suitup.app.ui.screens.auth.LoginScreen
import com.suitup.app.ui.screens.auth.LoginScreenModel
import com.suitup.app.ui.screens.auth.LoginUiEvent
import com.suitup.app.ui.screens.auth.OnboardingScreen
import com.suitup.app.ui.screens.auth.OnboardingScreenModel
import com.suitup.app.ui.screens.auth.OnboardingUiEvent
import com.suitup.app.ui.screens.auth.SplashScreen

class SplashVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        SplashScreen(
            onTimeout = {
                navigator?.replace(OnboardingVoyagerScreen())
            }
        )
    }
}

class OnboardingVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel { OnboardingScreenModel() }
        val state by screenModel.state.collectAsState()
        val navegarParaLogin by screenModel.navegarParaLogin.collectAsState()

        LaunchedEffect(navegarParaLogin) {
            if (navegarParaLogin) {
                screenModel.navegacaoConsumida()
                navigator?.replace(LoginVoyagerScreen())
            }
        }

        OnboardingScreen(
            currentPage = state.paginaActual,
            onNext = {
                screenModel.onEvent(OnboardingUiEvent.SeguinteClicado)
            },
            onSkip = {
                screenModel.onEvent(OnboardingUiEvent.SaltarClicado)
            }
        )
    }
}

class LoginVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel { LoginScreenModel() }
        val state by screenModel.state.collectAsState()
        val navegarParaShell by screenModel.navegarParaShell.collectAsState()

        LaunchedEffect(navegarParaShell) {
            if (navegarParaShell) {
                screenModel.navegacaoConsumida()
                navigator?.replaceAll(MainShellScreen())
            }
        }

        LoginScreen(
            email = state.email,
            password = state.palavraPasse,
            passwordVisible = state.palavraPasseVisivel,
            isLoading = state.carregando,
            emailError = state.erroEmail,
            passwordError = state.erroPalavraPasse,
            onEmailChange = {
                screenModel.onEvent(LoginUiEvent.EmailAlterado(it))
            },
            onPasswordChange = {
                screenModel.onEvent(LoginUiEvent.PalavraPasseAlterada(it))
            },
            onTogglePasswordVisibility = {
                screenModel.onEvent(LoginUiEvent.AlternarVisibilidadePalavraPasse)
            },
            onLogin = {
                screenModel.onEvent(LoginUiEvent.EntrarClicado)
            },
            onCreateAccount = {
                screenModel.onEvent(LoginUiEvent.EntrarDemoClicado)
            },
            onGoogleLogin = {
                screenModel.onEvent(LoginUiEvent.EntrarDemoClicado)
            },
            onAppleLogin = {
                screenModel.onEvent(LoginUiEvent.EntrarDemoClicado)
            }
        )
    }
}