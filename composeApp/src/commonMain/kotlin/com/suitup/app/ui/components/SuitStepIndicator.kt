package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

/**
 * Indicator 1-2-3-4 com bolinhas e linhas ligando.
 * O passo ativo tem fundo Ink (preto) e número branco.
 * Passos completos têm fundo Ink. Passos futuros têm border Mist.
 */
@Composable
fun SuitStepIndicator(
    currentStep: Int,
    totalSteps: Int = 4,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (step in 1..totalSteps) {
            val isActive = step == currentStep
            val isCompleted = step < currentStep

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isActive || isCompleted) SuitColors.Gold else SuitColors.SurfaceLow)
                    .border(
                        width = 1.dp,
                        color = if (isActive || isCompleted) SuitColors.Gold else SuitColors.Mist,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.toString(),
                    style = SuitTextStyles.labelMedium,
                    color = if (isActive || isCompleted) SuitColors.GoldInk else SuitColors.Smoke,
                )
            }

            if (step < totalSteps) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(1.dp)
                        .background(if (isCompleted) SuitColors.Gold else SuitColors.Mist)
                )
            }
        }
    }
}
