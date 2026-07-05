# Phase 9.6B — Payment + Proof + Confirmation

Implementation of the real Payment/Proof/Confirmation customer experience on top of the
already-existing real backend integration (`PaymentTrackingRepository`,
`RemotePaymentRepository`, `RemoteFileRepository`, `PaymentsApi`, `FileUploadApi`,
`ProofFilePicker`, `PagamentoScreenModel`). No backend contract, repository, or picker
logic was rewritten — this phase only completed and honestly wired the UI that was left
as a "visual foundation" by Phase 9.6A. Nothing committed.

## 1. Files changed

- **`ui/screens/checkout/CheckoutScreenModels.kt`** — `PagamentoUiState` gained three
  additive fields: `tipoFicheiroCarregado: String? = null`, `tamanhoFicheiroBytes: Long? =
  null` (both already available on the existing `SelectedProofFile` the picker produces,
  just not previously surfaced to the UI — needed for Task 11's "show file type/size, not
  just filename"), and `numeroPedido: String = ""` (populated from the already-fetched
  `backendOrder?.numero`, needed for Task 3's "short customer-facing reference, not raw
  UUID"). All three are populated in the existing `init` block / `FicheiroSeleccionado`
  event handler / cleared in `RemoverFicheiroClicado` — no new remote calls, no new
  business logic, no change to `submeter()`, `enviarComprovativo()`, `submeterModoDemo()`,
  or `concluirModoDemo()`. The real submission/upload/demo-fallback flow is byte-for-byte
  what it was before this phase.
- **`ui/screens/checkout/PaymentScreen.kt`** — completed (not rewritten from zero): kept
  every existing callback signature (`onPickFile`, `onRemoveFile`,
  `onPaymentReferenceChange`, `onSubmit`, `onContinueDemo`, `onCopyNumber`) and added
  `uploadedFileType`, `uploadedFileSizeBytes`, `orderReference` (new, additive, defaulted
  params) and replaced `paymentStatusLabel: String?` with `paymentStatus: PaymentStatus?`
  (the real domain enum, needed to drive semantic status badges instead of a pre-formatted
  string — the only call site, `CheckoutFlow.kt`, was updated accordingly). Five clearly
  separated sections: (1) order/amount summary with a real 5-state status chip, (2)
  payment method (M-Pesa only — the one real method, no invented options), (3) reference
  field, (4) submission status (`SuitAlertBanner`), (5) proof upload
  (`SuitUploadCard` + file type/size line). `SuitButton`'s native `loading` param is now
  used for the submit button instead of just swapping text, which also structurally
  prevents double-submission.
- **`ui/screens/checkout/ConfirmationScreen.kt`** — migrated fully from the old
  `PremiumTopBar`/`PremiumCard`/`PrimaryGoldButton`/`SecondaryDarkButton` system to
  `SuitDetailScaffold` + `SuitPrimaryTopBar` (no back chevron — preserves the screen's
  existing no-back-arrow behavior exactly) + `SuitFixedCtaBar` + `SuitButton` + `SuitCard`.
  Title/subtitle/badge color are now derived from the *real* `order.pagamento.status`
  (`PaymentStatus.PENDING/CONFIRMED/REJECTED`) via a small `resolveConfirmationCopy()`
  helper, instead of always announcing "Pedido criado com sucesso" — pending shows an
  amber badge + "submitted, awaiting confirmation" copy, confirmed shows a green badge,
  rejected shows a red badge with `ErrorIcon`. `PaymentStatusCard` is reused completely
  unmodified (see §5).
- **`ui/navigation/CheckoutFlow.kt`** — `PaymentVoyagerScreen.Content()` updated to pass
  the renamed/added `PaymentScreen` params (`paymentStatus = state.statusPagamento`,
  `uploadedFileType = state.tipoFicheiroCarregado`, `uploadedFileSizeBytes =
  state.tamanhoFicheiroBytes`, `proofUploaded = state.comprovativoEnviado`,
  `orderReference = state.numeroPedido`). No navigation structure, route order, or
  `LaunchedEffect`/`podeAvancar` logic touched. `ConfirmationVoyagerScreen` needed **no
  changes** — its existing props (`orderNumber`, `order`, `isDemo`, `onSeeOrders`,
  `onBackToHome`) are unchanged by the `ConfirmationScreen` migration.

## 2. Payment ScreenModel/state — inspected, not rewritten (Task 1)

Read `PagamentoUiState`/`PagamentoUiEvent`/`PagamentoScreenModel` in full before any edit.
Confirmed: order ID comes from the constructor param (set by `AddressScreen`'s
`EnderecoScreenModel.criarPedido()` → `PaymentVoyagerScreen(orderId)`); amount is
`state.totalPedidoMt`, sourced from the real order (`backendOrder?.total`) with a mock
fallback only when no backend order exists; the only real payment method is M-Pesa
(`MetodoPagamento.MpesaManual`, hard-coded server-side too — `PaymentMethodDto.MPESA`);
submission goes through `paymentRepository.submitPayment(...)`; proof upload through
`paymentRepository.uploadPaymentProof(...)`; pending state is `PaymentSubmitResult.Success
.payment?.status ?: PaymentStatus.PENDING`; navigation to Confirmation is driven by
`_podeAvancar` flipped only after a **real** `ProofUploadResult.Success` (or the explicit,
user-acknowledged demo path). No new state ownership was created — the three added fields
are additive projections of data the ScreenModel already fetches/holds.

## 3. Real submission & duplicate-reference (409) handling (Tasks 6, 8)

`RemotePaymentRepository.submit()` → `PaymentsApi.submitPayment()` performs a real
`POST /api/orders/{id}/payment`; the result is a real `ApiResult<PaymentRecord>`, never
faked. Crucially, **the exact required 409 message already existed** in
`PaymentTrackingRepository.paymentMessage()`:
`is ApiError.Conflict -> "Esta referência de pagamento já foi utilizada."` — this flows
straight into `PagamentoUiState.erro` untouched. Task 8 was therefore already satisfied by
the existing repository; this phase's contribution was making sure the UI renders `erro`
prominently and distinctly via `SuitAlertBanner(variant = Error)` instead of the previous
ad-hoc plain-`Text` treatment, so the message is never buried or genericized. No repository
or API code was touched.

## 4. Pending state, proof upload, and the 5-state hierarchy (Tasks 9–17)

`resolvePaymentStage()` (private, `PaymentScreen.kt`) derives one of five real states from
`paymentSubmitted` / `proofUploaded` / `paymentStatus`, never inventing a confirmed state
client-side:
- **A) Not submitted** — `StatusChipType.Inactive` ("Por enviar").
- **B) Pending, no proof** — `StatusChipType.Pending`, "envie o comprovativo".
- **C) Pending, proof uploaded** — `StatusChipType.Pending`, "aguardando confirmação".
- **D) Confirmed** — `StatusChipType.Confirmed` (green), admin-driven, read-only here.
- **E) Rejected** — `StatusChipType.Rejected` (red), admin-driven, read-only here.

Proof upload itself is unchanged real multipart: `ProofFilePicker.android.kt` reads real
bytes via `ContentResolver.openInputStream`, real `contentType`/size are read from the
`OpenDocument` result, and `FileUploadApi.uploadPaymentProof()` performs a real
`submitFormWithBinaryData` to `POST /api/orders/{orderId}/payment/proof`. Local size
pre-check (picker, fails fast before reading the full stream) and the repository-level
`validateProof()` (single source of truth for what's actually sent) both route failures
through the same `erro` field — no duplicate/inconsistent user-facing validation paths.
Only the *presentation* of an already-selected file changed: filename, MIME-derived type
label (PNG/JPEG/PDF), and a human-readable size (e.g. "JPEG · 482 KB") are now shown below
`SuitUploadCard`, computed by two small pure helpers (`formatProofMeta`/`formatFileSize`,
no `java.util.Formatter` — kept Kotlin-common-safe for the multiplatform target). Upload
progress remains a single honest indeterminate `isSubmitting`/`loading` state (no invented
percentage) since that's all the repository exposes — `SuitButton(loading = isSubmitting)`
now also structurally blocks duplicate taps rather than relying on `enabled` alone.

## 5. Confirmation screen (Tasks 18–19)

Fully on Stitch now (`SuitDetailScaffold`/`SuitPrimaryTopBar`/`SuitCard`/`SuitButton`/
`SuitSuccessBadge`). The previous unconditional "Pedido criado com sucesso" +
gold-checkmark framing is now status-driven: PENDING keeps the "submitted, awaiting
admin confirmation" framing (amber badge), CONFIRMED shows a green badge and confirmed
copy, REJECTED shows a red `ErrorIcon` badge and a "contact support" message — all sourced
from the real `order.pagamento.status`, never assumed. Demo mode keeps its own explicit,
clearly-labeled framing (gold badge, "not sent to server"), which is not a "fake success"
since demo is an intentional, user-acknowledged local-only path, not a silent substitution.
`PaymentStatusCard` is **reused completely unmodified** — it's also used by
`AdminScreens.kt` and `TrackOrderScreen.kt`, both explicitly out of scope this phase per
the "do not inspect Admin/Orders/Tracking" constraint, so its internal `PremiumCard`
container was deliberately left as-is rather than risk touching those screens' rendering.
This is a known, accepted visual-consistency gap (a dark card inside the now-light Bone
scaffold) — flagged in §7, not fixed, since fixing it means editing a component shared with
out-of-scope surfaces.

## 6. Customer proof detail (Task 20)

**Deferred to Phase 9.6C**, not built this phase. No dedicated navigation route or
data path currently exposes a safe way to view an uploaded proof file after the fact
(no "get my proof" endpoint call site wired anywhere in the customer flow, and the
proof-download risk profile — owner/admin 200, foreign customer 404, one observed
transient 500 — was explicitly flagged as informational-only, not to be built around this
phase). Building a new screen/route for this would require new navigation surface area
beyond the explicit Payment→Confirmation route this phase is scoped to preserve, so it is
documented here rather than invented.

## 7. Navigation & back behavior (Tasks 21–22)

Route order unchanged: Cart → Checkout → Measurements → Delivery Type → Address → Payment
→ Confirmation. `PaymentVoyagerScreen`/`ConfirmationVoyagerScreen` — no parallel
navigator, no duplicate screens, `LaunchedEffect(podeAvancar)`/`LaunchedEffect(state
.sessaoExpirada)` untouched. `PaymentScreen`'s "Voltar" button now also disables while
`isSubmitting` (previously it stayed clickable mid-request) — a small, safe addition, not
an invented lock: it only prevents leaving mid-flight, matching the "no duplicate
submission" spirit of Task 22 without adding any new state.

## 8. Responsive & keyboard behavior (Tasks 23–24)

Both screens use existing `SuitFormFlowScaffold`/`SuitDetailScaffold` (Bone background,
`imePadding()` already built into `SuitFormFlowScaffold`) with a `verticalScroll` content
column — narrow/short viewports scroll instead of clipping, and the reference field stays
reachable when focused via the scaffold's existing IME handling. No platform-specific
keyboard code was added.

## 9. Business-logic impact (Task 25)

Zero changes to `PaymentTrackingRepository.kt`, `RemotePaymentRepository.kt`,
`RemoteFileRepository.kt`, `PaymentsApi.kt`, `FileUploadApi.kt`, `ProofFilePicker.kt`/
`.android.kt`, or any backend route. The only "business logic"-adjacent file touched is
`CheckoutScreenModels.kt`, and only additively (three new state fields, populated from data
already fetched/held, zero new remote calls or control-flow changes).

## 10. Compile validation (Task 27)

`.\gradlew.bat :composeApp:compileKotlinDesktop` — **BUILD SUCCESSFUL** (7 actionable
tasks: 1 executed, 6 up-to-date) on the first run, zero compile errors across all four
changed files. `assembleDebug`/`test` were **not** run, per Task 27 (no compile failure
occurred that would justify the deeper gate).

## 11. Scope check

`git diff --stat` for this session: `CheckoutFlow.kt` (+6/-5 lines), `CheckoutScreenModels
.kt` (+15/-1 lines), `ConfirmationScreen.kt` (rewritten, +121/-… vs. prior), `PaymentScreen
.kt` (rewritten, +309/-… vs. prior) — exactly the four files this phase's task list names.
No file under `backend/`, `data/remote/`, `data/repository/`, `data/session/`, or any
Admin/Home/Catalog/Editor/Preview/Cart/Orders/Tracking/Profile/Auth screen was opened or
modified. Nothing committed.

## 12. Remaining / deferred (by design)

- **Customer Proof Detail screen** — deferred to Phase 9.6C (§6).
- **`PaymentStatusCard`'s dark `PremiumCard` shell** inside the now-light Confirmation
  scaffold — a visual mismatch, deliberately left unmodified because the component is
  shared with out-of-scope Admin/Tracking screens (§5). Worth revisiting in a phase that
  is allowed to touch those surfaces.
- **Admin confirm/reject UI** — out of scope, untouched, as instructed.
- **Not visually verified on a running emulator/device or desktop window** — this was a
  compile-and-static-review pass, consistent with "targeted validation only." Worth a
  visual sanity pass (especially the 5-state status chip contrast and the Confirmation
  badge colors) before shipping.

## Readiness

The customer-facing payment submission, real proof upload, duplicate-reference (409),
pending-state, and honest status-driven confirmation experience are now complete on top of
the pre-existing real backend integration, with no business-logic rewrites and no silent
mock fallbacks introduced. The worktree is ready to proceed to Phase 9.6C (Orders
List/Order Detail/Tracking/Customer Proof Detail). Nothing committed.

PHASE 9.6B: PASS
