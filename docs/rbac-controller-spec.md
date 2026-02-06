# TravelBooking Quarkus RBAC Controller Specifications (Root)

Central overview and pointers to per-folder specs. Implementation is documentation-only; no code included.

## Target Controller Layout
```
src/main/java/com/travel/controller/
|-- admin/   # 14 controllers (includes AdminSupplierToolsController, AdminTrustController)
|-- mobile/
\-- website/ # 18 controllers (includes WebGlobalizationController)
```

## Authorization System
- Annotation: `@Authorized` with `roles`, `requireOwner`, optional `permissions`.
- Patterns: `@Authorized(roles = "SUPER_ADMIN")`, `@Authorized(roles = {"TRAVELER","SUPPLIER_SUBSCRIBER"})`, `@Authorized(roles = "TRAVELER", requireOwner = true)`.
- Interceptor: extract JWT, populate SecurityContext, validate role, check ownership when required, evaluate permissions, throw Unauthorized/Forbidden on failure.
- Coverage: every endpoint declares `@Authorized`; only explicit public endpoints (auth entrypoints, public views/search) allow anonymous access.
- Resource ownership: for user-owned data, verify current user ID matches owner unless SUPER_ADMIN.

## Cross-Cutting Security and Data Rules
- Soft delete: `deleted` flag and `deletedAt` timestamp; filter out deleted in queries.
- Audit logging: admin actions, payments/refunds, account changes, moderation decisions, report handling.
- Rate limiting: apply to public endpoints.
- Input validation and consistent error handling everywhere.

## Standard Response Shapes
- Success:
  ```
  { "success": true, "message": "Operation completed", "data": { }, "timestamp": "2024-02-06T10:30:00Z" }
  ```
- Error:
  ```
  { "success": false, "message": "Error description", "error_code": "UNAUTHORIZED", "timestamp": "2024-02-06T10:30:00Z" }
  ```
- Paginated:
  ```
  { "success": true, "data": [ ], "pagination": { "total_items": 100, "page": 0, "size": 20, "total_pages": 5, "has_next": true, "has_previous": false }, "timestamp": "2024-02-06T10:30:00Z" }
  ```

## Detailed Specifications (split by folder)
- Admin (SUPER_ADMIN only): `docs/rbac-admin-spec.md`
- Mobile (TRAVELER, SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER): `docs/rbac-mobile-spec.md`
- Website (same roles, includes WebGlobalization): `docs/rbac-website-spec.md`

## Implementation Summary (v2.0)
- Controllers: 43 total (admin 14, mobile 11, website 18 including WebGlobalization); 330+ methods.
- Admin: SUPER_ADMIN exclusive; platform management, moderation, reporting, payments with audit logging.
- Mobile: lightweight payloads, small pagination, strict ownership checks.
- Website: richer payloads, larger pagination, dashboards/analytics, globalization endpoints.
- No inheritance: every controller/method is standalone with explicit `@Authorized`.

## Security Requirements
1. Explicit `@Authorized` on every method.
2. Role validation on every call.
3. Ownership validation when `requireOwner = true`.
4. Input validation for all request data.
5. Consistent error handling.
6. Audit logging for sensitive operations.
7. Rate limiting for public endpoints.

## Implementation Notes
1. No inheritance or shared base controllers.
2. Soft deletes everywhere; preserve audit trails.
3. Indexing and transactional integrity for performance and correctness.
4. Standardized responses; align error codes/messages across services.
