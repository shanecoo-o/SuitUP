# SuitUP Admin Catalog API Integration

## Status

Prompt 23 connects the existing admin catalog list, create/edit form, and activate/deactivate actions to the authenticated Spring Boot admin catalog API. Checkout, orders, payments, tracking, dashboard metrics, and admin order/payment screens remain local.

## Data-Source Mode

Admin and customer catalog use the centralized `CatalogDataSourceConfig` mode:

```text
API_WITH_MOCK_FALLBACK
```

Admin behavior is intentionally asymmetric:

- listing prefers the API and may display `MockCatalogStore` models with a visible demo warning when the backend is unavailable;
- create, update, activate, and deactivate require a successful backend response;
- failed API mutations never modify local models or display a false success state;
- explicit `MOCK` mode remains available for a fully local demo.

## Endpoints

Implemented from `backend/API_CONTRACT.md` and `AdminCatalogController`:

- `GET /api/admin/suit-models`
- `GET /api/admin/suit-models/{id}` remains available in the remote layer
- `POST /api/admin/suit-models`
- `PUT /api/admin/suit-models/{id}`
- `PATCH /api/admin/suit-models/{id}/activate`
- `PATCH /api/admin/suit-models/{id}/deactivate`

All requests use the existing Ktor Bearer plugin and persistent `TokenStore`.

## Listing

`AdminCatalogScreenModel` loads active and inactive backend models through `AdminCatalogRepository`. The existing cards, local images, prices, categories, and status chips are preserved.

UI states include:

- `A carregar modelos...`;
- `Nenhum modelo cadastrado.`;
- `Não foi possível carregar os modelos.`;
- retry action;
- success/error notices;
- visible demo-fallback notice;
- disabled edit/status controls while a status request is active.

## Create and Update

`AdminSuitFormScreenModel` validates name, category, description, positive price, fabric, and color before submitting.

Create sends currency `MZN` and the selected local `imageKey`. Update preserves the backend UUID, currency, active state, and optional `primaryImageFileId`.

The form:

- blocks double submission;
- shows `A guardar...` while submitting;
- remains open when the backend rejects the operation;
- returns to the list only after success;
- leaves the success message in the shared admin catalog state.

Success messages:

- `Modelo criado com sucesso.`
- `Modelo actualizado com sucesso.`
- `Modelo activado.`
- `Modelo desactivado.`

## Authentication and Errors

Final `401` responses clear the persistent session and Ktor Bearer cache, then replace navigation with Login.

Mapped messages:

- `400`: `Dados inválidos. Verifique os campos.`
- `401`: `Sessão expirada. Faça login novamente.`
- `403`: `Sem permissão para gerir catálogo.`
- `404`: `Modelo não encontrado.`
- `409`: `Já existe um modelo com estes dados.`
- network: `Não foi possível ligar ao servidor.`
- other: `Erro inesperado. Tente novamente.`

Status-change failures use `Não foi possível alterar o estado do modelo.` where a more specific auth/not-found message does not apply.

## Customer Consistency

Successful admin API responses update the shared customer catalog snapshot using backend-returned models. Active models appear in customer Home/Catalog; deactivated models are removed. Customer screens also refresh the public endpoint when opened.

The API-mode path does not mutate `MockCatalogStore` as a source of truth.

## Images

The multipart endpoint already exists in `FileUploadApi`:

```text
POST /api/admin/suit-models/{id}/image
```

No real file picker exists in the current admin form, so upload UI is intentionally pending. Local PNG resource keys remain as safe placeholders, and updates preserve any existing `primaryImageFileId`.

## Validation

The Android debug Kotlin compilation passed after integration.

The admin write smoke test could not run because the previously configured backend became unreachable before admin login. No create/update/activate/deactivate request reached the backend, so no test model was created. The current configured URL is `http://192.168.168.57:8080`.

Physical Android interaction and backend-offline UI behavior remain manual validation items.

## Physical Android Checklist

1. Confirm `/api/health` returns `UP`.
2. Log in as `admin@suitup.local`.
3. Open Gestão do Catálogo and confirm active/inactive API models load.
4. Create `Modelo Teste Admin <timestamp>` with price `9999 MZN`.
5. Edit its description or color and confirm the update.
6. Log in as CUSTOMER and confirm the active model appears after refresh.
7. Deactivate it as ADMIN and confirm it disappears from the customer catalog.
8. Reactivate it and confirm it returns.
9. Stop the backend and confirm listing shows the demo fallback warning without crashing.
10. Confirm mutation attempts while offline show errors and do not change model state.

## Remaining Limitations

- Physical-device validation is pending.
- Admin image upload UI/file picker is pending.
- Admin dashboard metrics, orders, and payments remain local.
- Development transport remains trusted-LAN HTTP and requires HTTPS for production.

## Recommended Next Phase

Prompt 24 - Connect customer checkout, order creation, order history, and tracking to `RemoteOrderRepository` behind a safe data-source switch.
