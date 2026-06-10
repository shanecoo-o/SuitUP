package com.suitup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suitup.data.MockData
import com.suitup.ui.components.PrimaryButton
import com.suitup.ui.components.ProgressRail
import com.suitup.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    var selectedFabric by remember { mutableStateOf(MockData.fabrics[0]) }
    val steps = listOf("Medidas", "Tecido", "Estilo", "Finalizar")
    val currentStep = 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EDITOR DE DESIGN", style = MaterialTheme.typography.labelLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // "3D" Visualizer Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFF2A2A2A), Color(0xFF131313))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "VISUALIZAÇÃO 3D",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White.copy(alpha = 0.1f)
                )
            }

            // Progress Rail Overlay
            ProgressRail(
                steps = steps,
                currentStep = currentStep,
                modifier = Modifier.padding(spacing.marginMobile).align(Alignment.TopStart)
            )

            // Fabric Selector Bottom Sheet Style
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f))
                    .padding(spacing.md)
            ) {
                Text(
                    text = "SELECIONE O TECIDO",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(spacing.sm))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    contentPadding = PaddingValues(vertical = spacing.xs)
                ) {
                    items(MockData.fabrics) { fabric ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(80.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(fabric.colorHex)))
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedFabric == fabric) MaterialTheme.colorScheme.primary
                                                else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedFabric = fabric },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedFabric == fabric) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fabric.name,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.md))
                PrimaryButton(text = "Confirmar Tecido", onClick = onFinishClick)
            }
        }
    }
}
