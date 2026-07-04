# SuitUP Android Persistent Token Storage

## Implementation

Prompt 21 replaces process-only Android authentication storage with `AndroidEncryptedTokenStore`.

The Android implementation:

- stores the access and refresh tokens in private `SharedPreferences`;
- encrypts each value independently with AES-256-GCM;
- generates a new random IV for every write;
- binds each encrypted value to its preference key through authenticated additional data;
- keeps the non-exportable AES key in Android Keystore;
- clears unreadable or tampered session data;
- performs preference and cryptographic work on `Dispatchers.IO`;
- never logs or exposes token values.

`MainActivity` creates the Android store with the application context and injects it into the common `App`. The common `App` retains `InMemoryTokenStore` as its default for previews, tests, and non-Android entry points.

No token storage dependency was added to Gradle.

## Startup Session Validation

On application startup:

1. Splash asks `AuthSessionManager` to restore the session.
2. The manager reads the encrypted persisted tokens.
3. Without tokens, navigation opens Login.
4. With tokens, `/api/auth/me` validates the session and backend roles.
5. If the access token is rejected and a refresh token exists, the client attempts `/api/auth/refresh`.
6. Rotated tokens are encrypted and persisted.
7. The session is validated through `/api/auth/me` and routed as ADMIN or CUSTOMER.
8. Failed refresh/session validation clears persistent tokens and returns to Login.

When the backend is unavailable, the app does not trust the stale token to open protected UI. Login receives `Não foi possível ligar ao servidor.` and the persisted token remains available for a later validation attempt.

## Logout

Customer and admin logout actions:

- remove access and refresh tokens from encrypted preferences;
- clear the Ktor Bearer token cache;
- clear the in-memory authenticated session;
- replace navigation with Login.

After a successful logout, reopening the app must remain on Login.

## Backend Environment

The active backend remains centralized in `ApiConfig.current()`:

```text
http://192.168.168.41:8080
```

Other centralized options:

- Android emulator: `ApiConfig.androidEmulator()` -> `http://10.0.2.2:8080`
- physical device: `ApiConfig.current()` or `ApiConfig.physicalDevice(host)`
- desktop/local: `ApiConfig.desktop()` -> `http://localhost:8080`

## Development HTTP Policy

Android targets API 28 or newer, where cleartext traffic is disabled by default. A Network Security Configuration now denies cleartext globally and permits it only for these development hosts:

- `192.168.168.41`
- `10.0.2.2`
- `localhost`

Application backup is disabled so encrypted session preferences are not included in normal cloud backup. Production must use HTTPS and remove the local cleartext exceptions.

## Physical Android Checklist

### Fresh Login

1. Confirm `http://192.168.168.41:8080/api/health` returns `UP` from the phone network.
2. Clear SuitUP app data or install fresh.
3. Open the app and confirm it reaches Login.
4. Log in with `admin@suitup.local` and the configured admin password.
5. Confirm the backend-provided ADMIN role opens the admin area.

### Restart Persistence

1. Remove SuitUP from recent apps after a successful login.
2. Force-stop it from Android settings if desired.
3. Reopen SuitUP.
4. Confirm Splash validates `/api/auth/me` and returns to the admin area without credentials.

### Logout Persistence

1. Use the admin back/logout action or customer Profile -> Sair.
2. Confirm Login opens.
3. Force-stop and reopen SuitUP.
4. Confirm it remains on Login.

### Customer Persistence

1. Register a unique `cliente.demo.<timestamp>@suitup.local` account.
2. Confirm CUSTOMER opens the customer home.
3. Force-stop and reopen the app.
4. Confirm CUSTOMER home is restored after backend validation.

### Backend Offline

1. Log in successfully first, then close the app.
2. Stop the backend temporarily while keeping the phone network active.
3. Reopen SuitUP.
4. Confirm the app does not open protected UI and displays `Não foi possível ligar ao servidor.` on Login.
5. Restart the backend and reopen the app to retry the persisted session.

## Limitations and Production Recommendations

- Physical-device interaction must be completed manually on the target handset.
- The current backend uses development HTTP on a trusted LAN; it does not provide transport confidentiality.
- Production must use HTTPS with a valid certificate and remove the development cleartext domains.
- Android Keystore protection depends on the device security implementation and may be hardware-backed when supported.
- Desktop and preview sessions remain in memory only.
- Biometric or device-credential gating is not enabled for background session restoration.

## Recommended Next Phase

Prompt 22 - Connect the customer catalog to `RemoteCatalogRepository` behind an explicit mock/API data-source switch while preserving the local editor and checkout flow.
