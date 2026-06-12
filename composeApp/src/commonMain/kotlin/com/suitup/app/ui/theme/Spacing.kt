package com.suitup.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Sistema de espaçamento do SuitUP.
 *
 * Escala baseada em 4px com macro-whitespace para sentir "premium / editorial".
 * Princípio aplicado: "Vary spacing for rhythm. Same padding everywhere is monotony."
 */
@Immutable
data class SuitSpacing(
    val xxxs: Dp = 2.dp,
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 40.dp,
    val huge: Dp = 56.dp,
    val massive: Dp = 80.dp,

    // Tokens semânticos (otimizados para mobile)
    val screenPadding: Dp = 20.dp,
    val cardPadding: Dp = 16.dp,
    val cardGap: Dp = 12.dp,
    val sectionGap: Dp = 24.dp,
    val inputGap: Dp = 8.dp,
    val fieldGap: Dp = 14.dp,
)

val LocalSuitSpacing = staticCompositionLocalOf { SuitSpacing() }
