package com.suitup.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.SuitHeroPlaceholder
import com.suitup.app.ui.components.SuitNextButton
import com.suitup.app.ui.components.SuitPageIndicator
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Conteúdo de uma página do onboarding.
 * Domain-pure — não conhece Compose. Vive aqui (em vez do domain/) porque
 * é puramente apresentacional, não regra de negócio.
 */
data class OnboardingPage(
    val title: String,
    val description: String,
)

val DefaultOnboardingPages = listOf(
    OnboardingPage(
        title = "Feito à sua medida",
        description = "Personalize cada detalhe do seu fato como quiser."
    ),
    OnboardingPage(
        title = "Veja em 3D",
        description = "Visualize o seu fato em três dimensões antes de encomendar."
    ),
    OnboardingPage(
        title = "Receba em casa",
        description = "Pague via M-Pesa e receba em qualquer ponto de Maputo."
    ),
)

/**
 * Ecrã 02 — Onboarding.
 *
 * Hero escuro com placeholder de figura, texto editorial sobreposto em baixo,
 * pager de 3 dots à esquerda, botão "Seguinte" à direita.
 *
 * Stateless: state hoisting via [currentPage] e callbacks.
 * O ScreenModel (Step 3) controla a página corrente.
 */
@Composable
fun OnboardingScreen(
    pages: List<OnboardingPage> = DefaultOnboardingPages,
    currentPage: Int = 0,
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {},
) {
    val page = pages.getOrElse(currentPage) { pages.first() }
    val isLast = currentPage >= pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Black)
    ) {
        // Hero placeholder ocupa o ecrã todo no fundo
        SuitHeroPlaceholder(
            modifier = Modifier.fillMaxSize(),
            background = SuitColors.Charcoal,
        )

        // Camada de escurecimento na parte inferior (sem gradient — banda sólida fina)
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(SuitColors.Black)
            )
        }

        // Conteúdo sobreposto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top: skip à direita
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (!isLast) {
                    Text(
                        text = "Saltar",
                        style = SuitTextStyles.labelMedium,
                        color = SuitColors.SurfaceWhite.copy(alpha = 0.7f),
                        modifier = Modifier
                            .clickable(onClick = onSkip)
                            .padding(8.dp),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Bottom: título + descrição + dots/botão
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = page.title,
                    style = SuitTextStyles.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = SuitColors.SurfaceWhite,
                )

                Text(
                    text = page.description,
                    style = SuitTextStyles.bodyLarge,
                    color = SuitColors.SurfaceWhite.copy(alpha = 0.75f),
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SuitPageIndicator(
                        currentPage = currentPage,
                        totalPages = pages.size,
                        dark = true,
                    )
                    SuitNextButton(
                        text = if (isLast) "Começar" else "Seguinte",
                        onClick = onNext,
                        dark = true,
                    )
                }
            }
        }
    }
}
