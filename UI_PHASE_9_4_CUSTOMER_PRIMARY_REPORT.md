# Phase 9.4 — Customer Primary Surfaces (Home / Catalog / Product Detail)

## 1. Scope executed this phase

- Audited current Home and Catalog screens, their ScreenModels, and navigation wiring.
- Determined Product Detail did not exist: Home's featured-model tap and Catalog's model tap both
  pushed `Editor2DPartsVoyagerScreen` directly.
- Audited all 27 real suit photos in `SuitUP-Design-Handoff/guardafato` (dimensions, alpha channel,
  visual classification). Confirmed the 5 assets already integrated in a prior partial pass
  (`suit_navy_executive`, `suit_charcoal_slim`, `suit_beige_casual`, `suit_brown_premium`,
  `suit_black_classic`) are distinct, good-quality, correctly color-matched photoreal choices.
- Built a new Product Detail screen and wired it into navigation between Catalog/Home and the
  existing Editor flow.
- Migrated Home and Catalog to the Phase 9.3 scaffold/top-bar/responsive/compact-nav system.
- Fixed the Home-side status-badge mapping duplication (Task 27 scope: Home only).

## 2. Asset audit summary

All 27 source photos are 1024×1536 (2:3), 8-bit. 5 are already selected and copied into
`composeResources/drawable/` with semantic names; file-size cross-reference confirmed:

| Semantic key | Source file | Color |
|---|---|---|
| `suit_navy_executive` | 10.png | Navy pinstripe |
| `suit_charcoal_slim` | 11.png | Grey pinstripe |
| `suit_beige_casual` | 17.png | Beige-tan |
| `suit_brown_premium` | 20.png | Muted brown-taupe |
| `suit_black_classic` | 23.png | Black |

`suit_black_classic` is intentionally shared by two mock models (m1 "Clássico Preto" and m6
"Smoking Preto") — both are genuinely black suits and no separate tuxedo/black-tie photo exists
in the audited batch. This is a documented content gap, not a mismatch; already noted in
`MockCatalogStore.kt` and `SuitImageResources.kt` comments from the prior pass.

The remaining 22 source photos were classified by direct visual inspection but not integrated
this phase (6 mock models is the current catalog size; expanding it is out of scope). Two
near-duplicate pairs were flagged for a future asset pass (14/15: near-identical light blue;
27/28: near-identical royal blue), and one file (21) was flagged `DO_NOT_USE` — inconsistent
flat/illustration style with a noisy vignette, not a usable product photo.

Legacy key aliases (`suit_classic_black`, `suit_navy_business`, `suit_grey_slim`,
`suit_casual_linen`) are kept in `SuitImageResources.kt` only because the Admin catalog dropdown
and the remote mapper's fallback (both out of scope this phase) still reference them.

## 3. Product Detail — new screen

- **New files**:
  - `ui/screens/product/ProductDetailScreenModel.kt` — reads `SuitModel` from
    `CustomerCatalogRepository` (already loaded by Home/Catalog, no new API call), falling back to
    `MockCatalogStore.getSuitModelById` if the model hasn't landed in shared state yet (same
    pattern as `MockOrderStore.ensureDraft`). Combines with `MockOrderStore.cart` for the cart
    badge count.
  - `ui/screens/product/ProductDetailScreen.kt` — `SuitDetailScaffold` + `SuitDetailTopBar` (back
    chevron, title, cart) + `SuitFixedCtaBar` hosting a single "Personalizar" `PrimaryGoldButton`.
    Content: `SuitImageContainer(context = ProductDetail)`, name/category/price, description,
    fabric/color. Handles loading (`SuitContentLoading`) and not-found (`EmptyStateCard`) states.
    No bottom nav — Product Detail is a pushed detail screen, not a tab root, matching the
    scaffold's structural bottom-nav rule.
  - `ui/navigation/ProductDetailScreens.kt` — `ProductDetailVoyagerScreen(modeloId)`. Its
    "Personalizar" CTA calls `MockOrderStore.startDraft(model.toModeloFato())` then pushes the
    existing `Editor2DPartsVoyagerScreen(model.id)` — the Editor entry contract is untouched.

## 4. Navigation rewiring

`ui/navigation/TabRootScreens.kt`:
- `HomeVoyagerScreen.onFeaturedModelClick` now pushes `ProductDetailVoyagerScreen(model.id)`
  instead of starting a draft and jumping straight to the Editor.
- `SelectModelVoyagerScreen.onModelClick` now pushes `ProductDetailVoyagerScreen(modelo.id)`.
  The `SelecionarModeloScreenModel.onEvent(ModeloClicado)` call (which still calls
  `MockOrderStore.startDraft`) was left untouched — it's idempotent by model id, and Product
  Detail's own "Personalizar" re-issues the same call before actually entering the Editor, so
  no ScreenModel logic needed to change.

New flow: **Home/Catalog → Product Detail → Editor 2D (Parts → Colors → Preview 3D)**.

## 5. Home screen migration

- Top bar: `PremiumTopBar` → `SuitPrimaryTopBar`.
- Compact-on-scroll nav: `rememberSuitNavDensity(listState)` wired into the shared
  `LocalSuitNavDensity` via `LaunchedEffect`, driving `MainShellScreen`'s `SuitBottomNav`.
- All hardcoded `20.dp` horizontal paddings replaced with `SuitTheme.responsive.horizontalContentPadding`.
- Hero image key fixed from the legacy alias `"suit_navy_business"` to the canonical
  `"suit_navy_executive"`.
- **Status-mapping bug fixed** (Task 27): removed the private `EstadoPedido.toStatusChipType()`
  duplicate mapping and the now-unused `StatusChip`/`StatusChipType` imports. `RecentOrderCard`
  now calls `SuitStatusBadge(text = order.estado.shortLabel(), kind = order.estado.toBadgeKind())`
  directly, reusing the canonical mapping in `OrderStatusUi.kt`.
- The hero's decorative side image kept its existing fixed-width `Box`/`Image` layout rather than
  being forced into `SuitImageContainer` (whose `ProductCard` spec is full-width + 0.82 aspect) —
  it is decorative CTA art, not a product card, and the two layouts aren't a fit.

## 6. Catalog screen migration

- Top bar: `PremiumTopBar` → `SuitPrimaryTopBar`; removed the dead `onBack` parameter (confirmed
  never passed non-null from its single call site).
- Compact-on-scroll nav: `rememberSuitNavDensity(gridState)` wired the same way as Home.
- Grid columns: hardcoded `GridCells.Fixed(2)` → `GridCells.Fixed(SuitGridPolicy.productColumns(widthClass))`.
- Loading state: replaced the single text-only `EmptyStateCard` with a 6-tile skeleton grid
  (`SuitSkeletonBlock` + two `SuitSkeletonLine`s per tile) shown while `isLoading && visible.isEmpty()`;
  error/empty (non-loading) states still use `EmptyStateCard`.
- Removed the redundant `actionLabel`/`onAction` "Personalizar" button from `SuitImageCard` — card
  tap alone now routes to Product Detail, so the duplicate in-card action button was removed.
- All hardcoded `20.dp` paddings replaced with responsive padding.

## 7. Compact-nav integration

Both Home and Catalog now push their own scroll-direction density into the shared
`LocalSuitNavDensity` state that `MainShellScreen` already reads to drive `SuitBottomNav` — this
closes the gap the Phase 9.3 infrastructure was built for but that no screen had used yet.

## 8. Validation

- `.\gradlew.bat :composeApp:compileKotlinMetadata` — BUILD SUCCESSFUL.
- `.\gradlew.bat :composeApp:compileKotlinDesktop` — BUILD SUCCESSFUL.
- No `assembleDebug`/`test` run, per this phase's mandate.

## 9. Explicitly out of scope / untouched

- ScreenModels' data-loading logic (`HomeScreenModel`, `SelecionarModeloScreenModel`) — unchanged.
- Repositories, remote mappers, Admin catalog screens/dropdown — unchanged.
- Editor 2D/3D screens and their entry contract — unchanged.
- Expansion of the 6-model mock catalog to use more of the 22 unclassified-but-unused assets.
