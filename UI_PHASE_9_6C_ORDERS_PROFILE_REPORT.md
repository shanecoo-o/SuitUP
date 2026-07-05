# Phase 9.6C — Orders + Profile Stitch Migration Report

## 1. Files changed

Modified:
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/orders/OrdersListScreen.kt` (already migrated pre-session; verified, no further edits)
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/orders/TrackOrderScreen.kt` (rewritten)
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/OrdersScreens.kt` (fallback null-pedido branch migrated)
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/profile/ProfileScreen.kt` (rewritten)
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/TabRootScreens.kt` (`ProfileVoyagerScreen` wired to new destinations)

Created:
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/profile/ProfileDestinationScreens.kt` (Personal Data, Addresses, Measurements, Notifications, Support composables)
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/ProfileDestinationScreens.kt` (corresponding Voyager `Screen` classes)

No ScreenModels, repositories, or domain models were modified. No new business logic was introduced.

## 2. Orders List

Already fully migrated to Stitch prior to this session (`SuitPrimaryTopBar`, `SuitContentLoading`, `ErrorStateCard`, `EmptyStateCard`, `SuitAlertBanner`, `SuitCard`, `SuitImageContainer`, `SuitStatusBadge`, canonical `shortLabel()`/`toBadgeKind()`, `formatMzn`, `suitImageResource`). Verified against `git diff` — no gaps found, no changes made.

## 3. Order Detail

The app has no separate Order Detail route — `SuitDetailScaffold`'s own doc comment identifies "Product Detail, Order Detail/Tracking" as one archetype, and `OrdersScreens.kt` always pushed straight to tracking. Order Detail requirements were folded into `TrackOrderScreen.kt` as a new `OrderSummarySection` (thumbnail via the existing image-continuity chain, status badge, fabric/color/lapel/button details, created/updated timestamps) and a new `DeliverySection` (client, delivery/pickup address, subtotal/delivery/total) — both using only real `Pedido` fields.

## 4. Image continuity

Reused the existing chain unchanged: `DesignFato.idModeloBase` → `MockCatalogStore.getModeloFatoById(id)?.urlImagemPrevia` → `suitImageResource(...)` → `SuitImageContainer`. No new mapping layer added.

## 5. Payment status display

`PaymentStatusCard` (existing, closed Phase 9.6B component) now receives real values: `paymentReference = order.pagamento.referenciaTransaccao`, `orderReference = "#${order.numero}"`, `hasProof = order.pagamento.caminhoImagemComprovativo != null`. A `SuitAlertBanner(Error)` is shown when `PaymentStatus.REJECTED`.

## 6. Customer Proof Detail — DEFERRED (data/route gap)

`order.pagamento.caminhoImagemComprovativo` does carry a real `proofFileId` for remote orders, but no customer-authorized download/preview backend route was found in `/backend` — `RemoteFileRepository.download` / `FileUploadApi.downloadFile` are only consumed on the Admin side. Building a customer preview would require inventing a route/auth path that doesn't exist. Decision: surface only the real `hasProof` boolean plus reference/order metadata via `PaymentStatusCard`; no fake preview or download action was added.

## 7. Tracking truthfulness

Verified `CustomerTrackingEvent`/`PaymentTrackingRepository` mapping: the backend only ever returns real past history, never fabricated future/completed stages. No bug existed here; no code changes were needed. `OrderTimeline` continues to render `Completed`/`Current`/`Pending`/`Rejected` states correctly for both the legacy `EventoPedido` mock timeline and the real backend `CustomerTrackingEvent` timeline.

## 8. Profile — main screen

`ProfileScreen.kt` rewritten onto `SuitPrimaryTopBar` + `SuitCard` + `SuitMenuRow`. Four previously-disabled rows (Dados pessoais, Endereços, Medidas, Notificações) are now enabled and route to real destinations; a new "Ajuda e suporte" row was added. "Pagamentos" and "Preferências" remain disabled — genuinely out of scope for this phase (no backend/feature exists), left as honest disabled rows rather than fake destinations.

## 9. Personal Data

Read-only: `SuitTextField(readOnly = true, enabled = false)` for `nome`/`email`/`telefone`, sourced from the real `Utilizador` (session-backed via `PerfilScreenModel`, falls back to mock only when unauthenticated). A caption states editing isn't available yet — no fake save/edit action was added since no update API exists.

## 10. Addresses

No persistent address book exists in the domain model — `EnderecoEntrega` is only ever attached to a specific `Pedido`. Built as an honest `EmptyStateCard` explaining addresses are per-order, not a saved book. No fake CRUD UI was built.

## 11. Measurements

Real `Utilizador.medidasGuardadas` (`Medidas?`). When present, all non-blank fields are rendered read-only (altura, peso, ombros, peito, cintura, quadril, manga, calça, casaco, pescoço, observações). When null, an honest `EmptyStateCard` is shown instead of fabricated data.

## 12. Notifications

No notification backend/feed exists anywhere in the codebase. Honest `EmptyStateCard` informational state — no fake notification list was invented.

## 13. Support

No support contact constants (phone/email/chat) exist anywhere in the codebase. Honest `EmptyStateCard` stating no contact channel is configured yet, pointing the user back to "Pedidos" for order status. No fake phone number/email/chat link was invented.

## 14. Placeholder cleanup

All five destinations previously showed "Disponível numa próxima fase" — none of that placeholder text remains for Personal Data, Addresses, Measurements, Notifications, or Support. "Pagamentos" and "Preferências" (genuinely out of scope) still use the same placeholder text, which remains accurate for them.

## 15. Business logic impact

None. No ScreenModel, repository, domain model, or backend code was changed. All new destination screens read existing state (`PerfilScreenModel.state.utilizador`) via a fresh `rememberScreenModel` instance, matching the existing pattern used elsewhere in the navigation layer (e.g. `TrackOrderVoyagerScreen`).

## 16. Compile result

`.\gradlew.bat :composeApp:compileKotlinDesktop` → **BUILD SUCCESSFUL**.

Errors found and fixed during this run:
- Missing `TimelineItemStatus` import in `TrackOrderScreen.kt`.
- Missing `androidx.compose.ui.unit.dp` import in `ProfileScreen.kt` and `ProfileDestinationScreens.kt`.
- Removed an unused `Medidas` import in `ProfileDestinationScreens.kt`.

## 17. Remaining risks

- Personal Data / Addresses / Measurements / Notifications / Support have no write-back capability by design (no backend exists) — if product later adds edit/CRUD APIs for these, the screens will need a second pass.
- Customer Proof Detail remains a data/route gap; if a customer-authorized file download route is added to the backend, `TrackOrderScreen.kt`'s `PaymentStatusCard` usage is the natural place to wire a real preview/download action.
- Only `compileKotlinDesktop` was run per phase scope (no `assembleDebug`/test suite/manual UI click-through was performed in this session).

---

PHASE 9.6C: PASS
