# SuitUP Spring Boot Backend

Independent REST API for SuitUP authentication, catalog, checkout/orders,
payments, proof metadata, timelines, and administration. It is intentionally
not included in the Kotlin Multiplatform root `settings.gradle.kts`, so backend
work cannot silently change the mobile build.

## Stack

- Java 17
- Spring Boot 3.4.13
- Gradle Kotlin DSL
- Spring Web, Data JPA, Validation, and Security
- PostgreSQL and Flyway
- Testcontainers for PostgreSQL integration tests

Flyway owns the database schema. Hibernate runs with `ddl-auto=validate` and
must never create or update production tables.

## Project structure

```text
backend/
|-- src/main/java/com/suitup/backend/
|   |-- auth/          registration, login, refresh, current user
|   |-- user/          users and CUSTOMER/ADMIN roles
|   |-- catalog/       public and administrative suit catalog
|   |-- order/         checkout, measurements, fulfillment, timelines
|   |-- payment/       submission, review, proof metadata integration
|   |-- upload/        file metadata only
|   |-- dashboard/     administrative metrics and recent activity
|   |-- security/      JWT, BCrypt, CORS, filters, 401/403 handlers
|   `-- common/        errors, money, persistence bases, idempotency
|-- src/main/resources/db/migration/  versioned Flyway migrations
|-- src/test/java/                    unit, MVC, security, integration tests
|-- API_CONTRACT.md                   complete HTTP contract
`-- BACKEND_IMPLEMENTATION_REPORT.md  technical and academic summary
```

## Documentation

- [API_CONTRACT.md](API_CONTRACT.md): every endpoint, request, response, role,
  status code, lifecycle, and known limitation.
- [BACKEND_IMPLEMENTATION_REPORT.md](BACKEND_IMPLEMENTATION_REPORT.md): concise
  architecture, security, database, testing, risk, and roadmap report.

## Environment and local database

When Docker is available, start PostgreSQL from `backend/`:

```powershell
docker compose up -d postgres
```

Defaults are development-only:

```text
DB_HOST=localhost
DB_PORT=5432
DB_NAME=suitup_db
DB_USER=suitup_user
DB_PASSWORD=suitup_password
SERVER_PORT=8080
SEED_DEV=true
JWT_SECRET=<at-least-32-random-bytes>
JWT_ACCESS_TOKEN_MINUTES=15
JWT_REFRESH_TOKEN_DAYS=14
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8081
```

Override them through environment variables outside local development. The
development seed inserts the two role codes, one local admin account, and the
six current mobile catalog examples. The `local` profile provides a development
JWT secret; production must provide a unique random `JWT_SECRET` of at least 32
bytes.

Local-only administrator credentials:

```text
Email: admin@suitup.local
Password: Admin12345!
```

Never use these credentials or the local JWT secret outside local development.

## Run locally

With PostgreSQL running and environment variables configured, from `backend/`:

```powershell
..\gradlew.bat bootRun
```

The API starts at `http://localhost:8080`. Flyway applies pending migrations
before Hibernate validates the mapped schema.

Docker is not installed on the current development PC. Consequently, the API
cannot currently be validated against the Docker Compose PostgreSQL instance;
that validation is explicitly deferred to Prompt 17. Non-database MVC and unit
tests remain fully runnable.

## Run tests

This project has independent Gradle settings but reuses the repository's Gradle
wrapper executable. From `backend/`:

```powershell
..\gradlew.bat --no-daemon --console=plain test --rerun-tasks
```

The Testcontainers suite is skipped automatically when Docker is unavailable.
All unit, mapper, service, security, and MVC tests still run without Docker.

## Health

`GET http://localhost:8080/api/health`

```json
{
  "status": "UP",
  "service": "suitup-backend"
}
```

The health, registration, login, and refresh endpoints are public. Other routes
require a valid bearer access token, and `/api/admin/**` additionally requires
the `ADMIN` role.

## Authentication

Available endpoints:

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
GET  /api/auth/me
```

Registration accepts `fullName`, `email`, optional `phone`, and `password`, and
always creates a `CUSTOMER`. Login and registration return short-lived access
and longer-lived refresh JWTs. Send the access token as:

```text
Authorization: Bearer <access-token>
```

Registration request:

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

Successful authentication response (tokens shortened):

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.access-token-value",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh-token-value",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "user": {
    "id": "7bc4c8f3-2a8d-4a7b-99b6-45d13c7a43a9",
    "fullName": "Joao Cliente",
    "email": "joao@example.com",
    "phone": "+258840000001",
    "enabled": true,
    "roles": ["CUSTOMER"]
  }
}
```

Refresh accepts `{ "refreshToken": "<refresh-token>" }` and rotates both tokens. Tokens are
currently stateless and are not stored or revoked server-side; logout therefore
means discarding them on the client. Server-side refresh-token persistence and
revocation remain future hardening work.

## Catalog API

Public catalog reads require no token and expose active models only:

```text
GET /api/suit-models
GET /api/suit-models/{id}
```

An inactive model is deliberately returned as `404` from the public detail
endpoint. Administrators can read and manage both active and inactive models:

```text
GET   /api/admin/suit-models
GET   /api/admin/suit-models/{id}
POST  /api/admin/suit-models
PUT   /api/admin/suit-models/{id}
PATCH /api/admin/suit-models/{id}/activate
PATCH /api/admin/suit-models/{id}/deactivate
```

All admin routes require a bearer token with the `ADMIN` role. Example creation:

```powershell
curl.exe -X POST http://localhost:8080/api/admin/suit-models `
  -H "Authorization: Bearer <access-token>" `
  -H "Content-Type: application/json" `
  -d '{"name":"Fato Clássico Preto","category":"Clássico","description":"Corte formal","price":8500.00,"currency":"MZN","fabricType":"Lã Premium","color":"Preto","imageKey":"suit_classic_black","active":true}'
```

Example response:

```json
{
  "id": "10000000-0000-0000-0000-000000000001",
  "name": "Fato Clássico Preto",
  "category": "Clássico",
  "description": "Corte formal",
  "price": 8500.00,
  "currency": "MZN",
  "fabricType": "Lã Premium",
  "color": "Preto",
  "imageKey": "suit_classic_black",
  "primaryImageFileId": null,
  "active": true
}
```

`currency` defaults to `MZN`, `active` defaults to `true` on creation, and
prices are persisted and returned by the server. Filters and pagination are
deferred until catalog volume requires them. Physical image upload and the
image-metadata endpoint are also deferred; `imageKey` and an existing uploaded
file identifier remain the current metadata strategies. The mobile application
continues to use its mock stores and is not connected to these endpoints yet.

## Orders and checkout API

Customer order routes require `CUSTOMER` or `ADMIN`:

```text
POST /api/orders
GET  /api/orders/my
GET  /api/orders/{id}
GET  /api/orders/{id}/timeline
```

The authenticated CUSTOMER identity always overrides any `customerUserId` in
the request. A customer can read only their own orders; unknown and non-owned
order identifiers both return `404` to avoid identifier enumeration. ADMIN can
read any order and use:

```text
GET   /api/admin/orders
GET   /api/admin/orders/{id}
GET   /api/admin/orders/{id}/timeline
PATCH /api/admin/orders/{id}/status
```

Checkout creation example:

```powershell
curl.exe -X POST http://localhost:8080/api/orders `
  -H "Authorization: Bearer <access-token>" `
  -H "Idempotency-Key: checkout-123" `
  -H "Content-Type: application/json" `
  -d '{"customerName":"Joao Cliente","customerPhone":"+258840000001","customerEmail":"joao@example.com","fulfillmentType":"PICKUP","pickupLocation":"Loja Maputo","items":[{"suitModelId":"10000000-0000-0000-0000-000000000001","fabric":"La Premium","color":"Preto","designSnapshot":{"lapel":"classica"},"quantity":1}],"measurement":{"heightCm":178,"chestCm":102,"waistCm":88,"shouldersCm":46,"sleeveCm":64,"trouserLengthCm":104}}'
```

The response is an `OrderResponse` with server-generated `id`, `orderNumber`,
item snapshots, measurements, timeline, and server-calculated amounts. New
orders start with `status=RECEIVED` and `paymentStatus=PENDING`. Prices always
come from the active catalog; the current delivery fee is `150.00 MZN` for
`DELIVERY` and `0.00 MZN` for `PICKUP`.

`Idempotency-Key` is optional, limited to 150 characters, and retained for 24
hours. Repeating the same key and payload replays the original order; reusing it
with a different payload returns `409`. The legacy JSON `idempotencyKey` field
remains supported, but the header takes precedence.

Status updates accept, for example, `{ "status": "IN_ANALYSIS", "note":
"Dados confirmados" }`. Invalid transitions return structured `400` errors.
Production cannot begin until payment is `CONFIRMED`. Filtering and pagination
for order lists remain deferred until volume requires them.

## Payments API

Customer payment routes require `CUSTOMER` or `ADMIN` and owner-or-admin access
to the order:

```text
POST /api/orders/{orderId}/payment
GET  /api/orders/{orderId}/payment
GET  /api/orders/{orderId}/payments
GET  /api/orders/{orderId}/payment-timeline
POST /api/orders/{orderId}/payment-proof-metadata
```

`GET /api/orders/{orderId}/payment` returns the latest payment, while
`GET /api/orders/{orderId}/payments` returns all
attempts newest first, including rejected attempts. Example submission:

```json
{
  "method": "MPESA",
  "amount": 8500.00,
  "transactionReference": "MPESA-123",
  "proofFileId": null,
  "note": "Pagamento submetido"
}
```

The amount must exactly match the server-calculated order total. MPESA, EMOLA,
and BANK_TRANSFER require a transaction reference; CASH_ON_PICKUP does not.
References are unique per payment method and duplicates return `409`. New
payments start as `PENDING` and also set the order payment status to `PENDING`.

Proof metadata accepts `image/png`, `image/jpeg`, or `application/pdf` up to 10
MB. It records names, MIME type, size, and an optional external path/URL, then
returns a `proofFileId` for payment submission. It does not receive or store
physical file bytes. Missing stored names and paths are replaced with unique
metadata-only identifiers.

Admin routes require `ADMIN`:

```text
GET   /api/admin/payments
GET   /api/admin/payments/pending
GET   /api/admin/payments/{paymentId}
GET   /api/admin/payments/{paymentId}/timeline
PATCH /api/admin/payments/{paymentId}/confirm
PATCH /api/admin/payments/{paymentId}/reject
```

Confirmation accepts `{ "note": "Validado" }`. Rejection requires, for
example, `{ "rejectionReason": "Comprovativo ilegivel", "note": "Reenviar" }`.
The authenticated admin identity always replaces any reviewer identifier sent
by the client. Only `PENDING` payments can transition to `CONFIRMED` or
`REJECTED`; both transitions update the order, reviewer, timestamps, and ordered
payment history. Invalid transitions and amount mismatches return structured
`400` responses.

## Admin dashboard API

`GET /api/admin/dashboard` requires an `ADMIN` bearer token. Unauthenticated
requests receive `401`, and authenticated customers receive `403`.

Example response (recent lists shortened):

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
    "IN_PRODUCTION": 1,
    "DELIVERED": 3
  },
  "recentOrders": [],
  "recentPendingPayments": []
}
```

Order totals and status counts come from the orders table. Payment counts come
from payment status, not the order snapshot, and confirmed revenue sums only
`CONFIRMED` payments; an empty sum is returned as `0`. Catalog counts distinguish
active and inactive suit models. The response also includes the five newest
orders and five most recently submitted pending payments. Values currently use
`MZN` and are calculated from database state on each request.

## Domain layer

The backend now includes JPA entities, repositories, DTO records, explicit
mappers, structured exceptions, and transactional services for:

- users and role assignment;
- active/inactive suit catalog management;
- order creation with server-calculated prices and immutable snapshots;
- measurement snapshots and order status transitions;
- payment submission, confirmation, rejection, and status history;
- upload metadata and administrative dashboard metrics.

Entities are internal persistence models and are never returned directly by API
controllers. Every HTTP response uses a dedicated DTO record.

## Business rules summary

- Public catalog responses contain active models only.
- Catalog prices are authoritative; order totals are calculated server-side.
- Order items and measurements are stored as checkout-time snapshots.
- Delivery costs `150.00 MZN`; pickup costs `0.00 MZN`.
- Orders begin as `RECEIVED`; payments begin as `PENDING`.
- Production cannot begin before payment confirmation.
- `Idempotency-Key` prevents duplicate order creation for 24 hours.
- Customers can access only their own orders, payments, and proof metadata.
- Payment amount must equal the order total; electronic references are unique.
- Only pending payments can be confirmed or rejected by ADMIN.
- Dashboard revenue sums confirmed payments only.

## Migration policy

- `V1__init_schema.sql`: tables and relationships.
- `V2__indexes_and_constraints.sql`: integrity checks, indexes, and timestamp triggers.
- `V3__dev_seed_data.sql`: required role codes plus seed data guarded by `SEED_DEV`.
- `V4__configure_dev_admin_credentials.sql`: local-only BCrypt admin credentials.

Never add destructive reset statements to versioned migrations. Development
database resets must be explicit Docker volume operations, not application boot
behaviour.

## Not implemented in this phase

- Physical upload and file storage
- Mobile Ktor integration
- SQLDelight or offline synchronization
- Real PostgreSQL/Flyway validation on this PC
- Advanced pagination and filters
- Persisted refresh-token revocation

## Next steps

1. Prompt 17: validate migrations, repositories, and integration tests against real PostgreSQL.
2. Prompt 18: implement secure physical file upload and storage abstraction.
3. Prompt 19: add the Ktor API client and remote repositories to KMP common code.
4. Prompt 20 and later: migrate mobile flows incrementally from mock to API data.
5. Later: add SQLDelight caching and offline synchronization.
