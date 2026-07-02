# SuitUP Mobile Auth Integration

## Status

Prompt 20 connects the mobile Splash, Login, Registration, session restoration, Profile identity, and Logout flows to the Spring Boot authentication API. Catalog, cart, checkout, orders, payments, and admin business data continue to use their existing local mock stores.

## Backend Configuration

The active configuration is centralized in `ApiConfig.current()` and currently targets the physical-device LAN backend:

```text
http://192.168.168.57:8080
```

Available alternatives remain centralized:

- Android emulator: `ApiConfig.androidEmulator()` -> `http://10.0.2.2:8080`
- Physical device: `ApiConfig.current()` or `ApiConfig.physicalDevice(host)`
- Desktop/local: `ApiConfig.desktop()` -> `http://localhost:8080`

Changing environments does not require editing screens or repositories.

## Session Architecture

`AuthSessionManager` is the app-facing session controller. It uses the existing `RemoteAuthRepository`, `AuthApi`, `TokenStore`, DTOs, `ApiResult`, and shared Ktor client.

It provides:

- login;
- registration;
- current-user lookup through `/api/auth/me`;
- explicit token refresh;
- startup session restoration;
- logout and token clearing;
- observable authenticated, unauthenticated, and checking states.

`AuthRuntime` owns one `SuitUpRemoteModule` and session manager initialized with the platform token store at app startup.

## Token Behavior

Successful login and registration store access and refresh tokens in `TokenStore`. Logout and failed expired-session recovery clear both tokens and invalidate the Ktor bearer cache. The Ktor auth plugin can refresh an expired access token through `/api/auth/refresh`.

Android now uses `AndroidEncryptedTokenStore`, backed by AES-256-GCM and Android Keystore. Previews and non-Android entry points retain `InMemoryTokenStore`. See `MOBILE_TOKEN_STORAGE_REPORT.md` for the storage and physical-device validation details.

Tokens and passwords are not logged by the mobile code.

## Navigation and Roles

Roles are mapped from the backend response:

- `ADMIN` -> existing admin dashboard;
- `CUSTOMER` -> existing customer main shell.

The previous local admin bypass was removed. The standard Login action now handles both roles, and the destination is selected only from backend-provided roles. Registration automatically stores the returned session and opens the customer shell for the backend-created CUSTOMER account.

Splash checks the current in-memory token state and calls `/api/auth/me`. An unauthorized session with a refresh token attempts refresh before returning to Login. Profile shows the authenticated backend user while cart and order counters remain local.

## UI Error Handling

Auth errors are presented in Portuguese:

- invalid credentials;
- unavailable server or timeout;
- expired session;
- forbidden account;
- invalid form data;
- duplicate email;
- missing endpoint;
- server and unknown errors.

Login and Registration disable submission while a request is running.

## Backend Smoke Test

Validated previously against the then-active backend; the current configured URL is `http://192.168.168.57:8080`:

- health returned `UP`;
- wrong admin password returned HTTP `401`;
- admin login returned role `ADMIN`;
- `/api/auth/me` returned `admin@suitup.local`;
- refresh returned a new access/refresh token pair;
- unique customer registration returned role `CUSTOMER`;
- the registered customer could log in and received both tokens.

No token value was printed during validation.

## Modules Still Using Mocks

- Customer and admin catalog now prefer `RemoteCatalogRepository` with explicit mock listing fallback.
- `MockOrderStore` remains active for cart, checkout, payments, orders, tracking, and admin order/payment screens.
- Admin dashboard metrics remain local/mock.

No backend order, payment, image-upload UI, or dashboard repository is wired to a screen yet.

## Remaining Limitations

- Physical-device restart and logout persistence still require manual validation on the target handset.
- Social-login buttons remain visual only and do not bypass backend authentication.
- Physical-device UI interaction must still be exercised on the Android handset.
- Development traffic still uses HTTP on the trusted LAN and must move to HTTPS for production.

## Recommended Next Phase

Prompt 24 - Connect customer checkout, order creation, order history, and tracking to `RemoteOrderRepository` behind a safe data-source switch.
