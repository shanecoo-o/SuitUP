package com.suitup.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tipografia SuitUP.
 *
 * Mockup pede: Playfair Display (títulos) + Inter (texto).
 *
 * NOTA: Para ficar funcional sem ficheiros de fontes em runtime,
 * usamos FontFamily.Serif e FontFamily.SansSerif como fallback.
 * Quando os ficheiros .ttf forem adicionados a composeResources/font/,
 * substituir por Font(Res.font.PlayfairDisplay_Bold) etc.
 *
 * Princípios aplicados:
 * - Tight tracking (-0.02em a -0.04em) em headlines
 * - Line-height 1.6 em body para legibilidade
 * - Hierarquia por escala + peso (ratio ≥1.25 entre steps)
 */

// Quando adicionares os ficheiros .ttf, importar via Res.font:
// import suitup.composeapp.generated.resources.Res
// import suitup.composeapp.generated.resources.PlayfairDisplay_Regular
// E criar:
// @Composable fun playfairDisplayFamily() = FontFamily(
//     Font(Res.font.PlayfairDisplay_Regular, FontWeight.Normal),
//     Font(Res.font.PlayfairDisplay_Medium, FontWeight.Medium),
//     Font(Res.font.PlayfairDisplay_Bold, FontWeight.Bold),
// )

private val DisplayFamily: FontFamily = FontFamily.Serif      // → Playfair Display
private val BodyFamily: FontFamily = FontFamily.SansSerif     // → Inter
private val MonoFamily: FontFamily = FontFamily.Monospace     // → JetBrains Mono / SF Mono

/**
 * Estilos semânticos do SuitUP.
 * Mais ricos que os defaults do Material e alinhados com a hierarquia visual do mockup.
 */
object SuitTextStyles {
    val displayLarge = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-1.2).sp,
    )
    val displayMedium = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.8).sp,
    )
    val displaySmall = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp,
    )

    val headlineLarge = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.3).sp,
    )
    val headlineMedium = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
    )
    val headlineSmall = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
    )

    val titleLarge = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )
    val titleMedium = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    )
    val titleSmall = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    val bodyLarge = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,                  // 1.6 ratio
    )
    val bodyMedium = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    )
    val bodySmall = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    )

    val labelLarge = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    val labelMedium = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
    )
    val labelSmall = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp,
    )

    /**
     * Eyebrow tag — pequeno texto em uppercase com letter-spacing largo.
     * Inspirado nas skills: "rounded-full px-3 py-1 text-[10px] uppercase tracking-[0.2em]"
     */
    val eyebrow = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.6.sp,
    )

    val mono = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    val button = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp,
    )

    val brand = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.4).sp,
    )
}

@Composable
fun suitMaterialTypography(): Typography = Typography(
    displayLarge = SuitTextStyles.displayLarge,
    displayMedium = SuitTextStyles.displayMedium,
    displaySmall = SuitTextStyles.displaySmall,
    headlineLarge = SuitTextStyles.headlineLarge,
    headlineMedium = SuitTextStyles.headlineMedium,
    headlineSmall = SuitTextStyles.headlineSmall,
    titleLarge = SuitTextStyles.titleLarge,
    titleMedium = SuitTextStyles.titleMedium,
    titleSmall = SuitTextStyles.titleSmall,
    bodyLarge = SuitTextStyles.bodyLarge,
    bodyMedium = SuitTextStyles.bodyMedium,
    bodySmall = SuitTextStyles.bodySmall,
    labelLarge = SuitTextStyles.labelLarge,
    labelMedium = SuitTextStyles.labelMedium,
    labelSmall = SuitTextStyles.labelSmall,
)
