# SuitUP Mobile Admin Operations Integration Report

## Scope

Prompt 26 connects the existing admin dashboard, order operations, and payment operations to the prepared Spring Boot remote layer. Prompt 25 working-tree changes were preserved. No backend, Gradle, manifest, MainActivity, customer navigation, or admin catalog behavior was rewritten.

## Configuration

- Backend URL activo no Prompt 27: `http://192.168.168.60:8080`
- Initial health check: `{"status":"UP","service":"suitup-backend"}`
- Central mode: `AdminDataSourceMode.API_WITH_MOCK_FALLBACK`
- Runtime: `AdminOperationsRuntime`
- Authentication: existing persistent bearer-token session

## Connected Endpoints

Dashboard:

- `GET /api/admin/dashboard`

Orders:

- `GET /api/admin/orders`
- `GET /api/admin/orders/{id}`
- `GET /api/admin/orders/{id}/timeline`
- `PATCH /api/admin/orders/{id}/status`

Payments:

- `GET /api/admin/payments`
- `GET /api/admin/payments/pending`
- `GET /api/admin/payments/{id}`
- `GET /api/admin/payments/{id}/timeline`
- `PATCH /api/admin/payments/{id}/confirm`
- `PATCH /api/admin/payments/{id}/reject`

The UI actively uses dashboard, order list/detail/timeline/status, payment list, confirm, and reject. Payment detail/timeline repository methods are prepared; there is no separate payment-detail screen in the existing UI, so payment actions continue to open the associated order detail.

## Dashboard

The dashboard now loads backend totals, active/inactive models, pending/confirmed/rejected payment counts, confirmed revenue, orders by status, and recent order summaries. Loading, retry, session expiry, backend error, and visibly labelled mock fallback states are supported.

## Admin Orders

The order list now loads backend `Pedido` models with customer, server status, payment status, total, and creation date. The empty message is `Nenhum pedido encontrado.`

Order detail loads backend customer, suit/customization data, measurements, delivery/pickup, totals, payment status, proof file ID, and the dedicated admin timeline. Nullable fields retain safe defaults.

Exact backend statuses are preserved in `Pedido.backendStatus` and mapped to:

- `RECEIVED`
- `IN_ANALYSIS`
- `MEASUREMENTS_CONFIRMED`
- `IN_PRODUCTION`
- `READY_FOR_DELIVERY`
- `DELIVERED`
- `CANCELLED`

Only backend-allowed next transitions are offered. `IN_PRODUCTION` remains hidden until payment status is `CONFIRMED`. Writes show a pending state and update the UI only after the backend response. Success text is `Estado do pedido actualizado.`

## Admin Payments

The payments screen now uses backend `PaymentRecord` entries joined with backend orders for customer/order context. It shows reference, method, amount, currency-compatible MZN formatting, status, associated order, proof file ID, and submitted state.

Confirmation calls the backend payment ID, not the order ID. Success text is `Pagamento confirmado com sucesso.` Rejection requires a non-blank reason and displays `Pagamento rejeitado.` after backend success. Failed writes never mutate mock or remote UI state as success.

Backend payment enums remain mapped to `PENDING`, `CONFIRMED`, and `REJECTED` through the existing remote mapper.

## Proof Access

The order/payment views display the backend proof file ID. `RemoteFileRepository.download(fileId)` already supports authenticated `GET /api/files/{fileId}`. Android preview/open behavior is intentionally pending because the previous UI had no proof action and the consolidated device validation is reserved for Prompt 27. Raw filesystem paths are never shown.

## Errors And Session

Admin operations map:

- 400 -> `Dados inválidos. Verifique as informações.`
- 401 -> `Sessão expirada. Faça login novamente.`
- 403 -> `Sem permissão para executar esta acção.`
- 404 -> `Registo não encontrado.`
- 409 -> `Esta acção não é permitida no estado actual.`
- network -> `Não foi possível ligar ao servidor.`
- unknown -> `Erro inesperado. Tente novamente.`

A 401 uses the existing logout and Login replacement flow.

## Mock Fallback

Dashboard, order, timeline, and payment reads may use existing local demo data only for network/server/unknown failures. The UI labels this mode and disables writes. API-with-fallback writes never mutate mocks after a backend failure. Explicit `MOCK` mode remains available centrally for demos.

## Files Created

- `composeApp/src/commonMain/kotlin/com/suitup/app/data/admin/AdminOperationsRepository.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/admin/AdminOperationsScreenModels.kt`
- `MOBILE_ADMIN_OPERATIONS_INTEGRATION_REPORT.md`

## Files Changed

- `composeApp/src/commonMain/kotlin/com/suitup/app/App.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/domain/model/Models.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/mapper/RemoteMappers.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/repository/remote/RemoteOrderRepository.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/repository/remote/RemotePaymentRepository.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/AdminScreens.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/admin/AdminScreens.kt`

## Prompt 27 Validation Status

- Static diff and contract checks: passed. Backend, Gradle, Manifest, and MainActivity have no Prompt 25/26 diff.
- Backend health: passed at `http://192.168.168.60:8080/api/health`.
- Build: passed, including a full `assembleDebug --rerun-tasks`.
- Tests: passed; Android unit-test tasks are `NO-SOURCE`.
- Admin authentication and payment-list access reached the backend during API diagnosis.
- Dashboard metrics, unambiguous order/payment detail selection, confirmation, rejection, and status mutation were not completed in the final smoke.
- Physical Android and offline UI validation remain blocked because no ADB device was connected.
- The repository is not yet safe to commit under the mandatory Prompt 27 gate.

## Remaining Limitations

- Proof preview/open UI is prepared at repository/data level but not exposed as an Android viewer.
- There is no standalone admin payment-detail screen; associated order detail supplies the operational context.
- Final compile and runtime verification are intentionally pending.

## Next Prompt

Prompt 27 - Run the final consolidated build, automated tests, backend API smoke tests, physical Android customer/admin walkthrough, offline/error validation, and produce the final integration readiness report without changing architecture unless a verified defect requires a focused fix.
