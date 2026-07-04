package com.suitup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.ui.components.PrimaryButton
import com.suitup.ui.components.SuitUPTextField
import com.suitup.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val spacing = LocalSpacing.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CRIAR CONTA") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.marginMobile)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = "Junte-se à elite da alfaiataria.",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(spacing.lg))

            SuitUPTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nome Completo",
                placeholder = "João Silva",
                leadingIcon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(spacing.md))

            SuitUPTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "joao@exemplo.com",
                leadingIcon = Icons.Default.Email
            )
            Spacer(modifier = Modifier.height(spacing.md))

            SuitUPTextField(
                value = password,
                onValueChange = { password = it },
                label = "Palavra-passe",
                placeholder = "••••••••",
                leadingIcon = Icons.Default.Lock
            )

            Spacer(modifier = Modifier.height(spacing.xl))

            PrimaryButton(text = "Registar Agora", onClick = onRegisterSuccess)
        }
    }
}
