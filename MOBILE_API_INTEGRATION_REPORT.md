# SuitUP Mobile API Infrastructure

## Status

Prompt 19 added the Ktor-based remote data layer. Auth and customer/admin catalog now use the backend through controlled source modes. Prompt 24 adds backend-first customer order creation/history/detail while preserving local editor/cart/checkout draft state and explicit mock fallback. Payments and admin dashboard/order/payment data remain local.

## Dependencies

The project already used Ktor 3.0.0, kotlinx.serialization, and Android/iOS/Desktop engines. This phase adds:

- `ktor-client-auth`
- `ktor-client-logging`

The shared client also uses the existing content-negotiation and kotlinx-json modules.

## Package Structure

```text
com.suitup.app.data.remote
|-- config       base URL and environment configuration
|-- http         HttpClient factory, JSON, ApiResult, typed errors
|-- auth         auth DTOs/API and TokenStore
|-- catalog      catalog DTOs and public/admin API
|-- orders       checkout/order DTOs and customer/admin API
|-- payments     payment DTOs and customer/admin API
|-- dashboard    dashboard DTOs and ADMIN API
|-- upload       multipart upload, download, and file DTOs
`-- SuitUpRemoteModule.kt

com.suitup.app.data.repository.remote
|-- RemoteAuthRepository
|-- RemoteCatalogRepository
|-- RemoteOrderRepository
|-- RemotePaymentRepository
|-- RemoteAdminRepository
`-- RemoteFileRepository

com.suitup.app.data.mapper
`-- RemoteMappers.kt
```

## Base URL

The single configuration point is `ApiConfig`.

```kotlin
val emulator = ApiConfig.androidEmulator()
val desktop = ApiConfig.desktop()
val physicalDevice = ApiConfig.physicalDevice("192.168.1.50")
```

Defaults:

- Android emulator: `http://10.0.2.2:8080`
- Desktop JVM: `http://localhost:8080`
- Physical device: use the backend PC LAN IPv4 address and port `8080`

For a physical Android device, the phone and backend PC must be on the same network. Windows Firewall may need an inbound rule allowing TCP port `8080`. The backend must listen on an interface reachable from the LAN; `localhost` on the phone refers to the phone itself and must not be used.

## HTTP Behavior

- JSON uses `ignoreUnknownKeys`, lenient parsing, and encoded defaults.
- Request, connect, and socket timeouts are configured centrally.
- Logging uses `INFO` and redacts authorization headers. Password and token bodies are not logged.
- Bearer access tokens are loaded through `TokenStore`.
- A `401` can trigger `/api/auth/refresh`; successful refresh rotates and stores both tokens.
- Non-2xx responses are converted to typed `ApiError` values.
- Network and cancellation behavior does not force UI crashes.

## Token Storage

`TokenStore` abstracts access, refresh, save, and clear operations. The current implementation is `InMemoryTokenStore`, protected by a coroutine mutex.

Tokens disappear when the application process ends. Persistent encrypted Android/iOS/Desktop storage is intentionally deferred to Prompt 20. No token or backend secret is hardcoded.

## DTO and Mapping Coverage

Serializable DTOs match the backend contract for:

- registration, login, refresh, and current user;
- public/admin catalog;
- checkout, measurements, order items, timelines, and admin status updates;
- payment submission, attempts, timelines, confirmation, rejection, and proof metadata;
- ADMIN dashboard metrics and recent summaries;
- generic private file upload/download, payment proof upload, and suit-model image upload.

UUID and timestamp values remain strings. Backend decimal numbers use `Double` at the transport boundary because current mobile domain prices are integer MZN. Mappers round transport values to integer meticais when converting to existing domain models. A future fractional-currency requirement should introduce a multiplatform fixed-decimal money type before screen wiring.

Catalog DTOs map to `SuitModel`; auth maps to `Utilizador`; orders map to the existing `Pedido`; payments map to `PaymentRecord`; dashboard maps to `AdminDashboardSummary`.

## Multipart Upload

`UploadFilePayload` provides a platform-neutral file boundary:

```kotlin
val payload = UploadFilePayload(
    filename = "comprovativo.png",
    contentType = "image/png",
    bytes = fileBytes,
)
```

The API layer supports:

- `POST /api/files/upload`
- `GET /api/files/{fileId}`
- `POST /api/orders/{orderId}/payment/proof`
- `POST /api/admin/suit-models/{id}/image`

Android file-picker and URI-to-byte conversion are not included in this phase.

## Composition and Current App Behavior

`SuitUpRemoteModule` constructs the client, APIs, token store, and remote repositories. The application root initializes it with the platform token store. Auth uses it directly, while customer catalog access is isolated behind `CustomerCatalogRepository` and its fallback mode.

Current local defaults remain for `MockOrderStore` and admin dashboard/order/payment data. `MockCatalogStore` remains an explicit listing fallback. Voyager navigation and the local editor/checkout flow remain intact.

## Next Phase

The recommended next prompt is to connect Login, Registration, session restoration, and logout to `RemoteAuthRepository` behind an explicit data-source switch. Catalog and order screens should remain on mocks until authentication and secure persistent token storage are verified on both emulator and physical Android hardware.
