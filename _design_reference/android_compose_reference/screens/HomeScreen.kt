package com.suitup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suitup.data.MockData
import com.suitup.ui.components.PrimaryButton
import com.suitup.ui.components.SuitUPCard
import com.suitup.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCatalogClick: () -> Unit,
    onEditorClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SUITUP",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = androidx.compose.ui.unit.sp(4)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.marginMobile),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            item {
                Spacer(modifier = Modifier.height(spacing.sm))
                Text(
                    text = "A sua jornada sartorial começa aqui.",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            item {
                SuitUPCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DESIGNER DE TERNOS 3D",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(spacing.sm))
                    Text(
                        text = "Crie uma peça única adaptada perfeitamente ao seu corpo e estilo.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(spacing.md))
                    PrimaryButton(text = "Começar Customização", onClick = onEditorClick)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Coleção Pronta",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    TextButton(onClick = onCatalogClick) {
                        Text("Ver Tudo", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            items(MockData.suits) { suit ->
                SuitUPCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = suit.name, style = MaterialTheme.typography.headlineSmall)
                            Text(
                                text = suit.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(spacing.sm))
                            Text(text = "€ ${suit.price}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        // Placeholder for image
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(spacing.lg))
            }
        }
    }
}
