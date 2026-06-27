package com.suitup.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.components.SuitLogoMark
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles
import com.suitup.app.ui.theme.SuitTheme
import kotlinx.coroutines.delay

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
            .background(SuitColors.InkBlack),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(72.dp)
                .height(2.dp)
                .background(SuitColors.GoldPrimary),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(SuitTheme.shapes.lg)
                    .background(SuitColors.WarmBlack),
                contentAlignment = Alignment.Center,
            ) {
                SuitLogoMark(size = 64.dp, tint = SuitColors.GoldChampagne)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "SuitUP",
                style = SuitTextStyles.displayMedium,
                color = SuitColors.Pearl,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "FEITO À SUA MEDIDA",
                style = SuitTextStyles.eyebrow,
                color = SuitColors.GoldChampagne,
            )
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .width(if (index == 0) 24.dp else 6.dp)
                        .height(3.dp)
                        .clip(SuitTheme.shapes.pill)
                        .background(
                            if (index == 0) SuitColors.GoldPrimary
                            else SuitColors.MutedGray
                        )
                )
            }
            Spacer(Modifier.height(56.dp))
        }
    }
}
