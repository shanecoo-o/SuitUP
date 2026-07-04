# SuitUP Mobile Payment And Tracking Integration Report

## Scope

Prompt 25 connects customer payment submission, physical payment-proof upload, and the dedicated customer order timeline to the existing Spring Boot backend. Admin orders, admin payment review, and dashboard metrics remain unchanged.

## Configuration

- Backend URL activo no Prompt 27: `http://192.168.168.60:8080`
- Health result: `{"status":"UP","service":"suitup-backend"}`
- Central mode: `PaymentTrackingDataSourceMode.API_WITH_MOCK_FALLBACK`
- Runtime owner: `PaymentTrackingRuntime`
- Authentication: existing persistent bearer-token flow

## Connected Endpoints

- `POST /api/orders/{orderId}/payment`
- `GET /api/orders/{orderId}/payment`
- `GET /api/orders/{orderId}/payments`
- `GET /api/orders/{orderId}/payment-timeline`
- `POST /api/orders/{orderId}/payment/proof`
- `GET /api/orders/{orderId}/timeline`
- `GET /api/files/{fileId}` remains available through `RemoteFileRepository`

The backward-compatible `POST /api/orders/{orderId}/payment-proof-metadata` method remains available in the existing remote repository but is not used by the active Android flow because physical bytes are available.

## Payment Flow

The payment screen now requires a transaction reference and a selected proof file. It sends `MPESA`, the backend order ID, the server total in MZN, the reference, and a note. The backend payment status is the source of truth.

Double submission is blocked by the loading state and a repository mutex. HTTP 409 maps to `Esta referência de pagamento já foi utilizada.` A successful payment displays `Pagamento submetido com sucesso.`

Payment and proof are intentionally separate operations. If payment succeeds but upload fails, the screen retains the submitted state and retries only the proof upload, avoiding a duplicate payment request.

## Physical Proof Upload

Android uses `ActivityResultContracts.OpenDocument` without changing `MainActivity`. The picker accepts PNG, JPEG, and PDF, reads bytes through `ContentResolver`, keeps no local path, and rejects known files larger than 10 MB before loading them into memory. The repository validates name, MIME type, non-empty bytes, and size again before multipart upload.

On success the screen displays `Comprovativo enviado com sucesso.` The uploaded file ID is associated with the latest pending backend payment. iOS and Desktop actuals currently return a clear unsupported-platform message.

## Payment Status Mapping

The backend contract currently exposes:

- `PENDING` -> `Pendente`
- `CONFIRMED` -> `Confirmado`
- `REJECTED` -> `Rejeitado`

The backend has no separate `SUBMITTED` enum. Submission is communicated by the success message while status remains `PENDING` until future admin confirmation.

## Tracking

`AcompanharPedidoScreenModel` loads the order detail and `/api/orders/{id}/timeline`. The visual timeline uses only returned history events and does not synthesize future production stages.

Mappings are exact for `RECEIVED`, `IN_ANALYSIS`, `MEASUREMENTS_CONFIRMED`, `IN_PRODUCTION`, `READY_FOR_DELIVERY`, `DELIVERED`, and `CANCELLED`. Loading, empty, error, retry, session expiry, and explicit local-demo fallback states are supported. The empty message is `Ainda não existe histórico para este pedido.`

## Errors And Fallback

The customer receives dedicated Portuguese messages for 401, 403, 404, 409, validation, network, and unknown failures. A 401 uses the existing sign-out flow.

Payment/proof failures never mark a remote payment as successful. Explicit mock payment fallback is offered only when the order actually exists in `MockOrderStore`. Timeline read fallback may show a matching local timeline with `A mostrar dados locais em modo demo.` Backend and local histories are not merged.

## Files Changed

- `composeApp/src/commonMain/kotlin/com/suitup/app/data/payment/PaymentTrackingRepository.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/mock/MockOrderStore.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/remote/config/ApiConfig.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/App.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/platform/ProofFilePicker.kt`
- `composeApp/src/androidMain/kotlin/com/suitup/app/ui/platform/ProofFilePicker.android.kt`
- `composeApp/src/iosMain/kotlin/com/suitup/app/ui/platform/ProofFilePicker.ios.kt`
- `composeApp/src/desktopMain/kotlin/com/suitup/app/ui/platform/ProofFilePicker.desktop.kt`
- `composeApp/src/androidMain/res/xml/network_security_config.xml`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/checkout/CheckoutScreenModels.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/checkout/PaymentScreen.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/checkout/ConfirmationScreen.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/CheckoutFlow.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/orders/OrdersScreenModels.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/orders/TrackOrderScreen.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/OrdersScreens.kt`

No backend, Gradle, manifest, MainActivity, or admin screen/repository file was changed.

## Prompt 27 Validation

The final local build gates passed. The active backend became reachable during validation and accepted real customer registration, order creation, payment submission, and multipart PNG proof upload. Physical Android validation could not run because `adb devices -l` returned no connected device.

- `:composeApp:assembleDebug --rerun-tasks`: passed after fixing the missing `PaymentStatus` import.
- Final incremental `:composeApp:assembleDebug`: passed after synchronously locking payment/admin submit actions against double taps.
- `test --rerun-tasks`: passed; Android unit-test tasks are `NO-SOURCE`.
- Final incremental `test`: passed.
- APK: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`.
- API payment: order `SU-2026-BBC0DEC5`, payment `7813eaa4-e4af-4660-add0-86332d07e771`, `PENDING`, `9650.00 MZN`.
- Duplicate reference: second order returned HTTP 409.
- Physical upload: PNG stored as `f2570117-8b43-4388-b594-435ea0b83d7a`; uploaded and downloaded sizes both 68 bytes; latest payment referenced the same file ID.
- Payment attempts: 1; payment timeline events: 1.
- Order timeline: 1 real event, `RECEIVED`; no fake production events.
- Physical Android: APK installed successfully on Samsung `SM_A165F`; `MainActivity` resumed and no fatal crash appeared in launch logs.
- Prompt 27 API smoke created a new real customer, server-priced order, `PENDING` M-Pesa payment, and multipart PNG proof.
- Prompt 27 proof download through `GET /api/files/{fileId}` returned HTTP 500 after the upload succeeded. Backend storage/download requires investigation.
- Prompt 27 duplicate-reference, customer refresh, and full customer-admin-customer mutation chain were not completed.
- Full physical UI checkout/payment/file-picker walkthrough remains blocked because no ADB device was connected.
- Backend-offline UI walkthrough remains blocked without a connected device; static/runtime repository rules still prevent false remote payment success.

## Remaining Limitations

- Prompt 27 admin confirmation/rejection was not proven against the new smoke payment.
- Physical proof download currently returns HTTP 500 from the backend despite successful upload.
- iOS/Desktop do not yet provide native document pickers.
- Full touch-driven physical validation and backend-offline UI validation remain manual.

## Next Prompt

Prompt 26 - Connect admin orders, payment confirmation/rejection, and admin dashboard metrics to the backend while preserving the validated customer payment, proof, and tracking flows.
