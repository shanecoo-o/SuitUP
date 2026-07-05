package com.suitup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.suitup.app.ui.screens.profile.AddressesScreen
import com.suitup.app.ui.screens.profile.MeasurementsScreen
import com.suitup.app.ui.screens.profile.NotificationsScreen
import com.suitup.app.ui.screens.profile.PerfilScreenModel
import com.suitup.app.ui.screens.profile.PersonalDataScreen
import com.suitup.app.ui.screens.profile.SupportScreen

class PersonalDataVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { PerfilScreenModel() }
        val state by screenModel.state.collectAsState()

        PersonalDataScreen(
            user = state.utilizador,
            onBack = { navigator.pop() },
        )
    }
}

class AddressesVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        AddressesScreen(onBack = { navigator.pop() })
    }
}

class MeasurementsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { PerfilScreenModel() }
        val state by screenModel.state.collectAsState()

        MeasurementsScreen(
            user = state.utilizador,
            onBack = { navigator.pop() },
        )
    }
}

class NotificationsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        NotificationsScreen(onBack = { navigator.pop() })
    }
}

class SupportVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        SupportScreen(onBack = { navigator.pop() })
    }
}
