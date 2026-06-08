package com.suitup.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSuitColors = staticCompositionLocalOf { SuitColorTokens() }

/**
 * Tema raiz do SuitUP.
 *
 * Fornece via CompositionLocal:
 * - SuitColors (tokens semânticos extra)
 * - SuitSpacing (escala de espaçamento)
 * - SuitShapes (formas semânticas)
 * - SuitMotion (durations + easings)
 *
 * Acesso conveniente via `SuitTheme.colors`, `SuitTheme.spacing`, etc.
 */
@Composable
fun SuitTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSuitColors provides SuitColorTokens(),
        LocalSuitSpacing provides SuitSpacing(),
        LocalSuitShapes provides SuitShapes(),
        LocalSuitMotion provides SuitMotion(),
    ) {
        MaterialTheme(
            colorScheme = SuitLightColorScheme,
            typography = suitMaterialTypography(),
            shapes = SuitMaterialShapes,
            content = content
        )
    }
}

object SuitTheme {
    val colors: SuitColorTokens
        @Composable get() = LocalSuitColors.current

    val spacing: SuitSpacing
        @Composable get() = LocalSuitSpacing.current

    val shapes: SuitShapes
        @Composable get() = LocalSuitShapes.current

    val motion: SuitMotion
        @Composable get() = LocalSuitMotion.current
}
