package com.suitup.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginClick = { navController.navigate("home") },
                onRegisterClick = { navController.navigate("registration") }
            )
        }
        composable("registration") {
            RegistrationScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("home") }
            )
        }
        composable("home") {
            HomeScreen(
                onCatalogClick = { navController.navigate("catalog") },
                onEditorClick = { navController.navigate("editor") },
                onProfileClick = { navController.navigate("tracking") }
            )
        }
        composable("editor") {
            EditorScreen(
                onBackClick = { navController.popBackStack() },
                onFinishClick = { navController.navigate("home") }
            )
        }
        composable("catalog") {
            CatalogScreen(
                onBackClick = { navController.popBackStack() },
                onSuitClick = { /* No-op */ }
            )
        }
        composable("tracking") {
            TrackingScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
