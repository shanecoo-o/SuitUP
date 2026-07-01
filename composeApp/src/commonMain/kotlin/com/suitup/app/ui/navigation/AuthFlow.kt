package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.suitup.app.data.session.AuthRuntime
import com.suitup.app.domain.model.AppUserRole
import com.suitup.app.domain.model.AuthSessionState
import com.suitup.app.ui.screens.auth.AuthNavigationTarget
import com.suitup.app.ui.screens.auth.LoginScreen
import com.suitup.app.ui.screens.auth.LoginScreenModel
import com.suitup.app.ui.screens.auth.LoginUiEvent
import com.suitup.app.ui.screens.auth.OnboardingScreen
import com.suitup.app.ui.screens.auth.OnboardingScreenModel
import com.suitup.app.ui.screens.auth.OnboardingUiEvent
import com.suitup.app.ui.screens.auth.RegisterScreen
import com.suitup.app.ui.screens.auth.RegisterScreenModel
import com.suitup.app.ui.screens.auth.SplashScreen

class SplashVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val sessionManager = AuthRuntime.sessionManager
        val sessionState by sessionManager.state.collectAsState()

        LaunchedEffect(Unit) {
            sessionManager.restoreSession()
        }

        LaunchedEffect(sessionState) {
            when (val session = sessionState) {
                AuthSessionState.Checking -> Unit
                is AuthSessionState.Unauthenticated -> navigator?.replace(LoginVoyagerScreen())
                is AuthSessionState.Authenticated -> {
                    if (session.account.primaryRole == AppUserRole.ADMIN) {
                        navigator?.replaceAll(AdminDashboardVoyagerScreen())
                    } else {
                        navigator?.replaceAll(MainShellScreen())
                    }
                }
            }
        }

        SplashScreen(onTimeout = {})
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
        val navigationTarget by screenModel.navigationTarget.collectAsState()

        LaunchedEffect(navigationTarget) {
            when (navigationTarget) {
                AuthNavigationTarget.CustomerHome -> {
                    screenModel.navegacaoConsumida()
                    navigator?.replaceAll(MainShellScreen())
                }
                AuthNavigationTarget.AdminDashboard -> {
                    screenModel.navegacaoConsumida()
                    navigator?.replaceAll(AdminDashboardVoyagerScreen())
                }
                null -> Unit
            }
        }

        LoginScreen(
            email = state.email,
            password = state.palavraPasse,
            passwordVisible = state.palavraPasseVisivel,
            isLoading = state.carregando,
            emailError = state.erroEmail,
            passwordError = state.erroPalavraPasse,
            generalError = state.erroGeral,
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
                navigator?.push(RegisterVoyagerScreen())
            },
        )
    }
}

class RegisterVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel { RegisterScreenModel() }
        val state by screenModel.state.collectAsState()
        val navigationTarget by screenModel.navigationTarget.collectAsState()

        LaunchedEffect(navigationTarget) {
            when (navigationTarget) {
                AuthNavigationTarget.CustomerHome -> {
                    screenModel.navegacaoConsumida()
                    navigator?.replaceAll(MainShellScreen())
                }
                AuthNavigationTarget.AdminDashboard -> {
                    screenModel.navegacaoConsumida()
                    navigator?.replaceAll(AdminDashboardVoyagerScreen())
                }
                null -> Unit
            }
        }

        RegisterScreen(
            state = state,
            onEvent = screenModel::onEvent,
            onBack = { navigator?.pop() },
        )
    }
}
