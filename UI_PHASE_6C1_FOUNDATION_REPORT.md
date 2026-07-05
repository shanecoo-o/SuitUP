# Phase 6C.1 — Production Design Foundation Report

Branch: `ui/figma-refactor` · Scope: theme/token consolidation + responsive engine only. No screens migrated, no backend/business logic touched.

## 1. Precondition

```
git status                 -> clean, nothing to commit
git branch --show-current  -> ui/figma-refactor
git log --oneline -15      -> includes Prompt 27 validation closure (9cbb443),
                               payment proof regression tests (b84ca37),
                               mobile admin/payments backend integration (b4089b5)
```
HEAD at start: `9cbb443 docs(validation): close prompt 27 lifecycle validation`. Precondition satisfied — proceeded.

## 2. Files inspected

- `ui/theme/Color.kt`, `Theme.kt`, `Typography.kt`, `Shape.kt`, `Spacing.kt`, `Motion.kt` (all pre-existing, read in full)
- `ui/components/*` (34 `Suit*` components) — consumption pattern of `SuitTheme.colors/spacing/shapes/motion`
- `ui/icons/SuitIcons.kt` — custom Canvas icon set, 22.dp default stroke-based icons
- `App.kt` — root composition wrapper
- `composeApp/build.gradle.kts` — confirmed `compose.components.resources` already present, no font files yet
- Design handoff: `UI_MIGRATION_AUDIT.md`, `UI_IMPLEMENTATION_PLAN.md`, `UI_SCREEN_MAPPING.md`, `UI_COMPONENT_INVENTORY.md`, and selectively extracted `stitch.zip` → Prompt 9 `DESIGN.md` + `suitup_implementation_blueprint.md`, Prompt 7 responsive/grid docs (temp-extracted read-only, deleted after reading; nothing written to the repo or the original zip)

## 3. Old foundation — KEEP / MERGE / REPLACE / DEPRECATE

| File | Verdict | Notes |
|---|---|---|
| Color.kt | **MERGE** | Solid, dark-first, already close to Stitch. Added new semantic tokens additively; existing aliases untouched. |
| Theme.kt | **MERGE** | Wrapper pattern kept; added 2 new CompositionLocals (elevation, icon sizes). |
| Typography.kt | **MERGE** | Existing 17-style scale kept as-is (still consumed live); added Stitch-named semantic roles as new fields, no renames. |
| Shape.kt | **MERGE** | Added one new radius tier (24dp); existing semantic shapes (`card`, `button`, etc.) untouched. |
| Spacing.kt | **MERGE** | Added 48dp + 128dp bottom-clearance tokens; existing scale untouched. |
| Motion.kt | **MERGE** | Added semantic use-case aliases over existing durations; no new values invented. |
| — (none existed) | **NEW** | Elevation.kt, IconSizes.kt, Responsive.kt created — genuinely missing pieces. |

No competing `OldTheme`/`NewStitchTheme` was created — one `SuitTheme` remains authoritative.

## 4. Final token architecture

**Colors** (`Color.kt`) — additive semantic set: `Background, SurfaceRaised, SurfaceInteractive, SurfaceSelected, Border, BorderStrong, TextPrimary, TextSecondary, TextMuted, AccentGold, AccentGoldPressed, AccentGoldDisabled, Scrim`, plus existing `Surface, Success, Warning, Error, Info, Overlay`. `SuitColorTokens` extended with matching fields. `SuitDarkColorScheme.outline/outlineVariant` retuned to `Border`/`BorderStrong` (previously both aliased `Mist`).

Documented discrepancies resolved (not invented silently):
- **Gold**: Stitch's two sources disagree (`blueprint.md` #F2CA50 vs `DESIGN.md`'s own rendered HTML `#D4AF37`). Kept existing `GoldPrimary #D4AF37` as `AccentGold` — it's already validated in production and matches DESIGN.md's actual output, avoiding an app-wide re-color on an unresolved spec conflict.
- **Error**: `blueprint.md` (#B3261E) and `DESIGN.md`'s M3 ramp (#FFB4AB / #93000A container) disagree and don't describe the same tone. Kept existing `Error #E74C3C` rather than picking one arbitrarily.
- **Success/Warning/Info**: not defined anywhere in the Stitch handoff — kept existing values.
- **AccentGoldDisabled**: no disabled-gold value specified anywhere; derived via alpha (0.38) rather than invented as a literal hex.
- **Scrim**: no scrim value specified; used Material-default 50% black, kept distinct from the existing 80%-black `Overlay` already used for modals.

**Typography** (`Typography.kt`) — added `display, pageTitle, sectionTitle, cardTitle, body, meta, status` as new fields sourced from Prompt 9 `DESIGN.md`'s concrete type scale (the only doc with sizes; `blueprint.md` only names the two font families). Existing `displayLarge/headlineLarge/...` families kept untouched and still used by all live screens — no visual change to any currently shipped screen.

**Spacing** (`Spacing.kt`) — added `xxxxl (48dp)` and `bottomScrollClearance (128dp)`, reconciling the three overlapping Stitch spacing tables (`blueprint.md` Space-tokens, `DESIGN.md` rem-based, Prompt 7's tier system) against the existing scale.

**Shape** (`Shape.kt`) — added `xxl (24dp)`, matching DESIGN.md's `xl`/1.5rem tier called out for Primary Cards. Not applied to the existing `card` shape yet (10dp) — that's a per-screen visual decision deferred to the screen-migration phases per this phase's explicit scope limit.

**Elevation** (new `Elevation.kt`) — `SuitElevationLevel{NONE,LOW,MEDIUM,HIGH}` mapped onto DESIGN.md's tonal "Level 0/1/2" surfaces (`Background → Surface → SurfaceRaised → SurfaceSelected+BorderStrong`), no drop shadows.

**Icon sizes** (new `IconSizes.kt`) — `small(16) / standard(22) / large(28) / feature(40) / navigation(24)`. Not specified anywhere in the Stitch handoff (confirmed gap) — `standard` matches the current live default so this introduces no behavior change.

## 5. Responsive architecture

New `Responsive.kt`, commonMain, built on `BoxWithConstraints` (no Android-only APIs). `SuitResponsiveRoot` wraps the Navigator in `App.kt` (the one non-token file touched, purely to make real measured space available app-wide — no screen visuals changed).

**Width classes** (Prompt 9 `blueprint.md`, chosen as authoritative per its "final engineering handoff" status; Prompt 7's earlier, conflicting breakpoint table is superseded):
- NARROW 320–359dp → 16dp padding
- STANDARD 360–399dp → 20dp padding
- MEDIUM 400–479dp → 24dp padding
- WIDE 480–599dp → 24dp padding + 440dp max-width, centered

**Height classes**: **not defined anywhere in the Stitch handoff** (confirmed absent from DESIGN.md, blueprint.md, and both Prompt 7 responsive docs). Implemented as an engineering judgment call, flagged as such in code comments: SHORT < 640dp, STANDARD 640–760dp, TALL > 760dp.

**Adaptive content width**: `SuitContentWidth.screen(440dp) / form(400dp) / sheet(480dp)`.

**Grid policy**: `SuitGridPolicy.productColumns()` / `.metricColumns()` — simple per-width-class lookups per the task's own examples, not a generic grid engine.

**Phone shape**: `SuitPhoneShape{TALL_NARROW, SHORT_WIDE, STANDARD, LARGE_WIDE}` derived from width+height together.

## 6. Window insets (audit only — Task 16)

No `Scaffold`, no `enableEdgeToEdge`, no `WindowInsets`/`statusBars()`/`navigationBars()` usage found anywhere in the codebase. The app currently does not do edge-to-edge layout or explicit inset handling. **This is a real gap**, not something fixed in this phase (it's screen/Activity-level, not a token). Flagged as a risk for the "Scaffolds/Nav" phase (Phase III per `UI_IMPLEMENTATION_PLAN.md`) — that phase should introduce a shared safe-area helper before any bottom-nav/fixed-CTA/immersive-editor screens are built, to avoid content sitting under system bars.

## 7. Hardcode audit (Task 19, summary)

Most frequent raw values across `ui/components/*` (not migrated in this phase): `1.dp` borders (24 occurrences), `8.dp`/`4.dp` gaps (~15 each), `12.dp`/`14.dp` padding, button heights `36/44/52dp` (already matching `SuitButton` size variants but re-declared per-component instead of centralized). Colors are already fully centralized in `Color.kt` — no raw hex found outside it. Future phases should thread `SuitTheme.spacing`/`SuitTheme.iconSizes` through these repeated literals when each component is actually restyled, not in bulk now.

## 8. Files created

- `ui/theme/Elevation.kt`
- `ui/theme/IconSizes.kt`
- `ui/theme/Responsive.kt`

## 9. Files modified

- `ui/theme/Color.kt` — additive semantic tokens + `SuitColorTokens` fields + outline/outlineVariant retune
- `ui/theme/Typography.kt` — additive semantic role fields
- `ui/theme/Shape.kt` — additive `xxl` tier
- `ui/theme/Spacing.kt` — additive `xxxxl` + `bottomScrollClearance`
- `ui/theme/Motion.kt` — additive semantic duration aliases
- `ui/theme/Theme.kt` — wired 2 new CompositionLocals + `SuitTheme.elevation/.iconSizes/.responsive` accessors
- `App.kt` — wrapped Navigator in `SuitResponsiveRoot` (no screen/business logic changed)

## 10. Business logic changes

None. No repository, ScreenModel, DTO, navigation flow, payment/order/admin/upload logic touched. No Gradle dependencies added.

## 11. Build result

- `:composeApp:assembleDebug` → **BUILD SUCCESSFUL**
- `:composeApp:compileKotlinDesktop` and `:composeApp:compileKotlinMetadata` (commonMain) → **BUILD SUCCESSFUL**, confirming the new foundation code is fully KMP-safe (no Android-only types)
- iOS targets remain disabled on this machine (no Mac toolchain) — unchanged from before this phase, not caused by these changes

## 12. Test result

`./gradlew test` → **BUILD SUCCESSFUL** (no existing unit tests exercise UI theme code; no regressions).

## 13. Remaining risks

1. **Height-class thresholds are unsourced** — no design document defines them; revisit if a later Stitch pass supplies concrete numbers.
2. **Gold/Error color ambiguity is deferred, not resolved** — both Stitch source files internally disagree; a design decision is still needed before any screen deliberately re-themes around a specific accent-gold or error hex.
3. **Window insets / edge-to-edge is unimplemented** — flagged for the Scaffold/Nav phase; building bottom-nav or fixed-CTA screens before that lands risks content clipping under system bars.
4. **`card`/`button` shape values were not moved to Stitch's larger radii** (e.g. 24dp cards) — deliberately deferred to screen-migration phases to avoid an app-wide visual change without per-screen validation.
5. **No font files added** — Typography still uses `FontFamily.Serif`/`SansSerif` system fallbacks; Playfair Display/Inter `.ttf` integration is a separate, explicitly out-of-scope task requiring a Gradle/resource decision.

## 14. Next phase readiness

Foundation now exposes: `SuitTheme.colors` (extended), `.typography` roles via `SuitTextStyles`, `.spacing`, `.shapes`, `.motion`, `.elevation`, `.iconSizes`, and `.responsive` (via `SuitResponsiveRoot`, already wired at the app root). Subsequent screen-migration phases can consume `SuitTheme.responsive.widthClass/heightClass/horizontalContentPadding/maxContentWidth` directly instead of ad hoc width math, and adopt the new semantic tokens per-component as each is actually restyled.

---

**PHASE 6C.1: PASS**
