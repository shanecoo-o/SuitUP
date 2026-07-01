# SuitUP Mobile Auth Integration

## Status

Prompt 20 connects the mobile Splash, Login, Registration, session restoration, Profile identity, and Logout flows to the Spring Boot authentication API. Catalog, cart, checkout, orders, payments, and admin business data continue to use their existing local mock stores.

## Backend Configuration

The active configuration is centralized in `ApiConfig.current()` and currently targets the physical-device LAN backend:

```text
http://192.168.168.41:8080
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

`AuthRuntime` owns one lazily created `SuitUpRemoteModule` and one session manager for the current app process.

## Token Behavior

Successful login and registration store access and refresh tokens in `TokenStore`. Logout and failed expired-session recovery clear both tokens and invalidate the Ktor bearer cache. The Ktor auth plugin can refresh an expired access token through `/api/auth/refresh`.

The current implementation remains `InMemoryTokenStore`. Tokens survive navigation but are lost when the application process ends. Encrypted persistent storage is intentionally still pending.

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

Validated against `http://192.168.168.41:8080`:

- health returned `UP`;
- wrong admin password returned HTTP `401`;
- admin login returned role `ADMIN`;
- `/api/auth/me` returned `admin@suitup.local`;
- refresh returned a new access/refresh token pair;
- unique customer registration returned role `CUSTOMER`;
- the registered customer could log in and received both tokens.

No token value was printed during validation.

## Modules Still Using Mocks

- `MockCatalogStore` remains active for customer and admin catalog screens.
- `MockOrderStore` remains active for cart, checkout, payments, orders, tracking, and admin order/payment screens.
- Admin dashboard metrics remain local/mock.

No backend catalog, order, payment, upload, or dashboard repository is wired to a screen in this phase.

## Remaining Limitations

- Authentication tokens are not persisted securely across process restarts.
- Social-login buttons remain visual only and do not bypass backend authentication.
- Physical-device UI interaction must still be exercised on the Android handset.
- Offline startup with an in-memory token cannot be reproduced after a process restart because the token no longer exists.

## Recommended Next Phase

Prompt 21 - Add secure persistent multiplatform token storage and authenticated app startup validation on a physical Android device.
