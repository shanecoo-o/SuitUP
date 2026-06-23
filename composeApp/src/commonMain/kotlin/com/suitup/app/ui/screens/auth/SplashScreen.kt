package com.suitup.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import suitup.composeapp.generated.resources.Res
import suitup.composeapp.generated.resources.logo_suitup

/**
 * Ecrã 01 — Splash.
 *
 * Logo centrado com tagline subtil. Após [autoAdvanceMillis] chama [onTimeout].
 * Composable stateless: a navegação real é injetada pelo Voyager no Step 2.
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit = {},
    autoAdvanceMillis: Long = 1800L,
) {
    LaunchedEffect(Unit) {
        delay(autoAdvanceMillis)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SuitColors.Bone),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.logo_suitup),
                contentDescription = "Logotipo SuitUP",
                modifier = Modifier.height(96.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Suit Up",
                style = SuitTextStyles.brand.copy(fontWeight = FontWeight.Normal),
                color = SuitColors.Ink,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Vista-se para o sucesso",
                style = SuitTextStyles.bodySmall,
                color = SuitColors.Slate,
            )
        }
    }
}
