# SuitUP Backend Implementation Report

## 1. Executive Summary

The SuitUP backend is an independent Java 17 and Spring Boot REST API prepared for the existing Kotlin Multiplatform mobile application. It implements authentication, role-based authorization, catalog administration, checkout/order processing, payment review, secure local file uploads, timelines, and administrative dashboard metrics.

The mobile application is intentionally not connected yet and continues to use `MockCatalogStore` and `MockOrderStore`. This separation preserves the currently working Android demo while the backend is validated independently.

## 2. Implemented Modules

- `auth`: registration, login, refresh, and current-user endpoints.
- `user`: users, roles, repositories, mapping, and lookup services.
- `catalog`: public active catalog and complete ADMIN catalog management.
- `order`: checkout, item snapshots, measurements, fulfillment, timelines, ownership, and status transitions.
- `payment`: submission, latest/all attempts, ADMIN review, status history, and amount/reference validation.
- `upload`: validated multipart upload, local filesystem storage, private retrieval, and database metadata.
- `dashboard`: ADMIN metrics, confirmed revenue, and recent operational records.
- `security`: stateless Spring Security, JWT, BCrypt, CORS, and structured 401/403 responses.
- `common`: error contract, persistence base classes, money rules, and idempotency support.

## 3. Architecture

The backend follows a layered package-by-feature architecture:

```text
HTTP Controller -> DTO validation -> Transactional Service -> Repository -> PostgreSQL
                                      |                  |
                                      +-> Mapper         +-> JPA Entity
```

Controllers expose DTO records only. Services own business rules and transaction boundaries. Repositories contain persistence queries without business decisions. JPA entities remain internal and are mapped explicitly to stable response DTOs. `spring.jpa.open-in-view=false` prevents accidental lazy database access from controllers.

The backend has independent Gradle settings and is not included in the mobile root build, reducing the risk of backend changes breaking the KMP application.

## 4. Database and Flyway

PostgreSQL is the target database. Flyway is the only schema owner and Hibernate uses `ddl-auto=validate`.

- `V1__init_schema.sql`: users, roles, catalog, uploads, orders, items, measurements, payments, histories, and idempotency tables.
- `V2__indexes_and_constraints.sql`: enum checks, money/measurement integrity, lifecycle checks, uniqueness, lookup indexes, and update triggers.
- `V3__dev_seed_data.sql`: required roles plus optional local admin and six catalog models under `SEED_DEV`.
- `V4__configure_dev_admin_credentials.sql`: local-only BCrypt password for `admin@suitup.local`.
- `V5__normalize_currency_columns_to_varchar.sql`: normalizes the `suit_models`, `orders`, and `payments` currency columns to `VARCHAR(3)` to match their JPA mappings.

Versioned migrations contain no destructive table drops or production resets. Java role, order-status, payment-status, payment-method, fulfillment, and upload-purpose values match database constraints.

The complete V1-V5 migration chain has been validated against PostgreSQL 16 through Testcontainers. Hibernate schema validation passes after the V5 currency normalization.

## 5. Security

- Stateless Spring Security with CSRF, form login, and HTTP Basic disabled.
- HS256 JWT access and refresh tokens implemented with Nimbus.
- Default access lifetime: 15 minutes; refresh lifetime: 14 days.
- BCrypt password hashing with strength 12.
- Public registration always assigns `CUSTOMER`; ADMIN cannot be requested publicly.
- `/api/admin/**` requires `ROLE_ADMIN`.
- Customer order/payment routes require `CUSTOMER` or `ADMIN` and enforce owner-or-admin access.
- Private file retrieval enforces owner-or-admin access and returns `404` for foreign files.
- Stored filenames are UUID-generated; original names never participate in filesystem resolution.
- Disabled users cannot authenticate.
- Password hashes and JPA user entities are never returned by controllers.
- Invalid authentication and authorization return structured JSON `401` and `403` responses.

Production must provide a unique `JWT_SECRET` of at least 32 random bytes and must not use the local seeded credentials.

## 6. Business Rules

- Only active models appear in the public catalog.
- Catalog prices are authoritative and are copied into immutable order-item snapshots.
- Delivery costs `150.00 MZN`; pickup costs `0.00 MZN`.
- New orders start as `RECEIVED` with payment status `PENDING`.
- Order creation supports a 24-hour `Idempotency-Key` contract.
- Customers cannot bind orders to another account; the JWT user ID overrides request identity.
- Customers receive `404` for missing and non-owned orders to reduce identifier disclosure.
- Production cannot start before payment is confirmed.
- Payment amount must equal the server-calculated order total.
- Electronic payments require a transaction reference; references are unique per method.
- Only pending payments can be confirmed or rejected.
- Payment review records the authenticated ADMIN, timestamps, order status, and history.
- Physical proofs can be attached only to the latest pending payment for an accessible order.
- Catalog images accept PNG/JPEG only and can be uploaded and linked only by ADMIN.
- Dashboard revenue includes confirmed payments only and converts a null database sum to zero.

## 7. API Modules

Implemented API groups:

- Health: readiness endpoint.
- Auth: register, login, refresh, and current identity.
- Public catalog: active list and detail.
- Admin catalog: list, detail, create, update, activate, deactivate, and physical image upload.
- Customer orders: create, own list/detail, and timeline.
- Admin orders: list, detail, timeline, and status update.
- Files: authenticated multipart upload and private owner-or-admin retrieval.
- Customer payments: submit, latest/all attempts, timeline, legacy proof metadata, and physical proof upload.
- Admin payments: all/pending list, detail, timeline, confirm, and reject.
- Admin dashboard: aggregate counts, confirmed revenue, and recent records.

The complete request, response, authorization, error, lifecycle, and limitation contract is in [API_CONTRACT.md](API_CONTRACT.md).

## 8. Tests

The backend test suite contains 72 tests across authentication, JWT, health/security, physical storage, catalog, orders, payments, dashboard, service business rules, and MVC contracts.

Current expected result:

```text
72 tests
0 failures
0 errors
0 skipped
```

Controller tests use Spring MVC and Spring Security test support. Storage tests use temporary directories and verify physical bytes, safe metadata, MIME/signature checks, size limits, traversal rejection, ownership, and retrieval. All three PostgreSQL/Testcontainers integration tests run and pass with Flyway at version 5.

## 9. Limitations

- No mobile Ktor client or remote repository implementation.
- Local file storage is single-instance and has no object-store replication or lifecycle policy.
- No SQLDelight cache or offline synchronization.
- No advanced pagination or filtering for large administrative lists.
- No persisted refresh-token revocation or server-side logout.
- No production secrets or production admin provisioning workflow.

## 10. Risks

- Future schema and derived-query changes must continue to run against PostgreSQL/Testcontainers to prevent database/JPA drift.
- Existing metadata-only proof records remain compatible but do not guarantee that external bytes exist.
- Local files require operational backup and should move to S3/MinIO before multi-instance production deployment.
- Stateless refresh tokens remain valid until expiration if copied or stolen.
- Non-paginated lists may become expensive as data volume grows.
- The mobile and backend DTO contracts can drift until Ktor DTO mapping tests are introduced.

## 11. Next Phases

1. Prompt 19: add Ktor client configuration, remote DTOs, mappers, and remote repositories to KMP common code.
2. Prompt 20 and later: migrate mobile screens incrementally from mock to remote repositories with loading/error/empty states.
3. Production storage phase: replace local files with S3/MinIO and add lifecycle/backup policy.
4. Later offline phase: SQLDelight cache, sync queue, conflict rules, and retry policy.
