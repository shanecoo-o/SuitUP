package com.suitup.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Sistema de motion do SuitUP.
 *
 * Princípio: ease-out exponencial. Sem bounce, sem elastic.
 * Motion deve sentir-se invisível — presente mas nunca distrativo.
 */
@Immutable
data class SuitMotion(
    // Durations em ms
    val instant: Int = 80,
    val fast: Int = 150,
    val normal: Int = 250,
    val slow: Int = 400,
    val deliberate: Int = 600,
    val cinematic: Int = 800,

    // Easing curves
    val easeOutQuart: Easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f),
    val easeOutExpo: Easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f),
    val easeInOutQuart: Easing = CubicBezierEasing(0.76f, 0f, 0.24f, 1f),
    val standard: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f),
)

val LocalSuitMotion = staticCompositionLocalOf { SuitMotion() }

/**
 * Specs prontos para uso comum.
 */
object SuitAnim {
    fun <T> fast(): AnimationSpec<T> = tween(
        durationMillis = 150,
        easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
    )

    fun <T> normal(): AnimationSpec<T> = tween(
        durationMillis = 250,
        easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    )

    fun <T> slow(): AnimationSpec<T> = tween(
        durationMillis = 400,
        easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    )

    fun <T> reveal(): AnimationSpec<T> = tween(
        durationMillis = 600,
        easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    )
}
