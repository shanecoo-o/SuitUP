# Phase 9.6A — Commerce Core (Cart → Checkout → Payment handoff)

Migration of the customer commerce flow (Cart, Customer Information, Measurements,
Delivery/Pickup, Address, Payment-entry handoff) to the shared Stitch component system
(`SuitDetailScaffold`/`SuitFormFlowScaffold`, `SuitDetailTopBar`, `SuitFixedCtaBar`/
`SuitDualBottomBar`, `SuitCard`, `SuitTextField`/`SuitDropdown`, `SuitButton`,
`SuitAlertBanner`, `SuitSelectableCard`, `SuitImageContainer`), preserving all existing
business logic, ScreenModel state ownership, and navigation order. No backend,
repository, session, or payment-submission logic was touched. Nothing committed.

## 1. Continuity fix (Task 3)

`ItemCarrinho` (`domain/model/Models.kt`) gained a new `imagemKey: String = ""` field
(6th field, default-valued, `@Serializable`-safe). `MockOrderStore.toCartItem()` now
resolves it via `MockCatalogStore.getModeloFatoById(design.idModeloBase)?.urlImagemPrevia
?: ""` — the same model lookup Product Detail/Editor/Preview already use, so the Cart
item image is the *actual selected suit photo*, not a generic placeholder. This is the
only change outside the six screen files listed below, and it is additive/mock-only
(`ItemCarrinho`, `MockOrderStore` — no backend/remote/repository code).

## 2. Files migrated

- **`ui/screens/cart/CartScreen.kt`** — rewritten. `SuitDetailScaffold` +
  `SuitDetailTopBar` (title "Carrinho", cart badge) + `SuitFixedCtaBar` (Total row +
  `SuitButton` "Continuar para checkout", omitted entirely when the cart is empty since
  there is nothing to check out). Item rows use `SuitImageContainer(context =
  SuitImageContext.Thumbnail)` rendering `painterResource(suitImageResource(item.imagemKey))`
  — the real suit photo — replacing the old `SuitGarmentMini` fake vector silhouette.
  Empty state is a locally-built light card (`ShirtIcon` + text + `SuitButton` "Explorar
  catálogo" wired to the same `onContinueShopping`), summary card rebuilt with `SuitCard`.
  Quantity stepper (`SuitQuantityStepper`), remove (`CloseIcon`), and edit ("Editar"
  text-link) preserved exactly — no new icons invented (`SuitIcons.kt` has no dedicated
  edit/trash icon; confirmed by grep before deciding to keep the existing pattern).
  Public composable signature is byte-for-byte unchanged, so `CartVoyagerScreen` in
  `TabRootScreens.kt` required **no changes** (verified by reading its call site).
- **`ui/screens/checkout/CheckoutScreen.kt`** (Customer Info, step 1) — rewritten:
  `SuitFormFlowScaffold` + `SuitDetailTopBar` + `SuitFixedCtaBar` wrapping `SuitButton`
  "Continuar para medidas" (same enabled condition: all three fields non-blank).
  `PremiumTextField` → `SuitTextField` (identical params), `PremiumCard` → `SuitCard`,
  `SectionHeader` → `SuitEyebrow` + `Text`, `SuitSwitch` unchanged. All field
  values/callbacks/validation untouched.
- **`ui/screens/checkout/CheckoutMedidasScreen.kt`** (Measurements, step 2) — rewritten:
  same scaffold/top-bar/CTA swap; the four `MedidasGrupo` sections (Medidas gerais,
  Tronco, Comprimentos, Observações) and all 10 field callbacks preserved exactly;
  inline validation text replaced with `SuitAlertBanner(variant = Error)` (Task 17); the
  "(*) obrigatório" footnote kept as plain text.
- **`ui/screens/checkout/DeliveryTypeScreen.kt`** (Delivery/Pickup, step 3) — finished
  (was already partially modernized with `SuitSelectableCard`/`SuitDualBottomBar`): swapped
  the remaining `PremiumTopBar`/`SectionHeader` for `SuitFormFlowScaffold`+`SuitDetailTopBar`
  and `SuitEyebrow`+`Text`. `SuitDualBottomBar` reused as-is in the `fixedCta` slot
  (matches the scaffold doc's "caller supplies the actual `SuitDualBottomBar`-style
  content" contract). The two delivery-option cards, icons, and selection logic
  untouched.
- **`ui/screens/checkout/AddressScreen.kt`** (Address/Pickup point, step 3 continuation) —
  finished the same way: `PremiumTopBar`/`SectionHeader`/`PremiumDropdown`/
  `PremiumTextField`/`PremiumCard`/`SecondaryDarkButton` → `SuitFormFlowScaffold`+
  `SuitDetailTopBar`+`SuitEyebrow`+`SuitDropdown`+`SuitTextField`+`SuitCard`+`SuitButton`
  (Secondary/Small, demo-fallback action). `SuitSegmentedToggle` (Entregar/Levantar),
  `SuitDualBottomBar` footer, `DeliveryForm`/`PickupForm` structure, and the
  `canContinue`/`isSubmitting` gating logic preserved exactly. Error path now uses
  `SuitAlertBanner(variant = Error)` instead of an ad hoc red `Text` inside a card (Task 17).
- **`ui/screens/checkout/PaymentScreen.kt`** (Payment entry, step 4 — **handoff only**,
  per Task 18) — visual-pass only: same scaffold/top-bar/card/button swaps as the other
  four screens (`PremiumCard`→`SuitCard`, `PremiumTextField`→`SuitTextField`,
  `PrimaryGoldButton`/`SecondaryDarkButton`→`SuitButton`), `SuitUploadCard` and
  `StatusChip` reused unchanged (both were already neutral/reusable). No change to the
  submission flow, proof-upload flow, pending/confirmation states, or `PagamentoScreenModel`
  — those remain explicitly reserved for Phase 9.6B. `ConfirmationScreen.kt` was not
  opened or touched.

## 3. Navigation / call-site audit (Tasks 5, 6, 8, 10)

Read `ui/navigation/CheckoutFlow.kt` in full (all 5 `*VoyagerScreen` classes) and
`TabRootScreens.kt`'s `CartVoyagerScreen`/`SelectModelVoyagerScreen`/`ProfileVoyagerScreen`
before making any change. Every migrated composable's **public parameter list is
identical** to what it was before this phase — no prop was added, removed, renamed, or
retyped. Consequently **no navigation file needed any edit**: the checkout route
sequence (Cart → Customer Info → Measurements → Delivery Type → Address → Payment →
Confirmation), the single real order-creation point (`EnderecoScreenModel.criarPedido()`
at the Address step, unchanged), and `MockOrderStore.beginCheckout()` on the Cart CTA are
all exactly as they were — confirmed by reading the file, not assumed. No second
checkout flow exists; no order is created earlier than before.

## 4. Compile validation (Task 22)

`.\gradlew.bat :composeApp:compileKotlinDesktop` — **BUILD SUCCESSFUL** on the first run
(7 actionable tasks: 1 executed, 6 up-to-date). Zero compile errors across all six
rewritten files plus the `imagemKey` continuity change; no surgical fixes were needed
this phase. `assembleDebug`/`test` were **not** run, per Task 22 (no compile failure
occurred that would justify the deeper gate).

## 5. Scope check

`git status`/`git diff --stat` confirm this session's changes are exactly: `Models.kt`
(+1 line), `MockOrderStore.kt` (+1 line), and the six screen files listed in Section 2.
No file under `backend/`, `data/remote/`, `data/repository/`, `data/session/`, or any
Admin/Auth/Profile/Payment-proof-upload file was opened or modified. `ConfirmationScreen.kt`
was not touched. Everything else showing as modified/untracked in `git status` belongs to
already-closed Phases 9.1–9.5 (theme, shared components, editor/preview, catalog/home,
image resources) and predates this session. Nothing was committed.

## 6. Remaining / deferred (by design, not oversight)

- **Payment submission/proof-upload/pending/confirmation redesign** — explicitly deferred
  to Phase 9.6B (Task 18). This phase only reskinned `PaymentScreen.kt`'s chrome/cards.
- **Optional checkout progress indicator (Task 7)** — `CheckoutStepIndicator` (an
  already-neutral, reusable component using Gold/SurfaceLow/Mist/Smoke tokens) continues
  to be used unmodified across all four numbered steps; no new indicator was built since
  the existing one already fits the shared token language.
- **Not visually verified on a running emulator/device or desktop window** — this was a
  compile-and-static-review pass, consistent with Task 22's "targeted validation only"
  instruction. Worth a quick visual sanity pass (especially `SuitFixedCtaBar`/
  `SuitDualBottomBar` clearance against each screen's scroll content) before shipping.
- **`SuitImageContainer`'s `Thumbnail` context** is sized via an explicit
  `Modifier.size(72.dp)` wrapper in `CartScreen.kt` (the component itself defaults to
  `fillMaxWidth()` + `aspectRatio(1f)`); this matches the original 72dp footprint of the
  replaced `SuitGarmentMini` icon.

## Readiness for Phase 9.6B

All in-scope commerce screens now speak the same shared scaffold/top-bar/CTA-bar/card/
field/button language as the rest of the migrated app. Business logic, validation,
ScreenModel ownership, and the checkout route/order-creation sequence are unchanged and
were verified by direct code reading, not assumption. The worktree is ready to proceed to
Phase 9.6B (payment submission/proof/confirmation UI). Nothing committed.

PHASE 9.6A: PASS
