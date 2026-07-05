# Phase 9.5A — Editor 2D Real Experience Report

## Scope

Replaced the two-step Editor flow (Parts screen → Colors screen → Preview 3D) with a single
immersive full-screen stage screen: real suit product photo, tappable hotspots, an adaptive
bottom customization sheet, and one CTA ("Ver Preview 3D") into the unmodified Preview 3D route.

## Files changed

**Added**
- `ui/screens/editor/EditorHotspot.kt` — `EditorHotspotCategory` (11 categories), `TieStyle`,
  `EditorHotspot`, `EditorHotspotLayout` (6 stage dots incl. a "more" hotspot), `FitRect` +
  `resolveFitRect(...)`.
- `ui/screens/editor/EditorCustomizationSheet.kt` — shared adaptive bottom sheet shell
  (70%/85% screen height), category chip row, scrollable body slot.
- `ui/screens/editor/Editor2DStageScreen.kt` — the new stage screen: immersive scaffold,
  hotspot-annotated product photo, color badge overlay, CTA bar, and the per-category
  customization bodies (lapel/fabric/color swatch rows, option grids, fit slider, vest/tie
  state-only controls).

**Modified**
- `ui/screens/editor/EditorScreenModels.kt` — additive only: `imagemKey` field on
  `EditorPartesUiState`; `BotoesAlterados`/`BolsoAlterado`/`MangasAlteradas`/`ForroAlterado`
  events + handlers on `EditorPartesScreenModel`; new `EditorAccessoriesUiState` /
  `EditorAccessoriesUiEvent` / `EditorAccessoriesScreenModel` (state-only, category C).
- `ui/navigation/EditorScreens.kt` — removed `Editor2DPartsVoyagerScreen` /
  `Editor2DColorsVoyagerScreen`, added `Editor2DStageVoyagerScreen(modeloId)` wiring all three
  screen models into `Editor2DStageScreen`. `Preview3DVoyagerScreen` untouched.
- `ui/navigation/ProductDetailScreens.kt` — `onCustomize` now pushes
  `Editor2DStageVoyagerScreen(model.id)`.

**Deleted**
- `ui/screens/editor/Editor2DPartsScreen.kt`, `ui/screens/editor/Editor2DColorsScreen.kt`
  (superseded by the consolidated stage screen; contents re-checked before deletion, nothing
  reusable left behind).

No backend/repository/remote API files touched. No ScreenModel rewritten — only additive
fields/events on the existing two, plus one new small state-only ScreenModel.

## Selected-model continuity

`Editor2DStageVoyagerScreen` reads `modeloId` and instantiates `EditorPartesScreenModel`,
`EditorCoresScreenModel` (both call `MockOrderStore.ensureDraft(modeloId)`), and
`EditorAccessoriesScreenModel`. All three survive sheet open/close and Editor↔Preview
navigation because Voyager keeps the screen instance (and its `rememberScreenModel`s) on the
back stack — no extra persistence plumbing was needed.

## Hotspot architecture & coordinate strategy (mandatory)

`EditorHotspot` stores normalized `(x, y)` in `0..1` against the **rendered image rect**, not
the outer stage box. `resolveFitRect(stageWidth, stageHeight, sourceWidth, sourceHeight)`
reproduces `ContentScale.Fit + Alignment.Center` letterboxing:
`scale = min(stageW/srcW, stageH/srcH)` → `renderedW/H = src * scale` → centered `left/top`
offsets. `sourceWidth/Height` come from `painterResource(...).intrinsicSize` at runtime (no
hardcoded aspect ratio); `stageWidth/Height` come from `BoxWithConstraints`. The rect is
recomputed (via `remember` keyed on both) whenever the stage size or image changes, then each
hotspot's dot center = `fitRect.left/top + normalized * fitRect.width/height`. Because the
math is ratio-based, it stays correct across window/orientation sizes and across different
photo aspect ratios without per-model tuning — only the dot *positions* (`EditorHotspotLayout`)
are a generic estimate for the current front-facing photo framing, flagged below as a risk.

## Sheet behavior

`EditorCustomizationSheet` is a fixed-height (`70%` normal / `85%` short-height, via
`SuitTheme.responsive.isShortHeight`) bottom sheet built from primitives (no Material3
`ModalBottomSheet`, consistent with the rest of the codebase), with a drag handle, category
label + close button, a horizontal category chip selector, and a scrollable body region
(`Modifier.weight(1f).verticalScroll(...)`). It is composed inside the stage's `content` Box
(after the CTA bar) together with a scrim, so it z-order-covers the CTA whenever a category is
active — no dependency on `SuitImmersiveScaffold`'s `fixedCta` slot was needed.

## State persistence

- Real domain fields (lapel, buttons, pocket, sleeves, lining, fit, color, fabric) persist via
  the existing `EditorPartesScreenModel`/`EditorCoresScreenModel` → `MockOrderStore`, unchanged
  from before this phase.
- Vest/Tie have no domain field, so `EditorAccessoriesScreenModel` holds them in memory only,
  scoped to the screen instance — persists across sheet toggling and Editor↔Preview navigation
  (same screen instance on the back stack) but resets if the user leaves the editor entirely.
  This is intentional and disclosed in-UI (`VestCategoryBody` copy).

## Real / controlled-overlay / state-only classification (Task 8)

| Category | Class | Notes |
|---|---|---|
| Lapel, Buttons, Pockets, Sleeves, Lining, Fit, Fabric | A — real | Existing `PartesFato`/`Tecido` fields, same as before; no new visuals invented for parts the photo can't actually show differently — only the sheet UI is new. |
| Color | B — controlled overlay | Floating `ColorBadge` (swatch chip + name) over the stage, not a full-photo tint — a whole-image tint would also recolor skin/shirt/background since no per-garment alpha mask exists. |
| Vest, Tie | C — state-only | No visual change on the stage photo; persisted only as UI state per the note above. |

## Responsive behavior

Sheet height and horizontal paddings come from `SuitTheme.responsive` (`isShortHeight`,
`screenHeight`, `horizontalContentPadding`), matching the pattern used by Phase 9.2/9.4
components. Hotspot placement is resolution-independent by construction (see coordinate
strategy above).

## Compile result

`.\gradlew.bat :composeApp:compileKotlinDesktop` → **BUILD SUCCESSFUL**. No other targets or
tests were run, per scope.

## Remaining risks / follow-ups (not addressed this phase)

- `EditorHotspotLayout` dot positions are an engineering estimate for the current photo
  framing; if future product photos frame the garment differently, positions need recalibration
  (no computer-vision detection was implemented, per scope).
- `TipoBolso`/`EstiloManga` label text lives as private extension functions in
  `Editor2DStageScreen.kt` rather than on the domain enums in `Models.kt` (out of scope this
  phase — flagged for a future small cleanup).
- Vest/Tie state resets on leaving the editor entirely (no backend field to persist it) —
  acceptable per Task 8 classification but worth revisiting if these become real product
  options.

PHASE 9.5A: PASS
