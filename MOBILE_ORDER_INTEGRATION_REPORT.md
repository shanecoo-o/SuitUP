# SuitUP Mobile Order Integration Report

## Scope

Prompt 24 connects authenticated customer order creation, order history, and order detail to the Spring Boot API while retaining the existing editor, cart, checkout UI, payment demo step, Voyager navigation, and `MockOrderStore`.

## Configuration

- Backend URL: `http://192.168.168.57:8080`
- Central mode: `OrderDataSourceMode.API_WITH_MOCK_FALLBACK`
- Mode owner: `OrderDataSourceConfig` in `CustomerOrderRepository.kt`
- Authentication: existing bearer token and persistent `TokenStore`

## Endpoints

- `POST /api/orders`: wired to checkout submission.
- `GET /api/orders/my`: wired to customer history, Home recent orders, and Profile count.
- `GET /api/orders/{id}`: wired to customer order detail.
- `GET /api/orders/{id}/timeline`: API and repository method available; the dedicated endpoint is not called by the tracking screen in this phase. Detail uses status history returned by the order response.

Admin order, admin payment, dashboard, payment submission, and proof upload endpoints were not connected.

## Checkout Mapping

The request is built outside UI code from the current `MockOrderStore` checkout/cart state:

- backend suit model UUID from `DesignFato.idModeloBase`;
- fabric, color, quantity, and a JSON design snapshot;
- required and optional measurements converted from form strings;
- authenticated customer name, phone, and optional email;
- `DELIVERY` with composed address or `PICKUP` with pickup location;
- measurement observations as notes when present.

The backend derives customer identity from the bearer token. The mobile does not send a locally invented user ID.

## Idempotency And Submission

- A `suitup-mobile-...` idempotency key is generated per checkout attempt and sent in the `Idempotency-Key` header.
- Double taps are blocked by the submitting state and repository mutex.
- The key is retained after a failed unchanged attempt, matching backend replay semantics.
- Editing delivery input resets the key and creates a new logical attempt.
- Cart, draft, and checkout state are cleared only after API success.

## Totals And Confirmation

Pre-submit cart totals remain estimates. After creation, the mapped `Pedido` uses backend subtotal, delivery fee, total, currency-compatible MZN values, status, payment status, and order number. The payment and confirmation screens reuse the created backend order ID and server total; they do not create a second order.

Payment proof submission remains a local visual simulation for Prompt 25 and is labelled accordingly on confirmation.

## History And Detail

The order list fetches `/api/orders/my` with loading, empty, error, retry, and session-expiry handling. The empty message is `Ainda não existem pedidos.` and the error message is `Não foi possível carregar os pedidos.`

Order detail fetches `/api/orders/{id}` and safely maps nullable customer, measurements, delivery/pickup, payment, item, and history data to the existing tracking layout. Known editor values in `designSnapshot` are mapped back to suit options; the detail shows creation/update dates, model, fabric, color, lapel, buttons, measurements, subtotal, delivery fee, and server total when available.

## Errors And Session

Portuguese messages distinguish unauthorized, forbidden, validation, network, not-found/conflict, server, and unknown errors. A 401 triggers the existing sign-out/session reset path. Failed API creation leaves the cart and checkout draft untouched.

## Mock Fallback

`MockOrderStore` remains available. Read failures may show local orders with an explicit demo banner. Create failure never silently reports success: the user must explicitly select `Continuar em modo demo`, and confirmation marks that the order was not sent to the server. Backend and mock lists are not merged.

## Files Changed For Prompt 24

- `composeApp/src/commonMain/kotlin/com/suitup/app/data/order/CustomerOrderRepository.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/mock/MockOrderStore.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/mapper/RemoteMappers.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/data/remote/config/ApiConfig.kt`
- `composeApp/src/androidMain/res/xml/network_security_config.xml`
- `composeApp/src/commonMain/kotlin/com/suitup/app/App.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/checkout/CheckoutScreenModels.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/checkout/AddressScreen.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/checkout/ConfirmationScreen.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/CheckoutFlow.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/orders/OrdersScreenModels.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/orders/OrdersListScreen.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/orders/TrackOrderScreen.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/OrdersScreens.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/home/HomeScreenModel.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/screens/profile/PerfilScreenModel.kt`
- `composeApp/src/commonMain/kotlin/com/suitup/app/ui/navigation/TabRootScreens.kt`

The working tree also contains uncommitted Prompt 22/23 catalog integration files; they are not part of the Prompt 24 order scope.

## Validation Status

- Static review: completed; checkout method placement and duplicate-order issue corrected.
- `./gradlew.bat :composeApp:assembleDebug --no-daemon --console=plain`: passed with exit code 0.
- APK: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`.
- `./gradlew.bat test --no-daemon --console=plain`: passed with exit code 0. Mobile test tasks with no sources are accepted by the prompt.
- Backend health: `UP` at `http://192.168.168.57:8080/api/health`.
- Backend smoke order: `83271d63-3959-44db-84c2-42228128ff85`, number `SU-2026-FB12AC2E`, status `RECEIVED`, payment `PENDING`.
- Idempotency: replaying the same payload and key returned the same order ID.
- Server totals: subtotal `9500.00`, delivery fee `150.00`, total `9650.00`, currency `MZN`.
- History/detail: the order appeared in `/api/orders/my`; `/api/orders/{id}` returned the matching order with one item.
- Physical Android validation: pending because `adb devices -l` returned no connected devices.
- Backend-offline physical validation: pending for the same reason; the implemented path preserves state and exposes explicit demo fallback.

## Remaining Limitations

- Payment creation and proof upload are not connected.
- The dedicated timeline endpoint is prepared but not consumed by the tracking screen.
- Physical-device UI checkout, history/detail, rapid-tap, and offline scenarios require a connected Android device.
- Admin orders, payments, and dashboard remain unchanged.

## Next Prompt

Prompt 25 - Connect customer payment submission and secure proof upload to the backend, then wire the dedicated customer tracking timeline endpoint without changing admin order/payment flows.
