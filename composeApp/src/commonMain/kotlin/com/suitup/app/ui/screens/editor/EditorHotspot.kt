package com.suitup.app.ui.screens.editor

/**
 * Customization categories reachable from the Editor 2D immersive stage (Phase 9.5A).
 * UI-only — distinct from [EditorPart] (the older parts-dropdown selector) because the
 * two flows classify garment areas differently and [EditorPart]'s state is still owned
 * by [EditorPartesScreenModel] / [EditorCoresScreenModel] unchanged.
 */
enum class EditorHotspotCategory(val label: String) {
    Lapel("Lapela"),
    Fabric("Tecido"),
    Color("Cor"),
    Buttons("Botões"),
    ChestPocket("Bolso do peito"),
    SidePockets("Bolsos laterais"),
    Sleeves("Mangas/Punhos"),
    Vest("Colete"),
    Tie("Gravata/Laço"),
    Lining("Forro"),
    Fit("Caimento");

    companion object {
        fun all() = entries.toList()
    }
}

/** UI-only accessory choice — no domain/backend field exists for it (Task 8: state-only). */
enum class TieStyle(val label: String) {
    None("Sem gravata"),
    Necktie("Gravata"),
    BowTie("Laço"),
}

/**
 * A tappable point over the rendered suit photo. Coordinates are normalized (0f..1f)
 * against the actual rendered image rectangle — never the outer stage box — see
 * [resolveFitRect] for how a normalized point maps to real stage pixels.
 */
data class EditorHotspot(
    val id: String,
    val category: EditorHotspotCategory,
    val normalizedX: Float,
    val normalizedY: Float,
)

/**
 * Default hotspot placement for a centered, front-facing suit product photo (the Phase
 * 9.4 asset style). Positions are an engineering estimate for a generically-framed
 * garment, not per-image computer vision — recalibrate if a future photo set frames the
 * garment differently (see phase report "remaining risks"). ChestPocket/SidePockets and
 * Fabric/Vest/Tie/Lining/Fit have no dedicated dot (avoids stage clutter); they are reached
 * through the "more" hotspot + the sheet's own category selector.
 */
object EditorHotspotLayout {
    const val MoreHotspotId = "more"

    val stageHotspots: List<EditorHotspot> = listOf(
        EditorHotspot("lapel", EditorHotspotCategory.Lapel, normalizedX = 0.50f, normalizedY = 0.20f),
        EditorHotspot("color", EditorHotspotCategory.Color, normalizedX = 0.50f, normalizedY = 0.40f),
        EditorHotspot("buttons", EditorHotspotCategory.Buttons, normalizedX = 0.50f, normalizedY = 0.56f),
        EditorHotspot("sleeves", EditorHotspotCategory.Sleeves, normalizedX = 0.17f, normalizedY = 0.50f),
        EditorHotspot("pockets", EditorHotspotCategory.ChestPocket, normalizedX = 0.68f, normalizedY = 0.68f),
        EditorHotspot(MoreHotspotId, EditorHotspotCategory.Fabric, normalizedX = 0.84f, normalizedY = 0.90f),
    )
}

/** Rendered rect of a [androidx.compose.ui.layout.ContentScale.Fit]-scaled, center-aligned image inside a stage box. */
internal data class FitRect(val left: Float, val top: Float, val width: Float, val height: Float)

/**
 * Mandatory coordinate strategy (Task 3): resolves where the source image actually
 * lands inside a [stageWidth] x [stageHeight] box under ContentScale.Fit + Center,
 * accounting for letterboxing — so hotspots never align against the outer parent box.
 */
internal fun resolveFitRect(stageWidth: Float, stageHeight: Float, sourceWidth: Float, sourceHeight: Float): FitRect {
    if (stageWidth <= 0f || stageHeight <= 0f || sourceWidth <= 0f || sourceHeight <= 0f) {
        return FitRect(0f, 0f, stageWidth, stageHeight)
    }
    val scale = minOf(stageWidth / sourceWidth, stageHeight / sourceHeight)
    val renderedWidth = sourceWidth * scale
    val renderedHeight = sourceHeight * scale
    return FitRect(
        left = (stageWidth - renderedWidth) / 2f,
        top = (stageHeight - renderedHeight) / 2f,
        width = renderedWidth,
        height = renderedHeight,
    )
}
