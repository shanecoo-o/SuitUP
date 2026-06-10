package com.suitup.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class SuitUPSpacing(
    val base: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 16.dp,
    val md: Dp = 24.dp,
    val lg: Dp = 32.dp,
    val xl: Dp = 48.dp,
    val gutter: Dp = 16.dp,
    val marginMobile: Dp = 20.dp,
    val marginDesktop: Dp = 64.dp
)

val LocalSpacing = staticCompositionLocalOf { SuitUPSpacing() }
