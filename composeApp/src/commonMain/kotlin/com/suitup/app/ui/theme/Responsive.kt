package com.suitup.app.ui.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * SuitUP responsive classification engine (Phase 6C.1).
 *
 * Width-class boundaries and adaptive padding are the Prompt 9
 * `suitup_implementation_blueprint.md` values (the final-engineering-handoff
 * source, per the migration audit's own resolution of the two conflicting
 * Stitch breakpoint tables — Prompt 7's earlier grid doc used different
 * boundaries and is superseded).
 *
 * Height-class thresholds are NOT specified anywhere in the Stitch handoff
 * (audited and confirmed missing) — the values below are an engineering
 * judgment call, not a sourced design value. Revisit if a later design pass
 * supplies concrete numbers.
 *
 * This lives in commonMain and is built on BoxWithConstraints (portable
 * across Android/iOS/Desktop) rather than any Android-only configuration API.
 */
enum class SuitWidthClass { NARROW, STANDARD, MEDIUM, WIDE }

enum class SuitHeightClass { SHORT, STANDARD, TALL }

@Immutable
data class SuitResponsiveInfo(
    val widthClass: SuitWidthClass,
    val heightClass: SuitHeightClass,
    val screenWidth: Dp,
    val screenHeight: Dp,
    val horizontalContentPadding: Dp,
    val maxContentWidth: Dp?,
    val isCompactLayout: Boolean,
    val isWideLayout: Boolean,
    val isShortHeight: Boolean,
)

private fun classifyWidth(width: Dp): SuitWidthClass = when {
    width.value < 360f -> SuitWidthClass.NARROW      // 320-359
    width.value < 400f -> SuitWidthClass.STANDARD    // 360-399
    width.value < 480f -> SuitWidthClass.MEDIUM      // 400-479
    else -> SuitWidthClass.WIDE                      // 480-599(+)
}

private fun classifyHeight(height: Dp): SuitHeightClass = when {
    height.value < 640f -> SuitHeightClass.SHORT
    height.value < 760f -> SuitHeightClass.STANDARD
    else -> SuitHeightClass.TALL
}

private fun horizontalPaddingFor(widthClass: SuitWidthClass): Dp = when (widthClass) {
    SuitWidthClass.NARROW -> 16.dp
    SuitWidthClass.STANDARD -> 20.dp
    SuitWidthClass.MEDIUM -> 24.dp
    SuitWidthClass.WIDE -> 24.dp
}

/** Wide phones cap and center content instead of stretching edge-to-edge (blueprint.md). */
private fun maxContentWidthFor(widthClass: SuitWidthClass): Dp? =
    if (widthClass == SuitWidthClass.WIDE) 440.dp else null

fun suitResponsiveInfoOf(screenWidth: Dp, screenHeight: Dp): SuitResponsiveInfo {
    val widthClass = classifyWidth(screenWidth)
    val heightClass = classifyHeight(screenHeight)
    return SuitResponsiveInfo(
        widthClass = widthClass,
        heightClass = heightClass,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        horizontalContentPadding = horizontalPaddingFor(widthClass),
        maxContentWidth = maxContentWidthFor(widthClass),
        isCompactLayout = widthClass == SuitWidthClass.NARROW || widthClass == SuitWidthClass.STANDARD,
        isWideLayout = widthClass == SuitWidthClass.WIDE,
        isShortHeight = heightClass == SuitHeightClass.SHORT,
    )
}

val LocalSuitResponsiveInfo = staticCompositionLocalOf {
    suitResponsiveInfoOf(360.dp, 720.dp) // Standard/Standard fallback so previews outside SuitResponsiveRoot don't crash
}

/**
 * Wraps [content] with the actual available Compose space classified into a
 * [SuitResponsiveInfo], exposed both as a parameter and via [LocalSuitResponsiveInfo]
 * so descendants can read `SuitTheme.responsive` without threading it through.
 */
@Composable
fun SuitResponsiveRoot(
    modifier: Modifier = Modifier,
    content: @Composable (SuitResponsiveInfo) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val info = suitResponsiveInfoOf(maxWidth, maxHeight)
        CompositionLocalProvider(LocalSuitResponsiveInfo provides info) {
            content(info)
        }
    }
}

/**
 * Reusable max-width caps for common containers (Task 13). Mobile-portrait only —
 * no tablet layouts. Forms/sheets cap earlier than the general screen content so
 * they stay readable even on the 480-599dp "wide" width class.
 */
object SuitContentWidth {
    val screen: Dp = 440.dp
    val form: Dp = 400.dp
    val sheet: Dp = 480.dp
}

/**
 * Shared grid-column policy (Task 14). Deliberately simple — a lookup per width
 * class, not a generic grid-arithmetic engine.
 */
object SuitGridPolicy {
    fun productColumns(widthClass: SuitWidthClass): Int = when (widthClass) {
        SuitWidthClass.NARROW -> 1
        SuitWidthClass.STANDARD -> 2
        SuitWidthClass.MEDIUM -> 2
        SuitWidthClass.WIDE -> 2
    }

    fun metricColumns(widthClass: SuitWidthClass): Int = when (widthClass) {
        SuitWidthClass.NARROW -> 1
        SuitWidthClass.STANDARD -> 2
        SuitWidthClass.MEDIUM -> 2
        SuitWidthClass.WIDE -> 3
    }
}

/**
 * Phone width/height "shape" derived from both classes together (Task 15) —
 * width alone is not a sufficient signal for hero/stage sizing decisions.
 */
enum class SuitPhoneShape { TALL_NARROW, SHORT_WIDE, STANDARD, LARGE_WIDE }

fun SuitResponsiveInfo.phoneShape(): SuitPhoneShape = when {
    heightClass == SuitHeightClass.SHORT && widthClass >= SuitWidthClass.MEDIUM -> SuitPhoneShape.SHORT_WIDE
    heightClass == SuitHeightClass.TALL && widthClass <= SuitWidthClass.NARROW -> SuitPhoneShape.TALL_NARROW
    widthClass == SuitWidthClass.WIDE && heightClass != SuitHeightClass.SHORT -> SuitPhoneShape.LARGE_WIDE
    else -> SuitPhoneShape.STANDARD
}
