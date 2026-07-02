# SuitUP Customer Catalog API Integration

## Status

Prompt 22 connects the customer Home featured models and Catalog/Select Model flow to the Spring Boot public catalog API. Editor, cart, checkout, orders, payments, tracking, and all admin data remain on their existing local stores.

## Data-Source Mode

The centralized mode is defined in `CatalogDataSourceConfig`:

```text
API_WITH_MOCK_FALLBACK
```

Available modes:

- `MOCK`: always use active models from `MockCatalogStore`;
- `API`: use the backend and show an error without fallback;
- `API_WITH_MOCK_FALLBACK`: prefer the backend and use active mock models when the request fails.

`CustomerCatalogRepository` owns the shared loading, source, model, error, and fallback state. Composables do not make HTTP requests.

## Backend

Active base URL:

```text
http://192.168.168.57:8080
```

Public endpoints used:

- `GET /api/suit-models`
- `GET /api/suit-models/{id}` is available in the remote repository and was smoke-tested, but list selection does not make a redundant detail request.

The online smoke test returned health `UP`, six active models, and a valid detail response for UUID `10000000-0000-0000-0000-000000000002`.

## Mapping

Backend `SuitModelDto` maps to the existing `SuitModel`, preserving:

- backend UUID;
- name, category, and description;
- price rounded to the current integer-MZN domain representation;
- currency;
- fabric and color;
- active state;
- local `imageKey`;
- optional `primaryImageFileId`.

`SuitModel.toModeloFato()` adapts the result to the existing customer cards and editor draft contract. The backend UUID is preserved as `ModeloFato.id`, then becomes `DesignFato.idModeloBase` in the existing local customization/cart flow.

Unknown or missing image keys continue to use the local classic-suit placeholder. No remote image download or backend file endpoint was added.

## UI Behavior

Home loads the shared customer catalog and shows API models in the existing featured-model cards.

Catalog provides:

- loading state: `A carregar catálogo...`;
- empty state: `Nenhum modelo disponível no momento.`;
- error state: `Não foi possível carregar o catálogo.`;
- retry action;
- a visible `modo demo` notice when mock fallback is active;
- existing category filters and model-card click behavior.

Selecting either an API or fallback model still calls `MockOrderStore.startDraft()` before opening the editor. Editor customization, preview, cart, and checkout behavior are unchanged.

## Offline Behavior

With `API_WITH_MOCK_FALLBACK`, network/server errors produce the Portuguese error notice and immediately expose active models from `MockCatalogStore`. Retry requests the API again. The remainder of the customer demo stays usable.

Physical-device offline behavior still needs manual validation on the target handset.

## Unchanged Modules

- `MockCatalogStore` remains available as customer/admin listing fallback; admin API-mode mutations do not use it as source of truth.
- `MockOrderStore` remains the editor draft, cart, checkout, payment, order, and tracking data source.
- Admin catalog is now connected to backend repositories; dashboard metrics, admin orders, and admin payments remain local.
- No backend source or database migration was changed.

## Physical Android Checklist

### Backend Online

1. Confirm `http://192.168.168.57:8080/api/health` returns `UP`.
2. Log in or register as CUSTOMER.
3. Open Home and confirm backend models appear under featured models.
4. Open Catálogo and confirm the active API models appear without the demo warning.
5. Select a model and confirm Editor opens with the selected model and UUID-backed draft.
6. Continue through Preview and confirm the existing mock flow remains functional.

### Backend Offline

1. Stop the backend temporarily after authentication or use an unavailable development host.
2. Open Catálogo.
3. Confirm the app does not crash.
4. Confirm `Não foi possível carregar o catálogo.` and the mode-demo notice appear.
5. Confirm local model selection still opens Editor.

### Admin Regression

1. Log in as ADMIN.
2. Confirm the admin area still opens.
3. Confirm admin catalog screens remain local/mock in this phase.

## Remaining Limitations

- Backend binary catalog images are not publicly loaded; local resource keys/placeholders are used.
- Current UI prices use integer MZN, so transport decimals are rounded.
- Admin catalog image upload UI is still pending.
- Physical-device online/offline interaction remains pending.

## Recommended Next Phase

Prompt 24 - Connect customer checkout, order creation, order history, and tracking to `RemoteOrderRepository` behind a safe data-source switch.
