# Phase 9.5B — Preview 3D + Continuity Report

**Status: implementation complete, targeted compile deferred to Prompt 9c.**
Everything below describes the code as written this phase. No
`.\gradlew.bat :composeApp:compileKotlinDesktop` run has happened yet, so this is a
theoretical/self-review report, not a validated one — treat file/line references as
"as authored," not "as proven to build."

## Renderer reality (Task 1)

The pre-existing "Preview 3D" was not 3D: `Suit3DPreview.kt`/`Garment3DSilhouette` drew a
hand-authored, solid-color vector garment on a `Canvas` (`Path` shapes, front/profile swapped
by `|cos(rotationY)|`), completely disconnected from the real product photo and from the
selected color logic used everywhere else in the app. No GLB/glTF asset, no scene graph, no
Filament — there was nothing to preserve.

Decision: do not fabricate volumetric 3D (explicitly FUTURE ONLY per constraints — no back-view
asset exists to invent from). Instead, `Suit3DPreview.kt` was rewritten to apply a real,
honest 3D-space transform (`Modifier.graphicsLayer { rotationY; scaleX; scaleY; cameraDistance }`)
directly to the actual selected-suit photo (`painterResource(suitImageResource(imageKey))`,
same mapping as Editor). Rotation is clamped to `±Preview3DRotationLimit` (25°) — enough for a
believable side-parallax "look," not a full spin, because only one front-facing photo per model
exists. This is IMPLEMENTABLE NOW and strictly more honest than the previous fake silhouette:
real asset, real color, a genuine (if bounded) perspective transform, no invented geometry.

## Visual migration

`Preview3DScreen.kt` moved from `PremiumTopBar` + `LazyColumn` + plain bottom `Column` of
buttons to the immersive Archetype-C pattern established in 9.5A:
`SuitImmersiveScaffold(topBar = SuitImmersiveTopBar(...), content = { ... })`, with the CTA bar
(`PreviewCtaBar`, gradient scrim + `PrimaryGoldButton`/`SecondaryDarkButton`) placed inside the
`content` Box via `Modifier.align(Alignment.BottomCenter)` rather than the scaffold's
`fixedCta` slot — mirroring `EditorCtaBar`'s z-order approach. `SuitImmersiveTopBar` has no
`onCart` param, so a small `PreviewCartTrailing` (cart icon + gold badge) was added, mirroring
`EditorCartTrailing`. The stage (`PreviewHero`: `ShowroomBackground` Canvas ambiance +
`Suit3DPreview` + color swatch) is full-bleed with no standard max-width constraint; only the
controls/summary column below it is padded/scrollable.

## Selected-suit continuity (Task 3)

No new selected-model store or singleton was introduced. `Preview3DScreenModel` (unchanged
constructor shape from 9.5A, extended additively) still keys everything off `modeloId`:
- `MockOrderStore.ensureDraft(modeloId)` → `imagemKey = draft.modelo.urlImagemPrevia` (added
  this phase, since `DesignFato` carries no image key).
- `MockOrderStore.currentDesign(modeloId)` → all real domain fields (fabric, color, lapel,
  buttons, pockets, sleeves, lining, fit) for the configuration summary.
- Vest/Tie (Class C, no backend field) are threaded as plain constructor parameters:
  `Editor2DStageVoyagerScreen.onNext` reads `accessoriesState.vestIncluded`/`tieStyle` at the
  moment of navigation and passes them into `Preview3DVoyagerScreen(modeloId, colorHex,
  vestIncluded, tieStyle)` → `Preview3DScreenModel`. This is a one-way snapshot, not a shared
  mutable owner — no new global state.
- Voyager keeps `Editor2DStageVoyagerScreen`'s `rememberScreenModel`s alive on the back stack,
  so `onEditAgain = { navigator.pop() }` returns to the exact same Editor state (partes, cores,
  accessories) with nothing re-initialized — satisfies Task 7 (no draft restart, no `startDraft`
  call from Preview).

## Controls (Tasks 2/4)

`PreviewControls` renders up to 6 `Suit3DControlButton`s (reused unchanged from 9.5A):
Girar (`GirarClicado`, cycles `Preview3DRotationPresets = [0f, 22f, -22f]` via nearest-`abs()`
match), Zoom+ / Zoom− (`ZoomInClicado`/`ZoomOutClicado`, `coerceIn(Preview3DScaleMin,
Preview3DScaleMax)`), Repor (`ResetClicado`, resets `Preview3DState()`; uses a plain "↺" glyph
in a `Text` since no dedicated reset icon exists in `SuitIcons.kt` — avoided adding one for a
single use), Luz (`AlternarLuz`, toggles the real `showLight` radial-gradient overlay in
`Suit3DPreview`), Fundo (`AlternarFundo`, toggles `backgroundDark`). Drag-to-tilt and
pinch-to-zoom on the stage itself (`detectDragGestures`/`detectTransformGestures` in
`Suit3DPreview`) remain live alongside the discrete buttons, both clamped to the same bounds —
no control implies a capability (e.g. full spin, physically-based lighting) that isn't real.

## Fallback

There is no separate "no-render" fallback path: the stage always renders the real
`painterResource(suitImageResource(imageKey))` photo, same as Editor — if `imageKey` were ever
empty, `suitImageResource` resolves to its existing default-image behavior, so there is no
crash and no empty black box. Nothing is labeled as physically accurate 3D geometry anywhere in
copy or code comments.

## Configuration summary (Task 6)

`ConfigurationSummarySection` wraps the existing, unmodified `CustomizationSummaryCard`
(`EditorComponents.kt`) rather than editing that shared component. It shows only fields
actually present in `Preview3DUiState.configurationDetails`, built in
`Preview3DScreenModel.init` via `buildList { ... }`: Tecido, Cor, Lapela, Botões, Bolsos
(`design.partes.bolso.label()`), Mangas (`design.partes.mangas.label()`), Forro, Caimento
(`fitLabel(...)`), Colete (`"Com colete"`/`"Sem colete"`), and Gravata only
`if (tieStyle != TieStyle.None)`. `TipoBolso.label()`/`EstiloManga.label()`/`fitLabel()` were
changed from `private` to `internal` in `Editor2DStageScreen.kt` (same package) to be reused
here without duplication. On compact layouts (`responsive.isCompactLayout`) the section starts
collapsed (model name + total only) with a "Ver detalhes"/"Ocultar detalhes" text toggle; on
larger layouts it starts expanded. It never exceeds the space left below the stage
(`Modifier.weight(1f).verticalScroll(...)` column), so it can't crowd out the preview.

## Forward action (Task 8 continuity)

`onOrder` calls the existing `screenModel.adicionarAoCarrinho()` (unchanged business logic,
still writing through to `MockOrderStore`) and then navigates to the existing
`CartVoyagerScreen()` — no new Cart UI, no faked success state, same as before this phase.

## Responsive behavior (Task 9)

`stageHeightFraction` (fraction of `responsive.screenHeight` given to `PreviewHero`) varies by
class: `0.36` when `isShortHeight`, `0.50` when `isWideLayout` or `heightClass == TALL`, `0.44`
otherwise — narrow/short prioritizes the stage by keeping controls/summary compact
(`verticalArrangement` spacing drops from 20.dp to 14.dp, padding from 18.dp to 12.dp when
short); wide layouts additionally center `PreviewControls` under a `widthIn(max = 420.dp)`
constraint instead of stretching it edge-to-edge. Summary default expand state is driven by
`responsive.isCompactLayout` as described above.

## Files changed this phase

**Rewritten in full**
- `ui/screens/editor/Suit3DPreview.kt` — real-photo `graphicsLayer` tilt/zoom, replacing the
  fake vector silhouette; dropped the now-unnecessary `garmentColor` param.
- `ui/screens/editor/Preview3DScreen.kt` — immersive scaffold, `PreviewHero`, `PreviewControls`
  (6 buttons), `ConfigurationSummarySection` (collapsible), `PreviewCtaBar`, `PreviewCartTrailing`,
  `PreviewColorSwatch`; `ShowroomBackground` Canvas ambiance kept verbatim.

**Modified, additive only**
- `ui/screens/editor/EditorScreenModels.kt` — `Preview3DRotationLimit`/`Presets`,
  `Preview3DScaleMin`/`Max`/`ZoomStep` constants; `imagemKey` on `Preview3DUiState`;
  `ZoomInClicado`/`ZoomOutClicado`/`ResetClicado` events; `Preview3DScreenModel` constructor
  gains `vestIncluded`/`tieStyle`; `init` now also reads `MockOrderStore.ensureDraft(modeloId)`
  and builds the full `detalhesConfiguracao` list.
- `ui/screens/editor/Editor2DStageScreen.kt` — `fitLabel`/`TipoBolso.label()`/
  `EstiloManga.label()` changed from `private` to `internal` for cross-file reuse (no logic
  changes).
- `ui/navigation/EditorScreens.kt` — `Editor2DStageVoyagerScreen.onNext` now passes
  `vestIncluded`/`tieStyle` into `Preview3DVoyagerScreen`; that class's constructor and
  `Preview3DScreenModel` instantiation updated to match; wired `onZoomIn`/`onZoomOut`/`onReset`.

No backend/repository/API files touched. No new ScreenModel added (only additive changes to
the existing `Preview3DScreenModel`). Nothing committed.

## Compile result

**Not run this phase** — deferred by explicit user instruction to Prompt 9c. The five touched
files (`Suit3DPreview.kt`, `Preview3DScreen.kt`, `EditorScreenModels.kt`,
`Editor2DStageScreen.kt`, `EditorScreens.kt`) have not been verified to compile together yet.
Likely-safe areas (unchanged import surfaces, same package for the `internal` visibility
changes) are lower risk; the areas most worth checking first in 9c are: the new
`Preview3DScreenModel` constructor call sites (only one call site, in `EditorScreens.kt`,
already updated), and icon/composable references added to `Preview3DScreen.kt`'s import list.

## Remaining 3D debt (documented, not implemented — FUTURE ONLY)

- No real volumetric 3D exists or was added. A genuine upgrade path would require: clean
  per-model product photography or a modeling pass → production-ready GLB/glTF assets → a real
  mobile 3D renderer (e.g. Filament or a KMP-compatible equivalent) wired into Compose via an
  interop surface. None of that exists in this codebase today.
- The old auto-vectorized SVG/Canvas silhouette approach is explicitly rejected as a direction
  to resurrect or extend — it was disconnected from the real product and is strictly inferior
  to the real-photo-plus-perspective approach shipped this phase.
- Back/side views remain impossible without new photography or 3D assets; the current
  ±25° tilt is a deliberately bounded parallax illusion, not a claim of full rotation.

PHASE 9.5B: PASS (implementation-complete; compile validation explicitly deferred to Prompt 9c per user instruction)
