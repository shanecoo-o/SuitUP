# Phase 9.5 Checkpoint — Validation + Surgical Fixing

Validation checkpoint for the accumulated, uncommitted UI-migration work from Phases 9.1
through 9.5B. Not an implementation phase — compile, fix only proven defects, build, test,
verify, report.

## 1. Changed-file scope

`git status` / `git diff --stat` / `git diff --name-only` confirm all tracked changes and
untracked additions belong to the UI migration: `App.kt`, theme (`Color`/`Motion`/`Shape`/
`Spacing`/`Theme`/`Typography`/`Responsive`/`Elevation`/`IconSizes`/`SuitBottomClearance`),
shared components (`Suit*`, `Domain*`, `Commerce*`, `Premium*`, `Editor*`, `Admin*`), navigation
(`EditorScreens`/`MainShellScreen`/`NavLocals`/`TabRootScreens`/`ProductDetailScreens`), catalog/
home/editor/preview screens, `SuitImageResources.kt`, the 9 suit product photos (4 old ones
removed, 5 new ones added), `MockCatalogStore.kt`, and the six `UI_PHASE_*` reports. No backend,
repository, payments, admin-business-logic, or auth files are touched. Scope check: **PASS**.

## 2. `.\gradlew.bat :composeApp:assembleDebug`

**BUILD SUCCESSFUL** (43 actionable tasks, 10 executed / 33 up-to-date). Full Android debug APK
assembles clean, including `compileDebugKotlinAndroid`, dexing, and packaging.

## 3. `.\gradlew.bat test`

**BUILD SUCCESSFUL**. `testDebugUnitTest`/`testReleaseUnitTest` report `NO-SOURCE` — there are no
JVM unit test sources in this module, so this is a clean no-op pass, not a masked failure.

## 4. `.\gradlew.bat :composeApp:compileKotlinDesktop`

Initial run **FAILED** with 7 errors, all in one file — see Section 5. After the fix, re-run:
**BUILD SUCCESSFUL**. Confirms shared `commonMain` UI stays KMP-safe (desktop target compiles
independently of Android).

## 5. Fixes applied (surgical, compile-proven only)

**Fix 1 — wrong import package for `graphicsLayer` (`Suit3DPreview.kt`)**
`Preview3DScreen`'s renderer rewrite (Phase 9.5B) imported
`androidx.compose.ui.draw.graphicsLayer`, but in Compose Multiplatform 1.7.0 the `graphicsLayer`
Modifier extension (both the lambda-based and parameter-based overloads) lives in
`androidx.compose.ui.graphics`, not `androidx.compose.ui.draw` (verified by inspecting
`GraphicsLayerModifierKt` inside the `ui-desktop-1.7.0.jar`). This produced 7 "Unresolved
reference" errors (`graphicsLayer`, `rotationY`, `scaleX`, `scaleY`, `cameraDistance`, `density`)
all cascading from the single bad import. Fixed by importing
`androidx.compose.ui.graphics.graphicsLayer` instead. No logic changed — this was purely an
import-path defect from item #6 on the known-risk checklist ("graphicsLayer parameter usage").

**Fix 2 — CTA bar / scroll clearance under-sized (`Preview3DScreen.kt`)**
Found during the responsive check (item: "bottom CTA overlap"), not compilation: `PreviewCtaBar`
stacks two `SuitButton`s (44dp default Medium height each) + 8dp spacing + 32dp vertical padding
≈ 128dp total height, floated via `Modifier.align(Alignment.BottomCenter)` over the scrollable
content column. That column only reserved a 96dp `Spacer` before the CTA, leaving ~32dp where
the disclaimer card could sit behind the CTA bar when scrolled to the bottom. Bumped the spacer
from `96.dp` to `140.dp` to give safe clearance. Recompiled desktop target after the change —
still **BUILD SUCCESSFUL**. No other responsive risks from the checklist (narrow overflow, fixed
hotspot dp coordinates, standard max-width on the immersive stage, nested scroll mistakes) were
found — the stage uses `fillMaxWidth()` + computed `height()` with no max-width cap, and there is
exactly one `verticalScroll` in the tree (no nested scroll containers).

No other files required changes. `EditorScreenModels.kt`, `Editor2DStageScreen.kt`, and
`EditorScreens.kt` (the other three files flagged as compile-risk in the phase prompt) compiled
without any issues — their constructor signatures, `internal` visibility changes, and the
`TieStyle` threading were already consistent.

## 6. Customer-to-preview flow (static verification)

`Product Detail(modeloId)` → `Editor2DStageVoyagerScreen(model.id)` → `Preview3DVoyagerScreen`
→ `Cart`:
- `ProductDetailScreens.kt:31` pushes `Editor2DStageVoyagerScreen(model.id)` — single, unchanged
  Product Detail screen (`grep` for a second Product Detail definition: none found).
- `EditorScreens.kt`'s `Editor2DStageVoyagerScreen.onNext` pushes
  `Preview3DVoyagerScreen(modeloId, colorHex, vestIncluded, tieStyle)` — same `modeloId` used to
  build `EditorPartesScreenModel`/`EditorCoresScreenModel` is threaded straight through.
- `Preview3DScreenModel.init` reads `MockOrderStore.ensureDraft(modeloId)` (image key) and
  `MockOrderStore.currentDesign(modeloId)` (domain fields) off that same `modeloId` — no new
  selected-model store, confirmed by grep (only one `fun suitImageResource` mapping function in
  the codebase, in `SuitImageResources.kt`).
- Both Product Detail and Preview push the same `CartVoyagerScreen()` (only one definition, in
  `TabRootScreens.kt`) — no duplicate navigation system.
- `grep` for `Garment3DSilhouette` and for the deleted `Editor2DPartsScreen`/
  `Editor2DColorsScreen`/`*VoyagerScreen` names across `commonMain`: zero references remain.

Flow check: **PASS**.

## 7. Selected-model continuity

Confirmed via code, not just prompt claim: `imagemKey` in `Preview3DUiState` is sourced from
`draft.modelo.urlImagemPrevia` (the same draft object Editor uses), and `garmentColor`/
`configurationDetails` are sourced from `MockOrderStore.currentDesign(modeloId)` — the same
backing store Editor writes to. Vest/Tie remain a one-way constructor snapshot from
`EditorAccessoriesScreenModel` state at navigation time (no backend field, no new global state,
as designed in 9.5B). **PASS**.

## 8. Editor state continuity (back navigation)

`Editor2DStageVoyagerScreen` instantiates `EditorPartesScreenModel`/`EditorCoresScreenModel`/
`EditorAccessoriesScreenModel` via `rememberScreenModel`, and `Preview3DScreen`'s `onEditAgain`
is wired to `navigator.pop()` — Voyager keeps the Editor screen instance (and its ScreenModels)
alive on the back stack, so no draft restart and no `startDraft` call from Preview. **PASS**.

## 9. Responsive risks

- Immersive stage: no standard content max-width applied (full-bleed `fillMaxWidth()`) — correct.
- CTA/scroll overlap: found and fixed (Section 5, Fix 2).
- No nested scroll containers, no hardcoded hotspot coordinates in Preview (hotspots are an
  Editor-only concept, unaffected by this phase).
- Not independently re-verified in a running emulator/device this session (out of scope for a
  compile-and-static checkpoint) — flagged below as a residual risk.

## 10. Remaining blockers / residual risks

- No blockers to compile, build, or test. Nothing prevents proceeding to Phase 9.6.
- Not yet visually verified on a running device/emulator or desktop window — the CTA-clearance
  fix (140dp) is a calculated correction based on component heights, not an observed screenshot;
  worth a quick visual sanity check in Phase 9.6 or before shipping.
- `EditorHotspotLayout` dot-position estimate and Vest/Tie in-memory-only reset-on-exit behavior
  (both already disclosed as accepted risks in `UI_PHASE_9_5A_EDITOR_REPORT.md`) remain
  unchanged and out of scope here.
- 3D renderer debt (real GLB/glTF + mobile renderer) remains FUTURE ONLY, documented in
  `UI_PHASE_9_5B_PREVIEW_REPORT.md`; unaffected by this checkpoint.

## Readiness for Phase 9.6

All required gates (`compileKotlinDesktop`, `assembleDebug`, `test`, repeat
`compileKotlinDesktop`) pass on the current worktree with two surgical fixes applied (one true
compile defect, one proven layout-clearance defect). Selected-model and Editor-state continuity
are confirmed by direct code inspection, not assumption. The worktree is ready to proceed to
Phase 9.6. Nothing was committed.

PHASE 9.5 CHECKPOINT: PASS
