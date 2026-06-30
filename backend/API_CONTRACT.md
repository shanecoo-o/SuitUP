# SuitUP Backend API Contract

Human-readable OpenAPI-style contract for the currently implemented Spring Boot API.

## Conventions

- Base URL for local development: `http://localhost:8080`
- Media type: `application/json`
- Authenticated requests: `Authorization: Bearer <access_token>`
- Access token lifetime: 15 minutes by default.
- Refresh token lifetime: 14 days by default.
- Dates: ISO-8601 UTC timestamps.
- Money: decimal values in `MZN`.
- Customer-owned resources return `404` when the resource does not exist or belongs to another customer.

### Roles

- `CUSTOMER`: customer catalog, order, checkout, and payment operations.
- `ADMIN`: all customer permissions plus `/api/admin/**` operations.

### Success and error status codes

- `200 OK`: successful read or update.
- `201 Created`: successful registration or resource creation.
- `400 Bad Request`: validation, malformed JSON/path parameters, amount mismatch, or invalid state transition.
- `401 Unauthorized`: missing, invalid, or expired access token; invalid login.
- `403 Forbidden`: authenticated user lacks the required role.
- `404 Not Found`: resource missing, inactive public catalog model, or customer ownership check failed.
- `409 Conflict`: duplicate email, transaction reference, idempotency conflict, or database uniqueness conflict.
- `413 Payload Too Large`: multipart file exceeds 10 MB.

Structured error example:

```json
{
  "timestamp": "2026-06-29T10:00:00Z",
  "status": 400,
  "error": "VALIDATION_FAILED",
  "message": "Existem campos inválidos no pedido",
  "path": "/api/orders",
  "fieldErrors": {
    "items": "must not be empty"
  }
}
```

## 1. Health

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| GET | `/api/health` | Public | Service readiness check | None | Health example below | `500` unexpected error |

```json
{
  "status": "UP",
  "service": "suitup-backend"
}
```

## 2. Authentication

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| POST | `/api/auth/register` | Public | Create a CUSTOMER account | Register example | `201`, Auth response | `400`, `409` duplicate email |
| POST | `/api/auth/login` | Public | Authenticate enabled user | Login example | `200`, Auth response | `400`, `401` invalid credentials |
| POST | `/api/auth/refresh` | Public | Rotate access and refresh tokens | Refresh example | `200`, Auth response | `400`, `401` invalid/expired/wrong token type |
| GET | `/api/auth/me` | Bearer, CUSTOMER or ADMIN | Read current identity | None | `200`, Current user response | `401` |

Register request:

```json
{
  "fullName": "Joao Cliente",
  "email": "joao@example.com",
  "phone": "+258840000001",
  "password": "Password123!"
}
```

Login request:

```json
{
  "email": "joao@example.com",
  "password": "Password123!"
}
```

Refresh request:

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh-token-value"
}
```

Auth response:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.access-token-value",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh-token-value",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "user": {
    "id": "20000000-0000-0000-0000-000000000001",
    "fullName": "Joao Cliente",
    "email": "joao@example.com",
    "phone": "+258840000001",
    "enabled": true,
    "roles": ["CUSTOMER"],
    "createdAt": "2026-06-29T10:00:00Z",
    "updatedAt": "2026-06-29T10:00:00Z"
  }
}
```

`GET /api/auth/me` returns only the nested `user` object shape. Public registration always assigns `CUSTOMER`; it cannot create an ADMIN.

## 3. Public Catalog

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| GET | `/api/suit-models` | Public | List active suit models | None | `200`, array of Suit model responses | `500` |
| GET | `/api/suit-models/{id}` | Public | Read one active suit model | UUID path | `200`, Suit model response | `400` invalid UUID, `404` missing/inactive |

Suit model response:

```json
{
  "id": "10000000-0000-0000-0000-000000000001",
  "name": "Fato Clássico Preto",
  "category": "Clássico",
  "description": "Corte clássico para eventos formais.",
  "price": 8500.00,
  "currency": "MZN",
  "fabricType": "Lã Premium",
  "color": "Preto",
  "imageKey": "suit_classic_black",
  "primaryImageFileId": null,
  "active": true,
  "createdAt": "2026-06-29T10:00:00Z",
  "updatedAt": "2026-06-29T10:00:00Z"
}
```

## 4. Admin Catalog

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| GET | `/api/admin/suit-models` | ADMIN | List active and inactive models | None | `200`, array of Suit model responses | `401`, `403` |
| GET | `/api/admin/suit-models/{id}` | ADMIN | Read any model | UUID path | `200`, Suit model response | `400`, `401`, `403`, `404` |
| POST | `/api/admin/suit-models` | ADMIN | Create model | Create model example | `201`, Suit model response | `400`, `401`, `403`, `409` |
| PUT | `/api/admin/suit-models/{id}` | ADMIN | Replace editable model fields | Update model example | `200`, Suit model response | `400`, `401`, `403`, `404`, `409` |
| PATCH | `/api/admin/suit-models/{id}/activate` | ADMIN | Activate model | None | `200`, Suit model response with `active=true` | `400`, `401`, `403`, `404` |
| PATCH | `/api/admin/suit-models/{id}/deactivate` | ADMIN | Deactivate model | None | `200`, Suit model response with `active=false` | `400`, `401`, `403`, `404` |
| POST | `/api/admin/suit-models/{id}/image` | ADMIN | Upload and link primary suit image | Multipart `file` (PNG/JPEG) | `201`, Stored file response | `400`, `401`, `403`, `404`, `413` |

Create model request (`currency` defaults to `MZN`, `active` defaults to `true`):

```json
{
  "name": "Fato Azul Executivo",
  "category": "Executivo",
  "description": "Fato azul-marinho para reuniões e cerimónias.",
  "price": 9500.00,
  "currency": "MZN",
  "fabricType": "Lã Premium",
  "color": "Azul Marinho",
  "imageKey": "suit_navy_business",
  "primaryImageFileId": null,
  "active": true
}
```

Update model request uses the same fields; `active` is required on update.

## 5. Customer Orders

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| POST | `/api/orders` | CUSTOMER or ADMIN | Create checkout order | Optional `Idempotency-Key`; Create order example | `201`, Order response | `400`, `401`, `403`, `404`, `409` |
| GET | `/api/orders/my` | CUSTOMER or ADMIN | List current user's orders | None | `200`, array of Order responses | `401`, `403` |
| GET | `/api/orders/{id}` | Owner or ADMIN | Read order detail | UUID path | `200`, Order response | `400`, `401`, `403`, `404` |
| GET | `/api/orders/{id}/timeline` | Owner or ADMIN | Read ordered production timeline | UUID path | `200`, array of Order status history responses | `400`, `401`, `403`, `404` |

Create order request:

```json
{
  "customerUserId": null,
  "customerName": "Joao Cliente",
  "customerPhone": "+258840000001",
  "customerEmail": "joao@example.com",
  "fulfillmentType": "DELIVERY",
  "deliveryAddress": "Av. Julius Nyerere, Maputo",
  "pickupLocation": null,
  "notes": "Entregar durante a tarde",
  "idempotencyKey": null,
  "items": [
    {
      "suitModelId": "10000000-0000-0000-0000-000000000001",
      "fabric": "Lã Premium",
      "color": "Preto",
      "designSnapshot": {
        "lapel": "classica",
        "buttons": 2
      },
      "quantity": 1
    }
  ],
  "measurement": {
    "heightCm": 178,
    "chestCm": 102,
    "waistCm": 88,
    "shouldersCm": 46,
    "sleeveCm": 64,
    "trouserLengthCm": 104,
    "neckCm": 40,
    "hipCm": 96,
    "notes": null
  }
}
```

For CUSTOMER tokens, `customerUserId` is ignored and replaced by the authenticated user ID. The `Idempotency-Key` header takes precedence over the legacy JSON field.

Order response:

```json
{
  "id": "30000000-0000-0000-0000-000000000001",
  "orderNumber": "SU-2026-ABC12345",
  "customerUserId": "20000000-0000-0000-0000-000000000001",
  "customerName": "Joao Cliente",
  "customerPhone": "+258840000001",
  "customerEmail": "joao@example.com",
  "status": "RECEIVED",
  "paymentStatus": "PENDING",
  "fulfillmentType": "DELIVERY",
  "deliveryAddress": "Av. Julius Nyerere, Maputo",
  "pickupLocation": null,
  "notes": "Entregar durante a tarde",
  "subtotalAmount": 8500.00,
  "deliveryFee": 150.00,
  "totalAmount": 8650.00,
  "currency": "MZN",
  "items": [
    {
      "id": "31000000-0000-0000-0000-000000000001",
      "suitModelId": "10000000-0000-0000-0000-000000000001",
      "suitName": "Fato Clássico Preto",
      "category": "Clássico",
      "fabric": "Lã Premium",
      "color": "Preto",
      "designSnapshot": {"lapel": "classica", "buttons": 2},
      "unitPrice": 8500.00,
      "quantity": 1,
      "lineTotal": 8500.00
    }
  ],
  "measurement": {
    "id": "32000000-0000-0000-0000-000000000001",
    "heightCm": 178,
    "chestCm": 102,
    "waistCm": 88,
    "shouldersCm": 46,
    "sleeveCm": 64,
    "trouserLengthCm": 104,
    "neckCm": 40,
    "hipCm": 96,
    "notes": null
  },
  "payments": [],
  "statusHistory": [
    {
      "id": "33000000-0000-0000-0000-000000000001",
      "oldStatus": null,
      "newStatus": "RECEIVED",
      "changedByUserId": "20000000-0000-0000-0000-000000000001",
      "note": "Pedido recebido",
      "createdAt": "2026-06-29T10:00:00Z"
    }
  ],
  "createdAt": "2026-06-29T10:00:00Z",
  "updatedAt": "2026-06-29T10:00:00Z"
}
```

## 6. Admin Orders

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| GET | `/api/admin/orders` | ADMIN | List all orders | None | `200`, array of Order responses | `401`, `403` |
| GET | `/api/admin/orders/{id}` | ADMIN | Read any order | UUID path | `200`, Order response | `400`, `401`, `403`, `404` |
| GET | `/api/admin/orders/{id}/timeline` | ADMIN | Read any order timeline | UUID path | `200`, array of Order status history responses | `400`, `401`, `403`, `404` |
| PATCH | `/api/admin/orders/{id}/status` | ADMIN | Advance/cancel production status | Status request example | `200`, Order response | `400`, `401`, `403`, `404` |

```json
{
  "status": "IN_ANALYSIS",
  "note": "Dados do cliente confirmados"
}
```

## 7. Customer Payments

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| POST | `/api/orders/{orderId}/payment` | Owner or ADMIN | Submit payment | Payment request example | `201`, Payment response | `400`, `401`, `403`, `404`, `409` |
| GET | `/api/orders/{orderId}/payment` | Owner or ADMIN | Read latest payment | UUID path | `200`, Payment response | `400`, `401`, `403`, `404` |
| GET | `/api/orders/{orderId}/payments` | Owner or ADMIN | List all attempts newest first | UUID path | `200`, array of Payment responses | `400`, `401`, `403`, `404` |
| GET | `/api/orders/{orderId}/payment-timeline` | Owner or ADMIN | Read latest payment timeline | UUID path | `200`, array of Payment status history responses | `400`, `401`, `403`, `404` |
| POST | `/api/orders/{orderId}/payment-proof-metadata` | Owner or ADMIN | Register proof metadata without bytes | Proof metadata example | `201`, Uploaded file metadata response | `400`, `401`, `403`, `404`, `409` |
| POST | `/api/orders/{orderId}/payment/proof` | Owner or ADMIN | Upload proof for latest pending payment | Multipart `file` | `201`, Stored file response | `400`, `401`, `403`, `404`, `413` |

Payment request:

```json
{
  "method": "MPESA",
  "amount": 8650.00,
  "transactionReference": "MPESA-123",
  "proofFileId": null,
  "note": "Pagamento submetido"
}
```

Payment response:

```json
{
  "id": "40000000-0000-0000-0000-000000000001",
  "orderId": "30000000-0000-0000-0000-000000000001",
  "method": "MPESA",
  "status": "PENDING",
  "amount": 8650.00,
  "currency": "MZN",
  "transactionReference": "MPESA-123",
  "proofFileId": null,
  "submittedAt": "2026-06-29T10:05:00Z",
  "confirmedAt": null,
  "rejectedAt": null,
  "reviewedByUserId": null,
  "rejectionReason": null,
  "statusHistory": [
    {
      "id": "41000000-0000-0000-0000-000000000001",
      "oldStatus": null,
      "newStatus": "PENDING",
      "changedByUserId": null,
      "note": "Pagamento submetido",
      "createdAt": "2026-06-29T10:05:00Z"
    }
  ],
  "createdAt": "2026-06-29T10:05:00Z",
  "updatedAt": "2026-06-29T10:05:00Z"
}
```

Proof metadata request:

```json
{
  "originalName": "comprovativo.png",
  "storedName": null,
  "contentType": "image/png",
  "sizeBytes": 204800,
  "storagePath": null,
  "publicUrl": null
}
```

Uploaded file metadata response:

```json
{
  "id": "42000000-0000-0000-0000-000000000001",
  "ownerUserId": "20000000-0000-0000-0000-000000000001",
  "purpose": "PAYMENT_PROOF",
  "originalName": "comprovativo.png",
  "storedName": "generated-comprovativo.png",
  "contentType": "image/png",
  "sizeBytes": 204800,
  "storagePath": "metadata://payment-proofs/generated-id",
  "publicUrl": null,
  "createdAt": "2026-06-29T10:04:00Z"
}
```

The metadata-only route remains for backward compatibility. The physical proof
route requires an existing pending payment and links the new `uploaded_files`
record through `payments.proof_file_id`.

## 8. Admin Payments

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| GET | `/api/admin/payments` | ADMIN | List all payments | None | `200`, array of Payment responses | `401`, `403` |
| GET | `/api/admin/payments/pending` | ADMIN | List pending payments oldest first | None | `200`, array of Payment responses | `401`, `403` |
| GET | `/api/admin/payments/{paymentId}` | ADMIN | Read payment detail | UUID path | `200`, Payment response | `400`, `401`, `403`, `404` |
| GET | `/api/admin/payments/{paymentId}/timeline` | ADMIN | Read ordered payment history | UUID path | `200`, array of Payment status history responses | `400`, `401`, `403`, `404` |
| PATCH | `/api/admin/payments/{paymentId}/confirm` | ADMIN | Confirm pending payment | Confirm request example | `200`, Payment response | `400`, `401`, `403`, `404` |
| PATCH | `/api/admin/payments/{paymentId}/reject` | ADMIN | Reject pending payment | Reject request example | `200`, Payment response | `400`, `401`, `403`, `404` |

Confirm request:

```json
{
  "note": "Referência validada"
}
```

Reject request:

```json
{
  "rejectionReason": "Comprovativo ilegível",
  "note": "Solicitar novo comprovativo"
}
```

Any client-supplied reviewer ID is ignored; the authenticated ADMIN becomes the reviewer.

## 9. Admin Dashboard

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| GET | `/api/admin/dashboard` | ADMIN | Aggregate operational metrics and recent activity | None | `200`, Dashboard response | `401`, `403`, `500` |

```json
{
  "totalOrders": 10,
  "activeSuitModels": 5,
  "inactiveSuitModels": 1,
  "pendingPayments": 2,
  "confirmedPayments": 6,
  "rejectedPayments": 2,
  "confirmedRevenue": 45000.00,
  "currency": "MZN",
  "ordersByStatus": {
    "RECEIVED": 3,
    "IN_ANALYSIS": 2,
    "MEASUREMENTS_CONFIRMED": 0,
    "IN_PRODUCTION": 1,
    "READY_FOR_DELIVERY": 1,
    "DELIVERED": 3,
    "CANCELLED": 0
  },
  "recentOrders": [],
  "recentPendingPayments": []
}
```

## 10. Private Files

| Method | Path | Auth | Purpose | Request | Success body | Common errors |
|---|---|---|---|---|---|---|
| POST | `/api/files/upload` | CUSTOMER or ADMIN | Store an owned file | Multipart `file`, `purpose` | `201`, Stored file response | `400`, `401`, `413` |
| GET | `/api/files/{fileId}` | Owner or ADMIN | Retrieve physical bytes | UUID path | `200`, binary body | `400`, `401`, `404` |

Allowed purposes are `SUIT_IMAGE`, `PAYMENT_PROOF`, `PROFILE`, and `OTHER`.
Allowed media types are `image/png`, `image/jpeg`, and `application/pdf` with a
10 MB maximum. `SUIT_IMAGE` and `PROFILE` reject PDF. Declared MIME type and
file signature must match. Empty files and path-like original filenames are
rejected.

Stored file response:

```json
{
  "fileId": "42000000-0000-0000-0000-000000000001",
  "originalFilename": "comprovativo.png",
  "contentType": "image/png",
  "sizeBytes": 204800,
  "purpose": "PAYMENT_PROOF",
  "createdAt": "2026-06-30T10:04:00Z",
  "url": "/api/files/42000000-0000-0000-0000-000000000001"
}
```

Physical responses never include `storedName` or `storagePath`. Image retrieval
uses inline content disposition; PDF retrieval uses attachment. Foreign and
missing file IDs both return `404`. Catalog image bytes are not public by
default even when `primaryImageFileId` appears in the public catalog response.

## Business Rules and Lifecycles

### Order status

```text
RECEIVED -> IN_ANALYSIS -> MEASUREMENTS_CONFIRMED -> IN_PRODUCTION
IN_PRODUCTION -> READY_FOR_DELIVERY -> DELIVERED
RECEIVED | IN_ANALYSIS | MEASUREMENTS_CONFIRMED | IN_PRODUCTION | READY_FOR_DELIVERY -> CANCELLED
```

`IN_PRODUCTION` requires `paymentStatus=CONFIRMED`.

### Payment status

```text
PENDING -> CONFIRMED
PENDING -> REJECTED
```

Only pending payments can be reviewed. Confirmation/rejection updates both payment and order payment status.

### Pricing and fulfillment

- Catalog prices are authoritative; client totals are ignored.
- Payment amount must exactly equal the order total.
- `DELIVERY` adds `150.00 MZN`.
- `PICKUP` adds `0.00 MZN`.
- Order items preserve suit, category, fabric, color, design, price, quantity, and line-total snapshots.

### Idempotency

- Header: `Idempotency-Key`, optional, maximum 150 characters.
- Stored for 24 hours.
- Same key and same payload replays the original order.
- Same key and different payload returns `409`.

### JWT behavior

- HS256 signed tokens.
- Access and refresh tokens carry distinct `token_type` claims.
- Refresh rotates both tokens.
- Tokens are stateless; server-side revocation/logout is not implemented.

## Known Limitations

- The legacy metadata-only proof route remains available and cannot verify external bytes.
- Local file storage is single-instance; production should use S3, MinIO, or managed object storage.
- Catalog image binary retrieval requires authentication; no separate public image route exists yet.
- Mobile still uses `MockCatalogStore` and `MockOrderStore`; no Ktor API client is connected.
- Lists do not yet support advanced filters or pagination.
- Refresh tokens are not persisted or revocable.
- Offline synchronization and SQLDelight are not implemented.
