# UI Phase 9.3 — Shared Scaffolds, Safe Areas, Window Insets, Top Bars, Bottom Navigation Report

## 1. Precondition

- Repository: `D:\codes\AndroidStudioProjects\SuitUP`
- Branch: `ui/figma-refactor`
- HEAD at start/end of this phase (no commit made): `9cbb443e18b9856b1be26230c08d31114e32265f`
- Working tree already carried uncommitted Phase 9.1/9.2 changes (theme tokens, shared components) — confirmed present and untouched in kind, only extended by this phase's additions.
- No backend/data/repository/session/ScreenModel/API DTO/Gradle files touched.

## 2. Current Navigation Audit

| Current Structure | File | Current Users | Current Problems | Target Shared Structure | Action |
|---|---|---|---|---|---|
| Root composition | `App.kt` | Whole app | No inset/edge-to-edge handling at all; no Scaffold; direct `Navigator(SplashVoyagerScreen())` | Confirmed non-edge-to-edge baseline stands; root left alone (see Task 3/4) | Audited only, not modified |
| Customer shell | `MainShellScreen.kt` | Post-login customer session | Manual `Column { Box(weight) ; SuitBottomNav }`; no scaffold abstraction, no top bar slot | `SuitPrimaryDestinationScaffold` | Rewired to use the new scaffold (structurally identical, zero Tab/content changes) |
| Tab architecture | `Tabs.kt` | `HomeTab`/`ModelsTab`/`OrdersTab`/`ProfileTab` | None — sound Voyager `Tab` + own `Navigator` pattern | Preserved as-is | Not modified |
| Auth flow | `AuthFlow.kt` | Splash/Onboarding/Login/Register | Screens push directly, no shared chrome; fine for auth (no nav/top bar needed) | N/A this phase | Audited only, not modified |
| Checkout flow | `CheckoutFlow.kt` | Checkout/Measurements/Delivery/Address/Payment/Confirmation | Each screen composable builds its own header inline (not via scaffold); out of scope to touch (feature screens) | `SuitFormFlowScaffold` (future adoption) | Audited only, not modified |
| Orders screens | `OrdersScreens.kt` | Orders list / Track order | Uses `PremiumTopBar` (existing dark wrapper around `SuitTopBar`) directly, no scaffold | `SuitListScaffold` / `SuitDetailScaffold` (future adoption) | Audited only, not modified |
| Tab-root screens | `TabRootScreens.kt` | Home/Cart/SelectModel/Profile | Plain composables, no scaffold | `SuitPrimaryDestinationScaffold` / `SuitListScaffold` (future adoption) | Audited only, not modified |
| Admin navigation shell | `AdminScreens.kt` | 6 individual push-based admin `Screen`s | No shared shell/TabNavigator/bottom nav at all; each screen independent | `AdminBottomNav` built as standalone infrastructure (not wired — see §18 risks) | Audited only, not modified |
| Customer bottom nav | `SuitBottomNav.kt` | `MainShellScreen.kt` | Self-contained `NavItem`, no density/translucency, no shared engine with any future Admin nav | Shared `SuitNavItem`/`SuitNavBar` engine + thin `SuitBottomNav` wrapper | Rewritten (backward compatible) |
| Admin bottom nav | *(did not exist)* | — | — | `AdminBottomNav.kt` on the same `SuitNavBar` engine | Created |
| Top bar | `SuitTopBar.kt` | 17 feature screens directly, plus `PremiumTopBar` wrapper | Single flexible engine already exists; no semantic variants for Primary/Detail/Immersive/Admin | 4 thin wrappers in `SuitTopBars.kt` delegating to unmodified `SuitTopBar` | `SuitTopBar.kt` untouched; `SuitTopBars.kt` created |
| Scaffold usage | *(none — plain Column/Box everywhere)* | — | No reusable scaffold archetypes at all; every screen improvises `Column`/`Box`/`weight(1f)` | `SuitScaffolds.kt` — 5 archetypes | Created |
| System bar/inset handling | `MainActivity.kt`, `themes.xml` | Android target | Confirmed **non-edge-to-edge**: no `enableEdgeToEdge()`/`WindowCompat.setDecorFitsSystemWindows(false)`; `themes.xml` sets transparent bar colors but does not override `decorFitsSystemWindows` (defaults `true`) | OS already reserves system-bar space; no parallel `WindowInsets` layer needed | Audited only, not modified |

## 3. Scaffold Architecture

Five small, single-purpose scaffolds in `ui/components/SuitScaffolds.kt` (no mega-scaffold, no boolean-soup):

| Scaffold | Slots | Archetype use |
|---|---|---|
| `SuitPrimaryDestinationScaffold` | `topBar?`, `bottomNav` (required), `content` | Home / Catalog / Orders / Profile tab roots |
| `SuitDetailScaffold` | `topBar?`, `fixedCta?`, `content` | Product/Order/Payment Detail |
| `SuitImmersiveScaffold` | `topBar?` (floats), `fixedCta?` (floats), `content` (full-bleed) | Editor 2D, Preview 3D |
| `SuitFormFlowScaffold` | `topBar?`, `fixedCta?`, `content`, `.imePadding()` | Register, Address, Checkout steps |
| `SuitListScaffold` | `topBar?`, `content` | Standalone pushed list/browse screens |

Each is a plain `Column`/`Box` composition (matching the existing `MainShellScreen` pattern) — none wrap Material3 `Scaffold`, avoiding a second automatic-inset-consuming layer. `SuitFixedCtaBar` is the one additional generic container (background + responsive padding) that a scaffold's `fixedCta` slot hosts; it holds caller-supplied buttons, it is not a button component itself.

## 4. Inset Ownership Model

Documented in full as the header doc-comment of `SuitScaffolds.kt`. Summary:

- **Root/Activity**: confirmed non-edge-to-edge — the OS reserves status/navigation bar space automatically, before Compose measures anything. Verified via `MainActivity.kt` (no `enableEdgeToEdge()`/`WindowCompat` call) and `themes.xml` (`decorFitsSystemWindows` not overridden, defaults `true`).
- **Decision**: given that baseline, no real `WindowInsets.systemBars`/`.navigationBars` padding is introduced anywhere this phase. Adding one now would double-pad on top of space the OS already reserves — the exact stacking bug the phase brief warns against. Revisit only if/when the app moves to edge-to-edge.
- **Exception**: `Modifier.imePadding()` in `SuitFormFlowScaffold` — a portable, keyboard-height-only inset that never overlaps system-bar space, so it cannot stack with the OS's own reservation.
- **Scaffold vs. content split**: a scaffold owns and draws edge-docked chrome (top bar / bottom nav / fixed CTA). Content owns only the middle region and, if it scrolls, reserves clearance at its own trailing edge via the matching `SuitBottomClearance` constant — content never draws its own bottom bar; a scaffold never pads content on its behalf.

## 5. Safe-Area Strategy

Same as §4 — one strategy, not two. No platform-specific safe-area code was added in `androidMain`/`iosMain`/`desktopMain`; the strategy is "don't fight the OS's existing reservation, and only add the one inset (`imePadding`) that is provably additive." This keeps the strategy commonMain-only.

## 6. Customer Navigation

`ui/components/SuitBottomNav.kt` was rewritten around a shared rendering engine:

- `SuitNavItem` — one renderable destination (`label`, `selected`, `onClick`, `icon: @Composable (tint) -> Unit`).
- `SuitNavBar(items, density, translucent)` — the actual bar renderer (divider, row, item layout, tint rule).
- `SuitBottomNav(selected, onSelect, density = Expanded, translucent = false)` — thin wrapper building `SuitNavItem`s from `SuitTab.entries` (`Início`/`Modelos`/`Pedidos`/`Perfil`, unchanged labels/icons) and delegating to `SuitNavBar`.

The existing call site (`MainShellScreen.kt`, `SuitBottomNav(selected = ..., onSelect = ...)`) compiles unchanged because the new params default. `AdminBottomNav.kt` reuses the identical `SuitNavBar` engine with a distinct `AdminTab` enum, so customer and admin nav are visually related without shared destinations or duplicated layout code (Task 15).

## 7. Customer Compact Behavior

`ui/components/SuitNavScrollBehavior.kt` provides `rememberSuitNavDensity(...)`, two overloads (`LazyListState`, `ScrollState`), both built on `snapshotFlow` watching scroll position/offset:

- Scrolling further down → `SuitNavDensity.Compact` (bar shrinks to 52dp, label dropped, icon-only).
- Scrolling back up → `SuitNavDensity.Expanded` (64dp, or 56dp on `isShortHeight` devices, icon+label).

Entirely opt-in: a screen passes the returned density into `SuitBottomNav(density = ...)`; screens that never call it keep the static expanded bar. No screen was wired to call it yet (that belongs to the actual Home/Catalog/Orders screen migrations, out of scope this phase) — the controller exists and compiles, ready for those phases to adopt.

## 8. Admin Navigation

`ui/components/AdminBottomNav.kt`: `AdminTab` enum (`Visão Geral`/`Gestão`/`Operações`/`Actividade`) + `AdminBottomNav` composable, built on the same `SuitNavBar` engine as the customer nav. Icons reused with zero new icons: `HomeIcon`/`ShirtIcon`/`BagIcon`/`BellIcon`.

**Deliberately not wired into a real Admin shell this phase.** `AdminScreens.kt` currently has six independent push-based `Screen`s (`AdminDashboardVoyagerScreen`, `AdminOrdersVoyagerScreen`, `AdminPaymentsVoyagerScreen`, `AdminOrderDetailsVoyagerScreen`, `AdminCatalogVoyagerScreen`, `AdminSuitFormVoyagerScreen`) with no `TabNavigator`. Wiring `AdminBottomNav` into a real shell would require either inventing screen content for "Actividade" (no backing screen exists) or regrouping the existing Catalog/Orders/Payments screens into "Gestão"/"Operações" — both are feature-screen restructures forbidden this phase (Task 25). This is flagged transparently in §18 rather than silently wired in an inconsistent way.

## 9. Top Bar System

`ui/components/SuitTopBars.kt` — four thin semantic wrappers, all delegating to the unmodified `SuitTopBar` engine (mirroring the existing `PremiumTopBar` precedent already in the codebase):

- `SuitPrimaryTopBar` — transparent, no back chevron (top-level tab destinations).
- `SuitDetailTopBar` — always has `onBack`; light by default, `dark` overridable (e.g. order tracking's Charcoal style).
- `SuitImmersiveTopBar` — dark by default (reads over `WarmBlack`/`EditorStage` stages), `trailing` slot for stage controls.
- `SuitAdminTopBar` — dark/Charcoal, no cart slot.

Purely additive: all 17 existing direct `SuitTopBar(...)` call sites, and the one `PremiumTopBar` call site, remain valid and untouched.

## 10. Fixed CTA Strategy

`SuitFixedCtaBar(background, content)` in `SuitScaffolds.kt` — a generic docked container (fill width, background, responsive horizontal padding + 12dp vertical padding) that a `SuitDetailScaffold`/`SuitImmersiveScaffold`/`SuitFormFlowScaffold`'s `fixedCta` slot hosts. It holds whatever `SuitButton`(s) a screen supplies; it is structural only, no business-specific CTA copy or logic.

## 11. Bottom-Clearance Policy

`ui/theme/SuitBottomClearance.kt` (created earlier this phase) replaces the single Phase 9.1 `bottomScrollClearance = 128dp` with explicit named policies:

| Constant | Value | Situation |
|---|---|---|
| `none` | 0dp | No bottom chrome |
| `bottomNavExpanded` | 64dp | Bottom nav, expanded density |
| `bottomNavCompact` | 52dp | Bottom nav, compact density |
| `fixedCta` | 84dp | Fixed CTA, no bottom nav |
| `immersiveCta` | 72dp | Fixed CTA on an immersive stage |
| `bottomNavPlusFloatingCta` | 140dp | Bottom nav + floating CTA together |

## 12. Navigation Visibility Matrix

| Screen | Customer Nav | Admin Nav | Top Bar Type | Bottom CTA Mode |
|---|---|---|---|---|
| Home | Visible | — | Primary | None |
| Catalog (Select Model) | Visible | — | Primary | None |
| Product Detail | Hidden | — | Detail | Fixed CTA |
| Editor 2D | Hidden | — | Immersive | Fixed CTA |
| Preview 3D | Hidden | — | Immersive | Fixed CTA |
| Cart | Hidden (pushed) | — | Detail | Fixed CTA |
| Checkout (all steps) | Hidden (pushed) | — | Detail (form-flow) | Fixed CTA |
| Orders (list) | Visible | — | Primary/List | None |
| Order Detail / Tracking | Hidden (pushed) | — | Detail | None (status-driven, no persistent action) |
| Profile | Visible | — | Primary | None |
| Admin Overview | — | Visible* | Admin | None |
| Admin Management | — | Visible* | Admin | None |
| Admin Operations | — | Visible* | Admin | None |
| Admin Activity | — | Visible* | Admin | None |
| Admin Order Detail | — | Hidden | Detail/Admin | None |
| Admin Payment Detail | — | Hidden | Detail/Admin | None |

`*` Admin nav visibility is structural per Task 16 (primary admin destinations show it, detail/flow screens don't) but **not yet wired** — see §8/§18. The matrix documents intended visibility for when a future phase introduces the Admin shell; today all Admin screens render with no bottom nav at all (pre-existing behavior, unchanged).

## 13. Responsive Behavior

- `SuitNavBar` height reduces automatically on `isShortHeight` devices (64dp → 56dp expanded) via `SuitTheme.responsive`, independent of width class (Task 23 — width and height stay independent inputs).
- No width-specific nav layout change was needed: `Arrangement.SpaceAround` + `Modifier.weight(1f)` per item already prevents overflow from NARROW to WIDE, and label `TextOverflow.Ellipsis` (added in this rewrite) prevents clipping/wrapping on the narrowest phones without shrinking type size.
- `SuitImmersiveScaffold`'s content fills the full available box — it does not apply `SuitContentWidth.screen` (440dp) to the stage, per Task 7's explicit warning.
- `SuitFixedCtaBar` and the other scaffolds use `SuitTheme.responsive.horizontalContentPadding`, so they inherit the existing NARROW/STANDARD/MEDIUM/WIDE padding scale rather than a hardcoded value.

## 14. Files Created

- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/theme/SuitBottomClearance.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/components/AdminBottomNav.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/components/SuitNavScrollBehavior.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/components/SuitTopBars.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/components/SuitScaffolds.kt`
- `UI_PHASE_9_3_NAVIGATION_REPORT.md` (this file)

## 15. Files Modified

- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/components/SuitBottomNav.kt` — rewritten to introduce the shared `SuitNavItem`/`SuitNavBar` engine; `SuitBottomNav` public signature is backward compatible (new `density`/`translucent` params default).
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/MainShellScreen.kt` — replaced the manual `Column { Box(weight(1f)) ; SuitBottomNav }` with `SuitPrimaryDestinationScaffold(bottomNav = { ... }, content = { tabNavigator.current.Content() })`. No Tab/Screen/business logic touched; `signOut`/`CompositionLocalProvider`/`TabNavigator` structure unchanged.

No other files were modified. `App.kt`, `Tabs.kt`, `AuthFlow.kt`, `CheckoutFlow.kt`, `OrdersScreens.kt`, `TabRootScreens.kt`, `AdminScreens.kt`, `SuitTopBar.kt`, `MainActivity.kt`, `themes.xml` were all audited and left untouched.

## 16. Business-Logic Impact

None. No `ScreenModel`, repository, session, API DTO, payment, order, admin-mutation, upload, or database code was touched. The only behavioral change in the running app is that `MainShellScreen` now composes through `SuitPrimaryDestinationScaffold` instead of an inline `Column`/`Box` — structurally identical output (same background, same weighted content area, same bottom nav), verified by reading the resulting composition tree above.

## 17. Targeted Compile Result

- `.\gradlew.bat :composeApp:compileKotlinMetadata` → `BUILD SUCCESSFUL` (task reports `SKIPPED`/no-op for this project configuration, consistent with prior phases — iOS targets are disabled on this machine so there is no separate metadata klib to compile against).
- `.\gradlew.bat :composeApp:compileKotlinDesktop` → `BUILD SUCCESSFUL` after one fix: `SuitNavScrollBehavior.kt` was initially missing `import androidx.compose.runtime.getValue` for the `by remember { mutableStateOf(...) }` delegate syntax, causing a real compile error (`Property delegate must have a 'getValue' method`). Added the missing import; recompiled clean.

No other compile errors were encountered across any of the new/modified files.

## 18. Remaining Risks

- **Admin nav is unwired.** `AdminBottomNav`/`AdminTab` exist and compile but are not consumed by any real Admin shell — no `AdminShellScreen`/`TabNavigator` was created, since doing so would require feature-screen content decisions (an "Actividade" screen doesn't exist; Catalog/Orders/Payments would need regrouping) that are out of this phase's scope. A future phase must either build that shell or explicitly decide Admin stays push-only.
- **`SuitDetailTopBar`/`SuitImmersiveTopBar`/`SuitAdminTopBar`/list & form-flow scaffolds have no adopters yet.** They compile and are ready, but no existing screen was migrated to use them (by design — Task 25). Their first real integration will surface any signature gaps the current design didn't anticipate (e.g. subtitle support, mentioned in Task 17 but not present on any existing screen today so not added speculatively).
- **`rememberSuitNavDensity` is untested against a live scrolling screen.** It compiles and its logic was reasoned through directly, but there is no host screen in this phase to visually confirm the expand/collapse feel; the first screen that adopts it should sanity-check the threshold behavior.
- **Non-edge-to-edge assumption is load-bearing.** If a later phase enables edge-to-edge (e.g. for a more modern Android look), the ownership-model doc comment in `SuitScaffolds.kt` is the single place that decision needs to be revisited — but until then, every scaffold's lack of `WindowInsets` handling is correct, not an oversight.

## 19. Next-Phase Readiness

The structural shell required by later screen-migration phases now exists: five scaffolds, a shared nav-rendering engine (customer + admin), four top-bar variants, a fixed-CTA container, and a named bottom-clearance policy — all commonMain, all additive, all compiling cleanly with zero business-logic or Voyager architecture changes. Phase 9.4+ (actual screen-by-screen migration) can adopt `SuitPrimaryDestinationScaffold`/`SuitDetailScaffold`/etc. per screen without needing further scaffold-layer design work, aside from the Admin-shell decision flagged in §18.

---

# Final Structured Response

1. **Git precondition**: Branch `ui/figma-refactor`, no commit made this phase, working tree carried forward uncommitted Phase 9.1/9.2 changes plus this phase's additions.
2. **Current HEAD**: `9cbb443e18b9856b1be26230c08d31114e32265f` (unchanged — nothing committed).
3. **Current navigation architecture audited**: `App.kt`, `MainShellScreen.kt`, `Tabs.kt`, `AdminScreens.kt`, `AuthFlow.kt`, `CheckoutFlow.kt`, `OrdersScreens.kt`, `TabRootScreens.kt`, `SuitBottomNav.kt`, `SuitTopBar.kt`, `MainActivity.kt`, `themes.xml` — full inventory in §2 above.
4. **Scaffold architecture created**: `SuitPrimaryDestinationScaffold`, `SuitDetailScaffold`, `SuitImmersiveScaffold`, `SuitFormFlowScaffold`, `SuitListScaffold`, plus `SuitFixedCtaBar` — all in `SuitScaffolds.kt`.
5. **Inset ownership model**: Documented explicitly (§4); non-edge-to-edge baseline confirmed, no `WindowInsets.systemBars` layer added (would double-pad), `Modifier.imePadding()` is the one safe exception, used only in `SuitFormFlowScaffold`.
6. **Customer nav result**: `SuitBottomNav` rewritten onto a shared `SuitNavItem`/`SuitNavBar` engine; expanded/compact/translucent states supported; existing call site backward compatible; labels/icons unchanged.
7. **Compact-on-scroll result**: `rememberSuitNavDensity` (two overloads: `LazyListState`, `ScrollState`) in `SuitNavScrollBehavior.kt`, `snapshotFlow`-driven, fully opt-in, not yet adopted by any screen (none migrated this phase).
8. **Admin nav result**: `AdminBottomNav`/`AdminTab` created on the same engine with distinct labels (Visão Geral/Gestão/Operações/Actividade); intentionally not wired into a shell (§18 risk).
9. **Top bar result**: `SuitPrimaryTopBar`/`SuitDetailTopBar`/`SuitImmersiveTopBar`/`SuitAdminTopBar` created in `SuitTopBars.kt`, all thin wrappers over the unmodified `SuitTopBar` engine.
10. **Fixed CTA result**: `SuitFixedCtaBar` generic docked container created; structural only, no CTA copy/logic.
11. **Bottom-clearance policy**: `SuitBottomClearance` object with `none`/`bottomNavExpanded`/`bottomNavCompact`/`fixedCta`/`immersiveCta`/`bottomNavPlusFloatingCta`.
12. **Navigation visibility matrix**: Provided in §12 for all 17 requested screens.
13. **Responsive nav result**: Height-aware bar sizing via `isShortHeight`; overflow-safe via `weight(1f)` + ellipsis; immersive stage explicitly does not apply the 440dp screen cap.
14. **Files created**: 5 new Kotlin files + this report (§14).
15. **Files modified**: `SuitBottomNav.kt`, `MainShellScreen.kt` (§15).
16. **Business logic changes**: None.
17. **Targeted compile result**: `compileKotlinMetadata` → BUILD SUCCESSFUL (no-op); `compileKotlinDesktop` → BUILD SUCCESSFUL (one missing-import fix applied and verified).
18. **Remaining risks**: Admin nav unwired; new top bar/scaffold variants have no adopters yet; compact-on-scroll untested against a live screen; non-edge-to-edge assumption is load-bearing for the ownership model (§18 detail).
19. **PHASE 9.3: PASS**
